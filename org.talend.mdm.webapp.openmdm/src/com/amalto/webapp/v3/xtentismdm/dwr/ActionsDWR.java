// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.v3.xtentismdm.dwr;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.core.webservice.WSDataCluster;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSDataModel;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSGetDataCluster;
import com.amalto.core.webservice.WSGetDataModel;
import com.amalto.core.webservice.WSRegexDataClusterPKs;
import com.amalto.core.webservice.WSRegexDataModelPKs;

public class ActionsDWR {

    private static final Logger LOG = Logger.getLogger(ActionsDWR.class);
    
	public Map<String, String> getClusters(String value){
		try {
			Map<String, String> map = new HashMap<String, String>();
			WSDataClusterPK[] wsDataClustersPKs = Util.getPort().getDataClusterPKs(
					new WSRegexDataClusterPKs("*") //$NON-NLS-1$
					).getWsDataClusterPKs();
			
			//CommonDWR.filterSystemClustersPK(wsDataClustersPKs, map);
			
			Map<String, XSystemObjects> xDataClustersMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
			for (int i = 0; i < wsDataClustersPKs.length; i++) {
				if(!XSystemObjects.isXSystemObject(xDataClustersMap, wsDataClustersPKs[i].getPk())){
					WSDataCluster wsGetDataCluster=Util.getPort().getDataCluster(new WSGetDataCluster(wsDataClustersPKs[i]));
					map.put(wsDataClustersPKs[i].getPk(),wsGetDataCluster.getDescription()==null?"":wsGetDataCluster.getDescription()); //$NON-NLS-1$
				}
			}
			
			return  map;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public Map<String, String> getModels(String value){
		try {
			Map<String, String> map = new HashMap<String, String>();
			WSDataModelPK[] wsDataModelsPKs = Util.getPort().getDataModelPKs(
					new WSRegexDataModelPKs("*") //$NON-NLS-1$
					).getWsDataModelPKs();
			
			//CommonDWR.filterSystemDataModelsPK(wsDataModelsPK, map);
			
			Map<String, XSystemObjects> xDataModelsMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
			for (int i = 0; i < wsDataModelsPKs.length; i++) {
				if(!XSystemObjects.isXSystemObject(xDataModelsMap, wsDataModelsPKs[i].getPk())){
					WSDataModel wsDataModel=Util.getPort().getDataModel(new WSGetDataModel(wsDataModelsPKs[i]));
					map.put(wsDataModelsPKs[i].getPk(), wsDataModel.getDescription()==null?"":wsDataModel.getDescription()); //$NON-NLS-1$
				}
			}
			
			return  map;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}

	public String getCluster(){
		try {
			Configuration config = Configuration.getInstance();
			return config.getCluster();
		} catch (Exception e) {
		    LOG.error(e.getMessage(), e);
			return null;
		}		
	}
	
	public String getModel(){
		try {
			Configuration config = Configuration.getInstance();
			return config.getModel();
		} catch (Exception e) {
		    LOG.error(e.getMessage(), e);
			return null;
		}	
	}
	
	public String setClusterAndModel(String cluster, String model){
		try {
			Configuration.initialize(cluster,model);
			//Used by javascript as a status code
			return "DONE"; //$NON-NLS-1$
		} catch (Exception e) {
			return e.getLocalizedMessage();
		}				
	}
}
