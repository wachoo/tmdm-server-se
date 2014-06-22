package com.amalto.service.jdbc.bean;

public class JdbcInfos {
	
	private String driverClassName;
	
	private String url;
	
	private String username;
	
	private String password;
	
	private String transformer;
	
	public JdbcInfos() {
	
	}
	
	public JdbcInfos(String driverClassName,String url,
			String username,String password) {
		super();
		this.driverClassName = driverClassName;
		this.password = password;
		this.url = url;
		this.username = username;
	}



	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTransformer() {
		return transformer;
	}

	public void setTransformer(String transformer) {
		this.transformer = transformer;
	}

	
}
