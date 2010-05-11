package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.BackgroundJobStatusType;
import urn_com_amalto_xtentis_webservice.WSBackgroundJob;
import urn_com_amalto_xtentis_webservice.WSBackgroundJobPK;
import urn_com_amalto_xtentis_webservice.WSFindBackgroundJobPKs;
import urn_com_amalto_xtentis_webservice.WSGetBackgroundJob;
import urn_com_amalto_xtentis_webservice.WSPutBackgroundJob;

public class BackgroundJobWebserviceTestCase extends WebserviceTestCase {

	public void testFindBackgroundJobPKs() {
		WSFindBackgroundJobPKs wsFindBackgroundJobPKs = new WSFindBackgroundJobPKs();
		wsFindBackgroundJobPKs.setStatus(BackgroundJobStatusType.RUNNING);
		try {
			WSBackgroundJobPK[] findBackgroundJobPKs = defaultPort
					.findBackgroundJobPKs(wsFindBackgroundJobPKs);
			assertNotNull(findBackgroundJobPKs);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetBackgroundJob() {
		try {
			WSFindBackgroundJobPKs wsFindBackgroundJobPKs = new WSFindBackgroundJobPKs();
			wsFindBackgroundJobPKs.setStatus(BackgroundJobStatusType.RUNNING);
			WSBackgroundJobPK[] findBackgroundJobPKs = defaultPort
					.findBackgroundJobPKs(wsFindBackgroundJobPKs);
			WSGetBackgroundJob wsBackgroundJobGet = new WSGetBackgroundJob();
			wsBackgroundJobGet.setPk(findBackgroundJobPKs[0].getPk());
			WSBackgroundJob wsBackgroundJob = defaultPort
					.getBackgroundJob(wsBackgroundJobGet);
			assertNotNull(wsBackgroundJob);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutBackgroundJob() {
		try {
			WSFindBackgroundJobPKs wsFindBackgroundJobPKs = new WSFindBackgroundJobPKs();
			wsFindBackgroundJobPKs.setStatus(BackgroundJobStatusType.RUNNING);
			WSBackgroundJobPK[] findBackgroundJobPKs = defaultPort
					.findBackgroundJobPKs(wsFindBackgroundJobPKs);
			WSGetBackgroundJob wsBackgroundJobGet = new WSGetBackgroundJob();
			wsBackgroundJobGet.setPk(findBackgroundJobPKs[0].getPk());
			WSBackgroundJob wsBackgroundJob = defaultPort
					.getBackgroundJob(wsBackgroundJobGet);
			WSPutBackgroundJob wsPutBackgroundJob = new WSPutBackgroundJob();
			wsPutBackgroundJob.setWsBackgroundJob(wsBackgroundJob);
			WSBackgroundJobPK wsBackgroundJobPK = defaultPort
					.putBackgroundJob(wsPutBackgroundJob);
			assertNotNull(wsBackgroundJobPK);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}
