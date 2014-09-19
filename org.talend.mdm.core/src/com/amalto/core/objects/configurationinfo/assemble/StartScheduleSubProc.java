package com.amalto.core.objects.configurationinfo.assemble;

import com.amalto.core.util.Util;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

public class StartScheduleSubProc extends AssembleSubProc {

    public static final Logger LOGGER = Logger.getLogger(StartScheduleSubProc.class);

    @Override
    public void run() throws Exception {
        if (Util.isEnterprise()) {
            String ip = "127.0.0.1";
            //FIXME:port maybe change
            String port = MDMConfiguration.getHttpPort();
            String uri = "http://" + ip + ":" + port + "/SrvSchedule/SrvScheduleServlet?action=startup";
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(uri);
            client.setConnectionTimeout(30000);
            int status = client.executeMethod(get);
            if (status != 200) {
                LOGGER.warn("Start up service schedule engine failed! ");
            }
        }
    }

}
