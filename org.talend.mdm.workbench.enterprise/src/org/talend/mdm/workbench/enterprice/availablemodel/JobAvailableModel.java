package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PartInitException;
import org.talend.mdm.workbench.enterprice.actions.DeleteJobAction;
import org.talend.mdm.workbench.enterprice.actions.GenerateJobDefaultTransformerAction;
import org.talend.mdm.workbench.enterprice.actions.ImportTISJobAction;
import org.talend.mdm.workbench.enterprice.editors.JobMainPage;
import org.talend.mdm.workbench.enterprice.editors.WorkflowBrowserMainPage;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.WSMDMJob;
import com.amalto.workbench.webservices.WSMDMNULL;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.XtentisPort;

public class JobAvailableModel extends AbstractAvailableModel {

	@Override
	public void addTreeObjects(XtentisPort port,IProgressMonitor monitor, TreeParent serverRoot) {
		try{
		//Job
		TreeParent jobs = new TreeParent(EXtentisObjects.JobRegistry.getDisplayName(),serverRoot,TreeObject.JOB_REGISTRY,null,null);
		WSMDMJob[] jobPKs = port.getMDMJob(new WSMDMNULL()).getWsMDMJob();
		if (jobPKs!=null) {
			monitor.subTask("Loading Jobs");
			for (int i = 0; i < jobPKs.length; i++) {
				String name = jobPKs[i].getJobName()+"_"+jobPKs[i].getJobVersion();
				TreeObject obj = new TreeObject(
						name,
						serverRoot,
						TreeObject.JOB,
						new WSViewPK(name),
						null   //no storage to save space
				);
				jobs.addChild(obj);
			}
		}
		serverRoot.addChild(jobs);
		monitor.worked(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		switch(xobject.getType()){
			case TreeObject.JOB:
				manager.add(new DeleteJobAction());
				manager.add(new GenerateJobDefaultTransformerAction());
				break;
			case TreeObject.JOB_REGISTRY:
				
				manager.add(new ImportTISJobAction());
				break;	
		}
	}

	@Override
	public void addPage(TreeObject xobject, XObjectEditor editor) {
		switch(xobject.getType()){
       	case TreeObject.JOB:
       		try {
				editor.addPage(new JobMainPage(editor));
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       		break;
		}
	}
}
