 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<% String language = (String)request.getSession().getAttribute("language");  %>

<html>

<head>


<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
  <title>b2boxV3</title>

	<link rel="shortcut icon" href="<%= request.getContextPath() %>/secure/img/o-logo-16.gif" ></link>

	<!--  DWR -->
	<script language="javascript1.2" type='text/javascript' src='<%= request.getContextPath() %>/secure/dwr/engine.js'></script>
 	<script language="javascript1.2" type='text/javascript' src='<%= request.getContextPath() %>/secure/dwr/util.js'></script>

	<!-- YUI  base JS --> 
	<script type="text/javascript" src="<%= request.getContextPath() %>/secure/yui-2.4.0/build/utilities/utilities.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/secure/yui-2.4.0/build/yuiloader/yuiloader-beta.js"></script>
	   
	<!--  YUI EXT -->
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/secure/extjs-2.0/resources/css/ext-all.css" />
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/secure/extjs-2.0/resources/css/xtheme-default.css" />
	<script type="text/javascript" src="<%= request.getContextPath() %>/secure/extjs-2.0/yui/ext-yui-adapter.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/secure/extjs-2.0/ext-all-debug.js"></script> 

	<!--  b2box core  -->
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/secure/css<%= request.getContextPath() %>.css"></link>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/secure/css/amalto-actions.css"></link>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/secure/css/amalto-menus.css"></link>
	
    <script language="javascript1.2" type="text/javascript" src="<%= request.getContextPath() %>/secure/js/bgutil.js"></script> 
    <script language="javascript1.2" type="text/javascript" src="<%= request.getContextPath() %>/secure/js/Language.js"></script>
    <script language="javascript1.2" type="text/javascript" src="<%= request.getContextPath() %>/secure/js/core.js"></script>
	<script language="javascript1.2" type="text/javascript" src="<%= request.getContextPath() %>/secure/js/actions.js"></script>
    
	<script language="javascript1.2" type='text/javascript' src='<%= request.getContextPath() %>/secure/dwr/interface/CommonInterface.js'></script>		


</head>

<body id="genericUI" style="font-family:tahoma,verdana,helvetica">
	<div id="header" class="generic-header">
    	<img src="<%= request.getContextPath() %>/secure/img/b2box/amalto-logo.gif"/> 
    	
	    <div class="language-select"><select style="align: right; font: normal  11px tahoma,verdana,helvetica; right: 5px" id="languageSelect" onchange="amalto.b2box.core.switchLanguage();">
	    	<option value="en" <%if( "en".equals(language)){%>selected<% }  %>>English</option>
	    	<option value="fr" <%if( "fr".equals(language)){%>selected<% }  %>>Francais</option>
	    </select></div>
	    
	    <div id="username-div" class="username"></div>
	    <% 
	    String timestamp = "2007/07/04";
	   	%>
	    <div id="timestamp-div" class="username"></div>
    </div>
    
    <!-- 
    <div id="config" class="ylayout-inactive-content">
        <div id="config-tb"></div>
        <div id="config-body" class="partners-list"></div>
    </div>
     -->

    <div id="menus" class="menus-list"></div>
    
    <div id="centerdiv"></div>
    
    <div id="actions"></div>
    
    <div id="status"></div>
	   

	<input type="hidden" id="contextPath" value="<%= request.getContextPath() %>"/>
	<input type="hidden" id="serverPath" value="<%= request.getScheme()+"://"+request.getLocalAddr() +":"+request.getLocalPort() %>"/>
</body>
</html>







