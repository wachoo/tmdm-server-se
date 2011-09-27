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
package org.talend.mdm.webapp.browserecords.server.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.Criteria;
import org.talend.mdm.webapp.browserecords.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.MultipleCriteria;
import org.talend.mdm.webapp.browserecords.client.model.SimpleCriterion;
import org.talend.mdm.webapp.browserecords.client.util.Parser;
import org.talend.mdm.webapp.browserecords.server.BrowseRecordsConfiguration;
import org.talend.mdm.webapp.browserecords.server.mockup.FakeData;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

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

    public static final String AND = "AND"; //$NON-NLS-1$

    public static final String OR = "OR"; //$NON-NLS-1$ 

    /**
     * DOC HSHU Comment method "getPort".
     * 
     * @return
     * @throws XtentisWebappException
     */
    public static XtentisPort getPort() throws XtentisWebappException {
        if (!BrowseRecordsConfiguration.isStandalone()) {
            return com.amalto.webapp.core.util.Util.getPort();
        } else {
            return com.amalto.webapp.core.util.Util.getPort(FakeData.MDM_DEFAULT_ENDPOINTADDRESS, FakeData.MDM_DEFAULT_USERNAME,
                    FakeData.MDM_DEFAULT_PASSWORD, com.amalto.webapp.core.util.Util._FORCE_WEB_SERVICE_);
        }
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
        WSWhereItem wi;
        if (criteria.contains("../../t")) { //$NON-NLS-1$
            List<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
            if (criteria.indexOf("../../t") - 5 > -1) { //$NON-NLS-1$
                conditions.add(buildWhereItemsByCriteria(Parser.parse(criteria.substring(0, criteria.indexOf("../../t") - 5) + ")")));//$NON-NLS-1$  //$NON-NLS-2$
            }
            conditions.add(buildWhereItem(criteria.substring(criteria.indexOf("../../t"), criteria.length() - 1))); //$NON-NLS-1$

            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        } else {
            wi = buildWhereItemsByCriteria(Parser.parse(criteria));
        }
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
        String filterXpaths, filterOperators;
        String filterValues = "";

        filterXpaths = filters[0];
        filterOperators = filters[1];
        if (filters.length <= 2) {
            filterValues = " "; //$NON-NLS-1$
        } else {
            // Value might contains spaces (if search contains a sentence) and split call might separated words.
            for (int i = 2; i < filters.length; i++) {
                filterValues += filters[i];
                if (i != filters.length - 1) {
                    filterValues += " ";
                }
            }
        }

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

    public static List<ItemNodeModel> getDefaultTreeModel(TypeModel model, String language) {
        List<ItemNodeModel> itemNodes = new ArrayList<ItemNodeModel>();

        if (model.getMinOccurs() > 1 && model.getMaxOccurs() > model.getMinOccurs()) {
            for (int i = 0; i < model.getMaxOccurs() - model.getMinOccurs(); i++) {
                ItemNodeModel itemNode = new ItemNodeModel();
                itemNodes.add(itemNode);
                if (model.getForeignkey() != null)
                    break;
            }
        } else {
            ItemNodeModel itemNode = new ItemNodeModel();
            itemNodes.add(itemNode);
        }

        for (ItemNodeModel node : itemNodes) {
            if (model.isSimpleType()) {
                setDefaultValue(model, node);
            } else {
                ComplexTypeModel complexModel = (ComplexTypeModel) model;
                List<TypeModel> children = complexModel.getSubTypes();
                List<ItemNodeModel> list = new ArrayList<ItemNodeModel>();
                for (TypeModel typeModel : children) {
                    list.addAll(getDefaultTreeModel(typeModel, language));
                }
                node.setChildNodes(list);
            }
            node.setName(model.getName());
            node.setBindingPath(model.getXpath());
            node.setDescription(model.getDescriptionMap().get(language));
            node.setLabel(model.getLabel(language));
        }
        return itemNodes;
    }

    private static void setDefaultValue(TypeModel model, ItemNodeModel node) {

        if (model.getDefaultValueExpression() != null && model.getDefaultValueExpression().trim().length() > 0) {
            if (!"".equals(model.getForeignkey()) && model.getForeignkey() != null) { //$NON-NLS-1$
                ForeignKeyBean foreignKeyBean = new ForeignKeyBean();
                foreignKeyBean.setId(model.getDefaultValue());
                foreignKeyBean.setForeignKeyPath(model.getForeignkey());
                node.setObjectValue(foreignKeyBean);
            } else {
                node.setObjectValue(model.getDefaultValue());
            }
        } else {
            if (model.getType().getTypeName().equals(DataTypeConstants.BOOLEAN.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.BOOLEAN.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DATE.getTypeName())) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
                String dateStr = df.format((Date) DataTypeConstants.DATE.getDefaultValue());
                node.setObjectValue(dateStr);
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DATETIME.getTypeName())) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
                DateFormat tf = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
                String dateStr = df.format((Date) DataTypeConstants.DATETIME.getDefaultValue());
                String timeStr = tf.format((Date) DataTypeConstants.DATETIME.getDefaultValue());
                node.setObjectValue(dateStr + "T" + timeStr); //$NON-NLS-1$
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DECIMAL.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.DECIMAL.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DOUBLE.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.DOUBLE.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.FLOAT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.FLOAT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.INT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.INT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.INTEGER.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.INTEGER.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.LONG.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.LONG.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.SHORT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.SHORT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.STRING.getTypeName())) {
                if (model.getForeignkey() != null && model.getForeignkey().trim().length() > 0) {
                    ForeignKeyBean foreignKeyBean = new ForeignKeyBean();
                    foreignKeyBean.setId(""); //$NON-NLS-1$
                    foreignKeyBean.setForeignKeyPath(model.getForeignkey());
                    node.setObjectValue(foreignKeyBean);
                } else {
                    node.setObjectValue((Serializable) DataTypeConstants.STRING.getDefaultValue());
                }
            } else if (model.getType().getTypeName().equals(DataTypeConstants.UUID.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.UUID.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.AUTO_INCREMENT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.AUTO_INCREMENT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.PICTURE.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.PICTURE.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.URL.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.URL.getDefaultValue());
            }
        }
    }
}
