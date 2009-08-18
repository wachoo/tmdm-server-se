package com.amalto.workbench.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;

import com.amalto.workbench.dialogs.ErrorExceptionDialog;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSMenu;
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
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSSynchronizationPlan;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.XtentisPort;

public class SaveXObjectAction extends Action{

	protected XObjectEditor editor = null;
	//private TreeObject initialXObject = null;
	int state=-1; //0: success 1:failed 
	
	public SaveXObjectAction(XObjectEditor editor) {
		super();
		this.editor = editor;
		//this.initialXObject = initialXObject;
		setImageDescriptor(ImageCache.getImage( "icons/save_edit.gif"));
	}
	
	public void run() {
		try {

			TreeObject xobject = (TreeObject)((XObjectEditorInput)editor.getEditorInput()).getModel();
			Object newWsObject = xobject.getWsObject();
			
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
	           		port.putDataModel(new WSPutDataModel((WSDataModel)newWsObject));
	           		break;
	          	case TreeObject.VIEW:
	           		port.putView(new WSPutView((WSView)newWsObject));
	           		break;           		
	          	case TreeObject.DATA_CLUSTER:
	           		port.putDataCluster(new WSPutDataCluster((WSDataCluster)newWsObject));
	           		break;                      		
	          	case TreeObject.STORED_PROCEDURE:
	           		port.putStoredProcedure(new WSPutStoredProcedure((WSStoredProcedure)newWsObject));
	           		break;   
	          	case TreeObject.ROLE:
	           		port.putRole(new WSPutRole((WSRole)newWsObject));
	           		break;   
	          	case TreeObject.ROUTING_RULE:
	           		port.putRoutingRule(new WSPutRoutingRule((WSRoutingRule)newWsObject));
	           		break;   
	          	case TreeObject.TRANSFORMER:
	           		port.putTransformerV2(new WSPutTransformerV2((WSTransformerV2)newWsObject));
	           		break;   	 
	          	case TreeObject.MENU:
	           		port.putMenu(new WSPutMenu((WSMenu)newWsObject));
	           		break;   	 	
	          	case TreeObject.UNIVERSE:
	           		port.putUniverse(new WSPutUniverse((WSUniverse)newWsObject));
	           		break;   	 	

	          	case TreeObject.SYNCHRONIZATIONPLAN:
	           		port.putSynchronizationPlan(new WSPutSynchronizationPlan((WSSynchronizationPlan)newWsObject));
	           		break;   
	          	default:
	           		MessageDialog.openError(this.editor.getSite().getShell(), "Error", "Unknown "+IConstants.TALEND+" Object Type: "+xobject.getType());
	           		return;
            }//switch
            
            //notify listeners that the data has been persisted
            if (xobject.getParent() == null) {
            	//add the item to the tree
            	if (xobject.getType() != TreeObject.DOCUMENT) {
            		TreeParent folder = xobject.findServerFolder(xobject.getType());
            		folder.addChild(xobject);
            	}
            	xobject.fireEvent(IXObjectModelListener.SAVE, xobject.getParent(), xobject);
                //new object notify the server root that it needs a refresh (actually not needed for this but a good time to do it)
                xobject.getServerRoot().fireEvent(IXObjectModelListener.NEED_REFRESH, null, xobject.getServerRoot());
            } else {
                //existing object saved
                xobject.fireEvent(IXObjectModelListener.SAVE, xobject.getParent(), xobject);
            }
            state=0;

		} catch (Exception e) {
			
			e.printStackTrace();
			ErrorExceptionDialog.openError(
					this.editor.getSite().getShell(), 
					"Error Occured on Saving", 
					"An error occured trying to save the "+IConstants.TALEND+" object instance: "+e.getLocalizedMessage()
					);
			state=1;
		}		
	}
	public int getState() {
		return state;
	}

	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}

	

	/***************************************
	 * Uploading of the document and save
	 * 
	 ***************************************/
	


}