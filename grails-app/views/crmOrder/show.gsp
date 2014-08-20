<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main">
    <g:set var="entityName" value="${message(code: 'crmOrder.label', default: 'Order')}"/>
    <title><g:message code="crmOrder.show.title" args="[entityName, crmOrder]"/></title>
    <r:script>
        $(document).ready(function () {
            $('#order-items tbody i').popover();
        });
    </r:script>
</head>

<body>

<g:set var="invoiceAddress" value="${crmOrder.invoice}"/>
<g:set var="deliveryAddress" value="${crmOrder.delivery}"/>

<div class="row-fluid">
<div class="span9">

<header class="page-header clearfix">
    <h1>
        <g:message code="crmOrder.show.title" args="[entityName, crmOrder]"/>
        <crm:user>
            <crm:favoriteIcon bean="${crmOrder}"/>
        </crm:user>
        <g:if test="${crmOrder.syncPending}">
            <i class="icon-share-alt"></i>
        </g:if>
        <g:if test="${crmOrder.syncPublished}">
            <i class="icon-warning-sign"></i>
        </g:if>
        <small>${(crmOrder.customerName ?: customerContact)?.encodeAsHTML()}</small>
    </h1>
</header>

<div class="tabbable">
<ul class="nav nav-tabs">
    <li class="active"><a href="#main" data-toggle="tab"><g:message code="crmOrder.tab.main.label"/></a>
    </li>
    <li><a href="#items" data-toggle="tab"><g:message code="crmOrder.tab.items.label"/><crm:countIndicator
            count="${crmOrder.items.size()}"/></a>
    </li>
    <crm:pluginViews location="tabs" var="view">
        <crm:pluginTab id="${view.id}" label="${view.label}" count="${view.model?.totalCount}"/>
    </crm:pluginViews>
</ul>

<div class="tab-content">
<div class="tab-pane active" id="main">
<div class="row-fluid">
    <div class="span3">
        <dl>
            <dt><g:message code="crmOrder.number.label" default="Number"/></dt>
            <dd><g:fieldValue bean="${crmOrder}" field="number"/></dd>

            <dt><g:message code="crmOrder.orderDate.label" default="Date"/></dt>
            <dd><g:formatDate type="date" date="${crmOrder.orderDate}"/></dd>

            <g:if test="${crmOrder.deliveryDate}">
                <dt><g:message code="crmOrder.deliveryDate.label" default="Date"/></dt>
                <dd><g:formatDate type="date" date="${crmOrder.deliveryDate}"/></dd>
            </g:if>

            <g:if test="${crmOrder?.orderStatus}">
                <dt><g:message code="crmOrder.orderStatus.label" default="Status"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="orderStatus"/></dd>
            </g:if>
            <g:if test="${crmOrder.orderType}">
                <dt><g:message code="crmOrder.orderType.label" default="Order Type"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="orderType"/></dd>
            </g:if>

            <g:if test="${crmOrder.reference2}">
                <dt><g:message code="crmOrder.reference2.label" default="Our Reference"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="reference2"/></dd>
            </g:if>

            <g:if test="${crmOrder.campaign}">
                <dt><g:message code="crmOrder.campaign.label" default="Campaign"/></dt>
                <dd><g:fieldValue bean="${crmOrder}" field="campaign"/></dd>
            </g:if>
        </dl>
    </div>

    <div class="span3">
        <dl>

            <g:if test="${crmOrder.customerName != invoiceAddress?.addressee}">
                <dt><g:message code="crmOrder.customer.label" default="Customer"/></dt>
                <dd><g:fieldValue bean="${crmOrder}" field="customerName"/></dd>
            </g:if>

            <dt><g:message code="crmOrder.invoice.label"/></dt>
            <g:if test="${crmOrder.customerCompany}">
                <dd><g:fieldValue bean="${crmOrder}" field="customerCompany"/></dd>
            </g:if>
            <g:render template="address" model="${[crmContact: customerContact, address: invoiceAddress]}"/>

            <g:if test="${crmOrder.customerEmail}">
                <dt><g:message code="crmOrder.customerEmail.label" default="Email"/></dt>

                <dd>
                    <a href="mailto:${crmOrder.customerEmail}"><g:decorate include="abbreviate" max="25">
                        <g:fieldValue bean="${crmOrder}" field="customerEmail"/>
                    </g:decorate></a>
                </dd>
            </g:if>

            <g:if test="${crmOrder.customerTel}">
                <dt><g:message code="crmOrder.customerTel.label" default="Telephone"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="customerTel"/></dd>
            </g:if>

            <g:if test="${crmOrder.reference1}">
                <dt><g:message code="crmOrder.reference1.label" default="Your Reference"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="reference1"/></dd>
            </g:if>
        </dl>

    </div>

    <div class="span3">
        <dl>
            <dt><g:message code="crmOrder.delivery.label"/></dt>
            <g:render template="address"
                      model="${[crmContact: deliveryContact, address: deliveryAddress ?: invoiceAddress]}"/>

            <g:if test="${crmOrder.reference3}">
                <dt><g:message code="crmOrder.reference3.label" default="Reference 3"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="reference3"/></dd>
            </g:if>

            <g:if test="${crmOrder.reference4}">
                <dt><g:message code="crmOrder.reference4.label" default="Reference 4"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="reference4"/></dd>
            </g:if>

            <g:if test="${crmOrder.deliveryType}">
                <dt><g:message code="crmOrder.deliveryType.label" default="Delivery Type"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="deliveryType"/></dd>
            </g:if>
        </dl>
    </div>

    <div class="span3">
        <dl>
            <dt><g:message code="crmOrder.totalAmount.label" default="Amount ex. VAT"/></dt>

            <dd><g:formatNumber type="currency" currencyCode="SEK"
                                number="${crmOrder.totalAmount}"/></dd>
            <dt><g:message code="crmOrder.totalVat.label" default="VAT"/></dt>

            <dd><g:formatNumber type="currency" currencyCode="SEK"
                                number="${crmOrder.totalVat}"/></dd>

            <dt><g:message code="crmOrder.totalAmountVAT.label" default="Amount incl. VAT"/></dt>

            <dd><g:formatNumber type="currency" currencyCode="SEK"
                                number="${crmOrder.totalAmountVAT}"/></dd>

            <g:set var="cent" value="${Math.round(crmOrder.totalAmountVAT).intValue() - crmOrder.totalAmountVAT}"/>
            <g:if test="${cent > 0.005 || cent < -0.005}">
                <dt><g:message code="crmOrder.cent.label" default="Öresutjämning"/></dt>

                <dd><g:formatNumber type="currency" currencyCode="SEK" number="${cent}"/>
                </dd>
            </g:if>
            <dt><g:message code="crmOrder.paymentAmount.label" default="Totals inc. VAT"/></dt>

            <dd><h3 style="margin-top: 0;"><g:formatNumber type="currency" currencyCode="SEK"
                                                           number="${crmOrder.totalAmountVAT}"
                                                           maxFractionDigits="0"/></h3>
            </dd>

            <dt><g:message code="crmOrder.paymentStatus.label" default="Payment Status"/></dt>
            <dd>${message(code: 'crmOrder.paymentStatus.' + crmOrder.paymentStatus, default: crmOrder.paymentStatus.toString())}</dd>

            <g:if test="${crmOrder.paymentDate}">
                <dt><g:message code="crmOrder.paymentDate.label" default="Payed Date"/></dt>
                <dd><g:formatDate type="date" date="${crmOrder.paymentDate}"/></dd>
            </g:if>

            <g:if test="${crmOrder.paymentType}">
                <dt><g:message code="crmOrder.paymentType.label" default="Payment Type"/></dt>

                <dd>${message(code: 'crmOrder.paymentType.' + crmOrder.paymentType, default: crmOrder.paymentType)}</dd>
            </g:if>

            <g:if test="${crmOrder.paymentId}">
                <dt><g:message code="crmOrder.paymentId.label" default="Payment ID"/></dt>

                <dd><g:fieldValue bean="${crmOrder}" field="paymentId"/></dd>
            </g:if>

            <g:if test="${crmOrder.payedAmount}">
                <dt><g:message code="crmOrder.payedAmount.label" default="Payed Amount"/></dt>

                <dd><g:formatNumber type="currency" currencyCode="SEK"
                                    number="${crmOrder.payedAmount}"/></dd>
            </g:if>
        </dl>
    </div>

</div>

<div class="form-actions btn-toolbar">
    <g:form>
        <g:hiddenField name="id" value="${crmOrder.id}"/>

        <crm:selectionMenu location="crmOrder" visual="primary">
            <crm:button type="link" controller="crmOrder" action="index"
                        visual="primary" icon="icon-search icon-white"
                        label="crmOrder.find.label"/>
        </crm:selectionMenu>

        <crm:button type="link" action="edit" id="${crmOrder?.id}"
                    group="true" visual="warning" icon="icon-pencil icon-white"
                    label="crmOrder.button.edit.label" permission="crmOrder:edit">
        </crm:button>

        <crm:button type="link" action="create"
                    group="true" visual="success" icon="icon-file icon-white"
                    label="crmOrder.button.create.label"
                    title="crmOrder.button.create.help"
                    permission="crmOrder:create"/>

        <div class="btn-group">
            <select:link action="export" accesskey="p" params="${[namespace:'crmOrder']}" selection="${new URI('bean://crmOrderService/list?id=' + crmOrder.id)}" class="btn btn-info">
                <i class="icon-print icon-white"></i>
                <g:message code="crmOrder.button.export.label" default="Print/Export"/>
            </select:link>
        </div>

        <div class="btn-group">
            <button class="btn btn-info dropdown-toggle" data-toggle="dropdown">
                <i class="icon-info-sign icon-white"></i>
                <g:message code="crmOrder.button.view.label" default="View"/>
                <span class="caret"></span></button>
            <ul class="dropdown-menu">
                <g:if test="${selection}">
                    <li>
                        <select:link action="list" selection="${selection}" params="${[view: 'list']}">
                            <g:message code="crmOrder.show.result.label" default="Show result in list view"/>
                        </select:link>
                    </li>
                </g:if>
                <crm:hasPermission permission="crmOrder:createFavorite">
                    <crm:user>
                        <g:if test="${crmOrder.isUserTagged('favorite', username)}">
                            <li>
                                <g:link action="deleteFavorite" id="${crmOrder.id}"
                                        title="${message(code: 'crmOrder.button.favorite.delete.help', args: [crmOrder])}">
                                    <g:message code="crmContact.button.favorite.delete.label"/></g:link>
                            </li>
                        </g:if>
                        <g:else>
                            <li>
                                <g:link action="createFavorite" id="${crmOrder.id}"
                                        title="${message(code: 'crmOrder.button.favorite.create.help', args: [crmOrder])}">
                                    <g:message code="crmOrder.button.favorite.create.label"/></g:link>
                            </li>
                        </g:else>
                    </crm:user>
                </crm:hasPermission>
            </ul>
        </div>

    </g:form>
</div>

<crm:timestamp bean="${crmOrder}"/>

</div>

<div class="tab-pane" id="items">
    <g:render template="items" model="${[list: crmOrder.items]}"/>
</div>

<crm:pluginViews location="tabs" var="view">
    <div class="tab-pane tab-${view.id}" id="${view.id}">
        <g:render template="${view.template}" model="${view.model}" plugin="${view.plugin}"/>
    </div>
</crm:pluginViews>
</div>

</div>

</div>

<div class="span3">

    <g:render template="/tags" plugin="crm-tags" model="${[bean: crmOrder]}"/>

</div>
</div>

</body>
</html>
