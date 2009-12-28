package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK;
import com.amalto.core.util.XtentisException;


public class SynchronizationItemCtrlBeanDefaultDelegator implements
		ISynchronizationItemCtrlBeanDelegator {


	public SynchronizationItemPOJO existsSynchronizationItem(
			SynchronizationItemPOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public SynchronizationItemPOJO getSynchronizationItem(
			SynchronizationItemPOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public Collection<SynchronizationItemPOJOPK> getSynchronizationItemPKs(
			String regex) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public SynchronizationItemPOJOPK putSynchronizationItem(
			SynchronizationItemPOJO synchronizationItem)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public SynchronizationItemPOJOPK removeSynchronizationItem(
			SynchronizationItemPOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public SynchronizationItemPOJO resolveSynchronization(
			SynchronizationItemPOJOPK pk, String resolvedProjection)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	
}
