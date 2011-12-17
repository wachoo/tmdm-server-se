package com.amalto.connector.jdbc.test;

import java.sql.Connection;
import java.sql.SQLException;

import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.jdbc.eis.BaseDAO;

public class JdbcTester {
	
	public static void main(String[] args) {
		
		testSelect();
		
	}

	private static void testInsert() {
		BaseDAO baseDAO=new BaseDAO("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/brick?useUnicode=true&characterEncoding=utf8&autoReconnect=true","root","");
		try {
			Connection connection=baseDAO.getConnection();
			baseDAO.initCommands(connection);
			
			StringBuffer sb=new StringBuffer();
			sb.append("<updateaction> "); 
			sb.append(" "); 
			sb.append("	<operation>INSERT</operation> "); 
			sb.append("	<part> "); 
			sb.append("	<content> "); 
			sb.append("		&amp;lt;Customer mapping-table=\"tuser\"&amp;gt; 	&amp;lt;CustomerId mapping-field=\"id\"&amp;gt;8&amp;lt;/CustomerId&amp;gt; 	&amp;lt;Name mapping-field=\"name\"&amp;gt;\"TEST008\"&amp;lt;/Name&amp;gt; 	&amp;lt;Address mapping-field=\"address\"&amp;gt;\"United States\"&amp;lt;/Address&amp;gt; &amp;lt;/Customer&amp;gt;  ");
			sb.append("	</content> "); 
			sb.append("	</part> "); 
			sb.append(" "); 
			sb.append("</updateaction> ");
			
			try {
				baseDAO.update(sb.toString());
			} catch (XtentisConnectorException e) {
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void testUpdate() {
		BaseDAO baseDAO=new BaseDAO("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/brick?useUnicode=true&characterEncoding=utf8&autoReconnect=true","root","");
		try {
			Connection connection=baseDAO.getConnection();
			baseDAO.initCommands(connection);
			
			StringBuffer sb=new StringBuffer();
			sb.append("<updateaction> "); 
			sb.append(" "); 
			sb.append("	<operation>UPDATE</operation> "); 
			sb.append("	<part> "); 
			sb.append("	<content> "); 
			sb.append("		&amp;lt;Customer mapping-table=\"tuser\"&amp;gt; 	&amp;lt;CustomerId mapping-field=\"id\" key-field=\"true\"&amp;gt;81&amp;lt;/CustomerId&amp;gt; 	&amp;lt;Name mapping-field=\"name\"&amp;gt;\"TEST008\"&amp;lt;/Name&amp;gt; 	&amp;lt;Address mapping-field=\"address\"&amp;gt;\"United States\"&amp;lt;/Address&amp;gt; &amp;lt;/Customer&amp;gt;  ");
			sb.append("	</content> "); 
			sb.append("	</part> "); 
			sb.append(" "); 
			sb.append("</updateaction> ");
			
			try {
				baseDAO.update(sb.toString());
			} catch (XtentisConnectorException e) {
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void testDelete() {
		BaseDAO baseDAO=new BaseDAO("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/brick?useUnicode=true&characterEncoding=utf8&autoReconnect=true","root","");
		try {
			Connection connection=baseDAO.getConnection();
			baseDAO.initCommands(connection);
			
			StringBuffer sb=new StringBuffer();
			sb.append("<updateaction> "); 
			sb.append(" "); 
			sb.append("	<operation>DELETE</operation> "); 
			sb.append("	<part> "); 
			sb.append("	<content> "); 
			sb.append("		&amp;lt;Customer mapping-table=\"tuser\"&amp;gt; 	&amp;lt;CustomerId mapping-field=\"id\"&amp;gt;81&amp;lt;/CustomerId&amp;gt; 	&amp;lt;/Customer&amp;gt;  ");
			sb.append("	</content> "); 
			sb.append("	</part> "); 
			sb.append(" "); 
			sb.append("</updateaction> ");
			
			try {
				baseDAO.update(sb.toString());
			} catch (XtentisConnectorException e) {
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void testSelect() {
		BaseDAO baseDAO=new BaseDAO("org.hsqldb.jdbcDriver","jdbc:hsqldb:C:/opt/jboss/server/default/data/hypersonic/localDB","sa","");
		try {
			Connection connection=baseDAO.getConnection();
			baseDAO.initCommands(connection);
			
			String selectSql="select * from JMS_ROLES";
			
			try {
				baseDAO.select(selectSql);
			} catch (XtentisConnectorException e) {
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void testShowTables() {
		BaseDAO baseDAO=new BaseDAO("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/brick?useUnicode=true&characterEncoding=utf8&autoReconnect=true","root","");
		try {
			Connection connection=baseDAO.getConnection();
			baseDAO.initCommands(connection);
			
			try {
				baseDAO.showTables();
			} catch (XtentisConnectorException e) {
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void testDescTable() {
		BaseDAO baseDAO=new BaseDAO("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/brick?useUnicode=true&characterEncoding=utf8&autoReconnect=true","root","");
		try {
			Connection connection=baseDAO.getConnection();
			baseDAO.initCommands(connection);
			String tableName="brick_users";
			try {
				System.out.println(baseDAO.describeTable(tableName));
			} catch (XtentisConnectorException e) {
				e.printStackTrace();
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
