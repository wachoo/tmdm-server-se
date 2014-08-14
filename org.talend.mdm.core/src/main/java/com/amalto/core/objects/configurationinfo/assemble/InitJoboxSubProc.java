package com.amalto.core.objects.configurationinfo.assemble;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.util.JoboxConfig;
import org.apache.log4j.Logger;

public class InitJoboxSubProc extends AssembleSubProc{

    protected static final Logger LOGGER = Logger.getLogger(InitJoboxSubProc.class);

    @Override
    public void run() throws Exception {
        String appHomePath = com.amalto.core.util.Util.getAppServerDeployDir();
        if (!new File(appHomePath).exists()) {
            throw new FileNotFoundException();
        }
        String jbossHome = new File(appHomePath).getAbsolutePath();
        //init
        JobContainer jobContainer = JobContainer.getUniqueInstance();
        Properties props = new Properties();
        props.put(JoboxConfig.JOBOX_HOME_PATH, jbossHome + File.separator + "jobox");
        try {
            jobContainer.init(props);
        } catch (Exception e) {
            LOGGER.error("Could not start job container.");
        }
    }
}
