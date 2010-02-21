package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSGetCurrentUniverse;
import urn_com_amalto_xtentis_webservice.WSGetUniversePKs;
import urn_com_amalto_xtentis_webservice.WSUniverse;
import urn_com_amalto_xtentis_webservice.WSUniversePK;

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

	public void testGetUniversePks() {

		try {
			WSUniversePK[] pks = defaultPort
					.getUniversePKs(new WSGetUniversePKs(".*"));
			for (int i = 0; i < pks.length; i++) {
				WSUniversePK pk = pks[i];
				System.out.println(pk.getPk());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}
