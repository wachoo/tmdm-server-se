package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSConceptRevisionMapMapEntry;
import urn_com_amalto_xtentis_webservice.WSDataCluster;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDeleteDataCluster;
import urn_com_amalto_xtentis_webservice.WSExistsDBDataCluster;
import urn_com_amalto_xtentis_webservice.WSExistsDataCluster;
import urn_com_amalto_xtentis_webservice.WSGetConceptsInDataCluster;
import urn_com_amalto_xtentis_webservice.WSGetConceptsInDataClusterWithRevisions;
import urn_com_amalto_xtentis_webservice.WSGetDataCluster;
import urn_com_amalto_xtentis_webservice.WSPutDBDataCluster;
import urn_com_amalto_xtentis_webservice.WSPutDataCluster;
import urn_com_amalto_xtentis_webservice.WSRegexDataClusterPKs;

public class DataClusterWebserviceTestCase extends WebserviceTestCase {
	/***************************************************************************
	 * DataCluster
	 * **************************************************************************/

	public void testGetDataCluster() {
		WSGetDataCluster wsGetDataCluster = new WSGetDataCluster();
		wsGetDataCluster.setWsDataClusterPK(new WSDataClusterPK("Order"));
		try {
			WSDataCluster wsDataCluster = defaultPort
					.getDataCluster(wsGetDataCluster);
			assertNotNull(wsDataCluster);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testexistsDataCluster() {
		WSExistsDataCluster wsExistsDataCluster = new WSExistsDataCluster();
		wsExistsDataCluster.setWsDataClusterPK(new WSDataClusterPK("Order"));
		try {
			WSBoolean flag = defaultPort.existsDataCluster(wsExistsDataCluster);
			System.out.println(flag);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testExistsDBDataCluster() {
		WSExistsDBDataCluster wsExistsDataCluster = new WSExistsDBDataCluster();
		wsExistsDataCluster.setName("Order");
		try {
			WSBoolean flag = defaultPort
					.existsDBDataCluster(wsExistsDataCluster);
			System.out.println(flag);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetDataClusterPKs() {
		WSRegexDataClusterPKs regexp = new WSRegexDataClusterPKs();
		regexp.setRegex("*");
		try {
			WSDataClusterPK[] wsDataClusterPKs = defaultPort
					.getDataClusterPKs(regexp);
			for (int i = 0; i < wsDataClusterPKs.length; i++) {
				System.out.println(wsDataClusterPKs[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutDataCluster() {
		WSPutDataCluster wsPutDataCluster = new WSPutDataCluster();
		WSDataCluster wsDataCluster = new WSDataCluster();
		wsDataCluster.setName("Order_2");
		wsPutDataCluster.setWsDataCluster(wsDataCluster);
		try {
			WSDataClusterPK wsDataClusterPK = defaultPort
					.putDataCluster(wsPutDataCluster);
			System.out.println(wsDataClusterPK.getPk());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testDeleteDataCluster() {
		WSDeleteDataCluster wsDeleteDataCluster = new WSDeleteDataCluster();
		wsDeleteDataCluster.setWsDataClusterPK(new WSDataClusterPK("Order_2"));
		try {
			WSDataClusterPK wsDataClusterPK = defaultPort
					.deleteDataCluster(wsDeleteDataCluster);
			System.out.println(wsDataClusterPK.getPk());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testPutDBDataCluster() {
		WSPutDBDataCluster wsDataCluster = new WSPutDBDataCluster();
		wsDataCluster.setName("Order");
		try {
			WSBoolean flag = defaultPort.putDBDataCluster(wsDataCluster);
			System.out.println(flag);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetConceptsInDataCluster() {
		WSGetConceptsInDataCluster wsGetConceptsInDataCluster = new WSGetConceptsInDataCluster();
		wsGetConceptsInDataCluster.setWsDataClusterPK(new WSDataClusterPK(
				"Order"));
		try {
			String[] concepts = defaultPort
					.getConceptsInDataCluster(wsGetConceptsInDataCluster);
			for (int i = 0; i < concepts.length; i++) {
				System.out.println(concepts[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetConceptsInDataClusterWithRevisions() {
		WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions = new WSGetConceptsInDataClusterWithRevisions();
		wsGetConceptsInDataClusterWithRevisions
				.setDataClusterPOJOPK(new WSDataClusterPK("Order"));
		try {
			WSConceptRevisionMapMapEntry[] wsConceptRevisionMapMapEntrys = defaultPort
					.getConceptsInDataClusterWithRevisions(wsGetConceptsInDataClusterWithRevisions);
			for (int i = 0; i < wsConceptRevisionMapMapEntrys.length; i++) {
				System.out.println(wsConceptRevisionMapMapEntrys[i]
						.getConcept());
			}
		} catch (RemoteException e) {

			e.printStackTrace();
		}

	}

}
