<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ tag body-content="empty" %>
<%@ attribute name="topic" required="true" type="org.jtalks.jcommune.model.entity.Topic" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt_rt" %>
<%@ taglib prefix="jtalks" uri="http://www.jtalks.org/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%--actual icon depends on both new posts presence and topic closed status--%>
<c:if test="${topic.hasUpdates}">
    <%--if there are new posts this icon should be a link--%>
    <a href="${pageContext.request.contextPath}/posts/${topic.firstUnreadPostId}">
</c:if>
<c:if test="${topic.hasUpdates && topic.closed}">
    <c:set var="iconName" value="closed-new-posts.png"/>
    <c:set var="titleCode" value="label.topic.new_posts"/>
</c:if>
<c:if test="${!topic.hasUpdates && topic.closed}">
    <c:set var="iconName" value="closed-no-new-posts.png"/>
    <c:set var="titleCode" value="label.topic.no_new_posts"/>
</c:if>
<c:if test="${topic.hasUpdates && !topic.closed}">
    <c:set var="iconName" value="new-posts.png"/>
    <c:set var="titleCode" value="label.topic.new_posts"/>
</c:if>
<c:if test="${!topic.hasUpdates && !topic.closed}">
    <c:set var="iconName" value="no-new-posts.png"/>
    <c:set var="titleCode" value="label.topic.no_new_posts"/>
</c:if>
<img class="status-img"
     src="${pageContext.request.contextPath}/resources/images/${iconName}"
     title="<spring:message code="${titleCode}"/>"/>
<c:if test="${topic.hasUpdates}">
    </a>
</c:if>
