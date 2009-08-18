package com.amalto.workbench.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author aiming
 *
 */
public class LabelText {
	protected Label label;
	protected Text text;
	WidgetFactory factory =new WidgetFactory();
	public LabelText(Composite parent, final String labelName){
       label = factory.createLabel(parent, labelName, SWT.NULL);
       label.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
        );
        text = factory.createText(parent, "",SWT.BORDER);
        text.setLayoutData(    
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );		
        factory.adapt(parent);
	}
	public Label getLabel() {
		return label;
	}
	public void setLabel(Label label) {
		this.label = label;
	}
	public Text getText() {
		return text;
	}
	public void setText(Text text) {
		this.text = text;
	}
	
}
