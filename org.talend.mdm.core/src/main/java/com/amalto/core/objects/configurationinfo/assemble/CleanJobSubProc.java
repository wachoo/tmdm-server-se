package com.amalto.core.objects.configurationinfo.assemble;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import org.talend.mdm.server.api.XmlServer;
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
            server.deleteCluster(null, ObjectPOJO.getCluster(BackgroundJobPOJO.class));
            server.createCluster(null, ObjectPOJO.getCluster(BackgroundJobPOJO.class));
        } catch (XtentisException e) {
            LOGGER.warn("Cleanup of Jobs failed!", e);
        }
    }

}
