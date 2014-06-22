package com.amalto.core.objects.routing.v2.ejb;

import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.naming.InitialContext;

import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.RoutingException;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;


/**
 * @author bgrieder
 * 
 * @ejb.bean name="RoutingOrderV2Ctrl"
 *           display-name="RoutingOrderV2Ctrl"
 *           description="Routing Engine"
 *           jndi-name="amalto/remote/core/RoutingOrderCtrlV2"
 * 		  local-jndi-name = "amalto/local/core/RoutingOrderCtrlV2"
 *           type="Stateless"
 *           view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 */
public class RoutingOrderV2CtrlBean implements SessionBean, TimedObject {
  

	private final static String LOGGING_EVENT = "logging_event";
	
	private final static long DELAY = 5;  //time after which the send is accomplished asynchronously TODO: move to conf file
	
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss,SSS");
	
	private SessionContext context;
		
    
    /**
     * DocumentCtrlBean.java
     * Constructor
     * 
     */
    public RoutingOrderV2CtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	context = ctx;
    }
    

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    }
    
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {}
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    
    

    /**
     * Executes a Routing Order now
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String executeSynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException {
    	return executeSynchronously(routingOrderPOJO,true);
    }
    
    /**
     * Executes a Routing Order now
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public String executeSynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO, boolean cleanUpRoutingOrder) throws XtentisException {    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug(
    		"executeSynchronously()   "+routingOrderPOJO.getName()+" : "+routingOrderPOJO.getItemPOJOPK().getUniqueID()
    	);    	
		
    	switch (routingOrderPOJO.getStatus()) {
    		case AbstractRoutingOrderV2POJO.ACTIVE:
    			//run as designed
    			break;
    		case AbstractRoutingOrderV2POJO.COMPLETED:
    		case AbstractRoutingOrderV2POJO.FAILED:
    			//create an active routing order
    			ActiveRoutingOrderV2POJO activeRO = new ActiveRoutingOrderV2POJO(
    				routingOrderPOJO.getName(),
    				routingOrderPOJO.getTimeScheduled(),
    				routingOrderPOJO.getItemPOJOPK(),
    				routingOrderPOJO.getMessage(),
    				routingOrderPOJO.getServiceJNDI(),
    				routingOrderPOJO.getServiceParameters(),
    				routingOrderPOJO.getBindingUniverseName(),
    				routingOrderPOJO.getBindingUserToken()
    			);
    			//delete the existing one
    			if (cleanUpRoutingOrder) removeRoutingOrder(routingOrderPOJO.getAbstractRoutingOrderPOJOPK());
    			//switch variables
    			routingOrderPOJO = activeRO;
    			break;
    	}
    	
    	
    	//update timing flags
   		routingOrderPOJO.setTimeLastRunStarted(System.currentTimeMillis());
        // here don't need store the routingorder ag!
        // putRoutingOrder(routingOrderPOJO);
   		
   		//Now anything goes right or wrong, we clean up the routing order from the active queue
		cleanUpRoutingOrder = true;
   	
		Object service=null;
		try {
			service = Util.retrieveComponent(
				null, 
				routingOrderPOJO.getServiceJNDI()
			);
		} catch (XtentisException e) {
			String err = "Unable to execute the Routing Order '"+routingOrderPOJO.getName()+"'." +
					" The service: '"+routingOrderPOJO.getServiceJNDI()+"' is not found. "+e.getMessage();
			moveToFailedQueue(routingOrderPOJO, err, e, cleanUpRoutingOrder);
		}
		String result = null;
		try {
            if (Util.getMethod(service, "setRoutingOrderPOJO") != null)
                Util.getMethod(service, "setRoutingOrderPOJO").invoke(service, routingOrderPOJO);
			result = (String)Util.getMethod(service, "receiveFromInbound").invoke(
					service,
					new Object[] {
							routingOrderPOJO.getItemPOJOPK(),
							routingOrderPOJO.getName(),
							routingOrderPOJO.getServiceParameters()
					}
			);
		} catch (IllegalArgumentException e) {
			String err = "Unable to execute the Routing Order '"+routingOrderPOJO.getName()+"'." +
			" The service: '"+routingOrderPOJO.getServiceJNDI()+"' cannot be executed due to wrong parameters. "+e.getMessage();
			moveToFailedQueue(routingOrderPOJO, err, e, cleanUpRoutingOrder);
		} catch (EJBException e) {
			String err = "Unable to execute the Routing Order '"+routingOrderPOJO.getName()+"'." +
			" The service: '"+routingOrderPOJO.getServiceJNDI()+"' cannot be executed. "+e.getMessage();
			moveToFailedQueue(routingOrderPOJO, err, e, cleanUpRoutingOrder);
		} catch (IllegalAccessException e) {
			String err = "Unable to execute the Routing Order '"+routingOrderPOJO.getName()+"'." +
			" The service: '"+routingOrderPOJO.getServiceJNDI()+"' cannot be executed due to security reasons. "+e.getMessage();
			moveToFailedQueue(routingOrderPOJO, err, e, cleanUpRoutingOrder);
			throw new XtentisException(err);
		} catch (InvocationTargetException e) {
			String err = "Unable to execute the Routing Order '"+routingOrderPOJO.getName()+"'." +
			" The service: '"+routingOrderPOJO.getServiceJNDI()+"' failed. ";
			if (e.getCause()!=null) err+=
				(e.getCause() instanceof XtentisException ? "" : e.getCause().getClass().getName()+": ")+e.getCause().getMessage();
			moveToFailedQueue(routingOrderPOJO, err, e, cleanUpRoutingOrder);
		}
		
		//The service call completed successfully -- add to the COMPLETED queue
		moveToCompletedQueue(
			routingOrderPOJO, 
    		null,
    		cleanUpRoutingOrder
    	);
		
		return result;

    }
    
    /**
	 * 
	 * Executes a Routing Order now in a particular universe
	 * @throws XtentisException
	 * 
	 * 
	 * @ejb.interface-method view-type = "both"
	 * @ejb.facade-method
	 */
	public String executeSynchronously(
			AbstractRoutingOrderV2POJO routingOrderPOJO,
			boolean cleanUpRoutingOrder, UniversePOJO universePOJO)
			throws XtentisException {

		org.apache.log4j.Logger.getLogger(this.getClass()).trace(

				"executeSynchronously()   " + routingOrderPOJO.getName()
						+ " : "
						+ routingOrderPOJO.getItemPOJOPK().getUniqueID()
						+ " in Universe " + universePOJO.getPK().getUniqueId()

		);

		// set the universe for the anonymous user

		if (universePOJO != null) {

			try {

				LocalUser.getLocalUser().setUniverse(universePOJO);

				org.apache.log4j.Logger.getLogger(this.getClass().getName())
						.debug(

								"executeSynchronously: Routing Order '"
										+ routingOrderPOJO.getItemPOJOPK()
												.getUniqueID()
										+ "' in Universe '"
										+ universePOJO.getPK().getUniqueId()
										+ "'"

						);

			} catch (XtentisException e) {

				String err = "Unable to set Universe "
						+ universePOJO.getPK().getUniqueId() +

						" for Routing Order "
						+ routingOrderPOJO.getPK().getUniqueId()
						+ ". Staying with HEAD." +

						" The service: '" + routingOrderPOJO.getServiceJNDI()
						+ "' failed. ";

				if (e.getCause() != null)
					err +=

					(e.getCause() instanceof XtentisException ? "" : e
							.getCause().getClass().getName()
							+ ": ")
							+ e.getCause().getMessage();

				moveToFailedQueue(routingOrderPOJO, err, e, cleanUpRoutingOrder);

			}

		}

		return executeSynchronously(routingOrderPOJO, cleanUpRoutingOrder);

	}
    
    
    private void moveToFailedQueue(AbstractRoutingOrderV2POJO routingOrderPOJO, String message, Exception e, boolean cleanUpRoutingOrder)  throws XtentisException{   	
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("addToErrorQueue() "+routingOrderPOJO.getName()+": "+message);
    	
    	//Log
		if (LOGGING_EVENT.equals(routingOrderPOJO.getItemPOJOPK().getConceptName()))
			org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+message,e);
		else
			org.apache.log4j.Logger.getLogger(this.getClass()).error(message,e);
    			
		//Create Failed Routing Order
		FailedRoutingOrderV2POJO failedRO = null;
		if (routingOrderPOJO instanceof FailedRoutingOrderV2POJO) {
			failedRO = (FailedRoutingOrderV2POJO)routingOrderPOJO;
		} else {
			failedRO = new FailedRoutingOrderV2POJO(routingOrderPOJO);
			if (cleanUpRoutingOrder)
				removeRoutingOrder(routingOrderPOJO.getAbstractRoutingOrderPOJOPK());
		}
    	failedRO.setMessage(
    		failedRO.getMessage()+
    		"\n---> FAILED "+sdf.format(new Date())+": "+message
    	);

		putRoutingOrder(failedRO);
		
		throw new RoutingException(message);
    }

    
    
    private void moveToCompletedQueue(AbstractRoutingOrderV2POJO routingOrderPOJO, String message, boolean cleanUpRoutingOrder)  throws XtentisException{   	
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("moveToCompletedQueue() "+routingOrderPOJO.getName()+": "+message);
    	    	
		try {
			//Create the Completed Routing Order
			CompletedRoutingOrderV2POJO completedRO = null;
			if (routingOrderPOJO instanceof CompletedRoutingOrderV2POJO) {
				completedRO = (CompletedRoutingOrderV2POJO)routingOrderPOJO;
			} else {
				completedRO = new CompletedRoutingOrderV2POJO(routingOrderPOJO);
				if (cleanUpRoutingOrder) 
					removeRoutingOrder(routingOrderPOJO.getAbstractRoutingOrderPOJOPK());
			}
			completedRO.setMessage(
				completedRO.getMessage()+
				"\n---> COMPLETED "+sdf.format(new Date())+(message!=null ? "\n"+message : "")
			);
			putRoutingOrder(completedRO);
		} catch (XtentisException e) {
			//try to move to the error queue
			String err = "ERROR SYSTRACE: Moving of routing order '"+routingOrderPOJO.getName()+"' to COMPLETED queue with message  '"+message+"' failed. Moving to FAILED queue.";
			org.apache.log4j.Logger.getLogger(this.getClass()).info(err,e);
		}
		
    }

        
    /**
     * Executes a Routing Order in delay milliseconds
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void executeAsynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO, long delayInMillis) throws XtentisException {
    	createTimer(routingOrderPOJO, delayInMillis);
    }

    /**
     * Executes a Routing Order in default DELAY milliseconds
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void executeAsynchronously(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException {
    	createTimer(routingOrderPOJO,DELAY);
    }
    
    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJOPK removeRoutingOrder(AbstractRoutingOrderV2POJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("removeRoutingOrder() "+pk.getUniqueId());

        try {
        	ObjectPOJO.remove(pk.getRoutingOrderClass(), pk);
        	return pk;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Routing Order of class "+pk.getRoutingOrderClass()+" and id "+pk.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err,e);
	    }
    }    
    
    
    /**
     * Creates or updates a Transformer
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJOPK putRoutingOrder(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException{  
    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putRouting Order() "+routingOrderPOJO.getName());
    	
        try {
        	
        	ObjectPOJOPK pk = routingOrderPOJO.store();
            if (pk == null) {
//        	    String err = "XML server failure. Unable to create/update the Routing Order. "+routingOrderPOJO.getPK().getUniqueId()+" with message\n"+routingOrderPOJO.getMessage();
//        	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
//            	throw new XtentisException(err);
            	return null;
            }
            
            return routingOrderPOJO.getAbstractRoutingOrderPOJOPK();
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Routing Order. "+routingOrderPOJO.getPK().getUniqueId()+" with message\n"+routingOrderPOJO.getMessage()
    	    		+"\n Exception: "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }

    }

    
    
    /**
     * Get Routing Order
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJO getRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException{

        try {
        	AbstractRoutingOrderV2POJO routingOrder =  ObjectPOJO.load(pk.getRoutingOrderClass(),pk);
        	if (routingOrder == null) {
        		String err= "The Routing Order "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return routingOrder;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Routing Order of class "+pk.getRoutingOrderClass()+" and id "+pk.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    
    
    /**
     * Get a RoutingOrder knowing its class - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public AbstractRoutingOrderV2POJO existsRoutingOrder(AbstractRoutingOrderV2POJOPK pk) throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(pk.getRoutingOrderClass(),pk);
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Unable to check the existence of the Routing Order of class "+pk.getRoutingOrderClass()+" and id "+pk.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug("existsRoutingOrder() "+info, e);
    	   return null;
	    }
    }
    
    /**
     * Find Active Routing Orders
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ActiveRoutingOrderV2POJO[] findActiveRoutingOrders(long lastScheduledTime, int maxRoutingOrders) throws XtentisException{
    	
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    		throw new XtentisException(err);
    	}
    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(
    		ActiveRoutingOrderV2POJO.class)
    	);
    	
    	//get the DB
    	XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
		String clusterName=ObjectPOJO.getCluster(ActiveRoutingOrderV2POJO.class.getName());
		String collectionpath= CommonUtil.getPath(revisionID, clusterName);
		String query="collection(\""+collectionpath+ "\")/*[number(@time-scheduled) lt "+lastScheduledTime+"][number(@time-last-run-started) eq -1]/name/text()";
		if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName())) {				
			query=" for $pivot0 in collection(\""+collectionpath+ "\")/*[number(@time-scheduled) lt "+lastScheduledTime+"][number(@time-last-run-started) eq -1]/name/text() return <result>{$pivot0}</result> ";								
		}
		Collection<String> names = server.runQuery(
			revisionID,
			clusterName, 
			query,   
			null
		);
		
		if ((names==null) || (names.size()==0)) return new ActiveRoutingOrderV2POJO[0];
		
		ArrayList<ActiveRoutingOrderV2POJO> routingOrdersList = new ArrayList<ActiveRoutingOrderV2POJO>();
		int i=0;
		for (Iterator<String> iterator = names.iterator(); iterator.hasNext() && i++ <maxRoutingOrders; ) {
			String name = iterator.next();
    		ActiveRoutingOrderV2POJO routingOrder = ObjectPOJO.load(
            	ActiveRoutingOrderV2POJO.class, 
            	new ActiveRoutingOrderV2POJOPK(name)
            );
            if (routingOrder != null)
                routingOrdersList.add(routingOrder);
		}
		ActiveRoutingOrderV2POJO[] routingOrders = 
			routingOrdersList.toArray(
				new ActiveRoutingOrderV2POJO[routingOrdersList.size()]
			);
		Arrays.sort(routingOrders);
		return routingOrders;
    }
    	
    
    /**
     * Find Dead Routing Orders
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ActiveRoutingOrderV2POJO[] findDeadRoutingOrders(long maxLastRunStartedTime, int maxRoutingOrders) throws XtentisException{
    	
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    		throw new XtentisException(err);
    	}
    	String revisionID = universe.getXtentisObjectsRevisionIDs().get(ObjectPOJO.getObjectsClasses2NamesMap().get(
    		ActiveRoutingOrderV2POJO.class)
    	);
    	
    	//Get the DB
    	XmlServerSLWrapperLocal server = null;
		try {
			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
			throw new RuntimeException(err);
		}
		String clusterName=ObjectPOJO.getCluster(ActiveRoutingOrderV2POJO.class.getName());
		String collectionpath= CommonUtil.getPath(revisionID, clusterName);
		String query="collection(\""+collectionpath+ "\")/*[number(@time-last-run-started) lt "+maxLastRunStartedTime+"][number(@time-last-run-started) gt 0]/name/text()";
		if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName())) {				
			query=" for $pivot0 in collection(\""+collectionpath+ "\")/*[number(@time-last-run-started) lt "+maxLastRunStartedTime+"][number(@time-last-run-started) gt 0]/name/text() return <result>{$pivot0}</result> ";								
		}		
		Collection<String> names = server.runQuery(
			revisionID,
			clusterName, 
			query,
			null
		);
		
		if ((names==null) || (names.size()==0)) return new ActiveRoutingOrderV2POJO[0];
		
		ArrayList<ActiveRoutingOrderV2POJO> routingOrdersList = new ArrayList<ActiveRoutingOrderV2POJO>();
		int i=0;
		for (Iterator<String> iterator = names.iterator(); iterator.hasNext() && i++ <maxRoutingOrders; ) {
			String name = iterator.next();
    		ActiveRoutingOrderV2POJO routingOrder = ObjectPOJO.load(
            	ActiveRoutingOrderV2POJO.class, 
            	new ActiveRoutingOrderV2POJOPK(name)
            );
    		routingOrdersList.add(routingOrder);
		}
		ActiveRoutingOrderV2POJO[] routingOrders = 
			routingOrdersList.toArray(
				new ActiveRoutingOrderV2POJO[routingOrdersList.size()]
			);
		Arrays.sort(routingOrders);
		return routingOrders;
    }
    

    /**
	 * Retrieve all Active Routing Order PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<ActiveRoutingOrderV2POJOPK> getActiveRoutingOrderPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(ActiveRoutingOrderV2POJO.class, regex);
    	ArrayList<ActiveRoutingOrderV2POJOPK> l = new ArrayList<ActiveRoutingOrderV2POJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new ActiveRoutingOrderV2POJOPK(iter.next()));
		}
    	return l;
    }

    /**
	 * Retrieve all Completed Routing Order PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<CompletedRoutingOrderV2POJOPK> getCompletedRoutingOrderPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(CompletedRoutingOrderV2POJO.class, regex);
    	ArrayList<CompletedRoutingOrderV2POJOPK> l = new ArrayList<CompletedRoutingOrderV2POJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new CompletedRoutingOrderV2POJOPK(iter.next()));
		}
    	return l;
    }

    /**
	 * Retrieve all Failed Routing Order PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<FailedRoutingOrderV2POJOPK> getFailedRoutingOrderPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(FailedRoutingOrderV2POJO.class, regex);
    	ArrayList<FailedRoutingOrderV2POJOPK> l = new ArrayList<FailedRoutingOrderV2POJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new FailedRoutingOrderV2POJOPK(iter.next()));
		}
    	return l;
    }

    
    /**
	 * Retrieve all RoutingOrder PKs whatever the class
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<AbstractRoutingOrderV2POJOPK> getAllRoutingOrderPKs(String regex) throws XtentisException {
    	ArrayList<AbstractRoutingOrderV2POJOPK> l = new ArrayList<AbstractRoutingOrderV2POJOPK>();
    	l.addAll(getActiveRoutingOrderPKs(regex));
    	l.addAll(getCompletedRoutingOrderPKs(regex));
    	l.addAll(getFailedRoutingOrderPKs(regex));
    	return l;

    }
    
    /**
     * Retrieve all RoutingOrder PKs by CriteriaWithPaging
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public Collection<AbstractRoutingOrderV2POJOPK> getRoutingOrderPKsByCriteriaWithPaging(
            Class<? extends AbstractRoutingOrderV2POJO> routingOrderV2POJOClass, String anyFieldContains, String name,
            long timeCreatedMin, long timeCreatedMax, long timeScheduledMin, long timeScheduledMax, long timeLastRunStartedMin,
            long timeLastRunStartedMax, long timeLastRunCompletedMin, long timeLastRunCompletedMax, String itemConceptContains,
            String itemIDsContain, String serviceJNDIContains, String serviceParametersContains, String messageContains,
            int start, int limit, boolean withTotalCount) throws XtentisException {

        String pojoName = ObjectPOJO.getObjectRootElementName(ObjectPOJO.getObjectName(routingOrderV2POJOClass));

        org.apache.log4j.Logger.getLogger(this.getClass()).debug(
                "getRoutingOrderPKsByCriteriaWithPaging() " + routingOrderV2POJOClass + "  " //$NON-NLS-1$ //$NON-NLS-2$
                        + ObjectPOJO.getObjectName(routingOrderV2POJOClass) + "  " + pojoName); //$NON-NLS-1$

        WhereAnd wAnd = new WhereAnd();

        if ((anyFieldContains != null) && (!"*".equals(anyFieldContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "//*", WhereCondition.CONTAINS, anyFieldContains, WhereCondition.PRE_NONE, //$NON-NLS-1$
                    false));
        }

        if ((name != null) && (!"*".equals(name))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/name", WhereCondition.CONTAINS, name, WhereCondition.PRE_NONE, false)); //$NON-NLS-1$
        }

        if (timeCreatedMin > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-created", WhereCondition.GREATER_THAN_OR_EQUAL, "" + timeCreatedMin, //$NON-NLS-1$ //$NON-NLS-2$
                    WhereCondition.PRE_NONE, false));
        }
        if (timeCreatedMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-created", WhereCondition.LOWER_THAN_OR_EQUAL, "" + timeCreatedMax, //$NON-NLS-1$ //$NON-NLS-2$
                    WhereCondition.PRE_NONE, false));
        }

        if (timeScheduledMin > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-scheduled", WhereCondition.GREATER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeScheduledMin, WhereCondition.PRE_NONE, false));
        }
        if (timeScheduledMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-scheduled", WhereCondition.LOWER_THAN_OR_EQUAL, "" + timeScheduledMax, //$NON-NLS-1$ //$NON-NLS-2$
                    WhereCondition.PRE_NONE, false));
        }

        if (timeLastRunStartedMin > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-started", WhereCondition.GREATER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeLastRunStartedMin, WhereCondition.PRE_NONE, false));
        }
        if (timeLastRunStartedMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-started", WhereCondition.LOWER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeLastRunStartedMax, WhereCondition.PRE_NONE, false));
        }

        if (timeLastRunCompletedMin > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-completed", WhereCondition.GREATER_THAN_OR_EQUAL, "" //$NON-NLS-1$//$NON-NLS-2$
                    + timeLastRunCompletedMin, WhereCondition.PRE_NONE, false));
        }
        if (timeLastRunCompletedMax > 0) {
            wAnd.add(new WhereCondition(pojoName + "/@time-last-run-completed", WhereCondition.LOWER_THAN_OR_EQUAL, "" //$NON-NLS-1$ //$NON-NLS-2$
                    + timeLastRunCompletedMax, WhereCondition.PRE_NONE, false));
        }

        if ((itemConceptContains != null) && (!"*".equals(itemConceptContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/item-pOJOPK/concept-name", WhereCondition.CONTAINS, itemConceptContains, //$NON-NLS-1$
                    WhereCondition.PRE_AND, false));
        }

        if ((itemIDsContain != null) && (!"*".equals(itemIDsContain))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/item-pOJOPK/ids", WhereCondition.CONTAINS, itemIDsContain, //$NON-NLS-1$
                    WhereCondition.PRE_AND, false));
        }

        if ((serviceJNDIContains != null) && (!"*".equals(serviceJNDIContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/service-jNDI", WhereCondition.CONTAINS, serviceJNDIContains.contains("/") //$NON-NLS-1$ //$NON-NLS-2$
                    || serviceJNDIContains.startsWith("*") ? serviceJNDIContains : serviceJNDIContains, WhereCondition.PRE_AND, //$NON-NLS-1$
                    false));
        }

        if ((serviceParametersContains != null) && (!"*".equals(serviceParametersContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/service-parameters", WhereCondition.CONTAINS, serviceParametersContains, //$NON-NLS-1$
                    WhereCondition.PRE_AND, false));
        }

        if ((messageContains != null) && (!"*".equals(messageContains))) { //$NON-NLS-1$
            wAnd.add(new WhereCondition(pojoName + "/message", WhereCondition.CONTAINS, messageContains, WhereCondition.PRE_AND, //$NON-NLS-1$
                    false));
        }

        ArrayList<AbstractRoutingOrderV2POJOPK> list = new ArrayList<AbstractRoutingOrderV2POJOPK>();

        Collection<ObjectPOJOPK> col = ObjectPOJO.findPKsByCriteriaWithPaging(routingOrderV2POJOClass, new String[] {
                pojoName + "/name", pojoName + "/@status" }, wAnd.getSize() == 0 ? null : wAnd, null, null, start, limit, withTotalCount);//$NON-NLS-1$ //$NON-NLS-2$

        for (Iterator<ObjectPOJOPK> iterator = col.iterator(); iterator.hasNext();) {
            ObjectPOJOPK objectPOJOPK = iterator.next();
            if (routingOrderV2POJOClass.equals(ActiveRoutingOrderV2POJO.class))
                list.add(new ActiveRoutingOrderV2POJOPK(objectPOJOPK.getIds()[0]));
            else if (routingOrderV2POJOClass.equals(CompletedRoutingOrderV2POJO.class))
                list.add(new CompletedRoutingOrderV2POJOPK(objectPOJOPK.getIds()[0]));
            else if (routingOrderV2POJOClass.equals(FailedRoutingOrderV2POJO.class))
                list.add(new FailedRoutingOrderV2POJOPK(objectPOJOPK.getIds()[0]));
        }

        return list;
    }
    
    /**
	 * Retrieve all RoutingOrder PKs by Criteria
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
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
        String messageContains) throws XtentisException {
            return getRoutingOrderPKsByCriteriaWithPaging(routingOrderV2POJOClass, anyFieldContains, name, timeCreatedMin,
                    timeCreatedMax, timeScheduledMin, timeScheduledMax, timeLastRunStartedMin, timeLastRunStartedMax,
                    timeLastRunCompletedMin, timeLastRunCompletedMax, itemConceptContains, itemIDsContain, serviceJNDIContains,
                    serviceParametersContains, messageContains, 0, Integer.MAX_VALUE, false);
    }
    

    
    /*****************************************************************
     *  T I M E R
    *****************************************************************/
    
    /**
     * 
     * @param intervalDuration
     * @return a TimerHandle
     */
    private TimerHandle createTimer(AbstractRoutingOrderV2POJO routingOrderPOJO, long ms) {
    	
    	UniversePOJO universePOJO = null;
    	
    	try {
    		
	    	if(routingOrderPOJO.getBindingUniverseName()!=null&&!routingOrderPOJO.getBindingUniverseName().equals("[HEAD]")){
	    		universePOJO = ObjectPOJO.load(null, UniversePOJO.class, new UniversePOJOPK(routingOrderPOJO.getBindingUniverseName()));
	    	}else{
	    		universePOJO = LocalUser.getLocalUser().getUniverse();
	    	}
	    	
        } catch (XtentisException e) {
                String err = "Unable to get the Universe for the local user: using head. "+e.getMessage();
                org.apache.log4j.Logger.getLogger(this.getClass().getName()).warn("createTimer "+err);
                e.printStackTrace();
        }
        
        //Create routing order data
        AsynchronousOrderData routingOrderData = new AsynchronousOrderData(universePOJO, routingOrderPOJO);

  
    	if (ms < DELAY) ms = DELAY;
        TimerService timerService =  context.getTimerService();
        Timer timer = timerService.createTimer(ms,routingOrderData);  
        TimerHandle th = timer.getHandle();
        return th;
    }
    
        
	/* (non-Javadoc)
	 * @see javax.ejb.TimedObject#ejbTimeout(javax.ejb.Timer)
	 */
	public void ejbTimeout(Timer timer) {
		
        RoutingEngineV2POJO routingEngine = RoutingEngineV2POJO.getInstance();
		//if routing engine is not running stop here
		if (routingEngine.getStatus() != RoutingEngineV2POJO.RUNNING) return;
		
		//recover routing order data
        AsynchronousOrderData routingOrderData = (AsynchronousOrderData) timer.getInfo();
        org.apache.log4j.Logger.getLogger(this.getClass()).trace(
               "ejbTimeout() retrieving routing order "+routingOrderData.routingOrderV2POJO.getPK().getUniqueId()+
               "in universe "+routingOrderData.currentUniversePOJO.getPK().getUniqueId()
        );

        
        //retrieve the Routing Order Ctrl. This should re-initialize the JACC Context
        RoutingOrderV2CtrlLocal routingOrderCtrl = null;
        try {
             routingOrderCtrl = Util.getRoutingOrderV2CtrlLocal();
        } catch (Exception e) {
               //an error occurred  - free the executor
               org.apache.log4j.Logger.getLogger(this.getClass()).info(
                      "ejbTimeout() ERROR SYSTRACE:Unable to retirve the Routing Order Ctrl for "+routingOrderData.routingOrderV2POJO.getPK().getUniqueId()+". "
                      +e.getMessage()
               );
        }



        try {
             routingOrderCtrl.executeSynchronously(routingOrderData.routingOrderV2POJO, true, routingOrderData.currentUniversePOJO);
        } catch (Exception e) {
             //an error occurred  - free the executor
             org.apache.log4j.Logger.getLogger(this.getClass()).info("ejbTimeout() ERROR SYSTRACE: Asynchronous execution failed. "+e.getMessage());
        }


	}
    

    

      
}