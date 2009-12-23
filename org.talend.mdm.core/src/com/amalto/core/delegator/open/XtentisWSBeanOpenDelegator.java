package com.amalto.core.delegator.open;

import java.rmi.RemoteException;

import com.amalto.core.delegator.XtentisWSBeanDefaultDelegator;
import com.amalto.core.webservice.WSGetUniversePKs;
import com.amalto.core.webservice.WSUniversePKArray;

public class XtentisWSBeanOpenDelegator extends XtentisWSBeanDefaultDelegator{

	@Override
	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex)
			throws RemoteException {
		throw new RemoteException("Not Supported");
	}

}
