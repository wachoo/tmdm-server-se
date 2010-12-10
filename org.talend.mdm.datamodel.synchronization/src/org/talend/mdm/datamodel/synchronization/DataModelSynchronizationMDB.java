// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.datamodel.synchronization;

import javax.ejb.EJBException;
import javax.ejb.MessageDrivenBean;
import javax.ejb.MessageDrivenContext;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.datamodel.management.SchemaManager;
import org.talend.mdm.commmon.util.datamodel.synchronization.DMUpdateEvent;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.schema.manage.SchemaCoreAgent;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.core.dmagent.SchemaWebAgent;

/**
 * @author hshu
 * @ejb.bean        name="DataModelSynchronizationMDB"  
 *                  description="Synchronize data model changes with the core side"  
 *                  local-jndi-name="amalto/remote/core/datamodelsynchronization"  
 *                  transaction-type="Container"  
 *                  --message-selector=""  
 *                  acknowledge-mode="Auto-acknowledge"  
 *                  destination-type="javax.jms.Topic"  
 *   
 * @ejb.transaction type="NotSupported"  
 *   
 * @jboss.message-driven   connection-factory-jndi-name="ConnectionFactory"  
 *                         destination-jndi-name="topic/testTopic"  
 *   
 * @jboss.pool   initial-beans-in-free-pool="1"  
 *               max-beans-in-free-pool="1"  
 */                 
public class DataModelSynchronizationMDB implements MessageDrivenBean, MessageListener {

	    private MessageDrivenContext ctx = null;
	    private QueueConnection conn;
	    private QueueSession session;
	    
	    public DataModelSynchronizationMDB()
	    {
	       
	    }
	    
	    public void setMessageDrivenContext(MessageDrivenContext ctx)
	    {
	        this.ctx = ctx;

	    }
	    
	    public void ejbCreate()
	    {
	        try {
	            init();
	        } catch (Exception e) {
	            throw new EJBException("Failed to init DataModelSynchronizationMDB", e);
	        }
	    }

	    public void ejbRemove()
	    {
	        ctx = null;
	        try {
	            if (session != null) {
	                session.close();
	            }
	            if (conn != null) {
	                conn.close();
	            }
	        } catch(JMSException e) {
	            e.printStackTrace();
	        }
	    }
	                
	    public void onMessage(Message message)
	    {
	        try {
	            
                if(message instanceof ObjectMessage)  {
	                 ObjectMessage msg=(ObjectMessage)message;
	                 
	                 DMUpdateEvent dmUpdateEvent=(DMUpdateEvent)msg.getObject();
	                 Logger.getLogger(this.getClass()).info(dmUpdateEvent);
	                 String eventType=dmUpdateEvent.getEventType();
	                 
	                 SchemaManager schemaCoreAgent=SchemaCoreAgent.getInstance();
	                 SchemaManager schemaWebAgent=SchemaWebAgent.getInstance();
	                 if(eventType.equals(DMUpdateEvent.EVENT_TYPE_INIT)||eventType.equals(DMUpdateEvent.EVENT_TYPE_UPDATE)) { 
	                     String dataModelSchema = getSchemaFromDB(dmUpdateEvent.getDataModelVersion(),dmUpdateEvent.getDataModelPK());
	                     schemaCoreAgent.updateToDatamodelPool(
                                 dmUpdateEvent.getDataModelVersion(), 
                                 dmUpdateEvent.getDataModelPK(),
                                 dataModelSchema
                                 );
	                     schemaWebAgent.updateToDatamodelPool(
	                             dmUpdateEvent.getDataModelVersion(), 
	                             dmUpdateEvent.getDataModelPK(),
	                             dataModelSchema
	                             );
	                 }else if(eventType.equals(DMUpdateEvent.EVENT_TYPE_DELETE)) {
	                     schemaCoreAgent.removeFromDatamodelPool(dmUpdateEvent.getDataModelVersion(), dmUpdateEvent.getDataModelPK());
	                     schemaWebAgent.removeFromDatamodelPool(dmUpdateEvent.getDataModelVersion(), dmUpdateEvent.getDataModelPK());
	                 }
	            }
	            
	        } catch (JMSException e) {
	            Logger.getLogger(this.getClass()).error(e.toString());
	        } catch (Throwable t) {
	            Logger.getLogger(this.getClass()).error(t.getMessage());
	        }
	    	
	    }
	                
	    private void init()
	        throws JMSException, NamingException
	    {
	        InitialContext iniCtx = new InitialContext();
	        Object tmp = iniCtx.lookup("ConnectionFactory");
	        QueueConnectionFactory qcf = (QueueConnectionFactory) tmp;
	        conn = qcf.createQueueConnection();
	        session = conn.createQueueSession(false,QueueSession.AUTO_ACKNOWLEDGE);
	        conn.start();
	    }
	    
	    /**
	     * DOC HSHU Comment method "getSchemaFromDB".
	     * @throws Exception 
	     */
	    private String getSchemaFromDB(String revisionID, String uniqueID){
	        String dataModelSchema=null;
            try {
                DataModelPOJO dataModelPOJO = ObjectPOJO.load(revisionID, DataModelPOJO.class,new ObjectPOJOPK(uniqueID));
                dataModelSchema=dataModelPOJO.getSchema();
            } catch (XtentisException e) {
                e.printStackTrace();
                return null;
            }
	        return dataModelSchema;
	    }

}
