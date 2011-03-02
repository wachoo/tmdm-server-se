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
package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class MultipleCriteria implements Criteria {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String operator;

    private List<Criteria> children = new ArrayList<Criteria>();

    public MultipleCriteria() {
        super();
    }

    public MultipleCriteria(String operator) {
        super();
        this.operator = operator;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("" + Parser.BEGIN_BLOCK); //$NON-NLS-1$

        boolean first = true;

        for (Criteria c : children) {
            if (!first)
                sb.append(" " + operator + " "); //$NON-NLS-1$  //$NON-NLS-2$

            sb.append(Parser.BEGIN_BLOCK + c.toString() + Parser.END_BLOCK);
            first = false;
        }
        sb.append(Parser.END_BLOCK);
        final String string = sb.toString();
        if (string.equals("" + Parser.BEGIN_BLOCK + Parser.END_BLOCK)) //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        else
            return string;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<Criteria> getChildren() {
        return this.children;
    }

    public void add(Criteria criteria) {
        children.add(criteria);
    }
}
