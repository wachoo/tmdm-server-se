package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDCompositor;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDIdentityConstraintCategory;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.XSDTypeDefinition;
import org.eclipse.xsd.XSDXPathDefinition;
import org.eclipse.xsd.XSDXPathVariety;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.ComplexTypeInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDChangeToComplexTypeAction extends UndoAction implements SelectionListener{

	protected boolean isConcept = false;
	protected XSDElementDeclaration decl = null;
	protected ComplexTypeInputDialog dialog =null;
	
	XSDElementDeclaration declNew = null;
	String typeName = null;
	boolean isChoice = false;
	boolean isAll = false;
	
	boolean showDlg = true; 
	public XSDChangeToComplexTypeAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage( "icons/change_to_complex.gif"));
		setText("Change to a Complex Type");
		setToolTipText("Make an Element a Complex Element or change the type of current Complex Element");
		setDescription(getToolTipText());
	}
	
	public XSDChangeToComplexTypeAction(DataModelMainPage page, XSDElementDeclaration dec,
			String name, boolean choice, boolean all) {
		
		this(page);
		
		declNew = dec;
		showDlg = false;
		typeName = name;
		isChoice = choice;
		isAll = all;
	}
	
	public IStatus doAction() {
		
		
		try {
            IStructuredSelection selection = (IStructuredSelection) page.getTreeViewer().getSelection();
            isConcept=false;
			// fliu
			// add declNew to support convert action invoked from new concept/new element menu, in this case 
			// declNew is the new created one not the selected one in tree vew
            if (selection.getFirstElement() instanceof XSDElementDeclaration || declNew != null) {
            	decl = (XSDElementDeclaration) selection.getFirstElement();
                if (declNew != null)
                {
                	decl = declNew;
                }
    			//check if concept or "just" element
    			EList l = decl.getIdentityConstraintDefinitions();
    			for (Iterator iter = l.iterator(); iter.hasNext(); ) {
    				XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
    				if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL)) {
    					isConcept=true;
    					break;
    				}
    			}
            } else {
            	if (selection.getFirstElement() != null) {
					// a sub element
					decl = (XSDElementDeclaration) ((XSDParticle) selection
							.getFirstElement()).getTerm();
				}
            }
            
       		///save current Type Definition
       		//XSDTypeDefinition current = decl.getTypeDefinition();      		
           
       		if (showDlg) {
       			
				dialog = new ComplexTypeInputDialog(this, page.getSite()
						.getShell(),Util.getComplexTypes(decl.getSchema()));

				dialog.setBlockOnOpen(true);
				int ret = dialog.open();
				if (ret == Dialog.CANCEL) {
					return Status.CANCEL_STATUS;
				}
			}

			if (!showDlg && !validateType()) {
				return Status.CANCEL_STATUS;
			}
            
       		XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
       		boolean anonymous = (typeName==null) || ("".equals(typeName));
			boolean alreadyExists = false;
			
			XSDComplexTypeDefinition complexType = null;
			//the sub element created if needed
			XSDParticle subParticle = null;
			XSDParticle groupParticle = null;
			XSDElementDeclaration subElement = null;
			
			//check if already exist
       		if (!anonymous) {
       			EList list = schema.getTypeDefinitions();
				for (Iterator iter = list.iterator(); iter.hasNext(); ) {
					XSDTypeDefinition td = (XSDTypeDefinition) iter.next();
					if ((td.getName().equals(typeName) && (td instanceof XSDComplexTypeDefinition))) {
						alreadyExists = true;
						complexType = (XSDComplexTypeDefinition)td;
						break;
					}
				}
       		}
       		
       		//Create if does not exist
       		if (!alreadyExists) {
       			
       			//add an element declaration
       			subElement = factory.createXSDElementDeclaration();
        		subElement.setName("subelement");
        		subElement.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));
       			
        		subParticle = factory.createXSDParticle();
       			subParticle.setMinOccurs(1);
       			subParticle.setMaxOccurs(1);
       			subParticle.setContent(subElement);
       			subParticle.updateElement();

       			//create group
       			XSDModelGroup group = factory.createXSDModelGroup();
       			if (isChoice)
       				group.setCompositor(XSDCompositor.CHOICE_LITERAL);
       			else if (isAll)
       				group.setCompositor(XSDCompositor.ALL_LITERAL);
       			else
       				group.setCompositor(XSDCompositor.SEQUENCE_LITERAL);
       			group.getContents().add(0,subParticle);
       			group.updateElement();

       			//create the complex type
       			complexType = factory.createXSDComplexTypeDefinition();
       			//complexType.setDerivationMethod(XSDDerivationMethod.EXTENSION_LITERAL);
       			if (!anonymous) {
       				complexType.setName(typeName);
       				schema.getContents().add(complexType);
       			}
       			complexType.updateElement();
       			
       			//add the group
       			groupParticle = factory.createXSDParticle();
       			groupParticle.setMinOccurs(1);
       			groupParticle.setMaxOccurs(1);
       			groupParticle.setContent(group);
       			groupParticle.updateElement();
       			
       			complexType.setContent(groupParticle);       			
       			complexType.updateElement();
			}//end if NOT already exusts
       		       		
       	    //set complex type to concept
       	    if (anonymous)
       	    	decl.setAnonymousTypeDefinition(complexType);
       	    else {
       	    	decl.setTypeDefinition(complexType);
       	    }
       	    
       	    //remove current if no more in use
//       	FIXME: must nor remove complex types in use by other elements
   	    	/*
       	    if (	(current.getName()!=null) &&  //anonymous type
       	    		(!schema.getSchemaForSchemaNamespace().equals(current.getTargetNamespace()))
       	    	){
       	    	List eut =Util.findElementsUsingType(schema, current.getTargetNamespace(), current.getName()); 
       	    	if (eut.size()==0)
       	    		schema.getContents().remove(current);  
       	    	//else
       	    	//	System.out.println("Type Definition "+current.getName()+" in use by "+((XSDElementDeclaration)eut.get(0)).getName());
       	    }
       	    */
       	    
       	    //remove unique key with name of concept
   			/*
       	    EList list = decl.getIdentityConstraintDefinitions();
       	    XSDIdentityConstraintDefinition toRemove = null;
       	    for (Iterator iter = list.iterator(); iter.hasNext(); ) {
				XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
				if (icd.getName().equals(decl.getName())) {
					toRemove = icd;
					break;
				}
			}
       	    if (toRemove!=null) list.remove(toRemove);
       	    */
   			
   			if (isConcept) {
   				
   				//remove exisiting unique key(s)
   				ArrayList keys = new ArrayList();
   	       	    EList list = decl.getIdentityConstraintDefinitions();
   	       	    for (Iterator iter = list.iterator(); iter.hasNext(); ) {
   					XSDIdentityConstraintDefinition icd = (XSDIdentityConstraintDefinition) iter.next();
   					if (icd.getIdentityConstraintCategory().equals(XSDIdentityConstraintCategory.UNIQUE_LITERAL))
   						keys.add( icd);
   				}
   	       	    decl.getIdentityConstraintDefinitions().removeAll(keys);
   				
	       		//add new unique key with first element declaration name
	       	    XSDElementDeclaration firstDecl = null;
	       	    if (complexType.getContent() instanceof XSDParticle) {
	       	    	if (((XSDParticle)complexType.getContent()).getTerm() instanceof XSDModelGroup) {
	       	    		XSDModelGroup group = (XSDModelGroup)((XSDParticle)complexType.getContent()).getTerm();
	       	    		EList gpl = group.getContents();
	       	    		for (Iterator iter = gpl.iterator(); iter.hasNext(); ) {
	       	    			XSDParticle part = (XSDParticle)iter.next();
							if (part.getTerm() instanceof XSDElementDeclaration) {
								firstDecl = (XSDElementDeclaration)part.getTerm();
								break;
							}
	       	    		}
	       	    	}
	       	    }
	       	    if (firstDecl != null) {
		       		XSDIdentityConstraintDefinition uniqueKey = factory.createXSDIdentityConstraintDefinition();
		       		uniqueKey.setIdentityConstraintCategory(XSDIdentityConstraintCategory.UNIQUE_LITERAL);
		       		uniqueKey.setName(decl.getName());
		       		XSDXPathDefinition selector = factory.createXSDXPathDefinition();
		       		selector.setVariety(XSDXPathVariety.SELECTOR_LITERAL);
		       		selector.setValue(".");
		       		uniqueKey.setSelector(selector);
		       		XSDXPathDefinition field = factory.createXSDXPathDefinition();
		       		field.setVariety(XSDXPathVariety.FIELD_LITERAL);
		       		field.setValue(firstDecl.getAliasName());
		       		uniqueKey.getFields().add(field);
		       		decl.getIdentityConstraintDefinitions().add(uniqueKey);
	       	    }

   			}// if isConcept
       	    
       		decl.updateElement();
       		
       		page.refresh();
      		
       		//FIXME: reveal the sub element created and edit it
       		/*
       		if (subParticle!=null) {
       			page.getTreeViewer().setExpandedState(decl, true);
       			page.getTreeViewer().setSelection(
       					new StructuredSelection(groupParticle),
       					true
       			);
       			ISelection sel = page.getTreeViewer().getSelection();
       			System.out.println(sel.toString());
       			XSDEditParticleAction editAction = new XSDEditParticleAction(page);
       			//editAction.run();
       		}
       		*/
       		declNew = null;
       		page.markDirty();
       		
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to change to Complex Type: "+e.getLocalizedMessage()
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
		isChoice = dialog.isChoice();
		isAll = dialog.isAll();
		
		if (!validateType())
		{
			return;
		}
		dialog.close();		
	}
	
	private boolean validateType()
	{
		if (! "".equals(typeName)) {
			EList list = schema.getTypeDefinitions();
			for (Iterator iter = list.iterator(); iter.hasNext(); ) {
				XSDTypeDefinition td = (XSDTypeDefinition) iter.next();
				if (td.getName().equals(typeName)) {
					if (td instanceof XSDSimpleTypeDefinition) {
						MessageDialog.openError(
								page.getSite().getShell(),
								"Error", 
								"This type \""+typeName+"\" already exists as a Simple Type"
						);
						return false;
					}
				}
			}//for
		}
		
		return true;
	}
}

