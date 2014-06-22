/*
 * Created on 12 déc. 2004
 *
 */
package com.amalto.connector.jca;

import java.io.Serializable;

import javax.resource.NotSupportedException;
import javax.resource.cci.InteractionSpec;

/**
 * @author bgrieder
 *
 */
public class InteractionSpecImpl implements InteractionSpec,Serializable {

    /** These functions are called by the application server on the resource adapter **/
    public static String FUNCTION_GET_STATUS = "FUNCTION_GET_STATUS";
    public static String FUNCTION_START = "FUNCTION_START";
    public static String FUNCTION_STOP = "FUNCTION_STOP";
    public static String FUNCTION_PULL = "FUNCTION_PULL";
    public static String FUNCTION_PUSH = "FUNCTION_PUSH";
    public static String FUNCTION_RECEIVE_FROM_ANOTHER_CONNECTOR = "FUNCTION_RECEIVE_FROM_ANOTHER_CONNECTOR";
    public static String FUNCTION_START_FROM_CONFIG = "FUNCTION_START_FROM_CONFIG";
    
    private String functionName = FUNCTION_GET_STATUS;
    private int interactionVerb = InteractionSpec.SYNC_SEND_RECEIVE;
    //private int executionTimeout = 0;

    
    /**
     * Constructor 
     */
    public InteractionSpecImpl() {
        super();
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("InteractionSpecImpl() ");
    }
    
    
    /*
    public int getExecutionTimeout() {
        return executionTimeout;
    }
    protected void setExecutionTimeout(int executionTimeout) {
        this.executionTimeout = executionTimeout;
    }
    */
    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) throws NotSupportedException{
        if (!(	(functionName.equals(FUNCTION_GET_STATUS)) ||
        		(functionName.equals(FUNCTION_PULL)) ||
        		(functionName.equals(FUNCTION_PUSH)) ||
        		(functionName.equals(FUNCTION_START)) ||
        		(functionName.equals(FUNCTION_START_FROM_CONFIG)) ||
        		(functionName.equals(FUNCTION_STOP))  ||
        		(functionName.equals(FUNCTION_RECEIVE_FROM_ANOTHER_CONNECTOR)) 
        		
                )) {
            String err = "The function "+functionName+ " is invalid!";
            org.apache.log4j.Category.getInstance(this.getClass()).error(err);
            throw new NotSupportedException(err);            
        }
        this.functionName = functionName;
    }
    public int getInteractionVerb() {
        return interactionVerb;
    }
    public void setInteractionVerb(int interactionVerb) {
        this.interactionVerb = interactionVerb;
    }

}
