<%@taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="baseURL" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html>
<head>
	<title>Register - Authoring Tools</title>
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
	<!-- Validator -->
	<script src="${baseURL}/resources/js/validator.min.js"></script>
</head>
<body>
<div class="wrapper">
	<h1>Authoring Tools Portal</h1>
	<h3>Register - Please complete the form below:</h3>
	<form id="regform" role="form" data-toggle="validator">
	<div class="col-md-6 form">
		<label>Full Name</label>
		<div class="form-group">
			<div class="form-group col-md-6">
		    	<input name="fName" id="fname" pattern="^([A-z.]){1,}$" maxlength="30" type="text" class="form-control" placeholder="first name" required>
		    	<div class="help-block with-errors">Up to 30 letters</div>
			</div>
			<div class="form-group col-md-6">
		    	<input name="lName" id="lname" pattern="^([A-z.]){1,}$" maxlength="30" type="text" class="form-control" placeholder="last name" required>
		    	<div class="help-block with-errors">Up to 30 letters</div>
			</div>
		</div>
		<div class="form-group">
	    	<label for="username" class="control-label">Username</label>
	    	<input name="login" id="username" pattern="^([_A-z0-9.]){3,}$" maxlength="30" type="text" class="form-control" placeholder="your preferred username" required>
	    	<div class="help-block with-errors">No less than 3, Up to 30 letters, numbers and underscores.</div>
		</div>
		<div class="form-group">
	    	<label for="email" class="control-label">Email</label>
	    	<input name="email" id="email" data-error="Oops, that email address is invalid." type="email" class="form-control" placeholder="your email address" required>
	    	<div class="help-block with-errors"></div>
		</div>
		<div class="form-group">
			<label for="password" class="control-label">Password</label>
			<input name="pass" id="password" data-minlength="8" type="password" class="form-control" placeholder="password" required>
			<span class="help-block">Minimum of 8 characters</span>
		</div>
		<div class="form-group">
			<label for="passwordagain" class="control-label">Password Confirm</label>
			<input id="passwordagain" data-match="#password" data-match-error="These don't match" type="password" class="form-control" placeholder="password Confirm" required>
			<div class="help-block with-errors"></div>
		</div>

	</div>
	<div class="col-md-6 form">
		<div class="form-group">
	    	<label for="org" class="control-label">Organization(Affiliation)</label>
	    	<input name="organization" id="org" maxlength="100" type="text" class="form-control" placeholder="organization title" required>
	    	<span class="help-block">Maximum of 100 characters</span>
		</div>
		<div class="form-group">
	    	<label for="acode" class="control-label">Affiliation Code</label>
	    	<input name="acode" id="acode" maxlength="10" type="text" class="form-control" placeholder="afficiation code" required>
	    	<span class="help-block">Maximum of 10 characters</span>
		</div>
		<div class="form-group">
			<label for="city" class="control-label">City</label>
			<input name="city" id="city" maxlength="30" type="text" class="form-control" placeholder="city name" required>
			<span class="help-block">Maximum of 30 characters</span>
		</div>
		<div class="form-group">
			<label for="country" class="control-label">Country</label>
			<input name="country" id="country" maxlength="50" type="text" class="form-control" placeholder="country" required>
			<span class="help-block">Maximum of 50 characters</span>
		</div>
	</div>
	<div style = "clear:both"></div>
	<div class="row">
		<div class="col-md-1"><button id="regdo" type="submit" class="btn btn-default">Register</button></div>
		<div class="col-md-11"><a href="${baseURL}/">Cancel</a></div>
	</div>


	</form>
</div>
<footer>PAWS Lab 2015</footer>
<script>
	<c:if test="${not empty goback}">${goback}</c:if>

	$('#regform').validator().on('submit', function (e){
		if(e.isDefaultPrevented()) {
			// handle the invalid form...
			return false;
		}else{
		    e.preventDefault();
		    var json = {
		    		"login" : $('#username').val(),
		    		"email" : $('#email').val(),
		    		"pass" : md5($('#password').val()),
		    		"fName" : $('#fname').val(),
		    		"lName" : $('#lname').val(),
		    		"organization" : $('#org').val(),
		    		"city" : $('#city').val(),
		    		"country" : $('#country').val(),
		    		"affiliation_code" : $('#acode').val()
		    	};
		    console.log(json);
		    regPost(json);
		}
	});
	
	function regPost(jsonObj){
		var req = JSON.stringify(jsonObj);
		$.ajax({
	        url: "${baseURL}/register",
	        data: req,
	        type: "POST",
	        dataType : 'json',
	        beforeSend: function(xhr) {
	            xhr.setRequestHeader("Accept", "application/json");
	            xhr.setRequestHeader("Content-Type", "application/json");
	            xhr.setRequestHeader("${_csrf.headerName}", "${_csrf.token}");
	        },
	        success: function(data) {
	        	var code = data.code;
	        	if(code === '0'){
	        		alert('Register completed!');
	        		location = "${baseURL}";
	        	}else if(code === '1'){
	        		alert('Username is already taken. Please try another one.');
	        	}else if(code === '2'){
	        		alert('Database error occurred. Please contact the administator.')
	        	}
	        },
	        error : function(jqXHR, textStatus, errorThrown) {
	            alert(jqXHR.status + " " + jqXHR.responseText);
	        }
	    });
	}
</script>
</body>
</html>