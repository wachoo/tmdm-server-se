package com.amalto.connector.svn.ra;

import java.io.Serializable;
import java.util.HashMap;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

import com.amalto.connector.implementation.AbstractXtentisResourceAdapter;
import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.svn.eis.SvnClient;
import com.amalto.core.objects.versioning.util.HistoryInfos;


public class SvnResourceAdapter extends AbstractXtentisResourceAdapter {

	//private HashMap<String, SvnClientConfiguration> configuration = new HashMap<String, SvnClientConfiguration>();
	//private HashMap<String,SvnClient> clients = new HashMap<String, SvnClient>();
	
	private SvnClient client = null;
	
	public SvnResourceAdapter() {
		super();
		setupLibrary();
	}
	
	@Override
	public String getJNDIName() {
		return "java:jca/xtentis/connector/svn";
	}

	@Override
	public HashMap<Serializable, Serializable> getRunningConfiguration()
			throws XtentisConnectorException {
			HashMap<Serializable,Serializable> config = new HashMap<Serializable,Serializable>();
			config.put("url",client.getUrl());
			config.put("username",client.getUser());
			config.put("password",client.getPassword());
		return config;
	}

	
	
	@Override
	public String[] getStatus(HashMap<Serializable, Serializable> statusMap) throws XtentisConnectorException {
			
		String status = AbstractXtentisResourceAdapter.STATUS_OK;
		String message = AbstractXtentisResourceAdapter.STATUS_OK;
		
		if (client== null) {
			status = AbstractXtentisResourceAdapter.STATUS_ERROR;
			message = AbstractXtentisResourceAdapter.STATUS_ERROR;
		} else if (!client.isConnected()) {
			status = AbstractXtentisResourceAdapter.STATUS_STOPPED;
			message = AbstractXtentisResourceAdapter.STATUS_STOPPED;
		}
		return new String[] {status,message};
	}
	

	@Override
	public HashMap<Serializable, Serializable> pullFromEIS(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {		
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		String command = (String)parametersIn.get("command");
		
		if (command.equals("checkout"))	{
			return checkout(parametersIn);
		} else if (command.equals("history")) {
			return history(parametersIn);
		} else if (command.equals("versions")) {
			return versions(parametersIn);
		} else if (command.equals("list")) {
			return list(parametersIn);
		}
		
		return parametersOut;	
	}

	@Override
	public HashMap<Serializable, Serializable> pushToEIS(HashMap<Serializable, Serializable> parametersIn) throws XtentisConnectorException {
		String command = (String)parametersIn.get("command");
		
		if (command.equals("commit"))	{
			return commit(parametersIn);
		} else if (command.equals("branch")) {
			return branch(parametersIn);
		} else if (command.equals("delete")) {
			return delete(parametersIn);
		}
		return parametersIn;	
	}


	@Override
	public HashMap<Serializable, Serializable> startNow(HashMap<Serializable, Serializable> newConf) throws XtentisConnectorException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("startNow() ");
		String url = (String)newConf.get("url");
		String user = (String)newConf.get("username");
		String password = (String)newConf.get("password");
		
		client = new SvnClient(url, user, password);
		try {
			client.connect(); 
		} catch (Exception e) {
			throw new XtentisConnectorException(e); 
		}
		return null;
	}

	@Override
	public HashMap<Serializable, Serializable> stopNow(HashMap<Serializable, Serializable> newConf) throws XtentisConnectorException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("stopNow() ");
		return null;
	}
	

	
	
	// commit file
	private HashMap<Serializable, Serializable> commit(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		
		String fileName = (String)parametersIn.get("fileName");
		byte[] content = (byte[])parametersIn.get("content");
		String comments = (String)parametersIn.get("comments");
		
		
		try {
			
				client.commit(fileName, content, comments);
		} catch (Exception e) {
			e.printStackTrace();
			throw ( new XtentisConnectorException("Error occured trying to commit a file : "+e.getLocalizedMessage()) );
		}
	
		parametersOut.put("success", "true");
		return parametersOut;	
	}
	
	
	
	private HashMap<Serializable, Serializable> history(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		
		String fileName = (String)parametersIn.get("fileName");
		
		try {
			
				HistoryInfos infos = client.history(fileName);
				parametersOut.put("history", infos);
		} catch (Exception e) {
			throw ( new XtentisConnectorException("Error occured trying to get history : "+e.getLocalizedMessage()) );
		}
	
		parametersOut.put("success", "true");
		return parametersOut;	
	}
	
	private HashMap<Serializable, Serializable> list(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		
		String path = (String)parametersIn.get("path");
		String tag = (String)parametersIn.get("tag");
		
		try {
			
				String []entries = client.list(path, tag);
				parametersOut.put("list", entries);
		} catch (Exception e) {
			throw ( new XtentisConnectorException("Error occured trying to list objects : "+e.getLocalizedMessage()) );
		}
	
		parametersOut.put("success", "true");
		return parametersOut;	
	}
	
	private HashMap<Serializable, Serializable> versions(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		
		String fileName = (String)parametersIn.get("fileName");
		
		try {
			
				HistoryInfos infos = client.versions(fileName);
				parametersOut.put("versions", infos);
		} catch (Exception e) {
			e.printStackTrace();
			throw ( new XtentisConnectorException("Error occured trying to get versions of an object : "+e.getLocalizedMessage()) );
		}
	
		parametersOut.put("success", "true");
		return parametersOut;	
	}
	
	// tag file
	private HashMap<Serializable, Serializable> branch(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		
		String path = (String)parametersIn.get("path");
		String comments = (String)parametersIn.get("comments");
		String tag  = (String)parametersIn.get("tag");
		
		try {
				client.branch(path, comments, tag);
		} catch (Exception e) {
			throw ( new XtentisConnectorException("Error occured trying to branch an object : "+e.getLocalizedMessage()) );
		}
	
		parametersOut.put("success", "true");
		return parametersOut;	
	}
	
	//delete path
	private HashMap<Serializable, Serializable> delete(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		
		String path = (String)parametersIn.get("path");
		String tag  = (String)parametersIn.get("tag");
		if (tag == null)
			tag = "";
		
		try {
				client.delete(path, tag);
		} catch (Exception e) {
			throw ( new XtentisConnectorException("Error occured trying to delete an object : "+e.getLocalizedMessage()));
		}
	
		parametersOut.put("success", "true");
		return parametersOut;	
	}

	// checkout file
	private HashMap<Serializable, Serializable> checkout(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		
		String fileName = (String)parametersIn.get("fileName");
		String tag  = (String)parametersIn.get("tag");
		String revision ="-1";
		if(parametersIn.get("revision")!=null&&((String)parametersIn.get("revision")).length()>0)revision= (String)parametersIn.get("revision");
		
		try {
			
				byte [] content = client.checkout(fileName, tag, Long.parseLong(revision));
				parametersOut.put("content", content);
		} catch (Exception e) {
			throw ( new XtentisConnectorException("Error occured trying to checkout an object : "+e.getLocalizedMessage()) );
		}
	
		parametersOut.put("success", "true");
		return parametersOut;	
	}
	

	
	
	/****************************************************************************************
	 * 
	 *  Utilities
	 *  
	 ****************************************************************************************/
	
	/*
	 * Initializes the library to work with a repository via 
	 * different protocols.
	 */
	private void setupLibrary() {
	    /*
	     * For using over http:// and https://
	     */
	    DAVRepositoryFactory.setup();
	    /*
	     * For using over svn:// and svn+xxx://
	     */
	    SVNRepositoryFactoryImpl.setup();
	    
	    /*
	     * For using over file:///
	     */
	    FSRepositoryFactory.setup();
	}	
	
	
	
}



