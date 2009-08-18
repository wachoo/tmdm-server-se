package com.amalto.workbench.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.dialogs.AnnotationOrderedListsDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDSetAnnotationWriteAction extends UndoAction {

	protected AnnotationOrderedListsDialog dlg = null;

	public XSDSetAnnotationWriteAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage("icons/annotation.gif"));
		setText("Set the Roles with Write Access");
		setToolTipText("Set the Roles That Have Write Access");
	}

	public IStatus doAction() {
		try {

			IStructuredSelection selection = (IStructuredSelection) page
					.getTreeViewer().getSelection();
			XSDAnnotationsStructure struc = new XSDAnnotationsStructure(
					(XSDComponent) selection.getFirstElement());
			if (struc.getAnnotation() == null) {
				throw new RuntimeException(
						"Unable to edit an annotation for object of type "
								+ selection.getFirstElement().getClass()
										.getName());
			}

			dlg = new AnnotationOrderedListsDialog(new ArrayList(struc
					.getWriteAccesses().values()), new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
				}

				public void widgetSelected(SelectionEvent e) {
					dlg.close();
				}
			}, page.getSite().getShell(),
					"Set The Roles That Have Write Access", "Roles", page,
					AnnotationOrderedListsDialog.AnnotationWrite_ActionType,
					null);

			dlg.setBlockOnOpen(true);
			int ret = dlg.open();
			if (ret == Window.CANCEL) {
	            return Status.CANCEL_STATUS;
			}

			struc.setAccessRole(dlg.getXPaths(), dlg.getRecursive(),
					(IStructuredContentProvider) page.getTreeViewer()
							.getContentProvider(), "X_Write");

			if (struc.hasChanged()) {
				page.refresh();
				page.getTreeViewer().expandToLevel(selection.getFirstElement(),
						2);
				page.markDirty();
			}

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(page.getSite().getShell(), "Error",
					"An error occured trying to set the Write Access: "
							+ e.getLocalizedMessage());
            return Status.CANCEL_STATUS;
		}
        return Status.OK_STATUS;
	}

	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}

}