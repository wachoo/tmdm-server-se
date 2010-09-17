package com.amalto.core.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.security.Principal;
import java.security.acl.Group;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.jxpath.AbstractFactory;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.Pointer;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.delegator.IXtentisWSDelegator;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.local.DroppedItemCtrlLocal;
import com.amalto.core.ejb.local.DroppedItemCtrlLocalHome;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.ejb.local.ItemCtrl2LocalHome;
import com.amalto.core.ejb.local.ServiceLocalHome;
import com.amalto.core.ejb.local.TransformerCtrlLocal;
import com.amalto.core.ejb.local.TransformerCtrlLocalHome;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.jobox.JobContainer;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlLocal;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlLocalHome;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocalHome;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocalHome;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocalHome;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocalHome;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocalHome;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocalHome;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocalHome;
import com.amalto.core.objects.view.ejb.local.ViewCtrlLocal;
import com.amalto.core.objects.view.ejb.local.ViewCtrlLocalHome;
import com.amalto.core.webservice.WSMDMJob;
import com.amalto.core.webservice.WSVersion;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.XmlServerException;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.impl.FacetImpl;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;

/**
 * @author Bruno GRieder
 *
 */
@SuppressWarnings("deprecation")
public  class Util {


	/*********************************************************************
	 *     System wide properties
	 *********************************************************************/
	/**
	 * Home Caches spped up execution but make deployment debugging
	 */	
	public final static boolean USE_HOME_CACHES = "true".equals(System.getProperty("com.amalto.use.home.caches"));
	
	/**
	 * helper class
	 */
	public static List<String> getRuntimeServiceJndiList(boolean withPrefix) {
		List<String> serviceJndiList = new ArrayList<String>();
		String serviceJndiPrefix="amalto/local/service";
		try {
		InitialContext ctx = new InitialContext();
		NamingEnumeration<NameClassPair> list = ctx.list(serviceJndiPrefix);
			while (list.hasMore()) {
			    NameClassPair nc;
				
				nc = list.next();
				
			    if(withPrefix){
			    	serviceJndiList.add(serviceJndiPrefix+"/"+nc.getName());
			    }else{
			    	serviceJndiList.add(nc.getName());
			    }
			}
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return serviceJndiList;
	}
	
	public static List<String> getRuntimeServiceJndiList(){
		return getRuntimeServiceJndiList(true);
	}
	/*********************************************************************
	 *     Xml server Access Stuff
	 *********************************************************************/
	
	/*
    public static  XmlServerSFWrapperLocal getXmlServerSFWrapperLocal() throws EJBException{
       	InitialContext initialContext = null;
       	String jndiName = "java:comp/env/ejb/XmlServerSFWrapperLocal";
        try {
        	initialContext= new InitialContext();
        	XmlServerSFWrapperLocalHome home = (XmlServerSFWrapperLocalHome)initialContext.lookup(jndiName);
        	return home.create();
        } catch(Exception e) {
    		String err = "Unable to lookup \""+jndiName+"\""
			+ ": " + e.getClass().getName() + ": "+ e.getLocalizedMessage();
    		org.apache.log4j.Category.getInstance(Util.class).error(err);
    		throw new EJBException(err);        	
        } finally {
           try {initialContext.close();} catch(Exception e){};
        }
    }
    */
    
	/**
	 * Retrieves the XML Server wrapper<br>
	 * Deprecated: use a direct lookup a shown below.
	 * <code>
	 * XmlServerSLWrapperLocal server = null;
			try {
				server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
			} catch (Exception e) {
				String err = "Unable to access the XML Server wrapper";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new RuntimeException(err);
			}
	 *</code>
	 *@deprecated - use a direct look up in your class. Not a call to this static method
	 */
//    public static  XmlServerSLWrapperLocal getXmlServerSLWrapperLocal() throws EJBException{
//       	InitialContext initialContext = null;
//       	String jndiName = "amalto/local/xmldb/xmlserverslwrapper";
//        try {
//        	initialContext= new InitialContext();
//        	XmlServerSLWrapperLocalHome home = (XmlServerSLWrapperLocalHome)initialContext.lookup(jndiName);
//        	return home.create();
//        } catch(Exception e) {
//    		String err = "Unable to lookup \""+jndiName+"\""
//			+ ": " + e.getClass().getName() + ": "+ e.getLocalizedMessage();
//    		throw new EJBException(err);        	
//        } finally {
//           try {initialContext.close();} catch(Exception e){};
//        }
//    }
    

	/*********************************************************************
	 *    Parsing Stuff
	 *********************************************************************/

    
    
    //static LRUCache<String, Document> xmlCache=new LRUCache<String, Document>(4000);
    public static Document parse(String xmlString) throws ParserConfigurationException,IOException, SAXException{
    	//Document doc=xmlCache.get(xmlString);
    	//if(doc!=null) return doc;
    	Document doc=parse(xmlString,null);
    	//xmlCache.put(xmlString, doc);
    	return doc;
    }
    static LRUCache<String, Document> xmlCache=new LRUCache<String, Document>(20);
    public static Document parseXSD(String xsd) throws ParserConfigurationException,IOException, SAXException{
    	Document doc=xmlCache.get(xsd);
    	if(doc!=null) return doc;
    	doc=parse(xsd,null);
    	xmlCache.put(xsd, doc);
    	return doc;
    }
    
    public static Document parse(String xmlString, String schema) throws ParserConfigurationException,IOException, SAXException{

		//parse
		Document d=null;
		SAXErrorHandler seh = new SAXErrorHandler();

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

		//check if dcument parsed correctly against the schema
		if (schema != null) {
			String errors = seh.getErrors();
			if (!errors.equals("")) {
				String err = "Document did not parse against schema: \n" + errors+"\n"
					+xmlString.substring(0, Math.min(100, xmlString.length()));
				throw new SAXException(err);
			}
		}
		return d;
    }
    
 
/**
 * @author ymli
 * fix the bug:0009642:
 * remove the null element to match the shema
 * @param element
 */
	public static boolean setNullNode(Node element) {
			//String xml = Util.nodeToString(element);
			boolean removed = false;
			NodeList nodelist = element.getChildNodes();
			for (int i = 0; i < nodelist.getLength(); i++) {
				Node node = nodelist.item(i);
				int length = node.getChildNodes().getLength();
				if (length<=1&&(node.getTextContent() == null || node.getTextContent().trim().equals(""))) {
					element.removeChild(node);
					//xml = Util.nodeToString(element);
					setNullNode(element);
					removed = true;
				}
				else if(length>1 && node!=null){
					if(setNullNode(node))
						setNullNode(node.getParentNode());
				}
			}
			return removed;
	}
    
    public static Document defaultValidate(Element element, String schema)throws Exception{
    	org.apache.log4j.Logger.getLogger(Util.class).trace("validate() "+element.getLocalName());
    	Node cloneNode = element.cloneNode(true);
    	
		//parse
		Document d=null;
		SAXErrorHandler seh = new SAXErrorHandler();

        //initialize the sax parser which uses Xerces
		System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//Schema validation based on schemaURL
		factory.setNamespaceAware(true);
		factory.setValidating((schema!=null));
		
		schema=schema.replaceFirst("<\\?xml.*\\?>", "");	
		schema=schema.replace("\r\n", "\n");
		
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
		builder.setEntityResolver(new SecurityEntityResolver());

		
		//add by ymli; fix the bug:0009642:remove the null element to match the shema
		setNullNode(cloneNode);
		
		String xmlstr=Util.nodeToString(cloneNode);
       	//if element is null, remove it aiming added 
       	//see 7828
		
		xmlstr=xmlstr.replaceAll("<\\w+?/>", "");
		//xmlstr=xmlstr.replaceAll("<\\w+?>\\s+?</\\w+?>", "");
				
		
		d = builder.parse(new InputSource(new StringReader(xmlstr)));
		
		//check if dcument parsed correctly against the schema
		if (schema != null) {
			String errors = seh.getErrors();
			if (!errors.equals("")) {
				String xmlString = Util.nodeToString(element); 
				String err = "The item "+element.getLocalName()+" did not validate against the model: \n" + errors+"\n"
					+xmlString;	//.substring(0, Math.min(100, xmlString.length()));
				throw new SAXException(err);
			}
		}
		
		return d;
    }
    
    public static Document validate(Element element, String schema) 
    	throws Exception{
    	return BeanDelegatorContainer.getUniqueInstance().getValidationDelegator().validation(element, schema);    	
    }
    
    
    public static Map<String, String> getNamespaceFromImportXSD(Element element, boolean type)
    {		
    	HashMap<String, String> nsMap = new HashMap<String, String>();
		NodeList list = null;
		// for Import
		try {
			list = Util.getNodeList(element, "//xsd:import");
			for(int i=0; i < list.getLength(); i++)
			{
				Node node = list.item(i);
				String ns = node.getAttributes().getNamedItem("namespace").getNodeValue();
				if(node.getAttributes().getNamedItem("schemaLocation") == null)
				{
					continue;
				}
				String location = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
				if(location == null || location.equals(""))
				{
					continue;
				}
				Document subDoc = parseImportedFile(location);
				if(type)
				   parseTypeFromImport(subDoc.getDocumentElement(), nsMap, ns);
				else
					parseElementFromImport(subDoc.getDocumentElement(), nsMap, ns);
				nsMap.putAll(getNamespaceFromImportXSD(subDoc.getDocumentElement(), type));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return nsMap;
		}

		// for Include
		try {
			list = Util.getNodeList(element, "//xsd:include");
			for(int i=0; i < list.getLength(); i++)
			{
				Node node = list.item(i);
				String ns = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
				if(ns == null)
				{
					continue;
				}
				Document subDoc = parseImportedFile(ns);
				if(type)
				    parseTypeFromImport(subDoc.getDocumentElement(), nsMap, ns);
				else
					parseElementFromImport(subDoc.getDocumentElement(), nsMap, ns);
				nsMap.putAll(getNamespaceFromImportXSD(subDoc.getDocumentElement(), type));
			}
		} catch (XtentisException e) {
			e.printStackTrace();
			return nsMap;
		}

    	return nsMap;
    }
    
    private static void parseTypeFromImport(Element elem, HashMap<String, String> map, String location)
    {
    	NodeList list;
		try {
			for(int i = 0; i < 2; i++)
			{
				if(i == 0)
				{
					list = Util.getNodeList(elem, "//xsd:complexType");
				}
				else
				{
					list = Util.getNodeList(elem, "//xsd:simpleType");
				}
				
				for (int idx = 0; idx < list.getLength(); idx++)
				{
					Node node = list.item(idx);
					if(node.getAttributes().getNamedItem("name") == null)continue;
					String typeName = node.getAttributes().getNamedItem("name").getNodeValue();
				    map.put(typeName + " : " + location , location);	
				}
			}
		} catch (XtentisException e) {
			e.printStackTrace();
		}

    }
    
    private static void parseElementFromImport(Element elem, HashMap<String, String> map, String location)
    {
    	NodeList list;
		try {
			list = Util.getNodeList(elem, "/xsd:schema/xsd:element");
			for (int idx = 0; idx < list.getLength(); idx++)
			{
				Node node = list.item(idx);
				if(node.getAttributes().getNamedItem("name") == null)continue;
				String typeName = node.getAttributes().getNamedItem("name").getNodeValue();
				String typeCatg = null;
				if(node.getAttributes().getNamedItem("type") != null)
				{
					typeCatg = node.getAttributes().getNamedItem("type").getNodeValue();
				}
				NodeList subList = null;
				if(typeCatg == null)
				{
				  subList= Util.getNodeList(node, "//xsd:complexType//xsd:element");
				}
				else
				{
					subList = Util.getNodeList(node, "//xsd:complexType[@name='" + typeCatg + "']" + "//xsd:element" );
				}
                String subNames = "";
                for(int i = 0; i < subList.getLength(); i++)
                {
                	subNames += subList.item(i).getAttributes().getNamedItem("name").getNodeValue() + " ";
                }
                if(!subNames.equals(""))
                {
    			    map.put(typeName + " : " + location , subNames.trim());	
                }
			}
			
		} catch (XtentisException e) {
			e.printStackTrace();
		}

    }
    static LRUCache<String, Document> xsdCache=new LRUCache<String, Document>(30);
    
    private static Document parseImportedFile(String xsdLocation)
    {
    	Document doc= xsdCache.get(xsdLocation);
    	if(doc!=null) return doc;
    	
	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);
		
		Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
		Matcher match = httpUrl.matcher(xsdLocation);
		Document d = null;
		try
		{
			if(match.matches())
			{
				List<String> authorizations = Util.getAuthorizationInfo();
	    		String xsd = Util.getResponseFromURL(xsdLocation, authorizations.get(0), authorizations.get(1));
	    		d = Util.parse(xsd);
			}
			else
			{
				DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        	d = documentBuilder.parse(new FileInputStream(xsdLocation));
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
		xsdCache.put(xsdLocation, d);
		return d;
    }

    
    public static void removeXpathFromDocument(Document document, String xpath,boolean reservedRoot)throws Exception {
    	Element root=document.getDocumentElement();
		NodeList toDeleteNodeList=Util.getNodeList(document, xpath);
		if (toDeleteNodeList != null) {
			Node lastParentNode = null;
			Node formatSiblingNode = null;
			for (int i = 0; i < toDeleteNodeList.getLength(); i++) {
				Node node = toDeleteNodeList.item(i);
				if(root.isSameNode(node)&&reservedRoot){
					while (node.hasChildNodes()) {
						node.removeChild(node.getFirstChild());
					}
				}else{
					lastParentNode = node.getParentNode();
					formatSiblingNode = node.getNextSibling();
					if (lastParentNode != null){
						lastParentNode.removeChild(node);
					}	
					if (formatSiblingNode != null && formatSiblingNode.getNodeValue() != null && formatSiblingNode.getNodeValue().matches("\\s+")){
						lastParentNode.removeChild(formatSiblingNode);
					}
				}
				
			}
		}
	}

    public static String[] getTextNodes(Node contextNode, String xPath) throws TransformerException{
    	return getTextNodes(contextNode,xPath,contextNode);
    }
    
    public static String[] getTextNodes(Node contextNode, String xPath,Node namespaceNode) throws TransformerException{
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
		} catch (TransformerException e) {
			String err = "Unable to get the text node(s) of "+xPath
					+": " + e.getClass().getName() + ": "
					+ e.getLocalizedMessage();
			throw new TransformerException(err);
		}
		return results;

    }    
    
    
    public static String getFirstTextNodeNotNull(Node contextNode, String xPath) throws TransformerException {
    	String val=  getFirstTextNode(contextNode,xPath,contextNode);
    	return val == null ? "" : val;
    }
    
    public static String getFirstTextNode(Node contextNode, String xPath,Node namespaceNode) throws TransformerException{
    	String[] res = getTextNodes(contextNode,xPath,namespaceNode);
    	if (res.length == 0) 
    		return null;
    	return res[0]; 
    }

    public static String getFirstTextNode(Node contextNode, String xPath) throws TransformerException{
    	return getFirstTextNode(contextNode,xPath,contextNode);
    }

    
    public static String[] getAttributeNodeValue(Node contextNode, String xPath, Node namespaceNode) throws TransformerException{
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
		} catch (TransformerException e) {
			String err = "Unable to get the text node(s) of "+xPath
					+": " + e.getClass().getName() + ": "
					+ e.getLocalizedMessage();
			throw new TransformerException(err);
		}
		return results;

    }   
    static LRUCache<String, List<String>> xsdNodesCache=new LRUCache<String, List<String>>(20);
    public static List<String> getALLNodesFromSchema(String xsd)throws Exception{
    	List<String> list=xsdNodesCache.get(xsd);
    	if(list!=null)return list;
    	Node node=Util.parseXSD(xsd).getDocumentElement();
    	list=getALLNodesFromSchema(node);
    	xsdNodesCache.put(xsd, list);
    	return list;
    }
    
    public static List<String> getALLNodesFromSchema(Node doc) throws XtentisException
    {
    	List<String> list = new ArrayList<String>();
        String prefix = doc.getPrefix();
        NodeList l = Util.getNodeList(doc, "./"+prefix+":element/@name");
        
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		DocumentBuilder builder;

        for (int i = 0; i < l.getLength(); i++) {
            Node n = l.item(i);
            list.add(n.getNodeValue());
        }
        
        try
        {
        	for(int xsdType = 0; xsdType < 2; xsdType++)
        	{
        		try
        		{
	        		if(xsdType == 0)
		               l = Util.getNodeList(doc, "./xsd:import");
	        		else
	        			l = Util.getNodeList(doc, "./xsd:include");
	        		}
        		catch(XtentisException e)
        		{
        			continue;
        		}
		        for (int elemNum = 0; elemNum < l.getLength(); elemNum++)
		        {
		        	Node importNode = l.item(elemNum);
		        	Node schemaLocaton = importNode.getAttributes().getNamedItem("schemaLocation");
		        	if(schemaLocaton == null)
		        	{
		        		continue;
		        	}
		        	String xsdLocation = schemaLocaton.getNodeValue();
		    		Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
		    		Matcher match = httpUrl.matcher(xsdLocation);
		    		Document d = null;
		    		if(match.matches())
		    		{
		    			List<String> authorizations = Util.getAuthorizationInfo();
			    		String xsd = Util.getResponseFromURL(xsdLocation, authorizations.get(0), authorizations.get(1));
			    		d = Util.parse(xsd);
		    		}
		    		else
		    		{
			        	builder = factory.newDocumentBuilder();
			        	FileInputStream fio=new FileInputStream(xsdLocation);
			        	try {
			        		d = builder.parse(fio);
			        	}finally {fio.close();}
		    		}

		        	list.addAll(getALLNodesFromSchema(d.getDocumentElement()));
		        }
        	}
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }

        return list;
    }
    
    /**
     * 
     * @param doc
     * @param type
     * @return
     * @throws XtentisException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws SAXException 
     * @throws  
     */
    public static Element getNameSpaceFromSchema(Node doc, String typeName) throws XtentisException
    {
		Pattern mask = Pattern.compile("(.*?):(.*?)");
		Matcher matcher = mask.matcher(typeName);
		String prefix = "";
		if (matcher.matches())
		{
			prefix = matcher.group(1);
		}
		else
			prefix = null;
		
		Element ns = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		//Schema validation based on schemaURL
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
	    	NodeList list = Util.getNodeList(doc,".//" + "xsd:import");
	    	for (int id = 0; id < list.getLength() && prefix != null; id++)
	    	{
	    		Node node = list.item(id);
	    		if(node.getAttributes().getNamedItem("schemaLocation") == null)
	    		{
	    			continue;
	    		}
	    		String schemalocation = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
	    		String namespace = node.getAttributes().getNamedItem("namespace").getNodeValue();
            	NodeList l = Util.getNodeList(doc, "//*[@type='" + typeName + "']", 
            			namespace, prefix);
            	if (l.getLength() > 0)
            		return (Element)l.item(0);
	    		Document d = builder.parse(new FileInputStream(schemalocation));
            	ns = getNameSpaceFromSchema(d, typeName);
            	if (ns != null)return ns;
	    	}
	    	if(list.getLength() == 0 || prefix == null)
	    	{
	    		list = Util.getNodeList(doc,".//" + "xsd:include");
	    		for(int id = 0; id < list.getLength(); id++)
	    		{
		    		Node node = list.item(id);
		    		Document d ;
		    		String schemalocation = node.getAttributes().getNamedItem("schemaLocation").getNodeValue();
		    		Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
		    		Matcher match = httpUrl.matcher(schemalocation);
		    		if(match.matches())
		    		{
			    		// to-fix : we need to provide a flexible way to find the user/pwd, rather than the hard code out here.
		    			List<String> authorizations = Util.getAuthorizationInfo();
			    		String xsd = Util.getResponseFromURL(schemalocation, authorizations.get(0), authorizations.get(1));
			    		d = Util.parse(xsd);
		    		}
		    		else
		    		{
		    			d = builder.parse(new FileInputStream(schemalocation));
		    		}
		    		
		    		
	            	ns = getNameSpaceFromSchema(d, typeName);
	            	if (ns != null)return ns;
	    		}
	    		list = Util.getNodeList(doc,"//*[@name='" + typeName + "']");
	    		if(list.getLength() > 0)
	    			return (Element)list.item(0);
	    	}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return ns;
    }
    
    public static String getResponseFromURL(String url, String user, String pwd)
    {
        BASE64Encoder encoder = new BASE64Encoder();
        StringBuffer buffer = new StringBuffer();
        String credentials = encoder.encode(new String(user + ":" + pwd).getBytes());
        
        try {
				URL urlCn = new URL(url);
				URLConnection conn = urlCn.openConnection();
				conn.setAllowUserInteraction(true);
				conn.setDoOutput(true);
				conn.setDoInput(true);
	            conn.setRequestProperty("Authorization", "Basic " + credentials);
	            conn.setRequestProperty("Expect", "100-continue"); 
	
	            InputStreamReader doc = 
	                new InputStreamReader(conn.getInputStream());
	            BufferedReader reader = new BufferedReader(doc);
	            String line = reader.readLine();
	            while(line != null)
	            {
	            	buffer.append(line);
	            	line = reader.readLine();
	            }
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		    
		  return buffer.toString();
    }
    /**
     * Check if a local or remote
     * component for the JNDI Name exists 
     * @param RMIProviderURL
     * @param jndiName
     */
    public static boolean existsComponent(String RMIProviderURL,String jndiName) throws XtentisException{
    	
        if ((RMIProviderURL == null) || (RMIProviderURL.equals("")))
        	RMIProviderURL = "LOCAL";
                
    	Hashtable<String,String> env = null;
    	if (!"LOCAL".equals(RMIProviderURL)) {	    	
	    	//FIXME: JBoss specific
	        env = new Hashtable<String,String>(3);
	        env.put(Context.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
	        env.put("java.naming.factory.url.pkgs","org.jboss.naming:org.jnp.interfaces");
	        env.put(Context.PROVIDER_URL, RMIProviderURL);      
    	}

    	Object home = null;
    	InitialContext initialContext = null;
        try {
        	initialContext= new InitialContext(env);
        	home = initialContext.lookup(jndiName);
        } catch(NamingException e) {} finally {
           try {initialContext.close();} catch(Exception e){};
        }
        
        return (home!=null);		
    }
    
    /**
     * Retrieve the local (if RMIProvideURL is null) or remote
     * component for the particular local JNDI Name
     * (c) bgrieder - lend to Amalto
     * @param RMIProviderURL 
     * @param jndiName
     */
    public static Object retrieveComponent(String RMIProviderURL,String jndiName) throws XtentisException{
    	
        if ((RMIProviderURL == null) || (RMIProviderURL.equals("")))
        	RMIProviderURL = "LOCAL";
                
    	Hashtable<String,String> env = null;
    	if (!"LOCAL".equals(RMIProviderURL)) {	    	
	    	//FIXME: JBoss specific
	        env = new Hashtable<String,String>(3);
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
    		throw new XtentisException(err);        	
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
    		throw new XtentisException(err);        	
        }
        Object component = null;
		try {
			component = create.invoke(home,(Object[])null);
        } catch(Exception e) {
    		String err = "Unable to call the create method of remote component \""+jndiName+"\""
			+": "+e.getClass().getName() + ": "+ e.getLocalizedMessage();
    		throw new XtentisException(err);
        }
        
        return component;

    }
    
    
    /**
     * Get the method of a component by its name
     * (c) bgrieder - lend to Amalto
     * @param component
     * @param methodName
     * @return the Method - niull if not found
     * @throws EJBException
     */
    public static Method getMethod(Object component, String methodName) throws EJBException{
		Method[] methods = component.getClass().getMethods();
		for (int i = 0; i < methods.length; i++) {
			if (methodName.equals(methods[i].getName())) {
				return methods[i];
			}
		}
		return null;
    }
    
    
    /**
     * Dumps info on a class
     * @param clazz
     */
    public static void dumpClass(Class<?> clazz) {
    	org.apache.log4j.Logger.getLogger(Util.class).debug("dumpClass() CLASS "+clazz.getName());
		Class<?>[] interfaces = clazz.getInterfaces();
		if (interfaces!=null) {
			for (int i = 0; i < interfaces.length; i++) {
				org.apache.log4j.Logger.getLogger(Util.class).debug("()  Class Interface "+i+": "+interfaces[i].getName());		
			}
		}
		Class<?>[] classes = clazz.getClasses();
		if (classes!=null) {
			for (int i = 0; i < classes.length; i++) {
				org.apache.log4j.Logger.getLogger(Util.class).debug("()  Classes "+i+": "+classes[i].getName());		
			}
		}	    		
		org.apache.log4j.Logger.getLogger(Util.class).debug("()  Super Class "+clazz.getSuperclass().getName());
		org.apache.log4j.Logger.getLogger(Util.class).debug("()  Generic Super Class "+clazz.getGenericSuperclass());
		Type[] types = clazz.getGenericInterfaces();
		if (types!=null) {
			for (int i = 0; i < types.length; i++) {
				org.apache.log4j.Logger.getLogger(Util.class).debug("()  Generic Interface"+i+": "+types[i]);		
			}
		}
		Method[] methods = clazz.getMethods();
		for (int i = 0; i < types.length; i++) {
			System.out.println("Types "+types[i]);
		}
		for (int i = 0; i < methods.length; i++) {
			org.apache.log4j.Logger.getLogger(Util.class).debug("Method "+methods[i].getName());
		}
	}

    
    
    
    /**
     * XSLT Utils
     * @param xslt
     * @return teh Top Business Concept
     */
    public static String getTopBusinessConceptName(Document xslt) throws TransformerException{
    	String prefix = xslt.getDocumentElement().getPrefix();
    	//String uri = xslt.getDocumentElement().getNamespaceURI();
    	String xpath = prefix+":template[@match='/']/"+prefix+":apply-templates/@mode";
    	return Util.getFirstTextNode(xslt.getDocumentElement(),xpath);
    	/*
    	String s = getAttributeNodeValue(
    			xslt.getDocumentElement(),
				"xs:template[@match='/']/xs:apply-templates/@mode",
				getRootElement("nsholder","http://www.w3.org/1999/XSL/Transform","xs")
				)[0]; 
    	return s;
    	*/
    }
    public static String getTopBusinessConceptPath(Document xslt) throws TransformerException{
    	String prefix = xslt.getDocumentElement().getPrefix();
    	//String uri = xslt.getDocumentElement().getNamespaceURI();
    	String xpath = prefix+":template[@match='/']/"+prefix+":apply-templates/@select";
    	return Util.getFirstTextNode(xslt.getDocumentElement(),xpath);
    	/*
    	String s = getAttributeNodeValue(
    			xslt.getDocumentElement(),
				"xs:template[@match='/']/xs:apply-templates/@select",
				getRootElement("nsholder","http://www.w3.org/1999/XSL/Transform","xs")
				)[0]; 
    	return s;
    	*/
    }
    
    
    public static Document setTopBusinessConceptPath(Document xslt) throws XtentisException{
    	try {
	    	String prefix = xslt.getDocumentElement().getPrefix();
	    	//String uri = xslt.getDocumentElement().getNamespaceURI();
	    	String xpath = prefix+":template[@match='/']/"+prefix+":apply-templates";
	    	NodeList l = Util.getNodeList(xslt.getDocumentElement(),xpath);    	
	    	if (l==null)
	    		throw new XtentisException("The root template match (match='/') of the transform cannot be found");
	    	int len = l.getLength();
	    	if (len!=1)
	    		throw new XtentisException("The root template match (match='/') of the transform is not univoque");
	    	((Element)l.item(0)).setAttribute("select",".");
	    	return xslt;
    	} catch (Exception e) {
      	    String err = "Unable to process the transform"
    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
      	    org.apache.log4j.Logger.getLogger(Util.class).error(err,e);
      	    throw new XtentisException(err);
    	}
    	
    }
    
    static LRUCache<String, XSDKey> xsdkeyCache=new LRUCache<String, XSDKey>(40);    

    public static XSDKey getBusinessConceptKey(Document xsd, String businessConceptName) throws TransformerException{
    	try {
    		String schema=nodeToString(xsd);
    		XSDKey key1=xsdkeyCache.get(schema+"#"+businessConceptName);
    		if(key1!=null)return key1;    		
        	String[] selectors = null;
        	String[] fields = null;
        	selectors = Util.getTextNodes(
    			xsd.getDocumentElement(),
    			"xsd:element/xsd:unique[@name='"+businessConceptName+"']/xsd:selector/@xpath",
				getRootElement("nsholder",xsd.getDocumentElement().getNamespaceURI(),"xsd")
				);
    	
    		fields =  Util.getTextNodes(
    			xsd.getDocumentElement(),
    			"xsd:element/xsd:unique[@name='"+businessConceptName+"']/xsd:field/@xpath",
				getRootElement("nsholder",xsd.getDocumentElement().getNamespaceURI(),"xsd")
				);
    		
    		if(selectors.length == 0)
    		{
        		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        		factory.setNamespaceAware(true);
        		factory.setValidating(false);
        		DocumentBuilder builder;
				builder = factory.newDocumentBuilder();
				NodeList list = null;
				XSDKey key = null;
	        	for(int xsdType = 0; xsdType < 2; xsdType++)
	        	{
	        		if(xsdType == 0)
		               list = Util.getNodeList(xsd, "./xsd:import");
	        		else
	        			list = Util.getNodeList(xsd, "./xsd:include");
			        for (int elemNum = 0; elemNum < list.getLength(); elemNum++)
			        {
			        	Node importNode = list.item(elemNum);
			        	if(importNode.getAttributes().getNamedItem("schemaLocation") == null)
			        	{
			        		continue;
			        	}
			        	String xsdLocation = importNode.getAttributes().getNamedItem("schemaLocation").getNodeValue();
			    		Pattern httpUrl = Pattern.compile("(http|https|ftp):(\\//|\\\\)(.*):(.*)");
			    		Matcher match = httpUrl.matcher(xsdLocation);
			    		Document d = null;
			    		if(match.matches())
			    		{
			    			List<String> authorizations = Util.getAuthorizationInfo();
				    		String data = Util.getResponseFromURL(xsdLocation, authorizations.get(0), authorizations.get(1));
				    		d = Util.parse(data);
			    		}
			    		else
			    		{
				        	builder = factory.newDocumentBuilder();
				        	d = builder.parse(new FileInputStream(xsdLocation));
			    		}

			        	key = getBusinessConceptKey(d, businessConceptName);
			        	if(key != null)
			        	{
				        	xsdkeyCache.put(nodeToString(d)+"#"+businessConceptName, key);
				        	break;
			        	}
			        		
			        }
	        	}
                return key;
    		}
    		else return new XSDKey(selectors[0],fields);
    	} catch(Exception e) {
    	    String err = "Unable to get the keys for the Business Concept "+businessConceptName+": "+e.getLocalizedMessage();
    		throw new TransformerException(err);
    	}
    
    }

    
    public static List<String> getAuthorizationInfo()
    {
    	ArrayList<String> authorizations = new ArrayList<String>();
    	String user = "", pwd = "";
    	try {
			Subject subject=LocalUser.getCurrentSubject();
			Set<Principal> set = subject.getPrincipals();
			for (Iterator<Principal> iter = set.iterator(); iter.hasNext(); ) {
				Principal principal = iter.next();
				if (principal instanceof Group) {
					Group group = (Group) principal;
					if("Username".equals(group.getName())) {
						if (group.members().hasMoreElements()) {
							user=group.members().nextElement().getName();
						}
					}else if("Password".equals(group.getName())){
						if (group.members().hasMoreElements()) {
							pwd=group.members().nextElement().getName();
						}
					}
				}
			}//for
			authorizations.add(user);
			authorizations.add(pwd);
		} catch (XtentisException e) {
			e.printStackTrace();
			return null;
		}
		
		return authorizations;
    }
    
    public static boolean isUUIDType(String type){
    	return EUUIDCustomType.allTypes().contains(type);
    	
    }
    public static Node processUUID(Element root, String schema, String dataCluster, String concept) throws Exception{
    	return Util.generateUUIDForElement(schema, dataCluster,concept, root, false);			
    }
    
    public static Node processUUID(Element root, String schema, String dataCluster, String concept,boolean pseudoAutoIncrement) throws Exception{
		
		return Util.generateUUIDForElement(schema, dataCluster,concept, root, pseudoAutoIncrement);			

    }
 
    public static List<UUIDPath> getUUIDNodes(String schema, String concept)throws Exception{
   
    	List<UUIDPath> list=new ArrayList<UUIDPath>();
    	Map<String,XSElementDecl> map=getConceptMap(schema);
    	if(map.get(concept)!=null) {
	    	XSComplexType xsct = (XSComplexType)(map.get(concept).getType()); 
	    	XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
	    	for(int i=0; i<xsp.length; i++){
	    		getChildren("/"+concept,xsp[i],list);
	    	}
    	}
    	return list;
    }
   
    private static void getChildren(String parentPath,XSParticle xsp, List<UUIDPath> list){
		//aiming added see 0009563
		if(xsp.getTerm().asModelGroup()!=null){ //is complex type
			XSParticle[] xsps=xsp.getTerm().asModelGroup().getChildren();
			String path=parentPath+"/"+xsp.getTerm().asElementDecl().getName();
			for (int i = 0; i < xsps.length; i++) {
				getChildren(path,xsps[i],list);
			}
		}
		if(xsp.getTerm().asElementDecl()==null) return;
		//end
		if(xsp.getTerm().asElementDecl().getType().isComplexType()==false ){
			String type=xsp.getTerm().asElementDecl().getType().getName();
			if(EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(type) || EUUIDCustomType.UUID.getName().equalsIgnoreCase(type)){
				String path=parentPath+"/"+xsp.getTerm().asElementDecl().getName();
				UUIDPath uuid=new UUIDPath();
				uuid.setXpath(path);
				uuid.setType(type);
				list.add(uuid);
			}
		}		
		if(xsp.getTerm().asElementDecl().getType().isComplexType()==true ){
			XSParticle particle = xsp.getTerm().asElementDecl()
			.getType().asComplexType().getContentType().asParticle();
			if(particle!=null){
				XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
				String path=parentPath+"/"+xsp.getTerm().asElementDecl().getName();
				for (int i = 0; i < xsps.length; i++) {
					getChildren(path,xsps[i],list);
				}
			}
		}		    	
    }
    //conceptmap Cache
    static LRUCache<String,  Map<String,XSElementDecl>> conceptMapCache=new LRUCache<String, Map<String,XSElementDecl>>(10);
    
	public static Map<String,XSElementDecl> getConceptMap(String xsd) 
	throws Exception{
		if(conceptMapCache.get(xsd)!=null) return conceptMapCache.get(xsd);
		XSOMParser reader = new XSOMParser();
		reader.setAnnotationParser(new DomAnnotationParserFactory());
		reader.setEntityResolver(new SecurityEntityResolver());
	    reader.parse(new StringReader(xsd));
	    XSSchemaSet xss = reader.getResult();
		Collection xssList = xss.getSchemas();
    	Map<String,XSElementDecl> mapForAll = new HashMap<String,XSElementDecl>() ;
		Map<String,XSElementDecl> map = null ;
		for (Iterator iter = xssList.iterator(); iter.hasNext();) {
			XSSchema schema = (XSSchema) iter.next();
			map = schema.getElementDecls();
			mapForAll.putAll(map);
		}   
		conceptMapCache.put(xsd, mapForAll);
		return mapForAll;
	}
	
	/**
	 * update the node according to the schema
	 * @param concept
	 * @param xsd
	 * @param updateNode
	 * @return
	 * @throws Exception
	 */
	public static Node updateNodeBySchema(String concept, String xsd, Node updateNode){
		try {
			Element oldNode=createItem(concept, xsd);
			
			//see 0011615: Complex type sequences are truncated when saving a record in both web app & studio
			HashMap<String, UpdateReportItem> updatedPath=compareElement("/"+concept,updateNode,oldNode);		
			return updateElement("/"+concept, oldNode, updatedPath);	
		}catch(Exception e) {
			return updateNode;
		}		
	}
	/**
	 * check concept's datamodel contains UUID or Auto_increment type fields and it's empty
	 * @param concept
	 * @param xsd
	 * @return
	 * @throws Exception
	 */
	public static boolean containsUUIDType(String concept, String xsd, Element root) {
		try {
		Map<String,XSElementDecl> map = getConceptMap(xsd);
    	XSComplexType xsct = (XSComplexType)(map.get(concept).getType());
    	XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
    	for (int j = 0; j < xsp.length; j++) {  
    		if(containsUUIDType(xsp[j],"/"+concept,root.getOwnerDocument())) {
    			return true;
    		}
    	}
		}catch(Exception e) {
			return false;
		}
		return false;
	}
	
	private static boolean containsUUIDType(XSParticle xsp, String xpathParent, Document d) {
		try {
		if(xsp.getTerm().asModelGroup()!=null){ //is complex type
			XSParticle[] xsps=xsp.getTerm().asModelGroup().getChildren();			
			for (int i = 0; i < xsps.length; i++) {
				containsUUIDType(xsps[i],xpathParent,d);
			}
		}
		if(xsp.getTerm().asElementDecl()==null) return false;
		String type=xsp.getTerm().asElementDecl().getType().getName();
		String xpath = xpathParent+"/"+xsp.getTerm().asElementDecl().getName();
		if( (EUUIDCustomType.UUID.getName().equals(type) || EUUIDCustomType.AUTO_INCREMENT.getName().equals(type)) && getNodeList(d,xpath).getLength()==0) 
			return true;			
		if(xsp.getTerm().asElementDecl().getType().isComplexType()==true ){
			XSParticle particle = xsp.getTerm().asElementDecl()
			.getType().asComplexType().getContentType().asParticle();
			if(particle!=null){
				XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
				String xpathParent1 = xpathParent+"/"+xsp.getTerm().asElementDecl().getName();				
				for (int i = 0; i < xsps.length; i++) {
					containsUUIDType(xsps[i],xpathParent1,d);
				}
			}
		}		
		}catch(Exception e) {
			return false;
		}
		return false;
	}
	public static boolean isOnlyUpdateKey(Node oldNode, String concept, XSDKey xsdKey,String[] keyvalues){
		try {
			String xml1 = "<"+concept+"></"+concept+">";
			Node node = Util.parse(xml1).getDocumentElement();
		    JXPathContext jxpContext = JXPathContext.newContext ( node );
		    jxpContext.setLenient(true);
		    jxpContext.setFactory(factory);
		    for (int i = 0; i < xsdKey.getFields().length && i<keyvalues.length; i++) {
		    	String xpath= xsdKey.getFields()[i];
		    	jxpContext.createPathAndSetValue(xpath, keyvalues[i]);
		    }
		    node=(Node)jxpContext.getContextBean();
		    String xmlstring=getXMLStringFromNode(oldNode);
		    xmlstring=xmlstring.replaceAll(">\\s+<", "><");
		    String keystring=getXMLStringFromNode(node);
		    keystring=keystring.replaceAll(">\\s+<", "><");
		    return xmlstring.equals(keystring);
		}catch(Exception e) {
			return false;
		}
	}
	/**
	 * create an "empty" item from scratch, set every text node to empty
	 * @param concept
	 * @param xsd
	 * @return
	 * @throws Exception
	 */
	public static Element createItem(String concept, String xsd) throws Exception{
		
		String xml1 = "<"+concept+"></"+concept+">";
		Document d = parse(xml1,null);						
		Map<String,XSElementDecl> map = getConceptMap(xsd);
    	XSComplexType xsct = (XSComplexType)(map.get(concept).getType());
    	XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
    	for (int j = 0; j < xsp.length; j++) {  		
    		//why don't set up children element? FIXME    		
    		setChilden(xsp[j], "/"+concept,d);
    	}
    	return d.getDocumentElement();
	}
	/**
	 * the return type maybe all/sequence/choice
	 * @param concept
	 * @param xsd
	 * @return
	 * @throws Exception
	 */
	public static String getConceptModelType(String concept, String xsd) {
		try{
			Map<String,XSElementDecl> map = getConceptMap(xsd);
	    	XSComplexType xsct = (XSComplexType)(map.get(concept).getType());
	    	return xsct.getContentType().asParticle().getTerm().asModelGroup().getCompositor().toString();
		}catch(Exception e){
			return "all";
		}			
	}
	
	private static void setChilden(XSParticle xsp, String xpathParent, Document d) throws Exception{
		//aiming added see 0009563
		if(xsp.getTerm().asModelGroup()!=null){ //is complex type
			XSParticle[] xsps=xsp.getTerm().asModelGroup().getChildren();			
			for (int i = 0; i < xsps.length; i++) {
				setChilden(xsps[i],xpathParent,d);
			}
		}
		if(xsp.getTerm().asElementDecl()==null) return;
		//end
			
		Element el = d.createElement(xsp.getTerm().asElementDecl().getName());
		Node node = Util.getNodeList(d,xpathParent).item(0);
		node.appendChild(el);
		if(xsp.getTerm().asElementDecl().getType().isComplexType()==true ){
			XSParticle particle = xsp.getTerm().asElementDecl()
			.getType().asComplexType().getContentType().asParticle();
			if(particle!=null){
				XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
				xpathParent = xpathParent+"/"+xsp.getTerm().asElementDecl().getName();
				for (int i = 0; i < xsps.length; i++) {
					setChilden(xsps[i],xpathParent, d);
				}
			}
		}		
	}

    public static String[] getTargetSystemsFromSchema(Document schema, String concept) throws Exception{
    	String[] targetSystems=null;
    	
    	Element rootNS=Util.getRootElement("nsholder",schema.getDocumentElement().getNamespaceURI(),"xsd");	
		String xpath="//xsd:element[@name='"+concept+"']//xsd:appinfo[@source='X_TargetSystem']";
		NodeList tsList=Util.getNodeList(schema.getDocumentElement(),xpath,rootNS.getNamespaceURI(),"xsd");
		
		if(tsList!=null){
			targetSystems=new String[tsList.getLength()];
			for (int i = 0; i < tsList.getLength(); i++) {
				Node tsNode=tsList.item(i);
				targetSystems[i]=tsNode.getTextContent();
			}
		}
		
		return targetSystems;
	}
    
    /**
     * 
     * @param schema
     * @param dataCluster
     * @param concept
     * @param elementname null:
     * @param conceptRoot
     * @throws Exception
     */
    private static Node generateUUIDForElement(String schema,String dataCluster,String concept, Element conceptRoot,boolean pseudoAutoIncrement) throws Exception{
    	List<UUIDPath> uuidLists=getUUIDNodes(schema, concept);
    	JXPathContext jxpContext = JXPathContext.newContext ( conceptRoot );
    	jxpContext.setLenient(true);
    	
    	jxpContext.setFactory(factory);
		for(int i=0; i<uuidLists.size(); i++){
			UUIDPath uuid=uuidLists.get(i);
			String xpath= uuid.getXpath();
			String type=uuid.getType();
			xpath=xpath.replaceFirst("/"+concept+"/", "");
			String value="";
			if(EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(type)){
				if(pseudoAutoIncrement) {
					Random rand =new Random();
					value=rand.nextInt(10000)+"";
				}else{
					String universe=LocalUser.getLocalUser().getUniverse().getName();
					Object o=jxpContext.getValue(xpath);
					if(o==null || o.toString().trim().length()==0)
						value=String.valueOf(AutoIncrementGenerator.generateNum(universe, dataCluster,concept+"."+xpath.replaceAll("/", ".")));
					else
						value=o.toString();
				}				
			}
			if(EUUIDCustomType.UUID.getName().equalsIgnoreCase(type)){
				Object o = jxpContext.getValue(xpath);
				if(o == null || o.toString().trim().length()==0)
				   value=UUID.randomUUID().toString();
				else
					value = o.toString();
			}						
			jxpContext.createPathAndSetValue(xpath, value);
						
		}		
		return (Node)jxpContext.getContextBean();
    }

     
	public static String getXMLStringFromNode(Node d) throws TransformerException{
		return nodeToString(d);
	}
     
    /**
     * @author achen
     * @param xsd
     * @param businessConceptName
     * @param keyName
     * @return
     * @throws TransformerException
     */
    public static String getBusinessConceptKeyType(Document xsd, String businessConceptName,String keyName) throws TransformerException{
    	try {
    		String type;
    		type = Util.getTextNodes(
    			xsd.getDocumentElement(),
    			"xsd:element[@name='"+businessConceptName+"']/xsd:complexType//xsd:element[@name='"+keyName+"']/@type",
				getRootElement("nsholder",xsd.getDocumentElement().getNamespaceURI(),"xsd")
				)[0];
    	
        	return type;
    	} catch(TransformerException e) {
    	    String err = "Unable to get the keys for the Business Concept "+businessConceptName+": "+e.getLocalizedMessage();
    		throw new TransformerException(err);
    	}    
    }
    
    /**
     *
     * @param item
     * @param key
     * @return the key ids
     * @throws XtentisException
     */
    public static String[] getKeyValuesFromItem(Element item, XSDKey key) throws TransformerException{   	    
    	return getItemKeyValues(item,key);
    }
    
    /**
     * Return the Item Primary key values
     * @param item
     * @param xsdKey
     * @return the Item Primary Key values
     * @throws TransformerException
     * @see #getBusinessConceptKey(Document, String)
     */
    public static String[] getItemKeyValues(Element item, XSDKey xsdKey) throws TransformerException{
    	try {    		
    		if(xsdKey!=null) {
	    		String[] vals = new String[xsdKey.getFields().length];
	    		for (int i = 0; i < xsdKey.getFields().length; i++) {    			
	    			String xpath=xsdKey.getFields()[i];
	    			xpath= xpath.replaceFirst("/"+item.getLocalName()+"/", "");
	    			vals[i] = Util.getFirstTextNode(item,xsdKey.getSelector()+"/"+xpath);
	    			if (vals[i]!=null) vals[i] = vals[i].trim(); //FIXME: Due to eXist trimming values @see ItemPOJO
				}
	        	return vals;
    		}
    	} catch (TransformerException e) {
    	    String err = "Unable to get the key value for the item "+item.getLocalName()
    			+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    		throw new TransformerException(err);
    	}    	    	
    	return null;
    }
    
    
    /**
     * Extracts the item PK by reading it and its model<br>
     * The less costly of all methods since all parsing is already done
     * @param item
     * @param dataModel
     * @return the Item PK
     */
    public static ItemPOJOPK getItemPOJOPK(DataClusterPOJOPK dataClusterPOJOPK, Element item, Document dataModel)
    	throws TransformerException
    {	
		String conceptName = item.getLocalName();
		String[] itemKeyValues = null;
		try {    
			XSDKey conceptKey = Util.getBusinessConceptKey(
	    		dataModel,
				conceptName
			);
			//	get key values
			itemKeyValues = Util.getItemKeyValues(
				item,
				conceptKey
			);
		}catch (TransformerException e) {
    	    String err = "Unable to get item PK for the item "+item.getLocalName()
			+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    throw new TransformerException(err);
		}    
		return new ItemPOJOPK(dataClusterPOJOPK, conceptName,itemKeyValues);
    }

    
    /*
    public static Document getExtractorXSLT(Document original) throws XtentisException{
    	
    	String prefix = original.getDocumentElement().getPrefix();
    	String topXpath = Util.getTopBusinessConceptPath(original);
    	
    	if (topXpath == null) {
    		throw new XtentisException("Unable to build Extractor XSLT: the Concept cannot be found");
    	}
    	
    	String rootAttrs = "";
    	NamedNodeMap attsMap = original.getDocumentElement().getAttributes();
    	int len = attsMap.getLength();
    	for (int i =0; i<len; i++) {
    		Attr att = (Attr)attsMap.item(i);
    		rootAttrs +=" "+att.getName()+"='"+att.getValue()+"'"; 
    	}
    	

    	topXpath = topXpath.replace('"','\'');
    	String xsl = 
			"<?xml version='1.0' encoding='UTF-8'?>"+
			"<xs:stylesheet "+rootAttrs+">"+
			"    <xs:output method='xml' indent='yes' omit-xml-declaration='yes' standalone='yes'/>"+
			"    <xs:template mode='extract' match='*'><xs:copy-of select='.'/></xs:template>"+
			"   <xs:template match='/'><xs:apply-templates mode='extract' select=\""+topXpath+"\"/></xs:template>"+    
			"</xs:stylesheet>";
    	
    	xsl = xsl.replaceAll("xs:",prefix+":");
    	
    	return Util.parse(xsl);
    	
    }
    */
    
    
    /**
	 * Returns a namespaced root element of a document
	 * Useful to create a namespace holder element
	 * @param namespace
	 * @return the root Element
	 */
	public static Element getRootElement(String elementName, String namespace, String prefix) throws TransformerException{
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
    	    throw new TransformerException(err);    
    	}
    	
    	return rootNS;
	}
	
	/**
	 * Generates an xml string from a node 
	 * (not pretty formatted)
	 * @param n the node
	 * @return the xml string
	 * @throws TransformerException
	 */
	public static String nodeToString(Node n) throws TransformerException{
		return nodeToString(n,true);
	}
	/**
	 * Generates an xml string from a node with or without the xml declaration 
	 * (not pretty formatted)
	 * @param n the node
	 * @return the xml string
	 * @throws TransformerException
	 */
	public static String nodeToString(Node n, boolean omitXMLDeclaration) throws TransformerException{
       	StringWriter sw = new StringWriter();
       	Transformer transformer = TransformerFactory.newInstance().newTransformer();       	
       	if (omitXMLDeclaration)
       		transformer.setOutputProperty("omit-xml-declaration","yes");
       	else
       		transformer.setOutputProperty("omit-xml-declaration","no");
       	transformer.setOutputProperty("indent","yes");
       	transformer.transform(
				new DOMSource(n),
				new StreamResult(sw)
				);
       	if (sw==null) return null;       	
		return sw.toString().replaceAll("\r\n", "\n");
	}
	
	/**
	 * Get a nodelist from an xPath
	 * @throws XtentisException
	 */
	public static NodeList getNodeList(Document d, String xPath) throws XtentisException{
		return getNodeList(d.getDocumentElement(),xPath,null,null);
	}
	/**
	 * Get a nodelist from an xPath
	 * @throws XtentisException
	 */
	public static NodeList getNodeList(Node contextNode, String xPath) throws XtentisException{
		return getNodeList(contextNode,xPath,null,null);
	}
	
	/**
	 * Get a nodelist from an xPath
	 * @throws XtentisException
	 */
	public static NodeList getNodeList(Node contextNode, String xPath, String namespace, String prefix) throws XtentisException{
		try {
		    XObject xo = XPathAPI.eval(
	    		contextNode,
				xPath,
				(namespace == null) ? contextNode : Util.getRootElement("nsholder",namespace,prefix)
		    );
		    if (xo.getType() != XObject.CLASS_NODESET) return null;
		    return xo.nodelist();
    	} catch (TransformerException e) {
    	    String err = "Unable to get the Nodes List for xpath '"+xPath+"'"
    	    	+((contextNode==null) ? "" : " for Node "+contextNode.getLocalName())
    			+": "+e.getLocalizedMessage();
    		throw new XtentisException(err);
    	}    	    	
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
			res+=(strings[i] == null ? "" : strings[i]);
		}
		return res;
	}
	
	private static Pattern conceptFromPathPattern = Pattern.compile("^/*(.*?)[\\[|/].*");
	/**
	 * Returns the first part - eg. the concept - from the path
	 * @param path
	 * @return
	 * 		The concept extracted from the path
	 */
    public static String getConceptFromPath(String path) {
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = conceptFromPathPattern.matcher(path);
    	if (m.matches()) {
    		return m.group(1);
    	} else {
    		return null;
    	}
    }
    
	/**
	 * Returns the list of items that look like parts numbers
	 * @param value
	 * 		The value to match
	 * @return
	 * 		The list
	 */
    public static Collection<String> getAllPartNumbers(String value) {
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

	/*********************************************************************
	 *     PROFILING
	 *********************************************************************/
	
    private static ArrayList<long[]> timeMarkers = new ArrayList<long[]>(); // [totalTops,lastTime,totalPeriod]
    
    /**
     * Mark and record the time 
     * @param marker
     * @return
     * 		the period of time
     * @throws Exception
     */
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
			period = time - (timeMarkers.get(marker))[1];
		else
			period = time - (timeMarkers.get(marker-1))[1];
		long totalPeriod = (timeMarkers.get(marker))[2] + period;
		long totalTops = (timeMarkers.get(marker))[0] + 1;
		timeMarkers.set(marker, new long[]{totalTops,time,totalPeriod});
		return period;
    }    
	
	public static void resetTimeMarkers() {
		timeMarkers = new ArrayList<long[]>();
	}
	
	public static ArrayList<long[]> getTimeMarkers() {
		return timeMarkers;
	}
	
	/**
	 * Dumps the time markers to the console
	 * @param msg
	 */
	public static void dumpTimeMarkers(String msg) {
		System.out.println("TIME MARKERS: "+msg);
		int i = 0;
		long totalProcessing = 0;
		for (Iterator<long[]> iter = timeMarkers.iterator(); iter.hasNext(); ) {
			long[] values = iter.next();
			if (i>0) totalProcessing+=values[2];
			System.out.println(
					"Marker "+(i++)+":"
					+"tops: "+values[0]+" -- "
					+"totalPeriods: "+values[2]+" -- "
					+"average: "+(values[2]/values[0])
			);
		}
		if (i>0)
			System.out.println("Total Processing: "+totalProcessing+" -- average: "+totalProcessing/(timeMarkers.get(0))[0]);
	}
	
	
    
	/*********************************************************************
	 *     SUBJECT - AUTHENTICATION
	 *********************************************************************/

	public static Subject getActiveSubject() throws XtentisException{
		//Get active Subject
		try {
			String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";
			return (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);
		} catch (Exception e) {
			throw new XtentisException(e.getMessage());
		}
		
		/*
		InitialContext ictx = new InitialContext();
		JaasSecurityManager jsm = (JaasSecurityManager) ictx.lookup("java:jaas/xtentisSecurity");		
		return jsm.getActiveSubject();
		*/
	}
	
	/**
	 * Extracts the username of the logged user from the {@link Subject}
	 * @param subject
	 * @return
	 * 		The username
	 * @throws XtentisException
	 */
	public static String getUsernameFromSubject(Subject subject) throws XtentisException{
		Set<Principal> set = subject.getPrincipals();
		for (Iterator<Principal> iter = set.iterator(); iter.hasNext(); ) {
			Principal principal = iter.next();
			if (! (principal instanceof Group)) {
				return principal.getName();
			}
		}
		return null;
	}
	/**
	 * @return
	 *   the username/password
	 */
	public static String getUsernameAndPasswordToken() {
		String token=null;
		try {
        	String userName=null;
        	String password=null;
			Subject subject=LocalUser.getCurrentSubject();
			Set<Principal> set = subject.getPrincipals();
			for (Iterator<Principal> iter = set.iterator(); iter.hasNext(); ) {
				Principal principal = iter.next();
				if (principal instanceof Group) {
					Group group = (Group) principal;
					if("Username".equals(group.getName())) {
						if (group.members().hasMoreElements()) {
							userName=group.members().nextElement().getName();
						}
					}else if("Password".equals(group.getName())){
						if (group.members().hasMoreElements()) {
							password=group.members().nextElement().getName();
						}
					}
				}
			}//for
			if(userName==null)userName="";
			if(password==null)password="";
			token=userName+"/"+password;
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		return token;
	}

	/**
	 * Extract the role names of the logged user from the Subject
	 * @param subject
	 * @return
	 * 		The collection of roles
	 * @throws XtentisException
	 */
	public static Collection<String> getRoleNamesFromSubject(Subject subject) throws XtentisException{
		ArrayList<String> roleNames = new ArrayList<String>();
		Set<Principal> set = subject.getPrincipals();
		for (Iterator<Principal> iter = set.iterator(); iter.hasNext(); ) {
			Principal principal = iter.next();
			if (principal instanceof Group) {
				Group group = (Group) principal;
				//@see XtentisMDMLoginModule
				if ("Roles".equals(group.getName())) {
					Enumeration<? extends Principal> principals = group.members();
					while (principals.hasMoreElements()) {
						roleNames.add(((Principal)principals.nextElement()).getName());
					}
				}
			}
		}
		return roleNames;
	}
	
	
	/*********************************************************************
	 *     DATES
	 *********************************************************************/
	
	public static String getTimestamp() {
		return getTimestamp(new Date());
	}
	
	public static String getTimestamp(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss.SSS' 'z");
		return sdf.format(d);
	}

	

    
	/*********************************************************************
	 *      XML STUFF
	 *********************************************************************/
	private static Pattern regexLT = Pattern.compile("&lt;");
	private static Pattern regexGT = Pattern.compile("&gt;");
	private static Pattern regexAMP = Pattern.compile("&amp;");
    
	public static String xmlDecode(String string) {
		if (string == null) return null;
    	string = regexLT.matcher(string).replaceAll("<");
    	string = regexGT.matcher(string).replaceAll(">");
    	string = regexAMP.matcher(string).replaceAll("&");
    	return string;
    }

	private static Pattern regexSup = Pattern.compile(">");
	private static Pattern regexInf = Pattern.compile("<");
	private static Pattern regexEt = Pattern.compile("&");
	
	public static String xmlEncode(String string) {
    	string = regexSup.matcher(string).replaceAll("&gt;");
    	string = regexInf.matcher(string).replaceAll("&lt;");
    	string = regexEt.matcher(string).replaceAll("&amp;");
    	return string;
    }


	/*********************************************************************
	 * 
	 *      LOCAL HOME GETTERS 
	 *      This cache system requires a JBoss restart on every core deployment 
	 *      
	 *********************************************************************/
	
	//The only Static HashMap around (hopefully)
	private static HashMap<String,EJBLocalHome> localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();

	public static void flushLocalHomes() throws NamingException{
		localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();
	}
	
	public static EJBLocalHome getLocalHome(String jndi) throws NamingException{
		EJBLocalHome localHome = null;
		if (true) {
			localHome = localHomes.get(jndi);
			if (localHome == null) {
				localHome = (EJBLocalHome)new InitialContext().lookup(jndi);
				localHomes.put(jndi, localHome);
			}
		} else {
			localHome = (EJBLocalHome)new InitialContext().lookup(jndi);
		}
//		dumpClass(localHome.getClass());
		return localHome;
	}
	public static RoleCtrlLocalHome getRoleCtrlLocalHome() throws NamingException {
		return (RoleCtrlLocalHome) getLocalHome(RoleCtrlLocalHome.JNDI_NAME);
	}
	public static RoleCtrlLocal getRoleCtrlLocal() throws NamingException,CreateException {
		return getRoleCtrlLocalHome().create();
	}
	
		
	public static UniverseCtrlLocalHome getUniverseCtrlLocalHome() throws NamingException {
		return (UniverseCtrlLocalHome) getLocalHome(UniverseCtrlLocalHome.JNDI_NAME);
	}
	public static UniverseCtrlLocal getUniverseCtrlLocal() throws NamingException,CreateException {
		return getUniverseCtrlLocalHome().create();
	}
		
	public static RoutingOrderV2CtrlLocal getRoutingOrderV2CtrlLocal() throws NamingException,CreateException {
		return getRoutingOrderV2CtrlLocalHome().create();
	}		
	public static RoutingRuleCtrlLocalHome getRoutingRuleCtrlLocalHome() throws NamingException {
		return (RoutingRuleCtrlLocalHome) getLocalHome(RoutingRuleCtrlLocalHome.JNDI_NAME);
	}
	public static RoutingRuleCtrlLocal getRoutingRuleCtrlLocal() throws NamingException,CreateException {
		return getRoutingRuleCtrlLocalHome().create();
	}		
	
	public static RoutingOrderV2CtrlLocalHome getRoutingOrderV2CtrlLocalHome() throws NamingException {
		return (RoutingOrderV2CtrlLocalHome) getLocalHome(RoutingOrderV2CtrlLocalHome.JNDI_NAME);
	}	
	public static StoredProcedureCtrlLocalHome getStoredProcedureCtrlLocalHome() throws NamingException {
		return (StoredProcedureCtrlLocalHome) getLocalHome(StoredProcedureCtrlLocalHome.JNDI_NAME);
	}
	public static StoredProcedureCtrlLocal getStoredProcedureCtrlLocal() throws NamingException,CreateException {
		return getStoredProcedureCtrlLocalHome().create();
	}
	
	public static ServiceLocalHome getServiceLocalHome() throws NamingException {
		return (ServiceLocalHome) getLocalHome(ServiceLocalHome.JNDI_NAME);
	}
	
	public static ItemCtrl2LocalHome getItemCtrl2LocalHome() throws NamingException {
		return (ItemCtrl2LocalHome) getLocalHome(ItemCtrl2LocalHome.JNDI_NAME);
	}
	public static ItemCtrl2Local getItemCtrl2Local() throws NamingException,CreateException {
		return getItemCtrl2LocalHome().create();
	}
	
	public static DroppedItemCtrlLocalHome getDroppedItemCtrlLocalHome() throws NamingException {
		return (DroppedItemCtrlLocalHome) getLocalHome(DroppedItemCtrlLocalHome.JNDI_NAME);
	}
	public static DroppedItemCtrlLocal getDroppedItemCtrlLocal() throws NamingException,CreateException {
		return getDroppedItemCtrlLocalHome().create();
	}

	public static DataModelCtrlLocalHome getDataModelCtrlLocalHome() throws NamingException {
		return (DataModelCtrlLocalHome) getLocalHome(DataModelCtrlLocalHome.JNDI_NAME);
	}
	public static DataModelCtrlLocal getDataModelCtrlLocal() throws NamingException,CreateException {
		return getDataModelCtrlLocalHome().create();
	}

	public static XmlServerSLWrapperLocal getXmlServerCtrlLocal() throws XtentisException {
        XmlServerSLWrapperLocal server = null;
		try {
			server  =  ((XmlServerSLWrapperLocalHome)getLocalHome(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();        				
		} catch (Exception e) {
			String err = "Error : unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err,e);
			throw new XtentisException(err);
		}
		return server;
	}
	public static DataClusterCtrlLocalHome getDataClusterCtrlLocalHome() throws NamingException {
		return (DataClusterCtrlLocalHome) getLocalHome(DataClusterCtrlLocalHome.JNDI_NAME);
	}
	public static DataClusterCtrlLocal getDataClusterCtrlLocal() throws NamingException,CreateException {
		return getDataClusterCtrlLocalHome().create();
	}	

	public static ViewCtrlLocalHome getViewCtrlLocalHome() throws NamingException {
		return (ViewCtrlLocalHome) getLocalHome(ViewCtrlLocalHome.JNDI_NAME);
	}
	public static ViewCtrlLocal getViewCtrlLocal() throws NamingException,CreateException {
		return getViewCtrlLocalHome().create();
	}		



	@Deprecated
	public static TransformerCtrlLocalHome getTransformerCtrlLocalHome() throws NamingException {
		return (TransformerCtrlLocalHome) getLocalHome(TransformerCtrlLocalHome.JNDI_NAME);
	}
	@Deprecated
	public static TransformerCtrlLocal getTransformerCtrlLocal() throws NamingException,CreateException {
		return getTransformerCtrlLocalHome().create();
	}	
	public static TransformerV2CtrlLocalHome getTransformerV2CtrlLocalHome() throws NamingException {
		return (TransformerV2CtrlLocalHome) getLocalHome(TransformerV2CtrlLocalHome.JNDI_NAME);
	}
	public static TransformerV2CtrlLocal getTransformerV2CtrlLocal() throws NamingException,CreateException {
		return getTransformerV2CtrlLocalHome().create();
	}	
	

	
	public static MenuCtrlLocalHome getMenuCtrlLocalHome() throws NamingException {
		return (MenuCtrlLocalHome) getLocalHome(MenuCtrlLocalHome.JNDI_NAME);
	}
	public static MenuCtrlLocal getMenuCtrlLocal() throws NamingException,CreateException {
		return getMenuCtrlLocalHome().create();
	}		
	
	public static BackgroundJobCtrlLocalHome getBackgroundJobCtrlLocalHome() throws NamingException {
		return (BackgroundJobCtrlLocalHome) getLocalHome(BackgroundJobCtrlLocalHome.JNDI_NAME);
	}
	public static BackgroundJobCtrlLocal getBackgroundJobCtrlLocal() throws NamingException,CreateException {
		return getBackgroundJobCtrlLocalHome().create();
	}		
	
	public static ConfigurationInfoCtrlLocalHome getConfigurationInfoCtrlLocalHome() throws NamingException {
		return (ConfigurationInfoCtrlLocalHome) getLocalHome(ConfigurationInfoCtrlLocalHome.JNDI_NAME);
	}
	public static ConfigurationInfoCtrlLocal getConfigurationInfoCtrlLocal() throws NamingException,CreateException {
		return getConfigurationInfoCtrlLocalHome().create();
	}		
		

	public static RoutingEngineV2CtrlLocalHome getRoutingEngineV2CtrlLocalHome() throws NamingException {
		return (RoutingEngineV2CtrlLocalHome) getLocalHome(RoutingEngineV2CtrlLocalHome.JNDI_NAME);
	}
	public static RoutingEngineV2CtrlLocal getRoutingEngineV2CtrlLocal() throws NamingException,CreateException {
		return getRoutingEngineV2CtrlLocalHome().create();
	}	
	
	
//	private static ConnectionFactory cachedConnectionFactory = null;
    public static Connection getConnection(String JNDIName) throws XtentisException {
    	try {
//    		if (cachedConnectionFactory == null) - Removed - no more caching of connections
//    			cachedConnectionFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
//    		return cachedConnectionFactory.getConnection();
    		return ((ConnectionFactory)(new InitialContext()).lookup(JNDIName)).getConnection();
    	} catch (Exception e) {
    		String err= "JNDI lookup error: "+e.getClass().getName() + ": " + e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(Util.class).error(err);
			throw new XtentisException(err);
    	}
    }
    


//	@Deprecated
//	public static TransformerCtrlLocalHome getTransformerCtrlLocalHome() throws NamingException {
//		return (TransformerCtrlLocalHome) getLocalHome(TransformerCtrlLocalHome.JNDI_NAME);
//	}
//	@Deprecated
//	public static TransformerCtrlLocal getTransformerCtrlLocal() throws NamingException,CreateException {
//		return getTransformerCtrlLocalHome().create();
//	}


	
	/***********************************************************************************************************
	 * Typed Content Manipulation 
	 ***********************************************************************************************************/
	
	private static Pattern extractCharsetPattern=Pattern.compile(".*charset\\s*=[\"|']?(.+)[\"|']([\\s|;].*)?");
	/**
	 * Extract the charset of a content type<br>
	 * e.g 'utf-8' in 'text/xml; charset="utf-8"'
	 * @param contentType
	 * @return the charset
	 */
	public static String extractCharset(String contentType) {
		String charset="UTF8";
		Matcher m = extractCharsetPattern.matcher(contentType);
		if (m.matches()) {
			charset = m.group(1).trim().toUpperCase();
		}
		//if ("UTF-8".equals(charset)) charset = "UTF8";
		return charset;
	}

	/**
	 * Extract the MIME type and sub type of a content type<br>
	 * e.g 'text/xml' in 'text/xml; charset="utf-8"'
	 * @param contentType
	 * @return the MIME Type and SubType
	 */
	public static String extractTypeAndSubType(String contentType) {
		if (contentType == null ) return null;
		return contentType.split(";")[0].trim().toLowerCase();
	}

	/**
	 * Extracts a byte array from an InputStream
	 * @param is
	 * @return the byte array
	 * @throws IOException
	 */
	public static byte[] getBytesFromStream(InputStream is) throws IOException{
		if (is==null) return null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		int b;
		while ((b=is.read())!=-1) bos.write(b);
		return bos.toByteArray();
	}

	
	/*********************************************************************
	 * 
	 *      GUID Generator
	 *      
	 *********************************************************************/

	
	/** Cached per JVM server IP. */
	private static String hexServerIP = null;

	// initialise the secure random instance
	private static final java.security.SecureRandom seeder = new java.security.SecureRandom();

	/**
	 * A 32 byte GUID generator (Globally Unique ID). These artificial keys SHOULD <strong>NOT </strong> be seen by the user,
	 * not even touched by the DBA but with very rare exceptions, just manipulated by the database and the programs.
	 *
	 * Usage: Add an id field (type java.lang.String) to your EJB, and add setId(XXXUtil.generateGUID(this)); to the ejbCreate method.
	 */
	public static final String generateGUID(Object o) {
		StringBuffer tmpBuffer = new StringBuffer(16);
		if (hexServerIP == null) {
			java.net.InetAddress localInetAddress = null;
			try {
				// get the inet address

				localInetAddress = java.net.InetAddress.getLocalHost();
			}
			catch (java.net.UnknownHostException uhe) {
				System.err.println("JobUtil: Could not get the local IP address using InetAddress.getLocalHost()!");
				// todo: find better way to get around this...
				uhe.printStackTrace();
				return null;
			}
			byte serverIP[] = localInetAddress.getAddress();
			hexServerIP = hexFormat(getInt(serverIP), 8);
		}

		String hashcode = hexFormat(System.identityHashCode(o), 8);
		tmpBuffer.append(hexServerIP);
		tmpBuffer.append(hashcode);

		long timeNow= System.currentTimeMillis();
		int timeLow = (int)timeNow & 0xFFFFFFFF;
		int node = seeder.nextInt();

		StringBuffer guid = new StringBuffer(32);
		guid.append(hexFormat(timeLow, 8));
		guid.append(tmpBuffer.toString());
		guid.append(hexFormat(node, 8));
		return guid.toString();
	}

	private static int getInt(byte bytes[]) {
		int i = 0;
		int j = 24;
		for (int k = 0; j >= 0; k++) {
			int l = bytes[k] & 0xff;
			i += l << j;
			j -= 8;
		}
		return i;
	}

	private static String hexFormat(int i, int j) {
		String s = Integer.toHexString(i);
		return padHex(s, j) + s;
	}

	private static String padHex(String s, int i) {
		StringBuffer tmpBuffer = new StringBuffer();
		if (s.length() < i) {
			for (int j = 0; j < i - s.length(); j++) {
				tmpBuffer.append('0');
			}
		}
		return tmpBuffer.toString();
	}
	
	public static ItemPOJO getItem(String datacluster, String xml)throws Exception{
		String projection = xml;
		Element root = Util.parse(projection).getDocumentElement();
		
		String concept = root.getLocalName();

		DataModelPOJO dataModel  = Util.getDataModelCtrlLocal().getDataModel(
					new DataModelPOJOPK(datacluster)
			);
		Document schema=Util.parse(dataModel.getSchema());
        XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(
        		schema,
				concept					
		);
       
		//get key values
		String[] ids = com.amalto.core.util.Util.getKeyValuesFromItem(
   			root,
				conceptKey
		);				
		DataClusterPOJOPK dcpk = new DataClusterPOJOPK(datacluster);
		ItemPOJOPK itemPOJOPK=new ItemPOJOPK(dcpk,concept, ids);
		return ItemPOJO.load(itemPOJOPK);
	}
	public static String beforeSaving(String concept,String xml, String resultUpdateReport)throws Exception{
		//check before saving transformer
		boolean isBeforeSavingTransformerExist=false;
		Collection<TransformerV2POJOPK> wst = getTransformerV2CtrlLocal().getTransformerPKs("*");
		for(TransformerV2POJOPK id: wst){
			if(id.getIds()[0].equals("beforeSaving_"+concept)){
				isBeforeSavingTransformerExist=true;
				break;
			}
		}
		//call before saving transformer
		if(isBeforeSavingTransformerExist){
			
			try {
				final String RUNNING = "XtentisWSBean.executeTransformerV2.running";
				TransformerContext context = new TransformerContext(
						new TransformerV2POJOPK("beforeSaving_" + concept));
				String exchangeData = mergeExchangeData(xml,resultUpdateReport);
				//String exchangeData = resultUpdateReport;
				context.put(RUNNING, Boolean.TRUE);
				TransformerV2CtrlLocal ctrl = getTransformerV2CtrlLocal();
				TypedContent wsTypedContent = new TypedContent(
						exchangeData
								.getBytes("UTF-8"),
						"text/xml; charset=utf-8");
				ctrl.execute(
						context, 
						wsTypedContent,
						new TransformerCallBack() {
							public void contentIsReady(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.contentIsReady() ");
							}
							public void done(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.done() ");
								context.put(RUNNING, Boolean.FALSE);
							}
						}
				);
				while (((Boolean)context.get(RUNNING)).booleanValue()) {
					Thread.sleep(100);
				}				
				//TODO process no plug-in issue
				String outputErrorMessage = "";
				//Scan the entries - in priority, taka the content of the 'output_error_message' entry, 
				for(Entry<String, TypedContent> entry: context.getPipelineClone().entrySet()){

					if ("output_error_message".equals(entry.getKey()	)) {
						outputErrorMessage = new String(entry.getValue().getContentBytes(), "UTF-8");
						break;
					}
				}
				//handle error message
				if (outputErrorMessage.length() > 0) {

					String errorCode = "";
					String errorMessage = "";
					Pattern pattern = Pattern
							.compile("<error code=['\042](.*)['\042]>(.*)</error>");
					Matcher matcher = pattern.matcher(outputErrorMessage);
					while (matcher.find())

					{
						errorCode = matcher.group(1);
						errorMessage = matcher.group(2);

					}
					if (!errorCode.equals("") && !errorCode.equals("0")) {
						errorMessage = "ERROR_3:" + errorMessage;
						return errorMessage;
					}

				}
				else
				{
					return "Failed to retrieve the before saving status. The record was not saved";
				}
			} catch (Exception e) {
				return e.getLocalizedMessage();
			}
		}
		return null;
	}	
	
	public static boolean beforeDeleting(String clusterName,String concept,String[] ids)throws Exception{
		//check before deleting transformer
		boolean isBeforeDeletingTransformerExist=false;
		Collection<TransformerV2POJOPK> transformers = getTransformerV2CtrlLocal().getTransformerPKs("*");
		for(TransformerV2POJOPK id: transformers){
			if(id.getIds()[0].equals("beforeDeleting_"+concept)){
				isBeforeDeletingTransformerExist=true;
				break;
			}
		}
		
		if(!isBeforeDeletingTransformerExist)return false;
		
		//call before deleting transformer
		
		final String RUNNING = "XtentisWSBean.executeTransformerV2.beforeDeleting.running";
	    TransformerContext context = new TransformerContext(new TransformerV2POJOPK("beforeDeleting_" + concept));
		context.put(RUNNING, Boolean.TRUE);
		TransformerV2CtrlLocal ctrl = getTransformerV2CtrlLocal();
		TypedContent wsTypedContent = new TypedContent(
				        buildItemPKString(clusterName,concept,ids).getBytes("UTF-8"),
						"text/xml; charset=utf-8");
				
		ctrl.execute(
						context, 
						wsTypedContent,
						new TransformerCallBack() {
							public void contentIsReady(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.beforeDeleting.contentIsReady() ");
							}
							public void done(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.beforeDeleting.done() ");
								context.put(RUNNING, Boolean.FALSE);
							}
						}
				);
				
		while (((Boolean)context.get(RUNNING)).booleanValue()) {
					Thread.sleep(100);
		}
				
		//TODO Scan the entries - in priority, taka the content of the specific entry
		
		return true;
	}
	public static String buildItemPKString(String clusterName,String conceptName,String[] ids) {
		
		 StringBuffer itemPKXmlString = new StringBuffer();
		
		 if(clusterName==null||clusterName.length()==0)return itemPKXmlString.toString();
		 if(conceptName==null||conceptName.length()==0)return itemPKXmlString.toString();
		 if(ids==null)return itemPKXmlString.toString();
		 
		 itemPKXmlString.append("<item-pOJOPK><concept-name>")
		                .append(conceptName)
		                .append("</concept-name><ids>")
		                .append(joinStrings(ids, "."))
		                .append("</ids><data-cluster-pOJOPK><ids>")
		                .append(clusterName)
		                .append("</ids></data-cluster-pOJOPK></item-pOJOPK>");
		                
      return itemPKXmlString.toString();
	}
	public static HashMap<String, UpdateReportItem> compareElement(String parentPath,Node newElement, Node oldElement )throws Exception{
		HashMap<String, UpdateReportItem> map =new LinkedHashMap<String, UpdateReportItem>();
		Set<String> xpaths=getXpaths(parentPath, newElement);
	    JXPathContext jxpContextOld = JXPathContext.newContext ( oldElement );
	    jxpContextOld.setLenient(true);
	    JXPathContext jxpContextNew = JXPathContext.newContext ( newElement );
	    jxpContextNew.setLenient(true);
	    String concept=newElement.getLocalName();
		for(String xpath: xpaths){			
			NodeList listnew=getNodeList(newElement, xpath);
			NodeList listold=getNodeList(oldElement, xpath);
			int num=Math.max(listnew.getLength(), listold.getLength());
			if(xpath.startsWith("/"+concept+"/")) {
				xpath=xpath.replaceFirst("/"+concept+"/", "");
			}
			if(num>1){//list
				for(int i=1; i<=num; i++){
					String xpath1=xpath+"["+i+"]";
					if(i>1){
						String fixPath=getFixedListXpath(xpath,newElement,oldElement,i);
						if(fixPath!=null)xpath1=fixPath; 
					}

					String oldvalue=(String)jxpContextOld.getValue(xpath1,String.class);
					String newvalue=(String)jxpContextNew.getValue(xpath1,String.class);
					if(newvalue!=null && newvalue.length()>0 && !newvalue.equals(oldvalue)|| oldvalue!=null && oldvalue.length()>0 && !oldvalue.equals(newvalue)){
						UpdateReportItem item =new UpdateReportItem(xpath1, oldvalue, newvalue);
						map.put(xpath1, item);
					}				
				}
			}else{
				String oldvalue=(String)jxpContextOld.getValue(xpath,String.class);
				String newvalue=(String)jxpContextNew.getValue(xpath,String.class);
				if(newvalue!=null && newvalue.length()>0 && !newvalue.equals(oldvalue)|| oldvalue!=null && oldvalue.length()>0 && !oldvalue.equals(newvalue)){
					UpdateReportItem item =new UpdateReportItem(xpath, oldvalue, newvalue);
					map.put(xpath, item);
				}
			}
		}
		return map;
	}
	/**
	 * this method only used in a list like a/b/c/d/e, e.g index=2, the right xpath maybe one of a/b/c/d[2]/e,a/b/c[2]/d/e,a/b/c[2]/d/e,a/b[2]/c/e,a[2]/b/c/d/e
	 *  that is null in oldElement
	 * @param xpath
	 * @param jxpContextOld
	 * @param jxpContextNew
	 * @param index
	 * @return
	 */
	private static String getFixedListXpath(String xpath, Node newElement, Node oldElement,int index)throws Exception{
		if(xpath.endsWith("/"))xpath=xpath.substring(0,xpath.length()-1);
		int pos=xpath.lastIndexOf('/');
		if(pos>0){
			String parentPath=xpath.substring(0,pos);
			String lastPath=xpath.substring(pos+1);
			NodeList listnew=getNodeList(newElement, parentPath);
			NodeList listold=getNodeList(oldElement, parentPath);
			int num=Math.max(listnew.getLength(), listold.getLength());
			if(num>1){//list
				String[] paths=parentPath.split("/");				
				for(int i=0; i<paths.length; i++){
					String str=paths[i];
					if(str.matches("\\w+\\[\\d\\]")) {
						continue;
					}
					StringBuffer sb=new StringBuffer();
					for(int j=0; j<paths.length; j++) {
						if(i==j) {
							sb.append(paths[j]+"["+index+"]/");
						}else {
							sb.append(paths[j]+"/");
						}
					}
					sb.append(lastPath);
					JXPathContext jxpContextOld = JXPathContext.newContext ( oldElement );
				    jxpContextOld.setLenient(true);
				    if(jxpContextOld.getValue(sb.toString())!=null) {
				    	return sb.toString();
				    }
				}

			}
		}
		return null;
	}	
	private static Set<String > getXpaths(String parentPath,Node node)throws Exception{
		Set<String> set=new LinkedHashSet<String>();
		NodeList list=node.getChildNodes();
		for(int i=0; i<list.getLength(); i++){
			Node n=list.item(i);
			
			if(n.getNodeType() == Node.ELEMENT_NODE){
				String nName=n.getNodeName();
				String xPath=parentPath+"/"+nName;		
				NodeList list1=getNodeList(node, xPath);
				int j=1;
				for(int ii=0; ii<list1.getLength(); ii++){
					Node node1=list1.item(ii);
					if(node1.getNodeType() == Node.ELEMENT_NODE){
						if(getElementNum(list1)>1){
							j++;
						}
					}
				}
				if( !hasChildren(n)){
					set.add(xPath);
				}else{
					//if list
					if(j>1){
						for(int ii=1; ii<=j;ii++){
							set.addAll(getXpaths(xPath+"["+ii+"]", n));
						}
					}else{
						set.addAll(getXpaths(xPath, n));
					}
				}
			}
		}
		return set;
	}
	static 		AbstractFactory factory = new AbstractFactory(){
		public boolean createObject(JXPathContext context, Pointer pointer, Object parent, String name, int index)
		 {
			 if (parent instanceof Node){
				 try{
					 Node node = (Node) parent;					 
					 Document doc1 = node.getOwnerDocument();
					 Element e = doc1.createElement(name);					 
					 if(index>0 ){ //list 
						 Pointer p=context.getRelativeContext(pointer).getPointer(name+"["+(index)+"]");
						 Node curNode=(Node)p.getNode();
						 if(curNode!=null){
							 if(curNode.getNextSibling()!=null){								 
								 node.insertBefore(e, curNode.getNextSibling());
							 }else{								
								 node.appendChild(e);
							 }
						 }else{
							 node.appendChild(e);
						 }		 
					 }else{
						 node.appendChild(e);
					 }
					 return true;}
					 catch(Exception e){
					 return false;
					 }
			 }else 
			 		return false;
		 }
		 public boolean declareVariable(JXPathContext context, String name) {return false; }
	};

	/**
	 * update the element according to updatedpath
	 * @param parentPath
	 * @param old
	 * @param updatedpath
	 * @return
	 * @throws Exception
	 */
	public static Node updateElement(String parentPath,Node old, HashMap<String, UpdateReportItem> updatedpath)throws Exception{
		if( updatedpath.size()==0) return old;
		//use JXPathContext to update the old element
	    JXPathContext jxpContext = JXPathContext.newContext ( old );
	    jxpContext.setLenient(true);
			 
		jxpContext.setFactory(factory);
		String concept=old.getLocalName();
		for( Map.Entry<String, UpdateReportItem> entry:updatedpath.entrySet()){
			String xpath= entry.getValue().getPath();
			if(xpath.startsWith("/"+concept+"/")) {
				xpath=xpath.replaceFirst("/"+concept+"/", "");
			}			
			jxpContext.createPathAndSetValue(xpath, entry.getValue().newValue);
		}		
		return (Node)jxpContext.getContextBean();
	}	
	private static UpdateReportItem getUpdatedItem(HashMap<String, UpdateReportItem> updatedpath, String xpath){
		for( Map.Entry<String, UpdateReportItem> entry:updatedpath.entrySet()){
			if(entry.getKey().startsWith(xpath)){
				return entry.getValue();
			}
		}
		return null;
	}
	private static boolean hasChildren(Node node){
		NodeList list=node.getChildNodes();
		for(int i=0; i<list.getLength(); i++){
			if(list.item(i).getNodeType() == Node.ELEMENT_NODE){
				return true;
			}
		}
		return false;
	}
	private static Node getElementChild(Node parent, int index){
		NodeList list =parent.getChildNodes();
		int j=0;
		for(int i=0; i<list.getLength(); i++){
			if(list.item(i).getNodeType() == Node.ELEMENT_NODE){
				if(j== index) return list.item(i);
				j++;
			}
		}
		return null;
	}	
	private static int getElementNum(NodeList list){
		int j=0;
		for(int i=0; i<list.getLength(); i++){
			if(list.item(i).getNodeType() == Node.ELEMENT_NODE){
				j++;
			}
		}
		return j;
	}
	public static String mergeExchangeData(String xml,
			String resultUpdateReport) {
		String exchangeData="<exchange>\n";
		exchangeData+="<report>"+resultUpdateReport+"</report>";
		exchangeData+="\n";
		exchangeData+="<item>"+xml+"</item>";
		exchangeData+="\n</exchange>";
		return exchangeData;
	}	
	
	public static String createUpdateReport(String[] ids, String concept,
			String operationType, HashMap<String, UpdateReportItem> updatedPath,String dataModelPK, String dataClusterPK )
			throws Exception {
		String username="";
		String revisionId="";
		

		try {
			
	    	
			username=LocalUser.getLocalUser().getUsername();
	    	UniversePOJO pojo=LocalUser.getLocalUser().getUniverse();
	    	if(pojo!=null)revisionId=pojo.getConceptRevisionID(concept);
	    	
		} catch (Exception e1) {
			e1.printStackTrace();
			throw e1;
		}
		
		String key = "";
		if(ids!=null){
			for (int i = 0; i < ids.length; i++) {
				key+=ids[i];
				if(i!=ids.length-1) key+=".";
			}
		}
		String xml2 = "" +
			"<Update>"+
			"<UserName>"+username+"</UserName>"+
            "<Source>genericUI</Source>"+
            "<TimeInMillis>"+System.currentTimeMillis()+"</TimeInMillis>"+
            "<OperationType>"+StringEscapeUtils.escapeXml(operationType)+"</OperationType>"+
            "<RevisionID>"+revisionId+"</RevisionID>"+
            "<DataCluster>"+dataClusterPK+"</DataCluster>"+
            "<DataModel>"+dataModelPK+"</DataModel>"+
            "<Concept>"+StringEscapeUtils.escapeXml(concept)+"</Concept>"+
            "<Key>"+StringEscapeUtils.escapeXml(key)+"</Key>";
		if("UPDATE".equals(operationType)){
			Collection<UpdateReportItem> list = updatedPath.values();
			boolean isUpdate=false;
			for (Iterator<UpdateReportItem> iter = list.iterator(); iter.hasNext();) {				
				UpdateReportItem item = iter.next();
				String oldValue=item.getOldValue()==null?"":item.getOldValue();
				String newValue=item.getNewValue()==null?"":item.getNewValue();
				if(newValue.equals(oldValue)) continue; 
		            xml2 += 
		            "<Item>"+
		            "   <path>"+StringEscapeUtils.escapeXml(item.getPath())+"</path>"+
		            "   <oldValue>"+StringEscapeUtils.escapeXml(oldValue)+"</oldValue>"+
		            "   <newValue>"+StringEscapeUtils.escapeXml(newValue)+"</newValue>"+
		           "</Item>"; 		
		            isUpdate=true;
			}     
			if(!isUpdate) return null;  
		}
        xml2 += "</Update>";
		return xml2;
	}

	public static boolean isItemCanVisible(ItemPOJOPK itempk)throws XtentisException {
		return LocalUser.getLocalUser().userItemCanRead(itempk)||LocalUser.getLocalUser().userItemCanWrite(itempk, itempk.getDataClusterPOJOPK().getUniqueId(), itempk.getConceptName());
	}
	
	public static String checkOnVersionCompatibility(WSVersion old) {
		Version version = Version.getVersion(IXtentisWSDelegator.class);
		String oldv=old.getMajor()+"."+old.getMinor()+"."+old.getRevision();
		String newv=version.getMajor()+"."+version.getMinor()+"."+version.getRevision();
		String str="The two MDM Servers is not compatible, one is "+ oldv + ", another is "+newv;
		if(version.getMajor()!=old.getMajor()|| version.getMinor()!=old.getMinor()) {
			return str;
		}
		return null;
	}	
	/*********************************************************************
	 *  TESTS
	 *********************************************************************/
 
 
	public static void testSpellCheck() throws Exception{
// 	String xml = getXML("/home/bgrieder/workspace/com.amalto.total.poc/src/com/amalto/total/poc/data/Amalto2.xml");
//		DataClusterBMP dcb = new DataClusterBMP();
// 	dcb.addToVocabulary(xml);
// 	System.out.println("VOCABULARY:\n"+dcb.getVocabulary());
// 	
// 	System.setProperty("jazzy.config", "com.amalto.core.util.JazzyConfiguration");
// 	
//  SpellDictionary dictionary = new SpellDictionaryHashMap(
//  		new StringReader(dcb.getVocabulary()), 
//				SpellCheckHandler.getPhonetsReader("fr")
//		);
//
//  SpellChecker spellCheck = new SpellChecker(dictionary);
//  SpellCheckHandler handler = new SpellCheckHandler();
//  spellCheck.addSpellCheckListener(handler);
//  
//  String toCheck = "Aalto dNs boneur boner bonh pascl pcheri 123-456 stars crstal";
//  int errors = spellCheck.checkSpelling(new StringWordTokenizer(toCheck.toLowerCase()));
//  if (errors ==0) {
//  	System.out.println("Nothing I can do");
//  	return;
//  }
//  
//  boolean IGNORE_NON_EXISTENT_WORDS = false;
//  //int depth = 4;
//  
//  ArrayList suggestions = new ArrayList();
//		Pattern p = Pattern.compile("\\p{Space}*([^\\p{Space}]{3,}?)\\p{Space}+");
//		Matcher m = p.matcher(" "+toCheck+" ");
//		int pos = 0;
//		while (m.find(pos)) {
//			pos = m.end()-1;
//			String word = m.group(1).trim().toLowerCase();
//			if (IGNORE_NON_EXISTENT_WORDS) {
//				if (handler.getSuggestions().containsKey(word)) {
//					//the spell hcecker di not ignore the word
//					Collection results = (Collection)handler.getSuggestions().get(word);
//					if (results.size()>0) {
//						suggestions.add(new ArrayList(results));
//					} // else we ignore the word because no suggestion
//				} else {
//					//the word exists (or has been  ignored on purpose by the spell checker)
//					//we suggest the original word
//					ArrayList results = new ArrayList();
//					results.add(new Word(word,0));
//					suggestions.add(new ArrayList(results));
//				}
//			} else {		
//				Collection results = (Collection)handler.getSuggestions().get(word);
//				if ((results==null) || (results.size()==0)) { 
//					results = new ArrayList();
//					results.add(new Word(word,0));
//				}
//				suggestions.add(new ArrayList(results));
//			}
//		}
//
//		System.out.println("TO CHECK: "+toCheck);
//		
//		ArrayList proposals = new ArrayList();
//		for (Iterator iter = suggestions.iterator(); iter.hasNext();) {
//			ArrayList sug = (ArrayList) iter.next();
//			ArrayList newProposals = new ArrayList();
//			for (Iterator iterator = sug.iterator(); iterator.hasNext(); ) {
//				Word suggestion = (Word) iterator.next();
//				if (proposals.size()==0) { // first run
//					newProposals.add(suggestion.getWord());
//				} else {
//					for (Iterator iterator2 = proposals.iterator(); iterator2.hasNext(); ) {
//						String proposal = (String) iterator2.next();
//						newProposals.add(proposal+" "+suggestion.getWord());
//					}
//				}
//			} //for suggestions
//			proposals = newProposals;
//		}//for words
//		
//		int i=0;
//		for (Iterator iter = proposals.iterator(); iter.hasNext(); ) {
//			String proposal = (String) iter.next();
//			System.out.println("P"+(++i)+": "+proposal);
//		}

		
		/*
		//iterate to build possibilities
		int proposalNum = 0;
		int firstWord = 0;
		int endWord = 0;
		while (true) {
			
			//build a proposal
		 String proposal = "";
			for (int i = 0; i < words.size(); i++) {
				proposal+="".equals(proposal) ?  "" : " ";
				int currentSugg =((Integer)currentSuggestion.get(i)).intValue(); 
				proposal+=((ArrayList)suggestions.get(i)).get(currentSugg);
			}
			System.out.println("PROPOSAL: "+proposal);
			proposalNum++;
			
			if (proposalNum == depth) break; 
			
			boolean noPossibility = true;
			boolean goOn = true;
			while(goOn) {
			 //try incrementing the first word
			 int currSugg = ((Integer)currentSuggestion.get(firstWord)).intValue();
			 int currMax = ((Integer)maxSuggestion.get(firstWord)).intValue();
			 if (currSugg<currMax) {
			  currentSuggestion.set(firstWord, new Integer(currSugg+1));
			 	noPossibility = false;
			 	goOn=false;
			 	break;
			 }

			 //reset the words
			 for (int i = 0; i < endWord; i++) {
			  currentSuggestion.set(i, new Integer(0));			  
 }
		  firstWord = 0;
		  //try incrementing second word
		  while (goOn) {
		currSugg = ((Integer)currentSuggestion.get(endWord)).intValue();
		currMax = ((Integer)maxSuggestion.get(endWord)).intValue();
		if (currSugg<currMax) {
		 currentSuggestion.set(endWord, new Integer(currSugg+1));
		 noPossibility = false;
		 goOn=false;
		 break;
		}
	 if(++endWord==words.size()) {
		 noPossibility = true; //no more possibility
		 goOn=false;
		 break;			  
	 }
		  }//incrementing the second word
			}//while find next 
			
			if (noPossibility) break;
			
		}//while next proposal
  */
 } 
 
 
//	private static String getXML(String filename) throws Exception{
//	 BufferedReader in = null;
//  in = new BufferedReader(
//  			new InputStreamReader(
//  			  new FileInputStream(filename),
//							"utf-8"
//					)
//		);
//	 String xsl="";
//  String line;
//  while ((line=in.readLine())!=null) xsl+=line+"\n";
//  return xsl;
// }
	
	/**
	 * check current server is Enterprise or Open
	 */
	public static boolean isEnterprise(){
		try{
	    	Object home = null;
	    	InitialContext initialContext = null;
	    	String jndiName="amalto/local/service/workflow";	    	
	        try {
	        	initialContext= new InitialContext(null);
	        	home = initialContext.lookup(jndiName);
	        } catch(NamingException e) {
	    		String err = "Unable to lookup \""+jndiName+"\""
				+ ": " + e.getClass().getName() + ": "+ e.getLocalizedMessage();
	    		throw new XtentisException(err);        	
	        } finally {
	           try {initialContext.close();} catch(Exception e){};
	        }
	        return home!=null;
		}catch(Exception e){
			return false;
		}
	}
	
	 public static boolean isDefaultSVNUP() throws Exception{
		 if(isEnterprise()){
		  Object service= 
		   Util.retrieveComponent(
		    null, 
		    "amalto/local/service/svn"
		   );
		  if(service==null)return false;
		  Boolean result = (Boolean)
		   Util.getMethod(service, "isUp").invoke(
		    service,
		    new Object[] {    
		    }
		   ); 
		  return result.booleanValue();
		 }else{
			 return false;
		 }
     }
	 public static boolean isSVNAutocommit()throws Exception{
		 if(isDefaultSVNUP()){
 			Object service= 
				Util.retrieveComponent(
					null, 
					"amalto/local/service/svn"
				);

			Boolean isauto = (Boolean)
				Util.getMethod(service, "isAutocommittosvn").invoke(
					service,
					new Object[] {}
				);            	
			return isauto;
		 }
		 return false;
	 }
	 
	 public static String getAppServerDeployDir() {
			String jbossHomePath=System.getenv("JBOSS_HOME");
			if(jbossHomePath==null)
			    jbossHomePath = System.getProperty("jboss.home.dir");
			return jbossHomePath;
	 }
	 public static String getJbossHomeDir() {
			String jbossHomePath=com.amalto.core.util.Util.getAppServerDeployDir();			
			String jbossHome=new File(jbossHomePath).getAbsolutePath();
			return jbossHome;
	 }
	 public static String getBarHomeDir() {
		 return Util.getJbossHomeDir()+File.separator+"barfiles";
	 }
	/*********************************************************************
	 *MAIN
	 *********************************************************************/	
 
	 public static void main(String args[]) throws Exception {
	 	//testSpellCheck();	
	 }

	public static void getView(ArrayList<String> views, IWhereItem whereItem) {
		if (whereItem instanceof WhereLogicOperator) {
			Collection<IWhereItem> subItems = ((WhereLogicOperator) whereItem)
					.getItems();
			if (subItems.size() == 1) {
				getView(views, subItems.iterator().next());
			}
			int i = 0;
			for (Iterator<IWhereItem> iter = subItems.iterator(); iter
					.hasNext();) {
				IWhereItem item = iter.next();
				getView(views, item);
			}
		}
		if (whereItem instanceof WhereCondition) {
			WhereCondition whereCondition = (WhereCondition) whereItem;
			views.add(whereCondition.getLeftPath());

		}

	}

	public static Map<String, XSElementDecl> getConceptMap(DataModelPOJO dataModelPOJO)
			throws Exception {

		String xsd = dataModelPOJO.getSchema();
		return getConceptMap(xsd);
	}

	private static XSElementDecl parseMetaDataTypes(XSElementDecl elem,
			String pathSlice, ArrayList<String> valuesHolder,
			boolean forFkValue) {
		valuesHolder.clear();
		XSContentType conType;
		if(elem==null) return null;
		XSType type = elem.getType();
		if (elem.getName().equals(pathSlice)) {
			if (elem.getType() instanceof XSComplexType) {
				valuesHolder.add("complex type");
			} else {
				XSSimpleType simpType = (XSSimpleType) elem.getType();
				valuesHolder.add(simpType.getName());
			}
			return elem;
		}
		if (type instanceof XSComplexType) {
			XSComplexType cmpxType = (XSComplexType) type;
			conType = cmpxType.getContentType();
			XSParticle[] children = conType.asParticle().getTerm()
					.asModelGroup().getChildren();
			for (XSParticle child : children) {
				if (child.getTerm() instanceof XSElementDecl) {
					XSElementDecl childElem = (XSElementDecl) child.getTerm();
					if (childElem.getName().equals(pathSlice)) {

						if (childElem.getType() instanceof XSSimpleType) {
							XSSimpleType simpType = (XSSimpleType) childElem
									.getType();
							Collection<FacetImpl> facets = (Collection<FacetImpl>) simpType
									.asRestriction().getDeclaredFacets();
							for (XSFacet facet : facets) {
								if (facet.getName().equals("enumeration")) {
									valuesHolder.add("enumeration");
									break;
								}
							}
							if ( !valuesHolder.contains("enumeration")) {
								String basicName = simpType.getBaseType()
										.getName();
								String simpTypeName = simpType.getName();
								if (simpType.getTargetNamespace().equals(
										"http://www.w3.org/2001/XMLSchema")) {
									simpTypeName = "xsd:" + simpTypeName;
								} else
									simpTypeName = "xsd:" + basicName;
								valuesHolder.add(simpTypeName);
							} else if (simpType.asRestriction() != null
									&& valuesHolder.contains("enumeration")) {
								XSType xsType=simpType.asRestriction().getBaseType();
								String simpTypeName="xsd:" +xsType.getName();
								valuesHolder.set(valuesHolder.indexOf("enumeration"), simpTypeName);
							}
						} else {
							XSComplexType cmpType = (XSComplexType) childElem
									.getType();
							valuesHolder.add("complex type");
						}
						return childElem;
					}
				}
			}
		} 

		return null;

	}
    public static Element getLoginProvisioningFromDB() throws Exception {
		
		ItemPOJOPK itempk=new ItemPOJOPK(new DataClusterPOJOPK("PROVISIONING"), "User", new String[] {LocalUser.getLocalUser().getUsername()});
		ItemPOJO item=ItemPOJO.load(itempk);
		
		Element user  = (Element)Util.getNodeList(item.getProjection(), "//User").item(0);
		return user;
    }	
    public static String getUserDataModel() throws Exception {
    	Element item=getLoginProvisioningFromDB();
    	NodeList nodeList = Util.getNodeList(item,"//property");
		for (int i = 0; i < nodeList.getLength(); i++) { 
			Node node = nodeList.item(i);
			if("model".equals(Util.getFirstTextNode(node,"name"))){		
				//configuration.setCluster(Util.getNodeList(node, "value").item(0).getTextContent());
				Node fchild=Util.getNodeList(node, "value").item(0).getFirstChild();
				return fchild.getNodeValue();
			}
		}
		return null;
    }
	
	public static Map<String, ArrayList<String>> getMetaDataTypes(
			IWhereItem whereItem) throws Exception {
		if (null != whereItem) {
			ArrayList<String> views = new ArrayList<String>();
			Util.getView(views, whereItem);
			Set<String> concepts=new HashSet<String>();
			for(String v: views) {
				concepts.add(v.split("/")[0]);
			}
			DataModelPOJO datamodelPojo=Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(getUserDataModel()));
			Map<String, XSElementDecl> xsdMap = getConceptMap(datamodelPojo.getSchema());
			HashMap<String, ArrayList<String>> metaDataTypes = new HashMap<String, ArrayList<String>>();
			for(String concept: concepts) {
				if(concept!=null && concept.length()>0) {
					HashMap<String, ArrayList<String>> types = new HashMap<String, ArrayList<String>>();
				
					XSElementDecl el = xsdMap.get(concept);
		
					for (String view : views) {
						ArrayList<String> dataTypesHolder = new ArrayList<String>();
						String[] pathSlices = view.split("/");
						XSElementDecl node = parseMetaDataTypes(el, pathSlices[0],
								dataTypesHolder, true);
						if (pathSlices.length > 1) {
							for (int i = 1; i < pathSlices.length; i++) {
								node = parseMetaDataTypes(node, pathSlices[i],
										dataTypesHolder, true);
							}
						}
						if(dataTypesHolder.size()>0) {
							types.put(view, dataTypesHolder);
						}
					}
					metaDataTypes.putAll(types);
				}
			}
			return metaDataTypes;
		}
		return null;
	}

	/**
	 * fix the web conditions.
	 * 
	 * @param conditions
	 *            in web ui.
	 */
	 public static IWhereItem fixWebCondtions(IWhereItem whereItem) throws XmlServerException
	   {
		  if(whereItem==null) return null;
	      if(whereItem instanceof WhereLogicOperator) {
	         ArrayList<IWhereItem> subItems = ((WhereLogicOperator)whereItem).getItems();
	         
	         for(int i = subItems.size() - 1; i >= 0; i-- ) {
	            IWhereItem item = subItems.get(i);   
	            item = fixWebCondtions(item);
	            
	            if(item instanceof WhereLogicOperator) {
	               if(((WhereLogicOperator)item).getItems().size() == 0) {
	                  subItems.remove(i);
	               }
	            }
	            else if(item == null) {
	               subItems.remove(i);
	            }
	         }
	      }
	      else if(whereItem instanceof WhereCondition) {
	         WhereCondition condition = (WhereCondition) whereItem;
	         whereItem = "*".equals(condition.getRightValueOrPath()) || 
	            condition.getRightValueOrPath().length() == 0  || 
	            ".*".equals(condition.getRightValueOrPath()) ? null : whereItem;
	      }
	      else {
	         throw new XmlServerException("Unknown Where Type : " + whereItem.getClass().getName());
	      }
	      
	      if(whereItem instanceof WhereLogicOperator) {
	         return ((WhereLogicOperator)whereItem).getItems().size() == 0 ? null : whereItem; 
	      }
	      
	      return whereItem;
	   }
		
	/**
	 * fix the conditions.
	 * 
	 * @param conditions
	 *            in workbench.
	 */
	
	public static void fixCondtions(ArrayList conditions) {
		for (int i = conditions.size() - 1; i >= 0; i--) {
			if (conditions.get(i) instanceof WhereCondition) {
				WhereCondition condition = (WhereCondition) conditions.get(i);

				if (condition.getRightValueOrPath() == null
						|| condition.getRightValueOrPath().length() == 0) {
					conditions.remove(i);
				}
			}
		}
	}
    public static WSMDMJob[] getMDMJobs() {
    	List<WSMDMJob> jobs = new ArrayList<WSMDMJob>();
		try {
			String jbossHomePath = Util.getAppServerDeployDir();
			
			String deploydir="";
			try {
				deploydir = new File(jbossHomePath).getAbsolutePath();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			deploydir=deploydir+File.separator+"server"+File.separator+"default"+File.separator+"deploy";
			System.out.println("deploy url:"+deploydir);
			if(!new File(deploydir).exists())throw new FileNotFoundException();
			
			File[] warFiles=new File(deploydir).listFiles(new FileFilter() {			
				public boolean accept(File pathname) {
					if(pathname.isFile() && pathname.getName().toLowerCase().endsWith(".war")) {
						return true;
					}
					return false;
				}
			});
			File[] zipFiles=new File(JobContainer.getUniqueInstance().getDeployDir()).listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					if(pathname.isFile() ||pathname.getName().toLowerCase().endsWith(".zip")) {
						return true;
					}
					return false;
				}
			});
			for(File war: warFiles) {
				WSMDMJob job= getJobInfo(war.getAbsolutePath());
				if(job!=null)
					jobs.add(job);
			}
			for(File zip: zipFiles) {
				WSMDMJob job= getJobInfo(zip.getAbsolutePath());
				if(job!=null)
					jobs.add(job);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jobs.toArray(new WSMDMJob[jobs.size()]);
    }
	/**
	 * get the JobInfo:jobName and version & suffix
	 * 
	 * @param fileName
	 * @return
	 */
	private static WSMDMJob getJobInfo(String fileName) {
		WSMDMJob jobInfo =null;
		try {
			ZipInputStream in = new ZipInputStream(
					new FileInputStream(fileName));
			ZipEntry z = null;
			try {
				String jobName = "";
				String jobVersion = "";
				//war
				if(fileName.endsWith(".war")) {
					while ((z = in.getNextEntry()) != null) {
						String dirName = z.getName();
						// get job version
						if (dirName.endsWith("undeploy.wsdd")) {						
							Pattern p = Pattern.compile(".*?_(\\d_\\d)/undeploy.wsdd");
							Matcher m = p.matcher(dirName);	
							m.groupCount();
							if (m.matches()) {
								jobVersion = m.group(1);
							}						
						}
						// get job name
						Pattern p = Pattern.compile(".*?/(.*?)\\.wsdl");
						Matcher m= p.matcher(dirName);
						if (m.matches()) {						
							jobName = m.group(1);
						}
					}
					if(jobName.length()>0) {
						jobInfo=new WSMDMJob(null,null,null);
						jobInfo.setJobName(jobName);
						jobInfo.setJobVersion(jobVersion.replaceAll("_", "."));
						jobInfo.setSuffix(".war");
						return jobInfo;
					}else {
						return null;
					}
				}//war
				if(fileName.endsWith(".zip")) {
					while ((z = in.getNextEntry()) != null) {
						String dirName = z.getName();
						int pos= dirName.indexOf('/');
			
						String dir=dirName.substring(0,pos);
						pos=dir.lastIndexOf('_');
						jobName = dir.substring(0,pos);
						jobVersion=dir.substring(pos+1);
						break;

					}
					if(jobName.length()>0) {
						jobInfo=new WSMDMJob(null,null,null);
						jobInfo.setJobName(jobName);
						jobInfo.setJobVersion(jobVersion);
						jobInfo.setSuffix(".zip");
						return jobInfo;
					}else {
						return null;
					}
				}
			} catch (FileNotFoundException e) {				
			} finally {
				in.close();
			}
		} catch (Exception e) {
			
		}

		return jobInfo;
	}
	
	public static String convertAutoIncrement(Properties p) {
		StringBuffer sb=new StringBuffer();
		sb.append("<AutoIncrement>");
		sb.append("<id>AutoIncrement</id>");
		for(Entry entry:p.entrySet()) {
			sb.append("<entry>");
			sb.append("<key>").append(entry.getKey()).append("</key>");
			sb.append("<value>").append(entry.getValue()).append("</value>");
			sb.append("</entry>");
		}
		sb.append("</AutoIncrement>");
		String xmlString= sb.toString();
		return xmlString;
	}
	public static Properties convertAutoIncrement(String xml)throws Exception {
		Properties p=new Properties();
		Node n=parse(xml).getDocumentElement();
		NodeList list=getNodeList(n, "entry");
		for(int i=0; i<list.getLength(); i++) {
			Node item=list.item(i);
			if(item.getNodeType() == Node.ELEMENT_NODE) {
				String key=getFirstTextNode(item, "key");
				String value=getFirstTextNode(item, "value");
				p.setProperty(key, value);
			}
		}		
		return p;
	}
    public static String stripeOuterBracket(String rowData)
    {
	     ArrayList<String> result = new ArrayList<String>();
		 int aggregate = 0;
		 int cordon = 0;
	     for(int i = 0; i < rowData.length(); i++)
		 {
	        char ch = rowData.charAt(i);
			if(ch == '[')
			{
			 aggregate++;
			 if(aggregate == 1)
			 {
			   cordon = i;
			 }
			}
			else if(ch == ']')
			{
	          aggregate--;
			  if(aggregate == 0)
			  {
				 result.add(rowData.substring(cordon+1, i));
			  }
			}
			else if(aggregate == 0)
				result.add(ch + "");
		 }
	     
	     return StringUtils.join(result.toArray(), ",");
    }
    
    /**
     * Escape any single quote characters that are included in the specified message string.
     * 
     * @param string The string to be escaped
     */
    protected static String escape(String string) {

        if ((string == null) || (string.indexOf('\'') < 0)) {
            return string;
        }

        int n = string.length();
        StringBuffer sb = new StringBuffer(n);

        for (int i = 0; i < n; i++) {
            char ch = string.charAt(i);

            if (ch == '\'') {
                sb.append('\'');
            }

            sb.append(ch);
        }

        return sb.toString();

    }
    
    public static String getMessage(String value, Object... args) {
        try {
            MessageFormat format = new MessageFormat(escape(value));

            value = format.format(args);
            return value;
        } catch (MissingResourceException e) {
            return "???" + value + "???";
        } 
    }
    
    public static String printWithFormat(Locale locale,String format,Object value) {
		
    	String formattedContent="";
    	
    	try {
    		
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			PrintStream ps = new PrintStream(baos);

			ps.format(locale,format, value);
			formattedContent=baos.toString();
			
			baos.close();
			
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		
		return formattedContent;

	}

    public static void restoreDatacluster(String filename) throws Exception{
    	if(!MDMConfiguration.isExistDb())return;
    	Properties config=MDMConfiguration.getConfiguration();
		List<String> list=new ArrayList<String>();
		String home=getExistHome();
		String path=new File(home+"/start.jar").getAbsolutePath();
		String server=config.getProperty("xmldb.server.name","localhost");
		String passwd=config.getProperty("xmldb.administrator.password","1bc29b36f623ba82aaf6724fd3b16718");
		String port=config.getProperty("xmldb.server.port","8080");
		String cmd="java -Xms128m -Xmx512m -Dfile.encoding=UTF-8 -jar "+path+" backup -u admin -p "+passwd;
		String[] cmds=cmd.split("\\s");
		list.addAll(Arrays.asList(cmds));
		list.add("-r");
		list.add(filename.trim());
		//add server
		String uri="-ouri=xmldb:exist://"+server+":"+port+"/exist/xmlrpc";
		list.add(uri);
        
		ConsoleSimulator.runCmd(list.toArray(new String[list.size()]), null, home);
    }
    
    
    public static void backupDatacluster(String datacluster, String filename) throws Exception{
    	if(!MDMConfiguration.isExistDb())return;
    	
    	Properties config=MDMConfiguration.getConfiguration();
		List<String> list=new ArrayList<String>();
		String home=getExistHome();

		String path=new File(home+"/start.jar").getAbsolutePath();
		String server=config.getProperty("xmldb.server.name","localhost");
		String passwd=config.getProperty("xmldb.administrator.password","1bc29b36f623ba82aaf6724fd3b16718");
		String port=config.getProperty("xmldb.server.port","8080");
		String cmd="java -Xms128m -Xmx512m -Dfile.encoding=UTF-8 -jar "+path+" backup -u admin -p "+passwd+" -b /db/"+datacluster;
		String[] cmds=cmd.split("\\s");
		list.addAll(Arrays.asList(cmds));
		list.add("-d");
		list.add(new File(home+"/"+filename.trim()).getAbsolutePath());
		//add server
		String uri="-ouri=xmldb:exist://"+server+":"+port+"/exist/xmlrpc";
		list.add(uri);
		//set exist home       
        if(System.getProperty("os.name").indexOf("win") != -1){//windows
        	list.add(0,"cmd");
        	list.add(1,"/c");
        }
        
        ConsoleSimulator.runCmd(list.toArray(new String[list.size()]), null, home);
    }
    
    public static String getExistHome(){
    	String home=getJbossHomeDir()+"/eXist";
    	home=new File(home).getAbsolutePath();    				
        System.setProperty("exist.home", home);
        System.out.println("exist.home==="+home);
        return home;
    }    
    
} 
