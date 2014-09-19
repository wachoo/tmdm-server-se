package com.amalto.core.delegator.impl;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.util.XtentisException;

public class DefaultLocalUserDelegator extends ILocalUser{

	public ILocalUser getILocalUser() throws XtentisException {
		return new DefaultLocalUserDelegator();
	}	
}
