package com.amalto.workbench.editors;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.exolab.castor.xml.Marshaller;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.BusinessPortHelper;
import com.amalto.workbench.utils.FontUtils;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSGetConceptsInDataCluster;
import com.amalto.workbench.webservices.WSGetObjectsForSynchronizationPlans;
import com.amalto.workbench.webservices.WSGetSynchronizationPlanItemsAlgorithms;
import com.amalto.workbench.webservices.WSGetSynchronizationPlanObjectsAlgorithms;
import com.amalto.workbench.webservices.WSPing;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;
import com.amalto.workbench.webservices.WSSynchronizationPlan;
import com.amalto.workbench.webservices.WSSynchronizationPlanAction;
import com.amalto.workbench.webservices.WSSynchronizationPlanActionCode;
import com.amalto.workbench.webservices.WSSynchronizationPlanItemsSynchronizations;
import com.amalto.workbench.webservices.WSSynchronizationPlanPK;
import com.amalto.workbench.webservices.WSSynchronizationPlanStatus;
import com.amalto.workbench.webservices.WSSynchronizationPlanStatusCode;
import com.amalto.workbench.webservices.WSSynchronizationPlanXtentisObjectsSynchronizations;
import com.amalto.workbench.webservices.WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations;
import com.amalto.workbench.widgets.ComplexTableViewer;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.LabelText;
import com.amalto.workbench.widgets.TisTableViewer;

public class SynchronizationMainPage extends AMainPageV2{

	protected DropTarget windowTarget;

	protected TableViewer instancesViewer;

	protected FormToolkit toolkit;
	
	private ComplexTableViewerColumn[] xtentisObjectColumns= new ComplexTableViewerColumn[]{
		new ComplexTableViewerColumn("Instance Pattern", true, ".*", ".*"),
		new ComplexTableViewerColumn("Local Revision ID", true, "", "[HEAD]"),
		new ComplexTableViewerColumn("Remote Revision ID", true, "", "[HEAD]"),
		new ComplexTableViewerColumn("Algorithm", false, "", "", "",ComplexTableViewerColumn.COMBO_STYLE,new String[] {},0)
	};
	
	//use name to identify each column, if you change it , you must change the reference part below 
	private ComplexTableViewerColumn[] itemsColumns=new ComplexTableViewerColumn[]{
		new ComplexTableViewerColumn("Local Cluster", false, "", "","",ComplexTableViewerColumn.COMBO_STYLE,new String[] {},0,true,true),
		new ComplexTableViewerColumn("Concept Name", true, "", "[any]","",ComplexTableViewerColumn.COMBO_STYLE,new String[]{},0,true,true),
		new ComplexTableViewerColumn("Local Revision ID", true, "", "[HEAD]","",ComplexTableViewerColumn.COMBO_STYLE,new String[] {},0,true,true),
		new ComplexTableViewerColumn("IDs Pattern       ", true, ".*", ".*"),
		new ComplexTableViewerColumn("Remote Cluster", false, "", ""),
		new ComplexTableViewerColumn("Remote Revision ID", true, "", "[HEAD]"),
		new ComplexTableViewerColumn("Algorithm", false, "", "","Manual",ComplexTableViewerColumn.COMBO_STYLE,new String[] {},0)
	};
		
	protected SyncronizationPlan syncPlan;
	protected LabelText descriptionText;

	protected Label statusText;
	protected Button startFullButton;
	protected Button startDifferentialButton;
	protected Button stopButton;
	protected Button resetButton;
	protected Timer timer;
	
	protected LabelText remoteSystemNameText;
	protected LabelText remoteSystemPasswordText;
	protected LabelText remoteSystemUsernameText;
	protected LabelText remoteSystemURLText;

	protected LabelText tisURLText;
	protected LabelText tisUsernameText;
	protected LabelText tisPasswordText;
	protected LabelText tisParametersText;

	
	private Map<String,ComplexTableViewer> xtentisViewers=new HashMap<String, ComplexTableViewer>();
	public SynchronizationMainPage(FormEditor editor) {
        super(
        		editor,
        		SynchronizationMainPage.class.getName(),
        		"SynchronizationPlan "+((XObjectEditorInput)editor.getEditorInput()).getName()
        );        
	}

	@Override
	protected void createCharacteristicsContent(FormToolkit toolkit,
			Composite charComposite) {
	try {
		if(syncPlan==null)syncPlan=new SyncronizationPlan();
		this.toolkit=toolkit;
		
        //make the Page window a DropTarget - we need to dispose it
        windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
        windowTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
        windowTarget.addDropListener(new DCDropTargetListener());
		
        //basic setting
		descriptionText =new LabelText(charComposite,"Description");        
		descriptionText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.description=descriptionText.getText().getText();
        		markDirty();
        	}
        });
		
		toolkit.createLabel(charComposite, "Status");
		statusText = toolkit.createLabel(charComposite, "");
		statusText.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,false,1,1)
        );		
		toolkit.createLabel(charComposite, "Actions");
        Composite actionsComposite = toolkit.createComposite(charComposite, SWT.NONE);
        actionsComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );       
        actionsComposite.setLayout(new GridLayout(4,false));
        //full synchro
		startFullButton = toolkit.createButton(actionsComposite,"",SWT.PUSH );
		startFullButton.setToolTipText("Start Full");
		startFullButton.setImage(ImageCache.getCreatedImage(EImage.RUN_EXC.getPath()));
		
		startFullButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
		startFullButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		//If dirty, ask user to commit or discard changes first
        		if (SynchronizationMainPage.this.isDirty()) {
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error starting the synchronization",
        				"Please save the Plan first"
        			);
        			return;
        		}
        		//do form validate
        		if (remoteSystemURLText.getText().getText()==null||remoteSystemURLText.getText().getText().trim().equals("")) {
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error starting the synchronization",
        				"Please input the URL address of remote system first"
        			);
        			return;
        		}
        		//Start it
        		WSSynchronizationPlan ws = (WSSynchronizationPlan) (getXObject().getWsObject()); 
        		try {
	                getPort().synchronizationPlanAction(new WSSynchronizationPlanAction(
	                	new WSSynchronizationPlanPK(ws.getName()),
	                	WSSynchronizationPlanActionCode.START_FULL
	                ));
                } catch (RemoteException ex) {
                	ex.printStackTrace();
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error Starting the Full Synchronization", 
        				ex.getLocalizedMessage()
        			);
                }
        		startRefreshTimer();
        	}
        });
		//differential synchro
		startDifferentialButton = toolkit.createButton(actionsComposite,"",SWT.PUSH);
		startDifferentialButton.setImage(ImageCache.getCreatedImage(EImage.RUNLAST_CO.getPath()));
		startDifferentialButton.setToolTipText("Start Differential");
		startDifferentialButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
		startDifferentialButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		//If dirty, ask user to commit or discard changes first
        		if (SynchronizationMainPage.this.isDirty()) {
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error starting the synchronization",
        				"Please save the Plan first"
        			);
        			return;
        		}
        		
        		//do form validate
        		if (remoteSystemURLText.getText().getText()==null||remoteSystemURLText.getText().getText().trim().equals("")) {
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error starting the synchronization",
        				"Please input the URL address of remote system first"
        			);
        			return;
        		}
        		
        		//Start the synchronization
        		WSSynchronizationPlan ws = (WSSynchronizationPlan) (getXObject().getWsObject()); 
        		try {
	                getPort().synchronizationPlanAction(new WSSynchronizationPlanAction(
	                	new WSSynchronizationPlanPK(ws.getName()),
	                	WSSynchronizationPlanActionCode.START_DIFFERENTIAL
	                ));
                } catch (RemoteException ex) {
                	ex.printStackTrace();
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error Starting the Differential Synchronization", 
        				ex.getLocalizedMessage()
        			);
                }
                startRefreshTimer();
        	}
        });
		//stop synchro
		stopButton = toolkit.createButton(actionsComposite,"",SWT.PUSH);
		stopButton.setImage(ImageCache.getCreatedImage(EImage.STOP.getPath()));
		stopButton.setToolTipText("Stop");
		stopButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
		stopButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		WSSynchronizationPlan ws = (WSSynchronizationPlan) (getXObject().getWsObject()); 
        		try {
	                getPort().synchronizationPlanAction(new WSSynchronizationPlanAction(
	                	new WSSynchronizationPlanPK(ws.getName()),
	                	WSSynchronizationPlanActionCode.STOP
	                ));
                } catch (RemoteException ex) {
                	ex.printStackTrace();
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error Stopping the Synchronization", 
        				ex.getLocalizedMessage()
        			);
                }
        		refreshStatus();
        	}
        });
		//stop synchro
		resetButton = toolkit.createButton(actionsComposite,"",SWT.PUSH);
		resetButton.setImage(ImageCache.getCreatedImage(EImage.UNDO_EDIT.getPath()));
		resetButton.setToolTipText("Reset");
		resetButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
		resetButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		WSSynchronizationPlan ws = (WSSynchronizationPlan) (getXObject().getWsObject()); 
        		try {
	                getPort().synchronizationPlanAction(new WSSynchronizationPlanAction(
	                	new WSSynchronizationPlanPK(ws.getName()),
	                	WSSynchronizationPlanActionCode.RESET
	                ));
                } catch (RemoteException ex) {
                	ex.printStackTrace();
        			MessageDialog.openError(
        				SynchronizationMainPage.this.getSite().getShell(), 
        				"Error Resetting the Synchronization", 
        				ex.getLocalizedMessage()
        			);
                }
        		refreshStatus();
        	}
        });
        
        
        //Remote MDM Section          
        Composite remoteMDMGroup = this.getNewSectionComposite("Remote MDM Server");
        remoteMDMGroup.setLayout(new GridLayout(1,true));
        Composite remoteMDMComposite = toolkit.createComposite(remoteMDMGroup, SWT.BORDER);
        remoteMDMComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );       
        remoteMDMComposite.setLayout(new GridLayout(2,false));

		remoteSystemNameText =new LabelText(remoteMDMComposite,"Name");        
		remoteSystemNameText.getText().addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (refreshing) return;
				syncPlan.remoteSystemName = remoteSystemNameText.getText().getText();
				markDirty();
			}
		});
		
		remoteSystemURLText =new LabelText(remoteMDMComposite,"URL");        
		remoteSystemURLText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.remoteSystemURL=remoteSystemURLText.getText().getText();
        		markDirty();
        	}
        });
		
		remoteSystemUsernameText =new LabelText(remoteMDMComposite,"Username");        
		remoteSystemUsernameText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.remoteSystemUsername=remoteSystemUsernameText.getText().getText();
        		markDirty();
        	}
        });
		
		remoteSystemPasswordText =new LabelText(remoteMDMComposite,"Password");        
		remoteSystemPasswordText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.remoteSystemPassword=remoteSystemPasswordText.getText().getText();
        		markDirty();
        	}
        });		
        
		
		//TIS Server Section          
        Composite TISGroup = this.getNewSectionComposite("TIS Server",ExpandableComposite.TWISTIE);
        TISGroup.setLayout(new GridLayout(1,true));
        Composite TISComposite = toolkit.createComposite(TISGroup, SWT.BORDER);
        TISComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );       
        TISComposite.setLayout(new GridLayout(2,false));

		
		tisURLText =new LabelText(TISComposite,"URL");        
		tisURLText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.tisURL=tisURLText.getText().getText();
        		markDirty();
        	}
        });
		
		tisUsernameText =new LabelText(TISComposite,"Username");        
		tisUsernameText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.tisUsername=tisUsernameText.getText().getText();
        		markDirty();
        	}
        });
		
		tisPasswordText =new LabelText(TISComposite,"Password");        
		tisPasswordText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.tisPassword=tisPasswordText.getText().getText();
        		markDirty();
        	}
        });		
		
		tisParametersText =new LabelText(TISComposite,"Parameters");        
		tisParametersText.getText().addModifyListener(new ModifyListener() {
        	public void modifyText(ModifyEvent e) {
        		if (refreshing) return;
        		syncPlan.tisParameters=tisParametersText.getText().getText();
        		markDirty();
        	}
        });		
		
        //Items Section          
        Composite itemsGroup = this.getNewSectionComposite("Items SynchronizationPlan");
        itemsGroup.setLayout(new GridLayout(1,true));
        Composite itemsComposite = toolkit.createComposite(itemsGroup, SWT.BORDER);
        itemsComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );       
        itemsComposite.setLayout(new GridLayout(1,true));
        
        
        //Get data clusters for items
        WSDataClusterPK[] xdcPKs = getPort().getDataClusterPKs(new WSRegexDataClusterPKs("")).getWsDataClusterPKs();
        final List<String> dataClustersStrings=new ArrayList<String>();  
        for (int i = 0; i < xdcPKs.length; i++) {
			String name = xdcPKs[i].getPk();
			//filter cache
			if (name!=null&&name!=""&&!"CACHE".equals(name)){
				dataClustersStrings.add(name);
			}
		}
        
        //Get algorithms for items
        String[] itemsAlgorithmsStrings = getPort().getSynchronizationPlanItemsAlgorithms(
        	new WSGetSynchronizationPlanItemsAlgorithms(new String[]{".*"})
        ).getStrings();
        
        //Table
        ComplexTableViewer itemsViewer=new ComplexTableViewer(
        	Arrays.asList(itemsColumns),
        	toolkit,
        	itemsComposite
        );
        itemsViewer.setMainPage(this);
        
        
        ComplexTableViewerColumn complexTableViewerDataClusterColumn=itemsViewer.getColumn(new ComplexTableViewerColumn("Local Cluster"));
        complexTableViewerDataClusterColumn.setComboValues(dataClustersStrings.toArray(new String[dataClustersStrings.size()]));
        final ComplexTableViewerColumn complexTableViewerModelConceptColumn=itemsViewer.getColumn(new ComplexTableViewerColumn("Concept Name"));  
        itemsViewer.getColumn(new ComplexTableViewerColumn("Algorithm")).setComboValues(itemsAlgorithmsStrings);
        final ComplexTableViewerColumn complexTableViewerRevisionIdColumn=itemsViewer.getColumn(new ComplexTableViewerColumn("Local Revision ID"));
        
        //above before create complexTableViewer
        itemsViewer.create();
        //below after create complexTableViewer
        
        ((CCombo)complexTableViewerDataClusterColumn.getControl()).addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {
				int selectedIndex=((CCombo)e.getSource()).getSelectionIndex();
				String clusterName=dataClustersStrings.get(selectedIndex);
				try {
					//Get data concepts for items
					String[] concepts= getPort().getConceptsInDataCluster(
							new WSGetConceptsInDataCluster(
									new WSDataClusterPK(clusterName)
							)
					).getStrings();
					
					complexTableViewerModelConceptColumn.setComboValues(concepts);
					((CCombo)complexTableViewerModelConceptColumn.getControl()).setItems(complexTableViewerModelConceptColumn.getComboValues());
					
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
        	
        });
        
        ((CCombo)complexTableViewerModelConceptColumn.getControl()).addSelectionListener(new SelectionListener(){

			public void widgetDefaultSelected(SelectionEvent e) {
				
			}

			public void widgetSelected(SelectionEvent e) {
				int selectedIndex=((CCombo)e.getSource()).getSelectionIndex();
				String[] concepts=complexTableViewerModelConceptColumn.getComboValues();
				String selectedConcept=concepts[selectedIndex];
				try {
					List<String> pRevisions = BusinessPortHelper.getPossibleItemsRevisionsInCurrentUniverse(getPort(), selectedConcept);
					complexTableViewerRevisionIdColumn.setComboValues(pRevisions.toArray(new String[pRevisions.size()]));
					((CCombo)complexTableViewerRevisionIdColumn.getControl()).setItems(complexTableViewerRevisionIdColumn.getComboValues());
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				
			}
        });
        
        instancesViewer=itemsViewer.getViewer();
        instancesViewer.setInput(syncPlan.getItemsList());
		
        //Xtentis Objects  Section
        Composite objecstGroup = this.getNewSectionComposite("System Objects SynchronizationPlan",ExpandableComposite.TWISTIE);
        objecstGroup.setLayout(new GridLayout(1,true));
        
        //Get algorithms for objects
        String[] objectsAlgorithmStrings = getPort().getSynchronizationPlanObjectsAlgorithms(
        	new WSGetSynchronizationPlanObjectsAlgorithms(new String[]{".*"})
        ).getStrings();
        
        Composite objectsComposite = toolkit.createComposite(objecstGroup, SWT.BORDER);
        objectsComposite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        objectsComposite.setLayout(new GridLayout(1,false));
        String[] xtentisObjects=getPort().getObjectsForSynchronizationPlans(new WSGetObjectsForSynchronizationPlans(new String[]{".*"})).getStrings();//IConstants.XTENTISOBJECTS;
        Map<String,String> objectsRevisionMap=BusinessPortHelper.getObjectsRevisionMapInCurrentUniverse(getPort());
        for(String object: xtentisObjects){
            Composite composite =toolkit.createComposite(objectsComposite,SWT.BORDER);
            composite.setLayoutData(
                    new GridData(SWT.FILL,SWT.RIGHT,true,true,1,1)
            );       	
            composite.setLayout(new GridLayout(2,false));
            
            Label label = toolkit.createLabel(composite, object, SWT.WRAP);
            GridData data =new GridData(SWT.FILL,SWT.RIGHT,false,true,1,1);
            data.widthHint=120;
            label.setLayoutData(data);
            label.setFont(FontUtils.getBoldFont(label.getFont()));

            TisTableViewer objectViewer=new TisTableViewer(Arrays.asList(xtentisObjectColumns),toolkit,composite);
            objectViewer.setMainPage(this);
            if(objectsRevisionMap.get(object)!=null&&objectsRevisionMap.get(object).length()>0){
            //objectViewer.getColumn(new ComplexTableViewerColumn("Instance Pattern")).setDefaultValue(object);
            objectViewer.getColumn(new ComplexTableViewerColumn("Local Revision ID")).setDefaultValue(objectsRevisionMap.get(object));
            }
            objectViewer.getColumn(new ComplexTableViewerColumn("Algorithm")).setComboValues(objectsAlgorithmStrings);
//            objectViewer.setLastcomboStrings(objectsAlgorithmStrings);
            objectViewer.create();
            List<Line> objList=syncPlan.getXtentisObjectsList().get(object);
            if(objList==null){
            	objList=new ArrayList<Line>();
            	syncPlan.getXtentisObjectsList().put(object, objList);
            }
            objectViewer.getViewer().setInput(objList);
            ((GridData)objectViewer.getViewer().getTable().getLayoutData()).heightHint=60;
            
            xtentisViewers.put(object, objectViewer);
        }

                              
        refreshData();

    } catch (Exception e) {
        e.printStackTrace();
    }
		
	}

	@Override
	protected void createActions() {
		// TODO Auto-generated method stub
		
	}

	protected void refreshData() {
		try {

			if (this.comitting) return;
			
			this.refreshing = true;
			
			WSSynchronizationPlan ws = (WSSynchronizationPlan) (getXObject().getWsObject());    	
			
			syncPlan = new SyncronizationPlan();	
			//basic
			syncPlan.setDescription(ws.getDescription()==null ? "" : ws.getDescription());
			
			syncPlan.setRemoteSystemName(ws.getRemoteSystemName());
			syncPlan.setRemoteSystemURL(ws.getRemoteSystemURL());
			syncPlan.setRemoteSystemUsername(ws.getRemoteSystemUsername());
			syncPlan.setRemoteSystemPassword(ws.getRemoteSystemPassword());
			
			syncPlan.setTisURL(ws.getTisURL());
			syncPlan.setTisUsername(ws.getTisUsername());
			syncPlan.setTisPassword(ws.getTisPassword());
			syncPlan.setTisParameters(ws.getTisParameters());
			
			//xtentisObjects
			Map<String, List<Line>> xtentisMap=new HashMap<String, List<Line>>();
			for(WSSynchronizationPlanXtentisObjectsSynchronizations xtentisSync:ws.getXtentisObjectsSynchronizations()){				
				List<Line> lines=new ArrayList<Line>();
				if(xtentisSync.getSynchronizations()!=null){
					for(WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations objSync:xtentisSync.getSynchronizations()){
						Line line=new Line(
							xtentisObjectColumns,
							new String[]{
								objSync.getInstancePattern(),
								objSync.getLocalRevisionID(),
								objSync.getRemoteRevisionID(),
								objSync.getAlgorithm()
							}
						);
						lines.add(line);
					}
				}
				xtentisMap.put(xtentisSync.getXtentisObjectName(), lines);
				//refresh the xtentisobject tableviewer
				ComplexTableViewer viewer=xtentisViewers.get(xtentisSync.getXtentisObjectName());
				if(viewer!=null){
					viewer.getViewer().setInput(lines);
				}
			}
			syncPlan.setXtentisObjectsList(xtentisMap);
			//Items
			List<Line> lines=new ArrayList<Line>();
			for(WSSynchronizationPlanItemsSynchronizations itemSync:ws.getItemsSynchronizations()){
				Line line=new Line(
					itemsColumns,
					new String[]{
						itemSync.getLocalCluster(),	
						itemSync.getConceptName(),
						itemSync.getLocalRevisionID(),
						itemSync.getIdsPattern(),
						itemSync.getRemoteCluster(),
						itemSync.getRemoteRevisionID(),
						itemSync.getAlgorithm()
					}
				);
				lines.add(line);
			}
	    	syncPlan.setItemsList(lines);
	    	
			//Now fill in the values on the page
            descriptionText.getText().setText(syncPlan.getDescription()==null ? "" : syncPlan.getDescription());

            remoteSystemURLText.getText().setText(syncPlan.getRemoteSystemURL()==null?"":syncPlan.getRemoteSystemURL());
            remoteSystemNameText.getText().setText(syncPlan.getRemoteSystemName()==null?"":syncPlan.getRemoteSystemName());
            remoteSystemUsernameText.getText().setText(syncPlan.getRemoteSystemUsername()==null?"":syncPlan.getRemoteSystemUsername());
            remoteSystemPasswordText.getText().setText(syncPlan.getRemoteSystemPassword()==null?"":syncPlan.getRemoteSystemPassword());

            tisURLText.getText().setText(syncPlan.getTisURL()==null?"":syncPlan.getTisURL());
            tisUsernameText.getText().setText(syncPlan.getTisUsername()==null?"":syncPlan.getTisUsername());
            tisPasswordText.getText().setText(syncPlan.getTisPassword()==null?"":syncPlan.getTisPassword());
            tisParametersText.getText().setText(syncPlan.getTisParameters()==null?"":syncPlan.getTisParameters());

            //refresh the item tableviewer
            instancesViewer.setInput(syncPlan.getItemsList());
            //refresh the status
            refreshStatus();
            
            this.refreshing = false;

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	
	@Override
	public void dispose() {
		stopRefreshTimer();
	    super.dispose();
	}
	
	protected void refreshStatus() {
		//check server status( maybe waste some time, but more safety ) 
		try {
			getPort().ping(new WSPing("check"));
		} catch (RemoteException e1) {			
			e1.printStackTrace();
			stopRefreshTimer();
			startFullButton.setEnabled(false);
    		startDifferentialButton.setEnabled(false);
    		stopButton.setEnabled(false);
    		resetButton.setEnabled(true);
		}
		
		WSSynchronizationPlan ws = (WSSynchronizationPlan) (getXObject().getWsObject());
		WSSynchronizationPlanStatus wsStatus = null;
		try {
	        wsStatus = getPort().synchronizationPlanAction(new WSSynchronizationPlanAction(
	        	new WSSynchronizationPlanPK(ws.getName()),
	        	WSSynchronizationPlanActionCode.STATUS
	        ));
        } catch (RemoteException e) {
        	e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "The status of the Plan cannot be fetched: "+e.getLocalizedMessage());
			
        } 
		
    	statusText.setText("["+wsStatus.getWsStatusCode().getValue()+"] "+wsStatus.getStatusMessage());
    	if (
    		WSSynchronizationPlanStatusCode.RUNNING.equals(wsStatus.getWsStatusCode())
    		|| WSSynchronizationPlanStatusCode.SCHEDULED.equals(wsStatus.getWsStatusCode())
    	) {
    		startFullButton.setEnabled(false);
    		startDifferentialButton.setEnabled(false);
    		stopButton.setEnabled(true);
    		resetButton.setEnabled(false);
    	} else if (WSSynchronizationPlanStatusCode.STOPPING.equals(wsStatus.getWsStatusCode())) {
    		startFullButton.setEnabled(false);
    		startDifferentialButton.setEnabled(false);
    		stopButton.setEnabled(false);
    		resetButton.setEnabled(true);
    	} else {
    		stopRefreshTimer();
    		startFullButton.setEnabled(true);
    		startDifferentialButton.setEnabled(true);
    		stopButton.setEnabled(false);
    		resetButton.setEnabled(false);
    	}
	}
	
	protected void startRefreshTimer() {
		if (timer == null) {
    		timer = new Timer("Synchronization Plan refresh timer", true);
    		timer.schedule(
    			new TimerTask() {
        			@Override
        			public void run() {
        				getSite().getShell().getDisplay().asyncExec(
							new Runnable() {
								public void run() {
									refreshStatus();
								}
							}
						);
        			}
        		}, 
    			0, 
    			2000
    		);
		}
	}
	
	protected void stopRefreshTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	protected void commit() {
		try {
			if (this.refreshing) return;
			
			this.comitting = true;
			//basic
			WSSynchronizationPlan ws = (WSSynchronizationPlan) (getXObject().getWsObject());    	
			ws.setDescription(syncPlan.getDescription());
			
			ws.setRemoteSystemName(syncPlan.getRemoteSystemName());
			ws.setRemoteSystemURL(syncPlan.getRemoteSystemURL());
			ws.setRemoteSystemUsername(syncPlan.getRemoteSystemUsername());
			ws.setRemoteSystemPassword(syncPlan.getRemoteSystemPassword());

			ws.setTisURL(syncPlan.getTisURL());
			ws.setTisUsername(syncPlan.getTisUsername());
			ws.setTisPassword(syncPlan.getTisPassword());
			ws.setTisParameters(syncPlan.getTisParameters());
			
			//xtentisobjects
			Map<String, List<Line>> xtentisobjects =syncPlan.getXtentisObjectsList();
			WSSynchronizationPlanXtentisObjectsSynchronizations[] xtentisObjSyncs=new WSSynchronizationPlanXtentisObjectsSynchronizations[xtentisobjects.size()];
			int j=0;
			for(Map.Entry<String, List<Line>> entry: xtentisobjects.entrySet()){
				WSSynchronizationPlanXtentisObjectsSynchronizations obj=new WSSynchronizationPlanXtentisObjectsSynchronizations();
				obj.setXtentisObjectName(entry.getKey());
				
				WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[] syncs=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[entry.getValue().size()];				
				int i=0; 
				for(Line line: entry.getValue()){
					syncs[i]=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations();
					syncs[i].setInstancePattern(line.keyValues.get(0).value);
					syncs[i].setLocalRevisionID(line.keyValues.get(1).value);
					syncs[i].setRemoteRevisionID(line.keyValues.get(2).value);
					syncs[i].setAlgorithm(line.keyValues.get(3).value);
					i++;
				}
				obj.setSynchronizations(syncs);
				xtentisObjSyncs[j]=obj;
				j++;
			}
			ws.setXtentisObjectsSynchronizations(xtentisObjSyncs);
			//items
			List<Line> items=syncPlan.getItemsList();
			WSSynchronizationPlanItemsSynchronizations[] itemSyncs=new WSSynchronizationPlanItemsSynchronizations[items.size()];
			for(int i=0; i<items.size(); i++){
				Line line=items.get(i);
				itemSyncs[i]=new WSSynchronizationPlanItemsSynchronizations();
				itemSyncs[i].setLocalCluster(line.keyValues.get(0).value);
				itemSyncs[i].setConceptName(line.keyValues.get(1).value);
				itemSyncs[i].setLocalRevisionID(line.keyValues.get(2).value);
				itemSyncs[i].setIdsPattern(line.keyValues.get(3).value);
				itemSyncs[i].setRemoteCluster(line.keyValues.get(4).value);
				itemSyncs[i].setRemoteRevisionID(line.keyValues.get(5).value);
				itemSyncs[i].setAlgorithm(line.keyValues.get(6).value);				
			}
			ws.setItemsSynchronizations(itemSyncs);
			
			this.comitting = false;
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error comtiting the page", "Error comitting the page: "+e.getLocalizedMessage());
		}    	
	}
	
	
	

	/****************************************************************************
	 *   Comptroller Model
	 ****************************************************************************/
	public class SyncronizationPlan {
		protected String description=null;
		protected String remoteSystemURL=null;
		protected String remoteSystemName=null;
	    protected String remoteSystemUsername=null;
	    protected String remoteSystemPassword;
		protected String tisURL=null;
		protected String tisParameters=null;
	    protected String tisUsername=null;
	    protected String tisPassword;
	    protected Map<String,List<Line>> xtentisObjectsList=new HashMap<String,List<Line>>();
	    protected List<Line> itemsList=new ArrayList<Line>();
	    
		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getRemoteSystemName() {
        	return remoteSystemName;
        }

		public void setRemoteSystemName(String remoteSystemName) {
        	this.remoteSystemName = remoteSystemName;
        }

		public String getRemoteSystemURL() {
			return remoteSystemURL;
		}

		public void setRemoteSystemURL(String url) {
			this.remoteSystemURL = url;
		}

		public String getRemoteSystemUsername() {
			return remoteSystemUsername;
		}

		public void setRemoteSystemUsername(String username) {
			this.remoteSystemUsername = username;
		}

		public String getRemoteSystemPassword() {
			return remoteSystemPassword;
		}

		public void setRemoteSystemPassword(String password) {
			this.remoteSystemPassword = password;
		}

		public String getTisURL() {
        	return tisURL;
        }

		public void setTisURL(String tisURL) {
        	this.tisURL = tisURL;
        }

		public String getTisParameters() {
        	return tisParameters;
        }

		public void setTisParameters(String tisParameters) {
        	this.tisParameters = tisParameters;
        }

		public String getTisUsername() {
        	return tisUsername;
        }

		public void setTisUsername(String tisUsername) {
        	this.tisUsername = tisUsername;
        }

		public String getTisPassword() {
        	return tisPassword;
        }

		public void setTisPassword(String tisPassword) {
        	this.tisPassword = tisPassword;
        }

		public Map<String, List<Line>> getXtentisObjectsList() {
			return xtentisObjectsList;
		}

		public void setXtentisObjectsList(Map<String, List<Line>> xtentisObjectsList) {
			this.xtentisObjectsList = xtentisObjectsList;
		}

		public List<Line> getItemsList() {
			return itemsList;
		}

		public void setItemsList(List<Line> itemsList) {
			this.itemsList = itemsList;
		}

		@Override
		public String toString() {
			StringWriter sw = new StringWriter();
			try {
				Marshaller.marshal(this, sw);
			} catch (Exception e) {
				System.out.println("ERROR marshalling Role");
				e.printStackTrace();
			}
			return sw.toString();
		}
	
	}//class SyncronizationPlan
	

}
