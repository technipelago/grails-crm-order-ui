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

class CrmDeliveryTypeController {

    static allowedMethods = [create: ['GET', 'POST'], edit: ['GET', 'POST'], delete: 'POST']

    static navigation = [
            [group: 'admin',
                    order: 880,
                    title: 'crmDeliveryType.label',
                    action: 'index'
            ]
    ]

    def selectionService
    def crmOrderService

    def domainClass = CrmDeliveryType

    def index() {
        redirect action: 'list', params: params
    }

    def list() {
        def baseURI = new URI('gorm://crmDeliveryType/list')
        def query = params.getSelectionQuery()
        def uri

        switch (request.method) {
            case 'GET':
                uri = params.getSelectionURI() ?: selectionService.addQuery(baseURI, query)
                break
            case 'POST':
                uri = selectionService.addQuery(baseURI, query)
                grails.plugins.crm.core.WebUtils.setTenantData(request, 'crmDeliveryTypeQuery', query)
                break
        }

        params.max = Math.min(params.max ? params.int('max') : 20, 100)

        try {
            def result = selectionService.select(uri, params)
            [crmDeliveryTypeList: result, crmDeliveryTypeTotal: result.totalCount, selection: uri]
        } catch (Exception e) {
            flash.error = e.message
            [crmDeliveryTypeList: [], crmDeliveryTypeTotal: 0, selection: uri]
        }
    }

    def create() {
        def crmDeliveryType = crmOrderService.createDeliveryType(params)
        switch (request.method) {
            case 'GET':
                return [crmDeliveryType: crmDeliveryType]
            case 'POST':
                if (!crmDeliveryType.save(flush: true)) {
                    render view: 'create', model: [crmDeliveryType: crmDeliveryType]
                    return
                }

                flash.success = message(code: 'crmDeliveryType.created.message', args: [message(code: 'crmDeliveryType.label', default: 'Delivery Type'), crmDeliveryType.toString()])
                redirect action: 'list'
                break
        }
    }

    def edit() {
        switch (request.method) {
            case 'GET':
                def crmDeliveryType = domainClass.get(params.id)
                if (!crmDeliveryType) {
                    flash.error = message(code: 'crmDeliveryType.not.found.message', args: [message(code: 'crmDeliveryType.label', default: 'Delivery Type'), params.id])
                    redirect action: 'list'
                    return
                }

                return [crmDeliveryType: crmDeliveryType]
            case 'POST':
                def crmDeliveryType = domainClass.get(params.id)
                if (!crmDeliveryType) {
                    flash.error = message(code: 'crmDeliveryType.not.found.message', args: [message(code: 'crmDeliveryType.label', default: 'Delivery Type'), params.id])
                    redirect action: 'list'
                    return
                }

                if (params.version) {
                    def version = params.version.toLong()
                    if (crmDeliveryType.version > version) {
                        crmDeliveryType.errors.rejectValue('version', 'crmDeliveryType.optimistic.locking.failure',
                                [message(code: 'crmDeliveryType.label', default: 'Delivery Type')] as Object[],
                                "Another user has updated this Type while you were editing")
                        render view: 'edit', model: [crmDeliveryType: crmDeliveryType]
                        return
                    }
                }

                crmDeliveryType.properties = params

                if (!crmDeliveryType.save(flush: true)) {
                    render view: 'edit', model: [crmDeliveryType: crmDeliveryType]
                    return
                }

                flash.success = message(code: 'crmDeliveryType.updated.message', args: [message(code: 'crmDeliveryType.label', default: 'Delivery Type'), crmDeliveryType.toString()])
                redirect action: 'list'
                break
        }
    }

    def delete() {
        def crmDeliveryType = domainClass.get(params.id)
        if (!crmDeliveryType) {
            flash.error = message(code: 'crmDeliveryType.not.found.message', args: [message(code: 'crmDeliveryType.label', default: 'Delivery Type'), params.id])
            redirect action: 'list'
            return
        }

        if (isInUse(crmDeliveryType)) {
            render view: 'edit', model: [crmDeliveryType: crmDeliveryType]
            return
        }

        try {
            def tombstone = crmDeliveryType.toString()
            crmDeliveryType.delete(flush: true)
            flash.warning = message(code: 'crmDeliveryType.deleted.message', args: [message(code: 'crmDeliveryType.label', default: 'Delivery Type'), tombstone])
            redirect action: 'list'
        }
        catch (DataIntegrityViolationException e) {
            flash.error = message(code: 'crmDeliveryType.not.deleted.message', args: [message(code: 'crmDeliveryType.label', default: 'Delivery Type'), params.id])
            redirect action: 'edit', id: params.id
        }
    }

    private boolean isInUse(CrmDeliveryType type) {
        def count = CrmOrder.countByDeliveryType(type)
        def rval = false
        if (count) {
            flash.error = message(code: "crmDeliveryType.delete.error.reference", args:
                    [message(code: 'crmDeliveryType.label', default: 'Delivery Type'),
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
