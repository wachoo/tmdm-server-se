/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.commmon.util.workbench.Version;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;


public class XObjectBrowser extends FormEditor implements IXObjectModelListener{	
	public static String ID="com.amalto.workbench.editors.XObjectBrowser";
	private ArrayList<IFormPage> formPages = new ArrayList<IFormPage>();
	private TreeObject initialXObject = null;  //backup
	
	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
        	
        	updateTitle();
        	
        	TreeObject xobject = (TreeObject)((XObjectBrowserInput)this.getEditorInput()).getModel();
        	
        	if (!xobject.isXObject()) return;
        	
        	//register model listener in order to close the browser when object is removed
        	xobject.addListener(this);
        	
            switch(xobject.getType()) {
	           	case TreeObject.VIEW:
	           		addPage(new  ViewBrowserMainPage(this));
	           		break;
	           	case TreeObject.DATA_CLUSTER:
	           		if (xobject.getDisplayName()!=null&&xobject.getDisplayName().equals(XSystemObjects.DC_MDMITEMSTRASH.getName())) {
	           			addPage(new ItemsTrashBrowserMainPage(this));
	           			break;
					}
	           		addPage(new DataClusterBrowserMainPage(this));
	           		break;	    
	           	case TreeObject.SUBSCRIPTION_ENGINE:
	           		boolean version_greater_2_19_0 = false;
					try {	
						Version ver = Util.getVersion(xobject);
						version_greater_2_19_0 = (ver.getMajor()>2) || ((ver.getMajor()==2)&&(ver.getMinor()>=19));
					} catch (XtentisException e) {}
	           		if (version_greater_2_19_0)
	           			addPage(new  RoutingEngineV2BrowserMainPage(this));
	           		else
	           			addPage(new  SubscriptionEngineBrowserMainPage(this));
	           		break;	    	           		
	           	default:
	           		MessageDialog.openError(this.getSite().getShell(), "Error", "Unsupported "+IConstants.TALEND+" Object Type: "+xobject.getType());
	           		return;
            }//switch
        } catch (PartInitException e) {
            MessageDialog.openError(this.getSite().getShell(), "Error", "Unable to open the editor :"+e.getLocalizedMessage());
        }
    }
    
    
    //Overloaded - Fixes issues with getEditor()
    public int addPage(IFormPage page) throws PartInitException {
		formPages.add(page);
		return super.addPage(page);
	}

	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#doSave(org.eclipse.core.runtime.IProgressMonitor)
     */
    public void doSave(IProgressMonitor monitor) {
    	//NO saving on browsers
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.ISaveablePart#isSaveAsAllowed()
     */
    public boolean isSaveAsAllowed() {
        return false;
    }
	public void doSaveAs() {
	}

    
	
	
	private void updateTitle() {
    	IEditorInput input = this.getEditorInput();
    	setPartName(input.getName());
    	setContentDescription("");
    }


	public void dispose() {
		//save space
		TreeObject xobject = (TreeObject)((XObjectBrowserInput)this.getEditorInput()).getModel(); 
		if (xobject!=null) { 
				xobject.setWsObject(null);
				//xobject.removeListener(this);
		}
		super.dispose();
	}

	/**
	 * Model Listener
	 */
	/*
	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		System.out.println("XObjectBrowser Handle Event "+child.getDisplayName()+" - "+type);
		TreeObject xobject = (TreeObject)((XObjectBrowserInput)this.getEditorInput()).getModel();
		switch(type) {
			case IXObjectModelListener.DELETE:
				if (xobject.equals(child)) this.close(false);
				break;
			case IXObjectModelListener.SAVE:
			case IXObjectModelListener.UPDATE:
				if (xobject.equals(child)) {
					//((AFormPage)getActivePageInstance()).refreshPage();
			    	for (int i = formPages.size()-1; i >=0 ; i--) {
			    		((AFormPage) formPages.get(i)).refreshPage();
					}
				}
			default:
		}
	}
	*/


	public TreeObject getInitialXObject() {
		return initialXObject;
	}

    
    protected void pageChange(int newPageIndex) {
        super.pageChange(newPageIndex);
        AFormPage page = (AFormPage)formPages.get(newPageIndex);
        page.refreshPage();
    }

    @Override
    public Image getTitleImage() {
    	TreeObject object = (TreeObject)((XObjectBrowserInput)this.getEditorInput()).getModel();
    	
		if (object.getType() == TreeObject._SERVER_)
			return ImageCache.getImage( EImage.TALEND_PICTO_SMALL.getPath()).createImage();
		else if (object.getType() == TreeObject.DATA_CLUSTER)
			return ImageCache.getImage( EImage.DATA_CLUSTER.getPath()).createImage();
		else if (object.getType() == TreeObject.DATA_MODEL)
			return ImageCache.getImage( EImage.DATA_MODEL.getPath()).createImage();
		else if (object.getType() == TreeObject.MENU)
			return ImageCache.getImage( EImage.MENU.getPath()).createImage();
		else if (object.getType() == TreeObject.TRANSFORMER)
			return ImageCache.getImage( EImage.TRANSFORMER.getPath()).createImage();
		else if (object.getType() == TreeObject.ROLE)
			return ImageCache.getImage( EImage.ROLE.getPath()).createImage();
		else if (object.getType() == TreeObject.STORED_PROCEDURE)
			return ImageCache.getImage( EImage.STORED_PROCEDURE.getPath()).createImage();
		else if (object.getType() == TreeObject.ROUTING_RULE)
			return ImageCache.getImage( EImage.ROUTING_RULE.getPath()).createImage();
		else if (object.getType() == TreeObject.VIEW)
			return ImageCache.getImage( EImage.VIEW.getPath()).createImage();
		else if (object.getType() == TreeObject.DOCUMENT)
			return ImageCache.getImage( EImage.DOCUMENTS.getPath()).createImage();			
		else if (object.getType() == TreeObject.SUBSCRIPTION_ENGINE)
			return ImageCache.getImage( EImage.SUBSCRIPTION_ENGINE.getPath()).createImage();
		
		return ImageCache.getImage( EImage.ERROR.getPath()).createImage();
    }
    
    
    
    
    
    /**
	 * Model Listener
	 */
	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		TreeObject xobject = (TreeObject)((XObjectBrowserInput)this.getEditorInput()).getModel();
		switch(type) {
			case IXObjectModelListener.DELETE:
				if (xobject.equals(child)) this.close(false);
				break;
			/*
			case IXObjectModelListener.SAVE:
				if (saveInProgress)
					this.editorDirtyStateChanged();
				break;
			case IXObjectModelListener.UPDATE:
				if (xobject.equals(child)) {
					((AFormPage)getActivePageInstance()).refreshPage();
				}
			*/
			default:
		}
	}
    
}
