package com.amalto.connector.jdbc.eis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import com.amalto.connector.jdbc.eis.command.DescribeCommand;
import com.amalto.connector.jdbc.eis.command.SelectCommand;
import com.amalto.connector.jdbc.eis.command.ShowTablesCommand;
import com.amalto.connector.jdbc.eis.command.UpdateCommand;
import com.amalto.connector.jdbc.eis.common.PrintStreamedExceptionHandler;
import com.amalto.connector.jdbc.eis.extractor.PrintStreamedDescribeResultSetExtractor;
import com.amalto.connector.jdbc.eis.extractor.PrintStreamedResultSetExtractor;
import com.amalto.connector.jdbc.eis.extractor.PrintStreamedShowTablesResultSetExtractor;
import com.amalto.connector.jdbc.eis.extractor.PrintStreamedUpdateRowcountHandler;

public class SqlAgent {
	
	public static final String EXCEPTION_PS="exceptionHandler";
	public static final String SELECT_PS="resultSetExtractor";
	public static final String UPDATE_PS="rowcountHandler";
	public static final String SHOWTABLE_PS="showTableResultSetExtractor";
	public static final String DESCRIBE_PS="describeResultSetExtractor";
	
	private String driverClassName;
	private String url;
	private String username;
	private String password;
	private Properties properties;
	
	private HashMap printStreamed;
	private SelectCommand selectCommand;
	private UpdateCommand updateCommand;
	private ShowTablesCommand showTablesCommand;
	private DescribeCommand describeCommand;
	
	
	public SqlAgent(String driverClassName, String url) {
		super();
		this.driverClassName = driverClassName;
		this.url = url;
	}
	
	public SqlAgent(String driverClassName,Properties properties, String url) {
		super();
		this.driverClassName = driverClassName;
		this.properties = properties;
		this.url = url;
	}
	
	
	public SqlAgent(String driverClassName,String url, String username,String password) {
		super();
		this.driverClassName = driverClassName;
		this.password = password;
		this.url = url;
		this.username = username;
	}
	
	
	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
	public HashMap getPrintStreamed() {
		return printStreamed;
	}

	public Connection getConnection() throws ClassNotFoundException, SQLException
	  {
		
	    Class.forName(this.driverClassName);
	    
	    if(this.properties != null)
	    {
	      DriverManager.getConnection(url, properties);
	    }
	    if(this.username != null)
	    {
	      return DriverManager.getConnection(this.url, this.username, this.password);
	    }
	    else
	    {
	      return DriverManager.getConnection(this.url);
	    }
	    
	  }
	
	public void releaseConnection(Connection connection) {
		
		try {
			if(connection!=null)connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	public HashMap initCommands(Connection connection) throws SQLException {
		this.printStreamed=new HashMap();
		
		PrintStreamedExceptionHandler exceptionHandler = new PrintStreamedExceptionHandler();
	    exceptionHandler.addPrintStream(System.out);
	    this.printStreamed.put(EXCEPTION_PS,exceptionHandler);

	    PrintStreamedResultSetExtractor resultSetExtractor = new PrintStreamedResultSetExtractor();
	    resultSetExtractor.addPrintStream(System.out);
	    this.printStreamed.put(SELECT_PS,resultSetExtractor);
	    this.selectCommand = new SelectCommand();
	    this.selectCommand.setConnection(connection);
	    this.selectCommand.setResultSetExtractor(resultSetExtractor);
	    this.selectCommand.setExceptionHandler(exceptionHandler);
	    
	    PrintStreamedUpdateRowcountHandler rowcountHandler = new PrintStreamedUpdateRowcountHandler();
	    rowcountHandler.addPrintStream(System.out);
	    this.printStreamed.put(UPDATE_PS,rowcountHandler);
	    this.updateCommand = new UpdateCommand();
	    this.updateCommand.setConnection(connection);
	    this.updateCommand.setExceptionHandler(exceptionHandler);
	    this.updateCommand.setRowcountHandler(rowcountHandler);
	    
	    PrintStreamedShowTablesResultSetExtractor showTableResultSetExtractor = new PrintStreamedShowTablesResultSetExtractor();
	    showTableResultSetExtractor.addPrintStream(System.out);
	    showTableResultSetExtractor.setDatabaseMetaData(connection.getMetaData());
	    this.printStreamed.put(SHOWTABLE_PS,showTableResultSetExtractor);
	    this.showTablesCommand = new ShowTablesCommand();
	    this.showTablesCommand.setConnection(connection);
	    this.showTablesCommand.setExceptionHandler(exceptionHandler);
	    this.showTablesCommand.setResultSetExtractor(showTableResultSetExtractor);
	    
	    PrintStreamedDescribeResultSetExtractor describeResultSetExtractor = new PrintStreamedDescribeResultSetExtractor();
	    describeResultSetExtractor.addPrintStream(System.out);
	    this.printStreamed.put(DESCRIBE_PS,describeResultSetExtractor);
	    this.describeCommand = new DescribeCommand();
	    this.describeCommand.setConnection(connection);
	    this.describeCommand.setResultSetExtractor(describeResultSetExtractor);
	    this.describeCommand.setExceptionHandler(exceptionHandler);
	    
	    return printStreamed;

	}
	
	protected void exeSelectCommand(String sqlCommand) {
		this.selectCommand.setCommand(sqlCommand);
		this.selectCommand.execute();
	}
	
	protected void exeUpdateCommand(String sqlCommand) {
		this.updateCommand.setCommand(sqlCommand);
		this.updateCommand.execute();
	}
	
	protected void exeShowTablesCommand() {
		this.showTablesCommand.execute();
	}

	protected void exeDescribeCommand(String tableName) {
		this.describeCommand.setTableName(tableName);
	    this.describeCommand.execute();
	}

}
