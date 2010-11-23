// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.gxt.framework.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AppHeader implements IsSerializable{

	public static final String APP_HEADER="APP_HEADER"; //$NON-NLS-1$
	
	public static final String LOGIN_USER="LOGIN_USER"; //$NON-NLS-1$

	
	private Map<String,Object> headerMap=null;
	
	public AppHeader() {
		this.headerMap=new HashMap<String,Object>();
		mockHeader(); //TODO mock header
	}

    private void mockHeader() {
		this.headerMap.put(LOGIN_USER, "administrator");
    }
	
	public void put(String key,Object value) {
		this.headerMap.put(key, value);
	}
	
	public Object get(String key) {
		return this.headerMap.get(key);
	}
	
	public Object get(String key,Object defaultValue) {
	    Object gettedValue = this.headerMap.get(key);
	    if(gettedValue==null)return defaultValue;
	    else return gettedValue;
    }
}
