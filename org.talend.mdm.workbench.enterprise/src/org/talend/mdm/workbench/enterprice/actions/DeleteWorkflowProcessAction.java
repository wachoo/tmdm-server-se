package org.talend.mdm.workbench.enterprice.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.amalto.workbench.actions.ServerRefreshAction;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSWorkflowProcessDefinitionUUID;
import com.amalto.workbench.webservices.WSWorkflowUnDeploy;
import com.amalto.workbench.webservices.XtentisPort;

public class DeleteWorkflowProcessAction extends Action{

	private ServerView server = ServerView.show();
	private TreeObject xobject;

				
	public DeleteWorkflowProcessAction() {
		super();
			
		setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
		setText("Delete");
		setToolTipText("Delete");
	}
	
	public void run() {
		if (this.server != null) { //called from ServerView
			ISelection selection = server.getViewer().getSelection();
			xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
		}
        
        if (xobject.getType()!=TreeObject.WORKFLOW_PROCESS) return;
       try{ 
	//      Access to server and get port
			XtentisPort port = Util.getPort(
					new URL(xobject.getEndpointAddress()),
					xobject.getUniverse(),
					xobject.getUsername(),
					xobject.getPassword()
			);		
			xobject.getParent().removeChild(xobject);
			WSWorkflowProcessDefinitionUUID uuid=(WSWorkflowProcessDefinitionUUID)xobject.getWsKey();
			port.workflowUnDeploy(new WSWorkflowUnDeploy(uuid));
			//refresh server tree
			new ServerRefreshAction(server,xobject.getServerRoot()).run();
       }catch(Exception e){
    	   e.printStackTrace();
       }
	}
}
