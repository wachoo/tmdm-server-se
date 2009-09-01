package com.amalto.webapp.core.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Util2;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;


//This is the new b2box Configuration Mechanism
//It is basically instantiated from an XML File containing configurations - see ConfigurationSmaple.xml in this package



public class XMLConfiguration {
	
	public static String CONFIGURATION_DATA_CLUSTER = "Xtentis Common Conf";
	public static String CONFIGURATION_DATA_MODEL = "Xtentis Common Conf";
	public static long CONFIGURATION_RELOAD_FREQUENCY = 60;
	public static String CONFIGURATION_CONCEPT_NAME = "Configuration";
	
	/**
	 * See if there is an ovveride of the default Data Clusters and Data Models for the Configuration
	 */
	static{
		try {
			Properties properties = MDMConfiguration.getConfiguration();
			if (properties.getProperty("configuration.datacluster")!=null)
				CONFIGURATION_DATA_CLUSTER = properties.getProperty("configuration.datacluster");
			if (properties.getProperty("configuration.datamodel")!=null)
				CONFIGURATION_DATA_MODEL = properties.getProperty("configuration.datamodel");
			if (properties.getProperty("configuration.reloadfrequency")!=null)
				CONFIGURATION_RELOAD_FREQUENCY = Long.parseLong(properties.getProperty("configuration.reloadfrequency"));
		} catch (Exception e) {}
	}

	private String name;
	private XMLConfiguration_Description description = new XMLConfiguration_Description();
	private String documentsDataCluster;
	private String documentsDataModel;
	private String dateFormat;
	private ArrayList<XMLConfiguration_Document> documents = new ArrayList<XMLConfiguration_Document>();


	/**
	 * Singleton
	 */
	private static XMLConfiguration instance;
	private static long lastReload = 0;

	/**
	 * Singleton
	 *
	 */
	protected XMLConfiguration(){
	}	

	public XMLConfiguration_Document getDoc(String conceptName) {
		Iterator<XMLConfiguration_Document> iter = documents.iterator();
		while(iter.hasNext()) {
			XMLConfiguration_Document doc = iter.next();
			if (doc.getConceptName().equals(conceptName)) return doc;
		}
		
		return null;
	}
	
	public XMLConfiguration_Document getDocByDocumentName(String documentName) {
		Iterator<XMLConfiguration_Document> iter = documents.iterator();
		while(iter.hasNext()) {
			XMLConfiguration_Document doc = iter.next();
			if (doc.getName().equals(documentName)) return doc;
		}
		
		return null;
	}
	
	public static HashMap<String, XMLConfiguration> getConfigurationsFromXMLConfigurationFile(String localFilename) throws XtentisWebappException{
		try {
			HashMap<String, XMLConfiguration> configurations = new HashMap<String, XMLConfiguration>();
			//read the file that must be the local to the web app server - expects an UTF-8 encoded file
			String xml = Util2.readTextFile(localFilename, "UTF8");
			Element root = Util.parse(xml).getDocumentElement(); 
			//retrieve the configuration names
			NodeList nl =  Util.getNodeList(root, "ListOfb2boxConfigurations/b2boxConfiguration");
			if ((nl!=null) && nl.getLength()>0) {
				for (int i = 0; i < nl.getLength(); i++) {
					Element ce = (Element) nl.item(i);
					configurations.put(
							Util.getFirstTextNode(ce, "Name"),
							parseXMLConfiguration(ce)
					);
				}
			}
			return configurations;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to retrive the configuration names from the local file "+localFilename+".");
		}
	}
	
	public static String[] getConfigurationNamesFromXMLConfigurationFile(String localFilename) throws XtentisWebappException{
		try {
			//read the file that must be the local to the web app server - expects an UTF-8 encoded file
			String xml = Util2.readTextFile(localFilename, "UTF8");
			Element root = Util.parse(xml).getDocumentElement(); 
			//retrieve the configuration names
			return Util.getTextNodes(root, "ListOfb2boxConfigurations/b2boxConfiguration/Name");
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to retrive the configuration names from the local file "+localFilename+".");
		}
	}
	
	
	public static XMLConfiguration getConfigurationFromXMLConfigurationFile(String localFilename, String configurationName) throws XtentisWebappException{
		try {
			//read the file that must be the local to the web app server - expects an UTF-8 encoded file
			String xml = Util2.readTextFile(localFilename, "UTF8");
			Element root = Util.parse(xml).getDocumentElement(); 
			//retrieve the conf
			Element ce = Util2.getFirstElement(root,"ListOfb2boxConfigurations/b2boxConfiguration[Name='"+configurationName+"']");
			if (ce==null)  
				throw new XtentisWebappException("The b2box Configuration named "+configurationName+" cannot be found in file \n"+xml);
			//parse the conf 
			return parseXMLConfiguration(ce);
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException("Unable to read the configuration "+configurationName+" from the local file "+localFilename+".");
		}
	}

	
	public static XMLConfiguration parseXMLConfiguration(Element b2boxConfigurationElement) throws XtentisWebappException{
		
		try {
			org.apache.log4j.Logger.getLogger(XMLConfiguration.class).trace("parseXMLConfiguration() "+Util.nodeToString(b2boxConfigurationElement));
		} catch (Exception e1) {
			org.apache.log4j.Logger.getLogger(XMLConfiguration.class).info("parseXMLConfiguration() ERROR SYSTRACE ",e1);
		}
		
		
		try {
			
			//System.err.println("point 1");
			XMLConfiguration conf = new XMLConfiguration();
			conf.setName(Util.getFirstTextNode(b2boxConfigurationElement, "Name"));
			conf.setDocumentsDataCluster(Util.getFirstTextNode(b2boxConfigurationElement, "DataCluster"));
			conf.setDocumentsDataModel(Util.getFirstTextNode(b2boxConfigurationElement, "DataModel"));
			conf.setDateFormat(Util.getFirstTextNode(b2boxConfigurationElement, "DateFormat"));
			conf.setDescription(XMLConfiguration_Description.parseXMLConfiguration_Description(Util2.getFirstElement(b2boxConfigurationElement, "Description")));
			NodeList nl = Util.getNodeList(b2boxConfigurationElement, "ListOfDocuments/Document");
			if (nl!=null) {
				//System.err.println("point 2");
				for (int i = 0; i < nl.getLength(); i++) {
					Element desc = (Element) nl.item(i);
					conf.getDocuments().add(
						XMLConfiguration_Document.parseXMLConfiguration_Document(desc)	
					);
				}
			}
			return conf;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			try {
				throw new XtentisWebappException("Unable to read the XMLConfiguration from "+Util.nodeToString(b2boxConfigurationElement));
			} catch (Exception ex) {
				throw new XtentisWebappException("Unable to read the XMLConfiguration from element "+b2boxConfigurationElement.getLocalName());
			}
		}
	}
	
	
	

	@Override
	/**
	 * Returns an XML version of the Configuration
	 */
	public String toString() {
		String xml="<Configuration>";
		xml+="<Application>b2box</Application>";
		xml+="<b2boxConfiguration>";
		xml+="	<Name>"+name+"</Name>";
		xml+=description.toString();
		xml+="	<DataCluster>"+documentsDataCluster+"</DataCluster>";
		xml+="	<DataModel>"+documentsDataModel+"</DataModel>";
		xml+="	<DateFormat>"+dateFormat+"</DateFormat>";
		xml+="	<ListOfDocuments>";
		for (Iterator<XMLConfiguration_Document> iter = documents.iterator(); iter.hasNext(); ) {
			XMLConfiguration_Document doc = iter.next();
			xml+=doc.toString();
		}
		xml+="	</ListOfDocuments>";
		xml+="</b2boxConfiguration>";
		xml+="</Configuration>";
		
		org.apache.log4j.Logger.getLogger(this.getClass()).info("Configuration\n "+xml);
		
		return xml;
	}


	public void store() throws XtentisWebappException{
		try {
			Util.getPort().putItem(
					new WSPutItem(
							new WSDataClusterPK(CONFIGURATION_DATA_CLUSTER), 
							this.toString(),
							new WSDataModelPK(CONFIGURATION_DATA_MODEL),false));
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisWebappException(
					"Unable to store the configuration in "+CONFIGURATION_DATA_CLUSTER+
					" using model "+CONFIGURATION_DATA_MODEL+"."
			);
		}
	}

	public static XMLConfiguration load() throws XtentisWebappException{
		String xml = "";
		try {
			xml = Util.getPort().getItem(
					new WSGetItem(
							new WSItemPK(
									new WSDataClusterPK(CONFIGURATION_DATA_CLUSTER),
									"Configuration", 
									new String[] { "b2box" }
							)
					)
			).getContent();
		
			//Get the b2box Configuration Element
			Element ce = Util2.getFirstElement(Util.parse(xml).getDocumentElement(), "b2boxConfiguration");
			//Parse it to an XMLCOnfiguration Object
			return parseXMLConfiguration(ce);
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisWebappException(
					"Unable to load the b2box configuration in "+CONFIGURATION_DATA_CLUSTER+
					e.getClass().getName()+": "+e.getMessage()
			);
		}
	}

	/**
	 * So that "old" b2box pages continue to work with this new package
	 * Will be deprecated soon
	 * @return an Old Style COnfiguration Object
	 * @throws XtentisException
	 */
/*	public Configuration getOldConfiguration() throws XtentisException{
		
		
		try {
			Configuration configuration = new Configuration();
			configuration.setCluster(documentsDataCluster);
			configuration.setDataModel(documentsDataModel);
			configuration.setDateFormat(dateFormat);
			//grab document Types and conceptNames/searchRoots
			ArrayList<String> docTypes = new ArrayList<String>();
			HashMap<String, String> conceptNames = new HashMap<String, String>();
			for (Iterator iter = documents.iterator(); iter.hasNext(); ) {
				XMLConfiguration_Document document = (XMLConfiguration_Document) iter.next();
				docTypes.add(document.getName());
				conceptNames.put(document.getName(),document.getConceptName());
			}
			configuration.setDocumentTypes(docTypes.toArray(new String[docTypes.size()]));
			configuration.setSearchRoot(conceptNames);
			configuration.setPivotFormat(name);
			//grab viewables which are actually the first viewable + the keys
			HashMap<String,ViewablePaths> viewables = new HashMap<String, ViewablePaths>();
			for (Iterator iter = documents.iterator(); iter.hasNext(); ) {
				XMLConfiguration_Document document = (XMLConfiguration_Document) iter.next();
				ViewablePaths paths = new ViewablePaths();
				String docName = document.getName();
				
				
				// Logger.getLogger(XMLConfiguration.class).error("docName: " + docName);
				
				ArrayList<String> xPathsList = new ArrayList<String>();
				
				Iterator<XMLConfiguration_Searchable> myPathsIter = document.getViewables().iterator();
				while(myPathsIter.hasNext()) {
					xPathsList.add(myPathsIter.next().getXPath());
				}
*/				
				/*

				//first viewables
				xPathsList.add(document.getViewables().get(0).getXPath());
				
				
				
				
				
				Logger.getLogger(XMLConfiguration.class).error("aaa: " + document.getViewables().get(0).getXPath());
				
				//keys
				//ArrayList<String> ks = new ArrayList<String>();
				for (Iterator iterator = document.getKeys().iterator(); iterator.hasNext(); ) {
					XMLConfiguration_XPath key = (XMLConfiguration_XPath) iterator.next();
					xPathsList.add(key.getXPath());
					Logger.getLogger(XMLConfiguration.class).error("bbb: " + key.getXPath());
				}
				*/
				
/*				
				
				
				
				//xPathsList.addAll(ks);
				paths.setXPath(xPathsList);
				viewables.put(docName, paths);
			}			
			configuration.setViewables(viewables);
			return configuration;
		} catch (Exception e) {
			throw new XtentisException(
					"Unable to get an old style configuration for the the configuration named "+name+
					": "+e.getClass().getName()+": "+e.getMessage()
			);
		}
	}
	
	*/
	
	/*******************************************************************************************
	 * 
	 * Singleton
	 * 
	 *******************************************************************************************/


	public static XMLConfiguration getInstance() throws XtentisWebappException{
		
		//check if we need to reload
		if (System.currentTimeMillis()>lastReload+CONFIGURATION_RELOAD_FREQUENCY*1000) {
			instance = null;
		}
		
		if (instance==null) {
			org.apache.log4j.Logger.getLogger(XMLConfiguration.class).debug("getInstance() (Re)Loading XML Configuration");
			//attempt to load an existing one
			instance = XMLConfiguration.load();
			lastReload = System.currentTimeMillis();
			if (instance==null) {
				throw new XtentisWebappException("Unable to get an XML Configuration instance either from memory or storage");
			}
		}
 		return instance;
	}


	public static void setInstance(XMLConfiguration instance) throws XtentisWebappException{
		XMLConfiguration.instance = instance;
		instance.store();
	}

	/*******************************************************************************************
	 * 
	 * Bean Stuff
	 * 
	 *******************************************************************************************/
	
	

	public String getDocumentsDataCluster() {
		return documentsDataCluster;
	}


	public void setDocumentsDataCluster(String dataCluster) {
		this.documentsDataCluster = dataCluster;
	}


	public String getDocumentsDataModel() {
		return documentsDataModel;
	}


	public void setDocumentsDataModel(String dataModel) {
		this.documentsDataModel = dataModel;
	}


	public String getDateFormat() {
		return dateFormat;
	}


	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}


	public XMLConfiguration_Description getDescription() {
		return description;
	}


	public void setDescription(XMLConfiguration_Description description) {
		this.description = description;
	}


	public ArrayList<XMLConfiguration_Document> getDocuments() {
		return documents;
	}


	public void setDocuments(ArrayList<XMLConfiguration_Document> documents) {
		this.documents = documents;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	
	

	
}
