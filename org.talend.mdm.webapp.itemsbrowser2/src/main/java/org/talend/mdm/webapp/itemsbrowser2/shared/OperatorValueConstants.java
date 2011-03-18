package org.talend.mdm.webapp.itemsbrowser2.shared;

import java.util.Arrays;
import java.util.List;

public class OperatorValueConstants {

    private static final long serialVersionUID = 1L;

    public static List<String> groupOperatorVales = Arrays.asList(new String[] { "AND", "OR" }); //$NON-NLS-1$ //$NON-NLS-2$ 

    public static List<String> fullOperatorValues = Arrays.asList(new String[] { "CONTAINS", "EQUALS", "NOT_EQUALS", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "GREATER_THAN", "GREATER_THAN_OR_EQUAL", "LOWER_THAN", "LOWER_THAN_OR_EQUAL", "STARTSWITH", "STRICTCONTAINS" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$

    public static List<String> fulltextOperatorValues = Arrays.asList(new String[] { "FULLTEXTSEARCH" }); //$NON-NLS-1$

    public static List<String> dateOperatorValues = Arrays.asList(new String[] { "EQUALS", "LOWER_THAN", "GREATER_THAN" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static List<String> numOperatorValues = Arrays.asList(new String[] { "EQUALS", "NOT_EQUALS", "GREATER_THAN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "GREATER_THAN_OR_EQUAL", "LOWER_THAN", "LOWER_THAN_OR_EQUAL" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    public static List<String> booleanOperatorValues = Arrays.asList(new String[] { "EQUALS" }); //$NON-NLS-1$

    public static List<String> enumOperatorValues = Arrays.asList(new String[] { "EQUALS", "LOWER_THAN", "GREATER_THAN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            "LOWER_THAN_OR_EQUAL", "GREATER_THAN_OR_EQUAL" }); //$NON-NLS-1$ //$NON-NLS-2$

}
