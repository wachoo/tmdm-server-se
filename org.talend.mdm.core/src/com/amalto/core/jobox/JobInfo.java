package com.amalto.core.jobox;

import java.util.HashMap;
import java.util.Map;

public class JobInfo {
	
	private String name;
	private String version;
	private String classpath;
	private String mainclass;
	private Map<String,String> defaultParamMap;
	
	public JobInfo(String name, String version) {
		super();
		this.name = name;
		this.version = version;
		this.classpath="";
		defaultParamMap=new HashMap<String, String>();
	}

	public void addParam(String key,String value) {
		defaultParamMap.put(key, value);
	}
	
	public Map<String, String> getDefaultParamMap() {
		return defaultParamMap;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	
	
	public String getMainclass() {
		return mainclass;
	}

	public void setMainclass(String mainclass) {
		this.mainclass = mainclass;
	}

	public String toString() {
		StringBuffer sb=new StringBuffer();
		sb.append("Name:")
		  .append(name)
		  .append(";Version:")
		  .append(version)
		  .append(";Classpath:")
		  .append(classpath)
		  .append(";Mainclass:")
		  .append(mainclass)
		  .append(";Param:")
		  .append(defaultParamMap);
		return sb.toString();
	}
	
	public int hashCode()
	{
	    return (this.getName()+"_"+this.getVersion()).hashCode();
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!o.getClass().equals(this.getClass())) {
			return false;
		}
		JobInfo other = (JobInfo) o;
		return (this.getName()+"_"+this.getVersion()).equals((other.getName()+"_"+other.getVersion()));
	}
}
