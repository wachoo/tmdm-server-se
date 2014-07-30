package com.amalto.webapp.core.dwr;

import java.util.ArrayList;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.servlet.GenericControllerServlet;
import com.amalto.webapp.core.util.GxtFactory;
import com.amalto.webapp.core.util.Menu;

/**
 * 
 * @author asaintguilhem
 * 
 */

public class LayoutDWR {

    public int getSubMenus(Menu menu, String language, ArrayList<JSONObject> rows, int level, int i) throws Exception {
        GxtFactory gxtFactory = GenericControllerServlet.gxtFactory;
        for (String key : menu.getSubMenus().keySet()) {
            Menu subMenu = menu.getSubMenus().get(key);
            if (gxtFactory.isExcluded(subMenu.getContext(), subMenu.getApplication())) {
                continue;
            }
            JSONObject entry = new JSONObject();
            entry.put("id", i);
            entry.put("level", level);
            entry.put("context", subMenu.getContext());
            entry.put("icon", subMenu.getIcon());
            entry.put("name", subMenu.getLabels().get(language));
            entry.put("desc", "");
            entry.put("application", subMenu.getApplication() == null ? "" : subMenu.getApplication());
            rows.add(entry);
            i++;
            if (subMenu.getSubMenus().size() > 0)
                i = getSubMenus(subMenu, language, rows, level + 1, i);
        }
        return i;
    }


}
