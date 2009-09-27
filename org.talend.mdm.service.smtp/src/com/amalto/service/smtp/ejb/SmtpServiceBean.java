package com.amalto.service.smtp.ejb;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ServiceCtrlBean;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * <h1>Service SMTP</h1>
 *
 * <h3>Description</h3>
 * This service sends an email through the SMTP connector.<br/>
 *
 * <h3>Parameters</h3>
 * Parameters are set in the key/pair form separated by & e.g. <code>key1=value1&key2=value2</code>
 * <ul>
 * <li><b>logfilename</b>: optional; the full path of a log file that records the mails sent;</li>
 * <li><b>from</b>: mandatory; the email address of the sender</li>
 * <li><b>to</b>: mandatory; the email addresses, separated by commas, of the recipients</li>
 * <li><b>cc</b>: optional; the email addresses, separated by commas, of the copied recipients</li>
 * <li><b>bcc</b>: optional; the email addresses, separated by commas, of the blind copied recipients</li>
 * <li><b>subjectprefix</b>: optional; a sentence inserted at the beginning of the subject line</li>
 * <li><b>transformer</b>: optional; an optional transformer. When no transformer is supplied,
 * the item xml is used as the body of the mail. When a transformer is supplied, the following variables,
 * are extracted from the pipeline after the transformer is run:<ul>
 * 		<li><b>body</b>: mandatory; the body content of the email</li>
 * 		<li><b>recipients</b>: optional; if provided, a list of email addresses separated by commas that
 * 		will be added to those provided by the <code>to</code> parameter</li>
 * 		<li><b>subject</b>: optional; if provided, will be appended to the <code>subjectprefix</code> parameter
 * 		to for the subject line</li>
 * 		<li><b>files</b>: optional; a comma separated list of full path to files that will added in the email
 * 		as attachments</li>
 * 		</ul>
 * </li>
 * </ul>
 *
 * <h3>Configuration</h3>
 * The following parameters are set via the UI:<ul>
 * <li><b>host</b>: the smtp server host name</li>
 * <li><b>port</b>: the smtp server port</li>
 * <li><b>username</b>: optional; the smtp server username</li>
 * <li><b>password</b>: optional; the smtp server password</li>
 *</ul>
 *
 * @author Bruno Grieder
 *
 * @ejb.bean name="Smtp"
 *           display-name="Name for Smtp"
 *           description="Description for Smtp"
 * 		  local-jndi-name = "amalto/local/service/smtp"
 *           type="Stateless"
 *           view-type="local"
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
public class SmtpServiceBean extends ServiceCtrlBean  implements SessionBean {

	protected class MailInfos
	{
		public String logFileName = null;
		public String from = null;
		public String to = null;
		public String cc = null;
		public String bcc = null;
		public String subjectPrefix = null;
		public String mails = null;
		public String[] fileNames = null;


		public String transformerParameters = null;
	}

	private static final long serialVersionUID = 7146969238534906425L;

	private boolean configurationLoaded = false;
	private boolean serviceStarted = false;

	private String host;
	private Integer port;
	private String username;
	private String password;
	private Boolean auth;
	private String permanentbcc;
	private String logfilename;
	private String transformer;


    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/service/smtp";
	}

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {

		return "This service sends an email through the SMTP connector";
	}

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getStatus() throws XtentisException {
		// N/A
		return "N/A";
	}


    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void start() throws XtentisException {

		try {

			if (!configurationLoaded) getConfiguration(null);

			serviceStarted = true;

			return;

		}
		catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
    	    String err = "Could not start the Smtp service: "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
    	    throw new XtentisException(err);
	    }

	}



    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void stop() throws XtentisException {
		// N/A
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



	private void parseParametersPOSTFormat(MailInfos mailInfos, String parameters) {
		String kvs[] = parameters.split("&");
		if (kvs!=null) {
			for (int i = 0; i < kvs.length; i++) {
				String[] kv = kvs[i].split("=");
				String key = kv[0].trim().toLowerCase();

				if (("logfilename".equals(key)) && (kv.length == 2)) {
					mailInfos.logFileName = kv[1];
				} else
				if (("from".equals(key)) && (kv.length == 2)) {
					mailInfos.from = kv[1];
				} else
				if (("to".equals(key)) && (kv.length == 2)) {
					mailInfos.to = kv[1];
				} else
				if (("cc".equals(key)) && (kv.length == 2)) {
					mailInfos.cc = kv[1];
				} else
				if (("bcc".equals(key)) && (kv.length == 2)) {
					mailInfos.bcc = kv[1];
				} else
				if (("subjectprefix".equals(key)) && (kv.length == 2)) {
					mailInfos.subjectPrefix = kv[1];
				} else
				if (("transformer".equals(key)) && (kv.length == 2)) {
					transformer = kv[1];
				}
			}
		}
	}

	private void parseParametersXMLFormat(MailInfos mailInfos, String parameters) throws TransformerException {

		try {
			Element root = Util.parse(parameters).getDocumentElement();;
			NodeList nl = root.getElementsByTagName("from");
			if (nl.item(0)!=null) mailInfos.from = nl.item(0).getTextContent();

			nl = root.getElementsByTagName("to");
			if (nl.item(0)!=null) mailInfos.to = nl.item(0).getTextContent();

			nl = root.getElementsByTagName("logfilename");
			if (nl.item(0)!=null) mailInfos.logFileName = nl.item(0).getTextContent();

			nl = root.getElementsByTagName("cc");
			if (nl.item(0)!=null) mailInfos.cc = nl.item(0).getTextContent();

			nl = root.getElementsByTagName("bcc");
			if (nl.item(0)!=null) mailInfos.bcc = nl.item(0).getTextContent();

			nl = root.getElementsByTagName("transformer");
			if (nl.item(0)!=null)
			{
				mailInfos.transformerParameters = Util.nodeToString(nl.item(0));

				nl = nl.item(0).getOwnerDocument().getElementsByTagName("name");
				transformer = nl.item(0).getTextContent();
				//root = nl.item(0);

			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}

	private void parseParameters(MailInfos mailInfos, String parameters) throws TransformerException {
		if (parameters.trim().charAt(0) == '<')	{
			parseParametersXMLFormat(mailInfos, parameters);
			System.out.print(mailInfos.transformerParameters);
		} else {
			parseParametersPOSTFormat(mailInfos, parameters);
		}
	}

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters) throws com.amalto.core.util.XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("receiveFromInbound() : sending message...");
		boolean isLoggingEvent = "logging_event".equals(itemPK.getConceptName());

		//check things are initialized and loaded
		if (!serviceStarted) {
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("receiveFromInbound() : service not started. starting...");
			start();
		}
		if (!configurationLoaded) getConfiguration(null);
		Connection conx = null;

		try {

			MailInfos mailInfos = new MailInfos();


			// Parse parameters
			if (parameters != null) {
				parseParameters(mailInfos, parameters);
			}

			//permanent BCC
			if (permanentbcc != null) {
				if (mailInfos.bcc != null) mailInfos.bcc = mailInfos.bcc + ", " + permanentbcc;
				else mailInfos.bcc = permanentbcc;
			}

			//run transformer if any
			String recipients = null;
			String subject = null;
			String body;
			String contentType;
			if (transformer == null) {
				// Send generic messages with no transformation
				String msg = StringEscapeUtils.unescapeXml(
						Util.getItemCtrl2Local().getItem(itemPK).getProjectionAsString()
				);
				subject = (mailInfos.subjectPrefix == null ? "" : mailInfos.subjectPrefix);
				body = (new BASE64Encoder()).encode(msg.getBytes("UTF-8"));
				contentType = "text/plain";
			} else {
				String xml = Util.getItemCtrl2Local().getItem(itemPK).getProjectionAsString();
				TransformerContext ctx = new TransformerContext(new TransformerV2POJOPK(transformer));
				ctx = Util.getTransformerV2CtrlLocal().executeUntilDone(
						ctx,
						new com.amalto.core.objects.transformers.v2.util.TypedContent(
								xml.getBytes("UTF8"),
								"text/xml; charset=utf-8"
						)
				);
				recipients = ctx.getFromPipeline("recipients") == null ? null : new String(ctx.getFromPipeline("recipients").getContentBytes(),"UTF-8");
				subject = (ctx.getFromPipeline("subject") == null ? null : new String(ctx.getFromPipeline("subject").getContentBytes(),"UTF-8"));
				subject = ((mailInfos.subjectPrefix == null ? "" : mailInfos.subjectPrefix)+(subject == null ? "" : " "+subject)).trim();
				if (ctx.getFromPipeline("body")==null) {
					String err= "SMTP Service: the body of the mail must available in an ouptut variable called 'body' in transformer '"+transformer+"'";
					org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
					throw new XtentisException(err);
				}

				if (ctx.getFromPipeline("files")!=null)
				{
					mailInfos.fileNames = (new String(ctx.getFromPipeline("files").getContentBytes(),"UTF-8")).split("\\;");
				}

				body = (new BASE64Encoder()).encode(ctx.getFromPipeline("body").getContentBytes());
				contentType = ctx.getFromPipeline("body").getContentType().split(";")[0].toLowerCase();
			}

			//build e-mail
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<outboundmails><email>");
			addXmlElement(sb, "from", mailInfos.from, true);
			addXmlElement(sb, "to", (mailInfos.to == null ? "" : mailInfos.to)+(recipients == null ? "" : (mailInfos.to==null?"":",")+recipients), true);
			addXmlElement(sb, "cc", mailInfos.cc, true);
			addXmlElement(sb, "bcc", mailInfos.bcc, true);
			addXmlElement(sb, "subject",subject , true);
			sb.append("<part><mime-type>"+contentType+"</mime-type><charset>UTF-8</charset>");
			addXmlElement(sb, "body", body, false);
			sb.append("</part>");

			if (mailInfos.fileNames!=null)
			{
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("fileNames size = "+mailInfos.fileNames.length);
				for(int i=0; i<mailInfos.fileNames.length; i++)
				{
					mailInfos.fileNames[i] = mailInfos.fileNames[i].trim();
					if ("".equals(mailInfos.fileNames))
						continue;

					org.apache.log4j.Logger.getLogger(this.getClass()).debug("fileName "+i+" = "+mailInfos.fileNames[i]);

					File f = new File(mailInfos.fileNames[i]);
					if (f.exists())
					{
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("fileName "+i+" = "+mailInfos.fileNames[i]+" exists");
						String fileName = mailInfos.fileNames[i];

						InputStream fInStream = new FileInputStream(f);
						int size = (int)f.length();
						byte[] bytes = new byte[size];
						fInStream.read(bytes, 0, size);

						String shortName = fileName;
						int lastIndex = shortName.lastIndexOf("/");
						if (lastIndex!=-1 && lastIndex<shortName.length()-1)
							shortName = shortName.substring(lastIndex+1);

						String lowFileName = fileName.toLowerCase();
						String mimeType = "text/plain";

						if (lowFileName.endsWith(".pdf"))
							mimeType = "application/pdf";
						else if (lowFileName.endsWith(".xls"))
							mimeType = "application/vnd.ms-excel";
						else if (lowFileName.endsWith(".doc"))
							mimeType = "application/msword";
						else
							mimeType = "text/plain";

						sb.append("<part><mime-type>"+mimeType+"</mime-type><charset>UTF-8</charset><filename>" +StringEscapeUtils.escapeXml(shortName)+ "</filename>");
						body = (new BASE64Encoder()).encode(bytes);
						addXmlElement(sb, "body", body, false);
						sb.append("</part>");

					} else
						org.apache.log4j.Logger.getLogger(this.getClass()).error("Attachment with fileName "+mailInfos.fileNames[i]+" doesn't exist");
				}
			}


			sb.append("</email></outboundmails>");
			mailInfos.mails = sb.toString();

			//Get Connection to the Smtp Sender
			conx  = getConnection("java:jca/xtentis/connector/smtp");
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();

	    	//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
	    	HashMap<String,Serializable> params = new HashMap<String,Serializable>();
	    	params.put("host", host);
	    	params.put("port", port);
	    	params.put("username", username);
	    	params.put("password", password);
	    	params.put("mails", mailInfos.mails);
	    	params.put("auth", auth);
	    	params.put("logfilename", mailInfos.logFileName);
	    	recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);

	    	//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);

			String statusCode = (String) result.get(RecordFactoryImpl.STATUS_CODE_OUT);
			String statusMsg = (String)((HashMap<String,Serializable>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");

			//parse the result
			if (!"OK".equals(statusCode)) {
				String err = "Smtp Service: could not post message: "+statusMsg;
				org.apache.log4j.Logger.getLogger(this.getClass()).debug(err);
				throw new XtentisException(err);
			} else {
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("receiveFromInbound() : status="+statusCode+", message="+statusMsg);
				// Remove the logging Event if it's been sent successfully
				if (isLoggingEvent) {
					Util.getItemCtrl2Local().deleteItem(itemPK);
					org.apache.log4j.Logger.getLogger(this.getClass()).debug("receiveFromInbound() : removed itemPK '"+itemPK.getIds()[0]+"'");
				}
			}

			String response = (new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss, SSS")).format(new Date(System.currentTimeMillis()))
			+": SUCCESS routing to SMTP Service.";

			return response;


		} catch (Exception e) {
			e.printStackTrace();
			String msg =
				(new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss, SSS")).format(new Date(System.currentTimeMillis()))
				+": ERROR routing to SMTP Service "
				+": "+e.getLocalizedMessage();

			if (! isLoggingEvent) {
				if (e instanceof XtentisException) {
					throw new XtentisException(e.getLocalizedMessage());
				} else {
					org.apache.log4j.Logger.getLogger(this.getClass()).error(msg+" ("+e.getClass().getName()+")");
					throw new XtentisException("Smtp Service Error: "+e.getClass().getName()+": "+e.getLocalizedMessage());
				}
			}

			return "Error occured trying to handle logging_event with Smtp service.";

	    } finally {
			try {conx.close();} catch (Exception e) { }
	    }

	}
    /**
     * Returns the XML schema for the configuration<br>
     * Can be null
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getConfigurationSchema() throws XtentisException{
		return
		"<xsd:schema" +
		" 		elementFormDefault='unqualified'" +
		"		xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
		">" +
		"<xsd:element name='configuration'>" +
		"			<xsd:complexType >" +
		"				<xsd:sequence>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='host' type='xsd:string'/>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='port' type='xsd:string'/>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='username' type='xsd:string'/>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='password' type='xsd:string'/>" +
		"					<xsd:element minOccurs='0' maxOccurs='1' nillable='false' name='permanentbcc' type='xsd:string'/>" +
		"				</xsd:sequence>" +
		"			</xsd:complexType>" +
		"</xsd:element>"+
		"</xsd:schema>";
    }	
	/**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String sendSimpleMail(String from,String to,String cc,String bcc,String subject,String body) throws XtentisException{
    	
    	String returnStatusYes = "Success";
		String returnStatusNo="Failure";
		Connection conx = null;
		
		try {
			//process parameters
			if(from==null||from.length()==0)return returnStatusNo;
			if(to==null||to.length()==0)return returnStatusNo;
			if(cc==null)cc="";
			if(bcc==null)bcc="";
			if(subject==null)subject="";
			if(body==null)body="";
			
			//check things are initialized and loaded
			if (!serviceStarted) {
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("receiveFromInbound() : service not started. starting...");
				start();
			}
			if (!configurationLoaded) getConfiguration(null);
			
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("host:"+host);
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("port:"+port);
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("username:"+username);
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("password:"+password);
			
			//build simple mail
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?> "); 
			sb.append(" "); 
			sb.append("<outboundmails> "); 
			sb.append(" "); 
			sb.append("	<email> "); 
			sb.append("		<from>").append(from.trim()).append("</from> "); 
			sb.append("		<to>").append(to.trim()).append("</to> "); 
			sb.append("		<cc>").append(cc.trim()).append("</cc> ");
			sb.append("		<bcc>").append(bcc.trim()).append("</bcc> ");
			sb.append("		<subject>").append(subject).append("</subject> ");
			sb.append("		<part> "); 
			sb.append("		<mime-type>text/plain</mime-type> "); 
			sb.append("		<charset>UTF-8</charset> "); 
			sb.append("		<body>").append((new BASE64Encoder()).encode(body.getBytes("UTF-8"))).append("</body> "); 
			sb.append("		</part> "); 
			sb.append("</email> "); 
			sb.append(" "); 
			sb.append("</outboundmails> ");
			
			
			//Get Connection to the Smtp Sender
			conx  = getConnection("java:jca/xtentis/connector/smtp");
			Interaction interaction = conx.createInteraction();
			InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
			
			//Create the Record
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			HashMap<String,Serializable> params = new HashMap<String,Serializable>();
			params.put("host", host);
			params.put("port", port);
			params.put("username", username);
			params.put("password", password);
			params.put("mails", sb.toString());
			params.put("auth", auth);
			params.put("logfilename", null);
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, params);

			//Process the post
			interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			MappedRecord result = (MappedRecord) interaction.execute(interactionSpec, recordIn);

			String statusCode = (String) result.get(RecordFactoryImpl.STATUS_CODE_OUT);
			String statusMsg = (String)((HashMap<String,Serializable>)result.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT)).get("message");
			
			if (!"OK".equals(statusCode)) {
				return returnStatusNo;
			}
			
			return returnStatusYes;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return returnStatusNo;
		} catch (NotSupportedException e) {
			e.printStackTrace();
			return returnStatusNo;
		} catch (ResourceException e) {
			e.printStackTrace();
			return returnStatusNo;
		}finally{
			if(conx!=null)
				try {
					conx.close();
				} catch (ResourceException e) {
					e.printStackTrace();
				}
		}
		

	}
    
    /**
    *
    * @get the default configuration
    * @throws EJBException
    *
    * @ejb.interface-method view-type = "local"
    * @ejb.facade-method
    */
    public String getDefaultConfiguration() {
    	return
    		"<configuration>"+
    		"	<host>localhost</host>"+
    		"	<port>25</port>"+
    		"	<username></username>"+
    		"	<password></password>"+
    		"	<permanentbcc></permanentbcc>"+
    		"	<transformer></transformer>"+
    		"	<logfilename></logfilename>"+
			"</configuration>";
    }
    /**
     * @author achen
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "There are two type parameters,\n\n" +
    			"One(Example) :\n"+
    			"from=b2box@customer.com&to=aiming_chen@hotmail.com&subjectprefix=MDM Logging Event" +"\n\n\n"+
    			"Two(Example) :\n"+
		"<parameters>\n"+
		"	<from>b2box@customer.com</from>\n"+
		"	<to>aiming_chen@hotmail.com</to>\n"+
		"	<cc></cc>\n"+
		"	<bcc></bcc>\n"+
		"	<logFileName></logFileName>\n"+
		"	<subjectprefix></transformer>\n"+
		"	<logfilename></logfilename>\n"+
		"	<transformer></transformer>\n"+
		"</parameters>\n";
    }
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getConfiguration(String optionalParameters) throws XtentisException {
    	try {
    		String configuration = loadConfiguration();
    		if (configuration == null) {
    			org.apache.log4j.Logger.getLogger(this.getClass()).debug("getConfiguration() : configuration is null, fall back to default one");
    			configuration = getDefaultConfiguration();
    		}

    		Document d = Util.parse(configuration);

    		// Parsing & checking of mandatory parameters
    		String tmphost = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "host"));
    		if  (tmphost == null) throw new XtentisException("Host required");
    		else this.host = tmphost;

    		String tmpport = Util.getFirstTextNode(d.getDocumentElement(), "port");
    		if  (tmpport == null) throw new XtentisException("Port number required");
    		else this.port = new Integer(tmpport);
    		if (this.port.intValue() < 1) throw new XtentisException("Invalid port number");


    		// If username is null then authentication is set to false
    		String usertmp = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "username"));
    		if (usertmp == null) auth = new Boolean(false);
    		else auth = new Boolean(true);
    		username = usertmp;

    		// Parsing of not so important parameters
    		transformer = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "transformer"));
    		password = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "password"));
    		permanentbcc = Util.getFirstTextNode(d.getDocumentElement(), "permanentbcc");
    		logfilename = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(), "logfilename"));

    		configurationLoaded = true;

    		//org.apache.log4j.Logger.getLogger(this.getClass()).debug("getConfiguration() : Configuration String: "+configuration);
    		org.apache.log4j.Logger.getLogger(this.getClass()).debug("getConfiguration() : Variables: host="+host+", port="+port+", "+
    				"username="+username+", password="+(password == null ? "null" : "(hidden)")+", permanentbcc="+permanentbcc+", logfilename="+logfilename);

    		return configuration;
        }
    	catch (XtentisException e) { throw (e); }
        catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the Smtp Service: "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }



    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void putConfiguration(String configuration) throws XtentisException {
		configurationLoaded = false;
		super.putConfiguration(configuration);
	}






	private void addXmlElement(StringBuffer target, String name, String value, boolean escapeXML) {

		if (value == null) value = "";
		else if (escapeXML) value = StringEscapeUtils.escapeXml(value);

		target.append("<"+name+">"+value+"</"+name+">");
	}
	
	/**
	 * @throws EJBException
	 *
	 * @ejb.interface-method view-type = "local"
	 * @ejb.facade-method
	 */
	 public Serializable fetchFromOutbound(String command, String parameters,String schedulePlanID) throws XtentisException {
			// N/A
			return null;
	 }



}