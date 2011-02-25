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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsService;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.model.BrowseItem;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormLineBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeCustomerConcept;
import org.talend.mdm.webapp.itemsbrowser2.server.mockup.FakeData;
import org.talend.mdm.webapp.itemsbrowser2.server.util.ModelCreator;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.callback.ElementProcess;
import org.talend.mdm.webapp.itemsbrowser2.shared.FieldVerifier;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSGetViewPKs;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
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
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ItemsServiceImpl extends RemoteServiceServlet implements ItemsService {

    private static final Logger LOG = Logger.getLogger(ItemsServiceImpl.class);

    public String greetServer(String input) throws IllegalArgumentException {
        // Verify that the input is valid.
        if (!FieldVerifier.isValidName(input)) {
            // If the input is not valid, throw an IllegalArgumentException back to
            // the client.
            throw new IllegalArgumentException("Name must be at least 4 characters long");
        }

        String serverInfo = getServletContext().getServerInfo();
        String userAgent = getThreadLocalRequest().getHeader("User-Agent");
        return "Hello, " + input + "!<br><br>I am running " + serverInfo + ".<br><br>It looks like you are using:<br>"
                + userAgent;
    }

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

        try {
            WSWhereItem wi = com.amalto.webapp.core.util.Util.buildWhereItems(criteria);

            String[] results = com.amalto.webapp.core.util.Util.getPort().viewSearch(
                    new WSViewSearch(new WSDataClusterPK(dataClusterPK), new WSViewPK(viewPk), wi, -1, skip, max, sortCol,
                            sortDir)).getStrings();
            ViewBean viewBean = getView(viewPk);
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
                ItemBean itemBean = new ItemBean("Agency", String.valueOf(i), results[i]);
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
            List<String> viewables = viewBean.getViewableXpaths();
            for (String viewable : viewables) {
                String value = XmlUtil.getTextValueFromXpath(docXml, viewable);
                itemBean.set(viewable, value);
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

            Map<String, String> metaDataTypes = null;
            if (Itemsbrowser2.IS_SCRIPT) {
                xsd = com.amalto.webapp.core.util.Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(model)))
                        .getXsdSchema();
                XmlUtil.parseXSD(xsd, concept);

                metaDataTypes = XmlUtil.getXpathToType();
            } else {
                xsd = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" blockDefault=\"\" elementFormDefault=\"unqualified\" finalDefault=\"\">  \n  <xsd:import namespace=\"http://www.w3.org/2001/XMLSchema\"/>  \n  <xsd:element abstract=\"false\" name=\"Agent\" nillable=\"false\" type=\"AgentType\"> \n    <xsd:annotation> \n      <xsd:appinfo source=\"X_Label_EN\">D* Agent</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Label_FR\">Agent D*</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Schematron\">&lt;pattern name=\"Check id and dates\" &gt; &lt;rule context=\"Id\"&gt;&lt;assert test=\". = concat(substring(../Firstname,1,3),substring(../Lastname,1,2))\"&gt;&lt;![CDATA[[EN:The Id must follow the following rule: first 3 characters of first name + first 2 characters of last name.][FR:L'Id doit suivre la règle : 3 premiers caractères du prénom + 2 premier caractères du nom.]]]&gt;&lt;/assert&gt;&lt;/rule&gt; &lt;rule context=\".\"&gt;&lt;assert test=\"normalize-space(TermDate)=&amp;apos;&amp;apos; or translate(StartDate,&amp;apos;-&amp;apos;,&amp;apos;&amp;apos;) &amp;lt; translate(TermDate,&amp;apos;-&amp;apos;,&amp;apos;&amp;apos;)\"&gt;&lt;![CDATA[[EN:The start date must be before than the termination date.][FR:La date d'entrée doit être antérieure à la date de sortie.]]]&gt;&lt;/assert&gt;&lt;/rule&gt; &lt;/pattern&gt;</xsd:appinfo>  \n        \n      <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n    </xsd:annotation>  \n    <xsd:unique name=\"Agent\"> \n      <xsd:selector xpath=\".\"/>  \n      <xsd:field xpath=\"Id\"/> \n    </xsd:unique> \n  </xsd:element>  \n  <xsd:complexType abstract=\"false\" mixed=\"false\" name=\"AgentType\"> \n    <xsd:all maxOccurs=\"1\" minOccurs=\"1\"> \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Picture\" nillable=\"false\" type=\"PICTURE\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Picture (optional)</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Photo (optionel)</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Id\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Identifier</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Identifiant</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_EN\">First 3 characters of first name + first 2 characters of last name.</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_FR\">3 premiers caractères du prénom + 2 premier caractères du nom.</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Firstname\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Firstname</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Prénom</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Lastname\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Lastname</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Nom</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"AgencyFK\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Agency</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Agence</xsd:appinfo>  \n          <xsd:appinfo source=\"X_ForeignKey\">Agency/Id</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo>  \n          <xsd:appinfo source=\"X_ForeignKeyInfo\">Agency/Name</xsd:appinfo>  \n          <xsd:appinfo source=\"X_ForeignKeyInfo\">Agency/City</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Retrieve_FKinfos\">true</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"CommissionCode\" nillable=\"false\" type=\"CommissionCodes\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Facet_EN\">Valid codes are 1 to 4.</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Facet_FR\">Codes valides de 1 à 4</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_EN\">Commission Code</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Code Commission</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_EN\">Value between 1 (lowest) and 4 (highest) which determines the agent's commission rate.</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Description_FR\">Valeur entre 1 (plus basse) et 4 (plus haute) qui détermine la commissionnement de l'agent.</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"StartDate\" nillable=\"false\" type=\"xsd:date\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Start date</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Date d'entrée</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"TermDate\" nillable=\"false\" type=\"xsd:date\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Termination Date</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Date de sortie</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Status\" nillable=\"false\" type=\"AgentStatus\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Status</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Statut</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Facet_EN\">Valid values are: pending, approved, terminated</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Facet_FR\">Valeurs possibles: pending, approved, terminated</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element> \n    </xsd:all> \n  </xsd:complexType>  \n  <xsd:simpleType name=\"CommissionCodes\"> \n    <xsd:restriction base=\"xsd:string\"> \n      <xsd:enumeration value=\"1\"/>  \n      <xsd:enumeration value=\"2\"/>  \n      <xsd:enumeration value=\"3\"/>  \n      <xsd:enumeration value=\"4\"/> \n    </xsd:restriction> \n  </xsd:simpleType>  \n  <xsd:element abstract=\"false\" name=\"Agency\" nillable=\"false\" type=\"AgencyType\"> \n    <xsd:annotation> \n        \n      <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Label_EN\">D* Agence</xsd:appinfo>  \n      <xsd:appinfo source=\"X_Label_FR\">Agence D*</xsd:appinfo> \n    </xsd:annotation>  \n    <xsd:unique name=\"Agency\"> \n      <xsd:selector xpath=\".\"/>  \n      <xsd:field xpath=\"Id\"/> \n    </xsd:unique> \n  </xsd:element>  \n  <xsd:complexType abstract=\"false\" mixed=\"false\" name=\"AgencyType\"> \n    <xsd:all maxOccurs=\"1\" minOccurs=\"1\"> \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Id\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Identifier</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Identifiant</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"Name\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Name</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Nom</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"City\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">City</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Ville</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"State\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">State</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Etat</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Zip\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Zip code</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Code postal</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Region\" nillable=\"false\" type=\"xsd:string\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">Region</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Région</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element>  \n      <xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"MoreInfo\" nillable=\"false\" type=\"URL\"> \n        <xsd:annotation> \n          <xsd:appinfo source=\"X_Label_EN\">More information</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Label_FR\">Plus d'info</xsd:appinfo>  \n            \n          <xsd:appinfo source=\"X_Write\">General_Manager</xsd:appinfo>  \n          <xsd:appinfo source=\"X_Write\">Manager_MWest</xsd:appinfo> \n        </xsd:annotation> \n      </xsd:element> \n    </xsd:all> \n  </xsd:complexType>  \n  <xsd:simpleType name=\"PICTURE\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"URL\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"AgentStatus\"> \n    <xsd:restriction base=\"xsd:string\"> \n      <xsd:enumeration value=\"approved\"/>  \n      <xsd:enumeration value=\"terminated\"/>  \n      <xsd:enumeration value=\"pending\"/>  \n      <xsd:enumeration value=\"rejected\"/> \n    </xsd:restriction> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"ComStatus\"> \n    <xsd:restriction base=\"xsd:string\"> \n      <xsd:enumeration value=\"pending\"/>  \n      <xsd:enumeration value=\"approved\"/>  \n      <xsd:enumeration value=\"rejected\"/> \n    </xsd:restriction> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"UUID\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType>  \n  <xsd:simpleType name=\"AUTO_INCREMENT\"> \n    <xsd:restriction base=\"xsd:string\"/> \n  </xsd:simpleType> \n</xsd:schema>\n";
                metaDataTypes = new HashMap<String, String>();
                metaDataTypes.put("Agency/State", "string");
                metaDataTypes.put("Agency/City", "string");
                metaDataTypes.put("Agency/Name", "string");
                metaDataTypes.put("Agency/MoreInfo", "URL");
                metaDataTypes.put("Agency/Id", "string");
                metaDataTypes.put("Agency/Zip", "string");
                metaDataTypes.put("Agency/Region", "string");
                metaDataTypes.put("Agency", "AgencyType");
            }

            vb.setMetaDataTypes(metaDataTypes);
            vb.setSearchables(getSearchables(viewPk, "en"));

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
            String[] searchables = { "Agency/Id", "Agency/Name", "Agency/City", "Agency/State", "Agency" };
            Map<String, String> labelMapSrc = null;
            if (Itemsbrowser2.IS_SCRIPT) {
                searchables = com.amalto.webapp.core.util.Util.getPort().getView(new WSGetView(new WSViewPK(viewPk)))
                        .getSearchableBusinessElements();
                labelMapSrc = XmlUtil.getXpathToLabel();
            } else {
                labelMapSrc = new HashMap<String, String>();
                labelMapSrc.put("Agency/State", "State");
                labelMapSrc.put("Agency/Name", "Name");
                labelMapSrc.put("Agency/MoreInfo", "More information");
                labelMapSrc.put("Agency/City", "City");
                labelMapSrc.put("Agency/Id", "Identifier");
                labelMapSrc.put("Agency/Zip", "Zip code");
                labelMapSrc.put("Agency/Region", "Region");
                labelMapSrc.put("Agency", "D* Agence");
            }
            Map<String, String> labelSearchables = new LinkedHashMap<String, String>();

            for (int i = 0; i < searchables.length; i++) {
                labelSearchables.put(searchables[i], labelMapSrc.get(searchables[i]));
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
                wsView = com.amalto.webapp.core.util.Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
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
            final Map<String, String> metaDataType = view.getMetaDataTypes();

            Document itemDoc = XmlUtil.parseText(itemXml);
            XmlUtil.iterate(itemDoc, new ElementProcess() {

                public void process(Element element) {

                    ItemFormLineBean itemFormLineBean = new ItemFormLineBean();

                    String path = element.getUniquePath();

                    // TODO check with complete schema
                    String label = element.getName();
                    String value = element.getText();

                    String fieldType = metaDataType.get(viewPk + "/" + label);
                    itemFormLineBean.setFieldType(fieldType);
                    itemFormLineBean.setFieldLabel(label);
                    Serializable model = ModelCreator.createModel(fieldType, value);
                    itemFormLineBean.setFieldValue(model);

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

    public PagingLoadResult<ItemBean> queryItemBean(QueryModel config) {
        PagingLoadConfig pagingLoad = config.getPagingLoadConfig();
        Object[] result = getItemBeans(config.getDataClusterPK(), config.getViewPK(), config.getCriteria().toString(), pagingLoad
                .getOffset(), pagingLoad.getLimit());
        List<ItemBean> itemBeans = (List<ItemBean>) result[0];
        int totalSize = (Integer) result[1];
        return new BasePagingLoadResult<ItemBean>(itemBeans, pagingLoad.getOffset(), totalSize);
    }

    public List<BaseModel> getViewsList(String language) {
        try {
            Map<String, String> viewMap = null;

            if (Itemsbrowser2.IS_SCRIPT) {
                String model = getCurrentDataModel();
                String[] businessConcept = com.amalto.webapp.core.util.Util.getPort().getBusinessConcepts(
                        new WSGetBusinessConcepts(new WSDataModelPK(model))).getStrings();
                ArrayList<String> bc = new ArrayList<String>();
                for (int i = 0; i < businessConcept.length; i++) {
                    bc.add(businessConcept[i]);
                }
                WSViewPK[] wsViewsPK;
                wsViewsPK = com.amalto.webapp.core.util.Util.getPort().getViewPKs(new WSGetViewPKs("Browse_items.*"))
                        .getWsViewPK();

                TreeMap<String, String> views = new TreeMap<String, String>();
                Pattern p = Pattern.compile(".*\\[" + language.toUpperCase() + ":(.*?)\\].*", Pattern.DOTALL);
                for (int i = 0; i < wsViewsPK.length; i++) {
                    WSView wsview = com.amalto.webapp.core.util.Util.getPort().getView(new WSGetView(wsViewsPK[i]));
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

            List<BaseModel> list = new ArrayList<BaseModel>();
            for (String key : viewMap.keySet()) {
                BaseModel bm = new BaseModel();
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
            WSBoolean wsBoolean = com.amalto.webapp.core.util.Util.getPort().existsItem(wsExistsItem);
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

            WSItemPK pk = com.amalto.webapp.core.util.Util.getPort().putItem(
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

    public PagingLoadResult<BaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load) {
        List<String> results = Arrays.asList(getSearchTemplateNames(view, isShared, load.getOffset(), load.getLimit()));
        List<BaseModel> list = new ArrayList<BaseModel>();
        for (String result : results) {
            BaseModel bm = new BaseModel();
            bm.set("name", result);
            bm.set("value", result);
            list.add(bm);
        }
        int totalSize = Integer.parseInt(countSearchTemplate(view));
        return new BasePagingLoadResult<BaseModel>(list, load.getOffset(), totalSize);
    }

    public List<BaseModel> getviewItemsCriterias(String view) {
        String[] results = getSearchTemplateNames(view, true, 0, 0);
        List<BaseModel> list = new ArrayList<BaseModel>();

        for (String result : results) {
            BaseModel bm = new BaseModel();
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

            String[] results = com.amalto.webapp.core.util.Util.getPort().xPathsSearch(
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
            return com.amalto.webapp.core.util.Util.getPort().count(
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
                WSItemPK wsItem = com.amalto.webapp.core.util.Util.getPort().deleteItem(
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
            String result = com.amalto.webapp.core.util.Util.getPort().getItem(
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

    public String getCurrentDataModel() throws Exception {
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        Configuration config = Configuration.getInstance(session);
        return config.getModel();
    }

}
