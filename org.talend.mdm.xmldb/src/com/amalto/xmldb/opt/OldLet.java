package com.amalto.xmldb.opt;


public class OldLet extends PartialXQL {
	
	
	private OldLetFor oldLetFor;
	private OldLetWhere oldLetWhere;
	private OldLetOrder oldLetOrder;
	private OldLetReturn oldLetReturn;
	
	
	public OldLet(String inputContent) {
		super(inputContent);
	}


	@Override
	protected void parse() {
		
		
		StringBuffer forStr=new StringBuffer();
		StringBuffer whereStr=new StringBuffer();
		StringBuffer orderbyStr=new StringBuffer();
		StringBuffer returnStr=new StringBuffer();
		
		String[] words=inputContent.split("\\s+");
		
		boolean fstart=false;
		boolean wstart=false;
		boolean ostart=false;
		boolean rstart=false;
		
		String lastWord="";
		for (int i = 0; i < words.length; i++) {
			
			if(words[i].equals("for")) {
				fstart=true;
				continue;
			}
			if(words[i].equals("where")) {
				wstart=true;
				fstart=false;
				continue;
			}
			
			if(words[i].equals("order")) {
				lastWord=words[i];
				continue;
			}
			if(words[i].equals("by")&&lastWord.equals("order")) {
				ostart=true;
				wstart=false;
				fstart=false;
				continue;
			}
			
			if(words[i].equals("return")) {
				rstart=true;
				ostart=false;
				wstart=false;
				fstart=false;
				continue;
			}
			
			if(fstart)forStr.append(words[i]+" ");
			else if(wstart)whereStr.append(words[i]+" ");
			else if(ostart)orderbyStr.append(words[i]+" ");
			else if(rstart)returnStr.append(words[i]+" ");
			
			
		}
	
		oldLetFor=new OldLetFor(forStr.toString());
		oldLetWhere=new OldLetWhere(whereStr.toString());
		oldLetOrder=new OldLetOrder(orderbyStr.toString());
		oldLetReturn=new OldLetReturn(returnStr.toString());

		
	}


	public OldLetFor getOldLetFor() {
		return oldLetFor;
	}


	public OldLetWhere getOldLetWhere() {
		return oldLetWhere;
	}


	public OldLetOrder getOldLetOrder() {
		return oldLetOrder;
	}


	public OldLetReturn getOldLetReturn() {
		return oldLetReturn;
	}
	
	

}
