<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrder.label', default: 'Order')}"/>
    <title><g:message code="crmOrder.list.title" args="[entityName]"/></title>
</head>

<body>

<crm:header title="crmContact.list.title" subtitle="crmOrder.totalCount.label"
            args="[entityName, crmOrderTotal]"/>

<table class="table table-striped">
    <thead>
    <tr>
        <crm:sortableColumn property="number"
                            title="${message(code: 'crmOrder.number.label', default: 'Number')}"/>
        <crm:sortableColumn property="orderDate"
                            title="${message(code: 'crmOrder.orderDate.label', default: 'Date')}"/>
        <crm:sortableColumn property="orderStatus"
                            title="${message(code: 'crmOrder.orderStatus.label', default: 'Status')}"/>
        <crm:sortableColumn property="customerLastName"
                            title="${message(code: 'crmOrder.customer.label', default: 'Customer')}"/>
        <crm:sortableColumn property="deliveryType"
                            title="${message(code: 'crmOrder.deliveryType.label', default: 'Delivery')}"/>
        <th><g:message code="crmOrder.delivery.label"/></th>
        <crm:sortableColumn property="totalAmount" style="text-align: right;"
                            title="${message(code: 'crmOrder.paymentAmount.label', default: 'Order Value')}"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${crmOrderList}" var="crmOrder">
        <tr>

            <td>
                <select:link controller="crmOrder" action="show" id="${crmOrder.id}" selection="${selection}">
                    ${fieldValue(bean: crmOrder, field: "number")}
                </select:link>
                <g:if test="${crmOrder.syncPublished}">
                    <i class="icon-warning-sign"></i>
                </g:if>
            </td>

            <td>
                <select:link controller="crmOrder" action="show" id="${crmOrder.id}" selection="${selection}">
                    <g:formatDate type="date" date="${crmOrder.orderDate}"/>
                </select:link>
            </td>

            <td>
                <g:fieldValue bean="${crmOrder}" field="orderStatus"/>
            </td>

            <td>
                ${fieldValue(bean: crmOrder, field: "customerName")}
            </td>

            <td>
                ${fieldValue(bean: crmOrder, field: "deliveryType")}
            </td>
            <td>
                ${fieldValue(bean: crmOrder, field: "delivery")}
            </td>
            <td style="text-align: right;">
                <g:formatNumber type="currency" currencyCode="SEK" number="${crmOrder.totalAmountVAT}"
                                maxFractionDigits="0"/>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>

<crm:paginate total="${crmOrderTotal}"/>

<div class="form-actions btn-toolbar">
    <g:form>
        <input type="hidden" name="offset" value="${params.offset ?: ''}"/>
        <input type="hidden" name="max" value="${params.max ?: ''}"/>
        <input type="hidden" name="sort" value="${params.sort ?: ''}"/>
        <input type="hidden" name="order" value="${params.order ?: ''}"/>

        <g:each in="${selection.selectionMap}" var="entry">
            <input type="hidden" name="${entry.key}" value="${entry.value}"/>
        </g:each>

        <crm:selectionMenu visual="primary"/>

        <g:if test="${crmOrderTotal}">
            <select:link action="export" accesskey="p" selection="${selection}" class="btn btn-info">
                <i class="icon-print icon-white"></i>
                <g:message code="crmOrder.button.export.label" default="Print/Export"/>
            </select:link>
        </g:if>

        <div class="btn-group">
            <crm:button type="link" action="create" visual="success" icon="icon-file icon-white"
                        label="crmOrder.button.create.label" permission="crmOrder:create"/>
        </div>
    </g:form>
</div>

</body>
</html>
