package com.amalto.workbench.utils;

import com.amalto.workbench.webservices.WSUniverse;

public class UserInfo {
	private String username;
	private String password;
	private String serverUrl;
	private String universe;	
	private WSUniverse wsUuniverse;
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
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public String getUniverse() {
		return universe;
	}
	public void setUniverse(String universe) {
		this.universe = universe;
	}
	public WSUniverse getWsUuniverse() {
		return wsUuniverse;
	}
	public void setWsUuniverse(WSUniverse wsUuniverse) {
		this.wsUuniverse = wsUuniverse;
	}
	
}
