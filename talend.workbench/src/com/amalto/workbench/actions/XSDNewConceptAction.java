package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.NewConceptOrElementDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;


public class XSDNewConceptAction extends UndoAction implements SelectionListener{

	//protected DataModelMainPage page = null;
	private XSDElementDeclaration decl;
	
	public XSDNewConceptAction(DataModelMainPage page) {
		super(page);
		this.page = page;
		setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
		setText("New Concept");
		setToolTipText("Create a new Business Concept");
	}
	
	public IStatus doAction(){
		try {			
			ArrayList customTypes = new ArrayList();
			for (Iterator iter =  schema.getTypeDefinitions().iterator(); iter.hasNext(); ) {
				XSDTypeDefinition type = (XSDTypeDefinition) iter.next();
				if (type instanceof XSDSimpleTypeDefinition)
					customTypes.add(type.getName());
			}
			ArrayList builtInTypes = new ArrayList();
			for (Iterator iter =  schema.getSchemaForSchema().getTypeDefinitions().iterator(); iter.hasNext(); ) {
				XSDTypeDefinition type = (XSDTypeDefinition) iter.next();
				if (type instanceof XSDSimpleTypeDefinition)
					builtInTypes.add(type.getName());
			}
			
       		NewConceptOrElementDialog id = new NewConceptOrElementDialog(this,
					page.getSite().getShell(), schema, "New Concept", customTypes, builtInTypes);
       		
       		id.setBlockOnOpen(true);
       		id.open();
       		
    		if (id.getReturnCode() == Window.CANCEL){
    			 return Status.CANCEL_STATUS;
    		}
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to create a new Concept: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}		
        return Status.OK_STATUS;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	
	/**
	 * author: fliu .this fun is to support button click event invoked from the
	 * new expansion of Concept creation dialog
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
	}
	
	/**
	 * author: fliu .this fun is to support button click event invoked from the
	 * new expansion of Concept creation dialog
	 */
	public void widgetSelected(SelectionEvent e) {
		NewConceptOrElementDialog dlg = (NewConceptOrElementDialog)((Widget)e.getSource()).getData("dialog");
		if (dlg.getReturnCode() == Window.OK)  {
       		XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
       		
       		decl = factory.createXSDElementDeclaration();
       		decl.setName(dlg.getTypeName());
       		decl.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));
       		
       		XSDIdentityConstraintDefinition uniqueKey = factory.createXSDIdentityConstraintDefinition();
       		uniqueKey.setIdentityConstraintCategory(XSDIdentityConstraintCategory.UNIQUE_LITERAL);
       		uniqueKey.setName(dlg.getTypeName());
       		XSDXPathDefinition selector = factory.createXSDXPathDefinition();
       		selector.setVariety(XSDXPathVariety.SELECTOR_LITERAL);
       		selector.setValue(".");
       		uniqueKey.setSelector(selector);
       		XSDXPathDefinition field = factory.createXSDXPathDefinition();
       		field.setVariety(XSDXPathVariety.FIELD_LITERAL);
       		field.setValue(".");
       		uniqueKey.getFields().add(field);
       		decl.getIdentityConstraintDefinitions().add(uniqueKey);
       		
       		schema.getContents().add(decl);
       		//schema.getElementDeclarations().add(decl);
       		decl.updateElement();

       		page.refresh();
       		//page.markDirty();
       		getOperationHistory();
       		UndoAction changeAction = null;
       		if (dlg.isComplexType()) {
				changeAction = new XSDChangeToComplexTypeAction(page, decl, dlg
						.getComplexType(), dlg.isChoice(), dlg.isAll());
			}
       		else
       		{
       			changeAction  = new XSDChangeToSimpleTypeAction(page, decl, dlg.getElementType(), dlg.isBuildIn());
       		}
			dlg.close();
			
       		
       		changeAction.doAction();
		}
	}
}