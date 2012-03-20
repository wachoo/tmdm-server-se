// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.journal.server.service;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;

import org.dom4j.Attribute;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.journal.server.model.ForeignKeyInfoTransformer;
import org.talend.mdm.webapp.journal.shared.FKInstance;
import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.history.Action;
import com.amalto.core.history.DocumentHistory;
import com.amalto.core.history.DocumentHistoryFactory;
import com.amalto.core.history.DocumentHistoryNavigator;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.EmptyDocument;
import com.amalto.core.history.ModificationMarker;
import com.amalto.core.history.UniqueIdTransformer;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;

/**
 * The server side implementation of the RPC service.
 */
public class JournalDBService {
    
    private DocumentHistory factory = DocumentHistoryFactory.getInstance().create();
    
    private static final String CURRENT_ACTION = "current"; //$NON-NLS-1$

    private static final String PREVIOUS_ACTION = "before";  //$NON-NLS-1$

    private static final String NEXT_ACTION = "next";  //$NON-NLS-1$
    
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss"); //$NON-NLS-1$

    public JournalDBService() {

    }
    
    public Object[] getResultListByCriteria(JournalSearchCriteria criteria, int start, int limit, String sort, String field,
            boolean isBrowseRecord) throws Exception {

        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
        String conceptName = "Update"; //$NON-NLS-1$
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();

        if (isBrowseRecord) {
            Configuration configuration = Configuration.getInstance(true);
            String dataCluster = configuration.getCluster();
            String dataModel = configuration.getModel();
            WSWhereCondition clusterwc = new WSWhereCondition(
                    "DataCluster", WSWhereOperator.EQUALS, dataCluster.trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereCondition modelwc = new WSWhereCondition(
                    "DataModel", WSWhereOperator.EQUALS, dataModel.trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$

            WSWhereItem wsWhereDataCluster = new WSWhereItem(clusterwc, null, null);
            WSWhereItem wsWhereDataModel = new WSWhereItem(modelwc, null, null);
            conditions.add(wsWhereDataCluster);
            conditions.add(wsWhereDataModel);
        }

        if (criteria.getEntity() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "Concept", isBrowseRecord ? WSWhereOperator.EQUALS : WSWhereOperator.CONTAINS, criteria.getEntity().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getKey() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "Key", isBrowseRecord ? WSWhereOperator.EQUALS : WSWhereOperator.CONTAINS, criteria.getKey().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getSource() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "Source", WSWhereOperator.EQUALS, criteria.getSource().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getOperationType() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "OperationType", WSWhereOperator.EQUALS, criteria.getOperationType().trim(), WSStringPredicate.NONE, false); //$NON-NLS-1$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getStartDate() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "TimeInMillis", WSWhereOperator.GREATER_THAN_OR_EQUAL, criteria.getStartDate().getTime() + "", WSStringPredicate.NONE, false); //$NON-NLS-1$ //$NON-NLS-2$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        if (criteria.getEndDate() != null) {
            WSWhereCondition wc = new WSWhereCondition(
                    "TimeInMillis", WSWhereOperator.LOWER_THAN_OR_EQUAL, criteria.getEndDate().getTime() + "", WSStringPredicate.NONE, false); //$NON-NLS-1$ //$NON-NLS-2$
            WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
            conditions.add(wsWhereItem);
        }

        WSWhereItem wi;
        if (conditions.size() == 0) {
            wi = null;
        } else if (conditions.size() == 1) {
            wi = conditions.get(0);
        } else {
            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        }

        WSGetItems item = new WSGetItems();
        item.setConceptName(conceptName);
        item.setWhereItem(wi);
        item.setTotalCountOnFirstResult(true);
        item.setSkip(start);
        item.setMaxItems(limit);
        item.setWsDataClusterPK(wsDataClusterPK);

        int totalSize = 0;
        List<JournalGridModel> list = new ArrayList<JournalGridModel>();
        WSStringArray resultsArray = Util.getPort().getItems(item);
        String[] results = resultsArray == null ? new String[0] : resultsArray.getStrings();
        Document document = Util.parse(results[0]);
        totalSize = Integer.parseInt(document.getDocumentElement().getTextContent());

        for (int i = 1; i < results.length; i++) {
            String result = results[i];
            list.add(this.parseString2Model(result));
        }

        Object[] resArr = new Object[2];
        resArr[0] = totalSize;
        resArr[1] = list;
        return resArr;
    }
        
    public JournalTreeModel getDetailTreeModel(String[] idsArr) throws Exception {
        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
        String conceptName = "Update"; //$NON-NLS-1$
        WSGetItem wsGetItem = new WSGetItem(new WSItemPK(wsDataClusterPK, conceptName, idsArr));
        WSItem wsItem = Util.getPort().getItem(wsGetItem);
        String content = wsItem.getContent();
        JournalTreeModel root = new JournalTreeModel("Update"); //$NON-NLS-1$
        
        if (content == null)
            return root;
        
        if (content.length() == 0)
            return root;
        
        Document doc = Util.parse(content);
        String concept = Util.getFirstTextNode(doc, "/Update/Concept"); //$NON-NLS-1$
        String dataModel = Util.getFirstTextNode(doc, "/Update/DataModel"); //$NON-NLS-1$
        String dataCluster = Util.getFirstTextNode(doc, "/Update/DataCluster"); //$NON-NLS-1$
            
        root.add(new JournalTreeModel("UserName:" + checkNull(Util.getFirstTextNode(doc, "/Update/UserName")))); //$NON-NLS-1$ //$NON-NLS-2$
        root.add(new JournalTreeModel("Source:" + checkNull(Util.getFirstTextNode(doc, "/Update/Source")))); //$NON-NLS-1$ //$NON-NLS-2$
        root.add(new JournalTreeModel("TimeInMillis:" + checkNull(Util.getFirstTextNode(doc, "/Update/TimeInMillis")))); //$NON-NLS-1$ //$NON-NLS-2$
        root.add(new JournalTreeModel("OperationType:" + checkNull(Util.getFirstTextNode(doc, "/Update/OperationType")))); //$NON-NLS-1$ //$NON-NLS-2$
        root.add(new JournalTreeModel("Concept:" + checkNull(concept))); //$NON-NLS-1$
        root.add(new JournalTreeModel("RevisionID:" + checkNull(Util.getFirstTextNode(doc, "/Update/RevisionID")))); //$NON-NLS-1$ //$NON-NLS-2$
        root.add(new JournalTreeModel("DataCluster:" + checkNull(dataCluster))); //$NON-NLS-1$
        root.add(new JournalTreeModel("DataModel:" + checkNull(dataModel))); //$NON-NLS-1$
        root.add(new JournalTreeModel("Key:" + checkNull(Util.getFirstTextNode(doc, "/Update/Key")))); //$NON-NLS-1$ //$NON-NLS-2$                       

        XSElementDecl decl = this.getXSElementDecl(dataModel, concept);
        Set<String> roleSet = Util.getNoAccessRoleSet(decl);
        boolean isAuth = Util.isAuth(roleSet);
        root.setAuth(isAuth);
        
        if(isAuth) {
            NodeList ls = Util.getNodeList(doc, "/Update/Item"); //$NON-NLS-1$
            if (ls.getLength() > 0) {
                for (int i = 0; i < ls.getLength(); i++) {
                    List<JournalTreeModel> list = new ArrayList<JournalTreeModel>();
                    String path = Util.getFirstTextNode(doc, "/Update/Item[" + (i + 1) + "]/path"); //$NON-NLS-1$//$NON-NLS-2$
                    String elementPath = path.replaceAll("\\[\\d+\\]$", ""); //$NON-NLS-1$//$NON-NLS-2$
                    FKInstance fkInstance = getRetrieveConf(decl, elementPath);

                    String oldValue = checkNull(Util.getFirstTextNode(doc, "/Update/Item[" + (i + 1) + "]/oldValue")); //$NON-NLS-1$//$NON-NLS-2$
                    String newValue = checkNull(Util.getFirstTextNode(doc, "/Update/Item[" + (i + 1) + "]/newValue")); //$NON-NLS-1$ //$NON-NLS-2$
                    
                    if (fkInstance.isRetireveFKInfo() && !"".equals(oldValue) && fkInstance.getFkInfo() != null) { //$NON-NLS-1$
                        oldValue = getFKInfoByRetrieveConf(dataCluster, fkInstance.getFkInfo(), oldValue);
                    }

                    if (fkInstance.isRetireveFKInfo() && !"".equals(newValue) && fkInstance.getFkInfo() != null) { //$NON-NLS-1$
                        newValue = getFKInfoByRetrieveConf(dataCluster, fkInstance.getFkInfo(), newValue);
                    }
                    
                    list.add(new JournalTreeModel("path:" + path)); //$NON-NLS-1$
                    list.add(new JournalTreeModel("oldValue:" + oldValue)); //$NON-NLS-1$
                    list.add(new JournalTreeModel("newValue:" + newValue)); //$NON-NLS-1$
                    
                    JournalTreeModel itemModel = new JournalTreeModel("Item", list); //$NON-NLS-1$
                    root.add(itemModel);
                }
            }
        }
        
        return root;
    }

    public String getComparisionTreeString(JournalParameters parameter) throws Exception {
        DocumentHistoryNavigator navigator = factory.getHistory(parameter.getDataClusterName(), parameter.getDataModelName(),
                parameter.getConceptName(), parameter.getId(), parameter.getRevisionId());

        MetadataRepository metadataRepository = new MetadataRepository();
        TypeMetadata documentTypeMetadata = metadataRepository.getType(parameter.getConceptName());
        if (documentTypeMetadata == null) {
            try {
                DataModelPOJO dataModel = com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModel(
                        new DataModelPOJOPK(parameter.getDataModelName()));
                if (dataModel == null) {
                    throw new IllegalArgumentException("Data model '" + parameter.getDataModelName() + "' does not exist."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                String schemaString = dataModel.getSchema();
                metadataRepository.load(new ByteArrayInputStream(schemaString.getBytes("UTF-8"))); //$NON-NLS-1$

                documentTypeMetadata = metadataRepository.getType(parameter.getDataModelName());
                if (documentTypeMetadata == null) {
                    throw new IllegalArgumentException(
                            "Cannot find type information for type '" + parameter.getDataModelName() + "' in data cluster '" + parameter.getDataClusterName() + "', in data model '" + parameter.getDataModelName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
            } catch (Exception e) {
                throw new ServletException("Could not initialize type information", e); //$NON-NLS-1$
            }
        }

        navigator.goTo(new Date(parameter.getDate()));
        Action modificationMarkersAction = navigator.currentAction();
        com.amalto.core.history.Document document = EmptyDocument.INSTANCE;
        if (CURRENT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            document = navigator.current();
        } else if (PREVIOUS_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasPrevious()) {
                document = navigator.previous();
            }
        } else if (NEXT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasNext()) {
                document = navigator.next();
            }
        } else {
            throw new ServletException(new IllegalArgumentException("Action '" + parameter.getAction() + " is not supported.")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        List<DocumentTransformer> transformers = Arrays.asList(
                new ForeignKeyInfoTransformer(documentTypeMetadata, parameter.getDataClusterName()), new UniqueIdTransformer(),
                new ModificationMarker(modificationMarkersAction));
        com.amalto.core.history.Document transformedDocument = document;
        for (DocumentTransformer transformer : transformers) {
            transformedDocument = document.transform(transformer);
        }

        return transformedDocument.exportToString();
    }
    
    public JournalTreeModel getComparisionTreeModel(String xmlStr){
        JournalTreeModel root = new JournalTreeModel("root", "Document"); //$NON-NLS-1$ //$NON-NLS-2$
        if(xmlStr == null)
            return root;
        
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = null;
        try {
            document = reader.read(new ByteArrayInputStream(xmlStr.getBytes()));
        } catch (DocumentException e) {
            e.printStackTrace();
        }  

        org.dom4j.Element rootElement = document.getRootElement();
        this.retrieveElement(rootElement, root);

        return root;
    }
    
    public boolean restoreRecord(JournalParameters parameter) throws Exception {
        Date historyDate = new Date(parameter.getDate());
        DocumentHistoryNavigator navigator = factory.getHistory(parameter.getDataClusterName(), parameter.getDataModelName(),
                parameter.getConceptName(), parameter.getId(), parameter.getRevisionId());
        navigator.goTo(historyDate);
        
        com.amalto.core.history.Document document = EmptyDocument.INSTANCE;
        if (CURRENT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            document = navigator.current();
        } else if (PREVIOUS_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasPrevious()) {
                document = navigator.previous();
            }
        } else if (NEXT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasNext()) {
                document = navigator.next();
            }
        } else {
            throw new ServletException(new IllegalArgumentException("Action '" + parameter.getAction() + " is not supported.")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        document.restore();
        return true;
    }
    
    private void retrieveElement(org.dom4j.Element element, JournalTreeModel root) {
        List list = element.elements();
        JournalTreeModel model = this.getModelByElement(element);
        root.add(model);
        if(list.size() == 0){
            return;
        }
        
        for(Object obj : list){
            org.dom4j.Element el = (org.dom4j.Element)obj;
            retrieveElement(el, model);
        }
    }
    
    private JournalTreeModel getModelByElement(org.dom4j.Element element) {
        JournalTreeModel model = null;
        Attribute idAttr=element.attribute("id"); //$NON-NLS-1$
        String id = null;
        if(idAttr != null){
            id = idAttr.getValue();
        }
        
        Attribute clsAttr=element.attribute("cls"); //$NON-NLS-1$
        String cls = null;
        if(clsAttr != null){
            cls = clsAttr.getValue();
        }
        
        String value = element.getText();
        if(!value.equalsIgnoreCase("")) //$NON-NLS-1$
            value = ":" + value; //$NON-NLS-1$
        
        if (cls == null)
            model = new JournalTreeModel(id, element.getName() + value);
        else
            model = new JournalTreeModel(id, element.getName() + value, cls);
        return model;
    }
    
    private String getFKInfoByRetrieveConf(String dataCluster, String fkInfo, String rightValueOrPath) {
        String fkInfoValue = ""; //$NON-NLS-1$
        String conceptName = fkInfo.substring(0, fkInfo.indexOf("/")); //$NON-NLS-1$
        String value = rightValueOrPath;

        if (rightValueOrPath.indexOf("[") == 0 && rightValueOrPath.lastIndexOf("]") == rightValueOrPath.length() - 1) { //$NON-NLS-1$ //$NON-NLS-2$
            value = rightValueOrPath.subSequence(1, rightValueOrPath.length() - 1).toString();
        }

        String ids[] = { value };

        try {
            WSItem wsItem = Util.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(dataCluster), conceptName, ids)));
            Document document = Util.parse(wsItem.getContent());
            NodeList list = Util.getNodeList(document, "/" + fkInfo); //$NON-NLS-1$
            Node it = list.item(0);

            if (it != null) {
                fkInfoValue = it.getTextContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fkInfoValue;
    }

    private XSElementDecl getXSElementDecl(String dataModel, String concept) throws Exception {
        Map<String, XSElementDecl> map = CommonDWR.getConceptMap(dataModel);
        return map.get(concept);
    }

    private JournalGridModel parseString2Model(String xmlStr) throws Exception {
        JournalGridModel model = new JournalGridModel();
        Document doc = Util.parse(xmlStr);
        String source = checkNull(Util.getFirstTextNode(doc, "result/Update/Source")); //$NON-NLS-1$
        String timeInMillis = checkNull(Util.getFirstTextNode(doc, "result/Update/TimeInMillis")); //$NON-NLS-1$

        model.setDataContainer(checkNull(Util.getFirstTextNode(doc, "result/Update/DataCluster"))); //$NON-NLS-1$
        model.setDataModel(checkNull(Util.getFirstTextNode(doc, "result/Update/DataModel")));  //$NON-NLS-1$
        model.setEntity(checkNull(Util.getFirstTextNode(doc, "result/Update/Concept"))); //$NON-NLS-1$
        model.setKey(checkNull(Util.getFirstTextNode(doc, "result/Update/Key"))); //$NON-NLS-1$
        model.setRevisionId(checkNull(Util.getFirstTextNode(doc, "result/Update/RevisionID"))); //$NON-NLS-1$
        model.setOperationType(checkNull(Util.getFirstTextNode(doc, "result/Update/OperationType"))); //$NON-NLS-1$
        model.setOperationTime(timeInMillis);
        model.setOperationDate(sdf.format(new Date(Long.parseLong(timeInMillis))));
        model.setSource(source);
        model.setUserName(checkNull(Util.getFirstTextNode(doc, "result/Update/UserName"))); //$NON-NLS-1$
        model.setIds(Util.joinStrings(new String[] { source, timeInMillis }, ".")); //$NON-NLS-1$

        return model;
    }

    private FKInstance getRetrieveConf(XSElementDecl decl, String path) throws Exception {
        FKInstance fkInstance = new FKInstance();
        XSType type = decl.getType();

        if (type instanceof XSComplexType) {
            XSComplexType cmpxType = (XSComplexType) type;
            XSContentType conType = cmpxType.getContentType();
            XSParticle[] children = conType.asParticle().getTerm().asModelGroup().getChildren();

            for (XSParticle child : children) {
                XSTerm term = child.getTerm();

                if (term instanceof XSElementDecl && ((XSElementDecl) term).getName().equals(path)) {
                    XSElementDecl childElem = (XSElementDecl) child.getTerm();
                    XSAnnotation xsa = childElem.getAnnotation();
                    if (xsa == null)
                        continue;
                    Element el = (Element) xsa.getAnnotation();
                    NodeList annotList = el.getChildNodes();

                    for (int k = 0; k < annotList.getLength(); k++) {
                        if ("appinfo".equals(annotList.item(k).getLocalName())) { //$NON-NLS-1$
                            Node source1 = annotList.item(k).getAttributes().getNamedItem("source"); //$NON-NLS-1$

                            if (source1 == null)
                                continue;
                            String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue(); //$NON-NLS-1$

                            if ("X_ForeignKeyInfo".equals(appinfoSource)) { //$NON-NLS-1$
                                fkInstance.setFkInfo(annotList.item(k).getFirstChild().getNodeValue());
                            }

                            if ("X_Retrieve_FKinfos".equals(appinfoSource)) { //$NON-NLS-1$
                                fkInstance.setRetireveFKInfo("true".equals(annotList.item(k).getFirstChild().getNodeValue())); //$NON-NLS-1$
                                break;
                            }
                        }
                    }
                }
            }
        }
        return fkInstance;
    }

    private String checkNull(String str) {
        if (str == null)
            return ""; //$NON-NLS-1$
        if (str.equalsIgnoreCase("null")) //$NON-NLS-1$
            return ""; //$NON-NLS-1$
        return str;
    }
}