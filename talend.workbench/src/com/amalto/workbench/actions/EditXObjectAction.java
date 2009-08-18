package com.amalto.workbench.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchPage;

import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
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

public class EditXObjectAction extends Action{

	private ServerView view = null;
	private TreeObject xobject = null;
	private IWorkbenchPage page = null;
	
	
	public EditXObjectAction(TreeObject xobject, IWorkbenchPage page) {
		super();
		this.xobject = xobject;
		this.page = page;
		setDetails();
	}
	
	public EditXObjectAction(ServerView view) {
		super();
		this.view = view;
		setDetails();
	}
	
	private void setDetails() {
		setImageDescriptor(ImageCache.getImage( "icons/edit.gif"));
		setText("Edit");
		setToolTipText("Edit/View this instance of the "+IConstants.TALEND+" Object");		
	}
	
	public void run() {
		try {
			super.run();
			if (this.view != null) { //called from ServerView
				ISelection selection = view.getViewer().getSelection();
				xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
			}
            
            if (!xobject.isXObject()) return;
            
//          Access to server and get port
			XtentisPort port = Util.getPort(
					new URL(xobject.getEndpointAddress()),
					xobject.getUniverse(),
					xobject.getUsername(),
					xobject.getPassword()
			);
              
            switch(xobject.getType()) {
	           	case TreeObject.DATA_MODEL:
	           		WSDataModel wsDataModel = port.getDataModel(new WSGetDataModel((WSDataModelPK)xobject.getWsKey())); 
	           		xobject.setWsObject(wsDataModel);
	           		break;
	           	case TreeObject.VIEW:
	           		WSView wsView = port.getView(new WSGetView((WSViewPK)xobject.getWsKey())); 
	           		xobject.setWsObject(wsView); 
	           		break;
	           	case TreeObject.DATA_CLUSTER:
	           		WSDataCluster wsDataCluster = port.getDataCluster(new WSGetDataCluster((WSDataClusterPK)xobject.getWsKey())	); 
	           		xobject.setWsObject(wsDataCluster);
	           		break;
	           	case TreeObject.STORED_PROCEDURE:
	           		WSStoredProcedure wsStoredProcedure = port.getStoredProcedure(new WSGetStoredProcedure((WSStoredProcedurePK)xobject.getWsKey())	); 
	           		xobject.setWsObject(wsStoredProcedure);
	           		break;	  
	           	case TreeObject.ROLE:
	           		WSRole wsRole = port.getRole(new WSGetRole((WSRolePK)xobject.getWsKey())	); 
	           		xobject.setWsObject(wsRole);
	           		break;	  	           		
	           	case TreeObject.ROUTING_RULE:
	           		WSRoutingRule wsRoutingRule = port.getRoutingRule(new WSGetRoutingRule((WSRoutingRulePK)xobject.getWsKey())	); 
	           		xobject.setWsObject(wsRoutingRule);
	           		break;	  	           		
	           	case TreeObject.TRANSFORMER:
	           		WSTransformerV2 wsTranformer = port.getTransformerV2(new WSGetTransformerV2((WSTransformerV2PK)xobject.getWsKey())); 
	           		xobject.setWsObject(wsTranformer);
	           		break;
	           	case TreeObject.MENU:
	           		WSMenu wsMenu = port.getMenu(new WSGetMenu((WSMenuPK)xobject.getWsKey())); 
	           		xobject.setWsObject(wsMenu);
	           		break;	 
	           	case TreeObject.UNIVERSE:
	           		WSUniverse wsUniverse = port.getUniverse(new WSGetUniverse((WSUniversePK)xobject.getWsKey())); 
	           		xobject.setWsObject(wsUniverse);
	           		break;	 
	           	case TreeObject.SYNCHRONIZATIONPLAN:
	           		WSSynchronizationPlan wsSynchronizationPlan = port.getSynchronizationPlan(new WSGetSynchronizationPlan((WSSynchronizationPlanPK)xobject.getWsKey())); 
	           		xobject.setWsObject(wsSynchronizationPlan);
	           		break;
	           	default:
	           		MessageDialog.openError(view.getSite().getShell(), "Error", "Unknown "+IConstants.TALEND+" Object Type: "+xobject.getType());
	           		return;
            }//switch
            
            if (page==null) this.page = view.getSite().getWorkbenchWindow().getActivePage();
            
       		this.page.openEditor(
                    new XObjectEditorInput(xobject,xobject.getDisplayName()),
                    "com.amalto.workbench.editors.XObjectEditor"
           	);
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					view.getSite().getShell(),
					"Error", 
					"An error occured trying to open the editor: "+e.getLocalizedMessage()
			);
		}		
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}