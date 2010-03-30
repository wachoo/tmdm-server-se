package com.amalto.webapp.widget;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.bean.ComboItemBean;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSXPathsSearch;
import com.sun.xml.xsom.XSElementDecl;


public class WidgetDWR {
	
	/**********************************************
     * where condition panel widget
     ***********************************************/
	
	public ListRange getFieldList(int start, int limit,String sort,String dir,String regex) throws Exception {
        ListRange listRange = new ListRange();
		
		if(regex==null||regex.length()==0)return listRange;
		String[] inputParams=regex.split("&");
		
		
		String businessConcepts=inputParams[0];
		String[] businessConceptsArray=businessConcepts.split(",");
		String language=inputParams[1];
		
		List<ComboItemBean> comboItems=new ArrayList<ComboItemBean>();
		Configuration config = Configuration.getInstance();
		String dataModelPK = config.getModel();
		Map<String,XSElementDecl> conceptMap=CommonDWR.getConceptMap(dataModelPK);
		
		for (int i = 0; i < businessConceptsArray.length; i++) {
			String businessConcept=businessConceptsArray[i];
			
			HashMap<String, String> xpathToLabel = new HashMap<String, String>();
			xpathToLabel = CommonDWR.getFieldsByDataModel(dataModelPK,conceptMap,businessConcept, language, false,false);
			xpathToLabel.remove(businessConcept);
			
			for (Iterator<String> iterator = xpathToLabel.keySet().iterator(); iterator.hasNext();) {
				String xpath = iterator.next();
				String label = xpathToLabel.get(xpath);
				
				if(businessConceptsArray.length==1)comboItems.add(new ComboItemBean(xpath,label));
				else comboItems.add(new ComboItemBean(xpath,xpath));
			}
		}
		
		listRange.setData(comboItems.toArray());
		listRange.setTotalSize(comboItems.size());

		return listRange;
	}
	
	/**********************************************
     * foreign key widget
     ***********************************************/
    
    /**
     * Get the number of foreign key.
     * @param xpathForeignKey
     * @return
     * @throws Exception
     */
    public String countForeignKey_filter(String xpathForeignKey) throws Exception {
       Configuration config = Configuration.getInstance();
       String conceptName = Util.getConceptFromPath(xpathForeignKey);
       
       WSWhereCondition whereCondition = Util.getConditionFromPath(xpathForeignKey);
       WSWhereItem whereItem = null;
       
       if(whereCondition != null){
          whereItem = new WSWhereItem(whereCondition, null, null);
       }
       
       return Util.getPort().count(
          new WSCount(
             new WSDataClusterPK(config.getCluster()),
             conceptName,
             whereItem,//null,
             -1
          )
       ).getValue();
    }
    
    /**
     * Get the list of foreign key.
     */
    public String getForeignKeyList(int start, int limit, String value, String xpathForeignKey, String xpathInfoForeignKey) throws RemoteException, Exception{
		String initxpathForeignKey="";
		initxpathForeignKey = Util.getForeignPathFromPath(xpathForeignKey);
		
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("getForeignKeyList() xPath FK: '"+initxpathForeignKey+"' xPath FK Info: '"+xpathInfoForeignKey+"' value: '"+value+"'");
				
		WSWhereCondition whereCondition=Util.getConditionFromPath(xpathForeignKey);
		WSWhereItem whereItem=null;
		if(whereCondition!=null){
			whereItem= new WSWhereItem (whereCondition,null,null);
		}
				
		Configuration config = Configuration.getInstance();
		//aiming modify there is bug when initxpathForeignKey when it's like 'conceptname/key'
		//so we convert initxpathForeignKey to 'conceptname'
		initxpathForeignKey=initxpathForeignKey.split("/")[0];
		//end
		
		// foreign key set by business concept
		if(initxpathForeignKey.split("/").length == 1){
			String conceptName = initxpathForeignKey;

			//determine if we have xPath Infos: e.g. labels to display
			String[] xpathInfos = new String[1];
			if(!"".equals(xpathInfoForeignKey))	
				xpathInfos = xpathInfoForeignKey.split(",");
			else
				xpathInfos[0] = conceptName;
			//aiming add .* to value
			value=value==null?"":value;			
			//end
			// build query - add a content condition on the pivot if we search for a particular value
			String filteredConcept = conceptName;
			boolean isKey = false;
			StringBuffer sb = new StringBuffer();   			    
			
			if(value!=null && !"".equals(value.trim())) {	
			   Pattern p = Pattern.compile("\\[(.*?)\\]");
			   Matcher m = p.matcher(value);
			   
			   while(m.find()){//key
		         sb = sb.append("[matches(. , \""+m.group(1)+"\", \"i\")]");
		         if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName()))
		            sb = sb.append("[ora:matches(. , \""+m.group(1)+"\", \"i\")]");
		         isKey = true;
			   }
			   if(isKey)
			      filteredConcept += sb.toString();
			   else{
			      value=value.equals(".*")? "":value+".*";
   				//Value is unlikely to be in attributes
   				filteredConcept+="[matches(. , \""+value+"\", \"i\")]";
   				if(EDBType.ORACLE.getName().equals(MDMConfiguration.getDBType().getName())) {
   					filteredConcept+="[ora:matches(. , \""+value+"\", \"i\")]";
   				}
			   }
			}
			
			//add the xPath Infos Path
			ArrayList<String> xPaths = new ArrayList<String>();
			for (int i = 0; i < xpathInfos.length; i++) {
				xPaths.add(xpathInfos[i].replaceFirst(conceptName, filteredConcept));
			}
			//add the key paths last, since there may be multiple keys
			xPaths.add(filteredConcept+"/../../i");
			
			//Run the query
			String [] results = Util.getPort().xPathsSearch(new WSXPathsSearch(
				new WSDataClusterPK(config.getCluster()),
				filteredConcept,
				new WSStringArray(xPaths.toArray(new String[xPaths.size()])),
				whereItem,
				-1,
				start,
				limit,
				null,
				null
			)).getStrings();
			
			if (results == null) results = new String[0];
			
			JSONObject json = new JSONObject();
			//json.put("count", results.length);
			
			JSONArray rows = new JSONArray();
			json.put("rows", rows);

			//add (?i) to incasesensitive
			//parse the results - each result contains the xPathInfo values, followed by the keys
			//the first row is totalCount
			for (int i = 0; i < results.length; i++) {
				//process no infos case
				if(!results[i].startsWith("<result>")){
					results[i]="<result>"+results[i]+"</result>";
				}
				results[i]=results[i].replaceAll("\\n", "");//replace \n
				results[i]=results[i].replaceAll(">(\\s+)<", "><"); //replace spaces between elements
				Element root = Util.parse(results[i]).getDocumentElement();
				NodeList list = root.getChildNodes();

				//recover keys - which are last
				String keys = "";
				for (int j = "".equals(xpathInfoForeignKey)?1:xpathInfos.length; j<list.getLength(); j++) {
					Node textNode = list.item(j).getFirstChild();		
					keys += "["+(textNode == null ? "" : textNode.getNodeValue())+"]";
				}
				
				//recover xPathInfos
				String infos = null;
				
				//if no xPath Infos given, use the key values
				if (xpathInfos.length == 0||"".equals(xpathInfoForeignKey)) {
					infos = keys;
				} else {
					//build a dash separated string of xPath Infos
    				for (int j = 0; j < xpathInfos.length; j++) {
    					infos = (infos == null ? "" : infos+"-");
    					Node textNode = list.item(j).getFirstChild();
    					infos  += textNode == null ? "" : textNode.getNodeValue();
    				}
				}
				
				if((keys.equals("[]")||keys.equals(""))&&(infos.equals("")||infos.equals("[]"))){
					//empty row
				}else{
					JSONObject row = new JSONObject();		
					//add by ymli. retrieve the correct results according value. fig bug:0010481					
					//if(keys.matches(value)||infos.matches(value)||keys.indexOf(value)!=-1||infos.indexOf(value)!=-1){
						row.put("keys", keys);
						row.put("infos", infos);
						rows.put(row);
					//}
				}
			}
			//edit by ymli; fix the bug:0011918: set the pageSize correctly.

			json.put("count", countForeignKey_filter(xpathForeignKey));				
			

			return json.toString();
		}
		
		throw new Exception("this should not happen");
    }

}
