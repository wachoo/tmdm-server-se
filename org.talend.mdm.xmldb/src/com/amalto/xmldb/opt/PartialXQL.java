package com.amalto.xmldb.opt;

public abstract class PartialXQL {
	
	protected String inputContent;
	
	public PartialXQL(String inputContent) {
		this.inputContent=inputContent;
	}
	
	protected void parse() {
		
	}

	public String getInputContent() {
		return inputContent;
	}
	
	

}
