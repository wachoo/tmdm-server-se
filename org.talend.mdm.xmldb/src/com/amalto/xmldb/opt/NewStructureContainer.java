package com.amalto.xmldb.opt;

public class NewStructureContainer {
	
	private NewFirstLet newFirstLet;
	private NewSecondLet newSecondLet;
	private NewReturn newReturn;
	
	public NewStructureContainer(NewFirstLet newFirstLet,
			NewSecondLet newSecondLet, NewReturn newReturn) {
		super();
		this.newFirstLet = newFirstLet;
		this.newSecondLet = newSecondLet;
		this.newReturn = newReturn;
	}
	
	public String gen() {
		
		StringBuffer newXQL=new StringBuffer();
		newXQL.append(newFirstLet.gen()).append("\n")
		      .append(newSecondLet.gen()).append("\n")
		      .append(newReturn.gen());
		return newXQL.toString();

	}
	

}
