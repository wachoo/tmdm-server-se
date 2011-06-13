package org.talend.mdm.webapp.general.client.layout;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;


public class BrandingBar extends ContentPanel {

    private static BrandingBar instance;
    
    private BrandingBar(){
        super();
    }
    
    public static BrandingBar getInstance(){
        if (instance == null){
            instance = new BrandingBar();
        }
        return instance;
    }
    
    private void buildBar(){
        HorizontalPanel hp = new HorizontalPanel();
        
    }
}
