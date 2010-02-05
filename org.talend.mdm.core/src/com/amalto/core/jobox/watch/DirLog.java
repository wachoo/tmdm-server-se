package com.amalto.core.jobox.watch;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DirLog {
	
	private Long dirModifiedTime ;
	
	private Map<String,Long> filesModifiedTime;
	
	public DirLog(Long dirModifiedTime,File dir) {
		super();
		
		this.dirModifiedTime = dirModifiedTime;
		
		this.filesModifiedTime=new HashMap<String, Long>();
		File[] files=dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			long modifiedTime = files[i].exists() ? files[i].lastModified() : -1;
			filesModifiedTime.put(files[i].getName(), new Long(modifiedTime));
		}
	}

	public Long getDirModifiedTime() {
		return dirModifiedTime;
	}

	public void setDirModifiedTime(Long dirModifiedTime) {
		this.dirModifiedTime = dirModifiedTime;
	}

	public Map<String, Long> getFilesModifiedTime() {
		return filesModifiedTime;
	}

	public void setFilesModifiedTime(Map<String, Long> filesModifiedTime) {
		this.filesModifiedTime = filesModifiedTime;
	}

}
