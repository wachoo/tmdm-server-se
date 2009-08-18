package com.amalto.workbench.actions;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDFactory;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.dialogs.BusinessElementInputDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDNewParticleFromParticleAction extends UndoAction implements SelectionListener{

	private BusinessElementInputDialog dialog = null;
	private XSDParticle selParticle = null;
	
	private String  elementName;
	private String  refName;
	private int minOccurs;
	private int maxOccurs;
	
	public XSDNewParticleFromParticleAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
		setText("Add Element (after)");
		setToolTipText("Add a new Business Element after this one. Add from the Type to add at First Position.");
	}
	
	public IStatus doAction() {
		try {
            IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
            selParticle = (XSDParticle) selection.getFirstElement();
            
            if (!(selParticle.getContainer() instanceof XSDModelGroup))  return Status.CANCEL_STATUS;;
            
            XSDModelGroup group = (XSDModelGroup) selParticle.getContainer();
            //get position of the selected particle in the container
            int index = 0;
            int i=0; 
            for (Iterator iter = group.getContents().iterator(); iter.hasNext(); ) {
				XSDParticle p = (XSDParticle) iter.next();
				if (p.equals(selParticle)) {
					index = i;
					break;
				}
				i++;
			}
            
            EList<XSDElementDeclaration> eDecls  = schema.getElementDeclarations();
			ArrayList<String> elementDeclarations = new ArrayList<String>();
			for (Iterator iter = eDecls.iterator(); iter.hasNext(); ) {
				XSDElementDeclaration d = (XSDElementDeclaration) iter.next();
				elementDeclarations.add(d.getQName());
			}
			elementDeclarations.add("");
			
            dialog = new BusinessElementInputDialog(this,page.getSite().getShell(),"Add a new Business Element", null, null, elementDeclarations, 0, 1);
            dialog.setBlockOnOpen(true);
       		int ret = dialog.open();
       		if (ret == Dialog.CANCEL){
                return Status.CANCEL_STATUS;
       		}
       		
       		XSDFactory factory = XSDSchemaBuildingTools.getXSDFactory();
       		
       		XSDElementDeclaration decl = factory.createXSDElementDeclaration();
       		decl.setName(this.elementName);
       		if (!refName.equals(""))
       		{
       			XSDElementDeclaration ref = Util.findReference(refName, schema.getElementDeclarations().get(0));
       			if (ref != null)
       			{
       				decl.setResolvedElementDeclaration(ref);
       			}
       		}
       		else
       		{
       		  decl.setTypeDefinition(schema.resolveSimpleTypeDefinition(schema.getSchemaForSchemaNamespace(), "string"));
       		}
       		
       		XSDParticle particle = factory.createXSDParticle();
       		particle.setContent(decl);
       		particle.setMinOccurs(this.minOccurs);
       		particle.setMaxOccurs(this.maxOccurs);
       		
       		group.getContents().add(index+1,particle);
       		group.updateElement();
       		
       		page.refresh();
       		page.getTreeViewer().setSelection(new StructuredSelection(particle),true);
       		page.markDirty();
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to create a new Business Element: "+e.getLocalizedMessage()
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
		if (dialog.getReturnCode() ==  -1) return; //there was a validation error	
		elementName = dialog.getElementName();
		refName = dialog.getRefName();
		minOccurs = dialog.getMinOccurs();
		maxOccurs = dialog.getMaxOccurs();
		
		//check that this element does not already exist
        XSDModelGroup group = (XSDModelGroup) selParticle.getContainer();
        //get position of the selected particle in the container
        for (Iterator iter = group.getContents().iterator(); iter.hasNext(); ) {
			XSDParticle p = (XSDParticle) iter.next();
			if (p.getTerm() instanceof XSDElementDeclaration) {
				XSDElementDeclaration thisDecl = (XSDElementDeclaration) p.getTerm();
				if (thisDecl.getName().equals(elementName)) {
					MessageDialog.openError(
							page.getSite().getShell(),
							"Error", 
							"The Business Element "+elementName+" already exists."
					);
					return;
				}
			}
		}//for
		dialog.close();		
	}
	


}