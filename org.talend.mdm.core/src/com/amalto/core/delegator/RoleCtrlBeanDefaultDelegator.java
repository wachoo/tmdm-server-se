package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.util.XtentisException;

public class RoleCtrlBeanDefaultDelegator implements IRoleCtrlBeanDelegator{

	public RolePOJO existsRole(RolePOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public RolePOJO getRole(RolePOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public Collection<RolePOJOPK> getRolePKs(String regex)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public RolePOJOPK putRole(RolePOJO role) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public RolePOJOPK removeRole(RolePOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

}
