package com.amalto.connector.smtp.eis;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;

import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.mail.InvalidXMLValueException;
import com.amalto.connector.mail.Mail;
import com.amalto.connector.mail.MailPart;
import com.amalto.connector.mail.OutboundMail;
import com.amalto.connector.mail.Util;
import com.amalto.connector.util.Version;

/**
 * 
 * @author jfeltesse
 */
public class SmtpSender implements XAResource {
	
	//TODO LogFile usage : when/what to log ?
	
	private static final String CONNECTION_TIMEOUT = "60000"; // ms, Time out at connection
	private static final String SOCKET_TIMEOUT = "120000"; // ms, Time out while processing
	
	private static final String PROVIDER = "smtp";
	private static final String XMAILER = "Amalto_b2box";	
	
	// Connection infos
	private String host;
	private int port;
	private String username;
	private String password;
	private boolean auth;
	private String logFileName;
	
	private Transport transport = null;
	private Session session = null;
	
	private String statusOutCode;
	private String statusOutText;
	
//	private String logHeader = "";
	
	
	
	/**
	 * A SmtpSender
	 * 
	 * @param host Smtp server host
	 * @param port Stmp server port
	 * @param username User name used to login
	 * @param password Password used to login
	 * @param auth Authenticated connection true/false
	 * @param logFileName The file to log to
	 */
	public SmtpSender(
			String host,
			int port,
			String username,
			String password,
			boolean auth,
			String logFileName
			) {
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.auth = auth;
		this.logFileName = logFileName;
	}
	
	
	
	/**
	 * Connects to the smtp server
	 * 
	 * @param tr Transport to connect
	 * @throws MessagingException, AuthenticationFailedException
	 */
	public void connect(Transport tr) throws MessagingException, AuthenticationFailedException {
		
		if (tr != null) {
			if (!tr.isConnected()) {
				if (auth) tr.connect(host, username, password);
				else tr.connect();
				if (tr.isConnected()) org.apache.log4j.Logger.getLogger(this.getClass()).debug(
						"connect() : Connected to host '"+host+"' on port "+port);
				else throw new MessagingException("connect() : Connection to host '"+host+"' on port "+port+" failed.");
			}
			else {
				if (tr.isConnected()) org.apache.log4j.Logger.getLogger(this.getClass()).debug(
						"connect() : Already connected to host '"+host+"' on port "+port);	
			}
		}
		else {
			throw new MessagingException("connect() : the transport object is null");
		}
	}
	
	
	
	/**
	 * Disonnects
	 * 
	 * @param tr Transport to disconnect
	 * @throws Exception
	 */
	public void disconnect(Transport tr) throws Exception {
		
		if (tr != null && tr.isConnected()) tr.close();
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("disconnect() : Disconnected.");
	}
	
	
	
	/**
	 * Set properties according to the parameters given at init to create a JavaMail Transport object
	 * 
	 * @return Returns a transport object
	 * @throws Exception
	 */
	private Transport setProperties() throws NoSuchProviderException {
		
		Properties props = System.getProperties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", String.valueOf(port));
		props.put("mail.smtp.connectiontimeout", CONNECTION_TIMEOUT);
		props.put("mail.smtp.timeout", SOCKET_TIMEOUT);
		if (auth) props.put("mail.smtp.auth", "true");
		// props.put("mail.debug", "true");
		
		session = Session.getInstance(props);
		
		Transport t = session.getTransport(PROVIDER);
		return t;		
	}
	
	
	
	/**
	 * Creates a JavaMail message
	 * 
	 * @param om
	 * @return a Message in javax.mail format
	 * @throws Exception
	 */
	private Message createMessage(OutboundMail om) throws MessagingException {
		
		MimeMessage msg = new MimeMessage(session);
		
		
		
//		org.apache.log4j.Logger.getLogger(this.getClass()).debug("createMessage() : "+om.getNbParts()+" parts");
		
		if (om.getNbParts() > 1) {	// Multipart message
			
			Multipart mp = new MimeMultipart();
			
			for (int i=0; i < om.getNbParts(); i++) {
				
				MailPart part = om.getPart(i);
				MimeBodyPart mbp = new MimeBodyPart();
				
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("createMessage() : part "+i+" dump > "+part.dump());
				
				if (part.getFileName()!=null) mbp.setFileName(part.getFileName());
				
				if (part.getMimeType().startsWith("text"))
					mbp.setContent(part.getBodyAsString(), part.getMimeType() + "; charset="+ part.getCharset()+"; format=flowed");
				else	// Binary data
				{
					mbp.setDataHandler(new DataHandler(new ByteArrayDataSource(part.getBody(), part.getMimeType())));
				}
				mp.addBodyPart(mbp);
				
			}
			msg.setContent(mp);
			
		}
		else {	// single part message
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("single part message ");
			
			MailPart part = om.getPart(0);
			if (part.getMimeType().startsWith("text"))
				msg.setContent(part.getBodyAsString(), part.getMimeType() + "; charset="+ part.getCharset()+"; format=flowed");
			else if (part.getMimeType().startsWith("multipart")) {
				
				ByteArrayInputStream bais = new ByteArrayInputStream(part.getBody());
				
				msg = new MimeMessage(null, bais);
			} else // Binary data
			{
				msg.setDataHandler(new DataHandler(new ByteArrayDataSource(part.getBody(), part.getMimeType())));
			}
		}
		
//		 From & To have already been checked if we make it until here
		msg.setFrom(new InternetAddress(om.getFrom(), false));
		msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(om.getTo(), false));
		
		if (om.getCc().length() > 0) msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(om.getCc(), false));
		if (om.getBcc().length() > 0) msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(om.getBcc(), false));
		
		if (om.getSubject().length() > 0) msg.setSubject(om.getSubject().replaceAll(System.getProperty("line.separator"), " "), Mail.DEFAULT_CHARSET);
		msg.setHeader("X-Mailer", XMAILER);
		msg.setSentDate(new Date());
		
		
		msg.saveChanges();
		
		return msg;
	}
	
	
	
	
	
	/**
	 * Pushes/pulls jdbc statements according to a given xml String
	 * 
	 * @param xml
	 * @throws XtentisConnectorException
	 */
	public void processMails(String xml) throws XtentisConnectorException {
		
		// System.out.println( xml );
		
//		Logger.getLogger(this.getClass()).debug("processMails() : xml="+xml);
		
		try {
			
			OutboundMail[] emails = parseXMLMails(xml);
			if (emails.length == 0) throw new XtentisConnectorException("No emails to send");
			
			transport = setProperties();
			connect(transport);
			
			for (int i = 0; i < emails.length; i++) {
//				Logger.getLogger(this.getClass()).debug("processMails() : email "+i);
				Message m = createMessage(emails[i]);
				transport.sendMessage(m, m.getAllRecipients());
			}
			
			// No exception thrown until here, we assume the mails were sent successfully
			this.statusOutCode = "OK";
        	this.statusOutText = emails.length+" email(s) sent successfully to host "+this.host;
			
		}
		catch (XtentisConnectorException e) { // Thrown by parseXMLMails()
			// Parsing of the xml failed. Other mails might be ok so,
			// we handle the exception here, returning an ERROR status.
			handleException("processMails() : Parsing of the xml failed", e);
		}
		catch (InvalidXMLValueException e) { // Thrown by parseXMLMails()
			// Parsing of the xml was fine but some illegal values were detected (unallowed empty fields etc.). Other mails might be ok so,
			// we handle the exception here, returning an ERROR status.
			handleException("processMails() : Invalid values in source xml", e);
		}
		catch (NoSuchProviderException e) { // Thrown by setProperties()
			// If this error if thrown then it means there's not the provider we wanted in the application classpath.
			// If there's not fix done then every call to processMails() will throw the same exception so,
			// we throw it upwards to make the application fail on purpose.
			handleException("processMails() : Can't find the provider \""+PROVIDER+"\"", e);
			throw new XtentisConnectorException(e);
		}
		catch (AuthenticationFailedException e) { // Thrown by connect() if the authentication fails
			handleException("processMails() : Authentication failed", e);
		}
		catch (MessagingException e) { // Thrown by connect(), createMessage()
			// Typical mail error. Can't establish connection, or something in the mail object given to JavaMail is fishy etc..
			// Recoverable exception that depends on the smtp server, the network or the mail that might not occur later or with other mails so,
			// we handle the exception here, returning an ERROR status.
			handleException("processMails() : Error while processing mails", e);
		}
		catch (Exception e) {
			handleException("processMails() : Unhandled Exception, propagating upwards", e);
			throw new XtentisConnectorException(e);
		}
		finally {
			try { disconnect(transport); }
			catch (Exception e) { throw new XtentisConnectorException("disconnection error", e); }
		}
	}
	
	
	
	
	private void handleException(String msg, Throwable t) {
		Logger.getLogger(this.getClass()).debug(msg, t);
		this.statusOutCode = "ERROR";
    	this.statusOutText = msg+": "+t.getClass()+" : "+t.getMessage();
	}
	
	
	
	
	/**
	 * Creates an array of OutboundMail objects from an xml String
	 * 
	 * @param xml
	 * @return the array of outbound mails
	 * @throws Exception
	 */
	private OutboundMail[] parseXMLMails(String xml) throws XtentisConnectorException, InvalidXMLValueException  {
		
		OutboundMail[] outboundmails = null;
				
		Document d = Util.parse(xml);
		
		NodeList emails = Util.getNodeList(d.getDocumentElement(),"./email");
		int nbEmails = emails.getLength();
//		Logger.getLogger(this.getClass()).debug("parseXMLMails() : nbEmails="+nbEmails);
		outboundmails = new OutboundMail[nbEmails];
		
		for (int i = 0; i < nbEmails; i++) {
//			Logger.getLogger(this.getClass()).debug("parseXMLMails() : for nbEmails loop, i="+i);
			Element em = (Element) emails.item(i);
			OutboundMail om = new OutboundMail(
					StringEscapeUtils.unescapeXml(Util.getFirstTextNode(em,"./from")),
					StringEscapeUtils.unescapeXml(Util.getFirstTextNode(em,"./to")),
					StringEscapeUtils.unescapeXml(Util.getFirstTextNode(em,"./cc")),
					StringEscapeUtils.unescapeXml(Util.getFirstTextNode(em,"./bcc")),
					StringEscapeUtils.unescapeXml(Util.getFirstTextNode(em,"./subject"))
			);
//			Logger.getLogger(this.getClass()).debug("parseXMLMails() : email n°"+ (i < 10 ? " " : "")+i+" dump > "+om.dump());
			
			if ( (om.getFrom().length() == 0) ||  (om.getTo().length() == 0))
				throw new InvalidXMLValueException("Mail n°"+i+" is not valid : Both FROM and TO fields are required.");
			
			NodeList parts = Util.getNodeList(em,"./part");
			for (int j = 0; j < parts.getLength(); j++) {
//				Logger.getLogger(this.getClass()).debug("parseXMLMails() : nbParts loop, j="+j);
				Element partElement = (Element) parts.item(j);
				String body = Util.getFirstTextNode(partElement,"./body");
				byte[] bodybytes = null;
				try { if (body != null) bodybytes = (new BASE64Decoder()).decodeBuffer(body); }
				catch (IOException ioe) { throw new InvalidXMLValueException("Couldn't decode the body of the part n°"+j+" in mail n°"+i);	}
				
				MailPart mp = new MailPart(
						StringEscapeUtils.unescapeXml(Util.getFirstTextNode(partElement,"./mime-type")),
						StringEscapeUtils.unescapeXml(Util.getFirstTextNode(partElement,"./charset")),
						bodybytes
						); 
				
				if ( Util.getFirstTextNode(partElement,"./filename") != null )
				{
					mp.setFileName(Util.getFirstTextNode(partElement, "./filename"));
				}
				
				om.addPart(mp);
			}
			outboundmails[i] = om;
//			Logger.getLogger(this.getClass()).debug("parseXMLMails() : dump outbound mail["+i+"] : "+om.dump());
		}
		
		return outboundmails;
	}
	
	
	
	/**
	 * Tests the smtp host : connects, disconnects
	 * 
	 * @throws XtentisConnectorException
	 */
	public void testConnection() throws XtentisConnectorException {
		
		Transport testtransport = null;
		
		try {
			testtransport = setProperties();
			connect(testtransport);
			Logger.getLogger(this.getClass()).debug("testConnection() : Success");
		}
		catch (NoSuchProviderException npe) { // Thrown by setProperties()
			throw new XtentisConnectorException("testConnection() : Can't find the provider \""+PROVIDER+"\"", npe);
		}
		catch (Exception e) {
			throw new XtentisConnectorException("testConnection() : failure", e);
		}
		finally {
			try { disconnect(testtransport); }
			catch (Exception e) { throw new XtentisConnectorException("testSmtp() : disconnection error", e); }
		}
		
	}
	
	
	
	
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return Version.getVersion(SmtpSender.class);
	}
	/**
	 * @return Returns the transport.
	 */
	public Transport getTransport() {
		return transport;
	}
	
	public String getStatusOutCode() {
		return statusOutCode;
	}
	
	public String getStatusOutText() {
		return statusOutText;
	}
	
	
	
	/**
	 * Writes to the log file specified at init. 
	 * 
	 * @param message
	 */
	public synchronized void writeToLog(String message) {
		try {
			FileOutputStream fos = null;
			if (this.logFileName!=null) { 
				fos= new FileOutputStream(new File(this.logFileName),true);
				fos.write(message.getBytes("UTF-8"));
			}
		} catch (Exception e) {
			Logger.getLogger(this.getClass()).debug(
					"writeToLog() Could not write to Log "+this.logFileName+":"+e.getLocalizedMessage());
		}
	}
	
	
	
	
	
	
	
	
	/****************************************************************************************
	 * 
	 *  XA Stuff
	 *  
	 ****************************************************************************************/
	
	public void commit(Xid arg0, boolean arg1) throws XAException { }
	
	public void end(Xid arg0, int arg1) throws XAException { }
	
	public void forget(Xid arg0) throws XAException { }
	
	public int getTransactionTimeout() throws XAException { return 0; }
	
	public boolean isSameRM(XAResource xares) throws XAException { return (xares == this); }
	
	public int prepare(Xid arg0) throws XAException { return XAResource.XA_OK; }
	
	public Xid[] recover(int arg0) throws XAException { return new Xid[0]; }
	
	public void rollback(Xid arg0) throws XAException { }
	
	public boolean setTransactionTimeout(int arg0) throws XAException { return false; }
	
	public void start(Xid arg0, int arg1) throws XAException { }
	
	
	
	
	
	
	//TODO Get rid of this class and use JavaMail 1.4's new class ?
	
	
	private class ByteArrayDataSource implements DataSource {
		
		private byte[] data;
		private String contentType;
		
		/**
		 * Datasource from a byte array
		 * 
		 * @param data the byte array of data
		 * @param contentType the MIME content type of the data
		 */
		public ByteArrayDataSource(byte[] data, String contentType) {
			this.data = data;
			this.contentType = contentType;
		}
		
		/**
		 * @return an InputStream representing the data
		 */
		public InputStream getInputStream() throws IOException {
			if (data == null) throw new IOException("No data");
			return new ByteArrayInputStream(data);
		}
		
		/**
		 * @return an OutputStream where the data can be written
		 */
		public OutputStream getOutputStream() throws IOException {
			throw new IOException("Not implemented yet");
		}
		
		/**
		 * @return the MIME type of the data
		 */
		public String getContentType() {
			return contentType;
		}
		
		/**
		 * @return the name of this object
		 */
		public String getName() {
			return "";
		}
		
	}
	
	
	
	
	
}
