/*
 * Created on 12 déc. 2004
 *
 */
package com.amalto.connector.jca;

import org.apache.log4j.Logger;

import java.io.Serializable;

import javax.resource.NotSupportedException;
import javax.resource.cci.InteractionSpec;

public class InteractionSpecImpl implements InteractionSpec, Serializable {

    /** These functions are called by the application server on the resource adapter **/
    public static final String FUNCTION_GET_STATUS = "FUNCTION_GET_STATUS"; //$NON-NLS-1$

    public static final String FUNCTION_START = "FUNCTION_START"; //$NON-NLS-1$

    public static final String FUNCTION_STOP = "FUNCTION_STOP"; //$NON-NLS-1$

    public static final String FUNCTION_PULL = "FUNCTION_PULL"; //$NON-NLS-1$

    public static final String FUNCTION_PUSH = "FUNCTION_PUSH"; //$NON-NLS-1$

    public static final String FUNCTION_RECEIVE_FROM_ANOTHER_CONNECTOR = "FUNCTION_RECEIVE_FROM_ANOTHER_CONNECTOR"; //$NON-NLS-1$

    public static final String FUNCTION_START_FROM_CONFIG = "FUNCTION_START_FROM_CONFIG"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(InteractionSpecImpl.class);

    private String functionName = FUNCTION_GET_STATUS;

    private int interactionVerb = InteractionSpec.SYNC_SEND_RECEIVE;

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) throws NotSupportedException {
        if (!((functionName.equals(FUNCTION_GET_STATUS)) || (functionName.equals(FUNCTION_PULL))
                || (functionName.equals(FUNCTION_PUSH)) || (functionName.equals(FUNCTION_START))
                || (functionName.equals(FUNCTION_START_FROM_CONFIG)) || (functionName.equals(FUNCTION_STOP)) || (functionName
                    .equals(FUNCTION_RECEIVE_FROM_ANOTHER_CONNECTOR))

        )) {
            String err = "The function " + functionName + " is invalid!";
            LOGGER.error(err);
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
