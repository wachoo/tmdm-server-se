package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.util.XtentisException;

public interface IRoleCtrlBeanDelegator extends BeanDelegator{
	public RolePOJOPK putRole(RolePOJO role) throws XtentisException;
	public RolePOJO getRole(RolePOJOPK pk) throws XtentisException;
	public RolePOJO existsRole(RolePOJOPK pk) throws XtentisException;
	public RolePOJOPK removeRole(RolePOJOPK pk) 
    throws XtentisException;
	public Collection<RolePOJOPK> getRolePKs(String regex) throws XtentisException;
	
}
