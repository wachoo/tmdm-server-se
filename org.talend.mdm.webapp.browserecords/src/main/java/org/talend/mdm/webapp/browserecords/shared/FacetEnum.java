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
package org.talend.mdm.webapp.browserecords.shared;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Widget;

public enum FacetEnum {

    MIN_INCLUSIVE("minInclusive"), //$NON-NLS-1$
    MAX_INCLUSIVE("maxInclusive"), //$NON-NLS-1$
    MIN_EXCLUSIVE("minExclusive"), //$NON-NLS-1$
    MAX_EXCLUSIVE("maxExclusive"), //$NON-NLS-1$

    LENGTH("length"), //$NON-NLS-1$
    MIN_LENGTH("minLength"), //$NON-NLS-1$
    MAX_LENGTH("maxLength"), //$NON-NLS-1$

    TOTAL_DIGITS("totalDigits"), //$NON-NLS-1$
    FRACTION_DIGITS("fractionDigits"), //$NON-NLS-1$

    ENUMERATION("enumeration"), //$NON-NLS-1$

    PATTERN("pattern"), //$NON-NLS-1$

    WHTE_SPACE("whiteSpace");//$NON-NLS-1$

    private String facetName;

    FacetEnum(String facetName) {
        this.facetName = facetName;
    }

    public String getFacetName() {
        return facetName;
    }

    public static void setFacetValue(String facet, Widget w, String value) {
        try {
            if (facet.equals(MIN_INCLUSIVE.getFacetName())) {
                NumberField field = (NumberField) w;
                // if ("integer".equals(field.getData("numberType"))){
                // field.setMinValue(Integer.parseInt(value));
                // } else {
                // field.setMinValue(Double.parseDouble(value));
                // }
                field.setData(MIN_INCLUSIVE.getFacetName(), value);
            } else if (facet.equals(MAX_INCLUSIVE.getFacetName())) {
                NumberField field = (NumberField) w;
                // if ("integer".equals(field.getData("numberType"))){
                // field.setMaxValue(Integer.parseInt(value));
                // } else {
                // field.setMaxValue(Double.parseDouble(value));
                // }
                field.setData(MAX_INCLUSIVE.getFacetName(), value);
            } else if (facet.equals(MIN_EXCLUSIVE.getFacetName())) {
                NumberField field = (NumberField) w;
                field.setData(MIN_EXCLUSIVE.getFacetName(), value);
            } else if (facet.equals(MAX_EXCLUSIVE.getFacetName())) {
                NumberField field = (NumberField) w;
                field.setData(MAX_EXCLUSIVE.getFacetName(), value);
            } else if (facet.equals(LENGTH.getFacetName())) {
                TextField<String> field = (TextField<String>) w;
                if (Integer.parseInt(value) > 0) {
                    field.setAllowBlank(false);
                }
                field.setMaxLength(Integer.parseInt(value));
                field.setMinLength(Integer.parseInt(value));
                field.setData(LENGTH.getFacetName(), value);
            } else if (facet.equals(MIN_LENGTH.getFacetName())) {
                TextField<String> field = (TextField<String>) w;
                if (Integer.parseInt(value) > 0) {
                    field.setAllowBlank(false);
                }
                field.setMinLength(Integer.parseInt(value));
                field.setData(MIN_LENGTH.getFacetName(), value);
            } else if (facet.equals(MAX_LENGTH.getFacetName())) {
                TextField<String> field = (TextField<String>) w;
                field.setMaxLength(Integer.parseInt(value));
                field.setData(MAX_LENGTH.getFacetName(), value);
            } else if (facet.equals(TOTAL_DIGITS.getFacetName())) {
                NumberField field = (NumberField) w;
                field.setData(TOTAL_DIGITS.getFacetName(), value);
            } else if (facet.equals(FRACTION_DIGITS.getFacetName())) {
                NumberField field = (NumberField) w;
                field.setData(FRACTION_DIGITS.getFacetName(), value);
            } else if (facet.equals(PATTERN.getFacetName())) {

            } else if (facet.equals(WHTE_SPACE.getFacetName())) {

            }
        } catch (ClassCastException e) {
            Log.error(e.getMessage(), e);
        }
    }
}
