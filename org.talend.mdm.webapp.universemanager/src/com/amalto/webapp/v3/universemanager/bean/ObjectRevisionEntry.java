package com.amalto.webapp.v3.universemanager.bean;

public abstract class ObjectRevisionEntry {
	
	protected String cleanRevisionName(String input){
   	 
   	   String output=input;
   	   if(output==null||output.equals(""))output="[HEAD]";
   	 
   	   return output;
   	 
    }

}
