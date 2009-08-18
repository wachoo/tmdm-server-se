package com.amalto.workbench.dialogs;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InputComboDialog extends Dialog {
	
	private String title;

	private String message;

	private String value = "";
	public String getValue() {
		return value;
	}

	private Button okButton;

	private Combo inputCombo;


	private Text errorMessageText;

	
	private String[] values;

	public InputComboDialog(Shell parentShell,String dialogTitle,
            String dialogMessage,String[] dialogValues, String initialValue) {
		super(parentShell);
		// TODO Auto-generated constructor stub
		this.message = dialogMessage;
		this.title = dialogTitle;
		this.message = dialogMessage;
		this.values = dialogValues;
		this.value = initialValue;
		
	}
	
	
	
	 protected Control createDialogArea(Composite parent) {
	        // create composite
	        Composite composite = (Composite) super.createDialogArea(parent);
	        // create message
	        if (message != null) {
	            Label label = new Label(composite, SWT.WRAP);
	            label.setText(message);
	            GridData data = new GridData(GridData.GRAB_HORIZONTAL
	                    | GridData.GRAB_VERTICAL | GridData.HORIZONTAL_ALIGN_FILL
	                    | GridData.VERTICAL_ALIGN_CENTER);
	            data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
	            label.setLayoutData(data);
	            label.setFont(parent.getFont());
	        }
	        
	        inputCombo = new Combo(composite,SWT.READ_ONLY);
	        inputCombo.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
	                | GridData.HORIZONTAL_ALIGN_FILL));
	        inputCombo.setItems(values);
	        inputCombo.add("");
			for(String pro:values){
				if(pro.equals(value)){
					inputCombo.setText(pro);
					break;
				}
			}
	        
	        
	        errorMessageText = new Text(composite, SWT.READ_ONLY | SWT.WRAP);
	        errorMessageText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
	                | GridData.HORIZONTAL_ALIGN_FILL));
	        errorMessageText.setBackground(errorMessageText.getDisplay()
	                .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

	        applyDialogFont(composite);
	        return composite;
	    }
	
	
	
	
	   protected void buttonPressed(int buttonId) {
	        if (buttonId == IDialogConstants.OK_ID) {
	            value = inputCombo.getText();
	        } else {
	            value = null;
	        }
	        super.buttonPressed(buttonId);
	    }
	   protected void configureShell(Shell shell) {
	        super.configureShell(shell);
	        if (title != null) {
				shell.setText(title);
			}
	    }
	   protected void createButtonsForButtonBar(Composite parent) {
	        // create OK and Cancel buttons by default
	        okButton = createButton(parent, IDialogConstants.OK_ID,
	                IDialogConstants.OK_LABEL, true);
	        createButton(parent, IDialogConstants.CANCEL_ID,
	                IDialogConstants.CANCEL_LABEL, false);
	        //do this here because setting the text will set enablement on the ok
	        // button
	        if (value != null) {
	            inputCombo.setText(value);
	        }
	    }
	   
	   
	   
}
