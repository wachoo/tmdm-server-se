package com.amalto.core.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DOC achen class global comment. Detailled comment
 */
public class ConsoleSimulator implements Runnable {

    private volatile boolean isStop = false;

    private static final int INFO = 0;

    private static final int ERROR = 1;

    private InputStream is;

    private int type;
   
    /** Creates a new instance of StreamInterceptor */
    public ConsoleSimulator(InputStream is, int type) {
        this.is = is;
        this.type = type;
    }

    public void run() {
    	synchronized (this) {
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader reader = new BufferedReader(isr);
	        String s;
	        try {
	            while ((!isStop) && (s = reader.readLine()) != null) {
	                if (s.length() != 0) {
	                    if (type == INFO) {
	                    	//monitor.subTask(s);
	                    	//monitor.worked(1);
	                        System.out.println("" + s);
	                    } else {
	                    	//monitor.subTask(s);
	                    	//monitor.worked(1);
	                        System.out.println("" + s);
	                    }
	                    try {
	                        Thread.sleep(10);
	                    } catch (InterruptedException ex) {
	                        ex.printStackTrace();
	                    	//monitor.subTask("Failed! " + ex.getLocalizedMessage());
	                    	//monitor.worked(1);	                        
	                    }
	                }
	            }
	        } catch (IOException ex) {
	        	ex.printStackTrace();
            	//monitor.subTask("Failed! " + ex.getLocalizedMessage());
            	//monitor.worked(1);
	        }finally{
	        	try {
					isr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	//monitor.subTask("sucessfully!");
            	//monitor.worked(1);
	        }

		}
     }

    public void stop() {
        isStop = true;
    }

    public static void runCmd(String[] cmd,String[]env,String file) throws Exception {
              	
            Process child = Runtime.getRuntime().exec(cmd, env, new File(file));
            InputStream stdin = child.getInputStream(); //
            InputStream stderr = child.getErrorStream();
            ConsoleSimulator in=new ConsoleSimulator(stdin, INFO);

            Thread tIn = new Thread(in);
            ConsoleSimulator err=new ConsoleSimulator(stderr, ERROR);

            Thread tErr = new Thread(err);
            tIn.start();
            tErr.start();
            // int result = child.waitFor();
            tIn.join();
            tErr.join();
            // child.destroy();

    }

    public static void main(String[] args) {
    	System.out.println(System.getProperty("os.name"));
        //runCmd("java -jar start.jar backup -u admin -p  -b /db/CONF -d c:/CONF.zip");
        // runCmd("echo heeel;");
        // System.exit(0);
    	List<String> list=new ArrayList<String>();
		String home="/media/mdm/opt/trunk/jboss_tem/eXist";
		System.setProperty("exist.home", home);
		System.out.println("exist.home===>"+home);
		String path=new File(home+"/start.jar").getAbsolutePath();		
		String cmd="java -Xms128m -Xmx512m -Dfile.encoding=UTF-8 -jar "+path+" backup -u admin -p -P";
		String[] cmds=cmd.split("\\s");
		list.addAll(Arrays.asList(cmds));
		list.add("-r");
		list.add("/home/achen/eXist-backup11.zip");
		//add server
		String uri="-ouri=xmldb:exist://localhost:8080/exist/xmlrpc";
		list.add(uri);
		//set exist home       
        if(System.getProperty("os.name").indexOf("win") != -1){//windows
        	list.add(0,"cmd");
        	list.add(1,"/c");
        }
        
		//Main.getMain().run(list.toArray(new String[list.size()]));
		try {
			ConsoleSimulator.runCmd(list.toArray(new String[list.size()]), null, home);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
//    	System.setProperty("exist.home", "/media/mdm/opt/trunk/jboss_tem/eXist");
//		String backup="/media/mdm/opt/trunk/jboss_tem/eXist/bin/backup.sh -u admin -p -P  -b /db/PROVISIONING -d /home/achen/eXist-backup11.zip";
//		String restore="/media/mdm/opt/trunk/jboss_tem/eXist/bin/backup.sh -u admin -p -P  -r /home/achen/eXist-backup11.zip";
//		String cmd="start \""+backup +"\"";
//		try {
//			runCmd(cmd);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
    }
}
