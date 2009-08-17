/*
 * Created on 22 sept. 2005
 *
 */
package com.amalto.workbench.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.rpc.Stub;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.methods.MultipartPostMethod;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSchemaContent;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.impl.XSDSchemaImpl;
import org.osgi.framework.Bundle;
import org.talend.mdm.commmon.util.workbench.Version;
import org.talend.mdm.commmon.util.workbench.ZipToFile;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.workbench.MDMWorbenchPlugin;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.webservices.WSComponent;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetComponentVersion;
import com.amalto.workbench.webservices.WSGetUniverse;
import com.amalto.workbench.webservices.WSGetUniversePKs;
import com.amalto.workbench.webservices.WSGetViewPKs;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.WSRoutingRuleExpression;
import com.amalto.workbench.webservices.WSRoutingRuleOperator;
import com.amalto.workbench.webservices.WSStringPredicate;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniversePK;
import com.amalto.workbench.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.workbench.webservices.WSVersion;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.webservices.WSWhereOperator;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.webservices.XtentisService_Impl;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;


/**
 * @author bgrieder
 */
public class Util {
	
	
	
	public static LinkedHashMap<String,String> iso2lang = new LinkedHashMap<String, String>();
	
	static {
		iso2lang.put("en","English");
		iso2lang.put("fr","French");
		iso2lang.put("es","Spanish");
		iso2lang.put("ab","Abkhazian");
		iso2lang.put("aa","Afar");
		iso2lang.put("af","Afrikaans");
		iso2lang.put("sq","Albanian");
		iso2lang.put("am","Amharic");
		iso2lang.put("ar","Arabic");
		iso2lang.put("hy","Armenian");
		iso2lang.put("as","Assamese");
		iso2lang.put("ay","Aymara");
		iso2lang.put("az","Azerbaijani");
		iso2lang.put("ba","Bashkir");
		iso2lang.put("eu","Basque");
		iso2lang.put("bn","Bengali (Bangla)");
		iso2lang.put("dz","Bhutani");
		iso2lang.put("bh","Bihari");
		iso2lang.put("bi","Bislama");
		iso2lang.put("br","Breton");
		iso2lang.put("bg","Bulgarian");
		iso2lang.put("my","Burmese");
		iso2lang.put("be","Byelorussian");
		iso2lang.put("km","Cambodian");
		iso2lang.put("ca","Catalan");
		iso2lang.put("zh","Chinese");
		iso2lang.put("co","Corsican");
		iso2lang.put("hr","Croatian");
		iso2lang.put("cs","Czech");
		iso2lang.put("da","Danish");
		iso2lang.put("nl","Dutch");
		iso2lang.put("eo","Esperanto");
		iso2lang.put("et","Estonian");
		iso2lang.put("fo","Faeroese");
		iso2lang.put("fa","Farsi");
		iso2lang.put("fj","Fiji");
		iso2lang.put("fi","Finnish");
		iso2lang.put("fy","Frisian");
		iso2lang.put("gl","Galician");
		iso2lang.put("ka","Georgian");
		iso2lang.put("de","German");
		iso2lang.put("el","Greek");
		iso2lang.put("kl","Greenlandic");
		iso2lang.put("gn","Guarani");
		iso2lang.put("gu","Gujarati");
		iso2lang.put("ha","Hausa");
		iso2lang.put("iw, he","Hebrew");
		iso2lang.put("hi","Hindi");
		iso2lang.put("hu","Hungarian");
		iso2lang.put("is","Icelandic");
		iso2lang.put("in, id","Indonesian");
		iso2lang.put("ia","Interlingua");
		iso2lang.put("ie","Interlingue");
		iso2lang.put("iu","Inuktitut");
		iso2lang.put("ik","Inupiak");
		iso2lang.put("ga","Irish");
		iso2lang.put("it","Italian");
		iso2lang.put("ja","Japanese");
		iso2lang.put("ji","Yiddish");
		iso2lang.put("jw","Javanese");
		iso2lang.put("kn","Kannada");
		iso2lang.put("ks","Kashmiri");
		iso2lang.put("kk","Kazakh");
		iso2lang.put("rw","Kinyarwanda");
		iso2lang.put("ky","Kirghiz");
		iso2lang.put("rn","Kirundi");
		iso2lang.put("ko","Korean");
		iso2lang.put("ku","Kurdish");
		iso2lang.put("lo","Laothian");
		iso2lang.put("la","Latin");
		iso2lang.put("lv","Latvian (Lettish)");
		iso2lang.put("ln","Lingala");
		iso2lang.put("lt","Lithuanian");
		iso2lang.put("mk","Macedonian");
		iso2lang.put("mg","Malagasy");
		iso2lang.put("ms","Malay");
		iso2lang.put("ml","Malayalam");
		iso2lang.put("mt","Maltese");
		iso2lang.put("gv","Manx Gaelic");
		iso2lang.put("mi","Maori");
		iso2lang.put("mr","Marathi");
		iso2lang.put("mo","Moldavian");
		iso2lang.put("mn","Mongolian");
		iso2lang.put("na","Nauru");
		iso2lang.put("ne","Nepali");
		iso2lang.put("no","Norwegian");
		iso2lang.put("oc","Occitan");
		iso2lang.put("or","Oriya");
		iso2lang.put("om","Oromo (Afan)");
		iso2lang.put("ps","Pashto (Pushto)");
		iso2lang.put("pl","Polish");
		iso2lang.put("pt","Portuguese");
		iso2lang.put("pa","Punjabi");
		iso2lang.put("qu","Quechua");
		iso2lang.put("rm","Rhaeto-Romance");
		iso2lang.put("ro","Romanian");
		iso2lang.put("ru","Russian");
		iso2lang.put("sm","Samoan");
		iso2lang.put("sg","Sangro");
		iso2lang.put("sa","Sanskrit");
		iso2lang.put("gd","Scots Gaelic");
		iso2lang.put("sr","Serbian");
		iso2lang.put("sh","Serbo-Croatian");
		iso2lang.put("st","Sesotho");
		iso2lang.put("tn","Setswana");
		iso2lang.put("sn","Shona");
		iso2lang.put("sd","Sindhi");
		iso2lang.put("si","Singhalese");
		iso2lang.put("ss","Siswati");
		iso2lang.put("sk","Slovak");
		iso2lang.put("sl","Slovenian");
		iso2lang.put("so","Somali");
		iso2lang.put("su","Sundanese");
		iso2lang.put("sw","Swahili");
		iso2lang.put("sv","Swedish");
		iso2lang.put("tl","Tagalog");
		iso2lang.put("tg","Tajik");
		iso2lang.put("ta","Tamil");
		iso2lang.put("tt","Tatar");
		iso2lang.put("te","Telugu");
		iso2lang.put("th","Thai");
		iso2lang.put("bo","Tibetan");
		iso2lang.put("ti","Tigrinya");
		iso2lang.put("to","Tonga");
		iso2lang.put("ts","Tsonga");
		iso2lang.put("tr","Turkish");
		iso2lang.put("tk","Turkmen");
		iso2lang.put("tw","Twi");
		iso2lang.put("ug","Uighur");
		iso2lang.put("uk","Ukrainian");
		iso2lang.put("ur","Urdu");
		iso2lang.put("uz","Uzbek");
		iso2lang.put("vi","Vietnamese");
		iso2lang.put("vo","Volapük");
		iso2lang.put("cy","Welsh");
		iso2lang.put("wo","Wolof");
		iso2lang.put("xh","Xhosa");
		iso2lang.put("yi","Yiddish");
		iso2lang.put("yo","Yoruba");
		iso2lang.put("zu","Zulu");
	}
	
	public static LinkedHashMap<String,String> lang2iso = new LinkedHashMap<String, String>();
	static {
		Set<String> isos = iso2lang.keySet();
		for (Iterator<String> iter = isos.iterator(); iter.hasNext(); ) {
			String iso = iter.next();
			String language = iso2lang.get(iso);
			lang2iso.put(language, iso);
			/*
			String existingISO = lang2iso.get(language);
			if (existingISO!=null) {
				lang2iso.put(language, existingISO+", "+iso);	
			} else {
				lang2iso.put(language, iso);
			}
			*/
		}
	}
	


	
	public static String default_endpoint_address = "http://localhost:8080/talend/TalendPort";
	
	/*********************************************************************
	 *      WEB SERVICES
	 *********************************************************************/

	public static XtentisPort getPort(TreeObject xobject) throws XtentisException{
		try {
			if(xobject==null)return null;
			return getPort(
					new URL(xobject.getEndpointAddress()),
					xobject.getUniverse(),
					xobject.getUsername(),
					xobject.getPassword()
			);
		} catch (MalformedURLException e) {
			throw new XtentisException("Invalid endpoint address: "+xobject.getEndpointAddress());
		}
	}
	
	public static XtentisPort getPort(String universe, String username, String password) throws XtentisException{
		try {
			return getPort(new URL(default_endpoint_address), universe, username, password);
		} catch (MalformedURLException e) {
			String err = "The default URL '"+default_endpoint_address+"' is invalid!";
			throw new XtentisException(err);
		}
	}
	
//	public static XtentisPort getPort(String endpointAddress, String username, String password) throws XtentisException{
	public static XtentisPort getPort(URL url, String universe, String username, String password) throws XtentisException{		

		try {

			//deactivate Certificate validation on all https connections by creating a non validating ssl socket factory
			SSLContext context = SSLContext.getInstance("TLS");
			X509TrustManager mgr = new X509TrustManager() {
				public void checkClientTrusted(	X509Certificate[] chain,String authType) throws CertificateException {	}
				public void checkServerTrusted(X509Certificate[] chain,	String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {return null;}
			};
			context.init(null, new TrustManager[]{mgr}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
			
			//prepare the Web Services Stub
			Stub stub = (Stub) (new XtentisService_Impl()).getXtentisPort();
			stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, url.toString());
			if (username!=null) {
				if (universe != null)
					stub._setProperty(Stub.USERNAME_PROPERTY, universe+"/"+username);
				else
					stub._setProperty(Stub.USERNAME_PROPERTY, username);
				
			}
			if (password!=null)
	        stub._setProperty(Stub.PASSWORD_PROPERTY, password);

			
			return (XtentisPort)stub;
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisException(
					"Unable to access endpoint at: "+url
					+": "+e.getLocalizedMessage());
		}
	}
	
	

	
	/*
	public static WSDataModel[] getAllDataModels(URL url, String username, String password) throws XtentisException{
		try {
			XtentisPort port = Util.getPort(url,username,password);
			return port.getDataModels(new WSRegexDataModels("*")).getWsDataModels();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisException(
					"Unable to retrieve all Data Models"
					+": "+e.getLocalizedMessage());
		}
	}
	*/
	
	public static WSDataModelPK[] getAllDataModelPKs(URL url, String universe, String username, String password) throws XtentisException{
		try {
			XtentisPort port = Util.getPort(url,universe,username,password);
			return port.getDataModelPKs(new WSRegexDataModelPKs("*")).getWsDataModelPKs();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisException(
					"Unable to retrieve all Data Model Names"
					+": "+e.getLocalizedMessage());
		}
	}
	
	
	
	public static WSDataClusterPK[] getAllDataClusterPKs(URL url, String universe, String username, String password) throws XtentisException{
		try {
			XtentisPort port = Util.getPort(url,universe,username,password);
			return port.getDataClusterPKs(new WSRegexDataClusterPKs("*")).getWsDataClusterPKs();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisException(
					"Unable to retrieve all Data Cluster Names"
					+": "+e.getLocalizedMessage());
		}
	}
	
	

	
	public static WSViewPK[] getAllViewPKs(URL url, String universe, String username, String password, String regex) throws XtentisException{
		try {
			if ((regex==null) || ("".equals(regex))) regex = "*";
			XtentisPort port = Util.getPort(url,universe,username,password);
			return port.getViewPKs(new WSGetViewPKs(regex)).getWsViewPK();
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisException(
					"Unable to retrieve all the Outbound Adaptors"
					+": "+e.getLocalizedMessage());
		}
	}


	
	/*********************************************************************
	 *      LOCAL FILE UTILS
	 *********************************************************************/
	
	public static String getXML(String filename) throws Exception{
	    BufferedReader in = null;
        in = new BufferedReader(
        			new InputStreamReader(new FileInputStream(filename), "utf-8")
        );
	
	    String xml="";
        String line;
        while ((line=in.readLine())!=null) xml+=line+"\n";
        return xml;
    }    
	
	public static String getPackageFilePath(Class<? extends Object> c, String filename) {
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
	 * @return the concept Name
	 */
    public static String getConceptFromPath(String path) {
    	Pattern p = Pattern.compile("(.*?)[\\[|/].*");
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = p.matcher(path);
    	if (m.matches()) 
    		return m.group(1);
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
			
	   		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			documentBuilderFactory.setNamespaceAware(true);
			documentBuilderFactory.setValidating((schema!=null));
			documentBuilderFactory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaLanguage",
					"http://www.w3.org/2001/XMLSchema");
			if (schema != null) {
				documentBuilderFactory.setAttribute(
					"http://java.sun.com/xml/jaxp/properties/schemaSource",
					new InputSource(new StringReader(schema))
					);
			}
			
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
			documentBuilder.setErrorHandler(seh);
			if(xmlString==null||xmlString.length()==0||xmlString.matches("\\s+"))return d;
			d = documentBuilder.parse(new InputSource(new StringReader(xmlString)));
			
			if (schema != null) {
				String errors = seh.getErrors();
				if (!errors.equals("")) {
					String err = "Document  did not parse against schema: \n" + errors+"\n"+xmlString;
					throw new Exception(err);
				}
			}
			return d;
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Unable to parse the document"
					+": " + e.getClass().getName() + ": "
					+ e.getLocalizedMessage()+
					"\n "+xmlString;
			throw new Exception(err);
		}
    }
    

    public static String[] getTextNodes(Node contextNode, String xPath) throws XtentisException{
    	return getTextNodes(contextNode,xPath,contextNode);
    }
    
    public static String[] getTextNodes(Node contextNode, String xPath,Node namespaceNode) throws XtentisException{
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
			throw new XtentisException(err);
		}
		return results;

    }    
    
    
    public static String getFirstTextNode(Node contextNode, String xPath,Node namespaceNode) throws XtentisException{
    	String[] res = getTextNodes(contextNode,xPath,namespaceNode);
    	if (res.length == 0) 
    		return null;
    	return res[0]; 
    }

    public static String getFirstTextNode(Node contextNode, String xPath) throws XtentisException{
    	return getFirstTextNode(contextNode,xPath,contextNode);
    }

    
    
	/*********************************************************************
	 *      FILE UPLOAD
	 *      
	 *      Multi-Part Form Post
	 *********************************************************************/
    public static String uploadFileToAppServer(String URL, String localFilename, String username, String password)  throws XtentisException{
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        /*
        System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.header", "debug");
        System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire.content", "debug");
        */
        
        HttpClient client = new HttpClient();
        MultipartPostMethod mppost = new MultipartPostMethod(URL);
        String response = null;
        
        try {
        	    
            client.setConnectionTimeout(60000);
            client.getState().setAuthenticationPreemptive(true);
            client.getState().setCredentials(null,null, new UsernamePasswordCredentials(username,password));
        
            mppost.addParameter(localFilename,new File(localFilename));
            
            client.executeMethod(mppost);
            if (mppost.getStatusCode() != HttpStatus.SC_OK) {
            	throw new XtentisException("Server sent error: "+mppost.getStatusCode()+": "+mppost.getStatusText()); 
            }
            response = mppost.getResponseBodyAsString();
            mppost.releaseConnection();
            return response;
        } catch (Exception e) {
            mppost.releaseConnection();        	
        	e.printStackTrace();
        	throw new XtentisException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }
    }
	/*********************************************************************
	 *     XSD Utils
	 *********************************************************************/
    
    
    public static boolean isTypeDerivedFrom(XSDTypeDefinition typedef, String namespace, String localName)   {
    	// Walk the baseTypes from this typedef seeing if any
    	// of them match the requested one
    	XSDTypeDefinition baseType = typedef.getBaseType();
    	
    	// As this convenience method if our parameters match
    	if (baseType.hasNameAndTargetNamespace(localName, namespace)) {
    		return true;
    	}
    	
    	// If not, check to see if we've run up to the top
    	// Performance note: this is horribly inefficient,
    	// re-calling this same method every time; but it
    	// serves as a good example
    	XSDTypeDefinition rootType = typedef.getRootType();
    	if (rootType == baseType) 	{
    		// If we've hit the root, we aren't derived from it
    		return false;
    	}  	else   	{
    		// Otherwise continue to traverse upwards
    		return isTypeDerivedFrom(baseType, namespace, localName);
    	}
    }
    
    /**
     * Find elementDeclarations that use any types derived from a named type.
     * 
     * <p>
     * This shows one way to query the schema for elementDeclarations and then
     * how to find specific kinds of typeDefinitions.
     * </p>
     * 
     * @param objList
     *            collection set to search for elemDecls
     * @param namespace
     *            for the type used
     * @param localName
     *            for the type used
     * @return Boolean  indicate any XSDElementDeclarations is found or not
     */
    public static boolean findElementsUsingType(ArrayList<Object> objList, String namespace, String localName)   {
    	if (null == localName)  	{
    		throw new IllegalArgumentException("findElementsUsingType called with null localName");
    	}
    	
    	
    	// A handy convenience method quickly gets all
    	// elementDeclarations within our schema; note that
    	// whether or not this returns types in included,
    	// imported, or redefined schemas is subject to change
    	
    	for (Iterator<Object> iter = objList.iterator(); iter.hasNext(); /* no-op */)  	{
    		Object obj = iter.next();
    		if (obj instanceof XSDElementDeclaration || obj instanceof XSDTypeDefinition)
    		{
    			XSDTypeDefinition typedef = null;
    			if (obj instanceof XSDElementDeclaration)
    			{
        			XSDElementDeclaration elem = (XSDElementDeclaration)obj;
    	    		if (elem.getAnonymousTypeDefinition() != null)	{
    	    			typedef = elem.getAnonymousTypeDefinition();
    	    		} else if (elem.getTypeDefinition() != null) {
    	    			typedef = elem.getTypeDefinition();
    	    		} else	{
    	    			// Element is not complete, since it has no type,
    	    			// thus it's not using our type
    	    			continue;
    	    		}
    			}
    			else {
					typedef = (XSDTypeDefinition) obj;
				}

	    		
	    		// Ask this type and walk the baseTypes from this
	    		// typedef seeing if any of them match the requested one
//	    		if (typedef.hasNameAndTargetNamespace(localName, namespace)
//	    				|| isTypeDerivedFrom(typedef, namespace, localName))
//	    		{
//	    			// We found it, return the element and continue
//	    			return true;
//	    		}
    			
	    		if (typedef instanceof XSDComplexTypeDefinition)
	    		{
	    			XSDComplexTypeDefinition type = (XSDComplexTypeDefinition)typedef;
					if (type.getContent() instanceof XSDParticle) {
						XSDParticle particle = (XSDParticle) type.getContent();
						if (particle.getTerm() instanceof XSDModelGroup) {
							XSDModelGroup group = (XSDModelGroup) particle.getTerm();
							EList<XSDParticle> elist = group.getContents();
							for (XSDParticle pt : elist) {
								if(pt.getContent() instanceof XSDElementDeclaration)
								{
									XSDTypeDefinition typeDef = ((XSDElementDeclaration) pt.getContent()).getTypeDefinition();
									if (typeDef != null && typeDef.getName() != null)
									{
										if ((localName.equals(typeDef.getName()))) {
											  return true;
											}
									}
								}
							}
						}
					}
	    		}
	    		else if (typedef instanceof XSDSimpleTypeDefinition)
	    		{
	    			XSDSimpleTypeDefinition type = (XSDSimpleTypeDefinition)typedef;
	    			XSDSimpleTypeDefinition baseType = type.getBaseTypeDefinition();
	    			if (baseType != null && baseType.getName().equals(localName))return true;
	    		}
    		}
    	}
    	
        return false;
    }
    
    public static List<String> getAllCustomSimpleDataType(XSDSchema schema)
    {
		ArrayList customTypes = new ArrayList();
		for (Iterator iter =  schema.getTypeDefinitions().iterator(); iter.hasNext(); ) {
			XSDTypeDefinition type = (XSDTypeDefinition) iter.next();
			if (type instanceof XSDSimpleTypeDefinition)
				customTypes.add(type.getName());
		}
		return customTypes;
    }
    
    public static List<String> getAllSchemaSimpleDataType(XSDSchema schema)
    {
    	List<String> builtInTypes = new ArrayList<String>();
		for (Iterator<XSDTypeDefinition> iter =  schema.getSchemaForSchema().getTypeDefinitions().iterator(); iter.hasNext(); ) {
			XSDTypeDefinition type = (XSDTypeDefinition) iter.next();
			if (type instanceof XSDSimpleTypeDefinition)
				builtInTypes.add(type.getName());
		}
		return builtInTypes;
    }
    
    public static Object findElementUsingName(XSDSchema schema, String name) {
		EList<XSDElementDeclaration> elems = schema.getElementDeclarations();
		for (XSDElementDeclaration elem : elems) {
			if (elem.getName().equals(name))
				return elem;
		}
		return null;
	}
    
    public static List<Object> getKeyInfo(Object key)
    {
    	if (!((key instanceof XSDElementDeclaration))) {
			return null;
		}
    	
    	List<Object> list = new ArrayList<Object>();
    	
    	Object parent = getParent(key);
        if (parent != null && parent instanceof XSDElementDeclaration)
        {
        	XSDElementDeclaration top = (XSDElementDeclaration)parent;
        	EList<XSDIdentityConstraintDefinition> idtylist = top.getIdentityConstraintDefinitions();
        	for (XSDIdentityConstraintDefinition idty : idtylist)
        	{
        		EList<XSDXPathDefinition> fields = idty.getFields();
        		for (XSDXPathDefinition path: fields)
        		{
        			if (path.getValue().equals(((XSDElementDeclaration)key).getName()))
        			{
        				list.add(idty);
        				list.add(path);
        				return list;
        			}
        		}
        	}
        }
        
        return null;
    }
    
    public static Object getParent(Object son)
    {
    	if (!((son instanceof XSDElementDeclaration) || (son instanceof XSDParticle))) {
			return null;
		}

		XSDElementDeclaration elem = null;
		if (son instanceof XSDParticle) {
			elem = (XSDElementDeclaration) ((XSDParticle) son).getContent();
		} else if (son instanceof XSDElementDeclaration) {
			elem = (XSDElementDeclaration) son;
		}

		EList<XSDSchemaContent> parentList = elem.getSchema().getContents();
		ArrayList<Object> list = new ArrayList<Object>();
		for (XSDSchemaContent top : parentList) {
			list.clear();
			if (!(top instanceof XSDElementDeclaration)) {
				continue;
			}
			XSDElementDeclaration decl = (XSDElementDeclaration) top;
			if (decl == son) return decl;
			if (decl.getTypeDefinition() instanceof XSDComplexTypeDefinition) {
				XSDComplexTypeDefinition type = (XSDComplexTypeDefinition) decl
						.getTypeDefinition();
				if (type.getContent() instanceof XSDParticle) {
					XSDParticle particle = (XSDParticle) type.getContent();
					if (particle.getTerm() instanceof XSDModelGroup) {
						XSDModelGroup group = (XSDModelGroup) particle
								.getTerm();
						EList<XSDParticle> elist = group.getContents();
						for (XSDParticle pt : elist) {
							if(pt.getContent() instanceof XSDElementDeclaration)
							if (((XSDElementDeclaration) pt.getContent()) == elem) {
								return decl;
							}

						}
					}
				}
			}
		}
		return null;
    }
    
    
	public static boolean checkConcept(XSDElementDeclaration decl) {
		boolean isConcept = false;
		EList l = decl.getIdentityConstraintDefinitions();
		for (Iterator iter = l.iterator(); iter.hasNext();) {
			XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter
					.next();
			if (icd.getIdentityConstraintCategory().equals(
					XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
				isConcept = true;
				break;
			}
		}
		return isConcept;
	}
	
    public static Object[] getAllObject(Object elem, ArrayList<Object> objList, IStructuredContentProvider provider) {

		Object[] elems = provider.getElements(elem);
		for (Object obj : elems) {
			if (!objList.contains(obj)) {
				objList.add(obj);
				getAllObject(obj, objList, provider);
			} else {
				continue;
			}
		}

		return objList.toArray();
		
	}
    
    public static void updateComplexType(Object elem, Object newType,  IStructuredContentProvider provider)
    {
    	if (!(newType instanceof XSDComplexTypeDefinition)){
    		return;
    	}
        ArrayList<Object> objList = new ArrayList<Object>();
    	Object[] allNodes = getAllObject(elem, objList, provider);
    	for (Object node : allNodes)
    	{
    		if (node instanceof XSDElementDeclaration)
    		{
				XSDElementDeclaration xsdElem = (XSDElementDeclaration) node;
				if (xsdElem.getTypeDefinition() == newType) {
					xsdElem.setTypeDefinition((XSDComplexTypeDefinition) newType);
				}
    		}
    		else if (node instanceof XSDParticle) {
					XSDParticle particle = (XSDParticle) node;
					if (particle.getTerm() instanceof XSDModelGroup) {
						XSDModelGroup group = (XSDModelGroup) particle
								.getTerm();
						EList<XSDParticle> elist = group.getContents();
						for (XSDParticle pt : elist) {
							if(pt.getContent() instanceof XSDElementDeclaration)
							if (((XSDElementDeclaration) pt.getContent()).getTypeDefinition() == newType) {
								((XSDElementDeclaration) pt.getContent()).setTypeDefinition((XSDComplexTypeDefinition)newType);
							}

						}
					}
					else if (particle.getTerm() instanceof XSDElementDeclaration)
					{
						XSDElementDeclaration xsdElem = (XSDElementDeclaration) particle.getTerm();
						if (xsdElem.getTypeDefinition() == newType) {
						xsdElem
								.setTypeDefinition((XSDComplexTypeDefinition) newType);
					      }
					}
				}
    	}
    	
    }
    
    public static void updateReference(Object decl, Object[] objs, String newValue) {
		if (!(decl instanceof XSDElementDeclaration)) {
			return;
		}

		for (Object obj : objs) {
			if (obj instanceof XSDParticle) {
				XSDTerm term = ((XSDParticle) obj).getTerm();

				if (term instanceof XSDElementDeclaration) {
					XSDElementDeclaration xsdElem = (XSDElementDeclaration) term;
					if (xsdElem == decl) {
						((XSDParticle) obj)
								.setTerm((XSDElementDeclaration) decl);
						((XSDParticle) obj).updateElement();
					}
				}
				if (!(((XSDParticle) obj).getContent() instanceof XSDElementDeclaration)) continue;
				XSDElementDeclaration elem = (XSDElementDeclaration) ((XSDParticle) obj).getContent();
				if (elem.isElementDeclarationReference())
				{
					if (elem.getResolvedElementDeclaration() == decl)
					{
						elem.setResolvedElementDeclaration((XSDElementDeclaration)decl);
					}
				}
			}
		}
		
	}
    
    public static  void deleteReference(Object decl, Object[] objs) {
		for (Object obj : objs) {
			if (obj instanceof XSDParticle) {
				XSDTerm term = ((XSDParticle) obj).getTerm();

				if (term instanceof XSDElementDeclaration) {
					XSDElementDeclaration xsdElem = (XSDElementDeclaration) term;
					if (xsdElem == decl) {
			       	    	XSDModelGroup group = (XSDModelGroup)((XSDParticle)obj).getContainer();
			       	    	if (group != null)
			       	    		group.getContents().remove(obj);
					}
				}
			}
		}
		
	}
    
    public static XSDElementDeclaration findReference(String refName,
			XSDElementDeclaration elem) {
		EList eDecls = elem.getSchema().getElementDeclarations();

		for (Iterator iter = eDecls.iterator(); iter.hasNext();) {
			XSDElementDeclaration d = (XSDElementDeclaration) iter.next();
			if (d.getQName().equals(refName)) {
				return d;
			}
		}

		return null;
	}
    
    private static Clipboard clipboard = null;
    /**
     * Clipboard support
     * @return the Clipboard
     */
    public static Clipboard getClipboard() {
		if (clipboard == null) {
			clipboard = new Clipboard(Display.getCurrent());
		}
		return clipboard;
	}
    
    
    public static Version getVersion(TreeObject xobject) throws XtentisException{
    	try {
    		WSVersion version  = getPort(xobject).getComponentVersion(new WSGetComponentVersion(WSComponent.DataManager,null));
    		return new Version(version.getMajor(),version.getMinor(),version.getRevision(),version.getBuild());
    	} catch (XtentisException e) {
    		throw(e);
    	} catch (Exception e) {
    		throw new XtentisException("Unable to get core version "+e.getClass().getName()+" : "+e.getMessage());
    	}
    }
    
    /**
     * @deprecated using getChildren(TreeParent xObject,int objectType)
     * @param xObject
     * @param objectType
     * @return
     */
    public static List<String> getCachedXObjectsNameSet(TreeObject xObject,int objectType) {
		List<String> xObjectsNameSet=new ArrayList<String>();
		if(xObject!=null){
			
			TreeParent treeNode = xObject.findServerFolder(objectType);
			TreeObject[] xObjectsSet = treeNode.getChildren();
			for (int i = 0; i < xObjectsSet.length; i++) {
				xObject = xObjectsSet[i];
				String xObjectName = xObject.getDisplayName();
				xObjectsNameSet.add(xObjectName);
			}
			
		}
		return xObjectsNameSet;
	}
	public static List<String> getChildren(TreeParent xObject,int objectType){
		List<String> objs=new ArrayList<String>();
		for( TreeObject obj:xObject.getChildren()){
			if(obj instanceof TreeParent){
				TreeParent parent=(TreeParent)obj;
				objs.addAll(getChildren(parent, objectType));
			}else{
				if(obj.getType() == objectType)
				objs.add(obj.getDisplayName());
			}
		}
		return objs;
	}
    
    public static List<String> getChildElementNames(Node node) throws Exception{
    	List<String> childNames=new ArrayList<String>();
    	//is simple type
    	if(Util.getNodeList(
				node,
				"xsd:complexType"
		).getLength()==0){
    		node.getChildNodes();
    		return childNames;
    	}
    	//check complex is sequence/all/choice?    	
   		NodeList seqlist=Util.getNodeList(
				node,
				"xsd:complexType//xsd:sequence"
		);
		String xpath="";
		if(seqlist.getLength()>0){
			xpath="xsd:complexType//xsd:sequence/xsd:element";
		}
		NodeList alllist=Util.getNodeList(
				node,
				"xsd:complexType//xsd:all"
		);
		if(alllist.getLength()>0){
			xpath="xsd:complexType//xsd:all/xsd:element";
		}
		NodeList choicelist=Util.getNodeList(
				node,
				"xsd:complexType//xsd:choice"
		);
		if(choicelist.getLength()>0){
			xpath="xsd:complexType//xsd:choice/xsd:element";
		}
		//get child element name list
    	NodeList list=Util.getNodeList(node, xpath);     	
    	for(int i=0; i<list.getLength(); i++){
    		String name=Util.getFirstTextNode(list.item(i), "@name");
    		if(name!=null)
    		childNames.add(name);
    	}
    	
    	return childNames;
    }
    
    public static XSDSchema createXsdSchema(String xsd){
	    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(false);
		InputSource source = new InputSource(new StringReader(xsd));
		DocumentBuilder documentBuilder;
		Document document = null;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
			 document = documentBuilder.parse(source);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return XSDSchemaImpl.createSchema(document.getDocumentElement());
    }
    
    public static List<XSDComplexTypeDefinition> getComplexTypes(XSDSchema xsd){
			EList<XSDTypeDefinition> types=xsd.getTypeDefinitions();
   			List<XSDComplexTypeDefinition> complexs=new ArrayList<XSDComplexTypeDefinition>();
   			for(XSDTypeDefinition type: types){
   				if(type instanceof XSDComplexTypeDefinition){
   					complexs.add((XSDComplexTypeDefinition)type);
   				}
   			}
   			return complexs;
    }
    /**
     * 
     * @param filename
     * @param server
     * @param monitor
     * @throws Exception
     */
    public static void importDataCluster(String filename, String server,IProgressMonitor monitor) throws Exception{
		List<String> list=new ArrayList<String>();
		String home=getExistHome();
		String path=new File(home+"/start.jar").getAbsolutePath();		
		String cmd="java -Xms128m -Xmx512m -Dfile.encoding=UTF-8 -jar "+path+" backup -u "+IConstants.EXIST_ADMIN+" -p "+IConstants.EXIST_ADMIN_PASSWD;
		String[] cmds=cmd.split("\\s");
		list.addAll(Arrays.asList(cmds));
		list.add("-r");
		list.add(filename);
		//add server
		String uri="-ouri=xmldb:exist://"+server+":"+IConstants.EXIST_PORT+"/exist/xmlrpc";
		list.add(uri);
		//set exist home       
        if(Platform.getOS().indexOf("win") != -1){//windows
        	list.add(0,"cmd");
        	list.add(1,"/c");
        }
        
		//Main.getMain().run(list.toArray(new String[list.size()]));
		ConsoleSimulator.runCmd(list.toArray(new String[list.size()]), null, home,monitor);
		//Runtime.getRuntime().exec(list.toArray(new String[list.size()]), null, new File(home));
    }
    
    /**
     * 
     * @param datacluster
     * @param filename
     * @param server
     * @param monitor
     * @throws Exception
     */
    public static void exportDataCluster(String datacluster, String filename,String server,IProgressMonitor monitor) throws Exception{
    	if("ALL".equalsIgnoreCase(datacluster)){
    		datacluster="";
    	}
		List<String> list=new ArrayList<String>();
		String home=getExistHome();
		String path=new File(home+"/start.jar").getAbsolutePath();		
		String cmd="java -Xms128m -Xmx512m -Dfile.encoding=UTF-8 -jar "+path+" backup -u "+IConstants.EXIST_ADMIN+" -p "+IConstants.EXIST_ADMIN_PASSWD+" -b /db/"+datacluster;
		//String cmd="java -Xms128m -Xmx512m -Dfile.encoding=UTF-8 org.exist.start.Main backup -u "+IConstants.EXIST_ADMIN+" -p "+IConstants.EXIST_ADMIN_PASSWD+" -b /db/"+datacluster;
		String[] cmds=cmd.split("\\s");
		list.addAll(Arrays.asList(cmds));
		list.add("-d");
		list.add(filename);
		//add server
		String uri="-ouri=xmldb:exist://"+server+":"+IConstants.EXIST_PORT+"/exist/xmlrpc";
		list.add(uri);
		//set exist home       
        if(Platform.getOS().indexOf("win") != -1){//windows
        	list.add(0,"cmd");
        	list.add(1,"/c");
        }
        
		//Main.getMain().run(list.toArray(new String[list.size()]));
        ConsoleSimulator.runCmd(list.toArray(new String[list.size()]), null, home,monitor);
		//Runtime.getRuntime().exec(list.toArray(new String[list.size()]), null, new File(home));
    }
    
    public static String getExistHome()throws Exception{
    	String home=getRealPath(MDMWorbenchPlugin.ID, "/");
		System.out.println("plugin jar name ==="+home);

		File tmp=new File(getTmpFolder());
    	home=new File(home+"/exist").getAbsolutePath();    				
        System.setProperty("exist.home", home);
        System.out.println("exist.home==="+home);
        return home;
    }    
    
    public static String getTmpFolder() {
        String tmp = System.getProperty("user.dir") + "/exist"; //$NON-NLS-1$ //$NON-NLS-2$
        tmp = tmp.replace('\\', '/');
        return tmp;
    }
    
    /**
     * Deletes the temporary files.
     */
    public static void deleteTempFiles() {
        String tmpFold = getTmpFolder();
        File file = new File(tmpFold);
        if (!file.exists() && !file.isDirectory()) {
            return;
        }
        ZipToFile.deleteDirectory(file);
    }
    
    public static String getRealPath(String bundleID, String entry)throws Exception
	 {
		URL urlentry;
		String strEntry;
		
	    Bundle bundle = Platform.getBundle(bundleID);
	
	    // get path URL
	    urlentry = bundle.getEntry(entry);
	
	    strEntry = FileLocator.toFileURL(urlentry).getPath();
		
		return strEntry;
	}
    
    //key is the objectname, value is revisionids
    public static Map<String, List<String>> getUniverseMap(XtentisPort port){
    	Map<String, List<String>> map=new HashMap<String, List<String>>();
		WSUniversePK[] universePKs = null;
		//boolean hasUniverses = true;
		try {
			universePKs = port.getUniversePKs(new WSGetUniversePKs("*")).getWsUniversePK();
			for(WSUniversePK pk: universePKs){
				WSUniverse universe=port.getUniverse(new WSGetUniverse(pk));
				for(WSUniverseXtentisObjectsRevisionIDs id:universe.getXtentisObjectsRevisionIDs()){
					if(map.get(id.getXtentisObjectName())==null){
						map.put(id.getXtentisObjectName(), new ArrayList<String>());
					}
					map.get(id.getXtentisObjectName()).add(id.getRevisionID());					
				}
			}
		} catch (Exception e) {
			System.out.println("No Universes");
		}
    	return map;
    }
    //key is the universename, value is revisionids
    public static Map<String, List<String>> getUniverseMap2(XtentisPort port){
    	Map<String, List<String>> map=new HashMap<String, List<String>>();
		WSUniversePK[] universePKs = null;
		//boolean hasUniverses = true;
		try {
			universePKs = port.getUniversePKs(new WSGetUniversePKs("*")).getWsUniversePK();
			for(WSUniversePK pk: universePKs){
				WSUniverse universe=port.getUniverse(new WSGetUniverse(pk));
				for(WSUniverseXtentisObjectsRevisionIDs id:universe.getXtentisObjectsRevisionIDs()){
					String name=universe.getName();
					if(map.get(name)==null){
						map.put(name, new ArrayList<String>());
					}
					map.get(name).add(id.getRevisionID());					
				}
			}
		} catch (Exception e) {
			System.out.println("No Universes");
		}
    	return map;
    }   
    public static boolean hasUniverse(TreeObject xobject){
    	if(xobject.isXObject()) return false;
        switch(xobject.getType()) {
	    	case TreeObject.DATA_MODEL:
	       	case TreeObject.VIEW:	           	  
	       	case TreeObject.MENU:
	       	case TreeObject.ROLE:
	       	case TreeObject.ROUTING_RULE:
	       	case TreeObject.SYNCHRONIZATIONPLAN:
	       	case TreeObject.STORED_PROCEDURE:
	       	case TreeObject.TRANSFORMER:
	       		return true;
	       	default:
	       		return false;
        }
    }	
    
    public static String[] convertWhereCondition(WSWhereCondition wc){
    	List<String> list=new ArrayList<String>();
    	list.add(wc.getLeftPath());
    	String operator ="";
		if (wc.getOperator().equals(WSWhereOperator.CONTAINS)) operator="Contains";
		else if (wc.getOperator().equals(WSWhereOperator.EQUALS)) operator="=";
		else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN)) operator=">";
		else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN_OR_EQUAL)) operator=">=";
		else if (wc.getOperator().equals(WSWhereOperator.JOIN)) operator="Contains Text Of";
		else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN)) operator="<";
		else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN_OR_EQUAL)) operator="<=";
		else if (wc.getOperator().equals(WSWhereOperator.NOT_EQUALS)) operator="!=";
		else if (wc.getOperator().equals(WSWhereOperator.STARTSWITH)) operator="Starts With";
		else if (wc.getOperator().equals(WSWhereOperator.STRICTCONTAINS)) operator="Strict Contains";
		list.add(operator);
		list.add(wc.getRightValueOrPath());
		
		String predicate="";
		if (wc.getStringPredicate().equals(WSStringPredicate.AND)) predicate="And";
		else if (wc.getStringPredicate().equals(WSStringPredicate.EXACTLY)) predicate="Exactly";
		else if (wc.getStringPredicate().equals(WSStringPredicate.NONE)) predicate="";
		else if (wc.getStringPredicate().equals(WSStringPredicate.NOT)) predicate="Not";
		else if (wc.getStringPredicate().equals(WSStringPredicate.OR)) predicate="Or";
		else if (wc.getStringPredicate().equals(WSStringPredicate.STRICTAND)) predicate="Strict And";
		list.add(predicate);		
    	return list.toArray(new String[list.size()]);
    }

    public static String[] convertRouteCondition(WSRoutingRuleExpression wc){
    	List<String> list=new ArrayList<String>();
    	list.add(wc.getXpath());
    	String operator ="";
		if (wc.getWsOperator().equals(WSRoutingRuleOperator.CONTAINS)) operator="Contains";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.EQUALS)) operator="=";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN)) operator=">";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL)) operator=">=";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.MATCHES)) operator="Matches";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN)) operator="<";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL)) operator="<=";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.NOT_EQUALS)) operator="!=";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.STARTSWITH)) operator="Starts With";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.IS_NULL)) operator="Is Null";
		else if (wc.getWsOperator().equals(WSRoutingRuleOperator.IS_NOT_NULL)) operator="Is Not Null";
		list.add(operator);
		list.add(wc.getValue());
		list.add(wc.getName()==null?"":wc.getName());	
    	return list.toArray(new String[list.size()]);
    }
    public static WSRoutingRuleExpression convertLineRoute(String[] values){
    	WSRoutingRuleExpression wc=new WSRoutingRuleExpression();

		wc.setXpath(values[0]);

		WSRoutingRuleOperator operator = null;
		if (values[1].equals("Contains"))
			operator = WSRoutingRuleOperator.CONTAINS;
		else if (values[1].equals(
				"Matches"))
			operator = WSRoutingRuleOperator.MATCHES;
		else if (values[1].equals("="))
			operator = WSRoutingRuleOperator.EQUALS;
		else if (values[1].equals(">"))
			operator = WSRoutingRuleOperator.GREATER_THAN;
		else if (values[1].equals(">="))
			operator = WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL;
		else if (values[1].equals("<"))
			operator = WSRoutingRuleOperator.LOWER_THAN;
		else if (values[1].equals("<="))
			operator = WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL;
		else if (values[1].equals("!="))
			operator = WSRoutingRuleOperator.NOT_EQUALS;
		else if (values[1].equals(
				"Starts With"))
			operator = WSRoutingRuleOperator.STARTSWITH;
		else if (values[1].equals(
				"Is Null"))
			operator = WSRoutingRuleOperator.IS_NULL;
		else if (values[1].equals("Is Not Null"))
			operator = WSRoutingRuleOperator.IS_NOT_NULL;
		wc.setWsOperator(operator);
		wc.setValue(values[2]);
		wc.setName(values[3]);
    	return wc;
    }
    public static WSWhereCondition convertLine(String[] values){
    	WSWhereCondition wc=new WSWhereCondition();

		wc.setLeftPath(values[0]);

		WSWhereOperator operator = null;
		if (values[1].equals("Contains"))
			operator = WSWhereOperator.CONTAINS;
		else if (values[1].equals(
				"Contains Text Of"))
			operator = WSWhereOperator.JOIN;
		else if (values[1].equals("="))
			operator = WSWhereOperator.EQUALS;
		else if (values[1].equals(">"))
			operator = WSWhereOperator.GREATER_THAN;
		else if (values[1].equals(">="))
			operator = WSWhereOperator.GREATER_THAN_OR_EQUAL;
		else if (values[1].equals("<"))
			operator = WSWhereOperator.LOWER_THAN;
		else if (values[1].equals("<="))
			operator = WSWhereOperator.LOWER_THAN_OR_EQUAL;
		else if (values[1].equals("!="))
			operator = WSWhereOperator.NOT_EQUALS;
		else if (values[1].equals(
				"Starts With"))
			operator = WSWhereOperator.STARTSWITH;
		else if (values[1].equals(
				"Strict Contains"))
			operator = WSWhereOperator.STRICTCONTAINS;
		wc.setOperator(operator);
		wc.setRightValueOrPath(values[2]);
		WSStringPredicate predicate = null;
		if (values[3].equals(""))
			predicate = WSStringPredicate.NONE;
		else if (values[3].equals("Or"))
			predicate = WSStringPredicate.OR;
		if (values[3].equals("And"))
			predicate = WSStringPredicate.AND;
		if (values[3].equals("Strict And"))
			predicate = WSStringPredicate.STRICTAND;
		if (values[3].equals("Exactly"))
			predicate = WSStringPredicate.EXACTLY;
		if (values[3].equals("Not"))
			predicate = WSStringPredicate.NOT;
		wc.setStringPredicate(predicate);

    	return wc;
    }
}
