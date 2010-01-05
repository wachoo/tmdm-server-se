package com.amalto.connector.mail;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

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
public class OutboundMail extends Mail implements Serializable {
	
	
	private static final long serialVersionUID = -642527381014332425L;
	
	
	private String from;
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	
	
	
	
	/**
	 * Default constructor
	 */
	public OutboundMail() {
		parts = new ArrayList();
	}
	
	
	/**
	 * Creates an outbound mail object from parameters
	 * 
	 * @param from
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 */
	public OutboundMail(
			String from,
			String to,
			String cc,
			String bcc, 
			String subject
			) {
		this.from = from;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		parts = new ArrayList();
	}
	
	
	
	/**
	 * @return Returns the bcc.
	 */
	public String getBcc() {
		return (bcc == null ? "" : bcc.trim());
	}
	/**
	 * @return Returns the cc.
	 */
	public String getCc() {
		return (cc == null ? "" : cc.trim());
	}
	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return (from == null ? "" : from.trim());
	}
	/**
	 * @return Returns the subject.
	 */
	public String getSubject() {
		return (subject == null ? "" : subject.trim());
	}
	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		return (to == null ? "" : to.trim());
	}
	
		
	/**
	 * @param bcc The bcc to set.
	 */
	public void setBcc(String bcc) {
		this.bcc = bcc;
	}
	/**
	 * @param cc The cc to set.
	 */
	public void setCc(String cc) {
		this.cc = cc;
	}
	/**
	 * @param from The from to set.
	 */
	public void setFrom(String from) {
		this.from = from;
	}
	/**
	 * @param subject The subject to set.
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @param to The to to set.
	 */
	public void setTo(String to) {
		this.to = to;
	}
	
	
	
	
	/**
	 * 
	 * @return a dump of the fields except password
	 */
	public String dump() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("From: "+ this.from);
		sb.append(", to: "+ this.to);
		sb.append(", cc: "+ this.cc);
		sb.append(", bcc: "+ this.bcc);
		sb.append(", subject: "+ this.subject);
		
		return sb.toString();
	}
	
	
	
	
	/**
	 * Serialization
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		
		out.writeChars("<email>");
		
		out.writeChars("<from>"+StringEscapeUtils.escapeXml(getFrom())+"</from>");
		out.writeChars("<to>"+StringEscapeUtils.escapeXml(getTo())+"</to>");
		out.writeChars("<cc>"+StringEscapeUtils.escapeXml(getCc())+"</cc>");
		out.writeChars("<bcc>"+StringEscapeUtils.escapeXml(getBcc())+"</bcc>");
		out.writeChars("<subject>"+StringEscapeUtils.escapeXml(getSubject())+"</subject>");
		
		for (int i = 0; i < super.getNbParts(); i++) {
			MailPart part = super.getPart(i);
			out.writeChars("<part>");
			out.writeChars("<mime-type>"+StringEscapeUtils.escapeXml(part.getMimeType())+"</mime-type>");
			out.writeChars("<charset>"+StringEscapeUtils.escapeXml(part.getCharset())+"</charset>");
			out.writeChars("<body>"+(part.getBody() == null ? "" : (new BASE64Encoder()).encode(part.getBody()))+"</body>");
			out.writeChars("</part>");
		}
		
		out.writeChars("</email>");
		
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
			
			this.from = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./from"));
			this.to = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./to"));
			this.cc = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./cc"));
			this.bcc = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./bcc"));
			this.subject = StringEscapeUtils.unescapeXml(Util.getFirstTextNode(d.getDocumentElement(),"./subject"));
			
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
