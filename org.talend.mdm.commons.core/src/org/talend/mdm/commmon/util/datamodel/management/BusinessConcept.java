// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.commmon.util.datamodel.management;

import com.sun.xml.xsom.XSElementDecl;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class BusinessConcept {

    public static final String APPINFO_X_HIDE = "X_Hide";

    public static final String APPINFO_X_WRITE = "X_Write";

    private XSElementDecl e;

    private String name;
    
    private String correspondTypeName;

    // TODO: translate it from technique to business logic
    // annotations{label,access rules,foreign keys,workflow,schematron,lookup fields...}
    // restrictions
    // enumeration

    public BusinessConcept(XSElementDecl e) {
        super();
        this.e = e;
        this.name = e.getName();
        this.correspondTypeName=e.getType().getName();
    }

    public XSElementDecl getE() {
        return e;
    }

    public String getName() {
        return name;
    }
    
    public String getCorrespondTypeName() {
        return correspondTypeName;
    }

    @Override
    public String toString() {
        return "BusinessConcept [name=" + name + "]";
    }

}
