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
package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class UrlBean implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String address;
	
	public UrlBean(){}
	
	public UrlBean(String name, String address){
		this.name = name;
		this.address = address;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String address){
		this.address = address;
	}
}
