package com.amalto.core.jobox.component;

import java.io.File;

import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxUtil;

public class JobDeploy {

    private String deployDir;

    private String workDir;

    public JobDeploy(JoboxConfig joboxConfig) {
        this.deployDir = joboxConfig.getDeployPath();
        this.workDir = joboxConfig.getWorkPath();
    }

    public void deployAll() {
        String[] filenames = new File(deployDir).list();
        for (String filename : filenames) {
            deploy(filename);
        }
    }

    /**
     * @param jobPackageName
     */
    public void deploy(String jobPackageName) {
        try {
            JoboxUtil.extract(deployDir + File.separator + jobPackageName, workDir + File.separator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        org.apache.log4j.Logger.getLogger(this.getClass()).info("Job " + jobPackageName + " has been deployed successfully! ");//$NON-NLS-1$//$NON-NLS-2$
    }

    public void undeploy(String jobEntityName) {
        JoboxUtil.delFolder(workDir + File.separator + jobEntityName);
        org.apache.log4j.Logger.getLogger(this.getClass()).info("Job " + jobEntityName + " has been undeployed successfully! ");//$NON-NLS-1$//$NON-NLS-2$
    }

}
