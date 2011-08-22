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

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class SimpleCriterion implements Criteria {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String key;

    private String operator;

    private String value;

    private String info;

    public SimpleCriterion() {
        super();
    }

    public SimpleCriterion(String key, String operator, String value) {
        super();
        this.key = key;
        this.operator = operator;
        this.value = value;
    }

    public SimpleCriterion(String key, String operator, String value, String info) {
        this(key, operator, value);
        this.info = info;
    }

    public String toString() {
        return (key == null ? "" : key) + " " + (operator == null ? "" : operator) + " " + (value == null ? "" : value); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    }

    public String toAppearanceString() {
        return (key == null ? "" : key) + " " + (operator == null ? "" : operator) + " " + (info == null ? value : info); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getOperator() {
        return this.operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getInfo() {
        return this.info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public boolean equal(SimpleCriterion criteria) {
        if (criteria == null)
            return false;

        if (key.equals(criteria.getKey()) && operator.equals(criteria.getOperator()) && value.equals(criteria.getValue())) {
            return true;
        }

        return false;
    }
}
