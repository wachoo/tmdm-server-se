
package com.amalto.service.svn.handler;

import java.io.Serializable;
import java.util.HashMap;

import javax.naming.InitialContext;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.objects.versioning.util.HistoryInfos;
import com.amalto.core.util.XtentisException;
import com.amalto.service.svn.bean.SvnConfiguration;

/**
 * 
 * @author bgrieder
 */
public class SvnHandler{

	public final static String PROCESS_ERROR="ERROR";
	public final static String PROCESS_OK="OK";
	
	private final static String SVN_JNDI="java:jca/xtentis/connector/svn";
		
	protected SvnConfiguration configuration = new SvnConfiguration();
	
	protected Connection connection = null;

	public SvnHandler(SvnConfiguration configuration) {
		super();
		this.configuration = configuration;
	}


	protected SvnConfiguration getConfiguration() {
		return this.configuration;
	}
	

	/****************************************************************************************
	 *  Utility methods 
	 ****************************************************************************************/
	
	public String getStatus(){
    	Connection conx = null;
    	try {
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			
			//get the status
	    	HashMap params = new HashMap();
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
			MappedRecord result = (MappedRecord)interaction.execute(interactionSpec, recordIn);
			
			String statusCode = (String)result.get(RecordFactoryImpl.STATUS_CODE_OUT);			
			return statusCode;

		} catch (Exception e) {
			return "ERROR";
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	

	public Connection testConnection() throws XtentisException {
    	try {
    		return getConnection();
    	} catch (Exception e) {
    		throw new XtentisException(e.getClass().getName()+": "+e.getLocalizedMessage());
    	}
	}
	
    protected Connection getConnection() throws XtentisException {
    	try {
    		if (connection == null ) connection =  ((ConnectionFactory)(new InitialContext()).lookup(SVN_JNDI)).getConnection();
    		return connection;
    	} catch (Exception e) {
    		throw new XtentisException(e.getClass().getName()+": "+e.getLocalizedMessage());
    	}
    }

    public void close() throws XtentisException {
    	try {
    		if (connection != null ) connection.close();
    	} catch (Exception e) {}
    }

    
    public HashMap<Serializable,Serializable> applyChanges() throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("applyChanges() ");
    	
    	Connection conx = null;
    	try {
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			
			//stop the listener
//			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
//			try {interaction.execute(interactionSpec, recordIn); } catch (Exception e) {}

			//start the listener
			HashMap<Serializable,Serializable> parameters = new HashMap<Serializable, Serializable>();
			
			parameters.put("url", configuration.getUrl());
			parameters.put("username", configuration.getUsername());
			parameters.put("password", configuration.getPassword());
			
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
			MappedRecord result = (MappedRecord)interaction.execute(interactionSpec, recordIn);
			
			//check the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String message = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "File System Connector handler: could not start the handler: "+message;
				org.apache.log4j.Category.getInstance(this.getClass()).error("start() "+err);
				throw new XtentisException(err);
			}
			
			return (HashMap<Serializable,Serializable>) result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
    		
    	} catch (ResourceException re) {
    		throw new XtentisException(re.getMessage());
    	} catch (XtentisException xce) {
    		throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not start the AS2 connector :"+ 
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
    }
    
    
	public byte [] checkoutFile(String filename, String tag,String revision) throws com.amalto.core.util.XtentisException  {
		Connection conx = null;
		
		try {
			//Get Connection to the File System Connector
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap parameters = new HashMap();
			
			parameters.put("command","checkout");
			parameters.put("fileName",filename);
			if (tag!=null && !"".equals(tag))parameters.put("tag",tag);
			if (revision!=null && !"".equals(revision))parameters.put("revision",revision);
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "File System Service: could not post message: "+msg;
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
			HashMap<String, Object> paramsOut =  (HashMap<String, Object>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
			byte[] content = (byte [])paramsOut.get("content");
			return content;
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the process on the Svn connector for file "+filename+" and tag "+tag 
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	
	public String [] list(String path, String tag) throws com.amalto.core.util.XtentisException  {
		Connection conx = null;
		
		try {
			//Get Connection to the File System Connector
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap parameters = new HashMap();
			
			parameters.put("command","list");
			parameters.put("path",path);
			if (tag!=null && !"".equals(tag))
				parameters.put("tag",tag);
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "File System Service: could not post message: "+msg;
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
			HashMap<String, Object> paramsOut =  (HashMap<String, Object>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
			String[] entries = (String [])paramsOut.get("list");
			return entries;
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the list process on the Svn connector for path "+path+" and tag "+tag 
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	
	public void commit(String path, String content, String comments) throws com.amalto.core.util.XtentisException  {
		Connection conx = null;
		
		try {
			//Get Connection to the File System Connector
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap parameters = new HashMap();
			
			parameters.put("command","commit");
			parameters.put("fileName",path);
			parameters.put("comments",comments);
			parameters.put("content",content.getBytes());
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "File System Service: could not post message: "+msg;
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the commit process on the Svn connector for file "+path 
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	
	public void branch(String path, String tag, String comments) throws com.amalto.core.util.XtentisException  {
		Connection conx = null;
		
		try {
			//Get Connection to the File System Connector
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap parameters = new HashMap();
			
			parameters.put("command","branch");
			parameters.put("path",path);
			parameters.put("comments",comments);
			parameters.put("tag",tag);
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "File System Service: could not post message: "+msg;
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the commit process on the Svn connector for file "+path 
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	
	public void delete(String path, String tag) throws com.amalto.core.util.XtentisException  {
		Connection conx = null;
		
		try {
			//Get Connection to the File System Connector
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap parameters = new HashMap();
			
			parameters.put("command","delete");
			parameters.put("path",path);
			parameters.put("tag",tag);
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "File System Service: could not post message: "+msg;
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the commit process on the Svn connector for file "+path 
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	
	public HistoryInfos getHistory(String path) throws com.amalto.core.util.XtentisException  {
		Connection conx = null;
		
		try {
			//Get Connection to the File System Connector
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap parameters = new HashMap();
			
			parameters.put("command","history");
			parameters.put("fileName",path);
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "Svn Service: could not post message: "+msg;
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
			HashMap<String, Object> paramsOut =  (HashMap<String, Object>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
			HistoryInfos history = (HistoryInfos)paramsOut.get("history");
			return history;
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the process on the Svn connector for path "+path 
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	
	public HistoryInfos getVersions(String path) throws com.amalto.core.util.XtentisException  {
		Connection conx = null;
		
		try {
			//Get Connection to the File System Connector
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap parameters = new HashMap();
			
			parameters.put("command","versions");
			parameters.put("fileName",path);
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "Svn Service: could not post message: "+msg;
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
			HashMap<String, Object> paramsOut =  (HashMap<String, Object>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
			HistoryInfos history = (HistoryInfos)paramsOut.get("versions");
			return history;
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the process on the Svn connector for path "+path 
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}
	
	
	public void start() throws com.amalto.core.util.XtentisException  {
   	org.apache.log4j.Logger.getLogger(this.getClass()).warn("start() ");
    	
    	Connection conx = null;
    	try {
			conx  = getConnection();
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			
			//stop the listener
//			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
//			try {interaction.execute(interactionSpec, recordIn); } catch (Exception e) {}

			//start the listener
			HashMap<Serializable,Serializable> parameters = new HashMap<Serializable, Serializable>();
			
			parameters.put("url", configuration.getUrl());
			parameters.put("username", configuration.getUsername());
			parameters.put("password", configuration.getPassword());
			
			org.apache.log4j.Logger.getLogger(this.getClass()).warn("configuration.getUrl() = "+configuration.getUrl());
			org.apache.log4j.Logger.getLogger(this.getClass()).warn("configuration.getUsername() = "+configuration.getUsername());
			org.apache.log4j.Logger.getLogger(this.getClass()).warn("configuration.getPassword() = "+configuration.getPassword());
			
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
			MappedRecord result = (MappedRecord)interaction.execute(interactionSpec, recordIn);
			
			//check the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String message = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "Svn Connector handler: could not start the handler: "+message;
				org.apache.log4j.Category.getInstance(this.getClass()).error("start() "+err);
				throw new XtentisException(err);
			}
    		
    	} catch (ResourceException re) {
    		throw new XtentisException(re.getMessage());
    	} catch (XtentisException xce) {
    		throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not start the Svn connector :"+ 
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
	}

	public void stop() throws com.amalto.core.util.XtentisException  {
	   	org.apache.log4j.Logger.getLogger(this.getClass()).debug("stop() ");
	    	
	    	Connection conx = null;
	    	try {
				conx  = getConnection();
				Interaction interaction = conx.createInteraction();
		    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
				MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
				
				//stop the listener
//				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
//				try {interaction.execute(interactionSpec, recordIn); } catch (Exception e) {}

				//start the listener
				HashMap<Serializable,Serializable> parameters = new HashMap<Serializable, Serializable>();
				
				parameters.put("url", configuration.getUrl());
				parameters.put("username", configuration.getUsername());
				parameters.put("password", configuration.getPassword());
				
		    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parameters);
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
				MappedRecord result = (MappedRecord)interaction.execute(interactionSpec, recordIn);
				
				//check the result
				if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
					String message = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
					String err = "Svn Connector handler: could not stop the handler: "+message;
					org.apache.log4j.Category.getInstance(this.getClass()).error("start() "+err);
					throw new XtentisException(err);
				}
	    		
	    	} catch (ResourceException re) {
	    		throw new XtentisException(re.getMessage());
	    	} catch (XtentisException xce) {
	    		throw(xce);
			} catch (Exception e) {
				e.printStackTrace();
				String err = "Could not start the Svn connector :"+ 
					e.getClass().getName()+": "+e.getLocalizedMessage();
				org.apache.log4j.Category.getInstance(SvnHandler.class).error(err);
				throw new XtentisException(e);
			} finally {
				try {conx.close();} catch (Exception e){}
			}
		}

	
}
