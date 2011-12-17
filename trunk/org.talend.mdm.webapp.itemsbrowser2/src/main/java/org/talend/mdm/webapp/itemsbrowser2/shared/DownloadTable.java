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

import com.google.gwt.user.client.rpc.IsSerializable;

public class DownloadTable implements Serializable, IsSerializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String name;

    private String[] fields;

    private String[] keys;

    public DownloadTable() {

    }

    public DownloadTable(String name, String[] keys, String[] fields) {
        this.name = name;
        this.fields = fields;
        this.keys = keys;
    }

    public String[] getFields() {
        return fields;
    }

    public void setFields(String[] fields) {
        this.fields = fields;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
