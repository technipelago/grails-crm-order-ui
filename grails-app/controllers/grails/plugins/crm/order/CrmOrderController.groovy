package grails.plugins.crm.order

import grails.converters.JSON
import grails.plugins.crm.core.CrmEmbeddedAddress
import grails.plugins.crm.core.TenantUtils
import grails.plugins.crm.core.WebUtils
import org.springframework.dao.DataIntegrityViolationException

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
            if (result.size() == 1) {
                redirect action: "show", id: result.head().ident()
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
        def crmOrder = new CrmOrder(invoice: new CrmEmbeddedAddress(), delivery: new CrmEmbeddedAddress())

        bindData(crmOrder, params, [include: CrmOrder.BIND_WHITELIST])
        bindData(crmOrder.invoice, params, 'invoice')
        bindData(crmOrder.delivery, params, 'delivery')

        if (request.method == "POST") {
            if (grailsApplication.config.crm.order.changeEvent) {
                crmOrder.event = CrmOrder.EVENT_CHANGED
            }
            if (crmOrder.save(flush: true)) {
                flash.success = message(code: 'crmOrder.created.message', args: [message(code: 'crmOrder.label', default: 'Order'), crmOrder.toString()])
                redirect(action: "show", id: crmOrder.id)
                return
            }
        }
        [crmOrder: crmOrder, statusList: crmOrderService.listOrderStatus(true, crmOrder.orderStatus),
                orderTypeList: crmOrderService.listOrderType(true, crmOrder.orderType),
                deliveryTypeList: crmOrderService.listDeliveryType(true, crmOrder.deliveryType)]
    }

    def edit(Long id) {
        def crmOrder = CrmOrder.findByIdAndTenantId(id, TenantUtils.tenant)
        if (!crmOrder) {
            flash.error = message(code: 'crmOrder.not.found.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect(action: "index")
            return
        }
        if (request.method == "POST") {
            if (params.int('version') != null && crmOrder.version > params.int('version')) {
                crmOrder.errors.rejectValue("version", "crmOrder.optimistic.locking.failure",
                        [message(code: 'crmOrder.label', default: 'Order')] as Object[],
                        "Another user has updated this Order while you were editing")
            } else {
                def oldStatus = crmOrder.orderStatus

                if (!crmOrder.invoice) crmOrder.invoice = new CrmEmbeddedAddress()
                if (!crmOrder.delivery) crmOrder.delivery = new CrmEmbeddedAddress()

                bindData(crmOrder, params, [include: CrmOrder.BIND_WHITELIST])
                bindData(crmOrder.invoice, params, 'invoice')
                bindData(crmOrder.delivery, params, 'delivery')

                if (grailsApplication.config.crm.order.changeEvent) {
                    def newStatus = crmOrder.orderStatus
                    if (oldStatus != newStatus) {
                        crmOrder.event = CrmOrder.EVENT_CHANGED
                    }
                }

                if (crmOrder.save(flush: true)) {
                    flash.success = message(code: 'crmOrder.updated.message', args: [message(code: 'crmOrder.label', default: 'Order'), crmOrder.toString()])
                    redirect(action: "show", id: crmOrder.id)
                    return
                }
            }
        }

        [crmOrder: crmOrder, statusList: crmOrderService.listOrderStatus(true, crmOrder.orderStatus),
                orderTypeList: crmOrderService.listOrderType(true, crmOrder.orderType),
                deliveryTypeList: crmOrderService.listDeliveryType(true, crmOrder.deliveryType)]
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
            return [crmOrder: crmOrder, customerContact: crmOrder.getCustomer(), deliveryContact: crmOrder.getDeliveryContact()]
        } else {
            flash.error = message(code: 'crmOrder.not.found.message', args: [message(code: 'crmOrder.label', default: 'Order'), id])
            redirect(action: "index")
        }
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
                [name, it.id, it.number, it.name, it.firstName, it.lastName, it.email, it.telephone,
                        addr.address1, addr.address2, addr.address3, addr.postalCode, addr.city]
            }
        } else {
            result = []
        }
        WebUtils.noCache(response)
        render result as JSON
    }

    def print(Long id, String template) {
        def user = crmSecurityService.currentUser
        try {
            def tempFile = event(for: "crmOrder", topic: "print", data: params + [user: user, tenant: TenantUtils.tenant]).waitFor(60000)?.value
            if (tempFile instanceof File) {
                try {
                    def filename = message(code: 'crmOrder.label', default: template ?: 'order') + '.pdf'
                    WebUtils.inlineHeaders(response, "application/pdf", filename)
                    WebUtils.renderFile(response, tempFile)
                } finally {
                    tempFile.delete()
                }
                return null // Success
            } else if (tempFile) {
                log.error("Print event returned an unexpected value: $tempFile (${tempFile.class.name})")
                flash.error = message(code: 'crmOrder.print.error.message', default: 'Printing failed due to an error', args: [tempFile.class.name])
            } else {
                flash.warning = message(code: 'crmOrder.print.nothing.message', default: 'Nothing was printed')
            }
        } catch (TimeoutException te) {
            flash.error = message(code: 'crmOrder.print.timeout.message', default: 'Printing did not complete')
        } catch (Exception e) {
            log.error("Print event throwed an exception", e)
            flash.error = message(code: 'crmOrder.print.error.message', default: 'Printing failed due to an error', args: [e.message])
        }
        redirect(action: "index") // error condition, return to search form.
    }
}
