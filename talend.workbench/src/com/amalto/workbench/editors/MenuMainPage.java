/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.MenuEntryDialog;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuEntry;
import com.amalto.workbench.webservices.WSMenuMenuEntriesDescriptions;

public class MenuMainPage extends AMainPageV2 {

 	public final static int LOCATION_BEFORE =0;
 	public final static int LOCATION_AFTER =1;
 	public final static int LOCATION_UNDER =2;

	
	protected Text descriptionText;
	protected TreeViewer menuTree=null;
	protected MenuManager menuTreeMgr = null;

	protected DropTarget windowTarget;	
	
	protected boolean refreshing = false;
	protected boolean comitting = false;
	
	
    public MenuMainPage(FormEditor editor) {
        super(
        		editor,
        		MenuMainPage.class.getName(),
        		"Menu "+((XObjectEditorInput)editor.getEditorInput()).getName()
        );        
    }

	protected void createCharacteristicsContent(FormToolkit toolkit, Composite mainComposite) {

        try {
            //description
            Label descriptionLabel = toolkit.createLabel(mainComposite, "Description", SWT.NULL);
            descriptionLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
            );
            descriptionText = toolkit.createText(mainComposite, "",SWT.BORDER|SWT.MULTI);
            descriptionText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            ((GridData)descriptionText.getLayoutData()).minimumHeight = 30;
            descriptionText.addModifyListener(new ModifyListener() {
            	public void modifyText(ModifyEvent e) {
            		if (refreshing) return;
            		//commit as we go
            		WSMenu wsMenu = ((WSMenu)getXObject().getWsObject());
            		wsMenu.setDescription(descriptionText.getText());
            		markDirty();
            	}
            }); 
            
            //make the Page window a DropTarget - we need to dispose it
            windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
            windowTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
            windowTarget.addDropListener(new DCDropTargetListener());
            
            Composite composite = toolkit.createComposite(mainComposite, SWT.BORDER);
            composite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
            );
            composite.setLayout(new GridLayout(1,false));
            
    		menuTree = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
            menuTree.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            ((GridData)menuTree.getControl().getLayoutData()).heightHint=150;
            
    		menuTree.setContentProvider(new ITreeContentProvider() {
    			public void dispose() {}
    			public Object[] getChildren(Object parentElement) {
    				if (parentElement instanceof TreeEntry) {
    					WSMenuEntry wsEntry = ((TreeEntry)parentElement).getWsMenuEntry();
    					if (wsEntry.getSubMenus()!=null) {
    						TreeEntry[] children = new TreeEntry[wsEntry.getSubMenus().length];
    						for (int i = 0; i < wsEntry.getSubMenus().length; i++) {
    							children[i]= new TreeEntry(
    									(TreeEntry) parentElement, 
    									wsEntry.getSubMenus()[i]
    								);
							}
    						return children;
    					}
    					return null;
    				}
    				
    				if (parentElement instanceof WSMenu) {	//the root
    					WSMenuEntry[] menuEntries = ((WSMenu)parentElement).getMenuEntries();
    					if (menuEntries != null) {
    						TreeEntry[] children = new TreeEntry[menuEntries.length];
    						for (int i = 0; i < menuEntries.length; i++) {
    							children[i]=	new TreeEntry(null, menuEntries[i]);
							}
    						return children;    						
    					}
    					return null;
    				}
    				
    				return null; //??!!?
    			}
    			public Object[] getElements(Object inputElement) {return getChildren(inputElement);}
    			public Object getParent(Object element) {return null;}
    			public boolean hasChildren(Object element) {
    				return (
    					(getChildren(element) == null) || (getChildren(element).length>0)
    				);
    				}
    			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    		});
    		menuTree.setLabelProvider(new LabelProvider(){
    			@Override
    			public String getText(Object element) {
    				WSMenuEntry wsMenuEntry = ((TreeEntry)element).getWsMenuEntry();
    				String label = wsMenuEntry.getId()+" - ";
    				for (int j = 0; j < wsMenuEntry.getDescriptions().length; j++) {
    					label+=
    						"["+wsMenuEntry.getDescriptions()[j].getLanguage()+": "+
    						wsMenuEntry.getDescriptions()[j].getLabel()+"] ";
    				}
    				if (label.length()>200) 
    					label = label.substring(0,197)+"...";
    				return label;
    			}
    			@Override
    			public Image getImage(Object element) {
    				return ImageCache.getImage( "icons/menu.gif").createImage();
    			}
    		});
    		
    		menuTreeMgr = new MenuManager();
    		menuTreeMgr.setRemoveAllWhenShown(true);
    		menuTreeMgr.addMenuListener(new IMenuListener() {
    			public void menuAboutToShow(IMenuManager manager) {
    				IStructuredSelection selection=((IStructuredSelection)menuTree.getSelection());
    				if ((selection == null) || (selection.getFirstElement()==null)) {
    					manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    					return;
    				}
//    				TreeEntry entry = (TreeEntry)selection.getFirstElement();
    				menuTreeMgr.add(new TreeEntryEditAction(menuTree));
    				menuTreeMgr.add(new TreeEntryAddAction(menuTree,LOCATION_BEFORE));
    				menuTreeMgr.add(new TreeEntryAddAction(menuTree,LOCATION_AFTER));
    				menuTreeMgr.add(new TreeEntryAddAction(menuTree,LOCATION_UNDER));
   					menuTreeMgr.add(new TreeEntryDeleteAction(menuTree));
    				menuTreeMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
    			}
    		});
    		menuTree.addDoubleClickListener(new IDoubleClickListener() {
    			public void doubleClick(DoubleClickEvent event) {
    				(new TreeEntryEditAction(menuTree)).run();
    			}
    		});
    		Menu menu = menuTreeMgr.createContextMenu(menuTree.getControl());
    		menuTree.getControl().setMenu(menu);
    		getSite().registerContextMenu(menuTreeMgr, menuTree);
    	
              
            refreshData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//createCharacteristicsContent



	protected void refreshData() {
		try {
//			System.out.println("refreshData() ");
			if (this.comitting) return;
			
			this.refreshing = true;
			
			WSMenu wsMenu = (WSMenu) (getXObject().getWsObject());   			
            descriptionText.setText(wsMenu.getDescription()==null ? "" : wsMenu.getDescription());
	    	menuTree.setInput(wsMenu);
            this.refreshing = false;

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	
	protected void commit() {
		try {
//			System.out.println("commit() ROLE\n"+role.toString());
			if (this.refreshing) return;
			
			this.comitting = true;
			
			//COMMIT AS WE GO
			
			this.comitting = false;
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error commiting the page", "Error comitting the page: "+e.getLocalizedMessage());
		}    	
	}
		
	protected void createActions() {
	}


	/*
	private void hookContextMenu(TreeViewer viewer) {
	}

	private void fillContextMenu(IMenuManager manager) {
	}
	*/
	
	public void dispose() {
		super.dispose();
		windowTarget.dispose();
	}
	
	

	
	class TreeEntry {
		private TreeEntry parentTreeEntry; //null for the root
		private WSMenuEntry wsMenuEntry;  //null for an attribute

		
		public TreeEntry(TreeEntry parentTreeEntry, WSMenuEntry wsMenuEntry) {
			super();
			this.parentTreeEntry = parentTreeEntry;
			this.wsMenuEntry = wsMenuEntry;
		}

		public TreeEntry getParentTreeEntry() {
			return parentTreeEntry;
		}
		public void setParentTreeEntry(TreeEntry parentTreeEntry) {
			this.parentTreeEntry = parentTreeEntry;
		}

		public WSMenuEntry getWsMenuEntry() {
			return wsMenuEntry;
		}
		public void setWsMenuEntry(WSMenuEntry wsMenuEntry) {
			this.wsMenuEntry = wsMenuEntry;
		}
		
		
		
	}
	
	
	class MenuEntryDialogSelectionListener implements SelectionListener{
		protected TreeViewer viewer = null;
		protected TreeEntry treeEntry = null;
		
		public MenuEntryDialogSelectionListener(TreeViewer viewer, TreeEntry treeEntry) {
			super();
			this.viewer = viewer;
			this.treeEntry = treeEntry;
		}
		
		public void widgetSelected(SelectionEvent e) {
			MenuEntryDialog dlg = (MenuEntryDialog)((Widget)e.getSource()).getData("dialog");
			if (dlg.getReturnCode() == Window.OK)  {			
				String id = dlg.getIdText().getText();
				if ("".equals(id)) {
					MessageDialog.openError(
							viewer.getControl().getShell(),
							"Error", 
							"The ID cannot be empty"
					);
					return;
				}
				LinkedHashMap<String, String> descriptions = ((LinkedHashMap<String, String>)dlg.getDescriptionsTableViewer().getInput());
				if (descriptions.size()==0) {
					MessageDialog.openError(
							viewer.getControl().getShell(),
							"Error", 
							"The Menu Entry must have at least one description"
					);
					return;
				}									
	        	treeEntry.getWsMenuEntry().setId(id);
	        	treeEntry.getWsMenuEntry().setContext("".equals(dlg.getContextText().getText()) ? null : dlg.getContextText().getText());
	        	treeEntry.getWsMenuEntry().setApplication("".equals(dlg.getApplicationNameText().getText()) ? null : dlg.getApplicationNameText().getText());
	        	WSMenuMenuEntriesDescriptions[] wsDescriptions = new WSMenuMenuEntriesDescriptions[descriptions.size()];
	        	Set<String> isoCodes = descriptions.keySet();
	        	int i=0;
	        	for (Iterator iter = isoCodes.iterator(); iter.hasNext(); ) {
					String isoCode = (String) iter.next();
					wsDescriptions[i] = new WSMenuMenuEntriesDescriptions();
					wsDescriptions[i].setLanguage(isoCode);
					wsDescriptions[i].setLabel(descriptions.get(isoCode));
					i++;
				}
	        	treeEntry.getWsMenuEntry().setDescriptions(wsDescriptions);
				viewer.setExpandedState(treeEntry, true);
				viewer.refresh(treeEntry, true);
				markDirty();
	        }
	        dlg.close();							        
			
		}
		public void widgetDefaultSelected(SelectionEvent e) {};
	}
	
	
	 class TreeEntryEditAction extends Action{

			protected TreeViewer viewer = null;
			protected TreeEntry treeEntry = null;
			protected MenuEntryDialog dlg = null;
						
			public TreeEntryEditAction(TreeViewer view) {
				super();
				this.viewer = view;
				treeEntry =(TreeEntry)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				setImageDescriptor(ImageCache.getImage( "icons/edit_obj.gif"));
				setText("Edit");
				setToolTipText("Edit ThisMenu Entry");		
			}
			
			public void run() {
				try {
					super.run();
					//if attribute, edit parent else edit this one
					dlg = new MenuEntryDialog(
							treeEntry.getWsMenuEntry(),
							new MenuEntryDialogSelectionListener(viewer,treeEntry),
							this.viewer.getControl().getShell(),
							"Edit the Menu Entry "+treeEntry.getWsMenuEntry().getId(),false
					);
					dlg.setBlockOnOpen(true);
					dlg.open();
					
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							viewer.getControl().getShell(),
							"Error", 
							"An error occured trying to edit the Menu Entry: "+treeEntry.getWsMenuEntry().getId()+" : "+e.getLocalizedMessage()
					);
				}		
			}
			public void runWithEvent(Event event) {super.runWithEvent(event);}
	 }//class RoleMenuTreeEditAction
	 
	 
	 
	 class TreeEntryAddAction extends Action{
		 
			protected TreeViewer viewer = null;
			protected TreeEntry treeEntry = null;
			protected MenuEntryDialog dlg = null;
			protected int location = 0;;
			protected int position = 0;
						
			public TreeEntryAddAction(TreeViewer view,int location) {
				super();
				this.viewer = view;
				this.location = location;
				String label="";
				TreeEntry currentEntry =(TreeEntry)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				switch (location) {
					case LOCATION_UNDER:
						position = 0;
						treeEntry = new TreeEntry(
							currentEntry,
							new WSMenuEntry("",null,"","",null)
						);
						label = "Add a sub menu entry to "+currentEntry.getWsMenuEntry().getId();
						break;
					case LOCATION_BEFORE:
						position = findSubMenuPosition(currentEntry);
						treeEntry = new TreeEntry(
							currentEntry.getParentTreeEntry(),
							new WSMenuEntry("",null,"","",null)
						);
						label = "Add a menu entry before this entry";
						break;
					case LOCATION_AFTER:
						position = findSubMenuPosition(currentEntry);
						treeEntry = new TreeEntry(
							currentEntry.getParentTreeEntry(),
							new WSMenuEntry("",null,"","",null)
						);
						label = "Add a menu entry after this entry";
						break;
				}
				setImageDescriptor(ImageCache.getImage( "icons/add_obj.gif"));
				setText(label);
				setToolTipText("Add a menu entry");		
			}
			
			public void run() {
				try {
					super.run();
					//if attribute, edit parent else edit this one
					dlg = new MenuEntryDialog(
							new WSMenuEntry(),
							new MenuEntryDialogSelectionListener(viewer,treeEntry),
							this.viewer.getControl().getShell(),
							"New Menu Entry "
					);
					dlg.setBlockOnOpen(true);
					dlg.open();
					if (dlg.getReturnCode() == Window.OK) {
						//add the entry to the WS Parent
						addSubMenu(treeEntry, position);
					}					
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							viewer.getControl().getShell(),
							"Error", 
							"An error occured trying to add a Menu Entry: "+e.getLocalizedMessage()
					);
				}		
			}
			public void runWithEvent(Event event) {super.runWithEvent(event);}
			

			protected int findSubMenuPosition(TreeEntry entry) {
				//find the position
				if (entry.getParentTreeEntry() == null) { //top level
					WSMenu menu = ((WSMenu)viewer.getInput());
					for (int i = 0; i < menu.getMenuEntries().length; i++) {
						if (menu.getMenuEntries()[i].equals(entry.getWsMenuEntry())) {
							position = i;
							break;
						}
					}
					return position;
				}
				//sub menu of a menu entry
				for (int i = 0; i < entry.getParentTreeEntry().getWsMenuEntry().getSubMenus().length; i++) {
					if (entry.getParentTreeEntry().getWsMenuEntry().getSubMenus()[i].equals(entry.getWsMenuEntry())) {
						position = i;
						break;
					}							
				}
				return position;
			}
			
			protected void addSubMenu(TreeEntry entry, int position) {
				ArrayList<WSMenuEntry> list = new ArrayList<WSMenuEntry>();
				if (entry.getParentTreeEntry() == null) {	//top level
					WSMenu menu = ((WSMenu)viewer.getInput());
					for (int i = 0; i < menu.getMenuEntries().length; i++) {
						if ((position==i) && (location != LOCATION_AFTER))list.add(entry.getWsMenuEntry());
						list.add(menu.getMenuEntries()[i]);
						if ((position==i) && (location == LOCATION_AFTER))list.add(entry.getWsMenuEntry());
					}
					menu.setMenuEntries(list.toArray(new WSMenuEntry[list.size()]));
					viewer.setExpandedState(menu, true);
					viewer.refresh(menu, true);
					System.out.println("THere ");
					return;
				}
				
				//sub menu of a menu entry
				WSMenuEntry wsParent =  entry.getParentTreeEntry().getWsMenuEntry();
				if ((wsParent.getSubMenus() == null) || (wsParent.getSubMenus().length==0)) { //no submenus yet
					System.out.println("Here ");
					wsParent.setSubMenus(new WSMenuEntry[]{entry.getWsMenuEntry()});
					viewer.setExpandedState(entry.getParentTreeEntry(), true);
					viewer.refresh(entry.getParentTreeEntry(), true);
					return;
				}
				for (int i = 0; i < wsParent.getSubMenus().length; i++) {
					if ((position==i) && (location != LOCATION_AFTER)) list.add(entry.getWsMenuEntry());
					list.add(wsParent.getSubMenus()[i]);
					if ((position==i) && (location != LOCATION_BEFORE)) list.add(entry.getWsMenuEntry());
				}
				wsParent.setSubMenus(list.toArray(new WSMenuEntry[list.size()]));
				viewer.setExpandedState(entry.getParentTreeEntry(), true);
				viewer.refresh(entry.getParentTreeEntry(), true);
			}
	 }//class RoleMenuTreeAddAction
	
	 
	 
	 class TreeEntryDeleteAction extends Action{

			protected TreeViewer viewer = null;
			protected TreeEntry treeEntry = null;
			protected MenuEntryDialog dlg = null;
						
			public TreeEntryDeleteAction(TreeViewer view) {
				super();
				this.viewer = view;
				treeEntry =(TreeEntry)((IStructuredSelection)viewer.getSelection()).getFirstElement();
				setImageDescriptor(ImageCache.getImage( "icons/delete_obj.gif"));
				setText("Delete Entry");
				setToolTipText("Delete This Menu Entry");		
			}
			
			public void run() {
				try {
					if (treeEntry.getParentTreeEntry() == null) {	//top level menu
						WSMenu menu = ((WSMenu)viewer.getInput());
						if (menu.getMenuEntries().length==1) {
							MessageDialog.openWarning(
									MenuMainPage.this.getSite().getShell(), 
									"Menu Entry Warning", 
									"A Menu must contain at least one menu entry"
							);
							return;
						}						
						ArrayList<WSMenuEntry> subMenus = new ArrayList(Arrays.asList(menu.getMenuEntries()));
						subMenus.remove(treeEntry.getWsMenuEntry());
						menu.setMenuEntries(subMenus.toArray(new WSMenuEntry[subMenus.size()]));
					} else {
						//sub Menu Entry of a sub menu
						ArrayList<WSMenuEntry> subMenus = new ArrayList(Arrays.asList(treeEntry.getParentTreeEntry().getWsMenuEntry().getSubMenus()));
						subMenus.remove(treeEntry.getWsMenuEntry());
						treeEntry.getParentTreeEntry().getWsMenuEntry().setSubMenus(subMenus.toArray(new WSMenuEntry[subMenus.size()]));
					}
					//refresh the viewer
					viewer.setExpandedState(treeEntry.getParentTreeEntry(), true);
					viewer.refresh(treeEntry.getParentTreeEntry(), false);
					//mark dirty
					markDirty();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							viewer.getControl().getShell(),
							"Error", 
							"An error occured trying to delete the Menu Entry: "+treeEntry.getWsMenuEntry().getId()+" : "+e.getLocalizedMessage()
					);
				}		
			}
			public void runWithEvent(Event event) {super.runWithEvent(event);}
	 }//class RoleMenuTreeDeleteAction
	 
	 
	/****************************************************************************
	 *   DND
	 ****************************************************************************/
	
	class DCDragSourceListener implements DragSourceListener {
		private int selected;

		public void dragFinished(DragSourceEvent event) {
			Control control = ((DragSource)event.widget).getControl();
			if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
				((List)control).remove(selected);
				MenuMainPage.this.markDirty();
			}
		}

		public void dragSetData(DragSourceEvent event) {
			Control control = ((DragSource)event.widget).getControl();
			if ((control instanceof List))
				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
					this.selected = ((List)control).getSelectionIndex();
					event.data =  ((List)control).getSelection()[0];
				}
		}

		public void dragStart(DragSourceEvent event) {
			Control control = ((DragSource)event.widget).getControl();
			if ((control instanceof List))
				event.doit = (((List)control).getItemCount()>0);
		}
	}
	
	class DCDropTargetListener implements DropTargetListener {

		public void dragEnter(DropTargetEvent event) {
			//priority to copy
			if ((event.operations & DND.DROP_COPY) == DND.DROP_COPY)
				event.detail = DND.DROP_COPY;
			else if ((event.operations & DND.DROP_MOVE) == DND.DROP_MOVE)
				event.detail = DND.DROP_MOVE;
			else	
				event.detail = DND.DROP_NONE;
		}
		public void dragLeave(DropTargetEvent event) {}
		public void dragOperationChanged(DropTargetEvent event) {}
		public void dragOver(DropTargetEvent event) {}
		public void drop(DropTargetEvent event) {
			Control control = ((DropTarget)event.widget).getControl();
			if ((control instanceof List) && ((event.operations & DND.DROP_COPY) == DND.DROP_COPY))
				if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) 
					if (!Arrays.asList(((List)control).getItems()).contains(event.data)) {
							((List)control).add((String)event.data);
							MenuMainPage.this.markDirty();
					}
		}
		public void dropAccept(DropTargetEvent event) {}
		
	}
	
	

	
	
}
