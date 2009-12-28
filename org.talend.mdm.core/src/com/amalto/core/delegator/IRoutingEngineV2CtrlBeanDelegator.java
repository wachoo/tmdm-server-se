package com.amalto.core.delegator;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Timer;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.util.XtentisException;


public interface IRoutingEngineV2CtrlBeanDelegator extends BeanDelegator {
	
	public void init();
	
	public void setSessionContext(SessionContext ctx)throws EJBException,RemoteException;
	
	public RoutingRulePOJOPK[] route(ItemPOJOPK itemPOJOPK)throws XtentisException;
	
	public void start() throws XtentisException;
	
	public void stop() throws XtentisException;
	
	public void suspend(boolean suspend) throws XtentisException;
	
	public int getStatus() throws XtentisException;
	
	public void ejbTimeout(Timer timer);
	
}
