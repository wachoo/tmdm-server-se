package com.amalto.workbench.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.xsd.XSDComplexTypeDefinition;

/**
 * this class is meant to encapsulate all widgets rendering concept 
 * it can output a composite to populate concept form
 * @author fliu
 *
 */
public class ConceptComposite {
	
	private  CCombo typeNameText=null;
	private Button sequenceButton = null;
	private Button choiceButton = null;
	private Button allButton = null;
	private Label typeNameLabel = null;
	
	private Composite container = null;

	public  ConceptComposite(Composite parent, boolean encloseTextField,List<XSDComplexTypeDefinition> types, boolean newComplex) {
		
		GridLayout layout = (GridLayout)parent.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;
		List<String> typeNames=new ArrayList<String>();
		for(XSDComplexTypeDefinition type: types){
			typeNames.add(type.getName());
		}
		typeNameLabel = new Label(parent, SWT.NONE);
		typeNameLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
		);
		
		typeNameLabel
				.setText(newComplex == false ? "Please enter the name of the complex type. Leave blank for anonymous"
						: "Please enter the name of the complex type");

		typeNameText = new CCombo(parent,SWT.SINGLE | SWT.BORDER);
		typeNameText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,2,1)
		);
		typeNameText.setItems(typeNames.toArray(new String[typeNames.size()]));

		Group radioGroup = new Group(parent,SWT.SHADOW_NONE);
		radioGroup.setText(encloseTextField ? "" : "Sub-Elements Group");
		radioGroup.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,2,1)
		);
		radioGroup.setLayout(new GridLayout(1,false));
		
		if (encloseTextField)
		{
			typeNameLabel.setParent(radioGroup);
			typeNameText.setParent(radioGroup);
		}
		
		allButton = new Button(radioGroup,SWT.RADIO);
		allButton.setText("All");
		allButton.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		
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

		allButton.setSelection(true);
		container = parent;
	}
	
	public String getText()
	{
		return typeNameText.getText();
	}
	
	
	public void setFocus()
	{
		typeNameText.setFocus();
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
	
	public Composite getComposite()
	{
		return container;
	}
	
	public void setSelectAllWidgets(boolean selected)
	{
		typeNameText.setEnabled(selected);
		sequenceButton.setEnabled(selected);
		choiceButton.setEnabled(selected);
		allButton.setEnabled(selected);
		typeNameLabel.setEnabled(selected);
	}
}
