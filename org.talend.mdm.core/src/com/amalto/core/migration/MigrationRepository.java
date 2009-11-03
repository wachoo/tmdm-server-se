package com.amalto.core.migration;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.util.Util;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;

public class MigrationRepository{

  private static boolean isExeuted=false;
  
  private static MigrationRepository repository = null;
  
  private  MigrationRepository() {
  }
  
  synchronized public static MigrationRepository getInstance() {
		if (repository == null) {
			repository = new MigrationRepository();
		}
		return repository;
	}
  
  public void execute()
  {
	  if(!isExeuted){
	      List<String> list = new ArrayList<String>();
	  
		  // look over the handlers dir, call up each handler to execute if it can
		  try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputStream in= MigrationRepository.class.getResourceAsStream("/com/amalto/core/migration/migration.xml");
			Document doc = builder.parse(in);	
			
			NodeList nodelist = doc.getElementsByTagName("Root");
			Node root = nodelist.item(0);		
			for (int id = 0; id < root.getChildNodes().getLength(); id++)
			{
				Node node = root.getChildNodes().item(id);
				if (node instanceof Element)
				   list.add(node.getNodeName());
			}
			// get the class definition and invoke the trigger function
			for(String clazz: list){
				try {
					AbstractMigrationTask task=(AbstractMigrationTask)Class.forName(clazz).newInstance();					
					task.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
			
			//clear the cache objects
			Util.getXmlServerCtrlLocal().clearCache();
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getCause());
			return;
		}
	  }
	  isExeuted=true;
  }
  
   
   public void connect()
   {	   
	   execute();
   }

}
