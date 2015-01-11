<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Locale"%>
<%@page import="com.amalto.core.util.Util"%>
<%@page import="com.amalto.core.util.Version"%>
<%
    String contextPath = request.getContextPath();
    Locale locale = request.getLocale();
    String language=locale.getLanguage();
    
    String _USERNAME_;
    String _PASSWORD_;
    String _UNIVERSE_;
    String _LOGIN_;
    String _ERROR_;
    
    if ("fr".equals(language)) {
         _USERNAME_ = "Identifiant";
         _PASSWORD_ = "Mot&nbsp;de&nbsp;passe";
         _UNIVERSE_ = "Version";
         _LOGIN_= "Connexion";
         _ERROR_ = "Mauvais identifiant ou mot de passe";
    } else if ("ru".equals(language)) {
         _USERNAME_ = "Имя&nbsp;пользователя";
         _PASSWORD_ = "Пароль";
         _UNIVERSE_ = "Версия";
         _LOGIN_= "Войти";
         _ERROR_ = "Неверное имя пользователя или пароль";       
    } else { 
         language="en"; //default language
         _USERNAME_ = "Login";
         _PASSWORD_ = "Password";
         _UNIVERSE_ = "Version";
         _LOGIN_ = "Login";
         _ERROR_ = "Login failed. Please check your login and password";
    }
    
    String editionTitle="TODO";
    String version=Version.getSimpleVersionAsString(this.getClass());
%>
<html>
<head>
<title>Talend MDM</title>
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
<meta name="description" content="Talend MDM login page"/>
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
    }body {
    margin: 0;
}
</style>

<link rel="stylesheet" type="text/css" href="<%= contextPath %>/auth/loginPage.css"/>

</head>
<body onload="document.loginform.j_username.focus();">
<table width="100%" class="header1" border="0">
      <tr><td height="128" width="50%" class="logo"><img src="<%= contextPath %>/auth/logo.png"></td><td class="version" id="loginVersion"><%=version%></td></tr>
      <tr><td class="suiteName" id="suiteName" colspan="2" height="56">Talend MDM</td></tr>
    </table>

    <table width="100%"  class="header2" border="0">
      <tr><td class="appName" id="appName">
      <%=editionTitle%>
      </td></tr>
    </table>
    <table width="100%" height="65%" cellpadding="0" cellspacing="1">
        <tr>
            <td valign="middle" align="center">
                <form method="POST" action="<%= contextPath %>/auth/j_security_check" name="loginform">
                    <br>
                    <table width="300" height="150" cellpadding="0" cellspacing="0"
                        class="form">
                        <tr>
                            <td colspan="2">
                                <c:if test="${'fail' eq param.auth}">
                                    <div style="text-align:center;color:red"><%= _ERROR_ %></div>
                                </c:if>
                            </td>
                        </tr>
                        <tr>
                            <td align="right" width="120"><%= _USERNAME_ %>:&nbsp;</td>
                            <td align="left"><input type="text" name="j_username" value="" onKeyDown="if(event.keyCode==13){document.all.login.click()}"/></td>
                        </tr>
                        <tr>
                            <td align="right" width="120"><%= _PASSWORD_ %>:&nbsp;</td>
                            <td align="left"><input type="password" name="j_password" value="" onKeyDown="if(event.keyCode==13){document.all.login.click()}"/>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="2" align="center"><input type="submit" name="login" value="<%=_LOGIN_%>"/></td>
                        </tr>
                    </table>
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                </form>
            </td>
        </tr>
    </table>
</body>
</html>