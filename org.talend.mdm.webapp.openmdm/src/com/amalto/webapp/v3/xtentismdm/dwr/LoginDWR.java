package com.amalto.webapp.v3.xtentismdm.dwr;

import java.util.ArrayList;
import java.util.List;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSGetUniversePKs;
import com.amalto.webapp.util.webservices.WSUniversePK;
import com.amalto.webapp.util.webservices.WSUniversePKArray;
import com.amalto.webapp.util.webservices.XtentisPort;

public class LoginDWR {
	
	public String helloMDM() {
		
		return "Welcome to use Talend MDM";

	}
	
	public String[] getUniverseNames() throws XtentisWebappException {
		
		List<String> universeNames=new ArrayList<String>();
		universeNames.add("HEAD");
		try {
			//FIXME use RMI port
			XtentisPort port=Util.getPort(null, null, Util._FORCE_RMI_);
			WSUniversePKArray pks=port.getUniversePKs(new WSGetUniversePKs(".*"));
			if(pks!=null){
				WSUniversePK[] wsUniversePKs=pks.getWsUniversePK();
			    if(wsUniversePKs!=null&&wsUniversePKs.length>0){
			    	for (int i = 0; i < wsUniversePKs.length; i++) {
			    		universeNames.add(wsUniversePKs[i].getPk());
					}
			    }
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisWebappException(e);
		}
		return universeNames.toArray(new String[universeNames.size()]);

	}

}
