package talend.core.transformer.plugin.v2.tiscall.ejb;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.SessionBean;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import talend.core.transformer.plugin.v2.tiscall.CompiledParameters;
import talend.core.transformer.plugin.v2.tiscall.ConceptMappingParam;
import talend.core.transformer.plugin.v2.tiscall.ContextParam;
import talend.core.transformer.plugin.v2.tiscall.util.JSONException;
import talend.core.transformer.plugin.v2.tiscall.util.JSONObject;
import talend.core.transformer.plugin.v2.tiscall.webservices.Args;
import talend.core.transformer.plugin.v2.tiscall.webservices.ArrayOfXsdString;
import talend.core.transformer.plugin.v2.tiscall.webservices.WSxml;
import talend.core.transformer.plugin.v2.tiscall.webservices.WSxmlService;

import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.TransformerPluginV2CtrlBean;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean 	name="TISCallTransformerPlugin"
 *           	display-name="Name for TISCallPlugin"
 *           	description="Description for TISCallPlugin"
 * 		  		local-jndi-name = "amalto/local/transformer/plugin/callJob"
 *           	type="Stateless"
 *           	view-type="local"
 *           	local-business-interface="com.amalto.core.objects.transformers.v2.util.TransformerPluginV2LocalInterface"
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
public class TISCallTransformerPluginBean extends TransformerPluginV2CtrlBean  implements SessionBean{
  
	private static final String CONTENT_TYPE = "com.amalto.core.plugin.TISCall.content.type";
	private static final String PORT = "com.amalto.core.plugin.TISCall.port";
	private static final String TIS_VARIABLE_NAME = "com.amalto.core.plugin.TISCall.tis.variable.name";
	
	private static final String INPUT_TEXT = "text";

	private static final String OUTPUT_TEXT = "result";
	
	private static final long serialVersionUID = 1L;
	
    private transient boolean configurationLoaded = false;
    
    CompiledParameters compiledParameters;

	public TISCallTransformerPluginBean() {
		super();
		try {
			getConfiguration(null);
		} catch (Exception e) {}
	}


	
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/transformer/plugin/callJob";
	}
	
	
	
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "Execute un call de TIS un texte et retourne le résultat";
		return "Executes a TIS Job on a text and returns the result";
	}


	
	/**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public ArrayList<TransformerPluginVariableDescriptor> getInputVariableDescriptors(String twoLettersLanguageCode) throws XtentisException {
		 ArrayList<TransformerPluginVariableDescriptor> inputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();
		 
		 //The csv_line descriptor
		 TransformerPluginVariableDescriptor descriptor1 = new TransformerPluginVariableDescriptor();
		 descriptor1.setVariableName(INPUT_TEXT);
		 descriptor1.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text.*")
						})
				)
		 );
		 HashMap<String, String> descriptions1 = new HashMap<String, String>();
		 descriptions1.put("en", "The text to run the TISCall on");
		 descriptor1.setDescriptions(descriptions1);
		 descriptor1.setMandatory(false);
		 descriptor1.setPossibleValuesRegex(null);
		 inputDescriptors.add(descriptor1);
		 
		 return inputDescriptors;
	}


	
	/**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public ArrayList<TransformerPluginVariableDescriptor> getOutputVariableDescriptors(String twoLettersLanguageCode) throws XtentisException {
		ArrayList<TransformerPluginVariableDescriptor> outputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();
		 
		 //The csv_line descriptor
		 TransformerPluginVariableDescriptor descriptor = new TransformerPluginVariableDescriptor();
		 descriptor.setVariableName(OUTPUT_TEXT);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/xml")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The result of the TIS call");
		 descriptor.setDescriptions(descriptions);
		 descriptor.setMandatory(true);
		 descriptor.setPossibleValuesRegex(null);
		 outputDescriptors.add(descriptor);
		 
		 return outputDescriptors;
	}
	
	
	
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void init(
			TransformerPluginContext context, 
			String compiledParameters 
			) throws XtentisException {
		try {
						
			if (!configurationLoaded) 
				loadConfiguration();
			
			CompiledParameters parameters = CompiledParameters.deserialize(compiledParameters);			
			//context.put(PARAMETERS, parameters);
	        this.compiledParameters=parameters;
	        URL wsdlURL = SynchronizationPlanCtrlLocal.class.getResource("/META-INF/wsdl/tis.wsdl");
			
			WSxmlService service = new WSxmlService(wsdlURL, new QName("http://talend.org", "WSxmlService"));
			WSxml port = service.getWSxml();
			
			//set the parameters
			BindingProvider bp = (BindingProvider)port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, parameters.getUrl());
			bp.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, parameters.getUsername());
			bp.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, parameters.getPassword());
			
			context.put(CONTENT_TYPE, parameters.getContentType());
			context.put(PORT, port);
			context.put(TIS_VARIABLE_NAME, parameters.getTisVariableName());
			
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not init the TISCall plugin:"+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
		} 
		
	}

	
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void execute(TransformerPluginContext context) throws XtentisException {
		
		
		String contentType = (String)context.get(CONTENT_TYPE);
		TypedContent textTC = (TypedContent)context.get(INPUT_TEXT);
		
		String tisVariableName = (String)context.get(TIS_VARIABLE_NAME);
		
		try {
					
			//attempt to read charset and rebuild the input text string
			String charset = Util.extractCharset(textTC.getContentType());
			String text = new String(textTC.getContentBytes(),charset);
			//if(text==null || text.length()==0) return;
//			org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() TIS CALL to '"+parameters.getUrl()+"' input \n"+text);

			//revover the port
			WSxml port = (WSxml) context.get(PORT);
			
			//the text should be a map(key=value)
			Properties p=new Properties();
			//p.load(new ByteArrayInputStream(text.getBytes()));	
			if(compiledParameters.getTisContext()!=null){
				for(ContextParam kv:compiledParameters.getTisContext()){
					String value=kv.getValue();
					//if piplevariablename ,get from globalcontext
					if(kv.isPipleVariableName()){
						 textTC=getGlobalContext().getFromPipeline(kv.getValue());
						 charset = Util.extractCharset(textTC.getContentType());
						 if(textTC!=null)
							 value = new String(textTC.getContentBytes(),charset);
						 else
							 value="";
					}
					p.setProperty(kv.getName(), value);					
				}
			}
			
			Args args = new Args();	
			Iterator it=p.keySet().iterator();
			while(it.hasNext()){
				String key=(String)it.next();
				String value=p.getProperty(key);
				String param = "--context_param "+key+"="+value;
				args.getItem().add(param);
			}
			
			//parse concept parameters
			ConceptMappingParam conceptMappingParam=compiledParameters.getConceptMappingParam();
			boolean hasConcept=false;
			String conceptName="";
			boolean hasFields=false;
			JSONObject fieldsObject=null;
			if(conceptMappingParam!=null){
				if(conceptMappingParam.getConcept().length()>0){
					hasConcept=true;
					conceptName=conceptMappingParam.getConcept();
				}
				if(conceptMappingParam.getFields().length()>0){
					hasFields=true;
					fieldsObject=new JSONObject(conceptMappingParam.getFields());
				}
			}
			//Build call parameters
			
//			Args args = new Args();			
//			args.getItem().add(param);
			List<ArrayOfXsdString> list=port.runJob(args).getItem();
			String result="";
			StringBuffer sb=new StringBuffer();
			if(list.size()>0){
				sb=sb.append("<results>\n");
				for(int i=0; i<list.size(); i++){
					List<String> results=list.get(i).getItem();
					
					if(hasConcept){
						sb=sb.append("<"+conceptName+">\n");
					}else{
						sb=sb.append("<item>\n");
					}
									
					for(int j=0; j<results.size(); j++){
						String str=results.get(j);
						if(str!=null)
						{
							if(hasFields){
								if(fieldsObject.has("p"+j)){
									String columnName=(String) fieldsObject.get("p"+j);
									sb=sb.append("<"+columnName+">" + str + "</"+columnName+">" +"\n");
								}else{
									//do nothing
								}
								
							}else{
								sb=sb.append("<attr>" + str + "</attr>" +"\n");
							}
							
						}
					}			
					
					if(hasConcept){
						sb=sb.append("</"+conceptName+">\n");
					}else{
						sb=sb.append("</item>\n");
					}
					
				}
				sb=sb.append("</results>" +"\n");
			}else{ //exception
				
			}
			//String result = port.runJob(args).getItem().get(0).getItem().get(0);
			result=sb.toString();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() TIS CALL  result \n"+result);
			//save result to context
			if (result != null) { 
    			context.put(OUTPUT_TEXT, new TypedContent(
    				result.getBytes("utf-8"),
    				contentType
    			));
			} else {
				context.put(OUTPUT_TEXT,null);
			}

			//call the callback content is ready
			context.getPluginCallBack().contentIsReady(context);				
			
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not execute the tisCall transformer plugin "+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
		} 
	}
	
	
    
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void end(TransformerPluginContext context) throws XtentisException {
    	context.removeAll();
	}


	
    
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getParametersSchema() throws XtentisException {
		return null;
	}
	
	
    
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDocumentation(String twoLettersLanguageCode) throws XtentisException {
		return
		"The TIS Call plugin executes a Web Service call to TIS on a text\n" +
		"\n" +
		"\n" +
		"Parameters\n" +
		"	url [mandatory]: the webservice port URL to the TIS Server"+"\n"+
		"	contextParam   : the contextParam of the tis job"+"\n"+
		"		name: the name of the context param"+"\n"+
		"		value: the value of context param, the value will be viewed as a priple" + "\n" + 
		"                   variable if the value is embraced with a brace, "+"\n"+
		"		isPipleVariableName [optional]: true to set contextParam value as one " + "\n" + 
		"                                            piplevariableName , this parameter will be " + "\n" + 
		"                                            ignored if value is embraced with brace" + "\n" +
		"	username [optional]: the username to use for the call"+"\n"+
		"	password [optional]: the password to  use for the call" +"\n"+
		"	contentType [optional]: the contentType of the returned data. Defaults to 'text/xml'" +"\n"+
		"	conceptMapping [optional]: Directly map the result of a TIS call to a TOM concept"+"\n"+
		"		concept: the name of the concept"+"\n"+
		"		fields: mapping rule with json format"+"\n"+
		"\n"+
		"\n"+
		"Example" +"\n"+
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
		"			<name>company</name>" +"\n"+
		"			<value>{pipleVariableName}</value>" +"\n"+
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
		"\n"+
		"\n";
	}


	private String getDefaultConfiguration(){
    	return
		"<configuration>"+
		"	<url>http://server:port/TISService/TISPort</url>"+
		"	<username></username>"+
		"	<password></password>"+
		"	<contentType>text/xml</contentType>"+
		"</configuration>";
    }
    


	
    /**
     * @throws XtentisException
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
    		configurationLoaded = true;
    		return configuration;
        } catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the TISCall Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }	
    }



	
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void putConfiguration(String configuration) throws XtentisException {
		configurationLoaded = false;
		super.putConfiguration(configuration);
	}
	
	

	
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String compileParameters(String parameters) throws XtentisException {
    	try {
    		CompiledParameters compiled = new CompiledParameters();
    		Element params = Util.parse(parameters).getDocumentElement();
    		
    		//TISCall - mandatory
    		String url = Util.getFirstTextNode(params, "url");
    		if (url==null) {
    			String err = "The url parameter of the TIS Call Transformer Plugin cannot be empty";
    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    			throw new XtentisException(err);
    		}
    		compiled.setUrl(url);
    		
    		String tisVariableName = Util.getFirstTextNode(params, "tisVariableName");
//    		if (tisVariableName==null) {
//    			String err = "The TIS variable name parameter of the TIS Call Transformer Plugin cannot be empty";
//    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
//    			throw new XtentisException(err);
//    		}
    		compiled.setTisVariableName(tisVariableName);
			Document parametersDoc = Util.parse(parameters);
			List<ContextParam> paramsList=new ArrayList<ContextParam>();
			NodeList paramList = Util.getNodeList(parametersDoc.getDocumentElement(), "//contextParam");
			for (int i=0; i<paramList.getLength(); i++) {
				String paramName = Util.getFirstTextNode(paramList.item(i), "name");
				String paramValue = Util.getFirstTextNode(paramList.item(i), "value");
				String isPipleVariableName = Util.getFirstTextNode(paramList.item(i), "isPipleVariableName");
				if (paramValue == null)
					paramValue = "";
				
				if (paramName!=null) {
					boolean ispiple=false;
					if(isPipleVariableName!=null){
						ispiple=Boolean.valueOf(isPipleVariableName).booleanValue();
					}
					Pattern pipeParamPattern = Pattern.compile("\\{([^\\}]+)\\}");
					Matcher match = pipeParamPattern.matcher(paramValue);
					if (match.matches()) {
						paramValue = match.group(1);
						ispiple = true;
					}
					paramsList.add(new ContextParam(paramName,paramValue,ispiple));
				}
			}
			compiled.setTisContext(paramsList);
    		//content Type - defaults to "text/plain; charset=utf-8"
    		String contentType = Util.getFirstTextNode(params, "contentType");
    		if (contentType == null) contentType = "text/xml; charset=utf-8";
    		compiled.setContentType(contentType);

    		//username - defaults to null
    		String username = Util.getFirstTextNode(params, "username");
    		compiled.setUsername(username);

    		//password - defaults to null
    		String password = Util.getFirstTextNode(params, "password");
    		compiled.setPassword(password);
    		
    		//conceptMapping
    		NodeList conceptMappingList = Util.getNodeList(parametersDoc.getDocumentElement(), "//conceptMapping");
    		if(conceptMappingList!=null&&conceptMappingList.getLength()>0){
    			Node conceptMapping=conceptMappingList.item(0);
    			String concept=Util.getFirstTextNode(conceptMapping, "concept");
    			String fields=Util.getFirstTextNode(conceptMapping, "fields");
    			try {
    				new JSONObject(fields);
    			} catch (JSONException e) {
    				String err = "The format of fields parameter of conceptMapping is invalid";
    				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    				throw new XtentisException(err);
    			}
    			compiled.setConceptMappingParam(new ConceptMappingParam(concept,fields));
    		}else{
    			compiled.setConceptMappingParam(null);
    		}
    		
    		return compiled.serialize();
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the configuration of the TISCall Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }	
	}




    
}