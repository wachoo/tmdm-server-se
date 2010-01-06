package org.talend.mdm.workbench.enterprice.dialog;

import java.util.ArrayList;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.workbench.enterprice.actions.VersioningCompareAction;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSBoolean;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSVersioningGetItemHistory;
import com.amalto.workbench.webservices.WSVersioningHistoryEntry;
import com.amalto.workbench.webservices.WSVersioningItemHistory;
import com.amalto.workbench.webservices.WSVersioningRestoreItemByRevision;

public class VersioningHistoryDialog extends Dialog {

	private final static int BUTTON_CANCEL = 11;
	
	private TreeObject xobject=null;
	protected boolean isItems = false;
	private WSItemPK[] wsItemPKs = null;

	protected Group restoreGroup;
	protected TableViewer revisionsViewer;
	
	private boolean enableRestore = false;
	
	public VersioningHistoryDialog(Shell shell, TreeObject xobject, WSItemPK[] wsItemPKs) {
		super(shell);
		this.xobject = xobject;
		this.wsItemPKs = wsItemPKs;
		this.isItems = true;
	}

	protected Control createDialogArea(Composite parent) {
		
		try {
			parent.getShell().setText("History");

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
				
			}
			
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout layout = (GridLayout)composite.getLayout();
			layout.numColumns = 1;
							
			restoreGroup = new Group(composite,SWT.SHADOW_NONE);
			restoreGroup.setLayout(new GridLayout(1,true));
			restoreGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			restoreGroup.setText("History "+resourcesName);
			restoreGroup.setEnabled(false);
			
			
			revisionsViewer = new TableViewer(restoreGroup);
			revisionsViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
            );
			((GridData)revisionsViewer.getControl().getLayoutData()).widthHint =250;
            ((GridData)revisionsViewer.getControl().getLayoutData()).heightHint=150;
            
            revisionsViewer.setContentProvider(new ArrayContentProvider());
            revisionsViewer.setLabelProvider(new ITableLabelProvider() {
            	public String getColumnText(Object element, int columnIndex) {
					WSVersioningHistoryEntry entry = (WSVersioningHistoryEntry) element;
					return
							entry.getRevision()+" - "+
							entry.getComments()+"  ["+
							entry.getDate()+" "+
							entry.getAuthor()+"]";
            	}
            	public Image getColumnImage(Object element, int columnIndex) {
            		return null;
            	}
            	public void addListener(ILabelProviderListener listener) {}
            	public void dispose() {}
            	public boolean isLabelProperty(Object element, String property) {
            		return false;
            	}
            	public void removeListener(ILabelProviderListener listener) {}
            });
            revisionsViewer.addDoubleClickListener(new IDoubleClickListener() {
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

			
			hookContextMenu();

			refreshData();
			
		    return composite;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					this.getShell(),
					"Error", 
					"An error occured trying to create the History Dialog: "+e.getLocalizedMessage()
			);
			return null;
		}
	}
		
	private void refreshData() {
		
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
	
		WSVersioningItemHistory itemHistories = null;
		if (isItems) {
			
			if(wsItemPKs==null){
				return false;
			}else if (wsItemPKs.length ==1 ) {
				try {
					itemHistories = Util.getPort(xobject).versioningGetItemHistory(
							new WSVersioningGetItemHistory(
									null,
									wsItemPKs[0]
							)
					);
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							this.getShell(), 
							"Versioning Error", 
							"Unable to retrieve the history of item: "+getItemPKUniqueID(wsItemPKs[0])
							+"\nCaused by: "+e.getLocalizedMessage()
					);
					return false;
				}
			}else {
				//TODO muti-objects
			}
			
		} else {
			
			
		}// if is Items
		
		ArrayList<WSVersioningHistoryEntry> entries = new ArrayList<WSVersioningHistoryEntry>();
		if (isItems&&itemHistories !=null) {
			WSVersioningHistoryEntry[] histories = itemHistories.getWsHistoryEntries();
			if(histories!=null){
				for (int i = 0; i < histories.length; i++) {
					entries.add(histories[i]);
				}
			}
		}
		
		if (entries.size()>0) {
			revisionsViewer.setInput(entries.toArray(new WSVersioningHistoryEntry[entries.size()]));
			revisionsViewer.setSelection(new StructuredSelection(entries.get(0)));
			enableRestore = true;
		}
		
		return true;
		
	}
	
	
	public void restoreResources() {
		boolean isOK=false;
		
		WSVersioningHistoryEntry entry = (WSVersioningHistoryEntry) ((IStructuredSelection)revisionsViewer.getSelection()).getFirstElement();
		if (entry == null) return;
		if(isItems){
			try {
		    
				 WSBoolean rtn=Util.getPort(xobject).versioningRestoreItemByRevision(new WSVersioningRestoreItemByRevision(
			        		null,
			        		this.wsItemPKs[0],
			        		entry.getRevision()
			         ));
				 isOK=rtn.is_true();
		        
			} catch (Exception exx) {
				exx.printStackTrace();
				MessageDialog.openError(
						VersioningHistoryDialog.this.getShell(),
						"Error", 
						"Unable to restore the document : "+exx.getLocalizedMessage()
				);
			}
		}else{
			
		}
		
		if(isOK){
			MessageDialog.openInformation(
					VersioningHistoryDialog.this.getShell(),
					"Info", 
					"Successfully restored item '"+getItemPKUniqueID(wsItemPKs[0])+"' from revision '"+entry.getRevision()+"' "
			);
		}
		
	}
	
	
	private String getItemPKUniqueID(WSItemPK wsItemPK) {
    	String fname = wsItemPK.getWsDataClusterPK().getPk()+"."+wsItemPK.getConceptName();
    	for (int i = 0; i < wsItemPK.getIds().length; i++) {
    		fname+="."+(wsItemPK.getIds()[i]==null? "" : wsItemPK.getIds()[i].trim()); //trim added due to exist bu triming the ids
		}
    	return fname;
	  }
		
	
	/**************************
	* ListViewer  CONTEXT MENU
	***************************/
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VersioningHistoryDialog.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(revisionsViewer.getControl());
		revisionsViewer.getControl().setMenu(menu);
		//getSite().registerContextMenu(menuMgr, viewer);
	}
	
	protected void fillContextMenu(IMenuManager manager) {
		if(isItems&&(wsItemPKs.length>0)){
			manager.add(new VersioningCompareAction(VersioningHistoryDialog.this.getShell(),revisionsViewer,xobject,wsItemPKs[0]));	
		}else{
			//TODO suport Object
		}
	}

}
