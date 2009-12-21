package org.talend.mdm.workbench.enterprice.actions;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.w3c.dom.Element;

import com.amalto.workbench.actions.UndoAction;
import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.XSDAnnotationsStructure;

public class XSDDeleteAnnotationSchematronAction 	extends UndoAction{


		protected String dataModelName;
		
		public XSDDeleteAnnotationSchematronAction(DataModelMainPage page,String dataModelName) {
			super(page);
			setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
			setText("Delete the Validation Rule");
			setToolTipText("Delete the Validation Rule");
			this.dataModelName = dataModelName;
		}
		
		public IStatus doAction() {
			try {
		         IStructuredSelection selection = (TreeSelection)page.getTreeViewer().getSelection();
		            XSDComponent xSDCom=null;
			
		           if (selection.getFirstElement() instanceof Element) {
						TreePath tPath = ((TreeSelection) selection).getPaths()[0];
						for (int i = 0; i < tPath.getSegmentCount(); i++) {
							if (tPath.getSegment(i) instanceof XSDAnnotation)
								xSDCom = (XSDAnnotation) (tPath.getSegment(i));
						}
						XSDAnnotationsStructure struc =new XSDAnnotationsStructure(xSDCom);
						Element e= (Element)selection.getFirstElement();
						if(!"X_Schematron".equals(e.getAttribute("source"))) return Status.CANCEL_STATUS;
						String pattern=e.getChildNodes().item(0).getNodeValue();
						struc.removeSchematron(pattern);
		       			page.refresh();
		       			page.getTreeViewer().expandToLevel(xSDCom, 2);
		       			page.markDirty();
		           }
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(
						page.getSite().getShell(),
						"Error", 
						"An error occured trying to Delete the Validation Rule: "+e.getLocalizedMessage()
				);
	            return Status.CANCEL_STATUS;
			}
	        return Status.OK_STATUS;
	       }
}
