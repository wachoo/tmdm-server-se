package com.amalto.connector.mail;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

/**
 * @author jfeltesse
 *
 */
public class MailPart implements Serializable {
	
	
	private static final long serialVersionUID = -7036003406445794913L;
	
	private String mimeType;
	private String charset;
	private byte[] body;
	private String fileName;
	
	
	
	public MailPart() {
		
	}
	
	
	public MailPart(String mimeType, String charset, byte[] body) {
		this.mimeType = mimeType;
		this.charset = charset;
		this.body = body;
	}

	
	
	
	/**
	 * @return Returns the contentType.
	 */
	public String getMimeType() {
		if (mimeType == null || mimeType.equals("")) return Mail.DEFAULT_CONTENT_TYPE;
		return (mimeType.trim()).toLowerCase();
	}
	/**
	 * @return Returns the charset.
	 */
	public String getCharset() {
		if (charset == null || charset.equals("")) return Mail.DEFAULT_CHARSET;
		return charset.trim();
	}
	/**
	 * @return Returns the body as a byte array.
	 */
	public byte[] getBody() {
		return body;
	}
	/**
	 * @return Returns the body as a String
	 */
	public String getBodyAsString() {
		if (this.body == null) return "";
		
		String encoded = "";
		try { encoded = new String(this.getBody(), this.getCharset()).trim();	}
		catch (UnsupportedEncodingException ue) { /* */ }
		return encoded;
	}
	/**
	 * @return Returns true if the content is empty, false otherwise
	 */
	public boolean isEmpty() {
		return (this.getBodyAsString().length() == 0 ? true : false);
	}
	
	




	/**
	 * @param mimeType The mimeType to set.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	/**
	 * @param charset The charset to set.
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}
	/**
	 * @param contents The content to set.
	 */
	public void setBody(byte[] body) {
		this.body = body;
	}
	
	
	
	
	/**
	 * 
	 * @return a dump of the fields
	 */
	public String dump() {
		
		StringBuffer sb = new StringBuffer();
		sb.append("Content type: "+ getMimeType());
		sb.append(", charset: "+ getCharset());
		sb.append(", content :\n"+ getBodyAsString());
		
		return sb.toString();
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	
	
	
}
