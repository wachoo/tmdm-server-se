package org.talend.mdm.workbench.enterprice.dialog;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.talend.mdm.workbench.enterprice.actions.VersioningProgressAction;

import com.amalto.workbench.actions.ServerRefreshAction;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSBackgroundJobPK;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSVersioningGetItemsVersions;
import com.amalto.workbench.webservices.WSVersioningGetObjectsVersions;
import com.amalto.workbench.webservices.WSVersioningHistoryEntry;
import com.amalto.workbench.webservices.WSVersioningItemsVersions;
import com.amalto.workbench.webservices.WSVersioningItemsVersionsItems;
import com.amalto.workbench.webservices.WSVersioningObjectsVersions;
import com.amalto.workbench.webservices.WSVersioningObjectsVersionsObjects;
import com.amalto.workbench.webservices.WSVersioningRestoreItems;
import com.amalto.workbench.webservices.WSVersioningRestoreObjects;
import com.amalto.workbench.webservices.WSVersioningTagItems;
import com.amalto.workbench.webservices.WSVersioningTagObjects;
import com.amalto.workbench.webservices.XtentisPort;

public class VersioningDialog extends Dialog {

	private final static int BUTTON_CANCEL = 11;
	
	private TreeObject selectedXObject=null;
	private XtentisPort port=null;
	protected boolean isItems = false;
	private String objectType = null;
	private String[] instances = null;
	private WSItemPK[] wsItemPKs = null;

	protected Text tagText;
	protected Text commentText;
	protected Group restoreGroup; 
	protected TableViewer tagsViewer;
	
	private boolean enableRestore = false;

	/**
	 */
	public VersioningDialog(Shell shell, TreeObject selectedXObject, String objectType, String[] instances) {
		super(shell);
		this.selectedXObject = selectedXObject;
		try {
			this.port=Util.getPort(selectedXObject);
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		this.objectType = objectType;
		this.instances = instances;
		this.isItems = false;
	}
	
	public VersioningDialog(Shell shell, XtentisPort port, WSItemPK[] wsItemPKs) {
		super(shell);
		this.port = port;
		this.wsItemPKs = wsItemPKs;
		this.isItems = true;
	}

	protected Control createDialogArea(Composite parent) {
		
		try {
			//Should not really be here but well,....
			parent.getShell().setText("Versioning");

			String resourcesName= "";
			if (isItems) {
				resourcesName = "Item";
				if (wsItemPKs!=null) {
					if (wsItemPKs.length == 1) {
						resourcesName+="  "+getItemPKUniqueID(wsItemPKs[0]);
					} else {
						resourcesName+=" [multiple]";
					}
				}
			} else {
				resourcesName = objectType;
				if (instances!=null) {
					if (instances.length == 1) {
						resourcesName+="  "+instances[0];
					} else {
						resourcesName+=" [multiple]";
					}
				}
			}
			
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout layout = (GridLayout)composite.getLayout();
			layout.numColumns = 1;
			
			
			Group tagGroup = new Group(composite,SWT.SHADOW_NONE);
			tagGroup.setLayout(new GridLayout(2,false));
			tagGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			tagGroup.setText("Tag "+resourcesName);

			Label tagLabel = new Label(tagGroup, SWT.NONE);
			tagLabel.setLayoutData(
					new GridData(SWT.LEFT,SWT.FILL,false,false,1,1)
			);
			tagLabel.setText("Tag");

			tagText = new Text(tagGroup, SWT.BORDER);
			tagText.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			tagText.setText("");

			Label commentLabel = new Label(tagGroup, SWT.NONE);
			commentLabel.setLayoutData(
					new GridData(SWT.LEFT,SWT.FILL,false,false,1,1)
			);
			commentLabel.setText("Comment");

			commentText = new Text(tagGroup, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
			commentText.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			commentText.setText("");
			((GridData)commentText.getLayoutData()).widthHint=250;
			((GridData)commentText.getLayoutData()).heightHint=25;

			
			Button tagButton = new Button(tagGroup,SWT.PUSH);
			tagButton.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
			);
			tagButton.setText("OK");
			tagButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {
						tagResources();
				}				
			});
							
			restoreGroup = new Group(composite,SWT.SHADOW_NONE);
			restoreGroup.setLayout(new GridLayout(1,true));
			restoreGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			restoreGroup.setText("Restore "+resourcesName);
			restoreGroup.setEnabled(false);
			
			
            tagsViewer = new TableViewer(restoreGroup);
            tagsViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
            );
            ((GridData)tagsViewer.getControl().getLayoutData()).heightHint=150;
            tagsViewer.setContentProvider(new ArrayContentProvider());
            tagsViewer.setLabelProvider(new ITableLabelProvider() {
            	public String getColumnText(Object element, int columnIndex) {
					WSVersioningHistoryEntry entry = (WSVersioningHistoryEntry) element;
					return
							entry.getTag()+" - "+
							entry.getComments()+"  ["+
							entry.getDate()+" "+
							entry.getAuthor()+"]";
            	}
            	public Image getColumnImage(Object element, int columnIndex) {
            		// TODO Auto-generated method stub
            		return null;
            	}
            	public void addListener(ILabelProviderListener listener) {}
            	public void dispose() {}
            	public boolean isLabelProperty(Object element, String property) {
            		return false;
            	}
            	public void removeListener(ILabelProviderListener listener) {}
            });
            tagsViewer.addDoubleClickListener(new IDoubleClickListener() {
            	public void doubleClick(DoubleClickEvent event) {
            			restoreResources();
	            }
            });

			
			Button restoreButton = new Button(restoreGroup,SWT.PUSH);
			restoreButton.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			restoreButton.setText("Restore");
			restoreButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {restoreResources();}				
			});

			
			//hookContextMenu();

			refreshData();
			
		    return composite;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					this.getShell(),
					"Error", 
					"An error occured trying to create the Versioning Dialog: "+e.getLocalizedMessage()
			);
			return null;
		}
	}
		
	private void refreshData() {
		//decide whether to diplay or not the restore dialog
		
		if(!initHistoryTableView())return;
		
		if (enableRestore) {
			restoreGroup.setEnabled(true);
		}
		
	}
	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, BUTTON_CANCEL, "Cancel",true);
	}


	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case BUTTON_CANCEL:
			this.close();
		}
	}
	
	/***********************************************
	 * Search Documents
	 ***********************************************/

	private boolean initHistoryTableView() {
		WSVersioningObjectsVersions objectsHistories = null;
		WSVersioningItemsVersions itemsHistories = null;
		if (isItems) {
			
			if(wsItemPKs==null){
				return false;
			}else if (wsItemPKs.length ==1 ) {
				try {
					itemsHistories = port.versioningGetItemsVersions(
							new WSVersioningGetItemsVersions(
									null,
									wsItemPKs
							)
					);
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							this.getShell(), 
							"Versioning Error", 
							"Unable to retrieve the versions of items "+objectType
							+"\nCaused by: "+e.getLocalizedMessage()
					);
					return false;
				}
			}else {
				//TODO muti-objects
			}
			
		} else {
			
			if (instances == null) {

				try {
					objectsHistories = port.versioningGetObjectsVersions(
							new WSVersioningGetObjectsVersions(
									null,
									objectType,
									instances
							)
					);
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							this.getShell(), 
							"Versioning Error", 
							"Unable to retrieve the versions of objects "+objectType
							+"\nCaused by: "+e.getLocalizedMessage()
					);
					return false;
				}				
			} else if (instances.length ==1 ) {
				try {
					objectsHistories = port.versioningGetObjectsVersions(
							new WSVersioningGetObjectsVersions(
									null,
									objectType,
									instances
							)
					);
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							this.getShell(), 
							"Versioning Error", 
							"Unable to retrieve the history of object "+objectType+" instance "+instances[0]
							+"\nCaused by: "+e.getLocalizedMessage()                                                                             
					);
					return false;
				}
			}
			//TODO muti-objects
		}// if is Items
		
		ArrayList<WSVersioningHistoryEntry> entries = new ArrayList<WSVersioningHistoryEntry>();
		if (!isItems&&objectsHistories !=null) {
			WSVersioningObjectsVersionsObjects history = objectsHistories.getObjects()[0];
			if(history.getWsVersionEntries()!=null){
				for (int i = 0; i < history.getWsVersionEntries().length; i++) {
					entries.add(history.getWsVersionEntries()[i]);
				}
			}
		}
		if (isItems&&itemsHistories !=null) {
			WSVersioningItemsVersionsItems history = itemsHistories.getItems()[0];
			if(history.getWsVersionEntries()!=null){
				for (int i = 0; i < history.getWsVersionEntries().length; i++) {
					entries.add(history.getWsVersionEntries()[i]);
				}
			}
		}
		
		if (entries.size()>0) {
			tagsViewer.setInput(entries.toArray(new WSVersioningHistoryEntry[entries.size()]));
			tagsViewer.setSelection(new StructuredSelection(entries.get(0)));
			enableRestore = true;
		}
		
		return true;
		
	}
	
	
	public void tagResources() {
		
		if ("".equals(tagText.getText().trim())) {
			MessageDialog.openError(this.getShell(), "Error", "Please enter a tag value");
			return;
		}

		if ("".equals(commentText.getText().trim())) {
			MessageDialog.openError(this.getShell(), "Error", "Please enter a comment");
			return;
		}

		if(isItems){
			
			try {
		        WSBackgroundJobPK jobPK =
			        this.port.versioningTagItems(new WSVersioningTagItems(
			        		null,
			        		tagText.getText(),
			        		commentText.getText(),
			        		this.wsItemPKs
			        ));
		        
		        new VersioningProgressAction(this.getShell(),this.port,jobPK).run();
		         
			} catch (Exception exx) {
				exx.printStackTrace();
				MessageDialog.openError(
						VersioningDialog.this.getShell(),
						"Error", 
						"Unable to tag the documents : "+exx.getLocalizedMessage()
				);
			}
			
		}else{
			
			try {
		        WSBackgroundJobPK jobPK =
			        this.port.versioningTagObjects(new WSVersioningTagObjects(
			        		null,
			        		tagText.getText(),
			        		commentText.getText(),
			        		this.objectType,
			        		this.instances
			        ));
		        
		        new VersioningProgressAction(this.getShell(),this.port,jobPK).run();
		         
			} catch (Exception exx) {
				exx.printStackTrace();
				MessageDialog.openError(
						VersioningDialog.this.getShell(),
						"Error", 
						"Unable to tag the documents : "+exx.getLocalizedMessage()
				);
			}
			
		}
		
		refreshData();
	}
	
	public void restoreResources() {
		WSVersioningHistoryEntry entry = (WSVersioningHistoryEntry) ((IStructuredSelection)tagsViewer.getSelection()).getFirstElement();
		if (entry == null) return;
		if(isItems){
			try {
		        WSBackgroundJobPK jobPK = 
			        this.port.versioningRestoreItems(new WSVersioningRestoreItems(
			        		null,
			        		entry.getTag(),
			        		commentText.getText(),
			        		this.wsItemPKs 
			    ));
		        
		        new VersioningProgressAction(this.getShell(),this.port,jobPK).run();

			} catch (Exception exx) {
				exx.printStackTrace();
				MessageDialog.openError(
						VersioningDialog.this.getShell(),
						"Error", 
						"Unable to restore the documents : "+exx.getLocalizedMessage()
				);
			}
		}else{
			try {
		        WSBackgroundJobPK jobPK = 
			        this.port.versioningRestoreObjects(new WSVersioningRestoreObjects(
			        		null,
			        		entry.getTag(),
			        		this.objectType,
			        		this.instances 
			    ));
		        
		        new VersioningProgressAction(this.getShell(),this.port,jobPK).run();

			} catch (Exception exx) {
				exx.printStackTrace();
				MessageDialog.openError(
						VersioningDialog.this.getShell(),
						"Error", 
						"Unable to restore the documents : "+exx.getLocalizedMessage()
				);
			}
			ServerRefreshAction serverRefreshAction=new ServerRefreshAction(ServerView.show(),this.selectedXObject.getServerRoot());
			serverRefreshAction.run();
			
		}
		
	}
	
	
	public StructuredViewer getTagsViewer() {
		return tagsViewer;
	}
	
	private String getItemPKUniqueID(WSItemPK wsItemPK) {
    	String fname = wsItemPK.getWsDataClusterPK().getPk()+"."+wsItemPK.getConceptName();
    	for (int i = 0; i < wsItemPK.getIds().length; i++) {
    		fname+="."+(wsItemPK.getIds()[i]==null? "" : wsItemPK.getIds()[i].trim()); //trim added due to exist bu triming the ids
		}
    	return fname;
	  }
		
//	/**************************
//	* ListViewer  CONTEXT MENU
//	***************************/
//	
//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				VersioningDialog.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(tagsViewer.getControl());
//		tagsViewer.getControl().setMenu(menu);
//		//getSite().registerContextMenu(menuMgr, viewer);
//	}
//	protected void fillContextMenu(IMenuManager manager) {
//        if (!tagsViewer.getSelection().isEmpty()) {
//        	manager.add(new ProjectDocumentAction(serverView,this));
//        	manager.add(new DeleteDocumentAction(serverView,this));
//        }
//        manager.add(new NewDocumentAction(serverView,this));
//	}

}
