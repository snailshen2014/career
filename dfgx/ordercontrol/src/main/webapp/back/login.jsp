<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org"
      xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
    <title>Spring Security Example </title>
</head>
<body>
<c:if test="${param.error}">
    用户名或密码错
</c:if>

<c:if test="${param.logout}">
    用户名或密码错
</c:if>
<form action="${pageContext.request.contextPath}/login" method="post">
    <div><label> 用户名 : <input type="text" name="username"/> </label></div>
    <div><label> 密  码 : <input type="password" name="password"/> </label></div>
    <div><input type="submit" value="登录"/></div>
</form>
</body>
</html>