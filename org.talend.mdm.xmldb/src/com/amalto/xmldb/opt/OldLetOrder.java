package com.amalto.xmldb.opt;

public class OldLetOrder extends PartialXQL{

	public OldLetOrder(String inputContent) {
		super(inputContent);
	}
	
	public boolean has() {
		
		if(inputContent.trim().length()>0)return true;
		return false;

	}

}
