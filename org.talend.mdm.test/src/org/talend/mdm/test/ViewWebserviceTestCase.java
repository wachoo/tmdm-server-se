package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSDeleteView;
import urn_com_amalto_xtentis_webservice.WSExistsView;
import urn_com_amalto_xtentis_webservice.WSGetView;
import urn_com_amalto_xtentis_webservice.WSGetViewPKs;
import urn_com_amalto_xtentis_webservice.WSPutView;
import urn_com_amalto_xtentis_webservice.WSView;
import urn_com_amalto_xtentis_webservice.WSViewPK;

public class ViewWebserviceTestCase extends WebserviceTestCase {
	/***************************************************************************
	 * View
	 * **************************************************************************/

	public void testGetView() {
		WSGetView wsViewGet = new WSGetView();
		wsViewGet.setWsViewPK(new WSViewPK("Browse_items_Student"));
		try {
			WSView wsView = defaultPort.getView(wsViewGet);
			assertNotNull(wsView);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsView() {
		WSExistsView wsExistsView = new WSExistsView();
		wsExistsView.setWsViewPK(new WSViewPK("Browse_items_Student"));
		try {
			WSBoolean flag = defaultPort.existsView(wsExistsView);
			System.out.println(flag.toString());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetViewPKs() {
		WSGetViewPKs regexp = new WSGetViewPKs();
		regexp.setRegex("*");
		try {
			WSViewPK[] wsViewPKArray = defaultPort.getViewPKs(regexp);
			for (int i = 0; i < wsViewPKArray.length; i++) {
				System.out.println(wsViewPKArray[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testDeleteView() {
		WSDeleteView wsDeleteView = new WSDeleteView();
		wsDeleteView.setWsViewPK(new WSViewPK("Browse_items_Student"));
		try {
			WSViewPK wsViewPK = defaultPort.deleteView(wsDeleteView);
			System.out.println(wsViewPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutView() {
		WSView wsView;
		try {
			wsView = defaultPort.getView(new WSGetView(new WSViewPK(
					"Browse_items_Student")));
			WSPutView wsPutView = new WSPutView();
			wsPutView.setWsView(wsView);
			WSViewPK wsViewPK = defaultPort.putView(wsPutView);
			System.out.println(wsViewPK.getPk());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
