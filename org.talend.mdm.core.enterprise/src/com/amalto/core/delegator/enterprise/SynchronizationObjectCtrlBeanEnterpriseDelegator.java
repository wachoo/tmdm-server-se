package com.amalto.core.delegator.enterprise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.amalto.core.delegator.SynchronizationObjectCtrlBeanDefaultDelegator;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationObjectPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationObjectPOJOPK;
import com.amalto.core.util.XtentisException;

public class SynchronizationObjectCtrlBeanEnterpriseDelegator extends
		SynchronizationObjectCtrlBeanDefaultDelegator {
	

    public SynchronizationObjectPOJOPK putSynchronizationObject(SynchronizationObjectPOJO synchronizationObject) throws XtentisException{  
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putSynchronizationObject() "+synchronizationObject.getPK().getUniqueId());
    	
        try {
            
            ObjectPOJOPK pk = synchronizationObject.store();
            if (pk == null) throw new XtentisException("Check the XML Server logs");
            return new SynchronizationObjectPOJOPK(pk);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the Synchronization Object "+synchronizationObject.getPK().getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

    }
    
     
    public SynchronizationObjectPOJO getSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException{

        try {
        	SynchronizationObjectPOJO sp =  ObjectPOJO.load(SynchronizationObjectPOJO.class,pk);
        	if (sp == null) {
        		String err= "The Synchronization Object "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return sp;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Synchronization Object "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    

    public SynchronizationObjectPOJO existsSynchronizationObject(SynchronizationObjectPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(SynchronizationObjectPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Synchronization Object exists:  "+pk.getUniqueId()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
    }
    


    public SynchronizationObjectPOJOPK removeSynchronizationObject(SynchronizationObjectPOJOPK pk) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("Removing "+pk.getUniqueId());

        try {
        	return new SynchronizationObjectPOJOPK(ObjectPOJO.remove(SynchronizationObjectPOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the Synchronization Plan "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }    
    
    
           
    public Collection<SynchronizationObjectPOJOPK> getSynchronizationObjectPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(SynchronizationObjectPOJO.class,regex);
    	ArrayList<SynchronizationObjectPOJOPK> l = new ArrayList<SynchronizationObjectPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new SynchronizationObjectPOJOPK(iter.next()));
		}
    	return l;
    }

}
