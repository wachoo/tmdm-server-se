package com.amalto.connector.mail;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.amalto.connector.jca.XtentisConnectorException;


/**
 * @author jfeltesse
 *
 */
public class InboundMail extends Mail implements Serializable {
	
	
	private static final long serialVersionUID = 5205404625711350814L;
	
	
	private String username;
	private String password;
	private String functionType;
	private String functionName;
	private HashMap<String,String> queryParams;
	
	
	
	/**
	 * Default constructor
	 */
	public InboundMail() {
		queryParams = new HashMap<String, String>();
		parts = new ArrayList();
	}
	
	
	/**
	 * Creates an inbound mail object from parameters
	 * 
	 * @param username
	 * @param password
	 * @param functionType
	 * @param functionName
	 * @param queryParams
	 */
	public InboundMail(
			String username,
			String password,
			String functionType,
			String functionName,
			HashMap<String,String> queryParams
			) {
		this.username = username;
		this.password = password;
		this.functionType = functionType;
		this.functionName = functionName;
		this.queryParams = queryParams;
		parts = new ArrayList();
	}
	
	
	
	
	/**
	 * @return Returns the functionName.
	 */
	public String getFunctionName() {
		return (functionName == null ? "" : functionName);
	}
	/**
	 * @return Returns the functionType.
	 */
	public String getFunctionType() {
		return (functionType == null ? "" : functionType);
	}
	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return (password == null ? "" : password);
	}
	/**
	 * @return Returns the queryParams.
	 */
	public HashMap getQueryParams() {
		return queryParams;
	}
	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return (username == null ? "" : username);
	}
	
	
	/**
	 * @param functionName The functionName to set.
	 */
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	/**
	 * @param functionType The functionType to set.
	 */
	public void setFunctionType(String functionType) {
		this.functionType = functionType;
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @param queryParams The queryParams to set.
	 */
	public void setQueryParams(HashMap<String,String> queryParams) {
		this.queryParams = queryParams;
	}
	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
	/**
	 * 
	 * @return a dump of the fields except password
	 */
	public String dump() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("Function type: "+ this.functionType);
		sb.append(", function name: "+ this.functionName);
		sb.append(", username: "+ this.username);
		
		if (this.queryParams.size() > 0) {
			sb.append("\nQuery params: ");
			Iterator iter = this.queryParams.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String) iter.next();
				String value = (String) this.getQueryParams().get(key);
				sb.append(key + "=" + value + " ");
			}
		}
		
		return sb.toString();
	}
	
	
	
	
	/**
	 * Serialization
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		
		out.writeChars("<inbound_mail>");
		
		out.writeChars("<username>"+StringEscapeUtils.escapeXml(getUsername())+"</username>");
		out.writeChars("<password>"+StringEscapeUtils.escapeXml(getPassword())+"</password>");
		out.writeChars("<function_type>"+StringEscapeUtils.escapeXml(getFunctionType())+"</function_type>");
		out.writeChars("<function_name>"+StringEscapeUtils.escapeXml(getFunctionName())+"</function_name>");
		
		if (this.queryParams.size() > 0) {
			Iterator iter = this.queryParams.keySet().iterator();
			while (iter.hasNext()) {
				out.writeChars("<parameter>");
				String key = (String) iter.next();
				String value = (String) this.getQueryParams().get(key);
				out.writeChars("<name>"+StringEscapeUtils.escapeXml(key)+"</name>");
				out.writeChars("<value>"+StringEscapeUtils.escapeXml(value)+"</value>");
				out.writeChars("</parameter>");
			}
		}
		
		for (int i = 0; i < super.getNbParts(); i++) {
			MailPart part = super.getPart(i);
			out.writeChars("<part>");
			out.writeChars("<mime-type>"+StringEscapeUtils.escapeXml(part.getMimeType())+"</mime-type>");
			out.writeChars("<charset>"+StringEscapeUtils.escapeXml(part.getCharset())+"</charset>");
			out.writeChars("<body>"+(part.getBody() == null ? "" : (new BASE64Encoder()).encode(part.getBody()))+"</body>");
			out.writeChars("</part>");
		}
		
		out.writeChars("</inbound_mail>");
		
	}
	
	
	/**
	 * Deserialization
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		try {
			Document d = Util.parse(in);
			
			this.username = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./username"));
			this.password = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./password"));
			this.functionType = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./function_type"));
			this.functionName = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./function_name"));
			
			NodeList params = Util.getNodeList(d.getDocumentElement(),"./parameter");
			for (int i = 0; i < params.getLength(); i++) {
				Element partElement = (Element) params.item(i);
				String name = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(partElement,"./name"));
				String value = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(partElement,"./value"));
				if (name != null) {
					if (value == null) value = "";
					this.queryParams.put(name, value);
				}
			}
			
			NodeList parts = Util.getNodeList(d.getDocumentElement(),"./part");
			for (int i = 0; i < parts.getLength(); i++) {
				Element partElement = (Element) parts.item(i);
				String content = Util.getFirstTextNode(partElement,"./body");
				addPart(new MailPart(
						StringEscapeUtils.unescapeXml(Util.getFirstTextNode(partElement,"./mime-type")),
						StringEscapeUtils.unescapeXml(Util.getFirstTextNode(partElement,"./charset")),
						(content == null ? null : (new BASE64Decoder()).decodeBuffer(content))
						));
			}
			
		}
		catch (XtentisConnectorException xmle) {
			throw new IOException(xmle.getLocalizedMessage());
		}
		catch (Exception e) {
			throw new IOException(e.getLocalizedMessage());
		}
	}
	
	
}
