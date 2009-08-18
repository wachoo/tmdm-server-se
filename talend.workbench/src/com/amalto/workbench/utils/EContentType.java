package com.amalto.workbench.utils;

import java.util.HashSet;
import java.util.Set;

public enum EContentType {
	TEXT_XML("text/xml"),
	TEXT_PLAIN("text/plain"),
	APPLICATION_XML("application/xhtml+xml"),
	APPLICATION_ITEMPK("application/xtentis.itempk");
	String name;
	EContentType(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static Set<String> allTypes(){
		Set<String> list=new HashSet<String>();
		for(EContentType type:values()){
			list.add(type.name);
		}
		return list;
	}
}
