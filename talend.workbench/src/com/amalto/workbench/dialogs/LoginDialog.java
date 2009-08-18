package com.amalto.workbench.dialogs;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class LoginDialog extends Dialog {

	private static String f = System.getProperty("user.dir")+"/.mdmworkbench.conf";
	
	private Properties properties = new Properties();
	private Collection<String> endpoints;

	private Collection<String> universes;

	private Combo endpointsCombo=null;
	private Text usernameText=null;
	private Text passwordText=null;
	
	private SelectionListener caller = null;
	private String title = "";

	private Combo universeCombo;
	


	/**
	 * @param parentShell
	 */
	public LoginDialog(SelectionListener caller, Shell parentShell, String title) {
		super(parentShell);
		this.caller = caller;
		this.title = title;
		setDefaultImage(ImageCache.getCreatedImage(EImage.TALEND_PICTO.getPath()));
	}


	protected Control createDialogArea(Composite parent) {
		
		endpoints = Arrays.asList(new String[]{Util.default_endpoint_address});
		try {
			properties.load(new FileInputStream(f));
			if (properties.getProperty("endpoints")!=null)	 
				endpoints=Arrays.asList(properties.getProperty("endpoints").split(","));
		} catch (Exception e) {}

		universes = Arrays.asList(new String[]{""});
		try {
			properties.load(new FileInputStream(f));
			if (properties.getProperty("universes")!=null)	 
				universes=Arrays.asList(properties.getProperty("universes").split(","));
		} catch (Exception e) {}
		
		//Should not really be here but well,....
		parent.getShell().setText(this.title);
		
		Composite composite = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;
		
		Label endpointsLabel = new Label(composite, SWT.NONE);
		endpointsLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.CENTER,true,false,1,1)
		);
		endpointsLabel.setText("Server");

		endpointsCombo = new Combo(composite, SWT.NONE);
		endpointsCombo.setLayoutData(
			new GridData(SWT.FILL,SWT.CENTER,true,false,1,1)
		);
		((GridData)endpointsCombo.getLayoutData()).widthHint = 300;
		for (Iterator<String> iter = endpoints.iterator(); iter.hasNext(); ) {
			String host = iter.next();
			endpointsCombo.add(host);	
		}
		endpointsCombo.select(0);
		

		Label usernameLabel = new Label(composite, SWT.NONE);
		usernameLabel.setLayoutData(
			new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
		);
		usernameLabel.setText("Username");

		usernameText = new Text(composite, SWT.NONE);
		usernameText.setLayoutData(
			new GridData(SWT.FILL,SWT.CENTER,true,false,1,1)
		);
		usernameText.setDoubleClickEnabled(false);

		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setLayoutData(
			new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
		);
		passwordLabel.setText("Password");

		passwordText = new Text(composite, SWT.PASSWORD);
		passwordText.setLayoutData(
			new GridData(SWT.FILL,SWT.CENTER,true,false,1,1)
		);
		
		//universe
		Label universeLabel = new Label(composite, SWT.NONE);
		universeLabel.setLayoutData(
			new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
		);
		universeLabel.setText("Universe");

		universeCombo = new Combo(composite, SWT.NONE);
		universeCombo.setLayoutData(
			new GridData(SWT.FILL,SWT.CENTER,true,false,1,1)
		);
		((GridData)universeCombo.getLayoutData()).widthHint = 300;
		for (Iterator<String> iter = universes.iterator(); iter.hasNext(); ) {
			String host = iter.next();
			universeCombo.add(host);	
		}
		universeCombo.select(0);
		
	    return composite;
	}

	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
		/*
	      createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
	                true);
	        createButton(parent, IDialogConstants.CANCEL_ID,
	                IDialogConstants.CANCEL_LABEL, false);
	  */
	}
	
	protected void okPressed() {
		//save hosts
		try {
//			String currentHost = endpointsCombo.getText();
//			String endpointsString = currentHost;
//			int i =0;
//			for (Iterator<String> iter = endpoints.iterator(); iter.hasNext(); ) {
//				String endpoint = iter.next();
//				if (! endpoint.equals(currentHost)) endpointsString+=","+endpoint;
//				if (++i == 10) break;
//			}
//			properties.setProperty("endpoints", endpointsString);
//			//save universe
//			String currentUniverse = universeCombo.getText();
//			String universeString = currentUniverse;
//			i =0;
//			for (Iterator<String> iter = universes.iterator(); iter.hasNext(); ) {
//				String universe = iter.next();
//				if (! universe.equals(currentUniverse)) universeString+=","+universe;
//				if (++i == 5) break;
//			}
//			properties.setProperty("universes", universeString);
//			
//			properties.store(new FileOutputStream(f), null);
		} catch (Exception e) {}
		
		setReturnCode(OK);
		//no close let Action Handler handle it
	}
	
	public Properties getProperties() {
		return properties;
	}


	public void saveUserTypes(){
		
	}
	

	public String getPasswordText() {
		return passwordText.getText();
	}


	public String getUsernameText() {
		return usernameText.getText();
	}
	
	public String getServer() {
		return endpointsCombo.getText();
	}

	public String getUniverse() {
		return universeCombo.getText();
	}

	

}
