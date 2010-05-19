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
		return Util.getForeignKeyList(start, limit, value, xpathForeignKey, xpathInfoForeignKey, null, true);
    }

}
