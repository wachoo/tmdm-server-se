package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class DOMViewDialog extends Dialog {

	public final static int BUTTON_CLOSE = 10;
	public final static int BUTTON_SAVE = 15;
	public final static int BUTTON_CANCEL = 20;
	
	public final static int TREE_VIEWER = 0;
	public final static int SOURCE_VIEWER = 1;

	protected Node node ;
	protected TreeViewer domViewer;
	protected SourceViewer sourceViewer;
	protected Combo dataModelCombo;
	protected TabFolder tabFolder;
	protected int firstTab = TREE_VIEWER;
	protected Label mcLable=null;
	//protected boolean elementChanged = false;
	
	private int buttonPressed=0;
	private boolean editable = false;
	private Collection<String> dataModelNames;
	private String selectedDataModel;
	
	private String desc;
	
	private Collection<Listener> listeners = new ArrayList<Listener>();

	public DOMViewDialog(Shell parentShell, Node node) {
		this(parentShell,node,false,null,TREE_VIEWER,null);
	}
	
	public DOMViewDialog(
			Shell parentShell, 
			Node node, 
			boolean editable, 
			Collection<String> dataModelNames,
			int firstTab,
			String selectedDataModel,
			String desc
			){
		this(parentShell,node,editable,dataModelNames,firstTab,selectedDataModel);
		this.desc=desc;
	}

	public DOMViewDialog(
			Shell parentShell, 
			Node node, 
			boolean editable, 
			Collection<String> dataModelNames,
			int firstTab,
			String selectedDataModel
			) {
		super(parentShell);
		this.node = node;
		this.editable = editable;
		this.dataModelNames = new ArrayList<String>();
		if (dataModelNames != null) {
			this.dataModelNames.addAll(dataModelNames);
		}
		this.firstTab = firstTab;
		this.selectedDataModel = selectedDataModel;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}
	
	
	protected Control createDialogArea(Composite parent) {
		
		try {
			//Should not really be here but well,....
			if(editable){
				parent.getShell().setText("XML Editor/Viewer");
			}else{
				parent.getShell().setText("XML Viewer");
			}
			
			Composite composite = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
			layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
			layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
			layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
			composite.setLayout(layout);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			applyDialogFont(composite);
			
			if(desc!=null&&desc.length()>0){
			new Label(composite, SWT.NONE).setText(desc);
			}
			
			tabFolder = new TabFolder(composite,SWT.TOP | SWT.H_SCROLL | SWT.V_SCROLL);
			tabFolder.setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
			((GridData)tabFolder.getLayoutData()).heightHint=600;
			((GridData)tabFolder.getLayoutData()).widthHint=600;
			
			tabFolder.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {
					if (tabFolder.getSelectionIndex() == 0) {
						if (node == null) {
							try {
	
								if(sourceViewer==null||sourceViewer.getDocument()==null)return;
								node = Util.parse(sourceViewer.getDocument().get());
								
								
							} catch (Exception ex) {
								ex.printStackTrace();
								tabFolder.setSelection(1);
								MessageDialog.openError(
										DOMViewDialog.this.getShell(), 
										"The XML is not valid", 
										"The XML is not valid:\n\n"+ex.getLocalizedMessage()
								);
								return;
							}
							domViewer.setInput(node);
							domViewer.expandAll();							
						}
					} else if (tabFolder.getSelectionIndex() == 1) {
						try {
							sourceViewer.setDocument(
									new org.eclipse.jface.text.Document(Util.nodeToString(node))
							);
							node=null; //this should be better implemented in a change listener on the text
						} catch (Exception ex) {
							MessageDialog.openError(
									DOMViewDialog.this.getShell(), 
									"An error occured", 
									"Unable to get the document as string:\n\n"+ex.getLocalizedMessage()
							);
							sourceViewer.setDocument(
									new org.eclipse.jface.text.Document("")
							);
						}
					}
				}//widget Selected
			});
				
					
			TabItem tiDOM = new TabItem(tabFolder,SWT.NULL);
			tiDOM.setText("Tree");
			tiDOM.setToolTipText("Display the item as a DOM Tree");
						
    		domViewer = new TreeViewer(tabFolder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
            domViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            //((GridData)domViewer.getControl().getLayoutData()).heightHint=300;
            //((GridData)domViewer.getControl().getLayoutData()).widthHint=300;
    		domViewer.setSorter(null);
    		domViewer.setLabelProvider(new DOMTreeLabelProvider());
    		domViewer.setContentProvider(new DOMTreeContentProvider());
    		domViewer.setInput(node);
    		domViewer.expandAll();
    		domViewer.getControl().setLayoutData(new GridData(
    				SWT.FILL, SWT.FILL, true, true
    		));
    		
    		tiDOM.setControl(domViewer.getControl());
    		
			TabItem tiSource = new TabItem(tabFolder,SWT.NULL);
			tiSource.setText("Source");
			tiSource.setToolTipText("Display the XML source of the item");
    		
    		org.eclipse.jface.text.Document doc = new org.eclipse.jface.text.Document(Util.nodeToString(node));
    		sourceViewer = new SourceViewer(tabFolder,new VerticalRuler(5),SWT.H_SCROLL | SWT.V_SCROLL);
            sourceViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
    		sourceViewer.setDocument(doc);
    		sourceViewer.setEditable(this.editable);
    		/*
    		sourceViewer.addTextListener(
				new ITextListener() {
					public void textChanged(org.eclipse.jface.text.TextEvent event) {
						if ((event.getText()==null) || ("".equals(event.getText()))) return;
						node = null;
						elementChanged = true;
					}
				}
    		);
    		*/
    		tiSource.setControl(sourceViewer.getControl());
    		
			tabFolder.setSelection(firstTab);
			if (firstTab == SOURCE_VIEWER) node = null; //force refresh of tree 
					
			//this.getShell().setSize(600, 600);
			//this.getShell().layout();
			
    		
		    return composite;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					this.getShell(),
					"Error", 
					"An error occured trying to create the DOM Viewer "+e.getLocalizedMessage()
			);
			return null;
		}
		
	}

	

	public int getButtonPressed() {
		return buttonPressed;		
	}
	
	public String getDataModelName() {
		return dataModelCombo.getText();
	}
	
	public Node getNode() {
		return this.node;
	}
	public String getXML() {
		return sourceViewer.getDocument().get();
	}
	
	
	protected void createButtonsForButtonBar(Composite parent) {
		
		if (! editable) {
			createButton(parent, BUTTON_CLOSE, "Close",true);
		} else {
			mcLable = new Label(parent,SWT.RIGHT);
			mcLable.setText("Data Model");
			dataModelCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
			String[] dms = dataModelNames.toArray(new String[dataModelNames.size()]);
			Arrays.sort(dms);
			dataModelCombo.setItems(dms);
			dataModelCombo.select(-1);
			if (selectedDataModel != null) {
				for (int i = 0; i < dms.length; i++) {
					String dm =  dms[i];
					if (dm.equals(selectedDataModel)) {
						dataModelCombo.select(i);
						break;
					}
				}
			}
			
			createButton(parent, BUTTON_SAVE, "Save",false);
			createButton(parent, BUTTON_CANCEL, "Cancel",true);
		}
	}



	protected void buttonPressed(int buttonId) {
		this.buttonPressed = buttonId;
				
		if (buttonId == BUTTON_SAVE) {
			//check that Data Model is not nul
			if (dataModelCombo.getSelectionIndex() == -1) {
				MessageDialog.openError(
						DOMViewDialog.this.getShell(), 
						"No Data Model is selected", 
						"A Data Model must be selected\n\n"
				);
				return;
			}
//			if save and on DOM viewer get the XML String
			if (tabFolder.getSelectionIndex() == 0) {
				try {
					sourceViewer.setDocument(
							new org.eclipse.jface.text.Document(
									Util.nodeToString(node)
							)
					);
				} catch (Exception ex) {
					tabFolder.setSelection(1);
					MessageDialog.openError(
							DOMViewDialog.this.getShell(), 
							"Error Serializing the XML", 
							"The DOM tree could not be serialized to an XML string:\n\n"+ex.getLocalizedMessage()
					);
					return;
				}
			}
		}

		Event e = new Event();
		e.button = buttonId;
		notifyListeners(e);
	}
	

	

	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	public void notifyListeners(Event e) {
		for (Iterator<Listener> iter = listeners.iterator(); iter.hasNext(); ) {
			Listener listener = iter.next();
			listener.handleEvent(e);
		}
	}
	
	
	/**
	 * DOM Tree Content Provider
	 * @author bgrieder
	 *
	 */
	class DOMTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

		public DOMTreeContentProvider( ) {
		}
		
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		public void dispose() {
		}
		
		public Object[] getElements(Object parent) {
			return getChildren(parent);
		}
		public Object getParent(Object child) {
			//if (child instanceof Element) {
				return ((Element)child).getParentNode();
			//}
			//return null;
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof Document) 
				return new Object[] {((Document)parent).getDocumentElement()};
			
			if (parent instanceof Element) {
				Element e = (Element) parent;
				ArrayList<Node> list = new ArrayList<Node>();
				//Attributes
				NamedNodeMap map = e.getAttributes();
				for (int i = 0; i < map.getLength(); i++) {
					list.add(map.item(i));
				}
				//Sub-Elements
				NodeList nl = ((Element)parent).getChildNodes();
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i) instanceof Element)
						list.add(nl.item(i));
				}
				if (list.size() == 0)
					return null;
				else
					return list.toArray(new Node[list.size()]);
			}
			
			return null;
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof Document)
				return true;
			if (parent instanceof Element)
				return (
						((Element)parent).getChildNodes().getLength()+
						((Element)parent).getAttributes().getLength())>1;
			return false;
		}
		

	}
	
	
	
	/**
	 * DOM Tree Label Provider
	 * @author bgrieder
	 *
	 */
	class DOMTreeLabelProvider extends LabelProvider {

		public String getText(Object obj) {
			if (obj instanceof Node) {
				Node e = (Node)obj;
				if (e.getChildNodes().getLength()>1)
					return e.getLocalName();
				else
					return e.getLocalName()+": "+e.getTextContent();
			}
			return "?? "+obj.getClass().getName()+" : "+obj.toString();

		}
		
		public Image getImage(Object obj) {
			if (obj instanceof Element) {
				if (((Element)obj).getChildNodes().getLength()>1)
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
				else
					return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
			}else if (obj instanceof Node) {
				return ImageCache.getImage( "icons/attribute.gif").createImage();
			}			
			return ImageCache.getImage( "icons/small_warn.gif").createImage();
		}
		
	}//Class DOM Tree Label Provider

	
	
	

}
