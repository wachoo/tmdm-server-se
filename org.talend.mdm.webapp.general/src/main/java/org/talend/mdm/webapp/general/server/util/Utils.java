package org.talend.mdm.webapp.general.server.util;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.general.model.MenuBean;

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
    
}
