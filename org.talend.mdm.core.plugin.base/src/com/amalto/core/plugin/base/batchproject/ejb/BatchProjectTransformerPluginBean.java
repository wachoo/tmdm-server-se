package com.amalto.core.plugin.base.batchproject.ejb;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.Plugin;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.plugin.base.batchproject.CompiledParameters;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 *
 * @author Starkey Shu
 *
 * @ejb.bean name="BatchProjectPlugin"
 *           display-name="Name for BatchProjectPlugin"
 *           description="Description for BatchProjectPlugin"
 * 		     local-jndi-name = "amalto/local/transformer/plugin/batchproject"
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
 */
     

public class BatchProjectTransformerPluginBean extends Plugin {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1100573389664234095L;
	//parameters
	public static final String PARAMETERS ="com.amalto.core.plugin.batchproject.parameters";
	//various
	private static final String INPUT_XML ="xml_instance";
	private static final String INPUT_PATH ="file_path";
	private static final String OUTPUT_XML ="unavailable_content";

	private static final Pattern declarationPattern = Pattern.compile("<\\?.*?\\?>",Pattern.DOTALL);
	private static final String overwriteRegex ="(true|false)";
	private static final String br ="\n";
	
	
    private transient boolean configurationLoaded = false;

	public BatchProjectTransformerPluginBean() {
		super();
		try {
			getConfiguration(null);
		} catch (Exception e) {};
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/transformer/plugin/batchproject";
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLettersLanguageCode)
			throws XtentisException {
		String description="";
		if(twoLettersLanguageCode.toLowerCase().equals("en")){
			description="Batch projecting items to the Data Manager";
		}else{
			description="Unsupported language! ";
		}
		return description;
	}

	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDocumentation(String twoLettersLanguageCode)
			throws XtentisException {
		return
		"Batch projecting items with the same concept to a specific Data Cluster. \n" +
		"\n" +
		"\n" +
		"Parameters\n" +
		"	dataClusterName [mandatory]: the Data Cluster to use "+"\n"+
		"	dataModelName [mandatory]: the Data Model to use "+"\n"+
		"	conceptName [mandatory]: the Concept Model to use "+"\n"+
		"	overwrite [optional]: overwrite an existing item 'true' or 'false'. Default: 'true'"+"\n"+
		"\n"+
		"\n"+
		"Example" +"\n"+
		"	<parameters>" +"\n"+
		"		<dataClusterName>Order</dataClusterName>" +"\n"+
		"		<dataModelName>Order</dataModelName>" +"\n"+
		"		<conceptName>Customer</conceptName>" +"\n"+
		"	</parameters>"+"\n"+
		"\n"+
		"\n"+
		"Notes for Plugin Developers: " +"\n"+
		"		empty"	;
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public ArrayList<TransformerPluginVariableDescriptor> getInputVariableDescriptors(
			String twoLettersLanguageCode) throws XtentisException {
		 ArrayList<TransformerPluginVariableDescriptor> inputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();
		
		 TransformerPluginVariableDescriptor descriptor = new TransformerPluginVariableDescriptor();
		 descriptor.setVariableName(INPUT_XML);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/xml")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The items instances to project");
		 descriptor.setDescriptions(descriptions);
		 descriptor.setMandatory(false);
		 descriptor.setPossibleValuesRegex(null);
		 inputDescriptors.add(descriptor);
		 
		 TransformerPluginVariableDescriptor descriptor2 = new TransformerPluginVariableDescriptor();
		 descriptor2.setVariableName(INPUT_PATH);
		 descriptor2.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/.*")
						})
				)
		 );
		 HashMap<String, String> descriptions2 = new HashMap<String, String>();
		 descriptions2.put("en", "An xml file path to pass to the batch project");
		 descriptor2.setDescriptions(descriptions2);
		 descriptor2.setMandatory(false);
		 descriptor2.setPossibleValuesRegex(null);
		 inputDescriptors.add(descriptor2);
		 
		 return inputDescriptors;
		
	}


	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public ArrayList<TransformerPluginVariableDescriptor> getOutputVariableDescriptors(
			String twoLettersLanguageCode) throws XtentisException {
		 ArrayList<TransformerPluginVariableDescriptor> outputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();

		 //The output descriptor
		 TransformerPluginVariableDescriptor descriptor = new TransformerPluginVariableDescriptor();
		 descriptor.setVariableName(OUTPUT_XML);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/xml")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The content of items which are invalid during projecting ");
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
	public String getParametersSchema() throws XtentisException {
		// Is this feature in use now?
		return
		"<xsd:schema" +
		" 		elementFormDefault='unqualified'" +
		"		xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
		">" +
		"<xsd:element name='parameters'>" +
		"			<xsd:complexType >" +
		"				<xsd:sequence>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='dataClusterName' type='xsd:string'/>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='dataModelName' type='xsd:string'/>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='conceptName' type='xsd:string'/>" +
		"					<xsd:element minOccurs='0' maxOccurs='1' nillable='false' name='overwrite' type='xsd:string'/>" +
		"				</xsd:sequence>" +
		"			</xsd:complexType>" +
		"</xsd:element>"+
		"</xsd:schema>";

	}

	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String compileParameters(String parameters) throws XtentisException {
		try {
					
			if(parameters==null||parameters.length()==0)return "";
			
    		CompiledParameters compiled = new CompiledParameters();
    		Element params = Util.parse(parameters).getDocumentElement();
    		
    		//mandatory case
			String dataClusterName = Util.getFirstTextNode(params, "dataClusterName");
			if (dataClusterName==null||dataClusterName.length()==0) {
				String err = "The dataClusterName parameter of the BatchProject Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setDataClusterName(dataClusterName);
    		
    		String dataModelName = Util.getFirstTextNode(params, "dataModelName");
			if (dataModelName==null||dataModelName.length()==0) {
				String err = "The dataModelName parameter of the BatchProject Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setDataModelName(dataModelName);
    		
    		String conceptName = Util.getFirstTextNode(params, "conceptName");
			if (conceptName==null||conceptName.length()==0) {
				String err = "The conceptName parameter of the BatchProject Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setConceptName(conceptName);
    		
    		//optional case
    		boolean isOverwrite=true;
    		String overwrite = Util.getFirstTextNode(params, "overwrite");
			if (overwrite!=null&&overwrite.length()>0) {
				if(!overwrite.trim().matches(overwriteRegex)){
					String err = "The format of the overwrite parameter of the BatchProject Transformer Plugin is invalid";
					org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
					throw new XtentisException(err);
				}
				isOverwrite=Boolean.parseBoolean(overwrite.trim());
			}
    		compiled.setOverwrite(isOverwrite);

    		return compiled.serialize();
    		
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the configuration of the BatchProject Plugin"
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
	public void init(TransformerPluginContext context, String compiledParameters)
			throws XtentisException {

		try {
			if (!configurationLoaded) loadConfiguration();

			//parse parameters
			CompiledParameters parameters=CompiledParameters.deserialize(compiledParameters);	

			context.put(PARAMETERS, parameters);
			
		} catch (Exception e) {
			String err = "Could not init the BatchProject plugin:"+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
		}
		
	}

    @Override
    protected String loadConfiguration() {
        return null;
    }

    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void execute(TransformerPluginContext context)
			throws XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() BatchProject");	
		try {
			CompiledParameters parameters= (CompiledParameters)context.get(PARAMETERS);
			TypedContent xmlTC = (TypedContent)context.get(INPUT_XML);
			TypedContent pathTC = (TypedContent)context.get(INPUT_PATH);
			//attempt to read charset
			String xml="";
			if (pathTC!=null&&pathTC.getContentBytes()!=null) {
				String charset = Util.extractCharset(pathTC.getContentType());
				String pathText = new String(pathTC.getContentBytes(),charset);
				 // Read the entire contents of sample.txt
	            xml = FileUtils.readFileToString(new File(pathText));
	            org.apache.log4j.Logger.getLogger(this.getClass()).info("Using xml file...");
			}else {
				String charset =  Util.extractCharset(xmlTC.getContentType());
				xml= new String(xmlTC.getContentBytes(),charset);
				org.apache.log4j.Logger.getLogger(this.getClass()).info("Using xml string...");
			}
			
			//cleanup declaration
			xml = declarationPattern.matcher(xml).replaceAll("");
			
			//main process
			String conceptName=parameters.getConceptName();
			String dataClusterName=parameters.getDataClusterName();
			String dataModelName=parameters.getDataModelName();
			boolean isOverwrite=parameters.isOverwrite();
			
            //get the Data Model POJO from the cache
			DataModelPOJO dataModelPOJO  = Util.getDataModelCtrlLocal().getDataModel(new DataModelPOJOPK(dataModelName));
			//TODO add data model cache
			
			String resultContent="<no-insert-items>"+br+br;
			Document doc=Util.parse(xml);
			NodeList nlist=Util.getNodeList(doc, "//"+conceptName);
			
			
			if(nlist.getLength()>0){
				long beginTime=System.currentTimeMillis();
				for (int i = 0; i < nlist.getLength(); i++) {
					Node selectedConceptNode=nlist.item(i);
					String selectedConceptXml=Util.nodeToString(selectedConceptNode);
					
					//Determine item Key
					Document dataModelXSD=Util.parse(dataModelPOJO.getSchema());
					ItemPOJOPK pk = Util.getItemPOJOPK(
							new DataClusterPOJOPK(dataClusterName),
							(Element) selectedConceptNode,
							dataModelXSD
					);
					
					
					//create the item
					ItemPOJO newItemPOJO = 	new ItemPOJO(
							pk.getDataClusterPOJOPK(),
							pk.getConceptName(),
							pk.getIds(),
							System.currentTimeMillis(),
							selectedConceptXml
					);
					
					//check exist
					if(!isOverwrite){
						ItemPOJO check = Util.getItemCtrl2Local().existsItem(pk);
						if (check!=null) {
							org.apache.log4j.Logger.getLogger(this.getClass()).warn("execute() BatchProject:Item "+pk.getUniqueID()+" already exists and overwrite is set to false --> skipping");
							resultContent+=(selectedConceptXml+br);
							continue;
						}
					}
						
					//now perform updates
					ItemPOJOPK puttedItemPOJOPK=null;
					try {
						puttedItemPOJOPK=Util.getItemCtrl2Local().putItem(
								newItemPOJO,
								dataModelPOJO
						);
					} catch (Exception e) {
						String err = "Could not project item "+pk.getUniqueID()+": "+e.getLocalizedMessage();
					    //org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);//error message stack be printed by ItemCtrl2Bean
					    resultContent+=(selectedConceptXml+br);
					}
					
					org.apache.log4j.Logger.getLogger(this.getClass()).info("Item "+(i+1)+" have been inserted. ");
					if(puttedItemPOJOPK!=null)context.setProjectedPKToGlobalContext(puttedItemPOJOPK);
					
				}
				org.apache.log4j.Logger.getLogger(this.getClass()).info("Totally "+nlist.getLength()+" items, used "+(System.currentTimeMillis()-beginTime)+" ms! ");
			}
			
			
			resultContent+="</no-insert-items>";
		    context.put(OUTPUT_XML, new TypedContent(resultContent.getBytes(),"text/xml; charset=utf-8"));
		    
		    
			//call the callback content is ready
			context.getPluginCallBack().contentIsReady(context);
			

		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not execute the BatchProject plugin "+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
		}
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("execute() BatchProject done");
	}

	private String getDefaultConfiguration(){
    	return
    		"<configuration>"+
    		"		<dataClusterName>Order</dataClusterName>" +"\n"+
    		"		<dataModelName>Order</dataModelName>" +"\n"+
    		"		<conceptName>Customer</conceptName>" +"\n"+
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
        } catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the BatchProject Transformer Plugin"
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
	}

	


}