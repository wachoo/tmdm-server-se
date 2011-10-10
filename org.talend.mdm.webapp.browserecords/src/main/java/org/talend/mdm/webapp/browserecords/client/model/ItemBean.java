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

import java.util.List;

public class ItemBean extends org.talend.mdm.webapp.base.client.model.ItemBean {

    private static final long serialVersionUID = -1733441307059646670L;

    public static final String TREEMODE = "M_TREE_VIEW", PERSOMODE = "M_PERSO_VIEW", SMARTMODE = "M_SMART_VIEW"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private String description;

    private String displayPKInfo;

    private String smartViewMode = TREEMODE;
    
    private List<String> pkInfoList;

    private boolean readOnly;

    private String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Deprecated
    public ItemBean() {
    }

    public ItemBean(String concept, String ids, String itemXml) {
        super(concept, ids, itemXml);
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

    public String getSmartViewMode() {
        return smartViewMode;
    }

    public void setSmartViewMode(String smartViewMode) {
        this.smartViewMode = smartViewMode;
    }
    
    public List<String> getPkInfoList() {
        return pkInfoList;
    }

    public void setPkInfoList(List<String> pkInfoList) {
        this.pkInfoList = pkInfoList;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
