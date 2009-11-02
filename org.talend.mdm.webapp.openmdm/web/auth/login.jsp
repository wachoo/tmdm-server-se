 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Locale"%>
<%
	String contextPath = request.getContextPath();
	Locale locale = request.getLocale();
	String language=locale.getLanguage();
	
	//if language is empty or unsupported
	if(language==""||!(language=="en"||language=="fr")){
	    language="en"; //default language
	}
	
	//default en
	String _USERNAME_ = "Login";
	String _PASSWORD_ = "Password";
	String _UNIVERSE_ ="Universe";
	String _ERROR = "Login failed. Please check your login and password";
	
	if ("fr".equals(language)) {
		 _USERNAME_ = "Identifiant";
		 _PASSWORD_ = "Mot&nbsp;de&nbsp;passe";
		 _ERROR = "Mauvais identifiant ou mot de passe";
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

<!--  DWR -->
<script language="javascript1.2" type='text/javascript' src='<%= request.getContextPath() %>/login/dwr/engine.js'></script>
<script language="javascript1.2" type='text/javascript' src='<%= request.getContextPath() %>/login/dwr/util.js'></script>
<script language="javascript1.2" type='text/javascript' src='<%= request.getContextPath() %>/login/dwr/interface/LoginInterface.js'></script>
<script type="text/javascript">
function f_submit(){	
	
	var username=document.loginform.j_username.value;
	var password=document.loginform.j_password.value;
	var universe=document.loginform.j_universe.value;
	if(universe!=''&&universe=='HEAD')universe='';
	
	if(universe){
		document.loginform.j_username.value=universe+"/"+username;
	}	
	//alert(document.loginform.j_username.value);
	document.loginform.submit();
}

function getUniverseList()
{

    LoginInterface.getUniverseNames({
	    callback:function(data) { 
	      DWRUtil.removeAllOptions("j_universe");
	      DWRUtil.addOptions("j_universe",data);
	    },
	    errorHandler:function(message) { alert(message); },
	    timeout:10000
    });
    
}

getUniverseList();

</script>

</head>
<body onload="document.loginform.j_username.focus();">


	<table width="100%" height="100%" cellpadding="0" cellspacing="1">
		<tr>
			<td valign="middle" align="center">
				<img src="<%= contextPath %>/auth/logo-big-talend.jpg"/>
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
							<td align="right" width="120"><%= _UNIVERSE_ %>:&nbsp;</td>
							<td align="left">
							<!--<input type="text" name="j_universe" value="" />-->
							<select id="j_universe" name="j_universe" onChange="getUniverseList()"/>
							</td>
						</tr>						
						<tr>
							<td colspan="2" align="center"><input type="submit" name="login" value="Login" onclick="f_submit()"/></td>
						</tr>
					</table>
				</form>
	
			</td>
		</tr>
	</table>

</body>
</html>
