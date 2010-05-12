package com.amalto.webapp.core.dwr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.security.jacc.PolicyContextException;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.w3c.dom.Document;

import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.license.LicenseUtil;
import com.amalto.webapp.util.webservices.WSLogout;


/**
 * 
 * @author asaintguilhem
 *
 */

public class LayoutDWR {
	
	/**
	 * should not be used
	 * @param language
	 */
	public void setLanguage(String language){
		WebContext ctx = WebContextFactory.get();
		ctx.getSession().setAttribute("language",language);
	}
	
	public String getMenus(String language) throws Exception{
		//ArrayList<String> menus = new ArrayList<String>();
		ArrayList<JSONObject> rows = new ArrayList<JSONObject>();
		getSubMenus(Menu.getRootMenu(), language, rows, 1,1);
//		System.out.println(new JSONArray(rows).toString());
		return new JSONArray(rows).toString();
	}

	
	public int getSubMenus(Menu menu, String language, ArrayList< JSONObject> rows, int level, int i) throws Exception{
		for (Iterator<String> iter = menu.getSubMenus().keySet().iterator(); iter.hasNext(); ) {
			String key = iter.next();
			Menu subMenu= menu.getSubMenus().get(key);
			JSONObject entry = new JSONObject();
			entry.put("id", i);
			entry.put("level", level);
			entry.put("context", subMenu.getContext());
			entry.put("icon", subMenu.getIcon());
			entry.put("name", subMenu.getLabels().get(language));
			entry.put("desc", "");
			entry.put("application", subMenu.getApplication() == null ? "" : subMenu.getApplication());
			rows.add(entry);
			i++;
			if (subMenu.getSubMenus().size()>0) 
				i=getSubMenus(subMenu, language, rows, level+1,i);			
		}
		return i;	
	}
	
	public String getUsername() throws Exception{
		if(!com.amalto.core.util.Util.isEnterprise()) {
			return Util.getLoginUserName();
		}
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
	
	public String getUsernameAndUniverse() throws Exception{
		if(!com.amalto.core.util.Util.isEnterprise()) {
			String name =Util.getLoginUserName() +",UNKNOWN";
			return name;
		}
		try {
			String givenname = null;
			String familyname = null;
			String xml = Util.getAjaxSubject().getXml();
			if(xml!=null){
				Document d = Util.parse(xml);
				givenname=Util.getFirstTextNode(d,"//givenname");
				familyname= Util.getFirstTextNode(d,"//familyname");
			}
			
			String name = "";
			String universe = Util.getLoginUniverse();
			if(familyname!=null && givenname!=null) name = givenname+" "+familyname +","+universe;
			else name= Util.getAjaxSubject().getUsername();
			return name;
		} catch (PolicyContextException e) {
			e.printStackTrace();
			return "";
		}	
	} 
	
	public void logout(){
		
		try {
	        Util.getPort().logout(new WSLogout(""));
        } catch (Exception e) {
        	String err = "Unable to logout";
        	org.apache.log4j.Logger.getLogger(this.getClass()).warn(err,e);
        }
//		
//		WebContext ctx = WebContextFactory.get();
//		ctx.getSession().invalidate();
	}
	
	/**
	 * license is expried.
	 * @return
	 */
	public boolean isExpired() throws RemoteException {
	   if(!com.amalto.core.util.Util.isEnterprise()) { 
	      return false;
	   }
	   
	   LicenseUtil instance = LicenseUtil.getInstance();
	   
	   if(instance.getLicenseDate() == null) {
	      throw new RemoteException("No license found.");
	   }
	   else if(instance.getToken() == null) {
		   throw new RemoteException("No set validation token.");
	   }
	   
	   return !instance.isLicenseDateValid() || !instance.isTokenDateValid();
	}
}
