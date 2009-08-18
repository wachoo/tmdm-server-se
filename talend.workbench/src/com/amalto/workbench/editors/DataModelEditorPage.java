/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.amalto.workbench.dialogs.ErrorExceptionDialog;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.webservices.WSDataModel;

/**
 * @deprecated
 * @author achen
 *
 */
public class DataModelEditorPage extends AFormPage implements ITextListener{

	private SourceViewer schemaViewer;
    private SectionPart editorPart;
	
	private boolean refreshing = false;
	private boolean comitting = false;
	
    public DataModelEditorPage(FormEditor editor) {
        super(
        		editor,
        		DataModelEditorPage.class.getName(),
        		"Schema" //+((XObjectEditorInput)editor.getEditorInput()).getName()
        );        
    }

    

	protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);

        try {
        	
            FormToolkit toolkit = managedForm.getToolkit();

            final ScrolledForm form = managedForm.getForm();
            TableWrapLayout formLayout = new TableWrapLayout();
            form.getBody().setLayout(formLayout);
            
            formLayout.numColumns = 1;
                        
            //create the FormPart
            editorPart = new SectionPart( 
                    form.getBody(),
                    toolkit,
                    ExpandableComposite.TWISTIE|ExpandableComposite.EXPANDED
             ) ;
            managedForm.addPart(editorPart);

            //Layout the components
            Section editorSection = editorPart.getSection();
            editorSection.setText("XML Schema");
            editorSection.setLayoutData(
                    new TableWrapData(TableWrapData.FILL_GRAB)
            );
            ((TableWrapData)editorSection.getLayoutData()).heightHint = 500;
            
            editorSection.addExpansionListener(new ExpansionAdapter() {
                public void expansionStateChanged(ExpansionEvent e) {
                    form.reflow(true);
                }
            });                        
            
            toolkit.createCompositeSeparator(editorSection);

            schemaViewer = new SourceViewer(
            		editorSection , 
            		new VerticalRuler(10), SWT.V_SCROLL | SWT.H_SCROLL
            );
            schemaViewer.addTextListener(this);
            WidgetUtils.initRedoUndo(schemaViewer);
            editorSection.setClient(schemaViewer.getControl());
            refreshData();
            
    		//if this created after the mainPage and it is dirty , mark this one as dirty too
    		DataModelMainPage mainPage = ((DataModelMainPage) getEditor().findPage(DataModelMainPage.class.getName()));
    		if (mainPage.isDirty()) this.markDirty();

            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//createFormContent
	

	protected void refreshData() {
		try {
			
			if (this.comitting) return;
            
            if (! this.equals(getEditor().getActivePageInstance())) return;
			
			this.refreshing = true;
			
			WSDataModel wsObject = (WSDataModel) (getXObject().getWsObject());    	
	    	            
            Document doc = new Document(wsObject.getXsdSchema());
            schemaViewer.setDocument(doc);
            
            this.refreshing = false;

		} catch (Exception e) {
			e.printStackTrace();
			ErrorExceptionDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	
	protected void commit() {
		try {
			
			if (this.refreshing) return;
			
			this.comitting = true;
			
	    	WSDataModel wsObject = (WSDataModel) (getXObject().getWsObject());
			if(schemaViewer!=null&&schemaViewer.getDocument()!=null)wsObject.setXsdSchema(schemaViewer.getDocument().get());
			
			this.comitting = false;
			
		} catch (Exception e) {
			e.printStackTrace();
			ErrorExceptionDialog.openError(this.getSite().getShell(), "Error comitting the page", "Error comitting the page: "+e.getLocalizedMessage());
		}    	
	}
		
	protected void createActions() {
	}


	public void textChanged(TextEvent event) {
		if (this.refreshing) return;
		markDirty();
		commitChanges();
	}

	/*
	private void hookContextMenu(TreeViewer viewer) {
	}

	private void fillContextMenu(IMenuManager manager) {
	}
	*/
	
	public void dispose() {
		if(schemaViewer!=null && schemaViewer.getUndoManager()!=null)
		schemaViewer.getUndoManager().disconnect();
		super.dispose();
	}
	
	public void markDirty() {
		if (editorPart!=null) editorPart.markDirty();
		DataModelMainPage page = ((DataModelMainPage) getEditor().findPage(DataModelMainPage.class.getName()));
		if ( !page.isDirty()) page.markDirtyWithoutCommit();
	}
	
	public boolean isDirty()
	{
		refreshData();
		if (editorPart!=null)
			return editorPart.isDirty();
		else
		{
			DataModelMainPage page = ((DataModelMainPage) getEditor().findPage(DataModelMainPage.class.getName()));
			return page.isDirty();
		}
	}
	
	public void markDirtyWithoutCommit() {
		if (editorPart!=null) editorPart.markDirty();
	}


}
