// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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

import java.io.Serializable;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class ItemResult implements Serializable {

    public static final int SUCCESS = 0;

    public static final int FAILURE = 1;

    public static final int UNCHANGED = 2;

    private int status;

    private String description;

    private String returnValue;

    public ItemResult() {
    }

    public ItemResult(int status) {
        this(status, null);
    }

    public ItemResult(int status, String description) {
        setStatus(status);
        setDescription(description);
    }

    public ItemResult(int status, String description, String returnValue) {
        setStatus(status);
        setDescription(description);
        setReturnValue(returnValue);
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }
}
