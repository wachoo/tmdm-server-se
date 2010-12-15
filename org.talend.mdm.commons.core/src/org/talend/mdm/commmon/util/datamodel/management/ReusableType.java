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

import com.sun.xml.xsom.XSType;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ReusableType {

    private XSType xsType;

    private String name;

    private String parentName;

    // TODO: translate it from technique to business logic
    // mainly maintain the relationships among different business concepts

    public ReusableType(XSType xsType) {
        super();
        this.xsType = xsType;
        name = xsType.getName();
        parentName = xsType.getBaseType().getName();// Is this the best way?
    }

    public String getParentName() {
        return parentName;
    }

    @Override
    public String toString() {
        return "[name=" + name + "]";
    }

}
