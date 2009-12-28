package com.amalto.core.delegator;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Timer;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJOPK;
import com.amalto.core.util.XtentisException;


public interface ISynchronizationPlanCtrlBeanDelegator extends BeanDelegator {
	
	public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException;
	
	public SynchronizationPlanPOJOPK putSynchronizationPlan(SynchronizationPlanPOJO synchronizationPlan) throws XtentisException;
	
	public SynchronizationPlanPOJOPK putSynchronizationPlan(String revisionID, SynchronizationPlanPOJO synchronizationPlan) throws XtentisException;
	
	public SynchronizationPlanPOJO getSynchronizationPlan(SynchronizationPlanPOJOPK pk) throws XtentisException;
	
	public SynchronizationPlanPOJO getSynchronizationPlan(String revisionID, SynchronizationPlanPOJOPK pk) throws XtentisException;
	
	public SynchronizationPlanPOJO existsSynchronizationPlan(SynchronizationPlanPOJOPK pk) throws XtentisException;
	
	public SynchronizationPlanPOJOPK removeSynchronizationPlan(SynchronizationPlanPOJOPK pk) throws XtentisException;
	
	public Collection<SynchronizationPlanPOJOPK> getSynchronizationPlanPKs(String regex) throws XtentisException;
	
	public ArrayList<String> synchronizationGetAllUnsynchronizedObjectsIDs(
	    	String revisionID, 
	    	String objectName, 
	    	String instancePattern, 
	    	String synchronizationPlanName) 
	        throws XtentisException;
	
	public String synchronizationGetMarshaledObject(String revisionID, String objectName, String uniqueID) throws XtentisException;
	
	public void synchronizationPutMarshaledObject(String revisionID, String objectName, String uniqueID, String xml) throws XtentisException;
	
	public ArrayList<ItemPOJOPK> synchronizationGetAllUnsynchronizedItemPOJOPKs(
	    	String revisionID,
	    	DataClusterPOJOPK dataClusterPOJOPK,
	    	String conceptPattern, 
	    	String instancePattern, 
	    	SynchronizationPlanPOJOPK synchronizationPlanPOJOPK,
	    	long start,
	    	int limit
	    ) throws XtentisException;
	
	public String synchronizationGetMarshaledItem(
	    	String revisionID,
	    	ItemPOJOPK itemPOJOPK
	    ) throws XtentisException;
	
	public ItemPOJOPK synchronizationPutMarshaledItem(
	    	String revisionID, 
	    	String xml
	    ) throws XtentisException;
	
	public String[] action(int action, SynchronizationPlanPOJOPK pk) throws XtentisException;
	
	public void ejbTimeout(Timer timer);
	
}
