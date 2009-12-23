package com.amalto.core.delegator;

import java.rmi.RemoteException;

import com.amalto.core.webservice.WSPing;
import com.amalto.core.webservice.WSString;

public abstract class XtentisWSBeanDefaultDelegator implements XtentisWSBeanDelegator{

	@Override
	public WSString ping(WSPing wsPing) throws RemoteException {
		return new WSString(wsPing.getEcho());
	}
	
	
}
