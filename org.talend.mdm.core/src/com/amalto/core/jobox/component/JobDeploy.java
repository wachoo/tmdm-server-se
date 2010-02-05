package com.amalto.core.jobox.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.amalto.core.jobox.util.JoboxConfig;
import com.amalto.core.jobox.util.JoboxUtil;

public class JobDeploy {

	private String deployDir;
	private String workDir;

	public JobDeploy(JoboxConfig joboxConfig) {
		this.deployDir=joboxConfig.getDeployPath();
		this.workDir=joboxConfig.getWorkPath();
	}

	public void deployAll() {
		String[] filenames=new File(deployDir).list();
		for (String filename : filenames) {
			deploy(filename);
		}
	}
	
	/**
	 * @param jobPackageName
	 */
	public void deploy(String jobPackageName) {
		try {
			extract(deployDir+File.separator+jobPackageName,workDir+File.separator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		org.apache.log4j.Logger.getLogger(this.getClass()).info("Job "+jobPackageName+" has been deployed successfully! ");
	}
	
	public void undeploy(String jobEntityName) {
		JoboxUtil.delFolder(workDir+File.separator+jobEntityName);
		org.apache.log4j.Logger.getLogger(this.getClass()).info("Job "+jobEntityName+" has been undeployed successfully! ");
	}
	
	 private ArrayList extract(String sZipPathFile, String sDestPath) throws Exception{
		  
		   ArrayList allFileName = new ArrayList();
		  
		   FileInputStream fins = new FileInputStream(sZipPathFile);
		   ZipInputStream zins = new ZipInputStream(fins);
		   ZipEntry ze = null;
		   byte ch[] = new byte[256];
		   while((ze = zins.getNextEntry()) != null){
			File zfile = new File(sDestPath + ze.getName());
			File fpath = new File(zfile.getParentFile().getPath());
			    
		    if(ze.isDirectory()){
		     if(!zfile.exists())
		      zfile.mkdirs();
		     zins.closeEntry();
		    }else{
		     if(!fpath.exists())
		      fpath.mkdirs();
		     FileOutputStream fouts = new FileOutputStream(zfile);
		     int i;
		     allFileName.add(zfile.getAbsolutePath());
		     while((i = zins.read(ch)) != -1)
		      fouts.write(ch,0,i);
		      zins.closeEntry();
		      fouts.close();
		    }
		   }
		   fins.close();
		   zins.close();

		  return allFileName;
		  
	}

}
