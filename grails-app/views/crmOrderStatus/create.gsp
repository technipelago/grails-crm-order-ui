<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrderStatus.label', default: 'Order Status')}"/>
    <title><g:message code="crmOrderStatus.create.title" args="[entityName]"/></title>
</head>

<body>

<crm:header title="crmOrderStatus.create.title" args="[entityName]"/>

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

        <g:form class="form-horizontal" action="create">

            <f:with bean="crmOrderStatus">
                <f:field property="name" input-autofocus=""/>
                <f:field property="description"/>
                <f:field property="param"/>
                <f:field property="icon"/>
                <f:field property="orderIndex"/>
                <f:field property="enabled"/>
            </f:with>

            <div class="form-actions">
                <crm:button visual="primary" icon="icon-ok icon-white" label="crmOrderStatus.button.save.label"/>
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
