package com.amalto.webapp.v3.universemanager.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XConverter;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSConceptRevisionMap;
import com.amalto.webapp.util.webservices.WSConceptRevisionMapMapEntry;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetConceptsInDataClusterWithRevisions;
import com.amalto.webapp.util.webservices.WSGetUniverse;
import com.amalto.webapp.util.webservices.WSGetUniversePKs;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSSynchronizationGetUnsynchronizedObjectsIDs;
import com.amalto.webapp.util.webservices.WSUniverse;
import com.amalto.webapp.util.webservices.WSUniversePK;
import com.amalto.webapp.util.webservices.WSUniversePKArray;

public class UniverseHandler {

	private static final Logger logger = Logger
			.getLogger(UniverseHandler.class);

	public static String[] getAllUniversePks() throws RemoteException,
			XtentisWebappException {
		logger.debug("getAllUniversePks");

		String[] pks = null;

		// Notice: These are pks from all universes, cause there is no universe
		// object in synchronization plan
		WSUniversePKArray wsUniversePKArray = Util.getPort().getUniversePKs(
				new WSGetUniversePKs(".*"));
		WSUniversePK[] wsUniversePKs = wsUniversePKArray.getWsUniversePK();

		pks = new String[wsUniversePKs.length];
		for (int i = 0; i < pks.length; i++) {
			WSUniversePK wsUniversePK = wsUniversePKs[i];
			pks[i] = wsUniversePK.getPk();
		}

		return pks;

	}

	public static UniversePOJO getUniversePOJO(String newUniverseName)
			throws RemoteException, XtentisWebappException, Exception {

		if(newUniverseName==null){
			UniversePOJO universePOJO=new UniversePOJO();//default universe
			return universePOJO;
		}else{
			WSUniverse wsUniverse= Util.getPort().getUniverse(new WSGetUniverse(new WSUniversePK(newUniverseName)));
			UniversePOJO sourceUniverse = XConverter.WS2POJO(wsUniverse);
			return sourceUniverse;
		}
		
	}
	
	public static String getCurrentUniverseName() throws Exception {
		return com.amalto.webapp.core.util.Util.getLoginUniverse();
	}
	
	public static String[] getDefaultDataClustersNames() throws Exception {
		
		String revisionID=null;
		String objectName="Data Cluster";
		String instancePattern=".*";
		WSStringArray wsStringArray=Util.getPort().synchronizationGetUnsynchronizedObjectsIDs(new WSSynchronizationGetUnsynchronizedObjectsIDs(revisionID,objectName,instancePattern,null));
		String[] dcPKs=wsStringArray.getStrings();
		
		return dcPKs;
	}
	
    public static Map getDataConceptRevisions(String sourceUniverseName,String dataClusterName) throws Exception {
    	
    	Map pair=new HashMap();
    	
    	if(sourceUniverseName.equals("[HEAD]"))sourceUniverseName="";
    	WSConceptRevisionMap wsConceptRevisionMap=Util.getPort().getConceptsInDataClusterWithRevisions(
				new WSGetConceptsInDataClusterWithRevisions(
						new WSDataClusterPK(dataClusterName),
						new WSUniversePK(sourceUniverseName))
		);
    	
    	WSConceptRevisionMapMapEntry[] conceptRevisionEntry=wsConceptRevisionMap.getMapEntry();
    	for (int i = 0; i < conceptRevisionEntry.length; i++) {
    		WSConceptRevisionMapMapEntry entry=conceptRevisionEntry[i];
    		String concept=entry.getConcept();
    		String revision=entry.getRevision();
    		
    		pair.put(concept, revision);
		}
		
		return pair;
	}
    
    public static String[] getRoutingRules(String revisionID) throws Exception {
    	
		String instancePattern=".*";
		String objectName="Routing Rule";
		WSStringArray wsStringArray=Util.getPort().synchronizationGetUnsynchronizedObjectsIDs(new WSSynchronizationGetUnsynchronizedObjectsIDs(revisionID,objectName,instancePattern,null));
		String[] objectPKs=wsStringArray.getStrings();
		
		return objectPKs;
	}
    
    public static String[] getTransforms(String revisionID) throws Exception {
    	
		String instancePattern=".*";
		String objectName="Transformer V2";
		WSStringArray wsStringArray=Util.getPort().synchronizationGetUnsynchronizedObjectsIDs(new WSSynchronizationGetUnsynchronizedObjectsIDs(revisionID,objectName,instancePattern,null));
		String[] objectPKs=wsStringArray.getStrings();
		
		return objectPKs;
	}
    
    public static String[] getRoles(String revisionID) throws Exception {
    	
		String instancePattern=".*";
		String objectName="Role";
		WSStringArray wsStringArray=Util.getPort().synchronizationGetUnsynchronizedObjectsIDs(new WSSynchronizationGetUnsynchronizedObjectsIDs(revisionID,objectName,instancePattern,null));
		String[] objectPKs=wsStringArray.getStrings();
		
		return objectPKs;
	}

}
