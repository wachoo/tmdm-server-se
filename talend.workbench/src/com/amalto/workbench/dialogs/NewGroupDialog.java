package com.amalto.workbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class NewGroupDialog extends Dialog {

	protected Button sequenceButton = null;
	protected Button choiceButton = null;
	protected Button allButton = null;
	
	private SelectionListener caller = null;
	
	private int minOccurs=1;
	private int maxOccurs=1;
	private Text minOccursText;
	private Text maxOccursText;
	

	/**
	 * @param parentShell
	 */
	public NewGroupDialog(
			SelectionListener caller, 
			Shell parentShell 
			) {
		super(parentShell);
		this.caller = caller;
	}

	

	protected Control createDialogArea(Composite parent) {
		//Should not really be here but well,....
		parent.getShell().setText("Add Group");
		
		final Composite composite = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;

		Group radioGroup = new Group(composite,SWT.SHADOW_NONE);
		radioGroup.setText("Sub-Elements Group");
		radioGroup.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,2,1)
		);
		radioGroup.setLayout(new GridLayout(1,false));
		allButton = new Button(radioGroup,SWT.RADIO);
		allButton.setText("All");
		allButton.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);		
		allButton.setSelection(true);
		
		sequenceButton = new Button(radioGroup,SWT.RADIO);
		sequenceButton.setText("Sequence");
		sequenceButton.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		choiceButton = new Button(radioGroup,SWT.RADIO);
		choiceButton.setText("Choice");
		choiceButton.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);

		
		
		Label minOccursLabel = new Label(composite, SWT.NONE);
		minOccursLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		minOccursLabel.setText("Minimum Occurence");

		minOccursText = new Text(composite, SWT.NONE);
		minOccursText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		minOccursText.setDoubleClickEnabled(false);
		minOccursText.setText(""+getMinOccurs());
		
		Label maxOccursLabel = new Label(composite, SWT.NONE);
		maxOccursLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		maxOccursLabel.setText("Maximum Occurence");

		maxOccursText = new Text(composite, SWT.NONE);
		maxOccursText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		maxOccursText.setText(getMaxOccurs()== -1 ? "" : ""+getMaxOccurs());
		
	    return composite;
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
		if ("".equals(minOccursText.getText()) && "".equals(maxOccursText.getText())) {
			minOccurs = 1;
			maxOccurs = 1;
			return;
		}
		try {
			minOccurs = Integer.parseInt(minOccursText.getText());
		} catch (Exception e1) {
			MessageDialog.openError(
					this.getShell(), 
					"Error", "The Minimum Occurence must be greater or equal to Zero"
			);
			setReturnCode(-1);
			minOccursText.setFocus();
			return;
		}
		if (minOccurs<0) {
			MessageDialog.openError(
					this.getShell(), 
					"Error", "The Minimum Occurence must be greater or equal to Zero"
			);
			setReturnCode(-1);
			minOccursText.setFocus();
			return;
		}
		
		if ("".equals(maxOccursText.getText())) {
			maxOccurs = -1;
		} else {
			try {
				maxOccurs = Integer.parseInt(maxOccursText.getText());
			} catch (Exception e2) {
				MessageDialog.openError(
						this.getShell(), 
						"Error", "The Maximum Occurence must be a Number or Blank (unbounded)."
				);
				setReturnCode(-1);
				maxOccursText.setFocus();
				return;
			}
			if ((maxOccurs<minOccurs) || (maxOccurs<=0))
				maxOccurs = -1;
		}
		
		setReturnCode(OK);
		//no close let Action Handler handle it
	}



	public boolean isSequence() {
		return sequenceButton.getSelection();
	}
	public boolean isChoice() {
		return choiceButton.getSelection();
	}
	public boolean isAll() {
		return allButton.getSelection();
	}

	
	
	public int getMaxOccurs() {
		return maxOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}
	
	

}
