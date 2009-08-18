/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;

public abstract class AFormPage extends FormPage {

	private boolean refreshing = false;
	private boolean comitting = false;

	public AFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Should be overriden
	 */
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		managedForm.getForm().setText(this.getTitle());
		createActions();
	}

	/**
	 * Called by The main Editor
	 * 
	 */
	public void refreshPage() {
		this.refreshing = true;
		if (!this.comitting)
			refreshData();
		this.refreshing = false;
	}

	/**
	 * Refresh the form with the model content
	 * 
	 */
	protected abstract void refreshData();

	/**
	 * Commit the form content to the model
	 * 
	 */
	protected abstract void commit();

	/**
	 * Commit changes back to the model - must be called by the listeners
	 * 
	 * @param onSave
	 */
	protected void commitChanges() {
		this.comitting = true;
		if (!this.refreshing) {
			TreeObject xobject = (TreeObject) this.getEditor().getEditorInput()
					.getAdapter(XObjectEditor.class);
			commit();
			if (xobject.getParent() != null) { // e.g. not standalone but part
												// of model
				xobject.fireEvent(IXObjectModelListener.UPDATE, xobject
						.getParent(), xobject);
			}
		}
		this.comitting = false;
	}

	/**
	 * @return the model manipulated by this Page
	 */
	public TreeObject getXObject() {
		return (TreeObject) this.getEditor().getEditorInput().getAdapter(
				XObjectEditor.class);
	}

	/**
	 * Called at time of the page creation
	 * 
	 */
	protected abstract void createActions();

	public boolean beforeDoSave() {
		return true;

	}

}
