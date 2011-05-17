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
package org.talend.mdm.webapp.itemsbrowser2.server.util;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.model.Criteria;
import org.talend.mdm.webapp.itemsbrowser2.client.model.MultipleCriteria;
import org.talend.mdm.webapp.itemsbrowser2.client.model.SimpleCriterion;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Parser;
import org.talend.mdm.webapp.itemsbrowser2.server.ItemsBrowserConfiguration;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeData;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.XtentisPort;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class CommonUtil {

    public static final char BEGIN_BLOCK = '('; //$NON-NLS-1$ 

    public static final char END_BLOCK = ')'; //$NON-NLS-1$ 

    public static final String AND = "AND"; //$NON-NLS-1$ 

    public static final String OR = "OR"; //$NON-NLS-1$ 

    /**
     * DOC HSHU Comment method "getPort".
     * 
     * @return
     * @throws XtentisWebappException
     */
    public static XtentisPort getPort() throws XtentisWebappException {
        if (!ItemsBrowserConfiguration.isStandalone()) {
            return com.amalto.webapp.core.util.Util.getPort();
        } else {
            return com.amalto.webapp.core.util.Util.getPort(FakeData.MDM_DEFAULT_ENDPOINTADDRESS, FakeData.MDM_DEFAULT_USERNAME,
                    FakeData.MDM_DEFAULT_PASSWORD, com.amalto.webapp.core.util.Util._FORCE_WEB_SERVICE_);
        }
    }

    public static boolean isEmpty(String s) {
        if (s == null)
            return true;
        if (s.trim().length() == 0)
            return true;
        return false;
    }

    /**
     * Join an arraylist of strings into a single string using a separator
     * 
     * @param strings
     * @param separator
     * @return a single string or null
     */
    public static String joinStrings(List<String> strings, String separator) {
        if (strings == null)
            return null;
        String res = ""; //$NON-NLS-1$ 
        for (int i = 0; i < strings.size(); i++) {
            res += (i > 0) ? separator : ""; //$NON-NLS-1$ 
            res += strings.get(i);
        }
        return res;
    }

    public static WSWhereItem buildWhereItems(String criteria) throws Exception {
        WSWhereItem wi = null;
        if (criteria.indexOf("../../t") > -1) { //$NON-NLS-1$
            ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
            if (criteria.indexOf("../../t") - 5 > -1) //$NON-NLS-1$
                conditions.add(buildWhereItemsByCriteria(Parser
                        .parse(criteria.substring(0, criteria.indexOf("../../t") - 5) + ")")));//$NON-NLS-1$  //$NON-NLS-2$   
            conditions.add(buildWhereItem(criteria.substring(criteria.indexOf("../../t"), criteria.length() - 1))); //$NON-NLS-1$

            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        } else
            wi = buildWhereItemsByCriteria(Parser.parse(criteria));
        return wi;
    }

    public static WSWhereItem buildWhereItemsByCriteria(Criteria criteria) throws Exception {
        WSWhereItem wi = null;
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
        if (criteria instanceof MultipleCriteria) {
            MultipleCriteria multipleCriteria = (MultipleCriteria) criteria;
            if (multipleCriteria.getOperator().equals(AND)) {
                WSWhereAnd and = new WSWhereAnd();

                for (Criteria current : multipleCriteria.getChildren()) {
                    if (current instanceof SimpleCriterion) {
                        WSWhereItem item = buildWhereItem(current.toString());
                        conditions.add(item);
                    } else if (current instanceof MultipleCriteria) {
                        WSWhereItem item = buildWhereItemsByCriteria(current);
                        conditions.add(item);
                    }
                }
                and.setWhereItems(conditions.toArray(new WSWhereItem[conditions.size()]));
                wi = new WSWhereItem(null, and, null);
            } else if (multipleCriteria.getOperator().equals(OR)) {
                WSWhereOr or = new WSWhereOr();

                for (Criteria current : multipleCriteria.getChildren()) {
                    if (current instanceof SimpleCriterion) {
                        WSWhereItem item = buildWhereItem(current.toString());
                        conditions.add(item);
                    } else if (current instanceof MultipleCriteria) {
                        WSWhereItem item = buildWhereItemsByCriteria(current);
                        conditions.add(item);
                    }
                }
                or.setWhereItems(conditions.toArray(new WSWhereItem[conditions.size()]));
                wi = new WSWhereItem(null, null, or);
            }
        } else if (criteria instanceof SimpleCriterion) {
            wi = buildWhereItem(criteria.toString());
        }
        return wi;
    }

    public static WSWhereItem buildWhereItem(String criteria) throws Exception {
        WSWhereItem wi;
        String[] filters = criteria.split(" "); //$NON-NLS-1$ 
        String filterXpaths, filterOperators, filterValues;

        filterXpaths = filters[0];
        filterOperators = filters[1];
        if (filters.length <= 2)
            filterValues = " "; //$NON-NLS-1$ 
        else
            filterValues = filters[2];

        if (filterXpaths == null || filterXpaths.trim().equals("")) //$NON-NLS-1$ 
            return null;

        WSWhereCondition wc = new WSWhereCondition(filterXpaths, Util.getOperator(filterOperators), filterValues,
                WSStringPredicate.NONE, false);
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
        WSWhereItem item = new WSWhereItem(wc, null, null);
        conditions.add(item);
        
        //FIXME: This is a workaround for boolean criteria
        if(filterValues!=null&&filterValues.equals("false")) {//$NON-NLS-1$ 
            wc.setStringPredicate(WSStringPredicate.OR);
            conditions.add(new WSWhereItem(new WSWhereCondition(
                    filterXpaths, 
                    Util.getOperator("EMPTY_NULL"),//$NON-NLS-1$ 
                    filterValues,
                    WSStringPredicate.NONE, 
                    false), null, null));
        }

        if (conditions.size() == 0) {
            wi = null;
        } else {
            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        }

        return wi;

    }
}
