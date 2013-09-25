package com.amalto.core.ejb;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.talend.mdm.commmon.util.bean.ItemCacheKey;
import org.talend.mdm.commmon.util.core.CommonUtil;
import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * @author Starkey Shu
 *
 */
public class DroppedItemPOJO implements Serializable{
   
	
    private String revisionID;
    private DataClusterPOJOPK dataClusterPOJOPK;
    private String uniqueId;
    private String conceptName;//redundancy
	private String[] ids;//redundancy
    private String partPath;
    
    private String insertionUserName;
    private Long insertionTime;
    
    private String projection;
    
    public DroppedItemPOJO() {
    	
	}
    
	public DroppedItemPOJO(String revisionID,DataClusterPOJOPK dataClusterPOJOPK, String uniqueId,String conceptName,String[] ids,
			String partPath, String projection,
			String insertionUserName, Long insertionTime) {
		super();
		this.revisionID = revisionID;
		this.dataClusterPOJOPK = dataClusterPOJOPK;
		this.uniqueId = uniqueId;
		this.conceptName = conceptName;
		this.ids = ids;
		this.partPath = partPath;
		this.projection = projection;
		this.insertionUserName = insertionUserName;
		this.insertionTime = insertionTime;
	}


	public String getRevisionID() {
		return revisionID;
	}


	public void setRevisionID(String revisionID) {
		this.revisionID = revisionID;
	}


	public DataClusterPOJOPK getDataClusterPOJOPK() {
		return dataClusterPOJOPK;
	}


	public void setDataClusterPOJOPK(DataClusterPOJOPK dataClusterPOJOPK) {
		this.dataClusterPOJOPK = dataClusterPOJOPK;
	}


	public String getUniqueId() {
		return uniqueId;
	}


	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	
	
	public String getConceptName() {
		return conceptName;
	}


	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}


	public String[] getIds() {
		return ids;
	}


	public void setIds(String[] ids) {
		this.ids = ids;
	}


	public String getPartPath() {
		return partPath;
	}


	public void setPartPath(String partPath) {
		this.partPath = partPath;
	}
	
	
	public String getInsertionUserName() {
		return insertionUserName;
	}


	public void setInsertionUserName(String insertionUserName) {
		this.insertionUserName = insertionUserName;
	}

	public Long getInsertionTime() {
		return insertionTime;
	}


	public void setInsertionTime(Long insertionTime) {
		this.insertionTime = insertionTime;
	}


	public String getProjection() {
		return projection;
	}


	public void setProjection(String projection) {
		this.projection = projection;
	}


	public DroppedItemPOJOPK obtainDroppedItemPK() {
		return new DroppedItemPOJOPK(
				revisionID,
				obtainRefItemPK(),
				partPath
		);
	}
	
	public ItemPOJOPK obtainRefItemPK() {
		return new ItemPOJOPK(
				dataClusterPOJOPK,
				conceptName,
				ids
		);

	}
	
	@Override
	public String toString() {
		//Marshal
		StringWriter sw = new StringWriter();
		try {
			Marshaller.marshal(this, sw);
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		return sw.toString();
	}
	
    /**
     * @param droppedItemPOJOPK
     * @return ItemPOJOPK
     * @throws XtentisException
     * 
     * recover dropped item
     */
    public static ItemPOJOPK recover(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException {
    	
        //validate input
    	if (droppedItemPOJOPK==null) return null;    	
    	ItemPOJOPK refItemPOJOPK=droppedItemPOJOPK.getRefItemPOJOPK();
    	String actionName="recover";
    	//for recover we need to be admin, or have a role of admin , or role of write on instance 
    	String userName=rolesFilter(refItemPOJOPK,actionName,"w");
    	//get the universe and revision ID
    	universeFilter(refItemPOJOPK);
    	String sourceItemRevision=droppedItemPOJOPK.getRevisionId();
    	//get XmlServerSLWrapperLocal
    	XmlServerSLWrapperLocal server=obtainXmlServerSLWrapperLocal();
    	
        try {
        	//load dropped content
        	String partPath=droppedItemPOJOPK.getPartPath();
        	if(partPath==null||partPath.length()==0)return null;
        	
        	StringBuffer getXmlDocument=new StringBuffer();
        	if(partPath.equals("/")){
        		String doc=server.getDocumentAsString(null, "MDMItemsTrash", droppedItemPOJOPK.getUniquePK(), null);
        		getXmlDocument.append(doc);
        	}else{
            	String query ="document('"+droppedItemPOJOPK.getUniquePK()+"')/dropped-item-pOJO/projection";
            	if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName())) {
            		String collectionpath= CommonUtil.getPath(null, "MDMItemsTrash");
            		collectionpath=collectionpath+"/"+droppedItemPOJOPK.getUniquePK()+".xml";
            		query ="for $pivot0 in doc(\""+collectionpath+"\")/dropped-item-pOJO/projection return $pivot0";
            	}
            	ArrayList<String> results=server.runQuery(null, "MDMItemsTrash", query,null);
        		if (results==null||results.size()==0) return null; 
        		for (int i = 0; i < results.size(); i++) {
        			getXmlDocument.append(results.get(i));
    			}
        	}
        	
        	if(getXmlDocument.toString()==null||getXmlDocument.toString().length()==0)return null;
        	
        	//bak source item
        	String bakDoc="";
        	if(partPath.equals("/")){
        		bakDoc=getXmlDocument.toString();
        	}else{
        		bakDoc=server.getDocumentAsString(sourceItemRevision, refItemPOJOPK.getDataClusterPOJOPK().getUniqueId(), refItemPOJOPK.getUniqueID(), null);
        	}
        	//recover source item
        	if(partPath.equals("/")){
        		
        		DroppedItemPOJO droppedItemPOJO=(DroppedItemPOJO) Unmarshaller.unmarshal(DroppedItemPOJO.class,new InputSource(new StringReader(getXmlDocument.toString())));
        		server.start(refItemPOJOPK.getDataClusterPOJOPK().getUniqueId());
                server.putDocumentFromString(droppedItemPOJO.getProjection(), refItemPOJOPK.getUniqueID(), refItemPOJOPK.getDataClusterPOJOPK().getUniqueId(), sourceItemRevision);
                server.commit(refItemPOJOPK.getDataClusterPOJOPK().getUniqueId());
        	}else{
        		Document partDom=Util.parse(getXmlDocument.toString());
        		String insertText=partDom.getFirstChild().getTextContent();
        		Node inserNode=Util.parse(insertText).getFirstChild();

        		if(bakDoc==null)throw new XtentisException("The source item is not exist now!\n"); 
        		Document targetDom=Util.parse(bakDoc);
        		
                String xPath = "/ii/p"+parserParentPartPath(partPath);
                
                //NOTE only one parent 
                Node singleParentNode=null;
                NodeList parentNodeList=Util.getNodeList(targetDom, xPath);
                
                if(parentNodeList.getLength()>0){
                	singleParentNode=parentNodeList.item(0);
                }
                if(singleParentNode!=null){
                	Node importNode = targetDom.importNode(inserNode, true);
                	singleParentNode.appendChild(importNode);
                }
                
                //validate
                String targetDomXml=Util.nodeToString(targetDom);
				ItemPOJO itemPOJO=ItemPOJO.parse(targetDomXml);
				if(itemPOJO.getDataModelName()!=null){
					DataModelPOJO dataModelPOJO=ObjectPOJO.load(itemPOJO.getDataModelRevision(), DataModelPOJO.class, new DataModelPOJOPK(itemPOJO.getDataModelName()));
					
					if(dataModelPOJO!=null){
						Element projection=null;
						try {
							projection = itemPOJO.getProjection();
						} catch (Exception e) {
							throw new XtentisException("\nThe recovered item can not be empty!");
						}
						Util.validate(projection, dataModelPOJO.getSchema());
					}
				}
                
                server.start(refItemPOJOPK.getDataClusterPOJOPK().getUniqueId());
                server.putDocumentFromString(targetDomXml, refItemPOJOPK.getUniqueID(), refItemPOJOPK.getDataClusterPOJOPK().getUniqueId(), sourceItemRevision);//need set indent-number
                server.commit(refItemPOJOPK.getDataClusterPOJOPK().getUniqueId());
        	}
        	//delete dropped item
            server.start("MDMItemsTrash"); //$NON-NLS-1$
        	long res = server.deleteDocument(
            		null,
            		"MDMItemsTrash", //$NON-NLS-1$
            		droppedItemPOJOPK.getUniquePK()
            );
            server.commit("MDMItemsTrash"); //$NON-NLS-1$
        	
        	if(res==-1){
        		//roll back
        		if(partPath.equals("/")){
        			server.deleteDocument(
        					sourceItemRevision,
        					refItemPOJOPK.getDataClusterPOJOPK().getUniqueId(),
        					refItemPOJOPK.getUniqueID()
                    );
        		}else{
                    server.start(refItemPOJOPK.getDataClusterPOJOPK().getUniqueId());
        			server.putDocumentFromString(bakDoc, refItemPOJOPK.getUniqueID(), refItemPOJOPK.getDataClusterPOJOPK().getUniqueId(), sourceItemRevision);
                    server.commit(refItemPOJOPK.getDataClusterPOJOPK().getUniqueId());
        		}
        	}

            //It need to remove it from cache, because it still be load on cache
            ItemPOJO.getCache().remove(
                    new ItemCacheKey(droppedItemPOJOPK.getRevisionId(), droppedItemPOJOPK.getRefItemPOJOPK().getUniqueID(),
                            droppedItemPOJOPK.getRefItemPOJOPK().getDataClusterPOJOPK().getUniqueId()));
        	return refItemPOJOPK;  
            
	    } catch (SAXException e) {
	    	String err = "The automatic recovered item did not obey the rules of data model.\nYou can do it manually.\n\n"+e.getLocalizedMessage();
	    	throw new XtentisException(err);
	    } catch (Exception e) {
    	    String err = "Unable to "+actionName+" the dropped item "+droppedItemPOJOPK.getUniquePK()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(DroppedItemPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    }
		

    }
    
   
    /**
     * @param regex
     * @return List<DroppedItemPOJOPK>
     * @throws XtentisException
     * 
     * find all pks of dropped items
     */
    public static List<DroppedItemPOJOPK> findAllPKs(String regex) throws XtentisException {
    	
    	String actionName="findAllPKs";
    	
    	universeFilter();
    	//get XmlServerSLWrapperLocal
    	XmlServerSLWrapperLocal server=obtainXmlServerSLWrapperLocal();
    	
    	initItemsTrash(server);
		
    	if ("".equals(regex) || "*".equals(regex) || ".*".equals(regex)) regex = null;
  
        try {
        	
        	//retrieve the item
            String[] ids = server.getAllDocumentsUniqueID(null,"MDMItemsTrash");
            
            if (ids==null) return new ArrayList<DroppedItemPOJOPK>();
 
            //build PKs collection
            List<DroppedItemPOJOPK> list = new ArrayList<DroppedItemPOJOPK>();
            Map<String, BusinessConcept> conceptMap = new HashMap<String, BusinessConcept>();
            for (int i = 0; i < ids.length; i++) {
            	String uid=ids[i];
            	if(regex!=null){
            		boolean match = ids[i].matches(regex);
            		if(match){
            			DroppedItemPOJOPK droppedItemPOJOPK=DroppedItemPOJOPK.buildUid2POJOPK(uid, conceptMap);
                    	if(droppedItemPOJOPK!=null)list.add(droppedItemPOJOPK);
            		}
            	}else{
            		DroppedItemPOJOPK droppedItemPOJOPK=DroppedItemPOJOPK.buildUid2POJOPK(uid, conceptMap);
                	if(droppedItemPOJOPK!=null)list.add(droppedItemPOJOPK);
            	}
            	
			}
            
            return list;
            
	    } catch (Exception e) {
	    	String err = "Unable to find all the identifiers for dropped items "
    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(DroppedItemPOJO.class).error(err,e);
		    throw new XtentisException(err);
	    }
    }
    
    
    /**
     * @param droppedItemPOJOPK
     * @return DroppedItemPOJO
     * @throws XtentisException
     * 
     * load a dropped item
     */
    public static DroppedItemPOJO load(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException {
        
        if (droppedItemPOJOPK==null) return null;
    	
    	ItemPOJOPK refItemPOJOPK=droppedItemPOJOPK.getRefItemPOJOPK();
    	String actionName="load";
    	//for load we need to be admin, or have a role of admin , or role of write on instance or role of read on instance
    	String userName=rolesFilter(refItemPOJOPK,actionName,"r");
    	//get the universe and revision ID
    	String sourceItemRevision=universeFilter(refItemPOJOPK);
    	//get XmlServerSLWrapperLocal
    	XmlServerSLWrapperLocal server=obtainXmlServerSLWrapperLocal();
    	
    	//load the dropped item
    	try {
          //retrieve the dropped item
          String droppedItemStr = server.getDocumentAsString(null, "MDMItemsTrash", droppedItemPOJOPK.getUniquePK());
                                  
          if (droppedItemStr==null) {
              return null;
          }
          
          DroppedItemPOJO droppedItemPOJO=(DroppedItemPOJO) Unmarshaller.unmarshal(DroppedItemPOJO.class,new InputSource(new StringReader(droppedItemStr)));
         
          return droppedItemPOJO;
	        	                                            
	    } catch (Exception e) {
	  	    String err = "Unable to load the dropped item  "+droppedItemPOJOPK.getUniquePK()
	  	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
	  	    org.apache.log4j.Logger.getLogger(DroppedItemPOJO.class).error(err,e);
	  	    throw new XtentisException(err);
	    }	                                            

    }
    
    /**
     * @param droppedItemPOJOPK
     * @return DroppedItemPOJOPK
     * @throws XtentisException
     * 
     * remove a dropped item record
     */
    public static DroppedItemPOJOPK remove(DroppedItemPOJOPK droppedItemPOJOPK) throws XtentisException {
        
    	if (droppedItemPOJOPK==null) return null;
    	
    	ItemPOJOPK refItemPOJOPK=droppedItemPOJOPK.getRefItemPOJOPK();
    	String actionName="remove";
    	//for remove we need to be admin, or have a role of admin , or role of write on instance
    	String userName=rolesFilter(refItemPOJOPK,actionName,"w");
    	//get the universe and revision ID
    	String sourceItemRevision=universeFilter(refItemPOJOPK);
    	//get XmlServerSLWrapperLocal
    	XmlServerSLWrapperLocal server=obtainXmlServerSLWrapperLocal();
    	
        try {
            server.start("MDMItemsTrash"); //$NON-NLS-1$
            //remove the record
            long res = server.deleteDocument(
            		null,
            		"MDMItemsTrash",
            		droppedItemPOJOPK.getUniquePK()
            );
            server.commit("MDMItemsTrash"); //$NON-NLS-1$
            if (res==-1) return null;
            
            return droppedItemPOJOPK;
            
	    } catch (Exception e) {
    	    String err = "Unable to "+actionName+" the dropped item "+droppedItemPOJOPK.getUniquePK()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(DroppedItemPOJO.class).error(err,e);
    	    throw new XtentisException(err);
	    }  

    }
    
    private static void initItemsTrash(XmlServerSLWrapperLocal server)throws XtentisException {
    	
    	try {
    		 
    		//init MDMItemsTrash Cluster
    		if(ObjectPOJO.load(null,DataClusterPOJO.class,new DataClusterPOJOPK("MDMItemsTrash"))==null){
    			//create record
    			DataClusterPOJO dataCluster=new DataClusterPOJO("MDMItemsTrash","Holds logical deleted items",null); 
    			ObjectPOJOPK pk = dataCluster.store(null);
    		    if (pk == null) throw new XtentisException("Unable to create the Data Cluster. Please check the XML Server logs");
    		    
    		    //create cluster
    		    boolean exist = server.existCluster(null, pk.getUniqueId());
    			if (!exist)
    			    server.createCluster(null, pk.getUniqueId()); 
    			//log
    			org.apache.log4j.Logger.getLogger(ItemPOJO.class).info("Init MDMItemsTrash Cluster");
    		}
            
	    } catch (Exception e) {
    	    String err = "Unable to init the items-trash "+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    throw new XtentisException(err, e);
	    }
		
    }
    
	private static XmlServerSLWrapperLocal obtainXmlServerSLWrapperLocal() throws XtentisException {
		XmlServerSLWrapperLocal server;
		try {
			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(DroppedItemPOJO.class).error(err,e);
			throw new XtentisException(err);
		}
		return server;
	}


	private static String universeFilter(ItemPOJOPK refItemPOJOPK)throws XtentisException {
		UniversePOJO universe = universeFilter();
    	String sourceItemRevisionID = universe.getConceptRevisionID(refItemPOJOPK.getConceptName());
    	return sourceItemRevisionID;
	}

	private static UniversePOJO universeFilter() throws XtentisException {
		UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(DroppedItemPOJO.class).error(err);
    		throw new XtentisException(err);
    	}
		return universe;
	}
	
	private static String rolesFilter(ItemPOJOPK refItemPOJOPK,String actionName,String authorizeMode)throws XtentisException {
		boolean authorized = false;
    	ILocalUser user = LocalUser.getLocalUser();
    	
    	if(authorizeMode.equals("w")){
    		
            if (MDMConfiguration.getAdminUser().equals(user.getUsername())
                    || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) {
        		authorized = true;
        	} else if (XSystemObjects.isExist(XObjectType.DATA_CLUSTER, refItemPOJOPK.getDataClusterPOJOPK().getUniqueId()) || user.userItemCanWrite(ItemPOJO.adminLoad(refItemPOJOPK),refItemPOJOPK.getDataClusterPOJOPK().getUniqueId(),refItemPOJOPK.getConceptName())) {
        		authorized = true;
        	}
    		
    	}else if(authorizeMode.equals("r")){
    		
            if (MDMConfiguration.getAdminUser().equals(user.getUsername())
                    || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) {
        		authorized = true;
        	} else if (user.userItemCanRead(ItemPOJO.adminLoad(refItemPOJOPK))) {
        		authorized = true;
        	}
    	}
    	
    	
    	if (! authorized) {
    	    String err = 
    	    	"Unauthorized "+actionName+" access by " +
    	    	"user "+user.getUsername()+" on a dropped item of Item '"+refItemPOJOPK.getUniqueID()+"'"; 
    	    org.apache.log4j.Logger.getLogger(ObjectPOJO.class).error(err);
    		throw new XtentisException(err);    				
    	}
    	
    	String passedUserName=user.getUsername();
    	return passedUserName;
	}


	private static String parserParentPartPath(String partPath) {
		int pos=partPath.lastIndexOf("/");
		String parentPartPath=partPath.substring(0, pos);
		return parentPartPath;
	}
	
}
