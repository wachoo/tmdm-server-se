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
package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModel;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class ItemFormBean extends ItemBaseModel{

    /**
     * 
     */
    private static final long serialVersionUID = 6830378208452308947L;
    
    private List<ItemFormLineBean> formLines = null;
    
    private String name = null;
    
    
    /**
     * DOC HSHU ItemFormBean constructor comment.
     */
    public ItemFormBean() {
        this.formLines=new ArrayList<ItemFormLineBean>();
    }
    
    
    /**
     * DOC HSHU Comment method "addLine".
     */
    public void addLine(ItemFormLineBean itemFormLineBean) {
        this.formLines.add(itemFormLineBean);
    }
    
    public Iterator<ItemFormLineBean> iteratorLine (){
        return this.formLines.iterator();
    }
    
    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }
    
}
