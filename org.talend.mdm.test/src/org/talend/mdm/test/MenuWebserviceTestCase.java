package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSDeleteMenu;
import urn_com_amalto_xtentis_webservice.WSExistsMenu;
import urn_com_amalto_xtentis_webservice.WSGetMenu;
import urn_com_amalto_xtentis_webservice.WSGetMenuPKs;
import urn_com_amalto_xtentis_webservice.WSMenu;
import urn_com_amalto_xtentis_webservice.WSMenuPK;
import urn_com_amalto_xtentis_webservice.WSPutMenu;

public class MenuWebserviceTestCase extends WebserviceTestCase {

	public void testGetMenu() {
		WSGetMenu wsGetMenu = new WSGetMenu();
		wsGetMenu.setWsMenuPK(new WSMenuPK("Browse items"));
		try {
			WSMenu wsMenu = defaultPort.getMenu(wsGetMenu);
			assertNotNull(wsMenu);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testExistsMenu() {
		WSExistsMenu wsExistsMenu = new WSExistsMenu();
		wsExistsMenu.setWsMenuPK(new WSMenuPK("Browse items"));
		try {
			WSBoolean wsBoolean = defaultPort.existsMenu(wsExistsMenu);
			assertNotNull(wsBoolean);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetMenuPKs() {
		WSGetMenuPKs regex = new WSGetMenuPKs("*");
		try {
			WSMenuPK[] wsMenuPKArray = defaultPort.getMenuPKs(regex);
			assertNotNull(wsMenuPKArray);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutMenu() {
		try {
			WSPutMenu wsPutMenu = new WSPutMenu();
			WSGetMenu wsGetMenu = new WSGetMenu();
			wsGetMenu.setWsMenuPK(new WSMenuPK("Browse items"));
			WSMenu wsMenu = defaultPort.getMenu(wsGetMenu);
			wsMenu.setName("Browse items test");
			wsPutMenu.setWsMenu(wsMenu);
			WSMenuPK wsMenuPk = defaultPort.putMenu(wsPutMenu);
			assertNotNull(wsMenuPk);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testDeleteMenu() {
		WSDeleteMenu wsMenuDelete = new WSDeleteMenu();
		wsMenuDelete.setWsMenuPK(new WSMenuPK("Browse items test"));
		try {
			WSMenuPK wsMenuPK = defaultPort.deleteMenu(wsMenuDelete);
			assertNotNull(wsMenuPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

}
