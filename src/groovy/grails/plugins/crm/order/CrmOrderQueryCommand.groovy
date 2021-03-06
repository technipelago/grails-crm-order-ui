/*
 * Copyright (c) 2012 Goran Ehrsson.
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

package grails.plugins.crm.order

import grails.validation.Validateable

/**
 * Command object used when searching for orders.
 */
@Validateable
class CrmOrderQueryCommand implements Serializable {
    String number
    String product
    String customer
    String address
    String email
    String telephone
    String status
    String type
    String delivery
    String campaign
    String fromDate
    String toDate
    String tags
}
