<%@ page language="java"%>
<%@page import="java.util.Locale"%>
<%
	String contextPath = request.getContextPath();
	Locale locale = request.getLocale();
	String language=locale.getLanguage();
	language="".equals(language)? "" : "fr"; //default language
	
	String _ERROR_ = "Vous n'�tes pas autorisé à  vous connecter à Denys";
	
	if ("en".equals(language)) {
		_ERROR_ = "You are not authorized to connect to b2box";
	}
	
%>



<html>
<head>
	<style>
		body { background-color:#FFFFFF;}
		td { color:#000000; font-family:verdana,arial,sans-serif; font-size: 12px; line-height:130%; }
		table.form
		{
			background-color:#FFFFFF;
			border-style: dashed;
			border-color: #000000;
			border-width: 1px;		
		}
	</style>
</head>
<body>
	<table width="100%" height="100%" cellpadding="0" cellspacing="1">
		<tr><td align="center"><%= _ERROR_ %></td></tr>
	</table>
</body>
</html>
