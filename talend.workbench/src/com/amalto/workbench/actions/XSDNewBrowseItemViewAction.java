package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDElementDeclaration;

import com.amalto.workbench.dialogs.AddBrowseItemsWizard;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDNewBrowseItemViewAction extends Action{
	
	private DataModelMainPage page;
	private List<XSDElementDeclaration> declList = new ArrayList<XSDElementDeclaration>();
	
	public XSDNewBrowseItemViewAction(DataModelMainPage page) {
		super();

		this.page = page;
		setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
		setText("Generate default Browse items views");
		setToolTipText("Generate default Browse items views");
	}
	
	public void run() {
		IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
		declList.clear();
		List list = selection.toList();
		for (Object obj: list)
		{
			if (obj instanceof XSDElementDeclaration)
			{
				if (Util.getParent(obj) == obj)
					declList.add((XSDElementDeclaration)obj);
			}
		}
		AddBrowseItemsWizard wizard = new AddBrowseItemsWizard(page, declList);
		WizardDialog dialog = new WizardDialog(page.getSite().getShell(), wizard);
		dialog.open(); 
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
}
