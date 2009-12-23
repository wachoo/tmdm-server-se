package org.talend.mdm.workbench.enterprice.dialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.commmon.util.core.ICoreConstants;

import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.ICellEditor;
import com.amalto.workbench.widgets.TisTableViewer;
import com.amalto.workbench.widgets.WidgetFactory;

public class WorkflowAccessDialog extends Dialog{
	String title;
	private TisTableViewer viewer;
	AMainPageV2 page;
	Collection<String> pattern;
	private ComplexTableViewerColumn[] columns;
	String conceptName;
	Collection<String> access;
	public WorkflowAccessDialog(Shell parentShell,String title,Collection<String> pattern,AMainPageV2 page,String conceptName) {		
		super(parentShell);
		this.pattern=pattern;
		this.page=page;
		this.title=title;
		this.conceptName=conceptName;
	}
	
	protected Control createDialogArea(Composite parent) {		
		parent.getShell().setText(this.title);		
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(new GridLayout(2,false));	
		
		List<String> roles=Util.getChildren(page.getXObject().getServerRoot(), TreeObject.ROLE);
		List<String> workflows=Util.getChildren(page.getXObject().getServerRoot(), TreeObject.WORKFLOW_PROCESS);
		
	    columns= new ComplexTableViewerColumn[]{
	    		new ComplexTableViewerColumn("Roles", false, "", "", "",ComplexTableViewerColumn.COMBO_STYLE,roles.toArray(new String[roles.size()]),0),
	    		new ComplexTableViewerColumn("Workflow", false, "", "", "",ComplexTableViewerColumn.COMBO_STYLE,workflows.toArray(new String[workflows.size()]),0),
	    		new ComplexTableViewerColumn("Access Rights", false, ICoreConstants.WORKFLOW_ACCESSES[0], ICoreConstants.WORKFLOW_ACCESSES[0], "",ComplexTableViewerColumn.COMBO_STYLE,ICoreConstants.WORKFLOW_ACCESSES,0)	    		
	    };
	    columns[0].setColumnWidth(100);
	    columns[1].setColumnWidth(300);
	    columns[2].setColumnWidth(200);	   
	    viewer=new TisTableViewer(Arrays.asList(columns),new WidgetFactory(),composite);
	    viewer.setMainPage(page);
	    viewer.setConceptName(conceptName);
	    viewer.setContext(true);
	    viewer.create();
	    viewer.getViewer().setInput(parseRules());
	    viewer.setHeight(110);
	    viewer.setWidth(600);
	    viewer.getMainComposite().setLayoutData( new GridData(SWT.FILL,SWT.FILL,true,true,2,3));
		return composite;
	}
	
	protected boolean isResizable() {
		return true;
	}
	
	private List<Line> parseRules() {
		List<Line> lines=new ArrayList<Line>();
	
		for(String access: pattern){
			String[] item=access.split("#");
			if(item.length==3){
				Line l=new Line(columns, new String[]{item[0],item[1],item[2]});
				lines.add(l);
			}
		}
		return lines;
	}

	protected void okPressed() {
		XpathSelectDialog.setContext(null);
		deactiveAllCellEditors();
		getAccesses();
		super.okPressed();
	}
	private void deactiveAllCellEditors(){
		CellEditor[] editors=viewer.getViewer().getCellEditors();
		for(CellEditor editor: editors){
			if( editor instanceof ICellEditor){
				((ICellEditor)editor).deactive();
			}
		}
	}
	private Collection<String> getAccesses(){
		TableItem[] items=viewer.getViewer().getTable().getItems();
		//List<String> rules=new ArrayList<String>();
		List<String> list=new ArrayList<String>();
		if(items.length > 0){			
			for(TableItem item:items){				
				Line line=(Line)item.getData();
				String role=line.keyValues.get(0).value;			
				String workflow=line.keyValues.get(1).value;
				String access=line.keyValues.get(2).value;		
				list.add(role+"#"+workflow+"#"+access);
			}
		}
		return access=list;	
	}
	
	public Collection<String> getAccess(){
		return access;
	}
}
