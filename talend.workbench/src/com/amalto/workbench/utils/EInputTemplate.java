package com.amalto.workbench.utils;

import java.util.HashSet;
import java.util.Set;

public enum EInputTemplate {
	APPLICATION_ITEMPK(EContentType.APPLICATION_ITEMPK.getName(),"<item-pOJOPK><concept-name>?</concept-name><ids>?</ids><data-cluster-pOJOPK><ids>?</ids></data-cluster-pOJOPK></item-pOJOPK>");
	
	String name;
	String content;
	EInputTemplate(String name, String content){
		this.name=name;
		this.content=content;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	public static Set<String> allTemplates(){
		Set<String> list=new HashSet<String>();
		for(EInputTemplate type:values()){
			list.add(type.name);
		}
		return list;
	}
}
