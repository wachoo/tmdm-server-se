package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.WSGetRolePKs;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.XtentisPort;

public class RoleAvailableModel extends AbstractAvailableModel {

	@Override
	public void addTreeObjects(XtentisPort port, IProgressMonitor monitor,
			TreeParent serverRoot) {
//		Roles
		WSRolePK[] rolePKs = null;
		boolean hasRoles = true;
		try {
			rolePKs = port.getRolePKs(new WSGetRolePKs("*")).getWsRolePK();
		} catch (Exception e) {
			System.out.println("NO ROLES");
			hasRoles = false;
		}
		TreeParent roles = null;
		if (hasRoles) {
			roles = new TreeParent(EXtentisObjects.Role.getDisplayName(),serverRoot,TreeObject.ROLE,null,null);
			if (rolePKs!=null) {
				monitor.subTask("Loading Roles");
				for (int i = 0; i < rolePKs.length; i++) {
					String name =rolePKs[i].getPk();
					TreeObject obj = new TreeObject(
							name,
							serverRoot,
							TreeObject.ROLE,
							new WSRolePK(name),
							null   //no storage to save space
					);
					roles.addChild(obj);
				}
			}
			serverRoot.addChild(roles);
			monitor.worked(1);
		}


	}

	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		// TODO Auto-generated method stub
		
	}

}
