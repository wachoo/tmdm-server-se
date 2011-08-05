// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package org.talend.mdm.webapp.adaptor.smtp2.shared;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * DOC achen class global comment. Detailled comment
 */
public class SmtpConfigurationBean extends BaseModelData implements Serializable, IsSerializable {

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
