package com.amalto.core.enterpriseutil;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.amalto.core.ejb.WorkflowServiceCtrlLocalBI;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocalHome;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocalHome;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlLocalHome;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationObjectCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationObjectCtrlLocalHome;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlLocalHome;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocalHome;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocalHome;
import com.amalto.core.objects.versioning.ejb.local.VersioningSystemCtrlLocal;
import com.amalto.core.objects.versioning.ejb.local.VersioningSystemCtrlLocalHome;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class EnterpriseUtil extends Util{
	//The only Static HashMap around (hopefully)
	private static HashMap<String,EJBLocalHome> localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();

	public static void flushLocalHomes() throws NamingException{
		localHomes = new HashMap<String, javax.ejb.EJBLocalHome>();
	}	
	public static EJBLocalHome getLocalHome(String jndi) throws NamingException{
		EJBLocalHome localHome = null;
		if (true) {
			localHome = localHomes.get(jndi);
			if (localHome == null) {
				localHome = (EJBLocalHome)new InitialContext().lookup(jndi);
				localHomes.put(jndi, localHome);
			}
		} else {
			localHome = (EJBLocalHome)new InitialContext().lookup(jndi);
		}
//		dumpClass(localHome.getClass());
		return localHome;
	}
	public static VersioningSystemCtrlLocalHome getVersioningSystemCtrlLocalHome() throws NamingException {
		return (VersioningSystemCtrlLocalHome) getLocalHome(VersioningSystemCtrlLocalHome.JNDI_NAME);
	}
	public static VersioningSystemCtrlLocal getVersioningSystemCtrlLocal() throws NamingException,CreateException {
		return getVersioningSystemCtrlLocalHome().create();
	}	
	
	public static RoutingOrderV2CtrlLocalHome getRoutingOrderV2CtrlLocalHome() throws NamingException {
		return (RoutingOrderV2CtrlLocalHome) getLocalHome(RoutingOrderV2CtrlLocalHome.JNDI_NAME);
	}
	public static RoutingOrderV2CtrlLocal getRoutingOrderV2CtrlLocal() throws NamingException,CreateException {
		return getRoutingOrderV2CtrlLocalHome().create();
	}		
	public static RoutingRuleCtrlLocalHome getRoutingRuleCtrlLocalHome() throws NamingException {
		return (RoutingRuleCtrlLocalHome) getLocalHome(RoutingRuleCtrlLocalHome.JNDI_NAME);
	}
	public static RoutingRuleCtrlLocal getRoutingRuleCtrlLocal() throws NamingException,CreateException {
		return getRoutingRuleCtrlLocalHome().create();
	}		
	public static RoleCtrlLocalHome getRoleCtrlLocalHome() throws NamingException {
		return (RoleCtrlLocalHome) getLocalHome(RoleCtrlLocalHome.JNDI_NAME);
	}
	public static RoleCtrlLocal getRoleCtrlLocal() throws NamingException,CreateException {
		return getRoleCtrlLocalHome().create();
	}
	
	public static TransformerV2CtrlLocalHome getTransformerV2CtrlLocalHome() throws NamingException {
		return (TransformerV2CtrlLocalHome) getLocalHome(TransformerV2CtrlLocalHome.JNDI_NAME);
	}
	public static TransformerV2CtrlLocal getTransformerV2CtrlLocal() throws NamingException,CreateException {
		return getTransformerV2CtrlLocalHome().create();
	}		
	public static UniverseCtrlLocalHome getUniverseCtrlLocalHome() throws NamingException {
		return (UniverseCtrlLocalHome) getLocalHome(UniverseCtrlLocalHome.JNDI_NAME);
	}
	public static UniverseCtrlLocal getUniverseCtrlLocal() throws NamingException,CreateException {
		return getUniverseCtrlLocalHome().create();
	}
	
	public static SynchronizationPlanCtrlLocalHome getSynchronizationPlanCtrlLocalHome() throws NamingException {
		return (SynchronizationPlanCtrlLocalHome) getLocalHome(SynchronizationPlanCtrlLocalHome.JNDI_NAME);
	}
	public static SynchronizationPlanCtrlLocal getSynchronizationPlanCtrlLocal() throws NamingException,CreateException {
		return getSynchronizationPlanCtrlLocalHome().create();
	}

	public static SynchronizationObjectCtrlLocalHome getSynchronizationObjectCtrlLocalHome() throws NamingException {
		return (SynchronizationObjectCtrlLocalHome) getLocalHome(SynchronizationObjectCtrlLocalHome.JNDI_NAME);
	}
	public static SynchronizationObjectCtrlLocal getSynchronizationObjectCtrlLocal() throws NamingException,CreateException {
		return getSynchronizationObjectCtrlLocalHome().create();
	}

	public static SynchronizationItemCtrlLocalHome getSynchronizationItemCtrlLocalHome() throws NamingException {
		return (SynchronizationItemCtrlLocalHome) getLocalHome(SynchronizationItemCtrlLocalHome.JNDI_NAME);
	}
	public static SynchronizationItemCtrlLocal getSynchronizationItemCtrlLocal() throws NamingException,CreateException {
		return getSynchronizationItemCtrlLocalHome().create();
	}

	public static String beforeSaving(String concept,String xml, String resultUpdateReport)throws Exception{
		//check before saving transformer
		boolean isBeforeSavingTransformerExist=false;
		Collection<TransformerV2POJOPK> wst = getTransformerV2CtrlLocal().getTransformerPKs("*");
		for(TransformerV2POJOPK id: wst){
			if(id.getIds()[0].equals("beforeSaving_"+concept)){
				isBeforeSavingTransformerExist=true;
				break;
			}
		}
		//call before saving transformer
		if(isBeforeSavingTransformerExist){
			
			try {
				final String RUNNING = "XtentisWSBean.executeTransformerV2.running";
				TransformerContext context = new TransformerContext(
						new TransformerV2POJOPK("beforeSaving_" + concept));
				String exchangeData = mergeExchangeData(xml,resultUpdateReport);
				//String exchangeData = resultUpdateReport;
				context.put(RUNNING, Boolean.TRUE);
				TransformerV2CtrlLocal ctrl = getTransformerV2CtrlLocal();
				TypedContent wsTypedContent = new TypedContent(
						exchangeData
								.getBytes("UTF-8"),
						"text/xml; charset=utf-8");
				ctrl.execute(
						context, 
						wsTypedContent,
						new TransformerCallBack() {
							public void contentIsReady(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.contentIsReady() ");
							}
							public void done(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.done() ");
								context.put(RUNNING, Boolean.FALSE);
							}
						}
				);
				while (((Boolean)context.get(RUNNING)).booleanValue()) {
					Thread.sleep(100);
				}				
				//TODO process no plug-in issue
				String outputErrorMessage = "";
				//Scan the entries - in priority, taka the content of the 'output_error_message' entry, 
				for(Entry<String, TypedContent> entry: context.getPipelineClone().entrySet()){

					if ("output_error_message".equals(entry.getKey()	)) {
						outputErrorMessage = new String(entry.getValue().getContentBytes(), "UTF-8");
						break;
					}
				}
				//handle error message
				if (outputErrorMessage.length() > 0) {

					String errorCode = "";
					String errorMessage = "";
					Pattern pattern = Pattern
							.compile("<error code=['\042](.*)['\042]>(.*)</error>");
					Matcher matcher = pattern.matcher(outputErrorMessage);
					while (matcher.find())

					{
						errorCode = matcher.group(1);
						errorMessage = matcher.group(2);

					}
					if (!errorCode.equals("") && !errorCode.equals("0")) {
						errorMessage = "ERROR_3:" + errorMessage;
						return errorMessage;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}	
	
	public static boolean beforeDeleting(String clusterName,String concept,String[] ids)throws Exception{
		//check before deleting transformer
		boolean isBeforeDeletingTransformerExist=false;
		Collection<TransformerV2POJOPK> transformers = getTransformerV2CtrlLocal().getTransformerPKs("*");
		for(TransformerV2POJOPK id: transformers){
			if(id.getIds()[0].equals("beforeDeleting_"+concept)){
				isBeforeDeletingTransformerExist=true;
				break;
			}
		}
		
		if(!isBeforeDeletingTransformerExist)return false;
		
		//call before deleting transformer
		
		final String RUNNING = "XtentisWSBean.executeTransformerV2.beforeDeleting.running";
	    TransformerContext context = new TransformerContext(new TransformerV2POJOPK("beforeDeleting_" + concept));
		context.put(RUNNING, Boolean.TRUE);
		TransformerV2CtrlLocal ctrl = getTransformerV2CtrlLocal();
		TypedContent wsTypedContent = new TypedContent(
				        buildItemPKString(clusterName,concept,ids).getBytes("UTF-8"),
						"text/xml; charset=utf-8");
				
		ctrl.execute(
						context, 
						wsTypedContent,
						new TransformerCallBack() {
							public void contentIsReady(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.beforeDeleting.contentIsReady() ");
							}
							public void done(TransformerContext context) throws XtentisException {
								org.apache.log4j.Logger.getLogger(this.getClass()).debug("XtentisWSBean.executeTransformerV2.beforeDeleting.done() ");
								context.put(RUNNING, Boolean.FALSE);
							}
						}
				);
				
		while (((Boolean)context.get(RUNNING)).booleanValue()) {
					Thread.sleep(100);
		}
				
		//TODO Scan the entries - in priority, taka the content of the specific entry
		
		return true;
	}
	
	public static String buildItemPKString(String clusterName,String conceptName,String[] ids) {
		
		 StringBuffer itemPKXmlString = new StringBuffer();
		
		 if(clusterName==null||clusterName.length()==0)return itemPKXmlString.toString();
		 if(conceptName==null||conceptName.length()==0)return itemPKXmlString.toString();
		 if(ids==null)return itemPKXmlString.toString();
		 
		 itemPKXmlString.append("<item-pOJOPK><concept-name>")
		                .append(conceptName)
		                .append("</concept-name><ids>")
		                .append(joinStrings(ids, "."))
		                .append("</ids><data-cluster-pOJOPK><ids>")
		                .append(clusterName)
		                .append("</ids></data-cluster-pOJOPK></item-pOJOPK>");
		                
       return itemPKXmlString.toString();
	}
	public static WorkflowServiceCtrlLocalBI getWorkflowService() throws XtentisException{
		String JNDI="amalto/local/service/workflow";
    	try {
    		
    		EJBLocalHome pluginHome=null;
    		InitialContext ctx = new InitialContext();
    		
   			pluginHome = (EJBLocalHome)ctx.lookup(JNDI);

	        //find create 
	        Method[] m = pluginHome.getClass().getMethods();
	        Method create = null;
	        for (int i = 0; i < m.length; i++) {
				if ("create".equals(m[i].getName())) {
					create = m[i];
					break;
				}
			}
	        if (create == null) {
	        	String err = "Unable to find create method on workflow service \""+JNDI+"\"";
	        	org.apache.log4j.Logger.getLogger(Util.class).error("getWorkflowService() "+err);
	    		throw new XtentisException(err);        	
	        }
	        
	        //call it
	        Object plugin = create.invoke(pluginHome,(Object[])null);
            //Util.dumpClass(plugin.getClass());
	    	return (WorkflowServiceCtrlLocalBI)plugin;
	    	
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    		String err = 
    			"Unable to instantiate the plugin  '"+JNDI+"': ";
    		if (e.getCause()!=null) {
    			err+="caused by "+e.getCause().getClass().getName()+": "+e.getCause().getMessage();
    		} else {
				err+=e.getClass().getName()+": "+e.getMessage();
    		}
    		org.apache.log4j.Logger.getLogger(Util.class).error("getWorkflowService() "+err);
    		throw new XtentisException(err);
	    }
    }
	
}
