package com.amalto.workbench.dialogs;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

import com.amalto.workbench.editors.TransformerMainPage;
import com.amalto.workbench.webservices.WSExtractedContent;

public class ProcessResultsDialog extends Dialog {

	private final static int BUTTON_CLOSE = 10;


	protected Combo variablesCombo;
	protected SourceViewer variablesViewer;
	
	protected String title;
	protected TreeMap<String, WSExtractedContent> resultsMap = null;

	/**
	 * @param parentShell
	 */
	public ProcessResultsDialog(Shell parentShell, String title, TreeMap<String, WSExtractedContent> map) {
		super(parentShell);
		this.title = title;
		this.resultsMap = map;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}

	protected Control createDialogArea(Composite parent) {
		
		try {
			
			parent.getShell().setText(title);
			
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout layout = (GridLayout)composite.getLayout();
			layout.numColumns = 2;
			 ((GridData)composite.getLayoutData()).widthHint = 800;
			
			Label variableLabel = new Label(composite,SWT.NONE);
            variableLabel.setLayoutData(
            		new GridData(SWT.LEFT,SWT.CENTER,true,true,1,1)
            );
            variableLabel.setText("Pipeline Variables");

			variablesCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
            variablesCombo.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,true,true,1,1)
            );
            variablesCombo.addModifyListener(new ModifyListener() {
            	public void modifyText(ModifyEvent e) {
            		String output = variablesCombo.getText();
    				if (output.startsWith(TransformerMainPage.DEFAULT_DISPLAY))
    					output = TransformerMainPage.DEFAULT_VAR+output.substring(TransformerMainPage.DEFAULT_DISPLAY.length());
            		ProcessResultsDialog.this.variablesViewer.setDocument(new Document(getText(output)));
            	}
            });
            /*
            variablesCombo.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						ProcessResultsPage.this.variablesViewer.setDocument(new Document(getText(variablesCombo.getText())));
        					}
            			}//keyReleased
            		}//keyListener
            );
            */
			
	        variablesViewer = new SourceViewer(
	        		composite, 
	        		new VerticalRuler(10), SWT.V_SCROLL | SWT.H_SCROLL
	        );
            variablesViewer.getControl().setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,true,true,2,1)
            );
            variablesViewer.configure(new TextSourceViewerConfiguration());
            ((GridData)variablesViewer.getControl().getLayoutData()).minimumHeight = 500;
           
            
            variablesCombo.setFocus();
            
	        refreshData();
	        
	        return composite;

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					this.getShell(),
					"Error", 
					"An error occured trying to create the Views Search window: "+e.getLocalizedMessage()
			);
			return null;
		}
		
	}

	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, BUTTON_CLOSE, "Close",false);
	}



	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case BUTTON_CLOSE:
			this.close();
		}
	}
	

	private static Pattern p=Pattern.compile(".*charset\\s*=[\"|']?(.+)[\"|']([\\s|;].*)?");
	protected String getText(String output) {
		WSExtractedContent ct = resultsMap.get(output);
		String contentType = ct.getContentType();
		byte[] bytes = ct.getWsByteArray().getBytes();
		if (bytes==null) return "";	
		//extract charset
		String charset="UTF8";
		Matcher m = p.matcher(contentType);
		if (m.matches()) {
			charset = m.group(1).trim().toUpperCase();
		}
		if ("UTF-8".equals(charset)) charset = "UTF8";
		//display
		try {
			return	new String(bytes,charset);
		} catch (Exception ex){
			StringWriter sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			return "ERROR - displaying the output with content type of "+contentType+"\n"+sw.toString();
		}
	}
	
	protected void refreshData() {
		try {
			           
            Set< String> outputs = resultsMap.keySet();
            for (Iterator iter = outputs.iterator(); iter.hasNext(); ) {
				String output = (String) iter.next();
				if (output.startsWith(TransformerMainPage.DEFAULT_VAR))
					output = TransformerMainPage.DEFAULT_DISPLAY+output.substring(TransformerMainPage.DEFAULT_VAR.length());
				variablesCombo.add(output);
			}
            variablesCombo.select(0);
			
            variablesViewer.setDocument(new Document(getText(variablesCombo.getText())));
            
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}

}
