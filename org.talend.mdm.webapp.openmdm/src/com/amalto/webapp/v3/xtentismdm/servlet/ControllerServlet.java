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

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.util.Util;
import com.amalto.webapp.core.util.Messages;
import com.amalto.webapp.core.util.MessagesFactory;
import com.amalto.webapp.core.util.WebappForbiddenLoginException;
import com.amalto.webapp.core.util.WebappRepeatedLoginException;

public class ControllerServlet extends com.amalto.webapp.core.servlet.GenericControllerServlet {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ControllerServlet.class);

    private static final Messages MESSAGES = MessagesFactory.getMessages("com.amalto.webapp.v3.xtentismdm.servlet.messages", //$NON-NLS-1$
            ControllerServlet.class.getClassLoader());

    private String target;

    @Override
    public void init(ServletConfig config) throws ServletException {
        target = config.getInitParameter("target"); //$NON-NLS-1$
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
            if ("admin".equals(username)) {//$NON-NLS-1$		    
                throw new WebappForbiddenLoginException(MESSAGES.getMessage(locale, "login.exception.forbidden", username)); //$NON-NLS-1$);
            }
            LinkedHashMap<String, String> onlineUsers = ILocalUser.getOnlineUsers();
            if (onlineUsers.containsKey(username)) {

                if (onlineUsers.get(username) != null && req.getSession().getId() != null
                        && !onlineUsers.get(username).equals(req.getSession().getId())) {
                    throw new WebappRepeatedLoginException(MESSAGES.getMessage(locale, "login.exception.repeated", username)); //$NON-NLS-1$
                }

            }
            // Dispatch call
            String jsp = req.getParameter("action"); //$NON-NLS-1$	
            if ("logout".equals(jsp)) { //$NON-NLS-1$
                ILocalUser.getOnlineUsers().remove(username);
                req.getSession().invalidate();
                res.sendRedirect("../index.html"); //$NON-NLS-1$
            } else {

                ILocalUser.getOnlineUsers().put(username, req.getSession().getId());
                if (target != null && target.trim().length() != 0) {
                    if (!target.equals("/talendmdm/secure/")) { //$NON-NLS-1$
                        res.sendRedirect(target);
                        return;
                    }
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
            String title = MESSAGES.getMessage(locale, "login.error"); //$NON-NLS-1$
            String message = e.getLocalizedMessage();
            String backLogin = MESSAGES.getMessage(locale, "back.login"); //$NON-NLS-1$
            String forceLogout = MESSAGES.getMessage(locale, "force.logout"); //$NON-NLS-1$
            StringBuilder html = new StringBuilder();
            html.append("<h3>").append(title).append("</h3>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<p><font size='4' color='red'>").append(message).append("</font></p>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<a href='").append(req.getContextPath()).append("/LogoutServlet?user=").append(username).append("'>").append(forceLogout).append("</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            html.append("<br/><br/>"); //$NON-NLS-1$
            html.append("<a href='").append(req.getContextPath()).append("/LogoutServlet'>").append(backLogin).append("</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            out.write(html.toString());
        } catch (Exception e) {
            req.getSession().invalidate();
            String title = MESSAGES.getMessage(locale, "login.error"); //$NON-NLS-1$
            String message = e.getLocalizedMessage();
            String backLogin = MESSAGES.getMessage(locale, "back.login"); //$NON-NLS-1$
            StringBuilder html = new StringBuilder();
            html.append("<h3>").append(title).append("</h3>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<p><font size='4' color='red'>").append(message).append("</font></p>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<a href='").append(req.getContextPath()).append("/LogoutServlet'>").append(backLogin).append("</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            out.write(html.toString());
        }
    }

    @Override
    @SuppressWarnings("nls")
    protected String getBody(String language, HttpServletRequest request) {
        LinkedHashMap<String, String> map = this.getLanguageMap();
        Set<String> set = map.keySet();
        String html = "";
        for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
            String key = iterator.next();
            String value = map.get(key);
            if (key.equals(language)) {
                html += "		<option value=\"" + key + "\" selected>" + value + "</option>\n";
            } else {
                html += "		<option value=\"" + key + "\">" + value + "</option>\n";
            }

        }
        String enterprise = Util.isEnterprise() ? "color:#EE0000;\"> Enterprise<br/>Edition"
                : "color:#B4DC10;\">Community <br/> Edition";
        return

        "<body id=\"genericUI\" style=\"font:13px tahoma,verdana,helvetica\">\n"
                + "<div id=\"header\" class=\"generic-header-background\">\n" +

                "<img src=\""
                + request.getContextPath()
                + "/secure/img/header-back-title.png\"/>\n"
                + "<div id=\"username-div\"  class=\"username\"></div>\n"
                + "<div><table style=\"position: absolute;top: -2px;right:1px;\">"
                + "<td><div><img src=\""
                + request.getContextPath()
                + "/secure/img/logo-mdm.png\"/></div></td>\n"
                + "<td><div style=\"font: bold 13px tahoma,verdana,helvetica;"
                + enterprise
                + "</div></td>\n"
                + "<td><div>"
                + "&nbsp;&nbsp;"
                + "</div></td>\n"
                + "<td><div><select style=\" font: normal  11px tahoma,verdana,helvetica; right: 5px\" id=\"languageSelect\" onchange=\"amalto.core.switchLanguage();\">\n"
                + html
                + "	</select></div></td>"
                + "<td><div id=\"logout-btn\" ></div></td>\n"
                +

                "<td><div style=\"float position:relative;top: 1px;\" ><a href='"
                + request.getContextPath()
                + "/secure/?action=logout' id='logout-btn' class='logout-btn'></a></div></td>\n"
                +

                "</tr>\n"
                + "</table></div>"
                +

                "</div>\n"
                +

                "<div id=\"menus\" class=\"menus-list\"></div>\n"
                + "<div id=\"centerdiv\"></div>\n"
                + "<div id=\"statusdiv\"></div>\n"
                + "<input type=\"hidden\" id=\"contextPath\" value="
                + request.getContextPath()
                + "/>\n"
                + "<input type=\"hidden\" id=\"serverPath\" value="
                + request.getScheme()
                + "://" + request.getLocalAddr() + ":" + request.getLocalPort() + "/>\n" + "</body>";
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
            if (io != null)
                try {
                    io.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
        }
        return map;
    }
}
