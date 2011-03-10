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
package org.talend.mdm.webapp.itemsbrowser2.shared;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC chliu class global comment. Detailled comment
 */
public abstract class TypeModel implements Serializable, IsSerializable {

    private String typeName;

    private String xpath;
    
    private String label;
    
    private int minOccurs;
    
    private int maxOccurs;

    private List<String> foreignKeyInfo;

    private String foreignkey;

    public TypeModel() {
        super();
    }

    public TypeModel(String typeName, String label) {
        super();
        this.typeName = typeName;
        this.label = label;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
    public String getXpath() {
        return xpath;
    }
    
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    
    public int getMinOccurs() {
        return minOccurs;
    }
    
    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }
    
    public int getMaxOccurs() {
        return maxOccurs;
    }
    
    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public List<String> getForeignKeyInfo() {
        return foreignKeyInfo;
    }

    public void setForeignKeyInfo(List<String> foreignKeyInfo) {
        this.foreignKeyInfo = foreignKeyInfo;
    }

    public String getForeignkey() {
        return foreignkey;
    }

    public void setForeignkey(String foreignkey) {
        this.foreignkey = foreignkey;
    }

    public int[] getRange(){
        int min = 0;
        int max = 0;
        if (getMinOccurs() <=0){
            min = 1;
        } else {
            min = getMinOccurs();
        }
        
        if (getMaxOccurs() == -1){
            max = Integer.MAX_VALUE;
        } else if (getMaxOccurs() >= min){
            max = getMaxOccurs();
        }
        return new int[]{min, max};
    }

    public boolean isMultiple(){
        int[] range = getRange();
        int min = range[0];
        int max = range[1];
        boolean multiple = false;
        if (max > min && min >= 1){
            multiple = true;
        }
        return multiple;
    }

    public abstract boolean isSimpleType();

    public abstract boolean hasEnumeration();
}
