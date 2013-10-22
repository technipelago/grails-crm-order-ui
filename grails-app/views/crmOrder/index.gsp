<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrder.label', default: 'Order')}"/>
    <title><g:message code="crmOrder.find.title" args="[entityName]"/></title>
    <r:require module="datepicker"/>
    <script type="text/javascript">
        jQuery(document).ready(function () {
            $('.date').datepicker({weekStart: 1});
        });
    </script>
</head>

<body>

<crm:header title="crmOrder.find.title" args="[entityName]"/>

<g:form action="list">

    <div class="row-fluid">

        <f:with bean="cmd">
            <div class="span4">
                <f:field property="number" label="crmOrder.number.label"
                         input-class="input-large" input-autofocus=""
                         input-placeholder="${message(code: 'crmOrderQueryCommand.number.placeholder', default: '')}"/>

                <f:field property="fromDate" label="crmOrder.query.fromDate.label">
                    <div class="inline input-append date"
                         data-date="${cmd.fromDate ?: formatDate(format: 'yyyy-MM-dd', date: new Date())}">
                        <g:textField name="fromDate" class="input-medium" size="10"
                                     placeholder="${message(code: 'crmOrderQueryCommand.fromDate.placeholder', default: '')}"
                                     value="${cmd.fromDate}"/><span
                            class="add-on"><i class="icon-th"></i></span>
                    </div>
                </f:field>

                <f:field property="toDate" label="crmOrder.query.toDate.label">
                    <div class="inline input-append date"
                         data-date="${cmd.toDate ?: formatDate(format: 'yyyy-MM-dd', date: new Date())}">
                        <g:textField name="toDate" class="input-medium" size="10"
                                     placeholder="${message(code: 'crmOrderQueryCommand.toDate.placeholder', default: '')}"
                                     value="${cmd.toDate}"/><span
                            class="add-on"><i class="icon-th"></i></span>
                    </div>
                </f:field>

                <f:field property="campaign" label="crmOrder.campaign.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.campaign.placeholder', default: '')}"/>

            </div>

            <div class="span4">
                <f:field property="customer" label="crmOrder.customer.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.customer.placeholder', default: '')}"/>

                <f:field property="address" label="crmOrder.invoice.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.address.placeholder', default: '')}"/>

                <f:field property="email" label="crmOrder.customerEmail.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.email.placeholder', default: '')}"/>

                <f:field property="telephone" label="crmOrder.customerTel.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.telephone.placeholder', default: '')}"/>
            </div>

            <div class="span4">
                <f:field property="type" label="crmOrder.orderType.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.type.placeholder', default: '')}"/>
                <f:field property="status" label="crmOrder.orderStatus.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.status.placeholder', default: '')}"/>
                <f:field property="delivery" label="crmOrder.deliveryType.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.delivery.placeholder', default: '')}"/>
                <f:field property="tags" label="crmOrder.tags.label"
                         input-class="input-large"
                         input-placeholder="${message(code: 'crmOrderQueryCommand.tags.placeholder', default: '')}"/>

            </div>

        </f:with>

    </div>

    <div class="form-actions btn-toolbar">
        <crm:selectionMenu visual="primary">
            <crm:button action="list" icon="icon-search icon-white" visual="primary"
                        label="crmOrder.button.search.label"/>
        </crm:selectionMenu>
        <crm:button type="link" group="true" action="create" visual="success" icon="icon-file icon-white"
                    label="crmOrder.button.create.label" permission="crmOrder:create"/>
        <g:link action="clearQuery" class="btn btn-link"><g:message code="crmOrder.button.query.clear.label"
                                                                    default="Reset fields"/></g:link>
    </div>

</g:form>

</body>
</html>
