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
import java.util.List;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class Constants implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static List<String> groupOperators = Arrays.asList(new String[] { "and", "or" });

    public static List<String> fullOperators = Arrays.asList(new String[] { "contains the word(s)", "is equal to",
            "is not equal to", "is greater than", "is greater or equals", "is lower than", "is lower or equals",
            "contains a word starting with", "contains the sentence" });

    public static List<String> fulltextOperators = Arrays.asList(new String[] { "Full text search" });

    public static List<String> dateOperators = Arrays.asList(new String[] { "equals", "is before", "is after" });

    public static List<String> numOperators = Arrays.asList(new String[] { "is equal to", "is not equal to", "is greater than",
            "is greater or equals", "is lower than", "is lower or equals" });

    public static List<String> booleanOperators = Arrays.asList(new String[] { "is equal to true", "is equal to false" });

    public static List<String> enumOperators = Arrays.asList(new String[] { "is equal to", "is lower than", "is greater than",
            "is lower or equals", "is greater or equals" });
}
