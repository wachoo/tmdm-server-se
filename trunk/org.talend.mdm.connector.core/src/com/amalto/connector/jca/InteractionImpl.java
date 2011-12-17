/*
 * Created on 11 déc. 2004
 *
 *
 */
package com.amalto.connector.jca;

import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.Record;
import javax.resource.cci.ResourceWarning;

import com.amalto.connector.interfaces.IXtentisResourceAdapter;


/**
 * @author bgrieder
 *
 */
public class InteractionImpl implements Interaction {
	
    public static final String VERSION = "1.0";
	
    /** The connection on which the interaction is run */
    private Connection connection;
    
    /**
     * Constructor - the calling connection is passed as parameter
     */
    public InteractionImpl(Connection connection) {
        super();
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("InteractionImpl() ");
        
        this.connection = connection;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Interaction#clearWarnings()
     */
    public void clearWarnings() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("clearWarnings() ");
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Interaction#close()
     */
    public void close() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("close() ");
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Interaction#execute(javax.resource.cci.InteractionSpec, javax.resource.cci.Record)
     */
    public Record execute(InteractionSpec spec, Record input)
            throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("execute(input) ");
        
          
        try {

	        //Validate the spec
	        if (spec==null) {
	           String err = "InteractionSpec cannot be null";
	           throw new ResourceException(err);
	        }
	
	        
	        //Validate the input
	        if (input!=null) {
	            if (! (input instanceof MappedRecordImpl)) {
		            String err = "Input must be a MappedRecord. This is of type: "
		                +input.getClass().getName();
		            throw new ResourceException(err);
	            }
	         }
	               
	        //Dispatch
	        IXtentisResourceAdapter ra=  (IXtentisResourceAdapter)((ConnectionImpl)getConnection()).getResourceAdapter();
	        String function = ((InteractionSpecImpl)spec).getFunctionName();
	        
	        if (function.equals(InteractionSpecImpl.FUNCTION_GET_STATUS)) {
	        	return ra.getConnectorStatus((MappedRecord)input);
	        }
	        
	        if (function.equals(InteractionSpecImpl.FUNCTION_START)) {
	        	return ra.startConnector((MappedRecord)input);
	        }

	        if (function.equals(InteractionSpecImpl.FUNCTION_START_FROM_CONFIG)) {
	        	return ra.startFromConfiguration((MappedRecord)input);
	        }
	        
	        if (function.equals(InteractionSpecImpl.FUNCTION_STOP)) {
	        	return ra.stopConnector((MappedRecord)input);
	        }
	        
	        if (function.equals(InteractionSpecImpl.FUNCTION_PULL)) {
	        	return ra.pull((MappedRecord)input);
	        }

	        if (function.equals(InteractionSpecImpl.FUNCTION_PUSH)) {
	        	return ra.push((MappedRecord)input);
	        }

	        if (function.equals(InteractionSpecImpl.FUNCTION_RECEIVE_FROM_ANOTHER_CONNECTOR)) {
	        	return ra.receiveFromAnotherConnector((MappedRecord)input);
	        }

	        
	        String err = "Function unknown to the Xtentis Adapter: "+function;
	        throw new ResourceException(err);
        } catch (ResourceException e) {
        	org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
        	throw(e);
        } catch (XtentisConnectorException e) {
        	org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
        	throw new ResourceException(e.getLocalizedMessage());        	
        } catch (Exception e) {
        	org.apache.log4j.Logger.getLogger(this.getClass()).info("ERROR SYSTRACE: "+e.getClass().getName()+": "+e.getLocalizedMessage(),e);
        	throw new ResourceException(e);
        }
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Interaction#execute(javax.resource.cci.InteractionSpec, javax.resource.cci.Record, javax.resource.cci.Record)
     */
    public boolean execute(InteractionSpec spec, Record input, Record output) throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("execute(spec, input, output) ");
        output = execute(spec, input);
        if (output == null) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Interaction#getConnection()
     */
    public Connection getConnection() {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getConnection() ");
        return this.connection;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.Interaction#getWarnings()
     */
    public ResourceWarning getWarnings() throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("getWarnings() ");
        return null;
    }
    
        
    
}
