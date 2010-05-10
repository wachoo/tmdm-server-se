package com.amalto.xmldb.opt;

public class NewFirstLet implements GenAble{
	
	private String xpath;
	
	private String forClause;
	
	public NewFirstLet(String xpath, String forClause) {
		
		this.xpath=xpath;
		this.forClause=forClause;
		
	}
	
	public String gen() {
		
		if(forClause==null) {
			return " let $_leres_ :="+xpath;
		}else {
			return " let $_leres_ :="+forClause;
		}
	}

}
