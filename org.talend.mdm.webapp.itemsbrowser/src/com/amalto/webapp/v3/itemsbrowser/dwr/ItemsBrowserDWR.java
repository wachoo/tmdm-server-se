package com.amalto.webapp.v3.itemsbrowser.dwr;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.security.jacc.PolicyContextException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringEscapeUtils;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.jboss.dom4j.DocumentException;
import org.jboss.dom4j.io.SAXReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSByteArray;
import com.amalto.webapp.util.webservices.WSConceptKey;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDeleteItem;
import com.amalto.webapp.util.webservices.WSDropItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSExecuteTransformerV2;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetTransformerPKs;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSGetViewPKs;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.amalto.webapp.util.webservices.WSRouteItemV2;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSTransformerContext;
import com.amalto.webapp.util.webservices.WSTransformerContextPipelinePipelineItem;
import com.amalto.webapp.util.webservices.WSTransformerPK;
import com.amalto.webapp.util.webservices.WSTransformerV2PK;
import com.amalto.webapp.util.webservices.WSTypedContent;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSWhereOr;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.amalto.webapp.v3.itemsbrowser.bean.Restriction;
import com.amalto.webapp.v3.itemsbrowser.bean.TreeNode;
import com.amalto.webapp.v3.itemsbrowser.bean.View;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;

/**cluster
 * 
 * 
 * @author asaintguilhem
 *
 */

public class ItemsBrowserDWR {

	public ItemsBrowserDWR() {
		super();
	}
	
	
	/**
	 * return a list of "browse items" views
	 * @param language
	 * @return a map name->description
	 * @throws RemoteException
	 * @throws Exception
	 */
	public Map<String,String> getViewsList(String language) throws RemoteException, Exception{
		//Configuration config = Configuration.getInstance();
		Configuration config = Configuration.getInstance(true);
		String model = config.getModel();
		String [] businessConcept = Util.getPort().	getBusinessConcepts(
					new WSGetBusinessConcepts(
							new WSDataModelPK(model)
						)
					).getStrings();
		ArrayList<String> bc = new ArrayList<String>();
		for (int i = 0; i < businessConcept.length; i++) {
			bc.add(businessConcept[i]);
		}
		WSViewPK[] wsViewsPK = Util.getPort().getViewPKs(new WSGetViewPKs("Browse_items.*")).getWsViewPK();
		String[] names = new String[wsViewsPK.length];
		TreeMap<String,String> views = new TreeMap<String,String>();
		Pattern p = Pattern.compile(".*\\["+language.toUpperCase()+":(.*?)\\].*",Pattern.DOTALL);
		for (int i = 0; i < wsViewsPK.length; i++) {
			WSView wsview = Util.getPort().getView(new WSGetView(wsViewsPK[i]));
			String concept = wsview.getName().replaceAll("Browse_items_","").replaceAll("#.*","");
			names[i] = wsViewsPK[i].getPk();
			if(		//wsviews[i].getWsDataClusterPK().getPk().equals(cluster) 
					//&& wsviews[i].getWsDataModelPK().getPk().equals(model) && 
					bc.contains(concept)
					){
				
				views.put(wsview.getName(),p.matcher(wsview.getDescription()).replaceAll("$1"));
			}
		}	
		return CommonDWR.getMapSortedByValue(views);
	}
	
	public View getView(String viewPK, String language){
		try {
			WebContext ctx = WebContextFactory.get();
			String concept =  CommonDWR.getConceptFromBrowseItemView(viewPK);
			Configuration config = Configuration.getInstance();
			String model = config.getModel();
			View view = new View(viewPK, language);
			WSConceptKey key = Util.getPort().getBusinessConceptKey(
					new WSGetBusinessConceptKey(
							new WSDataModelPK(model),
							concept));
			String[] keys = key.getFields();
			for (int i = 0; i < keys.length; i++) {
				if(".".equals(key.getSelector()))
					keys[i] = "/"+concept+"/"+keys[i];					
				else
					keys[i] = key.getSelector()+keys[i];
			}
			view.setKeys(key.getFields());
			ctx.getSession().setAttribute("foreignKeys",key.getFields());
			return view;
		} catch (RemoteException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * return a list of searchable elements of a browse items list
	 * @param viewPK
	 * @param language
	 * @return a map xpath->label
	 */
	/* public HashMap<String,String> getSearchables(String viewPK,String language){
		try{
			String[] searchables = new View(viewPK,language).getSearchables();
			HashMap<String,String> labelSearchables = new HashMap<String,String>();
			HashMap<String,String> xpathToLabel = CommonDWR.getFieldsByBrowseItemsView(viewPK,language,true);	
			Configuration config = Configuration.getInstance();
			String concept = CommonDWR.getConceptFromBrowseItemView( viewPK);
			xpathToLabel.put(concept,CommonDWR.getConceptLabel(config.getModel(),concept,language));
			for (int i = 0; i < searchables.length; i++) {
				labelSearchables.put(searchables[i],xpathToLabel.get(searchables[i]));
			}
			return labelSearchables.put;			
		}
		catch(Exception e){
			return null;
		}
	}*/
	
	/**
	 * return a list of viewable elements o a browse items list
	 * used for column header of a grid
	 * @param viewPK
	 * @param language
	 * @return an array of label
	 */
	
	public String[] getViewables(String viewPK, String language){		
		WebContext ctx = WebContextFactory.get();
		ctx.getSession().setAttribute("viewNameItems",null);
		try {
			Configuration config = Configuration.getInstance();
			String[] viewables = new View(viewPK,language).getViewables();
			String[] labelViewables = new String[viewables.length];
			HashMap<String,String> xpathToLabel = CommonDWR.getFieldsByDataModel(
					config.getModel(),
					CommonDWR.getConceptFromBrowseItemView(viewPK),
					language, true);
			for (int i = 0; i < viewables.length; i++) {
				labelViewables[i] = xpathToLabel.get(viewables[i]);
				//System.out.println(labelViewables[i]);
			}
			return labelViewables;
		}catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	public TreeNode getRootNode(String concept, String language) throws RemoteException, Exception{
		Configuration config = Configuration.getInstance();
		String dataModelPK = config.getModel();
		Map<String,XSElementDecl> map = CommonDWR.getConceptMap(dataModelPK);
		XSElementDecl decl = map.get(concept);
		if (decl == null) {
			String err = "Concept '"+concept+"' is not found in model '"+dataModelPK+"'";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
			throw new RemoteException(err);
		}
    	XSAnnotation xsa = decl.getAnnotation();    	
    	TreeNode rootNode = new TreeNode();
		ArrayList<String> roles = Util.getAjaxSubject().getRoles();
    	rootNode.fetchAnnotations(xsa,roles, language);
    	return rootNode;
	}
	
	
	
	/**
	 * start to parse the xsd.
	 *  set the maps : idToParticle, idToXpath and the list : nodeAutorization in the session
	 * @param concept
	 * @param ids
	 * @param nodeId the id of the root node in yui tree
	 * @return an error or succes message
	 */
	public String setTree(String concept, String[] ids, int nodeId, boolean foreignKey, int docIndex){
        WebContext ctx = WebContextFactory.get();	
		try {

			Configuration config = Configuration.getInstance();
			String dataModelPK = config.getModel();
			String dataClusterPK = config.getCluster();

			// get item
	        if(ids!=null){
				WSItem wsItem = Util.getPort().getItem(
						new WSGetItem(new WSItemPK(
								new WSDataClusterPK(dataClusterPK),
								concept, 
								ids
						))
				);
				Document document = Util.parse(wsItem.getContent());				
				if(foreignKey) ctx.getSession().setAttribute("itemDocumentFK",document);
				else ctx.getSession().setAttribute("itemDocument"+docIndex,document);
	        }
	        else{
	        	createItem(concept, docIndex);
	        }
			
			Map<String,XSElementDecl> map = CommonDWR.getConceptMap(dataModelPK);
        	
        	XSComplexType xsct = (XSComplexType)(map.get(concept).getType());
        	
        	HashMap<Integer,XSParticle> idToParticle;
			if(ctx.getSession().getAttribute("idToParticle") == null) {
				idToParticle = new HashMap<Integer,XSParticle>();
			}
			else {
				idToParticle = (HashMap<Integer,XSParticle>) ctx.getSession().getAttribute("idToParticle");
			}
			idToParticle.put(nodeId,xsct.getContentType().asParticle());
			ctx.getSession().setAttribute("idToParticle",idToParticle);
			
			HashMap<Integer,String> idToXpath;
			if(ctx.getSession().getAttribute("idToXpath") == null) {
				idToXpath = new HashMap<Integer,String>();
			}
			else {
				idToXpath = (HashMap<Integer,String>) ctx.getSession().getAttribute("idToXpath");
			}
			idToXpath.put(nodeId,"/"+concept);			
			ctx.getSession().setAttribute("idToXpath",idToXpath);
			
			HashMap<String,XSParticle> xpathToParticle = new HashMap<String,XSParticle>();					
			xpathToParticle.put("/"+concept,xsct.getContentType().asParticle());			
			ctx.getSession().setAttribute("xpathToParticle",xpathToParticle);
			
			ArrayList<String> nodeAutorization = new ArrayList<String>();
			ctx.getSession().setAttribute("nodeAutorization",nodeAutorization);
			
			return "OK";
		} catch (Exception e) {
			e.printStackTrace();
			return "ERROR";
		}		
	}
	private void setChildrenWithKeyMask(int id, String language, boolean foreignKey, int docIndex, boolean maskKey, boolean choice, XSParticle xsp,ArrayList<TreeNode> list,HashMap<String,TreeNode> xpathToTreeNode){
		//aiming added see 0009563
		if(xsp.getTerm().asModelGroup()!=null){ //is complex type
			XSParticle[] xsps=xsp.getTerm().asModelGroup().getChildren();
			if("choice".equals(xsp.getTerm().asModelGroup().getCompositor().toString()))
				choice = true;
			for (int i = 0; i < xsps.length; i++) {
				setChildrenWithKeyMask(id,language,foreignKey,docIndex,maskKey,choice,xsps[i],list,xpathToTreeNode);
			}
		}
		if(xsp.getTerm().asElementDecl()==null) return;
		//end
		
		WebContext ctx = WebContextFactory.get();	
		HashMap<Integer,XSParticle> idToParticle = 
			(HashMap<Integer,XSParticle>) ctx.getSession().getAttribute("idToParticle");
		HashMap<Integer,String> idToXpath = 
			(HashMap<Integer,String>) ctx.getSession().getAttribute("idToXpath");
		HashMap<String,XSParticle> xpathToParticle = 
			(HashMap<String,XSParticle>) ctx.getSession().getAttribute("xpathToParticle");
		ArrayList<String> nodeAutorization = 
			(ArrayList<String>) ctx.getSession().getAttribute("nodeAutorization");
		Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);
		String[] keys = (String[]) ctx.getSession().getAttribute("foreignKeys");
		//add by ymli
		/*ArrayList<String> pathToType = 
			(ArrayList<String>) ctx.getSession().getAttribute("pathToType");*/
		
		
		ArrayList<String> roles = new ArrayList<String>();
		try {
			roles = Util.getAjaxSubject().getRoles();
		} catch (PolicyContextException e1) {
			e1.printStackTrace();
		}		

		
		if(foreignKey) d = (Document) ctx.getSession().getAttribute("itemDocumentFK");		
		TreeNode treeNode = new TreeNode();    		
		treeNode.setChoice(choice);
		String xpath = idToXpath.get(id)+"/"+xsp.getTerm().asElementDecl().getName();
		
		if(xpathToTreeNode.containsKey(idToXpath.get(id)))
			treeNode.setParent(xpathToTreeNode.get(idToXpath.get(id)));
		
		
		int maxOccurs = xsp.getMaxOccurs();   	
		//idToXpath.put(nodeCount,xpath);//keep map <node id -> xpath>  in the session
		treeNode.setName(xsp.getTerm().asElementDecl().getName());
		treeNode.setDocumentation("");
		String typeNameTmp = "";
		treeNode.setVisible(true);
		
//		treeNode.setParent(parentNode);
		
		if(xsp.getTerm().asElementDecl().getType().getName()!=null)	
			typeNameTmp = xsp.getTerm().asElementDecl().getType().getName();
		
		//annotation support
		XSAnnotation xsa = xsp.getTerm().asElementDecl().getAnnotation();
		try {
			treeNode.fetchAnnotations(xsa, roles, language);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("NO ANNOT");
		}


		treeNode.setTypeName(typeNameTmp);
		treeNode.setXmlTag(xsp.getTerm().asElementDecl().getName());
		treeNode.setNodeId(nodeCount);
		treeNode.setMaxOccurs(maxOccurs);
		treeNode.setMinOccurs(xsp.getMinOccurs());
		treeNode.setNillable(xsp.getTerm().asElementDecl().isNillable());
		
		// this child is a complex type
		if(xsp.getTerm().asElementDecl().getType().isComplexType()==true) {    			
			XSParticle particle = xsp.getTerm().asElementDecl()
					.getType().asComplexType().getContentType().asParticle();
			idToParticle.put(nodeCount, particle);    	
			if(!treeNode.isReadOnly()){
				nodeAutorization.add(xpath);
			}
			treeNode.setType("complex");
			
			
			xpathToTreeNode.put(xpath, treeNode);
			if(maxOccurs<0 || maxOccurs>1){	//maxoccurs<0 is unbounded			
				try {
					NodeList nodeList = Util.getNodeList(d,xpath);
					for (int i = 0; i < nodeList.getLength(); i++) { 
						idToXpath.put(nodeCount,xpath+"["+(i+1)+"]");
						xpathToParticle.put(xpath+"["+(i+1)+"]",particle);
						TreeNode treeNodeTmp = (TreeNode) treeNode.clone();
						treeNodeTmp.setNodeId(nodeCount);
						idToParticle.put(nodeCount, particle);
						// TODO check addThisNode
		    			list.add(treeNodeTmp);  
						nodeCount++;
					}
					if(nodeList.getLength() == 0){
	    				idToXpath.put(nodeCount,xpath);
	    				xpathToParticle.put(xpath,particle);
	    				if(treeNode.isVisible()==true) {
	    	    			list.add(treeNode);    			
	    	    			nodeCount++; 
	    	    		} 
					}
				} catch (Exception e) {
					e.printStackTrace();
				}					
			}
			else {
				idToXpath.put(nodeCount,xpath);
				xpathToParticle.put(xpath,particle);
				if(treeNode.isVisible()==true) {
	    			list.add(treeNode);    			
	    			nodeCount++; 
	    		} 
			}
		}
		// this child is a simple type
		else {
			idToParticle.put(nodeCount, null);
			treeNode.setType("simple"); 

			// restriction support
			ArrayList<Restriction> restrictions = new ArrayList<Restriction>();
			ArrayList<String> enumeration = new ArrayList<String>();
			 Iterator<XSFacet> it = xsp.getTerm().asElementDecl().getType()
				.asSimpleType().asRestriction().iterateDeclaredFacets();
			 while (it.hasNext()) {
				XSFacet xsf = it.next();
				if("enumeration".equals(xsf.getName())) {
					enumeration.add(xsf.getValue().toString());
				}
				else{
					Restriction r = new Restriction(xsf.getName(),xsf.getValue().toString());
					restrictions.add(r);
				}					
			}
			treeNode.setEnumeration(enumeration);
			treeNode.setRestrictions(restrictions);
			
			// the user cannot edit any field when a foreign key is displayed
			if(foreignKey){
				treeNode.setReadOnly(true);
			}
			for (int i = 0; i < keys.length; i++) {
				if(xpath.equals(keys[i])){
					treeNode.setKey(true);
					treeNode.setKeyIndex(i);
					//treeNode.setReadOnly(true);
				}
					
			}

			
			// max occurs > 1 support
			try { 
				if(maxOccurs<0 || maxOccurs>1){
					NodeList nodeList = Util.getNodeList(d,xpath);
					for (int i = 0; i < nodeList.getLength(); i++) {
						if(!treeNode.isReadOnly())
							nodeAutorization.add(xpath+"["+(i+1)+"]");
						idToXpath.put(nodeCount,xpath+"["+(i+1)+"]");
						TreeNode treeNodeTmp = (TreeNode) treeNode.clone();
						if(nodeList.item(i).getFirstChild()!=null)
						{
							treeNodeTmp.setValue(nodeList.item(i).getFirstChild().getNodeValue());
//							setForeignKeyValueInfoToTreeNode(xpath, treeNodeTmp, d, nodeList.item(i).getFirstChild().getNodeValue());
						}
						treeNodeTmp.setNodeId(nodeCount);
						// TODO check addThisNode
		    			list.add(treeNodeTmp);  
						nodeCount++;
					}
					if(nodeList.getLength() == 0){
						if(!treeNode.isReadOnly())
							nodeAutorization.add(xpath);
    					idToXpath.put(nodeCount,xpath);
    		    		if(treeNode.isVisible()==true){
    		    			list.add(treeNode);    			
    		    			nodeCount++; 
    		    		}  
					}
				}
				else{
					if(!treeNode.isReadOnly())
						nodeAutorization.add(xpath);
					idToXpath.put(nodeCount,xpath);
					treeNode.setValue(StringEscapeUtils.escapeHtml(Util.getFirstTextNode(d,xpath)));
//					setForeignKeyValueInfoToTreeNode(xpath, treeNode, d, Util.getFirstTextNode(d,xpath));	
		    		if(treeNode.isVisible()==true){
		    			list.add(treeNode);    			
		    			nodeCount++; 
		    		}  
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(maskKey&&treeNode.isKey()){
			String oldPath=treeNode.getValue();
			treeNode.setValue("");
			if(treeNode.getTypeName().trim().toUpperCase().equals("UUID")||treeNode.getTypeName().trim().toUpperCase().equals("AUTO_INCREMENT")){
				treeNode.setReadOnly(true);
			}else{
				treeNode.setReadOnly(false);
			}
			
			
			HashMap<String,UpdateReportItem> updatedPath;
			if(ctx.getSession().getAttribute("updatedPath")!=null){
				updatedPath = (HashMap<String,UpdateReportItem>) ctx.getSession().getAttribute("updatedPath");
			}				
			else{
				updatedPath = new HashMap<String,UpdateReportItem>();
			}
			ctx.getSession().setAttribute("updatedPath",updatedPath);
			updatedPath.put(xpath, new UpdateReportItem(xpath,oldPath,""));
			
		}
		
	}	
	private int nodeCount; //aiming added to record the node count;
	/**
	 * give the children of a node
	 * @param id the id of the node in yui
	 * @param nodeCount the internal count of nodes in yui tree
	 * @param language
	 * @return an array of TreeNode
	 */
//	TreeNode parentNode,
	public TreeNode[] getChildren( int id, int nodeCount, String language, boolean foreignKey, int docIndex){
		return getChildrenWithKeyMask(id, nodeCount, language, foreignKey, docIndex, false);
	}
		
	public TreeNode[] getChildrenWithKeyMask(int id, int nodeCount, String language, boolean foreignKey, int docIndex, boolean maskKey){
		WebContext ctx = WebContextFactory.get();	
		HashMap<Integer,XSParticle> idToParticle = 
			(HashMap<Integer,XSParticle>) ctx.getSession().getAttribute("idToParticle");
		HashMap<Integer,String> idToXpath = 
			(HashMap<Integer,String>) ctx.getSession().getAttribute("idToXpath");
		HashMap<String,XSParticle> xpathToParticle = 
			(HashMap<String,XSParticle>) ctx.getSession().getAttribute("xpathToParticle");
		ArrayList<String> nodeAutorization = 
			(ArrayList<String>) ctx.getSession().getAttribute("nodeAutorization");
		Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);
		String[] keys = (String[]) ctx.getSession().getAttribute("foreignKeys");
		
		HashMap<String,TreeNode> xpathToTreeNode = 
			(HashMap<String,TreeNode>)ctx.getSession().getAttribute("xpathToTreeNode");
		
		if(xpathToTreeNode==null)
			xpathToTreeNode = new HashMap<String, TreeNode>();
		
		if(foreignKey) d = (Document) ctx.getSession().getAttribute("itemDocumentFK");
		
		boolean choice = false;
		ArrayList<String> roles = new ArrayList<String>();
		try {
			roles = Util.getAjaxSubject().getRoles();
		} catch (PolicyContextException e1) {
			e1.printStackTrace();
		}
	
		XSParticle[] xsp = null;
		if(idToParticle==null) return null;
		if(idToParticle.get(id)==null){//simple type case, no children
			return null;
		}
		this.nodeCount=nodeCount;//aiming added	
		xsp = idToParticle.get(id).getTerm().asModelGroup().getChildren();
		if("choice".equals(idToParticle.get(id).getTerm().asModelGroup().getCompositor().toString()))
			choice = true;

		ArrayList<TreeNode> list = new ArrayList<TreeNode>();
		//iterate over children
    	for (int j = 0; j < xsp.length; j++) {
    		setChildrenWithKeyMask(id,language,foreignKey,docIndex,maskKey,choice,xsp[j],list,xpathToTreeNode);
		}		
    	if(xpathToTreeNode!=null){
    		ctx.getSession().setAttribute("xpathToTreeNode", xpathToTreeNode);
    	}
		return list.toArray(new TreeNode[list.size()]); 
	}
	
	private void clearChildrenValue(Node node){
		if(node.getFirstChild()!=null && node.getFirstChild().getNodeType()==Node.TEXT_NODE){
			node.getFirstChild().setNodeValue("");
		}
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			clearChildrenValue(list.item(i));
		}
	}
	
	public String cloneNode(int siblingId, int newId, int docIndex) throws Exception{
		
			WebContext ctx = WebContextFactory.get();
		HashMap<Integer,XSParticle> idToParticle = 
			(HashMap<Integer,XSParticle>) ctx.getSession().getAttribute("idToParticle");
		HashMap<Integer,String> idToXpath = 
			(HashMap<Integer,String>) ctx.getSession().getAttribute("idToXpath");
		ArrayList<String> nodeAutorization = 
			(ArrayList<String>) ctx.getSession().getAttribute("nodeAutorization");
		XSParticle xsp = idToParticle.get(siblingId);
		// associate the new id node to the particle of his sibling
		idToParticle.put(newId,xsp);
		Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);		
		try {
			
			Node node = Util.getNodeList(d,idToXpath.get(siblingId)).item(0);
			//System.out.println(Util.getNodeList(d,idToXpath.get(siblingId)).getLength()+" "+idToXpath.get(siblingId));
			Node nodeClone = node.cloneNode(true);
			clearChildrenValue(nodeClone);
			// simulate an "insertAfter()" which actually doesn't exist
			insertAfter(nodeClone,node);
/*			if(node.getNextSibling()!=null)
				node.getParentNode().insertBefore(nodeClone,node.getNextSibling());
			else
				node.getParentNode().appendChild(nodeClone);
*/			
			String siblingXpath = idToXpath.get(siblingId).replaceAll("\\[\\d+\\]$","");
			int id = Util.getNodeList(d,siblingXpath).getLength();
			//String exist = idToXpath.get(newId);
			idToXpath.put(newId,siblingXpath+"["+id+"]");
			//System.out.println("clone:"+newId+" "+siblingXpath+"["+id+"]");
			nodeAutorization.add(siblingXpath+"["+id+"]");
			return "Cloned";
			
		} catch (Exception e) {
			e.printStackTrace();	
			return "Error";
		}
//		System.out.println("xml"+CommonDWR.getXMLStringFromDocument(d));
	
	}
	
	public void updateKeyNodesToEmptyInItemDocument(int docIndex) throws TransformerException{
		WebContext ctx = WebContextFactory.get();
		String[] keys = (String[]) ctx.getSession().getAttribute("foreignKeys");
		for (int i = 0; i < keys.length; i++) {
			try {
				Document d = (Document) ctx.getSession().getAttribute("itemDocument" + docIndex);
				//Document d2 = checkNode(keys[i], d);
				String oldValue = Util.getFirstTextNode(d, keys[i]);
				if (oldValue == null)
					Util.getNodeList(d, keys[i]).item(0).appendChild(
							d.createTextNode(""));
				else
					Util.getNodeList(d, keys[i]).item(0).getFirstChild()
							.setNodeValue("");
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
	}
	

	public String updateNode(int id, String content, int docIndex) throws TransformerException{
		WebContext ctx = WebContextFactory.get();
		HashMap<Integer,String> idToXpath = 
			(HashMap<Integer,String>) ctx.getSession().getAttribute("idToXpath");
		String xpath = idToXpath.get(id);
		return updateNode2(xpath,StringEscapeUtils.unescapeHtml(content),docIndex);
	}
	
	public static String updateNode2(String xpath, String content, int docIndex) throws TransformerException{
		WebContext ctx = WebContextFactory.get();
		Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);
		HashMap<Integer,String> idToXpath = 
			(HashMap<Integer,String>) ctx.getSession().getAttribute("idToXpath");
		ArrayList<String> nodeAutorization = 
			(ArrayList<String>) ctx.getSession().getAttribute("nodeAutorization");
		/*for (Iterator iter = nodeAutorization.iterator(); iter.hasNext();) {
			String element = (String) iter.next();
			System.out.println("autorisation "+element);
		}*/	

		//TODO
		
		int i = xpath.lastIndexOf("/");
		String subXpath = xpath.substring(0, i);
		if(!nodeAutorization.contains(xpath) 
				&& !nodeAutorization.contains(xpath.replaceAll("\\[.*\\]",""))&&!nodeAutorization.contains(subXpath)){
			return "Not authorized";
		}
		try {			
			Document d2 = checkNode(xpath, d);
			String oldValue = Util.getFirstTextNode(d,xpath);
			if(content.equals(oldValue))
				return "Nothing to update";
			//Util.getNodeList(d, xpath).item(0).setTextContent(content);
			if(oldValue==null)
				Util.getNodeList(d, xpath).item(0).appendChild(d.createTextNode(content));
			else
				Util.getNodeList(d, xpath).item(0).getFirstChild().setNodeValue(content);
			//TODO add path to session
			HashMap<String,UpdateReportItem> updatedPath;
			if(ctx.getSession().getAttribute("updatedPath")!=null){
				updatedPath = (HashMap<String,UpdateReportItem>) ctx.getSession().getAttribute("updatedPath");
			}				
			else{
				updatedPath = new HashMap<String,UpdateReportItem>();
			}			
			if(updatedPath.get(xpath)!=null) {
				oldValue = updatedPath.get(xpath).getOldValue();
			}
			UpdateReportItem item = new UpdateReportItem(xpath,oldValue,content);
			updatedPath.put(xpath,item);
			ctx.getSession().setAttribute("updatedPath",updatedPath);
			return "Node updated";
		} 
		catch (Exception e2) {
				e2.printStackTrace();
				return "Error";
		}
	}
	
	private static Document checkNode(String xpath, Document d) throws Exception{
		// try each element of the xpath and check if it exists in datamodel
		if(xpath.charAt(0)=='/') {
			xpath = xpath.substring(1);
		}
		String[] elements = xpath.split("/");
		String xpathParent = "/";
		for (int i = 0; i < elements.length; i++) {				
			if(CommonDWR.getNodeList(d, xpathParent+"/"+elements[i]).getLength()==0){
				d = createNode(xpathParent, elements[i], d);
			}
			if(i==0) xpathParent = "/"+elements[i];
			else xpathParent += "/"+elements[i];
		}
		return d;
	}
	
	private static Document createNode(String xpathParent, String nodeToBeCreated, Document d) throws Exception {
		WebContext ctx = WebContextFactory.get();
		HashMap xpathToParticle = 
			(HashMap) ctx.getSession().getAttribute("xpathToParticle");
		
		Element el = d.createElement(nodeToBeCreated);
		XSParticle[] xsp = null;
		
		xsp = ((XSParticle) xpathToParticle.get(xpathParent)).getTerm().asModelGroup().getChildren();

		String elementAfter ="";
		for (int i = 0; i < xsp.length; i++) {
			String element = xsp[i].getTerm().asElementDecl().getName();
			if(nodeToBeCreated.equals(element)){
				//System.out.println("found");		
				if(i==xsp.length-1){
					//System.out.println("case append child 1");
					Node parent = Util.getNodeList(d,xpathParent).item(0);
					parent.appendChild(el);
					return d;
				}
				for (int j = 0; j < xsp.length-i-1; j++) {
					elementAfter = xpathParent+"/"+xsp[i+j+1].getTerm().asElementDecl().getName();
					//System.out.println("element after : "+elementAfter);
					Node node = Util.getNodeList(d,elementAfter).item(0);
					if(node!=null){
						node.getParentNode().insertBefore(el,node);
						return d;
					}	
				}
				
				
				//TODO
				{
					System.out.println("case append child 2");
					Node parent = Util.getNodeList(d,xpathParent).item(0);
					parent.appendChild(el);
				}
			}
		}
		return d;
	}
	/**
	 * add by ymli
	 * if ListItem with xpath like '/PurchaseOrder/ListItems/POItem[i] is deleted, the xpath in idToXpath will be edited , 
	 * eg.
	 *  '/PurchaseOrder/ListItems/POItem[j]'(j>i), j--
	 * 
	 */
	public void editXpathInidToXpath(int id ,HashMap<Integer,String> idToXpath){
		String nodeXpath = idToXpath.get(id).replaceAll("\\[\\d+\\]$","");
		String patternXpath = nodeXpath.replaceAll("\\[", "\\\\[");
		patternXpath = patternXpath.replaceAll("\\]", "\\\\]");;
		Pattern p = Pattern.compile("(.*?)(\\[)(\\d+)(\\]$)");
		Matcher m = p.matcher(idToXpath.get(id));
		int nodeIndex = -1;
		if (m.matches()) 
			nodeIndex =  Integer.parseInt(m.group(3));
		Iterator<Integer> keys = idToXpath.keySet().iterator();
		while(keys.hasNext()){
			int key = keys.next();
			String xpath = idToXpath.get(key);
			
			if(xpath.matches(patternXpath+"\\[\\d+\\]$")){
				int pathIndex = -1;
				Matcher m1 = p.matcher(xpath);
				if (m1.matches()) 
					pathIndex =  Integer.parseInt(m1.group(3));
				if(nodeIndex<pathIndex){
					pathIndex--;
					xpath = nodeXpath+"["+pathIndex+"]";
//					keys.remove();
//					idToXpath.remove(key);
					idToXpath.put(key, xpath);
					
				}//if(nodeIndex
				
			}//if(xpath
		}//	while(keys.
	}
	
	/**
	 * add by ymli
	 * if ListItem with xpath like '/PurchaseOrder/ListItems/POItem[i] is deleted, the xpath in idToXpath will be edited , 
	 * eg.
	 *  '/PurchaseOrder/ListItems/POItem[j]'(j>i), j--
	 * 
	 */
	public void editUpdatedPath(HashMap<String,UpdateReportItem>updatedPath,String xpath){
		String subXpath = xpath.replaceAll("\\[\\d+\\]$", "");
		
		int b = xpath.indexOf("[")+1;
		int e = xpath.indexOf("]");
		int nodeIndex = Integer.parseInt((String) xpath.subSequence(b, e));
		Iterator<String> keys = updatedPath.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			if(key.matches(subXpath+"\\[\\d+\\]$")){
				int star = key.indexOf("[")+1;
				int end = key.indexOf("]");
				int pathIndex = Integer.parseInt((String) key.subSequence(star, end));
				if(nodeIndex<pathIndex){
					UpdateReportItem report = updatedPath.get(key);
					keys.remove();
					updatedPath.remove(key);
					pathIndex--;
					xpath = subXpath+"["+pathIndex+"]";
					updatedPath.put(xpath, report);
				}
			}
		}
	}
	public String removeNode(int id, int docIndex, String oldValue){ 
		WebContext ctx = WebContextFactory.get();
		HashMap<Integer,String> idToXpath = 
			(HashMap<Integer,String>) ctx.getSession().getAttribute("idToXpath");
		Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);
		/*try {
			System.out.println("Document:"+Util.nodeToString(d));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		try{
			
//			System.out.println("remove:"+id+" "+idToXpath.get(id));
			Util.getNodeList(d, idToXpath.get(id)).item(0).getParentNode()
				.removeChild(Util.getNodeList(d, idToXpath.get(id)).item(0));
			//add by ymli
			HashMap<String,UpdateReportItem> updatedPath;
			if(ctx.getSession().getAttribute("updatedPath")!=null){
				updatedPath = (HashMap<String,UpdateReportItem>) ctx.getSession().getAttribute("updatedPath");
			}				
			else{
				updatedPath = new HashMap<String,UpdateReportItem>();
			}
			UpdateReportItem ri = new UpdateReportItem(idToXpath.get(id),oldValue,"");
			
			//editUpdatedPath(updatedPath,idToXpath.get(id));
			editXpathInidToXpath(id,idToXpath);
			
			updatedPath.put(idToXpath.get(id),ri);
			idToXpath.remove(id);
			ctx.getSession().setAttribute("idToXpath",idToXpath);
			ctx.getSession().setAttribute("updatedPath",updatedPath);
			return "Deleted";
		}
		catch(Exception e){
			e.printStackTrace();
			return "Error";
		}	
	}
	
	//back up for old revision of save item
//	public static String saveItem(String[] ids, String concept, boolean newItem, int docIndex) throws Exception{
//		WebContext ctx = WebContextFactory.get();		
//		try {		
//			Configuration config = Configuration.getInstance();
//			String dataModelPK = config.getModel();
//			String dataClusterPK = config.getCluster();
//			Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);
//			String xml = CommonDWR.getXMLStringFromDocument(d);
//			xml = xml.replaceAll("<\\?xml.*?\\?>","");	
//			//<?xml version="1.0" encoding="UTF-8"?>
//			org.apache.log4j.Logger.getLogger(ItemsBrowserDWR.class).debug("saveItem() "+xml);
//			WSItemPK wsi = Util.getPort().putItem(
//					new WSPutItem(
//							new WSDataClusterPK(dataClusterPK), 
//							xml,
//							new WSDataModelPK(dataModelPK)));	
//			ctx.getSession().setAttribute("viewNameItems",null);
//			String operationType = "";
//			if(newItem==true) operationType = "CREATE";
//			else operationType = "UPDATE";
//			String result = pushUpdateReport(wsi.getIds(),concept,operationType);		
//			return result;
//		}
//		catch(Exception e){			
//			String err= "Unable to save item '"+concept+"."+Util.joinStrings(ids, ".")+"'";
//			org.apache.log4j.Logger.getLogger(ItemsBrowserDWR.class).error(err,e);
//			throw new Exception(e.getLocalizedMessage());
//		}		
//
//	}
	
	public static String saveItem(String[] ids, String concept, boolean newItem, int docIndex) throws Exception{
		WebContext ctx = WebContextFactory.get();		
		try {		
			Configuration config = Configuration.getInstance();
			String dataModelPK = config.getModel();
			String dataClusterPK = config.getCluster();
			Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);
			String xml = CommonDWR.getXMLStringFromDocument(d);
			xml = xml.replaceAll("<\\?xml.*?\\?>","");	
			//<?xml version="1.0" encoding="UTF-8"?>
			org.apache.log4j.Logger.getLogger(ItemsBrowserDWR.class).debug("saveItem() "+xml);
			
			ctx.getSession().setAttribute("viewNameItems",null);
			String operationType = "";
			if(newItem==true) operationType = "CREATE";
			else operationType = "UPDATE";
			
			//check updatedPath
			HashMap<String,UpdateReportItem> updatedPath = new HashMap<String,UpdateReportItem>();
			updatedPath = (HashMap<String,UpdateReportItem>) ctx.getSession().getAttribute("updatedPath");
			if(!"DELETE".equals(operationType) && updatedPath==null){
				return "ERROR_2";
			}
			//create updateReport
			String resultUpdateReport = createUpdateReport(ids, concept, operationType, updatedPath);
			
			//check before saving transformer
			boolean isBeforeSavingTransformerExist=false;
			WSTransformerPK[] wst = Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK();
			for (int i = 0; i < wst.length; i++) {
				if(wst[i].getPk().equals("beforeSaving_"+concept)){
					isBeforeSavingTransformerExist=true;
					break;
				}
			}
			//call before saving transformer
			if(isBeforeSavingTransformerExist){
				
				try {
					WSTransformerContext wsTransformerContext = new WSTransformerContext(
							new WSTransformerV2PK("beforeSaving_" + concept),
							null, null);
					String exchangeData = mergeExchangeData(xml,resultUpdateReport);
					//String exchangeData = resultUpdateReport;
					WSTypedContent wsTypedContent = new WSTypedContent(null,
							new WSByteArray(exchangeData
									.getBytes("UTF-8")),
							"text/xml; charset=utf-8");
					WSExecuteTransformerV2 wsExecuteTransformerV2 = new WSExecuteTransformerV2(
							wsTransformerContext, wsTypedContent);
					//TODO process no plug-in issue
					WSTransformerContextPipelinePipelineItem[] entries = Util
							.getPort().executeTransformerV2(
									wsExecuteTransformerV2).getPipeline()
							.getPipelineItem();
					String outputErrorMessage = "";
					//Scan the entries - in priority, taka the content of the 'output_error_message' entry, 
					for (int i = 0; i < entries.length; i++) {

						if ("output_error_message".equals(entries[i]
								.getVariable())) {
							outputErrorMessage = new String(entries[i]
									.getWsTypedContent().getWsBytes()
									.getBytes(), "UTF-8");
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
			//put item
			boolean isUpdateThisItem=true;
			if(newItem==true) isUpdateThisItem = false;

			WSItemPK wsi = Util.getPort().putItem(
					new WSPutItem(
							new WSDataClusterPK(dataClusterPK), 
							xml,
							new WSDataModelPK(dataModelPK),isUpdateThisItem));
			//update update report key
			resultUpdateReport=resultUpdateReport.replaceFirst("<Key>.*</Key>", "<Key>"+Util.joinStrings(wsi.getIds(),".")+"</Key>"); 
			//put update report
			synchronizeUpdateState(ctx);
					

			return persistentUpdateReport(resultUpdateReport,true);
		}
		catch(Exception e){			
			String err= "Unable to save item '"+concept+"."+Util.joinStrings(ids, ".")+"'";
			org.apache.log4j.Logger.getLogger(ItemsBrowserDWR.class).error(err,e);
			throw e;
		}		

	}


	private static String mergeExchangeData(String xml,
			String resultUpdateReport) {
		String exchangeData="<exchange>\n";
		exchangeData+="<report>"+resultUpdateReport+"</report>";
		exchangeData+="\n";
		exchangeData+="<item>"+xml+"</item>";
		exchangeData+="\n</exchange>";
		return exchangeData;
	}
	
	
	public String deleteItem(String concept, String[] ids) {
		WebContext ctx = WebContextFactory.get();
		try {
			Configuration config = Configuration.getInstance();
			String dataClusterPK = config.getCluster();
			
			if(com.amalto.core.util.Util.beforeDeleting(dataClusterPK,concept,ids)){
				return "OK - But go through the beforeDeleting transformer first";
			}
			
			TreeNode rootNode = getRootNode(concept, "en");
	        if(ids!=null && !rootNode.isReadOnly()){
				WSItemPK wsItem = Util.getPort().deleteItem(
						new WSDeleteItem(new WSItemPK(
								new WSDataClusterPK(dataClusterPK),
								concept, ids
								)));
				if(wsItem!=null)
					pushUpdateReport(ids,concept, "DELETE");
				else
					return "ERROR - delteItem is NULL";
				ctx.getSession().setAttribute("viewNameItems",null);
				return "OK";
	        }
	        else {
	        	return "OK - But no update report";
	        }
		}
		catch(Exception e){
			return "ERROR -" + e.getLocalizedMessage();
		}       
	}
	
	public String[] getUriArray(String concept, String[] ids){
		Configuration config;
		List<String> uriList=new ArrayList<String>();
		try {
			config = Configuration.getInstance();
		String dataClusterPK = config.getCluster();
		String content="";
		WSItemPK wsItem=new WSItemPK(new WSDataClusterPK(dataClusterPK),concept,ids);
    	if(wsItem!=null)
    		content=Util.getPort().getItem(new WSGetItem(wsItem))
					.getContent();
   	 for (Iterator iterator = parsXMLString(content).getRootElement().nodeIterator(); iterator.hasNext();) {
   		org.jboss.dom4j.Node node = ( org.jboss.dom4j.Node) iterator.next();
			if(node.getStringValue().startsWith("/imageserver"))
				{	uriList.add(node.getStringValue());
 				}
		}
    	System.out.println(uriList.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] uriArray=new String[uriList.size()];
		for (int i = 0; i < uriList.size(); i++) {
			uriArray[i]=uriList.get(i);
		}
		return uriArray;
		}

	public String logicalDeleteItem(String concept, String[] ids, String path)
	{
		WebContext ctx = WebContextFactory.get();
		try {
			Configuration config = Configuration.getInstance();
			String dataClusterPK = config.getCluster();
			TreeNode rootNode = getRootNode(concept, "en");
	        if(ids!=null && !rootNode.isReadOnly()){
	        	WSDroppedItemPK wsItem = Util.getPort().dropItem(
						new WSDropItem(new WSItemPK(
								new WSDataClusterPK(dataClusterPK),
								concept, ids
								), path));
				if(wsItem!=null)
					pushUpdateReport(ids,concept, "DELETE");
				else
					return "ERROR - dropItem is NULL";
				ctx.getSession().setAttribute("viewNameItems",null);
				return "OK";
	        }
	        else {
	        	return "OK - But no update report";
	        }
		}
		catch (Exception e) {
	    	return "ERROR -" + e.getLocalizedMessage();
	    }    	
	}
	/**
	 * create an "empty" item from scratch, set every text node to empty
	 * @param viewPK
	 * @throws RemoteException
	 * @throws Exception
	 */
	private void createItem(String concept, int docIndex) throws RemoteException, Exception{
		WebContext ctx = WebContextFactory.get(); 
		Configuration config = Configuration.getInstance(); 
		String xml1 = "<"+concept+"></"+concept+">";
		Document d = Util.parse(xml1);				
		ctx.getSession().setAttribute("itemDocument"+docIndex,d);
		Map<String,XSElementDecl> map = CommonDWR.getConceptMap(config.getModel());
    	XSComplexType xsct = (XSComplexType)(map.get(concept).getType());
    	XSParticle[] xsp = xsct.getContentType().asParticle().getTerm().asModelGroup().getChildren();
    	for (int j = 0; j < xsp.length; j++) {  		
    		//why don't set up children element? FIXME
    		
    		setChilden(xsp[j], "/"+concept, docIndex);
    	}
	}

	
	private void setChilden(XSParticle xsp, String xpathParent, int docIndex) throws Exception{
		//aiming added see 0009563
		if(xsp.getTerm().asModelGroup()!=null){ //is complex type
			XSParticle[] xsps=xsp.getTerm().asModelGroup().getChildren();			
			for (int i = 0; i < xsps.length; i++) {
				setChilden(xsps[i],xpathParent, docIndex);
			}
		}
		if(xsp.getTerm().asElementDecl()==null) return;
		//end
		
		WebContext ctx = WebContextFactory.get();
		Document d = (Document) ctx.getSession().getAttribute("itemDocument"+docIndex);
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
					setChilden(xsps[i],xpathParent, docIndex);
				}
			}
		}		
	}
	
	
	public String countForeignKey(String xpathForeignKey) throws Exception{
		Configuration config = Configuration.getInstance();
		String conceptName = Util.getConceptFromPath(xpathForeignKey);
		return Util.getPort().count(
			new WSCount(
				new WSDataClusterPK(config.getCluster()),
				conceptName,
				null,
				-1
			)
		).getValue();
	}
	/**
	 * lym
	 */
	public String countForeignKey_filter(String xpathForeignKey) throws Exception{
		Configuration config = Configuration.getInstance();
		String conceptName = Util.getConceptFromPath(xpathForeignKey);
		
		WSWhereCondition whereCondition=Util.getConditionFromPath(xpathForeignKey);
		WSWhereItem whereItem=null;
		if(whereCondition!=null){
			whereItem= new WSWhereItem (whereCondition,null,null);
		}
		
		return Util.getPort().count(
			new WSCount(
				new WSDataClusterPK(config.getCluster()),
				conceptName,
				whereItem,//null,
				-1
			)
		).getValue();
	}
	

//	public TreeMap<String,String> getForeignKeyList(String xpathForeignKey, String xpathInfoForeignKey, String value) throws RemoteException, Exception{
		
	public String getForeignKeyList(int start, int limit, String value, String xpathForeignKey, String xpathInfoForeignKey) throws RemoteException, Exception{
		String initxpathForeignKey="";
		initxpathForeignKey = Util.getForeignPathFromPath(xpathForeignKey);
		
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("getForeignKeyList() xPath FK: '"+initxpathForeignKey+"' xPath FK Info: '"+xpathInfoForeignKey+"' value: '"+value+"'");
				
		WSWhereCondition whereCondition=Util.getConditionFromPath(xpathForeignKey);
		WSWhereItem whereItem=null;
		if(whereCondition!=null){
			whereItem= new WSWhereItem (whereCondition,null,null);
		}
		
		
		Configuration config = Configuration.getInstance();
		//aiming modify there is bug when initxpathForeignKey when it's like 'conceptname/key'
		//so we convert initxpathForeignKey to 'conceptname'
		initxpathForeignKey=initxpathForeignKey.split("/")[0];
		//end
		
		// foreign key set by business concept
		if(initxpathForeignKey.split("/").length == 1){
			String conceptName = initxpathForeignKey;

			//determine if we have xPath Infos: e.g. labels to display
			String[] xpathInfos = new String[1];
			if(!"".equals(xpathInfoForeignKey))	
				xpathInfos = xpathInfoForeignKey.split(",");
			else
				xpathInfos[0] = conceptName;
			//aiming add .* to value
			value=value==null?"":value;
			value=value+".*";
			//end
			// build query - add a content condition on the pivot if we search for a particular value
			String filteredConcept = conceptName;
			if(value!=null && !"".equals(value.trim())){
				//filteredConcept+="[(descendant-or-self::* &= \""+value+"*\"),(descendant-or-self::*/attribute() &= \""+value+"*\")]";
				//Value is unlikely to be in attributes
				filteredConcept+="[matches(descendant-or-self::* , \""+value+"\", \"i\")]";
			}
			
			//add the xPath Infos Path
			ArrayList<String> xPaths = new ArrayList<String>();
			for (int i = 0; i < xpathInfos.length; i++) {
				xPaths.add(xpathInfos[i].replaceFirst(conceptName, filteredConcept));
			}
			//add the key paths last, since there may be multiple keys
			xPaths.add(filteredConcept+"/../../i");
			
			//Run the query
			String [] results = Util.getPort().xPathsSearch(new WSXPathsSearch(
				new WSDataClusterPK(config.getCluster()),
				filteredConcept,
				new WSStringArray(xPaths.toArray(new String[xPaths.size()])),
				whereItem,
				-1,
				start,
				limit,
				null,
				null
			)).getStrings();
			
			if (results == null) results = new String[0];
			
			JSONObject json = new JSONObject();
			//json.put("count", results.length);
			
			JSONArray rows = new JSONArray();
			json.put("rows", rows);

			//add (?i) to incasesensitive
			//parse the results - each result contains the xPathInfo values, followed by the keys
			for (int i = 0; i < results.length; i++) {
				//process no infos case
				if(!results[i].startsWith("<result>")){
					results[i]="<result>"+results[i]+"</result>";
				}
				results[i]=results[i].replaceAll("\\n", "");//replace \n
				results[i]=results[i].replaceAll(">(\\s+)<", "><"); //replace spaces between elements
				Element root = Util.parse(results[i]).getDocumentElement();
				NodeList list = root.getChildNodes();

				//recover keys - which are last
				String keys = "";
				for (int j = "".equals(xpathInfoForeignKey)?1:xpathInfos.length; j<list.getLength(); j++) {
					Node textNode = list.item(j).getFirstChild();		
					keys += "["+(textNode == null ? "" : textNode.getNodeValue())+"]";
				}
				
				//recover xPathInfos
				String infos = null;
				
				//if no xPath Infos given, use the key values
				if (xpathInfos.length == 0||"".equals(xpathInfoForeignKey)) {
					infos = keys;
				} else {
					//build a dash separated string of xPath Infos
    				for (int j = 0; j < xpathInfos.length; j++) {
    					infos = (infos == null ? "" : infos+"-");
    					Node textNode = list.item(j).getFirstChild();
    					infos  += textNode == null ? "" : textNode.getNodeValue();
    				}
				}
				
				if((keys.equals("[]")||keys.equals(""))&&(infos.equals("")||infos.equals("[]"))){
					//empty row
				}else{
					JSONObject row = new JSONObject();		
					//add by ymli. retrieve the correct results according value. fig bug:0010481					
					if(keys.matches("(?i)"+value)||infos.matches("(?i)"+value)||keys.indexOf("["+value)!=-1||infos.indexOf(value)!=-1){
						row.put("keys", keys);
						row.put("infos", infos);
						rows.put(row);
					}
				}
				
				json.put("count", rows.length());
				//update the map
//				map.put(StringEscapeUtils.escapeXml(keys), infos);
			}
//			return map;
			return json.toString();
		}
		
		throw new Exception("this should not happen");
//		//This is the "classic" case where not a Concept name but a full Path to a key is given
//		String[] xpaths = null;
//		
//		//if we have xPaths Infos data, add them to the xPath Query to pull them
//		if(!"".equals(xpathInfoForeignKey)){
//			String[] xpathInfos = xpathInfoForeignKey.split(",");			
//			xpaths = new String[xpathInfos.length+1];
//			xpaths[0] = initxpathForeignKey;
//			System.arraycopy(xpathInfos, 0, xpaths, 1, xpathInfos.length);
//		}else {
//			xpaths = new String[1];
//			xpaths[0] = initxpathForeignKey;
//		}
//		
//		//filter with value 
//		for (int i = 0; i < xpaths.length; i++) {
//			String xpath=xpaths[i];
//			
//			int pos=xpath.indexOf("/");
//			String conceptName=xpath.substring(0,pos);
//			String left=xpath.substring(pos);
//			
//			String filteredConcept=conceptName;
//			filteredConcept+="[matches(descendant-or-self::* , \""+value+"\")]";
//			xpath=filteredConcept+left;
//			
//			xpaths[i]=xpath;
//		}
//		 
//		//run the search
//		String[] results = Util.getPort().xPathsSearch(
//				new WSXPathsSearch(
//					new WSDataClusterPK(config.getCluster()),
//					xpaths[0],//pivot
//					new WSStringArray(xpaths),
//					whereItem,
//					-1,		//spell Threshold
//					0,		//start
//					Integer.MAX_VALUE,
//					null, 	//order By
//					null	//direction
//					)
//				).getStrings();
//		
//		if (results == null) results = new String[0];
//		
//		JSONObject json = new JSONObject();
//		json.put("count", results.length);
//		
//		JSONArray rows = new JSONArray();
//		json.put("rows", rows);
//		
//		//parse the results to put them in the map
//		for (int i = 0; i < results.length; i++) {
//			if(results[i]!=null){
//				Document d = Util.parse(results[i]);
//				String tmp = "";
//				for (int j = 0; j < xpaths.length; j++) {
//					tmp += " - "+Util.getFirstTextNode(d,"//"+xpaths[j].split("/")[xpaths[j].split("/").length-1]);
//				}
//				if(Util.getFirstTextNode(d,"//"+initxpathForeignKey.split("/")[initxpathForeignKey.split("/").length-1])!=null) {
//					String keys = Util.getFirstTextNode(d,"//"+initxpathForeignKey.split("/")[initxpathForeignKey.split("/").length-1]);
//					String infos = tmp.substring(3);
//					JSONObject row = new JSONObject();
//					row.put("keys", keys);
//					row.put("infos", infos);
//					rows.put(row);
////					map.put(Util.getFirstTextNode(d,"//"+xpathForeignKey.split("/")[xpathForeignKey.split("/").length-1]),tmp.substring(3));	
//				}
//			}		
//		}
//	
//		return json.toString();
//		return map;
	}
	
	
	private static String pushUpdateReport(String[] ids, String concept, String operationType)throws Exception{
		org.apache.log4j.Logger.getLogger(ItemsBrowserDWR.class).trace("pushUpdateReport() concept "+concept+" operation "+operationType);

		//check updatedPath
		WebContext ctx = WebContextFactory.get();
		HashMap<String,UpdateReportItem> updatedPath = new HashMap<String,UpdateReportItem>();
		updatedPath = (HashMap<String,UpdateReportItem>) ctx.getSession().getAttribute("updatedPath");
		if(!"DELETE".equals(operationType) && updatedPath==null){
			return "ERROR_2";
		}
		
		String xml2 = createUpdateReport(ids, concept, operationType,updatedPath);

		synchronizeUpdateState(ctx);
		return persistentUpdateReport(xml2,true);			
	}


	private static String persistentUpdateReport(String xml2,boolean routeAfterSaving) {
		try {
			WSItemPK itemPK = Util.getPort().putItem(
					new WSPutItem(
							new WSDataClusterPK("UpdateReport"), 
							xml2,
							new WSDataModelPK("UpdateReport"),false));
			org.apache.log4j.Logger.getLogger(ItemsBrowserDWR.class).debug(
					"pushUpdateReport() "+xml2);
			try {
				if(routeAfterSaving)Util.getPort().routeItemV2(new WSRouteItemV2(itemPK));
			} catch (RemoteException e) {
				//e.printStackTrace();
				org.apache.log4j.Logger.getLogger(ItemsBrowserDWR.class).warn("Can not route the item, maybe there is no Routing Rule defined for this item! ");
			}
			return "OK";
		} catch (RemoteException e) {			
			e.printStackTrace();
			return "ERROR";
		} catch (XtentisWebappException e) {
			e.printStackTrace();
			return "ERROR";
		}
	}


	private static void synchronizeUpdateState(WebContext ctx) {
		ctx.getSession().setAttribute("updatedPath",null);
		ctx.getSession().setAttribute("viewNameItems",null);
	}


	private static String createUpdateReport(String[] ids, String concept,
			String operationType, HashMap<String, UpdateReportItem> updatedPath)
			throws Exception {
		String username="";
		String revisionId="";
		
		String dataModelPK ="";
		String dataClusterPK ="";
		try {
			
			Configuration config = Configuration.getInstance();
	    	dataModelPK = config.getModel()==null?"":config.getModel();
	    	dataClusterPK = config.getCluster()==null?"":config.getCluster();
	    	
			username=Util.getLoginUserName();
	    	String universename=Util.getLoginUniverse();
	    	if(universename!=null&&universename.length()>0)revisionId=Util.getRevisionIdFromUniverse(universename, concept);
	    	
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
			for (Iterator<UpdateReportItem> iter = list.iterator(); iter.hasNext();) {
				UpdateReportItem item = iter.next();
		            xml2 += 
		            "<Item>"+
		            "   <path>"+StringEscapeUtils.escapeXml(item.getPath())+"</path>"+
		            "   <oldValue>"+StringEscapeUtils.escapeXml(item.getOldValue())+"</oldValue>"+
		            "   <newValue>"+StringEscapeUtils.escapeXml(item.getNewValue())+"</newValue>"+
		           "</Item>"; 		
				}     
		}
        xml2 += "</Update>";
		return xml2;
	}
	
	private void insertAfter(Node newNode, Node node){
		if(node.getNextSibling()!=null)
			node.getParentNode().insertBefore(newNode,node.getNextSibling());
		else
			node.getParentNode().appendChild(newNode);
	}	
	
	public static boolean checkIfTransformerExists(String concept, String language) {
		try{
			WSTransformerPK[] wst = Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK();
			for (int i = 0; i < wst.length; i++) {
				if(wst[i].getPk().equals("Smart_view_"+concept+"_"+language.toUpperCase())){
					return true;
				}
			}
			return false;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public boolean checkIfDocumentExists(String[] ids, String concept) throws Exception{
		Configuration config = Configuration.getInstance();
		 return  Util.getPort().existsItem(
				 new WSExistsItem(new WSItemPK(new WSDataClusterPK(config.getCluster()),concept, ids))
				 ).is_true();
	}
	
	public int countItems(String criteria, String dataObjet) throws Exception{
		Configuration config = Configuration.getInstance();
		String[] criterias = criteria.split("[\\s]+OR[\\s]+");
		ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>(); 
		
		for (String cria: criterias)
		{
			ArrayList<WSWhereItem> condition=new ArrayList<WSWhereItem>(); 
			String[] subCriterias = cria.split("[\\s]+AND[\\s]+");
			for (String subCria: subCriterias)
			{
				if (subCria.startsWith("("))
				{
					subCria = subCria.substring(1);
				}
				if (subCria.endsWith(")"))
				{
					subCria = subCria.substring(0, subCria.length() -1);
				}
				
				WSWhereItem whereItem = countItem(subCria, dataObjet);
				condition.add(whereItem);
			}
			if (condition.size() > 0) {
				WSWhereAnd and = new WSWhereAnd(condition
						.toArray(new WSWhereItem[condition.size()]));
				WSWhereItem whand = new WSWhereItem(null,and,null);
				conditions.add(whand);
			}
		}
		WSWhereOr or = new WSWhereOr(conditions.toArray(new WSWhereItem[conditions.size()]));
		WSWhereItem wi = new WSWhereItem(null,null,or);

		//count items 
		int count = Integer.parseInt(Util.getPort().count(new WSCount(
				new WSDataClusterPK(config.getCluster()),
				dataObjet,
				wi,
				0
		)).getValue());
		
		WebContext ctx = WebContextFactory.get();
		ctx.getSession().setAttribute("totalCountItems", count);
		
		return count;
	}
	
	public WSWhereItem countItem(String criteria, String dataObjet) throws Exception{
		WSWhereItem wi;
		String[] filters = criteria.split(" ");
		String filterXpaths, filterOperators ,filterValues ;

		filterXpaths = filters[0];
		filterOperators = filters[1];
		if (filters.length <= 2)
		    filterValues = " ";
		else
			filterValues = filters[2];

		WSWhereCondition wc=new WSWhereCondition(
				filterXpaths,
				getOperator(filterOperators),
				filterValues,
				WSStringPredicate.NONE,
				false
				);
		//System.out.println("iterator :"+i+"field - getErrors- : " + fields[i] + " " + operator[i]);
		//System.out.println("Xpath field - getErrors- : " + giveXpath(fields[i]) + " - values : "+ regexs[i]);
		ArrayList<WSWhereItem> conditions=new ArrayList<WSWhereItem>();
		WSWhereItem item=new WSWhereItem(wc,null,null);
		conditions.add(item);
						
		if(conditions.size()==0) { 
			wi=null;
		} else {
			WSWhereAnd and=new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
			wi=new WSWhereItem(null,and,null);
		}
		
		
		return wi;
		
	}
	
    public boolean prepareSessionForItemDetails(String concept,String language) {
    	
    	try {
    		
    		
			WebContext ctx = WebContextFactory.get();
			Configuration config = Configuration.getInstance();
			String model = config.getModel();
			
			CommonDWR.getFieldsByDataModel(model, concept, language, true);
			
			WSConceptKey key = Util.getPort().getBusinessConceptKey(
					new WSGetBusinessConceptKey(
							new WSDataModelPK(model),
							concept));
			String[] keys = key.getFields();
			for (int i = 0; i < keys.length; i++) {
				if(".".equals(key.getSelector()))
					keys[i] = "/"+concept+"/"+keys[i];					
				else
					keys[i] = key.getSelector()+keys[i];
			}
			ctx.getSession().setAttribute("foreignKeys",key.getFields());
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
    	return true;

	}
	
    private void setForeignKeyValueInfoToTreeNode(String xpath, TreeNode treeNode, Document doc, String realKey) throws Exception
    {
		Element elem = getForeignKeyInfoFromXSD(xpath);
		if(elem != null)
		{
			if(Util.getNodeList(elem, "./xsd:annotation/xsd:appinfo[@source='X_ForeignKey']").getLength() > 0)
			{
				String forgKey = Util.getNodeList(elem, "./xsd:annotation/xsd:appinfo[@source='X_ForeignKey']").item(0).getTextContent();
				String infos = "";
				NodeList infoList = Util.getNodeList(elem, "./xsd:annotation/xsd:appinfo[@source='X_ForeignKeyInfo']");
				for (int infoID = 0; infoID < infoList.getLength(); infoID++)
				{
					infos +=infoList.item(infoID).getTextContent() + ",";
				}
				if(infos.endsWith(","))
				{
					infos = infos.substring(0, infos.length() -1);
				}
				String valueInfo = "";
				String jasonInfo = getForeignKeyList(0, 100, ".*",  forgKey, infos);
				JSONObject jason = new JSONObject(jasonInfo);
				JSONArray rows = (JSONArray)jason.get("rows");
				if(realKey != null)
				{
					for(int n = 0; n < rows.length(); n++)
					{
						JSONObject row = (JSONObject)rows.get(n);
						if(realKey.equals(row.get("keys")))
						{
							valueInfo +=row.get("keys")+ "--" + row.get("infos");
							break;
						}

					}
					treeNode.setValueInfo(valueInfo);
				}
			}

		}
    }
    
    private Element getForeignKeyInfoFromXSD(String xpath)
    {
    	Element findOne = null;
    	try {
    		Configuration config = Configuration.getInstance(true);
    		String xsd = Util.getPort().getDataModel(
            		new WSGetDataModel(new WSDataModelPK(config.getModel()))).getXsdSchema();
    		Document doc = Util.parse(xsd);
    		if(xpath.charAt(0) == '/')
    			xpath = xpath.substring(1);
    		String[] elems = xpath.split("/");
    		if(elems.length > 0)
    		{
    			findOne = lookUpForeignInfos(doc.getDocumentElement(), null,  elems[0]);
    			int current = 1;
    			while(findOne != null && current < elems.length)
    			{
    				findOne = lookUpForeignInfos(doc.getDocumentElement(), findOne, elems[current]);
    				current++;
    			}
    		}
    		
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
    	return findOne;
    }
    
    private Element lookUpForeignInfos(Element content, Element parent, String name)
    {
    	Element result = null;
    	try {
			NodeList list = Util.getNodeList(content, "./xsd:element[@name='" + name + "']");
			if(list.getLength() == 0 || parent != null)
			{
				if(parent != null)
				{
					String typeAttr = null;
					Node typeNode = parent.getAttributes().getNamedItem("type");
					if(typeNode != null)
					{
						list = Util.getNodeList(content, "./xsd:complexType[@name='" + typeNode.getNodeValue() + "']//xsd:element[@name='" + name + "']");
						if(list.getLength() > 0)
						{
							return (Element)list.item(0);
						}
						
					}
					else
					{
						list = Util.getNodeList(parent, "./xsd:complexType//xsd:element[@name='" + name + "']");
						if(list.getLength() > 0)
						{
							return (Element)list.item(0);
						}
					}
				}
				
				NodeList importList = null;
				for (int nm = 0; nm < 2; nm++)
				{
					if (nm == 0) {
						importList = Util.getNodeList(content, "//xsd:import");
					} else {
						importList = Util.getNodeList(content, "//xsd:include");
					}
		    		for (int i = 0; i < importList.getLength(); i++)
		    		{
		    			String location = importList.item(i).getAttributes().getNamedItem("schemaLocation").getNodeValue();
		    			Document subDoc = Util.parseImportedFile(location);
		    			result =  lookUpForeignInfos(subDoc.getDocumentElement(), null, name);
		    			if(result != null)
		    			{
		    				break;
		    			}
		    		}
				}
			}
			else if(parent == null)
			{
				return (Element)list.item(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return result;
    }
    
    
    private void parseMetaDataTypes(Document doc, String concept, HashMap<String, ArrayList<String>> metaDataTypes, String hierarchicalName) throws Exception
    {
    	String hierarchy = hierarchicalName != null && !hierarchicalName.equals("")? hierarchicalName + "/" : hierarchicalName;
		NodeList nodeList = Util.getNodeList(doc, "//xsd:element[@name='" + concept + "']");
		for(int i = 0; i < nodeList.getLength(); i++)
		{
			String foreignKey = null;
			NodeList elemList = null;
			Node node = nodeList.item(i);
			String typeName = null;
			if(node.getAttributes().getNamedItem("type") != null)
			{
				typeName = node.getAttributes().getNamedItem("type").getNodeValue();
			}
			if(typeName != null)
			{
				elemList = Util.getNodeList(doc, "//xsd:complexType[@name='" + typeName + "']//xsd:element");
			}
			else
			{
				elemList = Util.getNodeList(doc, "//xsd:element[@name='" + concept + "']/xsd:complexType//xsd:element");
			}
			

			for(int c = 0; c < elemList.getLength() ;c++)
			{
				Node elem = elemList.item(c);
				String type = "";
				if(elem.getAttributes().getNamedItem("type") != null)
				{
					type = elem.getAttributes().getNamedItem("type").getNodeValue();
				}
				String name = "";
				if(elem.getAttributes().getNamedItem("name") != null)
				{
					name = elem.getAttributes().getNamedItem("name").getNodeValue();
				}
				
				if(name.equals("") && type.equals("") )
				{
					String ref = elem.getAttributes().getNamedItem("ref").getNodeValue();
					Pattern refDoc = Pattern.compile("(.*?):(.*?)");
					Matcher match = refDoc.matcher(ref);
					if(match.matches())
					{
						String prefix = match.group(1);
						String refName = match.group(2);
						String refUrl = doc.getDocumentElement().getAttributes().getNamedItem("xmlns:" + prefix).getNodeValue();
						NodeList importList = Util.getNodeList(doc, "./xsd:import[@namespace='" + refUrl + "']");
						NodeList includeList = Util.getNodeList(doc, "./xsd:include");
						if(importList.getLength() > 0)
						{
							Node imp = importList.item(0);
							String schemaLocation = imp.getAttributes().getNamedItem("schemaLocation").getNodeValue();
							Document impDoc = Util.parseImportedFile(schemaLocation);
							parseMetaDataTypes(impDoc, refName, metaDataTypes, (hierarchy == null ? "" : hierarchy ) + refName);
						}
						else if(includeList.getLength() > 0)
						{
							for (int includeIdx = 0; includeIdx < includeList.getLength(); includeIdx++)
							{
								Node incud = includeList.item(0);
								String schemaLocation = incud.getAttributes().getNamedItem("schemaLocation").getNodeValue();
								Document incudDoc = Util.parseImportedFile(schemaLocation);
								parseMetaDataTypes(incudDoc, refName, metaDataTypes, (hierarchy == null ? "" : hierarchy ) + refName);
							}
						}
					}
				}
				if(Util.getNodeList(elem, "//xsd:element[@name='" + name + "']" + "/xsd:annotation/xsd:appinfo[@source='X_ForeignKey']").getLength() > 0)
				{
					foreignKey = Util.getNodeList(elem, "//xsd:element[@name='" + name + "']" + "/xsd:annotation/xsd:appinfo[@source='X_ForeignKey']").item(0).getTextContent();
					NodeList foreignKeyInfos = Util.getNodeList(elem, "//xsd:element[@name='" + name + "']" + "/xsd:annotation/xsd:appinfo[@source='X_ForeignKeyInfo']");
					String keyInfo = "";
					for (int x = 0; x < foreignKeyInfos.getLength(); x++)
					{
						keyInfo += foreignKeyInfos.item(x).getTextContent() + (x == foreignKeyInfos.getLength()-1 ? "" : ",");
					}
					if(foreignKey != null)
					{
						String jasonData = getForeignKeyList(0, 100, ".*",  foreignKey, keyInfo);
						getForeignKeyInfo(metaDataTypes, elem.getAttributes().getNamedItem("name").getNodeValue(), jasonData);
						continue;
					}

				}
				
				if(type.startsWith("xsd:"))
				{
					ArrayList<String> contents = new ArrayList<String>();
					contents.add(type);
					metaDataTypes.put((hierarchy == null ? "" : hierarchy ) + name, contents);
				}
				else if(!type.equals(""))
				{
					ArrayList<String> typeInfo = new ArrayList<String>();
					if(Util.findXSDSimpleTypeInDocument(doc, elem, type, typeInfo))
					{
						metaDataTypes.put((hierarchy == null ? "" : hierarchy )  + name, typeInfo);
					}
					else
					{
						// meet a complex type
						typeInfo.add(0, "complex type");
						metaDataTypes.put((hierarchy == null ? "" : hierarchy ) + name, typeInfo);
					}
				}
			}
		
		}
		
		NodeList importList = null;
		for (int nm = 0; nm < 2 && nodeList.getLength() == 0; nm++)
		{
			if (nm == 0) {
				importList = Util.getNodeList(doc, "//xsd:import");
			} else {
				importList = Util.getNodeList(doc, "//xsd:include");
			}
    		for (int i = 0; i < importList.getLength(); i++)
    		{
    			String location = importList.item(i).getAttributes().getNamedItem("schemaLocation").getNodeValue();
    			Document subDoc = Util.parseImportedFile(location);
    			parseMetaDataTypes(subDoc, concept, metaDataTypes, hierarchy);
    		}
		}
    }
    
	public Map<String, ArrayList<String>> getMetaDataTypes(String viewPK) throws Exception
	{
		HashMap<String, ArrayList<String>> metaDataTypes = new HashMap<String, ArrayList<String>>();
		String concept = viewPK;
		if(concept.contains("Browse_items_"))
			 concept = CommonDWR.getConceptFromBrowseItemView(viewPK);
	    Map<String, List<String>> map = new HashMap<String, List<String>>();
		Configuration config = Configuration.getInstance();
		String xsd = Util.getPort().getDataModel(
        		new WSGetDataModel(new WSDataModelPK(config.getModel()))).getXsdSchema();
		Document doc = Util.parse(xsd);
		NodeList nodeList = Util.getNodeList(doc, "//xsd:element[@name='" + concept + "']");

		parseMetaDataTypes(doc, concept, metaDataTypes, null);

		return metaDataTypes;
	}
	
    private void getForeignKeyInfo(HashMap<String, ArrayList<String>> metaDataTypes, String elemName, String jasonData) throws Exception
    {

		ArrayList<String> contents = metaDataTypes.get(elemName);
		if(contents == null)
		{
			contents = new ArrayList<String>();
			contents.add("foreign key");
			metaDataTypes.put(elemName, contents);
		}
		JSONObject jason = new JSONObject(jasonData);
		JSONArray rows = (JSONArray)jason.get("rows");
		for(int n = 0; n < rows.length(); n++)
		{
			JSONObject row = (JSONObject)rows.get(n);
			contents.add(row.get("keys")+"");
		}
    }
    
	private WSWhereOperator getOperator(String option){
		WSWhereOperator res = null;
		if (option.equalsIgnoreCase("CONTAINS"))
			res = WSWhereOperator.CONTAINS;
		else if (option.equalsIgnoreCase("EQUALS"))
			res = WSWhereOperator.EQUALS;
		else if (option.equalsIgnoreCase("GREATER_THAN"))
			res = WSWhereOperator.GREATER_THAN;
		else if (option.equalsIgnoreCase("GREATER_THAN_OR_EQUAL"))
			res = WSWhereOperator.GREATER_THAN_OR_EQUAL;
		else if (option.equalsIgnoreCase("JOIN"))
			res = WSWhereOperator.JOIN;
		else if (option.equalsIgnoreCase("LOWER_THAN"))
			res = WSWhereOperator.LOWER_THAN;
		else if (option.equalsIgnoreCase("LOWER_THAN_OR_EQUAL"))
			res = WSWhereOperator.LOWER_THAN_OR_EQUAL;
		else if (option.equalsIgnoreCase("NOT_EQUALS"))
			res = WSWhereOperator.NOT_EQUALS;
		else if (option.equalsIgnoreCase("STARTSWITH"))
			res = WSWhereOperator.STARTSWITH;
		else if (option.equalsIgnoreCase("STRICTCONTAINS"))
			res = WSWhereOperator.STRICTCONTAINS;
		return res;											
	}
	public static void main(String[] args) {
		new ItemsBrowserDWR().deleteItem("Custom", new String[]{"28"});
	}
	private org.jboss.dom4j.Document parsXMLString(String xmlString) {
        SAXReader saxReader = new SAXReader();   
        org.jboss.dom4j.Document document = null;   
        try  
        {   
            document = saxReader.read(new StringReader(xmlString));   
        } catch (DocumentException e)   
        {   
            e.printStackTrace();   
            return null;   
        }
		return document;
		
	}
}
