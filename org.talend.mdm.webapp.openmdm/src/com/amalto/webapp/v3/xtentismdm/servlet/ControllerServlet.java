// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.v3.xtentismdm.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.core.util.Util;
import com.amalto.webapp.core.util.SessionListener;
import com.amalto.webapp.core.util.WebappForbiddenLoginException;
import com.amalto.webapp.core.util.WebappRepeatedLoginException;

public class ControllerServlet extends com.amalto.webapp.core.servlet.GenericControllerServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ControllerServlet.class);

    private static final Messages MESSAGES = MessagesFactory.getMessages("com.amalto.webapp.v3.xtentismdm.servlet.messages", //$NON-NLS-1$
            ControllerServlet.class.getClassLoader());

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doPost(arg0, arg1);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = null;
        Locale locale = req.getLocale();

        String language = "fr"; //$NON-NLS-1$
        if (locale.getLanguage() != null) {
            language = locale.getLanguage();
        }
        if (req.getSession().getAttribute("language") != null) { //$NON-NLS-1$
            language = (String) req.getSession().getAttribute("language"); //$NON-NLS-1$
        }
        try {
            String lang = getDefaultLanguage();
            if (lang != null && !"".equals(lang.trim())) { //$NON-NLS-1$
                language = lang;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        if (req.getParameter("language") != null) { //$NON-NLS-1$
            language = req.getParameter("language"); //$NON-NLS-1$
            req.getSession().setAttribute("language", language); //$NON-NLS-1$
        }

        req.getSession().setAttribute("language", language); //$NON-NLS-1$
        res.setContentType("text/html; charset=UTF-8"); //$NON-NLS-1$
        res.setHeader("Content-Type", "text/html; charset=UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$

        locale = new Locale(language);
        PrintWriter out = res.getWriter();

        try {
            // see 0013864
            username = com.amalto.webapp.core.util.Util.getAjaxSubject().getUsername();
            if (MDMConfiguration.getAdminUser().equals(username)) {
                throw new WebappForbiddenLoginException(MESSAGES.getMessage(locale, "login.exception.forbidden", username)); //$NON-NLS-1$);
            }

            // restore the session timeout
            if (req.getSession().getAttribute("sessionTimeOut") != null) {
                req.getSession().setMaxInactiveInterval((Integer) req.getSession().getAttribute("sessionTimeOut"));
            }

            // Dispatch call
            String jsp = req.getParameter("action"); //$NON-NLS-1$  
            if ("logout".equals(jsp)) { //$NON-NLS-1$
                req.getSession().invalidate();
                res.sendRedirect("../index.html"); //$NON-NLS-1$
            } else {
                SessionListener.registerUser(username, req.getSession().getId());

                String target = req.getParameter("target"); //$NON-NLS-1$
                // when parameter no equals "original" to redirect to /general/secure/
                if (!"original".equals(target)) { //$NON-NLS-1$
                    res.sendRedirect("/general/secure/"); //$NON-NLS-1$
                    return;
                }
                String html = "<html>\n" + "<head>\n" + "<title>Talend MDM</title>\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        + "<meta name=\"gwt:property\" content=\"locale=" + language + "\" >\n" + super.getCommonImport(); //$NON-NLS-1$ //$NON-NLS-2$
                html += super.getJavascriptImportsHtml();
                html += "<script type=\"text/javascript\" src=\"/welcome/secure/js/Welcome.js\"></script>\n"; //$NON-NLS-1$
                html += "<script type=\"text/javascript\" src=\"/welcome/secure/dwr/interface/WelcomeInterface.js\"></script>\n"; //$NON-NLS-1$
                html += "<script type=\"text/javascript\" src=\"/talendmdm/secure/js/conf.js\"></script>\n"; //$NON-NLS-1$
                html += "<script type=\"text/javascript\" src=\"/talendmdm/secure/js/actions.js\"></script>\n"; //$NON-NLS-1$
                html += "<script type=\"text/javascript\" src=\"/talendmdm/secure/dwr/interface/ActionsInterface.js\"></script>\n"; //$NON-NLS-1$
                html += "<script type=\"text/javascript\" src=\"/talendmdm/secure/dwr/interface/WidgetInterface.js\"></script>\n"; //$NON-NLS-1$

                // LOGGER
                html += "<link type=\"text/css\" rel=\"stylesheet\" href=\"/core/secure/yui-2.4.0/build/logger/assets/logger.css\" ></link>"; //$NON-NLS-1$
                html += "<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/logger/logger.js\"></script>"; //$NON-NLS-1$
                // CONTAINER
                html += "<link type=\"text/css\" rel=\"stylesheet\" href=\"/core/secure/yui-2.4.0/build/container/assets/container.css\" ></link>"; //$NON-NLS-1$
                html += "<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/container/container.js\"></script>"; //$NON-NLS-1$

                html += "</head>\n" + //$NON-NLS-1$

                        getBody(language, req) + "</html>\n"; //$NON-NLS-1$

                out.write(html);
            }

        } catch (WebappRepeatedLoginException e) {
            req.getSession().invalidate();
            String html = getHtmlError(req.getContextPath(),
                    MESSAGES.getMessage(locale, "login.exception.repeated", username), locale, username, true); //$NON-NLS-1$
            out.write(html);
        } catch (Exception e) {
            req.getSession().invalidate();
            String message = e.getLocalizedMessage();
            if (message == null) {
                message = MESSAGES.getMessage(locale, "error.occured");
            }
            String html = getHtmlError(req.getContextPath(), message, locale, username, false);
            out.write(html);
        }
    }

    @Override
    protected String getBody(String language, HttpServletRequest request) {
        LinkedHashMap<String, String> map = this.getLanguageMap();
        Set<String> set = map.keySet();
        String html = ""; //$NON-NLS-1$
        for (String key : set) {
            String value = map.get(key);
            if (key.equals(language)) {
                html += "       <option value=\"" + key + "\" selected>" + value + "</option>\n"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            } else {
                html += "       <option value=\"" + key + "\">" + value + "</option>\n"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            }

        }
        String enterprise = Util.isEnterprise() ? "color:#EE0000;\"> Enterprise<br/>Edition" //$NON-NLS-1$
                : "color:#B4DC10;\">Community <br/> Edition"; //$NON-NLS-1$
        return

        "<body id=\"genericUI\" style=\"font:13px tahoma,verdana,helvetica\">\n" //$NON-NLS-1$
                + "<div id=\"header\" class=\"generic-header-background\">\n" + //$NON-NLS-1$

                "<img src=\"" //$NON-NLS-1$
                + request.getContextPath()
                + "/secure/img/header-back-title.png\"/>\n" //$NON-NLS-1$
                + "<div id=\"username-div\"  class=\"username\"></div>\n" //$NON-NLS-1$
                + "<div><table style=\"position: absolute;top: -2px;right:1px;\">" //$NON-NLS-1$
                + "<td><div><img src=\"" //$NON-NLS-1$
                + request.getContextPath()
                + "/secure/img/logo-mdm.png\"/></div></td>\n" //$NON-NLS-1$
                + "<td><div style=\"font: bold 13px tahoma,verdana,helvetica;" //$NON-NLS-1$
                + enterprise
                + "</div></td>\n" //$NON-NLS-1$
                + "<td><div>" //$NON-NLS-1$
                + "&nbsp;&nbsp;" //$NON-NLS-1$
                + "</div></td>\n" //$NON-NLS-1$
                + "<td><div><select style=\" font: normal  11px tahoma,verdana,helvetica; right: 5px\" id=\"languageSelect\" onchange=\"amalto.core.switchLanguage();\">\n" //$NON-NLS-1$
                + html + "  </select></div></td>" //$NON-NLS-1$
                + "<td><div id=\"logout-btn\" ></div></td>\n" //$NON-NLS-1$
                + "<td><div style=\"float position:relative;top: 1px;\" ><a href='" //$NON-NLS-1$
                + request.getContextPath() + "/secure/?action=logout' id='logout-btn' class='logout-btn'></a></div></td>\n" //$NON-NLS-1$
                + "</tr>\n" //$NON-NLS-1$
                + "</table></div>" //$NON-NLS-1$
                + "</div>\n" //$NON-NLS-1$
                + "<div id=\"menus\" class=\"menus-list\"></div>\n" //$NON-NLS-1$
                + "<div id=\"centerdiv\"></div>\n" //$NON-NLS-1$
                + "<div id=\"statusdiv\"></div>\n" //$NON-NLS-1$
                + "<input type=\"hidden\" id=\"contextPath\" value=" //$NON-NLS-1$
                + request.getContextPath() + "/>\n" //$NON-NLS-1$
                + "<input type=\"hidden\" id=\"serverPath\" value=" //$NON-NLS-1$
                + request.getScheme() + "://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/>\n" + "</body>"; //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
    }

    private LinkedHashMap<String, String> getLanguageMap() {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        InputStream io = null;
        try {
            io = ControllerServlet.class.getResourceAsStream("languageSelection.xml"); //$NON-NLS-1$
            SAXReader reader = new SAXReader();
            Document document = reader.read(io);
            for (@SuppressWarnings("unchecked")
            Iterator<Element> iterator = document.getRootElement().elementIterator(); iterator.hasNext();) {
                Element element = iterator.next();
                String key = element.attributeValue("value");//$NON-NLS-1$
                String value = element.getText();
                map.put(key, value);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (io != null) {
                try {
                    io.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return map;
    }

    @SuppressWarnings("nls")
    private String getHtmlError(String contextPath, String message, Locale locale, String username, boolean repeatedLogin) {
        StringBuilder html = new StringBuilder();
        html.append("<html>");
        html.append("<head>");
        html.append("<meta http-equiv='Content-Type' content='text/html;charset=UTF-8'/>");
        html.append("<link rel='stylesheet' type='text/css' href='").append(contextPath).append("/auth/loginPage.css'/>");
        html.append("<table width='100%' class='header1' border='0'>");
        html.append("<tr><td height='128' width='50%' class='logo'><img src='").append(contextPath)
                .append("/auth/logo.png'></td></tr>");
        html.append("<tr><td class='suiteName' id='suiteName' colspan='2' height='56'>Talend MDM</td></tr>");
        html.append("</table>");
        html.append("</head>");
        html.append("<body style='text-align: center;'>");

        String errorTitle = MESSAGES.getMessage(locale, "login.error");
        String backLogin = MESSAGES.getMessage(locale, "back.login");

        html.append("<h3>").append(errorTitle).append("</h3>");
        html.append("<p><font size='4' color='red'>").append(message).append("</font></p>");
        if (repeatedLogin) {
            String forceLogout = MESSAGES.getMessage(locale, "force.logout");
            html.append("<a href='").append(contextPath).append("/LogoutServlet?user=").append(username).append("'>")
                    .append(forceLogout).append("</a>");
            html.append("<br/><br/>");
        }
        html.append("<a href='").append(contextPath).append("/LogoutServlet'>").append(backLogin).append("</a>");

        html.append("</body>");
        html.append("</html>");
        return html.toString();
    }
}
