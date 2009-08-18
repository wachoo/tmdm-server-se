package com.amalto.workbench.dialogs;

import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.editors.AMainPage;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSExistsRole;
import com.amalto.workbench.webservices.WSRole;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoleSpecification;
import com.amalto.workbench.webservices.WSRoleSpecificationInstance;
import com.amalto.workbench.webservices.XtentisPort;

public class NewUserWizard extends Wizard {
	private EditUserNamePage page1;
	private SelectRolePage page2;
	private ISelection selection;
	private TreeObject xobject;
	private ServerView view;
	private boolean isFinish = false;
	public NewUserWizard(TreeObject xobject,ServerView view){
		super();
		this.xobject = xobject;
		this.view = view;		
	}

	@Override
	public boolean performFinish() {
		String userName = page1.getUserName();
		TreeObject newInstance = null;
		TreeParent xfolder = (xobject.isXObject()) ? xobject.getParent()
				: (TreeParent) xobject;
		try {			
			WSRole role = new WSRole((String) userName, "", null);
			if (page2.isAdmin) {
				xmlParseSpecification(role, true);
				role.setDescription("[EN:Administrator]");
			} else {
				xmlParseSpecification(role, false);
				role.setDescription("[EN:Normal User]");
			}
			newInstance = new TreeObject((String) userName,
					xfolder.getServerRoot(),
					TreeObject.ROLE, 
					new WSRolePK((String) userName),
					role
					);
			LocalTreeObjectRepository.getInstance().mergeNewTreeObject(newInstance);
			XObjectEditor editpart;

			editpart = (XObjectEditor) view.getSite().getWorkbenchWindow()
					.getActivePage().openEditor(
							new XObjectEditorInput(newInstance, newInstance
									.getDisplayName()),
							"com.amalto.workbench.editors.XObjectEditor");
			if (editpart.getSelectedPage() instanceof AMainPageV2) {
				((AMainPageV2) editpart.getSelectedPage()).markDirty();
			}
			if (editpart.getSelectedPage() instanceof AMainPage)
				((AMainPage) editpart.getSelectedPage()).markDirty();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	
	public void xmlParseSpecification(WSRole role, boolean isAdmin) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document;
						
			if (isAdmin){
				InputStream in=NewUserWizard.class.getResourceAsStream("admin.xml");
				document = builder.parse(in);
			}
			else{
				InputStream in=NewUserWizard.class.getResourceAsStream("user.xml");
				document = builder.parse(in);
			}
			NodeList nodelist = document.getElementsByTagName("typeName");

			int size = nodelist.getLength();
			ArrayList<WSRoleSpecification> wsSpecifications = new ArrayList<WSRoleSpecification>();
			for (int i = 0; i < size; i++) {
				Node node = nodelist.item(i);// get the number i node
				NamedNodeMap map = node.getAttributes();

				Node isadminNode = map.getNamedItem("isAdmin");
				String isadminValue = isadminNode.getTextContent();
				Node typenameNode = map.getNamedItem("name");
				String typevalue = typenameNode.getTextContent();

				WSRoleSpecification wsSpecification = new WSRoleSpecification();
				if (isadminValue.equals("true"))
					wsSpecification.setAdmin(true);
				else
					wsSpecification.setAdmin(false);
				wsSpecification.setObjectType(typevalue);

				NodeList instances = node.getChildNodes();
				ArrayList<WSRoleSpecificationInstance> wsInstances = new ArrayList<WSRoleSpecificationInstance>();
				for (int j = 0; j < instances.getLength(); j++) {
					
					Node instance = instances.item(j);
					NamedNodeMap inMap = instance.getAttributes();
					if (inMap != null && inMap.getLength() > 0) {
						Node isWr = inMap.getNamedItem("isWritable");
						String isWritable = isWr.getTextContent();
						String isWrValue = instance.getTextContent();

						WSRoleSpecificationInstance wsInstance = new WSRoleSpecificationInstance();
						String[] parameters = new String[1];
						if(typevalue.equals("Menu")){
							parameters[0] = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<role-menu-parameters position=\"1\"><parent-iD></parent-iD></role-menu-parameters>";
						}
						wsInstance.setParameter(parameters);
						if (isWritable.equals("true"))
							wsInstance.setWritable(true);
						else
							wsInstance.setWritable(false);
						wsInstance.setInstanceName(isWrValue);
						wsInstances.add(wsInstance);
					}
				}
				wsSpecification.setInstance(wsInstances
						.toArray(new WSRoleSpecificationInstance[wsInstances.size()]));
				wsSpecifications.add(wsSpecification);

			}

			role.setSpecification(wsSpecifications
					.toArray(new WSRoleSpecification[wsSpecifications.size()]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addPages() {
		page1=new EditUserNamePage(selection);
		addPage(page1);
		page2 = new SelectRolePage(selection);
		addPage(page2);
	}

	class EditUserNamePage extends WizardPage {
		String userName = "";
		private Label tipLabel = null;
		//private Label warningLabel = null;
		public EditUserNamePage(ISelection selection) {
			super("RoleName");
			setTitle("Role Name");
			setDescription("Please enter a name for the new Role");
			setPageComplete(false);			
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout(1, false));
			tipLabel = new Label(composite,SWT.NONE);
			tipLabel.setText("Enter a name for the Role:");
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = 150;
//			gd.heightHint =150;
			final Text nameText = new Text(composite,SWT.BORDER);
			nameText.setLayoutData(gd);
			nameText.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					String name = nameText.getText();
					String errmsg=isValid(name);
					if(errmsg!=null){
						//warningLabel.setText(isValid(name));
						setErrorMessage(errmsg);
						setPageComplete(false);
					}
					else{
						setErrorMessage(errmsg);
						userName = name;
						setPageComplete(true);
					}
				}
			});
//			warningLabel = new Label(composite,SWT.NONE);
//			warningLabel.setText(isValid(null));
//			setPageComplete(false);
//			GridData gd1 = new GridData(GridData.FILL_HORIZONTAL);
//			gd1.widthHint = 200;
//			gd1.heightHint = 20;
//			warningLabel.setLayoutData(gd1);
			setControl(composite);
		}
		public String getUserName() {
			return userName;
		}
	}
	
	class SelectRolePage extends WizardPage {
		boolean isAdmin = true;
		public boolean isAdmin() {
			return isAdmin;
		}

		private Label tipLabel = null;
		private Button adminButton = null;
		private Button userButton = null;
		public SelectRolePage(ISelection selection) {
			super("RoleType");
			setTitle("Role Type");
			setDescription("Please Select a type for the new Role");
			setPageComplete(true);
		}

		public void createControl(Composite parent) {
			
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout(1, false));
			
			Group radioGroup = new Group(composite,SWT.SHADOW_NONE);
			radioGroup.setText("Type of Role");
			radioGroup.setLayoutData(
					new GridData(SWT.FILL,SWT.FILL,false,true,2,1)
			);
			radioGroup.setLayout(new GridLayout(2,false));
			adminButton = new Button(radioGroup,SWT.RADIO);
			adminButton.setText("Administrator");
			//adminButton.setSelection(true);
			
			userButton =  new Button(radioGroup,SWT.RADIO);
			userButton.setText("Normal User");
			adminButton.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					isFinish = true;
					setPageComplete(true);
					tipLabel.setVisible(true);
					tipLabel.setText("Administrator can operate all of the DataCluster,Menu,View,Role,Routing Rule and \nData Model etc. He can setup normal user's access right.");
					isAdmin = true;
					
				}
				
			});

			userButton.addSelectionListener(new SelectionListener(){

				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void widgetSelected(SelectionEvent e) {
					isFinish = true;
					setPageComplete(true);
					tipLabel.setText("Normal User can operate part of the DataCluster,Menu,View,Role,Routing Rule and \nData Model etc.");
					isAdmin = false;
					
				}
				
			});

			tipLabel = new Label(composite,SWT.NONE);
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.widthHint = 150;
			gd.heightHint = 50;
			tipLabel.setLayoutData(gd);
			setControl(composite);
			
		}
	}
	
	public String isValid(String newText) {
		if ((newText == null) || "".equals(newText))
			return "The Name cannot be empty";
		if (!Pattern.matches("\\w*(\\s*|#|\\w+)+\\w+", newText)) {
			return "The name cannot contains invalid character!";
		}
		if(exist(newText))
			return "The Role "+newText +" already exist";
		return null;
	}
	public boolean canFinish(){
		return isFinish;
	 }
	 
	public boolean exist(String userName){
		boolean isExist = false;
		XtentisPort port = null;
		try {
			port = Util.getPort(xobject);
			if (port.existsRole(new WSExistsRole(new WSRolePK((String) userName))).is_true()) 
				isExist = true;
			else
				isExist = false;
		} catch (XtentisException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return isExist;
	}	
}
