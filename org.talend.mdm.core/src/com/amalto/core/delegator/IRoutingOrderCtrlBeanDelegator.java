package com.amalto.core.delegator;

import java.rmi.RemoteException;
import java.util.Collection;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Timer;

import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.XtentisException;


public interface IRoutingOrderCtrlBeanDelegator extends BeanDelegator {
	
	public void setSessionContext(SessionContext ctx)
    throws EJBException,RemoteException;
	
	public String executeSynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException;
	
	public String executeSynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO, boolean cleanUpRoutingOrder) throws XtentisException;
	
	public String executeSynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO,
			boolean cleanUpRoutingOrder, UniversePOJO universePOJO)
			throws XtentisException;
	
	public void executeAsynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO, long delayInMillis) throws XtentisException;
	
	public void executeAsynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException;
	
	public AbstractRoutingOrderV2POJOPK removeRoutingOrder(AbstractRoutingOrderV2POJOPK pk) 
    throws XtentisException;
	
	public AbstractRoutingOrderV2POJOPK putRoutingOrder(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException;
	
	public AbstractRoutingOrderV2POJO getRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException;
	
	public AbstractRoutingOrderV2POJO existsRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException;
	
	public ActiveRoutingOrderV2POJO[] findActiveRoutingOrders(long lastScheduledTime, int maxRoutingOrders) throws XtentisException;
	
	public ActiveRoutingOrderV2POJO[] findDeadRoutingOrders(long maxLastRunStartedTime, int maxRoutingOrders) throws XtentisException;
	
	public Collection<ActiveRoutingOrderV2POJOPK> getActiveRoutingOrderPKs(String regex) throws XtentisException;
	
	public Collection<CompletedRoutingOrderV2POJOPK> getCompletedRoutingOrderPKs(String regex) throws XtentisException;
	
	public Collection<FailedRoutingOrderV2POJOPK> getFailedRoutingOrderPKs(String regex) throws XtentisException;
	
	public Collection<AbstractRoutingOrderV2POJOPK> getAllRoutingOrderPKs(String regex) throws XtentisException;
	
	public Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrderPKsByCriteria(
	    	Class<? extends AbstractRoutingOrderV2POJO> routingOrderV2POJOClass,
	    	String anyFieldContains,
	        String name,
	        long timeCreatedMin, long timeCreatedMax,
	        long timeScheduledMin, long timeScheduledMax,
	        long timeLastRunStartedMin, long timeLastRunStartedMax,
	        long timeLastRunCompletedMin, long timeLastRunCompletedMax,
	        String itemConceptContains,
	        String itemIDsContain,
	        String serviceJNDIContains,
	        String serviceParametersContains,
	        String messageContains
	    ) throws XtentisException;
	
	public void ejbTimeout(Timer timer);
	
}
