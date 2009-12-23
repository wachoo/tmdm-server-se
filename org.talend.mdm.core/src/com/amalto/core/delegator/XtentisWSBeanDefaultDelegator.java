package com.amalto.core.delegator;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlUtil;
import com.amalto.core.webservice.WSGetUniversePKs;
import com.amalto.core.webservice.WSPing;
import com.amalto.core.webservice.WSString;
import com.amalto.core.webservice.WSUniversePK;
import com.amalto.core.webservice.WSUniversePKArray;

public abstract class XtentisWSBeanDefaultDelegator implements XtentisWSBeanDelegator{

	@Override
	public WSString ping(WSPing wsPing) throws RemoteException {
		return new WSString(wsPing.getEcho());
	}
	
	
}
