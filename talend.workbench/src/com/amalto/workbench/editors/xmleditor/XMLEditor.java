package com.amalto.workbench.editors.xmleditor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

import com.amalto.workbench.actions.SaveXObjectAction;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.webservices.WSDataModel;

public class XMLEditor extends TextEditor {
	TreeObject xobject;
	private ColorManager colorManager;
	XObjectEditor editor;
	int state=-1;
	public XMLEditor(XObjectEditor editor,TreeObject xobject) {
		super();
		this.xobject=xobject;
		this.editor=editor;
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new XMLConfiguration(colorManager));
		setDocumentProvider(new XMLDocumentProvider());
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		initializeKeyPress((SourceViewer)getSourceViewer());
	}
	
	private void initializeKeyPress(final SourceViewer viewer)
	{		
		StyledText styledText = viewer.getTextWidget();
        styledText.addKeyListener(new KeyListener( ) {
            public void keyPressed(KeyEvent e) {
			}

            public void keyReleased(KeyEvent e) {
				if (e.keyCode == 'x' && e.stateMask == SWT.CTRL) {
					viewer.doOperation(ITextOperationTarget.CUT);
				} else if (e.keyCode == 'v' && e.stateMask == SWT.CTRL) {
					viewer.doOperation(ITextOperationTarget.PASTE);
				}
			}
        });
	}
	
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	@Override
	public boolean isEditable() {
		return true;
	}	
	@Override
	public void doSave(IProgressMonitor progressMonitor) {		
    	WSDataModel wsObject = (WSDataModel) (xobject.getWsObject());
    	IDocument doc=((XMLEditorInput)this.getEditorInput()).getDocument();
		wsObject.setXsdSchema(doc.get());
		
		SaveXObjectAction action=new SaveXObjectAction(editor);
		action.run();
		state=action.getState();
		super.doSave(progressMonitor);		
	}
	
	public void refresh(TreeObject xobject){
		state=-1;
		this.xobject=xobject;
		WSDataModel wsObject = (WSDataModel) (xobject.getWsObject());    	        
        Document doc = new Document(wsObject.getXsdSchema());
        setInput(new XMLEditorInput(doc));        
	}
	
	public void refresh(){
		state=-1;
    	WSDataModel wsObject = (WSDataModel) (xobject.getWsObject());
    	IDocument doc=((XMLEditorInput)this.getEditorInput()).getDocument();
		wsObject.setXsdSchema(doc.get());
	}
	
	public void markDirty(){
		firePropertyChange(PROP_DIRTY);
	}
	
	@Override
	public boolean isDirty() {	
		if(state==1){
			return true;
		}
		return super.isDirty();
	}

	public void setState(int state) {
		this.state = state;
	}
	
	
}
