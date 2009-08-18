package com.amalto.workbench.actions;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.dialogs.AnnotationOrderedListsDialog;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDSetAnnotationForeignKeyInfoAction extends UndoAction{

	protected AnnotationOrderedListsDialog dlg = null;
	protected String dataModelName;
	
	public XSDSetAnnotationForeignKeyInfoAction(DataModelMainPage page,String dataModelName) {
		super(page);
		setImageDescriptor(ImageCache.getImage( "icons/annotation.gif"));
		setText("Set the Foreign Key Infos");
		setToolTipText("Set the Foreign Key Infos");
		this.dataModelName = dataModelName;
	}
	
	public IStatus doAction() {
		try {
			
            IStructuredSelection selection = (IStructuredSelection)page.getTreeViewer().getSelection();
            XSDAnnotationsStructure struc = new XSDAnnotationsStructure((XSDComponent)selection.getFirstElement());
            if (struc.getAnnotation() == null) {
            	throw new RuntimeException("Unable to edit an annotation for object of type "+selection.getFirstElement().getClass().getName());
            }
            
            dlg = new AnnotationOrderedListsDialog(
            		new ArrayList(struc.getForeignKeyInfos().values()),
            		new SelectionListener() {
            			public void widgetDefaultSelected(SelectionEvent e) {}
            			public void widgetSelected(SelectionEvent e) {
            				dlg.close();
            			}
            		},
       				page.getSite().getShell(),
       				"Set the Foreign Key Infos",
       				"xPaths",
       				page,
       				AnnotationOrderedListsDialog.AnnotationForeignKeyInfo_ActionType,
       				dataModelName
       				
       		);
            
       		dlg.setBlockOnOpen(true);
       		int ret = dlg.open();
       		if (ret == Window.CANCEL) {
                return Status.CANCEL_STATUS;
       		}

       		struc.setForeignKeyInfos(dlg.getXPaths());
       		
       		if (struc.hasChanged()) {
       			page.refresh();
       			page.getTreeViewer().expandToLevel(selection.getFirstElement(), 2);
       			page.markDirty();
       		}
       		
       
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					page.getSite().getShell(),
					"Error", 
					"An error occured trying to set a Forign Key: "+e.getLocalizedMessage()
			);
            return Status.CANCEL_STATUS;
		}
        return Status.OK_STATUS;
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}