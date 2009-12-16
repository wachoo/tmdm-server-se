package org.talend.mdm.workbench.enterprice.editors;

import java.net.URL;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.talend.mdm.workbench.enterprice.widgets.ProcessList;
import org.talend.mdm.workbench.enterprice.widgets.ProcessWidget;

import com.amalto.workbench.editors.AMainPage;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSProcessInstance;
import com.amalto.workbench.webservices.WSProcessInstanceArray;
import com.amalto.workbench.webservices.WSProcessTaskInstance;
import com.amalto.workbench.webservices.WSWorkflowGetProcessInstances;
import com.amalto.workbench.webservices.WSWorkflowProcessDefinitionUUID;
import com.amalto.workbench.webservices.XtentisPort;

public class WorkflowBrowserMainPage extends AMainPage implements IXObjectModelListener {
	private Table table;
	private TableViewer viewer;
	private FormToolkit toolkit;
	private Group processCom;

	private ScrolledComposite ccrollComposite;
	private ProcessList plist;
	private TreeObject xobject;
	private XtentisPort port;
	public WorkflowBrowserMainPage(FormEditor editor) {
        super(
        		editor,
        		WorkflowBrowserMainPage.class.getName(),
        		((XObjectBrowserInput)editor.getEditorInput()).getName()+""
        );        
        //listen to events
        ((XObjectBrowserInput)editor.getEditorInput()).addListener(this);
    }

	@Override
	protected void createFormContent(IManagedForm managedForm) {

        try {
        	//sets the title
        	managedForm.getForm().setText(this.getTitle());
        	
        	//get the toolkit
        	toolkit = managedForm.getToolkit();
        	
        	//get the body
        	Composite composite = managedForm.getForm().getBody();
        	composite.setLayout(new GridLayout(1,false));
        	

        	Composite separator = toolkit.createCompositeSeparator(composite);
        	separator.setLayoutData(    
        			new GridData(SWT.FILL,SWT.CENTER,true,false,1,1)
        	);
        	((GridData)separator.getLayoutData()).heightHint = 2;     
        	
        	//process instances list
		    ccrollComposite = new ScrolledComposite(composite,  SWT.H_SCROLL | SWT.V_SCROLL);		    		       	
		    ccrollComposite.setLayoutData(new GridData(SWT.FILL,SWT.CENTER,true,false,1,1));        	
        	processCom=new Group(ccrollComposite,0);//toolkit.createComposite(ccrollComposite);
        	processCom.setText("Process Instance List");
        	processCom.setBackground(processCom.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
        	processCom.setLayout(new GridLayout(1,false));
        	GridData gd=new GridData(SWT.FILL,SWT.CENTER,true,true,1,1);
        	gd.heightHint=150;
        	processCom.setLayoutData(gd);
        	ccrollComposite.setContent(processCom);
        	ccrollComposite.setExpandHorizontal(true);
        	ccrollComposite.setExpandVertical(true);
        	ccrollComposite.setMinSize(400,4*31);
        	plist=new ProcessList(toolkit, processCom, ccrollComposite,viewer);
        	xobject=getXObject();
			port = Util.getPort(
					new URL(xobject.getEndpointAddress()),
					xobject.getUniverse(),
					xobject.getUsername(),
					xobject.getPassword()
			);	  
			WSWorkflowProcessDefinitionUUID uuid= (WSWorkflowProcessDefinitionUUID)xobject.getWsKey();

			WSProcessInstanceArray array=port.workflowGetProcessInstances(new WSWorkflowGetProcessInstances(uuid));
//			//test code
//			List<WSProcessInstance> list=new ArrayList<WSProcessInstance>();
//			for(int i=0; i<2; i++){
//				WSProcessInstance wsprocess=new WSProcessInstance("name"+i,"RUNNING");
//				list.add(wsprocess);
//			}
//			array.setInstance(list.toArray(new WSProcessInstance[list.size()]));
//			//end
			if(array!=null && array.getInstance()!=null){
				for(final WSProcessInstance instance:array.getInstance()){
					final ProcessWidget pw=plist.add(instance.getName());
					pw.setPort(port);
					pw.getStatusLabel().setText(instance.getState());					
				}
			}        				
        	//process Tasks 
        	Label label1=toolkit.createLabel(composite, "Process Tasks:");
        	label1.setLayoutData(    
        			new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
        	);
        	String[] columns={"Task","Status","Candidates"};
        	createTable(composite,columns);
        	plist.setViewer(viewer);
        } catch (Exception e) {
            e.printStackTrace();
        }	
	}//createFormContent
	
	private void createTable(Composite com,String[] columns){
		table =new Table(com,SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER|SWT.FULL_SELECTION);
		
		viewer = new TableViewer(table);
        int i=0;
        for(String column:columns){
        	TableColumn tableColumn=new TableColumn(table, SWT.CENTER);
        	tableColumn.setText(column);
        	if(i==0)
        		tableColumn.setWidth(300);
        	else if(i==1)
        		tableColumn.setWidth(100);
        	else if(i==2)
        		tableColumn.setWidth(300);
			//tableColumn.pack();			
			i++;
        }          
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
		GridData gd=new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
        table.setLayoutData( gd );
        gd.heightHint=120;        
        
        //set the content provider
        viewer.setContentProvider(new IStructuredContentProvider() {
        	public void dispose() {}
        	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
        	@SuppressWarnings("unchecked")
			public Object[] getElements(Object inputElement) {
        		if(inputElement instanceof WSProcessTaskInstance[])
        			return (WSProcessTaskInstance[]) inputElement;
        		else
        			return new Object[0];
        	}
        });
        
        //set the label provider
        viewer.setLabelProvider(new ITableLabelProvider() {

			public Image getColumnImage(Object element, int columnIndex) {
				// TODO Auto-generated method stub
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				WSProcessTaskInstance task=(WSProcessTaskInstance)element;
				switch(columnIndex){
				case 0:
					return task.getUuid()==null?"":task.getUuid();
				case 1:
					return task.getStatus()==null?"":task.getStatus();
				case 2:
					return task.getCandidates()==null?"":task.getCandidates();
				default:
						return "";
				}

			}

			public void addListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}

			public void dispose() {
				// TODO Auto-generated method stub
				
			}

			public boolean isLabelProperty(Object element, String property) {
				// TODO Auto-generated method stub
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
				// TODO Auto-generated method stub
				
			}      
        	
        });  
        
        //set input
        viewer.setInput(null);
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
		
	}

	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createCharacteristicsContent(FormToolkit toolkit,
			Composite charSection) {
		// TODO Auto-generated method stub
		
	}

}
