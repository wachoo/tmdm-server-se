package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PartInitException;
import org.talend.mdm.workbench.enterprice.editors.SynchronizationMainPage;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.WSGetSynchronizationPlanPKs;
import com.amalto.workbench.webservices.WSSynchronizationPlanPK;
import com.amalto.workbench.webservices.XtentisPort;

public class SyncAvailableModel extends AbstractAvailableModel {

	@Override
	public void addTreeObjects(XtentisPort port, IProgressMonitor monitor,
			TreeParent serverRoot) {
		try{
		TreeParent synchronizationPlans = null;
		WSSynchronizationPlanPK[] SynchronizationPlanPKs = port.getSynchronizationPlanPKs(new WSGetSynchronizationPlanPKs("*")).getWsSynchronizationPlanPK();
		//if (hasSynchronizationPlans) {
		synchronizationPlans = new TreeParent(EXtentisObjects.SynchronizationPlan.getDisplayName(),serverRoot,TreeObject.SYNCHRONIZATIONPLAN,null,null);
			if (SynchronizationPlanPKs!=null) {
				monitor.subTask("Loading SynchronizationPlans");
				for (int i = 0; i < SynchronizationPlanPKs.length; i++) {
					String id =SynchronizationPlanPKs[i].getPk();
					TreeObject obj = new TreeObject(
							id,
							serverRoot,
							TreeObject.SYNCHRONIZATIONPLAN,
							new WSSynchronizationPlanPK(id),
							null   //no storage to save space
					);
					synchronizationPlans.addChild(obj);
				}
			}
			monitor.worked(1);
			serverRoot.addChild(synchronizationPlans);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPage(TreeObject xobject, XObjectEditor editor) {
		switch(xobject.getType()){
		case TreeObject.SYNCHRONIZATIONPLAN:
			try {
				editor.addPage(new SynchronizationMainPage(editor));
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		break;	
		}
	}
}
