package com.amalto.webapp.core.bean;

import java.io.Serializable;

public class FileSystemListenerConfiguration implements Serializable {
	
	public static final String ACTION_STOP ="STOP";
	public static final String ACTION_RUN ="RUN";
	
	private String id;
	private String sourceDirectory;
	private String processedDirectory;
	private String errorPostfix = "error";
	private String processingPostfix = "processing";
	private long period = 1000;
	private String pattern = ".*";
	private String logFilename="processing.log";
	private String filesEncoding = "UTF-8";
	private String contentType = "text/plain";
	private boolean directAccess = true;
	private String action = ACTION_STOP;
	private String status = "STOPPED";  //ERROR, OK/RUNNING
	private String transformer = null;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getErrorPostfix() {
		return errorPostfix;
	}
	public void setErrorPostfix(String errorPostfix) {
		this.errorPostfix = errorPostfix;
	}
	public long getPeriod() {
		return period;
	}
	public void setPeriod(long frequency) {
		this.period = frequency;
	}
	public String getLogFilename() {
		return logFilename;
	}
	public void setLogFilename(String logFilename) {
		this.logFilename = logFilename;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getProcessedDirectory() {
		return processedDirectory;
	}
	public void setProcessedDirectory(String processedDirectory) {
		this.processedDirectory = processedDirectory;
	}
	public String getProcessingPostfix() {
		return processingPostfix;
	}
	public void setProcessingPostfix(String processingPostfix) {
		this.processingPostfix = processingPostfix;
	}
	public String getSourceDirectory() {
		return sourceDirectory;
	}
	public void setSourceDirectory(String sourceDirectory) {
		this.sourceDirectory = sourceDirectory;
	}
	public boolean isDirectAccess() {
		return directAccess;
	}
	public void setDirectAccess(boolean directAccess) {
		this.directAccess = directAccess;
	}
	public String getFilesEncoding() {
		return filesEncoding;
	}
	public void setFilesEncoding(String filesEncoding) {
		this.filesEncoding = filesEncoding;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTransformer() {
		return transformer;
	}
	public void setTransformer(String transformer) {
		this.transformer = transformer;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	
	
	/**
	 * Parse a serialized configuration into an object
	 * @param marshalledConfiguration
	 * @return a {@link FileSystemListenerConfiguration}
	 * @throws XtentisException
	
	public static FileSystemListenerConfiguration parse(String marshalledConfiguration) throws XtentisException{
		try {
			Element root = Util.parse(marshalledConfiguration).getDocumentElement();
			return parse(root);			
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to parse the File System Listener configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }			
	}
	 */
	
	/**
	 * Parse a DOM configuration into an object
	 * @param root
	 * @return a {@link FileSystemListenerConfiguration}
	 * @throws XtentisException
	
	public static FileSystemListenerConfiguration parse(Node root) throws XtentisException{
		try {
			FileSystemListenerConfiguration conf = new FileSystemListenerConfiguration();
			conf.setId(Util.getFirstTextNode(root, "id"));
			conf.setAction(Util.getFirstTextNode(root, "action"));
			conf.setDirectAccess("true".equals(Util.getFirstTextNode(root, "directAccess")));
			conf.setErrorPostfix(Util.getFirstTextNode(root, "errorPostfix"));
			conf.setFilesEncoding(Util.getFirstTextNode(root, "filesEncoding"));
			conf.setContentType(Util.getFirstTextNode(root, "contentType"));
			conf.setLogFilename(Util.getFirstTextNode(root, "logFilename"));
			conf.setPattern(Util.getFirstTextNode(root, "pattern"));
			conf.setPeriod(Integer.parseInt(Util.getFirstTextNode(root, "period")));
			conf.setProcessedDirectory(Util.getFirstTextNode(root, "processedDirectory"));
			conf.setProcessingPostfix(Util.getFirstTextNode(root, "setProcessingPostfix"));
			conf.setSourceDirectory(Util.getFirstTextNode(root, "sourceDirectory"));
			conf.setStatus(Util.getFirstTextNode(root, "status"));
			conf.setTransformer(Util.getFirstTextNode(root, "transformer"));
			return conf;
	    } catch (Exception e) {
    	    String err = "Unable to parse the File System Listener configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }			
	}
 */
	
	/**
	 * Serializes this configuration to an XML (Castor failed)
	 * @return the marshalled configuration 
	 * @throws XtentisException
	
	public String serialize() throws XtentisException{
    	try {
    		String xml = "<FileSystemListenerConfiguration>";
    		xml+="<id>"+(id == null ? "" : StringEscapeUtils.escapeXml(id))+"</id>";
    		xml+="<sourceDirectory>"+(sourceDirectory == null ? "" : StringEscapeUtils.escapeXml(sourceDirectory))+"</sourceDirectory>";
    		xml+="<processedDirectory>"+(processedDirectory == null ? "" : StringEscapeUtils.escapeXml(processedDirectory))+"</processedDirectory>";
    		xml+="<errorPostfix>"+(errorPostfix == null ? "" : StringEscapeUtils.escapeXml(errorPostfix))+"</errorPostfix>";
    		xml+="<processingPostfix>"+(processingPostfix == null ? "" : StringEscapeUtils.escapeXml(processingPostfix))+"</processingPostfix>";
    		xml+="<period>"+StringEscapeUtils.escapeXml(""+period)+"</period>";
    		xml+="<pattern>"+(pattern == null ? "" : StringEscapeUtils.escapeXml(pattern))+"</pattern>";
    		xml+="<logFilename>"+(logFilename == null ? "" : StringEscapeUtils.escapeXml(logFilename))+"</logFilename>";
    		xml+="<filesEncoding>"+(filesEncoding == null ? "" : StringEscapeUtils.escapeXml(filesEncoding))+"</filesEncoding>";
    		xml+="<directAccess>"+(directAccess ? "true" : "false")+"</directAccess>";
    		xml+="<action>"+(action == null ? "" : StringEscapeUtils.escapeXml(action))+"</action>";
    		xml+="<status>"+(status == null ? "" : StringEscapeUtils.escapeXml(status))+"</status>";
    		xml+="<transformer>"+(transformer == null ? "" : StringEscapeUtils.escapeXml(transformer))+"</transformer>";
    		xml += "</FileSystemListenerConfiguration>";
   			return xml;
	    } catch (Exception e) {
    	    String err = "Unable to serialize the File System Listener  configuration."
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(FileSystemConfiguration.class).error(err);
    	    throw new XtentisException(err);
	    }			
	}
 */

	
	
}		
