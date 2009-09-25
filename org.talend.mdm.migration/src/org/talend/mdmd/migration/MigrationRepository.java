package org.talend.mdmd.migration;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;

public class MigrationRepository{
  protected IXmlServerSLWrapper _server;
  private XDBReceiver _receiver;
  private static boolean isExeuted=false;
  private static String MIGRATION_FILE_NAME = "migration.xml";
  private static final int CONNECT_WAIT_TIME = 100 * 1000;
  
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
		InputStream in= Thread.currentThread().getContextClassLoader().getResourceAsStream(MIGRATION_FILE_NAME);
		Document doc = builder.parse(in);
		NodeList nodelist = doc.getElementsByTagName("Root");
		Node root = nodelist.item(0);		
		for (int id = 0; id < root.getChildNodes().getLength(); id++)
		{
			Node node = root.getChildNodes().item(id);
			if (node instanceof Element)
			   list.add(node.getNodeName());
		}
	} catch (Exception e) {
		org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getCause());
		return;
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
/*	  Class[] clses = getDirectoryEntries(list);
	  try {
			for (Class cls : clses) {
				Constructor cons = cls.getConstructor(new Class[] {});
				Object handler = cons.newInstance(new Object[] {});
				Method executeMethod = cls.getMethod("start",
						new Class[] {});
				executeMethod.invoke(handler, new Object[] {});
			}
		} catch (Exception ex) {
			org.apache.log4j.Logger.getLogger(this.getClass()).error(
					ex.getMessage());
		}*/
		
		//clear the cache objects
		ItemPOJO.clearCache();
		ObjectPOJO.clearCache();
	  }
	  isExeuted=true;
  }
  
  public Class[] getDirectoryEntries(List<String> list)
  {
      try
      {
    	  String packageName = getClass().getPackage().getName();
    	  packageName = packageName.replaceAll("[.]", "/");
          URL url = Thread.currentThread().getContextClassLoader().getResource(packageName);
          String name = url.getFile();
          name = name.substring(0, name.lastIndexOf("!"));
          name = name.substring(0, name.lastIndexOf("/"));
          if (name.startsWith("file:/"))
              name = name.substring("file:/".length());
          JarFileResourceLocator locator = new JarFileResourceLocator(name, list);
          
          Class[] res = locator.loadFile();
          return res;
      }
      catch (Exception e)
      {
          return new Class[0];
      }
      
  }
  
   public void connect(IXmlServerSLWrapper service)
   {
	   _server= service;
	 
	   if (_receiver != null) {
		_receiver.close();
	   }
	   _receiver = new XDBReceiver();
	   _receiver.start();
   }
 
	public void close()
	{
		_receiver.close();
		_server = null;
	}
	 
  class XDBReceiver extends Thread
  {
    private boolean _closed;
      
  	public XDBReceiver()
  	{
  		super("XDBReceiver");
          // Allow application to terminate while this thread is still
          // executing
  		setDaemon(true);
          _closed = false;
  	}
  	
      public  boolean connect() {
			return _server.isUpAndRunning();
		}
      
      public void run() {
			while (!_closed && !connect()) {
				try {
					Thread.sleep(CONNECT_WAIT_TIME);
				} catch (Exception e) {
					// ignore exceptions here
				}
			}
			
			if (!_closed && connect()) {
				MigrationRepository.this.execute();
			}
		}
      
      public void close()
      {

          _closed = true;
          //interrupt();
      }
  }
	public static void main(String[] args) {
		
		MigrationRepository re = new MigrationRepository();
		re.execute();
		 List<String> list = new ArrayList<String>();
		 list.add("org.talend.mdmd.migration.tasks.TiscallRenameToCallJobTask");
		Class [] files = re.getDirectoryEntries(list);
		URL url;
		
		  for (Class file: files)
		  {
			  try {
				Constructor cons = file.getConstructor(new Class[] {});
				Object handler = cons.newInstance(new Object[] {});
				Method executeMethod = file.getMethod("start",  new Class[]{});
				executeMethod.invoke(handler, new Object[] {});
			} catch (Exception e) {
				e.printStackTrace();
			} 
		  }
	}
  
}
