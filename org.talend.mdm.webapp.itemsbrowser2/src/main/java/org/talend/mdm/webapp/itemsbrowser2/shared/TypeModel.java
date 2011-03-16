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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataType;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC chliu class global comment. Detailled comment
 */
public abstract class TypeModel implements Serializable, IsSerializable {

    
    private String name;

    private DataType type;

    private String xpath;
    
    private Map<String,String> labelMap;
    
    private int minOccurs;
    
    private int maxOccurs;
    
    private boolean nillable = true;

    private List<String> foreignKeyInfo;

    private String foreignkey;
    
    /* compare with tree node to check properties
    private TreeNode parent;
    
    private String name;
    
    private String description;
    
    private String value;
    
    private String valueInfo;
    
    private boolean expandable;
    
    private String type;
    
    private int nodeId;
    
    private String taskId;
    
    private String typeName;
    
    private String xmlTag;
    
    private String documentation;
    
    private String labelOtherLanguage;
    
    private boolean readOnly = true;
    
    private int maxOccurs;
    
    private int minOccurs;
    
    private boolean nullable = true;
    
    private boolean choice;
    
    private boolean retrieveFKinfos = false;
    
    private String fkFilter;
    
    private String foreignKey;
    
    private String usingforeignKey;
    
    private boolean visible;
    
    private boolean key = false;
    
    private int keyIndex = -1;
    
    private String realValue;
    
    private String bindingPath;
    
    private boolean polymiorphise;
    
    private String realType;
    
    private List<Restriction> restrictions;
    
    private List<String> enumeration;
    
    private List<String> subTypes;
    */
    
    
    /**
     * DOC HSHU TypeModel constructor comment.
     */
    public TypeModel() {
        
    }

    public TypeModel(String name ,DataType type) {
        super();
        this.name = name;
        this.type = type;
        this.labelMap = new HashMap<String, String>();
    }

    public DataType getType() {
        return type;
    }

    /*
    public void setType(DataType typeName) {
        this.typeName = typeName;
    }*/
    
    public String getName() {
        return name;
    }

    /*
    public void setName(String name) {
        this.name = name;
    }*/
    
    public String getXpath() {
        return xpath;
    }
    

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getLabel(String language) {
        return getLabelMap().get(language);
    }

    
    public Map<String, String> getLabelMap() {
        return labelMap;
    }
    
    
    /**
     * Sets the labelMap.
     * @param labelMap the labelMap to set
     */
    public void addLabel(String language,String label) {
        labelMap = getLabelMap();
        labelMap.put(language, label);
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
    
    public boolean isNillable() {
        return nillable;
    }
    
    public void setNillable(boolean nillable) {
        this.nillable = nillable;
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

    public boolean isMultiOccurrence(){
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
