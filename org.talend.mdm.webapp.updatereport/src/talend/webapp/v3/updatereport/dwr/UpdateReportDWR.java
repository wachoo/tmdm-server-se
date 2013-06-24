package talend.webapp.v3.updatereport.dwr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;

import talend.webapp.v3.updatereport.bean.DataChangeLog;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.history.DocumentHistoryFactory;
import com.amalto.core.history.DocumentHistoryNavigator;
import com.amalto.core.history.exception.UnsupportedUndoPhysicalDeleteException;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSInt;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereAnd;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;

public class UpdateReportDWR {

    private Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    private static final Messages MESSAGES = MessagesFactory.getMessages("talend.webapp.v3.updatereport.dwr.messages", //$NON-NLS-1$
    		UpdateReportDWR.class.getClassLoader());

    public UpdateReportDWR() {

    }

    public boolean isAdminUser() {
        try {
            Set<String> roles = LocalUser.getLocalUser().getRoles();
            return roles.contains(ICoreConstants.SYSTEM_ADMIN_ROLE);
        } catch (Exception e) {
            logger.error(MESSAGES.getMessage(Locale.getDefault(), "updatereport.server.isAdminUser.error"), e);   //$NON-NLS-1$
            return false;
        }
    }

    public boolean isEnterpriseVersion() {
        return Webapp.INSTANCE.isEnterpriseVersion();
    }

    public ListRange getUpdateReportList(int start, int limit, String sort, String dir, String regex) throws Exception {

        ListRange listRange = new ListRange();

        if (limit == 0) {
            limit = Integer.MAX_VALUE;
        }
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

                WSWhereCondition clusterwc = new WSWhereCondition("DataCluster", WSWhereOperator.EQUALS, dataCluster //$NON-NLS-1$
                        .trim(), WSStringPredicate.NONE, false);

                WSWhereCondition modelwc = new WSWhereCondition("DataModel", WSWhereOperator.EQUALS, dataModel.trim(), //$NON-NLS-1$
                        WSStringPredicate.NONE, false);

                WSWhereItem wsWhereDataCluster = new WSWhereItem(clusterwc, null, null);
                WSWhereItem wsWhereDataModel = new WSWhereItem(modelwc, null, null);
                conditions.add(wsWhereDataCluster);
                conditions.add(wsWhereDataModel);
            }

            if (!criteria.isNull("concept")) { //$NON-NLS-1$
                String concept = (String) criteria.get("concept"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("Concept", itemsBrowser ? WSWhereOperator.EQUALS //$NON-NLS-1$
                        : WSWhereOperator.CONTAINS, concept.trim(), WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("key")) { //$NON-NLS-1$
                String key = (String) criteria.get("key"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("Key", itemsBrowser ? WSWhereOperator.EQUALS //$NON-NLS-1$
                        : WSWhereOperator.CONTAINS, key.trim(), WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("source")) { //$NON-NLS-1$
                String source = (String) criteria.get("source"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("Source", WSWhereOperator.EQUALS, source.trim(), //$NON-NLS-1$
                        WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("operationType")) { //$NON-NLS-1$
                String operationType = (String) criteria.get("operationType"); //$NON-NLS-1$
                WSWhereCondition wc = new WSWhereCondition("OperationType", WSWhereOperator.EQUALS, operationType.trim(), //$NON-NLS-1$
                        WSStringPredicate.NONE, false);
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("startDate")) { //$NON-NLS-1$
                String startDate = (String) criteria.get("startDate"); //$NON-NLS-1$
                SimpleDateFormat dataFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
                Date date = dataFmt.parse(startDate);
                WSWhereCondition wc = new WSWhereCondition("TimeInMillis", WSWhereOperator.GREATER_THAN_OR_EQUAL, date //$NON-NLS-1$
                        .getTime()
                        + "", WSStringPredicate.NONE, false); //$NON-NLS-1$
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

            if (!criteria.isNull("endDate")) { //$NON-NLS-1$
                String endDate = (String) criteria.get("endDate"); //$NON-NLS-1$
                SimpleDateFormat dataFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$
                Date date = dataFmt.parse(endDate);
                WSWhereCondition wc = new WSWhereCondition("TimeInMillis", WSWhereOperator.LOWER_THAN_OR_EQUAL, date //$NON-NLS-1$
                        .getTime()
                        + "", WSStringPredicate.NONE, false); //$NON-NLS-1$
                WSWhereItem wsWhereItem = new WSWhereItem(wc, null, null);
                conditions.add(wsWhereItem);
            }

        }

        WSWhereItem wi;
        if (conditions.size() == 0) {
            wi = null;
        } else if (conditions.size() == 1) {
            wi = conditions.get(0);
        } else {
            WSWhereAnd and = new WSWhereAnd(conditions.toArray(new WSWhereItem[conditions.size()]));
            wi = new WSWhereItem(null, and, null);
        }

        com.amalto.webapp.util.webservices.WSGetItemsSort getItems = new com.amalto.webapp.util.webservices.WSGetItemsSort();
        getItems.setConceptName(conceptName);
        getItems.setWhereItem(wi);
        getItems.setTotalCountOnFirstResult(true);
        getItems.setSkip(start);
        getItems.setMaxItems(limit);
        getItems.setWsDataClusterPK(wsDataClusterPK);
        if(sort != null){
            getItems.setSort("Update/" + upperCaseFirstLetter(sort)); //$NON-NLS-1$
        }
        if(dir != null){
            getItems.setDir(dir.toLowerCase() + "ending"); //$NON-NLS-1$
        }
        
        WSStringArray resultsArray = Util.getPort().getItemsSort(getItems);
        String[] results = resultsArray == null ? new String[0] : resultsArray.getStrings();

        // I would be better to return count as part of getItems call... but requires refactoring in getItems.
        Document document = Util.parse(results[0]);
        int totalSize = Integer.parseInt(document.getDocumentElement().getTextContent());

        if (logger.isDebugEnabled()) {
            logger.debug("Total:" + totalSize + ";Start:" + start + ";Limit:" + limit + ";Length:"  //$NON-NLS-1$  //$NON-NLS-2$  //$NON-NLS-3$  //$NON-NLS-4$
                                + (results.length));
        }

        // parse data
        DataChangeLog[] data = new DataChangeLog[results.length - 1];
        for (int i = 0; i < data.length; i++) {
            DataChangeLog item = new DataChangeLog();

            String result = results[i + 1]; // Start from 2nd result (first one is the total count).
            if (result != null) {
                // Not very OO
                Document doc = Util.parse(result);

                String userName = Util.getFirstTextNode(doc, "result/Update/UserName"); //$NON-NLS-1$
                String source = Util.getFirstTextNode(doc, "result/Update/Source"); //$NON-NLS-1$
                String timeInMillis = Util.getFirstTextNode(doc, "result/Update/TimeInMillis"); //$NON-NLS-1$
                String epochTime = Util.getFirstTextNode(doc, "result/Update/TimeInMillis"); //$NON-NLS-1$
                String operationType = Util.getFirstTextNode(doc, "result/Update/OperationType"); //$NON-NLS-1$
                String revisionID = Util.getFirstTextNode(doc, "result/Update/RevisionID"); //$NON-NLS-1$
                String dataCluster = Util.getFirstTextNode(doc, "result/Update/DataCluster"); //$NON-NLS-1$
                String dataModel = Util.getFirstTextNode(doc, "result/Update/DataModel"); //$NON-NLS-1$
                String concept = Util.getFirstTextNode(doc, "result/Update/Concept"); //$NON-NLS-1$
                String key = Util.getFirstTextNode(doc, "result/Update/Key"); //$NON-NLS-1$

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
                item.setIds(Util.joinStrings(new String[] { source, timeInMillis }, ".")); //$NON-NLS-1$

            }

            data[i] = item;
        }

        if (data.length <= 0) {
            listRange.setData(new DataChangeLog[0]);
            listRange.setTotalSize(0);
            return listRange;
        }
        WSBoolean isPagingAccurate = Util.getPort().isPagingAccurate(new WSInt(totalSize));
        listRange.setData(data);
        listRange.setTotalSize(totalSize);
        listRange.setPagingAccurate(isPagingAccurate.is_true());
        return listRange;
    }

    public String getReportString(int start, int limit, String regex, String language) throws Exception {
    	ListRange listRange = this.getUpdateReportList(start, limit, null, null, regex);
    	Object[] data = listRange.getData();

        return generateEventString(data, language);
    }
    
    public boolean isJournalHistoryExist(String dataClusterName,String dataModelName,String concept,String key,String operationType,String historyDate) {
        if (isEnterpriseVersion()) {
            try {
                if (UpdateReportPOJO.OPERATION_TYPE_CREATE.equals(operationType)
                        || UpdateReportPOJO.OPERATION_TYPE_UPDATE.equals(operationType)
                        || UpdateReportPOJO.OPERATION_TYPE_LOGICAL_DELETE.equals(operationType)
                        || UpdateReportPOJO.OPERATION_TYPE_PHYSICAL_DELETE.equals(operationType)
                        || UpdateReportPOJO.OPERATION_TYPE_RESTORED.equals(operationType)) {
                    DocumentHistoryNavigator navigator = DocumentHistoryFactory.getInstance().create().getHistory(dataClusterName,
                            dataModelName,
                            concept,
                            key.split("\\."), //$NON-NLS-1$
                            ""); //$NON-NLS-1$
                    navigator.goTo(new Date(Long.parseLong(historyDate)));
                    navigator.previous();                    
                    return true;                    
                } else {
                    if (!UpdateReportPOJO.OPERATION_TYPE_ACTION.equals(operationType)) {
                        logger.warn("Operation type '" + operationType + "' is not supported. Ignore action for document history."); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                    return false;
                }
            } catch (UnsupportedUndoPhysicalDeleteException exception) {
                if (logger.isDebugEnabled()) {
                    logger.warn("Undo for physical delete is not supported."); //$NON-NLS-1$
                }
                return false;
            } catch (Exception exception) {
                logger.error(exception.getMessage(), exception);
                return false;
            }
        } else {
            return true;
        }
    }
    
    private static String upperCaseFirstLetter(String str){
        byte[] byteArr = str.getBytes();
        byteArr[0] -= 32;
        return new String(byteArr);
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
