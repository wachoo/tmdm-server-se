package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse;
import urn_com_amalto_xtentis_webservice.WSUniverse;

public class UniverseWebserviceTestCase extends WebserviceTestCase {

	public void testGetCurrentUniverse() {
		WSGetCurrentUniverse wsGetCurrentUniverse = new WSGetCurrentUniverse();
		try {
			WSUniverse wsUniverse = defaultPort
					.getCurrentUniverse(wsGetCurrentUniverse);
			System.out.println(wsUniverse.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
