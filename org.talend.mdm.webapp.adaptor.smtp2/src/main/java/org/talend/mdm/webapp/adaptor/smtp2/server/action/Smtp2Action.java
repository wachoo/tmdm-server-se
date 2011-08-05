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
package org.talend.mdm.webapp.adaptor.smtp2.server.action;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service;
import org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean;
import org.w3c.dom.Document;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSServiceAction;
import com.amalto.webapp.util.webservices.WSServiceActionCode;
import com.amalto.webapp.util.webservices.WSServiceGetConfiguration;
import com.amalto.webapp.util.webservices.WSServicePutConfiguration;

/**
 * DOC achen  class global comment. Detailled comment
 */
public class Smtp2Action implements Smtp2Service {

    final static String JNDI_NAME = "amalto/local/service/smtp";

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service#saveConfiguration(org.talend.mdm.webapp.adaptor.smtp2
     * .shared.SmtpConfigurationBean)
     */
    public void saveConfiguration(SmtpConfigurationBean config) throws Exception {
        Util.getPort().putServiceConfiguration(new WSServicePutConfiguration(JNDI_NAME, serializeConfiguration(config)));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service#loadConfiguration()
     */
    public SmtpConfigurationBean loadConfiguration() throws Exception {
        String config = Util.getPort().getServiceConfiguration(new WSServiceGetConfiguration(JNDI_NAME, null)).getValue();
        Document d = Util.parse(config);
        SmtpConfigurationBean configuration = new SmtpConfigurationBean();
        configuration.setSmtpServer(Util.getFirstTextNode(d.getDocumentElement(), "host"));
        String port = Util.getFirstTextNode(d.getDocumentElement(), "port");
        if (port != null)
            configuration.setSmtpPort(new Integer(port).intValue());
        configuration.setSmtpUsername(Util.getFirstTextNode(d.getDocumentElement(), "username"));
        configuration.setSmtpPassword(Util.getFirstTextNode(d.getDocumentElement(), "password"));
        configuration.setSmtpBCC(Util.getFirstTextNode(d.getDocumentElement(), "permanentbcc"));
        return configuration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service#getStatus()
     */
    public String getStatus() throws Exception {
        String status = Util.getPort().serviceAction(new WSServiceAction(JNDI_NAME, WSServiceActionCode.STATUS, null, null))
                .getValue();
        if ("RUNNING".equals(status) || "OK".equals(status)) {
            return "OK";
        }
        return status;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service#start()
     */
    public String start() throws Exception {
        return Util.getPort().serviceAction(new WSServiceAction(JNDI_NAME, WSServiceActionCode.START, null, null)).getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service#stop()
     */
    public String stop() throws Exception {
        return Util.getPort().serviceAction(new WSServiceAction(JNDI_NAME, WSServiceActionCode.STOP, null, null)).getValue();
    }

    private static String serializeConfiguration(SmtpConfigurationBean configuration) throws Exception {
        try {
            String xml = "<configuration>";
            xml += "  <host>"
                    + ("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpServer())) ? "" : StringEscapeUtils
                            .escapeXml(configuration.getSmtpServer())) + "</host>";
            xml += "  <port>" + ((configuration.getSmtpPort() <= 0) ? "" : String.valueOf(configuration.getSmtpPort()))
                    + "</port>";

            xml += "   <username>"
                    + (("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpUsername()))) ? "" : StringEscapeUtils
                            .escapeXml(configuration.getSmtpUsername()))
                    + "</username>"
                    + "   <password>"
                    + (("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpPassword()))) ? "" : StringEscapeUtils
                            .escapeXml(configuration.getSmtpPassword()))
                    + "</password>"
                    + "   <permanentbcc>"
                    + (("".equals(StringEscapeUtils.escapeXml(configuration.getSmtpBCC()))) ? "" : StringEscapeUtils.escapeXml(
                            configuration.getSmtpBCC()).replaceAll("\n", ","));

            xml += "</permanentbcc></configuration>";
            return xml;

        } catch (Exception e) {
            Matcher m = Pattern.compile("(.*Exception:)(.+)", Pattern.DOTALL).matcher(e.getLocalizedMessage());
            if (m.matches())
                throw new Exception(m.group(2));
            throw new Exception(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Service#sendSampleEmail(java.lang.String, java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public String sendSampleEmail(String from, String to, String cc, String bcc, String subject, String body) throws Exception {
        String[] params = { from, to, cc, bcc, subject, body };
        String returnStatus = ""; //$NON-NLS-1$

        try {

            returnStatus = Util.getPort()
                    .serviceAction(new WSServiceAction(JNDI_NAME, WSServiceActionCode.EXECUTE, "sendSimpleMail", params))
                    .getValue();
        } catch (Exception e) {
            throw e;
        }
        return returnStatus;
    }
}
