package com.amalto.workbench.actions;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XtentisServerObjectsRetriever;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.views.ServerView;

public class ServerRefreshAction extends Action {

	protected ServerView view = null;
	protected String server = null;
    protected TreeParent serverRoot = null;
    protected TreeParent forcedRoot = null;
    protected TreeParent newRoot = null;
	
	
	
	public ServerRefreshAction(ServerView view, TreeParent forcedRoot) {
		this(view);
		this.forcedRoot = forcedRoot;
	}
		
	public ServerRefreshAction(ServerView view) {
		super();
		this.view = view;
		setImageDescriptor(ImageCache.getImage( EImage.REFRESH.getPath()));
		setText("Refresh");
		setToolTipText("Refresh the "+IConstants.TALEND+" Server Objects");
	}
	
	public void run() {
		try {
			if (forcedRoot == null)
				//get it dynamically
				serverRoot = (TreeParent)((IStructuredSelection)view.getViewer().getSelection()).getFirstElement();
			else
				serverRoot = forcedRoot;
			server = (String)serverRoot.getWsKey();  //we are at server root
			
            XtentisServerObjectsRetriever retriever = new XtentisServerObjectsRetriever(
                	server,
                	serverRoot.getUsername(),
                	serverRoot.getPassword(),
                	serverRoot.getUser().getUniverse()
                );
            
            retriever.run(new NullProgressMonitor());
			ServerRefreshAction.this.serverRoot.synchronizeWith(retriever.getServerRoot());
			LocalTreeObjectRepository.getInstance().switchOnListening();
			ServerView.show().getViewer().refresh();
//			Job refreshJob = new ServerRefreshJob(server,serverRoot.getUsername(),serverRoot.getPassword(),serverRoot.getUser().getUniverse()); 
//            refreshJob.setPriority(Job.INTERACTIVE);
//			refreshJob.schedule();
//            refreshJob.addJobChangeListener(new IJobChangeListener() {
//                public void aboutToRun(IJobChangeEvent event) {
//                    //view.getViewer().getControl().setEnabled(false);
//                }
//                public void awake(IJobChangeEvent event) {}
//                public void done(IJobChangeEvent event) {
//                    if (event.getResult().equals(Status.OK_STATUS)) {
//                        //view.getViewer().getControl().setEnabled(true);
//                        ServerRefreshJob job = (ServerRefreshJob)event.getJob();
//                        ServerRefreshAction.this.newRoot = job.getServerRoot();
//                        ServerRefreshAction.this.view.getViewer().getControl().getDisplay().asyncExec(new Runnable() {
//							public void run() {
//								ServerRefreshAction.this.serverRoot.synchronizeWith(ServerRefreshAction.this.newRoot);
//								LocalTreeObjectRepository.getInstance().switchOnListening();
//							}
//                        });
//                        
//                        //ServerRefreshAction.this.view.getViewer().refresh();
//                        //ServerRefreshAction.this.view.getViewer().expandToLevel(serverRoot, 1);
//                    } else {
//                        MessageDialog.openError(
//                                view.getSite().getShell(), 
//                                "Error", 
//                                "The refresh of the "+IConstants.TALEND+" Server "+(String)ServerRefreshAction.this.serverRoot.getWsKey()+" failed!"
//                         );
//                    }
//                }
//                public void running(IJobChangeEvent event) {}
//                public void scheduled(IJobChangeEvent event) {}
//                public void sleeping(IJobChangeEvent event) {}
//            });
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(view.getSite().getShell(), "Error", "Error while refreshing the "+IConstants.TALEND+" Server Objects: "+e.getLocalizedMessage());
		}
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	

}