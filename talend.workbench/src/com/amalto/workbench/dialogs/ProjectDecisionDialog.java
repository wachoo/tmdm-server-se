package com.amalto.workbench.dialogs;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.XtentisPort;

public class ProjectDecisionDialog extends Dialog {
	
	
	protected Combo dataClustersCombo;
	protected Combo dataModelsCombo;
	protected Button overwriteButton;

	private static Pattern dp = Pattern.compile("PROJECT\\s*\\((.*?),(.*?)[,(.*?)]?\\)\\s*",Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
	
	private TreeObject transformerObject = null;
	private String decision = null;
	private SelectionListener caller = null;
	private String title = "";

	/**
	 * @param parentShell
	 */
	public ProjectDecisionDialog(TreeObject transformerObject, String decision, Shell parentShell, String title, SelectionListener caller) {
		super(parentShell);
		this.transformerObject = transformerObject;
		this.decision = decision;
		this.caller = caller;
		this.title = title;
	}


	protected Control createDialogArea(Composite parent) {
				
		//Should not really be here but well,....
		parent.getShell().setText(this.title);
		
		Composite composite = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;
		
		try {
			
			//process the decision
			String dataClusterName = "";
			String dataModelName = "";
			boolean doOverwrite = true;
			Matcher m = dp.matcher(decision);
			if (m.matches()) {
				dataClusterName = m.group(1);
				dataModelName = m.group(2);
				if (m.groupCount()>=4) doOverwrite = (! "false".equals(m.group(3)));
			}
	    
			XtentisPort port = Util.getPort(
					new URL(transformerObject.getEndpointAddress()),
					transformerObject.getUniverse(),
					transformerObject.getUsername(),
					transformerObject.getPassword()
			);
					
			 //Grab the available Clusters
	        WSDataClusterPK[] dcpks = port.getDataClusterPKs(new WSRegexDataClusterPKs(".*")).getWsDataClusterPKs();
	
	        Label dataClustersLabel = new Label(composite,SWT.NULL);
	        dataClustersLabel.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );   
	        dataClustersLabel.setText( "Data Clusters");
	
	        dataClustersCombo = new Combo(composite,SWT.DROP_DOWN);
	        dataClustersCombo.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
	        );        
	        int dataClusterSelect=-1;
	        if (dcpks!=null) {
		        for (int i = 0; i < dcpks.length; i++) {
		        	dataClustersCombo.add(dcpks[i].getPk());
		        	if (dcpks[i].getPk().equals(dataClusterName)) dataClusterSelect = i; 
				}
	        }        
	        dataClustersCombo.select(dataClusterSelect);
			
			 //Grab the available Models
	        WSDataModelPK[] dmpks = port.getDataModelPKs(new WSRegexDataModelPKs(".*")).getWsDataModelPKs();
	        
	        Label dataModelsLabel = new Label(composite,SWT.NULL);
	        dataModelsLabel.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );   
	        dataModelsLabel.setText( "Data Models");
		
	        dataModelsCombo = new Combo(composite,SWT.DROP_DOWN);
	        dataModelsCombo.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
	        );        
	        int dataModelSelect=-1;
	        if (dmpks!=null) {
		        for (int i = 0; i < dmpks.length; i++) {
		        	dataModelsCombo.add(dmpks[i].getPk());
		        	if (dmpks[i].getPk().equals(dataModelName)) dataModelSelect = i; 
				}
	        }        
	        dataModelsCombo.select(dataModelSelect);
	        
	        //Overwrite
	        Label overwriteLabel = new Label(composite,SWT.NULL);
	        overwriteLabel.setLayoutData(
	                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
	        );   
	        overwriteLabel.setText( "Overwrite");
	        
	        overwriteButton = new Button(composite,SWT.CHECK);
	        overwriteButton.setLayoutData(
	                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
	        );   
	        overwriteButton.setSelection(doOverwrite);
	        
		} catch (Exception e) {
			MessageDialog.openError(ProjectDecisionDialog.this.getShell(), "Error Creating Dialog", "Could not create the dialog: "+e.getMessage());
		}
		
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
		getButton(IDialogConstants.OK_ID).setData("dialog",ProjectDecisionDialog.this);
		//no close let Action Handler handle it
	}
	 @Override
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		getButton(IDialogConstants.CANCEL_ID).setData("dialog",ProjectDecisionDialog.this);
		//no close let Action Handler handle it
	}
	 

	/**************************************************************************************************
	 * Public getters read by caller
	 ***************************************************************************************************/

	
	public String getDataClusterName() {
		if (dataClustersCombo.getSelectionIndex() == -1) return null;
		return dataClustersCombo.getItem(dataClustersCombo.getSelectionIndex());
	}

	public String getDataModelName() {
		if (dataModelsCombo.getSelectionIndex() == -1) return null;
		return dataModelsCombo.getItem(dataModelsCombo.getSelectionIndex());
	}

	public boolean doOverwrite() {
		return overwriteButton.getSelection();
	}
	
	
	public String getDecision() {
		return "PROJECT("+getDataClusterName()+","+getDataModelName()+","+(doOverwrite() ? "true" : "false")+")";
	}
		
}
