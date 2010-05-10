package com.amalto.xmldb.opt;

public class NewSecondLet implements GenAble{
	
	private NewSecondLetFor newSecondLetFor;
	private NewSecondLetReturn newSecondLetReturn;
	
	public NewSecondLet(String pivotVar,String subsequencePart,String returnStr) {
		newSecondLetFor = new NewSecondLetFor(pivotVar,subsequencePart);
		newSecondLetReturn = new NewSecondLetReturn(returnStr);
	}

	@Override
	public String gen() {
		StringBuffer newXQL=new StringBuffer();
		newXQL.append(" let $_page_ := ")
		      .append(newSecondLetFor.gen())
		      .append(newSecondLetReturn.gen());
		return newXQL.toString();
	}

}
