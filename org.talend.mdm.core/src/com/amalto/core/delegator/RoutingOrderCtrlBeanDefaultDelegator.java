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



public class RoutingOrderCtrlBeanDefaultDelegator implements
		IRoutingOrderCtrlBeanDelegator {
	

	public void setSessionContext(SessionContext ctx) throws EJBException,
			RemoteException {
		throw new RemoteException("Not Support!");
	}

	public String executeSynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public String executeSynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO,
			boolean cleanUpRoutingOrder) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public String executeSynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO,
			boolean cleanUpRoutingOrder, UniversePOJO universePOJO)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public void executeAsynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO, long delayInMillis)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public void executeAsynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public AbstractRoutingOrderV2POJOPK removeRoutingOrder(
			AbstractRoutingOrderV2POJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public AbstractRoutingOrderV2POJOPK putRoutingOrder(
			AbstractRoutingOrderV2POJO routingOrderPOJO)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public AbstractRoutingOrderV2POJO getRoutingOrder(
			AbstractRoutingOrderV2POJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public AbstractRoutingOrderV2POJO existsRoutingOrder(
			AbstractRoutingOrderV2POJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	
	public ActiveRoutingOrderV2POJO[] findActiveRoutingOrders(
			long lastScheduledTime, int maxRoutingOrders)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public ActiveRoutingOrderV2POJO[] findDeadRoutingOrders(
			long maxLastRunStartedTime, int maxRoutingOrders)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public Collection<ActiveRoutingOrderV2POJOPK> getActiveRoutingOrderPKs(
			String regex) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public Collection<CompletedRoutingOrderV2POJOPK> getCompletedRoutingOrderPKs(
			String regex) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public Collection<FailedRoutingOrderV2POJOPK> getFailedRoutingOrderPKs(
			String regex) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public Collection<AbstractRoutingOrderV2POJOPK> getAllRoutingOrderPKs(
			String regex) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrderPKsByCriteria(
			Class<? extends AbstractRoutingOrderV2POJO> routingOrderV2POJOClass,
			String anyFieldContains, String name, long timeCreatedMin,
			long timeCreatedMax, long timeScheduledMin, long timeScheduledMax,
			long timeLastRunStartedMin, long timeLastRunStartedMax,
			long timeLastRunCompletedMin, long timeLastRunCompletedMax,
			String itemConceptContains, String itemIDsContain,
			String serviceJNDIContains, String serviceParametersContains,
			String messageContains) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public void ejbTimeout(Timer timer) {
		org.apache.log4j.Logger.getLogger(this.getClass()).warn("Not Support!");
		
	}


}
