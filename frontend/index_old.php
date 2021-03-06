<!DOCTYPE html>
<html>
	<head>
		<title>LibSys</title>
		<meta charset="utf-8">
		<link rel="stylesheet" href="css/style.css">
	</head>
	<body>
		<div class="header">
			<h1>INNOPOLIS LIBRARY SYSTEM</h1>
			<h3>Please sign in using id and password</h3>
		</div>
		<div class="login-field">
			<h4>SIGN IN</h4>
			<div class="login-in-field">
				<form id="login-form">
					<p>ID: <input class="text-form" name="userID" required></p>
					<p>Password: <input class="text-form" type="password" name="password" required></p>
					<p><input type="submit" value="SIGN IN" id="login-button"></p>
				</form>
			</div>
		</div>
      
      	<script>
			function showResponse(){
				var form = document.getElementById('login-form');
				var requestText='userID='+form.elements.userID.value+'&password='+ form.elements.password.value;
				
				var request = new XMLHttpRequest();
				
				request.open("POST", "http://api.awes-projects.com/users/getAccessToken/", false);
				request.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
				request.send(requestText);
				
				if(request.status == 200){
					alert("You have logged in! Your token is " + JSON.parse(request.responseText).accessToken);
                  var resp = JSON.parse(request.responseText);
                  var date = new Date(+resp.expirationDate * 1000);
                  document.cookie = "accessToken=" + resp.accessToken + "; path=; domain=.awes-projects.com; expires=" + date.toUTCString();
                  return false;
				} else {
					alert("Something went wrong! Server sent " + request.status + " " + request.statusText);
                  return false;
				}
			}
			
			document.getElementById('login-button').onclick = function(){showResponse();return false;}
		</script>
	</body>
</html>