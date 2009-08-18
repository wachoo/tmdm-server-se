package com.amalto.workbench.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * DOC achen class global comment. Detailled comment
 */
public class ConsoleSimulator implements Runnable {

    private volatile boolean isStop = false;

    private static final int INFO = 0;

    private static final int ERROR = 1;

    private InputStream is;

    private int type;
    IProgressMonitor monitor;    
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
	                    	monitor.subTask(s);
	                    	monitor.worked(1);
	                        System.out.println("" + s);
	                    } else {
	                    	monitor.subTask(s);
	                    	monitor.worked(1);
	                        System.err.println("" + s);
	                    }
	                    try {
	                        Thread.sleep(10);
	                    } catch (InterruptedException ex) {
	                        ex.printStackTrace();
	                    	monitor.subTask("Failed! " + ex.getLocalizedMessage());
	                    	monitor.worked(1);	                        
	                    }
	                }
	            }
	        } catch (IOException ex) {
	        	ex.printStackTrace();
            	monitor.subTask("Failed! " + ex.getLocalizedMessage());
            	monitor.worked(1);
	        }finally{
            	monitor.subTask("sucessfully!");
            	monitor.worked(1);
	        }

		}
     }

    public void stop() {
        isStop = true;
    }

    public static void runCmd(String[] cmd,String[]env,String file, IProgressMonitor monitor) throws Exception {
              	
            Process child = Runtime.getRuntime().exec(cmd, env, new File(file));
            InputStream stdin = child.getInputStream(); //
            InputStream stderr = child.getErrorStream();
            ConsoleSimulator in=new ConsoleSimulator(stdin, INFO);
            in.monitor=monitor;
            Thread tIn = new Thread(in);
            ConsoleSimulator err=new ConsoleSimulator(stderr, ERROR);
            err.monitor=monitor;
            Thread tErr = new Thread(err);
            tIn.start();
            tErr.start();
            // int result = child.waitFor();
            tIn.join();
            tErr.join();
            // child.destroy();

    }

    public static void main(String[] args) {
        //runCmd("java -jar start.jar backup -u admin -p  -b /db/CONF -d c:/CONF.zip");
        // runCmd("echo heeel;");
        // System.exit(0);

    }
}
