package org.talend.mdm.workbench.enterprice.actions;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDELMDMJob;
import com.amalto.workbench.webservices.XtentisPort;

public class DeleteJobAction extends Action{

	private ServerView server = ServerView.show();
	private TreeObject xobject;

				
	public DeleteJobAction() {
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
        
        if (xobject.getType()!=TreeObject.JOB) return;
       try{ 
	//      Access to server and get port
			XtentisPort port = Util.getPort(
					new URL(xobject.getEndpointAddress()),
					xobject.getUniverse(),
					xobject.getUsername(),
					xobject.getPassword()
			);		
			xobject.getParent().removeChild(xobject);
			String filename= xobject.getDisplayName();//TODO
			
			
			String jobname = filename.substring(0,filename.lastIndexOf("_"));
			String version = filename.substring(filename.lastIndexOf("_")+1);
			//JobInfo info = new JobInfo(jobname,version);
			//delete file in db
			port.deleteMDMJob(new WSDELMDMJob(jobname,version));
			
			String endpointaddress=xobject.getEndpointAddress();
			//String filename= xobject.getDisplayName();//TODO
			String uploadURL = new URL(endpointaddress).getProtocol()+"://"+new URL(endpointaddress).getHost()+":"+new URL(endpointaddress).getPort()+"/datamanager/uploadFile?deletefile="+filename;
			
		
			String remoteFile = Util.uploadFileToAppServer(uploadURL, filename,"admin","talend");	
       }catch(Exception e){
    	   
       }
	}

}
