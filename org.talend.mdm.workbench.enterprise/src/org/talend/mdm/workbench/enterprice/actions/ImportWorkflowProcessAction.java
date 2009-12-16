package org.talend.mdm.workbench.enterprice.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

import com.amalto.workbench.actions.BrowseViewAction;
import com.amalto.workbench.actions.ServerRefreshAction;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSWorkflowDeploy;
import com.amalto.workbench.webservices.WSWorkflowProcessDefinitionUUID;
import com.amalto.workbench.webservices.XtentisPort;

public class ImportWorkflowProcessAction extends Action{

	private ServerView server = ServerView.show();
	private TreeParent xobject;

				
	public ImportWorkflowProcessAction() {
		super();			
		setImageDescriptor(ImageCache.getImage(EImage.PROCESS.getPath()));
		setText("Import a Process Archive");
		setToolTipText("Import a Process Archive");
	}
	
	public void run() {
		if (this.server != null) { //called from ServerView
			ISelection selection = server.getViewer().getSelection();
			xobject = (TreeParent)((IStructuredSelection)selection).getFirstElement();
		}
        
        if (xobject.getType()!=TreeObject.WORKFLOW) return;
       try{ 
	//      Access to server and get port
			XtentisPort port = Util.getPort(
					new URL(xobject.getEndpointAddress()),
					xobject.getUniverse(),
					xobject.getUsername(),
					xobject.getPassword()
			);		
			FileDialog fileDialog = new FileDialog (server.getSite().getShell(), SWT.OPEN);				
			fileDialog.setFilterExtensions (new String[]{"*.bar"});				
			String name=fileDialog.open();
			if(name!=null){
				//after deployed
				String endpointaddress=xobject.getEndpointAddress();
				String uploadURL = new URL(endpointaddress).getProtocol()+"://"+new URL(endpointaddress).getHost()+":"+new URL(endpointaddress).getPort()+"/datamanager/uploadFile";
				String remoteFile = Util.uploadFileToAppServer(uploadURL, name,"admin","talend");
				//port.workflowgetGetProcessInstances(new WSWorkflowProcessDefinitionUUID("",""));
				WSWorkflowProcessDefinitionUUID uuid=port.workflowDeploy(new WSWorkflowDeploy(remoteFile));
				TreeObject obj = new TreeObject(
						uuid.getProcessName()+"_"+uuid.getProcessVersion(),
						xobject.getServerRoot(),
						TreeObject.WORKFLOW_PROCESS,
						uuid,
						null   //no storage to save space
				);
				xobject.addChild(obj);
				BrowseViewAction action=new BrowseViewAction(server);
				action.setObject(obj);
				action.run();				
				
				//refresh server tree
				new ServerRefreshAction(server,xobject.getServerRoot()).run();
			}
       }catch(Exception e){
    	   e.printStackTrace();
       }
	}
}
