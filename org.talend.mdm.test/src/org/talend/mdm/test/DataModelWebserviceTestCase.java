package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSBusinessConcept;
import urn_com_amalto_xtentis_webservice.WSCheckSchema;
import urn_com_amalto_xtentis_webservice.WSConceptKey;
import urn_com_amalto_xtentis_webservice.WSDataModel;
import urn_com_amalto_xtentis_webservice.WSDataModelPK;
import urn_com_amalto_xtentis_webservice.WSDeleteBusinessConcept;
import urn_com_amalto_xtentis_webservice.WSDeleteDataModel;
import urn_com_amalto_xtentis_webservice.WSExistsDataModel;
import urn_com_amalto_xtentis_webservice.WSGetBusinessConceptKey;
import urn_com_amalto_xtentis_webservice.WSGetBusinessConcepts;
import urn_com_amalto_xtentis_webservice.WSGetDataModel;
import urn_com_amalto_xtentis_webservice.WSI18NString;
import urn_com_amalto_xtentis_webservice.WSKey;
import urn_com_amalto_xtentis_webservice.WSLanguage;
import urn_com_amalto_xtentis_webservice.WSPutBusinessConcept;
import urn_com_amalto_xtentis_webservice.WSPutBusinessConceptSchema;
import urn_com_amalto_xtentis_webservice.WSPutDataModel;
import urn_com_amalto_xtentis_webservice.WSRegexDataModelPKs;
import urn_com_amalto_xtentis_webservice.WSString;

public class DataModelWebserviceTestCase extends WebserviceTestCase {

	public void testGetDataModel() {
		WSGetDataModel wsDataModelget = new WSGetDataModel();
		wsDataModelget.setWsDataModelPK(new WSDataModelPK("Order"));
		try {
			WSDataModel wsdataModel = defaultPort.getDataModel(wsDataModelget);
			assertNotNull(wsdataModel);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsDataModel() {
		WSExistsDataModel wsExistsDataModel = new WSExistsDataModel();
		WSDataModelPK wsDataModelPK = new WSDataModelPK("Order");
		wsExistsDataModel.setWsDataModelPK(wsDataModelPK);
		try {
			WSBoolean flag = defaultPort.existsDataModel(wsExistsDataModel);
			assertTrue(flag.is_true());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetDataModelPKs() {
		WSRegexDataModelPKs regexp = new WSRegexDataModelPKs();
		regexp.setRegex("*");
		try {
			WSDataModelPK[] wsDataModelPKArray = defaultPort
					.getDataModelPKs(regexp);
			assertNotNull(wsDataModelPKArray);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testPutDataModel() {
		WSPutDataModel wsPutDataModel = new WSPutDataModel();
		WSDataModel wsDataModel = new WSDataModel();
		wsDataModel.setName("Order_2");
		wsDataModel.setXsdSchema("");
		wsPutDataModel.setWsDataModel(wsDataModel);
		try {
			WSDataModelPK wsDataModelPK = defaultPort
					.putDataModel(wsPutDataModel);
			assertNotNull(wsDataModelPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testDeleteDataModel() {
		WSDeleteDataModel wsDeleteDataModel = new WSDeleteDataModel();
		wsDeleteDataModel.setWsDataModelPK(new WSDataModelPK("Order_2"));
		try {
			WSDataModelPK wsDataModelPK = defaultPort
					.deleteDataModel(wsDeleteDataModel);
			assertNotNull(wsDataModelPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testCheckSchema() {
		WSCheckSchema wsSchema = new WSCheckSchema();
		wsSchema.setSchema(SCHEMA);
		try {
			WSString wsString = defaultPort.checkSchema(wsSchema);
			assertNotNull(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutBusinessConcept() {
		WSPutBusinessConcept wsPutBusinessConcept = new WSPutBusinessConcept();
		WSBusinessConcept wsBusinessConcept = new WSBusinessConcept();
		wsBusinessConcept.setName("Country_testPutBusinessConcept");
		wsBusinessConcept.setBusinessTemplate("Country_testPutBusinessConcept");
		WSI18NString[] wsLabelArray = new WSI18NString[1];
		WSI18NString wsI18NString = new WSI18NString();
		wsI18NString.setLanguage(WSLanguage.EN);
		wsI18NString.setLabel("Country_testPutBusinessConcept");
		wsLabelArray[0] = wsI18NString;
		wsBusinessConcept.setWsLabel(wsLabelArray);
		wsBusinessConcept.setWsDescription(0, wsI18NString);
		WSKey wsKey = new WSKey();
		wsKey.setSelectorpath(".");
		wsKey.setFieldpath(new String[3]);
		wsKey.setFieldpath(0, "isoCode");
		wsKey.setFieldpath(1, "label");
		wsKey.setFieldpath(2, "Continent");
		wsBusinessConcept.setWsUniqueKey(wsKey);
		wsPutBusinessConcept.setBusinessConcept(wsBusinessConcept);
		wsPutBusinessConcept.setWsDataModelPK(new WSDataModelPK("Order"));
		try {
			WSString wsString = defaultPort
					.putBusinessConcept(wsPutBusinessConcept);
			assertNotNull(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testPutBusinessConceptSchema() {
		try {
			String xml=//"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" blockDefault=\"\" elementFormDefault=\"unqualified\" finalDefault=\"\">"+
						"  <xsd:element  name=\"Country_testPutBusinessConceptSchema\" nillable=\"false\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">"+
							    "<xsd:annotation>"+ 
							      "<xsd:appinfo source=\"X_Write\">Default_User</xsd:appinfo>  "+
							      "<xsd:appinfo source=\"X_Write\">Default_Admin</xsd:appinfo> "+ 
							      "<xsd:appinfo source=\"X_Label_EN\">Country</xsd:appinfo> "+
							    "</xsd:annotation>  "+
							    "<xsd:complexType mixed=\"false\"> "+
							      "<xsd:all maxOccurs=\"1\" minOccurs=\"1\"> "+
							        "<xsd:element maxOccurs=\"1\" minOccurs=\"1\" name=\"isoCode\" nillable=\"false\" type=\"xsd:string\"> "+
							          "<xsd:annotation> "+
							            "<xsd:appinfo source=\"X_Write\">Default_User</xsd:appinfo>  "+
							            "<xsd:appinfo source=\"X_Write\">Default_Admin</xsd:appinfo>  "+
							            "<xsd:appinfo source=\"X_Label_EN\">isoCode</xsd:appinfo> "+
							          "</xsd:annotation>"+ 
							        "</xsd:element>  "+
							        "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"label\" nillable=\"false\" type=\"xsd:string\"> "+
							          "<xsd:annotation> "+
							            "<xsd:appinfo source=\"X_Write\">Default_User</xsd:appinfo>  "+
							            "<xsd:appinfo source=\"X_Write\">Default_Admin</xsd:appinfo> "+ 
							            "<xsd:appinfo source=\"X_Label_EN\">label</xsd:appinfo> "+
							            "</xsd:annotation> "+
							        "</xsd:element>"+  
							        "<xsd:element maxOccurs=\"1\" minOccurs=\"0\" name=\"Continent\" nillable=\"false\" type=\"xsd:string\"> "+
							          "<xsd:annotation> "+
							            "<xsd:appinfo source=\"X_Write\">Default_User</xsd:appinfo>  "+
							            "<xsd:appinfo source=\"X_Write\">Default_Admin</xsd:appinfo>  "+
							            "<xsd:appinfo source=\"X_Label_EN\">Continent</xsd:appinfo> "+
							          "</xsd:annotation>"+ 
							        "</xsd:element> "+
							      "</xsd:all> "+
							    "</xsd:complexType>  "+
							    "<xsd:unique name=\"Country\"> "+
							      "<xsd:selector xpath=\".\"/>  "+
							      "<xsd:field xpath=\"isoCode\"/> "+
							    "</xsd:unique> "+
							  "</xsd:element> ";//+
							  //"</xsd:schema>";

			WSPutBusinessConceptSchema wsPutBusinessConceptSchema = new WSPutBusinessConceptSchema();
			wsPutBusinessConceptSchema.setBusinessConceptSchema(xml);
			wsPutBusinessConceptSchema.setWsDataModelPK(new WSDataModelPK(
					"Order"));

			WSString wsString = defaultPort
					.putBusinessConceptSchema(wsPutBusinessConceptSchema);
			assertNotNull(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testDeleteBusinessConcept() {
		WSDeleteBusinessConcept wsDeleteBusinessConcept = new WSDeleteBusinessConcept();
		wsDeleteBusinessConcept.setBusinessConceptName("Student");
		wsDeleteBusinessConcept.setWsDataModelPK(new WSDataModelPK("Order"));
		try {
			WSString wsString = defaultPort
					.deleteBusinessConcept(wsDeleteBusinessConcept);
			assertNotNull(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testGetBusinessConcepts() {
		WSGetBusinessConcepts wsGetBusinessConcepts = new WSGetBusinessConcepts();
		wsGetBusinessConcepts.setWsDataModelPK(new WSDataModelPK("Order"));
		try {
			String[] stringArray = defaultPort
					.getBusinessConcepts(wsGetBusinessConcepts);
			assertNotNull(stringArray);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetBusinessConceptKey() {
		WSGetBusinessConceptKey wsGetBusinessConceptKey = new WSGetBusinessConceptKey();
		wsGetBusinessConceptKey.setConcept("Country");
		wsGetBusinessConceptKey.setWsDataModelPK(new WSDataModelPK("Order"));
		try {
			WSConceptKey wsConceptKey = defaultPort
					.getBusinessConceptKey(wsGetBusinessConceptKey);
			assertNotNull(wsConceptKey);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
