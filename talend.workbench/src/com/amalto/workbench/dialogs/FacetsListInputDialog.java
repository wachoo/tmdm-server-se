package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

public class FacetsListInputDialog extends Dialog {

	protected  List itemsList=null;
	protected Text newItemText = null;
	
	private ArrayList items = null;

	private SelectionListener caller = null;
	private String title = "";
	

	/**
	 * @param parentShell
	 */
	public FacetsListInputDialog(SelectionListener caller, Shell parentShell, String title) {
		this(caller,parentShell,title,new ArrayList());
	}

	/**
	 * @param parentShell
	 */
	public FacetsListInputDialog(
			SelectionListener caller, 
			Shell parentShell, 
			String title,
			ArrayList items
			) {
		super(parentShell);
		this.caller = caller;
		this.title = title;
		this.items = items;
	}

	

	protected Control createDialogArea(Composite parent) {
		//Should not really be here but well,....
		parent.getShell().setText(this.title);
		
		Composite composite = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;
		
		Label serverLabel = new Label(composite, SWT.NONE);
		serverLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
		);
		serverLabel.setText("New Item");

		newItemText = new Text(composite, SWT.NONE);
		newItemText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		newItemText.setText("");
		((GridData)newItemText.getLayoutData()).widthHint = 100;

		Button addButton = new Button(composite,SWT.PUSH);
		addButton.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		addButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
		addButton.setToolTipText("Add");
		parent.getShell().setDefaultButton(addButton);
		addButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	if (! "".equals(newItemText.getText()))
            		if (! Arrays.asList(itemsList.getItems()).contains(newItemText.getText()))
            			itemsList.add(newItemText.getText());
            }
        });
		
		Button deleteButton = new Button(composite,SWT.PUSH);
		deleteButton.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		deleteButton.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
		deleteButton.setToolTipText("Delete");
		deleteButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	ArrayList<String> backup = new ArrayList<String>(Arrays.asList(itemsList.getItems()));
            	java.util.List sels = Arrays.asList(itemsList.getSelection());
            	for (Iterator iter = sels.iterator(); iter.hasNext(); ) {
					String sel = (String) iter.next();
					backup.remove(sel);
				}
            	itemsList.setItems(backup.toArray(new String[backup.size()]));
            }
        });

		itemsList = new List(composite,SWT.MULTI |SWT.V_SCROLL);
		itemsList.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
		);
		((GridData)itemsList.getLayoutData()).heightHint = 100;
		for (Iterator iter = items.iterator(); iter.hasNext(); ) {
			String item = (String) iter.next();
			itemsList.add(item);
		}
				
	    return composite;
	}

	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,false);
	    createButton(parent, IDialogConstants.CANCEL_ID,IDialogConstants.CANCEL_LABEL, false);
		getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
	  
	}
	
	protected void okPressed() {
		items = new ArrayList<String>();
		items.addAll(Arrays.asList(itemsList.getItems()));
		setReturnCode(OK);
		//no close let Action Handler handle it
	}

	public ArrayList getItems() {
		return items;
	}
	

}
