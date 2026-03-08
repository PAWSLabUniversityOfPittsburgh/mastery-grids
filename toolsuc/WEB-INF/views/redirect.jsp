<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="baseURL" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>Redirecting...</title>
</head>
<body>
This page is redirecting you...
<form action="${gaURL}" method="post" id="gaForm">
	<input type="hidden" name="email" value="${gaUsername}" />
	<input type="hidden" name="password" value="${gaPassword}" />
</form>
<script>${script}</script>
</body>
</html>