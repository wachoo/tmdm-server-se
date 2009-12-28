package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK;
import com.amalto.core.util.XtentisException;


public interface ISynchronizationItemCtrlBeanDelegator extends BeanDelegator {
	
	public SynchronizationItemPOJOPK putSynchronizationItem(SynchronizationItemPOJO synchronizationItem) throws XtentisException;
	public SynchronizationItemPOJO getSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException;
	public SynchronizationItemPOJO existsSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException;
	public SynchronizationItemPOJOPK removeSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException;
	public Collection<SynchronizationItemPOJOPK> getSynchronizationItemPKs(String regex) throws XtentisException;
	public SynchronizationItemPOJO resolveSynchronization(SynchronizationItemPOJOPK pk, String resolvedProjection) throws XtentisException;
	
}
