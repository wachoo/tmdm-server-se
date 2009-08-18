package com.amalto.workbench.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class XSDDeleteXPathAction extends UndoAction{

	private XSDXPathDefinition xsdPath = null;
	
	public XSDDeleteXPathAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
		setText("Delete Field");
		setToolTipText("Delete a Field");
	}
	
	public void run(Object toDel) {
		if (!(toDel instanceof XSDXPathDefinition)) {
			return;
		}
		xsdPath = (XSDXPathDefinition) toDel;
		run();
	}
	
	public IStatus doAction() {
		try {
			
            // xsdPath is to support the multiple delete action on key press,
			// which each delete action on xpath must be explicit passed a xsd path to
			// delete
            XSDXPathDefinition xpath = xsdPath;
            if (xpath == null) {
				ISelection selection = page.getTreeViewer().getSelection();
				xpath = (XSDXPathDefinition) ((IStructuredSelection) selection)
						.getFirstElement();
			}
            XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) xpath.getContainer();
            if (icd == null)  return Status.CANCEL_STATUS;
            
            if (xpath.getVariety().equals(XSDXPathVariety.SELECTOR_LITERAL)) {
    			MessageDialog.openError(
    					page.getSite().getShell(),
    					"Error", 
    					"The Selector cannot be deleted"
    			);
                return Status.CANCEL_STATUS;
            }
            
            if (icd.getFields().size()==1) {
    			MessageDialog.openError(
    					page.getSite().getShell(),
    					"Error", 
    					"The Key must contain at least one field"
    			);
                return Status.CANCEL_STATUS;          	
            }
           
            icd.getFields().remove(xpath);
            icd.updateElement();
            xsdPath = null;
       		page.refresh();
       		page.markDirty();
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to remove a Field: "+e.getLocalizedMessage()
			);
			
            return Status.CANCEL_STATUS;
		}	
        return Status.OK_STATUS;
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	
    public void setXSDTODel(XSDXPathDefinition elem) {
    	xsdPath = elem;
	}

}