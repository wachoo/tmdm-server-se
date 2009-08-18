package com.amalto.workbench.dialogs;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerSorter;
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
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;

import com.amalto.workbench.actions.BrowseViewAction;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSView;

public class ViewsListDialog extends Dialog {

	private final static int BUTTON_BROWSE = 10;

	
	protected  ServerView serverView = null;
	
	private WSView[] results = null;
	
	protected Text sViewText;
	protected ListViewer viewsListViewer;

	/**
	 * @param parentShell
	 */
	public ViewsListDialog(ServerView serverView) {
		super(serverView.getSite().getShell());
		this.serverView = serverView;
	}

	protected Control createDialogArea(Composite parent) {
		
		try {
			//Should not really be here but well,....
			parent.getShell().setText("Views List");
			
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout layout = (GridLayout)composite.getLayout();
			layout.numColumns = 2;
			
			Group searchGroup = new Group(composite,SWT.SHADOW_NONE);
			searchGroup.setLayout(new GridLayout(2,true));
			searchGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
			);
			searchGroup.setText("Search Documents");

			Label documentLabel = new Label(searchGroup, SWT.NONE);
			documentLabel.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
			);
			documentLabel.setText("Document");
					
			sViewText = new Text(searchGroup, SWT.NONE);
			sViewText.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
			);
			sViewText.setText("*");
			
			Button searchButton = new Button(searchGroup,SWT.PUSH);
			searchButton.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
			);
			searchButton.setText("Search");
			searchButton.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {searchViews();}				
			});
			parent.getShell().setDefaultButton(searchButton);
			
			Group resultGroup = new Group(composite,SWT.SHADOW_NONE);
			resultGroup.setLayout(new GridLayout(2,false));
			resultGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
			);
			((GridData)resultGroup.getLayoutData()).minimumHeight = 200;
			resultGroup.setText("Search Results");
			
			viewsListViewer = new ListViewer(resultGroup,SWT.V_SCROLL | SWT.H_SCROLL);
            viewsListViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
            );
			viewsListViewer.setContentProvider(new ArrayContentProvider());
			viewsListViewer.setLabelProvider(new ILabelProvider() {
				public void addListener(ILabelProviderListener listener) {}
				public void dispose() {}
				public boolean isLabelProperty(Object element, String property) {return false;}
				public void removeListener(ILabelProviderListener listener) {}
				public Image getImage(Object element) {return null;}
				public String getText(Object element) {
					return ((WSView)element).getName();
				}			
			})	;
			viewsListViewer.setSorter(new ViewerSorter());
			viewsListViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event) {
					(new BrowseViewAction(ViewsListDialog.this.serverView,ViewsListDialog.this)).run();
				}
			});
			
			hookContextMenu();
			
			searchViews();
			
		    return composite;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					this.getShell(),
					"Error", 
					"An error occured trying to create the Views Search window: "+e.getLocalizedMessage()
			);
			return null;
		}
		
	}

	
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, BUTTON_BROWSE, "Browse",false);
	}



	protected void buttonPressed(int buttonId) {
		switch (buttonId) {
		case BUTTON_BROWSE:
			(new BrowseViewAction(serverView,this)).run();
			break;
		}
	}
	
	
	/***********************************************
	 * Search Documents
	 *
	 ***********************************************/
	
	public void searchViews() {
		try {
			ISelection selection =serverView.getViewer().getSelection();
	        TreeObject xo = (TreeObject)((IStructuredSelection)selection).getFirstElement();
			ViewsListDialog.this.results = Util.getAllViews(
					xo.getServer(),
					xo.getUsername(),
					xo.getPassword(),
					sViewText.getText().replaceAll("\\*",".*")
			);
			viewsListViewer.setInput(ViewsListDialog.this.results);
		} catch (Exception exx) {
			exx.printStackTrace();
			MessageDialog.openError(
					ViewsListDialog.this.getShell(),
					"Error", 
					"Unable to retrieve the views list: "+exx.getLocalizedMessage()
			);
		}
	}
	
	public StructuredViewer getViewsListViewer() {
		return viewsListViewer;
	}
		
	
	
	
	/**************************
	 * 
	 * ListViewer  CONTEXT MENU
	 */
	
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ViewsListDialog.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewsListViewer.getControl());
		viewsListViewer.getControl().setMenu(menu);
		//getSite().registerContextMenu(menuMgr, viewer);
	}
	protected void fillContextMenu(IMenuManager manager) {
        if (!viewsListViewer.getSelection().isEmpty()) {
        	manager.add(new BrowseViewAction(serverView,this));
        }
	}
	

}
