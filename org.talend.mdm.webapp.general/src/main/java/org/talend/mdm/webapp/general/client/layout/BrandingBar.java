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
