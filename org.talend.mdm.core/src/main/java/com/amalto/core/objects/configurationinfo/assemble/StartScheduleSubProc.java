package com.amalto.core.objects.configurationinfo.assemble;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.util.Util;

public class StartScheduleSubProc extends AssembleSubProc {

	@Override
	public void run() throws Exception {

		// workflow service

		if (Util.isEnterprise()) {
			
			String ip="127.0.0.1";
//			try {
//				InetAddress addr = InetAddress.getLocalHost();
//				ip =addr.getHostAddress().toString();
//			} catch (UnknownHostException e2) {
//				e2.printStackTrace();
//			}
			//FIXME:port maybe change
            String port = MDMConfiguration.getHttpPort();

			String uri="http://"+ip+":"+port+"/SrvSchedule/SrvScheduleServlet?action=startup";

			HttpClient client = new HttpClient();
			GetMethod get = new GetMethod(uri);

			client.setConnectionTimeout(30000);

			int status=client.executeMethod(get);
			if(status!=200)org.apache.log4j.Logger.getLogger(this.getClass()).warn("Start up service schedule engine failed! ");

		}

	}

}
