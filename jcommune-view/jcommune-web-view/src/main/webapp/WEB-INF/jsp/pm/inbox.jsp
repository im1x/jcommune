<%--

    Copyright (C) 2011  JTalks.org Team
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.
    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.
    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<head>
    <script
            src="${pageContext.request.contextPath}/resources/javascript/custom/privateMessages.js"></script>
    <title><spring:message code="label.inbox"/></title>
</head>
<body>
<div class="container">
    <h2><spring:message code="label.pm_title"/></h2>
    <hr/>
    <div class="row">
        <div class="span2">
            <jsp:include page="../../template/newPrivateMessage.jsp"/>
            <jsp:include page="../../template/pmFolders.jsp"/>
        </div>
        <!-- /span2 -->

        <div class="span9">
            <div class="pm_buttons del">
                <a class="btn btn-danger" id="deleteCheckedPM"
                   href="${pageContext.request.contextPath}/pm">
                    <i class="icon-trash icon-white"></i>
                    <spring:message code="label.delete"/>
                </a>
                <form:form id="deleteForm" method="DELETE"/>
            </div>

            <table class="table table-bordered table-condensed">
                <thead>
                <th class="pm_header_check">
                    <input type="checkbox" class="check_all"/></th>

                <th class="pm_header_info"><i class="icon-user"></i>
                    <spring:message code="label.sender"/></th>

                <th><i class="icon-font"></i> <spring:message code="label.pm.title"/></th>

                <th class="pm_sending_date"><i class="icon-calendar"></i>
                    <spring:message code="label.sending_date"/></th>
                </thead>

                <tbody>

                <c:choose>
                    <c:when test="${!(empty pmList)}">
                        <c:forEach var="pm" items="${pmList}">
                            <c:choose>
                                <c:when test="${pm.read}">
                                    <tr id="${pm.id}" class="mess" >
                                </c:when>
                                <c:otherwise>
                                    <tr id="${pm.id}" class="mess pm_unread">
                                </c:otherwise>
                            </c:choose>
                            <td><input type="checkbox" id="${pm.id}" class="checker"/></td>
                            <td class="pm_user_to_from">
                                <a href="${pageContext.request.contextPath}/users/${pm.userFrom.id}">
                                    <c:out value="${pm.userFrom.username}"/>
                                </a>
                            </td>
                            <td>
                                <a href="${pageContext.request.contextPath}/pm/inbox/${pm.id}">
                                    <c:out value="${pm.title}"/></a>
                            </td>
                            <td>
                                <jtalks:format value="${pm.creationDate}"/>
                            </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="4"><spring:message code="label.inbox.empty"/></td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
        <!-- /span9 -->
    </div>
    <!-- /row -->
</div>
<!-- /container -->

<div class="footer_buffer"></div>
</body>
