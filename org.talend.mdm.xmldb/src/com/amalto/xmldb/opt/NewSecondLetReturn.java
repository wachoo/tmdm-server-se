package com.amalto.xmldb.opt;

public class NewSecondLetReturn implements GenAble{
	
	private String retrunStr;

	public NewSecondLetReturn(String retrunStr) {
		this.retrunStr=retrunStr;
	}
	
    public String gen() {
		
		StringBuffer newXQL=new StringBuffer();
		newXQL.append(" return ")
		      .append(retrunStr);
		return newXQL.toString();

	}

}
