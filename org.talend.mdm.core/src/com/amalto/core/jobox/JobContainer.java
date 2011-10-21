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

package com.amalto.core.jobox;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import com.amalto.core.jobox.component.JobAware;
import com.amalto.core.jobox.component.JobDeploy;
import com.amalto.core.jobox.component.JobInvoke;
import com.amalto.core.jobox.component.JobInvoker;
import com.amalto.core.jobox.component.MDMJobInvoker;
import com.amalto.core.jobox.util.JobClassLoader;
import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxUtil;
import com.amalto.core.jobox.watch.DirMonitor;
import com.amalto.core.jobox.watch.JoboxListener;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JobContainer {

    private static final int WATCH_INTERVAL = 2000;

    /** unique instance */
    private static final JobContainer instance = new JobContainer();

    private static final Logger LOGGER = Logger.getLogger(JobContainer.class);

    private final Map<JobInfo, JobClassLoader> jobLoadersPool = new HashMap<JobInfo, JobClassLoader>();

    private JoboxConfig joboxConfig = null;

    private JobAware jobAware = null;

    private JobDeploy jobDeploy = null;

    private DirMonitor monitor;

    /**
     * Private constructor
     */
    private JobContainer() {
    }

    /**
     * @return Returns the unique instance of this class. In order to improve the performance, removed synchronized, using pseudo
     * singleton mode
     */
    public static JobContainer getUniqueInstance() {
        return instance;
    }

    /**
     * Initializes the Jobox container with the <code>props</code> properties.
     * @param props A {@link Properties} instance used to create a {@link JoboxConfig}.
     */
    public void init(Properties props) {
        // init config
        joboxConfig = new JoboxConfig(props);
        // check home
        File joboxDeployPath = new File(joboxConfig.getDeployPath());
        File joboxWorkPath = new File(joboxConfig.getWorkPath());
        if (!joboxDeployPath.exists()) {
            if(!joboxDeployPath.mkdirs()) {
                // TODO Exception
            }
        }
        if (!joboxWorkPath.exists()) {
            if(!joboxWorkPath.mkdirs()) {
                // TODO Exception
            }
        }

        LOGGER.info("Jobox Home is: " + joboxConfig.getJoboxHome());//$NON-NLS-1$
        // init component
        jobAware = new JobAware(joboxConfig);
        jobDeploy = new JobDeploy(joboxConfig);
        // clear work folder
        JoboxUtil.cleanFolder(joboxConfig.getWorkPath());
        // redeploy all to work folder
        jobDeploy.deployAll();
        // init classpath
        this.jobLoadersPool.clear();
        List<JobInfo> currentJobs = jobAware.findJobsInBox();
        for (JobInfo jobInfo : currentJobs) {
            JobClassLoader cl = new JobClassLoader();
            cl.addPath(jobInfo.getClasspath());
            if (!this.jobLoadersPool.containsKey(jobInfo)) {
                this.jobLoadersPool.put(jobInfo, cl);
            }
        }
        // start monitor
        // Create the monitor
        monitor = new DirMonitor(WATCH_INTERVAL);
        // Add some files to listen for
        monitor.addFile(new File(this.getDeployDir()));
        // Add a jobox listener
        monitor.addListener(new JoboxListener());
    }

    public void updateJobLoadersPool(JobInfo jobInfo) {
        if (this.jobLoadersPool.containsKey(jobInfo)) {
            JobClassLoader jobClassLoader = jobLoadersPool.get(jobInfo);
            LOGGER.info("Removing " + jobClassLoader);//$NON-NLS-1$
            jobClassLoader = null;
            jobLoadersPool.remove(jobInfo);
        }

        JobClassLoader cl = new JobClassLoader();
        cl.addPath(jobInfo.getClasspath());
        this.jobLoadersPool.put(jobInfo, cl);
    }

    public void removeFromJobLoadersPool(String jobEntityName) {
        // parse name and version
        String jobVersion = StringUtils.EMPTY; //$NON-NLS-1$
        String jobName = StringUtils.EMPTY; //$NON-NLS-1$
        Matcher m = JobAware.jobVersionNamePattern.matcher(jobEntityName);
        while (m.find()) {
            jobName = m.group(1);
            jobVersion = m.group(m.groupCount());
        }

        JobInfo jobInfo = new JobInfo(jobName, jobVersion);

        if (this.jobLoadersPool.containsKey(jobInfo)) {
            JobClassLoader jobClassLoader = jobLoadersPool.get(jobInfo);
            LOGGER.info("Removing " + jobClassLoader); //$NON-NLS-1$
            jobClassLoader = null;
            jobLoadersPool.remove(jobInfo);
        }

    }

    public JobAware getJobAware() {
        return jobAware;
    }

    public JobDeploy getJobDeployer() {
        return jobDeploy;
    }

    public JobInvoker getJobInvoker(String jobName, String version) {
        JobInfo jobInfo = getJobInfo(jobName, version);
        Class jobClass = getJobClass(jobInfo);
        Class[] interfaces = jobClass.getInterfaces();
        if (interfaces.length > 0) {
            for (Class currentInterface : interfaces) {
                if ("routines.system.api.TalendMDMJob".equals(currentInterface.getName())) {
                    return new MDMJobInvoker(jobInfo);
                }
            }
        }

        // Default invocation
        return new JobInvoke(jobInfo);
    }

    /**
     * @return Returns the deploy directory for the jobs.
     * @see com.amalto.core.jobox.util.JoboxConfig#getDeployPath()
     */
    public String getDeployDir() {
        return joboxConfig.getDeployPath();
    }

    /**
     * @return Returns the deploy directory for the jobs.
     * @see com.amalto.core.jobox.util.JoboxConfig#getWorkPath()
     */
    public String getWorkDir() {
        return joboxConfig.getWorkPath();
    }

    /**
     * @param jobName A job name
     * @param jobVersion A job version
     * @return Returns the {@link JobInfo} instance for this job if it exists, <code>null</code> otherwise.
     */
    public JobInfo getJobInfo(String jobName, String jobVersion) {
        JobInfo jobInfoPK = new JobInfo(jobName, jobVersion);
        Set<JobInfo> jobInformation = jobLoadersPool.keySet();
        for (JobInfo jobInfo : jobInformation) {
            if (jobInfo.equals(jobInfoPK)) {
                return jobInfo;
            }
        }
        return null;
    }

    /**
     * @return Returns information about all jobs deployed in this Jobox container.
     */
    public Set<JobInfo> getAllJobInfo() {
        return jobLoadersPool.keySet();
    }

    public Class getJobClass(JobInfo jobInfo) {
        JobClassLoader jobClassLoader = this.jobLoadersPool.get(jobInfo);
        try {
            return jobClassLoader.loadClass(jobInfo.getMainClass());
        } catch (ClassNotFoundException e) {
            LOGGER.error("Could not find class '" + jobInfo.getMainClass() + "'", e);
            return null;
        }
    }

    public void setContextStrToBeSaved(String entryPath, String cxt) {
        monitor.changeContextStr(entryPath, cxt);
        int idxSeparator = entryPath.lastIndexOf(File.separatorChar);
        if (idxSeparator != -1) {
            String entryName = entryPath.substring(idxSeparator + 1);
            jobDeploy.deploy(entryName);
        }

    }
}
