package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.PartInitException;
import org.talend.mdm.workbench.enterprice.editors.TransformerMainPage;
import org.talend.mdm.workbench.enterprice.editors.UniverseMainPage;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.WSGetUniversePKs;
import com.amalto.workbench.webservices.WSUniversePK;
import com.amalto.workbench.webservices.XtentisPort;

public class UniverseAvailableModel extends AbstractAvailableModel {

	@Override
	public void addTreeObjects(XtentisPort port,IProgressMonitor monitor, TreeParent serverRoot) {
		try{
		TreeParent Universes = null;
		//if (hasUniverses) {
		WSUniversePK[] universePKs=universePKs = port.getUniversePKs(new WSGetUniversePKs("*")).getWsUniversePK();
			Universes = new TreeParent(EXtentisObjects.Universe.getDisplayName(),serverRoot,TreeObject.UNIVERSE,null,null);
			if (universePKs!=null) {
				monitor.subTask("Loading Universes");
				for (int i = 0; i < universePKs.length; i++) {
					String id =universePKs[i].getPk();
					if (!id.startsWith("UNIVERSE-REVISION")) {
					TreeObject obj = new TreeObject(
							id,
							serverRoot,
							TreeObject.UNIVERSE,
							new WSUniversePK(id),
							null   //no storage to save space
					);
					Universes.addChild(obj);
				 }
				}
			}
			serverRoot.addChild(Universes);
			monitor.worked(1);
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
		case TreeObject.UNIVERSE:
			try {
				editor.addPage(new UniverseMainPage(editor));
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   		break;	
		}
	}
}
