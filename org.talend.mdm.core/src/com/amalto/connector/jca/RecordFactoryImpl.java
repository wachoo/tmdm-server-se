/*
 * Created on 27 nov. 2004
 */
package com.amalto.connector.jca;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.IndexedRecord;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.RecordFactory;

public class RecordFactoryImpl implements RecordFactory {

    public static final String RECORD_IN = "RECORD_IN"; //$NON-NLS-1$

    // the generic hashmap holding the parameters in
    public static final String PARAMS_HASHMAP_IN = "PARAMS_HASHMAP_IN"; //$NON-NLS-1$

    public static final String RECORD_OUT = "RECORD_OUT"; //$NON-NLS-1$

    public static final String RECORD_OUT_DESCRIPTION = "The generic record outbound to the Application server"; //$NON-NLS-1$

    // the status (OK STOPPED ERROR)
    public static final String STATUS_CODE_OUT = "STATUS_CODE_OUT"; //$NON-NLS-1$

    // the generic hashmap holding the values out
    public static final String PARAMS_HASHMAP_OUT = "PARAMS_HASHMAP_OUT"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_RECORD = "PUSH_MESSAGE_RECORD"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_RECORD_DESCRIPTION = "The message pushed to the application server"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_FUNCTION_TYPE = "PUSH_MESSAGE_FIELD_FUNCTION_TYPE"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_FUNCTION_NAME = "PUSH_MESSAGE_FIELD_FUNCTION_NAME"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP = "PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE = "PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET = "PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_PAYLOAD_BYTES = "PUSH_MESSAGE_FIELD_PAYLOAD_BYTES"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_USERNAME = "PUSH_MESSAGE_FIELD_USERNAME"; //$NON-NLS-1$

    public static final String PUSH_MESSAGE_FIELD_PASSWORD = "PUSH_MESSAGE_FIELD_PASSWORD"; //$NON-NLS-1$

    // Request configuration to the AS -
    // To request add a CONFIGURATION_FIELD_JNDI_NAME to the GET_CONFIGURATION_RECORD
    // the AS will send and AS_RESPONSE_RECORD containing the CONFIGURATION_FIELD_* below
    public static final String GET_CONFIGURATION_RECORD = "GET_CONFIGURATION_RECORD"; //$NON-NLS-1$

    public static final String GET_CONFIGURATION_RECORD_DESCRIPTION = "A message requesting the configuration from the application server";

    // Saves the configuration
    // Fill the CONFIGURATION_FIELD_* and add them to the SAVE_CONFIGURATION_RECORD
    public static final String SAVE_CONFIGURATION_RECORD = "SAVE_CONFIGURATION_RECORD"; //$NON-NLS-1$

    public static final String SAVE_CONFIGURATION_RECORD_DESCRIPTION = "A message containing the configuration to be saved on the application server";

    // Saves the configuration
    // Fill the CONFIGURATION_FIELD_* and add them to the SAVE_CONFIGURATION_RECORD
    public static final String IS_XML_SERVER_UP = "IS_XML_SERVER_UP"; //$NON-NLS-1$

    // Schedule the start of the adapter from the saved configuration
    // Fill the CONFIGURATION_FIELD_* and add them to the SAVE_CONFIGURATION_RECORD
    public static final String SCHEDULE_START = "SCHEDULE_START"; //$NON-NLS-1$

    // Configuration to use for save and get
    public static final String CONFIGURATION_FIELD_JNDI_NAME = "SAVE_CONFIGURATION_FIELD_JNDI_NAME"; //$NON-NLS-1$

    public static final String CONFIGURATION_FIELD_DESCRIPTION = "SAVE_CONFIGURATION_FIELD_DESCRIPTION"; //$NON-NLS-1$

    public static final String CONFIGURATION_FIELD_PORTLET_PAGE = "SAVE_CONFIGURATION_FIELD_PORTLET_PAGE"; //$NON-NLS-1$

    public static final String CONFIGURATION_FIELD_ISINBOUND = "SAVE_CONFIGURATION_FIELD_ISINBOUND"; // yes no //$NON-NLS-1$

    public static final String CONFIGURATION_FIELD_ISOUTBOUND = "SAVE_CONFIGURATION_FIELD_ISOUTBOUND";// yes no //$NON-NLS-1$

    public static final String CONFIGURATION_FIELD_PARAMETERS = "SAVE_CONFIGURATION_FIELD_PARAMETERS"; //$NON-NLS-1$

    public static final String AS_RESPONSE_RECORD = "AS_RESPONSE_RECORD"; //$NON-NLS-1$

    public static final String AS_RESPONSE_RECORD_DESCRIPTION = "The generic response record of the application server";

    public static final String AS_RESPONSE_FIELD_STATUS_CODE = "AS_RESPONSE_FIELD_STATUS_CODE"; // OK ERROR //$NON-NLS-1$

    public static final String AS_RESPONSE_FIELD_PARAMETERS = "AS_RESPONSE_FIELD_PARAMETERS"; //$NON-NLS-1$

    public MappedRecord createMappedRecord(String recordName) throws ResourceException {
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
        } else {
            record.setRecordShortDescription(RECORD_OUT_DESCRIPTION);
        }
        return record;
    }

    public IndexedRecord createIndexedRecord(String recordName) throws ResourceException {
        throw new NotSupportedException("Indexed Records are not supported by the bridge");
    }

}
