package com.amalto.xmldb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereOr;

public class PartialXQLPackage {
	
	    private static final Pattern pPattern=Pattern.compile("\\$pivot\\d+");
    	
    	private LinkedHashMap<String,String> forInCollectionMap=null;
    	
    	private Map<String,String> pivotWhereMap=null;
    	
    	private String xqWhere;
    	
    	private boolean useSubsequenceFirst;
    	
    	private String xqOrderBy;   	
    	
    	private List<String> joinKeys=null;
    	
    	private boolean useJoin;
    	
    	public PartialXQLPackage() {
    		forInCollectionMap=new LinkedHashMap<String, String>();
    		joinKeys=new ArrayList<String>();
    		useSubsequenceFirst=false;
    		useJoin=false;
		}
    	
    	public void addForInCollection(String pivotName,String collectionXpathExpr) {
			
    		this.forInCollectionMap.put(pivotName, collectionXpathExpr);

		}

		public LinkedHashMap<String, String> getForInCollectionMap() {
			return forInCollectionMap;
		}
		
		public void resetPivotWhereMap() {
			
			pivotWhereMap = getPivotWhereMap(xqWhere);
			
			if(pivotWhereMap.size()==0) {
				boolean usingFTSearch=false;
				if(xqWhere!=null&&xqWhere.trim().toLowerCase().startsWith("ft:"))usingFTSearch=true;
				if(usingFTSearch) {
					pivotWhereMap.put("$pivot0", xqWhere.trim());
				}
			}else if(pivotWhereMap.size()==1) {
				String replacedXQWhere=xqWhere.replaceAll("\\$pivot\\d+/", "");
				String pivotName=getPivotName(xqWhere);
				pivotWhereMap.put(pivotName, replacedXQWhere);
			}else if(pivotWhereMap.size()>1) {
				String[] whereItems=xqWhere.split("and");//FIXME:only support and under mix mode
				for (int i = 0; i < whereItems.length; i++) {				
					String whereItem=whereItems[i].trim();
					if(whereItem.indexOf(" "+WhereCondition.JOINS+ " ")!=-1) {
						joinKeys.add(whereItem);
					}else {
						String pivotName=getPivotName(whereItem);
						String replacedWhereItem=whereItem.replaceAll("\\$pivot\\d+/", "");
						//remove unecessary brakets
						
						replacedWhereItem=replacedWhereItem.replaceAll("(\\s*\\(+)(\\w+\\(.*?\\))(\\s*\\)+)", "$2");
						if(pivotWhereMap.get(pivotName)==null) {
							pivotWhereMap.put(pivotName, replacedWhereItem);
						}else {
							pivotWhereMap.put(pivotName, pivotWhereMap.get(pivotName)+" and "+replacedWhereItem);
						}
					}
				}
				
			}
			//System.out.println(pivotWhereMap);
			
		}

		public List<String> getJoinKeys() {
			return joinKeys;
		}

		public Map<String, String> getPivotWhereMap() {
			return pivotWhereMap;
		}

		public String getXqWhere() {
			return xqWhere;
		}

		public void setXqWhere(String xqWhere) {
			this.xqWhere = xqWhere;
		}

		private Map<String,String> getPivotWhereMap(String xqWhere) {
			Map<String,String> pivotWhereMap=new HashMap<String,String>();
			Matcher m = pPattern.matcher(xqWhere);
			while (m.find()) {
				pivotWhereMap.put(m.group(), null);      
			}
			return pivotWhereMap;
		}
		
		private String getPivotName(String input) {
			String pivotName=null;
			if(input==null||input.length()==0)return pivotName;
			Matcher m = pPattern.matcher(input);
			while (m.find()) {
				pivotName=m.group(0);
				break;
			}
			return pivotName;
		}

		public boolean isUseSubsequenceFirst() {
			return useSubsequenceFirst;
		}

		public void setUseSubsequenceFirst(boolean useSubsequenceFirst) {
			this.useSubsequenceFirst = useSubsequenceFirst;
		}

		public String getXqOrderBy() {
			return xqOrderBy;
		}

		public void setXqOrderBy(String xqOrderBy) {
			this.xqOrderBy = xqOrderBy;
		}

		public boolean isUseGlobalOrderBy() {
			if(forInCollectionMap.size()==1&&xqOrderBy!=null&&xqOrderBy.length()>0)return true;
			else return false;
		}
		
		
		public boolean isUseJoin() {
			return useJoin;
		}

		public void setUseJoin(boolean useJoin) {
			this.useJoin = useJoin;
		}

		public String genOrderByWithFirstExpr(String expr) {
			
			xqOrderBy=xqOrderBy.replace("$pivot0", "$r");
			
			StringBuffer sb=new StringBuffer();
			sb.append(" for $r in ")
			  .append(expr).append(" ")
			  .append(xqOrderBy)
			  .append(" return $r ");
			
			return sb.toString();
		}
    	
}
