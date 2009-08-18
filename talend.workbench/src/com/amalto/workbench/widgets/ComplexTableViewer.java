package com.amalto.workbench.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
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
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;

/**
 * This table viewer has:
 * 1.input Texts, add button
 * 2.normal tableviewer with up/down/delete button
 * 
 * @author aiming
 *
 */
public class ComplexTableViewer {
	protected List<ComplexTableViewerColumn> columns;
	protected Composite parent;
	protected FormToolkit toolkit;
	
	protected Composite mainComposite;
	protected Button addButton;
	protected TableViewer viewer;
	protected Button downButton;
	protected Button upButton;
	protected Button deleteButton;
	
	protected ComplexTableViewerColumn[] keyColumns;
	protected boolean editable = true;	
	protected static String ERROR_ITEMALREADYEXISTS_CONTENT = "This line already Exists!";
	protected static String ERROR_ITEMALREADYEXISTS_TITLE   = "Warning";
	//mainPage can be null
	protected AMainPageV2 mainPage;
	protected Table table;
	
	public AMainPageV2 getMainPage() {
		return mainPage;
	}
	public void setMainPage(AMainPageV2 mainPage) {
		this.mainPage = mainPage;
	}
    
	public void setKeyColumns(ComplexTableViewerColumn[] keyIndex) {
		keyColumns = keyIndex;
	}
	
	public void setEditable(boolean editable)
	{
		this.editable = editable;
	}
	public Button getAddButton() {
		return addButton;
	}
	public TableViewer getViewer() {
		return viewer;
	}
	public Button getDownButton() {
		return downButton;
	}
	public Button getUpButton() {
		return upButton;
	}
	public Button getDeleteButton() {
		return deleteButton;
	}

	public List<ComplexTableViewerColumn> getColumns() {
    	return columns;
    }
	
	public ComplexTableViewerColumn getColumn(ComplexTableViewerColumn compareComplexTableViewerColumn) {
		for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
			ComplexTableViewerColumn complexTableViewerColumn = (ComplexTableViewerColumn) iterator.next();
			if(complexTableViewerColumn.equals(compareComplexTableViewerColumn)){
				return complexTableViewerColumn;
			}
		}
    	return null;
    }
	
	
	public ComplexTableViewer(List<ComplexTableViewerColumn> columns, FormToolkit toolkit,Composite parent){
		this.columns=columns;
		this.parent=parent;
		this.toolkit=toolkit;
	}
	protected String[] getTextValues(){
		List<String> values=new ArrayList<String>();
		for(ComplexTableViewerColumn column:columns){
			String text = "";
			if (column.isCombo()) {
				text = ((CCombo)column.getControl()).getText();
			} else if(column.isText()) {
				text = ((Text)column.getControl()).getText();
			}else if(column.isXPATH()){
				Control text1=((Composite)column.getControl()).getChildren()[0];
				if(text1 instanceof Text){
					text= ((Text)text1).getText();
				}
			}
			values.add(text);
		}
		return values.toArray(new String[values.size()]);
	}
	protected String[] getInitValues(){
		List<String> values=new ArrayList<String>();
		for(ComplexTableViewerColumn column:columns){
			String text = column.getNillValue();
			if(column.isCombo() && column.getComboValues().length>0){
				text= column.getComboValues()[0];
			}
			values.add(text);
		}
		return values.toArray(new String[values.size()]);
	}
	protected void markDirty(){
		if(mainPage!=null){
			mainPage.markDirty();
		}
	}
	protected void createLabels(){
		int i=0;
		for(ComplexTableViewerColumn column:columns){
	        Label label = toolkit.createLabel(mainComposite, column.getName(), SWT.NULL);
	        if(i==columns.size()-1){
		        label.setLayoutData(
		                new GridData(SWT.FILL,SWT.FILL,false,false,2,1)
		        );			
	        }else{
		        label.setLayoutData(
		                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
		        );
	        }
        	if(!column.isNillable()){
        		label.setText(label.getText()+"(*)");		
        		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        	}	        	
        	if(column.isUnique()){
        		label.setText(label.getText()+"(U)");			 
        		label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLUE));
        	}
	        i++;
		}
	}
	
	protected void createTexts(){
		
		for (Iterator<ComplexTableViewerColumn> iterator = columns.iterator(); iterator.hasNext(); ) {
			ComplexTableViewerColumn column = iterator.next();
			if (column.isCombo()) {
				CCombo combo = new CCombo(mainComposite,SWT.BORDER|SWT.READ_ONLY);
				GridData gd = new GridData(SWT.NONE,SWT.TOP,false,false,1,1);
				if (column.getColumnWidth() > 0)
					gd.widthHint = column.getColumnWidth();
				combo.setLayoutData(gd);
				combo.setItems(column.getComboValues());
				if(column.getDefaultValue() ==null ||column.getDefaultValue().length()==0){
					combo.select(0);
				}else{
					combo.setText(column.getDefaultValue());
				}
				if(column.isComboEditable()){
					combo.setEditable(true);
					combo.setText("");
				}
				column.setControl(combo);
			} else {
				int style=SWT.BORDER;
				if (column.getTextLines()>1) {
					style = SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL;
				}
				final Text text = toolkit.createText(mainComposite, column.getDefaultValue(), style);
				GridData gdata = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
				if (column.getTextLines()>1)
					gdata.heightHint = text.getLineHeight()*column.getTextLines();
				text.setLayoutData(gdata);
				column.setControl(text);
		        text.addFocusListener(new FocusAdapter(){
		        	@Override
		        	public void focusGained(FocusEvent e) {
		        		text.selectAll();
		        	}
		        });
			}
		}


        addButton = toolkit.createButton(mainComposite,"",SWT.PUSH | SWT.CENTER);
        addButton.setLayoutData(
                new GridData(SWT.FILL,SWT.BOTTOM,false,false,1,1)
        );
        addButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        addButton.setToolTipText("Add");
        addButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	@SuppressWarnings("unchecked")
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {

        		String uniqueVal="";
        		String keyVal = "";
           		//Make sure texts are not nill (empty) where not authorized
        		for (Iterator<ComplexTableViewerColumn> iterator = columns.iterator(); iterator.hasNext(); ) {
        			ComplexTableViewerColumn column = iterator.next();
        			String text = "";
        			if (column.isCombo()) {
        				text = ((CCombo)column.getControl()).getText();
        			} else if(column.isText()) {
        				text = ((Text)column.getControl()).getText();
        			}else if(column.isXPATH()){
        				Control text1=((Composite)column.getControl()).getChildren()[0];
        				if(text1 instanceof Text){
        					text= ((Text)text1).getText();
        				}
        			}
        			if(text.length()==0) {
        				if (column.isNillable()) {
        					text = column.getNillValue();
        					if (column.isCombo()) {
                				((CCombo)column.getControl()).setText(text);
                			} else {
                				((Text)column.getControl()).setText(text);
                			}
        				} else {
        					MessageDialog.openError(
        						ComplexTableViewer.this.getViewer().getControl().getShell(), 
        						"Input Error",
        						"The Column '"+column.getName()+"' cannot be empty"       						
        					);
        					return ;
        				}
        			}
        			if (keyColumns != null && Arrays.asList(keyColumns).indexOf(column) >= 0)
        			{
        				keyVal +=text;
        			}
        			uniqueVal+="."+text;
        		}
        		
        		//check uniqueness by concatenating all the values
        		List<Line> list=(List<Line>)getViewer().getInput();
        		for(Line line: list){
        			String thisLineVal = "";
        			for(KeyValue keyvalue:line.keyValues){
        				thisLineVal+="."+keyvalue.value;
        			}
        			if(thisLineVal.equals(uniqueVal) || (keyVal.length() > 0 && thisLineVal.indexOf(keyVal) >= 0)){
        				MessageDialog.openInformation(null, ERROR_ITEMALREADYEXISTS_TITLE, ERROR_ITEMALREADYEXISTS_CONTENT);
        				return ;
        			}
        		}

        		//Update the model
        		Line line =new Line(columns.toArray(new ComplexTableViewerColumn[columns.size()]),getTextValues());
        		list.add(line);
        		//update the instances viewer
        		viewer.refresh();
        		markDirty();
         	};
        });		
	}
	
	
	protected void add() {
   		
	}
	protected void createTable(){
	        table =new Table(mainComposite,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER|SWT.FULL_SELECTION);
	        viewer = new TableViewer(table);
	        
	        //table.setLayoutData(new GridData(GridData.FILL_BOTH));
	        table.setToolTipText("Column with * is required, column with U is unique!");
	        for(ComplexTableViewerColumn column:columns){
	        	TableColumn tableColumn=new TableColumn(table, SWT.CENTER);
	        	tableColumn.setText(column.getName());
	        	if(!column.isNillable()){
	        		tableColumn.setImage(ImageCache.getCreatedImage(EImage.WILDCARD.getPath()));		        		
	        	}	        	
	        	if(column.isUnique()){
	        		tableColumn.setImage(ImageCache.getCreatedImage(EImage.UNIQUE.getPath()));		        		
	        	}
	        	if (column.getColumnWidth() > 0) {
					tableColumn.setWidth(column.getColumnWidth());
				} else {
					tableColumn.setWidth(200);
					tableColumn.pack();
				}
	        }  
	        
	        table.setHeaderVisible(true);
	        table.setLinesVisible(true);
	        table.addListener(SWT.MeasureItem, new Listener() {
	            public void handleEvent(Event event) {
	               event.height = event.gc.getFontMetrics().getHeight() + 10;
	            }
	         });

	}
	protected void createViewer(){
		createTable();
		GridData gd=new GridData(SWT.FILL,SWT.FILL,true,true,columns.size(),1);
        table.setLayoutData( gd );
        gd.heightHint=80;
       // Up Down Delete button group
        Composite stepUpDownComposite = toolkit.createComposite(mainComposite,SWT.NONE);
        stepUpDownComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
        stepUpDownComposite.setLayout(new GridLayout(1,false));
        
        upButton = toolkit.createButton(stepUpDownComposite,"",SWT.PUSH | SWT.CENTER);
        upButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
        );
        upButton.setImage(ImageCache.getCreatedImage(EImage.PREV_NAV.getPath()));
        upButton.setToolTipText("Move down the selected item");
        upButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	@SuppressWarnings("unchecked")
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		int index =viewer.getTable().getSelectionIndex();
        		if(index >0 && index < viewer.getTable().getItemCount() ){
        			//commit as we go
        			if(mainPage!=null){
        				mainPage.setComitting(true);
        			}
        			List<Line> items=(List<Line>)viewer.getInput();
        			Line line= items.get(index);
        			items.remove(index);
        			items.add(index-1,line);
        			
        			viewer.refresh();
        			
        			if(mainPage!=null){
        				mainPage.setComitting(false);
        			}
	            	markDirty();       		
        		}
        	};
        });
        downButton = toolkit.createButton(stepUpDownComposite,"",SWT.PUSH | SWT.CENTER);
        downButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
        );
        downButton.setImage(ImageCache.getCreatedImage(EImage.NEXT_NAV.getPath()));
        downButton.setToolTipText("Move down the selected item");
        downButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	@SuppressWarnings("unchecked")
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		int index =viewer.getTable().getSelectionIndex();
        		if(index >=0 && index < viewer.getTable().getItemCount()-1 ){
        			//commit as we go
        			if(mainPage!=null){
        				mainPage.setComitting(true);
        			}
        			List<Line> items=(List<Line>)viewer.getInput();
        			Line line= items.get(index);
        			items.remove(index);
        			items.add(index+1,line);
        			//viewer.setInput(items);
        			viewer.refresh();
        			//TODO
        			if(mainPage!=null){
        				mainPage.setComitting(false);
        			}
	            	markDirty();       		
        		}
        	};
        });
        deleteButton = toolkit.createButton(stepUpDownComposite,"",SWT.PUSH | SWT.CENTER);
        deleteButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,false,1,1)
        );
        deleteButton.setToolTipText("Delete the selected item");
        deleteButton.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
        deleteButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	@SuppressWarnings("unchecked")
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		int index =viewer.getTable().getSelectionIndex();
        		if(index >=0 && index < viewer.getTable().getItemCount() ){
        			List<Line> items=(List<Line>)viewer.getInput();
        			items.remove(index);
        			viewer.refresh();
        			markDirty();
        		}
        	};
        });
       

        // Create the cell editors --> We actually discard those later: not natural for an user
        CellEditor[] editors = new CellEditor[columns.size()];	        
        for(int i=0; i< columns.size(); i++){
        	if(columns.get(i).isText()){        		
        		editors[i] = new TextCellEditor(table);        		
        	}
        	else if(columns.get(i).isCombo())
        	{
        		editors[i] = new ComboBoxCellEditor(table, ((ComplexTableViewerColumn)columns.get(i)).getComboValues(), SWT.READ_ONLY);	
        	}else if(columns.get(i).isXPATH()){
        		editors[i]= new XpathCellEditor(table);
        	}	        
        }
        viewer.setCellEditors(editors);
        
        //set the content provider
        viewer.setContentProvider(new IStructuredContentProvider() {
        	public void dispose() {}
        	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
        	@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
        		ArrayList<Line> lines = (ArrayList<Line>)inputElement;
        		return lines.toArray(new Line[lines.size()]);
        	}
        });
        
        //set the label provider
        viewer.setLabelProvider(new ITableLabelProvider() {
        	public boolean isLabelProperty(Object element, String property) {return false;}
        	public void dispose() {}
        	public void addListener(ILabelProviderListener listener) {}
        	public void removeListener(ILabelProviderListener listener) {}
        	public String getColumnText(Object element, int columnIndex) {
        		Line line = (Line) element;
        		if(columnIndex>=0 && columnIndex<columns.size()){
        			for(KeyValue keyvalue:line.keyValues){
        				if(keyvalue.key.equals(columns.get(columnIndex).getName())){
        					String val = keyvalue.value; 
        					if (columns.get(columnIndex).isNillable()) {
        						if (columns.get(columnIndex).getNillValue().equals(val))
        							val = columns.get(columnIndex).getNillDisplay();
        					}
        					return val;
        				}
        			}
        		}
        		return "";
        	}
        	public Image getColumnImage(Object element, int columnIndex) {return null;}
        });

        // Set the column properties
        ArrayList<String> columnNames = new ArrayList<String>();
        for (ComplexTableViewerColumn column : columns) {
			columnNames.add(column.getName());
		}
        viewer.setColumnProperties(columnNames.toArray(new String[columnNames.size()]));
        
        //set the Cell Modifier
        viewer.setCellModifier(new ICellModifier() {
        	public boolean canModify(Object element, String property) {
        		//if (INSTANCE_ACCESS.equals(property)) return true; Deactivated
        		return editable;
        	}

			@SuppressWarnings("unchecked")
			public void modify(Object element, String property, Object value)
        	{
				if(value instanceof Integer){
					if(Integer.valueOf(value.toString())==-1) return;
				}
				// modify the text  and combo cell value 
				TableItem item = (TableItem) element;
				Line line = (Line) item.getData();
				int columnIndex = Arrays.asList(viewer.getColumnProperties())
						.indexOf(property);
				if (!isForceTextCellEditor(columnIndex)&&isAColumnWithCombo(columnIndex)) {
					String[] attrs = columns.get(columnIndex).getComboValues();
					value = attrs[Integer.parseInt(value.toString())];
				}
				KeyValue kv = line.keyValues.get(columnIndex);
				boolean noChange = kv.value.equals(value.toString());
				kv = new KeyValue(property, value.toString());
				Line copyLine = line.clone();
				copyLine.keyValues.set(columnIndex, kv);
				List<Line> items = (List<Line>) viewer.getInput();
				Collections.sort(items);
				int pos = Collections.binarySearch(items, copyLine);
				if (pos >= 0 && !copyLine.equals(line)) {
					MessageDialog.openInformation(null,
							ERROR_ITEMALREADYEXISTS_TITLE,
							ERROR_ITEMALREADYEXISTS_CONTENT);
					return;
				}
				line.keyValues.set(columnIndex, kv);
				viewer.update(line, null);
				if (!noChange)
				{
					markDirty();
				}
        	} 
        	public Object getValue(Object element, String property) {
        		int columnIndex = Arrays.asList(viewer.getColumnProperties())
						.indexOf(property);
				Line line = (Line) element;
				// add getting value from combo
				if (isAColumnWithCombo(columnIndex)) {
					String value = line.keyValues.get(columnIndex).value;
					String[] attrs = columns.get(columnIndex).getComboValues();
					return Arrays.asList(attrs).indexOf(value);
				}
				for (KeyValue keyvalue : line.keyValues) {
					if (property.equals(keyvalue.key)) {
						if (keyvalue.value.equals("")) {
							keyvalue.value = columns.get(columnIndex)
									.getNillDisplay();
						}
						return keyvalue.value;
					}
				}
        		return null;
        	}
        	
        	private boolean isAColumnWithCombo(int columnIdx)
        	{
        		return columns.get(columnIdx).isCombo();
        	}
        	
        	private boolean isForceTextCellEditor(int columnIdx)
        	{
        		return columns.get(columnIdx).isText();
        	}
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener(){

			public void selectionChanged(SelectionChangedEvent event) {
				// TODO Auto-generated method stub
    			Line line = (Line)((IStructuredSelection)event.getSelection()).getFirstElement();
    			
    			for(int columnIndex=0; columnIndex<columns.size(); columnIndex++){
    				if(line==null){
    					Control control=columns.get(columnIndex).getControl();
    					if(control instanceof Text){
    						((Text)control).setText("");
    					}
    					if(control instanceof CCombo){
    						((CCombo)control).select(0);
    					}
    					if(control instanceof Combo){
    						((CCombo)control).select(0);
    					}  					
    				}else{
		       			for(KeyValue keyvalue:line.keyValues){
		    				if(keyvalue.key.equals(columns.get(columnIndex).getName())){
		    					String val = keyvalue.value; 
		    					Control control=columns.get(columnIndex).getControl();
		    					if(control instanceof Text){
		    						((Text)control).setText(val);
		    					}
		    					if(control instanceof CCombo){
		    						((CCombo)control).setText(val);
		    					}
		    					if(control instanceof Combo){
		    						((CCombo)control).setText(val);
		    					}
		    				}
		    			}
    				}
    			}
			}
        	
        });
        
        
        //display for Delete Key events to delete an instance
        viewer.getTable().addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent e) {}
        	@SuppressWarnings("unchecked")
			public void keyReleased(KeyEvent e) {
        		if ((e.stateMask==0) && (e.character == SWT.DEL) && (viewer.getSelection()!=null)) {
        			Line line = (Line)((IStructuredSelection)viewer.getSelection()).getFirstElement();
        			//update the underlying role and refresh the table
        			//update the underlying model
        			List<Line> items=(List<Line>)viewer.getInput();
        			items.remove(line);
        			//refresh
        			viewer.refresh();
        			//mark for update
        			markDirty();
        		}
        	}
        });		
	}
	public void create(){
		mainComposite= toolkit.createComposite(parent);
		mainComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1)
        );		
		mainComposite.setLayout(new GridLayout(columns.size()+1,false));
        
        createLabels();
        createTexts();
        createViewer();        
	}
	
	
	public Composite getMainComposite() {
		return mainComposite;
	}


	class XpathCellEditor extends CellEditor{

		XpathWidget xpath;
		public XpathCellEditor(Composite parent){
			super(parent);
		}
		@Override
		protected Control createControl(Composite parent) {			
			xpath= new XpathWidget(parent, mainPage,false);
			((GridData)xpath.getComposite().getChildren()[0].getLayoutData()).heightHint=15;
			((GridData)xpath.getComposite().getChildren()[1].getLayoutData()).heightHint=15;
			return xpath.getComposite();
		}

		@Override
		protected Object doGetValue() {
			// TODO Auto-generated method stub
			return xpath.getText();
		}

		@Override
		protected void doSetFocus() {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void doSetValue(Object value) {
			// TODO Auto-generated method stub
			xpath.setText(value.toString());
		}
		
	}	
}

