package com.amalto.xmldb.opt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryOptimizer {
	
	private static final Pattern LRFormatPattern =Pattern.compile("^.*let\\b(.+)return\\b(.+)$");
	
	private String inputXQL=null;
	private String outputXQL=null;
	
	private OldStructureContainer oldStructureContainer=null;
	private OldLet oldLet=null;
	private OldReturn oldReturn=null;
	
	private boolean withTotalCountOnFirstRow;
	
	public QueryOptimizer(String inputXQL,boolean withTotalCountOnFirstRow) {
		this.inputXQL=inputXQL;
		this.withTotalCountOnFirstRow=withTotalCountOnFirstRow;
	}
	
	public void parse() {
		
		String letContent="";
		String returnContent="";
		
		inputXQL=inputXQL.replace("\n", " ");
		Matcher m = LRFormatPattern.matcher(inputXQL);
		while (m.find()) {
			letContent=m.group(1);
			returnContent=m.group(2);
		}
		
		
		oldLet=new OldLet(letContent);
		oldLet.parse();
		oldReturn=new OldReturn(returnContent);
		oldReturn.parse();
		oldStructureContainer=new OldStructureContainer(oldLet,oldReturn);
		
		
		
	}
	
	public String optimize() {
	
		oldLet.getOldLetFor().parse();
		String pivotVar=oldLet.getOldLetFor().getPivotVar();
		String pKIncollectionPath=oldLet.getOldLetFor().getPKIncollectionPath();
		String newCollectionPath=oldLet.getOldLetFor().getCollectionPathWithoutPK();
		
		if(pKIncollectionPath==null)return null;//FIXME://P/concept/pk
		
		String whereExpr=OldLetWhere.changePivotLocation(pivotVar, pKIncollectionPath, oldLet.getOldLetWhere().getInputContent());
		if(whereExpr.length()>0)newCollectionPath=newCollectionPath+"["+whereExpr+"]";
		
		String subForClause=null;
		if(oldLet.getOldLetOrder().has()) {
			StringBuffer sb=new StringBuffer();
			             sb.append(" for ")
			            .append(pivotVar)
			            .append(" in ")
			            .append(newCollectionPath)
			            .append(" order by ")
			            .append(oldLet.getOldLetOrder().getInputContent())
			            .append(" return ")
			            .append(pivotVar).append(" ");
		   subForClause=sb.toString();
		}
		
		NewFirstLet newFirstLet=new NewFirstLet(newCollectionPath,subForClause);
		NewSecondLet newSecondLet=new NewSecondLet(pivotVar,oldReturn.getSubsequencePart(),OldLetReturn.changePivotLocation(pivotVar, pKIncollectionPath, oldLet.getOldLetReturn().getInputContent()));
		NewReturn newReturn=new NewReturn(withTotalCountOnFirstRow);
		NewStructureContainer newStructureContainer=new NewStructureContainer(newFirstLet,newSecondLet,newReturn);
		return newStructureContainer.gen();
	
	}
	
	public static String optimizeAllInOne(String lawXQL,boolean withTotalCountOnFirstRow) {
		String transformedXQL=lawXQL;
		
		
		try {
			QueryOptimizer queryOptimizer=new QueryOptimizer(lawXQL,withTotalCountOnFirstRow);
			queryOptimizer.parse();
			String getXQL=queryOptimizer.optimize();
			if(getXQL!=null)transformedXQL=getXQL;
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(QueryOptimizer.class).error("Error occured during optimize XQL: "+e.getLocalizedMessage());
			e.printStackTrace();
		}
		
		org.apache.log4j.Logger.getLogger(QueryOptimizer.class).info("Optimized XQL: "+transformedXQL);
		
    	return transformedXQL;
	}
	
    public static void main(String[] args) {
    	StringBuffer testInput=new StringBuffer();
    	testInput.append("let $_leres_ := \n")
    			 .append("for $pivot0 in collection(\"/SmallBox\")//p/Customer[matches(., '', 'i')]\n")
    					.append(" return <result>{if ($pivot0) then $pivot0 else <Customer/>}{if ($pivot0/../../i) then $pivot0/../../i else <i/>}</result>\n")
    			 .append(" return subsequence($_leres_,1,100)\n");
//    	testInput.append(" let $_leres_ := ")
//	    	         .append("for $pivot0 in collection(\"/HugeBox\")//p/PurchaseOrder/PurchaseOrderId ")
//	    	         .append("where $pivot0 eq \"1\" and $pivot0/../Customer eq \"Hannaford Bros. Co.\" ")
//	    	         .append("order by $pivot0/../Date descending ")
//	    	         .append("return <result>{if ($pivot0) then $pivot0 else <PurchaseOrderId/>}{if ($pivot0/../Customer) then $pivot0/../Customer else <Customer/>}{if ($pivot0/../Date) then $pivot0/../Date else <Date/>}{if ($pivot0/../Shipper) then $pivot0/../Shipper else <Shipper/>}</result> ")
//                 .append("return insert-before(subsequence($_leres_,1,20),0,<totalCount>{count($_leres_)}</totalCount>)");
 
    	QueryOptimizer queryOptimizer=new QueryOptimizer(testInput.toString(),true);
    	queryOptimizer.parse();
    	String getXQL=queryOptimizer.optimize();
    	if(getXQL!=null)System.out.println(getXQL);
	}

}
