package com.amalto.xmldb.opt;

public class OldLetWhere extends PartialXQL{

	public OldLetWhere(String inputContent) {
		super(inputContent);
	}
	
	public static String changePivotLocation(String pivotVar,String PK, String whereContent) {
		
		whereContent=whereContent.trim();
		if(whereContent.length()>0) {
			whereContent=whereContent.replace(pivotVar+" ", pivotVar+"/"+PK+" ");
			whereContent=whereContent.replace("/../", "/");
		}
		
		return whereContent;

	}

}
