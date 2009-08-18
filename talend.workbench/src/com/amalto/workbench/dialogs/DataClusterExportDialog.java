package com.amalto.workbench.dialogs;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.FileSelectWidget;
import com.amalto.workbench.widgets.LabelCombo;
import com.amalto.workbench.widgets.WidgetFactory;

public class DataClusterExportDialog extends Dialog{
	FormToolkit toolkit=new WidgetFactory();
	private LabelCombo comboDataCluster;
	TreeObject xObject;
	private FileSelectWidget fw;
	private String server;
	String dataCluster;
	String filename;
	public DataClusterExportDialog(Shell parentShell,TreeObject xObject) {
		super(parentShell);
		this.xObject=xObject;
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		parent.getShell().setText("Data Cluster Export");
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 1;

		comboDataCluster=new LabelCombo(toolkit,composite,"Data Cluster",SWT.BORDER,2);
		comboDataCluster.getCombo().setEditable(false);
		//List<String> dcs=Util.getCachedXObjectsNameSet(this.xObject, TreeObject.DATA_CLUSTER);
		List<String> dcs=Util.getChildren(this.xObject.getServerRoot(), TreeObject.DATA_CLUSTER);
		dcs.add(0,"ALL");
		comboDataCluster.getCombo().setItems(dcs.toArray(new String[dcs.size()]));
		
		//file
		 
		if(xObject!=null&& xObject.getDisplayName()!=null){
			if(EXtentisObjects.DataCluster.getDisplayName().equalsIgnoreCase(xObject.getDisplayName())){
				comboDataCluster.getCombo().select(0);
			}else{				
				comboDataCluster.getCombo().setText(xObject.getDisplayName());
			}
			//fw.getText().setText(xObject.getDisplayName());
		}	
		fw=new FileSelectWidget(composite,"Target      ",new String[]{"*.*","*.zip"}, comboDataCluster.getCombo().getText());
		comboDataCluster.getCombo().addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {				
				
			}

			public void widgetSelected(SelectionEvent e) {
				if(comboDataCluster.getCombo().getText().length()>0){
					fw.getText().setText(comboDataCluster.getCombo().getText());
					fw.setFilename(comboDataCluster.getCombo().getText());
				}
			}			
		});
		return composite;
	}
	
	@Override
	protected void okPressed() {
		///db/CONF -d c:/CONF.zip";
		if(comboDataCluster.getCombo().getText().trim().length()==0){
			MessageDialog.openError(null, "Error", "Data Cluster should not be null!");
			comboDataCluster.getCombo().setFocus();
			return;
		}
		if(fw.getText().getText().trim().length()==0 ){
			MessageDialog.openError(null, "Error", "Target should not be null!");
			fw.getText().setFocus();
			return;
		}
		String url=xObject.getServerRoot().getEndpointAddress();
		server=null;
		try {
			 server= new URL(url).getHost();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}		
		dataCluster=comboDataCluster.getCombo().getText();
		filename=fw.getText().getText();
		Job job=new Job("Export Data Clusters : " + dataCluster+" ..."){
			@Override
			public IStatus run(IProgressMonitor monitor) {	
				try{					
					monitor.beginTask("Export Data Clusters : " + dataCluster+" ...", IProgressMonitor.UNKNOWN);
					Util.exportDataCluster(dataCluster, filename,server, monitor);
					monitor.done();
					return Status.OK_STATUS;
				}catch(Exception e){
					e.printStackTrace();
					return Status.CANCEL_STATUS;
				}
			}			
		};
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
		
		super.okPressed();
	}
	
//	@Override
//	protected Control createButtonBar(Composite parent) {
//		// TODO Auto-generated method stub
//		Composite composite = (Composite) super.createDialogArea(parent);
//		composite.setBackground(composite.getDisplay().getSystemColor(SWT.COLOR_WHITE));
//		createButton(composite, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
//		createButton(composite, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
//		return composite;
//	}	
}
