package com.amalto.workbench.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class SelectFieldDialog  extends Dialog{
	String title;
	private CCombo fieldNameCombo;
	private List<String>fields;
	private String field;
	private String defualtField;
	public SelectFieldDialog(Shell parentShell,String title, List<String>fields, String defualtField) {
		super(parentShell);
		this.title=title;
		this.fields=fields;
		this.defualtField=defualtField;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText(this.title);
		
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		
		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		serverLabel.setText("Field Name");


		fieldNameCombo=new CCombo(composite,SWT.DROP_DOWN|SWT.BORDER);
		fieldNameCombo.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		fieldNameCombo.setItems(fields.toArray(new String[fields.size()]));
		if(fields.size()>0){
			if(defualtField==null)
				fieldNameCombo.select(0);
			else
				fieldNameCombo.setText(defualtField);
		}			
		return composite;
	}
	

	public String getField() {
		return field;
	}
	
	@Override
	protected void okPressed() {
		field=fieldNameCombo.getText();
		super.okPressed();
	}
}
