package com.amalto.connector.jdbc.ra;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.amalto.connector.implementation.AbstractXtentisResourceAdapter;
import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.jdbc.eis.BaseDAO;
import com.amalto.connector.jdbc.eis.BaseDAOCallback;


public class JdbcResourceAdapter extends AbstractXtentisResourceAdapter {

	private static Logger logger = Logger.getLogger(JdbcResourceAdapter.class);
	
	private BaseDAO baseDAO = null;
	
	public JdbcResourceAdapter() {
		super();
	}
	
	@Override
	public String getJNDIName() {
		return "java:jca/xtentis/connector/jdbc";
	}

	@Override
	public HashMap<Serializable, Serializable> getRunningConfiguration()
			throws XtentisConnectorException {
		    logger.debug("getRunningConfiguration() ");	
			HashMap<Serializable,Serializable> config = new HashMap<Serializable,Serializable>();
			config.put("driverClassName",baseDAO.getDriverClassName());
			config.put("url",baseDAO.getUrl());
			config.put("username",baseDAO.getUsername());
			config.put("password",baseDAO.getPassword());
		return config;
	}

	
	
	@Override
	public String[] getStatus(HashMap<Serializable, Serializable> statusMap) throws XtentisConnectorException {
		logger.debug("getStatus() ");
		//TODO keep the status of connection
		return null;
	}
	

	@Override
	public HashMap<Serializable, Serializable> pullFromEIS(
			HashMap<Serializable, Serializable> parametersIn)
			throws XtentisConnectorException {
		
		logger.debug("pullFromEIS() ");
		HashMap<Serializable, Serializable> parametersOut = new HashMap<Serializable, Serializable>();
		parametersOut.put("success", "false");
		String command = (String)parametersIn.get("command");
		
		if (command.equals("select"))	{
			String resultSet="";
			final Serializable selectSqlObj=parametersIn.get("selectSql");
			resultSet=connectAndCallback(parametersIn, new BaseDAOCallback(){
							public String call() {
								String rtn="";
								try {
									rtn= baseDAO.select((String) selectSqlObj);
								} catch (XtentisConnectorException e) {
									e.printStackTrace();
								}
								return rtn;
							}
						});
			if(resultSet!=null&&resultSet.length()>0){
				parametersOut.put("success", "true");
				parametersOut.put("resultSet", resultSet);
			}
		} else if (command.equals("desc table")) {
			String resultSet="";
			final Serializable tableNameObj=parametersIn.get("tableName");
			resultSet=connectAndCallback(parametersIn, new BaseDAOCallback(){
							public String call() {
								String rtn="";
								try {
									rtn= baseDAO.describeTable((String) tableNameObj);
								} catch (XtentisConnectorException e) {
									e.printStackTrace();
								}
								return rtn;
							}
						});
			if(resultSet!=null&&resultSet.length()>0){
				parametersOut.put("success", "true");
				parametersOut.put("resultSet", resultSet);
			}
		} else if (command.equals("show tables")) {
			String resultSet="";
			resultSet=connectAndCallback(parametersIn, new BaseDAOCallback(){
							public String call() {
								String rtn="";
								try {
									rtn= baseDAO.showTables();
								} catch (XtentisConnectorException e) {
									e.printStackTrace();
								}
								return rtn;
							}
						});
			if(resultSet!=null&&resultSet.length()>0){
				parametersOut.put("success", "true");
				parametersOut.put("resultSet", resultSet);
			}
		} 
		
		return parametersOut;	
	}

	@Override
	public HashMap<Serializable, Serializable> pushToEIS(HashMap<Serializable, Serializable> parametersIn) throws XtentisConnectorException {
		
		logger.debug("pushToEIS() ");
		
		final Serializable actionContentObj=parametersIn.get("actionContent");
		connectAndCallback(parametersIn, new BaseDAOCallback(){
			public String call() {
				
				try {
					baseDAO.update((String) actionContentObj);
				} catch (XtentisConnectorException e) {
					e.printStackTrace();
				}
				
				return "";
			}
		});
		
		return parametersIn;	
	}

	private String connectAndCallback(
			HashMap<Serializable, Serializable> parametersIn,BaseDAOCallback baseDAOCallback)
			throws XtentisConnectorException {
		String rtn="";
		
		String driverClassName = (String)parametersIn.get("driverClassName");
		String url = (String)parametersIn.get("url");
		String username = (String)parametersIn.get("username");
		String password = (String)parametersIn.get("password");
		
		if(password.equals("null"))password="";
		
		baseDAO=new BaseDAO(driverClassName,url,username,password);
		Connection connection=null;
		try {
			connection=baseDAO.getConnection();
			baseDAO.initCommands(connection);
			rtn=baseDAOCallback.call();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			baseDAO.releaseConnection(connection);
		}
		
		return rtn;
	}


	@Override
	public HashMap<Serializable, Serializable> startNow(HashMap<Serializable, Serializable> newConf) throws XtentisConnectorException {
		logger.debug("startNow() ");
		//TODO init config, maybe we should cache some connection info
		return null;
	}

	@Override
	public HashMap<Serializable, Serializable> stopNow(HashMap<Serializable, Serializable> newConf) throws XtentisConnectorException {
		logger.debug("stopNow() ");
		return null;
	}
	
	
}



