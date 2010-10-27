package org.talend.mdm.test.transformer;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;

import org.talend.mdm.test.WebserviceTestCase;

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
 * TODO: you can make a abstract class for all plugin-testcases
 *
 */
public class DumpPluginWebserviceTestCase extends WebserviceTestCase {
	

	public void testRunDumpWithinADefinedTransformer() throws RemoteException, UnsupportedEncodingException {
		
		/*
		 *define transformer
		 */
		
		//define steps
		WSTransformerProcessStep[] steps=new WSTransformerProcessStep[1];
		//step 1
		steps[0]=new WSTransformerProcessStep(
					"amalto/local/transformer/plugin/dumpandgo",
					"dump the input content",
					"",//parameters
					new WSTransformerVariablesMapping[] {
							new WSTransformerVariablesMapping(
									"_DEFAULT_",
									"in_text",
									null
							)
					},
					new WSTransformerVariablesMapping[] {
							new WSTransformerVariablesMapping(
									"_DEFAULT_",
									"out_text",
									null
							)
					},
					new Boolean(false)
					);
			
		WSTransformerV2 wsTransformer = new WSTransformerV2(
			   "DumpPluginWebserviceTestCase",
			   "This is a test based transform to verify the dump plugin is okay or not",
			   steps 
			);
			
		WSTransformerV2PK wsTransformerV2PK=defaultPort.putTransformerV2(new WSPutTransformerV2(wsTransformer));
		
		//check exist
		WSBoolean wsExistAfterPut = defaultPort.existsTransformerV2(new WSExistsTransformerV2(wsTransformerV2PK));
		assertTrue(wsExistAfterPut.is_true());
		
		/*
		 *execute transformer
		 */
		String inputMessage="Hello dump! ";
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
									new WSByteArray(inputMessage.getBytes("UTF-8")),
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
		
		String valueFromProcess = "";
		if (entrie.getWsTypedContent().getWsBytes().getBytes() != null
				&& entrie.getWsTypedContent().getWsBytes().getBytes().length != 0) {
			valueFromProcess = new String(entrie.getWsTypedContent().getWsBytes().getBytes(), "UTF-8");
		}
		
		assertEquals(inputMessage, valueFromProcess);
		
		/*
		 *delete transformer
		 */
		defaultPort.deleteTransformerV2(new WSDeleteTransformerV2(new WSTransformerV2PK(wsTransformer.getName())));
		
		//check exist
		WSBoolean wsExistAfterDelete = defaultPort.existsTransformerV2(new WSExistsTransformerV2(wsTransformerV2PK));
		assertFalse(wsExistAfterDelete.is_true());

	}

}
