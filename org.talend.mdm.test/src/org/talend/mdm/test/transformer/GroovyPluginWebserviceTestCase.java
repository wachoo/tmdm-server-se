package org.talend.mdm.test.transformer;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.talend.mdm.test.WebserviceTestCase;
import org.xml.sax.SAXException;

import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSByteArray;
import urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2;
import urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2;
import urn_com_amalto_xtentis_webservice.WSExistsTransformerV2;
import urn_com_amalto_xtentis_webservice.WSPutTransformerV2;
import urn_com_amalto_xtentis_webservice.WSTransformerContext;
import urn_com_amalto_xtentis_webservice.WSTransformerContextPipelinePipelineItem;
import urn_com_amalto_xtentis_webservice.WSTransformerProcessStep;
import urn_com_amalto_xtentis_webservice.WSTransformerV2;
import urn_com_amalto_xtentis_webservice.WSTransformerV2PK;
import urn_com_amalto_xtentis_webservice.WSTransformerVariablesMapping;
import urn_com_amalto_xtentis_webservice.WSTypedContent;

/**
 * @author Starkey
 *
 */
public class GroovyPluginWebserviceTestCase extends WebserviceTestCase {
	

	public void testRunGroovyWithinADefinedTransformer() throws TransformerException, ParserConfigurationException, IOException, SAXException {
		
		/*
		 *define transformer
		 */
		
		//define steps
		WSTransformerProcessStep[] steps=new WSTransformerProcessStep[1];
		//step 1
		String parameters="";
		StringBuffer sb=new StringBuffer();
		sb.append("<parameters> ").append("\n"); 
		sb.append("    <autoParseXml>true</autoParseXml> ").append("\n"); 
		sb.append("    <script><![CDATA[ ").append("\n"); 
		sb.append(" ").append("\n"); 
		sb.append("            //get textnodes ").append("\n"); 
		sb.append("		       def langs = variableInput; ").append("\n"); 
		sb.append("		       println \"type = ${langs.attribute(\"type\")}\"; ").append("\n"); 
		sb.append("            langs.language.each{ ").append("\n"); 
		sb.append("               println it.text(); ").append("\n"); 
		sb.append("            } ").append("\n"); 
		sb.append(" ").append("\n"); 
		sb.append("		       //modify ").append("\n"); 
		sb.append("            def firstRecord = langs.language[0]; ").append("\n"); 
		sb.append("            firstRecord.value = ['Ruby']; ").append("\n"); 
		sb.append("            assert 'Ruby' == firstRecord.text(); ").append("\n"); 
		sb.append(" ").append("\n"); 
		sb.append("            //write to string ").append("\n"); 
		sb.append("            def writer = new java.io.StringWriter(); ").append("\n"); 
		sb.append("            def printer = new groovy.util.XmlNodePrinter( new PrintWriter( writer )); ").append("\n"); 
		sb.append("            printer.print( langs ); ").append("\n"); 
		sb.append(" ").append("\n"); 
		sb.append("		    return writer.toString(); ").append("\n"); 
		sb.append("		]]></script> ").append("\n"); 
		sb.append("</parameters> ").append("\n");
		parameters=sb.toString();

		steps[0]=new WSTransformerProcessStep(
					"amalto/local/transformer/plugin/groovy",
					"test the typical use cases of the groovy plugin",
					parameters,//parameters
					new WSTransformerVariablesMapping[] {
							new WSTransformerVariablesMapping(
									"_DEFAULT_",
									"variable_input",
									null
							)
					},
					new WSTransformerVariablesMapping[] {
							new WSTransformerVariablesMapping(
									"_DEFAULT_",
									"script_output",
									null
							)
					},
					new Boolean(false)
					);
			
		WSTransformerV2 wsTransformer = new WSTransformerV2(
			   "GroovyPluginWebserviceTestCase",
			   "This is a test based transform to verify the groovy plugin is okay or not",
			   steps 
			);
			
		WSTransformerV2PK wsTransformerV2PK=defaultPort.putTransformerV2(new WSPutTransformerV2(wsTransformer));
		
		//check exist
		WSBoolean wsExistAfterPut = defaultPort.existsTransformerV2(new WSExistsTransformerV2(wsTransformerV2PK));
		assertTrue(wsExistAfterPut.is_true());
		
		/*
		 *execute transformer
		 */
		String inputXML="<langs type=\"current\">" +
								"<language>Java</language>" +
								"<language>Groovy</language>" +
								"<language>JavaScript</language>" +
				        "</langs>";
		
		WSTransformerContext wsTransformerContext=
		   defaultPort.executeTransformerV2(
				  new WSExecuteTransformerV2(
					new WSTransformerContext(
					  new WSTransformerV2PK(wsTransformer.getName()),
					  new WSTransformerContextPipelinePipelineItem[] {
						  new WSTransformerContextPipelinePipelineItem(
						     "_DEFAULT_",
						     new WSTypedContent(
						    		null,
									new WSByteArray(inputXML.getBytes("UTF-8")),
									"text/xml; charset=utf-8")
						  )
					  },
					  null
					),
					null
				  )
				);
		
		//get output value
		WSTransformerContextPipelinePipelineItem[] entries = wsTransformerContext.getPipeline();
		WSTransformerContextPipelinePipelineItem entrie = null;
		for (int i = 0; i < entries.length; i++) {
			if ("_DEFAULT_".equals(entries[i].getVariable())) {
				entrie = entries[i];
				break;
			}
		}
		
		String xmlFromProcess = "";
		if (entrie.getWsTypedContent().getWsBytes().getBytes() != null
				&& entrie.getWsTypedContent().getWsBytes().getBytes().length != 0) {
			xmlFromProcess = new String(entrie.getWsTypedContent().getWsBytes().getBytes(), "UTF-8");
		}
		
		String actualValue=com.amalto.core.util.Util.getFirstTextNode(com.amalto.core.util.Util.parse(xmlFromProcess), "/langs/language");
		assertEquals("Ruby", actualValue.trim());
		
		/*
		 *delete transformer
		 */
		defaultPort.deleteTransformerV2(new WSDeleteTransformerV2(new WSTransformerV2PK(wsTransformer.getName())));
		
		//check exist
		WSBoolean wsExistAfterDelete = defaultPort.existsTransformerV2(new WSExistsTransformerV2(wsTransformerV2PK));
		assertFalse(wsExistAfterDelete.is_true());

	}

}
