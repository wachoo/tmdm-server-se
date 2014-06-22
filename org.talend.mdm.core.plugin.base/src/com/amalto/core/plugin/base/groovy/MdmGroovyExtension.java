package com.amalto.core.plugin.base.groovy;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class MdmGroovyExtension {
	
	public static String getItemProjection(String revision,String clusterName,String conceptName,String ids) {
	   
	   
	   String itemProjection="";
	   
	   if(clusterName==null||clusterName.length()==0)return itemProjection;
	   if(conceptName==null||conceptName.length()==0)return itemProjection;
	   if(ids==null)return itemProjection;
	   if(revision!=null&&(revision.trim().equals("")||revision.trim().equals("null")))revision=null;
	   
	   try {
		
		   ItemCtrl2Local itemCtrl2Local=Util.getItemCtrl2Local();
		   //parse ids
		   String[] idArray=ids.split("\\.");
		   ItemPOJOPK itemPOJOPK=new ItemPOJOPK(new DataClusterPOJOPK(clusterName),conceptName,idArray);
		   ItemPOJO itemPOJO=itemCtrl2Local.getItem(revision,itemPOJOPK);
		   if(itemPOJO!=null)itemProjection=itemPOJO.getProjectionAsString();
		   //itemProjection=StringEscapeUtils.unescapeXml(itemProjection);
		   
		
	   } catch (NamingException e) {
			e.printStackTrace();
	   } catch (CreateException e) {
			e.printStackTrace();
	   } catch (XtentisException e) {
		   e.printStackTrace();
	   }
		
       return itemProjection;
       
	}

}
