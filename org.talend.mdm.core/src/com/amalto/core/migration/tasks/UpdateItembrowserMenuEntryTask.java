package com.amalto.core.migration.tasks;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.amalto.core.objects.ObjectPOJOPK;
import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.server.api.Menu;
import com.amalto.core.objects.menu.MenuEntryPOJO;
import com.amalto.core.objects.menu.MenuPOJO;
import com.amalto.core.objects.menu.MenuPOJOPK;
import com.amalto.core.util.Util;

public class UpdateItembrowserMenuEntryTask extends AbstractMigrationTask {

    private static final Logger LOGGER = Logger.getLogger(UpdateItembrowserMenuEntryTask.class);

    /**
     * 
     */
    private static final String NEW_MENU_NAME = "Browse items2";//$NON-NLS-1$

    private static final String LEGACY_MENU_NAME = "Browse items";//$NON-NLS-1$

    @Override
    protected Boolean execute() {
        // Update Menu POJOS
        LOGGER.info("Updating Itemsbrowser Menus");//$NON-NLS-1$
        try {
            Menu menuCtrl = Util.getMenuCtrlLocal();
            MenuPOJO extMenuPOJO = menuCtrl.existsMenu(new MenuPOJOPK(new ObjectPOJOPK(LEGACY_MENU_NAME)));
            if (extMenuPOJO != null) {
                // remove
                menuCtrl.removeMenu(new MenuPOJOPK(extMenuPOJO.getPK()));
            }
            MenuPOJO gxtMenuPOJO = menuCtrl.existsMenu(new MenuPOJOPK(new ObjectPOJOPK(NEW_MENU_NAME)));
            if (gxtMenuPOJO == null) {
                // create
                gxtMenuPOJO = new MenuPOJO(NEW_MENU_NAME);
                gxtMenuPOJO.setDescription("Browse records 2");//$NON-NLS-1$
                ArrayList<MenuEntryPOJO> gxtMenuEntries = new ArrayList<MenuEntryPOJO>();
                MenuEntryPOJO gxtMenuEntryPOJO = new MenuEntryPOJO();
                gxtMenuEntryPOJO.setId(NEW_MENU_NAME);
                gxtMenuEntryPOJO.setContext("itemsbrowser2");//$NON-NLS-1$
                gxtMenuEntryPOJO.setApplication("ItemsBrowser2");//$NON-NLS-1$
                HashMap<String, String> gxtDescriptions = new HashMap<String, String>();
                gxtDescriptions.put("en", "Browse Records");//$NON-NLS-1$ //$NON-NLS-2$
                gxtDescriptions.put("fr", "Accès aux données");//$NON-NLS-1$ //$NON-NLS-2$
                gxtMenuEntryPOJO.setDescriptions(gxtDescriptions);
                gxtMenuEntries.add(gxtMenuEntryPOJO);
                gxtMenuPOJO.setMenuEntries(gxtMenuEntries);
                // add
                menuCtrl.putMenu(gxtMenuPOJO);
            }
        } catch (Exception e) {
            String err = "Unable to Update Itemsbrowser Entries.";//$NON-NLS-1$
            LOGGER.error(err, e);
            return false;
        }
        LOGGER.info("Done Updating Itemsbrowser Menus");//$NON-NLS-1$
        return true;
    }

}
