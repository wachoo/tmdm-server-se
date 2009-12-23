package com.amalto.core.delegator.enterprise;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Iterator;

import com.amalto.core.delegator.XtentisWSBeanDefaultDelegator;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlUtil;
import com.amalto.core.webservice.WSGetUniversePKs;
import com.amalto.core.webservice.WSUniversePK;
import com.amalto.core.webservice.WSUniversePKArray;

public class XtentisWSBeanEnterpriseDelegator extends XtentisWSBeanDefaultDelegator{
	
	@Override
	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex)
			throws RemoteException {
		try {
			UniverseCtrlLocal ctrl = UniverseCtrlUtil.getLocalHome().create();
			Collection c =
				ctrl.getUniversePKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSUniversePK[] pks = new WSUniversePK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSUniversePK(
						((UniversePOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSUniversePKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
}
