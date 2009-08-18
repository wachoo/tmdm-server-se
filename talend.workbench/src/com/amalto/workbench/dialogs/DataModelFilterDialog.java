package com.amalto.workbench.dialogs;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.DataModelFilter;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.LabelCombo;
import com.amalto.workbench.widgets.WidgetFactory;

public class DataModelFilterDialog extends Dialog {
	FormToolkit toolkit=new WidgetFactory();
	TreeObject xObject;
	DataModelFilter dataModelFilter;
	private LabelCombo comboRole;
	private Button btnReadOnly;
	private Button btnWrite;
	private Button btnHidden;
	private Button btnAll;
	public DataModelFilterDialog(Shell parentShell,TreeObject xObject, DataModelFilter dataModelFilter) {
		super(parentShell);
		this.xObject=xObject;
		this.dataModelFilter=dataModelFilter;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Data Model Filter");
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 1;
		
		comboRole=new LabelCombo(toolkit,composite,"Role",SWT.BORDER,2);
		comboRole.getCombo().setEditable(false);
		//List<String> roles=Util.getCachedXObjectsNameSet(this.xObject, TreeObject.ROLE);
		List<String> roles=Util.getChildren(this.xObject.getServerRoot(), TreeObject.ROLE);
		comboRole.getCombo().setItems(roles.toArray(new String[roles.size()]));
		if(dataModelFilter.getRole()==null || dataModelFilter.getRole().length()==0){
			comboRole.getCombo().select(0);
		}else{
			comboRole.getCombo().setText(dataModelFilter.getRole());
		}

		btnAll=toolkit.createButton(composite, "All", SWT.RADIO);
		btnAll.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		btnAll.setSelection(dataModelFilter.isAll());
		
		btnReadOnly=toolkit.createButton(composite, "Read only", SWT.RADIO);
		btnReadOnly.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		btnReadOnly.setSelection(dataModelFilter.isReadOnly());
		
		btnWrite=toolkit.createButton(composite, "Write access", SWT.RADIO);
		btnWrite.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		btnWrite.setSelection(dataModelFilter.isWriteAccess());
		
		btnHidden=toolkit.createButton(composite, "Hide access", SWT.RADIO);
		btnHidden.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,false,false,1,1));
		btnHidden.setSelection(dataModelFilter.isHiddenAccess());
		return composite;
	}
	
	@Override
	protected void okPressed() {
		dataModelFilter.setRole(comboRole.getCombo().getText());
		dataModelFilter.setReadOnly(btnReadOnly.getSelection());
		dataModelFilter.setWriteAccess(btnWrite.getSelection());
		dataModelFilter.setHiddenAccess(btnHidden.getSelection());
		dataModelFilter.setAll(btnAll.getSelection());
		super.okPressed();
	}
	@Override
	protected Control createButtonBar(Composite parent) {
		// TODO Auto-generated method stub
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		createButton(composite, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(composite, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		return composite;
	}
}
