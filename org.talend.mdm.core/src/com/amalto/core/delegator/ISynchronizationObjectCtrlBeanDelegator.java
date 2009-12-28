package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.synchronization.ejb.SynchronizationObjectPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationObjectPOJOPK;
import com.amalto.core.util.XtentisException;


public interface ISynchronizationObjectCtrlBeanDelegator extends BeanDelegator {
	
	public SynchronizationObjectPOJOPK putSynchronizationObject(SynchronizationObjectPOJO synchronizationObject) throws XtentisException;
	public SynchronizationObjectPOJO getSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException;
	public SynchronizationObjectPOJO existsSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException;
	public SynchronizationObjectPOJOPK removeSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException;
	public Collection<SynchronizationObjectPOJOPK> getSynchronizationObjectPKs(String regex) throws XtentisException;
}
