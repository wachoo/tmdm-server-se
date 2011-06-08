package talend.webapp.v3.updatereport.dwr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.core.util.Webapp;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;

import talend.webapp.v3.updatereport.bean.DataChangeLog;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Messages;
import com.amalto.webapp.core.util.MessagesFactory;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSCount;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSString;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;
import com.amalto.webapp.util.webservices.WSXPathsSearch;

public class UpdateReportDWR {

    private Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());
    
    private static final Messages MESSAGES = MessagesFactory.getMessages("talend.webapp.v3.updatereport.dwr.messages", //$NON-NLS-1$
    		UpdateReportDWR.class.getClassLoader());
       
    public UpdateReportDWR() {

    }

    public boolean isAdminUser() {
        try {
            return LocalUser.getLocalUser().isAdmin(ItemPOJO.class);
        } catch (XtentisException e) {
            logger.error(MESSAGES.getMessage(Locale.getDefault(), "updatereport.server.isAdminUser.error"), e);   //$NON-NLS-1$
            return false;
        }
    }

    public boolean isEnterpriseVersion() {
        return Webapp.INSTANCE.isEnterpriseVersion();
    }

    public ListRange getUpdateReportList(int start, int limit, String sort, String dir, String regex) throws Exception {

        ListRange listRange = new ListRange();

        WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
        String conceptName = "Update";// Hard Code //$NON-NLS-1$         

        // Where condition
        ArrayList<WSWhereItem> conditions = new ArrayList<WSWhereItem>();
        if (regex != null && regex.length() > 0) {
            JSONObject criteria = new JSONObject(regex);
            boolean itemsBrowser = !criteria.isNull("itemsBrowser") && criteria.get("itemsBrowser").equals("true"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

            if (itemsBrowser) {
                Configuration configuration = Configuration.getInstance(true);
                String dataCluster = configuration.getCluster();
                String dataModel = configuration.getModel();

                WSWhereCondition clusterwc = new WSWhereCondition("/Update/DataCluster", WSWhereOperator.EQUALS, dataCluster //$NON-NLS-1$
                        .trim(), WSStringPredicate.NONE, false);

                WSWhereCondition modelwc = new WSWhereCondition("/Update/DataModel", WSWhereOperator.EQUALS, dataModel.trim(), //$NON-NLS-1$
                        WSStringPredicate.NONE, false);

                WSWhereItem wsWhereDataCluster = new WSWhereItem(clusterwc, null, null);
                WSWhereItem wsWhereDataModel = new WSWhereItem(modelwc, null, null);
                conditions.add(wsWhereDataCluster);
                conditions.add(wsWhereDataModel);
            }

            if (!criteria.isNull("concept")) { //$NON-NLS-1$
                String concept = (String) criteria.get("concept"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("/Update/Concept", itemsBrowser ? WSWhereOperator.EQUALS //$NON-NLS-1$
                        : WSWhereOperator.CONTAINS, concept.trim(), WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("key")) { //$NON-NLS-1$
                String key = (String) criteria.get("key"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("/Update/Key", itemsBrowser ? WSWhereOperator.EQUALS //$NON-NLS-1$
                        : WSWhereOperator.CONTAINS, key.trim(), WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("source")) { //$NON-NLS-1$
                String source = (String) criteria.get("source"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("/Update/Source", WSWhereOperator.EQUALS, source.trim(), //$NON-NLS-1$
                        WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("operationType")) { //$NON-NLS-1$
                String operationType = (String) criteria.get("operationType"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("/Update/OperationType", WSWhereOperator.EQUALS, operationType.trim(), //$NON-NLS-1$
                        WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("startDate")) { //$NON-NLS-1$
                String startDate = (String) criteria.get("startDate"); //$NON-NLS-1$
                SimpleDateFormat dataFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
                Date date = dataFmt.parse(startDate);
                WSWhereCondition wc = new WSWhereCondition("/Update/TimeInMillis", WSWhereOperator.GREATER_THAN_OR_EQUAL, date //$NON-NLS-1$
                        .getTime()
                        + "", WSStringPredicate.NONE, false); //$NON-NLS-1$
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("endDate")) { //$NON-NLS-1$
                String endDate = (String) criteria.get("endDate"); //$NON-NLS-1$
                SimpleDateFormat dataFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
                Date date = dataFmt.parse(endDate);
                WSWhereCondition wc = new WSWhereCondition("/Update/TimeInMillis", WSWhereOperator.LOWER_THAN_OR_EQUAL, date //$NON-NLS-1$
                        .getTime()
                        + "", WSStringPredicate.NONE, false); //$NON-NLS-1$
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

        }

        WSWhereItem wi = null;
        if (conditions.size() == 0)
            wi = null;
        else {
            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        }

        // count each time
        WSString totalString = Util.getPort().count(new WSCount(wsDataClusterPK, conceptName, wi, -1));
        int totalSize = 0;
        if (totalString != null && totalString.getValue() != null && totalString.getValue().length() > 0)
            totalSize = Integer.parseInt(totalString.getValue());

        if (limit == 0) {
            if (totalSize == 0)
                limit = 20;
            else
                limit = totalSize;
        }
        String[] results = Util.getPort().xPathsSearch(
                new WSXPathsSearch(wsDataClusterPK, null, new WSStringArray(new String[] { conceptName }), wi, -1, start, limit,
                        "/Update/TimeInMillis", "descending")).getStrings(); //$NON-NLS-1$ //$NON-NLS-2$

        if (logger.isDebugEnabled())
            logger.debug("Total:" + totalSize + ";Start:" + start + ";Limit:" + limit + ";Length:"  //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$  //$NON-NLS-4$
                    + (results == null ? 0 : results.length));
        // sub result
        // start=start<results.length?start:results.length-1;
        // if(start<0)start=0;
        // int end=results.length<(start+limit)?results.length-1:(start+limit-1);
        //
        // String[] subResults=end+1-start<limit?new String[end+1-start]:new String[limit];
        // for (int i = start,j=0; i < end+1; i++,j++) {
        // subResults[j]=results[i];
        // }
        String[] subResults = results;
        // parse data
        DataChangeLog[] data = new DataChangeLog[subResults.length];
        for (int i = 0; i < data.length; i++) {
            DataChangeLog item = new DataChangeLog();

            String result = subResults[i];
            if (result != null) {
                // Not very OO
                Document doc = Util.parse(result);

                String userName = Util.getFirstTextNode(doc, "/Update/UserName"); //$NON-NLS-1$
                String source = Util.getFirstTextNode(doc, "/Update/Source"); //$NON-NLS-1$
                String timeInMillis = Util.getFirstTextNode(doc, "/Update/TimeInMillis"); //$NON-NLS-1$
                String epochTime = Util.getFirstTextNode(doc, "/Update/TimeInMillis"); //$NON-NLS-1$
                String operationType = Util.getFirstTextNode(doc, "/Update/OperationType"); //$NON-NLS-1$
                String revisionID = Util.getFirstTextNode(doc, "/Update/RevisionID"); //$NON-NLS-1$
                String dataCluster = Util.getFirstTextNode(doc, "/Update/DataCluster"); //$NON-NLS-1$
                String dataModel = Util.getFirstTextNode(doc, "/Update/DataModel"); //$NON-NLS-1$
                String concept = Util.getFirstTextNode(doc, "/Update/Concept"); //$NON-NLS-1$
                String key = Util.getFirstTextNode(doc, "/Update/Key"); //$NON-NLS-1$

                item.setUserName(userName);
                item.setSource(source);
                item.setTimeInMillis(timeInMillis);
                item.setEpochTime(epochTime);
                item.setOperationType(operationType);
                item.setRevisionID(revisionID);
                item.setDataCluster(dataCluster);
                item.setDataModel(dataModel);
                item.setConcept(concept);
                item.setKey(key);
                // item.setXmlSource(result);
                item.setIds(Util.joinStrings(new String[] { source, timeInMillis }, ".")); //$NON-NLS-1$

            }

            data[i] = item;
        }

        if (data.length <= 0) {
            listRange.setData(new DataChangeLog[0]);
            listRange.setTotalSize(0);
            return listRange;
        }

        listRange.setData(data);
        listRange.setTotalSize(totalSize);
        return listRange;
    }
       
    public String getReportString(int start, int limit, String regex, String language) throws Exception {
//    	int limitCount = 0;
//    	if(regex != null && regex.length() > 0){
//    		limitCount = limit;
//    	}
    	ListRange listRange = this.getUpdateReportList(start, limit, null, null, regex);
    	Object[] data = listRange.getData();

        return generateEventString(data, language);
    }

    // This is the HTML displayed in the time line bubble (when you click on an event).
    private String generateEventString(Object[] data, String language) throws ParseException{
    	StringBuilder sb = new StringBuilder("{'dateTimeFormat': 'iso8601',"); //$NON-NLS-1$
    	sb.append("'events' : [");	 //$NON-NLS-1$
    	  
    	for(int i =0; i<data.length; i++){
    		DataChangeLog datalog = (DataChangeLog) data[i];
    		sb.append("{'start':'").append(this.computeTime(datalog.getTimeInMillis())).append("',");  //$NON-NLS-1$ //$NON-NLS-2$                
    		sb.append("'title':'").append(datalog.getTimeInMillis()).append(" - ").append(datalog.getOperationType()).append("',");  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		sb.append("'link':'").append("javascript:showDialog(") //$NON-NLS-1$ //$NON-NLS-2$
			 .append("\"").append(datalog.getIds()).append("\",") //$NON-NLS-1$ //$NON-NLS-2$
			 .append("\"").append(datalog.getKey()).append("\",") //$NON-NLS-1$ //$NON-NLS-2$
			 .append("\"").append(datalog.getEpochTime()).append("\",") //$NON-NLS-1$ //$NON-NLS-2$
			 .append("\"").append(datalog.getConcept()).append("\",") //$NON-NLS-1$ //$NON-NLS-2$
			 .append("\"").append(datalog.getDataCluster()).append("\",") //$NON-NLS-1$ //$NON-NLS-2$
			 .append("\"").append(datalog.getDataModel()).append("\"").append(")',"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    		
    		Locale locale = new Locale(language);
    		    
    		sb.append("'description':'").append(MESSAGES.getMessage(locale, "updatereport.timeline.label.userName")).append(":").append(datalog.getUserName()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$ //$NON-NLS-4$
			.append(MESSAGES.getMessage(locale, "updatereport.timeline.label.source")).append(":").append(datalog.getSource()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
			.append(MESSAGES.getMessage(locale, "updatereport.timeline.label.entity")).append(":").append(datalog.getConcept()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
			.append(MESSAGES.getMessage(locale, "updatereport.timeline.label.revision")).append(":").append(datalog.getRevisionID()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
			.append(MESSAGES.getMessage(locale, "updatereport.timeline.label.dataContainer")).append(":").append(datalog.getDataCluster()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
			.append(MESSAGES.getMessage(locale, "updatereport.timeline.label.dataModel")).append(":").append(datalog.getDataModel()).append("<br>") //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$
			.append(MESSAGES.getMessage(locale, "updatereport.timeline.label.key")).append(":").append(datalog.getKey()).append("<br>"); //$NON-NLS-1$ //$NON-NLS-2$  //$NON-NLS-3$		
    		
    		sb.append("'}"); //$NON-NLS-1$
    		
    		if(i != data.length-1)
    			sb.append(","); //$NON-NLS-1$
    	}

    	sb.append("]}"); //$NON-NLS-1$
    	sb.append("@||@"); //$NON-NLS-1$
    	
    	if(data.length >= 1){
    		DataChangeLog obj = (DataChangeLog) data[0];  		
    		sb.append(this.changeDataFormat(obj.getTimeInMillis()));    		
    	}else{
    		sb.append(this.getDateStringPlusGMT(new Date()));
    	}
    	
    	sb.append("@||@"); //$NON-NLS-1$
    	if(data.length > 0){
    		sb.append("true"); //$NON-NLS-1$
    	}else{
    		sb.append("false"); //$NON-NLS-1$
    	}
    	return sb.toString();
    }

    private String changeDataFormat(String src) throws ParseException{
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");  //$NON-NLS-1$

    	Date d = sdf.parse(src);    	
    	return this.getDateStringPlusGMT(d);
    }
    
    private String getDateStringPlusGMT(Date d){
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH); //$NON-NLS-1$
		String timeStr = sdf.format(d);
    	return timeStr + " GMT"; //$NON-NLS-1$
    }
    
    private String computeTime(String src) throws ParseException{
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");  //$NON-NLS-1$
    	Date d = sdf.parse(src);
    	Calendar c = Calendar.getInstance();
    	int offset = c.getTimeZone().getRawOffset()/3600000;
    	c.setTime(d);
    	c.add(Calendar.HOUR, offset);
    	
    	return sdf.format(c.getTime());
    }
    
    public boolean checkDCAndDM(String dataCluster, String dataModel) {
        return Util.checkDCAndDM(dataCluster, dataModel);
    }
}
