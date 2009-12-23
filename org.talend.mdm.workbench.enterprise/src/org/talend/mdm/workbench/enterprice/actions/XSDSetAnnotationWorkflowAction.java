package org.talend.mdm.workbench.enterprice.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.talend.mdm.workbench.enterprice.dialog.WorkflowAccessDialog;
import org.w3c.dom.Element;

import com.amalto.workbench.actions.UndoAction;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.XSDTreeContentProvider;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDSetAnnotationWorkflowAction extends UndoAction {

	protected WorkflowAccessDialog dlg = null;
	protected String dataModelName;

	public XSDSetAnnotationWorkflowAction(DataModelMainPage page,
			String dataModelName) {
		super(page);
		setImageDescriptor(ImageCache.getImage("icons/annotation.gif"));
		setText("Set the Workflow Access");
		setToolTipText("Set the Workflow Access");
		this.dataModelName = dataModelName;
	}

	public IStatus doAction() {
		try {

			schema = ((XSDTreeContentProvider) page.getTreeViewer()
					.getContentProvider()).getXsdSchema();
			IStructuredSelection selection = (TreeSelection) page
					.getTreeViewer().getSelection();
			XSDComponent xSDCom = null;
			String conceptName = null;
			if (selection.getFirstElement() instanceof Element) {
				TreePath tPath = ((TreeSelection) selection).getPaths()[0];
				for (int i = 0; i < tPath.getSegmentCount(); i++) {
					if (tPath.getSegment(i) instanceof XSDAnnotation)
						xSDCom = (XSDAnnotation) (tPath.getSegment(i));
				}
			}else{
				xSDCom = (XSDComponent) selection.getFirstElement();
			}
			XSDAnnotationsStructure struc = new XSDAnnotationsStructure(xSDCom);
			if (struc.getAnnotation() == null) {
				throw new RuntimeException(
						"Unable to edit an annotation for object of type "
								+ xSDCom.getClass().getName());
			}

			if (xSDCom instanceof XSDElementDeclaration) {
				conceptName = xSDCom.getElement().getAttributes().getNamedItem(
						"name").getNodeValue();
			}
			if (xSDCom instanceof XSDAnnotation
					|| xSDCom instanceof XSDElementDeclaration) {

				dlg = new WorkflowAccessDialog(page.getSite().getShell(),
						getText(), struc.getWorkflows().values(), page,
						conceptName);
				dlg.setBlockOnOpen(true);
				dlg.create();
				// dlg.getShell().setSize(new Point(850,270));
				int ret = dlg.open();
				if (ret == Window.CANCEL) {
					return Status.CANCEL_STATUS;
				}

				struc.setWorkflows(dlg.getAccess());
				page.refresh();
				page.getTreeViewer().expandToLevel(xSDCom, 2);
				page.markDirty();
				return Status.OK_STATUS;

			}

			// struc.setSchematron(0,dlg.getPattern());

			// if (struc.hasChanged()) {
			page.refresh();
			page.getTreeViewer().expandToLevel(xSDCom, 2);
			page.markDirty();
			// }

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(page.getSite().getShell(), "Error",
					"An error occured trying to set the Workflow Access: "
							+ e.getLocalizedMessage());
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
}
