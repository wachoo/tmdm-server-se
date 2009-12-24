package com.amalto.webapp.v3.workflow.tasks.schema;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.dwr.CommonDWR;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

public class BPMSchemaManager {
	
	public static Map<String, VAnnotationInfo> parseAnnotationMapOnAConcept(
			String dataModelPK, Map<String, String> xpathVariablesMap) throws RemoteException, Exception {
		Map<String,VAnnotationInfo> annotationMap=new HashMap<String,VAnnotationInfo>();
		
			
			//get concept map
			Map<String,XSElementDecl> conceptMap = CommonDWR.getConceptMap(dataModelPK);
			
	    	//determine concept (FIXME:assume all variables are under the same concept and xpath should be complete)
	    	String conceptName=null;
	    	if(xpathVariablesMap!=null&&xpathVariablesMap.size()>0){
	    		String aXpathVariableValue=xpathVariablesMap.get(xpathVariablesMap.keySet().iterator().next());
	    		aXpathVariableValue = normalizeXpath(aXpathVariableValue);
	    		//System.out.println(aXpathVariableValue);
	    		conceptName=aXpathVariableValue.substring(0,aXpathVariableValue.indexOf("/"));
	    	}
	    	
	    	//get concept element
	    	XSElementDecl decl = conceptMap.get(conceptName);
			if (decl == null) {
				String err = "Concept '"+conceptName+"' is not found in model '"+dataModelPK+"'";
				org.apache.log4j.Logger.getLogger(BPMSchemaManager.class).error(err);
				throw new RemoteException(err);
			}
			
			//travel concept xsd
			travelXSElement(decl,conceptName,xpathVariablesMap,annotationMap);
			
		
		return annotationMap;
	}

	private static String normalizeXpath(String aXpathVariableValue) {
		if(aXpathVariableValue.startsWith("/"))aXpathVariableValue=aXpathVariableValue.substring(1);
		if(aXpathVariableValue.startsWith("/"))aXpathVariableValue=aXpathVariableValue.substring(1);
		if(aXpathVariableValue.endsWith("/"))aXpathVariableValue=aXpathVariableValue.substring(0,aXpathVariableValue.length()-1);
		aXpathVariableValue=aXpathVariableValue.replace("//", "/");
		return aXpathVariableValue;
	}
	
	private static void travelXSElement(XSElementDecl e,String currentXPath,Map<String,String> xpathVariablesMap,Map<String,VAnnotationInfo> annotationMap) {
		if(e!=null){
			
//			System.out.print( currentXPath );
//			System.out.println();
			
			setAnnoationNode(e,currentXPath, xpathVariablesMap, annotationMap);
			
			if(e.getType().isComplexType()){
				XSParticle[] subElements=e.getType().asComplexType().getContentType().asParticle().getTerm().asModelGroup().getChildren();
				if(subElements!=null){
					for (int i = 0; i < subElements.length; i++) {
						XSParticle xsParticle=subElements[i];
						XSElementDecl subElement=xsParticle.getTerm().asElementDecl();
						travelXSElement(subElement,currentXPath+"/"+subElement.getName(),xpathVariablesMap,annotationMap);
					}
				}
				
			}
			
		}
	}

	private static void setAnnoationNode(XSElementDecl e,String currentXPath,Map<String, String> xpathVariablesMap,Map<String,VAnnotationInfo> annotationMap) {
		for (Iterator iterator = xpathVariablesMap.keySet().iterator(); iterator.hasNext();) {
			String vname = (String) iterator.next();
			String xpath=normalizeXpath(xpathVariablesMap.get(vname));
			if(currentXPath.equals(xpath)){
				//assemble annotation map
				VAnnotationInfo vAnnotationInfo=new VAnnotationInfo();
				
				//parse annotation
				Map<String, String> labelMap=new HashMap<String, String>();
				String accessRightToken=null;
				if(e.getAnnotation()!=null&&e.getAnnotation().getAnnotation()!=null){
					Element annotations=(Element)e.getAnnotation().getAnnotation();
					NodeList annotList = annotations.getChildNodes();
					for (int k = 0; k < annotList.getLength(); k++) {					
						if("appinfo".equals(annotList.item(k).getLocalName())) {
							Node source=annotList.item(k).getAttributes().getNamedItem("source");
							if(source==null) continue;
							String appinfoSource = source.getNodeValue();
							String appinfoSourceValue =annotList.item(k).getFirstChild().getNodeValue();
							
							if(appinfoSource.equals("X_Workflow")){							
								accessRightToken=appinfoSourceValue;
							}
							else if(appinfoSource.contains("X_Label")){
								String lanPrefix=appinfoSource.substring(appinfoSource.lastIndexOf("_")+1);
								labelMap.put(lanPrefix, appinfoSourceValue);				
							}
						}
					}	
				}
				
				vAnnotationInfo.setLabelMap(labelMap);
				vAnnotationInfo.setAccessRightToken(accessRightToken);
				annotationMap.put(vname.substring(0,vname.lastIndexOf("_xpath")), vAnnotationInfo);
				break;
			}
			
		}
	}
	
    //test
//	public static void main(String[] args) {
//		
//		String var0Name="name_xpath";
//		String var0Value="/Test/name";
//		String var1Name="country_xpath";
//		String var1Value="/Test/country";
//		String var2Name="groupLeader_xpath";
//		String var2Value="/Test/group/groupLeader";
//		
//		String dataModelPK="User";
//		Map<String,String> xpathVariablesMap=new HashMap<String, String>();
//		xpathVariablesMap.put(var0Name, var0Value);
//		xpathVariablesMap.put(var1Name, var1Value);
//		xpathVariablesMap.put(var2Name, var2Value);
//		
//		Map<String, VAnnotationInfo> annotationMap = parseAnnotationMapOnAConcept(dataModelPK, xpathVariablesMap);
//		
//		System.out.println(annotationMap);
//	}

}
