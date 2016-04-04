<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="baseURL" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>Welcome - Authoring Tools</title>
	<!-- jQuery -->
	<script src="http://code.jquery.com/jquery-1.11.2.min.js"></script>
	<!-- cookiePlugin -->
	<script src="${baseURL}/resources/js/jquery.cookie.js"></script>
	<!-- md5 -->
	<script src="${baseURL}/resources/js/md5.js"></script>
	<!-- Latest compiled and minified CSS -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
	<!-- Optional theme -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap-theme.min.css">
	<!-- Latest compiled and minified JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.2/js/bootstrap.min.js"></script>
	<!-- Global Stylesheet -->
	<link rel="stylesheet" href="${baseURL}/resources/css/global.css">
	<script>
		$.cookie("${pageContext.request.userPrincipal.name}", "${hashVal}", {path : '/'});
	</script>
</head>
<body>
<c:url value="/j_spring_security_logout" var="logoutUrl" />
<form action="${logoutUrl}" method="post" id="logoutForm">
	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
</form>
<script>
	function formSubmit(){document.getElementById("logoutForm").submit();}
</script>
<div class="wrapper">
	<h1>Welcome to the Portal, ${pageContext.request.userPrincipal.name}</h1>
	<h3>Please click links below to redirect to the tool, or you can just
	<c:if test="${pageContext.request.userPrincipal.name != null}">
		<a href="javascript:formSubmit()"> logout</a>.
	</c:if>
	</h3>
	<ul class="appList">
		<sec:authorize access="hasRole('admin')">
		<li><a href="${baseURL}/redirect/admin?siteName=ca">
		<img src="${baseURL}/resources/img/ca.png"/>CourseAuthoring</a></li>
		<li><a href="${baseURL}/redirect/admin?siteName=ga">
		<img src="${baseURL}/resources/img/ga.png"/>GroupAuthoring</a></li>
		</sec:authorize>
		<sec:authorize access="hasRole('student')">
		<li><a href="${baseURL}/redirec/student?siteName=mg">
		<img src="${baseURL}/resources/img/mg.png"/>MasterGrid</a></li>
		</sec:authorize>
	</ul>
</div>
<footer>PAWS Lab 2015</footer>
</body>
</html>