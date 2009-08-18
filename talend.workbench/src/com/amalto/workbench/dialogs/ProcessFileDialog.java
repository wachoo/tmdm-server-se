package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Vector;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.amalto.workbench.editors.TransformerMainPage;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.webservices.WSTransformerProcessStep;
import com.amalto.workbench.webservices.WSTransformerV2;
/**
 *@deprecated 
 * @author cli
 *
 */
public class ProcessFileDialog extends Dialog {

	
	protected Composite variablesComposite;
	protected Combo encodingCombo;
	protected Combo mimeTypeCombo;
	protected TableViewer variablesViewer;
	protected ProjectDecisionDialog projectDecisionDialog;
	
	protected String projectDecision = null;
	
	protected TreeObject transformerObject = null;
	private String fileName = null;
	
	protected LinkedHashMap<String, String> variablesMap = new LinkedHashMap<String, String>();
	private SelectionListener caller = null;
	private String title = "";
	
	protected static String DISCARD = "DISCARD";
	protected static String CONSOLE = "TO CONSOLE";
	protected static String PROJECT = "PROJECT";
	
	
    //put the names in an array
    protected static Vector<String> DECISIONS = new Vector<String>(Arrays.asList(new String[]{DISCARD,CONSOLE,PROJECT}));

	/**
	 * @param parentShell
	 */
	public ProcessFileDialog(TreeObject transformerObject, String fileName, Shell parentShell, String title, SelectionListener caller) {
		super(parentShell);
		this.transformerObject = transformerObject;
		this.fileName = fileName;
		this.caller = caller;
		this.title = title;
		//feed the output variables with the default processs to console
		WSTransformerProcessStep[] pluginSpecs = ((WSTransformerV2)transformerObject.getWsObject()).getProcessSteps();
		if (pluginSpecs!=null) {
			for (int i = 0; i < pluginSpecs.length; i++) {
				//TODO
				variablesMap.put(pluginSpecs[i].getPluginJNDI(), CONSOLE); 
			}
		}
	}


	protected Control createDialogArea(Composite parent) {
				
		parent.getShell().setMinimumSize(400, 200);
		
		//Should not really be here but well,....
		parent.getShell().setText(this.title);

		Composite composite = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;
		
		Label fileLabel = new Label(composite, SWT.NONE);
		fileLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
		);
		fileLabel.setText("File: "+fileName);

        Label mimeTypeLabel = new Label(composite,SWT.NULL);
        mimeTypeLabel.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
        );   
        mimeTypeLabel.setText( "Encoding");
        mimeTypeCombo = new Combo(composite,SWT.DROP_DOWN);
        mimeTypeCombo.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
        );   
        mimeTypeCombo.add("text/xml");
        mimeTypeCombo.add("text/plain");
        if (
        		fileName.toLowerCase().endsWith("csv") ||
        		fileName.toLowerCase().endsWith("txt")
        	)
        	mimeTypeCombo.select(1);
        else
        	mimeTypeCombo.select(0);
        mimeTypeCombo.setVisible(true);

		
        Label encodingLabel = new Label(composite,SWT.NULL);
        encodingLabel.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
        );   
        encodingLabel.setText( "Encoding");
        encodingCombo = new Combo(composite,SWT.DROP_DOWN);
        encodingCombo.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
        );   
        encodingCombo.add("UTF-8");
        encodingCombo.add("ISO-8859-15");
        encodingCombo.add("ISO-8859-2");
        encodingCombo.add("ISO-8859-1");
        encodingCombo.add("UCS4");
        encodingCombo.select(0);
        encodingCombo.setVisible(true);
        
		//Variables
        variablesComposite = new Composite(composite, SWT.BORDER);
        variablesComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
        );
        variablesComposite.setLayout(new GridLayout(3,false));

		
        final String OUTPUT_VARIABLE="Output Variable";
        final String DECISION = "Decision";
        
        variablesViewer = new TableViewer(variablesComposite);
        variablesViewer.getControl().setLayoutData(    
                new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
        );
        ((GridData)variablesViewer.getControl().getLayoutData()).heightHint=100;
        // Set up the underlying table
        Table table = variablesViewer.getTable();
        //table.setLayoutData(new GridData(GridData.FILL_BOTH));

        new TableColumn(table, SWT.LEFT).setText(OUTPUT_VARIABLE);
        new TableColumn(table, SWT.LEFT).setText(DECISION);
        table.getColumn(0).setWidth(200);
        for (int i = 0, n = table.getColumnCount(); i < n; i++) {
          table.getColumn(i).pack();
        }
        
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
       

        // Create the cell editors --> We actually discard those later: not natural for an user
        CellEditor[] editors = new CellEditor[2];
        editors[0] = new TextCellEditor(table);
        editors[1] = new ComboBoxCellEditor(table,DECISIONS.toArray(new String[DECISIONS.size()]));
        variablesViewer.setCellEditors(editors);
        
        //set the content provider
        variablesViewer.setContentProvider(new IStructuredContentProvider() {
        	public void dispose() {}
        	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
        	public Object[] getElements(Object inputElement) {
        		LinkedHashMap<String, String> descisionsMap = (LinkedHashMap<String, String>)inputElement; 
        		Set<String> outputs = descisionsMap.keySet();
        		ArrayList<VariableLine> lines = new ArrayList<VariableLine>();
        		for (Iterator iter = outputs.iterator(); iter.hasNext(); ) {
					String output = (String) iter.next();
					VariableLine line = new VariableLine(output,ProcessFileDialog.getIndexFromDecision(variablesMap.get(output)));
					lines.add(line);
				}
        		return lines.toArray(new VariableLine[lines.size()]);
        	}
        });
        
        //set the label provider
        variablesViewer.setLabelProvider(new ITableLabelProvider() {
        	public boolean isLabelProperty(Object element, String property) {return false;}
        	public void dispose() {}
        	public void addListener(ILabelProviderListener listener) {}
        	public void removeListener(ILabelProviderListener listener) {}
        	public String getColumnText(Object element, int columnIndex) {
        		VariableLine line = (VariableLine) element;
        		switch (columnIndex) {
        		    case 0:
        		      return line.getOutput() == null ? TransformerMainPage.DEFAULT_DISPLAY : line.getOutput();
        		    case 1:
        		      return variablesMap.get(line.getOutput());
    		    }
        		return "";
        	}
        	public Image getColumnImage(Object element, int columnIndex) {return null;}
        });

        // Set the column properties
        variablesViewer.setColumnProperties(new String[]{OUTPUT_VARIABLE,DECISION});
        
        //set the Cell Modifier
        variablesViewer.setCellModifier(
	        new ICellModifier() {
	        	public boolean canModify(Object element, String property) {
	        		if (DECISION.equals(property)) return true;
	        		return false;
	        	}
	        	public void modify(Object element, String property, Object value) {
	        		if (DECISION.equals(property)) {
		        		TableItem tItem = (TableItem) element;
		        		VariableLine line = (VariableLine) tItem.getData();
		        		if (((Integer)value).intValue()==DECISIONS.indexOf(PROJECT)) {
		        			projectDecisionDialog = new ProjectDecisionDialog(
		        					transformerObject,
		        					variablesMap.get(line.getOutput()),
		        					ProcessFileDialog.this.getShell(),
		        					"Projecting output "+line.getOutput(),
		        					new SelectionListener() {
		        						public void widgetDefaultSelected(SelectionEvent e) {}
		        						public void widgetSelected(SelectionEvent e) {
		        							if (projectDecisionDialog.getReturnCode() == Window.OK) {
		        								if (projectDecisionDialog.getDataClusterName()==null) {
		        									MessageDialog.openError(projectDecisionDialog.getShell(), "PROJECT Error", "Please select a Data Cluster to project to.");
		        									return;
		        								}
		        								if (projectDecisionDialog.getDataModelName()==null) {
		        									MessageDialog.openError(projectDecisionDialog.getShell(), "PROJECT Error", "Please select a Data Model to validate against.");
		        									return;
		        								}
		        								projectDecision = projectDecisionDialog.getDecision();
		        							} else {
		        								projectDecision = null;
		        							}
		        							projectDecisionDialog.close();
		        						}
		        					}
		        			);
		        			projectDecisionDialog.setBlockOnOpen(true);
		        			projectDecisionDialog.open();
							if (projectDecision != null) {
								line.setDecision((Integer)value);
								variablesMap.put(line.getOutput(), projectDecision);
							}
		        		} else {
		        			line.setDecision((Integer)value);
		        			variablesMap.put(line.getOutput(), DECISIONS.get(line.getDecision().intValue()));
		        		}
		        		variablesViewer.refresh(line);
	        		}
	        	}
	        	public Object getValue(Object element, String property) {
	//        		System.out.println("getValue() "+property);
	        		VariableLine line = (VariableLine) element;
	        		if (OUTPUT_VARIABLE.equals(property)) {
	        			return line.getOutput();
	        		}
	        		if (DECISION.equals(property)) {
	        			return line.getDecision();
	        		}
	        		return null;
	        	}
	        }
		);
        
        //Listen for changes in the selection of the viewer to display additional parameters
        variablesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
        	public void selectionChanged(SelectionChangedEvent event) {}
        });
        
        //display for Delete Key events to delete an instance
        variablesViewer.getTable().addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent e) {}
        	public void keyReleased(KeyEvent e) {
//        		System.out.println("Table keyReleased() ");
        		/*
        		if ((e.stateMask==0) && (e.character == SWT.DEL) && (variablesViewer.getSelection()!=null)) {
        			VariableLine line = (VariableLine)((IStructuredSelection)variablesViewer.getSelection()).getFirstElement();
        			LinkedHashMap<String, String> variables = (LinkedHashMap<String, String>)variablesViewer.getInput(); 
        			variables.remove(line.getOutput());
        			variablesViewer.refresh();
        		}
        		*/
        	}
        });
        
		variablesViewer.setInput(variablesMap);
        variablesViewer.refresh();
		
		encodingCombo.setFocus();
		
	    return composite;
	}

	
	protected static Integer getIndexFromDecision(String decision) {
		if (decision == null) return new Integer(1);
		if (DISCARD.equals(decision)) return new Integer(0);
		if (CONSOLE.equals(decision)) return new Integer(1);
		if (decision.startsWith(PROJECT+"(")) return new Integer(2);
		return  new Integer(1);
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).addSelectionListener(this.caller);
		getButton(IDialogConstants.CANCEL_ID).addSelectionListener(this.caller);
	}
	
	@Override
	protected void okPressed() {
		setReturnCode(OK);
		getButton(IDialogConstants.OK_ID).setData("dialog",ProcessFileDialog.this);
		//no close let Action Handler handle it
	}
	 @Override
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		getButton(IDialogConstants.CANCEL_ID).setData("dialog",ProcessFileDialog.this);
		//no close let Action Handler handle it
	}
	 

	public String getFilename() {
		return fileName;
	}

	public String getMimeType() {
		return mimeTypeCombo.getText();
	}	
	
	public String getEncoding() {
		return encodingCombo.getText();
	}	

	public LinkedHashMap<String, String> getVariablesMap() {
		return variablesMap;
	}
	
	/**************************************************************************************************
	 * A table viewer line
	 ***************************************************************************************************/
	class VariableLine {
		private String output;
		private Integer decision;
		public VariableLine(String language, Integer decision) {
			super();
			this.output = language;
			this.decision = decision;
		}
		public Integer getDecision() {
			return decision;
		}
		public void setDecision(Integer decision) {
			this.decision = decision;
		}
		public String getOutput() {
			return output;
		}
		public void setOutput(String language) {
			this.output = language;
		}
		
		
	}


	
	
}
