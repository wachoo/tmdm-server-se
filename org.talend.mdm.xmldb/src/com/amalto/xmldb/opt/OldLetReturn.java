package com.amalto.xmldb.opt;

public class OldLetReturn extends PartialXQL{

	public OldLetReturn(String inputContent) {
		super(inputContent);
	}
	
    public static String changePivotLocation(String pivotVar,String PK, String returnContent) {
		
    	returnContent=returnContent.replace(pivotVar+" ", pivotVar+"/"+PK+" ");
    	returnContent=returnContent.replace("("+pivotVar+")", "("+pivotVar+"/"+PK+")");
    	returnContent=returnContent.replace("/../", "/");
		return returnContent;

	}

}
