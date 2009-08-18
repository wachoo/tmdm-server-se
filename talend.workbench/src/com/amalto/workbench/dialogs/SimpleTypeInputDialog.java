package com.amalto.workbench.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.widgets.ElementComposite;

public class SimpleTypeInputDialog extends Dialog{

	protected List customTypes=null;
	protected List builtInTypes=null;
	
	private SelectionListener caller = null;
	private String title = "";
	
	private String typeName = null;

	private ElementComposite elemPanel = null;
	/**
	 * @param parentShell
	 */
	public SimpleTypeInputDialog(
			SelectionListener caller, 
			Shell parentShell, 
			String title,
			List customTypes,
			List builtInTypes
			) {
		super(parentShell);
		this.caller = caller;
		this.title = title;
		this.customTypes = customTypes;
		this.builtInTypes = builtInTypes;
	}

	

	protected Control createDialogArea(Composite parent) {
		//Should not really be here but well,....
		parent.getShell().setText(this.title);
		
		final Composite composite = (Composite) super.createDialogArea(parent);
		// encapsulate all widgets into the ElementComposite which can be applied to several cases
		elemPanel = new ElementComposite(composite, customTypes, builtInTypes, false);
		
	    return elemPanel.getComposite();
	}
    
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
		/*
	      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
	                true);
	        createButton(parent, IDialogConstants.CANCEL_ID,
	                IDialogConstants.CANCEL_LABEL, false);
	  */
	}
	
	protected void okPressed() {
		typeName = elemPanel.getText();
//		if ((typeName==null) || ("".equals(typeName))) {
//			MessageDialog.openError(
//					this.getShell(), 
//					"Error", "The Key Name cannot be empty"
//			);
//			setReturnCode(-1);
//			elemPanel.setFocus();
//			return;
//		}
//				
		setReturnCode(OK);
		//no close let Action Handler handle it
	}



	public boolean isBuiltIn() {
		return elemPanel.isBuiltIn();
	}
	public String getTypeName() {
		return typeName;
	}
	

}
