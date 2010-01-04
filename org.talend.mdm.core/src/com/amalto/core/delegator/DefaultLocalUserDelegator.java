package com.amalto.core.delegator;

import java.util.HashSet;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;
import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpServletRequest;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;

public class DefaultLocalUserDelegator implements ILocalUser,IBeanDelegator{

	public Subject getICurrentSubject() throws XtentisException {
		// TODO Auto-generated method stub
		return null;
	}

	public ILocalUser getILocalUser() throws XtentisException {
		// TODO Auto-generated method stub
		return null;
	}

	public HashSet<String> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	public UniversePOJO getUniverse() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUserXML() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUsername() {
		// TODO Auto-generated method stub
		return null;
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

	public boolean userItemCanWrite(ItemPOJO item, String datacluster,
			String concept) throws XtentisException {
		// TODO Auto-generated method stub
		return true;
	}

	
}
