package com.amalto.webapp.core.util;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class AjaxSubject implements Serializable, HttpSessionBindingListener{
	
	public AjaxSubject(Subject subject) {
		fetchAuthenticationInfo(subject);
	}

	private String username = null;
	private String password = null;
	private String MDMServer = "localhost";
	private String MDMUsername = null;
	private String MDMPassword = null;
	private String sessionName = null;
	private String xml = null;
	private ArrayList<String> roles = new ArrayList<String>();
	
	private void fetchAuthenticationInfo(Subject subject) {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("fetchAuthenticationInfo() "+subject);
				
		Set set = subject.getPrincipals();
		for (Iterator iter = set.iterator(); iter.hasNext(); ) {
			Principal principal = (Principal) iter.next();
			if (principal instanceof Group) {
				Group group = (Group) principal;
				//@see WebAppLoginModule
				if("MDMServer".equals(group.getName())) {
					MDMServer =  ((Principal)group.members().nextElement()).getName();
				} else if("MDMUsername".equals(group.getName())) {
					MDMUsername =  ((Principal)group.members().nextElement()).getName();
				} else if("MDMPassword".equals(group.getName())) {
					MDMPassword =  ((Principal)group.members().nextElement()).getName();					
				} else if("Credential".equals(group.getName())) {
					password = ((Principal)group.members().nextElement()).getName();
				}else if("XtentisUser".equals(group.getName())) {
					xml = ((Principal)group.members().nextElement()).getName();
				}else if("Roles".equals(group.getName())) {
					Enumeration<? extends Principal> e = group.members(); 
					while (e.hasMoreElements()) {
						String role = ((Principal) e.nextElement()).getName();
						roles.add(role);
					}
				}
			} else {
				//it is the username
				if (principal.getName()!=null) //unfortunate bug of the JACC infrastructrure.....
					username = principal.getName();
			}
		}
		
		

	}
	
	/**
	 * @return Returns the subjectMap.
	 */
	public HashMap getSubjectMap() {
		HashMap<String,String> subjectMap = new HashMap<String,String>();
		subjectMap.put("MDMServer", MDMServer);
		subjectMap.put("MDMUsername", MDMUsername);
		subjectMap.put("MDMPassword", MDMPassword);
		subjectMap.put("username", username);
		subjectMap.put("password", password);
		return subjectMap;
	}
	
	public String[] getMDMData() {
		if (MDMUsername==null) 
			return new String[] {MDMServer, username, password};  
		else
			return new String[] {MDMServer, MDMUsername, MDMPassword};
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueBound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueBound(HttpSessionBindingEvent event) {
		sessionName = event.getName();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSessionBindingListener#valueUnbound(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		sessionName = null;
	}

	/**
	 * @return Returns the mDMPassword.
	 */
	public String getMDMPassword() {
		return MDMPassword;
	}

	/**
	 * @param password The mDMPassword to set.
	 */
	public void setMDMPassword(String password) {
		MDMPassword = password;
	}

	/**
	 * @return Returns the mDMServer.
	 */
	public String getMDMServer() {
		return MDMServer;
	}

	/**
	 * @param server The mDMServer to set.
	 */
	public void setMDMServer(String server) {
		MDMServer = server;
	}

	/**
	 * @return Returns the mDMUsername.
	 */
	public String getMDMUsername() {
		return MDMUsername;
	}

	/**
	 * @param username The mDMUsername to set.
	 */
	public void setMDMUsername(String username) {
		MDMUsername = username;
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @return Returns the sessionName.
	 */
	public String getSessionName() {
		return sessionName;
	}

	/**
	 * @param sessionName The sessionName to set.
	 */
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}

	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	
	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public ArrayList<String> getRoles() {
		return roles;
	}

	public void setRoles(ArrayList<String> roles) {
		this.roles = roles;
	}
	
	
	

}
