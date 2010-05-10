package com.amalto.xmldb.opt;


public class OldReturn extends PartialXQL{
	
	
	
	private String subsequencePart="";

	public OldReturn(String inputContent) {
		super(inputContent);
	}
	
	protected void parse() {
		int startPos=inputContent.indexOf("subsequence(");
		String tmp=inputContent.substring(startPos);
		tmp=tmp.substring(0,tmp.indexOf(")"));
		
		subsequencePart=tmp+")";
	}

	public String getSubsequencePart() {
		return subsequencePart;
	}


}
