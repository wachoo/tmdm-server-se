/*
 * Created on 9 août 2005
 */
package com.amalto.core.util;

import java.io.Serializable;

/**
 * @author bgrieder
 *
 */
public class AssignDocumentInfo implements Serializable{

    
	//for assign
	String docname;
	String destname;
	String xyql;
	
	public AssignDocumentInfo() {
		super();
	}
	
	public AssignDocumentInfo(
			String docname,
			String destname,
			String xyql
			) {
		this.docname = docname;
		this.destname = destname;
		this.xyql = xyql;
	}
	
	public String getXyql() {
		return xyql;
	}
	public void setXyql(String xyql) {
		this.xyql = xyql;
	}
	public String getDestname() {
		return destname;
	}
	public void setDestname(String destname) {
		this.destname = destname;
	}
	public String getDocname() {
		return docname;
	}
	public void setDocname(String docname) {
		this.docname = docname;
	}
}
	

