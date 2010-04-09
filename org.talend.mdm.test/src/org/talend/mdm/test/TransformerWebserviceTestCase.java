package org.talend.mdm.test;

import java.rmi.RemoteException;

import urn_com_amalto_xtentis_webservice.WSBackgroundJobPK;
import urn_com_amalto_xtentis_webservice.WSBoolean;
import urn_com_amalto_xtentis_webservice.WSDeleteTransformer;
import urn_com_amalto_xtentis_webservice.WSDeleteTransformerV2;
import urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2;
import urn_com_amalto_xtentis_webservice.WSExecuteTransformerV2AsJob;
import urn_com_amalto_xtentis_webservice.WSExistsTransformer;
import urn_com_amalto_xtentis_webservice.WSExistsTransformerPluginV2;
import urn_com_amalto_xtentis_webservice.WSExistsTransformerV2;
import urn_com_amalto_xtentis_webservice.WSExtractThroughTransformerV2;
import urn_com_amalto_xtentis_webservice.WSGetTransformer;
import urn_com_amalto_xtentis_webservice.WSGetTransformerPKs;
import urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2Details;
import urn_com_amalto_xtentis_webservice.WSGetTransformerPluginV2SList;
import urn_com_amalto_xtentis_webservice.WSGetTransformerV2;
import urn_com_amalto_xtentis_webservice.WSGetTransformerV2PKs;
import urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformer;
import urn_com_amalto_xtentis_webservice.WSProcessBytesUsingTransformerAsBackgroundJob;
import urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformer;
import urn_com_amalto_xtentis_webservice.WSProcessFileUsingTransformerAsBackgroundJob;
import urn_com_amalto_xtentis_webservice.WSPutTransformer;
import urn_com_amalto_xtentis_webservice.WSPutTransformerV2;
import urn_com_amalto_xtentis_webservice.WSString;
import urn_com_amalto_xtentis_webservice.WSTransformer;
import urn_com_amalto_xtentis_webservice.WSTransformerContext;
import urn_com_amalto_xtentis_webservice.WSTransformerContextPipelinePipelineItem;
import urn_com_amalto_xtentis_webservice.WSTransformerPK;
import urn_com_amalto_xtentis_webservice.WSTransformerPluginV2Details;
import urn_com_amalto_xtentis_webservice.WSTransformerPluginV2GetConfiguration;
import urn_com_amalto_xtentis_webservice.WSTransformerPluginV2PutConfiguration;
import urn_com_amalto_xtentis_webservice.WSTransformerPluginV2SListItem;
import urn_com_amalto_xtentis_webservice.WSTransformerProcessStep;
import urn_com_amalto_xtentis_webservice.WSTransformerV2;
import urn_com_amalto_xtentis_webservice.WSTransformerV2PK;
import urn_com_amalto_xtentis_webservice.WSTransformerVariablesMapping;
import urn_com_amalto_xtentis_webservice.WSTypedContent;

public class TransformerWebserviceTestCase extends WebserviceTestCase {

	public void testDeleteTransformer() {
		WSDeleteTransformer wsTransformerDelete = new WSDeleteTransformer();
		wsTransformerDelete.setWsTransformerPK(new WSTransformerPK("test"));
		try {
			WSTransformerPK wsTransformerPK = defaultPort
					.deleteTransformer(wsTransformerDelete);
			System.out.println(wsTransformerPK.getPk());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetTransformer() {
		WSGetTransformer wsGetTransformer = new WSGetTransformer();
		wsGetTransformer.setWsTransformerPK(new WSTransformerPK("test"));
		try {
			WSTransformer wsTransformer = defaultPort
					.getTransformer(wsGetTransformer);
			System.out.println(wsTransformer.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsTransformer() {
		WSExistsTransformer wsExistsTransformer = new WSExistsTransformer();
		wsExistsTransformer.setWsTransformerPK(new WSTransformerPK("test"));
		try {
			WSBoolean wsBoolean = defaultPort
					.existsTransformer(wsExistsTransformer);
			assertNotNull(wsBoolean);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetTransformerPKs() {
		WSGetTransformerPKs regex = new WSGetTransformerPKs();
		regex.setRegex("*");
		try {
			WSTransformerPK[] wsTransformerPKArray = defaultPort
					.getTransformerPKs(regex);
			for (int i = 0; i < wsTransformerPKArray.length; i++) {
				System.out.println(wsTransformerPKArray[i].getPk());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testPutTransformer() {
		try {
			WSPutTransformer wsPutTransformer = new WSPutTransformer();
			WSGetTransformer wsGetTransformer = new WSGetTransformer();
			wsGetTransformer.setWsTransformerPK(new WSTransformerPK("test"));
			wsGetTransformer.setWsTransformerPK(new WSTransformerPK("test2"));
			WSTransformer wsTransformer = defaultPort
					.getTransformer(wsGetTransformer);
			wsPutTransformer.setWsTransformer(wsTransformer);
			WSTransformerPK wsTransformerPK = defaultPort
					.putTransformer(wsPutTransformer);
			System.out.println(wsTransformerPK.getPk());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

//	public void testProcessBytesUsingTransformer(){
//		
//		WSProcessBytesUsingTransformer wsProjectBytes=new WSProcessBytesUsingTransformer();
//		wsProjectBytes.set
//		
//		WSPipeline
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator().processBytesUsingTransformer(
//						wsProjectBytes);
//	}
//
//	/**
//	 * @ejb.interface-method view-type = "service-endpoint"
//	 * @ejb.permission role-name = "authenticated" view-type =
//	 *                 "service-endpoint"
//	 */
//	public WSPipeline processFileUsingTransformer(
//			WSProcessFileUsingTransformer wsProcessFile) throws RemoteException {
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator().processFileUsingTransformer(
//						wsProcessFile);
//	}
//
//	/**
//	 * @ejb.interface-method view-type = "service-endpoint"
//	 * @ejb.permission role-name = "authenticated" view-type =
//	 *                 "service-endpoint"
//	 */
//	public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(
//			WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob)
//			throws RemoteException {
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator()
//				.processBytesUsingTransformerAsBackgroundJob(
//						wsProcessBytesUsingTransformerAsBackgroundJob);
//	}
//
//	/**
//	 * @ejb.interface-method view-type = "service-endpoint"
//	 * @ejb.permission role-name = "authenticated" view-type =
//	 *                 "service-endpoint"
//	 */
//	public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(
//			WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob)
//			throws RemoteException {
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator()
//				.processFileUsingTransformerAsBackgroundJob(
//						wsProcessFileUsingTransformerAsBackgroundJob);
//	}
//
//	/**
//	 * @ejb.interface-method view-type = "service-endpoint"
//	 * @ejb.permission role-name = "authenticated" view-type =
//	 *                 "service-endpoint"
//	 */
	public void testGetTransformerV2PKs() {
		WSGetTransformerV2PKs regex = new WSGetTransformerV2PKs("*");
		try {
			WSTransformerV2PK[] wsTransformerV2PKArray = defaultPort
					.getTransformerV2PKs(regex);
			for (int i = 0; i < wsTransformerV2PKArray.length; i++) {
				System.out.println(wsTransformerV2PKArray[i]);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	public void testPutTransformerV2() {
		try {
			WSPutTransformerV2 wsPutTransformerV2 = new WSPutTransformerV2();
			WSTransformerV2 wsTransformerV2=new WSTransformerV2(); 
			wsTransformerV2.setDescription("description");
			wsTransformerV2.setName("name");
			WSTransformerProcessStep[] processSteps=new WSTransformerProcessStep[1];
			WSTransformerProcessStep wsTransformerProcessStep0=new WSTransformerProcessStep();
			wsTransformerProcessStep0.setDescription("description");
			WSTransformerVariablesMapping[] outputMappings=new WSTransformerVariablesMapping[1];
			WSTransformerVariablesMapping wsTransformerVariablesMapping=new WSTransformerVariablesMapping();
			wsTransformerVariablesMapping.setPipelineVariable("pipelineVariable");
			wsTransformerVariablesMapping.setPluginVariable("pluginVariable");
			outputMappings[0]=wsTransformerVariablesMapping;
			wsTransformerProcessStep0.setOutputMappings(outputMappings);
			WSTransformerVariablesMapping[] inputMappings=new WSTransformerVariablesMapping[1];
			//WSTransformerVariablesMapping wsTransformerVariablesMapping=new WSTransformerVariablesMapping();
			wsTransformerVariablesMapping.setPipelineVariable("pipelineVariable");
			wsTransformerVariablesMapping.setPluginVariable("pluginVariable");
			WSTypedContent hardCoding = new WSTypedContent();
			hardCoding.setContentType("contentType");
			hardCoding.setUrl("url");
			
			wsTransformerVariablesMapping.setHardCoding(hardCoding);
			inputMappings[0]=wsTransformerVariablesMapping;
			wsTransformerProcessStep0.setInputMappings(inputMappings);
			wsTransformerProcessStep0.setDisabled(false);
			wsTransformerProcessStep0.setPluginJNDI("partialupdate");

			processSteps[0]=wsTransformerProcessStep0;
			wsTransformerV2.setProcessSteps(processSteps);
			
			wsPutTransformerV2.setWsTransformerV2(wsTransformerV2);
			WSTransformerV2PK wsTransformerV2PKReturn = defaultPort
					.putTransformerV2(wsPutTransformerV2);
			System.out.println(wsTransformerV2PKReturn);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
	public void testGetTransformerV2() {
		try {
			
			WSTransformerV2PK wsTransformerV2PK = new WSTransformerV2PK(); 
			wsTransformerV2PK.setPk("name");
			WSGetTransformerV2 wsGetTransformerV2 = new WSGetTransformerV2(
					wsTransformerV2PK);

			WSTransformerV2 wsTransformerV2 = defaultPort
					.getTransformerV2(wsGetTransformerV2);
			System.out.println(wsTransformerV2.getName());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsTransformerV2() {
		try {
			WSExistsTransformerV2 wsExistsTransformerV2 = new WSExistsTransformerV2();
			
			WSTransformerV2PK wsTransformerV2PK = new WSTransformerV2PK(); 
			wsTransformerV2PK.setPk("name");
			wsExistsTransformerV2.setWsTransformerV2PK(wsTransformerV2PK);
			WSBoolean wsBoolean = defaultPort
					.existsTransformerV2(wsExistsTransformerV2);
			System.out.println(wsBoolean.is_true());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}



//	// leo 2010年1月29日17:43:44
//	public void testExecuteTransformerV2(){
//		WSExecuteTransformerV2 wsExecuteTransformerV2=new WSExecuteTransformerV2();
//		WSTransformerContext wsTransformerContext =new WSTransformerContext();
//		WSTransformerContextPipelinePipelineItem[] wsTransformerContextPipelinePipelineItemArray=null;
//		wsTransformerContextPipelinePipelineItemArray[0].setWsTypedContent(WSTypedContent.)
//		wsTransformerContext.setPipeline(pipeline)
//		wsExecuteTransformerV2.setWsTransformerContext(wsTransformerContext)
//		WSTransformerContext
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator().executeTransformerV2(
//						wsExecuteTransformerV2);
//	}
//
//	/**
//	 * @ejb.interface-method view-type = "service-endpoint"
//	 * @ejb.permission role-name = "authenticated" view-type =
//	 *                 "service-endpoint"
//	 */
//	public WSBackgroundJobPK executeTransformerV2AsJob(
//			WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob)
//			throws RemoteException {
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator().executeTransformerV2AsJob(
//						wsExecuteTransformerV2AsJob);
//	}
//
//	/**
//	 * @ejb.interface-method view-type = "service-endpoint"
//	 * @ejb.permission role-name = "authenticated" view-type =
//	 *                 "service-endpoint"
//	 */
//	public WSTransformerContext extractThroughTransformerV2(
//			WSExtractThroughTransformerV2 wsExtractThroughTransformerV2)
//			throws RemoteException {
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator().extractThroughTransformerV2(
//						wsExtractThroughTransformerV2);
//	}
//
//	public WSTransformerV2PK deleteTransformerV2(
//			WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException {
//		return BeanDelegatorContainer.getUniqueInstance()
//				.getXtentisWSDelegator().deleteTransformerV2(
//						wsTransformerV2Delete);
//	}

	/***************************************************************************
	 * TRANSFORMER PLUGINS V2
	 * **************************************************************************/

	public void testGetTransformerPluginV2SList() {
		WSGetTransformerPluginV2SList wsGetTransformerPluginsList = new WSGetTransformerPluginV2SList();
		wsGetTransformerPluginsList.setLanguage("en");
		try {
			WSTransformerPluginV2SListItem[] wsTransformerPluginV2SArray = defaultPort
					.getTransformerPluginV2SList(wsGetTransformerPluginsList);
			for (int i = 0; i < wsTransformerPluginV2SArray.length; i++) {
				wsTransformerPluginV2SArray[i].getJndiName();
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testGetTransformerPluginV2Configuration() {
		try {
			WSGetTransformerPluginV2SList wsGetTransformerPluginsList = new WSGetTransformerPluginV2SList();
			wsGetTransformerPluginsList.setLanguage("en");
			WSTransformerPluginV2SListItem[] wsTransformerPluginV2SArray = defaultPort
					.getTransformerPluginV2SList(wsGetTransformerPluginsList);
			WSTransformerPluginV2GetConfiguration wsGetConfiguration = new WSTransformerPluginV2GetConfiguration();
			wsGetConfiguration.setJndiName(wsTransformerPluginV2SArray[0]
					.getJndiName());
			WSString wsString = defaultPort
					.getTransformerPluginV2Configuration(wsGetConfiguration);
			System.out.println(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testExistsTransformerPluginV2() {
		try {
			WSExistsTransformerPluginV2 wsExistsTransformerPlugin = new WSExistsTransformerPluginV2();
			WSGetTransformerPluginV2SList wsGetTransformerPluginsList = new WSGetTransformerPluginV2SList();
			wsGetTransformerPluginsList.setLanguage("en");
			WSTransformerPluginV2SListItem[] wsTransformerPluginV2SArray = defaultPort
					.getTransformerPluginV2SList(wsGetTransformerPluginsList);
			wsExistsTransformerPlugin
					.setJndiName(wsTransformerPluginV2SArray[0].getJndiName());
			WSBoolean flag = defaultPort
					.existsTransformerPluginV2(wsExistsTransformerPlugin);
			System.out.println(flag.is_true());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	public void testPutTransformerPluginV2Configuration() {
		try {
			WSGetTransformerPluginV2SList wsGetTransformerPluginsList = new WSGetTransformerPluginV2SList();
			wsGetTransformerPluginsList.setLanguage("en");
			WSTransformerPluginV2SListItem[] wsTransformerPluginV2SArray = defaultPort
					.getTransformerPluginV2SList(wsGetTransformerPluginsList);

			WSTransformerPluginV2PutConfiguration wsPutConfiguration = new WSTransformerPluginV2PutConfiguration();
			wsPutConfiguration.setJndiName(wsTransformerPluginV2SArray[0]
					.getJndiName());
			WSString wsString = defaultPort
					.putTransformerPluginV2Configuration(wsPutConfiguration);
			System.out.println(wsString);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void testGetTransformerPluginV2Details() {
		try {
			WSGetTransformerPluginV2Details wsGetTransformerPluginDetails = new WSGetTransformerPluginV2Details();
			WSGetTransformerPluginV2SList wsGetTransformerPluginsList = new WSGetTransformerPluginV2SList();
			wsGetTransformerPluginsList.setLanguage("en");
			WSTransformerPluginV2SListItem[] wsTransformerPluginV2SArray = defaultPort
					.getTransformerPluginV2SList(wsGetTransformerPluginsList);
			wsGetTransformerPluginDetails
					.setJndiName(wsTransformerPluginV2SArray[0].getJndiName());
			wsGetTransformerPluginDetails.setLanguage("en");
			WSTransformerPluginV2Details wsTransformerPluginV2Details = defaultPort
					.getTransformerPluginV2Details(wsGetTransformerPluginDetails);
			System.out.println(wsTransformerPluginV2Details.getDescription());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}
}
