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
			System.out.println(flag);
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
			for (int i = 0; i < wsDataModelPKArray.length; i++) {
				System.out.println(wsDataModelPKArray[i]);
			}
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
			System.out.println(wsDataModelPK.getPk());
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
			System.out.println(wsDataModelPK.getPk());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testCheckSchema() {
		WSCheckSchema wsSchema = new WSCheckSchema();
		wsSchema.setSchema(SCHEMA);
		try {
			WSString wsString = defaultPort.checkSchema(wsSchema);
			System.out.println(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutBusinessConcept() {
		WSPutBusinessConcept wsPutBusinessConcept = new WSPutBusinessConcept();
		WSBusinessConcept wsBusinessConcept = new WSBusinessConcept();
		wsBusinessConcept.setName("Student2");
		wsBusinessConcept.setBusinessTemplate("Student2Type");
		WSI18NString[] wsLabelArray = new WSI18NString[2];
		WSI18NString wsI18NString = new WSI18NString();
		wsI18NString.setLanguage(WSLanguage.EN);
		wsI18NString.setLabel("Student2");
		wsLabelArray[0] = wsI18NString;
		wsBusinessConcept.setWsLabel(wsLabelArray);
		WSKey wsKey = new WSKey();
		wsKey.setSelectorpath(".");
		wsKey.setFieldpath(new String[3]);
		wsKey.setFieldpath(0, "id");
		wsKey.setFieldpath(0, "name");
		wsKey.setFieldpath(0, "age");
		wsBusinessConcept.setWsUniqueKey(wsKey);
		wsPutBusinessConcept.setBusinessConcept(wsBusinessConcept);
		wsPutBusinessConcept.setWsDataModelPK(new WSDataModelPK("Order"));
		try {
			WSString wsString = defaultPort
					.putBusinessConcept(wsPutBusinessConcept);
			System.out.println(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testPutBusinessConceptSchema() {
		try {
			String s = "<xsd:element name=\"" + "Car\"" + " type="
					+ "\"CarType\"" + ">" + "	<xsd:annotation>";
			s += "<xsd:appinfo source=\"" + "X_Description_EN" + "\">" + "Car"
					+ "</xsd:appinfo>";
			s += "	</xsd:annotation>" + "	<xsd:unique name=\"" + "Car" + "\">"
					+ "		<xsd:selector xpath=\"" + "." + "\"/>";
			s += "<xsd:field xpath=\"" + "id" + "\"/>";
			s += "	</xsd:unique>" + "</xsd:element>";

			WSPutBusinessConceptSchema wsPutBusinessConceptSchema = new WSPutBusinessConceptSchema();
			wsPutBusinessConceptSchema.setBusinessConceptSchema(s);
			wsPutBusinessConceptSchema.setWsDataModelPK(new WSDataModelPK(
					"Order"));
			
			WSString wsString = defaultPort
					.putBusinessConceptSchema(wsPutBusinessConceptSchema);
			System.out.println(wsString);
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
			System.out.println(wsString);
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
			for (int i = 0; i < stringArray.length; i++) {
				System.out.println(stringArray[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetBusinessConceptKey() {
		WSGetBusinessConceptKey wsGetBusinessConceptKey = new WSGetBusinessConceptKey();
		wsGetBusinessConceptKey.setConcept("Student");
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
