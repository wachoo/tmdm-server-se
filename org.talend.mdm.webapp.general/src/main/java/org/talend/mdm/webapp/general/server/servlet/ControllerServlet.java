// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.general.server.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.talend.mdm.webapp.general.server.util.Utils;

import com.amalto.core.util.LocaleUtil;
import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.webapp.core.util.WebappRepeatedLoginException;

public class ControllerServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final Messages MESSAGES = MessagesFactory.getMessages("org.talend.mdm.webapp.general.server.servlet.messages", //$NON-NLS-1$
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
        
        Locale locale = LocaleUtil.getLocale(req);
        String language = locale.getLanguage();
        // temporary
        if (!"en".equals(language) || !"fr".equals(language)) //$NON-NLS-1$ //$NON-NLS-2$
            language = "en"; //$NON-NLS-1$
        req.getSession().setAttribute("language", language); //$NON-NLS-1$
        res.setContentType("text/html; charset=UTF-8"); //$NON-NLS-1$
        res.setHeader("Content-Type", "text/html; charset=UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$

        PrintWriter out = res.getWriter();
        String username = null;
        
        try {

            username = com.amalto.webapp.core.util.Util.getAjaxSubject().getUsername();
            
            String html = "<html>\n" + "<head>\n" + "<title>Talend MDM</title>\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    + "<meta id=\"gwt:property\" name=\"gwt:property\" content=\"locale=" + language + "\" >\n"; //$NON-NLS-1$ //$NON-NLS-2$
            html += "<link rel=\"stylesheet\" type=\"text/css\" href=\"/general/resources/css/gxt-all.css\" />\n"; //$NON-NLS-1$
            html += "<script type=\"text/javascript\" language=\"javascript\" src=\"/general/general/general.nocache.js\"></script>"; //$NON-NLS-1$
            html += Utils.getCommonImport();

            List<String> imports = Utils.getJavascriptImport();
            for (String js : imports) {
                html += js;
            }

            html += "</head>\n" + //$NON-NLS-1$

                    getBody(language, req) + "</html>\n"; //$NON-NLS-1$

            out.write(html);

        } catch (WebappRepeatedLoginException e) {
            req.getSession().invalidate();
            String title = MESSAGES.getMessage(locale, "login.error"); //$NON-NLS-1$
            String message = e.getLocalizedMessage();
            String backLogin = MESSAGES.getMessage(locale, "back.login"); //$NON-NLS-1$
            String forceLogout = MESSAGES.getMessage(locale, "force.logout"); //$NON-NLS-1$
            StringBuilder html = new StringBuilder();
            html.append("<h3>").append(title).append("</h3>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<p><font size='4' color='red'>").append(message).append("</font></p>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<a href='").append("/talendmdm/LogoutServlet?user=").append(username).append("'>").append(forceLogout).append("</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            html.append("<br/><br/>"); //$NON-NLS-1$
            html.append("<a href='").append("/talendmdm/LogoutServlet'>").append(backLogin).append("</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            out.write(html.toString());
        } catch (Exception e) {
            req.getSession().invalidate();
            String title = MESSAGES.getMessage(locale, "login.error"); //$NON-NLS-1$
            String message = e.getLocalizedMessage();
            String backLogin = MESSAGES.getMessage(locale, "back.login"); //$NON-NLS-1$
            StringBuilder html = new StringBuilder();
            html.append("<h3>").append(title).append("</h3>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<p><font size='4' color='red'>").append(message).append("</font></p>"); //$NON-NLS-1$ //$NON-NLS-2$
            html.append("<a href='").append("/talendmdm/LogoutServlet'>").append(backLogin).append("</a>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            out.write(html.toString());
        }
    }

    @SuppressWarnings("nls")
    protected String getBody(String language, HttpServletRequest request) {
        return "<body>"
                + "<iframe src=\"javascript:''\" id=\"__gwt_historyFrame\" tabIndex='-1' style=\"position:absolute;width:0;height:0;border:0\"></iframe>"
                + "</body>";
    }
}
