package com.amalto.core.objects.storedprocedure.ejb;

import java.util.Collection;

import javax.ejb.EJBException;
import javax.naming.InitialContext;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


/**
 * @author Bruno Grieder
 * 
 */
public class StoredProcedurePOJO extends ObjectPOJO{
   
    private String name;
    private String description;
    private String procedure;
    private boolean refreshCache;    
    /**
     * 
     */
    public StoredProcedurePOJO() {
        super();
    }
    
	public StoredProcedurePOJO(String name, String query) {
		super();
		this.name = name;
		this.procedure = query;
	}
	

	/**
	 * @return Returns the Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	public boolean isRefreshCache() {
		return refreshCache;
	}

	public void setRefreshCache(boolean refreshCache) {
		this.refreshCache = refreshCache;
	}

	/**
	 * @return Returns the Description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
	/**
	 * @return Returns the Procedure.
	 */
	public String getProcedure() {
		return procedure;
	}

	/**
	 * @param procedure The procedure to set.
	 */
	public void setProcedure(String procedure) {
		this.procedure = procedure;
	}

        
    public Collection<String> execute(String revisionID, DataClusterPOJOPK dataClusterPOJOPK, String[] parameters) throws XtentisException {
        
    	if (getProcedure()==null) return null;
    	
        try {
	    	if(refreshCache) {
	    		ItemPOJO.clearCache();
	    	}
            //get the xml server wrapper
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
        	String cluster=null;
        	if(dataClusterPOJOPK!=null)cluster=dataClusterPOJOPK.getUniqueId();
        	//execute
        	return server.runQuery(revisionID, cluster, getProcedure(), parameters);
 
	    } catch (Exception e) {
    	    String err = "Unable to execute the Stored Procedure "+getPK().getUniqueId()
    	    		+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new EJBException(err, e);
	    } 

    }
   
    
    
	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}

}
