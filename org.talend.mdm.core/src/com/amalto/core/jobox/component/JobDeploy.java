// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.jobox.component;

import java.io.File;

import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxException;
import com.amalto.core.jobox.util.JoboxUtil;
import org.apache.log4j.Logger;

/**
 * This class handles all deployment relative actions for the Jobox container.
 */
public class JobDeploy {
    
    private static final Logger LOGGER = Logger.getLogger(JobDeploy.class);
    
    private final String deployDir;

    private final String workDir;

    /**
     * @param joboxConfig A {@link JoboxConfig} configuration.
     */
    public JobDeploy(JoboxConfig joboxConfig) {
        this.deployDir = joboxConfig.getDeployPath();
        this.workDir = joboxConfig.getWorkPath();
    }

    /**
     * Force deployment of all files in deploy directory.
     * @see com.amalto.core.jobox.util.JoboxConfig#getDeployPath()
     * @see #deploy(String)
     */
    public void deployAll() {
        String[] fileNames = new File(deployDir).list();
        for (String filename : fileNames) {
            deploy(filename);
        }
    }

    /**
     * @param jobName 'Deploys' the job in the Jobox container. This operation consists in unzipping the job zip file
     * in the <em>deploy</em> directory in the <em>work</em> directory.
     * @see com.amalto.core.jobox.util.JoboxConfig#getDeployPath()
     * @see com.amalto.core.jobox.util.JoboxConfig#getWorkPath()
     */
    public void deploy(String jobName) {
        try {
            JoboxUtil.extract(deployDir + File.separator + jobName, workDir + File.separator);
        } catch (Exception e) {
            LOGGER.error("Job " + jobName + " has not been deployed due to exception:", e);//$NON-NLS-1$//$NON-NLS-2$
            throw new JoboxException(e);
        }
        LOGGER.info("Job " + jobName + " has been deployed successfully! ");//$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * @param jobEntityName 'Undeploys' the job in the Jobox container. This operation consists in deleting the job from the <em>work</em>
     * directory.
     * @see com.amalto.core.jobox.util.JoboxConfig#getWorkPath()
     */
    public void undeploy(String jobEntityName) {
        JoboxUtil.deleteFolder(workDir + File.separator + jobEntityName);
        LOGGER.info("Job " + jobEntityName + " has been undeployed successfully! ");//$NON-NLS-1$//$NON-NLS-2$
    }

    /**
     * Undeploy all files in deploy directory.
     * @see com.amalto.core.jobox.util.JoboxConfig#getDeployPath()
     */
    public void undeployAll() {
        String[] fileNames = new File(deployDir).list();
        for (String filename : fileNames) {
            undeploy(filename);
        }
    }
}
