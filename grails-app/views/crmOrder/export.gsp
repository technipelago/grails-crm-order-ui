<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrder.label', default: 'Order')}"/>
    <title><g:message code="crmOrder.export.title" args="[entityName]"/></title>
</head>

<body>

<crm:header title="crmOrder.export.title" subtitle="crmOrder.export.subtitle" args="[entityName]"/>

<g:each in="${layouts?.sort{it.name}}" var="l">
    <g:form action="export" class="well">
        <input type="hidden" name="q" value="${select.encode(selection: selection)}"/>
        <input type="hidden" name="ns" value="${l.ns}"/>
        <input type="hidden" name="topic" value="${l.topic}"/>
        <input type="hidden" name="template" value="${l.template}"/>

        <div class="row-fluid">
            <div class="span7">
                <h3>${l.name?.encodeAsHTML()}</h3>

                <p class="lead">
                    ${l.description?.encodeAsHTML()}
                </p>

                <button type="submit" class="btn btn-info">
                    <i class="icon-ok icon-white"></i>
                    <g:message code="crmOrder.button.select.label" default="Select"/>
                </button>
            </div>

            <div class="span5">
                <g:if test="${l.thumbnail}">
                    <img src="${l.thumbnail}" class="pull-right"/>
                </g:if>
            </div>
        </div>

    </g:form>
</g:each>

<div class="form-actions">
    <select:link action="list" selection="${selection}" class="btn">
        <i class="icon-remove"></i>
        <g:message code="crmOrder.button.back.label" default="Back"/>
    </select:link>
</div>

</body>
</html>
