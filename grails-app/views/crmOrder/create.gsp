<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrder.label', default: 'Order')}"/>
    <title><g:message code="crmOrder.create.title" args="[entityName]"/></title>
    <r:require module="datepicker"/>
    <script type="text/javascript">
        jQuery(document).ready(function () {
            <crm:datepicker/>
        });
    </script>
</head>

<body>

<crm:header title="crmOrder.create.title" args="[entityName]"/>

<g:hasErrors bean="${crmOrder}">
    <crm:alert class="alert-error">
        <ul>
            <g:eachError bean="${crmOrder}" var="error">
                <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                        error="${error}"/></li>
            </g:eachError>
        </ul>
    </crm:alert>
</g:hasErrors>

<g:form action="create">

    <f:with bean="crmOrder">

        <div class="row-fluid">

            <div class="span3">
                <div class="row-fluid">
                    <f:field property="number" input-class="span8" input-autofocus=""/>
                    <f:field property="orderDate">
                        <div class="inline input-append date"
                             data-date="${formatDate(format: 'yyyy-MM-dd', date: crmOrder.orderDate ?: new Date())}">
                            <g:textField name="orderDate" class="span8" size="10"
                                         placeholder="ÅÅÅÅ-MM-DD"
                                         value="${formatDate(format: 'yyyy-MM-dd', date: crmOrder.orderDate)}"/><span
                                class="add-on"><i
                                    class="icon-th"></i></span>
                        </div>
                    </f:field>
                    <f:field property="deliveryDate">
                        <div class="inline input-append date"
                             data-date="${formatDate(format: 'yyyy-MM-dd', date: crmOrder.deliveryDate ?: new Date())}">
                            <g:textField name="deliveryDate" class="span8" size="10"
                                         placeholder="ÅÅÅÅ-MM-DD"
                                         value="${formatDate(format: 'yyyy-MM-dd', date: crmOrder.deliveryDate)}"/><span
                                class="add-on"><i
                                    class="icon-th"></i></span>
                        </div>
                    </f:field>
                    <f:field property="orderStatus">
                        <g:select name="orderStatus.id" from="${statusList}" value="${crmOrder.orderStatus?.id}"
                                  optionKey="id" class="span12"/>
                    </f:field>
                    <f:field property="orderType">
                        <g:select name="orderType.id" from="${orderTypeList}" value="${crmOrder.orderType?.id}"
                                  optionKey="id" class="span12"/>
                    </f:field>

                    <f:field property="reference1" input-class="span12"/>
                    <f:field property="reference2" input-class="span12"/>
                    <f:field property="campaign" input-class="span12"/>
                </div>
            </div>

            <div class="span3">
                <div class="row-fluid">
                    <f:field property="customerName">
                        <g:textField name="customerFirstName" value="${crmOrder.customerFirstName}" class="span5"
                                     placeholder="Förnamn"/>
                        <g:textField name="customerLastName" value="${crmOrder.customerLastName}" class="span7"
                                     placeholder="Efternamn"/>
                    </f:field>
                    <f:field property="customerCompany" input-class="span12"/>
                    <f:field property="invoice.address1" input-class="span12"/>
                    <f:field property="invoice.address2" input-class="span12"/>
                    <f:field property="invoice.postalCode" label="crmAddress.postalAddress.label">
                        <g:textField name="invoice.postalCode" value="${invoiceAddress?.postalCode}" class="span4"/>
                        <g:textField name="invoice.city" value="${invoiceAddress?.city}" class="span8"/>
                    </f:field>
                    <f:field property="customerTel" input-class="span12"/>
                    <f:field property="customerEmail" input-class="span12"/>
                </div>
            </div>

            <div class="span3">
                <div class="row-fluid">
                    <f:field property="deliveryType">
                        <g:select name="deliveryType.id" from="${deliveryTypeList}" value="${crmOrder.deliveryType?.id}"
                                  optionKey="id" class="span12"/>
                    </f:field>
                    <f:field property="delivery.addressee" input-class="span12"/>
                    <f:field property="delivery.address1" input-class="span12"/>
                    <f:field property="delivery.address2" input-class="span12"/>
                    <f:field property="delivery.postalCode" label="crmAddress.postalAddress.label">
                        <g:textField name="delivery.postalCode" value="${deliveryAddress?.postalCode}" class="span4"/>
                        <g:textField name="delivery.city" value="${deliveryAddress?.city}" class="span8"/>
                    </f:field>
                    <f:field property="reference3" input-class="span12"/>
                    <f:field property="reference4" input-class="span12"/>
                </div>
            </div>

            <div class="span3">
            </div>

        </div>


        <div class="form-actions">
            <crm:button visual="success" icon="icon-ok icon-white" label="crmOrder.button.save.label"/>
        </div>

    </f:with>

</g:form>

</body>
</html>
