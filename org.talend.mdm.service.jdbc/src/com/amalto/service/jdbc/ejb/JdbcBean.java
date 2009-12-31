package com.amalto.service.jdbc.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.naming.InitialContext;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.talend.mdm.commmon.util.core.ITransformerConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ServiceCtrlBean;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.service.jdbc.bean.JdbcInfos;

/**
 * @author Starkey Shu
 * 
 * @ejb.bean 	name="Jdbc"
 *           	display-name="Name for Jdbc"
 *           	description="Description for Jdbc"
 * 		  		local-jndi-name = "amalto/local/service/jdbc"
 *           	type="Stateless"
 *           	view-type="local"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 * 
 * 
 */
public class JdbcBean extends ServiceCtrlBean  implements SessionBean{

	private static final long serialVersionUID = 5379672088510852086L;
	
	private final static String JDBC_JCA_JNDI= "java:jca/xtentis/connector/jdbc";

    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/service/jdbc";
	}
	
    /**
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getConfiguration(String optionalParameters) throws XtentisException{
    	return "";
    }

    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "This service handle the basic functionality of jdbc";
		return "This service handle the basic functionality of jdbc";
	}

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "There are two type parameters\n\n" +
		"One(Example) :\n"+
		"driverClassName=com.mysql.jdbc.Driver&url=jdbc:mysql://localhost:3306/brick&username=root&password=null&transformer=item2actionform" +"\n\n\n"+
		"Two(Example) :\n"+
		"<parameters>\n"+
		"	<driverClassName>com.mysql.jdbc.Driver</driverClassName>\n"+
		"	<url>jdbc:mysql://localhost:3306/brick</url>\n"+
		"	<username>root</username>\n"+
		"	<password>null</password>\n"+
		"	<transformer>item2actionform</transformer>\n"+
		"</parameters>\n\n\n"+
		"Notice :\n"+
    	"The transformer should expect to receive the content of the Item sent to the transformer in the DEFAULT variable \n"+
    	"with a content-type of text/xml.\nThe transformer convert Item to an ActionContent Xml Form of JDBC Adpter.\n";
    	
    }

    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getStatus() throws XtentisException {
		return "N/A"; 
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void start() throws XtentisException {
		return;
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void stop() throws XtentisException {
		return;
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public Serializable receiveFromOutbound(HashMap<String, Serializable> map) throws XtentisException {
		// N/A
		return null;
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters) throws XtentisException {
		try {
			
			//parse parameters
			JdbcInfos jdbcInfos =new JdbcInfos();
			if(parameters!=null&&parameters.length()>0){
				parseParameters(jdbcInfos,parameters);
			}
			if (jdbcInfos.getDriverClassName() == null || "".equals(jdbcInfos.getDriverClassName())) {
            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service Jdbc - mandatory parameter driverClassName is missing");
            	throw new XtentisException("Service Jdbc - mandatory parameter driverClassName is missing");
    		}
			if (jdbcInfos.getUrl() == null || "".equals(jdbcInfos.getUrl())) {
            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service Jdbc - mandatory parameter url is missing");
            	throw new XtentisException("Service Jdbc - mandatory parameter url is missing");
    		}
			if (jdbcInfos.getTransformer() == null || "".equals(jdbcInfos.getTransformer())) {
            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service Jdbc - mandatory parameter transformer name is missing");
            	throw new XtentisException("Service Jdbc - mandatory parameter transformer name is missing");
    		}
			
			// do transform
			String actionContent="";
			String itemXml = Util.getItemCtrl2Local().getItem(itemPK).getProjectionAsString();
			
			TransformerV2CtrlLocal tctrl = Util.getTransformerV2CtrlLocal();

			if (tctrl.existsTransformer(new TransformerV2POJOPK(jdbcInfos.getTransformer())) == null) {
            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service Jdbc is unable to call transformer "+jdbcInfos.getTransformer()+" - transformer doesn't exist");
            	throw new XtentisException("Unable to find the transformer "+jdbcInfos.getTransformer());
    		}

			ItemCtrl2Local ictrl  = Util.getItemCtrl2Local();
			ItemPOJO pojo = ictrl.getItem(itemPK);

			TransformerContext context = new TransformerContext(new TransformerV2POJOPK(jdbcInfos.getTransformer()));
			context.putInPipeline("_DEFAULT_", new TypedContent(itemXml.getBytes("UTF-8"),"text/xml"));

			TransformerContext contextResult =tctrl.executeUntilDone(context);
			if(contextResult.getFromPipeline(ITransformerConstants.VARIABLE_OUTPUT_TO_JDBCSERVICE)!=null){
				actionContent = new String(contextResult.getFromPipeline(ITransformerConstants.VARIABLE_OUTPUT_TO_JDBCSERVICE).getContentBytes(),"UTF-8");
			}
			
			// call push method of JDBC Adpter
			HashMap<String,Serializable> paramMap = new HashMap<String,Serializable>();
			paramMap.put("driverClassName", jdbcInfos.getDriverClassName());
			paramMap.put("url", jdbcInfos.getUrl());
			paramMap.put("username", jdbcInfos.getUsername());
			paramMap.put("password", jdbcInfos.getPassword());
			paramMap.put("actionContent", actionContent);
			
			Connection conx=null;   
            try {
				//Get Connection
				conx = getConnection(JDBC_JCA_JNDI);
				Interaction interaction = conx.createInteraction();
				InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
				//Create the Record
				MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
				recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN,paramMap);
				//Process the post
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
				MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
				String statusCode = (String) result.get(RecordFactoryImpl.STATUS_CODE_OUT);
				if (!"OK".equals(statusCode)) {
					String err = "JDBC Service: could not handle update of jdbc adpter ";
					org.apache.log4j.Logger.getLogger(this.getClass()).debug(err);
					throw new XtentisException(err);
				} 
			} catch (Exception e) {
				String err = "Unable to process this item through JCA: "+e.getClass().getName()+": "+e.getLocalizedMessage();
	            org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
			}finally {
				try {
					if(conx!=null)conx.close();
					} 
				catch (Exception e) {}
		    }
			
    	} catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to do update via jdbc adpter"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }
	    return "Update via jdbc adpter successfully";
	}
	
	private void parseParameters(JdbcInfos jdbcInfos, String parameters) throws TransformerException {
		if (parameters.trim().charAt(0) == '<')	{
			parseParametersXMLFormat(jdbcInfos, parameters);
		} else {
			parseParametersPOSTFormat(jdbcInfos, parameters);
		}
	}
	
	private void parseParametersXMLFormat(JdbcInfos jdbcInfos, String parameters) throws TransformerException {

		try {
			
			Element root = Util.parse(parameters).getDocumentElement();
			String driverClassName=Util.getFirstTextNode(root, "//driverClassName");
			String url=Util.getFirstTextNode(root, "//url");
			String username=Util.getFirstTextNode(root, "//username");
			String password=Util.getFirstTextNode(root, "//password");
			String transformer=Util.getFirstTextNode(root, "//transformer");
			
			jdbcInfos.setDriverClassName(driverClassName);
			jdbcInfos.setUrl(url);
			jdbcInfos.setUsername(username);
			jdbcInfos.setPassword(password);
			jdbcInfos.setTransformer(transformer);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	private void parseParametersPOSTFormat(JdbcInfos jdbcInfos, String parameters) {
		String kvs[] = parameters.split("&");
		if (kvs!=null) {
			for (int i = 0; i < kvs.length; i++) {
				String[] kv = kvs[i].split("=");
				String key = kv[0].trim().toLowerCase();

				if (("driverClassName".equals(key)) && (kv.length == 2)) {
					jdbcInfos.setDriverClassName(kv[1]);
				} else
				if (("url".equals(key)) && (kv.length == 2)) {
					jdbcInfos.setUrl(kv[1]);
				} else
				if (("username".equals(key)) && (kv.length == 2)) {
					jdbcInfos.setUsername(kv[1]);
				} else
				if (("password".equals(key)) && (kv.length == 2)) {
					jdbcInfos.setPassword(kv[1]);
				} else
				if (("transformer".equals(key)) && (kv.length == 2)) {
					jdbcInfos.setTransformer(kv[1]);
				}
			}
		}
	}
	
	/**
	 * @throws EJBException
	 *
	 * @ejb.interface-method view-type = "local"
	 * @ejb.facade-method
	 */
	 public Serializable fetchFromOutbound(String command, String parameters,String schedulePlanID) throws XtentisException {
		  
		 try {
			  
			    //parse parameters
			    Document paradoc=Util.parse(parameters);
			    JdbcInfos jdbcInfos = parseBasicConnectInfos(paradoc);
			    if (jdbcInfos.getDriverClassName() == null || "".equals(jdbcInfos.getDriverClassName())) {
	            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service Jdbc - mandatory parameter driverClassName is missing");
	            	throw new XtentisException("Service Jdbc - mandatory parameter driverClassName is missing");
	    		}
				if (jdbcInfos.getUrl() == null || "".equals(jdbcInfos.getUrl())) {
	            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service Jdbc - mandatory parameter url is missing");
	            	throw new XtentisException("Service Jdbc - mandatory parameter url is missing");
	    		}
				//do commands
				if (command.equals("select"))	{
					String selectSql=Util.getFirstTextNode(paradoc, "//selectSql");
					return select(jdbcInfos,selectSql);
				} else if (command.equals("desc table")) {
					String tableName=Util.getFirstTextNode(paradoc, "//tableName");
					return descTable(jdbcInfos,tableName);
				} else if (command.equals("show tables")) {
					return showTables(jdbcInfos);
				} 
			 
		    }catch (XtentisException e) {
	    		throw (e);
		    } catch (Exception e) {
	    	    String err = "Unable to do pull via jdbc adpter"+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
	    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
	    	    throw new XtentisException(err);
		    }
		    
		    return "";
		    
	 }

	private JdbcInfos parseBasicConnectInfos(Document paradoc) {
		
		String driverClassName="";
		String url="";
		String username="";
		String password="";
		try {
			
			driverClassName=Util.getFirstTextNode(paradoc, "//driverClassName");
			url=Util.getFirstTextNode(paradoc, "//url");
			username=Util.getFirstTextNode(paradoc, "//username");
			password=Util.getFirstTextNode(paradoc, "//password");
			
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		return new JdbcInfos(driverClassName,url,username,password);
		
	}

	private Serializable select(JdbcInfos jdbcInfos,String selectSql) throws XtentisException {
		String rs="";
		
		HashMap parametersMap = new HashMap();
		parametersMap.put("command","select");
		
		parametersMap.put("driverClassName",jdbcInfos.getDriverClassName());
		parametersMap.put("url",jdbcInfos.getUrl());
		parametersMap.put("username",jdbcInfos.getUsername());
		parametersMap.put("password",jdbcInfos.getPassword());
		
		parametersMap.put("selectSql",selectSql);
		
		rs = pullFromJdbcAdpter(parametersMap);
		
		return rs;
	}
	
	private Serializable descTable(JdbcInfos jdbcInfos,String tableName) throws XtentisException {
		String rs="";
		
		HashMap parametersMap = new HashMap();
		parametersMap.put("command","desc table");
		
		parametersMap.put("driverClassName",jdbcInfos.getDriverClassName());
		parametersMap.put("url",jdbcInfos.getUrl());
		parametersMap.put("username",jdbcInfos.getUsername());
		parametersMap.put("password",jdbcInfos.getPassword());
		
		parametersMap.put("tableName",tableName);
		
		rs = pullFromJdbcAdpter(parametersMap);
		
		return rs;
	}
	
	private Serializable showTables(JdbcInfos jdbcInfos) throws XtentisException {
        String rs="";
		
		HashMap parametersMap = new HashMap();
		parametersMap.put("command","show tables");
		
		parametersMap.put("driverClassName",jdbcInfos.getDriverClassName());
		parametersMap.put("url",jdbcInfos.getUrl());
		parametersMap.put("username",jdbcInfos.getUsername());
		parametersMap.put("password",jdbcInfos.getPassword());
		
		rs = pullFromJdbcAdpter(parametersMap);
		
		return rs;

	}
	
	private String pullFromJdbcAdpter(HashMap parametersMap)
			throws XtentisException {
		String rs="";
		Connection conx = null;
		try {
			//Get Connection to the JDBC Connector
			conx   =  ((ConnectionFactory)(new InitialContext()).lookup(JDBC_JCA_JNDI)).getConnection();
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, parametersMap);
			
			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);
			
			//parse the result
			if (!"OK".equals(result.get(RecordFactoryImpl.STATUS_CODE_OUT))) {
				String msg = (String)((HashMap)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
				String err = "JDBC Service: could not pull via jdbc adpter: "+msg;
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);	
				throw new XtentisException(err);
			}
			HashMap<String, Object> paramsOut =  (HashMap<String, Object>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
			rs = (String)paramsOut.get("resultSet");
			
		} catch (ResourceException re) {
			throw new XtentisException(re.getMessage());
		} catch (XtentisException xce) {
			throw(xce);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not complete the pull process on the JDBC connector "+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
			throw new XtentisException(e);
		} finally {
			try {conx.close();} catch (Exception e){}
		}
		return rs;
	}	

    
}