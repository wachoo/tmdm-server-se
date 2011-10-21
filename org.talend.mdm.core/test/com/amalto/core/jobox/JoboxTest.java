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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.amalto.core.jobox.component.JobAware;
import com.amalto.core.jobox.component.JobInvoker;
import com.amalto.core.jobox.component.MDMJobInvoker;
import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxException;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2CtrlBean;
import junit.framework.TestCase;

@SuppressWarnings("nls")
public class JoboxTest extends TestCase {

    public static final String JOBOX_TEST_DIR = "/tmp/jobox";

    private JobContainer jobContainer;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        jobContainer = JobContainer.getUniqueInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        jobContainer.getJobDeployer().undeployAll();

        File deployDir = new File(jobContainer.getDeployDir());
        File[] files = deployDir.listFiles();
        for (File file : files) {
            file.delete();
        }

        jobContainer = null; // TODO shut down method?
    }

    public void testInit() throws Exception {
        //init
        Properties props = new Properties();
        props.put(JoboxConfig.JOBOX_HOME_PATH, JOBOX_TEST_DIR);
        jobContainer.init(props);

        File deployDir = new File(jobContainer.getDeployDir());
        assertTrue(deployDir.exists());

        File workDir = new File(jobContainer.getWorkDir());
        assertTrue(workDir.exists());
    }

    public void testFailedDeploy() throws Exception {
        //init
        Properties props = new Properties();
        props.put(JoboxConfig.JOBOX_HOME_PATH, JOBOX_TEST_DIR);
        jobContainer.init(props);

        try {
            jobContainer.getJobDeployer().deploy("testJob1.zip");
            fail();
        } catch (Exception e) {
            // Expected
        }
    }

    public void testSuccessfulDeploy() throws Exception {
        //init
        Properties props = new Properties();
        props.put(JoboxConfig.JOBOX_HOME_PATH, JOBOX_TEST_DIR);
        deployFileToJobox("testJob1.zip");
        jobContainer.init(props);

        JobInfo jobInfo = jobContainer.getJobInfo("TestTalendMDMJob", "0.1");
        assertNotNull(jobInfo);
        assertEquals("TestTalendMDMJob", jobInfo.getName());
        assertEquals("0.1", jobInfo.getVersion());
        assertEquals("tests.testtalendmdmjob_0_1.TestTalendMDMJob", jobInfo.getMainClass());
        assertEquals("Default", jobInfo.getContextStr());
        assertTrue(jobInfo.getDefaultParamMap().isEmpty());

        Set<JobInfo> allJobs = jobContainer.getAllJobInfo();
        assertEquals(1, allJobs.size());
        assertEquals(jobInfo, allJobs.iterator().next());


    }

    public void testJobClass() throws Exception {
        //init
        Properties props = new Properties();
        props.put(JoboxConfig.JOBOX_HOME_PATH, JOBOX_TEST_DIR);
        deployFileToJobox("testJob1.zip");
        jobContainer.init(props);

        JobInfo jobInfo = jobContainer.getJobInfo("TestTalendMDMJob", "0.1");
        assertNotNull(jobInfo);

        Class jobClass = jobContainer.getJobClass(jobInfo);
        assertNotSame(Thread.currentThread().getContextClassLoader(), jobClass.getClassLoader());
    }

    public void testJobAware() throws Exception {
        //init
        Properties props = new Properties();
        props.put(JoboxConfig.JOBOX_HOME_PATH, JOBOX_TEST_DIR);
        deployFileToJobox("testJob1.zip");
        jobContainer.init(props);

        JobAware jobAware = jobContainer.getJobAware();
        List<JobInfo> jobsInBox = jobAware.findJobsInBox();
        assertEquals(1, jobsInBox.size());
        assertEquals("TestTalendMDMJob", jobsInBox.get(0).getName());
        assertEquals("0.1", jobsInBox.get(0).getVersion());

        JobInfo jobInfo = jobAware.loadJobInfo("TestTalendMDMJob");
        assertNull(jobInfo);

        jobInfo = jobAware.loadJobInfo("TestTalendMDMJob_0.1");
        assertNotNull(jobInfo);
        assertEquals("TestTalendMDMJob", jobInfo.getName());
        assertEquals("0.1", jobInfo.getVersion());
    }

    public void testExecuteMDMJob() throws Exception {
        //init
        Properties props = new Properties();
        props.put(JoboxConfig.JOBOX_HOME_PATH, JOBOX_TEST_DIR);
        deployFileToJobox("testJob1.zip");
        jobContainer.init(props);

        JobInfo jobInfo = jobContainer.getJobInfo("TestTalendMDMJob", "0.1");
        assertNotNull(jobInfo);

        Class jobClass = jobContainer.getJobClass(jobInfo);
        assertNotNull(jobClass);
        assertEquals("tests.testtalendmdmjob_0_1.TestTalendMDMJob", jobClass.getName());

        assertNotSame(Thread.currentThread().getContextClassLoader(), jobClass.getClassLoader());

        Class[] interfaces = jobClass.getInterfaces();
        assertEquals(1, interfaces.length);
        assertEquals("routines.system.api.TalendMDMJob", interfaces[0].getName());

        JobInvoker invoker = jobContainer.getJobInvoker("TestTalendMDMJob", "0.1");
        assertEquals(MDMJobInvoker.class, invoker.getClass());

        HashMap<String, String> parameters = new HashMap<String, String>();
        parameters.put(TransformerV2CtrlBean.DEFAULT_VARIABLE, "<exchange><report></report><item><key>0</key></item></exchange>");

        String[][] result = invoker.call(parameters);
        assertEquals(1, result.length);
        assertEquals(1, result[0].length);
        assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<exchange><report/><item><key>0</key></item></exchange>", result[0][0]);

        try {
            invoker.call();
            fail("Caller must provide exchange message.");
        } catch (JoboxException e) {
            // Expected
        }
    }

    private static void deployFileToJobox(String jobZipFile) throws URISyntaxException, IOException {
        URL resource = JoboxTest.class.getResource(jobZipFile);
        assertNotNull(resource);
        URI uri = resource.toURI();
        File jobFile = new File(uri);

        // Copy zip file to jobox dir
        InputStream jobFileInputStream = null;
        OutputStream deployedFileOutputStream = null;
        File deployedFile = new File(JOBOX_TEST_DIR  + "/deploy/" + jobZipFile);
        if (!deployedFile.exists()) {
            deployedFile.createNewFile();
        }
        try {
            jobFileInputStream = new FileInputStream(jobFile);
            deployedFileOutputStream = new FileOutputStream(deployedFile);
            byte[] buffer = new byte[1024];
            int byteRead;
            while ((byteRead = jobFileInputStream.read(buffer)) > 0) {
                deployedFileOutputStream.write(buffer, 0, byteRead);
            }
        } finally {
            if (jobFileInputStream != null) {
                jobFileInputStream.close();
            }
            if (deployedFileOutputStream != null) {
                deployedFileOutputStream.close();
            }
        }
        assertTrue(deployedFile.exists());
        assertTrue(deployedFile.length() > 0);
        assertEquals(jobFile.length(), deployedFile.length());
    }
}
