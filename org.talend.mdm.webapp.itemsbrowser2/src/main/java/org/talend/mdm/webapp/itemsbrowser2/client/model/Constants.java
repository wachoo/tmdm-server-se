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

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class Constants implements Serializable, IsSerializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static List<String> groupOperators = Arrays.asList(new String[] { MessagesFactory.getMessages().criteria_AND(),
            MessagesFactory.getMessages().criteria_OR() });

    public static Map<String, String> fullOperators = new HashMap<String, String>();

    public static Map<String, String> fulltextOperators = new HashMap<String, String>();

    public static Map<String, String> dateOperators = new HashMap<String, String>();

    public static Map<String, String> numOperators = new HashMap<String, String>();

    public static Map<String, String> booleanOperators = new HashMap<String, String>();

    public static Map<String, String> enumOperators = new HashMap<String, String>();

    static {

        fullOperators.put("CONTAINS", MessagesFactory.getMessages().criteria_CONTAINS());
        fullOperators.put("EQUALS", MessagesFactory.getMessages().criteria_EQUALS());
        fullOperators.put("NOT_EQUALS", MessagesFactory.getMessages().criteria_NOT_EQUALS());
        fullOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_GREATER_THAN());
        fullOperators.put("GREATER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_GREATER_THAN_OR_EQUAL());
        fullOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_LOWER_THAN());
        fullOperators.put("LOWER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_LOWER_THAN_OR_EQUAL());
        fullOperators.put("STARTSWITH", MessagesFactory.getMessages().criteria_STARTSWITH());
        fullOperators.put("STRICTCONTAINS", MessagesFactory.getMessages().criteria_STRICTCONTAINS());

        fulltextOperators.put("FULLTEXTSEARCH", MessagesFactory.getMessages().criteria_FULLTEXTSEARCH());

        dateOperators.put("EQUALS", MessagesFactory.getMessages().criteria_DATEEQUALS());
        dateOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_DATELOWER_THAN());
        dateOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_DATEGREATER_THAN());

        numOperators.put("EQUALS", MessagesFactory.getMessages().criteria_EQUALS());
        numOperators.put("NOT_EQUALS", MessagesFactory.getMessages().criteria_NOT_EQUALS());
        numOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_GREATER_THAN());
        numOperators.put("GREATER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_GREATER_THAN_OR_EQUAL());
        numOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_LOWER_THAN());
        numOperators.put("LOWER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_LOWER_THAN_OR_EQUAL());

        booleanOperators.put("EQUALSTRUE", MessagesFactory.getMessages().criteria_BOOLEQUALSTRUE());
        booleanOperators.put("EQUALSFALSE", MessagesFactory.getMessages().criteria_BOOLEQUALSFALSE());

        enumOperators.put("EQUALS", MessagesFactory.getMessages().criteria_EQUALS());
        enumOperators.put("LOWER_THAN", MessagesFactory.getMessages().criteria_LOWER_THAN());
        enumOperators.put("GREATER_THAN", MessagesFactory.getMessages().criteria_GREATER_THAN());
        enumOperators.put("LOWER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_LOWER_THAN_OR_EQUAL());
        enumOperators.put("GREATER_THAN_OR_EQUAL", MessagesFactory.getMessages().criteria_GREATER_THAN_OR_EQUAL());
    }

}
