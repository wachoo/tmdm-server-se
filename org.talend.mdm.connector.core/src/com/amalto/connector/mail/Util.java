package com.amalto.connector.mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Category;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.connector.jca.XtentisConnectorException;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;

/**
 * @author bgrieder
 *
 */
public final class Util {





	/*********************************************************************
	 *    Parsing Stuff
	 *********************************************************************/

	public static Document parse(java.io.ObjectInputStream in) throws XtentisConnectorException {
		String xml = new String();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String tmp = new String();
			while((tmp = br.readLine())!=null){
				sb.append(tmp);
			}
			xml = sb.toString();
		}
		catch (IOException e) {
			throw new XtentisConnectorException(e);
		}
		return parse(xml,null);
    }
    
    public static Document parse(String xmlString) throws XtentisConnectorException {
    	return parse(xmlString,null);
    }
    
    public static Document parse(String xmlString, String schema) throws XtentisConnectorException {

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
			throw new XtentisConnectorException(e);
		}
		
		//check if dcument parsed correctly against the schema
		if (schema != null) {
			String errors = seh.getErrors();
			if (!errors.equals("")) {
				String err = "Document  did not parse against schema: \n" + errors+"\n"
					+xmlString.substring(0, Math.min(100, xmlString.length()));
				Category.getInstance(Util.class).error(err);
				throw new XtentisConnectorException(err);
			}
		}
		return d;
    }
    
    
    
    
    public static String getFirstTextNode(Node contextNode, String xPath) throws XtentisConnectorException {
    	return getFirstTextNode(contextNode,xPath,contextNode);
    }
    
    public static String getFirstTextNode(Node contextNode, String xPath,Node namespaceNode) throws XtentisConnectorException{
    	String[] res = getTextNodes(contextNode,xPath,namespaceNode);
    	if (res.length == 0) return null;
    	else return res[0];
    }
    
    public static String[] getTextNodes(Node contextNode, String xPath) throws XtentisConnectorException {
    	return getTextNodes(contextNode, xPath, contextNode);
    }
    
    public static String[] getTextNodes(Node contextNode, String xPath, Node namespaceNode) throws XtentisConnectorException {
        String[] results=null;
        
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
			throw new XtentisConnectorException(e);
		}
		return results;
		
    }
    
    

    
    
    
	/**
	 * Get a nodelist from an xPath
	 * @throws XtentisConnectorException
	 */
	public static NodeList getNodeList(Document d, String xPath) throws XtentisConnectorException {
		return getNodeList(d.getDocumentElement(),xPath,null,null);
	}
	/**
	 * Get a nodelist from an xPath
	 * @throws XtentisConnectorException
	 */
	public static NodeList getNodeList(Node contextNode, String xPath) throws XtentisConnectorException {
		return getNodeList(contextNode,xPath,null,null);
	}
	
	/**
	 * Get a nodelist from an xPath
	 * @throws XtentisConnectorException
	 */
	public static NodeList getNodeList(Node contextNode, String xPath, String namespace, String prefix) throws XtentisConnectorException {
		try {
			XObject xo = XPathAPI.eval(
		    		contextNode,
					xPath,
					(namespace == null) ? contextNode : Util.getRootElement("nsholder",namespace,prefix)
			    	);
			if (xo.getType() != XObject.CLASS_NODESET) return null;
			return xo.nodelist();
		} catch (Exception e) {
			throw new XtentisConnectorException(e);
		}
	    
	}
    
	
	
	
    /**
	 * Returns a namespaced root element of a document
	 * Useful to create a namespace holder element
	 * @param namespace
	 * @return the root Element
	 */
	public static Element getRootElement(String elementName, String namespace, String prefix) throws ParserConfigurationException {
	 	Element rootNS=null;
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    DocumentBuilder builder = factory.newDocumentBuilder();
	    DOMImplementation impl = builder.getDOMImplementation();
	    Document namespaceHolder = impl.createDocument(namespace,(prefix==null?"":prefix+":")+elementName, null);
	    rootNS = namespaceHolder.getDocumentElement();
	    rootNS.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:"+prefix, namespace);
    	return rootNS;
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
    	ArrayList<String> l = new ArrayList<String>();
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

 }
