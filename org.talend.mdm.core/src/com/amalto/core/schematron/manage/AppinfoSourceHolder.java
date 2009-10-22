package com.amalto.core.schematron.manage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class AppinfoSourceHolder {
	
	private AppinfoSourceHolderPK owner;
	
	private List<AppinfoSourceTypeHolder> appinfoSourceTypeHolders;
	
	public AppinfoSourceHolder(AppinfoSourceHolderPK pk) {
		this.owner=pk;
		this.appinfoSourceTypeHolders=new ArrayList<AppinfoSourceTypeHolder>();
	}
	
	public void addSource(String sourceType,String xpath,String value) {
		
		int mark=appinfoSourceTypeHolders.indexOf(new AppinfoSourceTypeHolder(sourceType));
		if(mark==-1){
			AppinfoSourceTypeHolder appinfoSourceTypeHolder=new AppinfoSourceTypeHolder(sourceType,getBindingAlgorithm(sourceType));
			appinfoSourceTypeHolder.addRole(value, xpath);
			appinfoSourceTypeHolders.add(appinfoSourceTypeHolder);
		}else{
			AppinfoSourceTypeHolder appinfoSourceTypeHolder=appinfoSourceTypeHolders.get(mark);
			appinfoSourceTypeHolder.addRole(value, xpath);
		}

	}
	
	private AppinfoAddPathAbstractAlgorithm getBindingAlgorithm(String sourceType) {
		//just like simple factory
		if(sourceType.equals(SchemaManager.X_Hide_AppinfoSource)){							
			return new AppinfoAddPathParentFirstAlgorithm();
		}
		else if(sourceType.equals(SchemaManager.X_Write_AppinfoSource)){
			return new AppinfoAddPathParentFirstAlgorithm();					
		}
		
		
		return null;

	}
	
	public List<String> getResult(String sourceType,String role) {
		List<String> xpaths=null;
		
		int mark=appinfoSourceTypeHolders.indexOf(new AppinfoSourceTypeHolder(sourceType));
		if(mark!=-1){
			AppinfoSourceTypeHolder appinfoSourceTypeHolder=appinfoSourceTypeHolders.get(mark);
			xpaths=appinfoSourceTypeHolder.getResult(sourceType,role);
		}
		
		if(xpaths==null)return new ArrayList<String>();
		
		return xpaths;

	}
	
	@Override
	public String toString() {
		
		StringBuffer print=new StringBuffer();
		print.append("-").append(owner).append("\n");
		for (Iterator iterator = appinfoSourceTypeHolders.iterator(); iterator.hasNext();) {

			AppinfoSourceTypeHolder appinfoSourceTypeHolder = (AppinfoSourceTypeHolder) iterator.next();
			print.append(appinfoSourceTypeHolder);
			
		}
	
		return print.toString();
	}
	
	

}
