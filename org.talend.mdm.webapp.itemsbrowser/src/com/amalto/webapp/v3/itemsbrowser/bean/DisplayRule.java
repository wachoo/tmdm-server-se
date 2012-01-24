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
package com.amalto.webapp.v3.itemsbrowser.bean;



/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class DisplayRule{
    
    private String type;
    private String xpath;
    private String value;
    
    public DisplayRule(String type, String value) {
        super();
        this.type = type;
        this.value = value;
    }
    
            
    public DisplayRule(String type, String xpath, String value) {
        super();
        this.type = type;
        this.xpath = xpath;
        this.value = value;
    }


    public String getXpath() {
        return xpath;
    }

    
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }


    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getValue() {
        return value;
    }

    
    public void setValue(String value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "DisplayRule [type=" + type + ", xpath=" + xpath + ", value=" + value + "]";
    }
    
    
          
}
