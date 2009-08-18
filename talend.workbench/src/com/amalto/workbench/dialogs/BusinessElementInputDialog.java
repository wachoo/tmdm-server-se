package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class BusinessElementInputDialog extends Dialog {

	private Text elementNameText=null;
	private Combo refCombo=null;
	private Text minOccursText=null;
	private Text maxOccursText=null;
	private Collection<String> elementDeclarations = null;

	private String elementName="";
	private String refName="";
	private int minOccurs=0;
	private int maxOccurs=1;
	
	private SelectionListener caller = null;
	private String title = "";
	

	/**
	 * @param parentShell
	 */
	public BusinessElementInputDialog(SelectionListener caller, Shell parentShell, String title) {
		this(caller,parentShell,title,"","",new ArrayList<String>(),0,1);
	}

	/**
	 * @param parentShell
	 */
	public BusinessElementInputDialog(
			SelectionListener caller, 
			Shell parentShell, 
			String title,
			String elementName,
			String refName,
			Collection<String> decls,
			int minOccurs,
			int maxOccurs) {
		super(parentShell);
		this.caller = caller;
		this.title = title;
		this.elementName = elementName;
		this.refName = refName;
		this.elementDeclarations = decls;
		this.minOccurs = minOccurs;
		this.maxOccurs = maxOccurs;
	}

	

	protected Control createDialogArea(Composite parent) {
		//Should not really be here but well,....
		parent.getShell().setText(this.title);
		
		Composite composite = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;
		
		Label nameLabel = new Label(composite, SWT.NONE);
		nameLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		nameLabel.setText("Business Element Name");

		elementNameText = new Text(composite, SWT.BORDER);
		elementNameText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		elementNameText.setText(getElementName()==null? "" : getElementName());
		((GridData)elementNameText.getLayoutData()).widthHint = 200;
		//elementNameText.setSize(100, 22);

		
		Label refLabel = new Label(composite, SWT.NONE);
		refLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		refLabel.setText("Reference Name");

		refCombo = new Combo(composite, SWT.DROP_DOWN | SWT.SIMPLE | SWT.READ_ONLY);
		refCombo.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		refCombo.setItems(elementDeclarations.toArray(new String[elementDeclarations.size()]));
		refCombo.setText(getRefName() == null ? "": getRefName());
		((GridData)refCombo.getLayoutData()).widthHint = 200;
		
		
		if(refCombo.getText()!=""&&refCombo.getText()!=null){
			elementNameText.setText("");
			elementNameText.setEditable(false);
		}
		else{
			elementNameText.setEditable(true);
		}
		
		
		refCombo.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
				
			}

			public void widgetSelected(SelectionEvent e) {
				if(refCombo.getText()!=""&&refCombo.getText()!=null){
					elementNameText.setText("");
					elementNameText.setEditable(false);
				}
				else{
					elementNameText.setEditable(true);
				}
								
			}});
		

		
		Label minLabel = new Label(composite, SWT.NONE);
		minLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		minLabel.setText("Minimum Occurence");

		minOccursText = new Text(composite, SWT.NONE);
		minOccursText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		minOccursText.setDoubleClickEnabled(false);
		minOccursText.setText(""+getMinOccurs());
		
		Label maxLabel = new Label(composite, SWT.NONE);
		maxLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		maxLabel.setText("Maximum Occurence");

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
		elementName = elementNameText.getText();
		refName = refCombo.getText();
		if (	((elementName==null) || ("".equals(elementName))) && 
				((refName == null ) || "".equals(refName))	) {
			MessageDialog.openError(
					this.getShell(), 
					"Error", "The Business Element Name cannot be empty if the reference is empty"
			);
			setReturnCode(-1);
			elementNameText.setFocus();
			return;
		}
		
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

	public String getElementName() {
		return elementName;
	}

	public int getMaxOccurs() {
		return maxOccurs;
	}

	public int getMinOccurs() {
		return minOccurs;
	}

	public String getRefName() {
		return refName;
	}

	

	

}
