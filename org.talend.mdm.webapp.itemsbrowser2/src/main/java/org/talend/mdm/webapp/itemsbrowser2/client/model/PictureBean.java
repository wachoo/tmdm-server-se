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

public class PictureBean implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;
	private int width;
	private int height;
	private boolean preserveAspectRatio;
	
	public PictureBean(){}
	
	public PictureBean(String url, int width, int height, boolean preserveAspectRatio){
		this.url = url;
		this.width = width;
		this.height = height;
		this.preserveAspectRatio = preserveAspectRatio;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean getPreserveAspectRatio() {
		return preserveAspectRatio;
	}

	public void setPreserveAspectRatio(boolean preserveAspectRatio) {
		this.preserveAspectRatio = preserveAspectRatio;
	}
	
	public String toString(){
		return url + "?width=" + width + "&height=" + height + "&preserveAspectRatio=" + preserveAspectRatio;
	}
}
