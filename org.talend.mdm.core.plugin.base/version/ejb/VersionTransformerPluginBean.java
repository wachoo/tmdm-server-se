package com.amalto.core.plugin.base.version.ejb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.ejb.SessionBean;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.objects.transformers.v2.ejb.TransformerPluginV2CtrlBean;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.plugin.base.version.CompiledParameters;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * @author bgrieder
 * 
 * @ejb.bean name="VersionTransformerPlugin"
 *           display-name="Name for VersionPlugin"
 *           description="Description for VersionPlugin"
 * 		  local-jndi-name = "amalto/local/transformer/plugin/version"
 *           type="Stateless"
 *           view-type="local"
 *           local-business-interface="com.amalto.core.objects.transformers.v2.util.TransformerPluginV2LocalInterface"
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
public class VersionTransformerPluginBean extends TransformerPluginV2CtrlBean  implements SessionBean{
  
	private static final String PARAMETERS = "com.amalto.core.plugin.version.parameters"; 
	
	private static final String INPUT_XML = "xml";
	//private static final String INPUT_RESULTING_CONTENT_TYPE = "output_content_type";
	private static final String OUTPUT_TEXT = "text";
	
	private static final long serialVersionUID = 11487098995680L;
	
    private transient boolean configurationLoaded = false;


	public VersionTransformerPluginBean() {
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
		return "amalto/local/transformer/plugin/version";
	}
	
	
	
    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "Execute un version sur un xml et retourne le résultat";
		return "Executes an XPath on an XML document and returns the result";
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
		 descriptor1.setVariableName(INPUT_XML);
		 descriptor1.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/xml")
						})
				)
		 );
		 HashMap<String, String> descriptions1 = new HashMap<String, String>();
		 descriptions1.put("en", "The xml to run the Version on");
		 descriptor1.setDescriptions(descriptions1);
		 descriptor1.setMandatory(true);
		 descriptor1.setPossibleValuesRegex(null);
		 inputDescriptors.add(descriptor1);
		 
		 /* To be implemented when the Version is passed as variable
		 TransformerPluginVariableDescriptor descriptor2 = new TransformerPluginVariableDescriptor();
		 descriptor2.setVariableName(INPUT_RESULTING_CONTENT_TYPE);
		 descriptor2.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/plain")
						})
				)
		 );
		 HashMap<String, String> descriptions2 = new HashMap<String, String>();
		 descriptions2.put("en", "The reculting content-type after running the Xpath. Non Mandatory. Default: text/plain; charset=utf-8");
		 descriptor2.setDescriptions(descriptions2);
		 descriptor2.setMandatory(false);
		 descriptor2.setPossibleValuesRegex(null);
		 inputDescriptors.add(descriptor2);
		 */

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
								 Pattern.compile("text.*")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The result of the Version");
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
						
			if (!configurationLoaded) loadConfiguration();
			
			CompiledParameters parameters = CompiledParameters.deserialize(compiledParameters);
			context.put( PARAMETERS, parameters);
			
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not init the Version plugin:"+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(this.getClass()).error("start() "+err);
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
		
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() ");
		CompiledParameters parameters= (CompiledParameters)context.get( PARAMETERS);
		TypedContent xmlTC = (TypedContent)context.get(INPUT_XML);
		
		try {
			
			//attempt to read charset
			String charset = Util.extractCharset(xmlTC.getContentType());
			String xml = new String(xmlTC.getContentBytes(),charset);

			//read the bytes from Bytes in priority, 
			//If non available, attempt to read from the stream and insert the bytes into the TypedContent
			NodeList nodes = Util.getNodeList(Util.parse(xml), parameters.getXPath());
			for (int i = 0; i < nodes.getLength(); i++) {
				Node n = nodes.item(i);
				String res = null;
				if (parameters.getResultingContentType().startsWith("text/plain")) {
					res = Util.getFirstTextNode(n, ".");
				} else if (parameters.getResultingContentType().startsWith("text/xml")){
					res = Util.nodeToString(n);
				} else {
					String err = "Version Plugin: Could not parse the result of the Version with content type "+parameters.getResultingContentType()
										+"\n The version is "+parameters.getXPath()+".The xml is\n"+xml;
					org.apache.log4j.Category.getInstance(this.getClass()).error(err);
					throw new XtentisException(err);
				}
				//save result to context
				context.put(OUTPUT_TEXT, res);
				//call the callback content is ready
				context.getPluginCallBack().contentIsReady(context);				
			}
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() XPath done");
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not start the Version transformer plugin "+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Category.getInstance(this.getClass()).error("start() "+err);
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
		"The XPath plugin executes an XPath on an xml document\n" +
		"The result of the Version may be an XML fragment (content type of text/xml) or\n" +
		"A text fragment (content type text/plain) or\n" +
		" A nodes set in which case the plugin will loop over the results\n"+
		"\n" +
		"\n" +
		"Parameters\n" +
		"	the Version expression [mandatory]: the version expression to run"+"\n"+
		"	contentType [optional]: the content type of the result of the x path fragment"+"\n"+
		"\n"+
		"\n"+
		"Example" +"\n"+
		"	<parameters>" +"\n"+
		"		<Version>//Buyer/Name[../City='Paris']</Version>" +"\n"+
		"		<contentType>text/plain</contentType>" +"\n"+
		"	</parameters>"+"\n"+
		"\n"+
		"\n"+
		"Notes for Plugin Developers: " +"\n"+
		"	None";
	}


	private String getDefaultConfiguration(){
    	return
    		"<configuration>"+
    		"	<Version>.</Version>"+
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
    	    String err = "Unable to deserialize the configuration of the XPath Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
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
    		//content Type - defaults to "text/plain; charset=utf-8"
    		String contentType = Util.getFirstTextNode(params, "contentType");
    		if (contentType == null) contentType = "text/plain; charset=utf-8";
    		compiled.setResultingContentType(contentType);
    		//Version - mandatory
			String Version = Util.getFirstTextNode(params, "Version");
			if (Version==null) {
				String err = "The Version Parameter of the Version Transformer Plugin cannot be empty";
				org.apache.log4j.Category.getInstance(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setXPath(Version);
    		
    		return compiled.serialize();
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the configuration of the Version Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }	
	}




    
}