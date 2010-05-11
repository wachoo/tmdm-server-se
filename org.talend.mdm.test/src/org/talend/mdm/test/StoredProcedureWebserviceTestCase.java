package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSDeleteStoredProcedure;
import urn_com_amalto_xtentis_webservice.WSExecuteStoredProcedure;
import urn_com_amalto_xtentis_webservice.WSExistsStoredProcedure;
import urn_com_amalto_xtentis_webservice.WSGetStoredProcedure;
import urn_com_amalto_xtentis_webservice.WSPutStoredProcedure;
import urn_com_amalto_xtentis_webservice.WSRegexStoredProcedure;
import urn_com_amalto_xtentis_webservice.WSStoredProcedure;
import urn_com_amalto_xtentis_webservice.WSStoredProcedurePK;

public class StoredProcedureWebserviceTestCase extends WebserviceTestCase {
	/***************************************************************************
	 * StoredProcedureWebserviceTestCase
	 * **************************************************************************/

	public void testExecuteStoredProcedure() {
		WSExecuteStoredProcedure wsExecuteStoredProcedure = new WSExecuteStoredProcedure();
		wsExecuteStoredProcedure
				.setWsDataClusterPK(new WSDataClusterPK("Order"));
		WSStoredProcedurePK wsStoredProcedurePK = new WSStoredProcedurePK();
		wsStoredProcedurePK.setPk("test");
		wsExecuteStoredProcedure.setWsStoredProcedurePK(wsStoredProcedurePK);
		try {
			String[] wsStringArray = defaultPort
					.executeStoredProcedure(wsExecuteStoredProcedure);
			
			assertNotNull(wsStringArray);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testGetStoredProcedure() {
		WSGetStoredProcedure wsGetStoredProcedure = new WSGetStoredProcedure();
		WSStoredProcedurePK wsStoredProcedurePK = new WSStoredProcedurePK();
		wsStoredProcedurePK.setPk("test");
		wsGetStoredProcedure.setWsStoredProcedurePK(wsStoredProcedurePK);
		try {
			WSStoredProcedure wsStoredProcedure = defaultPort
					.getStoredProcedure(wsGetStoredProcedure);
			assertNotNull(wsStoredProcedure);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsStoredProcedure() {
		WSExistsStoredProcedure wsExistsStoredProcedure = new WSExistsStoredProcedure();
		WSStoredProcedurePK wsStoredProcedurePK = new WSStoredProcedurePK();
		wsStoredProcedurePK.setPk("test");
		wsExistsStoredProcedure.setWsStoredProcedurePK(wsStoredProcedurePK);
		try {
			WSBoolean flag = defaultPort
					.existsStoredProcedure(wsExistsStoredProcedure);
			assertTrue(flag.is_true());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetStoredProcedurePKs() {
		WSRegexStoredProcedure regex = new WSRegexStoredProcedure("*");
		try {
			WSStoredProcedurePK[] wsStoredProcedurePKArray = defaultPort
					.getStoredProcedurePKs(regex);
			
			assertNotNull(wsStoredProcedurePKArray);
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testPutStoredProcedure() {
		WSPutStoredProcedure wsPutStoredProcedure = new WSPutStoredProcedure();
		WSStoredProcedure wsStoredProcedure = new WSStoredProcedure();
		wsStoredProcedure.setName("test");
		wsStoredProcedure
				.setProcedure("update replace //Country[./isoCode = 'CN']/label with <label>CHINA</label>");
		wsPutStoredProcedure.setWsStoredProcedure(wsStoredProcedure);
		try {
			WSStoredProcedurePK wsStoredProcedurePK = defaultPort
					.putStoredProcedure(wsPutStoredProcedure);
			assertNotNull(wsStoredProcedurePK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testDeleteStoredProcedure() {
		WSDeleteStoredProcedure wsStoredProcedureDelete = new WSDeleteStoredProcedure();
		WSStoredProcedurePK wsStoredProcedurePK = new WSStoredProcedurePK();
		wsStoredProcedurePK.setPk("test");
		wsStoredProcedureDelete.setWsStoredProcedurePK(wsStoredProcedurePK);
		try {
			WSStoredProcedurePK wsStoredProcedurePKReturn = defaultPort
					.deleteStoredProcedure(wsStoredProcedureDelete);
			assertNotNull(wsStoredProcedurePKReturn);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
