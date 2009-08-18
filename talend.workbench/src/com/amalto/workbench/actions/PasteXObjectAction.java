package com.amalto.workbench.actions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;

import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WorkbenchClipboard;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSExistsDataCluster;
import com.amalto.workbench.webservices.WSExistsDataModel;
import com.amalto.workbench.webservices.WSExistsMenu;
import com.amalto.workbench.webservices.WSExistsRole;
import com.amalto.workbench.webservices.WSExistsRoutingRule;
import com.amalto.workbench.webservices.WSExistsStoredProcedure;
import com.amalto.workbench.webservices.WSExistsSynchronizationPlan;
import com.amalto.workbench.webservices.WSExistsTransformerV2;
import com.amalto.workbench.webservices.WSExistsUniverse;
import com.amalto.workbench.webservices.WSExistsView;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetDataModel;
import com.amalto.workbench.webservices.WSGetMenu;
import com.amalto.workbench.webservices.WSGetRole;
import com.amalto.workbench.webservices.WSGetRoutingRule;
import com.amalto.workbench.webservices.WSGetStoredProcedure;
import com.amalto.workbench.webservices.WSGetSynchronizationPlan;
import com.amalto.workbench.webservices.WSGetTransformerV2;
import com.amalto.workbench.webservices.WSGetUniverse;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSPutDataCluster;
import com.amalto.workbench.webservices.WSPutDataModel;
import com.amalto.workbench.webservices.WSPutMenu;
import com.amalto.workbench.webservices.WSPutRole;
import com.amalto.workbench.webservices.WSPutRoutingRule;
import com.amalto.workbench.webservices.WSPutStoredProcedure;
import com.amalto.workbench.webservices.WSPutSynchronizationPlan;
import com.amalto.workbench.webservices.WSPutTransformerV2;
import com.amalto.workbench.webservices.WSPutUniverse;
import com.amalto.workbench.webservices.WSPutView;
import com.amalto.workbench.webservices.WSRole;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSSynchronizationPlan;
import com.amalto.workbench.webservices.WSSynchronizationPlanPK;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniversePK;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.XtentisPort;

public class PasteXObjectAction extends Action{

	private ServerView view = null;
    private XtentisPort destPort = null;
	/*
	public CopyXObjectAction(TreeObject xobject, IWorkbenchPage page) {
		super();
		this.xobject = xobject;
		this.page = page;
		setDetails();
	}
	*/
	
	public PasteXObjectAction(ServerView view) {
		super();
		this.view = view;
		setDetails();
	}
	
	private void setDetails() {
		setImageDescriptor(ImageCache.getImage( "icons/paste.gif"));
		setText("Paste");
		setToolTipText("Paste this instance of the "+IConstants.TALEND+" Object");		
	}
	
	public void setXtentisPort(TreeObject remoteTreeObject)
	{
		 try {
			destPort = Util.getPort(
					new URL(remoteTreeObject.getEndpointAddress()),
					remoteTreeObject.getUniverse(),
					remoteTreeObject.getUsername(),
					remoteTreeObject.getPassword()
			);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void run() {
		TreeObject selected = (TreeObject)((IStructuredSelection)view.getViewer().getSelection()).getFirstElement();
	    Map<TreeObject, Object> keyTrackMap = new HashMap<TreeObject, Object>();
		try {
			super.run();
			if (this.view == null) {
				MessageDialog.openError(view.getSite().getShell(), "Paste Error", "This function must be called from the Object View");
				return;
			}
			
			if (destPort == null)
			{
				 destPort = Util.getPort(
						new URL(selected.getEndpointAddress()),
						selected.getUniverse(),
						selected.getUsername(),
						selected.getPassword()
				);
			}
			
			ArrayList<TreeObject> list = WorkbenchClipboard.getWorkbenchClipboard().get();
			while(true)
			{
				for (Iterator<TreeObject> iter = list.iterator(); iter.hasNext(); ) {
	//				is an XObject necessarily
					TreeObject xobject = iter.next();
					Object latestValue = keyTrackMap.get(xobject);
		            switch(xobject.getType()) {
			           	case TreeObject.DATA_MODEL: {
			           		WSDataModelPK key = (WSDataModelPK)xobject.getWsKey();
			           		WSDataModelPK newKey = latestValue != null ? (WSDataModelPK)latestValue : new WSDataModelPK(key.getPk());
			           		if (destPort.existsDataModel(new WSExistsDataModel(latestValue != null ? (WSDataModelPK)latestValue : (WSDataModelPK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Data Model with the name \""+ (latestValue!= null ? newKey.getPk(): key.getPk()) + "\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;
				           		}
				           		newKey = new WSDataModelPK(id.getValue()); 
				           	    keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSDataModel originalDataModel = originalPort
								.getDataModel(new WSGetDataModel(key));
			           		WSDataModel newDataModel = new WSDataModel(
			           				newKey.getPk(),
			           				originalDataModel.getDescription(),
			           				originalDataModel.getXsdSchema()
			           		);
			           		//write the new model
			           		destPort.putDataModel(new WSPutDataModel(newDataModel));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		}break;
			           	case TreeObject.VIEW: {
			           		WSViewPK key = (WSViewPK)xobject.getWsKey();
			           		WSViewPK newKey = latestValue != null ? (WSViewPK)latestValue : new WSViewPK(key.getPk());
			           		if (destPort.existsView(new WSExistsView(latestValue != null ? (WSViewPK)latestValue : (WSViewPK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A View with the name \"" + (latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{	
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;
				           		}
				           		newKey = new WSViewPK(id.getValue()); 
				           	    keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSView originalView = originalPort.getView(new WSGetView(key));
			           		WSView newView = new WSView(
			           				newKey.getPk(),
			           				originalView.getDescription(),
			           				originalView.getViewableBusinessElements(),
			           				originalView.getWhereConditions(),
			           				originalView.getSearchableBusinessElements()
			           		);
			           		//write the new model
			           		destPort.putView(new WSPutView(newView));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;
			           	case TreeObject.DATA_CLUSTER: {
			           		WSDataClusterPK key = (WSDataClusterPK)xobject.getWsKey();
			           		WSDataClusterPK newKey = latestValue != null ? (WSDataClusterPK)latestValue : new WSDataClusterPK(key.getPk());
			           		if (destPort.existsDataCluster(new WSExistsDataCluster(latestValue != null ? (WSDataClusterPK)latestValue : (WSDataClusterPK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Data Cluster with the name \""+(latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;	
				           		}
				           		newKey = new WSDataClusterPK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSDataCluster originalDataCluster = originalPort.getDataCluster(new WSGetDataCluster(key));
			           		WSDataCluster newDataCluster = new WSDataCluster(
			           				newKey.getPk(),
			           				originalDataCluster.getDescription(),
			           				originalDataCluster.getVocabulary()
			           		);
			           		//write the new model
			           		destPort.putDataCluster(new WSPutDataCluster(newDataCluster));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;
			           	case TreeObject.STORED_PROCEDURE: {
			           		WSStoredProcedurePK key = (WSStoredProcedurePK)xobject.getWsKey();
			           		WSStoredProcedurePK newKey = latestValue != null ? (WSStoredProcedurePK)latestValue : new WSStoredProcedurePK(key.getPk());
			           		if (destPort.existsStoredProcedure(new WSExistsStoredProcedure(latestValue != null ? (WSStoredProcedurePK)latestValue : (WSStoredProcedurePK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Stored Procedure with the name \""+(latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;	
				           		}
				           		newKey = new WSStoredProcedurePK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSStoredProcedure originalStoredProcedure = originalPort.getStoredProcedure(new WSGetStoredProcedure(key));
			           		WSStoredProcedure newStoredProcedure = new WSStoredProcedure(
			           				newKey.getPk(),
			           				originalStoredProcedure.getDescription(),
			           				originalStoredProcedure.getProcedure()
			           		);
			           		//write the new model
			           		destPort.putStoredProcedure(new WSPutStoredProcedure(newStoredProcedure));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;
			           	case TreeObject.ROLE: {
			           		WSRolePK key = (WSRolePK)xobject.getWsKey();
			           		WSRolePK newKey = latestValue != null ? (WSRolePK)latestValue : new WSRolePK(key.getPk());
			           		if (destPort.existsRole(new WSExistsRole(latestValue != null ? (WSRolePK)latestValue :(WSRolePK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Role with the name \""+(latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL) 
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;	
				           		}
				           		newKey = new WSRolePK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSRole originalRole = originalPort.getRole(new WSGetRole(key));
			           		WSRole newRole = new WSRole(
			           				newKey.getPk(),
			           				originalRole.getDescription(),
			           				originalRole.getSpecification()
			           		);
			           		//write the new model
			           		destPort.putRole(new WSPutRole(newRole));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;	  	           		
			           	case TreeObject.ROUTING_RULE: {
			           		WSRoutingRulePK key = (WSRoutingRulePK)xobject.getWsKey();
			           		WSRoutingRulePK newKey = latestValue != null ? (WSRoutingRulePK)latestValue : new WSRoutingRulePK(key.getPk());
			           		if (destPort.existsRoutingRule(new WSExistsRoutingRule(latestValue != null ? (WSRoutingRulePK)latestValue :(WSRoutingRulePK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Routing Rule with the name \""+(latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;	
				           		}
				           		newKey = new WSRoutingRulePK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSRoutingRule originalRoutingRule = originalPort.getRoutingRule(new WSGetRoutingRule(key));
			           		WSRoutingRule newRoutingRule = new WSRoutingRule(
			           				newKey.getPk(),
			           				originalRoutingRule.getDescription(),
			           				originalRoutingRule.isSynchronous(),
			           				originalRoutingRule.getConcept(),
			           				originalRoutingRule.getServiceJNDI(),
			           				originalRoutingRule.getParameters(),
			           				originalRoutingRule.getWsRoutingRuleExpressions(),
			           				originalRoutingRule.getCondition(),
			           				originalRoutingRule.getDeactive()
			           		);
			           		//write the new model
			           		destPort.putRoutingRule(new WSPutRoutingRule(newRoutingRule));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;	  	           		
			           	case TreeObject.TRANSFORMER: {
			           		WSTransformerV2PK key = (WSTransformerV2PK)xobject.getWsKey();
			           		WSTransformerV2PK newKey = latestValue != null ? (WSTransformerV2PK)latestValue : new WSTransformerV2PK(key.getPk());
			           		if (destPort.existsTransformerV2(new WSExistsTransformerV2(latestValue != null ? (WSTransformerV2PK)latestValue : (WSTransformerV2PK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Transformer with the name \""+ (latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;	
				           		}
				           		newKey = new WSTransformerV2PK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSTransformerV2 originalTransformer = originalPort.getTransformerV2(new WSGetTransformerV2(key));
			           		WSTransformerV2 newTransformer = new WSTransformerV2(
			           				newKey.getPk(),
			           				originalTransformer.getDescription(),
			           				originalTransformer.getProcessSteps()
			           		);
			           		//write the new model
			           		destPort.putTransformerV2(new WSPutTransformerV2(newTransformer));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;
			           	case TreeObject.MENU: {
			           		WSMenuPK key = (WSMenuPK)xobject.getWsKey();
			           		WSMenuPK newKey = latestValue != null ? (WSMenuPK)latestValue : new WSMenuPK(key.getPk());
			           		if (destPort.existsMenu(new WSExistsMenu(latestValue != null ? (WSMenuPK)latestValue : (WSMenuPK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Menu with the name \""+(latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL) 
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;	
				           		}
				           		newKey = new WSMenuPK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSMenu originalMenu = originalPort.getMenu(new WSGetMenu(key));
			           		WSMenu newMenu = new WSMenu(
			           				newKey.getPk(),
			           				originalMenu.getDescription(),
			           				originalMenu.getMenuEntries()
			           		);
			           		//write the new model
			           		destPort.putMenu(new WSPutMenu(newMenu));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;
	
			           	case TreeObject.UNIVERSE: {
			           		WSUniversePK key = (WSUniversePK)xobject.getWsKey();
			           		WSUniversePK newKey = latestValue != null ? (WSUniversePK)latestValue : new WSUniversePK(key.getPk());
			           		if (destPort.existsUniverse(new WSExistsUniverse(latestValue != null ? (WSUniversePK)latestValue : (WSUniversePK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A Universe with the name \""+ (latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;
				           		}
				           		newKey = new WSUniversePK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSUniverse originalUniverse = originalPort.getUniverse(new WSGetUniverse(key));
			           		WSUniverse newUniverse = new WSUniverse(
			           				newKey.getPk(),
			           				originalUniverse.getDescription(),
			           				originalUniverse.getXtentisObjectsRevisionIDs(),
			           				originalUniverse.getDefaultItemsRevisionID(),
			           				originalUniverse.getItemsRevisionIDs()
			           		);
			           		//write the new model
			           		destPort.putUniverse(new WSPutUniverse(newUniverse));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;
	
			           	case TreeObject.SYNCHRONIZATIONPLAN: {
			           		WSSynchronizationPlanPK key = (WSSynchronizationPlanPK)xobject.getWsKey();
			           		WSSynchronizationPlanPK newKey = latestValue != null ? (WSSynchronizationPlanPK)latestValue : new WSSynchronizationPlanPK(key.getPk());
			           		if (destPort.existsSynchronizationPlan(new WSExistsSynchronizationPlan(latestValue != null ? (WSSynchronizationPlanPK)latestValue : (WSSynchronizationPlanPK)xobject.getWsKey())).is_true()) {
				           		InputDialog id = new InputDialog(
				           				view.getSite().getShell(),
				           				"Pasting instance "+key.getPk(),
				           				"A SynchronizationPlan with the name \""+(latestValue!= null ? newKey.getPk(): key.getPk())+"\" already exists.\nEnter a new name if you do not want to overwriite the existing object",
				           				"Copy of "+(selected.getEndpointAddress().equals(xobject.getEndpointAddress()) ? "": xobject.getEndpointAddress().split(":")[0]+" ")+key.getPk(),
				           				new IInputValidator() {
				           					public String isValid(String newText) {
				           						if ((newText==null) || "".equals(newText)) return "The name cannot be empty";
				           						return null;
				           					};
				           				}
				           		);
				           		id.setBlockOnOpen(true);
				           		if (id.open() == Window.CANCEL)
				           		{
				           			list.remove(xobject);
				           			iter = list.iterator();
				           			break;
				           		}
				           		newKey = new WSSynchronizationPlanPK(id.getValue()); 
				           		keyTrackMap.put(xobject, newKey);
				           		break;
			           		}
			           		//fetch the copied model
		       				XtentisPort originalPort = Util.getPort(
		       						new URL(xobject.getEndpointAddress()),
		       						xobject.getUniverse(),
		       						xobject.getUsername(),
		       						xobject.getPassword()
		       				);
			           		WSSynchronizationPlan originalSynchronizationPlan = originalPort.getSynchronizationPlan(new WSGetSynchronizationPlan(key));
			           		WSSynchronizationPlan newSynchronizationPlan = new WSSynchronizationPlan(
			           				newKey.getPk(),
			           				originalSynchronizationPlan.getDescription(),
			           				originalSynchronizationPlan.getRemoteSystemName(),
			           				originalSynchronizationPlan.getRemoteSystemURL(),
			           				originalSynchronizationPlan.getRemoteSystemUsername(),
			           				originalSynchronizationPlan.getRemoteSystemPassword(),
			           				originalSynchronizationPlan.getTisURL(),
			           				originalSynchronizationPlan.getTisUsername(),
			           				originalSynchronizationPlan.getTisPassword(),
			           				originalSynchronizationPlan.getTisParameters(),
			           				originalSynchronizationPlan.getXtentisObjectsSynchronizations(),
			           				originalSynchronizationPlan.getItemsSynchronizations()
			           		);
			           		//write the new model
			           		destPort.putSynchronizationPlan(new WSPutSynchronizationPlan(newSynchronizationPlan));
			           		TreeObject newObj = new TreeObject(newKey.getPk(), xobject.getServerRoot(), xobject.getType(), newKey, null);
			           		xobject.getParent().addChild(newObj);
			           		list.remove(xobject);
			           		iter = list.iterator();
			           		} break;
	
			           	default:
			           		
		            }//switch
					
				}
				
				if (list.isEmpty())
					break;
			}
			           
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					view.getSite().getShell(),
					"Error", 
					"An error occured trying to copy: "+e.getLocalizedMessage()
			);
		}	finally {
			keyTrackMap.clear();
			//refresh view
			view.forceAllSiteToRefresh();
		}
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}