/*
 * Created on 27 oct. 200
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.amalto.workbench.editors;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.exolab.castor.xml.Marshaller;
import org.w3c.dom.Element;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.RoleMenuParameters;
import com.amalto.workbench.utils.RoleWhereCondition;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetMenuPKs;
import com.amalto.workbench.webservices.WSGetRolePKs;
import com.amalto.workbench.webservices.WSGetRoutingRulePKs;
import com.amalto.workbench.webservices.WSGetTransformerPKs;
import com.amalto.workbench.webservices.WSGetViewPKs;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.WSRegexStoredProcedure;
import com.amalto.workbench.webservices.WSRole;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoleSpecification;
import com.amalto.workbench.webservices.WSRoleSpecificationInstance;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSTransformerPK;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.DescAnnotationComposite;

public class RoleMainPage extends AMainPageV2 implements Observer{

	protected DescAnnotationComposite desAntionComposite ;
	protected Combo objectTypesCombo;
	protected Button isAdminButton;
	
	protected Composite instancesComposite;
	//protected Text instanceNameText;
	protected Combo instanceNameCombo;
	protected Combo instanceAccessCombo;
	protected TableViewer instancesViewer;
	
	protected Composite paramsContainerComposite;
	protected Label paramsLabel;
	protected Composite paramsClientComposite;

	protected DropTarget windowTarget;	
	
	protected boolean refreshing = false;
	protected boolean comitting = false;
	
	protected Role role;
	protected String roleName;
	
    public RoleMainPage(FormEditor editor) {
        super(
        		editor,
        		RoleMainPage.class.getName(),
        		"Role "+((XObjectEditorInput)editor.getEditorInput()).getName()
        );  
        roleName = ((XObjectEditorInput)editor.getEditorInput()).getName();
    }

	protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {

        try {
        	                     
            //description
        	desAntionComposite = new DescAnnotationComposite("Description" ," ...", toolkit, charComposite, (AMainPageV2)this);
        	desAntionComposite.setAnnotationDialogTitle("Set the Description of the Role");
            //make the Page window a DropTarget - we need to dispose it
            windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
            windowTarget.setTransfer(new Transfer[]{TextTransfer.getInstance()});
            windowTarget.addDropListener(new DCDropTargetListener());
            
            //permissions            
            Composite permissionsGroup = this.getNewSectionComposite("Permissions");
            permissionsGroup.setLayout(new GridLayout(1,true));
            
            //Object Combo and is Admin Button
            Composite objectTypesComposite = toolkit.createComposite(permissionsGroup, SWT.NULL);
            objectTypesComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            objectTypesComposite.setLayout(new GridLayout(4,false));
            Label objectTypeLabel = toolkit.createLabel(objectTypesComposite, "Object Type", SWT.NULL);
            objectTypeLabel.setLayoutData(
                    new GridData(SWT.BEGINNING,SWT.CENTER,false,true,1,1)
            );
            objectTypesCombo = new Combo(objectTypesComposite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
            objectTypesCombo.setLayoutData(
                    new GridData(SWT.BEGINNING,SWT.NONE,false,false,1,1)
            );
            objectTypesCombo.setVisibleItemCount(20);
            objectTypesCombo.addModifyListener(
        		new ModifyListener() {
        			public void modifyText(ModifyEvent e) {
        				String objecType = objectTypesCombo.getText(); 
        				Role.Specification specification = role.getSpecifications().get(objecType);
        				if (specification == null) {
        					specification = role.new Specification();
        					role.getSpecifications().put(objecType, specification);
        				}
    					instancesComposite.setVisible(! specification.isAdmin());
    					isAdminButton.setSelection(specification.isAdmin());
        				if (!specification.isAdmin()) {
        					//Feed The combo of instances with names prefetched
        					instanceNameCombo.removeAll();
        					try {feedInstanceNameCombo();} catch (Exception ex){ex.printStackTrace();};
        					//instanceNameText.setText("");
        					instanceAccessCombo.select(0);
        					instancesViewer.setInput(specification.getInstances());
        				}
        				//blank parameters
        				paramsContainerComposite.setVisible(false);
        				//Block certain actions depending on the type of object
        	            if ("Menu".equals(objectTypesCombo.getText())) {
        	            	isAdminButton.setEnabled(false);
        	            	instanceAccessCombo.setEnabled(false);
        	            } else {
        	            	isAdminButton.setEnabled(true);
        	            	instanceAccessCombo.setEnabled(true);        	            	
        	            }
        			}
        		}
            );
            
        
            isAdminButton = toolkit.createButton(objectTypesComposite, "Admin Permissions On All Instances", SWT.CHECK);
            isAdminButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            isAdminButton.addMouseListener(new MouseListener() {
            	public void mouseUp(MouseEvent e) {
//            		System.out.println("mouseUp() ");
//            		update underlying model
               		Role.Specification specification = role.getSpecifications().get(objectTypesCombo.getText());
            		if (specification == null) {
            			specification = role.new Specification();
            			role.getSpecifications().put(objectTypesCombo.getText(), specification);
            		}
					instancesComposite.setVisible(! isAdminButton.getSelection());
					specification.setAdmin(isAdminButton.getSelection());
    				if (! isAdminButton.getSelection()) {
    					instancesViewer.setInput(specification.getInstances());
    					instancesViewer.setSelection(null);
    				}
    				//blank params
    				paramsContainerComposite.setVisible(false);
    				//mark for update
            		markDirty();
            	}
            	public void mouseDoubleClick(MouseEvent e) {}
            	public void mouseDown(MouseEvent e) {}
            });
            
            
            //intances Viewer
            instancesComposite = toolkit.createComposite(permissionsGroup, SWT.BORDER);
            instancesComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            instancesComposite.setLayout(new GridLayout(3,false));

            Label instancesLabel = toolkit.createLabel(instancesComposite, "Read and Write Permissions On Specific Instances", SWT.NULL);
            instancesLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
            );
            instanceNameCombo = new Combo(instancesComposite, SWT.DROP_DOWN|SWT.SINGLE);
            instanceNameCombo.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            /*
            instanceNameText = toolkit.createText(instancesComposite, "",SWT.BORDER|SWT.SINGLE);
            instanceNameText.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            */
            
            instanceAccessCombo = new Combo(instancesComposite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
            instanceAccessCombo.setLayoutData(
                    new GridData(SWT.BEGINNING,SWT.NONE,false,false,1,1)
            );
            instanceAccessCombo.add("Read Only");
            instanceAccessCombo.add("Read & Write");
            instanceAccessCombo.addModifyListener(
        		new ModifyListener() {
        			public void modifyText(ModifyEvent e) {}
        		}
            );
            instanceAccessCombo.select(0);
            
            Button addInstanceButton = toolkit.createButton(instancesComposite,"",SWT.PUSH );
            addInstanceButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
            addInstanceButton.setToolTipText("Add");
            addInstanceButton.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
            );
            addInstanceButton.addSelectionListener(new SelectionListener() {
            	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
            	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
//            		System.out.println("Add instance widgetSelected() ");
            		//update underlying role
            		Role.Specification specification = role.getSpecifications().get(objectTypesCombo.getText());
            		if (specification == null) {
            			specification = role.new Specification();
            			role.getSpecifications().put(objectTypesCombo.getText(), specification);
            		}
            		Role.Specification.Instance instance = specification.new Instance();
            		instance.setWritable(instanceAccessCombo.getSelectionIndex()==1);

            		specification.getInstances().put(instanceNameCombo.getText(), instance);
            		
            		
            		
            		if ("Menu".equals(objectTypesCombo.getText())) {
            			//init Menu Parameters
    					RoleMenuParameters menuParameters = new RoleMenuParameters();
    					menuParameters.setParentID("");
    					menuParameters.setPosition(1);
    					String instanceName = instanceNameCombo.getText();
    					specification.getInstances().get(instanceName).setParameters(menuParameters.marshalMenuParameters());
            		}
            		
            		//update the instances viewer
            		instancesViewer.refresh();
            		//hide params (viewer has no selection)
            		paramsContainerComposite.setVisible(false);
            		//mark for need to save
            		markDirty();
            	};
            });

            //set up the instances Table Viewer
            final String INSTANCE_NAME="Instance Name Regular Expression";
            final String INSTANCE_ACCESS = "Access";
            
            instancesViewer = new TableViewer(instancesComposite,SWT.FULL_SELECTION | SWT.MULTI);
            instancesViewer.getControl().setLayoutData(    
                    new GridData(SWT.FILL,SWT.FILL,true,true,3,1)
            );
            ((GridData)instancesViewer.getControl().getLayoutData()).heightHint=100;
            
            // Set up the underlying table
            Table table = instancesViewer.getTable();
            
            
            //table.setLayoutData(new GridData(GridData.FILL_BOTH));

            new TableColumn(table, SWT.LEFT).setText(INSTANCE_NAME);
            new TableColumn(table, SWT.CENTER).setText(INSTANCE_ACCESS);
            table.getColumn(0).setWidth(200);
            table.getColumn(1).setWidth(200);
            for (int i = 2, n = table.getColumnCount(); i < n; i++) {
              table.getColumn(i).pack();
            }
            
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
           

            // Create the cell editors --> We actually discard those later: not natural for an user
            CellEditor[] editors = new CellEditor[4];
            editors[0] = new TextCellEditor(table);
            editors[1] = new TextCellEditor(table);
            instancesViewer.setCellEditors(editors);
            
            //set the content provider
            instancesViewer.setContentProvider(new IStructuredContentProvider() {
            	public void dispose() {}
            	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
            	public Object[] getElements(Object inputElement) {
//            		System.out.println("getElements() ");
            		HashMap<String, Role.Specification.Instance> instances = (HashMap<String, Role.Specification.Instance>) inputElement;
            		Set<String> names = instances.keySet();
            		ArrayList<InstanceLine> lines = new ArrayList<InstanceLine>();
            		for (Iterator iter = names.iterator(); iter.hasNext(); ) {
						String name = (String) iter.next();
//						System.out.println(name);//000000000000000000
//						mmenuParentIDCombo.add(name);
						InstanceLine line = new InstanceLine(name,instances.get(name).isWritable());
						lines.add(line);
					}
            		//we return an instance line made of a Sring and a boolean
            		return lines.toArray(new InstanceLine[lines.size()]);
            	}
            });
            
            //set the label provider
            instancesViewer.setLabelProvider(new ITableLabelProvider() {
            	public boolean isLabelProperty(Object element, String property) {return false;}
            	public void dispose() {}
            	public void addListener(ILabelProviderListener listener) {}
            	public void removeListener(ILabelProviderListener listener) {}
            	public String getColumnText(Object element, int columnIndex) {
//            		System.out.println("getColumnText() "+columnIndex);
            		InstanceLine line = (InstanceLine) element;
            		switch (columnIndex) {
            		    case 0:
            		      return line.getInstanceName();
            		    case 1:
            		      return line.isWritable() ? "Read & Write" : "Read Only";
        		    }
            		return "";
            	}
            	public Image getColumnImage(Object element, int columnIndex) {return null;}
            });

            // Set the column properties
            instancesViewer.setColumnProperties(new String[]{INSTANCE_NAME,INSTANCE_ACCESS});
            
            //set the Cell Modifier
            instancesViewer.setCellModifier(new ICellModifier() {
            	public boolean canModify(Object element, String property) {
            		//if (INSTANCE_ACCESS.equals(property)) return true; Deactivated
            		return false;
            	}
            	public void modify(Object element, String property, Object value) {
//            		System.out.println("modify() "+element.getClass().getName()+": "+property+": "+value);
            		//InstanceLine line = (InstanceLine)((IStructuredSelection)instancesViewer.getSelection()).getFirstElement();
            		//deactivated: editing in places is not natural for users 
            	}
            	public Object getValue(Object element, String property) {
//            		System.out.println("getValue() "+property);
            		InstanceLine line = (InstanceLine) element;
            		if (INSTANCE_NAME.equals(property)) {
            			return line.getInstanceName();
            		}
            		if (INSTANCE_ACCESS.equals(property)) {
            			return line.isWritable() ? "Read & Write" : "Read Only";
            		}
            		return null;
            	}
            });
            
            //Listen for changes in the selection of the viewer to display additional parameters
            instancesViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            	public void selectionChanged(SelectionChangedEvent event) {
//            		System.out.println("Viewer selectionChanged() ");
            		InstanceLine line = (InstanceLine)((IStructuredSelection)event.getSelection()).getFirstElement();
            		if (line!=null) {
	            		if ("View".equals(objectTypesCombo.getText())) {
	            			showViewParameters(line.getInstanceName());
	            		}
	            		if ("Menu".equals(objectTypesCombo.getText())) {
	            			showMenuParameters(line.getInstanceName());
	            		}
            		}
            	}
            });
            
            instancesViewer.refresh();

                 
            //ADDITIONAL PARAMETERS
            paramsContainerComposite = toolkit.createComposite(permissionsGroup, SWT.BORDER);
            paramsContainerComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            paramsContainerComposite.setLayout(new GridLayout(1,false));
            
            paramsLabel = toolkit.createLabel(paramsContainerComposite, "Parameters of a selected instance", SWT.NULL);
            paramsLabel.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            paramsClientComposite = toolkit.createComposite(paramsContainerComposite, SWT.BORDER);
            paramsClientComposite.setLayoutData(
                    new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
            );
            paramsClientComposite.setLayout(new StackLayout());
            wrap.Wrap(this, instancesViewer);
            
            refreshData();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }//createCharacteristicsContent

    public void update(Observable o, Object arg)
    {
    	if (arg != null && (arg == instancesViewer || arg == wcListViewer)) {
			deleteItems(arg);
		}
    }
    
    private void deleteItems(Object view)
    {
    	List list = null;
		if (instancesViewer == view) {
			list = ((IStructuredSelection) instancesViewer.getSelection())
					.toList();
		} else if (wcListViewer != null) {
			IStructuredSelection selections = (IStructuredSelection) wcListViewer
					.getSelection();
			list = Arrays.asList(selections.toArray());
			String instanceName = ((InstanceLine)((IStructuredSelection)instancesViewer.getSelection()).getFirstElement()).getInstanceName();
			ArrayList<RoleWhereCondition> wcList = (ArrayList<RoleWhereCondition>)wcListViewer.getInput();
			wcList.removeAll(list);
			LinkedHashSet<String> parameters = new LinkedHashSet<String>();
			for (Iterator iter = wcList.iterator(); iter.hasNext(); ) {
				RoleWhereCondition rwc = (RoleWhereCondition) iter.next();
				parameters.add(rwc.toString());
			}
			role
				.getSpecifications().get(objectTypesCombo.getText())
					.getInstances().get(instanceName)
						.setParameters(parameters);
			wcListViewer.refresh();
		}
    	

		for (Object le : list) {
			Object line = le;
			if (line instanceof InstanceLine) {
				HashMap map = role.getSpecifications().get(objectTypesCombo.getText())
					.getInstances();
				boolean is = map.containsKey(((InstanceLine) line).getInstanceName());
				map.remove(((InstanceLine) line).getInstanceName());
				// refresh
				instancesViewer.refresh();
				paramsContainerComposite.setVisible(false);
			}
			// mark for update
			markDirty();
		}
    }
    
    
	protected void refreshData() {
		try {
//			System.out.println("refreshData() ");
			if (this.comitting) return;
			
			this.refreshing = true;
			
			WSRole wsRole = (WSRole) (getXObject().getWsObject());    	
			desAntionComposite.setText(wsRole.getDescription()==null ? "" : wsRole.getDescription());
			role = new Role();
			role.setName(wsRole.getName());
			role.setDescription(wsRole.getDescription()==null ? "" : wsRole.getDescription());
			HashMap<String,Role.Specification> specifications = new HashMap<String, Role.Specification>();
			//don't delete the "//System.out.println(" in this if 
			if (wsRole.getSpecification()!=null) {
				
				for (int i = 0; i < wsRole.getSpecification().length; i++) {
					WSRoleSpecification wsSpecification= wsRole.getSpecification()[i];
					
					//System.out.println("<typeName isAdmin=\""+wsSpecification.isAdmin()+"\" name =\""+wsSpecification.getObjectType()+"\">");
					
					Role.Specification specification = (new Role()).new Specification();
					specification.setAdmin(wsSpecification.isAdmin());
					HashMap<String, Role.Specification.Instance> instances = new HashMap<String, Role.Specification.Instance>();
					if (wsSpecification.getInstance()!=null) {
						for (int j = 0; j < wsSpecification.getInstance().length; j++) {
							WSRoleSpecificationInstance wsInstance = wsSpecification.getInstance()[j];
							Role.Specification.Instance instance = specification.new Instance();
							instance.setWritable(wsInstance.isWritable());
							instance.setParameters(new LinkedHashSet<String>(Arrays.asList(wsInstance.getParameter())));
							instances.put(wsInstance.getInstanceName(),instance);
							//System.out.println("	"+"<instance isWritable=\""+wsInstance.isWritable()+"\">"+wsInstance.getInstanceName()+"</instance> ");
						}
					}
					//System.out.println("</typeName>");
					specification.setInstances(instances);
					specifications.put(wsSpecification.getObjectType(), specification);
				}
			}
			if(specifications.get("Role")==null||specifications.get("Role").getInstances().size()==0){
				//add by lym
				//if the Role is a new one or have no Roles Item,add itself as the role
				Role.Specification specification = (new Role()).new Specification();
				specification.setAdmin(false);
				HashMap<String, Role.Specification.Instance> instances = new HashMap<String, Role.Specification.Instance>();
				
				Role.Specification.Instance instance = specification.new Instance();
				instance.setWritable(false);
				instance.setParameters(new LinkedHashSet<String>(Arrays.asList(roleName)));
				
				instances.put(roleName, instance);
				specification.setInstances(instances);
				specifications.put("Role", specification);
				
			}
			role.setSpecifications(specifications);
			
			XtentisPort port = Util.getPort(getXObject());
            String[] objects = port.getObjectsForRoles(null).getStrings();
            Arrays.sort(objects);
	    	
			//Now fill in the values on the page
            desAntionComposite.setText(role.getDescription()==null ? "" : role.getDescription());
            int selected = (objectTypesCombo.getSelectionIndex() == -1 ? 0 : objectTypesCombo.getSelectionIndex());
	    	objectTypesCombo.setItems(objects);
	    	objectTypesCombo.select(selected);
         
            
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
			
			WSRole ws = (WSRole) (getXObject().getWsObject());
			ws.setDescription(desAntionComposite.getText());
			ws.setName(role.getName());
			Set<String> objectTypes =role.getSpecifications().keySet();
			ArrayList<WSRoleSpecification> wsSpecifications = new ArrayList<WSRoleSpecification>();
			for (Iterator iter = objectTypes.iterator(); iter.hasNext(); ) {
				String objectType = (String) iter.next();
				Role.Specification specification = role.getSpecifications().get(objectType);
				ArrayList<WSRoleSpecificationInstance> wsInstances = new ArrayList<WSRoleSpecificationInstance>();
				Set<String> instanceIds = specification.getInstances().keySet();
				for (Iterator iterator = instanceIds.iterator(); iterator.hasNext(); ) {
					String id = (String) iterator.next();
					Role.Specification.Instance instance = specification.getInstances().get(id);
					WSRoleSpecificationInstance wsInstance = new WSRoleSpecificationInstance(
							id,
							instance.isWritable(),
							instance.getParameters().toArray(new String[instance.getParameters().size()])
					);
					wsInstances.add(wsInstance);
				}
				WSRoleSpecification wsSpecification = new WSRoleSpecification(
						objectType,
						specification.isAdmin(),
						wsInstances.toArray(new WSRoleSpecificationInstance[wsInstances.size()])
				);
				wsSpecifications.add(wsSpecification);
			}
			ws.setSpecification(wsSpecifications.toArray(new WSRoleSpecification[wsSpecifications.size()]));
			
			this.comitting = false;
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(this.getSite().getShell(), "Error comtiting the page", "Error comitting the page: "+e.getLocalizedMessage());
		}    	
	}
		
	protected void createActions() {
	}


	public void textChanged(TextEvent event) {
		markDirty();
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
	

	/****************************************************************************
	 *   VIEWS Additional Parameters
	 ****************************************************************************/
	
	//View Options
	protected Text wcLeftText;
	protected Text wcRightText;
	protected Combo wcOperatorCombo;
	protected Combo wcPredicateCombo;
	protected ListViewer wcListViewer;
	private Composite wcComposite = null;
	
	protected void showViewParameters(String instanceName) {
//		System.out.println("showViewParameters() "+instanceName);
		if (wcComposite==null)
			wcComposite = getViewParametersComposite();
		((StackLayout)paramsClientComposite.getLayout()).topControl = wcComposite;
		paramsContainerComposite.setVisible(true);
		this.getManagedForm().reflow(true);
		refreshViewParameters(instanceName);
	}
	
	private void refreshViewParameters(String instanceName) {
		paramsLabel.setText("Additional xPath Filters For View "+instanceName);
	
		wcLeftText.setText("");
		wcOperatorCombo.select(0);
		wcRightText.setText("");
		wcPredicateCombo.setEnabled(true);
		wcPredicateCombo.select(0);
		
		wcListViewer.setInput(new ArrayList<RoleWhereCondition>());
		wcListViewer.refresh();
	
		//retrieve the parameters of the instance
		HashSet<String> parameters = 
			role.getSpecifications().get(objectTypesCombo.getText())
				.getInstances().get(instanceName)
					.getParameters();
		
		//build the list
		ArrayList<RoleWhereCondition> wcList = new ArrayList<RoleWhereCondition>();
		for (Iterator iter = parameters.iterator(); iter.hasNext(); ) {
			String marshalledWC = (String) iter.next();
//			System.out.println("Unmarshalling: "+marshalledWC);
			wcList.add(RoleWhereCondition.parse(marshalledWC));
		}
		//refresh the whole thing
		wcListViewer.setInput(wcList);
		
	}
	
	
	private Composite getViewParametersComposite() {
        FormToolkit toolkit =  this.getManagedForm().getToolkit();
        
        Composite composite = toolkit.createComposite(paramsClientComposite, SWT.BORDER);
        composite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        composite.setLayout(new GridLayout(5,false));
                
        wcLeftText = toolkit.createText(composite, "",SWT.BORDER|SWT.SINGLE);
        wcLeftText.setLayoutData(    
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        wcOperatorCombo = new Combo(composite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
        wcOperatorCombo.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
        wcOperatorCombo.add("Contains");
        wcOperatorCombo.add("Contains Text Of");
        wcOperatorCombo.add("Starts With");
        wcOperatorCombo.add("Strict Contains");
        wcOperatorCombo.add("=");
        wcOperatorCombo.add("!=");
        wcOperatorCombo.add(">");
        wcOperatorCombo.add(">=");
        wcOperatorCombo.add("<");
        wcOperatorCombo.add("<=");
        wcOperatorCombo.add("No Operator");
        wcOperatorCombo.select(0);
        wcOperatorCombo.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(SelectionEvent e) {}
        	public void widgetSelected(SelectionEvent e) {
        		if ("Contains".equals(wcOperatorCombo.getText())) {
        			wcPredicateCombo.setEnabled(true);
        		} else {
        			wcPredicateCombo.select(0);
        			wcPredicateCombo.setEnabled(false);
        		}
        	}
        });
        
        wcRightText = toolkit.createText(composite, "",SWT.BORDER|SWT.SINGLE);
        wcRightText.setLayoutData(    
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        wcRightText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {
				if ((e.stateMask==0) && (e.character == SWT.CR)){
            		addWhereCondition();
				}
			}
        });
        wcPredicateCombo = new Combo(composite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
        wcPredicateCombo.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
        wcPredicateCombo.add("");
        wcPredicateCombo.add("Or");
        wcPredicateCombo.add("And");
        wcPredicateCombo.add("Strict And");
        wcPredicateCombo.add("Exactly");
        wcPredicateCombo.add("Not");
        
        Button wcButton =toolkit.createButton(composite,"",SWT.PUSH );
        wcButton.setImage(ImageCache.getCreatedImage(EImage.ADD_OBJ.getPath()));
        wcButton.setToolTipText("Add");
        wcButton.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,false,true,1,1)
        );
        wcButton.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {};
        	public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
        		addWhereCondition();
        	};
        });        
        
        wcListViewer = new ListViewer(composite,SWT.BORDER | SWT.MULTI);
        wcListViewer.getControl().setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,5,1)
        );
        ((GridData)wcListViewer.getControl().getLayoutData()).minimumHeight = 100;
        wcListViewer.setContentProvider(new IStructuredContentProvider() {
			public void dispose() {}
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
			public Object[] getElements(Object inputElement) {
				ArrayList<RoleWhereCondition> wcList = (ArrayList<RoleWhereCondition>)inputElement;
				return wcList.toArray(new RoleWhereCondition[wcList.size()]);
			}
        });
        wcListViewer.setLabelProvider(new ILabelProvider() {
			public Image getImage(Object element) {return null;}
			public String getText(Object element) {
//				System.out.println("ELEMENT "+element.getClass().getName());
				RoleWhereCondition wc = (RoleWhereCondition) element;
				String text = wc.getLeftPath()+" ";
				text+=wc.getOperator();
				text+=" ";
				if (!wc.getOperator().equals("Join")) text+="\"";
				text+=wc.getRightValueOrPath();
				if (!wc.getOperator().equals("Join")) text+="\"";
				text+=" ";
				if ((wc.getPredicate()!=null) && (!"".equals(wc.getPredicate())))
					text+="["+wc.getPredicate()+"]";
				return text;
			}
			public void addListener(ILabelProviderListener listener) {}
			public void dispose() {}
			public boolean isLabelProperty(Object element, String property) {return false;}
			public void removeListener(ILabelProviderListener listener) {}
        });
        DragSource wcSource = new DragSource(wcListViewer.getControl(),DND.DROP_MOVE);
        wcSource.setTransfer(new Transfer[]{TextTransfer.getInstance()});
        wcSource.addDragListener(new WCDragSourceListener());

        wrap.Wrap(RoleMainPage.this, wcListViewer);
        return composite;
	}
	
	
	protected void addWhereCondition() {
  		markDirty();
		RoleWhereCondition wc = new RoleWhereCondition();
		wc.setLeftPath(wcLeftText.getText());
		wc.setOperator(wcOperatorCombo.getText());
		wc.setRightValueOrPath(wcRightText.getText());
		wc.setPredicate(wcPredicateCombo.getText());
		
		//we remarshall everything for simplicity
		ArrayList<RoleWhereCondition> wcList = (ArrayList<RoleWhereCondition>)wcListViewer.getInput();
		wcList.add(wc);
	
		//Process the list
		LinkedHashSet<String> parameters = new LinkedHashSet<String>();
		for (Iterator iter = wcList.iterator(); iter.hasNext(); ) {
			RoleWhereCondition wswc = (RoleWhereCondition) iter.next();
			parameters.add(wswc.toString());
		}
		
		//Update the underlying role
		String instanceName = ((InstanceLine)((IStructuredSelection)instancesViewer.getSelection()).getFirstElement()).getInstanceName();
		role
			.getSpecifications().get(objectTypesCombo.getText())
					.getInstances().get(instanceName)
						.setParameters(parameters);

		//refresh
		wcListViewer.setInput(wcList); //force refresh
		markDirty();	
	}
	

	
	
	/**
	 * Where Condition Drag
	 *
	 */	
	class WCDragSourceListener implements DragSourceListener {

		public void dragFinished(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection)RoleMainPage.this.wcListViewer.getSelection();
			if (selection.getFirstElement()!=null) {
				RoleWhereCondition wc = (RoleWhereCondition) selection.getFirstElement();
				//Update the underlying role
				String instanceName = ((InstanceLine)((IStructuredSelection)instancesViewer.getSelection()).getFirstElement()).getInstanceName();
				ArrayList<RoleWhereCondition> wcList = (ArrayList<RoleWhereCondition>)wcListViewer.getInput();
				wcList.remove(wc);
				//update underlying role
				LinkedHashSet<String> parameters = new LinkedHashSet<String>();
				for (Iterator iter = wcList.iterator(); iter.hasNext(); ) {
					RoleWhereCondition rwc = (RoleWhereCondition) iter.next();
					parameters.add(rwc.toString());
				}
				role
					.getSpecifications().get(objectTypesCombo.getText())
						.getInstances().get(instanceName)
							.setParameters(parameters);
				wcListViewer.refresh();
				markDirty();
			}
		}

		public void dragSetData(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection)RoleMainPage.this.wcListViewer.getSelection();
			if (selection.getFirstElement()!=null) {
					event.data =  selection.getFirstElement();
			}
		}

		public void dragStart(DragSourceEvent event) {
			IStructuredSelection selection = (IStructuredSelection)RoleMainPage.this.wcListViewer.getSelection();
			event.doit = (selection.getFirstElement()!=null);
		}


	}

	
	
	
	/****************************************************************************
	 *   MENUS
	 ****************************************************************************/
	
	protected Composite menuComposite = null;
	protected Text menuParentIDText = null;
	protected Combo menuPositionCombo = null;
	protected Combo menuParentIDCombo = null;


	protected boolean menuParametersRefreshing = false;
	
	public String[] getStrings(Object inputElement,String instanceName){
//		System.out.println("getElements() ");
		
		HashMap<String, Role.Specification.Instance> instances = 
			(HashMap<String, Role.Specification.Instance>) inputElement;
		Set<String> names = instances.keySet();
		
//		ArrayList<InstanceLine> lines = new ArrayList<InstanceLine>();
		String[] string = new String[names.size()];
		Iterator iter = names.iterator();
		string[0]="";
		for (int i=1; iter.hasNext();) {
			String name = (String) iter.next();
//			System.out.println(name);//000000000000000000
//			mmenuParentIDCombo.add(name);
//			InstanceLine line = new InstanceLine(name,instances.get(name).isWritable());
//			lines.add(line);
			if(!instanceName.equals(name)){
				string[i]=name;
				i++;
			}
				
		}
		//we return an instance line made of a Sring and a boolean
//		return lines.toArray(new InstanceLine[lines.size()]);
		return string;
	}
	
	
	protected void showMenuParameters(String instanceName) {//show the "menu location of menu entry..."
		if (menuComposite==null)
			menuComposite = getMenuParametersComposite();
		((StackLayout)paramsClientComposite.getLayout()).topControl = menuComposite;
		paramsContainerComposite.setVisible(true);
		this.getManagedForm().reflow(true);
		//add list here lym
		refreshMenuParameters(instanceName);
//		InstanceLine line = (InstanceLine)((IStructuredSelection)event.getSelection()).getFirstElement();
//		HashMap<String, Role.Specification.Instance> instances = (HashMap<String, Role.Specification.Instance>) inputElement;
//		Set<String> names = instances.keySet();
//		ArrayList<InstanceLine> lines = new ArrayList<InstanceLine>();
//		mmenuParentIDCombo.setItem(1,instanceName);
	    
		menuParentIDCombo.setItems(getStrings(instancesViewer.getInput(),instanceName));//add the items
		
		HashSet<String> parameters = role.getSpecifications().get(objectTypesCombo.getText())
										 .getInstances().get(instanceName).getParameters();
//		ArrayList<String> wcList = new ArrayList<String>();
//		int i =parameters.size();
		String parentId = new String();
		for (Iterator iter = parameters.iterator(); iter.hasNext(); ) {
			String parent = (String) iter.next();
//			System.out.println(parent);
			try{
				Element result = Util.parse(parent).getDocumentElement();
//				Document doc = Util.parse(parent);
//				result.getAttribute("parent-iD");
//				result.getAttributes();
				parentId = result.getTextContent();
//				System.out.println(result);
			}catch(Exception e){}
			
//			RoleWhereCondition.parse(parent);
		}
		String[] s = getStrings(instancesViewer.getInput(),instanceName);
		int i=0;
		for(;i<s.length;i++){
			if(parentId.equals(s[i])){
				menuParentIDCombo.select(i);
				break;
			}
			
		}
		
		//refresh the whole thing
//		wcListViewer.setInput(wcList);
		
	}
	

	private Composite getMenuParametersComposite() {
        FormToolkit toolkit =  this.getManagedForm().getToolkit();
        
        Composite composite = toolkit.createComposite(paramsClientComposite, SWT.BORDER);
        composite.setLayoutData(
                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
        );
        composite.setLayout(new GridLayout(2,false));
        
        Label menuParentIDLabel = toolkit.createLabel(composite, "Parent Entry ID", SWT.NULL);
        menuParentIDLabel.setLayoutData(
                new GridData(SWT.BEGINNING,SWT.CENTER,false,true,1,1)
        );
        
//        menuParentIDText = toolkit.createText(composite, "",SWT.BORDER|SWT.SINGLE);
//        menuParentIDText.setLayoutData(    
//                new GridData(SWT.FILL,SWT.FILL,true,true,1,1)
//        );
//        menuParentIDText.addModifyListener(new ModifyListener() {
//        	public void modifyText(ModifyEvent e) {
//        		if (menuParametersRefreshing) return;
//        		commitMenuParameters();
//        	}
//        });
        
        menuParentIDCombo = new Combo(composite,SWT.READ_ONLY|SWT.DROP_DOWN|SWT.SINGLE);
        GridData gridData =new GridData(SWT.BEGINNING,SWT.FILL,true,true,1,1);
        gridData.widthHint=150;
        menuParentIDCombo.setLayoutData(gridData);
        		
        		 
       
//        menuParentIDCombo.setItems(getElements());
        menuParentIDCombo.addSelectionListener(new SelectionListener(){
        	public void widgetDefaultSelected(SelectionEvent e) {}
        	public void widgetSelected(SelectionEvent e) {
        		if (menuParametersRefreshing) return;
        		//reset position
        		menuPositionCombo.select(0);
        		commitMenuParameters();
        	}
        });
        
        Label menuPositionLabel = toolkit.createLabel(composite, "Position in parent menu", SWT.NULL);
        menuPositionLabel.setLayoutData(
                new GridData(SWT.BEGINNING,SWT.CENTER,false,true,1,1)
        );
    
        menuPositionCombo = new Combo(composite,SWT.READ_ONLY |SWT.DROP_DOWN|SWT.SINGLE);
        menuPositionCombo.setLayoutData(
                new GridData(SWT.BEGINNING,SWT.FILL,false,false,1,1)
        );
        for (int i=1;i<=20;i++) menuPositionCombo.add(""+i);
        menuPositionCombo.addSelectionListener(new SelectionListener() {
        	public void widgetDefaultSelected(SelectionEvent e) {}
        	public void widgetSelected(SelectionEvent e) {
        		if (menuParametersRefreshing) return;
        		commitMenuParameters();
        	}
        });	
        	
		return composite;
	}
	
	/**
	 * Refresh the content of the menu parameters
	 * @param instanceName
	 */
	private void refreshMenuParameters(String instanceName) {
		paramsLabel.setText("Menu location of menu entry "+instanceName+" in parent menu");
		
		//retrieve the parameters of the instance
		LinkedHashSet<String> parameters = 
			role.getSpecifications().get(objectTypesCombo.getText())
				.getInstances().get(instanceName)
					.getParameters();
	
		RoleMenuParameters menuParameters = new RoleMenuParameters();
		menuParametersRefreshing =true;
		
		if ((parameters!=null) &&(parameters.size()>0)) {
			try {
				menuParameters = RoleMenuParameters.unmarshalMenuParameters(parameters);
			} catch (Exception e){}
//			menuParentIDText.setText(menuParameters.getParentID());
			
			menuParentIDCombo.select(menuParameters.getPosition());
			
			menuPositionCombo.select(menuParameters.getPosition()-1);
		} else {
			//force a first set of parameters
//			menuParentIDText.setText("");
			menuParentIDCombo.select(0);
			menuPositionCombo.select(0);
			commitMenuParameters();
		}
		menuParametersRefreshing =false;
	}
	
		 
	/**
	 * Commit the content of the menu parameters
	 * @param instanceName
	 */
	protected void commitMenuParameters() {
		RoleMenuParameters menuParameters = new RoleMenuParameters();
//		menuParameters.setParentID(menuParentIDText.getText());
		menuParameters.setParentID(menuParentIDCombo.getText());
		menuParameters.setPosition(menuPositionCombo.getSelectionIndex()+1);
		//commit as we go
		String instanceName = ((InstanceLine)((IStructuredSelection)instancesViewer.getSelection()).getFirstElement()).getInstanceName();
		role.getSpecifications().get(objectTypesCombo.getText())
			.getInstances().get(instanceName).setParameters(menuParameters.marshalMenuParameters());
	
			markDirty();
	}
	 
	protected void feedInstanceNameCombo() throws Exception{
		try {
			XtentisPort port = Util.getPort(getXObject());
			if ("Data Model".equals(objectTypesCombo.getText())) {
				WSDataModelPK[] pks = port.getDataModelPKs(new WSRegexDataModelPKs(".*")).getWsDataModelPKs();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
			} else if ("Data Cluster".equals(objectTypesCombo.getText())) {
				WSDataClusterPK[] pks = port.getDataClusterPKs(new WSRegexDataClusterPKs(".*")).getWsDataClusterPKs();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
			} else if ("Role".equals(objectTypesCombo.getText())) {
				WSRolePK[] pks = port.getRolePKs(new WSGetRolePKs(".*")).getWsRolePK();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
			} else if ("Menu".equals(objectTypesCombo.getText())) {
				WSMenuPK[] pks = port.getMenuPKs(new WSGetMenuPKs(".*")).getWsMenuPK();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
			} else if ("Routing Rule".equals(objectTypesCombo.getText())) {
				WSRoutingRulePK[] pks = port.getRoutingRulePKs(new WSGetRoutingRulePKs(".*")).getWsRoutingRulePKs();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
			} else if ("Stored Procedure".equals(objectTypesCombo.getText())) {
				WSStoredProcedurePK[] pks = port.getStoredProcedurePKs(new WSRegexStoredProcedure(".*")).getWsStoredProcedurePK();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
			} else if ("View".equals(objectTypesCombo.getText())) {
				WSViewPK[] pks = port.getViewPKs(new WSGetViewPKs(".*")).getWsViewPK();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
			} else if ("Transformer".equals(objectTypesCombo.getText())) {
				WSTransformerPK[] pks = port.getTransformerPKs(new WSGetTransformerPKs(".*")).getWsTransformerPK();
				if (pks!=null) {
					for (int i = 0; i < pks.length; i++) {
						instanceNameCombo.add(pks[i].getPk());
					}
				}
				
			}			
		}catch (XtentisException e) {
			throw(e);
		} catch (Exception e) {
			throw new XtentisException("Unable to find the instances of object "+objectTypesCombo.getText()+": "+e.getClass().getName()+": "+e.getMessage());
		}
	}
	
//	/****************************************************************************
//	 *   DND
//	 ****************************************************************************/
//	
//	class DCDragSourceListener implements DragSourceListener {
//		private int selected;
//
//		public void dragFinished(DragSourceEvent event) {
//			Control control = ((DragSource)event.widget).getControl();
//			if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
//				((List)control).remove(selected);
//				RoleMainPage.this.markDirty();
//			}
//		}
//
//		public void dragSetData(DragSourceEvent event) {
//			Control control = ((DragSource)event.widget).getControl();
//			if ((control instanceof List))
//				if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
//					this.selected = ((List)control).getSelectionIndex();
//					event.data =  ((List)control).getSelection()[0];
//				}
//		}
//
//		public void dragStart(DragSourceEvent event) {
//			Control control = ((DragSource)event.widget).getControl();
//			if ((control instanceof List))
//				event.doit = (((List)control).getItemCount()>0);
//		}
//	}
//	
//	class DCDropTargetListener implements DropTargetListener {
//
//		public void dragEnter(DropTargetEvent event) {
//			//priority to copy
//			if ((event.operations & DND.DROP_COPY) == DND.DROP_COPY)
//				event.detail = DND.DROP_COPY;
//			else if ((event.operations & DND.DROP_MOVE) == DND.DROP_MOVE)
//				event.detail = DND.DROP_MOVE;
//			else	
//				event.detail = DND.DROP_NONE;
//		}
//		public void dragLeave(DropTargetEvent event) {}
//		public void dragOperationChanged(DropTargetEvent event) {}
//		public void dragOver(DropTargetEvent event) {}
//		public void drop(DropTargetEvent event) {
//			Control control = ((DropTarget)event.widget).getControl();
//			if ((control instanceof List) && ((event.operations & DND.DROP_COPY) == DND.DROP_COPY))
//				if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) 
//					if (!Arrays.asList(((List)control).getItems()).contains(event.data)) {
//							((List)control).add((String)event.data);
//							RoleMainPage.this.markDirty();
//					}
//		}
//		public void dropAccept(DropTargetEvent event) {}
//		
//	}
	
	

	
	
	/****************************************************************************
	 *   Comptroler Model
	 ****************************************************************************/
	public class Role {
	
		private String name=null;
	    private String description=null;
	    //key is objectType
	    private HashMap<String, Specification> specifications = new HashMap<String, Specification>();
	    
	    public class Specification {
	    	private boolean isAdmin = false;
	    	//key is the instanceId
	    	private HashMap<String,Instance> instances = new HashMap<String, Instance>();
	    	
	    	public class Instance {
	    		private boolean isWritable = false;
	    		private LinkedHashSet<String> parameters = new LinkedHashSet<String>();
				public boolean isWritable() {
					return isWritable;
				}
				public void setWritable(boolean isWritable) {
					this.isWritable = isWritable;
				}
				public LinkedHashSet<String> getParameters() {
					return parameters;
				}
				public void setParameters(LinkedHashSet<String> parameters) {
					this.parameters = parameters;
				}
	    	}//class Instances

			public HashMap<String,Instance> getInstances() {
				return instances;
			}
			public void setInstances(HashMap<String,Instance> instances) {
				this.instances = instances;
			}
			public boolean isAdmin() {
				return isAdmin;
			}
			public void setAdmin(boolean isAdmin) {
				this.isAdmin = isAdmin;
			}
	    }//class Specification

		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public HashMap<String,Specification> getSpecifications() {
			return specifications;
		}
		public void setSpecifications(HashMap<String,Specification> specifications) {
			this.specifications = specifications;
		}
	    

		@Override
		public String toString() {
			StringWriter sw = new StringWriter();
			try {
				Marshaller.marshal(this, sw);
			} catch (Exception e) {
				System.out.println("ERROR marshalling Role");
				e.printStackTrace();
			}
			return sw.toString();
		}
	
	}//class Role
	
	
	/***
	 * Used to dipslay a Line in the Instances Viewer
	 * @author bgrieder
	 *
	 */
	public class InstanceLine {
		private String instanceName;
		private boolean isWritable;
		public InstanceLine(String instanceName, boolean isWritable) {
			super();
			this.instanceName = instanceName;
			this.isWritable = isWritable;
		}
		public String getInstanceName() {
			return instanceName;
		}
		public void setInstanceName(String instanceName) {
			this.instanceName = instanceName;
		}
		public boolean isWritable() {
			return isWritable;
		}
		public void setWritable(boolean isWritable) {
			this.isWritable = isWritable;
		}
	}
	
	
	/*
	 * read a file in the plugin package
	 * 
	 * 
	import org.eclipse.ui.internal.util.BundleUtility;
   public static URL URLFromPlugin(String pluginId,  String filePath) {
        if (pluginId == null || filePath == null) {
            throw new IllegalArgumentException();
        }

        // if the bundle is not ready then there is no image
        Bundle bundle = Platform.getBundle(pluginId);
        if (!BundleUtility.isReady(bundle)) {
			return null;
		}

        // look for the image (this will check both the plugin and fragment folders
        URL fullPath = BundleUtility.find(bundle, filePath);
        if (fullPath == null) {
            try {
                fullPath = new URL(filePath);
            } catch (MalformedURLException e) {
            	e.printStackTrace();
                return null;
            }
        }

        if (fullPath == null) {
			return null;
		}
        return fullPath;
    }
    */
	
}
