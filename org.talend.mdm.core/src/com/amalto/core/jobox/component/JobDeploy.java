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
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxException;
import com.amalto.core.jobox.util.JoboxUtil;

/**
 * This class handles all deployment relative actions for the Jobox container.
 */
public class JobDeploy {
    
    private static final Logger LOGGER = Logger.getLogger(JobDeploy.class);
    
    private final String deployDir;

    private final String workDir;

    private  Map<String, AtomicInteger> failedDeployJob = new HashMap<String, AtomicInteger>();

    private int jobReDeployMaxTimes = 3;

    /**
     * @param joboxConfig A {@link JoboxConfig} configuration.
     */
    public JobDeploy(JoboxConfig joboxConfig) {
        this.deployDir = joboxConfig.getDeployPath();
        this.workDir = joboxConfig.getWorkPath();
        String times = MDMConfiguration.getConfiguration().getProperty("job.redeploy.max.times"); //$NON-NLS-1$
        if (times != null) {
            jobReDeployMaxTimes = Integer.valueOf(times);
        }
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
    public void deploy(final String jobName) {
        try {
            JoboxUtil.extract(deployDir + File.separator + jobName, workDir + File.separator);
            LOGGER.info("Job " + jobName + " has been deployed successfully! ");//$NON-NLS-1$//$NON-NLS-2$            
        } catch (Exception e) {

            Timer timer = new Timer();
            timer.schedule(new RedeployJobTask(jobName), 100, 2000);
            synchronized (failedDeployJob) {
                AtomicInteger leftTimes = failedDeployJob.get(jobName);
                if (leftTimes.get() >= jobReDeployMaxTimes) {
                    LOGGER.error("Job " + jobName + " has not been deployed due to exception:" + e.getLocalizedMessage());//$NON-NLS-1$//$NON-NLS-2$
                    throw new JoboxException(e);
                }
            }
        }


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

    /**
     * 
     * DOC achen JobDeploy class global comment. Detailled comment
     */
    private class RedeployJobTask extends TimerTask {

        String jobName;

        RedeployJobTask(String jobName) {
            this.jobName = jobName;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.util.TimerTask#run()
         */
        @Override
        public void run() {
            synchronized (failedDeployJob) {
                AtomicInteger leftTimes = failedDeployJob.get(jobName);
                if (leftTimes == null) {
                    leftTimes = new AtomicInteger(1);
                    failedDeployJob.put(jobName, leftTimes);
                }
                if (leftTimes.get() < jobReDeployMaxTimes) {
                    leftTimes.incrementAndGet();
                    failedDeployJob.put(jobName, leftTimes);
                    LOGGER.info("Redeploy Job " + jobName + " " + leftTimes.get() + " times."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    deploy(jobName);
                }
            }
        }
    }
}
