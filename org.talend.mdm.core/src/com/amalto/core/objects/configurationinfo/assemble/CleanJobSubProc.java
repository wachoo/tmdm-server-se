package com.amalto.core.objects.configurationinfo.assemble;

import javax.naming.InitialContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.util.XtentisException;

public class CleanJobSubProc extends AssembleSubProc{


	
	@Override
	public void run() throws Exception {
		//clean-up background Job
		XmlServerSLWrapperLocal server = null;
		try {
			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Auto Configuration in the background: unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new RuntimeException(err);
		}
		
		//zap Background jobs
		try {
	        server.deleteCluster(null, ObjectPOJO.getCluster(BackgroundJobPOJO.class));
	        server.createCluster(null, ObjectPOJO.getCluster(BackgroundJobPOJO.class));
        } catch (XtentisException e) {
        	String err = "Cleanup of Jobs failed!";
			org.apache.log4j.Logger.getLogger(this.getClass()).warn(err,e);
        }
		
	}	

}
