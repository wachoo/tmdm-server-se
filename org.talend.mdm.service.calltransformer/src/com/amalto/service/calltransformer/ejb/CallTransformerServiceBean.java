

package com.amalto.service.calltransformer.ejb;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;

import sun.misc.BASE64Decoder;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ServiceCtrlBean;
import com.amalto.core.ejb.local.ItemCtrl2Local;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2CtrlBean;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerGlobalContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * <h1>Service Call Transformer</h1>
 *
 * <h3>Description</h3>
 * This service calls a transformer<br/>
 *
 * <h3>Parameters</h3>
 * This service takes a single parameter:<b>transformer</b>: the name of the transformer.<br/>
 * The transformer should expect to receive the content of the Item sent to the transformer
 * in the <code>DEFAULT</code> variable with a content-type of <code>text/xml</code>
 *
 * <h3>Configuration</h3>
 * No Configuration UI
 * <br/>
 * <br/>
 *
 * @author Bruno Grieder
 *
 * @ejb.bean name="CallTransformer"
 *           display-name="Call Transformer Service"
 *           description="Service d'intégration Call Transformer"
 * 		     local-jndi-name = "amalto/local/service/callprocess"
 *           type="Stateless"
 *           view-type="local"
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
public class CallTransformerServiceBean extends ServiceCtrlBean  implements SessionBean{

	private static final long serialVersionUID = 1L;
	private static final String Param_Transformer_Name = "process";

    private AbstractRoutingOrderV2POJO routingOrderPOJO = null;

//	private boolean configurationLoaded = false;

	//datamodels cach
	//protected HashMap<String, XSDKey> keys = new HashMap<String, XSDKey>();
	//protected HashMap<String, DataModelPOJO> dataModels = new HashMap<String, DataModelPOJO>();


//    private SessionContext context;

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
//    	context = ctx;
    }


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getJNDIName()
	 */
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/service/callprocess";
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getDescription()
	 */
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "Service qui appelle des processus";
		
		return "The service call process";
	}


    /**
     * @author achen
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "This service takes a single parameter: \n"+
    	"process: the name of the process. \n\n" +
    	"The process should expect to receive the content of the Item sent to the process in the DEFAULT variable \n"+
    	"with a content-type of text/xml. \n\n"+
    	"Example: "+Param_Transformer_Name+"=tiscall_test";
    }
	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getStatus()
	 */
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getStatus() throws XtentisException {
		return "OK";
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#start()
	 */
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void start() throws XtentisException {
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#stop()
	 */
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void stop() throws XtentisException {
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#receiveFromOutbound(java.util.HashMap, java.lang.String)
	 */
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public Serializable receiveFromOutbound(HashMap<String, Serializable> map) throws XtentisException {
		throw new XtentisException("The Call Transformer service is not meant to interact with adapters");
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.IServiceBean#run(java.lang.String)
	 */
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters) throws com.amalto.core.util.XtentisException {

		try {


			String transformer = null;

			if (parameters != null) {
				String kvs[] = parameters.split("&");
				if (kvs!=null) {
					for (int i = 0; i < kvs.length; i++) {
						String[] kv = kvs[i].split("=");
						String key = kv[0].trim().toLowerCase();

						if ((Param_Transformer_Name.equals(key)) && (kv.length == 2)) {
							transformer = kv[1].trim();
						}
					}

					if (transformer == null || "".equals(transformer)) {
		            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service CallTransformer - mandatory parameter transformer name is missing");
		            	throw new XtentisException("Service CallTransformer - mandatory parameter transformer name is missing");
		    		}

					TransformerV2CtrlLocal tctrl = Util.getTransformerV2CtrlLocal();

					if (tctrl.existsTransformer(new TransformerV2POJOPK(transformer)) == null) {
		            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service CallTransformer is unable to call transformer "+transformer+" - transformer doesn't exist");
		            	throw new XtentisException("Unable to find the transformer "+transformer);
		    		}

					ItemCtrl2Local ictrl  = Util.getItemCtrl2Local();
					ItemPOJO pojo = ictrl.getItem(itemPK);

					TransformerContext context = new TransformerContext(new TransformerV2POJOPK(transformer));
					context.putInPipeline(TransformerV2CtrlBean.DEFAULT_VARIABLE, new TypedContent(pojo.getProjectionAsString().getBytes(),"text/xml"));
					
                    AbstractRoutingOrderV2POJO routingOrder = (getRoutingOrderPOJO() == null ? Util.getRoutingOrderV2CtrlLocal()
                            .getRoutingOrder(new ActiveRoutingOrderV2POJOPK(routingOrderID)) : getRoutingOrderPOJO());
					String userToken=null;
					if(routingOrder!=null){
						try {
							userToken = new String((new BASE64Decoder()).decodeBuffer(routingOrder.getBindingUserToken()),"UTF-8");
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					TransformerGlobalContext globalContext=new TransformerGlobalContext(context);
					globalContext.setUserToken(userToken);
					
//					TransformerContext contextResult =
					tctrl.executeUntilDone(globalContext);

				}
			}

			return "CallTransformer Service successfully executed transformer '"+transformer+"'";
		} catch (Exception e) {
			String err =
				(new SimpleDateFormat("yyyyMMdd'T'HH:mm:ss, SSS")).format(new Date(System.currentTimeMillis()))
				+": ERROR routing to Call Transformer Service "
				+": "+e.getLocalizedMessage();
			if (e instanceof XtentisException) {
				throw new XtentisException(e);
			} else {
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err+" ("+e.getClass().getName()+")",e);
				throw new XtentisException(e);
			}
		}

	}


    /**
    *
    * @get the default configuration
    * @throws EJBException
    *
    * @ejb.interface-method view-type = "local"
    * @ejb.facade-method
    */
    public String getDefaultConfiguration(){

    	return
    		"<configuration/>";
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
    	try {
    		String configuration = loadConfiguration();
    		if (configuration == null) {
    			configuration = getDefaultConfiguration();
    		}
//    		configurationLoaded = true;
    		return configuration;
        } catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the Call Transformer Service"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }



    /**
     * @see com.amalto.core.ejb.ServiceCtrlBean#putConfiguration(java.lang.String)
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void putConfiguration(String configuration) throws XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("putConfiguration() "+configuration);
//		configurationLoaded = false;
		super.putConfiguration(configuration);
	}
	
	/**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public Serializable fetchFromOutbound(String command, String parameters,String schedulePlanID) throws XtentisException {
		
		
		try {
			
			//parse input parameter
			if(parameters==null||parameters.length()==0)throw new XtentisException("Parameters can not be empty! ");
			Document paramDoc=Util.parse(parameters);
			String transformerName=Util.getFirstTextNode(paramDoc, "//"+Param_Transformer_Name);
			
			String  typedContentType=Util.getFirstTextNode(paramDoc, "//typedContent/type");
			String	typedContentValue=Util.getFirstTextNode(paramDoc, "//typedContent/value");
			typedContentValue=StringEscapeUtils.unescapeXml(typedContentValue);
			
			//TODO care about output log to UI-console
			
			//execute main process
			if (transformerName == null || "".equals(transformerName)) {
            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service CallTransformer - mandatory parameter transformer name is missing");
            	throw new XtentisException("Service CallTransformer - mandatory parameter transformer name is missing");
    		}
			
			TransformerV2CtrlLocal tctrl = Util.getTransformerV2CtrlLocal();
			if (tctrl.existsTransformer(new TransformerV2POJOPK(transformerName)) == null) {
            	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Service CallTransformer is unable to call transformer "+transformerName+" - transformer doesn't exist");
            	throw new XtentisException("Unable to find the transformer "+transformerName);
    		}
			TransformerContext context = new TransformerContext(new TransformerV2POJOPK(transformerName));
			context.putInPipeline(TransformerV2CtrlBean.DEFAULT_VARIABLE, new TypedContent(typedContentValue.getBytes(),typedContentType));
			tctrl.executeUntilDone(context);

			return "OK";
		} catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to fetchFromOutbound of the Call Transformer Service"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

	}

    /**
     * Tie a routing order to a service controller
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void setRoutingOrderPOJO(AbstractRoutingOrderV2POJO routingOrderPOJO) throws XtentisException {
        this.routingOrderPOJO = routingOrderPOJO;
    }

    /**
     * DOC Starkey Comment method "getRoutingOrderPOJO".
     * 
     * @return
     */
    public AbstractRoutingOrderV2POJO getRoutingOrderPOJO() {
        return routingOrderPOJO;
    }


}