/*
 * Created on 15 nov. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XtentisServerObjectsRetriever;
import com.amalto.workbench.utils.IConstants;

public class ServerRefreshJob extends Job {

    private String server;
    private String username;
    private String password;
    private String universe;
    private TreeParent serverRoot = null;
    
    public ServerRefreshJob(String server, String username, String password,String universe) {
        super("Refreshing "+IConstants.TALEND+" Objects");
        this.server = server;
        this.username = username;
        this.password = password;
        this.universe=universe;
    }

    protected  IStatus run(IProgressMonitor monitor) {
        //FIXME: remove hardcoding of user
        XtentisServerObjectsRetriever retriever = new XtentisServerObjectsRetriever(server,username,password,universe);
        try {
            retriever.run(monitor);
            serverRoot = retriever.getServerRoot();
            return Status.OK_STATUS;
        } catch (InterruptedException ie) {
            //MessageDialog.openWarning(view.getSite().getShell(), "Canceled", "The Refresh of the "+IConstants.TALEND+" Server Objects was Canceled");
            return Status.CANCEL_STATUS;
        } catch (Exception e) {
            e.printStackTrace();
            //MessageDialog.openError(view.getSite().getShell(), "Error", "Error while refreshing the "+IConstants.TALEND+" Server Objects: "+e.getLocalizedMessage());
            return Status.CANCEL_STATUS;
        }
    }

    public TreeParent getServerRoot() {
        return serverRoot;
    }


}
