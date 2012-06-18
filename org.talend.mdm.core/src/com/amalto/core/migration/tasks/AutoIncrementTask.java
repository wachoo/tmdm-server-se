package com.amalto.core.migration.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.util.Util;

public class AutoIncrementTask extends AbstractMigrationTask {

	@Override
	protected Boolean execute() {
		File file = new File("auto_increment.conf");
		if(file.exists()) {
		try {
			FileInputStream reader=new FileInputStream(file);
			Properties p=new Properties();
			p.load(reader);
			//save to
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			p.storeToXML(bos, "","UTF-8");
			String xmlString=bos.toString("UTF-8");
            XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            xmlServerCtrlLocal.start(XSystemObjects.DC_CONF.getName());
            xmlServerCtrlLocal.putDocumentFromString(xmlString, "Auto_Increment", XSystemObjects.DC_CONF.getName(), null);
            xmlServerCtrlLocal.commit(XSystemObjects.DC_CONF.getName());
			//read from xml file
			bos.close();
			reader.close();
			//delete the auto_increment.conf
			file.delete();
		} catch (Exception e) {
			
		}
		}
		return true;
	}

}
