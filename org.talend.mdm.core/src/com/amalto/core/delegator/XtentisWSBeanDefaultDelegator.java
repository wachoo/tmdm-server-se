package com.amalto.core.delegator;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.DroppedItemPOJO;
import com.amalto.core.ejb.DroppedItemPOJOPK;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.TransformerCtrlBean;
import com.amalto.core.ejb.TransformerPOJO;
import com.amalto.core.ejb.TransformerPOJOPK;
import com.amalto.core.ejb.UpdateReportItemPOJO;
import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlUtil;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.menu.ejb.MenuEntryPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJOPK;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.RoutingRuleExpressionPOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJOPK;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlUtil;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanItemLine;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanObjectLine;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationRemoteInstance;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.transformers.v2.util.TransformerVariablesMapping;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlUtil;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.objects.view.ejb.local.ViewCtrlUtil;
import com.amalto.core.util.ArrayListHolder;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.RoleInstance;
import com.amalto.core.util.RoleSpecification;
import com.amalto.core.util.TransformerPluginContext;
import com.amalto.core.util.TransformerPluginSpec;
import com.amalto.core.util.UpdateReportItem;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.*;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereOr;

public class XtentisWSBeanDefaultDelegator implements IXtentisWSBeanDelegator {

	public WSString ping(WSPing wsPing) throws RemoteException {
		return new WSString(wsPing.getEcho());
	}

	public WSMDMConfig getMDMConfiguration() throws RemoteException {
		WSMDMConfig mdmConfig = new WSMDMConfig();
		Properties property = MDMConfiguration.getConfiguration();
		try {
			mdmConfig.setServerName(property.getProperty("xmldb.server.name"));
			mdmConfig.setServerPort(property.getProperty("xmldb.server.port"));
			mdmConfig.setUserName(property
					.getProperty("xmldb.administrator.username"));
			mdmConfig.setPassword(property
					.getProperty("xmldb.administrator.password"));
			mdmConfig.setXdbDriver(property.getProperty("xmldb.driver"));
			mdmConfig.setXdbID(property.getProperty("xmldb.dbid"));
			mdmConfig.setXdbUrl(property.getProperty("xmldb.dburl"));
			mdmConfig.setIsupurl(property.getProperty("xmldb.isupurl"));
		} catch (Exception e) {
			throw new RemoteException(
					(e.getCause() == null ? e.getLocalizedMessage() : e
							.getCause().getLocalizedMessage()));
		}

		return mdmConfig;
	}

	public WSInt initMDM(WSInitData initData) throws RemoteException {
		// TODO Auto-generated method stub
		return new WSInt(0);
	}

	public WSString logout(WSLogout logout) throws RemoteException {
		String msg = "OK";
		try {
			LocalUser user = LocalUser.getLocalUser();
			user.logout();
		} catch (Exception e) {
			msg = e.getMessage();
		}
		return new WSString(msg);
	}
	
	/******************Data Model ***********************/
	public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel)
			throws RemoteException {
		try {
			return new WSDataModelPK(Util.getDataModelCtrlLocal()
					.removeDataModel(
							new DataModelPOJOPK(wsDeleteDataModel
									.getWsDataModelPK().getPk())).getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(
					(e.getCause() == null ? e.getLocalizedMessage() : e
							.getCause().getLocalizedMessage()));
		}
	}

	public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel)
			throws RemoteException {
		try {
			return new WSBoolean((Util.getDataModelCtrlLocal().existsDataModel(
					new DataModelPOJOPK(wsExistsDataModel.getWsDataModelPK()
							.getPk())) != null));
		} catch (Exception e) {
			throw new RemoteException(
					(e.getCause() == null ? e.getLocalizedMessage() : e
							.getCause().getLocalizedMessage()));
		}
	}

	public WSDataModel getDataModel(WSGetDataModel wsDataModelget)
			throws RemoteException {
		try {
			return VO2WS(Util.getDataModelCtrlLocal().getDataModel(
					new DataModelPOJOPK(wsDataModelget.getWsDataModelPK()
							.getPk())));
		} catch (Exception e) {
			throw new RemoteException(
					(e.getCause() == null ? e.getLocalizedMessage() : e
							.getCause().getLocalizedMessage()));
		}
	}

	protected WSDataModel VO2WS(DataModelPOJO pojo) {
		WSDataModel s = new WSDataModel();
		s.setDescription(pojo.getDescription());
		s.setName(pojo.getName());
		s.setXsdSchema(pojo.getSchema());
		return s;
	}

	protected DataModelPOJO WS2VO(WSDataModel ws) throws Exception {
		DataModelPOJO dv = new DataModelPOJO();
		dv.setName(ws.getName());
		dv.setDescription(ws.getDescription());
		dv.setSchema(ws.getXsdSchema());
		return dv;
	}

	public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp)
			throws RemoteException {
		try {
			WSDataModelPKArray array = new WSDataModelPKArray();
			Collection<DataModelPOJOPK> l = Util.getDataModelCtrlLocal()
					.getDataModelPKs(regexp.getRegex());
			ArrayList<WSDataModelPK> wsList = new ArrayList<WSDataModelPK>();
			for (Iterator iter = l.iterator(); iter.hasNext();) {
				DataModelPOJOPK pk = (DataModelPOJOPK) iter.next();
				WSDataModelPK wsPK = new WSDataModelPK(pk.getUniqueId());
				wsList.add(wsPK);
			}
			array
					.setWsDataModelPKs(wsList.toArray(new WSDataModelPK[l
							.size()]));
			return array;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: " + e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err, e);
			throw new RemoteException(
					(e.getCause() == null ? e.getLocalizedMessage() : e
							.getCause().getLocalizedMessage()));
		}
	}

	public WSDataModelPK putDataModel(WSPutDataModel wsDataModel)
			throws RemoteException {
		try {
			return new WSDataModelPK(Util.getDataModelCtrlLocal().putDataModel(
					WS2VO(wsDataModel.getWsDataModel())).getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(
					(e.getCause() == null ? e.getLocalizedMessage() : e
							.getCause().getLocalizedMessage()));
		}
	}
	protected WSStoredProcedure POJO2WS(StoredProcedurePOJO storedProcedurePOJO) throws Exception{
		WSStoredProcedure ws = new WSStoredProcedure();
		ws.setName(storedProcedurePOJO.getName());
		ws.setDescription(storedProcedurePOJO.getDescription());
		ws.setProcedure(storedProcedurePOJO.getProcedure());
		return ws;
	}

	protected StoredProcedurePOJO WS2POJO(WSStoredProcedure wsStoredProcedure) throws Exception{
		StoredProcedurePOJO pojo = new StoredProcedurePOJO();
		pojo.setName(wsStoredProcedure.getName());
		pojo.setDescription(wsStoredProcedure.getDescription());
		pojo.setProcedure(wsStoredProcedure.getProcedure());
		return pojo;
	}
	protected WSTransformerContext POJO2WS(TransformerContext context) throws Exception{
		WSTransformerContext wsContext = new WSTransformerContext();
		
		WSTransformerContextPipeline wsPipeline = new WSTransformerContextPipeline();
		ArrayList<WSTransformerContextPipelinePipelineItem> wsList = new ArrayList<WSTransformerContextPipelinePipelineItem>();
		LinkedHashMap<String, TypedContent> pipeline = context.getPipelineClone();
		Set< String> variables = pipeline.keySet();
		for (Iterator iter = variables.iterator(); iter.hasNext(); ) {
			String variable = (String) iter.next();
			WSTransformerContextPipelinePipelineItem wsItem = new WSTransformerContextPipelinePipelineItem();
			wsItem.setVariable(variable);
			wsItem.setWsTypedContent(POJO2WS(pipeline.get(variable)));
			wsList.add(wsItem);
		}
		wsPipeline.setPipelineItem(wsList.toArray(new WSTransformerContextPipelinePipelineItem[wsList.size()]));
		wsContext.setPipeline(wsPipeline);
		
		WSTransformerContextProjectedItemPKs wsProjectedItemPKs = new WSTransformerContextProjectedItemPKs();
		ArrayList<WSItemPK> wsPKList = new ArrayList<WSItemPK>();
		SortedSet<ItemPOJOPK>projectedPKs = context.getProjectedPKs();
		for (Iterator iter = projectedPKs.iterator(); iter.hasNext(); ) {
			ItemPOJOPK pk = (ItemPOJOPK) iter.next();
			wsPKList.add(POJO2WS(pk));
		}
		wsProjectedItemPKs.setWsItemPOJOPK(wsPKList.toArray(new WSItemPK[wsPKList.size()]));
		wsContext.setProjectedItemPKs(wsProjectedItemPKs);
		
		return wsContext;
	}
	
	protected TransformerContext WS2POJO(WSTransformerContext wsContext) throws Exception{
		TransformerContext context = new TransformerContext(new TransformerV2POJOPK(wsContext.getWsTransformerPK().getPk()));
		
		if (wsContext.getPipeline()!=null) {
			if (wsContext.getPipeline().getPipelineItem()!=null)
				for (int i = 0; i < wsContext.getPipeline().getPipelineItem().length; i++) {
					WSTransformerContextPipelinePipelineItem wsItem = wsContext.getPipeline().getPipelineItem()[i];
					context.putInPipeline(wsItem.getVariable(), WS2POJO(wsItem.getWsTypedContent()));
				}
		}
		
		if (wsContext.getProjectedItemPKs() != null) {
			if (wsContext.getProjectedItemPKs().getWsItemPOJOPK() !=null)
				for (int i = 0; i < wsContext.getProjectedItemPKs().getWsItemPOJOPK().length; i++) {
					WSItemPK wsPK = wsContext.getProjectedItemPKs().getWsItemPOJOPK()[i];
					context.getProjectedPKs().add(WS2POJO(wsPK));
				}
		}
		
		return context;
	}


	protected WSTypedContent POJO2WS(TypedContent content) throws Exception{
		if (content==null) return null;
		WSTypedContent wsTypedContent = new WSTypedContent();
		if (content.getUrl() == null) {
			wsTypedContent.setWsBytes(new WSByteArray(content.getContentBytes()));
		}
		wsTypedContent.setUrl(content.getUrl());
		wsTypedContent.setContentType(content.getContentType());
		return wsTypedContent;
	}
	
	protected TypedContent WS2POJO(WSTypedContent wsContent) throws Exception{
		if (wsContent == null) return null;
		TypedContent content =null;
		if (wsContent.getUrl() == null) {
			content = new TypedContent(wsContent.getWsBytes().getBytes(),wsContent.getContentType());
		} else {
			content = new TypedContent(wsContent.getUrl(),wsContent.getContentType());
		}
		return content;
	}
	
	protected WSTransformerVariablesMapping POJO2WS(TransformerVariablesMapping mappings) throws Exception{
		WSTransformerVariablesMapping wsMapping = new WSTransformerVariablesMapping();
		wsMapping.setPluginVariable(mappings.getPluginVariable());
		wsMapping.setPipelineVariable(mappings.getPipelineVariable());
		wsMapping.setHardCoding(POJO2WS(mappings.getHardCoding()));
		return wsMapping;
	}
	
	protected TransformerVariablesMapping WS2POJO(WSTransformerVariablesMapping wsMapping) throws Exception{
		TransformerVariablesMapping mapping = new TransformerVariablesMapping();
		mapping.setPluginVariable(wsMapping.getPluginVariable());
		mapping.setPipelineVariable(wsMapping.getPipelineVariable());
		mapping.setHardCoding(WS2POJO(wsMapping.getHardCoding()));
		return mapping;
	}
	
	protected WSTransformerProcessStep POJO2WS(TransformerProcessStep processStep) throws Exception{
		WSTransformerProcessStep wsProcessStep = new WSTransformerProcessStep();
		wsProcessStep.setDescription(processStep.getDescription());
		wsProcessStep.setDisabled(processStep.isDisabled());
		wsProcessStep.setParameters(processStep.getParameters());
		wsProcessStep.setPluginJNDI(processStep.getPluginJNDI());
		
		ArrayList<WSTransformerVariablesMapping> wsMappings = new ArrayList<WSTransformerVariablesMapping>(); 
		ArrayList<TransformerVariablesMapping> list = processStep.getInputMappings();
		for (Iterator iter = list.iterator(); iter.hasNext(); ) {
			TransformerVariablesMapping mapping = (TransformerVariablesMapping) iter.next();
			wsMappings.add(POJO2WS(mapping));
		}
		wsProcessStep.setInputMappings(wsMappings.toArray(new WSTransformerVariablesMapping[wsMappings.size()]));
		
		wsMappings = new ArrayList<WSTransformerVariablesMapping>(); 
		list = processStep.getOutputMappings();
		for (Iterator iter = list.iterator(); iter.hasNext(); ) {
			TransformerVariablesMapping mapping = (TransformerVariablesMapping) iter.next();
			wsMappings.add(POJO2WS(mapping));
		}
		wsProcessStep.setOutputMappings(wsMappings.toArray(new WSTransformerVariablesMapping[wsMappings.size()]));
		return wsProcessStep;
	}
	
	protected TransformerProcessStep WS2POJO(WSTransformerProcessStep wsProcessStep) throws Exception{
		TransformerProcessStep processStep = new TransformerProcessStep();
		processStep.setDescription(wsProcessStep.getDescription());
		processStep.setDisabled(wsProcessStep.getDisabled().booleanValue());
		processStep.setParameters(wsProcessStep.getParameters());
		processStep.setPluginJNDI(wsProcessStep.getPluginJNDI());
		ArrayList<TransformerVariablesMapping> inputMappings = new ArrayList<TransformerVariablesMapping>();
		if (wsProcessStep.getInputMappings()!=null) {
			for (int i = 0; i < wsProcessStep.getInputMappings().length; i++) {
				inputMappings.add(WS2POJO(wsProcessStep.getInputMappings()[i]));
			}
		}
		processStep.setInputMappings(inputMappings);
		ArrayList<TransformerVariablesMapping> outputMappings = new ArrayList<TransformerVariablesMapping>();
		if (wsProcessStep.getOutputMappings()!=null) {
			for (int i = 0; i < wsProcessStep.getOutputMappings().length; i++) {
				outputMappings.add(WS2POJO(wsProcessStep.getOutputMappings()[i]));
			}
		}
		processStep.setOutputMappings(outputMappings);
		return processStep;
	}
    
	protected WSTransformerV2 POJO2WS(TransformerV2POJO transformerPOJO) throws Exception{
		WSTransformerV2 ws = new WSTransformerV2();
		ws.setName(transformerPOJO.getName());
		ws.setDescription(transformerPOJO.getDescription());
		ArrayList<WSTransformerProcessStep> wsSteps = new ArrayList<WSTransformerProcessStep>();
		ArrayList< TransformerProcessStep> processSteps = transformerPOJO.getProcessSteps();
		if (processSteps!=null) {
			for (Iterator iter = processSteps.iterator(); iter.hasNext(); ) {
				TransformerProcessStep processStep = (TransformerProcessStep)iter.next();
				wsSteps.add(POJO2WS(processStep));
			}
		}
		ws.setProcessSteps(wsSteps.toArray(new WSTransformerProcessStep[wsSteps.size()]));
		return ws;
	}

	protected TransformerV2POJO WS2POJO(WSTransformerV2 wsTransformerV2) throws Exception{
		TransformerV2POJO pojo = new TransformerV2POJO();
		pojo.setName(wsTransformerV2.getName());
		pojo.setDescription(wsTransformerV2.getDescription());
		ArrayList<TransformerProcessStep> steps = new ArrayList<TransformerProcessStep>();
		WSTransformerProcessStep[] wsSteps = wsTransformerV2.getProcessSteps();
		if (wsSteps!=null) {
			for (int i = 0; i < wsSteps.length; i++) {
				TransformerProcessStep step = WS2POJO(wsSteps[i]);
				steps.add(step);		
			}
		}
		pojo.setProcessSteps(steps);
		return pojo;
	}
	public static WSPipeline POJO2WS(HashMap<String,TypedContent> pipeline) throws Exception{
		if (pipeline == null) return null;
		
		ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
		Set keys = pipeline.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
			String output = (String) iter.next();
			TypedContent content = pipeline.get(output);
			byte[] bytes = content.getContentBytes();
			WSExtractedContent wsContent = new WSExtractedContent(
				new WSByteArray(bytes),
				content.getContentType()
			);
			WSPipelineTypedContentEntry wsEntry = new WSPipelineTypedContentEntry(
				output,
				wsContent
			);
			entries.add(wsEntry);
		}
		return new WSPipeline(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
	}
	
	
	public static HashMap<String, TypedContent> WS2POJO(WSPipeline wsPipeline) throws Exception{
		if (wsPipeline == null) return null;
		
		HashMap<String, TypedContent> pipeline = new HashMap<String, TypedContent>();
		WSPipelineTypedContentEntry[] entries = wsPipeline.getTypedContentEntry();
		if (entries == null) return pipeline;
		
		for (int i = 0; i < entries.length; i++) {
			pipeline.put(
				entries[i].getOutput(), 
				new TypedContent(
					entries[i].getWsExtractedContent().getWsByteArray().getBytes(),
					entries[i].getWsExtractedContent().getContentType()
				)
			);
		}
		return pipeline;
	}
	
	protected WSRole POJO2WS(RolePOJO rolePOJO) throws Exception{
		WSRole ws = new WSRole();
		ws.setName(rolePOJO.getName());
		ws.setDescription(rolePOJO.getDescription());
		Set<String> objectTypes =rolePOJO.getRoleSpecifications().keySet();
		ArrayList<WSRoleSpecification> wsSpecifications = new ArrayList<WSRoleSpecification>();
		for (Iterator iter = objectTypes.iterator(); iter.hasNext(); ) {
			String objectType = (String) iter.next();
			RoleSpecification specification = rolePOJO.getRoleSpecifications().get(objectType);
			//convert instances
			ArrayList<WSRoleSpecificationInstance> wsInstances = new ArrayList<WSRoleSpecificationInstance>();
			Set<String> instanceIds = specification.getInstances().keySet();
			for (Iterator iterator = instanceIds.iterator(); iterator.hasNext(); ) {
				String id = (String) iterator.next();
				RoleInstance instance = specification.getInstances().get(id);
				//convert parameters
				String[] wsParameters = instance.getParameters().toArray(new String[instance.getParameters().size()]);
				WSRoleSpecificationInstance wsInstance = new WSRoleSpecificationInstance(
						id,
						instance.isWriteable(),

						wsParameters
				);
				wsInstances.add(wsInstance);
			}
			WSRoleSpecification wsSpecification = new WSRoleSpecification(
					objectType,
					specification.isAdmin(),
					wsInstances.toArray(new WSRoleSpecificationInstance[wsInstances.size()])
			);
			wsSpecifications.add(wsSpecification);
		}
		ws.setSpecification(wsSpecifications.toArray(new WSRoleSpecification[wsSpecifications.size()]));
		return ws;
	}

	protected RolePOJO WS2POJO(WSRole wsRole) throws Exception{
		RolePOJO pojo = new RolePOJO();
		pojo.setName(wsRole.getName());
		pojo.setDescription(wsRole.getDescription());
		HashMap<String,RoleSpecification> specifications = new HashMap<String, RoleSpecification>();
		if (wsRole.getSpecification()!=null) {
			for (int i = 0; i < wsRole.getSpecification().length; i++) {
				WSRoleSpecification wsSpecification= wsRole.getSpecification()[i];
				RoleSpecification specification = new RoleSpecification();
				specification.setAdmin(wsSpecification.isAdmin());
				if (wsSpecification.getInstance() != null) {
					for (int j = 0; j < wsSpecification.getInstance().length; j++) {
						WSRoleSpecificationInstance wsInstance =wsSpecification.getInstance()[j];
						RoleInstance instance = new RoleInstance();
						instance.setWriteable(wsInstance.isWritable());
						instance.setParameters(new HashSet<String>());
						if (wsInstance.getParameter()!=null)
							instance.getParameters().addAll(Arrays.asList(wsInstance.getParameter()));
						specification.getInstances().put(
								wsInstance.getInstanceName(), 
								instance
						);
					}//
				}
				specifications.put(
						wsSpecification.getObjectType(), 
						specification
				);
			}//for specifications
		}
		pojo.setRoleSpecifications(specifications);
		return pojo;
	}

	protected WSMenu POJO2WS(MenuPOJO pojo) throws Exception{
		WSMenu ws = new WSMenu();
		ws.setName(pojo.getName());
		ws.setDescription(pojo.getDescription());
		if (pojo.getMenuEntries()!=null) {
			WSMenuEntry[] wsSubMenus = new WSMenuEntry[pojo.getMenuEntries().size()];
			int i=0;
			for (Iterator iter = pojo.getMenuEntries().iterator(); iter.hasNext(); ) {
				MenuEntryPOJO menuEntry = (MenuEntryPOJO) iter.next();
				wsSubMenus[i++] = POJO2WS(menuEntry);
			}
			ws.setMenuEntries(wsSubMenus);
		}
		return ws;
	}

	
	protected MenuPOJO WS2POJO(WSMenu ws) throws Exception{
		MenuPOJO pojo = new MenuPOJO();
		pojo.setName(ws.getName());
		pojo.setDescription(ws.getDescription());
		ArrayList<MenuEntryPOJO> menuEntries = new ArrayList<MenuEntryPOJO>();
		if (ws.getMenuEntries()!=null) {
			for (int i = 0; i < ws.getMenuEntries().length; i++) {
				menuEntries.add(WS2POJO(ws.getMenuEntries()[i]));
			}
		}
		pojo.setMenuEntries(menuEntries);
		return pojo;
	}	
	
	protected WSMenuEntry POJO2WS(MenuEntryPOJO pojo) throws Exception{
		WSMenuEntry ws = new WSMenuEntry();
		ws.setId(pojo.getId());
		Set<String> languages = pojo.getDescriptions().keySet();
		WSMenuMenuEntriesDescriptions[] wsDescriptions = new WSMenuMenuEntriesDescriptions[languages.size()];
		int i=0;
		for (Iterator iter = languages.iterator(); iter.hasNext(); ) {
			String language = (String) iter.next();
			wsDescriptions[i] = new WSMenuMenuEntriesDescriptions();
			wsDescriptions[i].setLanguage(language);
			wsDescriptions[i].setLabel(pojo.getDescriptions().get(language));
			i++;
		}
		ws.setDescriptions(wsDescriptions);
		ws.setContext(pojo.getContext());
		ws.setApplication(pojo.getApplication());
		if (pojo.getSubMenus()!=null) {
			WSMenuEntry[] wsSubMenus = new WSMenuEntry[pojo.getSubMenus().size()];
			i=0;
			for (Iterator iter = pojo.getSubMenus().iterator(); iter.hasNext(); ) {
				MenuEntryPOJO menuEntry = (MenuEntryPOJO) iter.next();
				wsSubMenus[i++] = POJO2WS(menuEntry);
			}
			ws.setSubMenus(wsSubMenus);
		}
		return ws;
	}

	protected MenuEntryPOJO WS2POJO(WSMenuEntry ws) throws Exception{
		MenuEntryPOJO pojo = new MenuEntryPOJO();
		pojo.setId(ws.getId());
		HashMap<String, String> descriptions = new HashMap<String, String>();
		if (ws.getDescriptions()!=null) {
			for (int i = 0; i < ws.getDescriptions().length; i++) {
				descriptions.put(
						ws.getDescriptions()[i].getLanguage(),
						ws.getDescriptions()[i].getLabel()
				);
			}
		}
		pojo.setDescriptions(descriptions);
		pojo.setContext(ws.getContext());
		pojo.setApplication(ws.getApplication());
		ArrayList<MenuEntryPOJO> subMenus = new ArrayList<MenuEntryPOJO>();
		if (ws.getSubMenus()!=null) {
			for (int i = 0; i < ws.getSubMenus().length; i++) {
				subMenus.add(WS2POJO(ws.getSubMenus()[i]));
			}
		}
		pojo.setSubMenus(subMenus);
		return pojo;
	}	
	protected WSBackgroundJob POJO2WS(BackgroundJobPOJO pojo) throws Exception{
		try {
		    WSBackgroundJob s = new WSBackgroundJob();
		    s.setId(pojo.getId());
		    s.setDescription(pojo.getDescription());
		    switch(pojo.getStatus()) {
		    	case 0:
		    		s.setStatus(BackgroundJobStatusType.COMPLETED);
		    		break;
		    	case 1:
		    		s.setStatus(BackgroundJobStatusType.RUNNING);
		    		break;
		    	case 2:
		    		s.setStatus(BackgroundJobStatusType.SUSPENDED);
		    		break;
		    	case 3:
		    		s.setStatus(BackgroundJobStatusType.STOPPED);
		    		break;
		    	case 4:
		    		s.setStatus(BackgroundJobStatusType.CANCEL_REQUESTED);
		    		break;
		    	case 5:
		    		s.setStatus(BackgroundJobStatusType.SCHEDULED);
		    		break;
		    	default:
		    		throw new Exception("Unknow BackgroundJob Status: "+pojo.getStatus());
		    }
		    s.setMessage(pojo.getMessage());
		    s.setPercentage(pojo.getPercentage());
		    s.setTimestamp(pojo.getTimestamp());
		    s.setPipeline(pojo.getWsPipeline());
			return s;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw(e);
		}
	}
		

	protected BackgroundJobPOJO WS2POJO(WSBackgroundJob ws) throws Exception{
	    BackgroundJobPOJO pojo = new BackgroundJobPOJO();
	    pojo.setId(ws.getId());
	    pojo.setMessage(ws.getMessage());
	    pojo.setDescription(ws.getDescription());
	    pojo.setPercentage(ws.getPercentage());
	    pojo.setTimestamp(ws.getTimestamp());
	    if (ws.getStatus().equals(BackgroundJobStatusType.CANCEL_REQUESTED)) {
	    	pojo.setStatus(BackgroundJobPOJO._CANCEL_REQUESTED_); 
	    } else if (ws.getStatus().equals(BackgroundJobStatusType.COMPLETED)) {
	    	pojo.setStatus(BackgroundJobPOJO._COMPLETED_); 
	    } else if (ws.getStatus().equals(BackgroundJobStatusType.RUNNING)) {
	    	pojo.setStatus(BackgroundJobPOJO._RUNNING_); 
	    } else if (ws.getStatus().equals(BackgroundJobStatusType.SCHEDULED)) {
	    	pojo.setStatus(BackgroundJobPOJO._SCHEDULED_);
	    } else if (ws.getStatus().equals(BackgroundJobStatusType.STOPPED)) {
	    	pojo.setStatus(BackgroundJobPOJO._STOPPED_);
	    } else if (ws.getStatus().equals(BackgroundJobStatusType.SUSPENDED)) {
	    	pojo.setStatus(BackgroundJobPOJO._SUSPENDED_);
	    }
	    pojo.setWsPipeline(ws.getPipeline());
	    //we do not rewrite the pipeline
		return pojo;
	}		

	protected WSRoutingOrderV2PK POJO2WS(AbstractRoutingOrderV2POJOPK pojo) throws Exception{
		if (pojo==null) return null;
		try {
		    WSRoutingOrderV2PK ws = new WSRoutingOrderV2PK();
		    ws.setName(pojo.getName());
		    switch(pojo.getStatus()) {
		    	case AbstractRoutingOrderV2POJO.ACTIVE:
		    		ws.setStatus(WSRoutingOrderV2Status.ACTIVE);
		    		break;
		    	case AbstractRoutingOrderV2POJO.COMPLETED:
		    		ws.setStatus(WSRoutingOrderV2Status.COMPLETED);
		    		break;
		    	case AbstractRoutingOrderV2POJO.FAILED:
		    		ws.setStatus(WSRoutingOrderV2Status.FAILED);
		    		break;
		    }	
		    return ws;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw(e);
		}
	}	    
	
	
	protected AbstractRoutingOrderV2POJOPK WS2POJO(WSRoutingOrderV2PK s) throws Exception{
		if (s==null) return null;
		try {			
		    AbstractRoutingOrderV2POJOPK pojo = null;
		    if (s.getStatus().equals(WSRoutingOrderV2Status.ACTIVE)) {
		    	pojo = new ActiveRoutingOrderV2POJOPK(s.getName());
		    } else if (s.getStatus().equals(WSRoutingOrderV2Status.COMPLETED)) {
		    	pojo = new CompletedRoutingOrderV2POJOPK(s.getName());
		    } else if (s.getStatus().equals(WSRoutingOrderV2Status.FAILED)) {
		    	pojo = new FailedRoutingOrderV2POJOPK(s.getName());
		    }  
			return pojo;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw(e);
		}
	}
	
	protected WSRoutingOrderV2 POJO2WS(AbstractRoutingOrderV2POJO pojo) throws Exception{
		if (pojo==null) return null;
		try {
		    WSRoutingOrderV2 ws = new WSRoutingOrderV2();
		    ws.setMessage(pojo.getMessage());
		    ws.setName(pojo.getName());
		    ws.setServiceJNDI(pojo.getServiceJNDI());
		    ws.setServiceParameters(pojo.getServiceParameters());
		    switch(pojo.getStatus()) {
		    	case AbstractRoutingOrderV2POJO.ACTIVE:
		    		ws.setStatus(WSRoutingOrderV2Status.ACTIVE);
		    		break;
		    	case AbstractRoutingOrderV2POJO.COMPLETED:
		    		ws.setStatus(WSRoutingOrderV2Status.COMPLETED);
		    		break;
		    	case AbstractRoutingOrderV2POJO.FAILED:
		    		ws.setStatus(WSRoutingOrderV2Status.FAILED);
		    		break;
		    }		    
		    ws.setTimeCreated(pojo.getTimeCreated());
		    ws.setTimeLastRunCompleted(pojo.getTimeLastRunCompleted());
		    ws.setTimeLastRunStarted(pojo.getTimeLastRunStarted());
		    ws.setTimeScheduled(pojo.getTimeScheduled());
		    ws.setWsItemPK(POJO2WS(pojo.getItemPOJOPK()));
		    ws.setBindingUniverseName(pojo.getBindingUniverseName());
		    ws.setBindingUserToken(pojo.getBindingUserToken());
			return ws;
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw(e);
		}
	}
	protected WSUniverse POJO2WS(UniversePOJO universePOJO) throws Exception{
		WSUniverse ws = new WSUniverse();
		ws.setName(universePOJO.getName());
		ws.setDescription(universePOJO.getDescription());
		//objects
		Set<String> objectTypes = universePOJO.getXtentisObjectsRevisionIDs().keySet();
		ArrayList<WSUniverseXtentisObjectsRevisionIDs> wsObjectsToRevisionIDs = new ArrayList<WSUniverseXtentisObjectsRevisionIDs>();
		for (Iterator<String> iter = objectTypes.iterator(); iter.hasNext(); ) {
			String objectType = iter.next();
			String revisionID = universePOJO.getXtentisObjectsRevisionIDs().get(objectType);
			wsObjectsToRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs(objectType, revisionID));
		}
		ws.setXtentisObjectsRevisionIDs(wsObjectsToRevisionIDs.toArray(new WSUniverseXtentisObjectsRevisionIDs[wsObjectsToRevisionIDs.size()]));
		//default items
		ws.setDefaultItemsRevisionID(universePOJO.getDefaultItemRevisionID());
		//items
		Set<String> patterns = universePOJO.getItemsRevisionIDs().keySet();
		ArrayList<WSUniverseItemsRevisionIDs> wsItemsToRevisionIDs = new ArrayList<WSUniverseItemsRevisionIDs>();
		for (Iterator<String> iter = patterns.iterator(); iter.hasNext(); ) {
			String pattern = iter.next();
			String revisionID = universePOJO.getItemsRevisionIDs().get(pattern);
			wsItemsToRevisionIDs.add(new WSUniverseItemsRevisionIDs(pattern, revisionID));
		}
		ws.setItemsRevisionIDs(wsItemsToRevisionIDs.toArray(new WSUniverseItemsRevisionIDs[wsItemsToRevisionIDs.size()]));
		return ws;
	}

	
	
	protected UniversePOJO WS2POJO(WSUniverse wsUniverse) throws Exception{
		UniversePOJO pojo = new UniversePOJO();
		pojo.setName(wsUniverse.getName());
		pojo.setDescription(wsUniverse.getDescription());
		//Xtentis Objects
		HashMap<String,String> xtentisObjectsRevisionIDs = new HashMap<String, String>();
		if (wsUniverse.getXtentisObjectsRevisionIDs()!=null) {
			for (int i = 0; i < wsUniverse.getXtentisObjectsRevisionIDs().length; i++) {
				xtentisObjectsRevisionIDs.put(
					wsUniverse.getXtentisObjectsRevisionIDs()[i].getXtentisObjectName(),
					wsUniverse.getXtentisObjectsRevisionIDs()[i].getRevisionID()
				);
			}//for specifications
		}
		pojo.setXtentisObjectsRevisionIDs(xtentisObjectsRevisionIDs);
		//Default Items
		pojo.setDefaultItemRevisionID(wsUniverse.getDefaultItemsRevisionID());
		//Items
		LinkedHashMap<String,String> itemRevisionIDs = new LinkedHashMap<String, String>();
		if (wsUniverse.getItemsRevisionIDs()!=null) {
			for (int i = 0; i < wsUniverse.getItemsRevisionIDs().length; i++) {
				itemRevisionIDs.put(
					wsUniverse.getItemsRevisionIDs()[i].getConceptPattern(),
					wsUniverse.getItemsRevisionIDs()[i].getRevisionID()
				);
			}//for specifications
		}
		pojo.setItemsRevisionIDs(itemRevisionIDs);
		return pojo;
	}
	protected WSSynchronizationPlan POJO2WS(SynchronizationPlanPOJO synchronizationPlanPOJO) throws Exception{
		WSSynchronizationPlan ws = new WSSynchronizationPlan();
		ws.setName(synchronizationPlanPOJO.getName());
		ws.setDescription(synchronizationPlanPOJO.getDescription());
		ws.setRemoteSystemName(synchronizationPlanPOJO.getRemoteSystemName());
		ws.setRemoteSystemURL(synchronizationPlanPOJO.getRemoteSystemURL());
		ws.setRemoteSystemUsername(synchronizationPlanPOJO.getRemoteSystemUsername());
		ws.setRemoteSystemPassword(synchronizationPlanPOJO.getRemoteSystemPassword());
		ws.setTisURL(synchronizationPlanPOJO.getTisURL());
		ws.setTisUsername(synchronizationPlanPOJO.getTisUsername());
		ws.setTisPassword(synchronizationPlanPOJO.getTisPassword());
		ws.setTisParameters(synchronizationPlanPOJO.getTisParameters());
		//objects
		Set<String> objectNames = synchronizationPlanPOJO.getXtentisObjectsSynchronizations().keySet();
		ArrayList<WSSynchronizationPlanXtentisObjectsSynchronizations> wsObjectsSynchroTables = new ArrayList<WSSynchronizationPlanXtentisObjectsSynchronizations>();
		for (Iterator<String> iter = objectNames.iterator(); iter.hasNext(); ) {
			String objectType = iter.next();
			
			ArrayListHolder<SynchronizationPlanObjectLine> linesMap = synchronizationPlanPOJO.getXtentisObjectsSynchronizations().get(objectType);
			ArrayList<WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations> wsLines = new ArrayList<WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations>();
			for (Iterator<SynchronizationPlanObjectLine> iterator2 = linesMap.getList().iterator(); iterator2.hasNext(); ) {
				SynchronizationPlanObjectLine line = iterator2.next();
				wsLines.add(new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations(
					line.getInstancePattern(), 
					line.getSourceRevisionID(), 
					line.getDestinationRevisionID(), 
					line.getAlgorithm()
				));
			}
			wsObjectsSynchroTables.add(new WSSynchronizationPlanXtentisObjectsSynchronizations(
				objectType,
				wsLines.toArray(new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[wsLines.size()])
			));
		}
		ws.setXtentisObjectsSynchronizations(wsObjectsSynchroTables.toArray(new WSSynchronizationPlanXtentisObjectsSynchronizations[wsObjectsSynchroTables.size()]));
		
		//items
		ArrayList<WSSynchronizationPlanItemsSynchronizations> wsItemsSynchroTable = new ArrayList<WSSynchronizationPlanItemsSynchronizations>();
		for (Iterator<SynchronizationPlanItemLine> iter = synchronizationPlanPOJO.getItemsSynchronizations().iterator(); iter.hasNext(); ) {
			SynchronizationPlanItemLine line = iter.next();
			wsItemsSynchroTable.add(new WSSynchronizationPlanItemsSynchronizations(
				line.getConceptName(),
				line.getIdsPattern(),
				line.getLocalClusterPOJOPK().getUniqueId(),
				line.getLocalRevisionID(),
				line.getRemoteClusterPOJOPK().getUniqueId(),
				line.getRemoteRevisionID(),
				line.getAlgorithm()
			));
		}
		ws.setItemsSynchronizations(wsItemsSynchroTable.toArray(new WSSynchronizationPlanItemsSynchronizations[wsItemsSynchroTable.size()]));
		
		//Current statuses are obtained using action(GET_STATUS)
//		Calendar lastRunStartedCalendar = Calendar.getInstance();
//		lastRunStartedCalendar.setTimeInMillis(synchronizationPlanPOJO.getLastRunStarted());
//		ws.setLastRunStarted(lastRunStartedCalendar);
//		
//		Calendar lastRunStoppedCalendar = Calendar.getInstance();
//		lastRunStoppedCalendar.setTimeInMillis(synchronizationPlanPOJO.getLastRunStopped());
//		ws.setLastRunStopped(lastRunStoppedCalendar);
//		
//		String statusCode  = synchronizationPlanPOJO.getCurrentStatusCode();
//		WSSynchronizationPlanStatusCode wsStatusCode = null;
//		if (SynchronizationPlanPOJO.STATUS_COMPLETED.equals(statusCode)) {
//			wsStatusCode = WSSynchronizationPlanStatusCode.COMPLETED;
//		} else if (SynchronizationPlanPOJO.STATUS_FAILED.equals(statusCode)) {
//			wsStatusCode = WSSynchronizationPlanStatusCode.FAILED;
//		} else if (SynchronizationPlanPOJO.STATUS_RUNNING.equals(statusCode)) {
//			wsStatusCode = WSSynchronizationPlanStatusCode.RUNNING;
//		} else if (SynchronizationPlanPOJO.STATUS_SCHEDULED.equals(statusCode)) {
//			wsStatusCode = WSSynchronizationPlanStatusCode.SCHEDULED;
//		} else if (SynchronizationPlanPOJO.STATUS_STOPPING.equals(statusCode)) {
//			wsStatusCode = WSSynchronizationPlanStatusCode.STOPPING;
//		}
//		
//		ws.setWsCurrentStatusCode(wsStatusCode);
//		ws.setCurrentStatusMessage(synchronizationPlanPOJO.getCurrentStatusMessage());
		
		return ws;
	}

	
	
	protected SynchronizationPlanPOJO WS2POJO(WSSynchronizationPlan wsSynchronizationPlan) throws Exception{
		SynchronizationPlanPOJO pojo = new SynchronizationPlanPOJO();
		pojo.setName(wsSynchronizationPlan.getName());
		pojo.setDescription(wsSynchronizationPlan.getDescription());
		pojo.setRemoteSystemName(wsSynchronizationPlan.getRemoteSystemName());
		pojo.setRemoteSystemURL(wsSynchronizationPlan.getRemoteSystemURL());
		pojo.setRemoteSystemUsername(wsSynchronizationPlan.getRemoteSystemUsername());
		pojo.setRemoteSystemPassword(wsSynchronizationPlan.getRemoteSystemPassword());
		pojo.setTisURL(wsSynchronizationPlan.getTisURL());
		pojo.setTisUsername(wsSynchronizationPlan.getTisUsername());
		pojo.setTisPassword(wsSynchronizationPlan.getTisPassword());
		pojo.setTisParameters(wsSynchronizationPlan.getTisParameters());
		
		//Xtentis Objects
		HashMap<String, ArrayListHolder<SynchronizationPlanObjectLine>> xtentisObjectsSynchronizations = new HashMap<String, ArrayListHolder<SynchronizationPlanObjectLine>>();
		WSSynchronizationPlanXtentisObjectsSynchronizations[] wsTables = wsSynchronizationPlan.getXtentisObjectsSynchronizations();
		if (wsTables != null) {
			for (int i = 0; i < wsTables.length; i++) {
				WSSynchronizationPlanXtentisObjectsSynchronizations wsTable = wsTables[i];
				ArrayListHolder<SynchronizationPlanObjectLine> objectLines = new ArrayListHolder<SynchronizationPlanObjectLine>();
				WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[] wsSynchronizations = wsTable.getSynchronizations();
				if (wsSynchronizations != null) {
					for (int j = 0; j < wsSynchronizations.length; j++) {
						objectLines.getList().add(
							new SynchronizationPlanObjectLine(
								wsSynchronizations[j].getInstancePattern(), 
								wsSynchronizations[j].getLocalRevisionID(),
								wsSynchronizations[j].getRemoteRevisionID(),
								wsSynchronizations[j].getAlgorithm()
							)
						);
					}
				}
				xtentisObjectsSynchronizations.put(wsTable.getXtentisObjectName(), objectLines);
			}
		}
		pojo.setXtentisObjectsSynchronizations(xtentisObjectsSynchronizations);
		
		//Items
		ArrayList<SynchronizationPlanItemLine> patternsMap = new ArrayList<SynchronizationPlanItemLine>();
		WSSynchronizationPlanItemsSynchronizations[] wsSynchronizations = wsSynchronizationPlan.getItemsSynchronizations();
		if (wsSynchronizations != null) {
			for (int j = 0; j < wsSynchronizations.length; j++) {
				patternsMap.add(
					new SynchronizationPlanItemLine(
						wsSynchronizations[j].getConceptName(),
						wsSynchronizations[j].getIdsPattern(),
						new DataClusterPOJOPK(wsSynchronizations[j].getLocalCluster()),
						wsSynchronizations[j].getLocalRevisionID(),
						new DataClusterPOJOPK(wsSynchronizations[j].getRemoteCluster()),
						wsSynchronizations[j].getRemoteRevisionID(),
						wsSynchronizations[j].getAlgorithm()
					)
				);
			}
		}
		pojo.setItemsSynchronizations(patternsMap);
		
		
		return pojo;
	}
	protected WSSynchronizationItem POJO2WS(SynchronizationItemPOJO synchronizationItemPOJO) throws Exception{
		WSSynchronizationItem ws = new WSSynchronizationItem();
		ws.setLastRunPlan(synchronizationItemPOJO.getLastRunPlan());
		ws.setLocalRevisionID(synchronizationItemPOJO.getLocalRevisionID());
		ws.setResolvedProjection(synchronizationItemPOJO.getResolvedProjection());
		ws.setWsItemPK(POJO2WS(synchronizationItemPOJO.getItemPOJOPK()));

		switch (synchronizationItemPOJO.getStatus()) {
			case SynchronizationItemPOJO.STATUS_EXECUTED:
				ws.setStatus(WSSynchronizationItemStatus.EXECUTED);
				break;
			case SynchronizationItemPOJO.STATUS_MANUAL:
				ws.setStatus(WSSynchronizationItemStatus.MANUAL);
				break;
			case SynchronizationItemPOJO.STATUS_PENDING:
				ws.setStatus(WSSynchronizationItemStatus.PENDING);
				break;
			case SynchronizationItemPOJO.STATUS_RESOLVED:
				ws.setStatus(WSSynchronizationItemStatus.RESOLVED);
				break;
		}
		

		//remote instances
		ArrayList<WSSynchronizationItemRemoteInstances> wsInstances = new ArrayList<WSSynchronizationItemRemoteInstances>();
		for (SynchronizationRemoteInstance instance : synchronizationItemPOJO.getRemoteIntances().values()) {
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(instance.getLastLocalSynchronizationTime());
			WSSynchronizationItemRemoteInstances wsInstance = new WSSynchronizationItemRemoteInstances(
				instance.getRemoteSystemName(),
				instance.getRevisionID(),
				instance.getXml(),
				cal
			);
			wsInstances.add(wsInstance);
		}
		ws.setRemoteInstances(wsInstances.toArray(new WSSynchronizationItemRemoteInstances[wsInstances.size()]));
				
		return ws;
	}

	
	
	protected SynchronizationItemPOJO WS2POJO(WSSynchronizationItem wsSynchronizationItem) throws Exception{
		SynchronizationItemPOJO pojo = new SynchronizationItemPOJO();
		pojo.setItemPOJOPK(WS2POJO(wsSynchronizationItem.getWsItemPK()));
		pojo.setLastRunPlan(wsSynchronizationItem.getLastRunPlan());
		pojo.setLocalRevisionID(wsSynchronizationItem.getLocalRevisionID());
		pojo.setResolvedProjection(wsSynchronizationItem.getResolvedProjection());
		
		if (WSSynchronizationItemStatus.EXECUTED.equals(wsSynchronizationItem.getStatus())) {
			pojo.setStatus(SynchronizationItemPOJO.STATUS_EXECUTED);
		} else if (WSSynchronizationItemStatus.MANUAL.equals(wsSynchronizationItem.getStatus())) {
			pojo.setStatus(SynchronizationItemPOJO.STATUS_MANUAL);
		} else if (WSSynchronizationItemStatus.PENDING.equals(wsSynchronizationItem.getStatus())) {
			pojo.setStatus(SynchronizationItemPOJO.STATUS_PENDING);
		} else if (WSSynchronizationItemStatus.RESOLVED.equals(wsSynchronizationItem.getStatus())) {
			pojo.setStatus(SynchronizationItemPOJO.STATUS_RESOLVED);
		} 

		HashMap<String,SynchronizationRemoteInstance> instances = new HashMap<String,SynchronizationRemoteInstance>();
		WSSynchronizationItemRemoteInstances[] wsInstances = wsSynchronizationItem.getRemoteInstances();
		if (wsInstances != null) {
			for (int i = 0; i < wsInstances.length; i++) {
				SynchronizationRemoteInstance instance = new SynchronizationRemoteInstance(
					wsInstances[i].getRemoteSystemName(),
					wsInstances[i].getRemoteRevisionID(),
					wsInstances[i].getXml(),
					wsInstances[i].getLastLocalSynchronizationTime().getTimeInMillis()
				);
				instances.put(instance.getKey(), instance);
			}
		}
		pojo.setRemoteIntances(instances);
		return pojo;
	}



	
	protected WSPipeline POJO2WSOLD(HashMap<String,com.amalto.core.util.TypedContent> pipeline) throws Exception{
		ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
		Set keys = pipeline.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
			String output = (String) iter.next();
			com.amalto.core.util.TypedContent content = pipeline.get(output);
			byte[] bytes = content.getBytes();
			if (bytes == null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int c;
				while ((c=content.getStream().read())!=-1) bos.write(c);
				bytes = bos.toByteArray();
			}
			WSExtractedContent wsContent = new WSExtractedContent(
					new WSByteArray(bytes),
					content.getContentType()
			);
			WSPipelineTypedContentEntry wsEntry = new WSPipelineTypedContentEntry(
					TransformerCtrlBean.DEFAULT_VARIABLE.equals(output) ? "" : output,
					wsContent
			);
			entries.add(wsEntry);
		}
		return new WSPipeline(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
	}
	
	protected WSDroppedItemPK POJO2WS(DroppedItemPOJOPK droppedItemPOJOPK) throws Exception{
		ItemPOJOPK refItemPOJOPK=droppedItemPOJOPK.getRefItemPOJOPK();
		return new WSDroppedItemPK(
				POJO2WS(refItemPOJOPK),
				droppedItemPOJOPK.getPartPath(),
				droppedItemPOJOPK.getRevisionId()
		);
		 
	}
	
	protected DroppedItemPOJOPK WS2POJO(WSDroppedItemPK wsDroppedItemPK) throws Exception{
		ItemPOJOPK refItemPOJOPK = WS2POJO(wsDroppedItemPK.getWsItemPK());
		return new DroppedItemPOJOPK(
				wsDroppedItemPK.getRevisionId(),
				refItemPOJOPK,
				wsDroppedItemPK.getPartPath()
		);
	}
	
	protected WSDroppedItem POJO2WS(DroppedItemPOJO droppedItemPOJO) throws Exception{

		WSDroppedItem wsDroppedItem=new WSDroppedItem(droppedItemPOJO.getRevisionID(),
				                                      new WSDataClusterPK(droppedItemPOJO.getDataClusterPOJOPK().getUniqueId()),
				                                      droppedItemPOJO.getUniqueId(),
				                                      droppedItemPOJO.getConceptName(),
				                                      droppedItemPOJO.getIds(),
				                                      droppedItemPOJO.getPartPath(),
				                                      droppedItemPOJO.getInsertionUserName(),
				                                      droppedItemPOJO.getInsertionTime(),
				                                      droppedItemPOJO.getProjection());
				                                      
		return wsDroppedItem;
		 
	}
	protected WSTransformerPluginV2VariableDescriptor POJO2WS(TransformerPluginVariableDescriptor descriptor) throws Exception{
		WSTransformerPluginV2VariableDescriptor wsDescriptor = new WSTransformerPluginV2VariableDescriptor();
		wsDescriptor.setVariableName(descriptor.getVariableName());
		if (descriptor.getDescriptions().size()>0)
			wsDescriptor.setDescription(descriptor.getDescriptions().values().iterator().next());
		wsDescriptor.setMandatory(descriptor.isMandatory());
		ArrayList<String> contentTypesRegex = new ArrayList<String>();
		if (descriptor.getContentTypesRegex()!=null) {
			for (Iterator iterator = descriptor.getContentTypesRegex().iterator(); iterator.hasNext(); ) {
				Pattern p = (Pattern) iterator.next();
				contentTypesRegex.add(p.toString());
			}
		}
		wsDescriptor.setContentTypesRegex(contentTypesRegex.toArray(new String[contentTypesRegex.size()]));
		ArrayList<String> possibleValuesRegex = new ArrayList<String>();
		if (descriptor.getPossibleValuesRegex()!=null) {
			for (Iterator iterator = descriptor.getPossibleValuesRegex().iterator(); iterator.hasNext(); ) {
				Pattern p = (Pattern) iterator.next();
				possibleValuesRegex.add(p.toString());
			}
		}
		wsDescriptor.setPossibleValuesRegex(possibleValuesRegex.toArray(new String[possibleValuesRegex.size()]));
		return wsDescriptor;
	}

	
	/******************Schema**********************/
	
	public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
		try {
		    return new WSString(
					Util.getDataModelCtrlLocal().checkSchema(wsSchema.getSchema())
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString deleteBusinessConcept(
			WSDeleteBusinessConcept wsDeleteBusinessConcept)
			throws RemoteException {
		try {
		    return new WSString(
					Util.getDataModelCtrlLocal().deleteBusinessConcept(
							new DataModelPOJOPK(wsDeleteBusinessConcept.getWsDataModelPK().getPk()),
							wsDeleteBusinessConcept.getBusinessConceptName()
					 )
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSConceptKey getBusinessConceptKey(
			WSGetBusinessConceptKey wsGetBusinessConceptKey)
			throws RemoteException {
		try {
			String schema = 
				Util.getDataModelCtrlLocal().getDataModel(
						new DataModelPOJOPK(wsGetBusinessConceptKey.getWsDataModelPK().getPk())
				).getSchema();

			XSDKey xsdKey = Util.getBusinessConceptKey(
					Util.parse(schema),
					wsGetBusinessConceptKey.getConcept()
			);
			return
				new WSConceptKey(
						xsdKey.getSelector(),
						xsdKey.getFields()
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray getBusinessConcepts(
			WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
		try {
		    return new WSStringArray(
					Util.getDataModelCtrlLocal().getAllBusinessConceptsNames(
							new DataModelPOJOPK(wsGetBusinessConcepts.getWsDataModelPK().getPk())
					 )
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept)
			throws RemoteException {
		   WSBusinessConcept bc = wsPutBusinessConcept.getBusinessConcept();
			try {
			    String s = 
			    "<xsd:element name="+bc.getName()+" type="+bc.getBusinessTemplate()+">"+
			    "	<xsd:annotation>";
			    WSI18NString[] labels = bc.getWsLabel();
			    for (int i = 0; i < labels.length; i++) {
			        s+="<xsd:appinfo source=\""+labels[i].getLanguage().getValue()+"\">"+labels[i].getLabel()+"</xsd:appinfo>";
	            }
			    WSI18NString[] docs = bc.getWsDescription();
			    for (int i = 0; i < docs.length; i++) {
			        s+="<xsd:documentation xml:lang=\""+docs[i].getLanguage().getValue()+"\">"+docs[i].getLabel()+"</xsd:documentation>";
	            }
			    s+=
			    "	</xsd:annotation>"+
			    "	<xsd:unique name=\""+bc.getName()+"\">"+
			    "		<xsd:selector xpath=\""+bc.getWsUniqueKey().getSelectorpath()+"\"/>";
			    for (int i = 0; i < bc.getWsUniqueKey().getFieldpath().length; i++) {
				    s+="<xsd:field xpath=\""+bc.getWsUniqueKey().getFieldpath()[i]+"\"/>";	
	            }
			    s+=
			    "	</xsd:unique>"+
			    "</xsd:element>";
			    return new WSString(
						Util.getDataModelCtrlLocal().putBusinessConceptSchema(
						        new DataModelPOJOPK(wsPutBusinessConcept.getWsDataModelPK().getPk()),
						        s
						 )
					);
			} catch (Exception e) {
				throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			}
	}

	public WSString putBusinessConceptSchema(
			WSPutBusinessConceptSchema wsPutBusinessConceptSchema)
			throws RemoteException {
		try {
		    return new WSString(
					Util.getDataModelCtrlLocal().putBusinessConceptSchema(
					        new DataModelPOJOPK(wsPutBusinessConceptSchema.getWsDataModelPK().getPk()),
					        wsPutBusinessConceptSchema.getBusinessConceptSchema()
					 )
				);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/***************************************************************************
	 * DataCluster
	 * **************************************************************************/	
	
	protected WSDataCluster VO2WS(DataClusterPOJO vo) {
	    WSDataCluster s = new WSDataCluster();
		s.setDescription(vo.getDescription());
		s.setName(vo.getName());
		s.setVocabulary(vo.getVocabulary());
		return s;
	}
	
	protected DataClusterPOJO WS2VO(WSDataCluster ws) throws Exception{
		DataClusterPOJO vo = new DataClusterPOJO();
		vo.setName(ws.getName());
		vo.setDescription(ws.getDescription());
		vo.setVocabulary("");
		return vo;
	}

	public WSDataClusterPK deleteDataCluster(
			WSDeleteDataCluster wsDeleteDataCluster) throws RemoteException {
		try {
		    return new WSDataClusterPK(
					Util.getDataClusterCtrlLocal().removeDataCluster(
							new DataClusterPOJOPK(wsDeleteDataCluster.getWsDataClusterPK().getPk())
					).getUniqueId()
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSBoolean existsDBDataCluster(
			WSExistsDBDataCluster wsExistsDataCluster) throws RemoteException {
		try {
			   
			String[] ids=Util.getXmlServerCtrlLocal().getAllClusters(wsExistsDataCluster.getRevisionID());
			List<String> list=new ArrayList<String>();
			return new WSBoolean(Arrays.asList(ids).contains(wsExistsDataCluster.getName()));
			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster)
			throws RemoteException {
		try {
		    return new WSBoolean( 
					Util.getDataClusterCtrlLocal().existsDataCluster(
							new DataClusterPOJOPK(wsExistsDataCluster.getWsDataClusterPK().getPk())
					) != null
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray getConceptsInDataCluster(
			WSGetConceptsInDataCluster wsGetConceptsInDataCluster)
			throws RemoteException {
		try {
			
			Set<String> concepts = Util.getItemCtrl2Local().getConceptsInDataCluster(
				new DataClusterPOJOPK(wsGetConceptsInDataCluster.getWsDataClusterPK().getPk())
			).keySet();
			
	 		return new WSStringArray(concepts.toArray(new String[concepts.size()]));

		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(
			WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions)
			throws RemoteException {
		try {
		    UniversePOJO pojo=null;
		    
		    if(wsGetConceptsInDataClusterWithRevisions==null||
		       wsGetConceptsInDataClusterWithRevisions.getUniversePK()==null||
		       wsGetConceptsInDataClusterWithRevisions.getUniversePK().getPk()==null||
		       wsGetConceptsInDataClusterWithRevisions.getUniversePK().getPk().equals("")){
		    	pojo=new UniversePOJO();//default head revision
		    }else{
		    	//get universe
				UniverseCtrlLocal ctrl = UniverseCtrlUtil.getLocalHome().create();
			    pojo=
					ctrl.getUniverse(
						new UniversePOJOPK(
								 wsGetConceptsInDataClusterWithRevisions.getUniversePK().getPk()
						)
					);
		    }
		    
		   //get conceptRevisions    
		   Map concepts = Util.getItemCtrl2Local().getConceptsInDataCluster(
					new DataClusterPOJOPK(wsGetConceptsInDataClusterWithRevisions.getDataClusterPOJOPK().getPk()),pojo
				);
		   
		   //convert
		   WSConceptRevisionMapMapEntry[] mapEntry=new WSConceptRevisionMapMapEntry[concepts.size()];
		   int i=0;
		   for (Iterator iterator = concepts.keySet().iterator(); iterator.hasNext();i++) {
			  String concept = (String) iterator.next();
			  String revisionId = (String) concepts.get(concept);
			  WSConceptRevisionMapMapEntry entry=new WSConceptRevisionMapMapEntry(concept,revisionId);
			  mapEntry[i]=entry;
		   }
		   WSConceptRevisionMap wsConceptRevisionMap=new WSConceptRevisionMap(mapEntry);
		   
		   return wsConceptRevisionMap;

	} catch (XtentisException e) {
		throw(new RemoteException(e.getLocalizedMessage()));			
	} catch (Exception e) {
		throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
	}
	}

	public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet)
			throws RemoteException {
		try {
		    return VO2WS( 
					Util.getDataClusterCtrlLocal().getDataCluster(
							new DataClusterPOJOPK(wsDataClusterGet.getWsDataClusterPK().getPk())
					)
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp)
			throws RemoteException {
		try {
		    WSDataClusterPKArray array = new WSDataClusterPKArray();
		    ArrayList<WSDataClusterPK> l = new ArrayList<WSDataClusterPK>();
		    Collection<DataClusterPOJOPK> vos = Util.getDataClusterCtrlLocal().getDataClusterPKs(regexp.getRegex());
		    for (Iterator iter = vos.iterator(); iter.hasNext(); ) {
				DataClusterPOJOPK pk = (DataClusterPOJOPK) iter.next();
				l.add(new WSDataClusterPK(pk.getUniqueId()));
            }
			array.setWsDataClusterPKs(l.toArray(new WSDataClusterPK[l.size()]));
			return array;
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster)
			throws RemoteException {
		try {
			Util.getXmlServerCtrlLocal().createCluster(wsDataCluster.getRevisionID(), wsDataCluster.getName());
			DataClusterPOJO pojo=new DataClusterPOJO(wsDataCluster.getName(),"","");				
			ObjectPOJOPK pk = pojo.store(wsDataCluster.getRevisionID());
			return new WSBoolean(true);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster)
			throws RemoteException {
		try {
		    return new WSDataClusterPK(
		    		Util.getDataClusterCtrlLocal().putDataCluster(
							WS2VO(wsDataCluster.getWsDataCluster())
					).getUniqueId()
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	
	/***************************************************************************
	 * View
	 * **************************************************************************/

    protected WSView VO2WS(ViewPOJO pojo) throws Exception{
	    WSView s = new WSView();
		s.setDescription(pojo.getDescription());
		s.setName(pojo.getName());

		String[] bes = null;
		Collection c = pojo.getSearchableBusinessElements().getList();
		if (c!=null) {		
			bes = new String[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				String be = (String) iter.next();
				bes[i++] = be;
			}
		}
		s.setSearchableBusinessElements(bes);

		c = pojo.getViewableBusinessElements().getList();
		if (c!=null) {
			bes = new String[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				String be = (String) iter.next();
				bes[i++] = be;
			}
		}
		s.setViewableBusinessElements(bes);

		c = pojo.getWhereConditions().getList();
		if (c!=null) {
			WSWhereCondition[] wcs = new WSWhereCondition[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				WhereCondition wh = (WhereCondition) iter.next();
				wcs[i++] = VO2WS(wh);
			}
			s.setWhereConditions(wcs);
		}

		return s;
	}
	

    
	protected ViewPOJO WS2VO(WSView ws) throws Exception{
		ViewPOJO pojo = new ViewPOJO();
		pojo.setName(ws.getName());
		pojo.setDescription(ws.getDescription());
				
		ArrayList l = new ArrayList();
	    String[] s = ws.getSearchableBusinessElements();
	    if (s!=null) {
		    for (int i = 0; i < s.length; i++) {
		    	l.add(ws.getSearchableBusinessElements()[i]);
			}
	    }
	    pojo.setSearchableBusinessElements(new ArrayListHolder(l));
	    
		l = new ArrayList();
	    s = ws.getViewableBusinessElements();
	    if (s!=null) {
		    for (int i = 0; i < s.length; i++) {
		    	l.add(ws.getViewableBusinessElements()[i]);
			}
	    }
	    pojo.setViewableBusinessElements(new ArrayListHolder(l));

		l = new ArrayList();
	    WSWhereCondition[] whs = ws.getWhereConditions();
	    if (whs!=null) {
		    for (int i = 0; i < whs.length; i++) {
		    	l.add(WS2VO(whs[i]));
			}
	    }
	    pojo.setWhereConditions(new ArrayListHolder(l));
	    
		return pojo;
	}
    
 
	
    protected WSWhereCondition VO2WS(WhereCondition vo) throws Exception{
    	WSWhereCondition ws = new WSWhereCondition();
    	WSWhereOperator op = WSWhereOperator.CONTAINS;
		String operator = vo.getOperator();
		if(operator.equals(WhereCondition.CONTAINS)) op=WSWhereOperator.CONTAINS; 
		else if(operator.equals(WhereCondition.STRICTCONTAINS)) op=WSWhereOperator.STRICTCONTAINS; 
		else if(operator.equals(WhereCondition.STARTSWITH)) op=WSWhereOperator.STARTSWITH;
		else if(operator.equals(WhereCondition.JOINS)) op=WSWhereOperator.JOIN;
		else if(operator.equals(WhereCondition.EQUALS)) op=WSWhereOperator.EQUALS;
		else if(operator.equals(WhereCondition.NOT_EQUALS))	op=WSWhereOperator.NOT_EQUALS;
		else if(operator.equals(WhereCondition.GREATER_THAN)) op=WSWhereOperator.GREATER_THAN;
		else if(operator.equals(WhereCondition.GREATER_THAN_OR_EQUAL)) op=WSWhereOperator.GREATER_THAN_OR_EQUAL;
		else if(operator.equals(WhereCondition.LOWER_THAN)) op=WSWhereOperator.LOWER_THAN;
		else if(operator.equals(WhereCondition.LOWER_THAN_OR_EQUAL)) op=WSWhereOperator.LOWER_THAN_OR_EQUAL;
		else if(operator.equals(WhereCondition.NO_OPERATOR)) op=WSWhereOperator.NO_OPERATOR;
		
		String predicate = vo.getStringPredicate();
		WSStringPredicate pr=WSStringPredicate.NONE;
		if ((predicate==null) || predicate.equals(WhereCondition.PRE_NONE)) pr=WSStringPredicate.NONE;
		else if (predicate.equals(WhereCondition.PRE_AND)) pr=WSStringPredicate.AND;
		else if (predicate.equals(WhereCondition.PRE_EXACTLY)) pr=WSStringPredicate.EXACTLY;
		else if (predicate.equals(WhereCondition.PRE_STRICTAND)) pr=WSStringPredicate.STRICTAND;
		else if (predicate.equals(WhereCondition.PRE_OR)) pr=WSStringPredicate.OR;
		else if (predicate.equals(WhereCondition.PRE_NOT)) pr=WSStringPredicate.NOT;

		ws.setLeftPath(vo.getLeftPath());
		ws.setOperator(op);
		ws.setRightValueOrPath(vo.getRightValueOrPath());
		ws.setStringPredicate(pr);
    	return ws;
    }
	
    
    protected IWhereItem WS2VO(WSWhereItem ws) throws Exception{
    	
    	if (ws==null) return null;
    	
    	if (ws.getWhereAnd() !=  null) {
    		WhereAnd wand = new WhereAnd();
    		WSWhereItem[] children = ws.getWhereAnd().getWhereItems();
    		if (children!=null) {
    			for (int i = 0; i < children.length; i++) {
					wand.add(WS2VO(children[i]));
				}
    		}
    		return wand;
    	} else if (ws.getWhereOr() !=  null) {
    		WhereOr wor = new WhereOr();
    		WSWhereItem[] children = ws.getWhereOr().getWhereItems();
    		if (children!=null) {
    			for (int i = 0; i < children.length; i++) {
					wor.add(WS2VO(children[i]));
				}
    		}
    		return wor;
    	} else if (ws.getWhereCondition() != null) {
    		return WS2VO(ws.getWhereCondition());
    	} else {
    		throw new IllegalArgumentException("The WSWhereItem mus have at least one child");
    	}
    }
	
	protected WhereCondition WS2VO(WSWhereCondition ws) throws Exception{
		
		String operator = WhereCondition.CONTAINS;
		if (ws.getOperator().equals(WSWhereOperator.CONTAINS)) {
			operator = WhereCondition.CONTAINS;
		} else	if (ws.getOperator().equals(WSWhereOperator.STRICTCONTAINS)) {
				operator = WhereCondition.STRICTCONTAINS;
		} else	if (ws.getOperator().equals(WSWhereOperator.STARTSWITH)) {
			operator = WhereCondition.STARTSWITH;
		} else	if (ws.getOperator().equals(WSWhereOperator.JOIN)) {
			operator = WhereCondition.JOINS;
		} else	if (ws.getOperator().equals(WSWhereOperator.EQUALS)) {
			operator = WhereCondition.EQUALS;
		} else	if (ws.getOperator().equals(WSWhereOperator.NOT_EQUALS)) {
				operator = WhereCondition.NOT_EQUALS;
		} else	if (ws.getOperator().equals(WSWhereOperator.GREATER_THAN)) {
			operator = WhereCondition.GREATER_THAN;
		} else	if (ws.getOperator().equals(WSWhereOperator.GREATER_THAN_OR_EQUAL)) {
			operator = WhereCondition.GREATER_THAN_OR_EQUAL;
		} else	if (ws.getOperator().equals(WSWhereOperator.LOWER_THAN)) {
			operator = WhereCondition.LOWER_THAN;
		} else	if (ws.getOperator().equals(WSWhereOperator.LOWER_THAN_OR_EQUAL)) {
			operator = WhereCondition.LOWER_THAN_OR_EQUAL;
		} else	if (ws.getOperator().equals(WSWhereOperator.NO_OPERATOR)) {
			operator = WhereCondition.NO_OPERATOR;
		}
		
		String predicate = WhereCondition.PRE_AND;
		if (ws.getStringPredicate().equals(WSStringPredicate.NONE)) {
			predicate = WhereCondition.PRE_NONE;
		} else	if (ws.getStringPredicate().equals(WSStringPredicate.AND)) {
			predicate = WhereCondition.PRE_AND;
		} else	if (ws.getStringPredicate().equals(WSStringPredicate.EXACTLY)) {
			predicate = WhereCondition.PRE_EXACTLY;
		} else	if (ws.getStringPredicate().equals(WSStringPredicate.STRICTAND)) {
			predicate = WhereCondition.PRE_STRICTAND;
		} else	if (ws.getStringPredicate().equals(WSStringPredicate.OR)) {
			predicate = WhereCondition.PRE_OR;
		} else	if (ws.getStringPredicate().equals(WSStringPredicate.NOT)) {
			predicate = WhereCondition.PRE_NOT;
		}
		
		return new WhereCondition(
			ws.getLeftPath(),
			operator,
			ws.getRightValueOrPath(),
			predicate,
			ws.isSpellCheck()
		);
	}

	public WSViewPK deleteView(WSDeleteView wsDeleteView)
			throws RemoteException {
		try {
		    return new WSViewPK(
					ViewCtrlUtil.getLocalHome().create().removeView(
							new ViewPOJOPK(wsDeleteView.getWsViewPK().getPk())
					).getIds()[0]
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSBoolean existsView(WSExistsView wsExistsView)
			throws RemoteException {
		try {
		    return new WSBoolean( 
					ViewCtrlUtil.getLocalHome().create().existsView(
							new ViewPOJOPK(wsExistsView.getWsViewPK().getPk())
					) != null
			);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSView getView(WSGetView wsViewGet) throws RemoteException {
		try {
		    return VO2WS( 
					ViewCtrlUtil.getLocalHome().create().getView(
							new ViewPOJOPK(wsViewGet.getWsViewPK().getPk())
					)
			);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
		try {
		    WSViewPKArray array = new WSViewPKArray();
		    String regex = (
		    		(regexp.getRegex()==null) || 
					("".equals(regexp.getRegex())) ||
					("*".equals(regexp.getRegex())) ?
							".*":regexp.getRegex()
			);
		    Collection<ViewPOJOPK> pks = ViewCtrlUtil.getLocalHome().create().getViewPKs(regex);
		    
		    ArrayList<WSViewPK> l = new ArrayList<WSViewPK>();
		    for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
				ViewPOJOPK pk = (ViewPOJOPK) iter.next();
				l.add(new WSViewPK(pk.getIds()[0]));
			}
			array.setWsViewPK(l.toArray(new WSViewPK[l.size()]));
			return array;
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSViewPK putView(WSPutView wsView) throws RemoteException {
		try {
		    return new WSViewPK(
					ViewCtrlUtil.getLocalHome().create().putView(
							WS2VO(wsView.getWsView())
					).getIds()[0]
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	/***************************************************************************
	 * Search
	 * **************************************************************************/
	
	protected LinkedHashMap WS2VO(WSLinkedHashMap wsLinkedHashMap) throws Exception{
		LinkedHashMap vo = new LinkedHashMap();
		WSGetItemsPivotIndexPivotWithKeysTypedContentEntry[] typedContentEntries=wsLinkedHashMap.getTypedContentEntry();
		for (int i = 0; i < typedContentEntries.length; i++) {
			WSGetItemsPivotIndexPivotWithKeysTypedContentEntry typedContentEntry=typedContentEntries[i];
			String key=typedContentEntry.getKey();
			String[] value=typedContentEntry.getValue().getStrings();
			vo.put(key, value);
		}
		return vo;
	}

	protected WSItemPK POJO2WS(ItemPOJOPK itemPK) throws Exception{
		return new WSItemPK(
				new WSDataClusterPK(itemPK.getDataClusterPOJOPK().getUniqueId()),
				itemPK.getConceptName(),
				itemPK.getIds()
		);
	}
	
	protected ItemPOJOPK WS2POJO(WSItemPK wsItemPK) throws Exception{
		return new ItemPOJOPK(
				new DataClusterPOJOPK(wsItemPK.getWsDataClusterPK().getPk()),
				wsItemPK.getConceptName(),
				wsItemPK.getIds()
		);
	}
	
	protected ItemPOJOPK[] WS2POJO(WSItemPK[] wsItemPKs) throws Exception{
		if(wsItemPKs==null){
			return null;
		}else{
			ItemPOJOPK[] itemPOJOPKs=new ItemPOJOPK[wsItemPKs.length];
			for (int i = 0; i < itemPOJOPKs.length; i++) {
				itemPOJOPKs[i]=WS2POJO(wsItemPKs[i]);
			}
			return itemPOJOPKs;
		}
	}
    
    /**
     * Serializes the object to an xml string
     * @return the xml string
     * 
     */
    protected String itemAsString(ItemPOJO iv) throws Exception{
    	
    	
        String item =
            "<businessconcept>" +
            "	<cluster>"+iv.getDataClusterPOJOPK().getUniqueId()+"</cluster>";
        	String[] ids = iv.getItemIds();
        	for (int i = 0; i < ids.length; i++) {
        		item+="	<id>"+ids[i]+"</id>";
			}
            item+=
            "	<lastmodifiedtime>"+iv.getInsertionTime()+"</lastmodifiedtime>";
            item+=        
            "	<projection>"+iv.getProjection()+"</projection>" ;
            item+=
            "</businessconcept>";
            
        return item;
    }	
	public WSString count(WSCount wsCount) throws RemoteException {
		try {
			long count = Util.getItemCtrl2Local().count(
				new DataClusterPOJOPK(wsCount.getWsDataClusterPK().getPk()),
				wsCount.getCountPath(),
				WS2VO(wsCount.getWhereItem()),
				wsCount.getSpellTreshold()
			);
			return new WSString(count+"");
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSBoolean existsItem(WSExistsItem wsExistsItem)
			throws RemoteException {
		try {
			return new WSBoolean(
				(Util.getItemCtrl2Local().existsItem(
						new ItemPOJOPK(
								new DataClusterPOJOPK(wsExistsItem.getWsItemPK().getWsDataClusterPK().getPk()),
								wsExistsItem.getWsItemPK().getConceptName(),
								wsExistsItem.getWsItemPK().getIds()
						)
				) != null)
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString getBusinessConceptValue(
			WSGetBusinessConceptValue wsGetBusinessConceptValue)
			throws RemoteException {
		try {
			ItemPOJO iv = Util.getItemCtrl2Local().getItem(
					new ItemPOJOPK(
							new DataClusterPOJOPK(wsGetBusinessConceptValue.getWsDataClusterPK().getPk()),
							wsGetBusinessConceptValue.getWsBusinessConceptPK().getConceptName(),
							wsGetBusinessConceptValue.getWsBusinessConceptPK().getIds()
							)
			);
			return new WSString(itemAsString(iv));

		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray getFullPathValues(
			WSGetFullPathValues wsGetFullPathValues) throws RemoteException {
		try {
			Collection res = Util.getItemCtrl2Local().getFullPathValues(
						new DataClusterPOJOPK(wsGetFullPathValues.getWsDataClusterPK().getPk()),
						wsGetFullPathValues.getFullPath(),
						WS2VO(wsGetFullPathValues.getWhereItem()),
						wsGetFullPathValues.getSpellThreshold(),
						wsGetFullPathValues.getOrderBy(),
						wsGetFullPathValues.getDirection()
				);

			if (res==null) return null;
			
		    return new WSStringArray((String[]) res.toArray(new String[res.size()]));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
		try {
			ItemPOJOPK pk =
				new ItemPOJOPK(
					new DataClusterPOJOPK(wsGetItem.getWsItemPK().getWsDataClusterPK().getPk()),
					wsGetItem.getWsItemPK().getConceptName(),
					wsGetItem.getWsItemPK().getIds()
				);

			ItemPOJO pojo = Util.getItemCtrl2Local().getItem(pk);
			
			return new WSItem(
					wsGetItem.getWsItemPK().getWsDataClusterPK(),
					pojo.getDataModelName(),
					pojo.getDataModelRevision(),
					wsGetItem.getWsItemPK().getConceptName(),
					wsGetItem.getWsItemPK().getIds(),
					pojo.getInsertionTime(),
					pojo.getProjectionAsString()
			);
		} catch (XtentisException e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSItemPKsByCriteriaResponse getItemPKsByCriteria(
			WSGetItemPKsByCriteria wsGetItemPKsByCriteria)
			throws RemoteException {
		
		//With Universe, this method must be reviewed since various concepts
		//may be store in various revisions
		
		try {
			
			String dataClusterName = wsGetItemPKsByCriteria.getWsDataClusterPK().getPk();
			
			//Check if user is allowed to read the cluster
			LocalUser user = LocalUser.getLocalUser();			
			boolean authorized = false;
	    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
	    		authorized = true;
	    	} else if (user.userCanRead(DataClusterPOJO.class, dataClusterName)) {
	    		authorized = true;
	    	}
	    	if (! authorized) {
	    		throw new RemoteException("Unauthorized read access on data cluster '"+dataClusterName+"' by user '"+user.getUsername()+"'");
	    	}
			
			//If not all concepts are store in the same revision, 
			//force the concept to be specified by the user. 
			//It would be too demanding to get all the concepts in all revisions (?)
			//The meat of this method should be ported to ItemCtrlBean
			String revisionID = null;
			if (wsGetItemPKsByCriteria.getConceptName() == null) {
    			if (user.getUniverse().getItemsRevisionIDs().size()>0) {
    				throw new RemoteException(
    					"User "+user.getUsername()+" is using items coming from multiple revisions." +
    					" In that particular case, the concept must be specified"
    				);
    			} else {
    				revisionID = user.getUniverse().getDefaultItemRevisionID();
    			}
			} else {
				revisionID = user.getUniverse().getConceptRevisionID(wsGetItemPKsByCriteria.getConceptName());
			}
			
			//FIXME: xQuery only
	 		String query = 
					"for $ii in /ii"+
					(wsGetItemPKsByCriteria.getContentKeywords() == null ? "": "[matches(./p , '"+wsGetItemPKsByCriteria.getContentKeywords()+"')]")+
					(wsGetItemPKsByCriteria.getFromDate().longValue()<=0 ? "" : "[./t >= "+wsGetItemPKsByCriteria.getFromDate().longValue()+"]")+
					(wsGetItemPKsByCriteria.getToDate().longValue()<=0 ? "" : "[./t <= "+wsGetItemPKsByCriteria.getToDate().longValue()+"]")+
					(wsGetItemPKsByCriteria.getKeysKeywords()==null ? "" : "[matches(./i , '"+wsGetItemPKsByCriteria.getKeysKeywords()+"')]")+
					(wsGetItemPKsByCriteria.getConceptName()==null ? "" : "[matches(./n , '"+wsGetItemPKsByCriteria.getConceptName()+"')]")+
					" return <r>{$ii/t}{$ii/n}<ids>{$ii/i}</ids></r>";
			
			DataClusterPOJOPK dcpk =	new DataClusterPOJOPK(dataClusterName);
			Collection<String> results = 
				Util.getItemCtrl2Local().runQuery(
					revisionID,
					dcpk,
					query,
					null
				);
			
						
			XPath xpath = XPathFactory.newInstance().newXPath();
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			
	 		WSItemPKsByCriteriaResponseResults[] res = new WSItemPKsByCriteriaResponseResults[results.size()];
	 		int i=0;
	 		for (Iterator iter = results.iterator(); iter.hasNext(); ) {
				String result = (String) iter.next();
//				result = _highlightLeft.matcher(result).replaceAll("");
//				result = _highlightRight.matcher(result).replaceAll("");
	 			Element r = documentBuilder.parse(new InputSource(new StringReader(result))).getDocumentElement();
	 			long t = new Long(xpath.evaluate("t",r)).longValue();
	 			String conceptName = xpath.evaluate("n",r);

	 			NodeList idsList = (NodeList)xpath.evaluate("./ids/i", r, XPathConstants.NODESET);
	 			String[] ids = new String[idsList.getLength()];
	 			for (int j = 0; j < idsList.getLength(); j++) {
					ids[j]= (idsList.item(j).getFirstChild() == null ? "" : idsList.item(j).getFirstChild().getNodeValue());  
				}
	 			res[i++] = new WSItemPKsByCriteriaResponseResults(
	 					t,
	 					new WSItemPK(
	 							wsGetItemPKsByCriteria.getWsDataClusterPK(),
	 							conceptName,
	 							ids
	 					)
	 			);
			}
 			return new WSItemPKsByCriteriaResponse(res);

		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
		try {
			Collection res = Util.getItemCtrl2Local().getItems(
					new DataClusterPOJOPK(wsGetItems.getWsDataClusterPK().getPk()), 
					wsGetItems.getConceptName(), 
					WS2VO(wsGetItems.getWhereItem()), 
					wsGetItems.getSpellTreshold(), 
					wsGetItems.getSkip(), 
					wsGetItems.getMaxItems()
			);
			return new WSStringArray((String[])res.toArray(new String[res.size()]));
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray getItemsPivotIndex(
			WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException {
		try {
			Collection res = Util.getItemCtrl2Local().getItemsPivotIndex(
					wsGetItemsPivotIndex.getClusterName(), 
					wsGetItemsPivotIndex.getMainPivotName(),
					WS2VO(wsGetItemsPivotIndex.getPivotWithKeys()), 
					wsGetItemsPivotIndex.getIndexPaths().getStrings(),
					WS2VO(wsGetItemsPivotIndex.getWhereItem()), 
					wsGetItemsPivotIndex.getPivotDirections()==null?null:wsGetItemsPivotIndex.getPivotDirections().getStrings(),
					wsGetItemsPivotIndex.getIndexDirections()==null?null:wsGetItemsPivotIndex.getIndexDirections().getStrings(), 
					wsGetItemsPivotIndex.getStart(), 
					wsGetItemsPivotIndex.getLimit()
			);
			return new WSStringArray((String[])res.toArray(new String[res.size()]));
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray quickSearch(WSQuickSearch wsQuickSearch)
			throws RemoteException {
		try {
			Collection c = Util.getItemCtrl2Local().quickSearch(
						new DataClusterPOJOPK(wsQuickSearch.getWsDataClusterPK().getPk()),
						new ViewPOJOPK(wsQuickSearch.getWsViewPK().getPk()),
						wsQuickSearch.getSearchedValue(),
						wsQuickSearch.isMatchAllWords(),
						wsQuickSearch.getSpellTreshold(),
						wsQuickSearch.getOrderBy(),
						wsQuickSearch.getDirection(),
						wsQuickSearch.getMaxItems(),
						wsQuickSearch.getSkip()
			);
			if (c==null) return null;
			return new WSStringArray((String[])c.toArray(new String[c.size()]));
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray viewSearch(WSViewSearch wsViewSearch)
			throws RemoteException {
		try {
			Collection res = Util.getItemCtrl2Local().viewSearch(
					new DataClusterPOJOPK(wsViewSearch.getWsDataClusterPK().getPk()),
					new ViewPOJOPK(wsViewSearch.getWsViewPK().getPk()),
					WS2VO(wsViewSearch.getWhereItem()),
					wsViewSearch.getSpellTreshold(),
					wsViewSearch.getOrderBy(),
					wsViewSearch.getDirection(),
					wsViewSearch.getSkip(),
					wsViewSearch.getMaxItems()
			);
			return new WSStringArray((String[])res.toArray(new String[res.size()]));
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch)
			throws RemoteException {
		try {
			Collection res = Util.getItemCtrl2Local().xPathsSearch(
					new DataClusterPOJOPK(wsXPathsSearch.getWsDataClusterPK().getPk()),
					wsXPathsSearch.getPivotPath(),
					new ArrayList<String>(Arrays.asList(wsXPathsSearch.getViewablePaths().getStrings())),
					WS2VO(wsXPathsSearch.getWhereItem()),
					wsXPathsSearch.getSpellTreshold(),
					wsXPathsSearch.getOrderBy(),
					wsXPathsSearch.getDirection(),
					wsXPathsSearch.getSkip(),
					wsXPathsSearch.getMaxItems()
			);
			return new WSStringArray((String[])res.toArray(new String[res.size()]));
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/***************************************************************************
	 *Put Item
	 * **************************************************************************/	
    protected WSItemPK putItem(WSPutItem wsPutItem,DataModelPOJO dataModel,Document schema,String[] itemKeyValues ) throws RemoteException {
    	
    	try{
		String projection = wsPutItem.getXmlString();
		
		Element root = Util.parse(projection).getDocumentElement();

		String concept = root.getLocalName();
		DataClusterPOJOPK dcpk = new DataClusterPOJOPK(wsPutItem.getWsDataClusterPK().getPk());
		//update the item using new field values see feature 0008854: Update an item instead of replace it 
		// load the item first if itemkey provided
		if(wsPutItem.getIsUpdate()){
			if(itemKeyValues.length>0){
				ItemPOJO pj=new ItemPOJO(
						dcpk,
						concept,
						itemKeyValues,
						System.currentTimeMillis(),
						projection
				);
				String revisionId=LocalUser.getLocalUser().getUniverse().getConceptRevisionID(concept);
				pj=ItemPOJO.load(revisionId, pj.getItemPOJOPK(),false);				
				if(pj!=null){// get the new projection
					// get updated path			
					Node old=pj.getProjection();
					Node newNode=root;					
					HashMap<String, UpdateReportItem> updatedPath=Util.compareElement("/"+old.getLocalName(), newNode, old);
					old=Util.updateElement("/"+old.getLocalName(), old, updatedPath);					
					String newProjection=Util.getXMLStringFromNode(old);
					projection = newProjection.replaceAll("<\\?xml.*?\\?>","");	
				}		
			}
		}else{
			//update the item according to datamodel 
			projection=Util.updateItem(concept, dataModel.getSchema(), root);
		}
		//end
		ItemPOJOPK itemPOJOPK =  
			Util.getItemCtrl2Local().putItem(
					new ItemPOJO(
							dcpk,
							concept,
							itemKeyValues,
							System.currentTimeMillis(),
							projection
					),
					dataModel
			);
		if (itemPOJOPK==null) return null;
		//aiming add if datacluster is 'PROVINIONING' and  current user,clear LocalUser cache
		if(XSystemObjects.DC_PROVISIONING.getName().equals(dataModel.getName()) && LocalUser.getLocalUser().getUsername().equals(itemKeyValues[0])){
			LocalUser.resetLocalUsers();
		}
		
		//update vocabulary
		//Util.getDataClusterCtrlLocal().getDataCluster(dcpk).addToVocabulary(projection);
		
		return new WSItemPK(
				new WSDataClusterPK(itemPOJOPK.getDataClusterPOJOPK().getUniqueId()),
				itemPOJOPK.getConceptName(),
				itemPOJOPK.getIds()
		);
	} catch (XtentisException e) {
		String err = "ERROR SYSTRACE: "+e.getMessage();
		org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
		throw(new RemoteException(e.getLocalizedMessage()));
	} catch (Exception e) {
		String err = "ERROR SYSTRACE: "+e.getMessage();
		org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
		throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
	}

    }    
	protected WSItemPK doPutItemWithCustomReport(
			com.amalto.core.webservice.WSPutItemWithReport wsPutItemWithReport,
			String customUserName
			)
			throws RemoteException {
		try {

			WSPutItem wsPutItem=wsPutItemWithReport.getWsPutItem();
			String source=wsPutItemWithReport.getSource();
			String operationType="";
			Map<String,UpdateReportItemPOJO> updateReportItemsMap=new HashMap<String, UpdateReportItemPOJO>();

			//before saving
			String projection = wsPutItem.getXmlString();
			Element root = Util.parse(projection).getDocumentElement();
			
			String concept = root.getLocalName();

			DataModelPOJO dataModel  = Util.getDataModelCtrlLocal().getDataModel(
						new DataModelPOJOPK(wsPutItem.getWsDataModelPK().getPk())
				);
			Document schema=Util.parse(dataModel.getSchema());
	        XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(
	        		schema,
					concept					
			);

			//get key values
			String[] ids = com.amalto.core.util.Util.getKeyValuesFromItem(
	   			root,
					conceptKey
			);				
			DataClusterPOJOPK dcpk = new DataClusterPOJOPK(wsPutItem.getWsDataClusterPK().getPk());
			ItemPOJOPK itemPOJOPK=new ItemPOJOPK(dcpk,concept, ids);	
			LocalUser user = LocalUser.getLocalUser();
			String userName="";
			if(customUserName!=null&&customUserName.length()>0){
				userName=customUserName;
			}else{
				userName=user.getUsername();
			}
			String revisionID ="";
			UniversePOJO universe = user.getUniverse();
            if(universe!=null){
            	revisionID=universe.getConceptRevisionID(concept);
            }
			//get operationType
			ItemPOJO itemPoJo=ItemPOJO.load(revisionID,itemPOJOPK);

			HashMap<String, UpdateReportItem> updatedPath=new HashMap<String, UpdateReportItem>();

			if(itemPoJo==null){
				operationType=UpdateReportPOJO.OPERATIONTYPE_CREATE;
			}else{
				operationType=UpdateReportPOJO.OPERATIONTYPE_UPDATEE;
				// get updated path			
				Element old=itemPoJo.getProjection();
				Element newNode=root;
				updatedPath=Util.compareElement("/"+old.getLocalName(), newNode, old);
				WSUpdateReportItemArray wsUpdateReportItemArray=new WSUpdateReportItemArray();
					for(Entry<String, UpdateReportItem> entry:updatedPath.entrySet()){
						UpdateReportItemPOJO pojo=new UpdateReportItemPOJO(entry.getValue().getPath(), entry.getValue().getOldValue(),entry.getValue().getNewValue());
						updateReportItemsMap.put(entry.getKey(), pojo);
				}		
			}

						
			String dataClusterPK = wsPutItem.getWsDataClusterPK().getPk();
	
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("[putItem-of-putItemWithReport] in dataCluster:"+dataClusterPK);
			WSItemPK wsi = putItem(wsPutItem, dataModel,schema, ids);	

			//create resultUpdateReport			
			String resultUpdateReport= Util.createUpdateReport(ids, concept, operationType, updatedPath, wsPutItem.getWsDataModelPK().getPk(), wsPutItem.getWsDataClusterPK().getPk());

			//invoke before saving
			if(wsPutItemWithReport.getInvokeBeforeSaving()){
				String err=Util.beforeSaving(concept, projection, resultUpdateReport);
				if(err!=null){
					err="execute beforeSaving ERROR:"+ err;
					org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
					throw new XtentisException(err);
				}
			}
			
			concept=wsi.getConceptName();
			ids=wsi.getIds();			
			//additional attributes for data changes log

            String dataModelPK = wsPutItem.getWsDataModelPK().getPk();
			if(updateReportItemsMap.size()>0){
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("[pushUpdateReport-of-putItemWithReport] with concept:"+concept+" operation:"+operationType);
				UpdateReportPOJO updateReportPOJO=new UpdateReportPOJO(concept, Util.joinStrings(ids, "."), operationType, source, System.currentTimeMillis(),dataClusterPK,dataModelPK,userName,revisionID,updateReportItemsMap);
				
				WSItemPK itemPK = putItem(
						new WSPutItem(
								new WSDataClusterPK("UpdateReport"), 
								updateReportPOJO.serialize(),
								new WSDataModelPK("UpdateReport"),false));			
				//routeItemV2(new WSRouteItemV2(itemPK));
				RoutingEngineV2CtrlLocal ctrl = Util.getRoutingEngineV2CtrlLocal();
				RoutingRulePOJOPK[] rules = ctrl.route(WS2POJO(itemPK));
			}

			return wsi;	

		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
		try {
			String projection = wsPutItem.getXmlString();
			
			Element root = Util.parse(projection).getDocumentElement();

			String concept = root.getLocalName();

			DataModelPOJO dataModel  = Util.getDataModelCtrlLocal().getDataModel(
						new DataModelPOJOPK(wsPutItem.getWsDataModelPK().getPk())
				);
			Document schema=Util.parse(dataModel.getSchema());
            XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(
            		schema,
					concept					
			);
           
			//get key values
			String[] itemKeyValues = com.amalto.core.util.Util.getKeyValuesFromItem(
       			root,
   				conceptKey
			);							
			//update the item using new field values see feature 0008854: Update an item instead of replace it 
			// load the item first if itemkey provided
			return putItem(wsPutItem, dataModel, schema, itemKeyValues);

		} catch (XtentisException e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray)
			throws RemoteException {
		WSPutItem[] items=wsPutItemArray.getWsPutItem();	
		List<WSItemPK> pks=new ArrayList<WSItemPK>();
		for(WSPutItem item: items){
			WSItemPK pk=putItem(item);
			pks.add(pk);
		}
		return new WSItemPKArray(pks.toArray(new WSItemPK[pks.size()]));
	}
	protected UpdateReportItemPOJO WS2POJO(WSUpdateReportItemPOJO wsUpdateReportItemPOJO) throws Exception{
		return new UpdateReportItemPOJO(
				wsUpdateReportItemPOJO.getPath(),
				wsUpdateReportItemPOJO.getOldValue(),
				wsUpdateReportItemPOJO.getNewValue()
		);
	}

	public WSItemPK putItemWithCustomReport(
			WSPutItemWithCustomReport wsPutItemWithCustomReport)
			throws RemoteException {
		return doPutItemWithCustomReport(wsPutItemWithCustomReport.getWsPutItemWithReport(),wsPutItemWithCustomReport.getUser());
	}

	public WSItemPK putItemWithReport(WSPutItemWithReport wsPutItemWithReport)
			throws RemoteException {
		return doPutItemWithCustomReport(wsPutItemWithReport,null);
	}

	public WSItemPKArray putItemWithReportArray(
			WSPutItemWithReportArray wsPutItemWithReportArray)
			throws RemoteException {
		WSPutItemWithReport[] items=wsPutItemWithReportArray.getWsPutItem();
		List<WSItemPK> pks=new ArrayList<WSItemPK>();
		for(WSPutItemWithReport item: items){
			WSItemPK pk=putItemWithReport(item);
			pks.add(pk);
		}
		return new WSItemPKArray(pks.toArray(new WSItemPK[pks.size()]));
	}

	public WSPipeline extractUsingTransformer(
			WSExtractUsingTransformer wsExtractUsingTransformer)
			throws RemoteException {
		try {
			TransformerPluginContext context = Util.getItemCtrl2Local().extractUsingTransformer(
					new ItemPOJOPK(
							new DataClusterPOJOPK(wsExtractUsingTransformer.getWsItemPK().getWsDataClusterPK().getPk()),
							wsExtractUsingTransformer.getWsItemPK().getConceptName(),
							wsExtractUsingTransformer.getWsItemPK().getIds()
					),
					new TransformerPOJOPK(wsExtractUsingTransformer.getWsTransformerPK().getPk())
			);
			HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			return POJO2WSOLD(pipeline);			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}

	}

	public WSPipeline extractUsingTransformerThruView(
			WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView)
			throws RemoteException {
		try {
			TransformerContext context = Util.getItemCtrl2Local().extractUsingTransformerThroughView(
					new DataClusterPOJOPK(wsExtractUsingTransformerThruView.getWsDataClusterPK().getPk()),
					new TransformerV2POJOPK(wsExtractUsingTransformerThruView.getWsTransformerPK().getPk()),
					new ViewPOJOPK(wsExtractUsingTransformerThruView.getWsViewPK().getPk()),
					WS2VO(wsExtractUsingTransformerThruView.getWhereItem()),
					wsExtractUsingTransformerThruView.getSpellTreshold(),
					wsExtractUsingTransformerThruView.getOrderBy(),
					wsExtractUsingTransformerThruView.getDirection(),
					wsExtractUsingTransformerThruView.getSkip(),
					wsExtractUsingTransformerThruView.getMaxItems()
			);
			HashMap<String, TypedContent> pipeline = context.getPipelineClone();
			return POJO2WS(pipeline);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));			
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/***************************************************************************
	 * Delete Items
	 * **************************************************************************/

	public WSItemPK deleteItem(WSDeleteItem wsDeleteItem)
			throws RemoteException {
		try {
			ItemPOJOPK itemPK = new ItemPOJOPK(
					new DataClusterPOJOPK(wsDeleteItem.getWsItemPK().getWsDataClusterPK().getPk()),
					wsDeleteItem.getWsItemPK().getConceptName(),
					wsDeleteItem.getWsItemPK().getIds()
					);
				Util.getItemCtrl2Local().deleteItem(itemPK);
				return itemPK == null ? null : wsDeleteItem.getWsItemPK();
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));				
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSInt deleteItems(WSDeleteItems wsDeleteItems)
			throws RemoteException {
		try {
			int numItems = 
				Util.getItemCtrl2Local().deleteItems(
						new DataClusterPOJOPK(wsDeleteItems.getWsDataClusterPK().getPk()),
						wsDeleteItems.getConceptName(),
						WS2VO(wsDeleteItems.getWsWhereItem()),
						wsDeleteItems.getSpellTreshold()
				);
				return new WSInt(numItems);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));				
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSDroppedItemPK dropItem(WSDropItem wsDropItem)
			throws RemoteException {
		try {
			WSItemPK wsItemPK=wsDropItem.getWsItemPK();
			String partPath=wsDropItem.getPartPath();
			
			DroppedItemPOJOPK droppedItemPOJOPK = Util.getItemCtrl2Local().dropItem(WS2POJO(wsItemPK), partPath);
			
			return POJO2WS(droppedItemPOJOPK);

		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));				
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	/***************************************************************************
	 * DirectQuery
	 * **************************************************************************/
	public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
		try {
			DataClusterPOJOPK dcpk = 
				(wsRunQuery.getWsDataClusterPK()== null) ?
						null:
						new DataClusterPOJOPK(wsRunQuery.getWsDataClusterPK().getPk());
			Collection<String> result = 
				Util.getItemCtrl2Local().runQuery(
					wsRunQuery.getRevisionID(),
					dcpk,
					wsRunQuery.getQuery(),
					wsRunQuery.getParameters()
				);
			//storeprocedure may modify the db, so we need to clear the cache
			Util.getXmlServerCtrlLocal().clearCache();
			return new WSStringArray(result.toArray(new String[result.size()]));
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));				
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	/***************************************************************************
	 * RoutingRule
	 * **************************************************************************/
	
    protected WSRoutingRule VO2WS(RoutingRulePOJO pojo) throws Exception{
	    WSRoutingRule s = new WSRoutingRule();
	    s.setName(pojo.getName());
		s.setDescription(pojo.getDescription());
		s.setConcept(pojo.getConcept());
		s.setParameters(pojo.getParameters());
		s.setServiceJNDI(pojo.getServiceJNDI());
		s.setSynchronous(pojo.isSynchronous());

		WSRoutingRuleExpression[] routingExpressions = null;
		Collection c = pojo.getRoutingExpressions();
		if (c!=null) {		
			routingExpressions = new WSRoutingRuleExpression[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				RoutingRuleExpressionPOJO rre = (RoutingRuleExpressionPOJO) iter.next();
				routingExpressions[i++] = VO2WS(rre);
			}
		}
		s.setWsRoutingRuleExpressions(routingExpressions);
		s.setCondition(pojo.getCondition());
		s.setDeactive(pojo.isDeActive());
		return s;
	}
	
    
	protected RoutingRulePOJO WS2VO(WSRoutingRule ws) throws Exception{
		RoutingRulePOJO pojo = new RoutingRulePOJO();
		pojo.setName(ws.getName());
		pojo.setDescription(ws.getDescription());
		pojo.setConcept(ws.getConcept());
		pojo.setParameters(ws.getParameters());
		pojo.setServiceJNDI(ws.getServiceJNDI());
		pojo.setSynchronous(ws.isSynchronous());
		
		ArrayList l = new ArrayList();
	    WSRoutingRuleExpression[] rre = ws.getWsRoutingRuleExpressions();
	    if (rre!=null) {
		    for (int i = 0; i < rre.length; i++) {
		    	l.add(WS2VO(rre[i]));
			}
	    }
	    pojo.setRoutingExpressions(l);
	    pojo.setCondition(ws.getCondition());
	    pojo.setDeActive(ws.getDeactive());
		return pojo;
	}	

	
    protected WSRoutingRuleExpression VO2WS(RoutingRuleExpressionPOJO vo) throws Exception{
    	WSRoutingRuleExpression ws = new WSRoutingRuleExpression();
    	
    	ws.setName(vo.getName());
    	ws.setXpath(vo.getXpath());
    	ws.setValue(vo.getValue());
    	switch (vo.getOperator()) {
    		case RoutingRuleExpressionPOJO.CONTAINS:
    			ws.setWsOperator(WSRoutingRuleOperator.CONTAINS);
    			break;
    		case RoutingRuleExpressionPOJO.EQUALS:
    			ws.setWsOperator(WSRoutingRuleOperator.EQUALS);
    			break;
    		case RoutingRuleExpressionPOJO.GREATER_THAN:
    			ws.setWsOperator(WSRoutingRuleOperator.GREATER_THAN);
    			break;
    		case RoutingRuleExpressionPOJO.GREATER_THAN_OR_EQUAL:
    			ws.setWsOperator(WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL);
    			break;
    		case RoutingRuleExpressionPOJO.IS_NOT_NULL:
    			ws.setWsOperator(WSRoutingRuleOperator.IS_NOT_NULL);
    			break;
    		case RoutingRuleExpressionPOJO.IS_NULL:
    			ws.setWsOperator(WSRoutingRuleOperator.IS_NULL);
    			break;
    		case RoutingRuleExpressionPOJO.LOWER_THAN:
    			ws.setWsOperator(WSRoutingRuleOperator.LOWER_THAN);
    			break;
    		case RoutingRuleExpressionPOJO.LOWER_THAN_OR_EQUAL:
    			ws.setWsOperator(WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL);
    			break;
    		case RoutingRuleExpressionPOJO.MATCHES:
    			ws.setWsOperator(WSRoutingRuleOperator.MATCHES);
    			break;
    		case RoutingRuleExpressionPOJO.NOT_EQUALS:
    			ws.setWsOperator(WSRoutingRuleOperator.NOT_EQUALS);
    			break;
    		case RoutingRuleExpressionPOJO.STARTSWITH:
    			ws.setWsOperator(WSRoutingRuleOperator.STARTSWITH);
    			break;
    	}
    	
    	return ws;
    }
	
    
    protected RoutingRuleExpressionPOJO WS2VO(WSRoutingRuleExpression ws) throws Exception{
    	
    	if (ws==null) return null;
    	
    	int operator = 1;
    	if (ws.getWsOperator().equals(WSRoutingRuleOperator.CONTAINS))
    		operator = RoutingRuleExpressionPOJO.CONTAINS;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.EQUALS))
    		operator = RoutingRuleExpressionPOJO.EQUALS;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN))
    		operator = RoutingRuleExpressionPOJO.GREATER_THAN;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.GREATER_THAN_OR_EQUAL))
    		operator = RoutingRuleExpressionPOJO.GREATER_THAN_OR_EQUAL;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.IS_NOT_NULL))
    		operator = RoutingRuleExpressionPOJO.IS_NOT_NULL;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.IS_NULL))
    		operator = RoutingRuleExpressionPOJO.IS_NULL;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN))
    		operator = RoutingRuleExpressionPOJO.LOWER_THAN;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.LOWER_THAN_OR_EQUAL))
    		operator = RoutingRuleExpressionPOJO.LOWER_THAN_OR_EQUAL;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.MATCHES))
    		operator = RoutingRuleExpressionPOJO.MATCHES;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.NOT_EQUALS))
    		operator = RoutingRuleExpressionPOJO.NOT_EQUALS;
    	else if (ws.getWsOperator().equals(WSRoutingRuleOperator.STARTSWITH))
    		operator = RoutingRuleExpressionPOJO.STARTSWITH;
    	
    	return new RoutingRuleExpressionPOJO(
    			ws.getName(),
    			ws.getXpath(),
    			operator,
    			ws.getValue()
    	);
    }

	public WSRoutingRulePK deleteRoutingRule(
			WSDeleteRoutingRule wsDeleteRoutingRule) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regex)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * SERVICES
	 * **************************************************************************/

	public WSCheckServiceConfigResponse checkServiceConfiguration(
			WSCheckServiceConfigRequest serviceName) throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					"amalto/local/service/"+serviceName.getJndiName()
				);
			
			Boolean result = (Boolean)
				Util.getMethod(service, "checkConfigure").invoke(
					service,
					new Object[] {	serviceName.getConf()						
					}
				);
			return new WSCheckServiceConfigResponse(result);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString getServiceConfiguration(
			WSServiceGetConfiguration wsGetConfiguration)
			throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					wsGetConfiguration.getJndiName()
				);
			
			String configuration = (String)
				Util.getMethod(service, "getConfiguration").invoke(
					service,
					new Object[] {
							wsGetConfiguration.getOptionalParameter()
					}
				);
			return new WSString(configuration);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSServiceGetDocument getServiceDocument(WSString serviceName)
			throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					"amalto/local/service/"+serviceName.getValue()
				);
			
			String desc = "";
			Object descObject=Util.getMethod(service, "getDescription").invoke(
				service,
				new Object[] {
						""
				}
			);
			if(descObject!=null)desc=(String)descObject;
			
			String configuration = "";
			Object configurationObject=Util.getMethod(service, "getConfiguration").invoke(
					service,
					new Object[] {
							""
					}
				);
			if(configurationObject!=null)configuration= (String)configurationObject;
			
			String doc = "";
			String schema = "";
			String defaultConf = "";
			try{
				
				Method getDocumentationMethod=Util.getMethod(service, "getDocumentation");
				if(getDocumentationMethod!=null){
					Object docObject=getDocumentationMethod.invoke(
							service,
							new Object[] {
									""
							}
					);		
					if(docObject!=null)doc=(String)docObject;
				}
				
				Method getDefaultConfigurationMethod=Util.getMethod(service, "getDefaultConfiguration");
				if(getDefaultConfigurationMethod!=null){
					Object defaultConfObject=getDefaultConfigurationMethod.invoke(
							service,
							new Object[]{
							}
					);
					if(defaultConfObject!=null)defaultConf=(String)defaultConfObject;
				}
				
				Method getConfigurationSchemaMethod=Util.getMethod(service, "getConfigurationSchema");
				if(getConfigurationSchemaMethod!=null){
					Object schemaObject=getConfigurationSchemaMethod.invoke(
							service,
							new Object[] {						
							}
					    );
					if(schemaObject!=null)schema=(String)schemaObject;
				}
				
			}catch(Exception e){
				e.printStackTrace();
			}
			return new WSServiceGetDocument(desc,configuration,doc,schema, defaultConf);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSServicesList getServicesList(WSGetServicesList wsGetServicesList)
			throws RemoteException {
		try {
			ArrayList<WSServicesListItem> wsList = new ArrayList<WSServicesListItem>();
			List<String > jndiList= Util.getRuntimeServiceJndiList();
			String serviceJndiPrefix="amalto/local/service/";
			for(String jndi: jndiList){
			    WSServicesListItem item =new WSServicesListItem();
			    item.setJndiName(jndi.replaceAll(serviceJndiPrefix, ""));
			    wsList.add(item);
			}
			return new WSServicesList(wsList.toArray(new WSServicesListItem[wsList.size()]));
//		} catch (XtentisException e) {
//			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString putServiceConfiguration(
			WSServicePutConfiguration wsPutConfiguration)
			throws RemoteException {
		try {
			Object service= 
				Util.retrieveComponent(
					null, 
					wsPutConfiguration.getJndiName()
				);
			
			Util.getMethod(service, "putConfiguration").invoke(
				service,
				new Object[] {
						wsPutConfiguration.getConfiguration()
				}
			);
			return new WSString(wsPutConfiguration.getConfiguration());
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSString serviceAction(WSServiceAction wsServiceAction)
			throws RemoteException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("serviceAction() "+wsServiceAction.getJndiName());
		try {
			Object service= 
				com.amalto.core.util.Util.retrieveComponent(
					null, 
					wsServiceAction.getJndiName()
				);
			String result = "";
			
			if (WSServiceActionCode.EXECUTE.equals(wsServiceAction.getWsAction())) {
				
					Method method = com.amalto.core.util.Util.getMethod(service, wsServiceAction.getMethodName());
					
					result = (String)method.invoke(
							service,
							wsServiceAction.getMethodParameters()
						);				
			} else {
				if (WSServiceActionCode.START.equals(wsServiceAction.getWsAction())) {
					com.amalto.core.util.Util.getMethod(service, "start").invoke(
							service,
							new Object[] {}
						);
				} else if (WSServiceActionCode.STOP.equals(wsServiceAction.getWsAction())) {
					com.amalto.core.util.Util.getMethod(service, "stop").invoke(
							service,
							new Object[] {}
						);				
				}
				result = (String)
					com.amalto.core.util.Util.getMethod(service, "getStatus").invoke(
							service,
							new Object[] {}
						);
			}
			return new WSString(result);
		} catch (com.amalto.core.util.XtentisException e) {
			e.printStackTrace();
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	/***************************************************************************
	 * Stored Procedure
	 * **************************************************************************/
	public WSStoredProcedurePK deleteStoredProcedure(
			WSDeleteStoredProcedure wsStoredProcedureDelete)
			throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = StoredProcedureCtrlUtil.getLocalHome().create();
			StoredProcedurePOJOPK pk =
				ctrl.removeStoredProcedure(
					new StoredProcedurePOJOPK(
							wsStoredProcedureDelete.getWsStoredProcedurePK().getPk()
					)
				);
			return new WSStoredProcedurePK(pk.getIds()[0]);		
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStringArray executeStoredProcedure(
			WSExecuteStoredProcedure wsExecuteStoredProcedure)
			throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = StoredProcedureCtrlUtil.getLocalHome().create();
			Collection c =
				ctrl.execute(
					new StoredProcedurePOJOPK(
							wsExecuteStoredProcedure.getWsStoredProcedurePK().getPk()
					),
					wsExecuteStoredProcedure.getRevisionID(),
					new DataClusterPOJOPK(
							wsExecuteStoredProcedure.getWsDataClusterPK().getPk()
					),
					wsExecuteStoredProcedure.getParameters()
				);
			if (c==null) return null;
			String[] xmls  = new String[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				xmls[i++] = (String) iter.next();
			}
			return new WSStringArray(xmls);		
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsStoredProcedure(
			WSExistsStoredProcedure wsExistsStoredProcedure)
			throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = StoredProcedureCtrlUtil.getLocalHome().create();
			StoredProcedurePOJO pojo =
				ctrl.existsStoredProcedure(
					new StoredProcedurePOJOPK(
							wsExistsStoredProcedure.getWsStoredProcedurePK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStoredProcedure getStoredProcedure(
			WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = StoredProcedureCtrlUtil.getLocalHome().create();
			StoredProcedurePOJO pojo =
				ctrl.getStoredProcedure(
					new StoredProcedurePOJOPK(
							wsGetStoredProcedure.getWsStoredProcedurePK().getPk()
					)
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStoredProcedurePKArray getStoredProcedurePKs(
			WSRegexStoredProcedure regex) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = StoredProcedureCtrlUtil.getLocalHome().create();
			Collection c =
				ctrl.getStoredProcedurePKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSStoredProcedurePK[] pks = new WSStoredProcedurePK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSStoredProcedurePK(
						((StoredProcedurePOJOPK) iter.next()).getIds()[0]
				);
			}
			return new WSStoredProcedurePKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStoredProcedurePK putStoredProcedure(
			WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
		try {
			StoredProcedureCtrlLocal ctrl = StoredProcedureCtrlUtil.getLocalHome().create();
			StoredProcedurePOJOPK pk =
				ctrl.putStoredProcedure(
					WS2POJO(wsStoredProcedure.getWsStoredProcedure())
				);
			return new WSStoredProcedurePK(pk.getIds()[0]);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	/***************************************************************************
	 * TransformerV2
	 * **************************************************************************/
	public WSTransformerV2PK deleteTransformerV2(
			WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSTransformerContext executeTransformerV2(
			WSExecuteTransformerV2 wsExecuteTransformerV2)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBackgroundJobPK executeTransformerV2AsJob(
			WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBoolean existsTransformerV2(
			WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSTransformerContext extractThroughTransformerV2(
			WSExtractThroughTransformerV2 wsExtractThroughTransformerV2)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSTransformerV2 getTransformerV2(
			WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSTransformerV2PKArray getTransformerV2PKs(
			WSGetTransformerV2PKs regex) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * TRANSFORMER PLUGINS V2
	 * **************************************************************************/

	public WSBoolean existsTransformerPluginV2(
			WSExistsTransformerPluginV2 wsExistsTransformerPlugin)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSString getTransformerPluginV2Configuration(
			WSTransformerPluginV2GetConfiguration wsGetConfiguration)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSTransformerPluginV2Details getTransformerPluginV2Details(
			WSGetTransformerPluginV2Details wsGetTransformerPluginDetails)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSTransformerPluginV2SList getTransformerPluginV2SList(
			WSGetTransformerPluginV2SList wsGetTransformerPluginsList)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSString putTransformerPluginV2Configuration(
			WSTransformerPluginV2PutConfiguration wsPutConfiguration)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	
	/***************************************************************************
	 * Role
	 * **************************************************************************/
	public WSRolePK deleteRole(WSDeleteRole wsRoleDelete)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBoolean existsRole(WSExistsRole wsExistsRole)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRolePK putRole(WSPutRole wsRole) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * Menu
	 * **************************************************************************/
	public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete)
			throws RemoteException {
		try {
			MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
			return
				new WSMenuPK(
					ctrl.removeMenu(
						new MenuPOJOPK(
								wsMenuDelete.getWsMenuPK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSBoolean existsMenu(WSExistsMenu wsExistsMenu)
			throws RemoteException {
		try {
			MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
			MenuPOJO pojo =
				ctrl.existsMenu(
					new MenuPOJOPK(
							wsExistsMenu.getWsMenuPK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
			MenuPOJO pojo =
				ctrl.getMenu(
					new MenuPOJOPK(
							wsGetMenu.getWsMenuPK().getPk()
					)
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
			Collection c =
				ctrl.getMenuPKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSMenuPK[] pks = new WSMenuPK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSMenuPK(
						((MenuPOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSMenuPKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
		try {
			MenuCtrlLocal ctrl = Util.getMenuCtrlLocal();
			MenuPOJOPK pk =
				ctrl.putMenu(
					WS2POJO(wsMenu.getWsMenu())
				);
			return new WSMenuPK(pk.getUniqueId());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	/***************************************************************************
	 * Versioning
	 * **************************************************************************/

	public WSString putVersioningSystemConfiguration(
			WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBackgroundJobPK versioningCommitItems(
			WSVersioningCommitItems wsVersioningCommitItems)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSVersioningInfo versioningGetInfo(
			WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSString versioningGetItemContent(
			WSVersioningGetItemContent wsVersioningGetItemContent)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSVersioningItemHistory versioningGetItemHistory(
			WSVersioningGetItemHistory wsVersioningGetItemHistory)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSVersioningItemsVersions versioningGetItemsVersions(
			WSVersioningGetItemsVersions wsVersioningGetItemsVersions)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSVersioningObjectsVersions versioningGetObjectsVersions(
			WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSVersioningUniverseVersions versioningGetUniverseVersions(
			WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBoolean versioningRestoreItemByRevision(
			WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBackgroundJobPK versioningRestoreItems(
			WSVersioningRestoreItems wsVersioningRestoreItems)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBackgroundJobPK versioningRestoreUniverse(
			WSVersioningRestoreUniverse wsVersioningRestoreUniverse)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBackgroundJobPK versioningTagItems(
			WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBackgroundJobPK versioningTagObjects(
			WSVersioningTagObjects wsVersioningTagObjects)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBackgroundJobPK versioningTagUniverse(
			WSVersioningTagUniverse wsVersioningTagUniverse)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	
	public WSBackgroundJobPK versioningRestoreObjects(
			WSVersioningRestoreObjects wsVersioningRestoreObjects)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * BackgroundJob
	 * **************************************************************************/

    public WSBackgroundJobPKArray findBackgroundJobPKs(
    		WSFindBackgroundJobPKs wsFindBackgroundJobPKs)
    		throws RemoteException {
    	try {
		    //WSBackgroundJobPKArray array = new WSBackgroundJobPKArray();
		    throw new RemoteException("WSBackgroundJobPKArray is not implemented in this version of the core");
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    public WSBackgroundJob getBackgroundJob(
    		WSGetBackgroundJob wsBackgroundJobGet) throws RemoteException {
    	try {
		    return POJO2WS( 
					BackgroundJobCtrlUtil.getLocalHome().create().getBackgroundJob(
							new BackgroundJobPOJOPK(wsBackgroundJobGet.getPk())
					)
			);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsputjob)
    		throws RemoteException {
    	try {
	        return new WSBackgroundJobPK(BackgroundJobCtrlUtil.getLocalHome().create().putBackgroundJob(
	                				WS2POJO(wsputjob.getWsBackgroundJob())
	            					).getUniqueId());
		} catch (Exception e) {
			throw new EJBException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
    }
    /***************************************************************************
	 * Routing Order V2
	 * **************************************************************************/
	public WSRoutingOrderV2PK deleteRoutingOrderV2(
			WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(
			WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSString executeRoutingOrderV2Synchronously(
			WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingOrderV2 existsRoutingOrderV2(
			WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingOrderV2 getRoutingOrderV2(
			WSGetRoutingOrderV2 wsGetRoutingOrder) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(
			WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(
			WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * Routing Engine V2
	 * **************************************************************************/
	
	public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem)
			throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}
	public WSRoutingEngineV2Status routingEngineV2Action(
			WSRoutingEngineV2Action wsRoutingEngineAction)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * Universe
	 * **************************************************************************/
	public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSUniverse getCurrentUniverse(
			WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
		try {
			//thei should force the User in the JACC context
			UniverseCtrlUtil.getLocalHome().create();
			//Fetch the user
			LocalUser user = LocalUser.getLocalUser();
			return POJO2WS(user.getUniverse());
		} catch (Exception e) {
			String err = "ERROR SYSTRACE: "+e.getMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).debug(err,e);
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	public WSStringArray getObjectsForUniverses(
			WSGetObjectsForUniverses wsUniverseDelete) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSUniverse getUniverse(WSGetUniverse wsGetUniverse)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSUniversePKArray getUniverseByRevision(
			WSGetUniverseByRevision revision) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSUniversePK putUniverse(WSPutUniverse wsUniverse)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * SynchronizationPlan
	 * **************************************************************************/
	public WSSynchronizationPlanPK deleteSynchronizationPlan(
			WSDeleteSynchronizationPlan wsSynchronizationPlanDelete)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSBoolean existsSynchronizationPlan(
			WSExistsSynchronizationPlan wsExistsSynchronizationPlan)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSStringArray getObjectsForSynchronizationPlans(
			WSGetObjectsForSynchronizationPlans wsSynchronizationPlanDelete)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationPlan getSynchronizationPlan(
			WSGetSynchronizationPlan wsGetSynchronizationPlan)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSStringArray getSynchronizationPlanItemsAlgorithms(
			WSGetSynchronizationPlanItemsAlgorithms regex)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSStringArray getSynchronizationPlanObjectsAlgorithms(
			WSGetSynchronizationPlanObjectsAlgorithms regex)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(
			WSGetSynchronizationPlanPKs regex) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationPlanPK putSynchronizationPlan(
			WSPutSynchronizationPlan wsSynchronizationPlan)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSString synchronizationGetItemXML(
			WSSynchronizationGetItemXML wsSynchronizationGetItemXML)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSString synchronizationGetObjectXML(
			WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(
			WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(
			WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationPlanStatus synchronizationPlanAction(
			WSSynchronizationPlanAction wsSynchronizationPlan)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSItemPK synchronizationPutItemXML(
			WSSynchronizationPutItemXML wsSynchronizationPutItemXML)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSString synchronizationPutObjectXML(
			WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * Synchronization Item
	 * **************************************************************************/
	public WSSynchronizationItemPK deleteSynchronizationItem(
			WSDeleteSynchronizationItem wsSynchronizationItemDelete)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSBoolean existsSynchronizationItem(
			WSExistsSynchronizationItem wsExistsSynchronizationItem)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationItem getSynchronizationItem(
			WSGetSynchronizationItem wsGetSynchronizationItem)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationItemPKArray getSynchronizationItemPKs(
			WSGetSynchronizationItemPKs regex) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationItemPK putSynchronizationItem(
			WSPutSynchronizationItem wsSynchronizationItem)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSSynchronizationItem resolveSynchronizationItem(
			WSResolveSynchronizationItem wsResolveSynchronizationItem)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}
	/***************************************************************************
	 * Drop Item
	 * **************************************************************************/
	public WSDroppedItemPKArray findAllDroppedItemsPKs(
			WSFindAllDroppedItemsPKs regex) throws RemoteException {
		try {
			
			List droppedItemPOJOPKs=Util.getDroppedItemCtrlLocal().findAllDroppedItemsPKs(regex.getRegex());
			
			WSDroppedItemPK[] wsDroppedItemPKs=new WSDroppedItemPK[droppedItemPOJOPKs.size()];
			for (int i = 0; i < droppedItemPOJOPKs.size(); i++) {
				DroppedItemPOJOPK droppedItemPOJOPK = (DroppedItemPOJOPK) droppedItemPOJOPKs.get(i);
				wsDroppedItemPKs[i]=POJO2WS(droppedItemPOJOPK);
			}
			
			return new WSDroppedItemPKArray(wsDroppedItemPKs);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem)
			throws RemoteException {
		try {
			
        	DroppedItemPOJO droppedItemPOJO=Util.getDroppedItemCtrlLocal().loadDroppedItem(WS2POJO(wsLoadDroppedItem.getWsDroppedItemPK()));
			
			return POJO2WS(droppedItemPOJO);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem)
			throws RemoteException {
	       try {
				
	        	ItemPOJOPK itemPOJOPK=Util.getDroppedItemCtrlLocal().recoverDroppedItem(WS2POJO(wsRecoverDroppedItem.getWsDroppedItemPK()));
				
				return POJO2WS(itemPOJOPK);
				
			} catch (XtentisException e) {
				throw(new RemoteException(e.getLocalizedMessage()));
			} catch (Exception e) {
				throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
			}
	}

	public WSDroppedItemPK removeDroppedItem(
			WSRemoveDroppedItem wsRemoveDroppedItem) throws RemoteException {
		try {
			
        	DroppedItemPOJOPK droppedItemPOJOPK=Util.getDroppedItemCtrlLocal().removeDroppedItem(WS2POJO(wsRemoveDroppedItem.getWsDroppedItemPK()));
			
			return POJO2WS(droppedItemPOJOPK);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	/***************************************************************************
	 * Workflow
	 * **************************************************************************/
	public WSBoolean workflowDeleteProcessInstances(
			WSWorkflowDeleteProcessInstancesRequest deleteWolkflowRequest)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSWorkflowProcessDefinitionUUID workflowDeploy(
			WSWorkflowDeploy deploy) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSWorkflowProcessDefinitionUUIDArray workflowGetProcessDefinitions(
			WSWorkflowGetProcessDefinitions wsWorkflowGetProcessDefinitions)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSProcessInstanceArray workflowGetProcessInstances(
			WSWorkflowGetProcessInstances instance) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSProcessTaskInstanceArray workflowGetTaskList(
			WSWorkflowGetTaskList tasklist) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	public WSBoolean workflowUnDeploy(WSWorkflowUnDeploy undeploy)
			throws RemoteException {
		throw new RemoteException("Not Support!");
	}

	/***************************************************************************
	 * Job
	 * **************************************************************************/
	public WSBoolean deleteMDMJob(WSDELMDMJob job) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSMDMJobArray getMDMJob(WSMDMNULL job) throws RemoteException {
		// TODO Auto-generated method stub
		throw new RemoteException("Not Support!");
	}

	public WSBoolean putMDMJob(WSPUTMDMJob job) throws RemoteException {
		throw new RemoteException("Not Support!");
	}

    
}
