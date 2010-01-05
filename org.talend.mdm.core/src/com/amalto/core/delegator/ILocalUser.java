package com.amalto.core.delegator;

import java.util.HashSet;

import javax.security.auth.Subject;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.XtentisException;

public interface ILocalUser {
	public HashSet<String> getRoles();
	public void setRoles(HashSet<String> roles);
	public String getUsername() ;
	public void setUsername(String username);
	public UniversePOJO getUniverse();
	public void setUniverse(UniversePOJO universe);
	public String getUserXML();	
	void setUserXML(String userXML);
	ILocalUser getILocalUser() throws XtentisException;
	Subject getICurrentSubject() throws XtentisException;
	void resetILocalUsers() throws XtentisException;
	void logout() throws XtentisException;
	boolean isAdmin(Class<?> objectTypeClass) throws XtentisException;
	public boolean userItemCanWrite(ItemPOJO item,String datacluster, String concept)throws XtentisException;
	boolean userItemCanRead(ItemPOJO item)throws XtentisException;
	boolean userCanWrite(Class<?> objectTypeClass, String instanceId) throws XtentisException;
	boolean userCanRead(Class<?> objectTypeClass, String instanceId) throws XtentisException;
	
}
