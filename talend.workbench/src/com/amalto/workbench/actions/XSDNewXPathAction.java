package com.amalto.workbench.actions;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.SelectFieldDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDNewXPathAction extends UndoAction{

	private XSDIdentityConstraintDefinition icd = null;
	
	public XSDNewXPathAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
		setText("New Field");
		setToolTipText("Create a new Field");
	}
	
	public IStatus doAction() {
		try {
            int index = 0;
            IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
            if (selection.getFirstElement() instanceof XSDIdentityConstraintDefinition) {
            	icd = (XSDIdentityConstraintDefinition) selection.getFirstElement();
            } else if (selection.getFirstElement() instanceof XSDXPathDefinition) {
            	XSDXPathDefinition xpath = (XSDXPathDefinition)selection.getFirstElement();
            	icd = (XSDIdentityConstraintDefinition)xpath.getContainer();
            	if (xpath.getVariety().equals(XSDXPathVariety.FIELD_LITERAL))
            		index = icd.getFields().indexOf(xpath)+1;
            	else
            		index = 0;
            } else {
            	MessageDialog.openError(this.page.getSite().getShell(),"Error", "huhhh: "+selection.getFirstElement().getClass().getName());
                return Status.CANCEL_STATUS;
            }
            
//       		InputDialog id = new InputDialog(
//       				page.getSite().getShell(),
//       				"New XPath",
//       				"Enter a new XPath to the field",
//       				null,
//       				new IInputValidator() {
//       					public String isValid(String newText) {
//       						if ((newText==null) || "".equals(newText)) return "The XPath cannot be empty";
//       						return null;
//       					};
//       				}
//       		);
            
            List<String> childNames = Util.getChildElementNames(icd.getContainer().getElement());
            SelectFieldDialog id=new SelectFieldDialog(page.getSite().getShell(),"Select one field",childNames,null);
            id.create();            
       		id.setBlockOnOpen(true);
       		int ret = id.open();
       		if (ret == Dialog.CANCEL) {
				return Status.CANCEL_STATUS;
			}
       		String field=id.getField();
       		if(field.length()==0) return Status.CANCEL_STATUS;;
       		XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
       		
       		XSDXPathDefinition xpath = factory.createXSDXPathDefinition();
       		xpath.setValue(field);
       		xpath.setVariety(XSDXPathVariety.FIELD_LITERAL);
       		
       		icd.getFields().add(index,xpath);
       		icd.updateElement();
       		
       		page.refresh();
       		page.getTreeViewer().setSelection(new StructuredSelection(xpath),true);
       		page.markDirty();
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to create a new Field: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}
		
        return Status.OK_STATUS;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}