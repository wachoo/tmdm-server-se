package com.amalto.core.schema.manage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AppinfoSourceTypeHolder {
	
	private String sourceType;
	
	private List<AppinfoSourceTypeRoleHolder> appinfoSourceTypeRoleHolders;
	
	private AppinfoAddPathAbstractAlgorithm bindingAlgorithm;
	
	public AppinfoSourceTypeHolder(String sourceType) {
		super();
		this.sourceType = sourceType;
	}

	public AppinfoSourceTypeHolder(String sourceType,AppinfoAddPathAbstractAlgorithm bindingAlgorithm) {
		super();
		this.sourceType = sourceType;
		this.appinfoSourceTypeRoleHolders=new ArrayList<AppinfoSourceTypeRoleHolder>();
		this.bindingAlgorithm=bindingAlgorithm;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public AppinfoAddPathAbstractAlgorithm getBindingAlgorithm() {
		return bindingAlgorithm;
	}

	public void setBindingAlgorithm(AppinfoAddPathAbstractAlgorithm bindingAlgorithm) {
		this.bindingAlgorithm = bindingAlgorithm;
	}
	
	public void addRole(String role,String xpath) {
		
		int mark=appinfoSourceTypeRoleHolders.indexOf(new AppinfoSourceTypeRoleHolder(sourceType,role));
		if(mark==-1){
			AppinfoSourceTypeRoleHolder appinfoSourceTypeRoleHolder=new AppinfoSourceTypeRoleHolder(sourceType,role,bindingAlgorithm);
			appinfoSourceTypeRoleHolder.addRolePath(xpath);
			appinfoSourceTypeRoleHolders.add(appinfoSourceTypeRoleHolder);
		}else{
			AppinfoSourceTypeRoleHolder appinfoSourceTypeRoleHolder=appinfoSourceTypeRoleHolders.get(mark);
			appinfoSourceTypeRoleHolder.addRolePath(xpath);
		}

	}
	
	public List<String> getResult(String sourceType,String role) {
		List<String> xpaths=null;
		
		int mark=appinfoSourceTypeRoleHolders.indexOf(new AppinfoSourceTypeRoleHolder(sourceType,role));
		if(mark!=-1){
			AppinfoSourceTypeRoleHolder appinfoSourceTypeRoleHolder=appinfoSourceTypeRoleHolders.get(mark);
			xpaths=appinfoSourceTypeRoleHolder.getXpaths();
		}
		
		return xpaths;

	}
	
	@Override
	public boolean equals(Object obj) {
		
		AppinfoSourceTypeHolder appinfoSourceTypeHolder=(AppinfoSourceTypeHolder)obj;
		return this.sourceType.equals(appinfoSourceTypeHolder.getSourceType());
		
	}
	
	@Override
	public String toString() {
		
		StringBuffer print=new StringBuffer();
		print.append("--").append(this.sourceType).append("\n");
		for (Iterator iterator = appinfoSourceTypeRoleHolders.iterator(); iterator.hasNext();) {

			AppinfoSourceTypeRoleHolder appinfoSourceTypeRoleHolder = (AppinfoSourceTypeRoleHolder) iterator.next();
			print.append(appinfoSourceTypeRoleHolder);
			
		}
		return print.toString();
	}

	
}
