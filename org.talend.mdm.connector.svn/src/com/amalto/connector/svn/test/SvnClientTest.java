package com.amalto.connector.svn.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

import talend.mdm.test.MDMTestCase;

import com.amalto.connector.svn.eis.SvnClient;

public class SvnClientTest extends MDMTestCase{
	
	private SvnClient client;
	
	@Override
	public void setUp() {
		
		super.setUp();
		
		setupLibrary();
		
		client=connectSvnServer("https://hshu:8443/svn/starkeylib/", "admin", "admin");
		if(client==null)log("Unable to connect to SVN repository! ");
	}
	
	
	public void testCommit() {
		try {
			//new folder & new file case
			//client.commit("mdm/c.txt", getBytesFromFile(new File("C:\\c.txt")), null);
			
			//old folder & new file case
			client.commit("mdm/d.txt", getBytesFromFile(new File("C:\\d.txt")), null);
			
			//old folder case
			//client.commit("mdm", null, null);
			
			//old folder & old file case
			//client.commit("mdm/d.txt", getBytesFromFile(new File("C:\\d.txt")), null);
		} catch (SVNException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private SvnClient connectSvnServer(String url, String user, String password) {
		SvnClient client = new SvnClient(url, user, password);
		try {
			client.connect();
			return client;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/*
	 * 
	 * Initializes the library to work with a repository via
	 * 
	 * different protocols.
	 */

	private void setupLibrary() {

		/*
		 * 
		 * For using over http:// and https://
		 */

		DAVRepositoryFactory.setup();

		/*
		 * 
		 * For using over svn:// and svn+xxx://
		 */

		SVNRepositoryFactoryImpl.setup();

		/*
		 * 
		 * For using over file:///
		 */

		FSRepositoryFactory.setup();

	}
	
	private byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
    
        // Get the size of the file
        long length = file.length();
    
        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
    
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
    
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
    
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
    
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }


}
