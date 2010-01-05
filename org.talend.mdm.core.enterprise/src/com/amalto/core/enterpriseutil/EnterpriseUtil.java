package com.amalto.core.enterpriseutil;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang.StringEscapeUtils;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.WorkflowServiceCtrlLocalBI;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocalHome;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocalHome;
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
import com.amalto.core.validation.Schema;
import com.amalto.core.validation.SchemaFactory;
import com.amalto.core.validation.Validator;
import com.amalto.core.validation.Violation;

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
	public static Document validate(Element element, String schema) 
	throws Exception{
		Document d=defaultValidate(element, schema);
		
		Document xsdDoc = parse(schema);
		//schematron validate see 0008753: Implement Schematron
		String concept=element.getLocalName();
		Node schemaRoot=xsdDoc.getDocumentElement();
	   	Element rootNS=getRootElement("nsholder",schemaRoot.getNamespaceURI(),"xsd");	
	   	String xpath="//xsd:element[@name='" + concept + "']//xsd:appinfo[@source='"+ ICoreConstants.X_Schematron+"']/text()";
		NodeList tsList=getNodeList(schemaRoot,xpath,rootNS.getNamespaceURI(),"xsd");
		
		//String SCHEMATRON=ICoreConstants.SCHEMATRON_TAG;//"<schema xmlns=\"http://www.ascc.net/xml/schematron\" ns=\"http://xml.apache.cocoon/xmlform\">";
		for(int i=0; i<tsList.getLength();i++){
			String sch= nodeToString(tsList.item(i), true);
			sch=StringEscapeUtils.unescapeXml(sch);
			//String sch= tsList.item(i).getNodeValue();
			//sch=sch.replaceAll("<"+ICoreConstants.SCHEMATRON_RULE+">", SCHEMATRON);
			//sch=sch.replaceAll("</"+ICoreConstants.SCHEMATRON_RULE+">", "</schema>");
			if(sch ==null || sch.trim().length()==0) continue;
			StringBuffer sb=new StringBuffer();
			sb.append("<schema xmlns=\"http://www.ascc.net/xml/schematron\" >");
			sb.append(sch);
			sb.append("</schema>");
			schematronValidate(element, sb.toString(), concept);
		}
		//end
		return d;
	}
    public static void schematronValidate(Element element, String schematron, String concept)throws Exception{
        
    	InputSource is = new InputSource ( new ByteArrayInputStream(schematron.getBytes("utf-8")) );
        SchemaFactory schf = SchemaFactory.lookup( SchemaFactory.NAMESPACE_SCHEMATRON );
        Schema sch = schf.compileSchema( is );
        Validator validator = sch.newValidator();

        // set preprocessor parameters 
         //if (args.length > 1)
           // validator.setProperty(Validator.PROPERTY_PHASE, "New");
         
         List violations = null;

         //violations = validator.validate( tbean );
         violations=validator.validate(element);
 
         // everything ok?
         if (violations == null || violations.size()==0) 
         {
           System.out.println("\nValidation ok, no messages generated");
         }
         else {
           System.out.println("Validation encountered errors. Messages :");
           Iterator viter = violations.iterator();
           StringBuffer sb =new StringBuffer();
           while (viter.hasNext ())
           {
             Violation v = (Violation) viter.next();
             //sb.append("Violation path: " + v.getPath() + ", message: " + v.getMessage() );
             sb.append( v.getMessage()+"\n" );
           }
           if(sb.toString().length()>0)
           throw new XtentisException(sb.toString());
         }
    }
}
