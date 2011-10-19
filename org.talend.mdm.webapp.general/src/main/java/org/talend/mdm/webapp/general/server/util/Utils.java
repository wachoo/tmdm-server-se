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
package org.talend.mdm.webapp.general.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.general.model.GroupItem;
import org.talend.mdm.webapp.general.model.LanguageBean;
import org.talend.mdm.webapp.general.model.MenuBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.webapp.core.util.Menu;

public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);

    private static final String GXT_PROPERTIES = "gxt.properties"; //$NON-NLS-1$

    private static final String EXCLUDING_PROPERTIES = "excluding.properties"; //$NON-NLS-1$

    private static final String WELCOMECONTEXT = "welcomeportal", WELCOMEAPP = "WelcomePortal";//$NON-NLS-1$ //$NON-NLS-2$

    private static final String DEFAULT_LANG = "en"; //$NON-NLS-1$

    /** a reference to the factory used to create Gxt instances */
    private static GxtFactory gxtFactory = new GxtFactory(GXT_PROPERTIES, EXCLUDING_PROPERTIES);

    public static void getJavascriptImportDetail(List<String> imports) {
        try {
            getJavascriptImportDetail(Menu.getRootMenu(), imports, 1, 1);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static int getSubMenus(Menu menu, String language, List<MenuBean> rows, int level, int i) {
        for (Iterator<String> iter = menu.getSubMenus().keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            Menu subMenu = menu.getSubMenus().get(key);
            if (gxtFactory.isExcluded(subMenu.getContext(), subMenu.getApplication())) {
                continue;
            }
            MenuBean item = new MenuBean();
            item.setId(i);
            item.setLevel(level);
            item.setContext(subMenu.getContext());
            item.setIcon(subMenu.getIcon());
            String name = subMenu.getLabels().get(language);
            if (name == null) {
                name = subMenu.getLabels().get(DEFAULT_LANG); // fallback to default
            }
            item.setName(name);
            item.setApplication(subMenu.getApplication() == null ? "" : subMenu.getApplication()); //$NON-NLS-1$
            rows.add(item);
            i++;
            if (subMenu.getSubMenus().size() > 0)
                i = getSubMenus(subMenu, language, rows, level + 1, i);
        }
        return i;
    }

    public static ArrayList<String> getJavascriptImport() throws Exception {
        ArrayList<String> imports = new ArrayList<String>();
        getJavascriptImportDetail(Menu.getRootMenu(), imports, 1, 1);
        // FIXME: This is a workaround for 4.2 only
        complementItemsbrowser(imports);
        completeThirdPartJS(imports);
        return imports;
    }

    private static int getJavascriptImportDetail(Menu menu, List<String> imports, int level, int i) throws Exception {
        if (menu.getParent() == null) {
            // add welcome by default
            Menu welMenu = new Menu();
            welMenu.setApplication(WELCOMEAPP);
            welMenu.setContext(WELCOMECONTEXT);
            menu.getSubMenus().put(WELCOMECONTEXT, welMenu);
        }

        for (Iterator<String> iter = menu.getSubMenus().keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            Menu subMenu = menu.getSubMenus().get(key);

            if (subMenu.getContext() != null) {
                if (gxtFactory.isExcluded(subMenu.getContext(), subMenu.getApplication())) {
                    continue;
                }
                String gxtEntryModule = gxtFactory.getGxtEntryModule(subMenu.getContext(), subMenu.getApplication());

                if (gxtEntryModule == null || subMenu.getContext().equals("itemsbrowser2")) { //$NON-NLS-1$
                    String tmp = "<script type=\"text/javascript\" src=\"/" + subMenu.getContext() + "/secure/dwr/interface/" //$NON-NLS-1$ //$NON-NLS-2$
                            + subMenu.getApplication() + "Interface.js\"></script>\n"; //$NON-NLS-1$
                    if (!imports.contains(tmp))
                        imports.add(tmp);
                    tmp = "<script type=\"text/javascript\" src=\"/" + subMenu.getContext() + "/secure/js/" //$NON-NLS-1$ //$NON-NLS-2$
                            + subMenu.getApplication() + ".js\"></script>\n"; //$NON-NLS-1$
                    if (!imports.contains(tmp))
                        imports.add(tmp);
                    if (subMenu.getContext().equals("itemsbrowser2")) { //$NON-NLS-1$
                        tmp = "<script type=\"text/javascript\" src=\"/" + subMenu.getContext() + "/" + gxtEntryModule + "/" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                + gxtEntryModule + ".nocache.js\"></script>\n"; //$NON-NLS-1$
                        imports.add(tmp);
                    }

                } else {
                    String tmp = "<script type=\"text/javascript\" src=\"/" + subMenu.getContext() + "/" + gxtEntryModule + "/" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            + gxtEntryModule + ".nocache.js\"></script>\n"; //$NON-NLS-1$
                    if (!imports.contains(tmp))
                        imports.add(tmp);
                }
                i++;
            }
            if (subMenu.getSubMenus().size() > 0)
                i = getJavascriptImportDetail(subMenu, imports, level + 1, i);
        }
        return i;
    }

    private static void complementItemsbrowser(ArrayList<String> imports) {
        boolean isItemsbrowserExist = false;
        // boolean isItemsbrowser2Exist = false;
        boolean isBrowserRecordExist = false;

        for (String importMenu : imports) {
            if (importMenu.indexOf("src=\"/itemsbrowser/secure/js/ItemsBrowser.js\"") != -1)isItemsbrowserExist = true;//$NON-NLS-1$
            //            if (importMenu.indexOf("src=\"/itemsbrowser2/secure/js/ItemsBrowser2.js\"") != -1)isItemsbrowser2Exist = true;//$NON-NLS-1$
            if (importMenu.indexOf("src=\"/browserecords/browserecords/browserecords.nocache.js\"") != -1)isBrowserRecordExist = true;//$NON-NLS-1$
        }
        if (isBrowserRecordExist && !isItemsbrowserExist) {
            imports.add("<script type=\"text/javascript\" src=\"/itemsbrowser/secure/dwr/interface/ItemsBrowserInterface.js\"></script>\n");//$NON-NLS-1$
            imports.add("<script type=\"text/javascript\" src=\"/itemsbrowser/secure/js/ItemsBrowser.js\"></script>\n");//$NON-NLS-1$
        }
    }

    private static void completeThirdPartJS(ArrayList<String> imports) {
        imports.add("<script type=\"text/javascript\" src=\"/talendmdm/secure/dwr/interface/WidgetInterface.js\"></script>\n");//$NON-NLS-1$
    }

    public static String getCommonImport() {
        return
        // EXT & YUI
        "<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/utilities/utilities.js\"></script>\n" //$NON-NLS-1$
                + "<script type=\"text/javascript\" src=\"/core/secure/yui-2.4.0/build/yuiloader/yuiloader-beta.js\"></script>\n" //$NON-NLS-1$
                + // "<script src=\"/core/secure/ext-2.2/adapter/yui/yui-utilities.js\" type=\"text/javascript\"></script>\n"+
                "<script type=\"text/javascript\" src=\"/core/secure/ext-2.2/adapter/yui/ext-yui-adapter.js\"></script>\n" //$NON-NLS-1$
                + "<script type=\"text/javascript\" src=\"/core/secure/ext-2.2/ext-all-debug.js\"></script>\n" //$NON-NLS-1$
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/ext-2.2/resources/css/ext-all_compatible.css\" />\n" //$NON-NLS-1$
                + // EXT-UX
                "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/editablecolumntree/ColumnNodeUI.js\"></script>\n" //$NON-NLS-1$
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/editablecolumntree/treeSerializer.js\"></script>\n" //$NON-NLS-1$
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/ext.ux/editablecolumntree/editable-column-tree.css\" />\n" //$NON-NLS-1$
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/MultiSelectTreePanel.js\"></script>\n" //$NON-NLS-1$
                + // Firefox3 Fixes
                "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/firefox3-fix.css\" />\n" //$NON-NLS-1$
                + // CORE
                "<script type=\"text/javascript\" src=\"/general/proxy_core.js\"></script>\n" //$NON-NLS-1$
                + "<script type=\"text/javascript\" src=\"/core/secure/dwr/interface/LayoutInterface.js\"></script>\n" //$NON-NLS-1$
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/webapp-core.css\" />\n" //$NON-NLS-1$
                + "<link rel=\"stylesheet\" type=\"text/css\" href=\"/core/secure/css/amalto-menus.css\" />\n" //$NON-NLS-1$
                + // Proxy DWR <-> Ext
                "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRAction.js\"></script>\n" //$NON-NLS-1$
                + "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRProxy.js\"></script>\n" //$NON-NLS-1$
                + // "<script type=\"text/javascript\" src=\"/core/secure/ext.ux/DWRProxy.js\"></script>\n"+
                  // utility class
                "<script type=\"text/javascript\" src=\"/core/secure/js/bgutil.js\"></script>\n" //$NON-NLS-1$
                + // graph class
                "<script type=\"text/javascript\" src=\"/core/secure/js/raphael-min.js\"></script>\n" //$NON-NLS-1$
                + // DWR
                "<script language=\"javascript1.2\" type='text/javascript' src='/core/secure/dwr/engine.js'></script>\n" //$NON-NLS-1$
                + "<script language=\"javascript1.2\" type='text/javascript' src='/core/secure/dwr/util.js'></script>\n" //$NON-NLS-1$
                + // Simile Wiget
                "<script src=\"/core/secure/timeline/timeline_js/timeline-api.js\" type=\"text/javascript\"></script>\n" //$NON-NLS-1$
                + "<script src=\"/core/secure/timeline/timeline_ajax/simile-ajax-api.js\" type=\"text/javascript\"></script>\n" //$NON-NLS-1$
                + "<link rel=\"stylesheet\" href=\"/core/secure/timeline/css/default.css\" type=\"text/css\">"; //$NON-NLS-1$
    }

    public static List<LanguageBean> getLanguages(String selectedLang) throws IOException, SAXException,
            ParserConfigurationException {
        InputStream is = Utils.class.getResourceAsStream("/languages.xml"); //$NON-NLS-1$
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<LanguageBean> languages = new ArrayList<LanguageBean>();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("language")) { //$NON-NLS-1$ 
                    LanguageBean lang = new LanguageBean();
                    lang.setText(node.getTextContent());
                    String value = node.getAttributes().getNamedItem("value").getNodeValue(); //$NON-NLS-1$
                    lang.setValue(value);
                    if (value.equals(selectedLang))
                        lang.setSelected(true);
                    languages.add(lang);
                }
            }
        }
        return languages;
    }

    public static List<GroupItem> getGroupItems(String language) throws IOException, SAXException,
 ParserConfigurationException {
        InputStream is = Utils.class.getResourceAsStream("/MenuGroup.xml"); //$NON-NLS-1$
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);
        Element root = doc.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        List<GroupItem> giList = new ArrayList<GroupItem>();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                if (node.getNodeName().equals("groupitem")) { //$NON-NLS-1$ 
                    GroupItem giNew = new GroupItem();
                    Node langNode = node.getAttributes().getNamedItem(language);
                    giNew.setGroupHeader(langNode == null ? "" : langNode.getNodeValue()); //$NON-NLS-1$
                    NodeList items = node.getChildNodes();
                    List<String> menuItems = new ArrayList<String>();
                    for (int k = 0; k < items.getLength(); k++) {
                        Node item = items.item(k);
                        if (item.getNodeName().equals("item")) { //$NON-NLS-1$
                            menuItems.add(item.getTextContent());
                        }
                    }
                    giNew.setMenuItems(menuItems);
                    giList.add(giNew);
                }
                }
            }

        return giList;
    }
}
