package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.availablemodel.IAvailableModel;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.webservices.WSGetTransformerV2PKs;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.XtentisPort;

public class TransformerAvailableModel extends AbstractAvailableModel {

	@Override
	public void addTreeObjects(XtentisPort port, IProgressMonitor monitor,
			TreeParent serverRoot) {
		WSTransformerV2PK[] transformerPKs = null;
		boolean hasTransformers = true;
		try {
			
			transformerPKs = port.getTransformerV2PKs(new WSGetTransformerV2PKs("")).getWsTransformerV2PK();
		} catch (Exception e) {
			System.out.println("No Transformers");
			// This server IS old
			hasTransformers = false;
		}
		TreeParent transformers = null;
		if (hasTransformers) {
			transformers = new TreeParent(EXtentisObjects.Transformer.getDisplayName(),serverRoot,TreeObject.TRANSFORMER,null,null);
			if (transformerPKs!=null) {
				monitor.subTask("Loading Transfomers");
				for (int i = 0; i < transformerPKs.length; i++) {
					String id =transformerPKs[i].getPk();
					TreeObject obj = new TreeObject(
							id,
							serverRoot,
							TreeObject.TRANSFORMER,
							new WSTransformerV2PK(id),
							null   //no storage to save space
					);
					transformers.addChild(obj);
				}
			}
			serverRoot.addChild(transformers);
			monitor.worked(1);
			
		}

	}

	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		// TODO Auto-generated method stub
		
	}

}
