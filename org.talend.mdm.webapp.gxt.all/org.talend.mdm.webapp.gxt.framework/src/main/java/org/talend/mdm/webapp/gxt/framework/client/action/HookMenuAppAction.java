// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.gxt.framework.client.action;

import org.talend.mdm.webapp.gxt.framework.client.PageRegister;
import org.talend.mdm.webapp.gxt.framework.client.mvc.AppView;
import org.talend.mdm.webapp.gxt.framework.client.page.DefaultMenuAppPage;
import org.talend.mdm.webapp.gxt.framework.client.page.MenuAppPage;
import org.talend.mdm.webapp.gxt.framework.shared.NavFolder;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class HookMenuAppAction implements Action{

    /* (non-Javadoc)
     * @see org.talend.mdm.webapp.gxt.framework.client.action.Action#execute(com.extjs.gxt.ui.client.event.BaseEvent)
     */
    public void execute(BaseEvent be) {
        
        NavFolder f = ((TreePanel<NavFolder>) be.getSource()).getSelectionModel().getSelectedItem();
        String id=f.getId();
        String name=f.getName();
        String context=f.getContext();
        String application=f.getApplication();
        
        TabPanel tabs = (TabPanel) Registry.get(AppView.CENTER_TAB_PANEL);
        TabItem gettedTabItem = tabs.findItem(id, false);
        if(gettedTabItem==null){
            TabItem newTabItem = new TabItem();

            newTabItem.setId(id);
            newTabItem.setText(name);
            newTabItem.setLayout(new FitLayout());
            newTabItem.setClosable(true);
            
            tabs.add(newTabItem);
            
            String checkId = context+"."+application;
            MenuAppPage page=PageRegister.registerMap.get(checkId);
            if(page==null){
                page = new DefaultMenuAppPage();
            }
            
            newTabItem.add(page);
            
            tabs.setSelection(newTabItem);
        }
        
    }

}
