package com.amalto.xmldb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.CommonUtil;

import com.amalto.commons.core.utils.XPathUtils;
import com.amalto.commons.core.utils.xpath.ri.Compiler;
import com.amalto.commons.core.utils.xpath.ri.compiler.Expression;
import com.amalto.commons.core.utils.xpath.ri.compiler.NodeNameTest;
import com.amalto.commons.core.utils.xpath.ri.compiler.Path;
import com.amalto.commons.core.utils.xpath.ri.compiler.Step;
import com.amalto.xmldb.util.PartialXQLPackage;
import com.amalto.xmldb.util.QueryBuilderContext;
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

    private static final Logger LOG = Logger.getLogger(QueryBuilder.class);
	/**
	 * Builds the xQuery Return statement
	 * @param viewableFullPaths
	 * @param pivotsMap
	 * @return
	 * @throws XmlServerException
	 */
	protected static String getXQueryReturn(
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
			//factor the root path
			factorFirstPivotInMap(pivotsMap, viewablePath.toString());
			//factor the path
			Expression factoredPath = XPathUtils.factorExpression(viewablePath, pivotsMap, true, true);


			xqReturn+=(moreThanOneViewable || totalCountOnfirstRow ? "{" :"");

			if (viewablePath instanceof Path) {
				//determine last Element Name (Step NodeTest) type and name
				Step lastStep = ((Path)viewablePath).getSteps()[((Path)viewablePath).getSteps().length-1];
				if (lastStep.getNodeTest() instanceof NodeNameTest) {
					String lastElementName = lastStep.getNodeTest().toString();
					//hshu modified,because Mantis interprets the 'i' tag as a text formatting (italic)
					if(lastElementName!=null&&lastElementName.equals("i"))lastElementName="xi";
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

	private static void factorFirstPivotInMap(LinkedHashMap<String, String> pivotsMap, String viewablePath) {
		if(viewablePath!=null&&viewablePath.trim().length()>0) {
			if (viewablePath.startsWith("/")) viewablePath = viewablePath.substring(1);
			String thisRootElementName = getRootElementNameFromPath(viewablePath.toString());
			if(!thisRootElementName.equals("")&&!pivotsMap.containsValue(thisRootElementName))XPathUtils.factorExpression(XPathUtils.compileXPath(thisRootElementName), pivotsMap, true, true);
		}
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
		LinkedHashMap<String,String> pivotsMap,
		PartialXQLPackage partialXQLPackage,
		QueryBuilderContext queryBuilderContext
	)throws XmlServerException {

		String xqFor = "" ;
		//build for
		int i=0;
    	for (Iterator<String> iter = pivotsMap.keySet().iterator(); iter.hasNext();i++ ) {
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
			String xQueryCollectionName=getXQueryCollectionName(revisionID, clusterName)+"/"+(isItemQuery ? "/p/" : "");
			//xqFor+=pivotName+" in "+xQueryCollectionName+path;
			//FIXME:subsequence is not support for multi-pivots
			if(pivotsMap.size()==1)
			    xqFor+=pivotName+" in "+"subsequence($_leres"+i+"_,"+(queryBuilderContext.getStart()+1)+","+queryBuilderContext.getLimit()+")";
			else 
				xqFor+=pivotName+" in "+"$_leres"+i+"_";	
			partialXQLPackage.addForInCollection(pivotName, xQueryCollectionName+path);
    	}
    	
    	if(pivotsMap.size()==1)partialXQLPackage.setUseSubsequenceFirst(true);

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
			IWhereItem whereItem,
			Map<String, ArrayList<String>> metaDataTypes
		) throws XmlServerException{
		try {
			if (whereItem instanceof WhereLogicOperator) {
				Collection<IWhereItem> subItems = ((WhereLogicOperator)whereItem).getItems();
				if (subItems.size() == 0) throw new XmlServerException("The logic operator must contain at least one element");
				if (subItems.size() == 1) return  //unnecessary AND or OR
					buildWhere(
						where,
						pivots,
						subItems.iterator().next(),
						metaDataTypes
				);
				int i=0;
				for (Iterator<IWhereItem> iter = subItems.iterator(); iter.hasNext(); ) {
					IWhereItem item = iter.next();	
					if (++i>1)
					   if(item instanceof WhereCondition) {
                         if(WhereCondition.PRE_OR.equals(((WhereCondition)item).getStringPredicate())) {
                            where = where + " or ("; 
                         }
                         else {
                            where = where + " and (";
                         }
                      }else					   
                          if (((WhereLogicOperator)whereItem).getType() == WhereLogicOperator.AND)
                              where+=" and (";
                          else
                              where+=" or (";
                   else
                       where+="(";
                   where = buildWhere(where, pivots, item,metaDataTypes)+")";					
				}//for
				return where;

			} else if(whereItem instanceof WhereCondition) {
				WhereCondition condition = (WhereCondition) whereItem;
				where+=buildWhereCondition(condition,pivots,metaDataTypes);
	            return where;
			} else {
				throw new XmlServerException("Unknown Where Type : "+whereItem.getClass().getName());
			}
	    } catch (Exception e) {
     	    String err = "Unable to build the XQuery Where Clause "
     	    		+": "+e.getLocalizedMessage();
     	    LOG.error(err,e);
     	    throw new XmlServerException(err);
	    }
	}
	
	public static String buildContains(String factorPivots, String encoded, boolean isFunction){
		if("*".equals(encoded) || ".*".equals(encoded)){				
			return "matches("+factorPivots+" , \".*\", \"i\") "+		
				"or (empty("+factorPivots+"/text())) ";
		}
		else if(isFunction) {
		   return "contains("+factorPivots+" , "+encoded+") ";
		}
		else{
			//case insensitive aiming added
			return "matches("+factorPivots+" , \""+encoded+"\",\"i\") ";									
		}
	}
	/**
	 * Build a where condition in XQuery using paths relative to the provided list of pivots
	 */
	public static String buildWhereCondition(WhereCondition wc, LinkedHashMap<String,String> pivots,Map<String, ArrayList<String>> metaDataTypes) throws XmlServerException{
		try {

			// all this is EXIST specific

			String where = "";
			String operator = wc.getOperator();

			// Parse (Right) Value argument,
			// detect if it is a numeric
			// and encode it to XML

			// The encoded argument
			String encoded = null;
			// numeric detection
			boolean isLeftPathNum = false;
			boolean isRightValueNum = false;
			boolean isXpathFunction = false;
			boolean isNum=false;
			if (wc.getRightValueOrPath() != null) {
				isXpathFunction = isValidatedFunction(wc.getRightValueOrPath()
						.trim());
				// handle case of String starting with a zero e.g. 00441065 or
				// ending with . e.g. 12345.
				if (!(wc.getRightValueOrPath().matches(".*\\D")
						|| wc.getRightValueOrPath().startsWith("0")
						|| wc.getRightValueOrPath().endsWith(".")
						|| wc.getRightValueOrPath().startsWith("+") || wc
						.getRightValueOrPath().startsWith("-"))) {
					try {
						Double.parseDouble(wc.getRightValueOrPath().trim());
						isRightValueNum = true;
					} catch (Exception e) {
					}
				}
				// TODO {Country/isoCode=[xsd:integer],
				
				if (null != metaDataTypes) {
					String leftPath = wc.getLeftPath();
					String type = metaDataTypes.get(leftPath).get(0);
					if (type.indexOf("xsd:double") >= 0
							|| type.indexOf("xsd:float") >= 0
							|| type.indexOf("xsd:integer") >= 0
							|| type.indexOf("xsd:decimal") >= 0
							|| type.indexOf("xsd:byte") >= 0
							|| type.indexOf("xsd:int") >= 0
							|| type.indexOf("xsd:long") >= 0
							|| type.indexOf("xsd:negativeInteger") >= 0
							|| type.indexOf("xsd:nonNegativeInteger") >= 0
							|| type.indexOf("xsd:nonPositiveInteger") >= 0
							|| type.indexOf("xsd:positiveInteger") >= 0
							|| type.indexOf("xsd:short") >= 0
							|| type.indexOf("xsd:unsignedLong") >= 0
							|| type.indexOf("xsd:unsignedInt") >= 0
							|| type.indexOf("xsd:unsignedShort") >= 0
							|| type.indexOf("xsd:unsignedByte") >= 0)
					{
						isLeftPathNum = true;	
					}
					

				}
				isNum=isLeftPathNum && isRightValueNum;

				encoded = isXpathFunction ? wc.getRightValueOrPath().trim()
						: StringEscapeUtils.escapeXml(wc.getRightValueOrPath());
				// aiming modify convert "" & " " to *
				if (encoded != null && encoded.trim().length() == 0) {
					encoded = "*";
				}
				// change * to .*
				encoded = encoded.replaceAll("\\.\\*|\\*", "\\.\\*");
			}
			if (".*".equals(encoded))
				return "";
			factorFirstPivotInMap(pivots, wc.getLeftPath());
			String factorPivots = XPathUtils.factor(wc.getLeftPath(), pivots)
					+ "";
			//see 0015004, if rightPath contains '/', we consider it as xpathFunction
			if(wc.getRightValueOrPath()!=null && wc.getRightValueOrPath().contains("/")) {
				encoded=XPathUtils.factor(wc.getRightValueOrPath(), pivots)+"";
				isXpathFunction=true;
			}
			
			if (operator.equals(WhereCondition.CONTAINS)) {
				String predicate = wc.getStringPredicate();
				// check if the left path is an attribute or an element
				String path = wc.getLeftPath();
				if (path.endsWith("/"))
					path = path.substring(0, wc.getLeftPath().length() - 1);
				String[] nodes = path.split("/");
				boolean isAttribute = nodes[nodes.length - 1].startsWith("@");
				if ((predicate == null)
						|| predicate.equals(WhereCondition.PRE_NONE)) {
					if (isAttribute) {
						where = " matches(" + factorPivots + " , \"" + encoded
								+ "\",\"i\") ";// factorPivots+" &= \""+encoded+"\" ";
					} else {
						where = buildContains(factorPivots, encoded,
								isXpathFunction);
					}
				} else if (predicate.equals(WhereCondition.PRE_AND)) {
					if (isAttribute) {
						where = " matches(" + factorPivots + " , \"" + encoded
								+ "\",\"i\") ";// factorPivots+" &= \""+encoded+"\" ";
					} else {
						where = buildContains(factorPivots, encoded,
								isXpathFunction);					
					}
				} else if (predicate.equals(WhereCondition.PRE_EXACTLY)) {
					if (isXpathFunction) {
						where = factorPivots + " eq " + encoded;
					} else {
						where = factorPivots + " eq \"" + encoded + "\"";
					}
				} else if (predicate.equals(WhereCondition.PRE_STRICTAND)) {
					// where = "near("+factorPivots+", \""+encoded+"\",1)";
					if (isXpathFunction) {
						where = "contains(" + factorPivots + ", " + encoded
								+ ") ";
					} else {
						where = "matches(" + factorPivots + ", \"" + encoded
								+ "\",\"i\") ";
					}
				} else if (predicate.equals(WhereCondition.PRE_OR)) {
					if (isAttribute) {
						where = " matches(" + factorPivots + " , \"" + encoded
								+ "\",\"i\") ";
					} else {
						if (isXpathFunction) {
							where = " contains(" + factorPivots + " , "
									+ encoded + ") ";
						} else {
							where = " matches(" + factorPivots + " , \""
									+ encoded + "\",\"i\") ";
						}
					}
				} else if (predicate.equals(WhereCondition.PRE_NOT)) {
					if (isAttribute) {
						where = "not matches(" + factorPivots + " , \""
								+ encoded + "\",\"i\") ";
					} else {
						if (isXpathFunction) {
							where = "not(" + " contains(" + factorPivots + " , "
									+ encoded + ") " + ")";
						} else {
							where = "not(" + " matches(" + factorPivots
									+ " , \"" + encoded + "\",\"i\") " +
									")";
						}
					}
				}

			} else if (operator.equals(WhereCondition.FULLTEXTSEARCH)) {
				// where = "near("+factorPivots+", \""+encoded+"\",1)";
				where = "ft:query(.,\""
						+ StringEscapeUtils.escapeXml(wc.getRightValueOrPath()
								.trim()) + "\")";
			} else if (operator.equals(WhereCondition.STRICTCONTAINS)) {
				// where = "near("+factorPivots+", \""+encoded+"\",1)";
				if (isXpathFunction) {
					where = "starts-with(" + factorPivots + ", " 
					+ encoded + ") ";
				}else {
					where = "matches(" + factorPivots + ", \"" + encoded
							+ "\",\"i\") ";
				}
			} else if (operator.equals(WhereCondition.STARTSWITH)) {
				// where = "near("+factorPivots+", \""+encoded+"*\",1)";
				if (isXpathFunction) {
					where = "starts-with(" + factorPivots + ", " 
							+ encoded + ") ";
				} else {
					where = "matches(" + factorPivots + ", \"" + encoded
							+ ".*\" ,\"i\") ";
				}
			} else if (operator.equals(WhereCondition.CONTAINS_TEXT_OF)) {

				//FIXME:ASSUME the pivots are the same?
				String factorRightPivot = XPathUtils.factor(encoded, pivots)+ ""; 
				where = "contains(" + factorPivots + ", " + factorRightPivot + "/text()) ";
	
			} else if (operator.equals(WhereCondition.JOINS)) {
	
				//FIXME:ASSUME the pivots are the same?
				String factorRightPivot = XPathUtils.factor(encoded, pivots)+ ""; 
				where =  factorPivots + " JOINS " + factorRightPivot ;
	
			}else if (operator.equals(WhereCondition.EQUALS)) {
				if (isNum) {
					where = "number(" + factorPivots + ") eq " + encoded;
				} else if (isXpathFunction) {
					where = factorPivots + "= " + encoded;
				} else {
					where = factorPivots + " eq \"" + encoded + "\"";
				}
			} else if (operator.equals(WhereCondition.NOT_EQUALS)) {
				if (isNum) {
					where = "number(" + factorPivots + ") ne " + encoded;
				} else if (isXpathFunction) {
					where = factorPivots + " != " + encoded;
				} else {
					where = factorPivots + " ne \"" + encoded + "\"";
				}
			} else if (operator.equals(WhereCondition.GREATER_THAN)) {
				if (isNum) {
					where = "number(" + factorPivots + ") gt " + encoded;
				} else if (isXpathFunction) {
					where = factorPivots + "> " + encoded;
				} else {
					where = factorPivots + " gt \"" + encoded + "\"";
				}
			} else if (operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) {
				if (isNum) {
					where = "number(" + factorPivots + ") ge " + encoded;
				} else if (isXpathFunction) {
					where = factorPivots + " >= " + encoded;
				} else {
					where = factorPivots + " ge \"" + encoded + "\"";
				}
			} else if (operator.equals(WhereCondition.LOWER_THAN)) {
				if (isNum) {
					where = "number(" + factorPivots + ") lt " + encoded;
				} else if (isXpathFunction) {
					where = factorPivots + " < " + encoded;
				} else {
					where = factorPivots + " lt \"" + encoded + "\"";
				}
			} else if (operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) {
				if (isNum) {
					where = "number(" + factorPivots + ") le " + encoded;
				} else if (isXpathFunction) {
					where = factorPivots + " <= " + encoded;
				} else {
					where = factorPivots + " le \"" + encoded + "\"";
				}
			} else if (operator.equals(WhereCondition.NO_OPERATOR)) {
				where = factorPivots.toString();
			}

			return where;

		} catch (Exception e) {
     	    String err = "Unable to build the Where Condition "
     	    		+": "+e.getLocalizedMessage();
     	    LOG.error(err,e);
     	    throw new XmlServerException(err);
	    }

	}

	/**
	 * check if is validated function.
	 */
	public static boolean isValidatedFunction(String value) {
      Pattern pattern = Pattern.compile("\\S+\\((\\S,*)*\\)$");
      Matcher matcher = pattern.matcher(value);
      return matcher.matches();
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
    	boolean withTotalCountOnFirstRow,
    	Map<String, ArrayList<String>> metaDataTypes
    ) throws XmlServerException {

    	try {

	    	String xqWhere = "";
	    	String xqOrderBy = "";
    		//build Pivots Map
    		LinkedHashMap<String,String> pivotsMap = new LinkedHashMap<String,String>();
			if (forceMainPivot != null) pivotsMap.put("$pivot0", forceMainPivot);

			if(start<0||limit<0||limit==Integer.MAX_VALUE) {
	    		start=0;
	    		limit=Integer.MAX_VALUE;	
	    	}
			PartialXQLPackage partialXQLPackage=new PartialXQLPackage();
			QueryBuilderContext queryBuilderContext=new QueryBuilderContext();
			queryBuilderContext.setStart(start);
			queryBuilderContext.setLimit(limit);
			//build return statement
			String xqReturn = getXQueryReturn(viewableFullPaths, pivotsMap, withTotalCountOnFirstRow);

	    	// 	build from  WhereItem
	    	if (whereItem == null)
	    		xqWhere = "";
	    	else {
	    	   xqWhere = buildWhere("", pivotsMap, whereItem,metaDataTypes);
	    	}
	    	partialXQLPackage.setXqWhere(xqWhere);
	    	//build order by
	    	if (orderBy == null) {
	    		xqOrderBy = "";
	    	} else {
	    		factorFirstPivotInMap(pivotsMap, orderBy);
	    		xqOrderBy = "order by "
	    					+XPathUtils.factor(orderBy, pivotsMap)
	    					+(direction == null ? "" : " "+direction);
	    	}
	    	partialXQLPackage.setXqOrderBy(xqOrderBy);
	    	//Get For
	    	String xqFor = getXQueryFor(
	    		isItemQuery,
	    		objectRootElementNamesToRevisionID,
	    		objectRootElementNamesToClusterName,
	    		pivotsMap,
	    		partialXQLPackage,
	    		queryBuilderContext
	    	);

	    	StringBuffer rawQueryStringBuffer = new StringBuffer();
	    	rawQueryStringBuffer.append(xqFor);
    		//add joinkeys
	    	partialXQLPackage.resetPivotWhereMap();
    		String joinstring=getJoinString(partialXQLPackage.getJoinKeys());
    		partialXQLPackage.setUseJoin(!("".equals(joinstring)));
	    	//rawQueryStringBuffer.append("".equals(xqWhere)? "" : "\nwhere "+xqWhere);
    		rawQueryStringBuffer.append("".equals(joinstring) ? "" : "\n"+joinstring);
	    	if(!partialXQLPackage.isUseGlobalOrderBy())rawQueryStringBuffer.append("".equals(xqOrderBy) ? "" : "\n"+xqOrderBy);
	    	rawQueryStringBuffer.append("\nreturn "+xqReturn);
	    	String rawQuery = rawQueryStringBuffer.toString();
	    		

	    	//Determine Query based on number of results an counts
	    	String query = null;
	    	
	    	boolean subsequence = (start>=0 && limit>=0 && limit!=Integer.MAX_VALUE);
	    	if (subsequence) {
	    		
	    		if (!partialXQLPackage.isUseSubsequenceFirst()) {
	    			if (withTotalCountOnFirstRow) {
			    		query =
			    			"let $_page_ := \n"+rawQuery
			    			+"\n return insert-before(subsequence($_page_,"+(start+1)+","+limit+"),0,<totalCount>{"
			    			+getCountExpr(partialXQLPackage)
			    			+"}</totalCount>)";
		    		} else {
	    	    		query =
	    	    			"let $_page_ := \n"+rawQuery
	    	    			+"\n return subsequence($_page_,"+(start+1)+","+limit+")";
		    		}
	    		}else {
	    			if (withTotalCountOnFirstRow) {
			    		query =
			    			"let $_page_ := \n"+rawQuery
			    			+"\n return insert-before($_page_,0,<totalCount>{"+getCountExpr(partialXQLPackage)+"}</totalCount>)";
		    		} else {
		    			query = rawQuery;
		    		}
	    		}
	    		
	    	} else {
	    		if (withTotalCountOnFirstRow) {
		    		query =
		    			"let $_page_ := \n"+rawQuery
		    			+"\n return insert-before($_page_,0,<totalCount>{"+getCountExpr(partialXQLPackage)+"}</totalCount>)";
	    		} else {
	    			query = rawQuery;
	    		}
	    	}
	    	
	    	//create a intermediate line for subsequence
	    	
	    	StringBuffer firstLets=new StringBuffer();
    		LinkedHashMap<String, String> forInCollectionMap = partialXQLPackage.getForInCollectionMap();
    		partialXQLPackage.resetPivotWhereMap();
    		Map<String,String> pivotWhereMap=partialXQLPackage.getPivotWhereMap();
	    	int i=0;
    		for (Iterator<String> iterator = forInCollectionMap.keySet().iterator(); iterator.hasNext();i++) {
				String root =  iterator.next();
				String expr =  forInCollectionMap.get(root);
				if(pivotWhereMap.get(root)!=null&&pivotWhereMap.get(root).length()>0)expr=expr+" [ "+pivotWhereMap.get(root)+" ] ";
				if(partialXQLPackage.isUseGlobalOrderBy())expr=partialXQLPackage.genOrderByWithFirstExpr(expr);
				firstLets.append("let $_leres").append(i).append("_ := ").append(expr).append(" \n");
			}

    		query=(firstLets.toString()+query);

	    	//replace () and to ""
	    	query=query.replaceAll(" \\(\\) and"," ");
	    	query=query.replaceAll(" and \\(\\)"," ");
	    	query=query.replaceAll("\\(\\(\\) and","( ");

	    	if(LOG.isDebugEnabled()) {
                LOG.debug("query:\n");
                LOG.debug(query);
	    	}
	    	return query;

    	} catch (XmlServerException e) {
    		throw(e);
	    } catch (Exception e) {
     	    String err = "Unable to build the Item XQuery";
     	    LOG.error(err,e);
     	    throw new XmlServerException(err);
	    }
    }

	private static String getCountExpr(PartialXQLPackage partialXQLPackage) {
		
		StringBuffer countExpr=new StringBuffer();
		if(partialXQLPackage.isUseJoin()) {
			countExpr.append("count($_page_)");
			return countExpr.toString();
		}
		countExpr.append("count($_leres0_)");
		int size=partialXQLPackage.getForInCollectionMap().size();
		if(size>1) {
			for (int i = 1; i < size; i++) {
				countExpr.append("*").append("count($_leres").append(i).append("_)");
			}	
		}
		return countExpr.toString();
	}
	/**
	 * get the foreign key join string
	 * @param joinKeys
	 * @return
	 */
	private static String getJoinString(List<String> joinKeys) {
		int c=0; 
		//StringBuffer sb=new StringBuffer();
		//String fk="";//FIXME only support one Foreignkey
		LinkedHashMap<String,ArrayList<String>> fkMaps=new LinkedHashMap<String, ArrayList<String>>();
		//String keyvalue="";
		for(String joinkey: joinKeys) {
			String[] items=joinkey.split("or|and");
			for(String item: items) {
				String key=item.trim();
				if(key.matches("\\((.*?)\\)"))
					key=key.replaceFirst("\\((.*?)\\)", "$1");
				String[] splits=key.split(WhereCondition.JOINS);
				if(splits.length==2) {
					String fk=splits[0].trim().replace("(", "").replace(")", "");
					String rightV=splits[1].trim().replace("(", "").replace(")", "");
					ArrayList<String> value=fkMaps.get(fk);
					if(value==null) {
						value=new ArrayList<String>();
						fkMaps.put(fk, value);
					}
					StringBuffer sb1=new StringBuffer();
					String var="$joinkey"+c;
					sb1.append(var+"#"+"let "+ var).append(" := concat(\"[\",").append(rightV).append(",\"]\")\n");
					value.add(sb1.toString());
					c++;
				}
			}
		}
		StringBuffer let=new StringBuffer();
		StringBuffer where=new StringBuffer();
		if(fkMaps.size()>0) where.append(" where ");
		int count=0;
		for(Entry<String,ArrayList<String>> entry: fkMaps.entrySet()) {
			String fk =entry.getKey()+"[not(.) or not(text()) or .]"; //see  	 0015254: Resolve FK info on search results 
			ArrayList<String> vars=new ArrayList<String>();
			for(String v:entry.getValue()) {
				int pos=v.indexOf("#");
				let.append(v.substring(pos+1));
				vars.add(v.substring(0,pos));				
			}
			String keyvalue="";
			if(vars.size()==1) {
				keyvalue=vars.get(0);
			}else {
				keyvalue="concat(";
				for(int k=0; k<vars.size(); k++) {
					if(k<vars.size()-1) {
						keyvalue +=vars.get(k)+",";
					}else {
						keyvalue +=vars.get(k);
					}
				}
				keyvalue+=")";
			}
			
			where.append(fk).append("=").append(keyvalue);
			if(count <fkMaps.size()-1) {
				where.append(" and ");
			}
			count++;
		}
		return let.toString()+where.toString();
	}
	/***********************************************************************
	 *
	 * Helper Methods
	 *
	 ***********************************************************************/
    
    /**
     * get the DB repository root path
     */
    public static String getDBRootPath(){
    	return CommonUtil.getDBRootPath();
    }
    /**
     * 
     * @param revisionID
     * @param clusterName
     * @return
     */
    public static String getPath(String revisionID, String clusterName){
    	return CommonUtil.getPath(revisionID, clusterName);
    }
    public static boolean isHead(String revisionID) {
    	if(revisionID!=null) revisionID=revisionID.replaceAll("\\[HEAD\\]|HEAD", "");
    	return (revisionID == null || "".equals(revisionID));
    }
	/**
	 * Determine the collection name based on the revision ID and Cluster Name
	 */
	public static String getXQueryCollectionName(String revisionID, String clusterName) throws XmlServerException {
		String collectionPath=getPath(revisionID, clusterName);
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