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

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class SubTypeBean implements Comparable<SubTypeBean>, Serializable {

    private String name;

    private String label;

    private String OrderValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOrderValue() {
        return OrderValue;
    }

    public void setOrderValue(String orderValue) {
        OrderValue = orderValue;
    }

    @Override
    public String toString() {
        return "SubTypeBean [name=" + name + ", label=" + label + ", OrderValue=" + OrderValue + "]";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    /*
     * (non-Jsdoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(SubTypeBean o) {

        if (o != null && o instanceof SubTypeBean) {
            if (o.getOrderValue() != null && this.getOrderValue() != null) {

                if (!o.getOrderValue().matches("\\d+"))o.setOrderValue(String.valueOf(Integer.MAX_VALUE));//$NON-NLS-1$
                if (!this.getOrderValue().matches("\\d+"))this.setOrderValue(String.valueOf(Integer.MAX_VALUE));//$NON-NLS-1$

                return Integer.valueOf(this.getOrderValue()).compareTo(Integer.valueOf(o.getOrderValue()));
            }
        }
        return 0;
    }

}
