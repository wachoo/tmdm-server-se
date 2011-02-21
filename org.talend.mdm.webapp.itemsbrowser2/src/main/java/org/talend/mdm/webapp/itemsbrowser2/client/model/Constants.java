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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class Constants implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static List<String> groupOperators = Arrays.asList(new String[] { "and", "or" });

    public static Map<String, String> fullOperators = new HashMap<String, String>();

    public static Map<String, String> fulltextOperators = new HashMap<String, String>();

    public static Map<String, String> dateOperators = new HashMap<String, String>();

    public static Map<String, String> numOperators = new HashMap<String, String>();

    public static Map<String, String> booleanOperators = new HashMap<String, String>();

    public static Map<String, String> enumOperators = new HashMap<String, String>();

    static {

        fullOperators.put("CONTAINS", "contains the word(s)");
        fullOperators.put("EQUALS", "is equal to");
        fullOperators.put("NOT_EQUALS", "is not equal to");
        fullOperators.put("GREATER_THAN", "is greater than");
        fullOperators.put("GREATER_THAN_OR_EQUAL", "is greater or equals");
        fullOperators.put("LOWER_THAN", "is lower than");
        fullOperators.put("LOWER_THAN_OR_EQUAL", "is lower or equals");
        fullOperators.put("STARTSWITH", "contains a word starting with");
        fullOperators.put("STRICTCONTAINS", "contains the sentence");

        fulltextOperators.put("FULLTEXTSEARCH", "Full text search");

        dateOperators.put("EQUALS", "equals");
        dateOperators.put("LOWER_THAN", "is before");
        dateOperators.put("GREATER_THAN", "is after");

        numOperators.put("EQUALS", "is equal to");
        numOperators.put("NOT_EQUALS", "is not equal to");
        numOperators.put("GREATER_THAN", "is greater than");
        numOperators.put("GREATER_THAN_OR_EQUAL", "is greater or equals");
        numOperators.put("LOWER_THAN", "is lower than");
        numOperators.put("LOWER_THAN_OR_EQUAL", "is lower or equals");

        booleanOperators.put("EQUALSTRUE", "is equal to true");
        booleanOperators.put("EQUALSFALSE", "is equal to false");

        enumOperators.put("EQUALS", "is equal to");
        enumOperators.put("LOWER_THAN", "is lower than");
        enumOperators.put("GREATER_THAN", "is greater than");
        enumOperators.put("LOWER_THAN_OR_EQUAL", "is lower or equals");
        enumOperators.put("GREATER_THAN_OR_EQUAL", "is greater or equals");
    }

}
