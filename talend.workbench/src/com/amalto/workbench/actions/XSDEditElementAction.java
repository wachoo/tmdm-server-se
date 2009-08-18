package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDEditElementAction extends UndoAction{
	
	public XSDEditElementAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage( "icons/edit_obj.gif"));
		setText("Edit Element");
		setToolTipText("Edit an Element");
	}
	
	public IStatus doAction() {
		try {
            ISelection selection = page.getTreeViewer().getSelection();
            XSDElementDeclaration decl = (XSDElementDeclaration)((IStructuredSelection)selection).getFirstElement();
            ArrayList<Object> objList = new ArrayList<Object>();
    		IStructuredContentProvider provider = (IStructuredContentProvider) page
			.getTreeViewer().getContentProvider();
            Object[] objs = Util.getAllObject(page.getSite(), objList, provider);
            String oldName = decl.getName();
            
       		InputDialog id = new InputDialog(
       				page.getSite().getShell(),
       				"Edit Element",
       				"Enter a new Name for the Element",
       				oldName,
       				new IInputValidator() {
       					public String isValid(String newText) {
       						if ((newText==null) || "".equals(newText)) return "The Concept Name cannot be empty";
       						EList list = schema.getElementDeclarations();
       						for (Iterator iter = list.iterator(); iter.hasNext(); ) {
								XSDElementDeclaration d = (XSDElementDeclaration) iter.next();
								if (d.getName().equals(newText)) return "This Concept already exists";
							}
       						return null;
       					};
       				}
       		);
            
       		id.setBlockOnOpen(true);
       		int ret = id.open();
       		if (ret == Window.CANCEL) {
				return Status.CANCEL_STATUS;
			}
       		
       		decl.setName(id.getValue());
       		decl.updateElement();
            Util.updateReference(decl, objs, id.getValue());
       	    //change unique key with new name of concept
       	    EList list = decl.getIdentityConstraintDefinitions();
       	    XSDIdentityConstraintDefinition toUpdate = null;
       	    for (Iterator iter = list.iterator(); iter.hasNext(); ) {
				XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
				if (icd.getName().equals(oldName)) {
					toUpdate = icd;
					break;
				}
			}
       	    if (toUpdate!=null) {
       	    	toUpdate.setName(id.getValue());
       	    	toUpdate.updateElement();
       	    }
       	           		
       		page.refresh();
       		page.markDirty();
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to edit an Element: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}
        return Status.OK_STATUS;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
}