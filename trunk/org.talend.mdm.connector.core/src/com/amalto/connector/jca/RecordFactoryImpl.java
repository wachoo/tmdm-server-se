/*
 * Created on 27 nov. 2004
 *
 */
package com.amalto.connector.jca;

import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.RecordFactory;

import javax.resource.NotSupportedException;



/**
 * RecordFactory.java
 * 
 * @author bgrieder
 */
public class RecordFactoryImpl implements RecordFactory {

	
	/*********************************************************
	 * Messages
	 *********************************************************/
	
    public static String RECORD_IN = "RECORD_IN";
    static String RECORD_IN_DESCRIPTION = "The generic inbound record sent by the AS to the Adapter";
    //the generic hashmap holding the parameters in
    public static String PARAMS_HASHMAP_IN = "PARAMS_HASHMAP_IN";

    public static String RECORD_OUT = "RECORD_OUT";
    static String RECORD_OUT_DESCRIPTION = "The generic record outbound to the Application server";
    //the status (OK STOPPED ERROR)
    public static String STATUS_CODE_OUT = "STATUS_CODE_OUT";
    //the generic hashmap holding the values out
    public static String PARAMS_HASHMAP_OUT = "PARAMS_HASHMAP_OUT";
    
    public static String PUSH_MESSAGE_RECORD = "PUSH_MESSAGE_RECORD";
    static String PUSH_MESSAGE_RECORD_DESCRIPTION = "The message pushed to the application server";
    public static String PUSH_MESSAGE_FIELD_FUNCTION_TYPE = "PUSH_MESSAGE_FIELD_FUNCTION_TYPE";
    public static String PUSH_MESSAGE_FIELD_FUNCTION_NAME = "PUSH_MESSAGE_FIELD_FUNCTION_NAME";
    public static String PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP = "PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP";
    public static String PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE = "PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE";
    public static String PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET = "PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET";
    public static String PUSH_MESSAGE_FIELD_PAYLOAD_BYTES = "PUSH_MESSAGE_FIELD_PAYLOAD_BYTES";
    public static String PUSH_MESSAGE_FIELD_USERNAME = "PUSH_MESSAGE_FIELD_USERNAME";
    public static String PUSH_MESSAGE_FIELD_PASSWORD = "PUSH_MESSAGE_FIELD_PASSWORD";
    
    
    //Request configuration to the AS -
    //To request add a CONFIGURATION_FIELD_JNDI_NAME to the GET_CONFIGURATION_RECORD
    //the AS will send and AS_RESPONSE_RECORD containing the CONFIGURATION_FIELD_* below
    public static String GET_CONFIGURATION_RECORD = "GET_CONFIGURATION_RECORD";
    static String GET_CONFIGURATION_RECORD_DESCRIPTION = "A message requesting the configuration from the application server";
    
    //Saves the configuration
    //Fill the CONFIGURATION_FIELD_* and add them to the SAVE_CONFIGURATION_RECORD
    public static String SAVE_CONFIGURATION_RECORD = "SAVE_CONFIGURATION_RECORD";
    static String SAVE_CONFIGURATION_RECORD_DESCRIPTION = "A message containing the configuration to be saved on the application server";
    
    //Saves the configuration
    //Fill the CONFIGURATION_FIELD_* and add them to the SAVE_CONFIGURATION_RECORD
    public static String IS_XML_SERVER_UP = "IS_XML_SERVER_UP";
    static String IS_XML_SERVER_UP_DESCRIPTION = "A message to the application server to check if the xml server is up and running";

    
    //Schedule the start of the adapter from the saved configuration
    //Fill the CONFIGURATION_FIELD_* and add them to the SAVE_CONFIGURATION_RECORD
    public static String SCHEDULE_START = "SCHEDULE_START";
    static String SCHEDULE_START_DESCRIPTION = "A message to the application server to schedule the start-up from configuration";
    
    //Configuration to use for save and get
    public static String CONFIGURATION_FIELD_JNDI_NAME = "SAVE_CONFIGURATION_FIELD_JNDI_NAME";
    public static String CONFIGURATION_FIELD_DESCRIPTION = "SAVE_CONFIGURATION_FIELD_DESCRIPTION";
    public static String CONFIGURATION_FIELD_PORTLET_PAGE = "SAVE_CONFIGURATION_FIELD_PORTLET_PAGE";
    public static String CONFIGURATION_FIELD_ISINBOUND = "SAVE_CONFIGURATION_FIELD_ISINBOUND";  // yes no
    public static String CONFIGURATION_FIELD_ISOUTBOUND = "SAVE_CONFIGURATION_FIELD_ISOUTBOUND";// yes no
    public static String CONFIGURATION_FIELD_PARAMETERS = "SAVE_CONFIGURATION_FIELD_PARAMETERS";


    public static String AS_RESPONSE_RECORD = "AS_RESPONSE_RECORD";
    static String AS_RESPONSE_RECORD_DESCRIPTION = "The generic response record of the application server";
    public static String AS_RESPONSE_FIELD_STATUS_CODE = "AS_RESPONSE_FIELD_STATUS_CODE"; //OK ERROR
    public static String AS_RESPONSE_FIELD_PARAMETERS = "AS_RESPONSE_FIELD_PARAMETERS";

    
    /* (non-Javadoc)
     * @see javax.resource.cci.RecordFactory#createMappedRecord(java.lang.String)
     */
    public MappedRecord createMappedRecord(String recordName)
            throws ResourceException {
//        org.apache.log4j.Category.getInstance(this.getClass()).debug("createMappedRecord() "+recordName);
        
        MappedRecordImpl record = new MappedRecordImpl();
        record.setRecordName(recordName);
        if (PUSH_MESSAGE_RECORD.equals(recordName)) {
        	record.setRecordShortDescription(PUSH_MESSAGE_RECORD_DESCRIPTION);
        } else if (GET_CONFIGURATION_RECORD.equals(recordName)) {
        	record.setRecordShortDescription(GET_CONFIGURATION_RECORD_DESCRIPTION);
        } else if (SAVE_CONFIGURATION_RECORD.equals(recordName)) {
        	record.setRecordShortDescription(SAVE_CONFIGURATION_RECORD_DESCRIPTION);
        } else if (AS_RESPONSE_RECORD.equals(recordName)) {
        	record.setRecordShortDescription(AS_RESPONSE_RECORD_DESCRIPTION);
        } else  {
        	record.setRecordShortDescription(RECORD_OUT_DESCRIPTION);
        }
    	return record;
    }

    /* (non-Javadoc)
     * @see javax.resource.cci.RecordFactory#createIndexedRecord(java.lang.String)
     */
    public IndexedRecord createIndexedRecord(String recordName)
            throws ResourceException {
        throw new NotSupportedException("Indexed Records are not supported by the bridge");
    }

}
