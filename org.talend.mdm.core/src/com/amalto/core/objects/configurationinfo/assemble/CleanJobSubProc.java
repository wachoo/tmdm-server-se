package com.amalto.core.objects.configurationinfo.assemble;

import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.backgroundjob.BackgroundJobPOJO;
import com.amalto.core.server.api.XmlServer;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;

public class CleanJobSubProc extends AssembleSubProc{

    private final static Logger LOGGER = Logger.getLogger(CleanJobSubProc.class);

    @Override
    public void run() throws Exception {
        //clean-up background Job
		XmlServer server;
        try {
            server = Util.getXmlServerCtrlLocal();
        } catch (Exception e) {
            String err = "Auto Configuration in the background: unable to access the XML Server wrapper";
            LOGGER.error(err, e);
            throw new RuntimeException(err, e);
        }
        // zap Background jobs
        try {
            server.deleteCluster(ObjectPOJO.getCluster(BackgroundJobPOJO.class));
            server.createCluster(ObjectPOJO.getCluster(BackgroundJobPOJO.class));
        } catch (XtentisException e) {
            LOGGER.warn("Cleanup of Jobs failed!", e);
        }
    }

}
