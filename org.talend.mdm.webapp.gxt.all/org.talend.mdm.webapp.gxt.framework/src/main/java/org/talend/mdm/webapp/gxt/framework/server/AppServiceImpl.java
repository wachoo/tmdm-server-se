package org.talend.mdm.webapp.gxt.framework.server;

import org.talend.mdm.webapp.gxt.common.PortManager;
import org.talend.mdm.webapp.gxt.framework.client.AppService;
import org.talend.mdm.webapp.gxt.framework.shared.NavFolder;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSGetMenu;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSGetMenuPKs;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSMenu;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSMenuEntry;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSMenuEntryDescriptions;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSMenuPK;
import org.talend.mdm.webapp.gxt.service.open.webservices.WSMenuPKArray;
import org.talend.mdm.webapp.gxt.service.open.webservices.XtentisPort_PortType;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class AppServiceImpl extends RemoteServiceServlet implements AppService {

    public String sayHello(String name) throws IllegalArgumentException {
        // Verify that the input is valid.
        if (name == null) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException("Name can't be empty! ");
        }

        return "Hello, " + name + "!";
    }

    public NavFolder getMenus(String language) throws Exception {
        
            NavFolder root = new NavFolder("-1","root",NavFolder._ROOT_);
            
            XtentisPort_PortType port=PortManager.getInstance().getPort();
            WSMenuPKArray wsMenuPKArray=port.getMenuPKs(new WSGetMenuPKs(".*"));
            WSMenuPK[] wsMenuPKs=wsMenuPKArray.getWsMenuPK();
            
            //TODO no hierarchy here
            for (int i = 0; i < wsMenuPKs.length; i++) {
                
                NavFolder menuEntry = new NavFolder(NavFolder._ENTRY_);
                WSMenuPK wsMenuPK = wsMenuPKs[i];
                WSMenu wsMenu=port.getMenu(new WSGetMenu(wsMenuPK));//FIXME retrieve once, more efficient
                menuEntry.setName(wsMenu.getName());
                
                WSMenuEntry[] wsMenuEntries=wsMenu.getMenuEntries();
                for (int j = 0; j < wsMenuEntries.length; j++) {
                    
                    WSMenuEntry wsMenuEntry=wsMenuEntries[j];
                    menuEntry.setId(wsMenuEntry.getId());
                    menuEntry.setApplication(wsMenuEntry.getApplication());
                    menuEntry.setContext(wsMenuEntry.getContext());
                    menuEntry.setIcon(wsMenuEntry.getIcon());
                    
                    WSMenuEntryDescriptions[] wsMenuEntryDescriptionsArray=wsMenuEntry.getDescriptions();
                    for (WSMenuEntryDescriptions wsMenuEntryDescriptions : wsMenuEntryDescriptionsArray) {
                        String gettedlanguage=wsMenuEntryDescriptions.getLanguage();
                        if(gettedlanguage!=null&&gettedlanguage.equals(language)){
                            menuEntry.setName(wsMenuEntryDescriptions.getLabel());
                            break;
                        }
                    }
                }
                if(menuEntry.getId()!=null&&menuEntry.getId().length()>0)root.add(menuEntry);
                
            }

            return root;
        
    }
}
