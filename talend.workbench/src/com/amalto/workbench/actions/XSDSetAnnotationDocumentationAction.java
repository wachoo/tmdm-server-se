package com.amalto.workbench.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDSetAnnotationDocumentationAction extends UndoAction{	
	
	public XSDSetAnnotationDocumentationAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage( "icons/annotation.gif"));
		setText("Set the Documentation");
		setToolTipText("Set the Documentation for this element");
	}
	
	public IStatus doAction() {
		try {
			
            IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
            XSDAnnotationsStructure struc = new XSDAnnotationsStructure((XSDComponent)selection.getFirstElement());
            if (struc.getAnnotation() == null) {
            	throw new RuntimeException("Unable to edit an annotation for object of type "+selection.getFirstElement().getClass().getName());
            }
            
       		InputDialog id = new InputDialog(
       				page.getSite().getShell(),
       				"Set the Documentation",
       				"Enter a text for the documentation - Leave BLANK to delete the Documentation",
       				struc.getDocumentation(),
       				new IInputValidator() {
       					public String isValid(String newText) {
       						return null;
       					};
       				}
       		);
            
       		id.setBlockOnOpen(true);
       		int ret = id.open();
       		if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
       		}
       		
       		struc.setDocumentation("".equals(id.getValue()) ? null : id.getValue());
       		
       		if (struc.hasChanged()) {
       			page.refresh();
       			page.getTreeViewer().expandToLevel(selection.getFirstElement(), 2);
       			page.markDirty();
       		}
       		
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to set the Documentation: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}
		
        return Status.OK_STATUS;
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}