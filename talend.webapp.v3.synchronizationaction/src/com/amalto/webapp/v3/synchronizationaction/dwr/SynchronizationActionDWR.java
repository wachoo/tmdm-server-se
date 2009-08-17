package com.amalto.webapp.v3.synchronizationaction.dwr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSGetSynchronizationPlanPKs;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanAction;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanActionCode;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanPK;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanPKArray;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanStatus;
import com.amalto.webapp.v3.synchronizationaction.bean.ServerURL;
import com.amalto.webapp.v3.synchronizationaction.bean.SyncInfo;
import com.amalto.webapp.v3.synchronizationaction.bean.SyncStatus;


public class SynchronizationActionDWR {
	public static final String SAVED_SERVER_URL="save.server.url";
	public static final String SERVER_URL_DEFAULT="http://localhost:8080/talend/TalendPort";
	private Logger logger=org.apache.log4j.Logger.getLogger(this.getClass());
	
	public SynchronizationActionDWR() {
		super();
	}
	
	public String[] getSyncNames(SyncInfo info)throws Exception{
		try{
			WSSynchronizationPlanPKArray array
				= Util.getPort(info.getServerURL(),info.getUsername(),info.getPassword(),Util._FORCE_WEB_SERVICE_).getSynchronizationPlanPKs(new WSGetSynchronizationPlanPKs(".*"));
			WSSynchronizationPlanPK[] pks=array.getWsSynchronizationPlanPK();
			if(pks!=null && pks.length>0){
				logger.debug("pks:"+pks.length);
				String[] syncNames=new String[pks.length];
				for(int i=0; i<pks.length; i++){
					syncNames[i]=pks[i].getPk();
				}
				logger.debug("getSyncNames() syncNames:"+Arrays.asList(syncNames));
				return syncNames;
			}else
				return new String[0];
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}
	
	public void startFull(SyncInfo info)throws Exception {
		try {
			Util.getPort(info.getServerURL(),info.getUsername(),info.getPassword(),Util._FORCE_WEB_SERVICE_).synchronizationPlanAction(new WSSynchronizationPlanAction(
                	new WSSynchronizationPlanPK(info.getSyncName()),
                	WSSynchronizationPlanActionCode.START_FULL
                ));
		} catch (Exception e) {
			e.printStackTrace();
			//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
			//if (m.matches()) throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}
	
	public void startDifferent(SyncInfo info)throws Exception {
		try {

			Util.getPort(info.getServerURL(),info.getUsername(),info.getPassword(),Util._FORCE_WEB_SERVICE_).synchronizationPlanAction(new WSSynchronizationPlanAction(
                	new WSSynchronizationPlanPK(info.getSyncName()),
                	WSSynchronizationPlanActionCode.START_DIFFERENTIAL
                ));
		} catch (Exception e) {
			e.printStackTrace();
			//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
			//if (m.matches()) throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}
	public void stop(SyncInfo info)throws Exception {
		try {

			Util.getPort(info.getServerURL(),info.getUsername(),info.getPassword(),Util._FORCE_WEB_SERVICE_).synchronizationPlanAction(new WSSynchronizationPlanAction(
                	new WSSynchronizationPlanPK(info.getSyncName()),
                	WSSynchronizationPlanActionCode.STOP
                ));
		} catch (Exception e) {
			e.printStackTrace();
			//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
			//if (m.matches()) throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}
	public void reset(SyncInfo info)throws Exception {
		try {

			Util.getPort(info.getServerURL(),info.getUsername(),info.getPassword(),Util._FORCE_WEB_SERVICE_).synchronizationPlanAction(new WSSynchronizationPlanAction(
                	new WSSynchronizationPlanPK(info.getSyncName()),
                	WSSynchronizationPlanActionCode.RESET
                ));
		} catch (Exception e) {
			e.printStackTrace();
			//Matcher m = Pattern.compile("(.*Exception:)(.+)",Pattern.DOTALL).matcher(e.getLocalizedMessage());
			//if (m.matches()) throw new Exception(m.group(2));
			throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
		}
	}

	public SyncStatus getStatus(SyncInfo info)throws Exception {
		WSSynchronizationPlanStatus wsStatus = null;
		try {
	        wsStatus = Util.getPort(info.getServerURL(),info.getUsername(),info.getPassword(),Util._FORCE_WEB_SERVICE_).synchronizationPlanAction(new WSSynchronizationPlanAction(
	        	new WSSynchronizationPlanPK(info.getSyncName()),
	        	WSSynchronizationPlanActionCode.STATUS
	        ));
	        SyncStatus status=new SyncStatus();
	        status.setValue(wsStatus.getWsStatusCode().getValue());
	        status.setMessage(wsStatus.getStatusMessage());
	        return status;
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
        }
	}
	
	public ListRange getSavedURLs()throws Exception{
		String[] urls=new String[]{SERVER_URL_DEFAULT};
		try{
			Properties configure= MDMConfiguration.getConfiguration();
			String url=configure.getProperty(SAVED_SERVER_URL);
			if(url!=null){
				urls= url.split(";");
			}
			Arrays.sort(urls);
			List<ServerURL> list=new ArrayList<ServerURL>();
			for(String id: urls){
				ServerURL item=new ServerURL();
				item.setId(id);
				item.setName(id);
				list.add(item);
			}
			ListRange listRange = new ListRange();
			listRange.setData(list.toArray(new ServerURL[list.size()]));
			listRange.setTotalSize(urls.length);
			return listRange;
		} catch (Exception e) {
        	e.printStackTrace();
        	throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
        }
	
	}
	
	public void saveURLs(String url)throws Exception{
		logger.debug("saveURLs()---- url =="+url);
		try{	
			
			String[] urls=url.split(";");
			if(urls.length==0)return;
			Properties configure= MDMConfiguration.getConfiguration();
			StringBuffer sb=new StringBuffer();
			for(int i=0; i<urls.length; i++){
				if(i<urls.length-1)
					sb.append(urls[i]).append(";");
				else
					sb.append(urls[i]);
			}
			configure.setProperty(SAVED_SERVER_URL, sb.toString());
			
			MDMConfiguration.save();
		} catch (Exception e) {
        	e.printStackTrace();
        	throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
        }
	}
}
