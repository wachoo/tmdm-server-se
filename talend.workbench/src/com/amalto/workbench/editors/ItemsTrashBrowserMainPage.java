/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;


import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDroppedItem;
import com.amalto.workbench.webservices.WSDroppedItemPK;
import com.amalto.workbench.webservices.WSFindAllDroppedItemsPKs;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSLoadDroppedItem;
import com.amalto.workbench.webservices.WSRecoverDroppedItem;
import com.amalto.workbench.webservices.WSRemoveDroppedItem;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.WidgetFactory;

public class ItemsTrashBrowserMainPage extends AMainPage implements IXObjectModelListener {

	
	protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	protected Text searchText;
	protected TableViewer resultsViewer; 
	
	protected boolean[] ascending = {true,false,false,false,false};

		
    public ItemsTrashBrowserMainPage(FormEditor editor) {
        super(
        		editor,
        		ItemsTrashBrowserMainPage.class.getName(),
        		"Data Cluster Browser " +((XObjectBrowserInput)editor.getEditorInput()).getName()
        );        
        //listen to events
        ((XObjectBrowserInput)editor.getEditorInput()).addListener(this);
    }

    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {
		//Everything is implemented in createFormContent
	}
    
	@Override
	protected void createFormContent(IManagedForm managedForm) {

        try {
 
        	//sets the title
        	managedForm.getForm().setText(this.getTitle());
        	
        	//get the toolkit
        	FormToolkit toolkit = managedForm.getToolkit();
        	
        	//get the body
        	Composite composite = managedForm.getForm().getBody();
        	composite.setLayout(new GridLayout(9,false));
        	
            
            /***
             * Search Text
             */
            Label descriptionLabel = toolkit.createLabel(composite, "Keywords", SWT.NULL);
            descriptionLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            searchText = toolkit.createText(composite, "*",SWT.BORDER|SWT.SINGLE);
            searchText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,false,2,1)
            );
            //searchText.addModifyListener(this);
            searchText.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(true));
        					}
            			}//keyReleased
            		}//keyListener
            );
            
            //search
        	Button bSearch = toolkit.createButton(composite, "", SWT.CENTER);
        	bSearch.setImage(ImageCache.getCreatedImage(EImage.SEARCH.getPath()));
        	bSearch.setToolTipText("Search");
            bSearch.setLayoutData(
                    new GridData(SWT.NONE,SWT.CENTER,false,false,1,1)
            );
            bSearch.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
					ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(true));
            	};
            }); 
            
            /***
             * Create Table
             */
            final Table table = createTable(composite);
            
            resultsViewer = new TableViewer(table);
            
            resultsViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,9,1)
            );
            ((GridData)resultsViewer.getControl().getLayoutData()).heightHint=500;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new ClusterTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {
            	public void doubleClick(DoubleClickEvent event) {
            		resultsViewer.setSelection(event.getSelection());
            		try {
            			new DisplayDroppedItemAction(
            					ItemsTrashBrowserMainPage.this.getSite().getShell(),
            					resultsViewer
            			).run();
            		} catch (Exception e) {
            			MessageDialog.openError(
            					ItemsTrashBrowserMainPage.this.getSite().getShell(), 
            					"Error", 
            					"Unable to display the element as a tree:\n"+
            					e.getClass().getName()+": "+e.getLocalizedMessage()
            			);
            		}
	            }
            });
            
            hookKeyboard();
               
    		hookContextMenu();
    		
    		managedForm.reflow(true); //nothng will show on the form if not called
    		
    		searchText.setFocus();
            //adapt body add mouse/focus listener for child
    		WidgetFactory factory=new WidgetFactory();
    		factory.adapt(managedForm.getForm().getBody());
        } catch (Exception e) {
            e.printStackTrace();
        }	
	}//createFormContent
	
	
	/**
	 * Create the Table
	 */
	private Table createTable(Composite parent) {
		int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | 
					SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		final Table table = new Table(parent, style);
		
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 5;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column 
		final TableColumn column = new TableColumn(table, SWT.LEFT, 0);		
		column.setText("Data Cluster");
		column.setWidth(150);
		column.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[0] = ! ascending[0];
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0,ascending[0]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[0] = ! ascending[0];
				table.setSortColumn(column);
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0,ascending[0]));
				if(ascending[0]){
					
					table.setSortDirection(SWT.UP);
				}
				else{
					table.setSortDirection(SWT.DOWN);
				}
			}
		});

		// 2nd column
		final TableColumn column1 = new TableColumn(table, SWT.LEFT, 1);
		column1.setText("Concept");
		column1.setWidth(150);
		// Add listener to column so tasks are sorted by description when clicked 
		column1.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[1] = ! ascending[1];
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1,ascending[1]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[1] = ! ascending[1];
				table.setSortColumn(column1);
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1,ascending[1]));
				if(ascending[1]){
					
					table.setSortDirection(SWT.UP);
				}
				else{
					table.setSortDirection(SWT.DOWN);
				}
			}
		});

		
		// 3rd column
		final TableColumn column2 = new TableColumn(table, SWT.LEFT, 2);
		column2.setText("Keys");
		column2.setWidth(150);
		// Add listener to column so tasks are sorted by description when clicked 
		column2.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[2] = ! ascending[2];
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2,ascending[2]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[2] = ! ascending[2];
				table.setSortColumn(column2);
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2,ascending[2]));
				if(ascending[2]){
					table.setSortDirection(SWT.UP);
				}
				else{
					
					table.setSortDirection(SWT.DOWN);
				}
			}
		});
		
		// 4th column
		final TableColumn column3 = new TableColumn(table, SWT.LEFT, 3);
		column3.setText("Revision ID");
		column3.setWidth(150);
		// Add listener to column so tasks are sorted by description when clicked 
		column3.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				table.setSortDirection(SWT.UP);
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(3,ascending[3]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[3] = ! ascending[3];
				table.setSortColumn(column3);
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(3,ascending[3]));
				if(ascending[3]){
					table.setSortDirection(SWT.UP);
				}
				else{
					table.setSortDirection(SWT.DOWN);
				}
			}
		});
		
		// 5th column
		final TableColumn column4 = new TableColumn(table, SWT.LEFT, 4);
		column4.setText("Part Path");
		column4.setWidth(150);
		// Add listener to column so tasks are sorted by description when clicked 
		column4.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[4] = ! ascending[4];
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(4,ascending[4]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[4] = ! ascending[4];
				table.setSortColumn(column4);
				ItemsTrashBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(4,ascending[4]));
				if(ascending[4]){
				
					table.setSortDirection(SWT.UP);
				}
				else{
					table.setSortDirection(SWT.DOWN);
				}
			}
		});
		
		return table;
	}
	
	@Override
	protected void createActions() {

		
	}
		
	
	/*********************************
	 * IXObjectModelListener interface
	 *********************************/
	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		refreshData();
	}

	
	protected void refreshData() {
	   try {
			
    		searchText.setFocus();
 
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	
	protected void commit() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error comtiting the page", "Error comitting the page: "+e.getLocalizedMessage());
		}    	
	}
		
	
	
	private void hookKeyboard()
	{
		resultsViewer.getControl().addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent e) {}
        	public void keyReleased(KeyEvent e) {
	        		if (e.keyCode == SWT.DEL){
	        			new RemoveAction(
								ItemsTrashBrowserMainPage.this.getSite().getShell(),
								ItemsTrashBrowserMainPage.this.resultsViewer
						).run();
	        		}
        		}
        	});
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				//ItemsTrashBrowserMainPage.this.fillContextMenu(manager);
				manager.add(
						new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS)
				);
				
				manager.appendToGroup(
						IWorkbenchActionConstants.MB_ADDITIONS,
						new RestoreAction(
								ItemsTrashBrowserMainPage.this.getSite().getShell(),
								ItemsTrashBrowserMainPage.this.resultsViewer
						)
				);
				
				manager.appendToGroup(
						IWorkbenchActionConstants.MB_ADDITIONS,
						new RemoveAction(
								ItemsTrashBrowserMainPage.this.getSite().getShell(),
								ItemsTrashBrowserMainPage.this.resultsViewer
						)
				);
				
				
			}
		});
		Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
		resultsViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, resultsViewer);
	}

	protected void fillContextMenu(IMenuManager manager) {
		
		return;
		
	}
	

	
	protected LineItem[] getResults(boolean showResultInfo) {
		
		Cursor waitCursor=null;		
		
		try {
			
			Display display = getEditor().getSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay();
			waitCursor = new Cursor(display,SWT.CURSOR_WAIT);
			this.getSite().getShell().setCursor(waitCursor);
			
	 		XtentisPort port = Util.getPort(getXObject());
            
            String search = searchText.getText();
            
            WSDroppedItemPK[] results=null;
            if(search!=null&&search.length()>0){
            	results =port.findAllDroppedItemsPKs(new WSFindAllDroppedItemsPKs(search)).getWsDroppedItemPK();
            }
            
            if((results==null)||(results.length==0)){
            	if (showResultInfo) {
                	MessageDialog.openInformation(this.getSite().getShell(), "Info", "Sorry, no result. ");
                	return new LineItem[0];
                }
            }else{
            	LineItem[] res = new LineItem[results.length];
    	 		for (int i = 0; i < results.length; i++) {
    	 			WSDroppedItemPK wsDroppedItemPK=results[i];
    	 			WSItemPK refWSItemPK=wsDroppedItemPK.getWsItemPK();
    	 			
    	 			String revison=wsDroppedItemPK.getRevisionId();
    	 			//if(revison==null||revison.equals(""))revison="head";
    	 				
    	 			res[i] = new LineItem(
    	 					refWSItemPK.getWsDataClusterPK().getPk(),
    	 					refWSItemPK.getConceptName(),
    	 					refWSItemPK.getIds(),
    	 					revison,
    	 					wsDroppedItemPK.getPartPath()
    	 			);
    			}
    	 		return res;
            }
           
            return new LineItem[0];
		} catch (Exception e) {
			e.printStackTrace();
			if ((e.getLocalizedMessage()!=null) && e.getLocalizedMessage().contains("10000"))
				MessageDialog.openError(this.getSite().getShell(), "Too Many Results", "More than 10000 results returned by the search. \nPlease narrow your search.");
			else
				MessageDialog.openError(this.getSite().getShell(), "Unable to perform the search", e.getLocalizedMessage());
			return null;
		} finally {
			try {
				this.getSite().getShell().setCursor(null);
				waitCursor.dispose();} catch (Exception e) {}
		}
		
		
	}
	
	/***************************************************************
	 * Display Dropped Item Action
	 *
	 ***************************************************************/
	class DisplayDroppedItemAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public DisplayDroppedItemAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/edit_obj.gif"));
			setText("Display Dropped Item");
			setToolTipText("View a DOM Tree of the XML source");
		}
		
		public void run() {
			try {
				super.run();
				
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				LineItem li = (LineItem) selection.getFirstElement();
				
				WSDroppedItem wsDroppedItem = 
				Util.getPort(getXObject()).loadDroppedItem(
						new WSLoadDroppedItem(
								new WSDroppedItemPK(
										new WSItemPK(
										new WSDataClusterPK(li.getDataCluster()),
										li.getConcept(),
										li.getIds()
								),
								li.getPartPath(),
								li.getRevision())
								
						)
				);
				
				//mask
				String projection=wsDroppedItem.getProjection();
				Pattern pLoad = Pattern.compile(".*?(<c>.*?</t>).*?(<p>(.*)</p>|<p/>).*",Pattern.DOTALL);
				Matcher m = pLoad.matcher(projection);
				if(m.matches()){
					if(m.group(2)==null||m.group(2).equals("<p/>")){
						projection="";
	            	}else{
	            		projection=m.group(3);
	            	}
				}
				
				String userName=wsDroppedItem.getInsertionUserName()==null?"undefine":wsDroppedItem.getInsertionUserName();
				String droppedTime=wsDroppedItem.getInsertionTime()==null?"undefine":sdf.format(wsDroppedItem.getInsertionTime());
				String desc="This item was dropped by "+userName+" on "+droppedTime;
				
        		final DOMViewDialog d =  new DOMViewDialog(
        				ItemsTrashBrowserMainPage.this.getSite().getShell(),
        				Util.parse(projection),
        				false,
        				null,
        				DOMViewDialog.TREE_VIEWER,
        				null,
        				desc
        		);
        		
        		d.addListener(new Listener() {
        			public void handleEvent(Event event) {
        				if (event.button == DOMViewDialog.BUTTON_CLOSE) {
        					d.close();
        				}//if
        				
        			}//handleEvent
        		});
        		
        		d.setBlockOnOpen(true);
        		d.open();
        		
	       
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(
						shell,
						"Error", 
						"An error occured trying to view the result as a DOM tree: "+e.getLocalizedMessage()
				);
			}		
		}
		public void runWithEvent(Event event) {
			super.runWithEvent(event);
		}

	}
	
	/***************************************************************
	 * Restore Dropped Item Action
	 *
	 ***************************************************************/
	class RestoreAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public RestoreAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/add_obj.gif"));
			setText("Restore the dropped item");
			setToolTipText("Restore the dropped item");
		}
		
		public void run() {
			try {
				super.run();
				
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				LineItem li = (LineItem) selection.getFirstElement();
				
				if(li==null)return;
				
				WSDroppedItemPK wsDroppedItemPK = new WSDroppedItemPK(
						new WSItemPK(
								new WSDataClusterPK(li.getDataCluster()),
								li.getConcept(),
								li.getIds()
						),
						li.getPartPath(),
						li.getRevision()
						);
				
				
				
				Util.getPort(getXObject()).recoverDroppedItem(new WSRecoverDroppedItem(wsDroppedItemPK));
				
				//TODO response status deal
				
				//refresh the search
				ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(false));
	       
			} catch (Exception e) {
				//e.printStackTrace();
				MessageDialog.openError(
						shell,
						"Error", 
						"An error occured trying to restore the selected dropped item:\n\n"+e.getLocalizedMessage()
				);
			}		
		}
		public void runWithEvent(Event event) {
			super.runWithEvent(event);
		}

	}
	
	
	/***************************************************************
	 * Delete Dropped Item Action
	 *
	 ***************************************************************/
	class RemoveAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public RemoveAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/delete_obj.gif"));
			IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
			setText("Remove the dropped item");
			setToolTipText("Remove the dropped item");
		}
		
		public void run() {
			try {
				super.run();
								
				//retrieve the item
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
                LineItem li = (LineItem) selection.getFirstElement(); 
				if(li==null)return;

				if (! MessageDialog.openConfirm(
						this.shell, 
						"Confirm Deletion", 
						"Are you sure you want to remove the selected one ?")
					)	return;

				WSDroppedItemPK wsDroppedItemPK = new WSDroppedItemPK(
						new WSItemPK(
								new WSDataClusterPK(li.getDataCluster()),
								li.getConcept(),
								li.getIds()
						),
						li.getPartPath(),
						li.getRevision()
						);
				//run
				Util.getPort(getXObject()).removeDroppedItem(new WSRemoveDroppedItem(wsDroppedItemPK));
				//refresh the search
				ItemsTrashBrowserMainPage.this.resultsViewer.setInput(getResults(false));
	       
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(
						shell,
						"Error", 
						"An error occured trying to delete the items: "+e.getLocalizedMessage()
				);
			}		
		}
		public void runWithEvent(Event event) {
			super.runWithEvent(event);
		}

	}
	
	
	
	
	
	/***************************************************************
	 * Table Label Provider
	 *
	 ***************************************************************/
	class ClusterTableLabelProvider implements  ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			LineItem li = (LineItem) element;
			switch (columnIndex) {
			case 0:
				return li.getDataCluster();
			case 1:
				return li.getConcept();
			case 2:
				return Util.joinStrings(li.getIds(), ".");
			case 3:
				return li.getRevision();
			case 4:
				return li.getPartPath();	
			default:
				return "???????";
			}
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

	}
	
	
	/***************************************************************
	 * Table Sorter
	 *
	 ***************************************************************/
	class TableSorter extends ViewerSorter {
		
		int column=0;
		boolean asc = true;
				
		public TableSorter(int column, boolean ascending) {
			super();
			this.column = column;
			this.asc = ascending;
		}

		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			LineItem li1 = (LineItem) e1;
			LineItem li2 = (LineItem) e2;
			
			int res=0;
			
			switch (column) {
				
				case 0:
					res = li1.getDataCluster().compareToIgnoreCase(li2.getDataCluster());
					break;	
				case 1:
					res = li1.getConcept().compareToIgnoreCase(li2.getConcept());
					break;
				case 2:
					res = Util.joinStrings(li1.getIds(), ".").compareToIgnoreCase(Util.joinStrings(li2.getIds(), "."));
					break;
				case 3:
					res = li1.getRevision().compareToIgnoreCase(li2.getRevision());
					break;
				case 4:
					res = li1.getPartPath().compareToIgnoreCase(li2.getPartPath());
					break;	
				default:
					res=0;
			}
			if (asc) 
				return res;
			else
				return -res;
		}
		
		
	}
	
	
	
	/***************************************************************
	 * A Line Item Bean
	 *
	 ***************************************************************/
	class LineItem {
		
		private String dataCluster;
		private String concept;
		private String[] ids;
		private String revision;
		private String partPath;
		
		public LineItem(String dataCluster, String concept, String[] ids, String revision, String partPath) {
			super();
			this.concept = concept;
			this.dataCluster = dataCluster;
			this.ids = ids;
			this.partPath = partPath;
			this.revision = revision;
		}

		public String getDataCluster() {
			return dataCluster;
		}

		public void setDataCluster(String dataCluster) {
			this.dataCluster = dataCluster;
		}

		public String getConcept() {
			return concept;
		}

		public void setConcept(String concept) {
			this.concept = concept;
		}

		public String[] getIds() {
			return ids;
		}

		public void setIds(String[] ids) {
			this.ids = ids;
		}

		public String getRevision() {
			return revision;
		}

		public void setRevision(String revision) {
			this.revision = revision;
		}

		public String getPartPath() {
			return partPath;
		}

		public void setPartPath(String partPath) {
			this.partPath = partPath;
		}
		
	}



	


}
