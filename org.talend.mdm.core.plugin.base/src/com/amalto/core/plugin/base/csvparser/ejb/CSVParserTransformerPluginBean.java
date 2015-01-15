package com.amalto.core.plugin.base.csvparser.ejb;

import com.amalto.core.objects.Plugin;
import com.amalto.core.objects.transformers.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.util.TypedContent;
import com.amalto.core.objects.transformers.util.TypedContent_Do_Not_Process;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <h1>CSV Parser Plugin</h1>
 * <h3>Plugin name: csvparser</h3>
 * <h3>Description</h3>
 * The CSV Parser plugin takes one or more CSV lines as input and will XMLize it/them.<br/>
 * This plugin is typically called right after the Lines Reader plugin which reads a text line by line.<br/>
 * <br/>
 * The plugin offers the possibility to<ul>
 * <li>extract column content based on the column name or the column number</li>
 * <li>create xml list structures by grouping columns using the <code>LOOP</code> instruction in the template</li>
 * </ul>
 * <h3>Inputs</h3>
 * <ul>
 * <li><b>csv_line</b>: the input line(s) to XMLize</li>
 * </ul>
 * <h3>Outputs</h3>
 * <ul>
 * <li><b>xml</b>: the XMLized line(s) (see <code>template</code> and the <code>mulitplelines</code> parameters)</li>
 * </ul>
 * <h3>Parameters</h3>
 * The parameters are specified as an XML which includes the template of the xml to be generated <pre>
    &lt;parameters&gt;
    	&lt;separator&gt;COLUMN_SEPARATOR&lt;/separator&gt;
    	&lt;template&gt;
    	&lt;![CDATA[
    		&lt;MyXml&gt;
    			&lt;MyElement1&gt;COLUMN_SPECIFICATION&lt;/MyElement1&gt;
    			...
    			[LOOP COLUMN_NAME_PATTERN]
    				&lt;MyLoopElement1&gt;COLUMN_SPECIFICATION&lt;/MyLoopElement1&gt;
    				...
    				&lt;MyLoopElementN&gt;COLUMN_SPECIFICATION&lt;/MyLoopElementN&gt;
    			[/LOOP]
    			...
    			&lt;MyElementN&gt;COLUMN_SPECIFICATION&lt;/MyElementN&gt;
        	&lt;/MyXml&gt;
    	]]&gt;
    	&lt;/template&gt;
    	&lt;headersOnFirstLine&gt;true|false&lt;/headersOnFirstLine&gt;
    	&lt;headers&gt;COMMA_SEPARATED_LIST&lt;/headers&gt;
    	&lt;failOnMissingHeaders&gt;true|false&lt;/failOnMissingHeaders&gt;
    	&lt;multiplelines&gt;true|false&lt;/multiplelines&gt;
    	&lt;lineseparator&gt;LINE_SEPARATOR&lt;/lineseparator&gt;
    &lt;/parameters&gt;
 * </pre>
 * <ul>
 * <li><b>separator</b>: the sequence of characters used as a separator between columns in the CSV, typically a comma or semi-colon</li>
 * <li><b>template</b>: the template xml that will be generated. The template is usually inserted in a CDATA section to avoid escaping the xml reserved characters.
 * Where inserted, the COLUMN_SPECIFICATIONS will be replaced by the corresponding column value in the CSV. The COLUMN_SPECIFICATION is a character sequence
 * between accolades {} and can be<ul>
 * 	<li>either a column number: for column n, use <code>{n}</code></li>
 * 	<li>or a column name pattern: for pattern <code>LineNum.*</code>, use <code>{LineNum.*}</code></li>
 * </ul></li>
 * When using a column name pattern, headers must have been specified either by setting <code>headersOnFirstLine</code> to <code>true</code> or
 * specifying headers using the <code>headers</code> parameters.<br/>
 * To insert an accolade in the XML, escape it using a backslash e.g. <code>\{</code><br/>
 * <br/>
 * The LOOP instruction will perform logical grouping of columns and repeat the sub-template inside the instruction on these groups. A group of columns
 * is constituted by all the columns between a first column matching COLUMN_NAME_PATTERN inclusive and the next column matching COLUMN_NAME_PATTERN exclusive or
 * the end of the columns as appropriate.</li>
 * <li><b>headersOnFirstLine</b>: optional; defaults to <code>false</code>. When set to <code>true</code>, the CSV Parser will expect that the
 * first line pushed to it by the transformer contains the list of header names. These names are cached by the CSV parser and can be used
 * in the COLUMN_SPECIFICATIONS of the <code>template</code></li>
 * <li><b>headers</b>: optional; default to empty. When <code>headersOnFirstLine</code> is set to <code>false</code> but you want to use column names in the template,
 * specify the list of headers using this parameter</li>
 * <li><b>failOnMissingHeaders</b>: optional; defaults to <code>true</code>. If set to <code>false</code> the transformer will not stop when a column referenced by name or
 * number is not found</li>
 * <li><b>multiplelines</b>: optional; defaults to <code>false</code>. If set to <code>true</code> the plugin will loop over the lines and generate the template XML for each line.
 * It will then return a single xml with the list of generated xmls as sub-elements of a root <code>multiple</code> element: <code>&lt;multiple&gt;xml1...xmlN&lt;/multiple&gt;</code></li>
 * <li><b>lineseparator</b>: when <code>multiplelines</code> is set to <code>true</code>, the character sequence use as a separator between the lines. For a carriage return, use <code>\n</code></li>
 *</ul>
 * <h3>Example</h3>
 * The following example parameters will generate an XML starting with root element <code>MyXml</code> from a single line of CSV
 * </ul>
 * The transformer will stop if a column cannot be found.
 * <pre>
  &lt;parameters&gt;
    &lt;separator&gt;,&lt;/separator&gt;                              &lt;!-- the fields are separated by a comma --&gt;
    &lt;headersOnFirstLine&gt;true&lt;/headersOnFirstLine&gt;         &lt;!-- the header names are on the first line of the CSV --&gt;
    &lt;template&gt;
      &lt;![CDATA[
        &lt;MyXml&gt;
          &lt;Field1&gt;{1}&lt;/Field1&gt;                            &lt;!-- first column --&gt;
          &lt;Combo&gt;{2}-{4}&lt;/Combo&gt;                          &lt;!-- values of fields 2 and 4 separated by a dash --&gt;
          &lt;NotInterpreted&gt;\{3}&lt;/NotInterpreted&gt;           &lt;!-- the value will remain {3} --&gt;
          &lt;HeaderReference&gt;{/Header1}&lt;/HeaderReference&gt;   &lt;!-- the value in column named 'Header1' --&gt;
          [LOOP lineNumber.*]                             &lt;!-- starts a loop triggered by headers matching the regular expression lineNumber.* --&gt;
            &lt;Line&gt;                                        &lt;!-- inside the loop - multiple Line elements may be generated --&gt;
              &lt;Quantity&gt;{/Quantity.*}&lt;/Quantity&gt;          &lt;!-- the value in first column matching 'Quantity.*' after the one matching 'lineNumber.*'--&gt;
            &lt;/Line&gt;
          [/LOOP]                                         &lt;!-- End Of Loop --&gt;
        &lt;/MyXml&gt;
      ]]&gt;
    &lt;/template&gt;
  &lt;/parameters&gt;
 * </pre>
 *
 * @author Bruno Grieder
 *
 * @ejb.bean name="CSVParserTransformerPlugin"
 *           display-name="Name for CSVParserPlugin"
 *           description="Description for CSVParserPlugin"
 * 		  local-jndi-name = "amalto/local/transformer/plugin/csvparser"
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
@Service("amalto/local/transformer/plugin/csvparser")
public class CSVParserTransformerPluginBean extends Plugin {

	public static final String SEPARATOR ="com.amalto.core.plugin.csvparser.separator";
	public static final String MULTIPLELINES ="com.amalto.core.plugin.csvparser.multiplelines";
	public static final String LINESEPARATOR ="com.amalto.core.plugin.csvparser.lineseparator";
	public static final String TEMPLATE ="com.amalto.core.plugin.csvparser.template";
	public static final String COMPILED_TEMPLATE ="com.amalto.core.plugin.csvparser.compiled.template";
	public static final String LINE_NUMBER ="com.amalto.core.plugin.csvparser.line.number";
	public static final String HEADERS ="com.amalto.core.plugin.csvparser.line.headers";
	public static final String HEADERS_ON_FIRST_LINE ="com.amalto.core.plugin.csvparser.line.headers.on.first.line";
	public static final String FAIL_ON_MISSING_HEADERS ="com.amalto.core.plugin.csvparser.fail.on.missing.headers";

	private static final long serialVersionUID = 114870935892480L;
	private static final String INPUT_CSV_LINE ="csv_line";
	private static final String OUTPUT_XML ="xml";

	private static Pattern templatePattern = Pattern.compile(".*?<template>\\s*(<!\\[CDATA\\[)?(.*?)(\\]\\]>)?\\s*</template>.*",Pattern.DOTALL);

    private transient boolean configurationLoaded = false;

	public CSVParserTransformerPluginBean() {
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
		return "amalto/local/transformer/plugin/csvparser";
	}


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getDescription()
	 */
    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "Découpe une ligne de texte avec des champs séparés par un séparateur";
		return "Parses a text line with fields separated with a separator";
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
		 descriptor.setVariableName(INPUT_CSV_LINE);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/plain")
						})
				)
		);
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The csv line to parse");
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
		 descriptor.setVariableName(OUTPUT_XML);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/xml")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The xmlized csv line");
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

			//extract parameters
			Element params = Util.parse(compiledParameters).getDocumentElement();
			String separator = Util.getFirstTextNode(params, "separator");
			if (separator == null) {
				String err = "Invalid parameters for the CSV parser line "+context.getPluginNumber()+": the separator cannot be found";
				org.apache.log4j.Logger.getLogger(this.getClass()).error("init() "+err);
				throw new XtentisException(err);
			}

			String lineseparator = Util.getFirstTextNode(params, "lineseparator");
			if (lineseparator == null) {
				String err = "Invalid parameters for the CSV parser line "+context.getPluginNumber()+": the lineseparator cannot be found";
				org.apache.log4j.Logger.getLogger(this.getClass()).error("init() "+err);
				throw new XtentisException(err);
			}

			Boolean multiplelines = new Boolean(false);
			String ml = Util.getFirstTextNode(params, "multiplelines");
			if ((ml != null) && (("true,yes,1".indexOf(ml.toLowerCase())!=-1)))
				multiplelines = new Boolean(true);


			//template extraction is done using a pattern
			String template;
			Matcher m = templatePattern.matcher(compiledParameters);
			if (m.matches()) {
				if (m.groupCount()==3) {
					//There is a <![CDATA[
					template = m.group(2);
				} else {
					template = m.group(1);
				}
			} else {
				String err = "Invalid parameters for the CSV parser line "+context.getPluginNumber()+": the template cannot be found";
				org.apache.log4j.Logger.getLogger(this.getClass()).error("init() "+err);
				throw new XtentisException(err);
			}
			ArrayList<String> headers = new ArrayList<String>();
			String hl = Util.getFirstTextNode(params, "headers");	//a comma separated list of headers
			if (hl!=null) {
				headers = new ArrayList<String>(Arrays.asList(hl.split(",")));
			}
			Boolean headersOnFirstLine = new Boolean(false);
			String hofl = Util.getFirstTextNode(params, "headersOnFirstLine");	//the first line of the CSV contains the header
			if (hofl!=null) {
				if (("true,yes,1".indexOf(hofl.toLowerCase())!=-1))
				headersOnFirstLine = new Boolean(true);
			}
			Boolean failOnMissingHeaders = new Boolean(true);
			String fonmh = Util.getFirstTextNode(params, "failOnMissingHeaders");	//the first line of the CSV contains the header
			if (fonmh!=null) {
				if (("false,no,0".indexOf(hofl.toLowerCase())!=-1))
				headersOnFirstLine = new Boolean(false);
			}

			//Insert all the rest  in the context
			context.put(LINE_NUMBER, new Integer(0));
			context.put(SEPARATOR, separator);
			context.put(MULTIPLELINES, multiplelines);
			context.put(LINESEPARATOR, lineseparator);
			context.put(TEMPLATE, template);
			context.put(HEADERS, headers);
			context.put(HEADERS_ON_FIRST_LINE, headersOnFirstLine);
			context.put(FAIL_ON_MISSING_HEADERS, failOnMissingHeaders);
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not init the CSV Parser plugin:"+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
		}

	}

    @Override
    protected String loadConfiguration() {
        return null;
    }


	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#start()
	 */
    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void execute(TransformerPluginContext context) throws XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() ");

		try {
			//fetch parameters
			String  separator = (String)context.get(SEPARATOR);
			boolean multiplelines = ((Boolean)context.get(MULTIPLELINES)).booleanValue();
			String lineseparator = (String)context.get(LINESEPARATOR);
			String  template = (String)context.get(TEMPLATE);
			int lineNumber = ((Integer)context.get(LINE_NUMBER)).intValue();
			ArrayList<String> headers = (ArrayList<String>)context.get(HEADERS);
			boolean headersOnFirstLine = ((Boolean)context.get(HEADERS_ON_FIRST_LINE)).booleanValue();
			boolean failOnMissingHeaders = ((Boolean)context.get(FAIL_ON_MISSING_HEADERS)).booleanValue();
			//fetch the content
			TypedContent content = (TypedContent)context.get(INPUT_CSV_LINE);
			String charset = Util.extractCharset(content.getContentType());

			//update the item count
			context.put(LINE_NUMBER, new Integer(++lineNumber));

			//read the recrod fully
			String record = new String(content.getContentBytes(), charset);

			//separate lines
			String[] lines = record.split(lineseparator.toString());

			//get the fields
			String[] fields = record.split(separator, -1);

			//If this is the first line and we must grab the headers, just do that
			if (lineNumber == 1) {
				if (headersOnFirstLine) {
					headers = new ArrayList<String>(Arrays.asList(fields));
					context.put(HEADERS, headers);
				}
				//now that we have headers, compile the template
				context.put(COMPILED_TEMPLATE, compileTemplate(template, headers,failOnMissingHeaders));
				if (headersOnFirstLine) {
					//insert a stopped content to avoid further processing of the line
					org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute(): calling callback stop ");
					context.put(OUTPUT_XML, new TypedContent_Do_Not_Process());
					context.getPluginCallBack().contentIsReady(context);
					return;
				}

			}

			String text = "";
			if (multiplelines) {
				text = "<multiple>";


				org.apache.log4j.Logger.getLogger(this.getClass()).debug("lines.length = "+lines.length);

				for (int i=0; i < lines.length; i++) {
					String line = lines[i];

					String [] tmpFields = line.split(separator, -1);

					text  += runTemplate(
							(ArrayList<Step>)context.get(COMPILED_TEMPLATE),
							tmpFields
						);
				}
				text += "</multiple>";

			} else {
			//this is a "NORMAL" data line - run the compiled template
				text  = runTemplate(
					(ArrayList<Step>)context.get(COMPILED_TEMPLATE),
					fields
				);
			}

			//notify of ready content
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute(): calling callback content is ready ");
			byte[] bytes = text.getBytes("utf-8");
			context.put(OUTPUT_XML, new TypedContent(bytes,"text/xml; charset=utf-8"));
			context.getPluginCallBack().contentIsReady(context);

			//end -execute
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute(): calling callback done ");

		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not execute the CSV Parser transformer plugin at position "+context.getPluginNumber()
				+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
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
    	//clean up
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
		"The CSV parser parses a line of fields values (or a set of lines) and it generates an xml with the extracted values." +"\n"+
		"            2/ a set of line and generate an xml with the extracted values." +"\n"+
		"The CSV parser is typically invoked right after a step calling the lineparser plugin." +"\n"+
		"\n"+
		"The plugin will replace \n" +
		"- the {number} sequences  in the template with the value of the corresponding column in the line. " +
		" The first column of the line has number 1"+"\n"+
		"- the {/'regex'} sequences  in the template with the value of the column which HEADER name matches 'regex'" +"\n"+
		"\n"+
		"Column header names must be specified\n" +
		"- either as a comma separated list in a <headers>,header1,header2,...</headers> element \n" +
		"- or must be present on the first line of the CSV in which case a section <headersOnFirstLine>true</headersOnFirstLine> " +
		" should be specified in the parameters\n"+
		"\n"+
		"To avoid interpretation of an accolade, backslash it: \\{"+"\n"+
		"\n"+
		"The CSV parser can also loop over a sequence of columns using structure [LOOP regex] template [/LOOP]"+"\n"+
		"The loop will be triggered every time a header matching 'regex' is found and the 'template' will be executed"+"\n"+
		"An error will be triggered if not matching header is found, unless 'failOnMissingHeaders' is set to 'false'"+"\n"+
		"\n"+
		"Parameters\n" +
		"	separator: the separator used between fields\n"+
		"	lineseparator: the line separator used at the end of each line\n"+
		"	multiplelines: multiple lines (true or false)\n"+
		"	template: the new text containing the extraction sequences\n"+
		"	headers: [OPTIONAL defaults to nil]. A separated list of column names\n"+
		"	headersOnFirstLine: [OPTIONAL defaults to false]. If set to true, will pick up column names from the CSV first line\n"+
		"	failOnMissingHeaders: [OPTIONAL defaults to true]. If set to false, no error will be triggered when trying to match a header name\n"+
		"\n"+
		"\n"+
		"Example - this will generate an xml starting with element MyXml\n" +
		"	<parameters>" +"\n"+
		"		<separator>,</separator>  <!-- the fields are separated by a comma -->"+"\n"+
		"		<headersOnFirstLine>true</headersOnFirstLine>  <!-- the header names are on the first line of the CSV -->"+"\n"+
		"		<template>" +"\n"+
		"		<![CDATA["+"\n"+
		"			<MyXml>" +"\n"+
		"					<Field1>{1}</Field1> <!-- first field -->" +"\n"+
		"					<Combo>{2}-{4}</Combo> <!-- values of fields 2 and 4 separated by a dash -->" +"\n"+
		"					<NotInterpreted>\\{3}</NotInterpreted>  <!-- the value will remain {3}" +"\n"+
		"					<HeaderReference>{/Header1}</HeaderReference>  <!-- the value in column named 'Header1'" +"\n"+
		"					[LOOP lineNumber.*]   <!--starts a loop triggered by headers matching the regular expression lineNumber.*" +"\n"+
		"					<Line>							 <!--inside the loop - multiple Line elements may be generated -->" +"\n"+
		"						<Quantity>{/Quantity.*}</Quantity>  <!-- the value in first column matching 'Quantity.*' " +"\n"+
		"																		after the one matching 'lineNumber.*'-->" +"\n"+
		"					</Line>" +"\n"+
		"					[/LOOP] <!-- End Of Loop -->" +"\n"+
		"			</MyXml>" +"\n"+
		"		]]>"+"\n"+
		"		</template>" +"\n"+
		"	</parameters>" +"\n"+
		"";
	}


	private String getDefaultConfiguration(){
    	return
    		"<configuration>"+
    		"	<charset>utf-8</charset>"+
			"</configuration>";
    }



	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#getConfiguration(java.lang.String)
	 */
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
    	    String err = "Unable to deserialize the configuration of the CSV Parser Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }



	/* (non-Javadoc)
	 * @see com.amalto.core.ejb.ServiceCtrlBean#putConfiguration(java.lang.String)
	 */
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
	 * Compile/run the template
	 ********************************************************************************************/



	/**
	 * Runs a pre-compiled template
	 * @param steps
	 * @param fields
	 * @return the String generated from runing the compiled template on the fields
	 * @throws XtentisException
	 */
	private String runTemplate(ArrayList<Step> steps, String[] fields) throws XtentisException{
		String result="";
		for (Iterator<Step> iter = steps.iterator(); iter.hasNext(); ) {
			Step step = iter.next();
			if (step.constant != null) {
				//a constant
				result+=step.constant;
			} else if (step.column != -1) {
				//a field reference
				if (step.column>fields.length) {
					String err = 	"CSV Parser ERROR. No field at position "+step.column
							+". The last fields is at position "+fields.length;
					org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
					throw new XtentisException(err);
				}
				//apply operations
				if (step.operations.contains("trim")) fields[step.column-1] = fields[step.column-1].trim();
				//add to result
				result+=StringEscapeUtils.escapeXml(fields[step.column-1]);
			} else {
				//HUUHHH
			}
		}
		return result;
	}


	/********************************************************************************************
	 * Compilation - decompilation of parameters
	 ********************************************************************************************/

	/**
	 * A Compiled Step
	 */
	class Step implements Serializable{
		public String constant = null;;
		public int column = -1;
		public ArrayList<String> operations=new ArrayList<String>();
		protected int getColumn() {
			return column;
		}
		protected void setColumn(int column) {
			this.column = column;
		}
		protected String getConstant() {
			return constant;
		}
		protected void setConstant(String constant) {
			this.constant = constant;
		}
		protected ArrayList<String> getOperations() {
			return operations;
		}
		protected void setOperations(ArrayList<String> operations) {
			this.operations = operations;
		}
	}


	/**
	 * Compiles a template after the headers have been determined (if any)
	 * @param template
	 * @param headers
	 * @return the compiled template as a series of Steps that can be run using run template
	 * @throws XtentisException
	 */
	private ArrayList<Step> compileTemplate(String template, ArrayList<String> headers, boolean failOnMissingHeaders) throws XtentisException{
		try {
			ArrayList<Step> steps = new ArrayList<Step>();
			steps.addAll(compileLoops(template, headers, 0, headers.size()-1,failOnMissingHeaders));
			return steps;
		} catch (XtentisException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			String err = "CSV Parser could not compile the template: "+e.getClass().getName()+": "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);
		}
	}

	private static  Pattern loopsPattern = Pattern.compile("[^\\\\]\\[LOOP (.+?)([,.+?]*)\\](.+)\\[/LOOP]", Pattern.DOTALL);
	private static  Pattern failPattern = Pattern.compile("\\s*fail\\s*=\\s*(.+?)\\s*", Pattern.DOTALL);

	private ArrayList<Step> compileLoops(String template, ArrayList<String> headers, int startHeader, int lastHeader, boolean failOnMissingHeaders) throws XtentisException{
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileLoops() "+startHeader+"-"+lastHeader+": "+template);
		ArrayList<Step> steps = new ArrayList<Step>();

		int lastEnd = 0;
		Matcher m = loopsPattern.matcher(template);

		while (m.find(lastEnd ==0 ? 0 : lastEnd -1)) {
			//portion of the template before the loop
			String beforeLoop = template.substring(lastEnd, m.start()+1);
			//there is no loop there send it for further processing
			steps.addAll(compileSpecs(beforeLoop, headers, startHeader,failOnMissingHeaders));

			String loopHeaderRegex = m.group(1).trim();
			String loopTemplate="";;
			String optionString = "";
			boolean failMissingHeadersInLoop = false;
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileLoops() Match "+m.group(1)+" - "+m.group(2)+" - "+m.group(3));
			optionString = m.group(2);
			loopTemplate = m.group(3);
			String[] options = optionString.split(",");
			for (int i = 0; i < options.length; i++) {
				Matcher fpm = failPattern.matcher(options[i].trim());
				if (fpm.matches()) {
					if ("true,1,yes".indexOf(fpm.group(1))>=0) failMissingHeadersInLoop = true;
				}
			}

			Pattern loopHeaderPattern = Pattern.compile(loopHeaderRegex, Pattern.DOTALL);
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileLoops() Loop header Regex "+loopHeaderRegex);

			//loop over matching headers
			int previousHeaderMatch=-1;
			for (int i = startHeader; i <= lastHeader; i++) {
				String header = headers.get(i);
				Matcher lhm = loopHeaderPattern.matcher(header);
				if (lhm.matches()) {
					//we have a header match--> check if we have a previous match to run
					if (previousHeaderMatch!=-1)
						steps.addAll(compileLoops(loopTemplate, headers, previousHeaderMatch, i-1,failMissingHeadersInLoop));
					//update previous Header Match
					previousHeaderMatch = i;
				}
			}
			//Check if we had a match
			if (previousHeaderMatch==-1) {
				if (failOnMissingHeaders) {
					String err = "No matching header can be found for [LOOP "+loopHeaderRegex+"]";
					org.apache.log4j.Logger.getLogger(this.getClass()).error("compileLoops() "+err);
					throw new XtentisException(err);
				} else {
					org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileLoops() not matching header found for LOOP "+loopTemplate);
				}
			} else {
					steps.addAll(compileLoops(loopTemplate, headers, previousHeaderMatch, lastHeader,failMissingHeadersInLoop));
			}
			lastEnd= m.end();
		}
		//org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileLoops() LastEnd "+lastEnd);
		//portion of the template after the loop
		String afterLoop = template.substring(lastEnd);
		//there is no loop there send it for further processing
		steps.addAll(compileSpecs(afterLoop, headers, startHeader,failOnMissingHeaders));
		return steps;
	}

	private static  Pattern specsPattern = Pattern.compile("[^\\\\]\\{(.+?[,.+?]*)\\}");

	private ArrayList<Step> compileSpecs(String template, ArrayList<String> headers, int startHeader, boolean failOnMissingHeaders) throws XtentisException{
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileSpecs() "+startHeader+": "+template);
		ArrayList<Step> steps = new ArrayList<Step>();

		int lastEnd = 0;
		Matcher m = specsPattern.matcher(template);

		while (m.find(lastEnd ==0 ? 0 : lastEnd -1)) {
			//the column number
			int column=-1;
			//parse the specs
			ArrayList<String> specs= new ArrayList<String>(Arrays.asList(m.group(1).split(",")));
			//we have either a number reference of a header reference
			if (specs.get(0).startsWith("/")) {
				//A field reference --> find a matching header starting at postion 0
				if (headers.size()==0) {
					String err = "CSV Parser no headers provided in the CSV or Parameters. No header available matching the name '"+specs.get(0).substring(1)+"'";
					if (failOnMissingHeaders) {
						org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
						throw new XtentisException(err);
					} else {
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileSpecs() "+err);
					}
				}
				for (int i = startHeader; i < headers.size(); i++) {
					if (headers.get(i).matches(specs.get(0).substring(1))) {
						column = i+1;
						break;
					}
				}
				if (column==-1) {
					String err = "CSV Parser at position could not find any header matching the name '"+specs.get(0).substring(1)+"' starting from column "+startHeader;
					if (failOnMissingHeaders) {
						org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
						throw new XtentisException(err);
					} else {
						org.apache.log4j.Logger.getLogger(this.getClass()).debug("compileSpecs() "+err);
					}
				}
			} else {
				//a column number specification
				column = (new Integer(specs.get(0))).intValue();
			}

			//the step containing the hard coded values before the match
			Step constantStep = new Step();
			constantStep.constant = template.substring(lastEnd, m.start()+1);
			steps.add(constantStep);

			//the step containing the column ref
			Step columnStep = new Step();
			columnStep.column = column;
			//the intructions: trim, etc...
			for (int i = 1; i < specs.size(); i++) {
				columnStep.operations.add(specs.get(i));
			}
			steps.add(columnStep);

			lastEnd= m.end();
		}
		//the remaining constant text after the last match
		Step step = new Step();
		step.constant = template.substring(lastEnd).replaceAll("\\\\\\{", "{");
		steps.add(step);

		return steps;

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