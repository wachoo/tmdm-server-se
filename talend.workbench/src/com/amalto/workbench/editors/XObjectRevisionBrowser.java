package com.amalto.workbench.editors;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSPutUniverse;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.XtentisPort;

public class XObjectRevisionBrowser extends FormEditor {	
	public static String ID="com.amalto.workbench.editors.XObjectRevisionBrowser";
	private ArrayList<IFormPage> formPages = new ArrayList<IFormPage>();
	private TreeObject initialXObject = null;  //backup
	protected boolean saveInProgress = false;
	
	/*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
     */
    protected void addPages() {
        try {
        	
        	updateTitle();
        	
        	TreeObject xobject = (TreeObject)((XObjectBrowserInput)this.getEditorInput()).getModel();
        	
        	if (xobject.isXObject()) return;
        	
        	if(Util.hasUniverse(xobject))
	           	addPage(new  BrowseRevisionMainPage(this));
        	else	           
	           MessageDialog.openError(this.getSite().getShell(), "Error", "Unsupported "+IConstants.TALEND+" Object Type: "+xobject.getType());
	           		
            //switch
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
		// NO saving on browsers
//		System.out.println("doSave");
		try {
			this.saveInProgress = true;
			int numPages = formPages.size();
			monitor.beginTask("Saving " + this.getEditorInput().getName(),numPages + 1);
			for (int i = 0; i < numPages; i++) {
				if ((formPages.get(i)) instanceof AFormPage) {
					if (!((AFormPage) (formPages.get(i))).beforeDoSave())
						return;
				}
				(formPages.get(i)).doSave(monitor);
				monitor.worked(1);
				if (monitor.isCanceled()) {
					this.saveInProgress = false;
					return;
				}
				TreeObject xobject = (TreeObject) ((XObjectBrowserInput) this
						.getEditorInput()).getModel();
				if (!xobject.isXObject())
					return;

				// Access to server and get port

				XtentisPort port = Util.getPort(new URL(xobject
						.getEndpointAddress()), xobject.getUniverse(), xobject
						.getUsername(), xobject.getPassword());
				List<WSUniverse> universes = ((BrowseRevisionMainPage) formPages
						.get(i)).getUniverses();
				for (WSUniverse wsUniverse : universes)
					port.putUniverse(new WSPutUniverse(wsUniverse));
			}
		} catch (RemoteException e) {
			e.printStackTrace();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (XtentisException e) {
			e.printStackTrace();
		}

		monitor.done();
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
    	setPartName(input.getName().replaceAll("\\[.*\\]", ""));
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
			return ImageCache.getImage( EImage.ROUTING_RULE.getPath()).createImage();
		
		return ImageCache.getImage( EImage.ERROR.getPath()).createImage();
    }
    
    
    
    


}
