<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrderStatus.label', default: 'Order Status')}"/>
    <title><g:message code="crmOrderStatus.edit.title" args="[entityName, crmOrderStatus]"/></title>
</head>

<body>

<crm:header title="crmOrderStatus.edit.title" args="[entityName, crmOrderStatus]"/>

<div class="row-fluid">
    <div class="span9">

        <g:hasErrors bean="${crmOrderStatus}">
            <crm:alert class="alert-error">
                <ul>
                    <g:eachError bean="${crmOrderStatus}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                error="${error}"/></li>
                    </g:eachError>
                </ul>
            </crm:alert>
        </g:hasErrors>

        <g:form class="form-horizontal" action="edit"
                id="${crmOrderStatus?.id}">
            <g:hiddenField name="version" value="${crmOrderStatus?.version}"/>

            <f:with bean="crmOrderStatus">
                <f:field property="name" input-autofocus=""/>
                <f:field property="description"/>
                <f:field property="param"/>
                <f:field property="icon"/>
                <f:field property="orderIndex"/>
                <f:field property="enabled"/>
            </f:with>

            <div class="form-actions">
                <crm:button visual="primary" icon="icon-ok icon-white" label="crmOrderStatus.button.update.label"/>
                <crm:button action="delete" visual="danger" icon="icon-trash icon-white"
                            label="crmOrderStatus.button.delete.label"
                            confirm="crmOrderStatus.button.delete.confirm.message"
                            permission="crmOrderStatus:delete"/>
                <crm:button type="link" action="list"
                            icon="icon-remove"
                            label="crmOrderStatus.button.cancel.label"/>
            </div>
        </g:form>
    </div>

    <div class="span3">
        <crm:submenu/>
    </div>
</div>

</body>
</html>
