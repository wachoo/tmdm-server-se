package com.amalto.core.delegator;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;

public abstract class ILocalUser implements IBeanDelegator{
	public Subject getICurrentSubject() throws XtentisException {
		String SUBJECT_CONTEXT_KEY = "javax.security.auth.Subject.container";       		
		Subject subject;
		try {
			subject = (Subject) PolicyContext.getContext(SUBJECT_CONTEXT_KEY);
		} catch (PolicyContextException e1) {
			String err = "Unable find the local user: the JACC Policy Context cannot be accessed: "+e1.getMessage();
			org.apache.log4j.Logger.getLogger(LocalUser.class).error(err,e1);
			throw new XtentisException(err);
		}
		return subject;
	}
	
	public ILocalUser getILocalUser() throws XtentisException {
		return null;
	}

	public HashSet<String> getRoles() {
		// TODO Auto-generated method stub
		HashSet<String> set=new HashSet<String>();
		set.add("administration");
		set.add("authenticated");
		return set;
	}

	public UniversePOJO getUniverse() {
		HashMap<String, String>map=new HashMap<String, String>();
		for(String name:UniversePOJO.getXtentisObjectName()){
			map.put(name, null);
		}
		return new UniversePOJO("[HEAD]","",map,new LinkedHashMap<String, String>());
	}

	public String getUserXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername()  {
		Set<Principal> set;
		try {
			set = getICurrentSubject().getPrincipals();
			for (Iterator<Principal> iter = set.iterator(); iter.hasNext(); ) {
				Principal principal = iter.next();
				if (principal instanceof Group) {
					Group group = (Group) principal;
					//@see XtentisMDMLoginModule
					if("Username".equals(group.getName())) {
						if (group.members().hasMoreElements()) {
							return group.members().nextElement().getName();
						}
					}
				}
			}//for
		} catch (XtentisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "admin";
	}

	public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}

	public void logout() throws XtentisException {
		String SERVLET_CONTEXT_KEY = "javax.servlet.http.HttpServletRequest";
		HttpServletRequest request;
		try {
			request = (HttpServletRequest) PolicyContext.getContext(SERVLET_CONTEXT_KEY);
		} catch (PolicyContextException e1) {
			String err = "Unable find the local servlet request: the JACC Policy Context cannot be accessed: "+e1.getMessage();
			org.apache.log4j.Logger.getLogger(LocalUser.class).error(err,e1);
			throw new XtentisException(err);
		}
		if (request != null) request.getSession().invalidate();
	}

	public void resetILocalUsers() throws XtentisException {
		// TODO Auto-generated method stub
		
	}

	public void setRoles(HashSet<String> roles) {
		// TODO Auto-generated method stub
		
	}

	public void setUniverse(UniversePOJO universe) {
		// TODO Auto-generated method stub
		
	}

	public void setUserXML(String userXML) {
		// TODO Auto-generated method stub
		
	}

	public void setUsername(String username) {
		// TODO Auto-generated method stub
		
	}

	public boolean userCanRead(Class<?> objectTypeClass, String instanceId)
			throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean userCanWrite(Class<?> objectTypeClass, String instanceId)
			throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean userItemCanRead(ItemPOJO item) throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean userItemCanRead(ItemPOJOPK item) throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}
	public boolean userItemCanWrite(ItemPOJO item, String datacluster,
			String concept) throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}
	public boolean userItemCanWrite(ItemPOJOPK item, String datacluster,
			String concept) throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}	
}
