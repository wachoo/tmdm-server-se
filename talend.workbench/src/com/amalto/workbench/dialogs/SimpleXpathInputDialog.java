package com.amalto.workbench.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class SimpleXpathInputDialog extends Dialog {

	private AMainPageV2 parentPage;
	
	private String title = "";

	private String dialogMessage = "";

	private String initialValue = "";

	private SelectionListener caller = null;
	
	private String xpath = "";
	private String dataModelName;

	/**
	 * @param parentShell
	 */
	public SimpleXpathInputDialog(AMainPageV2 parentPage, String title,
			String dialogMessage, String initialValue, SelectionListener caller,String dataModelName) {
		super(parentPage.getSite().getShell());
		this.parentPage=parentPage;
		this.title = title;
		this.dialogMessage = dialogMessage;
		this.initialValue = initialValue;
		this.caller = caller;
		this.dataModelName = dataModelName;
	}

	protected Control createDialogArea(Composite parent) {
		// Should not really be here but well,....
		parent.getShell().setText(this.title);

		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		
		Label dialogMessageLbl = new Label(composite, SWT.NULL);
		dialogMessageLbl.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,2,1));
		dialogMessageLbl.setText(dialogMessage);
		
		final Text textControl = new Text(composite, SWT.BORDER|SWT.SINGLE);
		textControl.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true,1,1));
		((GridData)textControl.getLayoutData()).minimumWidth = 300;
		textControl.addModifyListener(new ModifyListener(){

			public void modifyText(ModifyEvent e) {
				SimpleXpathInputDialog.this.xpath=textControl.getText();
			}
			
		});
		
		
		Button xpathButton = new Button(composite,SWT.PUSH | SWT.CENTER);
		xpathButton.setLayoutData(new GridData(SWT.FILL,SWT.FILL,false,true,1,1));
		xpathButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
		xpathButton.setToolTipText("Select xpath");
		xpathButton.addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
			public void widgetSelected(SelectionEvent e) {
				XpathSelectDialog dlg = new XpathSelectDialog(
						parentPage.getSite().getShell(),
						parentPage.getXObject().getParent(),
						"Select Xpath",
						parentPage.getSite(),
						false,
						dataModelName
				);
		        dlg.setBlockOnOpen(true);
				dlg.open();
				
				if (dlg.getReturnCode() == Window.OK)  {
					textControl.setText(dlg.getXpath());
					dlg.close();
				}
				
			}
			
		});
		
		textControl.forceFocus();
		
		//init value
		textControl.setText(initialValue==null?"":initialValue);
		
		return composite;
	}

	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
		getButton(IDialogConstants.CANCEL_ID).addSelectionListener(this.caller);
	}


	@Override
	protected void okPressed() {
		setReturnCode(OK);
		getButton(IDialogConstants.OK_ID).setData("dialog",SimpleXpathInputDialog.this);
		//no close let Action Handler handle it
	}
	 @Override
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		getButton(IDialogConstants.CANCEL_ID).setData("dialog",SimpleXpathInputDialog.this);
		//no close let Action Handler handle it
	}
	
	public String getXpath() {
		return xpath;
	}

	

}
