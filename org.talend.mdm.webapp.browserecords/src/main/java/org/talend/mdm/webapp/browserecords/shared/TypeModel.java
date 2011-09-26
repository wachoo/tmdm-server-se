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
package org.talend.mdm.webapp.browserecords.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.browserecords.client.model.DataType;
import org.talend.mdm.webapp.browserecords.client.model.SubTypeBean;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC chliu class global comment. Detailled comment
 */
public abstract class TypeModel implements Serializable, IsSerializable {

    private String name;

    private DataType type;

    private String xpath;

    private Map<String, String> labelMap;

    private Map<String, String> descriptionMap;

    private boolean readOnly = true;

    private boolean visible = true;

    private boolean polymiorphism = true;

    private int minOccurs;

    private int maxOccurs;

    private boolean nillable = true;

    private boolean retrieveFKinfos = false;

    private String foreignkey;

    private List<String> foreignKeyInfo;

    private String fkFilter;

    private List<String> primaryKeyInfo;

    private Map<String, String> facetErrorMsgs;

    private Map<String, String> displayFomats;

    private ArrayList<SubTypeBean> reusableTypes;

    private boolean denyCreatable = false;

    private boolean denyLogicalDeletable = false;

    private boolean denyPhysicalDeleteable = false;

    private boolean isAbstract = false;

    private String defaultValueExpression;

    private String defaultValue;

    /**
     * DOC HSHU TypeModel constructor comment.
     */

    public TypeModel() {
        super();
    }

    public TypeModel(String name, DataType type) {
        super();
        this.name = name;
        this.type = type;
        this.labelMap = new HashMap<String, String>();
        this.descriptionMap = new HashMap<String, String>();
        // FIXME do we need init here?
        this.facetErrorMsgs = new HashMap<String, String>();
        this.displayFomats = new HashMap<String, String>();

    }

    public DataType getType() {
        return type;
    }

    /*
     * public void setType(DataType typeName) { this.typeName = typeName; }
     */

    public String getName() {
        return name;
    }

    /*
     * public void setName(String name) { this.name = name; }
     */

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getLabel(String language) {
        String label = getLabelMap().get(language);
        if (label == null)
            return getName();
        return label;
    }

    public Map<String, String> getLabelMap() {
        return labelMap;
    }

    /**
     * Sets the labelMap.
     * 
     * @param labelMap the labelMap to set
     */
    public void addLabel(String language, String label) {
        labelMap = getLabelMap();
        labelMap.put(language, label);
    }

    public Map<String, String> getDescriptionMap() {
        return descriptionMap;
    }

    public void addDescription(String language, String label) {
        descriptionMap = getDescriptionMap();
        descriptionMap.put(language, label);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isPolymiorphism() {
        return polymiorphism;
    }

    public void setPolymiorphism(boolean polymiorphism) {
        this.polymiorphism = polymiorphism;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public ArrayList<SubTypeBean> getReusableTypes() {
        return reusableTypes;
    }

    public void setReusableTypes(ArrayList<SubTypeBean> subTypes) {
        this.reusableTypes = subTypes;
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

    public String getForeignkey() {
        return foreignkey;
    }

    public void setForeignkey(String foreignkey) {
        this.foreignkey = foreignkey;
    }

    public boolean isRetrieveFKinfos() {
        return retrieveFKinfos;
    }

    public void setRetrieveFKinfos(boolean retrieveFKinfos) {
        this.retrieveFKinfos = retrieveFKinfos;
    }

    public List<String> getForeignKeyInfo() {
        return foreignKeyInfo;
    }

    public void setForeignKeyInfo(List<String> foreignKeyInfo) {
        this.foreignKeyInfo = foreignKeyInfo;
    }

    public String getFkFilter() {
        return fkFilter;
    }

    public void setFkFilter(String fkFilter) {
        this.fkFilter = fkFilter;
    }

    public List<String> getPrimaryKeyInfo() {
        return primaryKeyInfo;
    }

    public void setPrimaryKeyInfo(List<String> primaryKeyInfo) {
        this.primaryKeyInfo = primaryKeyInfo;
    }

    public Map<String, String> getFacetErrorMsgs() {
        return facetErrorMsgs;
    }

    public void addFacetErrorMsg(String language, String label) {
        facetErrorMsgs = getFacetErrorMsgs();
        facetErrorMsgs.put(language, label);
    }

    public Map<String, String> getDisplayFomats() {
        return displayFomats;
    }

    public void addDisplayFomat(String language, String label) {
        displayFomats = getDisplayFomats();
        displayFomats.put(language, label);
    }

    public boolean isDenyCreatable() {
        return denyCreatable;
    }

    public void setDenyCreatable(boolean denyCreatable) {
        this.denyCreatable = denyCreatable;
    }

    public boolean isDenyLogicalDeletable() {
        return denyLogicalDeletable;
    }

    public void setDenyLogicalDeletable(boolean denyLogicalDeletable) {
        this.denyLogicalDeletable = denyLogicalDeletable;
    }

    public boolean isDenyPhysicalDeleteable() {
        return denyPhysicalDeleteable;
    }

    public void setDenyPhysicalDeleteable(boolean denyPhysicalDeleteable) {
        this.denyPhysicalDeleteable = denyPhysicalDeleteable;
    }

    public String getDefaultValueExpression() {
        return defaultValueExpression;
    }

    public void setDefaultValueExpression(String defaultValueExpression) {
        this.defaultValueExpression = defaultValueExpression;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public int[] getRange() {
        int min = 0;
        int max = 0;
        if (getMinOccurs() <= 0) {
            min = 1;
        } else {
            min = getMinOccurs();
        }

        if (getMaxOccurs() == -1) {
            max = Integer.MAX_VALUE;
        } else if (getMaxOccurs() >= min) {
            max = getMaxOccurs();
        }
        return new int[] { min, max };
    }

    public boolean isMultiOccurrence() {
        int[] range = getRange();
        int min = range[0];
        int max = range[1];
        boolean multiple = false;
        if (max > min && min >= 1) {
            multiple = true;
        }
        return multiple;
    }

    public abstract boolean isSimpleType();

    public abstract boolean hasEnumeration();

    public String toString() {
        return this.getName();
    }
}
