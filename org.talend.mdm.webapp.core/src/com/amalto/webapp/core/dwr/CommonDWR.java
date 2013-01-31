package com.amalto.webapp.core.dwr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.security.jacc.PolicyContextException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.FieldLabel;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSRegexDataClusterPKs;
import com.amalto.webapp.util.webservices.WSRegexDataModelPKs;
import com.sun.org.apache.xpath.internal.XPathAPI;
import com.sun.org.apache.xpath.internal.objects.XObject;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;

/**
 * 
 * @author asaintguilhem
 *
 */

public class CommonDWR {

	
	//private static HashMap<String,String> xpathToLabel;
	
	public void setLanguage(String language){
		WebContext ctx = WebContextFactory.get();
		ctx.getSession().setAttribute("language",language);
	}

	//TODO Should not be used anymore
	/*
	public String getMenus(String language) throws RemoteException, Exception{
		Menu menu = Menu.getRootMenu();
		//ArrayList<String> menus = new ArrayList<String>();
		//JSONObject rows = new JSONObject();		
		String json = "{";
		int i =0;
		for (Iterator iter = menu.getSubMenus().keySet().iterator(); iter.hasNext(); ) {
			String key = (String) iter.next();
			Menu subMenu= menu.getSubMenus().get(key);
			/*menus.add(subMenu.getLabels().get(language));
			JSONObject fields = new JSONObject();
			fields.put("run",subMenu.getApplication());
			fields.put("name",subMenu.getLabels().get(language));
			fields.put("id",i);			
			rows.put(""+i,fields);*/
/*			json += "'"+i+"':{id:'"+i+"', name: '"+subMenu.getLabels().get(language)+"', desc: '', run: "+subMenu.getApplication()+"}";
			if(iter.hasNext()){
				json += ",";
			}
			i++;
		}
		json +="}";
		//return menus;	
		//'items':{id:'items', name: BROWSE_ITEMS[language], desc: '', run: browseItems}, 
		//return rows.toString();
		return json;
	}*/
	
	public static String[] getClusters(){
		try {
			WSDataClusterPK[] wsDataClustersPK = Util.getPort().getDataClusterPKs(
					new WSRegexDataClusterPKs("*")
					).getWsDataClusterPKs();
			ArrayList<String> list = new ArrayList<String>();			
			filterSystemClustersPK(wsDataClustersPK, list);
			return  (String[])list.toArray(new String[list.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String[] getModels(){
		try {
			WSDataModelPK[] wsDataModelsPK = Util.getPort().getDataModelPKs(
					new WSRegexDataModelPKs("*")
					).getWsDataModelPKs();
			ArrayList<String> list = new ArrayList<String>();
			filterSystemDataModelsPK(wsDataModelsPK, list);
			return  (String[])list.toArray(new String[list.size()]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static void filterSystemClustersPK(WSDataClusterPK[] wsDataClustersPK, ArrayList<String> list) {
		Map<String, XSystemObjects> xDataClustersMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
		for (int i = 0; i < wsDataClustersPK.length; i++) {
			if(!XSystemObjects.isXSystemObject(xDataClustersMap, wsDataClustersPK[i].getPk())){
				list.add(wsDataClustersPK[i].getPk());
			}
		}
	}
	
	public static void filterSystemClustersPK(WSDataClusterPK[] wsDataClustersPK, Map<String, String> map) {
		Map<String, XSystemObjects> xDataClustersMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
		for (int i = 0; i < wsDataClustersPK.length; i++) {
			if(!XSystemObjects.isXSystemObject(xDataClustersMap, wsDataClustersPK[i].getPk())){
				map.put(wsDataClustersPK[i].getPk(),wsDataClustersPK[i].getPk());
			}
		}
	}

	private static void filterSystemDataModelsPK(WSDataModelPK[] wsDataModelsPK, ArrayList<String> list) {
		Map<String, XSystemObjects> xDataModelsMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
		for (int i = 0; i < wsDataModelsPK.length; i++) {
			if(!XSystemObjects.isXSystemObject(xDataModelsMap, wsDataModelsPK[i].getPk())){
				list.add(wsDataModelsPK[i].getPk());
			}	
		}
	}
	
	public static void filterSystemDataModelsPK(WSDataModelPK[] wsDataModelsPK, Map<String, String> map) {
		Map<String, XSystemObjects> xDataModelsMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
		for (int i = 0; i < wsDataModelsPK.length; i++) {
			if(!XSystemObjects.isXSystemObject(xDataModelsMap, wsDataModelsPK[i].getPk())){
				map.put(wsDataModelsPK[i].getPk(), wsDataModelsPK[i].getPk());
			}
		}
	}
	
	
	
	public String getUsername() throws Exception{
		try {
			String xml = Util.getAjaxSubject().getXml();
			Document d = Util.parse(xml);
			String givenname = Util.getFirstTextNode(d,"//givenname");
			String familyname = Util.getFirstTextNode(d,"//familyname");
			String name = "";
			if(familyname!=null && givenname!=null) name = givenname+" "+familyname;
			else name= Util.getAjaxSubject().getUsername();
			return name;
		} catch (PolicyContextException e) {
			e.printStackTrace();
			return "";
		}	
	}
	
	public boolean isTechnicalAdmin(){
		return true;
	}
	
	public String setClusterAndModel(String cluster, String model){
		try {
			Configuration.initialize(cluster,model);
			return "Done";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(cluster+" "+model);
		return "ERROR";
	}
	
	public String getCluster(){
		try {
			Configuration config = Configuration.getInstance();
			return config.getCluster();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public String getModel(){
		try {
			Configuration config = Configuration.getInstance();
			return config.getModel();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}	
	}
/*	
	public static HashMap<String, String> getFieldsByView(String language, boolean includeComplex) 
		throws RemoteException,	Exception {
		Configuration config = Configuration.getInstance();
		HashMap<String, String> xpathToLabel = new HashMap<String,String>();
		String[] concepts = Util.getPort().getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(config.getModel()))).getStrings();
		for (int i = 0; i < concepts.length; i++) {
			xpathToLabel.putAll(getFieldsByDataModel(config.getModel(), concepts[i], language,includeComplex));
		}		
		return xpathToLabel;
	}	
	
	public static HashMap<String,String> getFieldsByBrowseItemsView(String viewPK, String language, boolean includeComplex) 
			throws RemoteException, Exception{
        String concept = getConceptFromBrowseItemView(viewPK);
        Configuration config = Configuration.getInstance();       
        return getFieldsByDataModel(config.getModel(),concept,language, includeComplex);
	}
*/	
	public static String getConceptLabel(String dataModelPK, String concept, String language)
		throws RemoteException, Exception{
        String x_Label = (new FieldLabel(language)).getLabelAnnotation();
		Map<String,XSElementDecl> map = getConceptMap(dataModelPK);	
    	return getLabel(map.get(concept),x_Label).equals("")?map.get(concept).getName():getLabel(map.get(concept),x_Label);
	}
	
	public static HashMap<String,String> getFieldsByDataModel(
			String dataModelPK, String concept, String language, boolean includeComplex) 
			throws RemoteException, Exception{
		return getFieldsByDataModel(dataModelPK,concept,language,includeComplex,false);
	}
	
	public static HashMap<String,String> getFieldsByDataModel(
			String dataModelPK, String concept, String language, boolean includeComplex,boolean includeFKReference) 
			throws RemoteException, Exception{
		return getFieldsByDataModel(dataModelPK,null,concept,language,includeComplex,includeFKReference);
	}
	
	public static HashMap<String,String> getFieldsByDataModel(
			String dataModelPK,Map<String,XSElementDecl> inputConceptMap, String concept, String language, boolean includeComplex,boolean includeFKReference) 
			throws RemoteException, Exception{
		
		WebContext ctx = WebContextFactory.get();
        String x_Label = (new FieldLabel(language)).getLabelAnnotation();
		
		Map<String,XSElementDecl> conceptMap=null;
		if(inputConceptMap==null||inputConceptMap.size()==0) {
			conceptMap = getConceptMap(dataModelPK);
		}else {
			conceptMap= inputConceptMap;
		}
		
    	XSComplexType xsct = (XSComplexType)(conceptMap.get(concept).getType());
    	
    	HashMap<String,String> xpathToLabel = new HashMap<String,String>();
    	xpathToLabel.put(concept,getLabel(conceptMap.get(concept),x_Label).equals("")?conceptMap.get(concept).getName():getLabel(conceptMap.get(concept),x_Label));
    	//xpathToLabel.put(concept,CommonDWR.getConceptLabel(dataModelPK,concept,language));
    	XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
    	for (int j = 0; j < xsp.length; j++) {  
   			getChildren(xsp[j],""+concept,x_Label,includeComplex,includeFKReference,xpathToLabel);
    	}
    	//FIXME
    	//Why we need to update session attribute in this place?
    	//decouple please
    	if(ctx!=null&&ctx.getSession()!=null)ctx.getSession().setAttribute("xpathToLabel",xpathToLabel);
    	return xpathToLabel; 
	}
	
	public static boolean isElementHidden(XSParticle xsp){
		 try {
			ArrayList<String> roles = Util.getAjaxSubject().getRoles();
			XSAnnotation xsa = xsp.getTerm().asElementDecl().getAnnotation();
			if (xsa != null && xsa.getAnnotation() != null) {
                Element el = (Element) xsa.getAnnotation();
                NodeList annotList = el.getChildNodes();
                for (int k = 0; k < annotList.getLength(); k++) {
                    if ("appinfo".equals(annotList.item(k).getLocalName())) {
                        Node source = annotList.item(k).getAttributes().getNamedItem("source");
                        if (source == null)
                            continue;
                        String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();
                        if ("X_Hide".equals(appinfoSource)) {
                            if (roles.contains(annotList.item(k).getFirstChild().getNodeValue())) {
                                return true;
                            }
                        }
                    }
                }
			}
		} catch (PolicyContextException e) {
			e.printStackTrace();
		}
		return false;
	}
	private static void getChildren(XSParticle xsp, String xpathParent, String x_Label, boolean includeComplex,boolean includeFKReference,HashMap<String,String> xpathToLabel){
		//aiming added see 0009563
		if(xsp.getTerm().asModelGroup()!=null){ //is complex type
			XSParticle[] xsps=xsp.getTerm().asModelGroup().getChildren();
			for (int i = 0; i < xsps.length; i++) {
				getChildren(xsps[i],xpathParent,x_Label, includeComplex,includeFKReference, xpathToLabel);
			}
		}
		if(xsp.getTerm().asElementDecl()==null) return;
		//end
		if(xsp.getTerm().asElementDecl().getType().isComplexType()==false || includeComplex==true){
			//Hidden the NO_ACCESS elment
			if(isElementHidden(xsp)){
				return ;
			}
			
			String toPutKey=xpathParent+"/"+xsp.getTerm().asElementDecl().getName();
			if(includeFKReference){
				String foreignkeyPath=getForeignkeyPath(xsp.getTerm().asElementDecl());
				if(foreignkeyPath!=null)toPutKey+="@FK_"+foreignkeyPath;
			}
			
			//FIXME:USE XPATH WITHOUT CONCEPT AS LABEL, MAYBE CAUSE SOME BUGS ON OLD INVOKING PLACES  
			String xlabel="";
			if(getLabel(xsp.getTerm().asElementDecl(),x_Label).equals("")){
				xlabel=xpathParent+"/"+xsp.getTerm().asElementDecl().getName();
				if(xlabel.indexOf("/")!=-1){
					xlabel=xlabel.substring(xlabel.indexOf("/")+1);
				}
			}else{
				xlabel=getLabel(xsp.getTerm().asElementDecl(),x_Label);
			}
			
			xpathToLabel.put(
					toPutKey,
					xlabel
					);	
		}		
		if(xsp.getTerm().asElementDecl().getType().isComplexType()==true ){
			XSParticle particle = xsp.getTerm().asElementDecl()
			.getType().asComplexType().getContentType().asParticle();
			if(particle!=null){
				XSParticle[] xsps = particle.getTerm().asModelGroup().getChildren();
				for (int i = 0; i < xsps.length; i++) {
					getChildren(xsps[i],xpathParent+"/"+xsp.getTerm().asElementDecl().getName(),x_Label, includeComplex,includeFKReference, xpathToLabel);
				}
			}
		}		 
	}
	
	private static String getForeignkeyPath(XSElementDecl elementDecl) {
		
		String foreignkeyPath=null;
		
		//annotation support
		XSAnnotation xsa = elementDecl.getAnnotation();;
		if(xsa!=null && xsa.getAnnotation()!=null){
			Element el = (Element) xsa.getAnnotation();
			NodeList annotList = el.getChildNodes();
			for (int k = 0; k < annotList.getLength(); k++) {
				if("appinfo".equals(annotList.item(k).getLocalName())) {
					Node source=annotList.item(k).getAttributes().getNamedItem("source");
					if(source==null) continue;
					String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();
					if("X_ForeignKey".equals(appinfoSource)){
						foreignkeyPath=annotList.item(k).getFirstChild().getNodeValue();
						break;
					}
				}
			}
		}
		
		return foreignkeyPath;

	}
	
	public static String[] getBusinessConceptKeyPaths(String dataModelPK, String businessConceptName) throws Exception{
    	
		String[] keyPaths=null;
		
		try {
    		String xsdXml = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelPK))).getXsdSchema();
    		Document xsd=Util.parse(xsdXml);
    		
        	String selector = null;
        	String[] fields = null;
    		selector = Util.getTextNodes(
    			xsd.getDocumentElement(),
    			"xsd:element/xsd:unique[@name='"+businessConceptName+"']/xsd:selector/@xpath",
				getRootElement("nsholder",xsd.getDocumentElement().getNamespaceURI(),"xsd")
				)[0];
    	
    		fields =  Util.getTextNodes(
    			xsd.getDocumentElement(),
    			"xsd:element/xsd:unique[@name='"+businessConceptName+"']/xsd:field/@xpath",
				getRootElement("nsholder",xsd.getDocumentElement().getNamespaceURI(),"xsd")
				);
    		
    		String prefixPath="";
    		if(selector!=null){
    			if(selector.length()>0&&!selector.equals("."))prefixPath=selector+"/";
    		}
    		keyPaths=new String[fields.length];
    		for (int i = 0; i < keyPaths.length; i++) {
    			keyPaths[i]=businessConceptName+"/"+prefixPath+fields[i];
			}
    		
    	} catch(TransformerException e) {
    	    String err = "Unable to get the keys for the Business Concept "+businessConceptName+": "+e.getLocalizedMessage();
    		throw new TransformerException(err);
    	} catch (RemoteException e) {
			e.printStackTrace();
			throw new RemoteException();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return keyPaths;
    }
	
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
	
	private static String getLabel(XSElementDecl xsed, String x_Label){
		String label = "";
		try{
			XSAnnotation xsa = xsed.getAnnotation();
			Element el = (Element)xsa.getAnnotation();
			NodeList list = el.getChildNodes();			
			for (int k = 0; k < list.getLength(); k++) {
				if("appinfo".equals(list.item(k).getLocalName())){
					Node source=list.item(k).getAttributes().getNamedItem("source");
					if(source==null) continue;
					String appinfoSource = source.getNodeValue();					
					if(x_Label.equals(appinfoSource)){							
						label = list.item(k).getFirstChild().getNodeValue();		
						//System.out.println("xlabel found :"+label);		
						break;
					}
				}
			}	
		}
		catch(Exception e){
			//System.out.println("no annotation");
		}
		return label;
	}

	public static Map<String,XSElementDecl> getConceptMap(String dataModelPK) 
		throws RemoteException, Exception{
		String xsd = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelPK))).getXsdSchema();
		return com.amalto.core.util.Util.getConceptMap(xsd);
	}
	
	public static String getConceptFromBrowseItemView(String viewPK){
        String concept = viewPK.replaceAll("Browse_items_","");
        concept = concept.replaceAll("#.*","");
        return concept;
	}
	
	public static String getXMLStringFromDocument(Document d) throws Exception{
//		StringWriter writer = new StringWriter();
//		TransformerFactory.newInstance().newTransformer()
//		.transform(new DOMSource(d), new StreamResult(writer));
//		return writer.toString();
		return Util.nodeToString(d.getDocumentElement());
	}
	
	public static NodeList getNodeList(Node contextNode, String xPath) throws TransformerException{
	    XObject xo = XPathAPI.eval(
	    		contextNode,
				xPath
		    );
	    if (xo.getType() != XObject.CLASS_NODESET) return null;
	    return xo.nodelist();
	}
	
	public static LinkedHashMap<String,String> getMapSortedByValue(Map<String,String> map){
		TreeSet<Map.Entry> set = new TreeSet<Map.Entry>(new Comparator() {
			public int compare(Object obj, Object obj1) {
				return ((Comparable) ((Map.Entry) obj).getValue())
						.compareTo(((Map.Entry) obj1).getValue());
			}
		});
		set.addAll(map.entrySet());
		LinkedHashMap<String, String> sortedMap = new LinkedHashMap<String, String>();
		for (Iterator i = set.iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			sortedMap.put((String) entry.getKey(), (String) entry.getValue());
		}
		
		return sortedMap;		
	}
}
