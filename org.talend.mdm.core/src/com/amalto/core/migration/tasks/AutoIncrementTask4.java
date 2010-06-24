package com.amalto.core.migration.tasks;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;

public class AutoIncrementTask4 extends AbstractMigrationTask {

	@Override
	protected Boolean execute() {
		try{
			String xmlString=Util.getXmlServerCtrlLocal().getDocumentAsString(null, XSystemObjects.DC_CONF.getName(), "Auto_Increment");
			if(xmlString==null) return true;
			DataClusterPOJOPK DC=new DataClusterPOJOPK(XSystemObjects.DC_CONF.getName());
			String[] IDS=new String[] {"AutoIncrement"};
			String CONCEPT="AutoIncrement";			
			xmlString=xmlString.replace("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">", "");
			//Util.getXmlServerCtrlLocal().putDocumentFromString(xmlString, AUTO_INCREMENT, XSystemObjects.DC_CONF.getName(), null);
			ItemPOJO pojo = new ItemPOJO(					
					DC,	//cluster
					CONCEPT,								//concept name
					IDS,
					System.currentTimeMillis(),			//insertion time
					xmlString												//actual data
			);
			pojo.store(null);
			
			//delete the original file
			Util.getXmlServerCtrlLocal().deleteDocument(null, XSystemObjects.DC_CONF.getName(), "Auto_Increment");
		} catch (Exception e) {
					
		}
		return true;
	}

}
