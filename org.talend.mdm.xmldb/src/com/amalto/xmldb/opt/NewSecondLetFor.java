package com.amalto.xmldb.opt;

public class NewSecondLetFor implements GenAble{

	private String pivotVar;
	private String subsequencePart;

	public NewSecondLetFor(String pivotVar, String subsequencePart) {
		super();
		this.pivotVar = pivotVar;
		this.subsequencePart = subsequencePart;
	}
	
    public String gen() {
		
		StringBuffer newXQL=new StringBuffer();
		newXQL.append("for ")
		      .append(pivotVar)
		      .append(" in ")
		      .append(subsequencePart);
		return newXQL.toString();

	}

}
