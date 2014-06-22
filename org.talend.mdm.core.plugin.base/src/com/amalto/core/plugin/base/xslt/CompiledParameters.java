package com.amalto.core.plugin.base.xslt;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import net.sf.saxon.Configuration;
import net.sf.saxon.PreparedStylesheet;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.amalto.core.util.Util;

public class CompiledParameters implements Serializable {
	
	private static final long serialVersionUID = 2908739898798787L;
	
	private PreparedStylesheet preparedStyleSheet = null;
	private String outputMethod = null;
	/**
	 * Waiting for Prepared StyleSheet to work
	 * @deprecated
	 */
	private String xslt;
	
	//FIXME: Perpaerd Style Sheet do not work
	public PreparedStylesheet getPreparedStyleSheet() {
		return preparedStyleSheet;
	}
	public void setPreparedStyleSheet(PreparedStylesheet preparedStyleSheet) {
		this.preparedStyleSheet = preparedStyleSheet;
	}
	public String getOutputMethod() {
		return outputMethod;
	}
	public void setOutputMethod(String outputMethod) {
		this.outputMethod = outputMethod;
	}
	
	
	/**
	 * @deprecated - use Prepared StyleSheet
	 * @return the xslt
	 */
	public String getXslt() {
		return xslt;
	}
	/**
	 * @deprecated - use Prepared StyleSheet
	 * @param xslt
	 */
	public void setXslt(String xslt) {
		this.xslt = xslt;
	}
	
	
	public String serialize() throws IOException{
		String xml="<parameters>";
		xml+="<method>"+getOutputMethod()+"</method>";
		xml+="<xslt>"+StringEscapeUtils.escapeXml(xslt)+"</xslt>";
		//serialize PreparedSheet
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream ois = new ObjectOutputStream(bos);
		ois.writeObject(preparedStyleSheet);
		String base64= new BASE64Encoder().encode(bos.toByteArray());
		xml+="<sheet>"+base64+"</sheet>";
		xml+="</parameters>";
		return xml;
	}
	
	public static CompiledParameters deserialize(String xml) 
		throws IOException,
						ClassNotFoundException,
						TransformerException,
						ParserConfigurationException,
						SAXException
	{
		CompiledParameters parameters = new CompiledParameters();
		Element params = Util.parse(xml).getDocumentElement();
		
		parameters.setXslt(Util.getFirstTextNode(params, "xslt"));
		
		parameters.setOutputMethod(Util.getFirstTextNode(params, "method"));
		
//		FIXME: Saxon is Buggy
		//String base64 = Util.getFirstTextNode(params, "sheet");
		//byte[] bytes = new BASE64Decoder().decodeBuffer(base64); 
		//ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		//ObjectInputStream ois = new ObjectInputStream(bis);
		//PreparedStylesheet ss = PreparedStylesheet.loadCompiledStylesheet(conf, ois);
		//parameters.setPreparedStyleSheet(ss);
	
		return parameters;
	}
	
}


