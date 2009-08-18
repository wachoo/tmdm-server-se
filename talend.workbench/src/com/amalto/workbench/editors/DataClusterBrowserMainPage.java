/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;


import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
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
import org.eclipse.swt.widgets.Combo;
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
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSDeleteItem;
import com.amalto.workbench.webservices.WSDropItem;
import com.amalto.workbench.webservices.WSGetConceptsInDataCluster;
import com.amalto.workbench.webservices.WSGetDataCluster;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSGetItemPKsByCriteria;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSItemPKsByCriteriaResponseResults;
import com.amalto.workbench.webservices.WSPutItem;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.WSRouteItemV2;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniverseItemsRevisionIDs;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.CalendarSelectWidget;
import com.amalto.workbench.widgets.WidgetFactory;

public class DataClusterBrowserMainPage extends AMainPage implements IXObjectModelListener {

//	private boolean refreshing;
	
	protected static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	
	protected Text searchText;
	protected Text fromText;
	protected Text toText;
	protected Combo conceptCombo;
	protected Text keyText;
	protected TableViewer resultsViewer; 
	protected ListViewer wcListViewer; 

	protected boolean[] ascending = {true,false,false};
	private static int DEFAULT_VIEWER_HEIGHT = 500;
	
	//protected String previousDataModel=null;
		
    public DataClusterBrowserMainPage(FormEditor editor) {
        super(
        		editor,
        		DataClusterBrowserMainPage.class.getName(),
        		"Data Cluster Browser " +((XObjectBrowserInput)editor.getEditorInput()).getName()
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
        	FormToolkit toolkit = managedForm.getToolkit();
        	
        	//get the body
        	Composite composite = managedForm.getForm().getBody();
        	composite.setLayout(new GridLayout(9,false));
        	
        	//We do not implement IFormPart: we do not care about lifecycle management

        	
        	//from
        	Label fromLabel = toolkit.createLabel(composite, "From", SWT.NULL);
        	fromLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
        	
            CalendarSelectWidget fromCalendar=new CalendarSelectWidget(toolkit,composite,true);
            fromText=fromCalendar.getText();
            fromText.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(true));
        						readjustViewerHeight();
        					}
            			}//keyReleased
            		}//keyListener
            );
            //to
            Label toLabel = toolkit.createLabel(composite, "To", SWT.NULL);
            toLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            
            CalendarSelectWidget toCalendar=new CalendarSelectWidget(toolkit,composite,false);
            toText=toCalendar.getText();
            toText.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(true));
        					}
            			}//keyReleased
            		}//keyListener
            );


        	Label conceptLabel = toolkit.createLabel(composite, "Concept", SWT.NULL);
            conceptLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            conceptCombo = new Combo(composite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
            conceptCombo.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            ((GridData) conceptCombo.getLayoutData()).widthHint = 180;
            conceptCombo.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(true));
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
					DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(true));
					readjustViewerHeight();
            	};
            });    
            


            /**********
             * Second Line
             */
        	Label keyLabel = toolkit.createLabel(composite, "Keys", SWT.NULL);
            keyLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            keyText = toolkit.createText(composite, "",SWT.BORDER|SWT.SINGLE);
            keyText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.CENTER,false,false,5,1)
            );
            keyText.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(true));
        					}
            			}//keyReleased
            		}//keyListener
            );        	

            
            
            /***
             * Search Text
             */
            Label descriptionLabel = toolkit.createLabel(composite, "Keywords", SWT.NULL);
            descriptionLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,false,1,1)
            );
            searchText = toolkit.createText(composite, "",SWT.BORDER|SWT.SINGLE);
            searchText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,false,2,1)
            );
//         searchText.addModifyListener(this);
            searchText.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(true));
        					}
            			}//keyReleased
            		}//keyListener
            );
            
            
            final Table table = createTable(composite);
            
            resultsViewer = new TableViewer(table);
            
            resultsViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,9,1)
            );
            ((GridData)resultsViewer.getControl().getLayoutData()).heightHint=DEFAULT_VIEWER_HEIGHT;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new ClusterTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {
            	public void doubleClick(DoubleClickEvent event) {
            		resultsViewer.setSelection(event.getSelection());
            		try {
            			new EditItemAction(
            					DataClusterBrowserMainPage.this.getSite().getShell(),
            					resultsViewer
            			).run();
            		} catch (Exception e) {
            			MessageDialog.openError(
            					DataClusterBrowserMainPage.this.getSite().getShell(), 
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
	 *  readjust the viewer height
	 */
	private void readjustViewerHeight()
	{
		LineItem[] items = (LineItem[])resultsViewer.getInput();
		GridData grid = (GridData)resultsViewer.getTable().getLayoutData();
		int actualHeight = 14*items.length;
		if (actualHeight < DEFAULT_VIEWER_HEIGHT)actualHeight = DEFAULT_VIEWER_HEIGHT;
		grid.heightHint = actualHeight;
		resultsViewer.refresh();
		getManagedForm().reflow(true);
	}
	
	/**
	 * Create the Table
	 */
	private Table createTable(Composite parent) {
		int style = SWT.MULTI | SWT.BORDER | SWT.H_SCROLL  |
					SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		final Table table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);		
					
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		
		//		table.setSortDirection(0);
		// 1st column 
		final TableColumn column = new TableColumn(table, SWT.LEFT, 0);	
//		table.setSortColumn(column);
//		table.setSortDirection(SWT.UP);
		column.setText("Date");
		column.setWidth(150);
//		column.setImage(getDefaultImage());//============
		column.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[0] = ! ascending[0];
				DataClusterBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0,ascending[0]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[0] = ! ascending[0];
				DataClusterBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(0,ascending[0]));
				if(ascending[0]){
					table.setSortColumn(column);
					table.setSortDirection(SWT.DOWN);
				}
				else{
					table.setSortColumn(column);
					table.setSortDirection(SWT.UP);
				}
				
					
//					column.setImage(ImageCache.getCreatedImage("icons/view_menu_down.gif"));//============
//				else
//					column.setImage(ImageCache.getCreatedImage("icons/view_menu_up.gif"));//============
			}
		});

		// 2nd column
		final TableColumn column1 = new TableColumn(table, SWT.LEFT, 1);
//		 table.setSortColumn(column1);
		column1.setText("Concept");
		column1.setWidth(150);
		// Add listener to column so tasks are sorted by description when clicked 
		column1.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[1] = ! ascending[1];
				DataClusterBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1,ascending[1]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[1] = ! ascending[1];
				DataClusterBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(1,ascending[1]));
				if(ascending[1]){
					table.setSortColumn(column1);
					table.setSortDirection(SWT.DOWN);
				}
				else{
					table.setSortColumn(column1);
					table.setSortDirection(SWT.UP);
				}
				
			}
		});

		
		// 3rd column
		final TableColumn column2 = new TableColumn(table, SWT.LEFT, 2);
//		table.setSortColumn(column2);
		column2.setText("Keys");
		column2.setWidth(150);
		// Add listener to column so tasks are sorted by description when clicked 
		column2.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[2] = ! ascending[2];
				DataClusterBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2,ascending[2]));
			}
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				ascending[2] = ! ascending[2];
				DataClusterBrowserMainPage.this.resultsViewer.setSorter(new TableSorter(2,ascending[2]));
				if(ascending[2]){
					table.setSortColumn(column2);
					table.setSortDirection(SWT.DOWN);
				}
				else{
					table.setSortColumn(column2);
					table.setSortDirection(SWT.UP);
				}
				
			}
		});
		
		return table;
	}
		
	protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {
		//Everything is implemented in createFormContent
	}//createCharacteristicsContent

	
	protected void refreshData() {
		try {
			if(conceptCombo.isDisposed()) return;
            //if (! this.String xml = (String)((IStructuredSelection)event.getSelection()).getFirstElement();equals(getEditor().getActivePageInstance())) return;
    		XtentisPort port = Util.getPort(getXObject());
            
			WSDataCluster cluster = null;
			if (getXObject().getWsObject() == null) { //then fetch from server			
	    		cluster = 
	    			port.getDataCluster(
	    					new WSGetDataCluster(
	    						(WSDataClusterPK)getXObject().getWsKey()
	    					)
	    			);
	    		getXObject().setWsObject(cluster);
			} else { //it has been opened by an editor - use the object there
				cluster = (WSDataCluster)getXObject().getWsObject();
			}
			
			String[] concepts = port.getConceptsInDataCluster(
					new WSGetConceptsInDataCluster(
							new WSDataClusterPK(cluster.getName())
					)
			).getStrings();
			Arrays.sort(concepts);
			//add revision ID for concepts
			addRevisionID(concepts);
			
			conceptCombo.removeAll();
			conceptCombo.add("*");
			for (int i = 0; i < concepts.length; i++) {
				conceptCombo.add(concepts[i]);
			}
			conceptCombo.select(0);
			
    		searchText.setFocus();
 
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error refreshing the page", "Error refreshing the page: "+e.getLocalizedMessage());
		}    	
	}
	private void addRevisionID(String[] concepts){
		
		WSUniverse wsUniverse=getXObject().getServerRoot().getUser().getWsUuniverse();
		WSUniverseItemsRevisionIDs[] ids=wsUniverse.getItemsRevisionIDs();
		for(int i=0; i<concepts.length; i++){
			boolean isSet=false;
			for(WSUniverseItemsRevisionIDs id: ids){
				if(Pattern.compile(id.getConceptPattern()).matcher(concepts[i]).matches()){
					concepts[i]=concepts[i] + " [" + id.getRevisionID() +"]";
					isSet=true;
					break;
				}
			}
			if(!isSet)concepts[i]=concepts[i] + " [" + IConstants.HEAD +"]";
		}
	}
	protected void commit() {
		try {

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error comtiting the page", "Error comitting the page: "+e.getLocalizedMessage());
		}    	
	}
		

	protected void createActions() {

	}
	
	private void hookKeyboard()
	{
		resultsViewer.getControl().addKeyListener(new KeyListener() {
        	public void keyPressed(KeyEvent e) {}
        	public void keyReleased(KeyEvent e) {
	        		if (e.keyCode == SWT.DEL){
	        			new PhysicalDeleteItemsAction(
								DataClusterBrowserMainPage.this.getSite().getShell(),
								DataClusterBrowserMainPage.this.resultsViewer
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
				//ViewBrowserMainPage.this.fillContextMenu(manager);
				manager.add(
						new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS)
				);
				manager.appendToGroup(
						IWorkbenchActionConstants.MB_ADDITIONS,
						new EditItemAction(
								DataClusterBrowserMainPage.this.getSite().getShell(),
								DataClusterBrowserMainPage.this.resultsViewer
						)
				);
				manager.appendToGroup(
						IWorkbenchActionConstants.MB_ADDITIONS,
						new PhysicalDeleteItemsAction(
								DataClusterBrowserMainPage.this.getSite().getShell(),
								DataClusterBrowserMainPage.this.resultsViewer
						)
				);
				manager.appendToGroup(
						IWorkbenchActionConstants.MB_ADDITIONS,
						new LogicalDeleteItemsAction(
								DataClusterBrowserMainPage.this.getSite().getShell(),
								DataClusterBrowserMainPage.this.resultsViewer
						)
				);
				manager.appendToGroup(
						IWorkbenchActionConstants.MB_ADDITIONS,
						new NewItemAction(
								DataClusterBrowserMainPage.this.getSite().getShell(),
								DataClusterBrowserMainPage.this.resultsViewer
						)
				);
				manager.appendToGroup(
					IWorkbenchActionConstants.MB_ADDITIONS,
					new SubmitItemsAction(
							DataClusterBrowserMainPage.this.getSite().getShell(),
							DataClusterBrowserMainPage.this.resultsViewer
					)
			);
			}
		});
		Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
		resultsViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, resultsViewer);
	}

	protected void fillContextMenu(IMenuManager manager) {
//		IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
		
		return;
	}
	

	
	protected LineItem[] getResults(boolean showResultInfo) {
		
		Cursor waitCursor=null;		
		
		try {
			
			Display display = getEditor().getSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay();
			waitCursor = new Cursor(display,SWT.CURSOR_WAIT);
			this.getSite().getShell().setCursor(waitCursor);
			
	 		XtentisPort port = Util.getPort(getXObject());
   		 
	 		long from = -1;
	 		long to = -1;
	 		
	 		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	 		Pattern pattern = Pattern.compile("^\\d{4}\\d{2}\\d{2} \\d{2}:\\d{2}:\\d{2}$");
	 		
	 		if (! "".equals(fromText.getText())) {
	 			
	 			String dateTimeText=fromText.getText().trim();
	 			Matcher matcher = pattern.matcher(dateTimeText);
	 			if(!matcher.matches()){
	 				MessageDialog.openWarning(this.getSite().getShell(), "Warning", "Time format is illegal! ");
	 				return new LineItem[0];
	 			}
	 			
	            try {
	                Date d = sdf.parse(fromText.getText());
	                from = d.getTime();
	            } catch (ParseException pe) {}
	 		}

	 		if (! "".equals(toText.getText())) {
	 			String dateTimeText=toText.getText().trim();
	 			Matcher matcher = pattern.matcher(dateTimeText);
	 			if(!matcher.matches()){
	 				MessageDialog.openWarning(this.getSite().getShell(), "Warning", "Time format is illegal! ");
	 				return new LineItem[0];
	 			}
	 			
	            try {
	                Date d = sdf.parse(toText.getText());
	                to = d.getTime();
	            } catch (ParseException pe) {}
	 		}

            String concept = conceptCombo.getText();
            if ("*".equals(concept) | "".equals(concept)) concept = null;
            if(concept != null){
            	concept=concept.replaceAll("\\[.*\\]", "").trim();
            }
            String keys = keyText.getText();
            if ("*".equals(keys) | "".equals(keys)) keys = null;
            
            String search = searchText.getText();
            if ("*".equals(search) | "".equals(search)) search = null;

            WSItemPKsByCriteriaResponseResults[] results =
	            port.getItemPKsByCriteria(new WSGetItemPKsByCriteria(
	            		(WSDataClusterPK)getXObject().getWsKey(),
	            		concept,
	            		search,
	            		keys,
	            		from,
	            		to,
	            		0,
	            		Integer.MAX_VALUE
	            	)
	            ).getResults();
            
            if (showResultInfo&&(results==null)) {
            	MessageDialog.openInformation(this.getSite().getShell(), "Info", "Sorry, no result. ");
            	return new LineItem[0];
            }
            if(results==null)return new LineItem[0];
            
            LineItem[] res = new LineItem[results.length];
	 		for (int i = 0; i < results.length; i++) {
	 			res[i] = new LineItem(
	 					results[i].getDate(),
	 					results[i].getWsItemPK().getConceptName(),
	 					results[i].getWsItemPK().getIds()
	 			);
			}
            
	 		
	 		return res;
		} catch (Exception e) {
			e.printStackTrace();
			if (	(e.getLocalizedMessage()!=null) &&
					e.getLocalizedMessage().contains("10000")
				)
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
	
	
	/*********************************
	 * IXObjectModelListener interface
	 */
	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		refreshData();
	}	
	

	
	/***************************************************************
	 * Edit Item Action
	 * @author bgrieder
	 *
	 ***************************************************************/
	class EditItemAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public EditItemAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/edit_obj.gif"));
			setText("Edit Item");
			setToolTipText("View as a DOM Tree or edit the XML source");
		}
		
		public void run() {
			try {
				super.run();
				
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				LineItem li = (LineItem) selection.getFirstElement();
				
				WSItem wsItem=Util.getPort(getXObject()).getItem(
						new WSGetItem(
								new WSItemPK(
										(WSDataClusterPK)getXObject().getWsKey(),
										li.getConcept().trim(),
										li.getIds()
								)
						)
				);
				String xml = wsItem.getContent();
				
				WSDataModelPK[] dmPKs = 
					Util.getPort(getXObject()).getDataModelPKs(
							new WSRegexDataModelPKs("*")
					).getWsDataModelPKs();
				ArrayList<String> dataModels = new ArrayList<String>();
				if (dmPKs != null) {
					for (int i = 0; i < dmPKs.length; i++) {
						if (! "XMLSCHEMA---".equals(dmPKs[i].getPk()))
							dataModels.add(dmPKs[i].getPk());
					}
				}
				
        		final DOMViewDialog d =  new DOMViewDialog(
        				DataClusterBrowserMainPage.this.getSite().getShell(),
        				Util.parse(xml),
        				true,
        				dataModels,
        				DOMViewDialog.TREE_VIEWER,
        				wsItem.getDataModelName()
        		);
        		d.addListener(new Listener() {
        			public void handleEvent(Event event) {
        				if (event.button == DOMViewDialog.BUTTON_SAVE) {
        					//attempt to save
        					try {
	        					Util.getPort(getXObject()).putItem(
	        							new WSPutItem(
	        									(WSDataClusterPK)getXObject().getWsKey(),
	        									d.getXML(),
	        									"".equals(d.getDataModelName()) ? null : new WSDataModelPK(d.getDataModelName())
	        							)
	        					);
	        					//previousDataModel = d.getDataModelName();
        					}catch (Exception e) {
        						MessageDialog.openError(
        								shell,
        								"Error saving the Item", 
        								"An error occured trying save the Item:\n\n "+e.getLocalizedMessage()
        						);
        						return;
        					}
        				}//if
        				d.close();
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
	 * Delete Items Action
	 * @author bgrieder
	 *
	 ***************************************************************/
	
	class LogicalDeleteItemsAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public LogicalDeleteItemsAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/delete_obj.gif"));
			
			IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
			if (selection.size()==1)
				setText("Logically delete the selected item");
			else
				setText("Logically delete these "+selection.size()+" Items");
			
			setToolTipText("Logically delete the Selected Item"+(selection.size()>1? "s":""));
		}
		
		public void run() {
			try {
				super.run();
								
				//retrieve the list of items
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				List<LineItem> lineItems = selection.toList();

				if (lineItems.size()==0) return;

				InputDialog id = new InputDialog(
						this.shell,
	       				"Confirm Deletion",
	       				"Are you sure you want to drop the selected "+lineItems.size()+" items to items-trash?\nSet Part-Path:",
	       				"/",
	       				new IInputValidator() {
	       					public String isValid(String newText) {
	       						if ((newText==null) || !newText.matches("^\\/.*$")) return "Illegal Part-Path";
	       						return null;
	       					};
	       				}
	       		);
	            
	       		id.setBlockOnOpen(true);
	       		int ret = id.open();
	       		if (ret == Dialog.CANCEL) return;
	       		
				//Instantiate the Monitor with actual deletes
	       		LogicalDeleteItemsWithProgress diwp = 
					new LogicalDeleteItemsWithProgress(
							getXObject(),
							lineItems,
							id.getValue(),
							this.shell
					);
				//run
				new ProgressMonitorDialog(this.shell).run(
						false,	//fork 
						true, 	//cancelable
						diwp
				);
				//refresh the search
				DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(false));
	       
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
		
		//Progress Monitor that implements the actual delete
		class LogicalDeleteItemsWithProgress implements IRunnableWithProgress {
			TreeObject xObject;
			Collection<LineItem> lineItems;
			String partPath;
			Shell parentShell;

			public LogicalDeleteItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, String partPath, Shell shell) {
				super();
				this.xObject = object;
				this.lineItems = lineItems;
				this.partPath = partPath;
				this.parentShell = shell;
			}

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask("Deleting items", lineItems.size());
					
					XtentisPort port = Util.getPort(xObject);
				
					
					int i=0;
					for (Iterator<LineItem> iter = lineItems.iterator(); iter.hasNext(); ) {
						LineItem lineItem = iter.next();
						String itemID = ((WSDataClusterPK)xObject.getWsKey()).getPk()+"."+lineItem.getConcept()+"."+Util.joinStrings(lineItem.getIds(), ".");
						monitor.subTask("Processing item "+(i++)+": "+itemID);
						if (monitor.isCanceled())  {
							MessageDialog.openWarning(
									this.parentShell,
									"User canceled the logically delete",
									"The logical-deletes was canceled by the user on item "+i+"\n"+
									"Some items may have not been logically deleted"
							);
							return;
						}
						port.dropItem(
							new WSDropItem(
								new WSItemPK(
									(WSDataClusterPK)xObject.getWsKey(),
									lineItem.getConcept(),
									lineItem.getIds()
								),partPath
							)
						);
						monitor.worked(1);
					}//for
					
					monitor.done();
				} catch (Exception e) {
					//e.printStackTrace();
					MessageDialog.openError(
							shell,
							"Error logically Deleting", 
							"An error occured trying to logically delete the items:\n\n"+e.getLocalizedMessage()
					);
				}//try				
				
			}//run
		}//class DeleteItemsWithProgress

	}//class DeletItemsAction
	
	class PhysicalDeleteItemsAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public PhysicalDeleteItemsAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/delete_obj.gif"));
			IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
			if (selection.size()==1)
				setText("Physically delete the selected item");
			else
				setText("Physically delete these "+selection.size()+" Items");
			 
			setToolTipText("Physically delete the selected Item"+(selection.size()>1? "s":""));
		}
		
		public void run() {
			try {
				super.run();
								
				//retrieve the list of items
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				List<LineItem> lineItems = selection.toList();

				if (lineItems.size()==0) return;
				
				if (! MessageDialog.openConfirm(
						this.shell, 
						"Confirm Deletion", 
						"Are you sure you want to delete the selected "+lineItems.size()+" items?")
					)	return;

				//Instantiate the Monitor with actual deletes
				PhysicalDeleteItemsWithProgress diwp = 
					new PhysicalDeleteItemsWithProgress(
							getXObject(),
							lineItems,
							this.shell
					);
				//run
				new ProgressMonitorDialog(this.shell).run(
						false,	//fork 
						true, 	//cancelable
						diwp
				);
				//refresh the search
				DataClusterBrowserMainPage.this.resultsViewer.setInput(getResults(false));
	       
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
		
		//Progress Monitor that implements the actual delete
		class PhysicalDeleteItemsWithProgress implements IRunnableWithProgress {
			TreeObject xObject;
			Collection<LineItem> lineItems;
			Shell parentShell;

			public PhysicalDeleteItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, Shell shell) {
				super();
				this.xObject = object;
				this.lineItems = lineItems;
				this.parentShell = shell;
			}

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				try {
					monitor.beginTask("Deleting items", lineItems.size());
					
					XtentisPort port = Util.getPort(getXObject());
					
					int i=0;
					for (Iterator<LineItem> iter = lineItems.iterator(); iter.hasNext(); ) {
						LineItem lineItem = iter.next();
						String itemID = ((WSDataClusterPK)getXObject().getWsKey()).getPk()+"."+lineItem.getConcept()+"."+Util.joinStrings(lineItem.getIds(), ".");
						monitor.subTask("Processing item "+(i++)+": "+itemID);
						if (monitor.isCanceled())  {
							MessageDialog.openWarning(
									this.parentShell,
									"User Canceled the delete",
									"The deletes werz canceled by the user on item "+i+"\n"+
									"Some Items may have not been deleted"
							);
							return;
						}
						port.deleteItem(
							new WSDeleteItem(
								new WSItemPK(
									(WSDataClusterPK)getXObject().getWsKey(),
									lineItem.getConcept(),
									lineItem.getIds()
								)
							)
						);
						monitor.worked(1);
					}//for
					
					monitor.done();
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							shell,
							"Error Deleting", 
							"An error occured trying to delete the items:\n\n "+e.getLocalizedMessage()
					);
				}//try				
				
			}//run
		}//class DeleteItemsWithProgress

	}//class DeletItemsAction
	
	
	
	/***************************************************************
	 * New Item Action
	 * @author bgrieder
	 *
	 ***************************************************************/
	class NewItemAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public NewItemAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/add_obj.gif"));
			setText("New Item");
			setToolTipText("Add a new Item");
		}
		
		public void run() {
			try {
				super.run();
				
				//IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				//LineItem li = (LineItem) selection.getFirstElement();
				
				String xml = "<NewItem><NewElement></NewElement></NewItem>";
				
				WSDataModelPK[] dmPKs = 
					Util.getPort(getXObject()).getDataModelPKs(
							new WSRegexDataModelPKs("*")
					).getWsDataModelPKs();
				ArrayList<String> dataModels = new ArrayList<String>();
				if (dmPKs != null) {
					for (int i = 0; i < dmPKs.length; i++) {
						if (! "XMLSCHEMA---".equals(dmPKs[i].getPk()))
							dataModels.add(dmPKs[i].getPk());
					}
				}
				
        		final DOMViewDialog d =  new DOMViewDialog(
        				DataClusterBrowserMainPage.this.getSite().getShell(),
        				Util.parse(xml),
        				true,
        				dataModels,
        				DOMViewDialog.SOURCE_VIEWER,
        				null
        		);
        		d.addListener(new Listener() {
        			public void handleEvent(Event event) {
        				if (event.button == DOMViewDialog.BUTTON_SAVE) {
        					//attempt to save
        					try {
	        					Util.getPort(getXObject()).putItem(
	        							new WSPutItem(
	        									(WSDataClusterPK)getXObject().getWsKey(),
	        									d.getXML(),
	        									"".equals(d.getDataModelName()) ? null : new WSDataModelPK(d.getDataModelName())
	        							)
	        					);
        					}catch (Exception e) {
        						MessageDialog.openError(
        								shell,
        								"Error saving the Item", 
        								"An error occured trying save the Item:\n\n "+e.getLocalizedMessage()
        						);
        						return;
        					}
        				}//if
        				d.close();
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
	 * SubmitItems Action
	 * @author bgrieder
	 *
	 ***************************************************************/
	class SubmitItemsAction extends Action{

		protected Shell shell = null;
		protected Viewer viewer;
		
		public SubmitItemsAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/execute.gif"));
			IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
			if (selection.size()==1)
				setText("Route the selected item");
			else
				setText("Route these "+selection.size()+" Items");
			setToolTipText("Route the Selected Item"+(selection.size()>1? "s":""));
		}
		
		public void run() {
			try {
				super.run();
								
				//retrieve the list of items
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				List<LineItem> lineItems = selection.toList();

				if (lineItems.size()==0) return;
				
				if (! MessageDialog.openConfirm(
						this.shell, 
						"Confirm Deletion", 
						"Are you sure you want to route the selected "+(lineItems.size()>1?lineItems.size()+" ":"")+"item(s) using the Subscription Engine?")
					)	return;

				//Instantiate the Monitor with actual deletes
				SubmitItemsWithProgress diwp = 
					new SubmitItemsWithProgress(
							getXObject(),
							lineItems,
							this.shell
					);
				//run
				new ProgressMonitorDialog(this.shell).run(
						false,	//fork 
						true, 	//cancelable
						diwp
				);
	       
			} catch (Exception e) {
				e.printStackTrace();
				MessageDialog.openError(
						shell,
						"Error", 
						"An error occured trying route the items: "+e.getLocalizedMessage()
				);
			}		
		}
		public void runWithEvent(Event event) {
			super.runWithEvent(event);
		}
		
		//Progress Monitor that implements the actual delete
		class SubmitItemsWithProgress implements IRunnableWithProgress {
			TreeObject xObject;
			Collection<LineItem> lineItems;
			Shell parentShell;

			public SubmitItemsWithProgress(TreeObject object, Collection<LineItem> lineItems, Shell shell) {
				super();
				this.xObject = object;
				this.lineItems = lineItems;
				this.parentShell = shell;
			}

			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				monitor.beginTask("Deleting items", lineItems.size());
				XtentisPort port = null;
				try {
					port = Util.getPort(getXObject());
				} catch (Exception e) {
					e.printStackTrace();
					MessageDialog.openError(
							shell,
							"Error Routing", 
							"An error occured trying to route the items:\n\n "+e.getLocalizedMessage()
					);
				}//try				
					
				int i=0;
				for (Iterator<LineItem> iter = lineItems.iterator(); iter.hasNext(); ) {
					LineItem lineItem = iter.next();
					String itemID = ((WSDataClusterPK)getXObject().getWsKey()).getPk()+"."+lineItem.getConcept()+"."+Util.joinStrings(lineItem.getIds(), ".");
					monitor.subTask("Processing item "+(i++)+" - "+itemID);
					if (monitor.isCanceled())  {
						MessageDialog.openWarning(
								this.parentShell,
								"User Canceled the Routing",
								"The submissions were canceled by the user on item "+i+"\n"+
								"Some Items may have not been routed"
						);
						return;
					}
					try {
						port.routeItemV2(
							new WSRouteItemV2(
								new WSItemPK(
									(WSDataClusterPK)getXObject().getWsKey(),
									lineItem.getConcept(),
									lineItem.getIds()
								)
							)
						);
					} catch (Exception e) {
						e.printStackTrace();
						MessageDialog.openError(
								shell,
								"Error Routing", 
								"An error occured trying to route the item '"+itemID+"':\n\n "+e.getLocalizedMessage()
						);
					}//try		
					monitor.worked(1);
				}//for
				
				monitor.done();
				
			}//run
		}//class DeleteItemsWithProgress

	}//class DeletItemsAction
	
	
	
	/***************************************************************
	 * Table Label Provider
	 * @author bgrieder
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
				return sdf.format(new Date(li.getTime()));
			case 1:
				return li.getConcept();
			case 2:
				return Util.joinStrings(li.getIds(), ".");
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
	 * @author bgrieder
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
					res= (int)( li1.getTime() - li2.getTime());
					break;
				case 1:
					res = li1.getConcept().compareToIgnoreCase(li2.getConcept());
					break;
				case 2:
					res = Util.joinStrings(li1.getIds(), ".").compareToIgnoreCase(Util.joinStrings(li2.getIds(), "."));
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
	 * @author bgrieder
	 *
	 ***************************************************************/
	class LineItem {
		private long time;
		private String concept;
		private String[] ids;
		
		
		public LineItem(long time, String concept, String[] ids) {
			super();
			this.time = time;
			this.concept = concept;
			this.ids = ids;
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
		public long getTime() {
			return time;
		}
		public void setTime(long time) {
			this.time = time;
		}
		
		
	}

}
