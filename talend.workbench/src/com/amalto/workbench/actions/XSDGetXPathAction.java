package com.amalto.workbench.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDTerm;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class XSDGetXPathAction extends UndoAction{
	
	public XSDGetXPathAction(DataModelMainPage page) {
		super(page);
		setImageDescriptor(ImageCache.getImage(EImage.XPATH.getPath()));
		setText("Copy XPath");
		setToolTipText("Copy the XPath");
	}
	
	public IStatus doAction() {
		try {
			
            IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
            XSDParticle particle = (XSDParticle) selection.getFirstElement();
            XSDTerm term = particle.getTerm();
            
            if (! (term instanceof  XSDElementDeclaration))  return Status.CANCEL_STATUS;
            
            Clipboard clipboard = Util.getClipboard();
            
            
            String path ="";
            TreeItem item = page.getTreeViewer().getTree().getSelection()[0];
            do {
            	XSDConcreteComponent component = (XSDConcreteComponent)item.getData();
            	if (component instanceof XSDParticle) {
            		if (((XSDParticle)component).getTerm() instanceof XSDElementDeclaration)
            			path = "/"+((XSDElementDeclaration)((XSDParticle)component).getTerm()).getName()+path;
            	} else if (component instanceof XSDElementDeclaration) {
            			path=((XSDElementDeclaration)component).getName()+path;
            	}
            	//System.out.println("          "+path+ "             $$"+component.toString()+"$$");
            	item = item.getParentItem();
            } while (item!=null);
            
            //System.out.println("PATH: "+path);
            clipboard.setContents(new Object[]{path}, new Transfer[] {TextTransfer.getInstance()});
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
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}