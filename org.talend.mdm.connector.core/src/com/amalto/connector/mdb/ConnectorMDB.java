package com.amalto.connector.mdb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.ejb.EJBException;
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

import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.MappedRecordImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.ejb.local.ItemCtrl2LocalHome;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocalHome;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * 
 * @author bgrieder
 *
 */
public abstract class ConnectorMDB implements MessageDrivenBean, MessageListener, TimedObject {

	
	protected final static String STATUS_ERROR="ERROR";
	protected final static String STATUS_OK="OK";
	protected final static String STATUS_STOPPED="STOPPED";

	private MessageDrivenContext ctx;


	public void ejbCreate()   {
	}
   
	public void ejbRemove()   {
	}

	public void setMessageDrivenContext(MessageDrivenContext ctx)   {
		this.ctx = ctx;
	}

	public Record onMessage(Record inputData) throws ResourceException {
		
		try {
			MappedRecordImpl msg = (MappedRecordImpl) inputData;
			
			String recordName = msg.getRecordName();
	        if ((RecordFactoryImpl.PUSH_MESSAGE_RECORD).equals(recordName)) {
	        	return pushToXtentis(msg);
	        } else if ((RecordFactoryImpl.IS_XML_SERVER_UP).equals(recordName)) {
	        	return isXMLServerUp(msg);
	        } else if ((RecordFactoryImpl.SAVE_CONFIGURATION_RECORD).equals(recordName)) {
	        	return saveConfiguration(msg);
	        } else if ((RecordFactoryImpl.GET_CONFIGURATION_RECORD).equals(recordName)) {
	        	return getConfiguration(msg);
	        } else if ((RecordFactoryImpl.SCHEDULE_START).equals(recordName)) {
	        	return scheduleStart(msg);
	        } else  {
	        	throw new ResourceException("Unknow message: "+recordName);
	        }
	    } catch (ResourceException e) {
	        throw e;
	    } catch (Exception e) {
	    	throw new ResourceException(e.getClass().getName()+": "+e.getLocalizedMessage());
	    }
	}
	

	private Record isXMLServerUp(MappedRecordImpl msg) throws ResourceException{
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("isXMLServerUp() ");
		
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
			throw new ResourceException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
		
	}
	
	
	private Record pushToXtentis(MappedRecordImpl message) throws ResourceException{

//		dumpMessage(message);		
		try {
			String functionType = (String)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_TYPE);
			if ("service".equals(functionType.toLowerCase())) {
				return pushToXtentisViaService(message);
			} else if ("ia".equals(functionType.toLowerCase())) {
				throw new ResourceException("The 'ia' call is deprecated in push to Xtentis");
				//return pushToXtentisViaIA(message);
			} else {
				MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
				response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
				HashMap<String,Serializable> params = new HashMap<String,Serializable>();
				params.put("message", "Function "+functionType+" is not implemented");
				return response;				
			}
		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}		
	}

	
	private Record pushToXtentisViaService(MappedRecordImpl message) throws ResourceException{
		
		MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
		String functionName = (String)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_NAME);
		try {
			HashMap<String,Serializable> hmap = new HashMap<String,Serializable>();
			hmap.put("contentType", (Serializable)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE));
			hmap.put("charset", (Serializable)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET));
			hmap.put("username", (Serializable)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_USERNAME));
			hmap.put("password", (Serializable)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PASSWORD));
			hmap.put("bytes", (Serializable)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_BYTES));
			HashMap<String,Serializable> params = (HashMap<String,Serializable>)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP);
			hmap.put("parameters", params);
			String cluster = null;
			if (params!=null) {
				 cluster = (String) params.get("cluster");
			}
			//FIXME: put default cluster in conf
			hmap.put("cluster",(cluster == null ? "Inbox" : cluster));

			//access the service
			Object service = null;
			try {
				service = Util.retrieveComponent(
						null, 
						(functionName.contains("/") ? functionName.trim() : "amalto/local/service/"+functionName.trim())
				);
			} catch (XtentisException e) {
				response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
				HashMap<Serializable,Serializable> result = new HashMap<Serializable, Serializable>();
				result.put("message", "Service "+functionName+" cannot be found");
				response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, result);;
				return response;
			} catch (Exception e) {
				response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
				HashMap<Serializable,Serializable> result = new HashMap<Serializable, Serializable>();
				result.put("message", "Service  "+functionName+" cannot be accessed: "+e.getLocalizedMessage());
				response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, result);;
				return response;

			}
			
			Object result=null;
			try {
				//call the service method
				result = Util.getMethod(service, "receiveFromOutbound").invoke(
						service,
						new Object[] {hmap}
				);
			} catch (Exception e) {
				response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
				HashMap<Serializable,Serializable> errorMap = new HashMap<Serializable, Serializable>();
				errorMap.put("message","Service  "+functionName+" failed."+(e.getCause()!=null ? e.getCause().getMessage() : ""));
				response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, errorMap);
				return response;				
			}
						
			//send response back to client
			response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_OK);
			response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, result);
			return response;

		} catch (Exception e) {
			response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_ERROR);
			HashMap<Serializable,Serializable> errorMap = new HashMap<Serializable, Serializable>();
			errorMap.put(
					"message",
					"The call to service  "+functionName+" cannot be completed: "+
					e.getClass().getName()+": "+e.getLocalizedMessage()
			);
			response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, errorMap);
			return response;				
		}
	}
	

	private Record saveConfiguration(MappedRecordImpl msg)  throws ResourceException{
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("saveConfiguration() ");
		
		try {
			
			String jndiName = (String)msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME);
			if ((jndiName==null) || ("".equals(jndiName))) 
					throw new ResourceException("The JNDI Name cannot be null or empty");
			Object parameters = msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS);
			
			//Serialize Parameters to BASE64
			String base64Params = null;
			if (parameters!=null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(parameters);
				base64Params = (new BASE64Encoder()).encode(baos.toByteArray());
			}

			//Serialize the record to xml
			String xml = "";
			xml+="<JCAAdapter>";
			xml+="	<jndiname>"+jndiName+"</jndiname>";
			xml+="	<parameters>"+base64Params+"</parameters>";
			xml+="</JCAAdapter>";
				
			//Write the record - should we use webservices?
			ItemCtrl2Local ictrl = getItemCtrlLocal();
			ItemPOJO pojo = new ItemPOJO(					
					getJCAAdaptersCluster(),	//cluster
					"JCAAdapter",								//concept name
					new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")},			//item ids
					System.currentTimeMillis(),			//insertion time
					xml												//actual data
			);
			if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName())) {
				Util.getXmlServerCtrlLocal().createCluster(null, "JCAAdapters");
			}		
			ictrl.putItem(
					pojo, 
					null	//no datamodel
			);
			
			MappedRecord response = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
			response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, STATUS_OK);
			HashMap<String,Serializable> params = new HashMap<String,Serializable>();
			params.put("message", "JCA Adapter "+jndiName+" was saved successfully!");
			response.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, params);
			return response;
			
		} catch (ResourceException e) {
			throw e;
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * Get the configuration from the AS
	 * If the configuration is not found will send ERROR in the STATUS filed of the RESPONSE message
	 * @param msg
	 * @return the configuration if found
	 * @throws ResourceException
	 */
	private Record getConfiguration(MappedRecordImpl msg)  throws ResourceException{
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getConfiguration() ");
		
		
		MappedRecord resp = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
		String jndiName = (String)msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME);
		
		try {
			
			if ((jndiName==null) || ("".equals(jndiName))) 
					throw new ResourceException("The JNDI Name cannot be null or empty when requesting the configuration");

			//fetch the data
			ItemCtrl2Local ictrl = getItemCtrlLocal();
			ItemPOJO pojo = ictrl.existsItem(
					new ItemPOJOPK(
							getJCAAdaptersCluster(),
							"JCAAdapter",
							new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")}
					)
			);
			
			//fix to move old configuration document names to new filesystem friendly names (to allow for backups) 
			//in version 1.3.6
			if (pojo == null) {
				ItemPOJOPK oldPK = new ItemPOJOPK(
					getJCAAdaptersCluster(),
					"JCAAdapter",
					new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")}
				);
				pojo = ictrl.existsItem(oldPK);
				if (pojo == null) throw new ResourceException("The configuration for connector '"+jndiName+"' cannot be found.");
				//recreate the file 'new style' and delete this on
				pojo.setItemIds(new String[]{jndiName.replaceAll("\\/", "_").replaceAll(":", "_")});
				ictrl.putItem(pojo,null);
				ictrl.deleteItem(oldPK, false); // Don't override integrity check
			}
			//End fix for version < 1.3.6
			
			//Parse the configuration data
			Element e = pojo.getProjection();
			
			//Fill in the configuration record
			resp.put(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME, jndiName);
			String base64Params = Util.getFirstTextNode(e, "parameters");
			Object parameters = null;
			if (base64Params!=null) {
				byte[] bytes = (new BASE64Decoder()).decodeBuffer(base64Params);
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
				parameters = ois.readObject();
			}
			resp.put(RecordFactoryImpl.CONFIGURATION_FIELD_PARAMETERS, parameters);
			resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, "OK");
			
		} catch (Exception e) {
			resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, "ERROR");
			HashMap<String,Serializable> map = new HashMap<String,Serializable>();
			map.put("message", "Could not get the configuration for JCA Adapter "+jndiName);
			map.put("exception", e.getClass().getName()+": "+e.getLocalizedMessage());
			resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, map);
		}
		return resp;
	}
	                                                        
   
//   private void dumpMessage(MappedRecordImpl message) {
//		System.out.println(
//				"Record Name: "+message.getRecordName()+
//				"\nFunction Name: "+message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_NAME)+
//				"\nFunction Type: "+message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_FUNCTION_TYPE)+
//				"\nContent Type: "+message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CONTENT_TYPE)+
//				"\nCharset: "+message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET)+
//				"\nUsername: "+message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_USERNAME)+
//				"\nPassword: "+message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PASSWORD)
//		);
//
//		System.out.println("Parameters: ");
//		HashMap params = (HashMap)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_IA_PARAMS_HASHMAP);
//		Set keys = params.keySet();
//		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
//			String key = (String) iter.next();
//			System.out.println("    "+params.get(key));
//		}
//				
//		try {
//			System.out.println(
//					"\nPAYLOAD\n"+new String(
//							((byte[])message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_BYTES)),
//							(String)message.get(RecordFactoryImpl.PUSH_MESSAGE_FIELD_PAYLOAD_CHARSET)
//						)
//			);
//		} catch (Exception e) {
//			System.out.println("Could not print payload");
//		}			   
//   }
   
   
   private DataClusterPOJOPK getJCAAdaptersCluster() throws ResourceException{
	   org.apache.log4j.Logger.getLogger(this.getClass()).trace("getJCAAdaptersCluster() ");
	   	   
		try {
//			DataClusterCtrlLocal dcctrl = getDataClusterCtrlLocal();
//			DataClusterPOJO dataCluster = 
//				dcctrl.existsDataCluster(new DataClusterPOJOPK("JCAAdapters"));
//			if (dataCluster==null) {
//				//the cluster was not found - attempt to create it
//				return dcctrl.putDataCluster(
//						new DataClusterPOJO(
//								"JCAAdapters", 
//								"JCA Adapters", 
//								"" //no intitial vocabulary
//				));
//			}  
			return new DataClusterPOJOPK("JCAAdapters");
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}
  
   }
   
   
   private ItemCtrl2Local getItemCtrlLocal() throws ResourceException{
	   org.apache.log4j.Logger.getLogger(this.getClass()).trace("getItemCtrlLocal() ");
		try {
			InitialContext ctx = new InitialContext();
			return ((ItemCtrl2LocalHome) ctx.lookup("amalto/local/core/itemctrl2")).create();
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
   }
   
   private DataClusterCtrlLocal getDataClusterCtrlLocal() throws ResourceException{
	   org.apache.log4j.Logger.getLogger(this.getClass()).trace("getDataClusterCtrlLocal() ");
		try {
			
			InitialContext ctx = new InitialContext();
			return ((DataClusterCtrlLocalHome) ctx.lookup("amalto/local/core/dataclusterctrl")).create();
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	   	   
  }
   
   private XmlServerSLWrapperLocal getXmlServerSLWrapperLocal() throws ResourceException{
	   org.apache.log4j.Logger.getLogger(this.getClass()).trace("getXmlServerSLWrapperLocal() ");
		try {
			
			InitialContext ctx = new InitialContext();
			return ((XmlServerSLWrapperLocalHome) ctx.lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			throw new ResourceException(e.getClass().getName() + ": " + e.getLocalizedMessage());
		}	
   }
   
   
   /*****************************************************************
    *  Schedule start
   *****************************************************************/
   

   private Record scheduleStart(MappedRecordImpl msg)  throws ResourceException {
	   String jndiName = (String)msg.get(RecordFactoryImpl.CONFIGURATION_FIELD_JNDI_NAME);
	   MappedRecord resp = (new RecordFactoryImpl()).createMappedRecord(RecordFactoryImpl.AS_RESPONSE_RECORD);
	   org.apache.log4j.Logger.getLogger(this.getClass()).debug("scheduleStart() of "+jndiName);

	   try {
		   ctx.getTimerService().createTimer(2000,jndiName);  //2 seconds
		   resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, "OK");
		   
		} catch (Exception e) {
			resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE, "ERROR");
			HashMap<String,Serializable> map = new HashMap<String,Serializable>();
			map.put("message", "Could not schedule the start up of the JCA Adapter "+jndiName);
			map.put("exception", e.getClass().getName()+": "+e.getLocalizedMessage());
			resp.put(RecordFactoryImpl.AS_RESPONSE_FIELD_PARAMETERS, map);
		}
		return resp;
	
   }
   
   
   /* (non-Javadoc)
	 * @see javax.ejb.TimedObject#ejbTimeout(javax.ejb.Timer)
	 */
	public void ejbTimeout(Timer timer) {
		String JNDIName = null;
		try {
			
			JNDIName = (String) timer.getInfo();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("ejbTimeout() "+JNDIName);
			
			//cancel all other existing timers
			TimerService timerService =  ctx.getTimerService();

			Collection<Timer> timers = timerService.getTimers();
			for (Iterator<Timer> iterator = timers.iterator(); iterator.hasNext(); ) {
				Timer timer2 = iterator.next();
				org.apache.log4j.Logger.getLogger(this.getClass()).trace("ejbTimeout() Cancelling Timer "+timer2.getHandle());
				timer2.cancel();
				
			}
			
		} catch (NoSuchObjectLocalException e1) {
			org.apache.log4j.Logger.getLogger(this.getClass()).warn("This timer already has been cancelled! ");
			return;
		}
		
		//check if XML server is up an running now
		try {
			MappedRecord response = (MappedRecord)isXMLServerUp(null); 			
			if (!"OK".equals(response.get(RecordFactoryImpl.AS_RESPONSE_FIELD_STATUS_CODE))) {
				throw new ResourceException("XML SERVER IS NOT STARTED");
			}
		} catch (ResourceException re) {
			//reschedule again
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("ejbTimeout() XML Server is till not running - rescheduling connector start-up in 25s");
			ctx.getTimerService().createTimer(25000,JNDIName);  //2 seconds			
			return;
		}
		
		Connection connection=null;
		try {
    		ConnectionFactory	cxFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();				
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START_FROM_CONFIG);
			connection = cxFactory.getConnection();
			connection.createInteraction().execute(interactionSpec, null);				
		} catch (Exception e) {
			String err = 
				"The scheduled start of JCA Adapter "+JNDIName+" cannot be completed: "+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error("ejbTimeout(): "+err);
		} finally {
			try { connection.close();} catch (Exception x) {}
		}
		
	}
   
	
}

