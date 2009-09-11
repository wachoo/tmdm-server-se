package com.amalto.core.objects.synchronization;

import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;

/**
 * Using xquery to do the sync
 * @author achen
 *
 */
public class XquerySyncUtil {
	
	public static String getObjectSyncQuery(String db, String id,String localRevisionID,String remoteRevisionID,String alorithm){
		if(localRevisionID!=null) localRevisionID=localRevisionID.replaceAll("\\[HEAD\\]|HEAD", "");
		if(remoteRevisionID!=null) remoteRevisionID=remoteRevisionID.replaceAll("\\[HEAD\\]|HEAD", "");
		StringBuffer sb=new StringBuffer();
		if(SynchronizationPlanPOJO.LOCAL_WINS.equalsIgnoreCase(alorithm)){
			String winner=localRevisionID==null||localRevisionID.length()==0?"": "R-"+localRevisionID+"/";
			sb=sb.append("let $localdocs := for $localdoc in collection('/db/"+winner + db +"') \n");
			sb=sb.append("where text:groups(util:document-name($localdoc) , '"+ id+ "') \n");
			sb=sb.append("return $localdoc \n");
			sb=sb.append("for $local in $localdocs \n");
			String remote=remoteRevisionID==null||remoteRevisionID.length()==0?"": "R-"+remoteRevisionID+"/";
			sb=sb.append("return xmldb:store('/db/"+remote  +db +"'"+",util:document-name($local),$local) \n");			
		}
		if(SynchronizationPlanPOJO.REMOTE_WINS.equalsIgnoreCase(alorithm)){
			String winner=remoteRevisionID==null||remoteRevisionID.length()==0?"": "R-"+remoteRevisionID+"/";
			sb=sb.append("let $localdocs := for $localdoc in collection('/db/"+winner  +db +"') \n");
			sb=sb.append("where text:groups(util:document-name($localdoc) , '"+ id+ "') \n");
			sb=sb.append("return $localdoc \n");
			sb=sb.append("for $local in $localdocs \n");
			String remote=localRevisionID==null||localRevisionID.length()==0?"": "R-"+localRevisionID+"/";
			sb=sb.append("return xmldb:store('/db/"+remote  +db +"'"+",util:document-name($local),$local) \n");			
		}		
		return sb.toString();
	}

	public static String getItemSyncQuery(String localcluster,String concept, String localRevisionID,String id, String remotecluster,String remoteRevisionID,String alorithm){
		if(localRevisionID!=null) localRevisionID=localRevisionID.replaceAll("\\[HEAD\\]|HEAD", "");
		if(remoteRevisionID!=null) remoteRevisionID=remoteRevisionID.replaceAll("\\[HEAD\\]|HEAD", "");
		StringBuffer sb=new StringBuffer();
		if(SynchronizationPlanPOJO.LOCAL_WINS.equalsIgnoreCase(alorithm)){
			String winner=localRevisionID==null||localRevisionID.length()==0?"": "R-"+localRevisionID+"/";
			sb=sb.append("let $localdocs := for $localdoc in collection('/db/"+winner + localcluster +"') \n");
			sb=sb.append("where text:groups(util:document-name($localdoc) , '"+ id+ "')" +" and  text:groups(util:document-name($localdoc) , '"+ concept+ "') \n");
			sb=sb.append("return $localdoc \n");
			sb=sb.append("for $local in $localdocs \n");
			String remote=remoteRevisionID==null||remoteRevisionID.length()==0?"": "R-"+remoteRevisionID+"/";
			sb=sb.append("return xmldb:store('/db/"+remote  +localcluster +"'"+",util:document-name($local),$local) \n");			
		}
		if(SynchronizationPlanPOJO.REMOTE_WINS.equalsIgnoreCase(alorithm)){
			String winner=remoteRevisionID==null||remoteRevisionID.length()==0?"": "R-"+remoteRevisionID+"/";
			sb=sb.append("let $localdocs := for $localdoc in collection('/db/"+winner  +remotecluster +"') \n");
			sb=sb.append("where text:groups(util:document-name($localdoc) , '"+ id+ "')" +" and  text:groups(util:document-name($localdoc) , '"+ concept+ "') \n");
			sb=sb.append("return $localdoc \n");
			sb=sb.append("for $local in $localdocs \n");
			String remote=localRevisionID==null||localRevisionID.length()==0?"": "R-"+localRevisionID+"/";
			sb=sb.append("return xmldb:store('/db/"+remote  +remotecluster +"'"+",util:document-name($local),$local) \n");			
		}		
		return sb.toString();
	}

}
