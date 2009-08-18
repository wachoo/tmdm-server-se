package com.amalto.webapp.v3.adapter.smtp.dwr;

public class SmtpConfigurationBean {

   private String smtpServer;
   private int smtpPort;
   private String smtpUsername;
   private String smtpPassword;
   private String smtpBCC;
   
	   
	public String getSmtpServer() {
		return smtpServer;
	}
	public void setSmtpServer(
		String smtpServer) {
		this.smtpServer = smtpServer;
	}
	public int getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(
		int smtpPort) {
		this.smtpPort = smtpPort;
	}
	public String getSmtpUsername() {
		return smtpUsername;
	}
	public void setSmtpUsername(
		String smtpUsername) {
		this.smtpUsername = smtpUsername;
	}
	public String getSmtpPassword() {
		return smtpPassword;
	}
	public void setSmtpPassword(
		String smtpPassword) {
		this.smtpPassword = smtpPassword;
	}
	public String getSmtpBCC() {
		return smtpBCC;
	}
	public void setSmtpBCC(
		String smtpBCC) {
		this.smtpBCC = smtpBCC;
	}
		
}
