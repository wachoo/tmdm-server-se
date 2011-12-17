package org.talend.mdm.jaas.jboss.open.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;



/**
 * @author bgrieder
 *
 */
public final class Util {

	/*********************************************************************
	 *      PARSING
	 *********************************************************************/
	

    public static Document parse(String xmlString) throws XtentisLoginException{
    	return parse(xmlString,null);
    }
    	
    public static Document parse(String xmlString, String schema) throws XtentisLoginException{

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
			throw new XtentisLoginException(err);
		}
		
		//check if dcument parsed correctly against the schema
		if (schema != null) {
			String errors = seh.getErrors();
			if (!errors.equals("")) {
				String err = "Document  did not parse against schema: \n" + errors+"\n"+xmlString;
				throw new XtentisLoginException(err);
			}
		}
		return d;
    }

    public static String[] getTextNodes(Node contextNode, String xPath) throws XtentisLoginException{
    	return getTextNodes(contextNode,xPath,contextNode);
    }
    
    public static String[] getTextNodes(Node contextNode, String xPath,Node namespaceNode) throws XtentisLoginException{
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
			throw new XtentisLoginException(err);
		}
		return results;

    }    
    
    
    public static String getFirstTextNode(Node contextNode, String xPath,Node namespaceNode) throws XtentisLoginException{
    	String[] res = getTextNodes(contextNode,xPath,namespaceNode);
    	if (res.length == 0) 
    		return null;
    	return res[0]; 
    }

    public static String getFirstTextNode(Node contextNode, String xPath) throws XtentisLoginException{
    	return getFirstTextNode(contextNode,xPath,contextNode);
    }

    
    public static String[] getAttributeNodeValue(Node contextNode, String xPath, Node namespaceNode) throws XtentisLoginException{
        String[] results;
        
        //test for hard-coded values
        if (xPath.startsWith("\"") && xPath.endsWith("\""))
            return new String[] { xPath.substring(1, xPath.length()-1)};
            
        //test for incomplete path
        //if (! xPath.endsWith(")")) xPath+="/text()";
        
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
			throw new XtentisLoginException(err);
		}
		return results;

    }    
    
    /**
     * Retrieve the local (if RMIProvideURL is null) or remote
     * component for the particular local JNDI Name
     * (c) bgrieder - lend to Amalto
     * @param RMIProvideURL 
     * @param jndiName
     */
    public static Object retrieveComponent(String RMIProviderURL,String jndiName) {
    	
        if ((RMIProviderURL == null) || (RMIProviderURL.equals("")))
        	RMIProviderURL = "LOCAL";
                
    	Hashtable env = null;
    	if (!"LOCAL".equals(RMIProviderURL)) {	    	
	    	//FIXME: JBoss specific
	        env = new Hashtable(3);
	        env.put(Context.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
	        env.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
	        env.put(Context.PROVIDER_URL, RMIProviderURL);
	        
    	}

    	Object home = null;
    	InitialContext initialContext = null;
        try {
        	initialContext= new InitialContext(env);
        	home = initialContext.lookup(jndiName);
        } catch(NamingException e) {
    		String err = "Unable to lookup \""+jndiName+"\""
			+ ": " + e.getClass().getName() + ": "+ e.getLocalizedMessage();
    		throw new EJBException(err);        	
        } finally {
           try {initialContext.close();} catch(Exception e){};
        }
    	
        //find create and call it
        Method[] m = home.getClass().getMethods();
        Method create = null;
        for (int i = 0; i < m.length; i++) {
			if ("create".equals(m[i].getName())) {
				create = m[i];
				break;
			}
		}
        if (create == null) {
        	String err = "Unable to find create method on home of component \""+jndiName+"\"";
    		throw new EJBException(err);        	
        }
        Object component = null;
		try {
			component = create.invoke(home,(Object[])null);
        } catch(Exception e) {
    		String err = "Unable to call the create method of remote component \""+jndiName+"\""
			+": "+e.getClass().getName() + ": "+ e.getLocalizedMessage();
    		throw new EJBException(err);        	
        }
        
        return component;
		
    }
    
    
  
    
    
    /**
	 * Returns a namespaced root element of a document
	 * Useful to create a namespace holder element
	 * @param namespace
	 * @return the root Element
	 */
	public static Element getRootElement(String elementName, String namespace, String prefix) throws XtentisLoginException{
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
    	    throw new XtentisLoginException(err);    
    	}
    	return rootNS;
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
	 */
    public static String getConceptFromPath(String path) {
    	Pattern p = Pattern.compile("(.*?)[\\[|/].*");
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = p.matcher(path);
    	if (m.matches()) {
    		return m.group(1);
    	} else {
    		return null;
    	}
    }
    
	/**
	 * Returns the list of items that look like parts numbers
	 * @param path
	 * @return
	 */
    public static Collection getAllPartNumbers(String value) {
    	ArrayList l = new ArrayList();
    	l.add(value);
    	Pattern p = Pattern.compile("([0-9]+[\\*]?)[\\p{Punct}]");  //-_'#~`\\\\\\/
    	Matcher m = p.matcher(value);
    	try {
    		String s = m.replaceAll("$1 ");
    		if (!s.equals(value)) {
    			l.add(s.trim());
    			l.add(s.trim().replaceAll(" ", ""));
    		}
    	} catch (Exception e) {}
    	return l;
    }

	/*********************************************************************
	 *     PROFILING
	 *********************************************************************/
	
    private static ArrayList timeMarkers = new ArrayList(); // [totalTops,lastTime,totalPeriod]
    
	public static long topTime(int marker) throws Exception{
		long time = System.currentTimeMillis();
		if (timeMarkers.size()<=marker) {
			for (int i = timeMarkers.size(); i <= marker; i++) {
				if (i==0) 
					timeMarkers.add(new long[]{0L,System.currentTimeMillis(),0L});
				else
					timeMarkers.add(new long[]{0L,0L,0L});
			}
		}
		long period = 0;
		if (marker == 0)
			period = time - ((long[])timeMarkers.get(marker))[1];
		else
			period = time - ((long[])timeMarkers.get(marker-1))[1];
		long totalPeriod = ((long[])timeMarkers.get(marker))[2] + period;
		long totalTops = ((long[])timeMarkers.get(marker))[0] + 1;
		timeMarkers.set(marker, new long[]{totalTops,time,totalPeriod});
		return period;
    }    
	
	public static void resetTimeMarkers() {
		timeMarkers = new ArrayList();
	}
	
	public static ArrayList getTimeMarkers() {
		return timeMarkers;
	}
	
	public static void dumpTimeMarkers(String msg) {
		System.out.println("TIME MARKERS: "+msg);
		int i = 0;
		long totalProcessing = 0;
		for (Iterator iter = timeMarkers.iterator(); iter.hasNext(); ) {
			long[] values = (long[]) iter.next();
			if (i>0) totalProcessing+=values[2];
			System.out.println(
					"Marker "+(i++)+":"
					+"tops: "+values[0]+" -- "
					+"totalPeriods: "+values[2]+" -- "
					+"average: "+(values[2]/values[0])
			);
		}
		if (i>0)
			System.out.println("Total Processing: "+totalProcessing+" -- average: "+totalProcessing/((long[])timeMarkers.get(0))[0]);
	}
    
    
	/*********************************************************************
	 *     TESTS
	 *********************************************************************/
    
    
    public static void main(String args[]) throws Exception {
    	/*****************  TESTING ************/
    	/*
    	Collection c = getAllPartNumbers("123-45\\67/678");
    	for (Iterator iter = c.iterator(); iter.hasNext(); ) {
			String part = (String) iter.next();
			System.out.println(part);
		}
		*/
    }
    
  
    
	/*********************************************************************
	 *      UTILS
	 *********************************************************************/
	
	public static String getXML(String filename) throws Exception{
	    BufferedReader in = null;
        in = new BufferedReader(
        			new InputStreamReader(
        			        new FileInputStream(filename),
							"utf-8"
					)
		);
	    String xsl="";
        String line;
        while ((line=in.readLine())!=null) xsl+=line+"\n";
        return xsl;
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
	
}
