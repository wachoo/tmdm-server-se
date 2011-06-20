package com.amalto.core.jobox;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;

import com.amalto.core.jobox.component.JobAware;
import com.amalto.core.jobox.component.JobDeploy;
import com.amalto.core.jobox.component.JobInvoke;
import com.amalto.core.jobox.util.JobClassLoader;
import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxUtil;
import com.amalto.core.jobox.watch.DirMonitor;
import com.amalto.core.jobox.watch.JoboxListener;

public class JobContainer {

    private static final int WATCH_INTERVAL = 2000;

    /** unique instance */
    private static JobContainer sInstance = null;

    private JoboxConfig joboxConfig = null;

    private JobAware jobAware = null;

    private JobDeploy jobDeploy = null;

    private JobInvoke jobInvoke = null;

    private Map<JobInfo, JobClassLoader> jobLoadersPool = new HashMap<JobInfo, JobClassLoader>();

    private DirMonitor monitor;

    /**
     * Private constuctor
     */
    private JobContainer() {
        super();
    }

    /**
     * Get the unique instance of this class. In order to improve the performance, removed synchronized, using pseudo
     * singleton mode
     */
    public static JobContainer getUniqueInstance() {

        if (sInstance == null) {
            sInstance = new JobContainer();
        }

        return sInstance;

    }

    public void init(Properties props) {
        // init config
        joboxConfig = new JoboxConfig(props);
        // check home
        File joboxDeployPath = new File(joboxConfig.getDeployPath());
        File joboxWorkPath = new File(joboxConfig.getWorkPath());
        if (!joboxDeployPath.exists())
            joboxDeployPath.mkdirs();
        if (!joboxWorkPath.exists())
            joboxWorkPath.mkdirs();
        org.apache.log4j.Logger.getLogger(this.getClass()).info("Jobox Home is: " + joboxConfig.getJoboxHome());//$NON-NLS-1$
        // init component
        jobAware = new JobAware(joboxConfig);
        jobDeploy = new JobDeploy(joboxConfig);
        jobInvoke = new JobInvoke(joboxConfig);
        // clear work folder
        JoboxUtil.cleanFolder(joboxConfig.getWorkPath());
        // redeploy all to work folder
        jobDeploy.deployAll();
        // init classpath
        this.jobLoadersPool.clear();
        List<JobInfo> currentJobs = jobAware.findJobsInBox();
        for (Iterator iterator = currentJobs.iterator(); iterator.hasNext();) {
            JobInfo jobInfo = (JobInfo) iterator.next();

            JobClassLoader cl = new JobClassLoader();
            cl.addPath(jobInfo.getClasspath());
            if (!this.jobLoadersPool.containsKey(jobInfo))
                this.jobLoadersPool.put(jobInfo, cl);
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
            org.apache.log4j.Logger.getLogger(this.getClass()).info("Removing " + jobClassLoader);//$NON-NLS-1$
            jobClassLoader = null;
            jobLoadersPool.remove(jobInfo);
        }

        JobClassLoader cl = new JobClassLoader();
        cl.addPath(jobInfo.getClasspath());
        this.jobLoadersPool.put(jobInfo, cl);
    }

    public void removeFromJobLoadersPool(String jobEntityName) {

        // parse name and version
        String jobVersion = "";//$NON-NLS-1$
        String jobName = "";//$NON-NLS-1$
        Matcher m = JobAware.jobVersionNamePattern.matcher(jobEntityName);
        while (m.find()) {
            jobName = m.group(1);
            jobVersion = m.group(m.groupCount());
        }

        JobInfo jobInfo = new JobInfo(jobName, jobVersion);

        if (this.jobLoadersPool.containsKey(jobInfo)) {
            JobClassLoader jobClassLoader = jobLoadersPool.get(jobInfo);
            org.apache.log4j.Logger.getLogger(this.getClass()).info("Removing " + jobClassLoader);//$NON-NLS-1$
            jobClassLoader = null;
            jobLoadersPool.remove(jobInfo);
        }

    }

    public JobAware getJobAware() {
        return jobAware;
    }

    public JobDeploy getJobDeploy() {
        return jobDeploy;
    }

    public JobInvoke getJobInvoke() {
        return jobInvoke;
    }

    public String getDeployDir() {
        return joboxConfig.getDeployPath();
    }

    public JobInfo getJobInfo(String jobName, String jobVersion) {

        JobInfo theJobInfo = null;
        JobInfo jobInfoPK = new JobInfo(jobName, jobVersion);

        Set<JobInfo> jobInfos = this.jobLoadersPool.keySet();

        for (JobInfo jobInfo : jobInfos) {
            if (jobInfo.equals(jobInfoPK)) {
                theJobInfo = jobInfo;
                break;
            }
        }

        return theJobInfo;
    }

    public Class getJobClass(JobInfo jobInfo) {
        Class clazz = null;
        JobClassLoader jobClassLoader = this.jobLoadersPool.get(jobInfo);
        try {
            clazz = jobClassLoader.loadClass(jobInfo.getMainclass());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }

    public void setContextStrToBeSaved(String entryPath, String cxt) {
        monitor.changeContextStr(entryPath, cxt);
        int seporIdx = entryPath.lastIndexOf(File.separatorChar);
        if (seporIdx != -1) {
            String entryName = entryPath.substring(seporIdx + 1);
            jobDeploy.deploy(entryName);
        }

    }
}
