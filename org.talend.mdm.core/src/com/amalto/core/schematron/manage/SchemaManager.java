package com.amalto.core.schematron.manage;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.util.Util;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;


public class SchemaManager {
	
	public static final String X_Hide_AppinfoSource="X_Hide";
	
	public static final String X_Write_AppinfoSource="X_Write";

	
	//TODO: support more source types here
	
	/** A cache of XSSchema, the key is the datamodel name*/
	private static HashMap<String, XSSchemaSet> parsedDatamodelCache=new HashMap<String, XSSchemaSet>();
	
	/**Max size of the cache*/
	private static int CACHE_PDATAMODEL_MAX_SIZE=1000;

	public static void analyzeAnnotationsOfConcept(DataModelPOJO bindingDataModelPOJO,String conceptName,AppinfoSourceHolder appinfoSourceHolder) {
		
		try {
			
			XSSchemaSet result = getParsedDatamodel(bindingDataModelPOJO);
			
			iteratorDatamodel(result,conceptName,appinfoSourceHolder);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void resetParsedDatamodelCache(String datamodelName) {
		if(parsedDatamodelCache.get(datamodelName)!=null)parsedDatamodelCache.remove(datamodelName);
	}

	private static XSSchemaSet getParsedDatamodel(DataModelPOJO bindingDataModelPOJO) throws SAXException,
			IOException {
		
		XSSchemaSet result=null;
		
		if(parsedDatamodelCache.size()>CACHE_PDATAMODEL_MAX_SIZE)parsedDatamodelCache.clear();
		
		//use cache
		if(parsedDatamodelCache.get(bindingDataModelPOJO.getName())!=null){
			result=parsedDatamodelCache.get(bindingDataModelPOJO.getName());
		}else{
			//parse
			XSOMParser reader = new XSOMParser();
			reader.setAnnotationParser(new DomAnnotationParserFactory());
			reader.parse(new StringReader(bindingDataModelPOJO.getSchema()));
			result  = reader.getResult();
			
			parsedDatamodelCache.put(bindingDataModelPOJO.getName(), result);
		}
		
		
		return result;
	}

	private static void iteratorDatamodel(XSSchemaSet result,String conceptName,AppinfoSourceHolder appinfoSourceHolder) {
		// iterate each XSSchema object. XSSchema is a per-namespace schema.
		Iterator itr = result.iterateSchema();
		while( itr.hasNext() ) {
		  XSSchema s = (XSSchema)itr.next();
		  //System.out.println("Target namespace: "+s.getTargetNamespace());
		  visitConcepts(s,conceptName,appinfoSourceHolder);
		}
	}

	private static void visitConcepts(XSSchema s,String conceptName,AppinfoSourceHolder appinfoSourceHolder) {
		//System.out.println(s.getElementDecls());
		Iterator jtr = s.iterateElementDecls();
		  while( jtr.hasNext() ) {
			  
		    XSElementDecl e = (XSElementDecl)jtr.next();
		    if(e!=null&&e.getName().equals(conceptName)){
		    	travelXSElement(e,"/"+conceptName,appinfoSourceHolder);
		        break;	
		    }
		  }
	}

	private static void travelXSElement(XSElementDecl e,String currentXPath,AppinfoSourceHolder appinfoSourceHolder) {
		if(e!=null){
			
//			System.out.print( currentXPath );
//			System.out.println();
			
			parseAnnotation(currentXPath,e,appinfoSourceHolder);
			
			if(e.getType().isComplexType()){
				XSParticle[] subElements=e.getType().asComplexType().getContentType().asParticle().getTerm().asModelGroup().getChildren();
				if(subElements!=null){
					for (int i = 0; i < subElements.length; i++) {
						XSParticle xsParticle=subElements[i];
						XSElementDecl subElement=xsParticle.getTerm().asElementDecl();
						travelXSElement(subElement,currentXPath+"/"+subElement.getName(),appinfoSourceHolder);
					}
				}
				
			}
			
		}
	}

	private static void parseAnnotation(String currentXPath,XSElementDecl e,AppinfoSourceHolder appinfoSourceHolder) {
		if(e.getAnnotation()!=null&&e.getAnnotation().getAnnotation()!=null){
			Element annotations=(Element)e.getAnnotation().getAnnotation();
			NodeList annotList = annotations.getChildNodes();
			for (int k = 0; k < annotList.getLength(); k++) {					
				if("appinfo".equals(annotList.item(k).getLocalName())) {
					Node source=annotList.item(k).getAttributes().getNamedItem("source");
					if(source==null) continue;
					String appinfoSource = source.getNodeValue();
					String appinfoSourceValue =annotList.item(k).getFirstChild().getNodeValue();
					
					if(appinfoSource.equals(X_Hide_AppinfoSource)){							
						appinfoSourceHolder.addSource(X_Hide_AppinfoSource,currentXPath,appinfoSourceValue);
					}
					else if(appinfoSource.equals(X_Write_AppinfoSource)){
						appinfoSourceHolder.addSource(X_Write_AppinfoSource,currentXPath,appinfoSourceValue);					
					}
					
					
				}
			}	
		}
	}	
	
	public static Document executeHideCheck(String itemContent,HashSet<String> roles,AppinfoSourceHolder appinfoSourceHolder) throws Exception {
		if(itemContent==null||itemContent.length()==0)return null;
		
		Document itemDocument=Util.parse(itemContent);
		for (Iterator<String> iterator = roles.iterator(); iterator.hasNext();) {
			String role =  iterator.next();
			executeHideCheck(itemDocument,role,appinfoSourceHolder);
		}
		return itemDocument;
	}
	
	private static void executeHideCheck(Document itemDocument,String role,AppinfoSourceHolder appinfoSourceHolder) throws Exception {
		List<String> result=appinfoSourceHolder.getResult(SchemaManager.X_Hide_AppinfoSource,role);
		
		for (Iterator<String> iterator = result.iterator(); iterator.hasNext();) {
			String xpath =  iterator.next();
			Util.removeXpathFromDocument(itemDocument, xpath,true);
		}
		
		
	}
	
	

}
