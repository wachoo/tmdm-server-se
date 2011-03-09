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
package org.talend.mdm.webapp.itemsbrowser2.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.model.BrowseItem;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormLineBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeCustomerConcept;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeData;
import org.talend.mdm.webapp.itemsbrowser2.server.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XsdUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.ElementProcess;
import org.talend.mdm.webapp.itemsbrowser2.shared.ComplexTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.SimpleTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.core.util.Messages;
import com.amalto.webapp.core.util.MessagesFactory;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSConceptKey;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSDropItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSGetViewPKs;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;
import com.amalto.webapp.util.webservices.WSRouteItemV2;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * DOC HSHU class global comment. Detailled comment
 * 
 * Customize MDM Jboss related methods here
 */
public class ItemServiceCommonHandler extends ItemsServiceImpl {

    private static final Logger LOG = Logger.getLogger(ItemServiceCommonHandler.class);

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.itemsbrowser2.server.messages", ItemsServiceImpl.class.getClassLoader()); //$NON-NLS-1$

    public ArrayList<ItemBean> getFakeCustomerItems() {
        return null;
    }

    /*
     * (non-Jsdoc)
     * 
     * @see org.talend.mdm.webapp.itemsbrowser2.client.ItemsService#getEntityItems(java.lang.String)
     */
    public List<ItemBean> getEntityItems(String entityName) {
        List<ItemBean> items = null;

        // if (entityName.equals("customer"))
        if (Itemsbrowser2.IS_SCRIPT) {
            items = (List<ItemBean>) getItemBeans("DStar", "Browse_items_Agency", "Agency FULLTEXTSEARCH *", 0, 20)[0];
        } else {
            items = FakeData.getFakeCustomerItems();
        }
        //
        // else if (entityName.equals("state"))
        // items = FakeData.getFakeStateItems();

        return items;
    }

    private Object[] getItemBeans(String dataClusterPK, String viewPk, String criteria, int skip, int max) {

        String sortDir = null;
        String sortCol = null;

        int totalSize = 0;

        List<ItemBean> itemBeans = new ArrayList<ItemBean>();
        String concept = getConceptFromBrowseItemView(viewPk);
        try {
            WSWhereItem wi = com.amalto.webapp.core.util.Util.buildWhereItems(criteria);

            String[] results = CommonUtil.getPort().viewSearch(
                    new WSViewSearch(new WSDataClusterPK(dataClusterPK), new WSViewPK(viewPk), wi, -1, skip, max, sortCol,
                            sortDir)).getStrings();
            ViewBean viewBean = getView(viewPk);

            // TODO change ids to array?
            String ids = null;
            for (int i = 0; i < results.length; i++) {

                if (i == 0) {
                    totalSize = Integer.parseInt(com.amalto.webapp.core.util.Util.parse(results[i]).getDocumentElement()
                            .getTextContent());
                    continue;
                }

                // aiming modify when there is null value in fields, the viewable fields sequence is the same as the
                // childlist of result
                if (!results[i].startsWith("<result>")) {
                    results[i] = "<result>" + results[i] + "</result>";
                }

                Document doc = XmlUtil.parseText(results[i]);
                for (String key : viewBean.getKeys()) {
                    ids = XmlUtil.queryNode(doc, key.replaceAll(concept, "result")).getText();
                }

                ItemBean itemBean = new ItemBean(concept, ids, results[i]);
                // ItemBean itemBean = new ItemBean("customer", String.valueOf(i), results[i]);
                dynamicAssemble(itemBean, viewBean);
                itemBeans.add(itemBean);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return new Object[] { itemBeans, totalSize };

    }

    public void dynamicAssemble(ItemBean itemBean, ViewBean viewBean) throws DocumentException {
        if (itemBean.getItemXml() != null) {
            Document docXml = XmlUtil.parseText(itemBean.getItemXml());
            Map<String, TypeModel> types = viewBean.getMetaDataTypes();
            Set<String> xpaths = types.keySet();
            for (String path : xpaths) {
                TypeModel typeModel = types.get(path);
                if (typeModel.isSimpleType()) {
                    String textValue = XmlUtil.getTextValueFromXpath(docXml, path.substring(path.indexOf('/') + 1));
                    if (typeModel.getTypeName().equals(DataTypeConstants.DATE)) {
                        Date date = CommonUtil.parseDate(textValue, "yyyy-MM-dd");
                        itemBean.set(path, date);
                    } else {
                        itemBean.set(path, textValue);
                    }
                }
            }
        }
    }

    /**
     * DOC HSHU Comment method "getView".
     */
    public ViewBean getView(String viewPk) {
        try {
            ViewBean vb = new ViewBean();
            vb.setViewPK(viewPk);
            // TODO mockup

            String[] viewables = null;
            if (Itemsbrowser2.IS_SCRIPT) {
                // test arguments Browse_items_Agency
                viewables = getViewables(viewPk, "en");
            } else {
                viewables = FakeData.getEntityViewables(viewPk);
            }

            // /////
            if (viewables != null) {
                for (String viewable : viewables) {
                    vb.addViewableXpath(viewable);
                }
            }
            vb.setViewables(viewables);

            String xsd;
            // TODO
            String model = getCurrentDataModel();
            String concept = getConceptFromBrowseItemView(viewPk);

            Map<String, TypeModel> metaDataTypes = null;
            if (Itemsbrowser2.IS_SCRIPT) {
                xsd = CommonUtil.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(model))).getXsdSchema();

                XsdUtil.parseXSD(xsd, concept);
                metaDataTypes = XsdUtil.getXpathToType();

            } else {
                xsd = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" blockDefault=\"\" elementFormDefault=\"unqualified\" finalDefault=\"\">  \n  <xsd:import namespace=\"http://www.w3.org/2001/XMLSchema\"/>  \n  <xsd:element abstract=\"false\" name=\"Agent\" nillable=\"false\" type=\"AgentType\"> \n    <xsd:annotation> \n      <xsd:appinfo source=\"X_Label_EN\">D* Agent</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Label_FR\">Agent D*</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Schematron\">&lt;pattern name=\"Check id and dates\" &gt; &lt;rule context=\"Id\"&gt;&lt;assert test=\". = concat(substring(../Firstname,1,3),substring(../Lastname,1,2))\"&gt;&lt;![CDATA[[EN:The Id must follow the following rule: first 3 characters of first name + first 2 characters of last name.][FR:L'Id doit suivre la règle : 3 premiers caractères du prénom + 2 premier caractères du nom.]]]&gt;&lt;/assert&gt;&lt;/rule&gt; &lt;rule context=\".\"&gt;&lt;assert test=\"normalize-space(TermDate)=&amp;apos;&amp;apos; or translate(StartDate,&amp;apos;-&amp;apos;,&amp;apos;&amp;apos;) &amp;lt; translate(TermDate,&amp;apos;-&amp;apos;,&amp;apos;&amp;apos;)\"&gt;&lt;![CDATA[[EN:The start date must be before than the termination date.][FR:La date d'entrée doit être antérieure à la date de sortie.]]]&gt;&lt;/assert&gt;&lt;/rule&gt; &lt;/pattern&gt;</xsd:appinfo>  \n        \n      <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n    </xsd:annotation>  \n    <xsd:unique name=\"Agent\"> \n      <xsd:selector xpath=\".\"/>  \n      <xsd:field xpath=\"Id\"/> \n    </xsd:unique> \n  </xsd:element>  \n  <xsd:complexType abstract=\"false\" mixed=\"false\" name=\"AgentType\"> \n    <xsd:all maxOccurs=\"1\" minOccurs=\"1\"> \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Picture\" nillable=\"false\" type=\"PICTURE\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Picture (optional)</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Photo (optionel)</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Id\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Identifier</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Identifiant</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_EN\">First 3 characters of first name + first 2 characters of last name.</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_FR\">3 premiers caractères du prénom + 2 premier caractères du nom.</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Firstname\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Firstname</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Prénom</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Lastname\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Lastname</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Nom</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"AgencyFK\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Agency</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Agence</xsd:appinfo>  \n          <xsd:appinfo source=\"X_ForeignKey\">Agency/Id</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo>  \n          <xsd:appinfo source=\"X_ForeignKeyInfo\">Agency/Name</xsd:appinfo>  \n          <xsd:appinfo source=\"X_ForeignKeyInfo\">Agency/City</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Retrieve_FKinfos\">true</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"CommissionCode\" nillable=\"false\" type=\"CommissionCodes\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Facet_EN\">Valid codes are 1 to 4.</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Facet_FR\">Codes valides de 1 à 4</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_EN\">Commission Code</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Code Commission</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_EN\">Value between 1 (lowest) and 4 (highest) which determines the agent's commission rate.</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_FR\">Valeur entre 1 (plus basse) et 4 (plus haute) qui détermine la commissionnement de l'agent.</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"StartDate\" nillable=\"false\" type=\"xsd:date\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Start date</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Date d'entrée</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TermDate\" nillable=\"false\" type=\"xsd:date\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Termination Date</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Date de sortie</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Status\" nillable=\"false\" type=\"AgentStatus\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Status</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Statut</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Facet_EN\">Valid values are: pending, approved, terminated</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Facet_FR\">Valeurs possibles: pending, approved, terminated</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element> \n    </xsd:all> \n  </xsd:complexType>  \n  <xsd:simpleType name=\"CommissionCodes\"> \n    <xsd:restriction base=\"xsd:string\"> \n      <xsd:enumeration value=\"1\"/>  \n      <xsd:enumeration value=\"2\"/>  \n      <xsd:enumeration value=\"3\"/>  \n      <xsd:enumeration value=\"4\"/> \n    </xsd:restriction> \n  </xsd:simpleType>  \n  <xsd:element abstract=\"false\" name=\"Agency\" nillable=\"false\" type=\"AgencyType\"> \n    <xsd:annotation> \n        \n      <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Label_EN\">D* Agence</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Label_FR\">Agence D*</xsd:appinfo> \n    </xsd:annotation>  \n    <xsd:unique name=\"Agency\"> \n      <xsd:selector xpath=\".\"/>  \n      <xsd:field xpath=\"Id\"/> \n    </xsd:unique> \n  </xsd:element>  \n  <xsd:complexType abstract=\"false\" mixed=\"false\" name=\"AgencyType\"> \n    <xsd:all maxOccurs=\"1\" minOccurs=\"1\"> \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Id\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Identifier</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Identifiant</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Name\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Name</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Nom</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"City\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">City</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Ville</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"State\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">State</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Etat</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Zip\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Zip code</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Code postal</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Region\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Region</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Région</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"MoreInfo\" nillable=\"false\" type=\"URL\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">More information</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Plus d'info</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element> \n    </xsd:all> \n  </xsd:complexType>  \n  <xsd:simpleType name=\"PICTURE\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"URL\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"AgentStatus\"> \n    <xsd:restriction base=\"xsd:string\"> \n      <xsd:enumeration value=\"approved\"/>  \n      <xsd:enumeration value=\"terminated\"/>  \n      <xsd:enumeration value=\"pending\"/>  \n      <xsd:enumeration value=\"rejected\"/> \n    </xsd:restriction> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"ComStatus\"> \n    <xsd:restriction base=\"xsd:string\"> \n      <xsd:enumeration value=\"pending\"/>  \n      <xsd:enumeration value=\"approved\"/>  \n      <xsd:enumeration value=\"rejected\"/> \n    </xsd:restriction> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"UUID\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"AUTO_INCREMENT\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType> \n</xsd:schema>\n";
                metaDataTypes = new HashMap<String, TypeModel>();
                metaDataTypes.put("Agency/State", new SimpleTypeModel("string", "State", true, false, null));
                metaDataTypes.put("Agency/City", new SimpleTypeModel("string", "City", true, false, null));
                metaDataTypes.put("Agency/Name", new SimpleTypeModel("string", "Name", true, false, null));
                metaDataTypes.put("Agency/MoreInfo", new SimpleTypeModel("URL", "More information", true, false, null));
                metaDataTypes.put("Agency/Id", new SimpleTypeModel("string", "Identifier", true, false, null));
                metaDataTypes.put("Agency/Zip", new SimpleTypeModel("string", "Zip code", true, false, null));
                metaDataTypes.put("Agency/Region", new SimpleTypeModel("string", "Region", true, false, null));
                metaDataTypes.put("Agency", new ComplexTypeModel("AgencyType", "D* Agence"));
            }

            vb.setMetaDataTypes(metaDataTypes);
            vb.setSearchables(getSearchables(viewPk, "en"));

            WSConceptKey key = com.amalto.webapp.core.util.Util.getPort().getBusinessConceptKey(
                    new WSGetBusinessConceptKey(new WSDataModelPK(model), concept));

            String[] keys = key.getFields();
            keys = Arrays.copyOf(keys, keys.length);
            for (int i = 0; i < keys.length; i++) {
                if (".".equals(key.getSelector())) //$NON-NLS-1$
                    //                    keys[i] = "/" + concept + "/" + keys[i]; //$NON-NLS-1$  //$NON-NLS-2$
                    keys[i] = concept + "/" + keys[i]; //$NON-NLS-1$ 
                else
                    keys[i] = key.getSelector() + keys[i];
            }
            vb.setKeys(keys);

            return vb;
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private Map<String, String> getSearchables(String viewPk, String language) {
        try {

            String[] searchables = CommonUtil.getPort().getView(new WSGetView(new WSViewPK(viewPk)))
                    .getSearchableBusinessElements();
            Map<String, TypeModel> labelMapSrc = XsdUtil.getXpathToType();

            Map<String, String> labelSearchables = new LinkedHashMap<String, String>();

            for (int i = 0; i < searchables.length; i++) {
                labelSearchables.put(searchables[i], labelMapSrc.get(searchables[i]).getLabel());
            }

            return labelSearchables;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public String[] getViewables(String viewPK, String language) {
        try {
            WSView wsView = null;
            try {
                wsView = CommonUtil.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            String[] viewables = wsView.getViewableBusinessElements();
            return viewables;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }

    }

    /**
     * DOC HSHU Comment method "setForm".
     */
    public ItemFormBean setForm(ItemBean item, ViewBean view) {

        final ItemFormBean itemFormBean = new ItemFormBean();
        if (item == null) {
            return itemFormBean;
        }
        itemFormBean.setName(item.getConcept() + " " + item.getIds());

        // get item
        // TODO: add data cluster to the criteria
        String itemXml = item.getItemXml();

        // get datamodel
        final FakeCustomerConcept fakeCustomerConcept = new FakeCustomerConcept();

        // go through item
        try {

            final String viewPk = view.getViewPK().substring("Browse_items_".length());
            final Map<String, TypeModel> metaDataType = view.getMetaDataTypes();

            Document itemDoc = XmlUtil.parseText(itemXml);
            XmlUtil.iterate(itemDoc, new ElementProcess() {

                public void process(Element element) {

                    ItemFormLineBean itemFormLineBean = new ItemFormLineBean();

                    String path = element.getUniquePath();

                    // TODO check with complete schema
                    String label = element.getName();
                    String value = element.getText();

                    String fieldType = metaDataType.get(viewPk + "/" + label).getTypeName();
                    itemFormLineBean.setFieldType(fieldType);
                    itemFormLineBean.setFieldLabel(label);
                    itemFormLineBean.setFieldValue(value);

                    // check foreign key
                    List<String> paths = fakeCustomerConcept.getForeignKeyPaths();
                    if (paths.contains(path))
                        itemFormLineBean.setHasForeignKey(true);

                    itemFormBean.addLine(itemFormLineBean);
                }
            });

        } catch (DocumentException e) {
            LOG.error(e);
        }

        return itemFormBean;
    }

    public ItemResult saveItemBean(ItemBean item) {
        try {
            String message = null;
            int status = 0;
            boolean ifNew = item.getIds().equals("") ? true : false;
            String operationType;
            if (ifNew)
                operationType = "CREATE"; //$NON-NLS-1$
            else
                operationType = "UPDATE"; //$NON-NLS-1$ 

            boolean isUpdateThisItem = true;
            if (ifNew)
                isUpdateThisItem = false;
            // if update, check the item is modified by others?
            WSItemPK wsi = null;
            WSPutItemWithReport wsPutItemWithReport = new WSPutItemWithReport(new WSPutItem(new WSDataClusterPK(
                    getCurrentDataCluster()), item.getItemXml(), new WSDataModelPK(getCurrentDataModel()), isUpdateThisItem),
                    "genericUI", true); //$NON-NLS-1$
            wsi = CommonUtil.getPort().putItemWithReport(wsPutItemWithReport);

            if (com.amalto.webapp.core.util.Util.isTransformerExist("beforeSaving_" + item.getConcept())) { //$NON-NLS-1$
                // TODO
            } else {
                message = "The record was saved successfully."; //$NON-NLS-1$
                status = ItemResult.SUCCESS;
            }
            return new ItemResult(status, message);
        } catch (Exception e) {
            ItemResult result;
            // TODO
            if (e.getLocalizedMessage().indexOf("routing failed:") == 0) {
                String saveSUCCE = "Save item '" + item.getConcept() + "."
                        + com.amalto.webapp.core.util.Util.joinStrings(new String[] { item.getIds() }, ".")
                        + "' successfully, But " + e.getLocalizedMessage();
                result = new ItemResult(ItemResult.FAILURE, saveSUCCE);
            } else {
                String err = "Unable to save item '" + item.getConcept() + "."
                        + com.amalto.webapp.core.util.Util.joinStrings(new String[] { item.getIds() }, ".") + "'"
                        + e.getLocalizedMessage();
                if (e.getLocalizedMessage().indexOf("ERROR_3:") == 0) {
                    err = e.getLocalizedMessage();
                }
                result = new ItemResult(ItemResult.FAILURE, err);
            }
            return result;
        }
    }

    public ItemResult deleteItemBean(ItemBean item) {
        try {
            String dataClusterPK = getCurrentDataCluster();
            String concept = item.getConcept();
            String[] ids = new String[] { item.getIds() };
            String outputErrorMessage = com.amalto.core.util.Util.beforeDeleting(dataClusterPK, concept, ids);

            String message = null;
            String errorCode = null;
            if (outputErrorMessage != null) {
                Document doc = XmlUtil.parse(outputErrorMessage);
                // TODO what if multiple error nodes ?
                String xpath = "/descendant::error"; //$NON-NLS-1$
                Node errorNode = doc.selectSingleNode(xpath);
                if (errorNode instanceof Element) {
                    Element errorElement = (Element) errorNode;
                    errorCode = errorElement.attributeValue("code"); //$NON-NLS-1$
                    message = errorElement.getText();
                }
            }

            if (outputErrorMessage == null || "0".equals(errorCode)) { //$NON-NLS-1$               
                WSItemPK wsItem = com.amalto.webapp.core.util.Util.getPort().deleteItem(
                        new WSDeleteItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids)));
                if (wsItem != null)
                    pushUpdateReport(ids, concept, "PHYSICAL_DELETE"); //$NON-NLS-1$ 
                // deleted from the list.
                else
                    message = "ERROR - Unable to delete item";

                if (message == null || message.length() == 0)
                    message = "The record was deleted successfully."; //$NON-NLS-1$                
            } else {
                // Anything but 0 is unsuccessful
                if (message == null || message.length() == 0)
                    message = "An error might have occurred. The record was not deleted."; //$NON-NLS-1$
                message = "ERROR_3" + message; //$NON-NLS-1$
            }

            if (message.indexOf("ERROR") > -1)
                return new ItemResult(ItemResult.FAILURE, message);
            else
                return new ItemResult(ItemResult.SUCCESS, message);

        } catch (Exception e) {
            return new ItemResult(ItemResult.FAILURE, "ERROR -" + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    public ItemResult logicalDeleteItem(ItemBean item, String path) {
        try {
            String dataClusterPK = getCurrentDataCluster();

            String xml = null;
            String concept = item.getConcept();
            String[] ids = new String[] { item.getIds() };
            WSItem item1 = com.amalto.webapp.core.util.Util.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids)));
            xml = item1.getContent();

            WSDroppedItemPK wsItem = com.amalto.webapp.core.util.Util.getPort().dropItem(
                    new WSDropItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids), path));

            if (wsItem != null && xml != null)
                if ("/".equalsIgnoreCase(path)) { //$NON-NLS-1$
                    pushUpdateReport(ids, concept, "LOGIC_DELETE"); //$NON-NLS-1$
                }
                // TODO updatereport

                else
                    return new ItemResult(ItemResult.FAILURE, "ERROR - dropItem is NULL");

            return new ItemResult(ItemResult.SUCCESS, "OK");

        } catch (Exception e) {
            return new ItemResult(ItemResult.FAILURE, "ERROR -" + e.getLocalizedMessage());
        }
    }

    private String pushUpdateReport(String[] ids, String concept, String operationType) throws Exception {
        if (LOG.isTraceEnabled())
            LOG.trace("pushUpdateReport() concept " + concept + " operation " + operationType);

        // TODO check updatedPath
        HashMap<String, UpdateReportItem> updatedPath = null;
        if (!("PHYSICAL_DELETE".equals(operationType) || "LOGIC_DELETE".equals(operationType)) && updatedPath == null) { //$NON-NLS-1$
            return "ERROR_2";
        }

        String xml2 = createUpdateReport(ids, concept, operationType, updatedPath);

        if (LOG.isDebugEnabled())
            LOG.debug("pushUpdateReport() " + xml2);

        // TODO routeAfterSaving is true
        return persistentUpdateReport(xml2, false);
    }

    private String createUpdateReport(String[] ids, String concept, String operationType,
            HashMap<String, UpdateReportItem> updatedPath) throws Exception {

        String revisionId = null;
        String dataModelPK = getCurrentDataModel() == null ? "" : getCurrentDataModel();
        String dataClusterPK = getCurrentDataCluster() == null ? "" : getCurrentDataCluster();

        String username = com.amalto.webapp.core.util.Util.getLoginUserName();
        String universename = com.amalto.webapp.core.util.Util.getLoginUniverse();
        if (universename != null && universename.length() > 0)
            revisionId = com.amalto.webapp.core.util.Util.getRevisionIdFromUniverse(universename, concept);

        StringBuilder keyBuilder = new StringBuilder();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                keyBuilder.append(ids[i]);
                if (i != ids.length - 1)
                    keyBuilder.append(".");
            }
        }
        String key = keyBuilder.length() == 0 ? "null" : keyBuilder.toString(); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        // TODO what is StringEscapeUtils.escapeXml used for
        sb.append("<Update><UserName>").append(username).append("</UserName><Source>genericUI</Source><TimeInMillis>") //$NON-NLS-1$
                .append(System.currentTimeMillis()).append("</TimeInMillis><OperationType>") //$NON-NLS-1$
                .append(operationType).append("</OperationType><RevisionID>").append(revisionId) //$NON-NLS-1$
                .append("</RevisionID><DataCluster>").append(dataClusterPK).append("</DataCluster><DataModel>") //$NON-NLS-1$
                .append(dataModelPK).append("</DataModel><Concept>").append(concept) //$NON-NLS-1$
                .append("</Concept><Key>").append(key).append("</Key>"); //$NON-NLS-1$

        if ("UPDATE".equals(operationType)) { //$NON-NLS-1$
            Collection<UpdateReportItem> list = updatedPath.values();
            boolean isUpdate = false;
            for (Iterator<UpdateReportItem> iter = list.iterator(); iter.hasNext();) {
                UpdateReportItem item = iter.next();
                String oldValue = item.getOldValue() == null ? "" : item.getOldValue();
                String newValue = item.getNewValue() == null ? "" : item.getNewValue();
                if (newValue.equals(oldValue))
                    continue;
                sb.append("<Item>   <path>").append(item.getPath()).append("</path>   <oldValue>")//$NON-NLS-1$
                        .append(oldValue).append("</oldValue>   <newValue>")//$NON-NLS-1$
                        .append(newValue).append("</newValue></Item>");//$NON-NLS-1$
                isUpdate = true;
            }
            if (!isUpdate)
                return null;
        }
        sb.append("</Update>");//$NON-NLS-1$
        return sb.toString();
    }

    private static String persistentUpdateReport(String xml2, boolean routeAfterSaving) throws Exception {
        if (xml2 == null)
            return "OK";

        WSItemPK itemPK = com.amalto.webapp.core.util.Util.getPort().putItem(
                new WSPutItem(new WSDataClusterPK("UpdateReport"), xml2, new WSDataModelPK("UpdateReport"), false)); //$NON-NLS-1$

        if (routeAfterSaving)
            com.amalto.webapp.core.util.Util.getPort().routeItemV2(new WSRouteItemV2(itemPK));

        return "OK";
    }

    public PagingLoadResult<ItemBean> queryItemBean(QueryModel config) {
        PagingLoadConfig pagingLoad = config.getPagingLoadConfig();
        Object[] result = getItemBeans(config.getDataClusterPK(), config.getViewPK(), config.getCriteria().toString(), pagingLoad
                .getOffset(), pagingLoad.getLimit());
        List<ItemBean> itemBeans = (List<ItemBean>) result[0];
        int totalSize = (Integer) result[1];
        return new BasePagingLoadResult<ItemBean>(itemBeans, pagingLoad.getOffset(), totalSize);
    }

    public List<ItemBaseModel> getViewsList(String language) {
        try {
            Map<String, String> viewMap = null;

            if (Itemsbrowser2.IS_SCRIPT) {
                String model = getCurrentDataModel();
                String[] businessConcept = CommonUtil.getPort().getBusinessConcepts(
                        new WSGetBusinessConcepts(new WSDataModelPK(model))).getStrings();
                ArrayList<String> bc = new ArrayList<String>();
                for (int i = 0; i < businessConcept.length; i++) {
                    bc.add(businessConcept[i]);
                }
                WSViewPK[] wsViewsPK;
                wsViewsPK = CommonUtil.getPort().getViewPKs(new WSGetViewPKs("Browse_items.*")).getWsViewPK();

                TreeMap<String, String> views = new TreeMap<String, String>();
                Pattern p = Pattern.compile(".*\\[" + language.toUpperCase() + ":(.*?)\\].*", Pattern.DOTALL);
                for (int i = 0; i < wsViewsPK.length; i++) {
                    WSView wsview = CommonUtil.getPort().getView(new WSGetView(wsViewsPK[i]));
                    String concept = wsview.getName().replaceAll("Browse_items_", "").replaceAll("#.*", "");
                    if (bc.contains(concept)) {
                        String viewDesc = p.matcher(
                                !wsview.getDescription().equals("") ? wsview.getDescription() : wsview.getName())
                                .replaceAll("$1");
                        views.put(wsview.getName(), viewDesc.equals("") ? wsview.getName() : viewDesc);
                    }
                }
                viewMap = getMapSortedByValue(views);
            } else {
                viewMap = new HashMap<String, String>();
                viewMap.put("Browse_items_Agency", "Agency");
                viewMap.put("Browse_items_Agent", "Agent");
            }

            List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
            for (String key : viewMap.keySet()) {
                ItemBaseModel bm = new ItemBaseModel();
                bm.set("name", viewMap.get(key));
                bm.set("value", key);
                list.add(bm);
            }
            return list;
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private String getConceptFromBrowseItemView(String viewPK) {
        String concept = viewPK.replaceAll("Browse_items_", "");
        concept = concept.replaceAll("#.*", "");
        return concept;
    }

    private static LinkedHashMap<String, String> getMapSortedByValue(Map<String, String> map) {
        TreeSet<Map.Entry> set = new TreeSet<Map.Entry>(new Comparator() {

            public int compare(Object obj, Object obj1) {
                return ((Comparable) ((Map.Entry) obj).getValue()).compareTo(((Map.Entry) obj1).getValue());
            }
        });
        set.addAll(map.entrySet());
        LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
        for (Iterator i = set.iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            sortedMap.put((String) entry.getKey(), (String) entry.getValue());
        }

        return sortedMap;
    }

    /*********************************************************************
     * Bookmark management
     *********************************************************************/

    public boolean isExistCriteria(String dataObjectLabel, String id) {
        try {
            WSItemPK wsItemPK = new WSItemPK();
            wsItemPK.setConceptName("BrowseItem");

            WSDataClusterPK wsDataClusterPK = new WSDataClusterPK();
            wsDataClusterPK.setPk(XSystemObjects.DC_SEARCHTEMPLATE.getName());
            wsItemPK.setWsDataClusterPK(wsDataClusterPK);

            String[] ids = new String[1];
            ids[0] = id;
            wsItemPK.setIds(ids);

            WSExistsItem wsExistsItem = new WSExistsItem(wsItemPK);
            WSBoolean wsBoolean = CommonUtil.getPort().existsItem(wsExistsItem);
            return wsBoolean.is_true();
        } catch (XtentisWebappException e) {
            // TODO Auto-generated catch block
            LOG.error(e.getMessage(), e);
            return false;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public String saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString) {
        String returnString = "OK";
        try {
            String owner = com.amalto.webapp.core.util.Util.getLoginUserName();
            BrowseItem searchTemplate = new BrowseItem();
            searchTemplate.setViewPK(viewPK);
            searchTemplate.setCriteriaName(templateName);
            searchTemplate.setShared(isShared);
            searchTemplate.setOwner(owner);
            searchTemplate.setCriteria(criteriaString);

            WSItemPK pk = CommonUtil.getPort().putItem(
                    new WSPutItem(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), searchTemplate
                            .marshal2String(), new WSDataModelPK(XSystemObjects.DM_SEARCHTEMPLATE.getName()), false));

            if (pk != null)
                returnString = "OK";
            else
                returnString = null;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            returnString = e.getMessage();
        } finally {
            return returnString;
        }
    }

    public PagingLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load) {
        List<String> results = Arrays.asList(getSearchTemplateNames(view, isShared, load.getOffset(), load.getLimit()));
        List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
        for (String result : results) {
            ItemBaseModel bm = new ItemBaseModel();
            bm.set("name", result);
            bm.set("value", result);
            list.add(bm);
        }
        int totalSize = Integer.parseInt(countSearchTemplate(view));
        return new BasePagingLoadResult<ItemBaseModel>(list, load.getOffset(), totalSize);
    }

    public List<ItemBaseModel> getviewItemsCriterias(String view) {
        String[] results = getSearchTemplateNames(view, true, 0, 0);
        List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();

        for (String result : results) {
            ItemBaseModel bm = new ItemBaseModel();
            bm.set("name", result);
            bm.set("value", result);
            list.add(bm);
        }
        return list;
    }

    private String[] getSearchTemplateNames(String view, boolean isShared, int start, int limit) {
        try {
            int localStart = 0;
            int localLimit = 0;
            if (start == limit && limit == 0) {
                localStart = 0;
                localLimit = Integer.MAX_VALUE;
            } else {
                localStart = start;
                localLimit = limit;

            }
            WSWhereItem wi = new WSWhereItem();

            WSWhereCondition wc1 = new WSWhereCondition("BrowseItem/ViewPK", WSWhereOperator.EQUALS, view,
                    WSStringPredicate.NONE, false);

            WSWhereCondition wc3 = new WSWhereCondition("BrowseItem/Owner", WSWhereOperator.EQUALS,
                    com.amalto.webapp.core.util.Util.getLoginUserName(), WSStringPredicate.NONE, false);
            WSWhereCondition wc4;
            WSWhereOr or = new WSWhereOr();
            if (isShared) {
                wc4 = new WSWhereCondition("BrowseItem/Shared", WSWhereOperator.EQUALS, "true", WSStringPredicate.OR, false);

                or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null), new WSWhereItem(wc4, null, null) });
            } else {
                or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null) });
            }

            WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { new WSWhereItem(wc1, null, null),

            new WSWhereItem(null, null, or) });

            wi = new WSWhereItem(null, and, null);

            String[] results = CommonUtil.getPort().xPathsSearch(
                    new WSXPathsSearch(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), null,// pivot
                            new WSStringArray(new String[] { "BrowseItem/CriteriaName" }), wi, -1, localStart, localLimit, null, // order
                            // by
                            null // direction
                    )).getStrings();

            for (int i = 0; i < results.length; i++) {
                results[i] = results[i].replaceAll("<CriteriaName>(.*)</CriteriaName>", "$1");
            }
            return results;

        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }

    private String countSearchTemplate(String view) {
        try {
            WSWhereItem wi = new WSWhereItem();

            // Configuration config = Configuration.getInstance();
            WSWhereCondition wc1 = new WSWhereCondition("BrowseItem/ViewPK", WSWhereOperator.EQUALS, view,
                    WSStringPredicate.NONE, false);
            /*
             * WSWhereCondition wc2 = new WSWhereCondition( "hierarchical-report/data-model", WSWhereOperator.EQUALS,
             * config.getModel(), WSStringPredicate.NONE, false);
             */
            WSWhereCondition wc3 = new WSWhereCondition("BrowseItem/Owner", WSWhereOperator.EQUALS,
                    com.amalto.webapp.core.util.Util.getLoginUserName(), WSStringPredicate.NONE, false);

            WSWhereOr or = new WSWhereOr(new WSWhereItem[] { new WSWhereItem(wc3, null, null) });

            WSWhereAnd and = new WSWhereAnd(new WSWhereItem[] { new WSWhereItem(wc1, null, null),
            /* new WSWhereItem(wc2, null, null), */
            new WSWhereItem(null, null, or) });

            wi = new WSWhereItem(null, and, null);
            return CommonUtil.getPort().count(
                    new WSCount(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), "BrowseItem", wi, -1))
                    .getValue();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return "0";
        }
    }

    public String deleteSearchTemplate(String id) {
        try {
            String[] ids = { id };
            String concept = "BrowseItem";
            String dataClusterPK = XSystemObjects.DC_SEARCHTEMPLATE.getName();
            if (ids != null) {
                WSItemPK wsItem = CommonUtil.getPort().deleteItem(
                        new WSDeleteItem(new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids)));

                if (wsItem == null)
                    return "ERROR - deleteTemplate is NULL";
                return "OK";
            } else {
                return "OK";
            }
        } catch (Exception e) {
            return "ERROR -" + e.getLocalizedMessage();
        }
    }

    public String getCriteriaByBookmark(String bookmark) {
        try {
            String criteria = "";
            String result = CommonUtil.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(XSystemObjects.DC_SEARCHTEMPLATE.getName()), "BrowseItem",
                            new String[] { bookmark }))).getContent().trim();
            if (result != null) {
                if (result.indexOf("<SearchCriteria>") != -1)
                    criteria = result.substring(result.indexOf("<SearchCriteria>") + 16, result.indexOf("</SearchCriteria>"));
            }
            return criteria;
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return null;
    }
}
