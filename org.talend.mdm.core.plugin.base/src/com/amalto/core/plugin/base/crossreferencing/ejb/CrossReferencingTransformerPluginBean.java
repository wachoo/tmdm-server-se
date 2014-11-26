package com.amalto.core.plugin.base.crossreferencing.ejb;

import com.amalto.core.objects.Plugin;
import com.amalto.core.objects.datacluster.DataClusterPOJOPK;
import com.amalto.core.objects.transformers.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.util.TypedContent;
import com.amalto.core.plugin.base.crossreferencing.CompiledParameters;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.core.server.api.Item;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.rmi.RemoteException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>Cross Referencing Plugin</h1>
 * <h3>Plugin name: crossreferencing</h3>
 * <h3>Description</h3>
 * The cross-referencing plugin takes an input XML and will cross-reference (e.g. replace the content of)
 * a designated list of elements or attributes using the content extracted from Items stored in the database.<br/>
 * <br/>
 * The plugin offers the possibility to<ul>
 * <li>perform multiple cross-references on the source XML document in one pass</li>
 * <li>either ignore a failed cross-reference or throw an error and stop execution of the transformer</li>
 * <li>specify a default value when the cross-referencing fails and errors are ignored</li>
 * </ul>
 * Note: this plugins provides enhanced functionality over the built-in cross-referencing provided by the XSLT plugin<br/>
 * <h3>Inputs</h3>
 * <ul>
 * <li><b>xml</b>: the source xml on which the cross-referencing will be performed</li>
 * </ul>
 * <h3>Outputs</h3>
 * <ul>
 * <li><b>text</b>: the result of the processing</li>
 * </ul>
 * <h3>Parameters</h3>
 * The parameters are specified as an XML in the form of a list of <code>CrossRef</code> elements<pre>
    &lt;parameters&gt;
    	&lt;CrossRef&gt;
    		&lt;xrefName&gt;A_UNIQUE_NAME&lt;/xrefName&gt;
    		&lt;xrefCluster&gt;ITEMS_CLUSTER&lt;/xrefCluster&gt;
    		&lt;xrefRootElement&gt;ROOT_XPATH&lt;/xrefRootElement&gt;
    		&lt;xrefIn&gt;
    			&lt;mapping&gt;
    				&lt;xrefElement&gt;XML_SOURCE_XPATH&lt;/xrefElement&gt;
    				&lt;xrefPath&gt;ITEM_KEY_XPATH&lt;/xrefPath&gt;
    			&lt;/mapping&gt;
    		&lt;/xrefIn&gt;
    		&lt;xrefOut&gt;
    			&lt;mapping&gt;
    				&lt;xrefElement&gt;XML_TARGET_XPATH&lt;/xrefElement&gt;
    				&lt;xrefPath&gt;ITEM_VALUE_XPATH&lt;/xrefPath&gt;
    			&lt;/mapping&gt;
    		&lt;/xrefOut&gt;
    		&lt;xrefIgnore&gt;FALSE&lt;/xrefIgnore&gt;
    		&lt;xrefDefault&gt;EMPTY&lt;/xrefDefault&gt;
    	&lt;/CrossRef&gt;
    	&lt;CrossRef&gt;
    	...
    	&lt;/CrossRef&gt;
    	...
    &lt;/parameters&gt;
 * </pre>
 * <ul>
 * <li><b>xrefName</b>: an unique name for this CrossRef</li>
 * <li><b>xrefCluster</b>: the name of the cluster where matching Items will be searched</li>
 * <li><b>xrefRootElement</b>: the XPath to an element in the source XML document to which all other XPaths will be relative.
 * This is useful to process a list of sub-elements in the source documents: assuming a PuchaseOrder with multiple <code>LineItem</code>s,
 * specifying <code>//LineItem</code> will process the cross-referencing on all <code>LineItem</code>s.</li>
 * <li><b>xrefIn/mapping/xrefElement</b>: an XPath relative to <code>xrefRootElement</code> in the source XML document.
 * The content of this element or attribute will be used to search a matching Item</li>
 * <li><b>xrefIn/mapping/xrefPath</b>: an XPath to an Item element or attribute, starting with the Item concept name. The first item
 * where this XPath content matches the content of <code>xrefIn/mapping/xrefElement</code> will be selected.</li>
 * <li><b>xrefOut/mapping/xrefElement</b>: an XPath relative to <code>xrefRootElement</code> in the source XML document.
 * The content of this element or attribute will be replaced with the content extracted from <code>xrefOut/mapping/xrefPath</code></li>
 * <li><b>xrefOut/mapping/xrefPath</b>: an XPath to an Item element or attribute, starting with the same Item concept name as the <code>xrefIn/mapping/xrefPath</code>.
 * The content of this XPath will be used to replace the content of the <code>xrefOut/mapping/xrefElement</code> XPath.</li>
 * <li><b>xreIgnore</b>: optional; defaults to <code>false</code>. If set to <code>true</code> a failed cross-referencing will not stop the
 * transformer process and the <code>xrefDefault</code> value will be mapped to the <code>xrefOut/mapping/xrefElement</code> element or attribute</li>
 * <li><b>xrefDefault</b>: optional; default to '' (empty); the target value used when a cross-referencing fails and
 * <code>xrefIgnore</code> is set to <code>true</code>. See <code>xrefIgnore</code></li>
 * </ul>
 *
 * <h3>Example</h3>
 * The following example parameters will cross-reference (e.g. replace the content of) the <code>UOM</code> attribute of sub-elements <code>Quantity/Value</code>
 * in all elements <code>PurchaseOrderLineItem</code> of the source XML by <ul>
 * <li>1. Getting the value of the <code>OriginalUOM</code> attribute of the Quantity/Value element</li>
 * <li>2. Using this value to find a <code>UnitOfMeasure</code> Item in the cluster CROSSREFERENCING where the text content of sub-element <code>cXMLUnit</code> equals the value</li>
 * <li>3. Extracting the text content of the <code>XBITSunit</code> sub-element of the <code>UnitOfMeasure</code> Item
 * </ul>
 * The transformer will stop if the cross-referencing cannot be performed.
 * <pre>
    &lt;parameters&gt;
    	&lt;CrossRef&gt;
    		&lt;xrefName&gt;myCrossRefName&lt;/xrefName&gt;
    		&lt;xrefCluster&gt;CROSSREFERENCING&lt;/xrefCluster&gt;
    		&lt;xrefRootElement&gt;//PurchaseOrderLineItem&lt;/xrefRootElement&gt;
    		&lt;xrefIn&gt;
    			&lt;mapping&gt;
    				&lt;xrefElement&gt;Quantity/Value/@OriginalUOM&lt;/xrefElement&gt;
    				&lt;xrefPath&gt;UnitOfMeasure/cXMLUnit&lt;/xrefPath&gt;
    			&lt;/mapping&gt;
    		&lt;/xrefIn&gt;
    		&lt;xrefOut&gt;
    			&lt;mapping&gt;
    				&lt;xrefElement&gt;Quantity/Value/@UOM&lt;/xrefElement&gt;
    				&lt;xrefPath&gt;UnitOfMeasure/XBITSUnit&lt;/xrefPath&gt;
    			&lt;/mapping&gt;
    		&lt;/xrefOut&gt;
    	&lt;/CrossRef&gt;
    &lt;/parameters&gt;
 * </pre>
 *
 * <br/>
 * <br/>
 * @author Bruno Grieder, David Le Niniven
 *
 * @ejb.bean name="CrossReferencingTransformerPluginBean"
 *           display-name="Name for CrossReferencingTransformerPluginBean"
 *           description="Description for CrossReferencingTransformerPluginBean"
 * 		  local-jndi-name = "amalto/local/transformer/plugin/crossreferencing"
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
 *
 */
public class CrossReferencingTransformerPluginBean extends Plugin {
	private static final long serialVersionUID = 62487085713480L;

    private transient boolean configurationLoaded = false;

    private static final String PARAMETERS = "com.amalto.core.plugin.crossreferencing.parameters";


	private static final String INPUT_XML = "xml";
	private static final String OUTPUT_TEXT = "text";


    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/transformer/plugin/crossreferencing";
	}



    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "Transforme un XML en utilisant des règles de transcodifications";
		return "Transform an XML using crossreferencing rules";
	}



	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public ArrayList<TransformerPluginVariableDescriptor> getInputVariableDescriptors(String twoLettersLanguageCode) throws XtentisException {
		 ArrayList<TransformerPluginVariableDescriptor> inputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();

		 //The document descriptor
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
		 descriptions1.put("en", "The item instance to transform");
		 descriptions1.put("fr", "Le document à transformer");
		 descriptor1.setDescriptions(descriptions1);
		 descriptor1.setMandatory(true);
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
								 Pattern.compile("text/.*")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The result of the xml transformation");
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
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("init() ");

			if (!configurationLoaded) loadConfiguration();
			//fetech the parameters
			CompiledParameters parameters = CompiledParameters.deserialize(compiledParameters);


			//Insert all this in the context
			context.put( PARAMETERS, parameters);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not init the CrossReferencing Plugin: "+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error("init() "+err);
			throw new XtentisException(err);
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
	public void execute(TransformerPluginContext context) throws XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() ");

		try {
			//fetch data and parameters from context
			CompiledParameters parameters= (CompiledParameters)context.get( PARAMETERS);
			TypedContent xmlTC = (TypedContent)context.get(INPUT_XML);


			//get the charset
			String charset = Util.extractCharset(xmlTC.getContentType());

			//get the xml String
			String xml = new String(xmlTC.getContentBytes(),charset);

			//TODO: make xref work for non XML
			String result="";


			try {
				Document xmlDoc = Util.parse(xml);
				Document xmlParameters = Util.parse(parameters.getParameters());

				NodeList rules = Util.getNodeList(xmlParameters.getDocumentElement(), "//CrossRef");

				for (int i =0; i< rules.getLength(); i++) {
	    			processMappings(xmlDoc, rules.item(i));
				}

				result = Util.nodeToString(xmlDoc,false);
			} catch (Exception e) {
				throw new XtentisException("CrossReferencing plugin: "+e.getMessage());
			}


        	//calback
			if (org.apache.log4j.Logger.getLogger(this.getClass()).isDebugEnabled())
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() OUTPUT AFTER CROSSREF\n"+result);


			//Call Back
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() Inserting CrossReferencing Result in "+OUTPUT_TEXT);
			byte[] bytes = result.getBytes("utf-8");
			context.put(
					OUTPUT_TEXT,
					new TypedContent(
							bytes,
							"text/xml; charset=utf-8"
					)
			);

			context.getPluginCallBack().contentIsReady(context);

		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not start the CrossReferencing plugin: "+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error("execute() "+err);
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
     * Process the mappings after xsl transformation
     */
    private void processMappings(Document xmlDoc, Node crossRef) throws XtentisException{
    	String xrefName = "";
    	try {
    		xrefName = Util.getFirstTextNode(crossRef,"./xrefName");

    		String xrefcluster = Util.getFirstTextNode(crossRef,"./xrefCluster");

			String xrefRootElement = Util.getFirstTextNode(crossRef,"./xrefRootElement");

			NodeList xrefInMappings =  Util.getNodeList(crossRef,"./xrefIn/mapping");

			Vector<String> xrefElements = new Vector<String>();
			Vector<String> xrefPaths = new Vector<String>();

			for (int i =0; i < xrefInMappings.getLength(); i++) {
				Node mappingNode = xrefInMappings.item(i);

				String tmpXrefElement = Util.getFirstTextNode(mappingNode, "xrefElement");
				String tmpXrefPath = Util.getFirstTextNode(mappingNode, "xrefPath");
				xrefElements.add(tmpXrefElement);
				xrefPaths.add(tmpXrefPath);
			}

			String xrefOutElement =  Util.getFirstTextNode(crossRef,"./xrefOut/mapping/xrefElement");

			String xrefOutPath =  Util.getFirstTextNode(crossRef,"./xrefOut/mapping/xrefPath");

			String xrefIgnoreS = Util.getFirstTextNode(crossRef,"./xrefIgnore");

			boolean xrefIgnore = false;
			if (xrefIgnoreS!=null && (xrefIgnoreS.equals("true") || xrefIgnoreS.equals("1")))
				xrefIgnore = true;

			String xrefDefault = Util.getFirstTextNode(crossRef,"./xrefDefault");


			NodeList rootNodes = null;
			if (xrefRootElement == null || "".equals(xrefRootElement))
				xrefRootElement = ".";

			rootNodes = Util.getNodeList(xmlDoc, xrefRootElement);

			for (int j=0; j < rootNodes.getLength(); j++) {


				HashMap<String,String> itemvals = new HashMap<String,String>();

				for (int k =0; k < xrefElements.size(); k++) {
					String xrefPath = xrefPaths.get(k); //Path dans la table de transco
					String xrefElement = xrefElements.get(k);//Path dans le doc
					String itemval = "";

					if (xrefElement.startsWith("["))  //hardcoded value
						itemval = xrefElement.substring(1, xrefElement.length()-1);
					else {
						//Check that the element to xref is unique in the source document
						NodeList xrefInNodes = Util.getNodeList(rootNodes.item(j), xrefElement);
						if (xrefInNodes.getLength()  > 1)
							throw new XtentisException("Ambiguous mapping with path "+xrefElement+" - returns "+xrefInNodes.getLength()+" elements.");

						if (xrefInNodes.getLength()  == 0) {
							org.apache.log4j.Logger.getLogger(this.getClass()).debug("processMappings() xrefInNodes.getLength() = "+xrefInNodes.getLength());
							break;
						}

						//Check that the XPath points to an Attribute or text node only, not a branch
						Node resNode = xrefInNodes.item(0);
						if (resNode.getNodeType() == Node.ATTRIBUTE_NODE || resNode.getNodeType() == Node.TEXT_NODE)
							itemval = xrefInNodes.item(0).getNodeValue();
						else if (resNode.getNodeType() == Node.ELEMENT_NODE) {
							Node child = resNode.getFirstChild();
							if (child!=null && child.getNodeType() == Node.TEXT_NODE)
								itemval = child.getNodeValue();
							else if (child!=null)
								throw new XtentisException("Invalid xrefIn path "+xrefElement+" - xref path should correspond to attributes or text nodes only.");
							else
								itemval = "";
						} else
							throw new XtentisException("Invalid xrefIn path "+xrefElement+" - xref path should correspond to attributes or text nodes only.");
					}
					
					itemvals.put(xrefPath,Util.stripeOuterBracket(itemval).split(",")[0]);
				}

				if (itemvals.size() == 0)
					break;

				//perform cross-ref
				String valeur = getCrossReferencedValue(itemvals, xrefcluster, xrefOutPath, xrefIgnore, xrefDefault);

				//check that the element to put the result into is unique
				NodeList xrefOutNodes = Util.getNodeList(rootNodes.item(j), xrefOutElement);
				if (xrefOutNodes.getLength()  > 1)
					throw new XtentisException("Ambigus mapping with path "+xrefOutPath+" - returns "+xrefOutNodes.getLength()+" elements.");

				if (xrefOutNodes.getLength()  == 0) {
					org.apache.log4j.Logger.getLogger(this.getClass()).debug("processMappings() xrefOutNodes.getLength() = "+xrefOutNodes.getLength());
					break;
				}

				Node xrefOutNode = xrefOutNodes.item(0);

				//check that the lement to put the value into is an attribute or a text node
				if (xrefOutNode.getNodeType() == Node.ATTRIBUTE_NODE || xrefOutNode.getNodeType() == Node.TEXT_NODE)
					xrefOutNode.setNodeValue(valeur);
				else if (xrefOutNode.getNodeType() == Node.ELEMENT_NODE) {
					Node child = xrefOutNode.getFirstChild();
					if (child!=null && child.getNodeType() == Node.TEXT_NODE)
						child.setNodeValue(valeur);
					else if (child!=null)
						throw new XtentisException("Invalid xrefOut path "+xrefOutElement+" - xref path should correspond to attributes or text nodes only.");
					else {
						xrefOutNode.appendChild(xmlDoc.createTextNode(valeur));
					}
				} else
					throw new XtentisException("Invalid xrefOut path "+xrefOutElement+" - xref path should correspond to attributes or text nodes only.");

			}


	    } catch (XtentisException e) {
	    	e.printStackTrace();
	    	throw(e);
    	} catch (Exception e) {
    		e.printStackTrace();
            //clean-up the wrapper
    	    String err = "Unable to process the mappings for the element \""+xrefName+"\""
    	    		+((e.getClass().equals(XtentisException.class))? "" : ": "+e.getClass().getName())
    	    		+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }

    private String getCrossReferencedValue(HashMap<String,String> itemvals, String xrefcluster, String xrefOutPath, boolean xrefIgnore, String xrefDefault)  throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("getCrossReferencedValue() xrefcluster = "+ xrefcluster+" xrefOutPath = "+xrefOutPath+" xrefIgnore = "+xrefIgnore+" xrefDefault = "+xrefDefault);

    	WhereAnd wAnd = new WhereAnd();

        Collection<Map.Entry<String,String>> c = itemvals.entrySet();
        int h=0;
		for (Iterator<Map.Entry<String,String>> iter = c.iterator(); iter.hasNext(); ) {
			h++;
			Map.Entry<String,String> entry = iter.next();
			wAnd.add(new WhereCondition(
					entry.getKey(),
					WhereCondition.EQUALS,
					entry.getValue(),
					WhereCondition.PRE_NONE,
					false
			));
		}

		//Retrieve the Item Controller
		Item itemCtrl = Util.getItemCtrl2Local();
		ArrayList<String> resList = null;
        try {
            resList = itemCtrl.xPathsSearch(
                    new DataClusterPOJOPK(xrefcluster),
                    null,
                    new ArrayList<String>(Arrays.asList(new String[]{xrefOutPath})),
                    wAnd,
                    -1,    //spell
                    0,        //start
                    1,        //limit
                    false
            );
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        String val = "";

        if ((resList==null)||(resList.size()==0)) {
        	if (xrefIgnore) {
        		if (xrefDefault!=null)
        			val = xrefDefault;
        		else
        			val = "";
        	} else {
	        	String ks = "";
		        c = itemvals.entrySet();
				for (Iterator<Map.Entry<String,String>> iter = c.iterator(); iter.hasNext(); ) {
					Map.Entry<String,String> entry = iter.next();
					ks+=" "+entry.getKey()+"=\""+entry.getValue()+"\"";
				}
	        	throw new XtentisException("Foreign keys values not found for: "+ks);
        	}
        } else {
        	//read result
           	Pattern p = Pattern.compile("<.*>(.*?)</.*>",Pattern.DOTALL);
			Matcher m = p.matcher(resList.iterator().next());
			if (m.matches())
				val = m.group(1);
			else
				throw new XtentisException("Result values were not understood for crossref element");
        }

        org.apache.log4j.Logger.getLogger(this.getClass()).debug("getCrossReferencedValue() xrefcluster = "+ xrefcluster+" xrefOutPath = "+xrefOutPath+" xrefIgnore = "+xrefIgnore+" xrefDefault = "+xrefDefault+" returns val = "+val);


        return val;
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
		"The CrossReferencing plugin transform xml content using cross referencing tables. " +
		"\n" +
		"\n" +
		"Parameters\n" +
		"	An xml file describing all cross referencing parameters"+"\n"+
		"\n"+
		"\n"+
		"Example" +"\n"+
		"	<parameters>" +"\n"+
		"		<CrossRef>" +"\n"+
		"			<xrefName>myCrossRefName</xrefName>" +"\n"+
		"			<xrefCluster>b2box CROSSREFERENCING</xrefCluster>" +"\n" +
		"			<xrefRootElement>//PurchaseOrderLineItem</xrefRootElement>" +"\n" +
		"			<xrefIn>\n" +
		"				<mapping>\n" +
		"					<xrefElement>Quantity/Value/@UOM</xrefElement>\n" +
		"					<xrefPath>UnitOfMeasure/cXMLUnit</xrefPath>\n" +
		"				</mapping>\n" +
		"			</xrefIn>" +"\n" +
		"			<xrefOut>\n" +
		"				<mapping>\n" +
		"					<xrefElement>Quantity/Value/@UOM</xrefElement>\n" +
		"					<xrefPath>UnitOfMeasure/XBITSUnit</xrefPath>\n" +
		"				</mapping>\n" +
		"			</xrefOut>" +"\n" +
		"		</CrossRef>\n"+
		"	</parameters>"+"\n"+
		"\n";
	}


	private String getDefaultConfiguration(){
    	return
    		"<configuration>"+
    		"	<charset>utf-8</charset>"+
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
    	    String err = "Unable to deserialize the configuration of the CrossReferencing Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
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


	/********************************************************************************************
	 * Compilation - decompilation of parameters
	 ********************************************************************************************/

    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String compileParameters(String parameters) throws XtentisException {
    	try {

    		CompiledParameters compiled = new CompiledParameters();


			setCompilationErrors("");

			compiled.setParameters(parameters);

    		return compiled.serialize();

//    	} catch (XtentisException e) {
//    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the parameters of the CrossReferencing Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
	}


	private String compilationErrors = "";
	public String getCompilationErrors() {
		return compilationErrors;
	}
	public void setCompilationErrors(String compilationError) {
		this.compilationErrors = compilationError;
	}
	protected synchronized void add2CompilationErrors(String err) {
		compilationErrors+="\n"+err;
	}

}
