/*
 * Created on 27 nov. 2004
 *
 */
package com.amalto.connector.jca;

import javax.resource.cci.ResourceAdapterMetaData;

import com.amalto.connector.interfaces.IXtentisResourceAdapter;


/**
  * ResourceAdapterMetaData.java
 * 
 * @author bgrieder
 */
public class ResourceAdapterMetaDataImpl implements ResourceAdapterMetaData {

    public String getAdapterName() {
       return IXtentisResourceAdapter.class.getName();
    }
    public String getAdapterShortDescription() {
        return "Xtentis Connector";
    }
    public String getAdapterVendorName() {
        return "Talend SA";
    }
    public String getAdapterVersion() {
        return "core libraries v1.0";
    }
    public String[] getInteractionSpecsSupported() {
        String[] is = {
            InteractionSpecImpl.FUNCTION_GET_STATUS,
            InteractionSpecImpl.FUNCTION_PULL,
            InteractionSpecImpl.FUNCTION_PUSH,
            InteractionSpecImpl.FUNCTION_START,
            InteractionSpecImpl.FUNCTION_STOP
        };
        return is;
    }
    public String getSpecVersion() {
        return InteractionImpl.VERSION;
    }

    public boolean supportsExecuteWithInputAndOutputRecord() {
        return true;
    }
    public boolean supportsExecuteWithInputRecordOnly() {
        return true;
    }
    public boolean supportsLocalTransactionDemarcation() {
        //the adapter does not implement the LocalTransaction interface
        return false;
    }
}
