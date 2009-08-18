/*
 * Created on 27 oct. 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.amalto.workbench.actions.EditXObjectAction;
import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.dialogs.QueryParametersDialog;
import com.amalto.workbench.dialogs.TextViewDialog;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSRunQuery;
import com.amalto.workbench.webservices.WSStoredProcedure;

public class StoredProcedureMainPage extends AMainPage implements ITextListener{

	protected Text descriptionText;
	protected SourceViewer procedureViewer;
	protected TableViewer resultsViewer;
	protected Combo dataClusterCombo;
	protected Label resultsLabel;
	
	protected LinkedList<String> currentParameters = new LinkedList<String>();
	
	private boolean refreshing = false;
	private boolean comitting = false;
	
    public StoredProcedureMainPage(FormEditor editor) {
        super(
        		editor,
        		StoredProcedureMainPage.class.getName(),
        		"Stored Procedure "+((XObjectEditorInput)editor.getEditorInput()).getName()
        );        
    }

	protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {

        try {
        	
        	WSStoredProcedure wsStoredProcedure = (WSStoredProcedure) (getXObject().getWsObject());


            //description
            Label descriptionLabel = toolkit.createLabel(charComposite, "Description", SWT.NULL);
            descriptionLabel.setLayoutData(
            		new GridData(SWT.BEGINNING,SWT.CENTER,false,false,1,1)
            );
            
            descriptionText = toolkit.createText(charComposite, "",SWT.BORDER);
            descriptionText.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            descriptionText.setText(wsStoredProcedure.getDescription()==null ? "" : wsStoredProcedure.getDescription());
            descriptionText.addModifyListener(this);

            //Procedure
            Group storedProcedureGroup = new Group(charComposite,SWT.SHADOW_NONE);
            storedProcedureGroup.setText("Procedure");
            storedProcedureGroup.setLayout(new GridLayout(1,true));
            storedProcedureGroup.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,2,1)
            );
            ((GridData)storedProcedureGroup.getLayoutData()).minimumHeight = 100;
            procedureViewer = new SourceViewer(storedProcedureGroup, new VerticalRuler(10), SWT.V_SCROLL);
            procedureViewer.getControl().setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            procedureViewer.addTextListener(this);
            WidgetUtils.initRedoUndo(procedureViewer);
            /************************************************************
             * Execute Stored Procedure
             ************************************************************/
            
            
            Composite resultsGroup = this.getNewSectionComposite("Execute Procedure");
            resultsGroup.setLayout(new GridLayout(4, false));
            
            //data cluster
            Hyperlink dataClusterLink = toolkit.createHyperlink(resultsGroup, "Data Cluster",SWT.NULL);
            dataClusterLink.setLayoutData(
                    new GridData(SWT.FILL,SWT.CENTER,false,true,1,1)
            );         
            dataClusterLink.addHyperlinkListener(new IHyperlinkListener() {
            	public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {}
				public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {}
				public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
					TreeParent serverRoot = StoredProcedureMainPage.this.getXObject().getServerRoot();
					TreeObject iaObject = new TreeObject(
							StoredProcedureMainPage.this.dataClusterCombo.getText(),
							serverRoot,
							TreeObject.DATA_CLUSTER,
							new WSDataClusterPK(StoredProcedureMainPage.this.dataClusterCombo.getText()),
							null
					);
					(new EditXObjectAction(iaObject,StoredProcedureMainPage.this.getSite().getPage())).run();
            	};
            });
            dataClusterCombo = new Combo(resultsGroup,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
            dataClusterCombo.setLayoutData(
                    new GridData(SWT.BEGINNING,SWT.NONE,false,false,1,1)
            );
            
            Button executeButton = new Button(resultsGroup,SWT.PUSH);
            executeButton.setText("Execute Procedure");
            executeButton.addMouseListener(new MouseListener() {
            	public void mouseUp(MouseEvent e) {
            		executeProcedure();
            	}
            	public void mouseDoubleClick(MouseEvent e) {}
            	public void mouseDown(MouseEvent e) {}
            });
            
            resultsLabel = toolkit.createLabel(resultsGroup, "                                                                                                           ", SWT.NULL);
            resultsLabel.setLayoutData(
            		new GridData(SWT.BEGINNING,SWT.CENTER,true,false,1,1)
            );

            
            resultsViewer = new TableViewer(resultsGroup);
            resultsViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,4,1)
            );
            ((GridData)resultsViewer.getControl().getLayoutData()).heightHint=300;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new XMLTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {
            	public void doubleClick(DoubleClickEvent event) {
            		//String result = (String)((IStructuredSelection)event.getSelection()).getFirstElement();
            		resultsViewer.setSelection(event.getSelection());
            		new ResultsViewAction(
            				StoredProcedureMainPage.this.getSite().getShell(),
            				resultsViewer
            		).run();
            	}
            });
            
            hookContextMenu();
            
            refreshData();
            
            dataClusterCombo.select(0);
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }//createCharacteristicsContent


	protected void refreshData() {
		if (this.comitting) return;
		
		this.refreshing = true;
		
    	WSStoredProcedure wsStoredProcedure = (WSStoredProcedure) (getXObject().getWsObject());    	
    	String s;
    	
    	s = wsStoredProcedure.getDescription()==null ? "" : wsStoredProcedure.getDescription();
    	if (!s.equals(descriptionText.getText())) descriptionText.setText(s);
    	
    	Document doc = new Document(wsStoredProcedure.getProcedure());
        procedureViewer.setDocument(doc);
        
        dataClusterCombo.removeAll();
        WSDataClusterPK[] dataClusterPKs;
        try {
	        dataClusterPKs = Util.getAllDataClusterPKs(
	        		new URL(getXObject().getEndpointAddress()),
	        		getXObject().getUniverse(),
	        		getXObject().getUsername(),
	        		getXObject().getPassword()
	        );
        } catch (Exception ex) {
        	MessageDialog.openError(
        			StoredProcedureMainPage.this.getSite().getShell(),
        			"Error",
        			"Unable to get the list of data clusters:\n"+
        			ex.getClass().getName()+": "+ex.getLocalizedMessage()
        	);
        	this.refreshing = false;
        	return;
        }
        if (
        		(dataClusterPKs == null) || (dataClusterPKs.length == 0)
        		|| ((dataClusterPKs.length==1) && ("CACHE".equals(dataClusterPKs[0].getPk())))
        	) {
        	MessageDialog.openError(
        			this.getSite().getShell(), 
        			"Error", 
        			"Please create Data Clusters before editing an Inbound Adaptor");
        	return;
        }
        dataClusterCombo.add("[ALL]");
        for (int  i = 0; i < dataClusterPKs.length; i++) {
        	if (!"CACHE".equals(dataClusterPKs[i].getPk()))	//FIXME: hardcoded CACHE 
        		dataClusterCombo.add(dataClusterPKs[i].getPk());
		}
    	
    	this.refreshing = false;
	}
	
	protected void commit() {
		if (this.refreshing) return;
		
		this.comitting = true;
		
    	WSStoredProcedure wsStoredProcedure = (WSStoredProcedure) (getXObject().getWsObject());
		wsStoredProcedure.setDescription(descriptionText.getText());
		wsStoredProcedure.setProcedure(procedureViewer.getDocument().get());
		
		this.comitting = false;
	}

	//no specific actions here
	public void createActions() {
		return;
	}
	
	public void textChanged(TextEvent event) {
		if (this.refreshing) return;
		markDirty();
	}
		
	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager();
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				//ViewBrowserMainPage.this.fillContextMenu(manager);
				manager.add(
						new ResultsViewAction(
								StoredProcedureMainPage.this.getSite().getShell(),
								StoredProcedureMainPage.this.resultsViewer
						)
				);
			}
		});
		Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
		resultsViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, resultsViewer);
	}
	
	protected void executeProcedure() {
		BusyIndicator.showWhile(this.getPartControl().getDisplay(), new Runnable() {
			public void run() {
           		WSDataClusterPK dcpk = null;
        		if (! "[ALL]".equals(dataClusterCombo.getText())) 
        			dcpk = new WSDataClusterPK(dataClusterCombo.getText());
        		try {
        			String proc = procedureViewer.getDocument().get();
        			//read parameters
        			int number=0;
        			
        			while(true) {
        				Pattern p = Pattern.compile(".*[^\\\\]%"+number+"[^\\d].*", Pattern.DOTALL);
        				Matcher m = p.matcher(proc);
        				if (! m.matches()) 
        					break;
        				else
        					++number;
        			}
        			String[] ps = null;
        			if (number>0) {
            			//transfer current parameters to new array
            			ps = new String[number];
            			for (int i = 0; i < number; i++) {
							if (i<currentParameters.size()) 
								ps[i] = currentParameters.get(i);
							else
								ps[i] = "";
            			}
            			//call parameters window
            			QueryParametersDialog dialog = 
            				new QueryParametersDialog(
            					StoredProcedureMainPage.this.getSite().getShell(),
            					ps
            				);
            			dialog.setBlockOnOpen(true);
            			dialog.open();
            			if (dialog.getButtonPressed() == QueryParametersDialog.BUTTON_CANCEL) return;
            			ps = dialog.getParameters();
            			//Apply parameters
            			for (int i = 0; i < ps.length; i++) {
//            				transfer parameters back into current parameters
							if (i<currentParameters.size()) 
								currentParameters.set(i, ps[i]);
							else
								currentParameters.add(ps[i]);
							//replace parameters
//							proc=proc.replaceAll("([^\\\\])%"+i+"([^\\d])", "$1"+ps[i]+"$2");
						}
        			}
        			//perform call
            		String[] results = 
            		Util.getPort(getXObject()).runQuery(
            				new WSRunQuery(
            					null,
            					dcpk,
            					proc,
            					currentParameters.toArray(new String[currentParameters.size()])
            				)
            		).getStrings();
            		resultsLabel.setText("Procedure returned "+results.length+" items.");
            		resultsViewer.setInput(results);
        		} catch (Exception ex) {
        			MessageDialog.openError(
        					StoredProcedureMainPage.this.getSite().getShell(),
        					"Error",
        					"Unable to execute the procedure:\n"+
        					ex.getClass().getName()+": "+ex.getMessage()
        					);
        		}
			}
		});
	}

	
	class ResultsViewAction extends Action{

		private Shell shell = null;
		private Viewer viewer;
		
		public ResultsViewAction(Shell shell, Viewer viewer) {
			super();
			this.shell = shell;
			this.viewer = viewer;
			setImageDescriptor(ImageCache.getImage( "icons/add_obj.gif"));
			setText("Details");
			setToolTipText("View in Details");
		}
		
		public void run() {
			try {
				super.run();
				
				IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
				String result = (String) selection.getFirstElement();
				//clean up highlights
				result = result.replaceAll("\\s*__h", "").replaceAll("h__\\s*", "");
	            
				//try to parse it
				try {
					final DOMViewDialog d = new DOMViewDialog(
							shell, 
							Util.parse(result)
					);
					d.setBlockOnOpen(true);
					d.addListener(new Listener() {
						public void handleEvent(Event event) {
							if (event.button == DOMViewDialog.BUTTON_CLOSE)
								d.close();
						}
					});
					d.open();
				} catch (Exception e) {
					//not an XML
					Dialog d = new TextViewDialog(
							shell, 
							result
					);
					d.setBlockOnOpen(true);
					d.open();
				}
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



}
