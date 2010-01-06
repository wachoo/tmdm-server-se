package org.talend.mdm.workflow.client;

import org.talend.mdm.workflow.client.util.MDMConnectTimeOutException;

import urn_com_amalto_xtentis_webservice.WSPing;

public class TalendMDMChecker extends TalendMDMAdapter{

	/**
	 * @param url
	 * @param universe
	 * 
	 * use default username/password
	 * to avoid no rights exception 
	 */
	public TalendMDMChecker(String url, String universe){
		setUrl(url);
		setUniverse(universe);
		setUsername(TalendMDMAdapter.DEFAULT_USERNAME);
		setPassword(TalendMDMAdapter.DEFAULT_PASSWORD);
	}

	
	public void validate() throws MDMConnectTimeOutException{
		try {
			//init
			initPort();
			//ping
			WSPing wsPing=new WSPing();
			wsPing.setEcho("Hello MDM! ");
			port.ping(wsPing);
			
		} catch (Exception e) {
			throw new MDMConnectTimeOutException(e);
		}
	}

}
