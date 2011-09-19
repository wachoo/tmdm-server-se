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
package org.talend.mdm.webapp.browserecords.client.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemBean extends ItemBaseModel {

    /**
     * 
     */
    private static final long serialVersionUID = -1733441307059646670L;

    private String concept;

    private String ids;

    private String itemXml;

    private Map<String, ForeignKeyBean> foreignkeyDesc = new HashMap<String, ForeignKeyBean>();

    private String description;

    private String displayPKInfo;

    /**
     * DOC HSHU ItemBean constructor comment.
     */
    @Deprecated
    public ItemBean() {
        // TODO Auto-generated constructor stub
    }

    public ItemBean(String concept, String ids, String itemXml) {
        super();
        this.concept = concept;
        this.ids = ids;
        this.itemXml = itemXml;

    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getItemXml() {
        return itemXml;
    }

    public void setItemXml(String itemXml) {
        this.itemXml = itemXml;
    }

    /**
     * DOC HSHU Comment method "getPk".
     */
    public String getPk() {
        String pk = concept;

        if (ids != null) {
            pk += " " + ids;//$NON-NLS-1$ 
        }

        return pk;
    }

    public void setForeignkeyDesc(String fkValue, ForeignKeyBean desc) {
        foreignkeyDesc.put(fkValue, desc);
    }

    public ForeignKeyBean getForeignkeyDesc(String fkValue) {
        return foreignkeyDesc.get(fkValue);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayPKInfo() {
        return displayPKInfo;
    }

    public void setDisplayPKInfo(String displayPKInfo) {
        this.displayPKInfo = displayPKInfo;
    }

    @Override
    public String toString() {
        return "ItemBean [concept=" + concept + ", ids=" + ids + ", itemXml=" + itemXml + "]";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$  
    }

    /**
     * DOC HSHU Comment method "copy".
     */
    public void copy(ItemBean itemBean) {
        // FIXME is this ugly?
        setConcept(itemBean.getConcept());
        setIds(itemBean.getIds());
        this.foreignkeyDesc = itemBean.foreignkeyDesc;
        setItemXml(itemBean.getItemXml());
        Collection<String> names = itemBean.getPropertyNames();
        for (String name : names) {
            this.set(name, itemBean.get(name));
        }
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof ItemBean))
            return false;

        ItemBean item = (ItemBean) obj;
        if (item.ids != null) {
            return item.ids.equals(this.ids);
        }
        return item.ids == this.ids;

    }
}
