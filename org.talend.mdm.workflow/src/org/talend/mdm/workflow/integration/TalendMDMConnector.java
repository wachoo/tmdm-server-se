package org.talend.mdm.workflow.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ow2.bonita.connector.core.ConnectorError;
import org.ow2.bonita.connector.core.ProcessConnector;
import org.talend.mdm.workflow.client.TalendMDMChecker;
import org.talend.mdm.workflow.client.TalendMDMItemUpdater;
import org.talend.mdm.workflow.client.util.MDMConnectTimeOutException;

public class TalendMDMConnector extends ProcessConnector {

	private String port;
	private String updatemap;
	private String datacontainer;
	private String datamodel;
	private String host;
	private String version;
	
	private Map<String,Object> itemUpdateMap;

	@Override
	protected void executeConnector() throws Exception {
		// TODO: table widget
	    if (updatemap != null && itemUpdateMap == null) {
	      itemUpdateMap = new HashMap<String, Object>();
	      final String[] couples = updatemap.split(";");
	      for (String couple : couples) {
	        String key = couple.split("=")[0];
	        String value = couple.split("=")[1];
	        itemUpdateMap.put(key, value); 
	      }
	    }

	    final String url = "http://" + host + ":" + port +"/talend/TalendPort";
	    final TalendMDMItemUpdater itemUpdater = new TalendMDMItemUpdater(url,version);
	    itemUpdater.setDataCluster(datacontainer);
	    itemUpdater.setDataModel(datamodel);
	    itemUpdater.setItemUpdateMap(itemUpdateMap);
	    itemUpdater.update();
	}

	@Override
	protected List<ConnectorError> validateValues() {
		List<ConnectorError> errorValues=new ArrayList<ConnectorError>();
		
		final String url = "http://" + host + ":" + port +"/talend/TalendPort";
		TalendMDMChecker talendMDMChecker=new TalendMDMChecker(url,version);
		try {
			talendMDMChecker.validate();
			return null;
		} catch (MDMConnectTimeOutException e) {
			errorValues.add(new ConnectorError(host,e));
			errorValues.add(new ConnectorError(port,e));
			errorValues.add(new ConnectorError(version,e));
			return errorValues;
		}
		
	}

	public void setPort(String port) {
		this.port = port;
	}

	public void setUpdatemap(String updatemap) {
		this.updatemap = updatemap;
	}

	public void setDatacontainer(String datacontainer) {
		this.datacontainer = datacontainer;
	}

	public void setDatamodel(String datamodel) {
		this.datamodel = datamodel;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setVersion(String version) {
		this.version = (version == null ? "" : version);
	}

}
