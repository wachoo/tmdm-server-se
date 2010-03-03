package com.amalto.core.jobox.watch;

import java.util.Iterator;
import java.util.List;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.JobInfo;
import com.amalto.core.jobox.util.JoboxUtil;

public class JoboxListener implements DirListener
{



	public void fileChanged(List newFiles, List deleteFiles, List modifyFiles) {

		if(newFiles.size()>0) {
			//new
			for (Iterator iterator = newFiles.iterator(); iterator.hasNext();) {
				String jobPackageName = (String) iterator.next();
				//deploy
				JobContainer.getUniqueInstance().getJobDeploy().deploy(jobPackageName);
				//add to classpath
				JobInfo jobInfo=JobContainer.getUniqueInstance().getJobAware().loadJobInfo(JoboxUtil.trimExtension(jobPackageName));
				JobContainer.getUniqueInstance().updateJobLoadersPool(jobInfo);
			}
		}
		
		if(deleteFiles.size()>0) {
			//delete
			for (Iterator iterator = deleteFiles.iterator(); iterator.hasNext();) {
				String jobPackageName = (String) iterator.next();
				String jobEntityName = JoboxUtil.trimExtension(jobPackageName);
				//undeploy
				JobContainer.getUniqueInstance().getJobDeploy().undeploy(jobEntityName);
				//remove classpath
				JobContainer.getUniqueInstance().removeFromJobLoadersPool(jobEntityName);
			}
		}
		
		
		if(modifyFiles.size()>0) {
			//modify
			for (Iterator iterator = modifyFiles.iterator(); iterator.hasNext();) {
				String jobPackageName = (String) iterator.next();
				//deploy
				JobContainer.getUniqueInstance().getJobDeploy().deploy(jobPackageName);
				//add to classpath
				JobInfo jobInfo=JobContainer.getUniqueInstance().getJobAware().loadJobInfo(JoboxUtil.trimExtension(jobPackageName));
				JobContainer.getUniqueInstance().updateJobLoadersPool(jobInfo);
			}
		}
	}
}
