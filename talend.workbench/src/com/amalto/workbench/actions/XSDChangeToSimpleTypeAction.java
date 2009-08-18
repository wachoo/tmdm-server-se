package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.SimpleTypeInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;

public class XSDChangeToSimpleTypeAction extends UndoAction implements SelectionListener{

	private SimpleTypeInputDialog dialog = null;
	private String typeName = null;
	private boolean builtIn = false;
	private boolean isConcept = false;
	private XSDSimpleTypeDefinition simpleType;
	private XSDElementDeclaration declNew = null;
	boolean showDlg = true; 
	
	public XSDChangeToSimpleTypeAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage( "icons/change_to_simple.gif"));
		setText("Change to a Simple Type");
		setToolTipText("Make Element a Simple Element or change the current Simple Type");
		setDescription(getToolTipText());
	}
	
	public XSDChangeToSimpleTypeAction(DataModelMainPage page, XSDElementDeclaration dec, String name,
			boolean built) {
		this(page);
		showDlg = false;
		typeName = name;
		builtIn = built;
		
		declNew = dec;
	}
	
	public IStatus doAction() {
		try {
			XSDElementDeclaration decl = null;
			IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
			// fliu
			// add declNew to support convert action invoked from new concept/new element menu, in this case 
			// declNew is the new created one not the selected one in tree vew
			if (selection.getFirstElement() instanceof XSDElementDeclaration || declNew != null) {
				isConcept = true;
				decl = (XSDElementDeclaration) selection.getFirstElement();
				if (declNew != null)
				{
					decl = declNew;
				}
			} else {
				isConcept = false;
				if (selection.getFirstElement() != null) {
					decl = (XSDElementDeclaration) ((XSDParticle) selection
							.getFirstElement()).getTerm();
				}
			}
			
			//build list of custom types and built in types
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
			
			if (showDlg)
			{
				dialog = new SimpleTypeInputDialog(
						this,
						page.getSite().getShell(),
						"Make Simple Type",
						customTypes,
						builtInTypes
				);
				
				dialog.setBlockOnOpen(true);
				int ret = dialog.open();
				if (ret == Window.CANCEL) {
					return Status.CANCEL_STATUS;
				}
			}
			
			//if concept
			//remove all unique keys and make new one
			if (isConcept) {
  				//remove exisitng unique key(s)
   				ArrayList keys = new ArrayList();
   	       	    EList list = decl.getIdentityConstraintDefinitions();
   	       	    for (Iterator iter = list.iterator(); iter.hasNext(); ) {
   					XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
   					if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
   						keys.add( icd);
   				}
   	       	    decl.getIdentityConstraintDefinitions().removeAll(keys);
   				//add new unique Key
				XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
				XSDIdentityConstraintDefinition uniqueKey = factory.createXSDIdentityConstraintDefinition();
				uniqueKey.setIdentityConstraintCategory(XSDIdentityConstraintCategory.UNIQUE_LITERAL);
				uniqueKey.setName(decl.getName());
				XSDXPathDefinition selector = factory.createXSDXPathDefinition();
				selector.setVariety(XSDXPathVariety.SELECTOR_LITERAL);
				selector.setValue(".");
				uniqueKey.setSelector(selector);
				XSDXPathDefinition field = factory.createXSDXPathDefinition();
				field.setVariety(XSDXPathVariety.FIELD_LITERAL);
				field.setValue(".");
				uniqueKey.getFields().add(field);
				decl.getIdentityConstraintDefinitions().add(uniqueKey);
			}
			
			//Save current type definition 
			XSDTypeDefinition current = decl.getTypeDefinition();
			
			//set new one
			if (builtIn) {
				decl.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), typeName));
			} else {
				//check if concept already exists
				if(typeName !=null&&typeName.length()>0){
					XSDSimpleTypeDefinition std = schema.resolveSimpleTypeDefinition(typeName);
					if (!schema.getTypeDefinitions().contains(std)) {
						std.setBaseTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));
						schema.getContents().add(std);
					}
					decl.setTypeDefinition(std);
				}
				else{
					XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
					simpleType = factory.createXSDSimpleTypeDefinition();
					simpleType.setBaseTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));
					decl.setAnonymousTypeDefinition(simpleType);
				}
			}
			decl.updateElement();
			
       	    //remove current if no more in use
//			if (current != null) {
//	       	    if (	(current.getName()!=null) &&  //anonymous type
//	       	    		(!schema.getSchemaForSchemaNamespace().equals(current.getTargetNamespace()))
//	       	    	){
//	       	    	List eut =Util.findElementsUsingType(schema, current.getTargetNamespace(), current.getName()); 
//	       	    	if (eut.size()==0)
//	       	    		schema.getContents().remove(current);   
//	       	    }
//			}
			
			declNew = null;
			page.refresh();
			page.markDirty();
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to change to Simple Type: "+e.getLocalizedMessage()
			);
			return Status.CANCEL_STATUS;
		}
		return Status.OK_STATUS;
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	
	/********************************
	 * Listener to input dialog
	 */
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	public void widgetSelected(SelectionEvent e) {
		if (dialog.getReturnCode()==-1) return;
		typeName = dialog.getTypeName();
		builtIn = dialog.isBuiltIn();
		
		//if built in, check that the type actually exists
		if (builtIn && !validateType()) {
			return;
		}
		dialog.close();		
	}
	
	private boolean validateType()
	{
		boolean found =false;
        for (Iterator iter =  schema.getSchemaForSchema().getTypeDefinitions().iterator(); iter.hasNext(); ) {
			XSDTypeDefinition type = (XSDTypeDefinition) iter.next();
			if (type instanceof XSDSimpleTypeDefinition)
				if (type.getName().equals(typeName)) {
					found = true;
					break;
				}
        }
        if (!found) {
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"The built-in type "+typeName+" does not exist"
			);            	
			return false;
		}
        
        return true;
	}
	

}