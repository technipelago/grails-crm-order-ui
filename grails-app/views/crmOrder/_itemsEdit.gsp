<table id="item-list" class="table">
    <thead>
    <tr>
        <th><g:message code="crmOrderItem.productId.label"/></th>
        <th><g:message code="crmOrderItem.productName.label"/></th>
        <!--
        <th><g:message code="crmOrderItem.comment.label"/></th>
        -->
        <th><g:message code="crmOrderItem.quantity.label"/></th>
        <th><g:message code="crmOrderItem.unit.label"/></th>
        <th><g:message code="crmOrderItem.price.label"/></th>
        <th><g:message code="crmOrderItem.discount.label"/></th>
        <th><g:message code="crmOrderItem.vat.label"/></th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <g:each in="${bean.items}" var="item" status="row">
        <g:render template="item" model="${[bean: item, row: row, metadata: metadata]}"/>
    </g:each>
    </tbody>
    <tfoot>
    <tr>
        <td colspan="7">
            <g:if test="${bean.id}">
                <crm:button action="edit" visual="warning" icon="icon-ok icon-white" label="crmOrder.button.update.label"/>
            </g:if>
            <g:else>
                <crm:button visual="warning" icon="icon-ok icon-white" label="crmOrder.button.save.label"/>
            </g:else>
            <button type="button" class="btn btn-success" id="btn-add-item">
                <i class="icon-plus icon-white"></i>
                <g:message code="crmOrderItem.button.add.label" default="Add Item"/>
            </button>
        </td>
    </tr>
    </tfoot>
</table>