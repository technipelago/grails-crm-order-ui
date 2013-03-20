<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmDeliveryType.label', default: 'Delivery Type')}"/>
    <title><g:message code="crmDeliveryType.edit.title" args="[entityName, crmDeliveryType]"/></title>
</head>

<body>

<crm:header title="crmDeliveryType.edit.title" args="[entityName, crmDeliveryType]"/>

<div class="row-fluid">
    <div class="span9">

        <g:hasErrors bean="${crmDeliveryType}">
            <crm:alert class="alert-error">
                <ul>
                    <g:eachError bean="${crmDeliveryType}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                error="${error}"/></li>
                    </g:eachError>
                </ul>
            </crm:alert>
        </g:hasErrors>

        <g:form class="form-horizontal" action="edit"
                id="${crmDeliveryType?.id}">
            <g:hiddenField name="version" value="${crmDeliveryType?.version}"/>

            <f:with bean="crmDeliveryType">
                <f:field property="name" input-autofocus=""/>
                <f:field property="description"/>
                <f:field property="param"/>
                <f:field property="icon"/>
                <f:field property="orderIndex"/>
                <f:field property="enabled"/>
            </f:with>

            <div class="form-actions">
                <crm:button visual="primary" icon="icon-ok icon-white" label="crmDeliveryType.button.update.label"/>
                <crm:button action="delete" visual="danger" icon="icon-trash icon-white"
                            label="crmDeliveryType.button.delete.label"
                            confirm="crmDeliveryType.button.delete.confirm.message"
                            permission="crmDeliveryType:delete"/>
                <crm:button type="link" action="list"
                            icon="icon-remove"
                            label="crmDeliveryType.button.cancel.label"/>
            </div>
        </g:form>
    </div>

    <div class="span3">
        <crm:submenu/>
    </div>
</div>

</body>
</html>
