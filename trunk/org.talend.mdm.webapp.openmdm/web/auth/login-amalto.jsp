 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Locale"%>
<%
	String contextPath = request.getContextPath();
	Locale locale = request.getLocale();
	String language=locale.getLanguage();
	language="".equals(language)? "en" : language; //default language

	String _USERNAME_ = "Identifiant";
	String _PASSWORD_ = "Mot&nbsp;de&nbsp;passe";
	String _ERROR = "Mauvais identifiant ou mot de passe";
	
	if ("en".equals(language)) {
		_USERNAME_ = "Login";
		_PASSWORD_ = "Password";
		_ERROR = "Login failed. Please check your login and password";
	}
	
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<style>
	body { 
		background-color:#FFFFFF;
	}
	td { 
		color:#000000;
		font-family:verdana,arial,sans-serif;
		font-size: 12px;
		line-height:130%;
	}
	table.form {
		background-color:#FFFFFF;
		border-style: dashed;
		border-color: green;
		border-width: 1px;
		font-family: Trebuchet MS, sans-serif;
		padding-right:10px
	}
</style>
</head>
<body onload="document.loginform.j_username.focus();">


	<table width="100%" height="100%" cellpadding="0" cellspacing="1">
		<tr>
			<td valign="middle" align="center">
				<img src="<%= contextPath %>/auth/logo-big.jpg"/>
			</td>
			<td valign="middle" align="center">
				<form method="POST" action="j_security_check" name="loginform">
					<br>
					<table width="300" height="150" cellpadding="0" cellspacing="0"
						class="form">
						<tr>
							<td colspan="2">
							<% if(request.getParameter("error")!=null) {%>
							<div style="text-align:center;color:red"><%= _ERROR %></div>
							<% } %>
							</td>
						</tr>
						<tr>
							<td align="right" width="120"><%= _USERNAME_ %>:&nbsp;</td>
							<td align="left"><input type="text" name="j_username" value="" /></td>
						</tr>
						<tr>
							<td align="right" width="120"><%= _PASSWORD_ %>:&nbsp;</td>
							<td align="left"><input type="password" name="j_password" value="" />
							</td>
						</tr>
						<tr>
							<td colspan="2" align="center"><input type="submit" name="login" value="Login" /></td>
						</tr>
					</table>
				</form>
	
			</td>
		</tr>
	</table>

</body>
</html>
