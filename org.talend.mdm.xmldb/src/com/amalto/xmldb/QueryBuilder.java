package com.amalto.xmldb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

import com.amalto.commons.core.utils.XPathUtils;
import com.amalto.commons.core.utils.xpath.ri.Compiler;
import com.amalto.commons.core.utils.xpath.ri.compiler.Expression;
import com.amalto.commons.core.utils.xpath.ri.compiler.NodeNameTest;
import com.amalto.commons.core.utils.xpath.ri.compiler.Path;
import com.amalto.commons.core.utils.xpath.ri.compiler.Step;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereLogicOperator;
import com.amalto.xmlserver.interfaces.XmlServerException;


/**
 * An XML DB Implementation of the wrapper that works with eXist Open
 *
 * @author Bruno Grieder
 */
public class QueryBuilder {


	/**
	 * Builds the xQuery Return statement
	 * @param viewableFullPaths
	 * @param pivotsMap
	 * @return
	 * @throws XmlServerException
	 */
	private static String getXQueryReturn(
		ArrayList<String> viewableFullPaths,
		LinkedHashMap<String,String> pivotsMap,
		boolean totalCountOnfirstRow
	)throws XmlServerException {

		int i=0;
    	boolean moreThanOneViewable = viewableFullPaths.size()>1;
    	String xqReturn = moreThanOneViewable || totalCountOnfirstRow ? "<result>" : "";

    	for (Iterator<String> iter = viewableFullPaths.iterator(); iter.hasNext(); ) {
			String bename = iter.next();
			//remove leading slashes
			if (bename.startsWith("/")) bename = bename.substring(1);
			//compile the path
			Expression viewablePath = XPathUtils.compileXPath(bename);
			//factor the path
			Expression factoredPath = XPathUtils.factorExpression(viewablePath, pivotsMap, true, true);


			xqReturn+=(moreThanOneViewable || totalCountOnfirstRow ? "{" :"");

			if (viewablePath instanceof Path) {
				//determine last Element Name (Step NodeTest) type and name
				Step lastStep = ((Path)viewablePath).getSteps()[((Path)viewablePath).getSteps().length-1];
				if (lastStep.getNodeTest() instanceof NodeNameTest) {
					String lastElementName = lastStep.getNodeTest().toString();
    				if (lastStep.getAxis() == Compiler.AXIS_ATTRIBUTE) {
    					xqReturn+= "<"+lastElementName+">{string("+factoredPath+ ")}</"+lastElementName+">";
    				} else {
    					xqReturn+="if ("+factoredPath+") then "+factoredPath+" else <"+lastElementName+"/>";
    				}
				} else {
					// /text() or /position(), etc....
					if (moreThanOneViewable) {
						//create an element
						xqReturn+="<viewable"+i+">{"+factoredPath+"}</viewable"+(i++)+">";
					} else {
						//return the expression as such
						xqReturn+=factoredPath;
					}
				}
			} else {
				//Constant, Variable Reference or Operation
				if (moreThanOneViewable) {
					//create an element
					xqReturn+="<viewable"+i+">{"+factoredPath+"}</viewable"+(i++)+">";
				} else {
					//return the expression as such
					xqReturn+=factoredPath;
				}
			}

			xqReturn+=(moreThanOneViewable || totalCountOnfirstRow ? "}" :"");
    	}

    	xqReturn += moreThanOneViewable || totalCountOnfirstRow ? "</result>" : "";

    	return xqReturn;
	}


	/**
	 * Builds the xQuery Return statement for an Items Query
	 * @param viewableFullPaths
	 * @param pivotsMap
	 * @return
	 * @throws XmlServerException
	 */
	private static String getXQueryFor(
		boolean isItemQuery,
    	LinkedHashMap<String, String> rootElementNamesToRevisionID,
    	LinkedHashMap<String, String> rootElementNamesToClusterName,
		LinkedHashMap<String,String> pivotsMap
	)throws XmlServerException {

		String xqFor = "" ;
		//build for
    	for (Iterator<String> iter = pivotsMap.keySet().iterator(); iter.hasNext(); ) {
			String pivotName = iter.next();
			//get the path for this pivot
			String path = pivotsMap.get(pivotName);
			//get the concept
			String rootElementName = getRootElementNameFromPath(path);
			//determine revision
			String revisionID = null;
			if (isItemQuery) {
    			Set<String> patterns = rootElementNamesToRevisionID.keySet();
    			for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
    				String pattern = iterator.next();
    				if (rootElementName.matches(pattern)) {
    					revisionID = rootElementNamesToRevisionID.get(pattern);
    					break;
    				}
    			}
			} else {
				//object name, not a pattern --> direct match
				revisionID = rootElementNamesToRevisionID.get(rootElementName);
			}
			//determine cluster
			String clusterName = null;
			if (isItemQuery) {
				Set<String> patterns = rootElementNamesToClusterName.keySet();
				for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
					String pattern = iterator.next();
					if (rootElementName.matches(pattern)) {
						clusterName = rootElementNamesToClusterName.get(pattern);
						break;
					}
				}
			} else {
				//object name, not a pattern --> direct match
				clusterName = rootElementNamesToClusterName.get(rootElementName);
			}

			xqFor+="".equals(xqFor)?"for ": ", ";
			xqFor+=pivotName+" in "+getXQueryCollectionName(revisionID, clusterName)+"/"+(isItemQuery ? "ii/p/" : "")+path;
    	}

    	return xqFor;
	}

	/**
	 * Build the Query Where clause
	 * @param where
	 * @param pivots
	 * @param whereItem
	 * @return
	 * @throws XmlServerException
	 */
	public static String buildWhere(
			String where,
			LinkedHashMap<String,String> pivots,
			IWhereItem whereItem
		) throws XmlServerException{
		try {
			if (whereItem instanceof WhereLogicOperator) {
				Collection<IWhereItem> subItems = ((WhereLogicOperator)whereItem).getItems();
				if (subItems.size() == 0) throw new XmlServerException("The logic operator must contain at least one element");
				if (subItems.size() == 1) return  //unnecessary AND or OR
					buildWhere(
						where,
						pivots,
						subItems.iterator().next()
				);
				int i=0;
				for (Iterator<IWhereItem> iter = subItems.iterator(); iter.hasNext(); ) {
					IWhereItem item = iter.next();
					if (++i>1)
						if (((WhereLogicOperator)whereItem).getType() == WhereLogicOperator.AND){
							where=where.length()==0?where:where+" and ";					
						}
						else{
							where=where.length()==0?where:where+" or ";							
						}
					else
						where+="";
					String strwhere=buildWhere(where, pivots, item);
					where=strwhere.length()==0?"":strwhere;
				}//for
				return where;

			} else if(whereItem instanceof WhereCondition) {
				WhereCondition condition = (WhereCondition) whereItem;
				where+=buildWhereCondition(condition,pivots);
	            return where;
			} else {
				throw new XmlServerException("Unknown Where Type : "+whereItem.getClass().getName());
			}
	    } catch (Exception e) {
     	    String err = "Unable to build the XQuery Where Clause "
     	    		+": "+e.getLocalizedMessage();
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+QueryBuilder.class).info(err,e);
     	    throw new XmlServerException(err);
	    }
	}
	
	public static String buildContains(String factorPivots, String encoded){
		if("*".equals(encoded) || ".*".equals(encoded)){				
			return "matches("+factorPivots+" , \".*\", \"i\") "+		
				"or (empty("+factorPivots+"/text())) ";
		}else{
			//case insensitive aiming added
			return "matches("+factorPivots+" , \""+encoded+"\",\"i\") ";									
		}
	}
	/**
	 * Build a where condition in XQuery using paths relative to the provided list of pivots
	 */
	private static String buildWhereCondition(WhereCondition wc, LinkedHashMap<String,String> pivots) throws XmlServerException{
		try {

			//all this is EXIST specific

			String where = "";
			String operator = wc.getOperator();

			//Parse (Right) Value argument,
			//detect if it is a numeric
			//and encode it to XML

			//The encoded argument
			String encoded = null;
			//numeric detection
			boolean isNum = false;
			if (wc.getRightValueOrPath() != null) {
    			//handle case of String starting with a zero e.g. 00441065 or ending with . e.g. 12345.
    			if (!(
    					wc.getRightValueOrPath().matches(".*\\D")
    					|| wc.getRightValueOrPath().startsWith("0")
    					|| wc.getRightValueOrPath().endsWith(".")
    					|| wc.getRightValueOrPath().startsWith("+")
    					|| wc.getRightValueOrPath().startsWith("-")
    			)){
    				try {
    					Double.parseDouble(wc.getRightValueOrPath().trim());
    					isNum = true;
    				} catch (Exception e) {}
    			}

    			//String encoded = wc.getRightValueOrPath().replaceAll("\\&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    			encoded = StringEscapeUtils.escapeXml(wc.getRightValueOrPath());
    			//aiming modify convert "" & " " to *
    			if(encoded!=null && encoded.trim().length()==0){
    				encoded="*";
    			}
    			//change * to .*
    			encoded=encoded.replaceAll("\\.\\*|\\*", "\\.\\*");
			}
			if(".*".equals(encoded)) return "";
			String factorPivots=XPathUtils.factor(wc.getLeftPath(), pivots)+"";
			//case insensitive aiming added			
			//encoded=encoded.toLowerCase();
			//end
			if(operator.equals(WhereCondition.CONTAINS)) {
				String predicate = wc.getStringPredicate();
				//check if the left path is an attribute or an element
				String path = wc.getLeftPath();
				if (path.endsWith("/")) path = path.substring(0, wc.getLeftPath().length()-1);
				String[] nodes = path.split("/");
				boolean isAttribute = nodes[nodes.length-1].startsWith("@");
				if ((predicate==null) || predicate.equals(WhereCondition.PRE_NONE)) {
					if (isAttribute) {
						where =
							" matches("+factorPivots+" , \""+encoded+"\",\"i\") ";//factorPivots+" &= \""+encoded+"\" ";
					} else {
						where =buildContains(factorPivots, encoded);
//							"("+factorPivots+"/descendant-or-self::* &= \""+encoded+"\") "+						
//							"or ("+factorPivots+"/descendant-or-self::*/attribute() &= \""+encoded+"\") ";
					}
				} else	 if (predicate.equals(WhereCondition.PRE_AND)) {
					if (isAttribute) {
						where =
							" matches("+factorPivots+" , \""+encoded+"\",\"i\") ";//factorPivots+" &= \""+encoded+"\" ";
					} else {
						where =buildContains(factorPivots, encoded);
//							"("+factorPivots+"/descendant-or-self::* &= \""+encoded+"\") "+
//							"or ("+factorPivots+"/descendant-or-self::*/attribute() &= \""+encoded+"\") ";
					}
				} else if (predicate.equals(WhereCondition.PRE_EXACTLY)) {
					where = factorPivots+" eq \""+encoded+"\"";
				} else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
					//where = "near("+factorPivots+", \""+encoded+"\",1)";
					where = "matches("+factorPivots+", \""+encoded+"\",\"i\") ";
				} else	if (predicate.equals(WhereCondition.PRE_OR)) {
					if (isAttribute) {
						where =
							" matches("+factorPivots+" , \""+encoded+"\",\"i\") ";
					} else {
						where =
							" matches("+factorPivots+" , \""+encoded+"\",\"i\") ";
							
					}
				} else	if (predicate.equals(WhereCondition.PRE_NOT)) {
					if (isAttribute) {
						where =
							"not matches("+factorPivots+" , \""+encoded+"\",\"i\") ";
					} else {
						where =
							"not("+
								" matches("+factorPivots+" , \""+encoded+"\",\"i\") "+
								//"or matches("+factorPivots+"/descendant-or-self::*/attribute() , \""+encoded+"\") "+
							")";
					}
				}
				/* WAITING FOR FIX FROM EXIST
				if ((predicate==null) || predicate.equals(WhereCondition.PRE_NONE))
					where = "contains("+factorPivots+",\""+encoded+"\")";
				else	 if (predicate.equals(WhereCondition.PRE_AND))
					where = "contains("+factorPivots+",\""+encoded+"\")";
				else if (predicate.equals(WhereCondition.PRE_EXACTLY))
					where = factorPivots+" eq \""+encoded+"\"";
				else if (predicate.equals(WhereCondition.PRE_STRICTAND))
					where = "near("+factorPivots+", \""+encoded+"\",1)";
				else	if (predicate.equals(WhereCondition.PRE_OR))
					where = factorPivots+" |= \""+encoded+"\"";
				else	if (predicate.equals(WhereCondition.PRE_NOT))
					where = "not(contains("+factorPivots+",\""+encoded+"\"))";
				*/

			} else if(operator.equals(WhereCondition.FULLTEXTSEARCH)) {
				//where = "near("+factorPivots+", \""+encoded+"\",1)";
				where = "ft:query(..,\""+StringEscapeUtils.escapeXml(wc.getRightValueOrPath().trim())+"\")";
			} else if(operator.equals(WhereCondition.STRICTCONTAINS)) {
				//where = "near("+factorPivots+", \""+encoded+"\",1)";
				where = "matches("+factorPivots+", \""+encoded+"\",\"i\") ";
			} else if(operator.equals(WhereCondition.STARTSWITH)) {
				//where = "near("+factorPivots+", \""+encoded+"*\",1)";
				where = "matches("+factorPivots+", \""+encoded+".*\" ,\"i\") ";
			} else if(operator.equals(WhereCondition.JOINS)) {
				//where = XPathUtils.factor(wc.getRightValueOrPath(),pivots)+" = "+factorPivots; //Join error aiming added
				where = "matches("+factorPivots+", \""+encoded+"\",\"i\") ";
			} else	 if(operator.equals(WhereCondition.EQUALS)) {
				if (isNum) {
					where = "number("+factorPivots+") eq "+encoded;
				} else  {
					where = factorPivots+" eq \""+encoded+"\"";
				}
			} else if(operator.equals(WhereCondition.NOT_EQUALS)) {
				if (isNum) {
					where = "number("+factorPivots+") ne "+encoded;
				} else {
					where = factorPivots+" ne \""+encoded+"\"";
				}
			} else	 if(operator.equals(WhereCondition.GREATER_THAN)) {
				if (isNum) {
					where = "number("+factorPivots+") gt "+encoded;
				} else {
					where = factorPivots+" gt \""+encoded+"\"";
				}
			} else	if(operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) {
				if (isNum) {
					where = "number("+factorPivots+") ge "+encoded;
				} else {
					where = factorPivots+" ge \""+encoded+"\"";
				}
			} else if(operator.equals(WhereCondition.LOWER_THAN)) {
				if (isNum) {
					where = "number("+factorPivots+") lt "+encoded;
				} else {
					where = factorPivots+" lt \""+encoded+"\"";
				}
			} else	if(operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) {
				if (isNum) {
					where = "number("+factorPivots+") le "+encoded;
				} else {
					where = factorPivots+" le \""+encoded+"\"";
				}
			} else	if(operator.equals(WhereCondition.NO_OPERATOR)) {
				where = factorPivots.toString();
			}

			return where;

	    } catch (Exception e) {
     	    String err = "Unable to build the Where Condition "
     	    		+": "+e.getLocalizedMessage();
     	    org.apache.log4j.Logger.getLogger(QueryBuilder.class).info(err,e);
     	    throw new XmlServerException(err);
	    }

	}

	/**
	 * Builds an XQuery
	 * @param isItemQuery
	 * @param objectRootElementNamesToRevisionID
	 * @param objectRootElementNamesToClusterName
	 * @param forceMainPivot
	 * @param viewableFullPaths
	 * @param whereItem
	 * @param orderBy
	 * @param direction
	 * @param start
	 * @param limit
	 * @return
	 * @throws XmlServerException
	 */
    public static String getQuery(
    	boolean isItemQuery,
    	LinkedHashMap<String, String> objectRootElementNamesToRevisionID,
    	LinkedHashMap<String, String> objectRootElementNamesToClusterName,
    	String forceMainPivot,
    	ArrayList<String> viewableFullPaths,
    	IWhereItem whereItem,
    	String orderBy,
    	String direction,
    	int start,
    	long limit,
    	boolean withTotalCountOnFirstRow
    ) throws XmlServerException {

    	try {

	    	String xqWhere="";
	    	String xqOrderBy = "";
    		//build Pivots Map
    		LinkedHashMap<String,String> pivotsMap = new LinkedHashMap<String,String>();
			if (forceMainPivot != null) pivotsMap.put("$pivot0", forceMainPivot);

			//build return statement
			String xqReturn = getXQueryReturn(viewableFullPaths, pivotsMap, withTotalCountOnFirstRow);

	    	// 	build from  WhereItem
	    	if (whereItem == null)
	    		xqWhere="";
	    	else
	    		xqWhere = buildWhere("", pivotsMap, whereItem);

	    	//build order by
	    	if (orderBy == null) {
	    		xqOrderBy = "";
	    	} else {
	    		xqOrderBy = "order by "
	    					+XPathUtils.factor(orderBy, pivotsMap)
	    					+(direction == null ? "" : " "+direction);
	    	}

	    	//Get For
	    	String xqFor = getXQueryFor(
	    		isItemQuery,
	    		objectRootElementNamesToRevisionID,
	    		objectRootElementNamesToClusterName,
	    		pivotsMap
	    	);

	    	String rawQuery =
	    		xqFor
	    		+("".equals(xqWhere)? "" : "\nwhere "+xqWhere)
	    		+("".equals(xqOrderBy) ? "" : "\n"+xqOrderBy)
	    		+"\nreturn "+xqReturn;

	    	//Determine Query based on number of results an counts
	    	String query = null;
	    	boolean subsequence = (start>=0 && limit>=0 && limit!=Integer.MAX_VALUE);
	    	if (subsequence) {
	    		if (withTotalCountOnFirstRow) {
		    		query =
		    			"let $_leres_ := \n"+rawQuery
		    			+"\n return insert-before(subsequence($_leres_,"+(start+1)+","+limit+"),0,<totalCount>{count($_leres_)}</totalCount>)";
	    		} else {
    	    		query =
    	    			"let $_leres_ := \n"+rawQuery
    	    			+"\n return subsequence($_leres_,"+(start+1)+","+limit+")";
	    		}
	    	} else {
	    		if (withTotalCountOnFirstRow) {
		    		query =
		    			"let $_leres_ := \n"+rawQuery
		    			+"\n return insert-before($_leres_,0,<totalCount>{count($_leres_)}</totalCount>)";
	    		} else {
	    			query = rawQuery;
	    		}
	    	}
	    	System.out.println("query:\n");
	    	System.out.println(query);
	    	return query;

    	} catch (XmlServerException e) {
    		throw(e);
	    } catch (Exception e) {
     	    String err = "Unable to build the Item XQuery";
     	    org.apache.log4j.Logger.getLogger("INFO SYSTRACE "+QueryBuilder.class).info(err,e);
     	    throw new XmlServerException(err);
	    }
    }


	/***********************************************************************
	 *
	 * Helper Methods
	 *
	 ***********************************************************************/

	/**
	 * Determine the collection name based on the revision ID and Cluster Name
	 */
	public static String getXQueryCollectionName(String revisionID, String clusterName) throws XmlServerException {
		if(revisionID!=null) revisionID=revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
		String collectionPath =
       		(revisionID == null || "".equals(revisionID) ? "" : "R-"+revisionID+"/")
       		+(clusterName == null ? "" : clusterName);

       	if ("".equals(collectionPath)) return "";

       	String encoded = null;
           try {
    	        encoded = URLEncoder.encode(collectionPath,"utf-8");
           } catch (UnsupportedEncodingException unlikely) {
    	        String err = "Unable to encode the collection path '"+collectionPath+"'. UTF-8 is not suported ?!?!";
    	        throw new XmlServerException(err);
           }
       	// java.net.URLEncoder encodes space (' ') as a plus sign ('+'),
       	// instead of %20 thus it will not be decoded properly by eXist when the
       	// request is parsed. Therefore replace all '+' by '%20'.
       	// If there would have been any plus signs in the original string, they would
       	// have been encoded by URLEncoder.encode()
       	// control = control.replace("+", "%20");//only works with JDK 1.5
       	encoded = encoded.replaceAll("\\+", "%20");
        //%2F seems to be useless
    	encoded = encoded.replaceAll("%2F", "/");

       	return "collection(\""+encoded+"\")";
	}

	private static Pattern pathWithoutConditions = Pattern.compile("(.*?)[\\[|/].*");
	/**
	 * Returns the first part - eg. the concept - from the path
	 * @param path
	 * @return the Concept
	 */
    public static String getRootElementNameFromPath(String path) {
    	if (!path.endsWith("/")) path+="/";
    	Matcher m = pathWithoutConditions.matcher(path);
    	if (m.matches()) {
    		return m.group(1);
    	} else {
    		return null;
    	}
    }


}