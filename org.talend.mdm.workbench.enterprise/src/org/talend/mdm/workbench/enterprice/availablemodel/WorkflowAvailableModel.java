package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PartInitException;
import org.talend.mdm.workbench.enterprice.actions.GenerateWorkflowDefaultTransformerAction;
import org.talend.mdm.workbench.enterprice.actions.ImportWorkflowProcessAction;
import org.talend.mdm.workbench.enterprice.editors.WorkflowBrowserMainPage;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.editors.XObjectBrowser;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.WSWorkflowGetProcessDefinitions;
import com.amalto.workbench.webservices.WSWorkflowProcessDefinitionUUID;
import com.amalto.workbench.webservices.WSWorkflowProcessDefinitionUUIDArray;
import com.amalto.workbench.webservices.XtentisPort;

public class WorkflowAvailableModel extends AbstractAvailableModel {

	
	public void addTreeObjects(XtentisPort port,IProgressMonitor monitor,TreeParent serverRoot) {
		try {
			
			TreeParent workflow=new TreeParent(
					EXtentisObjects.Workflow.getDisplayName(),
					serverRoot,
					TreeObject.WORKFLOW,
					null,
					null);
			WSWorkflowProcessDefinitionUUIDArray array=port.workflowGetProcessDefinitions(new WSWorkflowGetProcessDefinitions(".*"));
			if(array!=null && array.getWsWorkflowProcessDefinitions()!=null){
				for (WSWorkflowProcessDefinitionUUID id:array.getWsWorkflowProcessDefinitions()){
					TreeObject obj = new TreeObject(
							id.getProcessName()+"_"+id.getProcessVersion(),
							serverRoot,
							TreeObject.WORKFLOW_PROCESS,
							id,
							null   //no storage to save space
					);
					workflow.addChild(obj);
				}
			}
			serverRoot.addChild(workflow);
			monitor.worked(1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		// TODO Auto-generated method stub
		switch(xobject.getType()){
		case TreeObject.WORKFLOW:
			manager.add(new ImportWorkflowProcessAction());
			break;
		case TreeObject.WORKFLOW_PROCESS:
			manager.add(new GenerateWorkflowDefaultTransformerAction());
			break;
		}
	}
	
	@Override
	public void addPage(TreeObject xobject, XObjectBrowser editor) {
		switch(xobject.getType()){
       	case TreeObject.WORKFLOW_PROCESS:
       		try {
				editor.addPage(new WorkflowBrowserMainPage(editor));
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       		break;
		}
		
	}
}
