package com.amalto.workbench.actions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPage;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSPutDataCluster;

public class DataClusterImportAction extends Action{
	private ServerView view = null;
	private TreeObject xobject = null;
	private IWorkbenchPage page = null;
	private String filename;
	private String server;
	
	
	public DataClusterImportAction(TreeObject xobject, IWorkbenchPage page) {
		super();
		this.xobject = xobject;
		this.page = page;
		setDetails();
	}
	
	public DataClusterImportAction(ServerView view) {
		super();
		this.view = view;
		setDetails();
	}
	
	private void setDetails() {
		setImageDescriptor(ImageCache.getImage( EImage.IMPORT.getPath()));
		setText("Import");
		setToolTipText("Import Data Cluster");		
	}
	
	public void run() {
		try {
			super.run();
			if (this.view != null) { //called from ServerView
				ISelection selection = view.getViewer().getSelection();
				xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
			}
            
            //if (!xobject.isXObject()) return;

			FileDialog fileDialog = new FileDialog (view.getSite().getShell(), SWT.OPEN);				
			//fileDialog.setFilterNames(new String[]{"__contents__.xml"});
			fileDialog.setText("Please select file to import");
			fileDialog.setFilterExtensions(new String[]{"__contents__.xml"});
			filename=fileDialog.open();
			if(filename!=null && new File(filename).exists()){
				String url=xobject.getServerRoot().getEndpointAddress();
				server=null;
				try {
					 server= new URL(url).getHost();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}		
				
				Job job=new Job("Import data cluster ..."){
					@Override
					public IStatus run(IProgressMonitor monitor) {	
						try{
							//import datacluster data
							File f=new File(filename);
							String dataCluster=f.getParentFile().getName();

							monitor.beginTask("Import data cluster " + dataCluster +" ..." ,IProgressMonitor.UNKNOWN);
							Util.importDataCluster(filename,server,monitor);
							
							//create datacluster
							if(!"db".equalsIgnoreCase(dataCluster)){
								
								monitor.subTask("Creating data cluster " + dataCluster +" ..." );
								WSDataCluster dc = new WSDataCluster(
										(String)dataCluster,
										"",
										""												//vocabulary
								);
								Util.getPort(xobject).putDataCluster(new WSPutDataCluster(dc));
							}
							monitor.done();							
							return Status.OK_STATUS;
						}catch(Exception e){
							e.printStackTrace();
							return Status.CANCEL_STATUS;
						}finally{
							monitor.subTask("Refreshing server ..." );
							new ServerRefreshAction(view,xobject.getServerRoot()).run();
						}
					}			
				};
				job.setPriority(Job.INTERACTIVE);
				job.schedule();				
			}
		}catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					view.getSite().getShell(),
					"Error", 
					"An error occured trying to Import Data Cluster: "+e.getLocalizedMessage()
			);
		}finally{
			
		}		
	}
}
