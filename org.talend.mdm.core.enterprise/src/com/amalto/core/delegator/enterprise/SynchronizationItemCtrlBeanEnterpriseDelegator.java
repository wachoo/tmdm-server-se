package com.amalto.core.delegator.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.amalto.core.delegator.SynchronizationItemCtrlBeanDefaultDelegator;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJOPK;
import com.amalto.core.util.XtentisException;

public class SynchronizationItemCtrlBeanEnterpriseDelegator extends
		SynchronizationItemCtrlBeanDefaultDelegator {
	

    public SynchronizationItemPOJOPK putSynchronizationItem(SynchronizationItemPOJO synchronizationItem) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putSynchronizationItem() "+synchronizationItem.getPK().getUniqueId());
    	
        try {
            
            ObjectPOJOPK pk = synchronizationItem.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new SynchronizationItemPOJOPK(synchronizationItem.getLocalRevisionID(), synchronizationItem.getItemPOJOPK());
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Synchronization Object "+synchronizationItem.getPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }

    }
    
     

    public SynchronizationItemPOJO getSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException{

        try {
        	SynchronizationItemPOJO sp =  ObjectPOJO.load(SynchronizationItemPOJO.class,pk);
        	if (sp == null) {
        		String err= "The Synchronization Object "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	//read the latest XML from the live local object if the status is manual
        	if (sp.getStatus() == SynchronizationItemPOJO.STATUS_MANUAL) {
        		ItemPOJO item = ItemPOJO.load(sp.getLocalRevisionID(), sp.getItemPOJOPK());
        		if (item == null) {
            		sp.setResolvedProjection(null);
            	} else {
            		sp.setResolvedProjection(item.getProjectionAsString());
            	}
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Synchronization Object "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }
    
  
    public SynchronizationItemPOJO existsSynchronizationItem(SynchronizationItemPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(SynchronizationItemPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Synchronization Object exists:  "+pk.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
    }
    


    public SynchronizationItemPOJOPK removeSynchronizationItem(SynchronizationItemPOJOPK pk) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing "+pk.getUniqueId());

        try {
        	ObjectPOJO.remove(SynchronizationItemPOJO.class,pk);
        	return pk;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Synchronization Plan "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err, e);
	    }
    }    
    
    
    public Collection<SynchronizationItemPOJOPK> getSynchronizationItemPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(SynchronizationItemPOJO.class,regex);
    	ArrayList<SynchronizationItemPOJOPK> l = new ArrayList<SynchronizationItemPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new SynchronizationItemPOJOPK(iter.next()));
		}
    	return l;
    }
    

    public SynchronizationItemPOJO resolveSynchronization(SynchronizationItemPOJOPK pk, String resolvedProjection) throws XtentisException{
    	
        try {
        	SynchronizationItemPOJO pojo = getSynchronizationItem(pk);
        	
        	//update the local item
        	try {
	            ItemPOJO item = ItemPOJO.load(pojo.getLocalRevisionID(), pojo.getItemPOJOPK());
	            if (item==null) {
	            	String err = "Unable to resolve: the local item '"+pojo.getItemPOJOPK()+"' in revision '"+pojo.getLocalRevisionID()+"'" +
                			" was deleted";
                	org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
                	throw new XtentisException(err);
	            }
	            item.setPlanPK(new SynchronizationPlanPOJOPK(pojo.getLastRunPlan()));
	            item.setProjectionAsString(resolvedProjection);
	            item.store(pojo.getLocalRevisionID());
        	} catch (XtentisException e) {
    	    	throw(e);
            } catch (Exception e) {
            	String err = "Unable to resolve: the local item '"+pojo.getItemPOJOPK()+"' in revision '"+pojo.getLocalRevisionID()+"'" +
            			" could not be updated: "+e.getMessage();
            	org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
            	throw new XtentisException(err);
            }
        	
        	//update the item
        	pojo.setResolvedProjection(resolvedProjection);
        	pojo.setStatus(SynchronizationItemPOJO.STATUS_RESOLVED);
        	pojo.store();
        	
        	return pojo;
        	
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to resolve the synchronization "+pk.getUniqueId()+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
    }

}
