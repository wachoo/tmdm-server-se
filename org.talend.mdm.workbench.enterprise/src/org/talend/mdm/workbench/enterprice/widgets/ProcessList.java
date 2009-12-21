package org.talend.mdm.workbench.enterprice.widgets;

import java.rmi.RemoteException;
import java.util.HashMap;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.webservices.WSWorkflowDeleteProcessInstancesRequest;
import com.amalto.workbench.webservices.XtentisPort;

public class ProcessList {
	int processNum;
	ProcessWidget cur;
	HashMap<String,ProcessWidget> map=new HashMap<String, ProcessWidget>();

	FormToolkit toolkit;
	private Composite parent;
	private ScrolledComposite ccrollComposite;
	TableViewer viewer;
	private XtentisPort port;
	public ProcessList(XtentisPort port,FormToolkit toolkit, Composite composite,ScrolledComposite ccrollComposite,TableViewer viewer){
		this.port = port;
		this.toolkit= toolkit;
		this.parent=composite;
		this.ccrollComposite=ccrollComposite;
		this.viewer=viewer;
	}
	public ProcessWidget add(String name){
		if(map.get(name)!=null){
			MessageDialog.openWarning(null, "Warning", name+ " exists already!");
			return null;
		}		
		final ProcessWidget pw=new ProcessWidget(toolkit,parent,name,this,viewer);
		pw.getDelButton().addSelectionListener(new SelectionAdapter(){
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					delete(pw.getName());
					port.workflowDeleteProcessInstances(new WSWorkflowDeleteProcessInstancesRequest(pw.getName()));
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		map.put(name, pw);
		parent.layout(true);
		ccrollComposite.setMinSize(400, 31* map.size());
		return pw;
	}
	public ProcessWidget delete(String name ){
		ProcessWidget pw=map.remove(name);
		Composite com=pw.getComposite().getParent();
		pw.getComposite().dispose();
		com.layout(true);
		ccrollComposite.setMinSize(400, 31* map.size());
		return pw;
	}
	
	public ProcessWidget get(String name){
		return map.get(name);
	}
	public TableViewer getViewer() {
		return viewer;
	}
	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
		for(ProcessWidget pw: map.values()){
			pw.setViewer(viewer);
		}
	}
	
	
}
