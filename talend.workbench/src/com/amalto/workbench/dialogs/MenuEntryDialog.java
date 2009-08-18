package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSMenuEntry;
import com.amalto.workbench.webservices.WSMenuMenuEntriesDescriptions;

public class MenuEntryDialog extends Dialog {
	
	private Text idText=null;
	private Text contextText=null;
	private Text applicationNameText=null;
	
	protected Composite descriptionsComposite;
	protected Text labelText;
	protected Combo languagesCombo;
	protected TableViewer descriptionsViewer;
	
	private WSMenuEntry wsMenuEntry = null;
	protected LinkedHashMap<String, String> descriptionsMap = new LinkedHashMap<String, String>();
	private SelectionListener caller = null;
	private String title = "";
	private boolean isChanged = true;
	
	

	/**
	 * @param parentShell
	 */
	public MenuEntryDialog(WSMenuEntry wsMenuEntry, SelectionListener caller, Shell parentShell, String title) {
		super(parentShell);
		this.wsMenuEntry = wsMenuEntry;
		this.caller = caller;
		this.title = title;
		//feed the descritions hashmap used by the labels Table
		WSMenuMenuEntriesDescriptions[] descriptions = wsMenuEntry.getDescriptions();
		if (descriptions!=null) {
			for (int i = 0; i < descriptions.length; i++) {
				descriptionsMap.put(descriptions[i].getLanguage(), descriptions[i].getLabel());
			}
		}
	}
	public MenuEntryDialog(WSMenuEntry wsMenuEntry, SelectionListener caller, Shell parentShell, String title,boolean isChanged) {
		super(parentShell);
		this.wsMenuEntry = wsMenuEntry;
		this.caller = caller;
		this.title = title;
		this.isChanged = isChanged;
		//feed the descritions hashmap used by the labels Table
		WSMenuMenuEntriesDescriptions[] descriptions = wsMenuEntry.getDescriptions();
		if (descriptions!=null) {
			for (int i = 0; i < descriptions.length; i++) {
				descriptionsMap.put(descriptions[i].getLanguage(), descriptions[i].getLabel());
			}
		}
	}

	protected Control createDialogArea(Composite parent) {
				
		//Should not really be here but well,....
		parent.getShell().setText(this.title);
		
		Composite composite = (Composite) super.createDialogArea(parent);
		
		GridLayout layout = (GridLayout)composite.getLayout();
		layout.numColumns = 2;
		//layout.verticalSpacing = 10;
		
		Label idLabel = new Label(composite, SWT.NONE);
		idLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		
		idLabel.setText("ID");
        if(this.isChanged){
        	idText = new Text(composite, SWT.NONE);
        }
        else{
        	idText = new Text(composite, SWT.NONE|SWT.READ_ONLY);
        }
		
		idText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		((GridData)idText.getLayoutData()).widthHint = 300;
		idText.setDoubleClickEnabled(false);
		

		Label contextLabel = new Label(composite, SWT.NONE);
		contextLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		contextLabel.setText("Context");

		contextText = new Text(composite, SWT.NONE);
		contextText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		contextText.setDoubleClickEnabled(false);

		Label applicationNameLabel = new Label(composite, SWT.NONE);
		applicationNameLabel.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		applicationNameLabel.setText("Application");

		applicationNameText = new Text(composite, SWT.NONE);
		applicationNameText.setLayoutData(
				new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
		);
		
		//Labels
        descriptionsComposite = new Composite(composite, SWT.BORDER);
        descriptionsComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
        );
        descriptionsComposite.setLayout(new GridLayout(3,false));

        Label descriptionsLabel = new Label(descriptionsComposite, SWT.NULL);
        descriptionsLabel.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
        );
        descriptionsLabel.setText("Menu Entry Labels");
        
        languagesCombo = new Combo(descriptionsComposite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
        languagesCombo.setLayoutData(
                new GridData(SWT.BEGINNING,SWT.NONE,false,false,1,1)
        );
        Set<String> languages = Util.lang2iso.keySet();
        for (Iterator iter = languages.iterator(); iter.hasNext(); ) {
			String language = (String) iter.next();
			languagesCombo.add(language);
		}
        languagesCombo.addModifyListener(
    		new ModifyListener() {
    			public void modifyText(ModifyEvent e) {}
    		}
        );
        languagesCombo.select(0);
        
        labelText = new Text(descriptionsComposite, SWT.BORDER|SWT.SINGLE);
        labelText.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        labelText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if ((e.stateMask==0) && (e.character == SWT.CR)){
					String isoCode = Util.lang2iso.get(languagesCombo.getText());
	        		descriptionsMap.put(isoCode, labelText.getText());
	        		descriptionsViewer.refresh();
				}
			}
        });

        
        Button addLabelButton = new Button(descriptionsComposite,SWT.PUSH );
        addLabelButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
        addLabelButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        addLabelButton.setToolTipText("Add");
        addLabelButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		String isoCode = Util.lang2iso.get(languagesCombo.getText());
        		descriptionsMap.put(isoCode, labelText.getText());
        		descriptionsViewer.refresh();
        	};
        });
		
        final String LANGUAGE="Language";
        final String LABEL = "Label";
        
        descriptionsViewer = new TableViewer(descriptionsComposite,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER|SWT.FULL_SELECTION);
        descriptionsViewer.getControl().setLayoutData(    
                new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
        );
        ((GridData)descriptionsViewer.getControl().getLayoutData()).heightHint=100;
        // Set up the underlying table
        Table table = descriptionsViewer.getTable();
        //table.setLayoutData(new GridData(GridData.FILL_BOTH));

        new TableColumn(table, SWT.LEFT).setText(LANGUAGE);
        new TableColumn(table, SWT.CENTER).setText(LABEL);
        table.getColumn(0).setWidth(150);
        table.getColumn(1).setWidth(150);
        for (int i = 2, n = table.getColumnCount(); i < n; i++) {
          table.getColumn(i).pack();
        }
        
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
       

        // Create the cell editors --> We actually discard those later: not natural for an user
        CellEditor[] editors = new CellEditor[2];
        editors[0] = new TextCellEditor(table);
        editors[1] = new TextCellEditor(table);
        descriptionsViewer.setCellEditors(editors);
        
        //set the content provider
        descriptionsViewer.setContentProvider(new IStructuredContentProvider() {
        	public void dispose() {}
        	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
        	public Object[] getElements(Object inputElement) {
//        		System.out.println("getElements() ");
        		LinkedHashMap<String, String> descs = (LinkedHashMap<String, String>)inputElement; 
        		Set<String> languages = descs.keySet();
        		ArrayList<DescriptionLine> lines = new ArrayList<DescriptionLine>();
        		for (Iterator iter = languages.iterator(); iter.hasNext(); ) {
					String language = (String) iter.next();
					DescriptionLine line = new DescriptionLine(
							Util.iso2lang.get(language),
							descs.get(language)
					);
					lines.add(line);
				}
        		//we return an instance line made of a Sring and a boolean
        		return lines.toArray(new DescriptionLine[lines.size()]);
        	}
        });
        
        //set the label provider
        descriptionsViewer.setLabelProvider(new ITableLabelProvider() {
        	public boolean isLabelProperty(Object element, String property) {return false;}
        	public void dispose() {}
        	public void addListener(ILabelProviderListener listener) {}
        	public void removeListener(ILabelProviderListener listener) {}
        	public String getColumnText(Object element, int columnIndex) {
//        		System.out.println("getColumnText() "+columnIndex);
        		DescriptionLine line = (DescriptionLine) element;
        		switch (columnIndex) {
        		    case 0:
        		      return line.getLanguage();
        		    case 1:
        		      return line.getLabel();
    		    }
        		return "";
        	}
        	public Image getColumnImage(Object element, int columnIndex) {return null;}
        });

        // Set the column properties
        descriptionsViewer.setColumnProperties(new String[]{LANGUAGE,LABEL});
        
        //set the Cell Modifier
        descriptionsViewer.setCellModifier(new ICellModifier() {
        	public boolean canModify(Object element, String property) {
        		//if (INSTANCE_ACCESS.equals(property)) return true; Deactivated
        		return false;
        	}
        	public void modify(Object element, String property, Object value) {
//        		System.out.println("modify() "+element.getClass().getName()+": "+property+": "+value);
        		//DescriptionLine line = (DescriptionLine)((IStructuredSelection)instancesViewer.getSelection()).getFirstElement();
        		//deactivated: editing in places is not natural for users 
        	}
        	public Object getValue(Object element, String property) {
//        		System.out.println("getValue() "+property);
        		DescriptionLine line = (DescriptionLine) element;
        		if (LANGUAGE.equals(property)) {
        			return line.getLanguage();
        		}
        		if (LABEL.equals(property)) {
        			return line.getLabel();
        		}
        		return null;
        	}
        });
        
        //Listen for changes in the selection of the viewer to display additional parameters
        descriptionsViewer.addSelectionChangedListener(new ISelectionChangedListener() {
        	public void selectionChanged(SelectionChangedEvent event) {}
        });
        
        //display for Delete Key events to delete an instance
        descriptionsViewer.getTable().addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent e) {}
        	public void keyReleased(KeyEvent e) {
//        		System.out.println("Table keyReleased() ");
        		if ((e.stateMask==0) && (e.character == SWT.DEL) && (descriptionsViewer.getSelection()!=null)) {
        			DescriptionLine line = (DescriptionLine)((IStructuredSelection)descriptionsViewer.getSelection()).getFirstElement();
        			String isoCode = Util.lang2iso.get(line.getLanguage());
        			LinkedHashMap<String, String> descs = (LinkedHashMap<String, String>)descriptionsViewer.getInput(); 
        			descs.remove(isoCode);
        			descriptionsViewer.refresh();
        		}
        	}
        });
        
        descriptionsViewer.refresh();
				
		if (wsMenuEntry != null) {
			idText.setText(wsMenuEntry.getId() == null ? "" : wsMenuEntry.getId());
			contextText.setText(wsMenuEntry.getContext() == null ? "" : wsMenuEntry.getContext());
			applicationNameText.setText(wsMenuEntry.getApplication() == null ? "" : wsMenuEntry.getApplication());
			descriptionsViewer.setInput(descriptionsMap);
		}
		
		
		idText.setFocus();
		
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
		getButton(IDialogConstants.OK_ID).setData("dialog",MenuEntryDialog.this);
		//no close let Action Handler handle it
	}
	 @Override
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		getButton(IDialogConstants.CANCEL_ID).setData("dialog",MenuEntryDialog.this);
		//no close let Action Handler handle it
	}
	 

	/**************************************************************************************************
	 * Public getters read by caller
	 ***************************************************************************************************/

	
	public Text getApplicationNameText() {
		return applicationNameText;
	}

	public Text getContextText() {
		return contextText;
	}

	public Text getIdText() {
		return idText;
	}
	
	public TableViewer getDescriptionsTableViewer() {
		return descriptionsViewer;
	}
	
	
	/**************************************************************************************************
	 * A table viewer line
	 ***************************************************************************************************/
	class DescriptionLine {
		private String language;
		private String label;
		public DescriptionLine(String language, String label) {
			super();
			this.language = language;
			this.label = label;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getLanguage() {
			return language;
		}
		public void setLanguage(String language) {
			this.language = language;
		}
		
		
	}
	
	
	
}
