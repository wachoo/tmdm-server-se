package com.amalto.core.jobox.component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.JobInfo;
import com.amalto.core.jobox.JobInvokeConfig;
import com.amalto.core.jobox.util.JobNotFoundException;
import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxException;
import com.amalto.core.jobox.util.MissingMainClassException;

public class JobInvoke {

    private String workDir;

    public JobInvoke(JoboxConfig joboxConfig) {
        this.workDir = joboxConfig.getWorkPath();
    }

    public String[][] call(JobInvokeConfig config, Map<String, String> inputMap) {
        return call(config.getJobName(), config.getJobVersion(), config.getJobMainClass(), inputMap);
    }

    /**
     * @param jobName
     * @param jobVersion
     * @return
     */
    public String[][] call(String jobName, String jobVersion) {
        return call(jobName, jobVersion, null, null);
    }

    /**
     * @param jobName
     * @param jobVersion
     * @param mainClass
     * @return
     */
    public String[][] call(String jobName, String jobVersion, String mainClass) {
        return call(jobName, jobVersion, mainClass, null);
    }

    /**
     * @param jobName
     * @param jobVersion
     * @param inputMap
     * @return
     * @throws JoboxException
     */
    public String[][] call(String jobName, String jobVersion, Map<String, String> inputMap) throws JoboxException {
        return call(jobName, jobVersion, null, inputMap);
    }

    /**
     * @param jobName
     * @param jobVersion
     * @param mainClass
     * @param inputMap
     * @return
     */
    public String[][] call(String jobName, String jobVersion, String mainClass, Map<String, String> inputMap)
            throws JoboxException {
        // showResult(execCmd("hello_run.bat",null,"F:/jobox/work/hello_0.1/hello"));
        String[][] result = null;

        try {
            JobInfo jobInfo = JobContainer.getUniqueInstance().getJobInfo(jobName, jobVersion);
            if (jobInfo == null)
                throw new JobNotFoundException();
            if (mainClass == null) {
                if (jobInfo.getMainclass() == null)
                    throw new MissingMainClassException();
            } else {
                jobInfo.setMainclass(mainClass);
            }

            Class jobclass = JobContainer.getUniqueInstance().getJobClass(jobInfo);
            Method runJobMethod = jobclass.getMethod("runJob", String[].class);//$NON-NLS-1$

            Map<String, String> paramMap = jobInfo.getDefaultParamMap();
            // merge with default map
            if (inputMap != null) {
                for (Iterator<String> iterator = inputMap.keySet().iterator(); iterator.hasNext();) {
                    String inputParamName = iterator.next();
                    String inputParamValue = inputMap.get(inputParamName);
                    paramMap.put(inputParamName, inputParamValue);
                }
            }

            List<String> params = new ArrayList<String>();
            for (Iterator<String> iterator = paramMap.keySet().iterator(); iterator.hasNext();) {
                String paramName = iterator.next();
                String paramValue = paramMap.get(paramName);
                if (paramName != null && paramValue != null) {
                    params.add("--context_param" + " " + paramName + "=" + paramValue);//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
                }
            }

            Object jobInstance = jobclass.newInstance();
            Object returnObject = runJobMethod.invoke(jobInstance, (Object) params.toArray(new String[params.size()]));
            result = (String[][]) returnObject;

        } catch (JobNotFoundException e) {
            throw (e);
        } catch (Exception e) {
            e.printStackTrace();
            throw new JoboxException();
        }

        return result;
    }

    public Process execCmd(String command, String[] envp, String dirPath) {

        Runtime run = Runtime.getRuntime();
        Process pro = null;
        try {
            pro = run.exec("cmd /c" + command, envp, new File(dirPath));//$NON-NLS-1$
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pro;
    }

    public void showResult(Process pro) {
        InputStream is = pro.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String s = null;
        try {
            s = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (s != null) {
            System.out.println(s);
            try {
                s = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
