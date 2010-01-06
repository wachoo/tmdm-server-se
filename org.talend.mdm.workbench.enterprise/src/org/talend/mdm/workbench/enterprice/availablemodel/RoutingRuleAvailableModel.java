package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PartInitException;
import org.talend.mdm.workbench.enterprice.editors.RoutingRuleMainPage;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.WSGetRoutingRulePKs;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.XtentisPort;

public class RoutingRuleAvailableModel extends AbstractAvailableModel {

	@Override
	public void addTreeObjects(XtentisPort port, IProgressMonitor monitor,
			TreeParent serverRoot) {
		WSRoutingRulePK[] routingRulePKs = null;
		boolean hasRoutingRules = true;
		try {
			routingRulePKs = port.getRoutingRulePKs(new WSGetRoutingRulePKs("")).getWsRoutingRulePKs();
		} catch (Exception e) {
			System.out.println("NO ROUTING RULES");
			// This server does not handle roles (pre 2.13)
			hasRoutingRules = false;
		}
		TreeParent rules = null;
		if (hasRoutingRules) {
			rules = new TreeParent(EXtentisObjects.RoutingRule.getDisplayName(),serverRoot,TreeObject.ROUTING_RULE,null,null);
			if (routingRulePKs!=null) {
				monitor.subTask("Loading Routing Rules");
				for (int i = 0; i < routingRulePKs.length; i++) {
					String id =routingRulePKs[i].getPk();
					TreeObject obj = new TreeObject(
							id,
							serverRoot,
							TreeObject.ROUTING_RULE,
							new WSRoutingRulePK(id),
							null   //no storage to save space
					);
					rules.addChild(obj);
				}
			}
			serverRoot.addChild(rules);
			monitor.worked(1);
		}

	}

	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addPage(TreeObject xobject, XObjectEditor editor) {
		switch(xobject.getType()){
		case TreeObject.ROUTING_RULE:
	        try {
				editor.addPage(new RoutingRuleMainPage(editor));
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		break;
		}
	}
}
