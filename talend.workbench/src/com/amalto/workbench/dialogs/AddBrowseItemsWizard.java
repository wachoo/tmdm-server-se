package com.amalto.workbench.dialogs;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDXPathDefinition;

import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.models.IXObjectModelListener;
import com.amalto.workbench.models.KeyValue;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSDeleteView;
import com.amalto.workbench.webservices.WSGetRole;
import com.amalto.workbench.webservices.WSGetView;
import com.amalto.workbench.webservices.WSPutRole;
import com.amalto.workbench.webservices.WSPutView;
import com.amalto.workbench.webservices.WSRole;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoleSpecification;
import com.amalto.workbench.webservices.WSRoleSpecificationInstance;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.ComplexTableViewer;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;

public class AddBrowseItemsWizard extends Wizard{

	private AMainPageV2 page;
	private XtentisPort port;
	private List<XSDElementDeclaration> declList = null;
	private Map<String, List<Line>>  browseItemToRoles = new HashMap<String , List<Line>>();
	private static String INSTANCE_NAME = "Browse Item View";
	private static String BROWSE_ITEMS = "Browse_items_";
	
	private static ComplexTableViewerColumn[] roleConfigurationColumns= new ComplexTableViewerColumn[]{
		new ComplexTableViewerColumn("Role Name", false, "", "", "",ComplexTableViewerColumn.COMBO_STYLE,new String[] {},0),
		new ComplexTableViewerColumn("Access", false, "", "", "",ComplexTableViewerColumn.COMBO_STYLE,new String[] {},0)
		};
	
	public AddBrowseItemsWizard(AMainPageV2 launchPage, List<XSDElementDeclaration> list) {
		super();
		setWindowTitle("Generate default Browse items views");
		page = launchPage;
		declList = list;
		for (XSDElementDeclaration dl : declList) {
			browseItemToRoles.put(BROWSE_ITEMS + dl.getName(), new ArrayList<Line>());
		}
	}
	
	public void addPages() {
	    addPage(new ConfigureRolePage());
	  }
	
	public boolean performFinish() {
		if (saveConfiguration()) {
			page.getXObject().fireEvent(IXObjectModelListener.NEED_REFRESH,
					null, page.getXObject().getParent().getParent());
			return true;
		}
		
		return false;
	}
	
    
    private XtentisPort getXtentisPort()
    {
		try {
			if (port == null) {
				port = Util
						.getPort(
								new URL(page.getXObject().getEndpointAddress()),
								page.getXObject().getUniverse(), page
										.getXObject().getUsername(), page
										.getXObject().getPassword());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return port;
    }
    
    private void newBrowseItemView(String browseItem) throws RemoteException
    {
    	for (XSDElementDeclaration decl: declList)
    	{
    		String fullName = BROWSE_ITEMS + decl.getName();
    		if (fullName.equals(browseItem))
    		{
    			XtentisPort port = getXtentisPort();
    			WSView view = new WSView();
    			WSPutView wrap = new WSPutView();
    			view.setName(browseItem);
            	EList<XSDIdentityConstraintDefinition> idtylist = decl.getIdentityConstraintDefinitions();
            	List<String> keys = new ArrayList<String>();
            	for (XSDIdentityConstraintDefinition idty : idtylist) {
            		 EList<XSDXPathDefinition> xpathList = idty.getFields();
            		 for (XSDXPathDefinition path: xpathList)
            		 {
                 		String key = decl.getName();
            			 key += "/" + path.getValue();
            			 keys.add(key);
            		 }
					
				}
            	view.setSearchableBusinessElements(keys.toArray(new String[]{}));
            	view.setViewableBusinessElements(keys.toArray(new String[]{}));
            	view.setDescription("[EN:" + decl.getName() + "]");
            	wrap.setWsView(view);
            	
            	
            	WSViewPK viewPk = new WSViewPK();
            	viewPk.setPk(browseItem);
            	
            	WSDeleteView delView = new WSDeleteView();
            	delView.setWsViewPK(viewPk);
            	WSGetView getView = new WSGetView();
            	getView.setWsViewPK(viewPk);
            	try {
					port.getView(getView);
					port.deleteView(delView);
				} catch (RemoteException ex) {
				}

    			port.putView(wrap);
    		}
    	}
    }
    
    private void modifyRolesWithAttachedBrowseItem(String browseItem, List<Line> roles) throws RemoteException
    {
    	for (Line line : roles)
    	{
    		List<KeyValue> keyValues = line.keyValues;
    		String roleName = keyValues.get(0).value;
    		XtentisPort port = getXtentisPort();
    		WSGetRole getRole = new WSGetRole();
    		getRole.setWsRolePK(new WSRolePK(roleName));
    		WSRole role = port.getRole(getRole);
    		for (WSRoleSpecification spec: role.getSpecification())
    		{
    			if (spec.getObjectType().equals("View"))
    			{
    				WSRoleSpecificationInstance[] specInstance = spec.getInstance();
    				WSRoleSpecificationInstance  newInstance = new WSRoleSpecificationInstance();
    				newInstance.setInstanceName(browseItem);
    				newInstance.setWritable(keyValues.get(1).value.equals("Read Only") ? false : true);
    				WSRoleSpecificationInstance[] newSpecInstance = new WSRoleSpecificationInstance[specInstance.length + 1];
    				System.arraycopy(specInstance, 0, newSpecInstance, 0, specInstance.length);
    				newSpecInstance[specInstance.length] = newInstance;
    				spec.setInstance(newSpecInstance);
    				break;
    			}
    		}
    		WSPutRole wrap = new WSPutRole();
    		wrap.setWsRole(role);
    		port.putRole(wrap);
    	}
    }
    
	  private boolean saveConfiguration()
	  {
		  Iterator<String> browseIterator =  browseItemToRoles.keySet().iterator();
		  while(browseIterator.hasNext())
		  {
			  String browse = browseIterator.next();
			  List<Line> roles = browseItemToRoles.get(browse);
			  try {
				newBrowseItemView(browse);
				modifyRolesWithAttachedBrowseItem(browse, roles);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				MessageDialog.openError(
						page.getSite().getShell(),
						"Error", 
						"An error occured trying to save the browse view : "+e.getLocalizedMessage()
				);
				return false;
			}
		  }
		  
		  return true;
	  }
    
	class ConfigureRolePage extends WizardPage {
	    private  TableViewer browseViewer;
		private ComplexTableViewer complexTableViewer;
		public ConfigureRolePage() {
			super("Configure Browse items views with the Roles available");
		    setTitle("Configure Browse items views with the Roles available");
		    setDescription("Please associate the Browse items views and the available role accesses");

		    // Page isn't complete until an e-mail address has been added
		    setPageComplete(true);
		  }
		
		  public void createControl(Composite parent) {
			  Composite composite = new Composite(parent, SWT.BORDER);
			  composite.setLayout(new GridLayout(1, false));
			  browseViewer = new TableViewer(composite,SWT.FULL_SELECTION | SWT.SINGLE | SWT.H_SCROLL);
			  GridData gd = new GridData(SWT.FILL,SWT.FILL,true,true,1,1);
			  gd.widthHint = 600;
			  browseViewer.getControl().setLayoutData(gd);
			  ((GridData)browseViewer.getControl().getLayoutData()).heightHint=100;
			  Table table = browseViewer.getTable();
			  TableColumn column = new TableColumn(table, SWT.CENTER);
			  column.setText(INSTANCE_NAME);
			  column.setWidth(615);

			  
	          table.setHeaderVisible(true);
	          table.setLinesVisible(true);
	          
	          CellEditor[] editors = new CellEditor[1];
	          editors[0] = new TextCellEditor(table);
	          browseViewer.setCellEditors(editors);
	          
	          browseViewer.setContentProvider(new IStructuredContentProvider() {
	          	public void dispose() {}
	          	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	          	public Object[] getElements(Object inputElement) {
	          		ArrayList<XSDElementDeclaration> values = (ArrayList<XSDElementDeclaration>)inputElement;
	          		return values.toArray(new XSDElementDeclaration[values.size()]);
	          	}
	          });
	          
	          browseViewer.setLabelProvider(new ITableLabelProvider() {
	            	public boolean isLabelProperty(Object element, String property) {return false;}
	            	public void dispose() {}
	            	public void addListener(ILabelProviderListener listener) {}
	            	public void removeListener(ILabelProviderListener listener) {}
	            	public String getColumnText(Object element, int columnIndex) {

	            		return BROWSE_ITEMS + ((XSDElementDeclaration)element).getName();
	            	}
	            	public Image getColumnImage(Object element, int columnIndex) {return null;}
	            });
	          
	          browseViewer.setCellModifier(new ICellModifier() {
	            	public boolean canModify(Object element, String property) {
	            		return true;
	            	}
	            	public void modify(Object element, String property, Object value) 
	            	{
	            		TableItem item = (TableItem) element;
	            		XSDElementDeclaration elem = (XSDElementDeclaration)item.getData();
	            		if (!(BROWSE_ITEMS + elem.getName()).equals(value.toString()))
	            		{
	            			for (XSDElementDeclaration theElem: declList)
	            			{
	            				if(theElem == elem)
	            					continue;
	            				if ((BROWSE_ITEMS + theElem.getName()).equals(value.toString()))
	            				{
	    							MessageDialog.openInformation(null, "Warnning",
	    							"The Browse Items name already exists");
	    							return;
	            				}
	            			}
	            			List<Line> lines = browseItemToRoles.get(BROWSE_ITEMS + elem.getName());
	            			browseItemToRoles.remove(BROWSE_ITEMS + elem.getName());
		            		int prex = value.toString().indexOf(BROWSE_ITEMS);
		            		elem.setName(value.toString().substring(prex + BROWSE_ITEMS.length()));
		            		browseItemToRoles.put(value.toString(), lines);
		            		refreshRoleView(BROWSE_ITEMS + elem.getName());
		            		browseViewer.update(elem, null);
	            		}
	            	}
	            	
	            	public Object getValue(Object element, String property) {
	            		XSDElementDeclaration elem = (XSDElementDeclaration)element;
	            		return BROWSE_ITEMS + elem.getName();
	            	}
	            });
	          
	            //Listen for changes in the selection of the viewer to display additional parameters
	          browseViewer.addSelectionChangedListener(new ISelectionChangedListener() {
	            	public void selectionChanged(SelectionChangedEvent event) {
	            		XSDElementDeclaration decl = (XSDElementDeclaration)((IStructuredSelection)event.getSelection()).getFirstElement();
	            		refreshRoleView(BROWSE_ITEMS + decl.getName());
	            		UpdateComplexViewButton(true);
	            	}
	            });
              browseViewer.setInput(declList);
              browseViewer.setColumnProperties(new String[]{INSTANCE_NAME});
	          browseViewer.refresh();

	          Label infoLabel = new Label(composite, SWT.NONE);
	          infoLabel.setText("Role Access definition");
	          ComplexTableViewerColumn ruleColumn = roleConfigurationColumns[0];
	          ruleColumn.setColumnWidth(250);
	          //List<String> roles=Util.getCachedXObjectsNameSet(page.getXObject(), TreeObject.ROLE);
	          List<String> roles=Util.getChildren(page.getXObject().getServerRoot(), TreeObject.ROLE);
	          ruleColumn.setComboValues(roles.toArray(new String[]{}));
	          ComplexTableViewerColumn acsColumn = roleConfigurationColumns[1];
	          acsColumn.setColumnWidth(250);
	          acsColumn.setComboValues(new String[]{"Read Only", "Read & Write"});
	          complexTableViewer = new ComplexTableViewer(Arrays
					.asList(roleConfigurationColumns), page.getManagedForm()
					.getToolkit(), composite);
	          complexTableViewer.setKeyColumns(new ComplexTableViewerColumn[]{roleConfigurationColumns[0]});
	          complexTableViewer.create();
	          complexTableViewer.getViewer().setInput(new ArrayList<Line> ());
	          UpdateComplexViewButton(false);
	          setControl(composite);
		  }
		  
		  private void UpdateComplexViewButton(boolean enabled)
		  {
	          complexTableViewer.getAddButton().setEnabled(enabled);
	          complexTableViewer.getUpButton().setEnabled(enabled);
	          complexTableViewer.getDownButton().setEnabled(enabled);
	          complexTableViewer.getDeleteButton().setEnabled(enabled);
		  }
		  
		  private void refreshRoleView(String browseItem)
		  {
			  List<Line> roles = browseItemToRoles.get(browseItem);
			  complexTableViewer.getViewer().setInput(roles);
			  complexTableViewer.getViewer().refresh();
		  }
		  
	}
}

