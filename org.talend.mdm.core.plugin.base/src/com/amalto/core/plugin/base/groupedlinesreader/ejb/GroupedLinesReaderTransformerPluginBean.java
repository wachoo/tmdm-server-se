package com.amalto.core.plugin.base.groupedlinesreader.ejb;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.ejb.SessionBean;

import org.w3c.dom.Element;

import com.amalto.core.objects.transformers.v2.ejb.TransformerPluginV2CtrlBean;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * <h1>Grouped Lines Reader Plugin</h1>
 * <h3>Plugin name: groupedlinesreader</h3>
 * <h3>Description</h3>
 * The Grouped Lines Reader reads a number of lines as long as a condition is met and pass the group to the next plugin.
 * The condition is met as long as a motif made of a sequence of characters is identical on each line.<br/>
 * The plugin accepts two types of files: CSV and FlatFile. When in CSV mode, the motif is the value of a column identified by
 * its number. In FlatFile mode, the motif is a fixed length sequence of character of the line read at a determined position.<br/>
 * <br/>
 * This plugin is a "looping" plugin. Once a group of lines is read, the group is sent for processing by the rest of the transformer,
 * then the next group is read and sent for processing, and so forth until the end of the text.<br/>
 * This plugin is usually used in front of the Fixed Length Parser Plugin or the CSV Parser Plugin
 * <h3>Inputs</h3>
 * <ul>
 * <li><b>text_content</b>: the text to split</li>
 * </ul>
 * <h3>Outputs</h3>
 * <ul>
 * <li><b>line</b>: one or more of the lines read</li>
 * </ul>
 * <h3>Parameters</h3>
 * The parameters are specified as an XML <pre>
    &lt;parameters&gt;
      &lt;type&gt;CSV|FlatFile&lt;/type&gt;
      &lt;position&gt;FIRST_CHARACTER_OR_CSV_COLUMN&lt;/position&gt;
      &lt;length&gt;NUMBER_OF_CHARACTERS&lt;/length&gt;
      &lt;separator&gt;SEPARATOR&lt;/separator&gt;
      &lt;ignoreFirstLines&gt;NUMBER_OF_LINES_TO_IGNORE&lt;/ignoreFirstLines&gt;
      &lt;ignoreBlankLine&gt;true|false&lt;/ignoreBlankLine&gt;
    &lt;/parameters&gt;
 * </pre>
 * <ul>
 * <li><b>type</b>: Either <code>CSV</code> or <code>FlatFile</code>. The type determines how the motif is "calculated". In <code>CSV</code> mode, the motif is the value of
 * a column. In <code>FlatFile</code> mode, the motif is a fixed length sequence of characters at a given position;</li>
 * <li><b>position</b>: the position of the motif. In <code>CSV</code> mode, the column number. In <code>FlatFile</code> mode, the first character.</li>
 * <li><b>length</b>: used in In <code>FlatFile</code> mode only, the number of characters that compose the motif starting with the <code>position</code> character.</li>
 * <li><b>ignoreFirstLines</b>: optional; defaults to zero. The number of lines at the beginning of the text that will be ignored by this plugin</li>
 * <li><b>ignoreBlankLine</b>: optional; defaults to <code>true</code>. If set to <code>true</code>, blank lines will be ignored by the processing</li>
 *</ul>
 * <h3>Example</h3>
 * The following example parameters will generate group of lines from a CSV where columns are separated by commas. The groups
 * will be made of lines where the 3rd column is identical to each other. The first line (the headers line) will be ignored.
 *
 * <pre>
    &lt;parameters&gt;
      &lt;type&gt;CSV&lt;/type&gt;" +"\n"+
      &lt;position&gt;3&lt;/position&gt;" +"\n"+
      &lt;separator&gt;,&lt;/separator&gt;" +"\n"+
      &lt;ignoreFirstLines&gt;1&lt;/ignoreFirstLines&gt;" +"\n"+
    &lt;/parameters&gt;
 * </pre>
 *
 * @author Bruno Grieder, David Le Niniven
 *
 * @ejb.bean name="GroupedLinesReaderTransformerPlugin"
 *           display-name="Name for GroupedLinesReaderTransformerPlugin"
 *           description="Description for GroupedLinesReaderTransformerPlugin"
 * 		  local-jndi-name = "amalto/local/transformer/plugin/groupedlinesreader"
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
public class GroupedLinesReaderTransformerPluginBean extends TransformerPluginV2CtrlBean  implements SessionBean{

	public static final String PARAMETERS ="com.amalto.core.plugin.groupedlinesreader.parameters";
	public static final String LINES_TO_IGNORE =  "com.amalto.core.plugin.groupedlinesreader.linesToIgnore" ;
	public static final String IGNORE_BLANK_LINE =  "com.amalto.core.plugin.groupedlinesreader.ignoreBlankLine" ;
	public static final String NUMLINES = "com.amalto.core.plugin.groupedlinesreader.numLines" ;
	public static final String STOP = "com.amalto.core.plugin.groupedlinesreader.stop";

	private static final String TYPE = "com.amalto.core.plugin.groupedlinesreader.type";
	private static final String POSITION = "com.amalto.core.plugin.groupedlinesreader.position";
	private static final String LENGTH = "com.amalto.core.plugin.groupedlinesreader.length";
	private static final String SEPARATOR = "com.amalto.core.plugin.groupedlinesreader.separator";


	private static final long serialVersionUID = 2148709892480L;
	private static final String INPUT_CONTENT = "text_content";
	private static final String OUTPUT_LINE = "line";

    private transient boolean configurationLoaded = false;



	public GroupedLinesReaderTransformerPluginBean() {
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
		return "amalto/local/transformer/plugin/groupedlinesreader";
	}



    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "Lit un texte, regroupe certaines lignes";
		return "Reads a text, grouping several lines";
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
		 TransformerPluginVariableDescriptor descriptor = new TransformerPluginVariableDescriptor();
		 descriptor.setVariableName(INPUT_CONTENT);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/.*")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The text content to read");
		 descriptor.setDescriptions(descriptions);
		 descriptor.setMandatory(true);
		 descriptor.setPossibleValuesRegex(null);
		 inputDescriptors.add(descriptor);
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
		 descriptor.setVariableName(OUTPUT_LINE);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/.*")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The grouped lines");
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
			//parse parameters
			Element params = Util.parse(compiledParameters).getDocumentElement();

			//attempt to read lines to ignore
			Integer linesToIgnore=new Integer(0);
			String temp = Util.getFirstTextNode(params, "ignoreFirstLines");
			if (temp!=null) linesToIgnore = new Integer(temp);
			Boolean ignoreBlankLine = Boolean.TRUE;
			temp = Util.getFirstTextNode(params, "ignoreBlankLine");
			if (temp!=null) ignoreBlankLine = Boolean.parseBoolean(temp);

			String type = Util.getFirstTextNode(params, "type");
			String position = Util.getFirstTextNode(params, "position");
			String length = Util.getFirstTextNode(params, "length");
			String separator = Util.getFirstTextNode(params, "separator");

			context.put( IGNORE_BLANK_LINE, ignoreBlankLine);
			context.put( PARAMETERS, params);
			context.put( LINES_TO_IGNORE, linesToIgnore);

			context.put(TYPE, type);
			context.put(POSITION, position);
			context.put(LENGTH, length);
			context.put(SEPARATOR, separator);

			org.apache.log4j.Logger.getLogger(this.getClass()).debug("init() DocumentExtarcor = ok");
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not init the GroupedLinesReader plugin:"+
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
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() ");

		try {

			//Element params= (Element)context.get( PARAMETERS);
			TypedContent content = (TypedContent)context.get(INPUT_CONTENT);

			//*** Fetch parameters
			String type = (String)context.get(TYPE);
			String position = (String) context.get(POSITION);
			String length = (String) context.get(LENGTH);
			String  separator = (String)context.get(SEPARATOR);

			org.apache.log4j.Logger.getLogger(this.getClass()).debug("Plugin mode = : "+type);

			//read the charset
			String charset  = Util.extractCharset(content.getContentType());

			//Read from the stream and insert the bytes into the TypedContent when processing
			//If no stream is available, attempt to read the bytes
			BufferedReader br = new BufferedReader(new InputStreamReader(content.getContentStream(),charset));

			int linesToIgnore = ((Integer)context.get( LINES_TO_IGNORE)).intValue();
			boolean ignoreBlankLine =( (Boolean)context.get(IGNORE_BLANK_LINE)).booleanValue();
			String line = null;
			int i=0;
			int lines = 0;
			context.put( NUMLINES, lines);
			String previousCondition = "";
			String currentcondition = "";
			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			boolean firstIteration = true;

			while (
					(context.get( STOP) == null)
					&& ((line = br.readLine())!=null))
			{
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("i = "+i);
				if (++i>linesToIgnore) {
					if (!(ignoreBlankLine && "".equals(line.trim()))) {

						context.put( NUMLINES, ++lines);
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() Processing line "+lines+" - content \n"+line);
						line = line + "\n";
						boolean conditionHasChanged = false;

						if (type.equals("CSV")){
							String[] fields = line.split(separator);
							currentcondition = fields[Integer.parseInt(position)];
						} else if (type.equals("FlatFile")){
							currentcondition = line.substring(Integer.parseInt(position),Integer.parseInt(position)+Integer.parseInt(length));
						}

						if (firstIteration) {
							previousCondition = currentcondition;
							firstIteration = false;
						}
						conditionHasChanged = !(previousCondition.equals(currentcondition));
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("current condition: "+currentcondition);


						//if content is ready then invoke the next plugin
						if ((conditionHasChanged)){
							org.apache.log4j.Logger.getLogger(this.getClass()).debug("Condition has changed - Try to call next plugin");
							//reinsert the bytes into the context - for use by subsequent plugins

							context.put(OUTPUT_LINE, new TypedContent(bytes.toByteArray(),"text/plain; charset=utf-8"));
							context.getPluginCallBack().contentIsReady(context);
							bytes.reset();
							bytes.write(line.getBytes("utf-8"));
						} else {
							//	update bytes
							bytes.write(line.getBytes("utf-8"));
						}

						previousCondition = currentcondition;
					}
				}
			}//while

			//extract last document
			if (bytes!=null){
				context.put(OUTPUT_LINE, new TypedContent(bytes.toByteArray(),"text/plain; charset=utf-8"));
				context.getPluginCallBack().contentIsReady(context);
			}

			//cleanup
			br.close();

		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Could not execute the Grouped Lines Reader transformer plugin "+
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
	public String getParametersSchema() throws XtentisException {
		return
		"<xsd:schema" +
		" 		elementFormDefault='unqualified'" +
		"		xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
		">" +
		"<xsd:element name='parameters'>" +
		"			<xsd:complexType >" +
		"				<xsd:sequence>" +
		"					<xsd:element minOccurs='0' maxOccurs='1' nillable='false' name='contentType' type='xsd:int'/>" +
		"					<xsd:element minOccurs='0' maxOccurs='1' nillable='false' name='charset' type='xsd:int'/>" +
		"					<xsd:element minOccurs='0' maxOccurs='1' nillable='false' name='ignoreFirstLines' type='xsd:int'/>" +
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
	public String getDocumentation(String twoLettersLanguageCode) throws XtentisException {
		return
		"The Document Extractor plugin reads a logical document which can be put in several lines. " +
		"Each extracted document is sent after each other for further processing by subsequent plugins\n" +
		"\n" +
		"\n" +
		"Parameters\n" +
		"	ignoreFirstLines [optional]: will ignore the number of lines specified at the beginning of the text"+"\n"+
		"	ignoreBlankLine [optional]: will ignore the blank lines: true or false (default is true)"+"\n"+
		"\n"+
		"\n"+
		"Example" +"\n"+
		"         <parameters>" +"\n"+
		"                 <ignoreFirstLines>0</ignoreFirstLines>" +"\n"+
		"                 <type>CSV</type>" +"\n"+
		"                 <position>3</position>" +"\n"+
		"                 <separator>,</separator>" +"\n"+
		"         </parameters>" +"\n"+
		"\n"+
		"\n"+
		"Notes for Plugin Developers: " +"\n"+
		"	Context:" +"\n"+
		"		com.amalto.core.plugin.Grouped Lines Reader.numlines: the number of lines processed"	;
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
        } catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the GroupedLinesReader Transformer Plugin"
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
		return parameters;
	}





}