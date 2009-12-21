package org.talend.mdm.workbench.enterprice.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDElementDeclaration;

import com.amalto.workbench.actions.UndoAction;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDDeleteValidationRulesAction extends UndoAction{

	private XSDAnnotation xsdElem = null;
	
	public XSDDeleteValidationRulesAction(DataModelMainPage page) {
		super(page);
		//this.page = page;
		setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
		setText("Delete All Validation Rules");
		setToolTipText("Delete All Validation Rules");
	}
	/**
	 * Author: Fiu 
	 * this function is meant to support deletion on key press
	 * which can pass a specific object to be deleted
	 * @param toDel
	 */
	public void run(Object toDel)
	{
		if (!(toDel instanceof XSDAnnotation)) {
			return;
		}
		xsdElem = (XSDAnnotation) toDel;
		if(MessageDialog.openConfirm(null, "Confirm", "Are you sure want to delete all Validataion Rules?")){
			super.run();
		}
	}
	
	public IStatus doAction() {
		try {
			if(!MessageDialog.openConfirm(null, "Confirm", "Are you sure you want to delete all Validataion Rules?")){
				return Status.CANCEL_STATUS;
			}
            // xsdElem is to support the multiple delete action on key press,
			// which each delete action on element must be explicit passed a xsdElem to
			// delete
			XSDComponent decl = xsdElem;
            if (decl == null){
                ISelection selection = page.getTreeViewer().getSelection();
                if(((IStructuredSelection)selection).getFirstElement() instanceof XSDAnnotation){
                	decl = (XSDAnnotation)((IStructuredSelection)selection).getFirstElement();
                }
                if(((IStructuredSelection)selection).getFirstElement() instanceof XSDElementDeclaration){
                	decl = (XSDElementDeclaration)((IStructuredSelection)selection).getFirstElement();
                }
            }
            XSDComponent elem=decl instanceof XSDAnnotation? (XSDComponent)decl.getContainer() :decl;
            XSDAnnotationsStructure struc =new XSDAnnotationsStructure(elem);
            
            struc.setSchematrons(new ArrayList<String>());


            schema.update();
            
            xsdElem = null;
       		page.refresh();
       		page.markDirty();
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to remove the Element: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}
        return Status.OK_STATUS;
	}

}