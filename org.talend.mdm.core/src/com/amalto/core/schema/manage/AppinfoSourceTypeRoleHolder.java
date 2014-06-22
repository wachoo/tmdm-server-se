package com.amalto.core.schema.manage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AppinfoSourceTypeRoleHolder {
	
	private String sourceType;
	
	private String role;
	
	private List<String> xpaths;
	
	private AppinfoAddPathAbstractAlgorithm operationAlgorithm;//reference only
	
	public AppinfoSourceTypeRoleHolder( String sourceType, String role) {
		super();
		this.role = role;
		this.sourceType = sourceType;
	}

	public AppinfoSourceTypeRoleHolder(String sourceType,String role,AppinfoAddPathAbstractAlgorithm operationAlgorithm) {
		super();
		this.role = role;
		this.sourceType = sourceType;
		this.xpaths=new ArrayList<String>();
		this.operationAlgorithm=operationAlgorithm;
	}
	

	public void addRolePath(String path) {
		//check algorithm
		if(operationAlgorithm!=null){
			if(operationAlgorithm.check(xpaths, path))addRolePathDirectly(path);
		}else{
			addRolePathDirectly(path);
		}
	}

	private void addRolePathDirectly(String path) {
		if(xpaths!=null&&!xpaths.contains(path)){
			xpaths.add(path);
		}
	}
	
	private String getUID() {
		
      return this.sourceType+"/"+this.role;
      
	}
	
	
	public List<String> getXpaths() {
		return xpaths;
	}

	@Override
	public boolean equals(Object obj) {
		
		AppinfoSourceTypeRoleHolder roleHolder=(AppinfoSourceTypeRoleHolder)obj;
		
		return this.getUID().equals(roleHolder.getUID());
		
	}
	
	
	@Override
	public String toString() {
		
		StringBuffer print=new StringBuffer();
		print.append("---").append(this.role).append("\n");
		for (Iterator iterator = xpaths.iterator(); iterator.hasNext();) {

			String xpath = (String) iterator.next();
			print.append("----").append(xpath).append("\n");
			
		}
		return print.toString();
	}

}
