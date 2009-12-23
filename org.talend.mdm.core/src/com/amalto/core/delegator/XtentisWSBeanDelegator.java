package com.amalto.core.delegator;

import java.rmi.RemoteException;

import com.amalto.core.webservice.WSGetUniversePKs;
import com.amalto.core.webservice.WSPing;
import com.amalto.core.webservice.WSString;
import com.amalto.core.webservice.WSUniversePKArray;

public interface XtentisWSBeanDelegator extends BeanDelegator{
	
	public WSString ping(WSPing wsPing) throws RemoteException;
	
	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex) throws RemoteException;
	
}
