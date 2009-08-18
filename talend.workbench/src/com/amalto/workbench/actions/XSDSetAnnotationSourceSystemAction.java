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

public class XSDSetAnnotationSourceSystemAction extends UndoAction{	
	
	public XSDSetAnnotationSourceSystemAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage( "icons/annotation.gif"));
		setText("Set the Source System");
		setToolTipText("Set the Source System name for the content of this element");
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
       				"Set the Source System",
       				"Enter the name of the Source System for the content of this element - Leave BLANK to delete the Source System",
       				struc.getSourceSystem(),
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
       		
       		
       		struc.setSourceSystem("".equals(id.getValue()) ? null : id.getValue());
       		
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
					"An error occured trying to set a Forign Key: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}
        return Status.OK_STATUS;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}