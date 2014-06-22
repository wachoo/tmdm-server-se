package com.amalto.webapp.v3.viewbrowser.bean;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.xml.xsom.XSAnnotation;

public class TreeNode implements Cloneable {

	public TreeNode() {
		super();
	}

	private String name;
	private String value;
	private boolean expandable;
	private String type;
	private int nodeId;


	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.getMessage());
		}
	}


	public boolean isExpandable() {
		return expandable;
	}


	public void setExpandable(boolean expandable) {
		this.expandable = expandable;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public int getNodeId() {
		return nodeId;
	}


	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getValue() {
		return value;
	}


	public void setValue(String value) {
		this.value = value;
	}

	



	
}