package org.talend.mdm.webapp.general.server.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class GxtFactory {

    /** the log used by this class */
    private static Logger log = Logger.getLogger(GxtFactory.class);

    /** the collection of actions that we know about */
    private Map<String, String> entries = new HashMap<String, String>();
    
    private Map<String, GxtProjectModel> gxtProjects;

    /** the name of the action mapping file */
    private String gxtRegisterFileName;

    private Map<String, Boolean> excludingMapping = new HashMap<String, Boolean>();
    private String excludingFileName;
    private String gxtResources;
    /**
     * Creates a new instance, using the given configuration file.
     */
    public GxtFactory(String gxtRegisterFileName, String excludingFileName, String gxtResources) {
        this.gxtRegisterFileName = gxtRegisterFileName;
        this.excludingFileName = excludingFileName;
        this.gxtResources = gxtResources;
        init();
    }

    /**
     * Initialises this component, reading in and creating the map of action names to action classes.
     */
    private void init() {
        try {
            // load the properties file containing the name -> class name mapping
            InputStream in = getClass().getClassLoader().getResourceAsStream(gxtRegisterFileName);
            Properties props = new Properties();
            props.load(in);

            // and store them in a HashMap
            Enumeration<?> e = props.propertyNames();
            String actionName;
            String className;

            while (e.hasMoreElements()) {
                actionName = (String) e.nextElement();
                className = props.getProperty(actionName);

                entries.put(actionName, className);
            }

            InputStream excludingIn = getClass().getClassLoader().getResourceAsStream(excludingFileName);
            Properties excludeProps = new Properties();
            excludeProps.load(excludingIn);

            Enumeration<?> excludeEnum = excludeProps.propertyNames();
            String appName;
            Boolean isExcluding;

            while (excludeEnum.hasMoreElements()) {
                appName = (String) excludeEnum.nextElement();
                isExcluding = Boolean.valueOf(excludeProps.getProperty(appName));
                excludingMapping.put(appName, isExcluding);
            }
            initGxtResource();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    private void initGxtResource(){
    	try {
	    	InputStream gxtResourceIn = getClass().getClassLoader().getResourceAsStream(gxtResources);
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
	        Document gxtResourceDoc = builder.parse(gxtResourceIn);
	        Element resourcesEl = gxtResourceDoc.getDocumentElement();
	        NodeList childNodes = resourcesEl.getChildNodes();
	        if (childNodes != null){
	        	gxtProjects = new HashMap<String, GxtProjectModel>();
	        	for (int i = 0;i < childNodes.getLength();i++){
	        		Node projectNode = childNodes.item(i);
	        		if (projectNode.getNodeType() == Node.ELEMENT_NODE && projectNode.getNodeName().equals("project")){
	        			Element projectEl = (Element) projectNode;
	        			GxtProjectModel gxtPro = new GxtProjectModel();
	        			gxtPro.setContext(projectEl.getAttribute("context"));
	        			gxtPro.setApplication(projectEl.getAttribute("application"));
	        			gxtPro.setModel(projectEl.getAttribute("module"));
	        			gxtProjects.put(gxtPro.getContext() + "." + gxtPro.getApplication(), gxtPro);
	        			NodeList styleNodes = projectEl.getChildNodes();
	        			if (styleNodes != null){
	        				List<String> cssAddresses = new ArrayList<String>();
	        				for (int j = 0;j < styleNodes.getLength();j++){
	        					Node styleNode = styleNodes.item(j);
	        					if (styleNode.getNodeType() == Node.ELEMENT_NODE && styleNode.getNodeName().equals("style")){
	        						Element styleEl = (Element) styleNode;
	        						cssAddresses.add(styleEl.getAttribute("src"));
	        					}
	        				}
	        				gxtPro.setCss_addresses(cssAddresses);
	        			}
	        		}
	        	}
	        }
    	} catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getGxtEntryModule(String context, String application) {
        String key = context + '.' + application;
        if (entries != null && entries.containsKey(key)) {
            return entries.get(key) == null ? null : (String) entries.get(key);
        }
        return null;
    }
    
    public GxtProjectModel getGxtCss(String context, String application) {
    	String key = context + '.' + application;
    	if (gxtProjects != null && gxtProjects.containsKey(key)){
    		return gxtProjects.get(key) == null ? null : gxtProjects.get(key);
    	}
    	return null;
    }

    public Boolean isExcluded(String context, String application) {
        String key = context + '.' + application;
        Boolean isExcluded = excludingMapping.get(key);
        if (isExcluded != null) {
            return isExcluded;
        }
        return false;
    }

}
