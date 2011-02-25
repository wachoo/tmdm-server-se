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
package org.talend.mdm.webapp.itemsbrowser2.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ViewBean implements Serializable, IsSerializable {

    private List<String> viewableXpaths;

    public List<String> getViewableXpaths() {
        return viewableXpaths;
    }

    public void addViewableXpath(String xpath) {
        if (this.viewableXpaths == null)
            viewableXpaths = new ArrayList<String>();
        viewableXpaths.add(xpath);
    }

    /**
     * DOC HSHU Comment method "getEntityNameFromViewName".
     */
    public static String getEntityFromViewName(String viewName) {

        String entity = null;
        if (viewName == null)
            return entity;

        int pos = viewName.lastIndexOf("_");
        entity = viewName.substring(pos + 1);
        return entity;

    }

    private String viewPK;

    private String description;

    private String descriptionLocalized;

    private String[] viewables;

    private Map<String, String> searchables;

    private Map<String, String> metaDataTypes;

    private String[] keys;

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getSearchables() {
        return searchables;
    }

    public void setSearchables(Map<String, String> searchables) {
        this.searchables = searchables;
    }

    public Map<String, String> getMetaDataTypes() {
        return metaDataTypes;
    }

    public void setMetaDataTypes(Map<String, String> metaDataTypes) {
        this.metaDataTypes = metaDataTypes;
    }

    public String[] getViewables() {
        return viewables;
    }

    public void setViewables(String[] viewables) {
        this.viewables = viewables;
    }

    public String getViewPK() {
        return viewPK;
    }

    public void setViewPK(String viewPK) {
        this.viewPK = viewPK;
    }

    public String getDescriptionLocalized() {
        return descriptionLocalized;
    }

    public void setDescriptionLocalized(String descriptionLocalized) {
        this.descriptionLocalized = descriptionLocalized;
    }

}
