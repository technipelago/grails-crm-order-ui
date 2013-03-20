<table class="table table-striped">
    <thead>
    <tr>
        <th><g:message code="crmOrder.number.label" default="Number"/>
        <th><g:message code="crmOrder.orderDate.label" default="Order Date"/>
        <th><g:message code="crmOrder.orderStatus.label" default="Status"/>
        <th><g:message code="crmOrder.deliveryDate.label" default="Delivery Date"/>
        <th><g:message code="crmOrder.delivery.label" default="Delivery Address"/>
        <th><g:message code="crmOrder.totalAmount.label" default="Order Value"/>
    </tr>
    </thead>
    <tbody>
    <g:each in="${result}" var="crmOrder">
        <tr>

            <td>
                <g:link controller="crmOrder" action="show" id="${crmOrder.id}">
                    ${fieldValue(bean: crmOrder, field: "number")}
                </g:link>
            </td>

            <td>
                <g:link controller="crmOrder" action="show" id="${crmOrder.id}">
                    <g:formatDate type="date" date="${crmOrder.orderDate}"/>
                </g:link>
            </td>

            <td>
                <g:fieldValue bean="${crmOrder}" field="orderStatus"/>
            </td>

            <td>
                <g:formatDate type="date" date="${crmOrder.deliveryDate}"/>
            </td>

            <td>
                ${fieldValue(bean: crmOrder, field: "delivery")}
            </td>
            <td style="font-weight:bold;text-align: right;">
                <g:formatNumber type="currency" currencyCode="SEK" number="${crmOrder.totalAmountVAT}"
                                maxFractionDigits="0"/>
            </td>
        </tr>
    </g:each>
    </tbody>
</table>