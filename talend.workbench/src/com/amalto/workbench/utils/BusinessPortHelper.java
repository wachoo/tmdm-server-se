package com.amalto.workbench.utils;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.workbench.webservices.WSGetCurrentUniverse;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniverseItemsRevisionIDs;
import com.amalto.workbench.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.workbench.webservices.XtentisPort;

/**
 * @author hshu
 *
 * This is a business helper class ,which composite WS-Port to implement more complex business logic  
 */
public class BusinessPortHelper {
	
	public static List<String> getPossibleItemsRevisionsInCurrentUniverse(XtentisPort port,String conceptName) throws RemoteException {
		
		List<String> possibleItemsRevisionsList=new ArrayList<String>();
		
		WSUniverse wUuniverse=port.getCurrentUniverse(new WSGetCurrentUniverse());
		WSUniverseItemsRevisionIDs[] itemsRevisionIDs=wUuniverse.getItemsRevisionIDs();
		for (int i = 0; i < itemsRevisionIDs.length; i++) {
			WSUniverseItemsRevisionIDs wSUniverseItemsRevisionIDs=itemsRevisionIDs[i];
			
			String conceptPatternString=wSUniverseItemsRevisionIDs.getConceptPattern();
			String revisionID=wSUniverseItemsRevisionIDs.getRevisionID();
			
			Pattern conceptPattern = Pattern.compile(conceptPatternString);
			Matcher matcher = conceptPattern.matcher(conceptName);
			if(matcher.matches()){
				possibleItemsRevisionsList.add(revisionID);
			}
		}
		
		return possibleItemsRevisionsList;
		
	}
	
	public static Map<String,String> getObjectsRevisionMapInCurrentUniverse(XtentisPort port) {
		
		Map<String,String> objectsRevisionMap=new HashMap<String,String>();
		
		try {
			WSUniverse wUuniverse=port.getCurrentUniverse(new WSGetCurrentUniverse());
			WSUniverseXtentisObjectsRevisionIDs[] xtentisObjectsRevisionIDs=wUuniverse.getXtentisObjectsRevisionIDs();
			for (int i = 0; i < xtentisObjectsRevisionIDs.length; i++) {
				WSUniverseXtentisObjectsRevisionIDs wSUniverseXtentisObjectsRevisionIDs=xtentisObjectsRevisionIDs[i];
				
				String xtentisObjectName=wSUniverseXtentisObjectsRevisionIDs.getXtentisObjectName();
				String revisionID=wSUniverseXtentisObjectsRevisionIDs.getRevisionID();
				
				objectsRevisionMap.put(xtentisObjectName, revisionID);
				
				
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return objectsRevisionMap;

	}

}
