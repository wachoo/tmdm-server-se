package org.talend.mdm.webapp.itemsbrowser2.client.model;

import java.io.Serializable;

public class PictureBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String url;
	private int width;
	private int height;
	private String preserveAspectRatio;
	
	public PictureBean(){}
	
	public PictureBean(String url, int width, int height, String preserveAspectRatio){
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

	public String getPreserveAspectRatio() {
		return preserveAspectRatio;
	}

	public void setPreserveAspectRatio(String preserveAspectRatio) {
		this.preserveAspectRatio = preserveAspectRatio;
	}
}
