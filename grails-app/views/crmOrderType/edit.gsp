<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrderType.label', default: 'Order Type')}"/>
    <title><g:message code="crmOrderType.edit.title" args="[entityName, crmOrderType]"/></title>
</head>

<body>

<crm:header title="crmOrderType.edit.title" args="[entityName, crmOrderType]"/>

<div class="row-fluid">
    <div class="span9">

        <g:hasErrors bean="${crmOrderType}">
            <crm:alert class="alert-error">
                <ul>
                    <g:eachError bean="${crmOrderType}" var="error">
                        <li <g:if test="${error in org.springframework.validation.FieldError}">data-field-id="${error.field}"</g:if>><g:message
                                error="${error}"/></li>
                    </g:eachError>
                </ul>
            </crm:alert>
        </g:hasErrors>

        <g:form class="form-horizontal" action="edit"
                id="${crmOrderType?.id}">
            <g:hiddenField name="version" value="${crmOrderType?.version}"/>

            <f:with bean="crmOrderType">
                <f:field property="name" input-autofocus=""/>
                <f:field property="description"/>
                <f:field property="param"/>
                <f:field property="icon"/>
                <f:field property="orderIndex"/>
                <f:field property="enabled"/>
            </f:with>

            <div class="form-actions">
                <crm:button visual="primary" icon="icon-ok icon-white" label="crmOrderType.button.update.label"/>
                <crm:button action="delete" visual="danger" icon="icon-trash icon-white"
                            label="crmOrderType.button.delete.label"
                            confirm="crmOrderType.button.delete.confirm.message"
                            permission="crmOrderType:delete"/>
                <crm:button type="link" action="list"
                            icon="icon-remove"
                            label="crmOrderType.button.cancel.label"/>
            </div>
        </g:form>
    </div>

    <div class="span3">
        <crm:submenu/>
    </div>
</div>

</body>
</html>
