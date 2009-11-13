/*
 * Created on 22 sept. 2005
 *
 */
package com.amalto.webapp.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.Stub;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.security.Base64Encoder;
import org.jboss.security.SimpleGroup;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sun.misc.BASE64Decoder;

import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.webapp.util.webservices.WSBase64KeyValue;
import com.amalto.webapp.util.webservices.WSConnectorResponseCode;
import com.amalto.webapp.util.webservices.WSGetUniverse;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSUniverse;
import com.amalto.webapp.util.webservices.WSUniversePK;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.XtentisPort;
import com.amalto.webapp.util.webservices.XtentisService_Impl;
import com.amalto.xmlserver.interfaces.IXmlServerEBJLifeCycle;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.XmlServerException;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;




/**
 * @author bgrieder
 */
public class Util {
	
	private static String endpoint_address = "http://localhost:8080/talend/TalendPort";
	
	public static int _AUTO_ = 0;
	public static int _FORCE_RMI_ = 1;
	public static int _FORCE_WEB_SERVICE_ = 2;
	
	/*********************************************************************
	 *      WEB SERVICES
	 *********************************************************************/
	
	
    public static XtentisPort getPort() throws XtentisWebappException {
    	AjaxSubject as;
    	try {
    		as = Util.getAjaxSubject();
    	} catch (Exception e) {
    		throw new XtentisWebappException("Unable to access the logged user data");
    	}
    	if (as==null) throw new XtentisWebappException("Session Expired");
//    	org.apache.log4j.Category.getInstance(Util.class).debug("getPort() ");
    	String[] mdm = as.getMDMData();
    	String url = "http://"+mdm[0]+"/talend/TalendPort";
		return Util.getPort(url, mdm[1], mdm[2]);
	}
	
	public static XtentisPort getPort(String username, String password) throws XtentisWebappException{
		return getPort(endpoint_address,username,password,_AUTO_);
	}
	
	public static XtentisPort getPort(String endpointAddress, String username, String password) throws XtentisWebappException{
		return getPort(endpointAddress,username,password,_AUTO_);
	}
	
	public static XtentisPort getPort(String endpointAddress, String username, String password, int force) throws XtentisWebappException{
		
		if (force == _FORCE_RMI_) return getRMIEndPoint();
		if (force == _FORCE_WEB_SERVICE_) return getWSPort(endpointAddress, username, password);
		
		//Auto
		if (endpointAddress.contains("localhost"))
			return getRMIEndPoint();
		
		return getWSPort(endpointAddress, username, password);

	}
	
	public static XtentisPort getPort(String username, String password, int force) throws XtentisWebappException{
		return getPort(endpoint_address,username,password,force);
	}

	private static XtentisPort getWSPort(String endpointAddress, String username, String password) throws XtentisWebappException{
		try {
			Stub stub = (Stub) (new XtentisService_Impl()).getXtentisPort();
			stub._setProperty(
					Stub.ENDPOINT_ADDRESS_PROPERTY,
	        		endpointAddress);
	        stub._setProperty(
	                Stub.USERNAME_PROPERTY,
	                  username);
	        stub._setProperty(
	                Stub.PASSWORD_PROPERTY,
	                  password);
			
			return (XtentisPort)stub;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisWebappException(
					"Unable to access endpoint at: "+endpointAddress
					+": "+e.getLocalizedMessage());
		}
	}
	
	
	private static XtentisPort getRMIEndPoint() throws XtentisWebappException{
		
		return new XtentisRMIPort();

        
	}
	
	/*********************************************************************
	 *      LOCAL FILE UTILS
	 *********************************************************************/
	
	public static String getXML(Class<?> c, String filename) throws Exception{
	    BufferedReader in = null;
        in = new BufferedReader(
        			new InputStreamReader(
        			        c.getResourceAsStream(filename)
        			)
        );
	
	    String xml="";
        String line;
        while ((line=in.readLine())!=null) xml+=line+"\n";
        return xml;
    }    
	
	public static String getPackageFilePath(Class<?> c, String filename) {
		return c.getResource(filename).getPath();
	}

	
	/*********************************************************************
	 *     NODE UTILS
	 *********************************************************************/
	
	/**
	 * Join an array of strings into a single string using a separator
	 * @param strings
	 * @param separator
	 * @return a single string or null
	 */
	public static String joinStrings(String[] strings, String separator) {
		if (strings == null) return null;
		String res = "";
		for (int i = 0; i < strings.length; i++) {
			res+= (i>0) ? separator : "";
			res+=strings[i];
		}
		return res;
	}
	
	/**
	 * Returns the first part - eg. the concept - from the path
	 * @param path
	 * @return
	 * 		the Concept Name
	 */
    public static String getConceptFromPath(String path) {
    	Pattern p = Pattern.compile("(.*?)[\\[|/].*");
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = p.matcher(path);
    	if (m.matches()) 
    		return m.group(1);
   		return null;
    }
    
    public static String getForeignPathFromPath(String path){
    	int pos= path.indexOf("[");    	    	
    	if(pos != -1){
    		return path.substring(0,pos);
    	}
    	return path;
    	
    }
    
    
    public static WSWhereCondition getConditionFromPath(String path) {
    	Pattern p = Pattern.compile("(.*?)\\[(.*?)(&=|!=|>=|<=|>|<|=])(.*?)\\].*");
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = p.matcher(path);
    	if (m.matches()){
    		WSWhereCondition wc=new WSWhereCondition();
    		wc.setLeftPath(m.group(2).trim());
    		com.amalto.webapp.util.webservices.WSWhereOperator operator = changeToWSOperator(m.group(3));
    		wc.setOperator(operator);
    		wc.setRightValueOrPath(m.group(4).trim().replaceAll("'|\"", ""));
    		wc.setSpellCheck(true);
    		wc.setStringPredicate(WSStringPredicate.NONE);
    		return wc;
    	}
   		return null;
    }
    
    public static com.amalto.webapp.util.webservices.WSWhereOperator changeToWSOperator(String operator){
    	if("=".equals(operator))return com.amalto.webapp.util.webservices.WSWhereOperator.EQUALS;
    	if("!=".equals(operator))return com.amalto.webapp.util.webservices.WSWhereOperator.NOT_EQUALS;
    	if("<".equals(operator))return com.amalto.webapp.util.webservices.WSWhereOperator.LOWER_THAN;
    	if("<=".equals(operator))return com.amalto.webapp.util.webservices.WSWhereOperator.LOWER_THAN_OR_EQUAL;
    	if(">".equals(operator))return com.amalto.webapp.util.webservices.WSWhereOperator.GREATER_THAN;
    	if(">=".equals(operator))return com.amalto.webapp.util.webservices.WSWhereOperator.GREATER_THAN_OR_EQUAL;
    	if("&=".equals(operator))return com.amalto.webapp.util.webservices.WSWhereOperator.CONTAINS;
    	return null;
    }
    
	/**
	 * Generates an xml string from a node 
	 * (not pretty formatted)
	 * @param n the node
	 * @return the xml string
	 * @throws Exception
	 */
	public static String nodeToString(Node n) throws Exception{
       	StringWriter sw = new StringWriter();
       	Transformer transformer = TransformerFactory.newInstance().newTransformer();
       	transformer.setOutputProperty("omit-xml-declaration","yes");
       	transformer.setOutputProperty("indent","yes");
       	transformer.transform(
				new DOMSource(n),
				new StreamResult(sw)
				);
		return sw.toString();
	}

	/**
	 * Get a nodelist from an xPath
	 * @throws Exception
	 */
	public static NodeList getNodeList(Document d, String xPath) throws Exception{
		return getNodeList(d.getDocumentElement(),xPath,null,null);
	}
	/**
	 * Get a nodelist from an xPath
	 * @throws Exception
	 */
	public static NodeList getNodeList(Node contextNode, String xPath) throws Exception{
		return getNodeList(contextNode,xPath,null,null);
	}
	
	/**
	 * Get a nodelist from an xPath
	 * @throws Exception
	 */
	public static NodeList getNodeList(Node contextNode, String xPath, String namespace, String prefix) throws Exception{
	    XObject xo = XPathAPI.eval(
    		contextNode,
			xPath,
			(namespace == null) ? contextNode : Util.getRootElement("nsholder",namespace,prefix)
	    );
	    if (xo.getType() != XObject.CLASS_NODESET) return null;
	    return xo.nodelist();
	}
	
    /**
	 * Returns a namespaced root element of a document
	 * Useful to create a namespace holder element
	 * @param namespace
	 * @return the root Element
	 */
	public static Element getRootElement(String elementName, String namespace, String prefix) throws Exception{
	 	Element rootNS=null;
    	try {
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setNamespaceAware(true);   
	        DocumentBuilder builder = factory.newDocumentBuilder();
	    	DOMImplementation impl = builder.getDOMImplementation();
	    	Document namespaceHolder = impl.createDocument(namespace,(prefix==null?"":prefix+":")+elementName, null);    
	    	rootNS = namespaceHolder.getDocumentElement();
	    	rootNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:"+prefix, namespace);
    	} catch (Exception e) {
    	    String err="Error creating a namespace holder document: "+e.getLocalizedMessage();
    	    throw new Exception(err);    
    	}
    	return rootNS;
	}

    public static Document parse(String xmlString) throws Exception{
    	return parse(xmlString,null);
    }
    	
    public static Document parse(String xmlString, String schema) throws Exception{

		//parse
		Document d=null;
		SAXErrorHandler seh = new SAXErrorHandler();

		try {
	        //initialize the sax parser which uses Xerces
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			//Schema validation based on schemaURL
			factory.setNamespaceAware(true);
			factory.setValidating((schema!=null));
			factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			if (schema != null) {
			    factory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaSource",
					new InputSource(new StringReader(schema))
					);
			}
			DocumentBuilder builder;
			builder = factory.newDocumentBuilder();
			builder.setErrorHandler(seh);
			d = builder.parse(new InputSource(new StringReader(xmlString)));
		} catch (Exception e) {
			String err = "Unable to parse the document"
					+": " + e.getClass().getName() + ": "
					+ e.getLocalizedMessage()+
					"\n "+xmlString;
			throw new Exception(err);
		}
		
		//check if dcument parsed correctly against the schema
		if (schema != null) {
			String errors = seh.getErrors();
			if (!errors.equals("")) {
				String err = "Document  did not parse against schema: \n" + errors+"\n"+xmlString;
				throw new Exception(err);
			}
		}
		return d;
    }
    
    
    public static String[] getTextNodes(Node contextNode, String xPath) throws XtentisWebappException{
    	return getTextNodes(contextNode,xPath,contextNode);
    }
    
    public static String[] getTextNodes(Node contextNode, String xPath,Node namespaceNode) throws XtentisWebappException{
        String[] results=null;;
        
        //test for hard-coded values
        if (xPath.startsWith("\"") && xPath.endsWith("\""))
            return new String[] { xPath.substring(1, xPath.length()-1)};
            
        //test for incomplete path (elements missing /text())
        if (!xPath.matches(".*@[^/\\]]+"))  //attribute
        	if (! xPath.endsWith(")")) //function
        		xPath+="/text()";
               
        try {
	        XObject xo = XPathAPI.eval(contextNode, xPath,namespaceNode);
	        if (xo.getType() == XObject.CLASS_NODESET) {
	            NodeList l = xo.nodelist();
	            int len = l.getLength();
	            results = new String[len];
	            for (int i = 0; i < len; i++) {
	                Node n = l.item(i);
	                results[i] = n.getNodeValue();
	            }
	        } else {
	            results = new String[]{xo.toString()};
	        }
		} catch (Exception e) {
			String err = "Unable to get the text node(s) of "+xPath
					+": " + e.getClass().getName() + ": "
					+ e.getLocalizedMessage();
			throw new XtentisWebappException(err);
		}
		return results;

    }    
    
    
    public static String getFirstTextNode(Node contextNode, String xPath,Node namespaceNode) throws XtentisWebappException{
    	String[] res = getTextNodes(contextNode,xPath,namespaceNode);
    	if (res.length == 0) 
    		return null;
    	return res[0]; 
    }

    
    /**
     * Get the first text node matching the Xpath
     * @param contextNode
     * @param xPath
     * @return the String or null if not found
     * @throws XtentisWebappException
     */
    public static String getFirstTextNode(Node contextNode, String xPath) throws XtentisWebappException{
    	return getFirstTextNode(contextNode,xPath,contextNode);
    }
    
    
	/*********************************************************************
	 *      JACC - JAAS
	 *********************************************************************/	
    public static AjaxSubject getAjaxSubject() throws PolicyContextException{
//    	Retrieve the subject
		String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
		return new AjaxSubject((Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY));
    }
    
    public static String getPrincipalMember(String key) throws Exception{
    	String result="";
    	// Get the Authenticated Subject

        Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");

        // Now look for a Group 

        Set principals = subject.getPrincipals(Principal.class);

        Iterator iter = principals.iterator();

        while(iter.hasNext())

		{

		  Principal p = (Principal)iter.next();
		  if(p instanceof SimpleGroup)
		  {

             SimpleGroup sg = (SimpleGroup)p;

             if(key.equals(sg.getName()))

             {

                Enumeration en = sg.members();

                while(en.hasMoreElements())

                {

					String info = en.nextElement().toString();
					
					result=result+","+info;

                }

             }

          }

        }
        
        if(result.length()>0)result=result.substring(1);
    	return result;
    }
    
    public static String getLoginUserName()throws Exception {
		return getPrincipalMember("Username");

	}
    
    public static String getLoginUniverse() throws Exception{
		return getPrincipalMember("Universe");

	}
    
    public static String getLoginRoles() throws Exception{
		return getPrincipalMember("Roles");

	}
    
    public static String getRevisionIdFromUniverse(String universeName,String conceptName) throws Exception{
    	String revisonId="";
		WSUniverse wsUniverse=Util.getPort().getUniverse(new WSGetUniverse(new WSUniversePK(universeName)));
		UniversePOJO universe=XConverter.WS2POJO(wsUniverse);
		revisonId=universe.getConceptRevisionID(conceptName);
		return revisonId;
	}
    
    public static Element getLoginProvisioningFromDB() throws Exception,XmlServerException {
			IXmlServerSLWrapper server=null;
			//get the DB implementation class
			String serverClass = MDMConfiguration.getConfiguration().getProperty("xmlserver.class");
			if ((serverClass==null) || "".equals(serverClass)) serverClass = "com.amalto.xmldb.XmldbSLWrapper";
			
			//instantiate the DB implementation class
			//we cannot user ObjectPOJO.load since it will try to check our authentication
			try {
			    server = (IXmlServerSLWrapper) Class.forName(serverClass).newInstance();
			    if (server instanceof IXmlServerEBJLifeCycle) {
			    	((IXmlServerEBJLifeCycle)server).doCreate();
			    }
			} catch (Throwable t) {
				String err = "Unable to start the XMLDB driver "+serverClass+": "+t.getMessage();
				throw new IllegalArgumentException(err);
			}
			String provisioningCluster  = "PROVISIONING";
			String userConcept = "User";
			String username = Util.getLoginUserName();
			String userString = server.getDocumentAsString(
					null, //head
					provisioningCluster, 
					provisioningCluster+"."+userConcept+"."+username
				);
			Element user  = (Element)Util.getNodeList(Util.parse(userString), "//"+userConcept).item(0);
			return user;
    }

	/*********************************************************************
	 *      PASSWORD UTILS
	 *********************************************************************/	
	
	 /**
	 * Computes an md5 hash of a string.
	 * 
	 * @param text
	 *           the hashed string
	 * @return the string hash
	 * @exception NullPointerException
	 *               if text is null
	 */
	public static byte[] md5(String text, String charset) {
		// arguments check
		if (text == null) {
			throw new NullPointerException("null text");
		}

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes(charset));
			return md.digest();
		} catch (NoSuchAlgorithmException e) {
			//log.error("Cannot find MD5 algorithm", e);
			throw new RuntimeException("Cannot find MD5 algorithm");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("No such encoding: "+charset);
		}
	}

	/**
	 * Computes an md5 hash and returns the result as a string in hexadecimal
	 * format.
	 * 
	 * @param text the hashed string
	 * @return the string hash
	 * @exception NullPointerException if text is null
	 */
	public static String md5AsHexString(String text, String charset) {
		return toHexString(md5(text,charset));
	}

	/**
	 * Returns a string in the hexadecimal format.
	 * 
	 * @param bytes the converted bytes
	 * @return the hexadecimal string representing the bytes data
	 * @throws IllegalArgumentException if the byte array is null
	 */
	public static String toHexString(byte[] bytes) {
		if (bytes == null) {
			throw new IllegalArgumentException("byte array must not be null");
		}
		StringBuffer hex = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			hex.append(Character.forDigit((bytes[i] & 0XF0) >> 4, 16));
			hex.append(Character.forDigit((bytes[i] & 0X0F), 16));
		}
		return hex.toString();
	}
	
	/*********************************************************************
	 *      WEB SERVICES
	 *********************************************************************/
	
	public static HashMap<String,Object> getMapFromKeyValues(WSBase64KeyValue[] params) throws RemoteException{
		try {
	    	HashMap<String,Object> map = new HashMap<String, Object>();
	    	if (params != null) {
				for (int i = 0; i < params.length; i++) {
					if (params[i]!=null) {
						String key = params[i].getKey();
						byte[] bytes = (new BASE64Decoder()).decodeBuffer(params[i].getBase64StringValue());
						if (bytes!=null) {
							ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
							ObjectInputStream ois = new ObjectInputStream(bais);
							map.put(key, ois.readObject());
						} else {
							map.put(key, null);
						}
					}
				}
	    	}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}    	
    }
	    
    public static WSBase64KeyValue[] getKeyValuesFromMap(HashMap<String,Object> params) throws RemoteException{    	
    	try {
    		if (params==null) return null;
    		WSBase64KeyValue[] keyValues = new WSBase64KeyValue[params.size()];
    		Set<String> keys = params.keySet();
    		int i=0;
    		for (Iterator<String> iter = keys.iterator(); iter.hasNext(); ) {
				String key = iter.next();
				Object value = params.get(key);
				if (value!=null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(value);
					String base64Value = Base64Encoder.encode(baos.toByteArray());
					keyValues[i] = new WSBase64KeyValue();
					keyValues[i].setKey(key);
					keyValues[i].setBase64StringValue(base64Value);
					i++;
				}
			}
			return keyValues;
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}    	
    }

	
	public static String getCodeFromWSConnectorResponseCode(WSConnectorResponseCode code) {
		if (code.equals(WSConnectorResponseCode.OK)) return "OK";
		if (code.equals(WSConnectorResponseCode.STOPPED)) return "STOPPED";
		return "ERROR";
	}
	
	
	/*********************************************************************
	 *      VERSIONING
	 *********************************************************************/
	
	
	private static final String PROP_FILE = "/version.properties";

	/**
	 * Returns <code>String</code> representation of package version
	 * information.
	 */
	public static String getVersion(Class<?> clazz) {
		Properties props =  loadProps(clazz);
		return 
			"v"
			+props.getProperty("major")+"."+props.getProperty("minor")+"."+props.getProperty("rev")+"_"+props.getProperty("build.number")
			+" "+props.getProperty("build.date")
			+" : "+props.getProperty("description");
	}

	// load props as resource on classpath
	private static Properties loadProps(Class<?> clazz) {
		InputStream is;
		Properties props = new Properties();
		is = clazz.getResourceAsStream(PROP_FILE);
		if (is == null) {
			throw new RuntimeException("Couldn't find: " + PROP_FILE + " on CLASSPATH");
		}
		try {
			props.load(is);
			is.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return props;
	}
//    public static void main(String[] args) {
//    	getConditionFromPath("Country[Country/isoCode!=CN]");
//    	
//	}
}
