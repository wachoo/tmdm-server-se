package org.talend.mdm.webapp.browserecords.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.TreeModel;


public class ItemNodeModel extends BaseTreeModel implements Cloneable {

    private static final long serialVersionUID = 1L;

    private static int ID = 0;

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

    private boolean nillable = true;

    private boolean choice;

    private ArrayList<String> enumeration;

    private boolean retrieveFKinfos = false;

    private String fkFilter;

    private String foreignKey;

    private String usingforeignKey;

    private boolean visible;

    private boolean key = false;

    private int keyIndex = -1;

    private HashMap<String, String> facetErrorMsgs = new HashMap<String, String>();

    private String realValue;

    private String bindingPath;

    private boolean polymorphism;

    // private ArrayList<SubTypeBean> subTypes;


    private String realType;

    private boolean isAbstract = false;

    private boolean autoExpand;

    private boolean denyCreatable;

    private boolean denyLogicalDeletable;

    private boolean denyPhysicalDeletable;


    public ItemNodeModel() {
        set("id", ID++);

    }
    public ItemNodeModel(String name) {
        set("id", ID++);
        set("name", name);
    }
    public ItemNodeModel(String name, BaseTreeModel[] children) {
        this(name);
        for (int i = 0; i < children.length; i++) {
            add(children[i]);
        }
    }

    public String toString() {
        return getName();
    }

    public Integer getId() {
        return (Integer) get("id");
    }

    private String[] displayFormats = new String[2];

    public void setDisplayFormats(String lang, String format) {
        /*
         * if(displayFomats.containsKey(lang)) displayFomats.remove(lang); displayFomats.put(lang, fomat);
         */
        displayFormats[0] = lang;
        displayFormats[1] = format;
    }

    /**
     * @author ymli fix the bug:0013463
     * @param lang
     * @param fomat
     */
    public String[] getDisplayFormats() {
        return displayFormats;
    }
    public ItemNodeModel copy() {
        // try {
            return new ItemNodeModel();
        // }
    }
    public String getName() {
        return get("name"); //$NON-NLS-1$
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueInfo() {
        return valueInfo;
    }

    public void setValueInfo(String valueInfo) {
        this.valueInfo = valueInfo;
    }

    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getXmlTag() {
        return xmlTag;
    }

    public void setXmlTag(String xmlTag) {
        this.xmlTag = xmlTag;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getLabelOtherLanguage() {
        return labelOtherLanguage;
    }

    public void setLabelOtherLanguage(String labelOtherLanguage) {
        this.labelOtherLanguage = labelOtherLanguage;
    }

    public boolean isReadOnly() {
        return readOnly;
    }
    
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
    
    public int getMaxOccurs() {
        return maxOccurs;
    }

    
    public void setMaxOccurs(int maxOccurs) {
        this.maxOccurs = maxOccurs;
    }

    public int getMinOccurs() {
        return minOccurs;
    }

    public void setMinOccurs(int minOccurs) {
        this.minOccurs = minOccurs;
    }

    public boolean isNillable() {
        return nillable;
    }

    public void setNillable(boolean nillable) {
        this.nillable = nillable;
    }

    public boolean isChoice() {
        return choice;
    }

    public void setChoice(boolean choice) {
        this.choice = choice;
    }

    public ArrayList<String> getEnumeration() {
        return enumeration;
    }

    public void setEnumeration(ArrayList<String> enumeration) {
        this.enumeration = enumeration;
    }

    public boolean isRetrieveFKinfos() {
        return retrieveFKinfos;
    }

    public void setRetrieveFKinfos(boolean retrieveFKinfos) {
        this.retrieveFKinfos = retrieveFKinfos;
    }

    public String getFkFilter() {
        return fkFilter;
    }

    public void setFkFilter(String fkFilter) {
        this.fkFilter = fkFilter;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    public String getUsingforeignKey() {
        return usingforeignKey;
    }

    public void setUsingforeignKey(String usingforeignKey) {
        this.usingforeignKey = usingforeignKey;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public HashMap<String, String> getFacetErrorMsgs() {
        return facetErrorMsgs;
    }

    public void setFacetErrorMsg(String lang, String facet) {
        if (facetErrorMsgs.containsKey(lang))
            facetErrorMsgs.remove(lang);
        facetErrorMsgs.put(lang, facet);
    }

    public String getRealValue() {
        return realValue;
    }

    public void setRealValue(String realValue) {
        this.realValue = realValue;
    }

    public String getBindingPath() {
        StringBuffer xp = new StringBuffer();
        List<String> paths = new ArrayList<String>();
        TreeModel parent = this;
        
        while (parent != null) {
            paths.add((String) parent.get("name"));
            parent = parent.getParent();
        }

        for (int i = paths.size() - 1; i >= 0; i--) {
            if (i != paths.size() - 1)
                xp.append("/");
            xp.append(paths.get(i));
        }
        return xp.toString();
    }

    public void setBindingPath(String bindingPath) {
        this.bindingPath = bindingPath;
    }



    public String getRealType() {
        return realType;
    }

    public void setRealType(String realType) {
        this.realType = realType;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isAutoExpand() {
        return autoExpand;
    }

    public void setAutoExpand(boolean autoExpand) {
        this.autoExpand = autoExpand;
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

    public boolean isDenyPhysicalDeletable() {
        return denyPhysicalDeletable;
    }

    public void setDenyPhysicalDeletable(boolean denyPhysicalDeletable) {
        this.denyPhysicalDeletable = denyPhysicalDeletable;
    }

    public boolean isPolymorphism() {
        return polymorphism;
    }

    public void setPolymorphism(boolean polymorphism) {
        this.polymorphism = polymorphism;
    }

    public void setName(String name) {
        set("name", name);

    }

    private ArrayList<String> primaryKeyInfo;

    public ArrayList<String> getPrimaryKeyInfo() {
        return primaryKeyInfo;
    }

    public void setPrimaryKeyInfo(ArrayList<String> primaryKeyInfo) {
        this.primaryKeyInfo = primaryKeyInfo;
    }

    private ArrayList<String> foreignKeyInfo;

    public ArrayList<String> getForeignKeyInfo() {
        return foreignKeyInfo;
    }

    public void setForeignKeyInfo(ArrayList<String> foreignKeyInfo) {
        this.foreignKeyInfo = foreignKeyInfo;
    }
    // public ArrayList<Restriction> getRestrictions() {
    // return restrictions;
    // }
    //
    // public void setRestrictions(ArrayList<Restriction> restrictions) {
    // this.restrictions = restrictions;
    // }

    public void setChildNodes(List<ItemNodeModel> defaultTreeModel) {
        removeAll();
        if (defaultTreeModel != null) {
            for (ModelData child : defaultTreeModel) {
                add(child);
            }
        }

    }

}