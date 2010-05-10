package com.amalto.xmldb.opt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OldLetFor extends PartialXQL{
	
	private static final Pattern inPattern =Pattern.compile("^(.+)\\bin\\b(.+)$");
	private static final Pattern pPattern =Pattern.compile("^(.*//p/.+)/(.*)$");
	
	String pivotVar;
	String collectionPath;
	String PKIncollectionPath;
	String collectionPathWithoutPK;

	public OldLetFor(String inputContent) {
		super(inputContent);
	}
	
	@Override
	protected void parse() {
		Matcher m = inPattern.matcher(inputContent);
		while (m.find()) {
			pivotVar=m.group(1);
			collectionPath=m.group(2);
		}
		pivotVar=pivotVar.trim();
		collectionPath=collectionPath.trim();
		
		Matcher m1 = pPattern.matcher(collectionPath);
		while (m1.find()) {
			collectionPathWithoutPK=m1.group(1);
			PKIncollectionPath=m1.group(2);
		}
	}
	
	
	public String getPivotVar() {
		return pivotVar;
	}

	public String getCollectionPath() {
		return collectionPath;
	}

	public String getPKIncollectionPath() {
		return PKIncollectionPath;
	}

	public String getCollectionPathWithoutPK() {
		return collectionPathWithoutPK;
	}
	

}
