package com.amalto.workbench.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;

import com.amalto.workbench.dialogs.DataClusterExportDialog;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.views.ServerView;

public class DataClusterExportAction extends Action {
	private ServerView view = null;
	private TreeObject xobject = null;
	private IWorkbenchPage page = null;
	
	
	public DataClusterExportAction(TreeObject xobject, IWorkbenchPage page) {
		super();
		this.xobject = xobject;
		this.page = page;
		setDetails();
	}
	
	public DataClusterExportAction(ServerView view) {
		super();
		this.view = view;
		setDetails();
	}
	
	private void setDetails() {
		setImageDescriptor(ImageCache.getImage( EImage.EXPORT.getPath()));
		setText("Export");
		setToolTipText("Export Data Cluster");		
	}
	
	public void run() {
		try {
			super.run();
			if (this.view != null) { //called from ServerView
				ISelection selection = view.getViewer().getSelection();
				xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
			}
            
            //if (!xobject.isXObject()) return;
            //String dcName=xobject.getDisplayName();
            
            
            DataClusterExportDialog dialog=new DataClusterExportDialog(view.getSite().getShell(),xobject);
            dialog.create();
            //dialog.getShell().setSize(new Point(500,200));
            dialog.open();
		}catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					view.getSite().getShell(),
					"Error", 
					"An error occured trying to Export Data Cluster: "+e.getLocalizedMessage()
			);
		}		
	}
}
