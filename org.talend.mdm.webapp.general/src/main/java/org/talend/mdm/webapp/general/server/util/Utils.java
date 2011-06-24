package org.talend.mdm.webapp.general.server.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.general.model.ItemBean;
import org.talend.mdm.webapp.general.model.MenuBean;
import org.talend.mdm.webapp.general.server.GeneralServiceImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Menu;


public class Utils {

    private static final Logger LOG = Logger.getLogger(Utils.class);
    
    public static void getJavascriptImportDetail(List<String> imports){
        try {
            getJavascriptImportDetail(Menu.getRootMenu(),  imports, 1, 1);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
    
    public static int getSubMenus(Menu menu, String language, List<MenuBean> rows, int level, int i) {
        for (Iterator<String> iter = menu.getSubMenus().keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            Menu subMenu = menu.getSubMenus().get(key);
            MenuBean item = new MenuBean();
            item.setId(i);
            item.setLevel(level);
            item.setContext(subMenu.getContext());
            item.setIcon(subMenu.getIcon());
            item.setName(subMenu.getLabels().get(language));
            item.setApplication(subMenu.getApplication() == null ? "" : subMenu.getApplication()); //$NON-NLS-1$
            rows.add(item);
            i++;
            if (subMenu.getSubMenus().size() > 0)
                i = getSubMenus(subMenu, language, rows, level + 1, i);
        }
        return i;
    }
    
    public static int getJavascriptImportDetail(Menu menu, List<String> imports, int level, int i) throws Exception{
        for (Iterator<String> iter = menu.getSubMenus().keySet().iterator(); iter.hasNext(); ) {
            String key = iter.next();
            Menu subMenu= menu.getSubMenus().get(key);
            
            if(subMenu.getContext()!=null) {
                String tmp ="<script type=\"text/javascript\" src=\"/"+subMenu.getContext()+"/secure/js/"+subMenu.getApplication()+".js\"></script>\n"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                imports.add(tmp);
                i++;
            }
            if (subMenu.getSubMenus().size()>0) 
                i=getJavascriptImportDetail(subMenu, imports, level+1,i);           
        }
        return i;   
    }
    
    public static List<ItemBean> getLanguages() {
        InputStream is = GeneralServiceImpl.class.getResourceAsStream("/languages.xml"); //$NON-NLS-1$
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        List<ItemBean> languages = new ArrayList<ItemBean>();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            Element root = doc.getDocumentElement();
            NodeList nodes = root.getChildNodes();
            for (int i = 0;i < nodes.getLength();i++){
                Node node = nodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    if (node.getNodeName().equals("language")){ //$NON-NLS-1$ 
                        ItemBean lang = new ItemBean();
                        lang.setText(node.getTextContent());
                        lang.setValue(node.getAttributes().getNamedItem("value").getNodeValue()); //$NON-NLS-1$
                        languages.add(lang);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return languages;
    }
}
