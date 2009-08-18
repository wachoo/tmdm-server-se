/* 
 * Copyright 2005 OpenSymphony 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not 
 * use this file except in compliance with the License. You may obtain a copy 
 * of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 *   
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT 
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the 
 * License for the specific language governing permissions and limitations 
 * under the License.
 * 
 */

package talend.ext.service.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import talend.ext.service.util.CommonUtil;

import com.amalto.core.util.XtentisException;


public class ServiceJob implements Job{

    private static Log _log = LogFactory.getLog(ServiceJob.class);

    public ServiceJob() {
    }

    /**
     * <p>
     * Called by the <code>{@link org.quartz.Scheduler}</code> when a
     * <code>{@link org.quartz.Trigger}</code> fires that is associated with
     * the <code>Job</code>.
     * </p>
     * 
     * @throws JobExecutionException
     *             if there is an exception while executing the job.
     */
    public void execute(JobExecutionContext context)
        throws JobExecutionException {
    	
    		
    		String jobName=context.getJobDetail().getName();
        	
        	JobDataMap data = context.getJobDetail().getJobDataMap();
        	String serviceName=(String) (data.get("serviceName")==null?"":data.get("serviceName"));
        	String methodName=(String) (data.get("methodName")==null?"":data.get("methodName"));
        	String parameters=(String) (data.get("parameters")==null?"":data.get("parameters"));
        	
        	_log.info("["+jobName+"]:Scheduling Job... ");
        	
        	try {
    			CommonUtil.invokeService(serviceName, methodName, parameters);
    		} catch (XtentisException e) {
    			e.printStackTrace();
    			_log.error("["+jobName+"]:Occurred error while scheduling! "+e);
    		}
    	
    }

	

}