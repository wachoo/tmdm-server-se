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
package org.talend.mdm.webapp.browserecords.server.displayrule;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class DisplayRule implements Serializable, IsSerializable {
    private String xpath;
    private String value;
    
    public DisplayRule(String xpath, String value) {
        super();
        this.xpath = xpath;
        this.value = value;
    }
            
    public DisplayRule(String type, String xpath, String value) {
        super();
        this.xpath = xpath;
        this.value = value;
    }
    
    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
}
