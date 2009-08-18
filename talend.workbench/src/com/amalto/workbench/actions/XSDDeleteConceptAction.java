package com.amalto.workbench.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDTypeDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.XSDTreeContentProvider;
import com.amalto.workbench.utils.Util;

public class XSDDeleteConceptAction extends UndoAction{

	//private DataModelMainPage page = null;
	private XSDElementDeclaration xsdElem = null;
	
	public XSDDeleteConceptAction(DataModelMainPage page) {
		super(page);
		//this.page = page;
		setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
		setText("Delete Concept");
		setToolTipText("Delete a Business Concept");
	}
	
	public void run(Object toDel)
	{
		if (!(toDel instanceof XSDElementDeclaration)) {
			return;
		}
		xsdElem = (XSDElementDeclaration) toDel;
		super.run();
	}
	
	public IStatus doAction() {
		try {
            ((XSDTreeContentProvider)page.getTreeViewer().getContentProvider()).getXSDSchemaAsString();
            // xsdElem is to support the multiple delete action on key press,
			// which each delete action on concept must be explicit passed a xsdElem to
			// delete
            XSDElementDeclaration decl = xsdElem;
            if (decl == null){
                ISelection selection = page.getTreeViewer().getSelection();
                decl = (XSDElementDeclaration)((IStructuredSelection)selection).getFirstElement();
            }
            ArrayList<Object> objList = new ArrayList<Object>();
    		IStructuredContentProvider provider = (IStructuredContentProvider) page
			.getTreeViewer().getContentProvider();
            Object[] objs = Util.getAllObject(page.getSite(), objList, provider);
            Util.deleteReference(decl, objs);
            //backup current Type Definition
            XSDTypeDefinition current = decl.getTypeDefinition();
            System.out.println(schema.contains(xsdElem));
            //remove declaration
            schema.getContents().remove(decl);
    		
            //remove type definition is no more used and type is not built in
//       	    if (	(current.getName()!=null) &&  //anonymous type
//       	    		(!schema.getSchemaForSchemaNamespace().equals(current.getTargetNamespace()))
//       	    	){
//       			if (Util.findElementsUsingType(schema,current.getTargetNamespace(), current.getName()).size()==0)
//       				schema.getContents().remove(current);
//			}
            
            schema.update();
            xsdElem = null;
       		page.refresh();
       		page.markDirty();
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to remove Concept: "+e.getLocalizedMessage()
			);
			
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
    public void setXSDTODel(XSDElementDeclaration elem) {
    	xsdElem = elem;
	}
}