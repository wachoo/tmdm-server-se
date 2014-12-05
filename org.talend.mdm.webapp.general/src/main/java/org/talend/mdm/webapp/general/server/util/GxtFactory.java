package org.talend.mdm.webapp.general.server.util;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.*;

public class GxtFactory {

    private static final Logger          LOGGER           = Logger.getLogger(GxtFactory.class);

    // the collection of actions that we know about
    private final Map<String, String>    entries          = new HashMap<String, String>();

    private final Map<String, Boolean>   excludingMapping = new HashMap<String, Boolean>();

    private Map<String, GxtProjectModel> gxtProjects;

    /**
     * Creates a new instance, using the given configuration file.
     */
    public GxtFactory(String gxtRegisterFileName, String excludingFileName, String gxtResources) {
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
            InputStream gxtResourceIn = getClass().getClassLoader().getResourceAsStream(gxtResources);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document gxtResourceDoc = builder.parse(gxtResourceIn);
            Element resourcesEl = gxtResourceDoc.getDocumentElement();
            NodeList childNodes = resourcesEl.getChildNodes();
            if (childNodes != null) {
                gxtProjects = new HashMap<String, GxtProjectModel>();
                for (int i = 0; i < childNodes.getLength(); i++) {
                    Node projectNode = childNodes.item(i);
                    if (projectNode.getNodeType() == Node.ELEMENT_NODE && projectNode.getNodeName().equals("project")) { //$NON-NLS-1
                        Element projectEl = (Element) projectNode;
                        GxtProjectModel gxtPro = new GxtProjectModel();
                        gxtPro.setContext(projectEl.getAttribute("context")); //$NON-NLS-1
                        gxtPro.setApplication(projectEl.getAttribute("application")); //$NON-NLS-1
                        gxtPro.setModel(projectEl.getAttribute("module")); //$NON-NLS-1
                        gxtProjects.put(gxtPro.getContext() + "." + gxtPro.getApplication(), gxtPro);
                        NodeList styleNodes = projectEl.getChildNodes();
                        if (styleNodes != null) {
                            List<String> cssAddresses = new ArrayList<String>();
                            for (int j = 0; j < styleNodes.getLength(); j++) {
                                Node styleNode = styleNodes.item(j);
                                if (styleNode.getNodeType() == Node.ELEMENT_NODE && styleNode.getNodeName().equals("style")) { //$NON-NLS-1
                                    Element styleEl = (Element) styleNode;
                                    cssAddresses.add(styleEl.getAttribute("src")); //$NON-NLS-1
                                }
                            }
                            gxtPro.setCss_addresses(cssAddresses);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public String getGxtEntryModule(String context, String application) {
        String key = context + '.' + application;
        if (entries != null && entries.containsKey(key)) {
            return entries.get(key) == null ? null : entries.get(key);
        }
        return null;
    }

    public GxtProjectModel getGxtCss(String context, String application) {
        String key = context + '.' + application;
        if (gxtProjects != null && gxtProjects.containsKey(key)) {
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
