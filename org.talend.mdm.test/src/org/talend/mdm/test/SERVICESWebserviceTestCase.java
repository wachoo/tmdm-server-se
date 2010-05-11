package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSCheckServiceConfigRequest;
import urn_com_amalto_xtentis_webservice.WSCheckServiceConfigResponse;
import urn_com_amalto_xtentis_webservice.WSGetServicesList;
import urn_com_amalto_xtentis_webservice.WSServiceAction;
import urn_com_amalto_xtentis_webservice.WSServiceActionCode;
import urn_com_amalto_xtentis_webservice.WSServiceGetConfiguration;
import urn_com_amalto_xtentis_webservice.WSServiceGetDocument;
import urn_com_amalto_xtentis_webservice.WSServicePutConfiguration;
import urn_com_amalto_xtentis_webservice.WSServicesListItem;
import urn_com_amalto_xtentis_webservice.WSString;

public class SERVICESWebserviceTestCase extends WebserviceTestCase {
	/***************************************************************************
	 * SERVICES
	 * **************************************************************************/

	public void testGetServiceDocument() {
		WSString serviceName = new WSString();
		serviceName.setValue("amalto/local/service/smtp");
		try {
			WSServiceGetDocument wsServiceGetDocument = defaultPort
					.getServiceDocument(serviceName);
			assertNotNull(wsServiceGetDocument);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetServiceConfiguration() {
		WSServiceGetConfiguration wsGetConfiguration = new WSServiceGetConfiguration();
		wsGetConfiguration.setJndiName("amalto/local/service/smtp");
		wsGetConfiguration
				.setOptionalParameter("<?xml version='1.0' encoding='UTF-8'?><xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'><xsl:output method='xml'  indent='yes' omit-xml-declaration='yes'/><xsl:template match='/' priority='1'><updateaction><operation>INSERT</operation><part><content><xsl:value-of select='.'/></content></part></updateaction></xsl:template></xsl:stylesheet>");
		try {
			WSString wsString = defaultPort
					.getServiceConfiguration(wsGetConfiguration);
			assertNotNull(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testCheckServiceConfiguration() {
		WSCheckServiceConfigRequest serviceName = new WSCheckServiceConfigRequest();
		serviceName.setJndiName("amalto/local/service/smtp");
		serviceName
				.setConf("<configuration><host>localhost</host><port>25</port><username/><password/><permanentbcc/><transformer/><logfilename/></configuration>");
		try {
			WSCheckServiceConfigResponse wsCheckServiceConfigResponse = defaultPort
					.checkServiceConfiguration(serviceName);
			boolean flag = wsCheckServiceConfigResponse.getCheckResult();
			assertTrue(flag);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutServiceConfiguration() {
		WSServicePutConfiguration wsPutConfiguration = new WSServicePutConfiguration();
		wsPutConfiguration.setJndiName("amalto/local/service/smtp");
		wsPutConfiguration
				.setConfiguration("<configuration><host>localhost</host><port>25</port><username/><password/><permanentbcc/><transformer/><logfilename/></configuration>");
		try {
			WSString wsString = defaultPort
					.putServiceConfiguration(wsPutConfiguration);
			assertNotNull(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testServiceAction() {
		WSServiceAction wsServiceAction = new WSServiceAction();
		wsServiceAction.setJndiName("amalto/local/service/smtp");
		wsServiceAction.setMethodName("start");
		WSServiceActionCode wsServiceActionCode = WSServiceActionCode.START;
		wsServiceAction.setWsAction(wsServiceActionCode);
		try {
			WSString wsString = defaultPort.serviceAction(wsServiceAction);
			assertNotNull(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetServicesList() {
		WSGetServicesList wsGetServicesList = new WSGetServicesList();
		wsGetServicesList.setLanguage("en");
		try {
			WSServicesListItem[] wsServicesListItemArray = defaultPort
					.getServicesList(wsGetServicesList);
			
			assertNotNull(wsServicesListItemArray);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}
