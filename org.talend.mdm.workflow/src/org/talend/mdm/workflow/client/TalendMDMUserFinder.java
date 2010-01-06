package org.talend.mdm.workflow.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import urn_com_amalto_xtentis_webservice.WSDataClusterPK;
import urn_com_amalto_xtentis_webservice.WSRunQuery;
import urn_com_amalto_xtentis_webservice.WSStringArray;

public class TalendMDMUserFinder extends TalendMDMAdapter{
	private static Pattern usernamePattern = Pattern.compile(".*?<username>(.*)</username>.*",Pattern.DOTALL);

	/**
	 * @param url
	 * @param universe
	 * 
	 * use default username/password
	 * to avoid no rights exception 
	 */
	public TalendMDMUserFinder(String url, String universe){
		super(url,universe);
	}

	/**
	 * @param roleId
	 * @return
	 * @throws Exception
	 * 
	 * find user name list of an specific roleId
	 */
	public List<String> find(String roleId) throws Exception{
		
		List<String> users=new ArrayList<String>();
		
		if(roleId==null)return users;
		if(roleId.equals("administration")){
			users.add("admin");
			return users;
		}
		
		//FIXME change to jxpath
		StringBuffer xquery=new StringBuffer();
		xquery.append("for $pivot0 in collection(\"PROVISIONING\")/ii/p/User "); 
		xquery.append("where $pivot0/roles/role eq \"").append(roleId).append("\" "); 
		xquery.append("return if ($pivot0) then $pivot0 else <User/> ");
		//System.out.println(xquery.toString());
		
		WSDataClusterPK wsDataClusterPK=new WSDataClusterPK();
		wsDataClusterPK.setPk("PROVISIONING");
		
		WSRunQuery wsRunQuery=new WSRunQuery();
		wsRunQuery.setRevisionID(null);
		wsRunQuery.setWsDataClusterPK(wsDataClusterPK);
		wsRunQuery.setQuery(xquery.toString());

		WSStringArray wsStringArray=port.runQuery(wsRunQuery);
		List<String> usersXml=wsStringArray.getStrings();
		for (Iterator iterator = usersXml.iterator(); iterator.hasNext();) {
			String userXml = (String) iterator.next();
			
			Matcher matcher = usernamePattern.matcher(userXml);
            if (matcher.matches()) {
            	String username=matcher.group(1).trim();
            	users.add(username);
            }
		}
		
		return users;

	}

}
