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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class OperatorConstants implements Serializable, IsSerializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static List<String> groupOperators = Arrays.asList(new String[] { "AND", "OR" });

    public static Map<String, String> fullOperators = new LinkedHashMap<String, String>();

    public static Map<String, String> fulltextOperators = new LinkedHashMap<String, String>();

    public static Map<String, String> dateOperators = new LinkedHashMap<String, String>();

    public static Map<String, String> numOperators = new LinkedHashMap<String, String>();

    public static Map<String, String> booleanOperators = new LinkedHashMap<String, String>();

    public static Map<String, String> enumOperators = new LinkedHashMap<String, String>();

    static {

        fullOperators.put("EQUALS", MessagesFactory.getMessages().criteria_EQUALS()); //$NON-NLS-1$
        fullOperators.put("CONTAINS", MessagesFactory.getMessages().criteria_CONTAINS()); //$NON-NLS-1$
        fullOperators.put("NOT_EQUALS", MessagesFactory.getMessages().criteria_NOT_EQUALS()); //$NON-NLS-1$
        fullOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_GREATER_THAN()); //$NON-NLS-1$
        fullOperators.put("GREATER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_GREATER_THAN_OR_EQUAL()); //$NON-NLS-1$
        fullOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_LOWER_THAN()); //$NON-NLS-1$
        fullOperators.put("LOWER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_LOWER_THAN_OR_EQUAL()); //$NON-NLS-1$
        fullOperators.put("STARTSWITH", MessagesFactory.getMessages().criteria_STARTSWITH()); //$NON-NLS-1$
        fullOperators.put("STRICTCONTAINS", MessagesFactory.getMessages().criteria_STRICTCONTAINS()); //$NON-NLS-1$

        fulltextOperators.put("FULLTEXTSEARCH", MessagesFactory.getMessages().criteria_FULLTEXTSEARCH()); //$NON-NLS-1$

        dateOperators.put("EQUALS", MessagesFactory.getMessages().criteria_DATEEQUALS()); //$NON-NLS-1$
        dateOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_DATELOWER_THAN()); //$NON-NLS-1$
        dateOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_DATEGREATER_THAN()); //$NON-NLS-1$

        numOperators.put("EQUALS", MessagesFactory.getMessages().criteria_EQUALS()); //$NON-NLS-1$
        numOperators.put("NOT_EQUALS", MessagesFactory.getMessages().criteria_NOT_EQUALS()); //$NON-NLS-1$
        numOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_GREATER_THAN()); //$NON-NLS-1$
        numOperators.put("GREATER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_GREATER_THAN_OR_EQUAL()); //$NON-NLS-1$
        numOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_LOWER_THAN()); //$NON-NLS-1$
        numOperators.put("LOWER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_LOWER_THAN_OR_EQUAL()); //$NON-NLS-1$

        booleanOperators.put("EQUALS", MessagesFactory.getMessages().criteria_EQUALS()); //$NON-NLS-1$       

        enumOperators.put("EQUALS", MessagesFactory.getMessages().criteria_EQUALS()); //$NON-NLS-1$
        enumOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_LOWER_THAN()); //$NON-NLS-1$
        enumOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_GREATER_THAN()); //$NON-NLS-1$
        enumOperators.put("LOWER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_LOWER_THAN_OR_EQUAL()); //$NON-NLS-1$
        enumOperators.put("GREATER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_GREATER_THAN_OR_EQUAL()); //$NON-NLS-1$
    }

}
