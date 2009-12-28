package com.amalto.core.delegator;

import java.rmi.RemoteException;

import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Timer;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.util.XtentisException;



public class RoutingEngineV2CtrlBeanDefaultDelegator implements
		IRoutingEngineV2CtrlBeanDelegator {
	
	public void init() {
		// do nothing
	}
	
	public void setSessionContext(SessionContext ctx) throws EJBException,
			RemoteException {
		org.apache.log4j.Logger.getLogger(this.getClass()).warn("Not Support 'SetSessionContext'! ");
	}


	public RoutingRulePOJOPK[] route(ItemPOJOPK itemPOJOPK)
			throws XtentisException {
		//throw new XtentisException("Not Support!");
		org.apache.log4j.Logger.getLogger(this.getClass()).warn("Not Support 'Route'! ");
		return null;
	}


	public void start() throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public void stop() throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public void suspend(boolean suspend) throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public int getStatus() throws XtentisException {
		throw new XtentisException("Not Support!");
	}


	public void ejbTimeout(Timer timer) {
		org.apache.log4j.Logger.getLogger(this.getClass()).warn("Not Support!");
		
	}
	

}
