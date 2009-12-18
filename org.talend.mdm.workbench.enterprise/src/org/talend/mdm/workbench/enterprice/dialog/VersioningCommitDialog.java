package org.talend.mdm.workbench.enterprice.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.workbench.enterprice.actions.VersioningProgressAction;

import com.amalto.workbench.webservices.WSBackgroundJobPK;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSVersioningCommitItems;
import com.amalto.workbench.webservices.XtentisPort;

public class VersioningCommitDialog extends Dialog {

	private final static int BUTTON_CANCEL = 11;
	
	private XtentisPort port=null;
	private WSItemPK[] wsItemPKs = null;
	protected boolean isItems = false;

	protected Text commentText;
	protected Group confirmGroup; 
	protected TableViewer targetViewer;

	
	public VersioningCommitDialog(Shell shell, XtentisPort port, WSItemPK[] wsItemPKs) {
		super(shell);
		this.port = port;
		this.wsItemPKs = wsItemPKs;
		this.isItems = true;
	}

	protected Control createDialogArea(Composite parent) {
		
		try {

			parent.getShell().setText("Commit");

			String resourcesName= "";
			if (isItems) {
				resourcesName = "Item(s)";
			} else {
				resourcesName = "Object(s)";	
			}
			
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout layout = (GridLayout)composite.getLayout();
			layout.numColumns = 1;
			
			
			Group commentGroup = new Group(composite,SWT.SHADOW_NONE);
			commentGroup.setLayout(new GridLayout(1,false));
			commentGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			commentGroup.setText("Comment");

			commentText = new Text(commentGroup, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			commentText.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			commentText.setText("");
			((GridData)commentText.getLayoutData()).widthHint=250;
			((GridData)commentText.getLayoutData()).heightHint=25;
			
	
			Button commitButton = new Button(commentGroup,SWT.PUSH);
			commitButton.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			commitButton.setText("Commit");
			commitButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {
						commitResources();
				}				
			});
							
			confirmGroup = new Group(composite,SWT.SHADOW_NONE);
			confirmGroup.setLayout(new GridLayout(1,true));
			confirmGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			confirmGroup.setText(resourcesName);
					
			targetViewer = new TableViewer(confirmGroup);
			targetViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
            );
            ((GridData)targetViewer.getControl().getLayoutData()).heightHint=150;
            targetViewer.setContentProvider(new ArrayContentProvider());
            targetViewer.setLabelProvider(new ITableLabelProvider() {
            	public String getColumnText(Object element, int columnIndex) {
            		WSItemPK entry = (WSItemPK) element;
					return getItemPKUniqueID(entry);
            	}
            	public Image getColumnImage(Object element, int columnIndex) {
            		return null;
            	}
            	public void addListener(ILabelProviderListener listener) {}
            	public void dispose() {}
            	public boolean isLabelProperty(Object element, String property) {
            		return false;
            	}
            	public void removeListener(ILabelProviderListener listener) {}
            });
           
			//hookContextMenu();

			refreshData();
			
		    return composite;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					this.getShell(),
					"Error", 
					"An error occured trying to create the Commit Dialog: "+e.getLocalizedMessage()
			);
			return null;
		}
	}
		
	private void refreshData() {
		
		if (isItems&&wsItemPKs!=null&&wsItemPKs.length>0) {
			targetViewer.setInput(wsItemPKs);
			//targetViewer.setSelection(new StructuredSelection(wsItemPKs[0]));
		}
		
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, BUTTON_CANCEL, "Cancel",true);
	}


	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case BUTTON_CANCEL:
			this.close();
		}
	}
	
	/***********************************************
	 * Search Documents
	 ***********************************************/
	
	public void commitResources() {

		if ("".equals(commentText.getText().trim())) {
			MessageDialog.openError(this.getShell(), "Error", "Please enter a comment");
			return;
		}

		if(targetViewer.getTable().getItemCount()>0&&isItems){
			
			try {
		        WSBackgroundJobPK jobPK =
			        this.port.versioningCommitItems(new WSVersioningCommitItems(
			        		null,
			        		this.wsItemPKs,
			        		commentText.getText()
			        ));
		        
		        new VersioningProgressAction(this.getShell(),this.port,jobPK).run();
		         
			} catch (Exception exx) {
				exx.printStackTrace();
				MessageDialog.openError(
						VersioningCommitDialog.this.getShell(),
						"Error", 
						"Unable to commit the documents : "+exx.getLocalizedMessage()
				);
			}
			
		}
		
		this.close();
	}
	
	private String getItemPKUniqueID(WSItemPK wsItemPK) {
    	String fname = wsItemPK.getWsDataClusterPK().getPk()+"."+wsItemPK.getConceptName();
    	for (int i = 0; i < wsItemPK.getIds().length; i++) {
    		fname+="."+(wsItemPK.getIds()[i]==null? "" : wsItemPK.getIds()[i].trim()); //trim added due to exist bu triming the ids
		}
    	return fname;
	}
}
