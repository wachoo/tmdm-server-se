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

package org.talend.mdm.service.calljob.ejb;

import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.talend.mdm.service.calljob.CompiledParameters;
import org.talend.mdm.service.calljob.ContextParam;
import org.talend.mdm.service.calljob.webservices.Args;
import org.talend.mdm.service.calljob.webservices.ArrayOfXsdString;
import org.talend.mdm.service.calljob.webservices.WSxml;
import org.talend.mdm.service.calljob.webservices.WSxmlService;
import org.w3c.dom.Element;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ServiceCtrlBean;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.JobInvokeConfig;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

/**
 * @author achen
 * 
 * @ejb.bean 	name="CallJobServiceBean"
 *           	display-name="Name for CallJobService"
 *           	description="Description for CallJobService"
 * 		  		local-jndi-name = "amalto/local/service/callJob"
 *           	type="Stateless"
 *           	view-type="local"           	
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 * 
 * 
 */
public class CallJobServiceBean extends ServiceCtrlBean  implements SessionBean{

	private static final String LTJ_PROTOCOL = "ltj";

    private static final long serialVersionUID = 1L;

    public static final String JNDI_NAME = "amalto/local/service/callJob";

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public Serializable fetchFromOutbound(String command, String parameters, String schedulePlanID) throws XtentisException {
		throw new XtentisException("The callJob service is not meant to interact with adapters");
	}

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLettersLanguageCode) throws XtentisException {
        // TODO Missing i18n like thing
		return "The service call job";
	}

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getJNDIName() throws XtentisException {
		return JNDI_NAME;
	}

    /**
     * @author achen
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
		return
		"CallJob Service\n" +
		"\n" +
		"Parameters\n" +
		"	url [mandatory]: the webservice port URL to the TIS Server"+"\n"+
		"		or the local talend job URL: ltj://<jobName>/<jobVersion>/[jobMainClass]"+"\n"+
		"	contextParam   : the contextParam of the tis job"+"\n"+
		"		name: the name of the context param"+"\n"+
		"		value: the value of context param, the value will be viewed as a priple" + "\n" + 
		"              variable if the value is embraced with a brace, its content will be like: "+"\n"+
		"              <exchange><report>{update report here}</report><item>{item pointed to by Update/Key}</item></exchange>\n"+
		"	username [optional]: the username to use for the call"+"\n"+
		"	password [optional]: the password to  use for the call" +"\n"+
		"	contentType [optional]: the contentType of the returned data. Defaults to 'text/xml'" +"\n"+
		"	conceptMapping [optional]: Directly map the result of a TIS call to a MDM entity"+"\n"+
		"		concept: the name of the concept"+"\n"+
		"		fields: mapping rule with json format"+"\n"+
		"\n"+
		"Example1" +"\n"+
		"	<configuration>" +"\n"+
		"		<url>http://server:port/TISService/TISPort</url>" +"\n"+
		"		<contextParam>" +"\n"+	
		"			<name>firstname</name>" +"\n"+
		"			<value>jack</value>" +"\n"+
		"		</contextParam>" +"\n"+
		"		<contextParam>" +"\n"+	
		"			<name>lastname</name>" +"\n"+
		"			<value>jones</value>" +"\n"+
		"		</contextParam>" +"\n"+		
		"		<contextParam>" +"\n"+	
		"			<name>xmlInput</name>" +"\n"+
		"			<value>{}</value>" +"\n"+
		"		</contextParam>" +"\n"+		
		"		<username>john</username>" +"\n"+
		"		<password>doe</password>" +"\n"+
		"		<conceptMapping>" +"\n"+	
		"			<concept>User</concept>" +"\n"+
		"			<fields>" +"\n"+
		"			  {" +"\n"+
		"			  p1:firstname," +"\n"+
		"			  p2:lastname" +"\n"+
		"			  }" +"\n"+
		"			</fields>" +"\n"+
		"		</conceptMapping>" +"\n"+
		"	</configuration>"+"\n"+
		"Example2" +"\n"+
		"	<configuration>" +"\n"+
		"		<url>ltj://tiscall_multi_return/0.1</url>" +"\n"+
		"		<contextParam>" +"\n"+	
		"			<name>nb_line</name>" +"\n"+
		"			<value>5</value>" +"\n"+
		"		</contextParam>" +"\n"+
		"	</configuration>"+"\n"+
		"\n";    }
    
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getStatus() throws XtentisException {
		return "OK";
	}
    /**
    *
    * @see com.amalto.core.ejb.ServiceCtrlBean#getConfiguration(java.lang.String)
    * @throws EJBException
    *
    * @ejb.interface-method view-type = "local"
    * @ejb.facade-method
    */
   public String getConfiguration(String optionalParameters) throws XtentisException{
   		return getDefaultConfiguration();
   }

    /**
     * @throws EJBException
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String compiledParameters) throws XtentisException {
        try {
            CompiledParameters parameters = CompiledParameters.deserialize(compiledParameters);
            //context.put(PARAMETERS, parameters);
            URL wsdlURL = this.getClass().getResource("/META-INF/wsdl/tis.wsdl");

            WSxmlService service = new WSxmlService(wsdlURL, new QName("http://talend.org", "WSxmlService"));
            WSxml port = service.getWSxml();

            //set the parameters
            JobInvokeConfig jobInvokeConfig;
            URI uri = URI.create(parameters.getUrl());
            String protocol = uri.getScheme();
            String jobName = uri.getHost();
            String jobVersion = uri.getPath();
            String jobMainClass = uri.getQuery();

            if (LTJ_PROTOCOL.equals(protocol)) {
                jobInvokeConfig = new JobInvokeConfig();
                jobInvokeConfig.setJobName(jobName);
                jobInvokeConfig.setJobVersion(jobVersion);
                if (jobMainClass.length() > 0) {
                    jobInvokeConfig.setJobMainClass(jobMainClass);
                }
            } else {
                // TODO HTTP should be supported
                throw new IllegalArgumentException("Protocol '" + protocol + "' is not supported.");
            }

            BindingProvider bp = (BindingProvider) port;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, parameters.getUrl());
            bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, parameters.getUsername());
            bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, parameters.getPassword());

            //execute

            //the text should be a map(key=value)
            Properties p = new Properties();
            if (parameters.getTisContext() != null) {
                for (ContextParam kv : parameters.getTisContext()) {
                    String value = kv.getValue();
                    if (kv.isItemXML()) {
                        //get item string from itempojopk
                        ItemCtrl2Local itemCtrl2Local = Util.getItemCtrl2Local();
                        ItemPOJO pojo = itemCtrl2Local.getItem(itemPK);
                        String updateReportXml = pojo.getProjectionAsString();
                        Element root = Util.parse(updateReportXml).getDocumentElement();
                        String concept = Util.getFirstTextNode(root, "Concept");
                        String key = Util.getFirstTextNode(root, "Key");
                        String[] ids = key.split("\\.");
                        String clusterPK = Util.getFirstTextNode(root, "DataCluster");
                        ItemPOJOPK itemPk = new ItemPOJOPK(new DataClusterPOJOPK(clusterPK), concept, ids);
                        try {
                            ItemPOJO itempojo = itemCtrl2Local.getItem(itemPk);

                            String itemXml = itempojo.getProjectionAsString();
                            value = Util.mergeExchangeData(itemXml, updateReportXml);
                        } catch (Exception e) {
                            throw new XtentisException(e);
                        }
                    }
                    p.setProperty(kv.getName(), value);
                }
            }

            Args args = new Args();
            Map<String, String> argsMap = new HashMap<String, String>();
            for (Object o : p.keySet()) {
                String key = (String) o;
                String value = p.getProperty(key);
                String param = "--context_param " + key + "=" + value;
                args.getItem().add(param);

                argsMap.put(key, value);
            }

            JobContainer.getUniqueInstance().getJobInvoker(jobName, jobVersion).call(argsMap);
            return "callJob Service successfully executed!'";
        } catch (XtentisException xe) {
            throw (xe);
        } catch (Exception e) {
            String err = "Could not execute callJob service " +
                    e.getClass().getName() + ": " + e.getLocalizedMessage();
            org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
            throw new XtentisException(e);
        }
    }

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public Serializable receiveFromOutbound(HashMap<String, Serializable> map)
			throws XtentisException {
		// TODO Auto-generated method stub
		return null;
	}

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void start() throws XtentisException {
		// TODO Auto-generated method stub
		
	}

    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void stop() throws XtentisException {
		// TODO Auto-generated method stub
		
	}

}
