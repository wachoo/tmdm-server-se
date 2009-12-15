package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PartInitException;
import org.talend.mdm.workbench.enterprice.editors.RoutingEngineV2BrowserMainPage;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.editors.XObjectBrowser;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.XtentisPort;

public class SubscriptionEngineAvailableModel extends AbstractAvailableModel {

	@Override
	public void addTreeObjects(XtentisPort port, IProgressMonitor monitor,
			TreeParent serverRoot) {
		TreeObject  engine = new TreeObject(
				EXtentisObjects.SubscriptionEngine.getDisplayName(),
				serverRoot,
				TreeObject.SUBSCRIPTION_ENGINE,
				null,
				null
		);
		serverRoot.addChild(engine);
		monitor.worked(1);

	}

	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPage(TreeObject xobject, XObjectBrowser editor) {
		switch(xobject.getType()){
       	case TreeObject.SUBSCRIPTION_ENGINE:
       		try {
				editor.addPage(new RoutingEngineV2BrowserMainPage(editor));
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
       		break;
		}
	}
}
