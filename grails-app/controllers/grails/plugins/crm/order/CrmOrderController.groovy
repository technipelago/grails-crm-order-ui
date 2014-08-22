package grails.plugins.crm.order

import grails.converters.JSON
import grails.plugins.crm.core.CrmEmbeddedAddress
import grails.plugins.crm.core.TenantUtils
import grails.plugins.crm.core.WebUtils
import grails.plugins.crm.core.CrmValidationException
import org.springframework.dao.DataIntegrityViolationException

import javax.servlet.http.HttpServletResponse
import java.util.concurrent.TimeoutException

/**
 * Order CRUD Controller.
 */
class CrmOrderController {

    static allowedMethods = [create: ["GET", "POST"], edit: ["GET", "POST"], delete: "POST"]

    def crmSecurityService
    def selectionService
    def crmOrderService
    def crmContactService
    def crmTagService
    def userTagService

    def crmProductService // optional

    def index() {
        // If any query parameters are specified in the URL, let them override the last query stored in session.
        def cmd = new CrmOrderQueryCommand()
        def query = params.getSelectionQuery()
        bindData(cmd, query ?: WebUtils.getTenantData(request, 'crmOrderQuery'))
        [cmd: cmd]
    }

    def list() {
        def baseURI = new URI('bean://crmOrderService/list')
        def query = params.getSelectionQuery()
        def uri

        switch (request.method) {
            case 'GET':
                uri = params.getSelectionURI() ?: selectionService.addQuery(baseURI, query)
                break
            case 'POST':
                uri = selectionService.addQuery(baseURI, query)
                WebUtils.setTenantData(request, 'crmOrderQuery', query)
                break
        }

        params.max = Math.min(params.max ? params.int('max') : 10, 100)

        def result
        try {
            result = selectionService.select(uri, params)
            if (result.totalCount == 1 && params.view != 'list') {
                // If we only got one record, show the record immediately.
                redirect action: "show", params: selectionService.createSelectionParameters(uri) + [id: result.head().ident()]
            } else {
                [crmOrderList: result, crmOrderTotal: result.totalCount, selection: uri]
            }
        } catch (Exception e) {
            flash.error = e.message
            [crmOrderList: [], crmOrderTotal: 0, selection: uri]
        }
    }

    def clearQuery() {
        WebUtils.setTenantData(request, 'crmOrderQuery', null)
        redirect(action: 'index')
    }

    def create() {
        def crmOrder = new CrmOrder(invoice: new CrmEmbeddedAddress(), delivery: new CrmEmbeddedAddress(), orderDate: new java.sql.Date(System.currentTimeMillis()))

        if (request.post) {
            try {
                crmOrder = crmOrderService.saveOrder(crmOrder, params)
            } catch (CrmValidationException e) {
                crmOrder = e[0]
            }
            if (!crmOrder.hasErrors()) {
                def currentUser = crmSecurityService.currentUser
                event(for: "crmOrder", topic: "created", fork: false, data: [id: crmOrder.id, tenant: crmOrder.tenantId, user: currentUser?.username])
                flash.success = message(code: 'crmOrder.created.message', args: [message(code: 'crmOrder.label', default: 'Order'), crmOrder.toString()])
                redirect(action: "show", id: crmOrder.id)
                return
            }
        } else {
            bindData(crmOrder, params, [include: CrmOrder.BIND_WHITELIST])
            bindData(crmOrder.invoice, params, 'invoice')
            bindData(crmOrder.delivery, params, 'delivery')
        }

        def metadata = [:]
        metadata.orderStatusList = crmOrderService.listOrderStatus(null).findAll { it.enabled }
        if (crmOrder.orderStatus && !metadata.orderStatusList.contains(crmOrder.orderStatus)) {
            metadata.orderStatusList << crmOrder.orderStatus
        }
        metadata.orderTypeList = crmOrderService.listOrderType(null).findAll { it.enabled }
        if (crmOrder.orderType && !metadata.orderTypeList.contains(crmOrder.orderType)) {
            metadata.orderTypeList << crmOrder.orderType
        }
        metadata.deliveryTypeList = crmOrderService.listDeliveryType(null).findAll { it.enabled }
        if (crmOrder.deliveryType && !metadata.deliveryTypeList.contains(crmOrder.deliveryType)) {
            metadata.deliveryTypeList << crmOrder.deliveryType
        }

        return [crmOrder: crmOrder, metadata: metadata]
    }

    def edit(Long id) {
        def crmOrder = CrmOrder.findByIdAndTenantId(id, TenantUtils.tenant)
        if (!crmOrder) {
            flash.error = message(code: 'crmOrder.not.found.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect(action: "index")
            return
        }
        if (request.post) {
            if (params.int('version') != null && crmOrder.version > params.int('version')) {
                crmOrder.errors.rejectValue("version", "crmOrder.optimistic.locking.failure",
                        [message(code: 'crmOrder.label', default: 'Order')] as Object[],
                        "Another user has updated this Order while you were editing")
            } else {
                def ok = false
                try {
                    crmOrder = crmOrderService.saveOrder(crmOrder, params)
                    ok = !crmOrder.hasErrors()
                } catch (CrmValidationException e) {
                    crmOrder = (CrmOrder) e[0]
                } catch (Exception e) {
                    // Re-attach object to this Hibernate session to avoid problems with uninitialized associations.
                    if (!crmOrder.isAttached()) {
                        crmOrder.discard()
                        crmOrder.attach()
                    }
                    log.warn("Failed to save crmOrder@$id", e)
                    flash.error = e.message
                }

                if (ok) {
                    def currentUser = crmSecurityService.currentUser
                    event(for: "crmOrder", topic: "updated", fork: false, data: [id: crmOrder.id, tenant: crmOrder.tenantId, user: currentUser?.username])
                    flash.success = message(code: 'crmOrder.updated.message', args: [message(code: 'crmOrder.label', default: 'Order'), crmOrder.toString()])
                    redirect(action: "show", id: crmOrder.id)
                    return
                }
            }
        }

        def metadata = [:]
        metadata.orderStatusList = crmOrderService.listOrderStatus(null).findAll { it.enabled }
        if (crmOrder.orderStatus && !metadata.orderStatusList.contains(crmOrder.orderStatus)) {
            metadata.orderStatusList << crmOrder.orderStatus
        }
        metadata.orderTypeList = crmOrderService.listOrderType(null).findAll { it.enabled }
        if (crmOrder.orderType && !metadata.orderTypeList.contains(crmOrder.orderType)) {
            metadata.orderTypeList << crmOrder.orderType
        }
        metadata.deliveryTypeList = crmOrderService.listDeliveryType(null).findAll { it.enabled }
        if (crmOrder.deliveryType && !metadata.deliveryTypeList.contains(crmOrder.deliveryType)) {
            metadata.deliveryTypeList << crmOrder.deliveryType
        }
        metadata.vatList = getVatOptions()
        metadata.allProducts = getProductList(crmOrder)

        return [crmOrder: crmOrder, metadata: metadata]
    }

    def delete(Long id) {
        def crmOrder = CrmOrder.findByIdAndTenantId(id, TenantUtils.tenant)
        if (!crmOrder) {
            flash.error = message(code: 'crmOrder.not.found.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect(action: "list")
            return
        }

        try {
            def tombstone = crmOrder.toString()
            crmOrder.delete(flush: true)
            flash.warning = message(code: 'crmOrder.deleted.message', args: [message(code: 'crmOrder.label', default: 'Order'), tombstone])
            redirect(action: "index")
        }
        catch (DataIntegrityViolationException e) {
            flash.error = message(code: 'crmOrder.not.deleted.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect(action: "edit", id: id)
        }
    }

    def show(Long id) {
        def crmOrder = CrmOrder.findByIdAndTenantId(id, TenantUtils.tenant)
        if (crmOrder) {
            return [crmOrder: crmOrder, customerContact: crmOrder.getCustomer(), deliveryContact: crmOrder.getDeliveryContact(),
                    selection: params.getSelectionURI()]
        } else {
            flash.error = message(code: 'crmOrder.not.found.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect(action: "index")
        }
    }

    def export() {
        def user = crmSecurityService.getUserInfo()
        def namespace = params.namespace ?: 'crmOrder'
        if (request.post) {
            def filename = message(code: 'crmOrder.label', default: 'Order')
            try {
                def topic = params.topic ?: 'export'
                def result = event(for: namespace, topic: topic,
                        data: params + [user: user, tenant: TenantUtils.tenant, locale: request.locale, filename: filename]).waitFor(60000)?.value
                if (result?.file) {
                    try {
                        WebUtils.inlineHeaders(response, result.contentType, result.filename ?: namespace)
                        WebUtils.renderFile(response, result.file)
                    } finally {
                        result.file.delete()
                    }
                    return null // Success
                } else {
                    flash.warning = message(code: 'crmOrder.export.nothing.message', default: 'Nothing was exported')
                }
            } catch (TimeoutException te) {
                flash.error = message(code: 'crmOrder.export.timeout.message', default: 'Export did not complete')
            } catch (Exception e) {
                log.error("Export event throwed an exception", e)
                flash.error = message(code: 'crmOrder.export.error.message', default: 'Export failed due to an error', args: [e.message])
            }
            redirect(action: "index")
        } else {
            def uri = params.getSelectionURI()
            def layouts = event(for: namespace, topic: (params.topic ?: 'exportLayout'),
                    data: [tenant: TenantUtils.tenant, username: user.username, uri: uri]).waitFor(10000)?.values?.flatten()
            [layouts: layouts, selection: uri]
        }
    }

    private List getVatOptions() {
        getVatList().collect {
            [label: "${it}%", value: (it / 100).doubleValue()]
        }
    }

    private List<Number> getVatList() {
        grailsApplication.config.crm.currency.vat.list ?: [0]
    }

    private List getProductList(final CrmOrder crmOrder) {
        def result
          // TODO Remove dependency on crmProductService and use synchronous application event to request product list.
        if (crmProductService != null) {
            result = crmProductService.list().collect{[id: it.number, label: it.toString()]}
        } else {
            result = []
        }
        for(item in crmOrder?.items) {
            if(! result.find{it.id == item.productId}) {
                result << [id: item.productId, label: item.productName]
            }
        }
        result.sort{it.id}
    }

    def addItem(Long id) {
        def crmOrder = id ? crmOrderService.getOrder(id) : null
        def count = crmOrder?.items?.size() ?: 0
        def vat = grailsApplication.config.crm.currency.vat.default ?: 0
        def metadata = [:]
        metadata.vatList = getVatOptions()
        metadata.allProducts = getProductList(crmOrder)

        def item = new CrmOrderItem(order: crmOrder, orderIndex: count + 1, quantity: 1, unit: 'st', price: 0, discount: 0, vat: vat)
        render template: 'item', model: [row: 0, bean: item, metadata: metadata]
    }

    def deleteItem(Long id) {
        def item = CrmOrderItem.get(id)
        if (item) {
            def order = item.order
            if (order.tenantId == TenantUtils.tenant) {
                try {
                    item.delete(flush: true)
                    render 'true'
                } catch (Exception e) {
                    log.error("Failed to delete CrmOrderItem($id)", e)
                    render 'false'
                }
            } else {
                response.sendError(HttpServletResponse.SC_FORBIDDEN)
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
        }
    }

    def createFavorite(Long id) {
        def crmOrder = crmOrderService.getOrder(id)
        if (!crmOrder) {
            flash.error = message(code: 'crmOrder.not.found.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect action: 'index'
            return
        }
        userTagService.tag(crmOrder, grailsApplication.config.crm.tag.favorite, crmSecurityService.currentUser?.username, TenantUtils.tenant)

        redirect(action: 'show', id: params.id)
    }

    def deleteFavorite(Long id) {
        def crmOrder = crmOrderService.getOrder(id)
        if (!crmOrder) {
            flash.error = message(code: 'crmOrder.not.found.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect action: 'index'
            return
        }
        userTagService.untag(crmOrder, grailsApplication.config.crm.tag.favorite, crmSecurityService.currentUser?.username, TenantUtils.tenant)
        redirect(action: 'show', id: id)
    }

    def autocompleteCustomer(String a, String q) {
        def result
        if (crmContactService != null) {
            if (!a) {
                a = 'name'
            }
            result = crmContactService.list([(a): q], [max: 20]).collect {
                def name = [it.name, it.email, it.telephone, it.number].findAll { it }.join(', ')
                def addr = it.address ?: [:]
                [name, it.id, it.number, it.fullName, it.firstName, it.lastName, it.email, it.telephone,
                        addr.address1, addr.address2, addr.address3, addr.postalCode, addr.city]
            }
        } else {
            result = []
        }
        WebUtils.noCache(response)
        render result as JSON
    }

    def autocompleteOrderStatus() {
        def result = crmOrderService.listOrderStatus(params.remove('term'), params).collect { it.toString() }
        WebUtils.defaultCache(response)
        render result as JSON
    }

    def autocompleteOrderType() {
        def result = crmOrderService.listOrderType(params.remove('term'), params).collect { it.toString() }
        WebUtils.defaultCache(response)
        render result as JSON
    }

    def autocompleteDeliveryType() {
        def result = crmOrderService.listDeliveryType(params.remove('term'), params).collect { it.toString() }
        WebUtils.defaultCache(response)
        render result as JSON
    }

    def autocompleteTags() {
        params.offset = params.offset ? params.int('offset') : 0
        if (params.limit && !params.max) params.max = params.limit
        params.max = Math.min(params.max ? params.int('max') : 25, 100)
        def result = crmTagService.listDistinctValue(CrmOrder.name, params.remove('q'), params)
        WebUtils.defaultCache(response)
        render result as JSON
    }

}
