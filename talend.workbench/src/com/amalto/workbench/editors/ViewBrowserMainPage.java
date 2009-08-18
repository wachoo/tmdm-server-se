/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import java.net.URL;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSQuickSearch;
import com.amalto.workbench.webservices.WSStringPredicate;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.webservices.WSWhereOperator;
import com.amalto.workbench.webservices.XtentisPort;

public class ViewBrowserMainPage extends AMainPage implements IXObjectModelListener {

//	private boolean refreshing;
	
	protected Combo dataClusterCombo;
	protected Text searchText;
	protected TableViewer resultsViewer; 
	protected List viewableBEsList;
	protected List searchableBEsList;
	protected ListViewer wcListViewer; 
	protected Label resultsLabel;
	
    public ViewBrowserMainPage(FormEditor editor) {
        super(
        		editor,
        		ViewBrowserMainPage.class.getName(),
        		"View Browser " +((XObjectBrowserInput)editor.getEditorInput()).getName()
        );        
        //listen to events
        ((XObjectBrowserInput)editor.getEditorInput()).addListener(this);
    }

	protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {

        try {
            
            Label vbeLabel = toolkit.createLabel(charComposite, "Viewable Elements", SWT.NULL);
            vbeLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );

            Label sbeLabel = toolkit.createLabel(charComposite, "Searchable Elements", SWT.NULL);
            sbeLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            
            viewableBEsList = new List(charComposite,SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
            viewableBEsList.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            ((GridData)viewableBEsList.getLayoutData()).heightHint = 100;
            
            
            searchableBEsList = new List(charComposite,SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
            searchableBEsList.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            ((GridData)searchableBEsList.getLayoutData()).heightHint = 100;
            
   		
            Label wcLabel = toolkit.createLabel(charComposite, "Conditions", SWT.NULL);
            wcLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,2,1)
            );
            wcListViewer = new ListViewer(charComposite,SWT.BORDER);
            wcListViewer.getControl().setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
            );
            ((GridData)wcListViewer.getControl().getLayoutData()).minimumHeight = 100;
            wcListViewer.setContentProvider(new IStructuredContentProvider() {
				public void dispose() {}
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
				public Object[] getElements(Object inputElement) {
					return 
						((WSView) inputElement).getWhereConditions() == null ? 
								new WSWhereCondition[0] : 
								((WSView) inputElement).getWhereConditions();
				}
            });
            wcListViewer.setLabelProvider(new ILabelProvider() {
				public Image getImage(Object element) {return null;}
				public String getText(Object element) {
					WSWhereCondition wc = (WSWhereCondition) element;
					String text = wc.getLeftPath()+" ";
					if (wc.getOperator().equals(WSWhereOperator.CONTAINS)) text+="Contains";
					else if (wc.getOperator().equals(WSWhereOperator.EQUALS)) text+="=";
					else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN)) text+=">";
					else if (wc.getOperator().equals(WSWhereOperator.GREATER_THAN_OR_EQUAL)) text+=">=";
					else if (wc.getOperator().equals(WSWhereOperator.JOIN)) text+="Contains Text Of";
					else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN)) text+="<";
					else if (wc.getOperator().equals(WSWhereOperator.LOWER_THAN_OR_EQUAL)) text+="<=";
					else if (wc.getOperator().equals(WSWhereOperator.NOT_EQUALS)) text+="!=";
					else if (wc.getOperator().equals(WSWhereOperator.STARTSWITH)) text+="Starts With";
					else if (wc.getOperator().equals(WSWhereOperator.STRICTCONTAINS)) text+="Strict Contains";
					text+=" ";
					if (!wc.getOperator().equals(WSWhereOperator.JOIN)) text+="\"";
					text+=wc.getRightValueOrPath();
					if (!wc.getOperator().equals(WSWhereOperator.JOIN)) text+="\"";
					text+=" ";
					if (wc.getStringPredicate().equals(WSStringPredicate.AND)) text+="[and]";
					else if (wc.getStringPredicate().equals(WSStringPredicate.EXACTLY)) text+="[exactly]";
					else if (wc.getStringPredicate().equals(WSStringPredicate.NONE)) text+="";
					else if (wc.getStringPredicate().equals(WSStringPredicate.NOT)) text+="[not]";
					else if (wc.getStringPredicate().equals(WSStringPredicate.OR)) text+="[or]";
					else if (wc.getStringPredicate().equals(WSStringPredicate.STRICTAND)) text+="[strict and]";
					return text;
				}
				public void addListener(ILabelProviderListener listener) {}
				public void dispose() {}
				public boolean isLabelProperty(Object element, String property) {return false;}
				public void removeListener(ILabelProviderListener listener) {}
            });
            
            
            
            Composite resultsGroup = this.getNewSectionComposite("Search and results");
            resultsGroup.setLayout(new GridLayout(4, false));
            
            /***
             * Search Text
             */
            Label descriptionLabel = toolkit.createLabel(resultsGroup, "Search", SWT.NULL);
            descriptionLabel.setLayoutData(
                    new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1)
            );
            
            dataClusterCombo = new Combo(resultsGroup,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
            dataClusterCombo.setLayoutData(
                    new GridData(SWT.LEFT,SWT.CENTER,false,false,1,1)
            );
            ((GridData)dataClusterCombo.getLayoutData()).minimumWidth = 100; 
            
            searchText = toolkit.createText(resultsGroup, "",SWT.BORDER|SWT.SINGLE);
            searchText.setLayoutData(    
                    new GridData(SWT.FILL,SWT.CENTER,true,false,1,1)
            );
            //((GridData)searchText.getLayoutData()).minimumWidth = 200;
//         searchText.addModifyListener(this);
            searchText.addKeyListener(
            		new KeyListener() {
            			public void keyPressed(KeyEvent e) {}
            			public void keyReleased(KeyEvent e) {
        					if ((e.stateMask==0) && (e.character == SWT.CR)) {
        						ViewBrowserMainPage.this.resultsViewer.setInput(getResults());
        					}
            			}//keyReleased
            		}//keyListener
            );
            
        	Button bSearch = toolkit.createButton(resultsGroup, "Search", SWT.CENTER);
            bSearch.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
            );
            bSearch.addListener(SWT.Selection, new Listener() {
                public void handleEvent(Event event) {
                	ViewBrowserMainPage.this.resultsViewer.setInput(getResults());
            	};
            });    

            
            
            resultsLabel = toolkit.createLabel(resultsGroup, "Search", SWT.NULL);
            resultsLabel.setLayoutData(
                    new GridData(SWT.LEFT,SWT.CENTER,false,false,4,1)
            );
            resultsLabel.setText("                                          ");
            
            resultsViewer = new TableViewer(resultsGroup);
            resultsViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
            );
            ((GridData)resultsViewer.getControl().getLayoutData()).heightHint=500;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new XMLTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {
            	public void doubleClick(DoubleClickEvent event) {
            		resultsViewer.setSelection(event.getSelection());
            		try {
            			new DOMViewAction(
            					ViewBrowserMainPage.this.getSite().getShell(),
            					resultsViewer
            			).run();
            		} catch (Exception e) {
            			MessageDialog.openError(
            					ViewBrowserMainPage.this.getSite().getShell(), 
            					"Error", 
            					"Unable to display the element as a tree:\n"+
            					e.getClass().getName()+": "+e.getLocalizedMessage()
            			);
            		}
	            }
            });
               
    		hookContextMenu();
    		    		
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//createCharacteristicsContent
	
	
	protected void refreshData() {
		try {

			if(viewableBEsList.isDisposed()||searchableBEsList.isDisposed()||wcListViewer.getList().isDisposed())return;
            //if (! this.String xml = (String)((IStructuredSelection)event.getSelection()).getFirstElement();equals(getEditor().getActivePageInstance())) return;
            
			WSView view = null;
			if (getXObject().getWsObject() == null) { //then fetch from server			
	    		XtentisPort port = Util.getPort(getXObject());
	    		view = 
	    			port.getView(
	    					new WSGetView(
	    						(WSViewPK)getXObject().getWsKey()
	    					)
	    			);
	    		getXObject().setWsObject(view);
			} else { //it has been opened by an editor - use the object there
				view = (WSView)getXObject().getWsObject();
			}
			
			

    		String paths[] = view.getViewableBusinessElements();
            //Fill the vbe List
            viewableBEsList.removeAll();
            for (int i = 0; i < paths.length; i++) {
    			viewableBEsList.add(paths[i]);
    		}

            paths = view.getSearchableBusinessElements();
            searchableBEsList.removeAll();
            if (paths!=null) {
	            for (int i = 0; i < paths.length; i++) {
	    			searchableBEsList.add(paths[i]);
	    		}
            }
                		
            wcListViewer.setInput(view);
            wcListViewer.refresh();
            
            
            dataClusterCombo.removeAll();
            WSDataClusterPK[] dataClusterPKs = Util.getAllDataClusterPKs(
            		new URL(getXObject().getEndpointAddress()),
            		getXObject().getUniverse(),
            		getXObject().getUsername(),
            		getXObject().getPassword()
            );
            if (
            		(dataClusterPKs == null) || (dataClusterPKs.length == 0)
              	) {
            	MessageDialog.openError(
            			this.getSite().getShell(), 
            			"Error", 
            			"Please create Data Clusters before browsing views");
            	return;
            }
            for (int  i = 0; i < dataClusterPKs.length; i++) {
            	dataClusterCombo.add(dataClusterPKs[i].getPk());
			}
            dataClusterCombo.select(0);
            
            this.getManagedForm().reflow(true);
            
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
		

	protected void createActions() {

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				//ViewBrowserMainPage.this.fillContextMenu(manager);
				manager.add(
						new DOMViewAction(
								ViewBrowserMainPage.this.getSite().getShell(),
								ViewBrowserMainPage.this.resultsViewer
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
	

	public String[] getResults() {
		
		Cursor waitCursor=null;
		
		try {
			
			Display display = getEditor().getSite().getPage().getWorkbenchWindow().getWorkbench().getDisplay();
			waitCursor = new Cursor(display,SWT.CURSOR_WAIT);
			this.getSite().getShell().setCursor(waitCursor);
			
	 		XtentisPort port = Util.getPort(getXObject());
	  		 
	 		String[] results = 
	 		port.quickSearch(
	 				new WSQuickSearch(
	 						new WSDataClusterPK(dataClusterCombo.getText()),
	 						(WSViewPK)getXObject().getWsKey(),
	 						("".equals(searchText.getText()) ? "*" : searchText.getText()),
	 						10, 	//max Items
	 						0,	//skip
	 						Integer.MAX_VALUE,	//spell threshold
	 						true,	//do AND between words
	 						null,
	 						null
	 				)
	 		).getStrings();
	 		
	 		resultsLabel.setText(results.length+" results");
	 		/* DONE in LabelProvider
	 		String[] res = new String[results.length];
	 		for (int i = 0; i < results.length; i++) {
	 			res[i] = results[i].replaceAll("\\s*__h", "").replaceAll("h__\\s*", "");
			}
			*/
	 		return results;
		} catch (Exception e) {
			e.printStackTrace();
			if (	(e.getLocalizedMessage()!=null) &&
					e.getLocalizedMessage().contains("10000")
				)
				MessageDialog.openError(this.getSite().getShell(), "Too Many Results", "More than 10000 results returned by the search. \nPlease narrow your search.");
			else
				MessageDialog.openError(this.getSite().getShell(), "Unable to perform the search", e.getLocalizedMessage());
			return null;
		}  finally {
			try {
				this.getSite().getShell().setCursor(null);
				waitCursor.dispose();} catch (Exception e) {}
		}
	 		
	}
	
	class DOMViewAction extends Action{

		private Shell shell = null;
		private Viewer viewer;
		
		public DOMViewAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/add_obj.gif"));
			setText("View Tree");
			setToolTipText("View as a DOM Tree");
		}
		
		public void run() {
			try {
				super.run();
				
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				String xml = (String) selection.getFirstElement();
				
				//clean up highlights
				xml = xml.replaceAll("\\s*__h", "").replaceAll("h__\\s*", "");
				
        		final DOMViewDialog d =  new DOMViewDialog(
        				ViewBrowserMainPage.this.getSite().getShell(),
        				Util.parse(xml)
        		);
        		d.addListener(new Listener() {
        			public void handleEvent(Event event) {
        				if (event.button == DOMViewDialog.BUTTON_CLOSE) //what else?
        				d.close();
        			}
        		});
        		d.setBlockOnOpen(true);
        		d.open();
        		d.close();
	       
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
	
	
	protected static Pattern highlightLeft = Pattern.compile("\\s*__h");
	protected static Pattern highlightRight = Pattern.compile("h__\\s*");
	protected static Pattern emptyTags = Pattern.compile("\\s*<(.*?)\\/>\\s*");
	protected static Pattern openingTags = Pattern.compile("\\s*<([^\\/].*?[^\\/])>\\s*");
	protected static Pattern closingTags = Pattern.compile("\\s*</(.*?)>\\s*");
	
	class XMLTableLabelProvider implements  ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String xml = (String) element;
			xml =highlightLeft.matcher(xml).replaceAll("");
			xml =highlightRight.matcher(xml).replaceAll("");
			xml =emptyTags.matcher(xml).replaceAll("[$1]");
			xml =openingTags.matcher(xml).replaceAll("[$1: ");
			xml =closingTags.matcher(xml).replaceAll("]");
			if (xml.length()>=150)
				return xml.substring(0, 150)+"...";
			return xml;
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


	
	
	/*********************************
	 * IXObjectModelListener interface
	 */
	public void handleEvent(int type, TreeObject parent, TreeObject child) {
		refreshData();
	}	


}
