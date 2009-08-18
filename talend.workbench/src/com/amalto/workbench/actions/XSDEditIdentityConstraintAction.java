package com.amalto.workbench.actions;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;

public class XSDEditIdentityConstraintAction extends UndoAction{

	protected XSDIdentityConstraintDefinition constraint;
	
	public XSDEditIdentityConstraintAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage( "icons/edit_obj.gif"));
		setText("Edit Key");
		setToolTipText("Edit a Key");
	}
	
	public IStatus doAction() {
		try {
			
            ISelection selection = page.getTreeViewer().getSelection();
            constraint = (XSDIdentityConstraintDefinition)((IStructuredSelection)selection).getFirstElement();
            String oldName = constraint.getName();
            
            
       		InputDialog id = new InputDialog(
       				page.getSite().getShell(),
       				"Edit Key",
       				"Enter a new Name for the Key",
       				oldName,
       				new IInputValidator() {
       					public String isValid(String newText) {
       						if ((newText==null) || "".equals(newText)) return "The Concept Name cannot be empty";
       						XSDSchema schema = XSDEditIdentityConstraintAction.this.constraint.getSchema();
       						EList list = schema.getIdentityConstraintDefinitions();
       						for (Iterator iter = list.iterator(); iter.hasNext(); ) {
								XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
								if (icd.getName().equals(newText)) return "This Key already exists";
							}
       						return null;
       					};
       				}
       		);
            
       		id.setBlockOnOpen(true);
       		int ret = id.open();
       		if (ret == Dialog.CANCEL){
                return Status.CANCEL_STATUS;
       		}
       		
       		constraint.setName(id.getValue());
       		constraint.updateElement();
       		       	           		
       		page.refresh();
       		page.markDirty();
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to edit a Concept: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}
        return Status.OK_STATUS;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}