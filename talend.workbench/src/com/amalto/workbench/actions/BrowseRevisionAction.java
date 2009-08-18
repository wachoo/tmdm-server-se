package com.amalto.workbench.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.amalto.workbench.editors.XObjectRevisionBrowser;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.views.ServerView;

public class BrowseRevisionAction extends Action {
	private ServerView server = null;

	
	public BrowseRevisionAction(ServerView serverView) {
		super();
		this.server = serverView;			
		setImageDescriptor(ImageCache.getImage(EImage.SEARCH.getPath()));
		setText("Browse Revision");
		setToolTipText("Browse revision");
	}
	
	public void run() {
		try {
			super.run();
			
	        ISelection selection = this.server.getViewer().getSelection();
	        TreeObject xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
	        //String name=xobject.getDisplayName().replaceAll("\\[.*\\]", "");
       		server.getSite().getWorkbenchWindow().getActivePage().openEditor(
                    new XObjectBrowserInput(xobject,xobject.getDisplayName()),
                    XObjectRevisionBrowser.ID
           	);
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					server.getSite().getShell(),
					"Error", 
					"An error occured trying to open the view browser: "+e.getLocalizedMessage()
			);
		}		
	}
}
