<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@page session="true"%>
<c:set var="baseURL" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>Sign In - Authoring Tools</title>
	<!-- jQuery -->
	<script src="http://code.jquery.com/jquery-1.11.2.min.js"></script>
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
</head>
<body>

<div class="wrapper">
	<h1>Authoring Tools Portal</h1>
 	<div class="lCol">
 		<h3>Please Sign In</h3>
		<c:if test="${not empty error}">
			<div class="alert alert-warning" role="alert">${error}</div>
		</c:if>
		<c:if test="${not empty msg}">
			<div class="alert alert-info" role="alert">${msg}</div>
		</c:if>
		<form id="loginf" name='loginForm' action="<c:url value='/j_spring_security_check'/>" method='POST'>
			<div class="form-group">
				<label>Username</label>
				<input type="text" class="form-control" name='username'/>
			</div>
			<div class="form-group">
				<label>Password</label>
				<input id="pwd" type="password" class="form-control" name='password'/>
			</div>
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
			<button id="loginb" type="submit" class="btn btn-default">Submit</button>
		</form>
	</div>
	<div class="rCol">
		<h3>Not a user yet?</h3>
		Click <a href="${baseURL}/register/">here</a> to register.
	</div>
</div>
<footer>PAWS Lab 2015</footer>
<script>
	<c:if test="${not empty goback}">${goback}</c:if>
	$('#loginb').click(function(){
		$('#pwd').val(md5($('#pwd').val()));
		$('#loginf').submit();
	});
</script>
</body>
</html>