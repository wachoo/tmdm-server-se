package com.amalto.workbench.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;

import com.amalto.workbench.dialogs.VersioningDialog;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSSynchronizationPlanPK;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSUniversePK;
import com.amalto.workbench.webservices.WSViewPK;

public class VersioningXObjectAction extends Action{

	private ServerView view = null;
	
	
	public VersioningXObjectAction(ServerView view) {
		super();
		this.view = view;
		setImageDescriptor(ImageCache.getImage( "icons/versioning.gif"));
		setText("Versioning");
		setToolTipText("Manages the versioning of this item");
	}
	
	public void run() {
		try {
			super.run();
			IStructuredSelection selection = (IStructuredSelection)view.getViewer().getSelection();
			TreeObject xobject = (TreeObject) selection.iterator().next();
			String type = null;
			String instance = null;
			//FIXME Versioning of a single resource at a time for the moment
			switch (xobject.getType()) {
				/*
				case TreeObject.INBOUND_ADAPTOR :
					type =  "Inbound Adaptor";
					instance = (xobject.isXObject() ? ((WSInboundAdaptorPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.INBOUND_PLUGIN:
					type =  "Inbound Plugin";
					instance = (xobject.isXObject() ? ((WSInboundPluginPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.DESTINATION :
					type =  "Destination";
					instance = (xobject.isXObject() ? ((WSDestinationPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.OUTBOUND_ADAPTOR:
					type =  "Outbound Adaptor";
					instance = (xobject.isXObject() ? ((WSOutboundAdaptorPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.OUTBOUND_PLUGIN:
					type =  "Outbound Plugin";
					instance = (xobject.isXObject() ? ((WSOutboundPluginPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.DOCUMENT:
					type =  "Document";
					instance = (xobject.isXObject() ? ((WSDocumentPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.ITEM:
					type =  "Item";
					instance = (xobject.isXObject() ? ((WSItemPK)xobject.getWsKey()).getPk() : null);
				*/
				case TreeObject.VIEW:
					type =  "View";
					instance = (xobject.isXObject() ? ((WSViewPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.DATA_MODEL:
					type =  "Data Model";
					instance = (xobject.isXObject() ? ((WSDataModelPK)xobject.getWsKey()).getPk() : null);
					break;
				/*
				case TreeObject.SOURCE:
					type =  "Source";
					instance = (xobject.isXObject() ? ((WSSourcePK)xobject.getWsKey()).getPk() : null);
					break;
				*/
				case TreeObject.DATA_CLUSTER:
					type =  "Data Cluster";
					instance = (xobject.isXObject() ? ((WSDataClusterPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.SUBSCRIPTION_ENGINE:
					type =  "Routing Orders";
					instance = null;
					break;
				case TreeObject.ROLE:
					type =  "Role";
					instance = (xobject.isXObject() ? ((WSRolePK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.STORED_PROCEDURE:
					type =  "Stored Procedure";	
					instance = (xobject.isXObject() ? ((WSStoredProcedurePK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.TRANSFORMER:
					type =  "Transformer V2";
					instance = (xobject.isXObject() ? ((WSTransformerV2PK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.MENU:
					type =  "Menu";	
					instance = (xobject.isXObject() ? ((WSMenuPK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.ROUTING_RULE:
					type =  "Routing Rule";	
					instance = (xobject.isXObject() ? ((WSRoutingRulePK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.UNIVERSE:
					type="Universe";
					instance = (xobject.isXObject() ? ((WSUniversePK)xobject.getWsKey()).getPk() : null);
					break;
				case TreeObject.SYNCHRONIZATIONPLAN:
					type="Synchronization Plan";
					instance = (xobject.isXObject() ? ((WSSynchronizationPlanPK)xobject.getWsKey()).getPk() : null);
					break;
			}  
	        
			
			if (type==null) {
				MessageDialog.openInformation(
						view.getSite().getShell(), 
						"Versioning Not Supported", 
						"Versioning is not supported for these deprecated objects"
				);
				return;
			}
			
			VersioningDialog dialog = new VersioningDialog(
					this.view.getSite().getShell(), 
					Util.getPort(xobject),
					type,
					instance == null ? null : new String[] {instance}
			);
			
			dialog.setBlockOnOpen(true);
			dialog.open();
                   
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					view.getSite().getShell(),
					"Error", 
					"An error occured trying to delete the "+IConstants.TALEND+" object: "+e.getLocalizedMessage()
			);
		}		
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}