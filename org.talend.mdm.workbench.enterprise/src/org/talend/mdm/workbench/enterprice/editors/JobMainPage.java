package org.talend.mdm.workbench.enterprice.editors;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.editors.AMainPage;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.XtentisPort;

public class JobMainPage extends AMainPage implements IXObjectModelListener {
	protected String jobName;
	protected Label statusLabel;
	 public JobMainPage(FormEditor editor) {
	        super(
	        		editor,
	        		JobMainPage.class.getName(),
	        		"Job "+((XObjectEditorInput)editor.getEditorInput()).getName()
	        		+Util.getRevision((TreeObject)((XObjectEditorInput)editor.getEditorInput()).getModel())
	        );  
	        jobName = ((XObjectEditorInput)editor.getEditorInput()).getName();
	    }
	@Override
	protected void createCharacteristicsContent(FormToolkit toolkit,Composite charSection) {
		// TODO Auto-generated method stub
		
	}
	
	protected void createFormContent(IManagedForm managedForm) {

        try {
        	managedForm.getForm().setText(this.getTitle());
        	
        	//get the toolkit
        	FormToolkit toolkit = managedForm.getToolkit();
        	
        	//get the body
        	Composite composite = managedForm.getForm().getBody();
        	composite.setLayout(new GridLayout(1,false));
        	
        	        	
        	//Create a Router status holder
        	Composite statusComposite = toolkit.createComposite(composite);
            statusComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            statusComposite.setLayout(new GridLayout(3,false));
            Label descriptionLabel = toolkit.createLabel(statusComposite, "Service Status: ", SWT.NULL);
            descriptionLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            statusLabel = toolkit.createLabel(statusComposite, "                                           ", SWT.NULL);
            statusLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            //start/stop/suspend/resume
            Button checkButton = toolkit.createButton(statusComposite, "Check", SWT.CENTER);
            checkButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            checkButton.addSelectionListener(new SelectionListener (){

				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					checkServiceStatus();
				}
            	
            });
            Composite separator = toolkit.createCompositeSeparator(composite);
        	separator.setLayoutData(    
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
        	((GridData)separator.getLayoutData()).heightHint = 2;
        }
        catch (Exception e) {
            e.printStackTrace();
        }	
	}
	
	
	public void checkServiceStatus() {
		try {//
			String URLPath ="http://"+getXObject().getEndpointHost()+":"+getXObject().getEndpointPort()+"/"+this.jobName+"/services/"+this.jobName.substring(0,this.jobName.lastIndexOf("_"));
			org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
			HttpMethod method = new GetMethod(URLPath);
			
			client.executeMethod(method);
			//System.out.println(method.getStatusLine().toString());
			
	        if(method.getStatusLine().toString().indexOf("OK")!=-1)
	        	statusLabel.setText("Ready");
	        else
				statusLabel.setText("Fail");
	        method.releaseConnection();
		}catch (Exception e) {
			
		e.printStackTrace();
		MessageDialog.openError(
				this.getSite().getShell(),
				"Error", 
				"An error occured trying to check the job : "+e.getLocalizedMessage()
		);
		}
	}		
	@Override
	protected void commit() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void createActions() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void refreshData() {
		// TODO Auto-generated method stub
try {
			
    		XtentisPort port = Util.getPort(getXObject());
    		//WSRoutingEngineV2Status wsStatus = port.routingEngineV2Action(new WSRoutingEngineV2Action(WSRoutingEngineV2ActionCode.STATUS));
    		//WSJobStatus wsjobStatus = port.checkJobStatusAction(new WSJobStatusAction(new Boolean(false) ));
			//statusLabel.setText(wsjobStatus.getValue());
 
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		// TODO Auto-generated method stub
		refreshData();
	}


}
