package org.talend.mdm.workbench.enterprice.actions;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.BackgroundJobStatusType;
import com.amalto.workbench.webservices.WSBackgroundJob;
import com.amalto.workbench.webservices.WSBackgroundJobPK;
import com.amalto.workbench.webservices.WSGetBackgroundJob;
import com.amalto.workbench.webservices.XtentisPort;

public class VersioningProgressAction extends Action{

	protected static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	private Shell shell;
	private XtentisPort port;
	private WSBackgroundJobPK wsJobPK;	
	
	public VersioningProgressAction(Shell shell, XtentisPort port, WSBackgroundJobPK wsJobPK) {
		super();
		this.shell = shell;
		this.port = port;
		this.wsJobPK = wsJobPK;
		setImageDescriptor(ImageCache.getImage( "icons/save_edit.gif"));
	}
	
	public void run() {
        
		try {

			new ProgressMonitorDialog(shell).run(
					true,  //fork 
					true, 	//cancelable
					new ProcessFileRunnable(
							shell,
							port,
							wsJobPK
					)
			);		
    
		} catch (Exception e) {
			MessageDialog.openError(
					shell,
					"Error Occured", 
					"An error occured trying to perform a versionning operation:  "
					+(e.getCause()!=null ? e.getCause().getMessage() : e.getMessage()) 
			);
		}		
	}
	
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	

	
	
	class ProcessFileRunnable implements IRunnableWithProgress{
		
		protected VersioningProgressAction parent;
		protected Shell runnableShell;
		protected XtentisPort runnablePort;
		protected WSBackgroundJobPK runnableWsJobPK;
		
		protected WSBackgroundJob job = null;
		
		public ProcessFileRunnable(
				Shell shell,
				XtentisPort port, 
				WSBackgroundJobPK wsJobPK
			) {
			this.runnableShell = shell;
			this.runnablePort = port;
			this.runnableWsJobPK = wsJobPK;
		}
	
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			try {
				
				monitor.beginTask("Starting Versioning Operation", 4);
	
				/******************************************
				 * Watch the Background Job
				 ******************************************/
				
				boolean firstTime = true;
				
				do {
					
					if (firstTime) {
						Thread.sleep(1500L);
						firstTime = false;
					} else {
						Thread.sleep(5000L);
					}
					
					
					if (monitor.isCanceled()) {
						throw new InterruptedException("User Cancel");
					}
					
					job  =  runnablePort.getBackgroundJob(new WSGetBackgroundJob(runnableWsJobPK.getPk()));	
					monitor.subTask(job.getMessage());

				} while (
							job.getStatus().equals(BackgroundJobStatusType.RUNNING) 
							|| job.getStatus().equals(BackgroundJobStatusType.SCHEDULED)
							);

				if (job.getStatus().equals(BackgroundJobStatusType.STOPPED)) 
					throw new XtentisException("The job was stopped. "+job.getMessage());
				
				monitor.worked(1);			
				monitor.done();
				
				runnableShell.getDisplay().syncExec (new Runnable () {
					public void run () {
							MessageDialog.openInformation(
									runnableShell,
									"Versioning", 
									"The operation completed successfully.\n\n"+job.getMessage()
							);
					}
				});
				
				
			} catch (InterruptedException ie) {
				throw (ie);
			} catch (XtentisException e) {
				throw(new InvocationTargetException(new XtentisException(e.getMessage())));
			} catch (Exception e) {
				throw new InvocationTargetException(new XtentisException("Could not perform the versioning operation. "+e.getClass().getName()+": "+e.getLocalizedMessage()));
			}		
		}//run
		
	}//PutDocumentRunnable


}