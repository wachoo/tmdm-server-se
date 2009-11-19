package org.talend.mdm.ext.publish.util;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.XtentisException;

public class DomainObjectsDAOImpl implements DomainObjectsDAO{
	
	private static final String CLUSTER_NAME = "MDMDomainObjects";
	
	
	private XmlServerSLWrapperLocal server;
	
	public DomainObjectsDAOImpl(XmlServerSLWrapperLocal server) {
		this.server=server;
		
	}
	
    public String[] getAllPKs() throws XtentisException{
		
    	String[] pks=server.getAllDocumentsUniqueID(null, CLUSTER_NAME);
		return pks;
		
	}

	public boolean putResource(String domainObjectName,String xmlContent){
		
		try {
			
			long rtnStatus=server.putDocumentFromString(xmlContent, domainObjectName, CLUSTER_NAME, null);
			if(rtnStatus==-1){
				return false;
			}else{
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}
	
	public String getResource(String domainObjectName) throws XtentisException{
		return server.getDocumentAsString(null, CLUSTER_NAME, domainObjectName);
	}

}
