package org.talend.mdm.workbench.enterprice.widgets;

import java.rmi.RemoteException;
import java.util.Map;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.webservices.WSProcessTaskInstance;
import com.amalto.workbench.webservices.WSProcessTaskInstanceArray;
import com.amalto.workbench.webservices.WSWorkflowGetTaskList;
import com.amalto.workbench.webservices.XtentisPort;

public class ProcessWidget {
	FormToolkit toolkit;
	private Composite parent;
	//private Button suspendButton;
	//private Button startButton;
	//private Button stopButton;
	private Button delButton;
	String name;
	ProcessList plist;
	TableViewer viewer;
	XtentisPort port;
	public ProcessWidget(FormToolkit toolkit, Composite composite, String name, ProcessList plist,TableViewer viewer){
		this.toolkit= toolkit;
		this.parent=toolkit.createComposite(composite,0);
		this.name=name;
		this.plist=plist;
		this.viewer=viewer;
		GridLayout layout=new GridLayout();
		layout.numColumns=7;
		layout.marginBottom=0;
		layout.marginHeight=0;
	
		this.parent.setLayout(layout);	
		create();
	}
	
	public TableViewer getViewer() {
		return viewer;
	}

	public void setViewer(TableViewer viewer) {
		this.viewer = viewer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHeight(){
		return parent.computeSize(SWT.DEFAULT, SWT.DEFAULT).y+5;
	}
	public Composite getComposite(){
		return parent;
	}
	public Button getDelButton() {
		return delButton;
	}
	public void setDelButton(Button delButton) {
		this.delButton = delButton;
	}
	
	public XtentisPort getPort() {
		return port;
	}

	public void setPort(XtentisPort port) {
		this.port = port;
	}

	public void setSelection(String name){
		
		for(Map.Entry<String, ProcessWidget> entry: plist.map.entrySet()){
			if(name.equals(entry.getKey())){
				setBackground(parent.getShell().getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
			}else{
				entry.getValue().setBackground(parent.getParent().getBackground());
			}
		}
		WSProcessTaskInstanceArray tasklist;
		try {
			tasklist = port.workflowGetTaskList(new WSWorkflowGetTaskList(name));
			WSProcessTaskInstance[] tasks=tasklist.getWstaskinstance();
			viewer.setInput(tasks);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void setBackground(Color color){
		Control[] childs=parent.getChildren();
		for(Control c:childs){
			c.setBackground(color);
		}
		parent.setBackground(color);		
	}
	SelectionAdapter listener=new SelectionAdapter(){
		@Override
		public void widgetSelected(SelectionEvent e) {
			setSelection(name);
			//test
			if(e.widget instanceof Button){
				Button btn=(Button)e.widget;
				statusLabel.setText(btn.getToolTipText().toUpperCase()+"ED");
			}
		}
	};
	MouseListener mouseListener=new MouseAdapter(){
		@Override
		public void mouseUp(MouseEvent e) {
			setSelection(name);
		}
	};
	private Label process;
	private Label statusLabel;
	private void create(){
		process=toolkit.createLabel(parent, name);
		GridData gd=new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1);
		gd.widthHint=240;
		process.setLayoutData(gd);
		//process.addSelectionListener(listener);
		Label label=toolkit.createLabel(parent, "Status:");
		statusLabel=toolkit.createLabel(parent, "RUNNING");
		gd=new GridData(SWT.FILL,SWT.CENTER,false,true,1,1);
		gd.widthHint=300;		
		statusLabel.setLayoutData(gd);
        //start/stop/suspend/resume
     /*   startButton = toolkit.createButton(parent, "", SWT.TOGGLE);
        startButton.setImage(ImageCache.getCreatedImage(EImage.RUN_EXC.getPath()));
        startButton.setToolTipText("Start");
        startButton.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
        );
        
        stopButton = toolkit.createButton(parent, "", SWT.TOGGLE);
        stopButton.setImage(ImageCache.getCreatedImage(EImage.STOP.getPath()));
        stopButton.setToolTipText("Stop");
        stopButton.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
        );
        suspendButton = toolkit.createButton(parent, "",  SWT.TOGGLE);
        suspendButton.setImage(ImageCache.getCreatedImage(EImage.SUSPEND.getPath()));
        suspendButton.setToolTipText("Suspend");
        suspendButton.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
        );
        suspendButton.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
            	if (suspendButton.getSelection()) {
            		//suspendSubscriptionEngine();
            		suspendButton.setText("Resume");
            		suspendButton.redraw();
            	} else {
            		//resumeSubscriptionEngine();
            		suspendButton.setText("Suspend");
            		suspendButton.redraw();
            	}
        	};
        });*/    		
        delButton = toolkit.createButton(parent, "",  SWT.TOGGLE);
        delButton.setImage(ImageCache.getCreatedImage(EImage.DELETE_OBJ.getPath()));
        delButton.setToolTipText("Delete");
        delButton.setLayoutData(
                new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
        );
      /*  startButton.addSelectionListener(listener);
        stopButton.addSelectionListener(listener);	
        suspendButton.addSelectionListener(listener);*/
        
        label.addMouseListener(mouseListener);
        process.addMouseListener(mouseListener);
        statusLabel.addMouseListener(mouseListener);
        statusLabel.addMouseListener(mouseListener);              
        parent.addMouseListener(mouseListener);              
	}
	/*public Button getSuspendButton() {
		return suspendButton;
	}
	public void setSuspendButton(Button suspendButton) {
		this.suspendButton = suspendButton;
	}
	public Button getStartButton() {
		return startButton;
	}
	public void setStartButton(Button startButton) {
		this.startButton = startButton;
	}
	public Button getStopButton() {
		return stopButton;
	}
	public void setStopButton(Button stopButton) {
		this.stopButton = stopButton;
	}*/

	public Label getProcess() {
		return process;
	}

	public void setProcess(Label process) {
		this.process = process;
	}

	public Label getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(Label statusLabel) {
		this.statusLabel = statusLabel;
	}
	
}
