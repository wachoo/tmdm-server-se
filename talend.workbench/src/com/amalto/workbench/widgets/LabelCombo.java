package com.amalto.workbench.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class LabelCombo {
	protected Label label;
	protected CCombo combo;
	private Composite composite;
	
	public LabelCombo(FormToolkit toolkit,Composite parent, final String labelName,int comboStyle,int columns){
	    composite= toolkit.createComposite(parent);
		composite.setLayoutData(
                new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1)
        );		
		composite.setLayout(new GridLayout(columns,false)); 
		
	   label = toolkit.createLabel(composite, labelName, SWT.NULL);
       label.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
        );
        combo=new CCombo(composite,comboStyle);
        combo.setLayoutData(    
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );		
	}

	public Control getComposite() {
		return composite;
	}

	public Label getLabel() {
		return label;
	}

	public CCombo getCombo() {
		return combo;
	}


	
}
