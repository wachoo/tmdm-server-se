package com.amalto.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;

/**
 * @deprecated
 * AutoIncrement to generate a num
 * The increment num is saved in each data cluster
 * @author achen
 *
 */
public class AutoIncrementGenerator2 {
	//static volatile long  num=-1;	
	
	private static Map<String, String> map=new HashMap<String, String>();
	private static Map<String, DataClusterPOJO> dbMap=new HashMap<String, DataClusterPOJO>();
	static{
		init();
	}
	private static void init(){
		try {
		    Collection<DataClusterPOJOPK> vos = Util.getDataClusterCtrlLocal().getDataClusterPKs(".*");		    
		    for (Iterator iter = vos.iterator(); iter.hasNext(); ) {
				DataClusterPOJOPK pk = (DataClusterPOJOPK) iter.next();
				DataClusterPOJO pojo=Util.getDataClusterCtrlLocal().getDataCluster(pk);
//				String auto=pojo.getAutoincrement();
//				String[] filedValues=auto.split(";");
//				for(String key:filedValues){
//					if(key!=null && key.trim().length()>0){
//						String[] splits=key.split("=");
//						if(splits.length == 2){
//							String field=splits[0];
//							String value=splits[1];
//							map.put(pojo.getName()+"."+field,value);
//						}
//					}
//				}
				dbMap.put(pojo.getName(), pojo);
            }			
		} catch (XtentisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CreateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @return
	 */
	public synchronized static long  generateNum(String datacluster,String filedName){
		String key=datacluster+"."+filedName;
		long num=0;	
		String n=map.get(key);
		if(n==null){
			num=0;				
		}else{
			num=Long.valueOf(n).longValue();
		}
		num++;		
		map.put(key, String.valueOf(num));				
		return num;
	}
	
	public static void saveToDb() throws XtentisException, NamingException, CreateException{
		Map<String,List<String>> fieldMap=new HashMap<String, List<String>>();
		for(Map.Entry<String, String> entry:map.entrySet()){
			String key=entry.getKey();
			String value=entry.getValue();
			int pos=key.indexOf('.');
			if(pos!=-1){
				String dataCluster=key.substring(0,pos);
				String fieldname=key.substring(pos+1); 
				List<String> list=fieldMap.get(dataCluster);
				if(list==null){
					list=new ArrayList<String>();
					fieldMap.put(dataCluster, list);
				}
				list.add(fieldname+"="+value);					           
			}
			
		}
		for(Map.Entry<String, List<String>> entry:fieldMap.entrySet()){
			String dataCluster=entry.getKey();
			List<String> list=entry.getValue();
			StringBuffer sb=new StringBuffer();
			for(String str:list){
				sb.append(str).append(";");
			}
			DataClusterPOJO pojo=dbMap.get(dataCluster);
			if(pojo!=null){
				//FIXME
				//pojo.setAutoincrement(sb.toString());
				pojo.store();
			}
		}
	}
}
