package org.talend.mdm.ext.publish.util;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.XtentisException;

public class PicturesDAOImpl implements PicturesDAO{
	
	private static final String CLUSTER_NAME = "MDMItemImages";
	
	
	private XmlServerSLWrapperLocal server;
	
	public PicturesDAOImpl(XmlServerSLWrapperLocal server) {
		this.server=server;
		
	}
	
    public String[] getAllPKs() throws XtentisException{
		
    	String[] pks=server.getAllDocumentsUniqueID(null, CLUSTER_NAME);
		return pks;
		
	}

}
