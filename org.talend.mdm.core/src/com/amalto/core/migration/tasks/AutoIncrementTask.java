package com.amalto.core.migration.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.util.Properties;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.util.AutoIncrementGenerator;
import com.amalto.core.util.Util;

public class AutoIncrementTask extends AbstractMigrationTask {

	@Override
	protected Boolean execute() {
		File file = new File("auto_increment.conf");
		if(file.exists()) {
		try {
			FileReader reader=new FileReader(file);
			Properties p=new Properties();
			p.load(reader);
			//save to
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			p.storeToXML(bos, "","UTF-8");
			String xmlString=bos.toString("UTF-8");
			Util.getXmlServerCtrlLocal().putDocumentFromString(xmlString, "Auto_Increment", XSystemObjects.DC_CONF.getName(), null);
			//read from xml file
			bos.close();
			//delete the auto_increment.conf
			file.delete();
		} catch (Exception e) {
			
		}
		}
		return true;
	}

}
