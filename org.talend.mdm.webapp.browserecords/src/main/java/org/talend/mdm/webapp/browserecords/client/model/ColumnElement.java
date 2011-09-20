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

import java.io.Serializable;
import java.util.List;

public class ColumnElement implements Serializable {

    private String label;

    private String xPath;

    private List<ColumnElement> children;

    public ColumnElement() {

    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getxPath() {
        return xPath;
    }

    public void setxPath(String xPath) {
        this.xPath = xPath;
    }

    public List<ColumnElement> getChildren() {
        return children;
    }

    public void setChildren(List<ColumnElement> children) {
        this.children = children;
    }
}
