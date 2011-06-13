package org.talend.mdm.webapp.general.client.layout;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;

public class WorkSpace extends LayoutContainer {

    private static WorkSpace instance;
    
    private TabPanel workTabPanel = new TabPanel();
    
    private WorkSpace(){
        super();
        this.setLayout(new FitLayout());
        this.add(workTabPanel);
    }
    
    public static WorkSpace getInstance(){
        if (instance == null){
            instance = new WorkSpace();
        }
        return instance;
    }
    
    public void addWorkTab(String itemId, String title, String url){
        TabItem item = workTabPanel.getItemByItemId(itemId);
        if (item == null){
            item = new TabItem(title);
            item.setItemId(itemId);
            item.setClosable(true);
            item.setLayout(new FitLayout());
            Frame frame = new Frame(url);
            frame.getElement().getStyle().setBorderWidth(0.0D, Unit.PX);
            item.add(frame);
            workTabPanel.add(item);
        }
        workTabPanel.setSelection(item);
    }
}
