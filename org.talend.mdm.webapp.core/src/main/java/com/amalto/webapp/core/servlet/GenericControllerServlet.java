package com.amalto.webapp.core.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.core.util.GxtFactory;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.core.webservice.WSLogout;

public abstract class GenericControllerServlet extends HttpServlet {

    private static final long serialVersionUID = -9096081513471307793L;

    private static final String GXT_PROPERTIES = "gxt.properties"; //$NON-NLS-1$

    private static final String EXCLUDING_PROPERTIES = "excluding.properties"; //$NON-NLS-1$

    /** a reference to the factory used to create Gxt instances */
    public static final GxtFactory gxtFactory = new GxtFactory(GXT_PROPERTIES, EXCLUDING_PROPERTIES);

    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doPost(arg0, arg1);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

        PrintWriter out = res.getWriter();
        try {

            Locale locale = req.getLocale();
            String language = Util.DEFAULT_LANGUAGE;
            if (locale.getLanguage() != null) {
                language = locale.getLanguage();
            }
            if (req.getSession().getAttribute("language") != null) { //$NON-NLS-1$
                language = (String) req.getSession().getAttribute("language"); //$NON-NLS-1$
            }

            res.setContentType("text/html; charset=UTF-8"); //$NON-NLS-1$
            res.setHeader("Content-Type", "text/html; charset=UTF-8"); //$NON-NLS-1$//$NON-NLS-2$

            // Dispatch call
            String action = req.getParameter("action"); //$NON-NLS-1$

            if ("logout".equals(action)) { //$NON-NLS-1$
                // logout the LocalUser cache
                try {
                    Util.getPort().logout(new WSLogout());
                } catch (Exception e) {
                    String err = "Unable to call logout() on the server side"; //$NON-NLS-1$
                    org.apache.log4j.Logger.getLogger(this.getClass()).warn(err, e);
                }
                // invalidate the session
                req.getSession().invalidate();
                res.sendRedirect("../index.html"); //$NON-NLS-1$
            } else {
                String html = "<html>" + "<head>" + "<title>Webapp core</title>" + getCommonImport(); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
                // getJavascriptImports
                html += getJavascriptImportsHtml();

                html += "</head>" + getBody(language, req) + "</html>"; //$NON-NLS-1$//$NON-NLS-2$

                out.write(html);

                // dispatch
                /*
                 * action = "js/core.jsp"; RequestDispatcher disp; disp = req.getRequestDispatcher(action); // forward
                 * the request to the dispatcher disp.forward(req, res);
                 */
                // disp.include(req,res);
            }

        } catch (Exception e) {
            out.write("<h1>ERROR</H1>"); //$NON-NLS-1$
            e.printStackTrace(out);
        } finally {
            // super.doPost(req, res);
        }
    }

    protected String getJavascriptImportsHtml() throws Exception {
        StringBuilder html = new StringBuilder();
        ArrayList<String> imports = getJavascriptImport();
        for (String jsImport : imports) {
            html.append(jsImport);
        }
        return html.toString();
    }

    private ArrayList<String> getJavascriptImport() throws Exception {
        ArrayList<String> imports = new ArrayList<String>();
        getJavascriptImportDetail(Menu.getRootMenu(), imports, 1, 1);
        // FIXME: This is a workaround for 4.2 only
        complementItemsbrowser(imports);
        return imports;
    }

    /**
     * DOC HSHU Comment method "containLegacyItemsbrowser".
     */
    private void complementItemsbrowser(ArrayList<String> imports) {
        boolean isItemsbrowserExist = false;
        boolean isItemsbrowser2Exist = false;
        for (String importMenu : imports) {
            if (importMenu.contains("src=\"/itemsbrowser/secure/js/ItemsBrowser.js\"")) { //$NON-NLS-1$
                isItemsbrowserExist = true;
            }
            if (importMenu.contains("src=\"/itemsbrowser2/secure/js/ItemsBrowser2.js\"")) { //$NON-NLS-1$
                isItemsbrowser2Exist = true;
            }
        }
        if (isItemsbrowser2Exist && !isItemsbrowserExist) {
            imports.add("<script type=\"text/javascript\" src=\"/itemsbrowser/secure/dwr/interface/ItemsBrowserInterface.js\"></script>\n");//$NON-NLS-1$
            imports.add("<script type=\"text/javascript\" src=\"/itemsbrowser/secure/js/ItemsBrowser.js\"></script>\n");//$NON-NLS-1$
        }
    }

    private int getJavascriptImportDetail(Menu menu, ArrayList<String> imports, int level, int i) {

        for (String key : menu.getSubMenus().keySet()) {
            Menu subMenu = menu.getSubMenus().get(key);

            if (subMenu.getContext() != null) {
                if (gxtFactory.isExcluded(subMenu.getContext(), subMenu.getApplication())) {
                    continue;
                }
                String tmp = "<script type=\"text/javascript\" src=\"/" + subMenu.getContext() + "/secure/dwr/interface/" //$NON-NLS-1$//$NON-NLS-2$
                        + subMenu.getApplication() + "Interface.js\"></script>\n"; //$NON-NLS-1$
                imports.add(tmp);
                tmp = "<script type=\"text/javascript\" src=\"/" + subMenu.getContext() + "/secure/js/" //$NON-NLS-1$//$NON-NLS-2$
                        + subMenu.getApplication() + ".js\"></script>\n"; //$NON-NLS-1$
                imports.add(tmp);

                // tmp
                // ="<link rel=\"stylesheet\" type=\"text/css\" href=\"/"+subMenu.getContext()+"/secure/css/"+subMenu.getApplication()+".css\"></link>\n";
                // imports.add(tmp);
                String gxtEntryModule = gxtFactory.getGxtEntryModule(subMenu.getContext(), subMenu.getApplication());
                if (gxtEntryModule != null) {
                    tmp = "<script type=\"text/javascript\" src=\"/" + subMenu.getContext() + "/" + gxtEntryModule + "/" //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
                            + gxtEntryModule + ".nocache.js\"></script>\n"; //$NON-NLS-1$
                    imports.add(tmp);
                }
                i++;
            }

            if (subMenu.getSubMenus().size() > 0) {
                i = getJavascriptImportDetail(subMenu, imports, level + 1, i);
            }
        }
        return i;
    }

    @SuppressWarnings("nls")
    protected String getCommonImport() {
        return
        // EXT & YUI
        "<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/utilities/utilities.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/yuiloader/yuiloader-beta.js\"></script>\n"
                +
                // "<script src=\"/core/secure/ext-2.2/adapter/yui/yui-utilities.js\" type=\"text/javascript\"></script>\n"+
                "<script type=\"text/javascript\" src=\"/core/secure/ext-2.2/adapter/yui/ext-yui-adapter.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/ext-2.2/ext-all-debug.js\"></script>\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/ext-2.2/resources/css/ext-all.css\" />\n"
                +
                // EXT-UX
                "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/editablecolumntree/ColumnNodeUI.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/editablecolumntree/treeSerializer.js\"></script>\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/ext.ux/editablecolumntree/editable-column-tree.css\" />\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/i18n/PropertyReader.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/i18n/Bundle.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/MultiSelectTreePanel.js\"></script>\n"
                +
                // Firefox3 Fixes
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/firefox3-fix.css\" />\n"
                +
                // CORE
                "<script type=\"text/javascript\" src=\"/core/secure/js/core.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/dwr/interface/LayoutInterface.js\"></script>\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/webapp-core.css\" />\n"
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/amalto-menus.css\" />\n"
                +
                // Proxy DWR <-> Ext
                "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRAction.js\"></script>\n"
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRProxy.js\"></script>\n"
                +
                // "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRProxy.js\"></script>\n"+
                // utility class
                "<script type=\"text/javascript\" src=\"/core/secure/js/bgutil.js\"></script>\n"
                +
                // graph class
                "<script type=\"text/javascript\" src=\"/core/secure/js/raphael-min.js\"></script>\n"
                +
                // DWR
                "<script language=\"javascript1.2\" type='text/javascript' src='/core/secure/dwr/engine.js'></script>\n"
                + "<script language=\"javascript1.2\" type='text/javascript' src='/core/secure/dwr/util.js'></script>\n"
                +
                // Simile Wiget
                "<script src=\"/core/secure/timeline/timeline_js/timeline-api.js\" type=\"text/javascript\"></script>\n"
                + "<script src=\"/core/secure/timeline/timeline_ajax/simile-ajax-api.js\" type=\"text/javascript\"></script>\n"
                + "<link rel=\"stylesheet\" href=\"/core/secure/timeline/css/default.css\" type=\"text/css\">";
    }

    @SuppressWarnings("nls")
    protected String getBody(String language, HttpServletRequest request) {
        String html = "		<option value=\"en\" selected>English</option>\n" + "		<option value=\"fr\">Francais</option>\n";
        if (language.equals("fr")) {
            html = "		<option value=\"en\" >English</option>\n" + "		<option value=\"fr\" selected>Francais</option>\n";
        }

        return "<body id=\"genericUI\">\n" + "<div id=\"header\" class=\"generic-header\">\n" +
        // TODO image
                "	<img src=\""
                + request.getContextPath()
                + "/secure/img/top-banner-talend.gif\"/>\n"
                +
                // "	<div id=\"logout-btn\" class=\"logout-btn\"></div>\n"+
                "	<div class=\"language-select\"><select style=\"align: right; font: normal  11px tahoma,verdana,helvetica; right: 5px\" id=\"languageSelect\" onchange=\"amalto.core.switchLanguage();\">\n"
                + html + "	</select></div>\n" +

                "	<div id=\"username-div\" class=\"username\"></div>\n" + "<a href='" + request.getContextPath()
                + "/secure/?action=logout' id='logout-btn' class='logout-btn'>"
                + (language.equals("fr") ? "d&eacute;connexion" : "logout") + "</a>\n" + "</div>\n"
                + "<div id=\"menus\" class=\"menus-list\"></div>\n"
                + "<div id=\"centerdiv\"></div>\n"
                +
                // "<div id=\"actions\"></div>\n"+
                "<div id=\"statusdiv\"></div>\n" + "<input type=\"hidden\" id=\"contextPath\" value=" + request.getContextPath()
                + "/>\n" + "<input type=\"hidden\" id=\"serverPath\" value=" + request.getScheme() + "://"
                + request.getLocalAddr() + ":" + request.getLocalPort() + "/>\n" + "</body>";
    }

    protected String getDefaultLanguage() throws Exception {
        return com.amalto.webapp.core.util.Util.getDefaultLanguage();
    }
}
