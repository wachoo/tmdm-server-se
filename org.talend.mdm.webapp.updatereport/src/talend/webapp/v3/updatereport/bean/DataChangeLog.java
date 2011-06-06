package talend.webapp.v3.updatereport.bean;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DataChangeLog {
	
	
	
	private String source;
	
	private String timeInMillis;
	
    private String operationType;
    
	private String concept;
	
	private String key;
	
	private String xmlSource;
	
	private String ids;
	
	//additional fields
	private String userName;
	
    private String revisionID;
    
    private String dataCluster;
    
    private String dataModel;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

    private String epochTime;

	public String getSource() {
		return cleanOutput(source);
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTimeInMillis() {
		String showTime="";
		if(timeInMillis!=null&&timeInMillis.length()>0&&!timeInMillis.equals("null")){
			Calendar c= Calendar.getInstance();
			c.setTimeInMillis(Long.parseLong(timeInMillis));
			showTime=sdf.format(c.getTime());
		}
		return showTime;
	}

	public void setTimeInMillis(String timeInMillis) {
		this.timeInMillis = timeInMillis;
	}

	public String getOperationType() {
		return cleanOutput(operationType);
	}

	public void setOperationType(String operationType) {
		this.operationType = operationType;
	}

	public String getConcept() {
		return cleanOutput(concept);
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getKey() {
		return cleanOutput(key);
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getXmlSource() {
		return xmlSource;
	}

	public void setXmlSource(String xmlSource) {
		this.xmlSource = xmlSource;
	}
	
	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}
	
	public String getUserName() {
		return cleanOutput(userName);
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getRevisionID() {
		return cleanOutput(revisionID);
	}

	public void setRevisionID(String revisionID) {
		this.revisionID = revisionID;
	}

	public String getDataCluster() {
		return cleanOutput(dataCluster);
	}

	public void setDataCluster(String dataCluster) {
		this.dataCluster = dataCluster;
	}

	public String getDataModel() {
		return cleanOutput(dataModel);
	}

	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
	}

	private String cleanOutput(String output) {
		
		if(output==null||output.equals("null"))output="";
		
		output=output.trim();
		
		return output;
	}

    public void setEpochTime(String epochTime) {
        this.epochTime = epochTime;
    }

    public String getEpochTime() {
        return epochTime;
    }
}
