package com.amalto.xmldb.util;

public class Util {

	public static void shutdown() {
        Thread myThread = new Thread() {

            public void run()
            {
            	org.apache.log4j.Logger.getLogger(Util.class).info("Sutting Down");;
                System.exit(-1);
            }
        };

        myThread.setDaemon(false);
        myThread.start();
	}
	
	
}
