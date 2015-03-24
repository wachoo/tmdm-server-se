// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.webservice;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="WSProcessInstanceArray")
public class WSProcessInstanceArray {
    protected com.amalto.core.webservice.WSProcessInstance[] instance;
    
    public WSProcessInstanceArray() {
    }
    
    public WSProcessInstanceArray(com.amalto.core.webservice.WSProcessInstance[] instance) {
        this.instance = instance;
    }
    
    public com.amalto.core.webservice.WSProcessInstance[] getInstance() {
        return instance;
    }
    
    public void setInstance(com.amalto.core.webservice.WSProcessInstance[] instance) {
        this.instance = instance;
    }
}
