<%@ page import="org.talend.mdm.webapp.general.server.util.*, com.amalto.webapp.core.util.*, java.util.*"%>
<!doctype html>
<html>
  <head>
    <title>Talend MDM</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta name="gwt:property" content="locale=<%= request.getParameter("language") %>" >
    <title>Web Application Starter Project</title>
    
    <script type="text/javascript" language="javascript" src="general/general.nocache.js"></script>
    <%
	out.println(Utils.getCommonImport());
	List<String> imports = Utils.getJavascriptImport();
	for (String js : imports){
		out.print(js);
	}
    %>
    <link rel="stylesheet" type="text/css" href="resources/css/gxt-all.css" />
  </head>
  <body>
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
  </body>
</html>
