package com.amalto.connector.mdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.MappedRecord;
import javax.resource.cci.MessageListener;
import javax.resource.cci.Record;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.MappedRecordImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

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

    private Record pushToXtentisViaService(MappedRecordImpl message) throws ResourceException {
        MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
        String functionName = (String) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_NAME);
        try {
            Map<String, Serializable> map = new HashMap<String, Serializable>();
            map.put("contentType", (Serializable) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE)); //$NON-NLS-1$
            map.put("charset", (Serializable) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET)); //$NON-NLS-1$
            map.put("username", (Serializable) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_USERNAME)); //$NON-NLS-1$
            map.put("password", (Serializable) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PASSWORD)); //$NON-NLS-1$
            map.put("bytes", (Serializable) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_BYTES)); //$NON-NLS-1$
            HashMap<String, Serializable> params = (HashMap<String, Serializable>) message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP);
            map.put("parameters", params); //$NON-NLS-1$
            String cluster = null;
            if (params != null) {
                cluster = (String) params.get("cluster"); //$NON-NLS-1$
            }
            //FIXME: put default cluster in conf
            map.put("cluster", (cluster == null ? "Inbox" : cluster)); //$NON-NLS-1$ //$NON-NLS-2$
            //access the service
            Object service;
            try {
                service = Util.retrieveComponent(
                        null,
                        (functionName.contains("/") ? functionName.trim() : "amalto/local/service/" + functionName.trim()) //$NON-NLS-1$ //$NON-NLS-2$
                );
            } catch (XtentisException e) {
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
                Map<Serializable, Serializable> result = new HashMap<Serializable, Serializable>();
                result.put("message", "Service " + functionName + " cannot be found"); //$NON-NLS-1$
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, result);
                return response;
            } catch (Exception e) {
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
                Map<Serializable, Serializable> result = new HashMap<Serializable, Serializable>();
                result.put("message", "Service  " + functionName + " cannot be accessed: " + e.getLocalizedMessage()); //$NON-NLS-1$
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, result);
                return response;
            }
            Object result;
            try {
                //call the service method
                result = Util.getMethod(service, "receiveFromOutbound").invoke( //$NON-NLS-1$
                        service,
                        map);
            } catch (Exception e) {
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
                Map<Serializable, Serializable> errorMap = new HashMap<Serializable, Serializable>();
                errorMap.put("message", "Service  " + functionName + " failed." + (e.getCause() != null ? e.getCause().getMessage() : "")); //$NON-NLS-1$
                response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, errorMap);
                return response;
            }
            //send response back to client
            response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_OK);
            response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, result);
            return response;
        } catch (Exception e) {
            response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
            Map<Serializable, Serializable> errorMap = new HashMap<Serializable, Serializable>();
            errorMap.put(
                    "message", //$NON-NLS-1$
                    "The call to service  " + functionName + " cannot be completed: " +
                            e.getClass().getName() + ": " + e.getLocalizedMessage()
            );
            response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, errorMap);
            return response;
        }
    }


    private Record saveConfiguration(MappedRecordImpl msg) throws ResourceException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("saveConfiguration() ");
        }
        try {
            String jndiName = (String) msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME);
            if ((jndiName == null) || ("".equals(jndiName))) { //$NON-NLS-1$
                throw new ResourceException("The JNDI Name cannot be null or empty");
            }
            Object parameters = msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS);
            //Serialize Parameters to BASE64
            String base64Params = null;
            if (parameters != null) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(outputStream);
                oos.writeObject(parameters);
                base64Params = (new BASE64Encoder()).encode(outputStream.toByteArray());
            }
            //Serialize the record to xml
            String xml = "<JCAAdapter>"; //$NON-NLS-1$
            xml += "	<jndiname>" + jndiName + "</jndiname>"; //$NON-NLS-1$ //$NON-NLS-2$
            xml += "	<parameters>" + base64Params + "</parameters>"; //$NON-NLS-1$
            xml += "</JCAAdapter>"; //$NON-NLS-1$
            //Write the record
            ItemCtrl2Local itemCtrlLocal = getItemCtrlLocal();
            ItemPOJO pojo = new ItemPOJO(
                    getJCAAdaptersCluster(), //cluster
                    "JCAAdapter", //concept name
                    new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")}, //item ids
                    System.currentTimeMillis(), //insertion time
                    xml //actual data
            );
            itemCtrlLocal.putItem(pojo, null);
            MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
            response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_OK);
            Map<String, Serializable> params = new HashMap<String, Serializable>();
            params.put("message", "JCA Adapter " + jndiName + " was saved successfully!"); //$NON-NLS-1$
            response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, params);
            return response;
        } catch (ResourceException e) {
            throw e;
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    /**
     * Get the configuration from the AS
     * If the configuration is not found will send ERROR in the STATUS filed of the RESPONSE message
     *
     * @return the configuration if found
     * @throws ResourceException
     */
    private Record getConfiguration(MappedRecordImpl msg) throws ResourceException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getConfiguration() ");
        }
        MappedRecord resp = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
        String jndiName = (String) msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME);
        try {
            if ((jndiName == null) || ("".equals(jndiName))) { //$NON-NLS-1$
                throw new ResourceException("The JNDI Name cannot be null or empty when requesting the configuration");
            }
            // fetch the data
            ItemCtrl2Local itemCtrl2Local = getItemCtrlLocal();
            ItemPOJO pojo = itemCtrl2Local.existsItem(
                    new ItemPOJOPK(
                            getJCAAdaptersCluster(),
                            "JCAAdapter", //$NON-NLS-1$
                            new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    )
            );
            //fix to move old configuration document names to new filesystem friendly names (to allow for backups)
            //in version 1.3.6
            if (pojo == null) {
                ItemPOJOPK oldPK = new ItemPOJOPK(
                        getJCAAdaptersCluster(),
                        "JCAAdapter", //$NON-NLS-1$
                        new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")} //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                );
                pojo = itemCtrl2Local.existsItem(oldPK);
                if (pojo == null) {
                    throw new ResourceException("The configuration for connector '" + jndiName + "' cannot be found.");
                }
                //recreate the file 'new style' and delete this on
                pojo.setItemIds(new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                itemCtrl2Local.putItem(pojo, null);
                itemCtrl2Local.deleteItem(oldPK, false); // Don't override integrity check
            }
            //End fix for version < 1.3.6
            //Parse the configuration data
            Element e = pojo.getProjection();
            //Fill in the configuration record
            resp.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, jndiName);
            String base64Params = Util.getFirstTextNode(e, "parameters"); //$NON-NLS-1$
            Object parameters = null;
            if (base64Params != null) {
                byte[] bytes = (new BASE64Decoder()).decodeBuffer(base64Params);
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                parameters = ois.readObject();
            }
            resp.put(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS, parameters);
            resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_OK);
        } catch (Exception e) {
            resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
            HashMap<String, Serializable> map = new HashMap<String, Serializable>();
            map.put("message", "Could not get the configuration for JCA Adapter " + jndiName);
            map.put("exception", e.getClass().getName() + ": " + e.getLocalizedMessage());
            resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, map);
        }
        return resp;
    }

    private DataClusterPOJOPK getJCAAdaptersCluster() throws ResourceException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getJCAAdaptersCluster() ");
        }
        try {
            return new DataClusterPOJOPK("JCAAdapters"); //$NON-NLS-1$
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e);
        }
    }

    private ItemCtrl2Local getItemCtrlLocal() throws ResourceException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getItemCtrlLocal() ");
        }
        try {
            return Util.getItemCtrl2Local();
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e); //$NON-NLS-1$
        }
    }

    private XmlServerSLWrapperLocal getXmlServerSLWrapperLocal() throws ResourceException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("getXmlServerSLWrapperLocal() ");
        }
        try {
            return Util.getXmlServerCtrlLocal();
        } catch (Exception e) {
            throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage(), e); //$NON-NLS-1$
        }
    }

    /**
     * **************************************************************
     * Schedule start
     * ***************************************************************
     */


    private Record scheduleStart(MappedRecordImpl msg) throws ResourceException {
        String jndiName = (String) msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME);
        MappedRecord resp = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("scheduleStart() of " + jndiName);
        }
        try {
            ctx.getTimerService().createTimer(2000, jndiName); // 2 seconds
            resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_OK);
        } catch (Exception e) {
            resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
            HashMap<String, Serializable> map = new HashMap<String, Serializable>();
            map.put("message", "Could not schedule the start up of the JCA Adapter " + jndiName); //$NON-NLS-1$
            map.put("exception", e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
            resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, map);
        }
        return resp;
    }

    public void ejbTimeout(Timer timer) {
        String JNDIName;
        try {
            JNDIName = (String) timer.getInfo();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ejbTimeout() " + JNDIName);
            }
            //cancel all other existing timers
            TimerService timerService = ctx.getTimerService();
            Collection<Timer> timers = timerService.getTimers();
            for (Timer currentTimer : timers) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("ejbTimeout() Cancelling Timer " + currentTimer.getHandle());
                }
                currentTimer.cancel();
            }
        } catch (NoSuchObjectLocalException e1) {
            LOGGER.warn("This timer already has been cancelled! ");
            return;
        }
        //check if XML server is up an running now
        try {
            MappedRecord response = (MappedRecord) isXMLServerUp();
            if (!STATUS_OK.equals(response.get(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE))) {
                throw new ResourceException("XML Server is not started.");
            }
        } catch (ResourceException re) {
            //reschedule again
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ejbTimeout() XML Server is till not running - rescheduling connector start-up in 25s");
            }
            ctx.getTimerService().createTimer(25000, JNDIName);  //2 seconds			
            return;
        }
        Connection connection = null;
        try {
            ConnectionFactory cxFactory = (ConnectionFactory) (new InitialContext()).lookup(JNDIName);
            InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
            interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START_FROM_CONFIG);
            connection = cxFactory.getConnection();
            connection.createInteraction().execute(interactionSpec, null);
        } catch (Exception e) {
            String err =
                    "The scheduled start of JCA Adapter " + JNDIName + " cannot be completed: " +
                            e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error("ejbTimeout(): " + err);
        } finally {
            try {
                connection.close();
            } catch (Exception x) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Connection close error in com.amalto.connector.mdb.ConnectorMDB.ejbTimeout.", x);
                }
            }
        }
    }
}

