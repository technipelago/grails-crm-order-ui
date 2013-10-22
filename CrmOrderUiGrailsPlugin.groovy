/*
 * Copyright (c) 2013 Goran Ehrsson.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

class CrmOrderUiGrailsPlugin {
    def groupId = "grails.crm"
    def version = "1.2.0"
    def grailsVersion = "2.0 > *"
    def dependsOn = [:]
    def pluginExcludes = [
            "grails-app/views/error.gsp"
    ]
    def title = "CRM Order Management UI"
    def author = "Göran Ehrsson"
    def authorEmail = "goran@technipelago.se"
    def description = '''\
Grails CRM Order Management User Interface
'''
    def documentation = "http://grails.org/plugin/crm-order-ui"
    def license = "APACHE"
    def organization = [name: "Technipelago AB", url: "http://www.technipelago.se/"]

    def issueManagement = [system: "github", url: "https://github.com/technipelago/grails-crm-order-ui/issues"]
    def scm = [url: "https://github.com/technipelago/grails-crm-order-ui"]

    def features = {
        crmOrderUi {
            description "Order Management User Interface"
            link controller: "crmOrder", action: "index"
            permissions {
                user "crmOrder:*"
                admin "crmOrder,crmOrderType,crmOrderStatus,crmDeliveryType:*"
            }
        }
    }

}
