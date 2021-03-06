/*
 * Copyright 2013 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package grails.plugins.crm.order

import org.springframework.dao.DataIntegrityViolationException

import javax.servlet.http.HttpServletResponse

class CrmOrderTypeController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    static navigation = [
            [group: 'admin',
                    order: 860,
                    title: 'crmOrderType.label',
                    action: 'index'
            ]
    ]

    def selectionService
    def crmOrderService

    def domainClass = CrmOrderType

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        def baseURI = new URI('gorm://crmOrderType/list')
        def query = params.getSelectionQuery()
        def uri

        switch (request.method) {
            case 'GET':
                uri = params.getSelectionURI() ?: selectionService.addQuery(baseURI, query)
                break
            case 'POST':
                uri = selectionService.addQuery(baseURI, query)
                grails.plugins.crm.core.WebUtils.setTenantData(request, 'crmOrderTypeQuery', query)
                break
        }

        params.max = Math.min(params.max ? params.int('max') : 20, 100)

        try {
            def result = selectionService.select(uri, params)
            [crmOrderTypeList: result, crmOrderTypeTotal: result.totalCount, selection: uri]
        } catch (Exception e) {
            flash.error = e.message
            [crmOrderTypeList: [], crmOrderTypeTotal: 0, selection: uri]
        }
    }

    def create() {
        def crmOrderType = crmOrderService.createOrderType(params)
        switch (request.method) {
            case 'GET':
                return [crmOrderType: crmOrderType]
            case 'POST':
                if (!crmOrderType.save(flush: true)) {
                    render view: 'create', model: [crmOrderType: crmOrderType]
                    return
                }

                flash.success = message(code: 'crmOrderType.created.message', args: [message(code: 'crmOrderType.label', default: 'Order Type'), crmOrderType.toString()])
                redirect action: 'list'
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def crmOrderType = domainClass.get(params.id)
                if (!crmOrderType) {
                    flash.error = message(code: 'crmOrderType.not.found.message', args: [message(code: 'crmOrderType.label', default: 'Order Type'), params.id])
                    redirect action: 'list'
                    return
                }

                return [crmOrderType: crmOrderType]
            case 'POST':
                def crmOrderType = domainClass.get(params.id)
                if (!crmOrderType) {
                    flash.error = message(code: 'crmOrderType.not.found.message', args: [message(code: 'crmOrderType.label', default: 'Order Type'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (crmOrderType.version > version) {
                        crmOrderType.errors.rejectValue('version', 'crmOrderType.optimistic.locking.failure',
                                [message(code: 'crmOrderType.label', default: 'Order Type')] as Object[],
                                "Another user has updated this Type while you were editing")
                        render view: 'edit', model: [crmOrderType: crmOrderType]
                        return
                    }
                }

                crmOrderType.properties = params

                if (!crmOrderType.save(flush: true)) {
                    render view: 'edit', model: [crmOrderType: crmOrderType]
                    return
                }

                flash.success = message(code: 'crmOrderType.updated.message', args: [message(code: 'crmOrderType.label', default: 'Order Type'), crmOrderType.toString()])
                redirect action: 'list'
                break
        }
    }

    def delete() {
        def crmOrderType = domainClass.get(params.id)
        if (!crmOrderType) {
            flash.error = message(code: 'crmOrderType.not.found.message', args: [message(code: 'crmOrderType.label', default: 'Order Type'), params.id])
            redirect action: 'list'
            return
        }

        if (isInUse(crmOrderType)) {
            render view: 'edit', model: [crmOrderType: crmOrderType]
            return
        }

        try {
            def tombstone = crmOrderType.toString()
            crmOrderType.delete(flush: true)
            flash.warning = message(code: 'crmOrderType.deleted.message', args: [message(code: 'crmOrderType.label', default: 'Order Type'), tombstone])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.error = message(code: 'crmOrderType.not.deleted.message', args: [message(code: 'crmOrderType.label', default: 'Order Type'), params.id])
            redirect action: 'edit', id: params.id
        }
    }

    private boolean isInUse(CrmOrderType type) {
        def count = CrmOrder.countByOrderType(type)
        def rval = false
        if (count) {
            flash.error = message(code: "crmOrderType.delete.error.reference", args:
                    [message(code: 'crmOrderType.label', default: 'Order Type'),
                            message(code: 'crmOrder.label', default: 'Orders'), count],
                    default: "This {0} is used by {1} {2}")
            rval = true
        }
        return rval
    }

    def moveUp(Long id) {
        def target = domainClass.get(id)
        if (target) {
            def sort = target.orderIndex
            def prev = domainClass.createCriteria().list([sort: 'orderIndex', order: 'desc']) {
                lt('orderIndex', sort)
                maxResults 1
            }?.find { it }
            if (prev) {
                domainClass.withTransaction { tx ->
                    target.orderIndex = prev.orderIndex
                    prev.orderIndex = sort
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
        }
        redirect action: 'list'
    }

    def moveDown(Long id) {
        def target = domainClass.get(id)
        if (target) {
            def sort = target.orderIndex
            def next = domainClass.createCriteria().list([sort: 'orderIndex', order: 'asc']) {
                gt('orderIndex', sort)
                maxResults 1
            }?.find { it }
            if (next) {
                domainClass.withTransaction { tx ->
                    target.orderIndex = next.orderIndex
                    next.orderIndex = sort
                }
            }
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND)
        }
        redirect action: 'list'
    }
}
