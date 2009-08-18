package com.amalto.workbench.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDSimpleTypeDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.ISchemaContentProvider;
import com.amalto.workbench.utils.Util;

public class XSDDeleteTypeDefinition extends  UndoAction{
	
	private XSDComplexTypeDefinition xsdCmpexType;
	private XSDSimpleTypeDefinition xsdSimpType;
	

	public XSDDeleteTypeDefinition(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
		setText("Delete type definition");
		setToolTipText("Remove a type definition from the XSD Schema context");
		setDescription(getToolTipText());
	}
	
	public void run(Object toDel)
	{
		if (!(toDel instanceof XSDComplexTypeDefinition||toDel instanceof XSDSimpleTypeDefinition)) {
			return;
		}
		if(toDel instanceof XSDComplexTypeDefinition)
			xsdCmpexType = (XSDComplexTypeDefinition) toDel;
		else
			xsdSimpType = (XSDSimpleTypeDefinition) toDel;
		run();
	}
	
	public IStatus doAction() {
		IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
		XSDSchema schema = ((ISchemaContentProvider)page.getTreeViewer().getContentProvider()).getXsdSchema();
		
		ArrayList<Object> objList = new ArrayList<Object>();
		Util.getAllObject(page.getSite(), objList, (IStructuredContentProvider)page.getSchemaContentProvider());
		Util.getAllObject(page.getSite(), objList, (IStructuredContentProvider)page.getTypeContentProvider());
		
		if (selection.getFirstElement() instanceof XSDSimpleTypeDefinition) {
			XSDSimpleTypeDefinition simpleType = (XSDSimpleTypeDefinition)selection.getFirstElement();
			if(xsdSimpType!=null)
				simpleType = xsdSimpType;
			boolean find = Util.findElementsUsingType(objList, null, simpleType.getName());
			if(find){
				MessageDialog
				.openWarning(page.getSite().getShell(), "Warnning",
						"The Simple type definition : " + simpleType.getName() + " is referring to other Elements");
				xsdSimpType = null;
				return Status.CANCEL_STATUS;
			}//iif(!list.isEmpty())
			schema.getContents().remove(simpleType);
		}//if (selection.getFirstElement()
		else{

			XSDComplexTypeDefinition complxType = (XSDComplexTypeDefinition)selection.getFirstElement();
			if (xsdCmpexType != null) {
				complxType = xsdCmpexType;
			}

			boolean find = Util.findElementsUsingType(objList, null, complxType.getName());
			if (find)
			{
				MessageDialog
						.openWarning(page.getSite().getShell(), "Warnning",
								"The Complex type definition : " + complxType.getName() + " is referring to other Elements");
				xsdCmpexType = null;
				return Status.CANCEL_STATUS;
			}
			schema.getContents().remove(complxType);
		}

		
		xsdCmpexType = null;
		page.refresh();
		page.markDirty();
		
		return Status.OK_STATUS;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	
    public void setXSDTODel(XSDComplexTypeDefinition elem) {
    	xsdCmpexType = elem;
	}
    public void setXSDTODel(XSDSimpleTypeDefinition xsdSimpType) {
		this.xsdSimpType = xsdSimpType;
	}
    
}
