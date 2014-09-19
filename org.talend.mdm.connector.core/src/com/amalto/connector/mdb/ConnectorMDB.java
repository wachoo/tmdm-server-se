package com.amalto.connector.mdb;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.MappedRecordImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.server.XmlServer;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;

import javax.ejb.*;
import javax.naming.InitialContext;
import javax.resource.ResourceException;
import javax.resource.cci.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class ConnectorMDB implements MessageDrivenBean, MessageListener, TimedObject {

    private static final Logger LOGGER = Logger.getLogger(ConnectorMDB.class); 
    
    protected final static String STATUS_ERROR = "ERROR"; //$NON-NLS-1$

    protected final static String STATUS_OK = "OK"; //$NON-NLS-1$

    protected final static String STATUS_STOPPED = "STOPPED"; //$NON-NLS-1$

    private MessageDrivenContext ctx;

    public void ejbCreate() {
    }

    public void ejbRemove() {
    }

    public void setMessageDrivenContext(MessageDrivenContext ctx) {
        this.ctx = ctx;
    }

    public Record onMessage(Record inputData) throws ResourceException {
        try {
            MappedRecordImpl msg = (MappedRecordImpl) inputData;
            String recordName = msg.getRecordName();
            if ((RecordFactoryImpl.PUSH_MESSAGE_RECORD).equals(recordName)) {
                return pushToXtentis(msg);
            } else if ((RecordFactoryImpl.IS_XML_SERVER_UP).equals(recordName)) {
                return isXMLServerUp();
            } else if ((RecordFactoryImpl.SAVE_CONFIGURATION_RECORD).equals(recordName)) {
                return saveConfiguration(msg);
            } else if ((RecordFactoryImpl.GET_CONFIGURATION_RECORD).equals(recordName)) {
                return getConfiguration(msg);
            } else if ((RecordFactoryImpl.SCHEDULE_START).equals(recordName)) {
                return scheduleStart(msg);
            } else {
                throw new ResourceException("Unknown message: " + recordName);
            }
        } catch (ResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    private Record isXMLServerUp() throws ResourceException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("isXMLServerUp()");
        }
        MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
        try {
            XmlServerSLWrapperLocal server = getXmlServerSLWrapperLocal();
            if (server.isUpAndRunning()) {
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_OK);
            } else {
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
            }
            return response;
        } catch (ResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    private Record pushToXtentis(MappedRecordImpl message) throws ResourceException {
        try {
            String functionType = (String) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_TYPE);
            if ("service".equals(functionType.toLowerCase())) { //$NON-NLS-1$
                return pushToXtentisViaService(message);
            } else if ("ia".equals(functionType.toLowerCase())) { //$NON-NLS-1$
                throw new ResourceException("The 'ia' call is deprecated in push to Xtentis");
            } else {
                MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
                Map<String, Serializable> params = new HashMap<String, Serializable>();
                params.put("message", "Function " + functionType + " is not implemented"); //$NON-NLS-1$
                return response;
            }
        } catch (ResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    private XmlServer getXmlServerSLWrapperLocal() throws ResourceException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getXmlServerSLWrapperLocal() ");
        }
        try {
            return Util.getXmlServerCtrlLocal();
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e); //$NON-NLS-1$
        }
    }
}

