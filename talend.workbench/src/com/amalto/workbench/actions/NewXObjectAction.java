package com.amalto.workbench.actions;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;

import com.amalto.workbench.dialogs.ViewInputDialog;
import com.amalto.workbench.editors.AMainPage;
import com.amalto.workbench.editors.AMainPageV2;
import com.amalto.workbench.editors.XObjectEditor;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDataCluster;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModel;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSExistsDataCluster;
import com.amalto.workbench.webservices.WSExistsDataModel;
import com.amalto.workbench.webservices.WSExistsMenu;
import com.amalto.workbench.webservices.WSExistsRole;
import com.amalto.workbench.webservices.WSExistsRoutingRule;
import com.amalto.workbench.webservices.WSExistsStoredProcedure;
import com.amalto.workbench.webservices.WSExistsSynchronizationPlan;
import com.amalto.workbench.webservices.WSExistsTransformerV2;
import com.amalto.workbench.webservices.WSExistsUniverse;
import com.amalto.workbench.webservices.WSExistsView;
import com.amalto.workbench.webservices.WSGetObjectsForSynchronizationPlans;
import com.amalto.workbench.webservices.WSGetObjectsForUniverses;
import com.amalto.workbench.webservices.WSMenu;
import com.amalto.workbench.webservices.WSMenuEntry;
import com.amalto.workbench.webservices.WSMenuMenuEntriesDescriptions;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSPutDataModel;
import com.amalto.workbench.webservices.WSRole;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRuleExpression;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSSynchronizationPlan;
import com.amalto.workbench.webservices.WSSynchronizationPlanItemsSynchronizations;
import com.amalto.workbench.webservices.WSSynchronizationPlanPK;
import com.amalto.workbench.webservices.WSSynchronizationPlanXtentisObjectsSynchronizations;
import com.amalto.workbench.webservices.WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations;
import com.amalto.workbench.webservices.WSTransformerV2;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniverseItemsRevisionIDs;
import com.amalto.workbench.webservices.WSUniversePK;
import com.amalto.workbench.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.workbench.webservices.WSView;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.WSWhereCondition;
import com.amalto.workbench.webservices.XtentisPort;

public class NewXObjectAction extends Action{

	private ServerView view = null;
	
	
	public NewXObjectAction(ServerView view) {
		super();
		this.view = view;
		setImageDescriptor(ImageCache.getImage( "icons/add_obj.gif"));
		setText("New");
		setToolTipText("Create a new instance of an "+IConstants.TALEND+" Object");
	}
	
	public void run() {
		try {
			super.run();
            ISelection selection = view.getViewer().getSelection();
            TreeObject xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
            xobject = LocalTreeObjectRepository.getInstance().registerNewTreeObject(xobject);       
            TreeParent xfolder = (xobject.isXObject()) ? xobject.getParent() : (TreeParent) xobject;

            //get New Key
       		Object key  = null;

       		//(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue, IInputValidator validator)
            switch(xfolder.getType()) {
        		case TreeObject.TRANSFORMER:
        			ViewInputDialog vid = new ViewInputDialog(
        					view.getSite(),
        					(TreeParent)view.getRoot().getChildren()[0].findServerFolder(TreeObject.TRANSFORMER),
        					view.getSite().getShell(),
	           				"New "+IConstants.TALEND+" Object Instance",	           				
	           				"Enter a Name for the New Instance                                                                                  ",
	           				"Smart_view_",
	           				new IInputValidator() {
	           					public String isValid(String newText) {
	           						if ((newText==null) || "".equals(newText)) 
	           							return "The Name cannot be empty";
	           						if(!Pattern.matches("\\w*(\\s*|#|\\w+)+\\w+", newText)){
	           							return "The name cannot contains invalid character!";
	           						}
	           						return null;
	           					};
	           				},true
	           		);
        			vid.setBtnShow(false);
        			vid.create();
        			//vid.getShell().setSize(new Point(500,270));
        			vid.setBlockOnOpen(true);
	           		if (vid.open() == Window.CANCEL) return;
	           		key  = vid.getValue();
        			break;
        			
        		case TreeObject.VIEW:
//        			InputDialog id = new InputDialog(
//	           				view.getSite().getShell(),
//	           				"New "+IConstants.TALEND+" Object Instance",
//	           				"The view name should follow the pattern: Browse_items_<ConceptName>[#For your purpose]",
//	           				"Browse_items_",
//	           				new IInputValidator() {
//	           					public String isValid(String newText) {
//	           						if ((newText==null) || "".equals(newText)) 
//	           							return "The Name cannot be empty";
//	           						if(!Pattern.matches("\\w*(\\s*|#|\\w+)+\\w+", newText)){
//	           							return "The name cannot contains invalid character!";
//	           						}
//	           						return null;
//	           					};
//	           				}
//	           		);
        			
        			ViewInputDialog tid = new ViewInputDialog(
        					view.getSite(),
        					(TreeParent)view.getRoot().getChildren()[0].findServerFolder(TreeObject.TRANSFORMER),
        					view.getSite().getShell(),
	           				"New "+IConstants.TALEND+" Object Instance",
	           				"Enter a Name for the New Instance",
	           				"Browse_items_",
	           				new IInputValidator() {
	           					public String isValid(String newText) {
	           						if ((newText==null) || "".equals(newText)) 
	           							return "The Name cannot be empty";
	           						if(!Pattern.matches("\\w*(\\s*|#|\\w+)+\\w+", newText)){
	           							return "The name cannot contains invalid character!";
	           						}
	           						return null;
	           					};
	           				},false
	           		);
        			
        			tid.create();
        			tid.getShell().setSize(new Point(600,180));
        			tid.setBlockOnOpen(true);
	           		if (tid.open() == Window.CANCEL) return;
	           		key  = tid.getValue();
	           		break;
	           	case TreeObject.SOURCE:
	           	case TreeObject.DESTINATION:
	           	case TreeObject.DATA_MODEL:
	           	case TreeObject.INBOUND_ADAPTOR:         		
	           	case TreeObject.INBOUND_PLUGIN:
	           	case TreeObject.OUTBOUND_ADAPTOR:         		
	           	case TreeObject.OUTBOUND_PLUGIN:
	           
	           	case TreeObject.DATA_CLUSTER:
	           	case TreeObject.STORED_PROCEDURE:
	           //	case TreeObject.ROLE:
	           	case TreeObject.ROUTING_RULE:
	           
	           	case TreeObject.MENU:
	           	case TreeObject.UNIVERSE:
	           	case TreeObject.SYNCHRONIZATIONPLAN:
	           		InputDialog id1 = new InputDialog(
	           				view.getSite().getShell(),
	           				"New "+IConstants.TALEND+" Object Instance",
	           				"Enter a Name for the New Instance",
	           				null,
	           				new IInputValidator() {
	           					public String isValid(String newText) {
	           						if ((newText==null) || "".equals(newText)) 
	           							return "The Name cannot be empty";
	           						if(!Pattern.matches("\\w*(\\s*|#|\\w+)+\\w+", newText)){
	           							return "The name cannot contains invalid character!";
	           						}
	           						return null;
	           					};
	           				}
	           		);
	           		id1.setBlockOnOpen(true);
	           		if (id1.open() == Window.CANCEL) return;
	           		key  = id1.getValue();
	           		break;
	           	default:
	           		//server or not supported
	           		return;
	        }//switch
            
//          Access to server and get port
			XtentisPort port = Util.getPort(
					new URL(xobject.getEndpointAddress()),
					xobject.getUniverse(),
					xobject.getUsername(),
					xobject.getPassword()
			);
              
			//create a new bare Instance
			TreeObject newInstance = null;
            switch(xobject.getType()) {

	           	case TreeObject.DATA_MODEL: {
	           		//check if already exists
           			if (port.existsDataModel(new WSExistsDataModel(new WSDataModelPK((String)key))).is_true()) {;
           				MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Data Model "+(String)key+" already exists");
           				return;
           			}
	           		//add
	           		WSDataModel wsDataModel = new WSDataModel(
	           				(String)key,
	           				"",
	           				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
	           				"<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"/>"
	           		);
	           		port.putDataModel(new WSPutDataModel(wsDataModel));
	           		newInstance = new TreeObject(
	    							(String)key,
	    							xfolder.getServerRoot(),
	    							TreeObject.DATA_MODEL,
	    							new WSDataModelPK((String)key),
	    							wsDataModel
	    			);           		
	           		break; }

	           	case TreeObject.VIEW: {
	           		//check if already exists
	           		if(port.existsView(new WSExistsView(new WSViewPK((String)key))).is_true()) {
	           				MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","View "+(String)key+" already exists");
	           				return;
	           		}
	           		//add
	                WSDataModelPK[] dataModelPKs = Util.getAllDataModelPKs(
	                		new URL(xobject.getEndpointAddress()),
	                		xobject.getUniverse(),
	                		xobject.getUsername(),
	                		xobject.getPassword()
	                );
	                String firstDataModel = null;
	                for (int i = 0; i < dataModelPKs.length; i++) {
						if (dataModelPKs[i].getPk().indexOf("XMLSCHEMA--")==-1) {
							firstDataModel = dataModelPKs[i].getPk();
							break;
						}
					}
	                if (firstDataModel == null) {
	                	MessageDialog.openError(
	                			view.getSite().getShell(), 
	                			"Error", 
	                			"Please create a Data Model before editing a View");
	                	return;
	                }
	                WSDataClusterPK[] dataClusterPKs = Util.getAllDataClusterPKs(
	                		new URL(xobject.getEndpointAddress()),
	                		xobject.getUniverse(),
	                		xobject.getUsername(),
	                		xobject.getPassword()
	                );
	                String firstItemCluster = null;
	                for (int i = 0; i < dataClusterPKs.length; i++) {
						if (!dataClusterPKs[i].getPk().equals("CACHE")) {  //FIXME: hardcoded CACHE
							firstItemCluster = dataClusterPKs[i].getPk();
							break;
						}
					}
	                if (firstItemCluster == null) {
	                	MessageDialog.openError(
	                			view.getSite().getShell(), 
	                			"Error", 
	                			"Please create a Data Cluster for Items before editing a View");
	                	return;
	                }
	           		WSView wsview = new WSView(
	           				(String)key,
	           				"",
	           				new String[] {},
	           				new WSWhereCondition[0],
	           				new String[] {}
	           		);
	           		//port.putView(new WSPutView(view));
	           		newInstance = new TreeObject(
	    							(String)key,
	    							xfolder.getServerRoot(),
	    							TreeObject.VIEW,
	    							new WSViewPK((String)key),
	    							wsview
	    			);           		
	           		break;	}
	           	
                case TreeObject.DATA_CLUSTER: {
                    //check if already exists
                    if (port.existsDataCluster(new WSExistsDataCluster(new WSDataClusterPK((String)key))).is_true()) {
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Data Cluster "+(String)key+" already exists");
                        return;
                    }
                    //add
                    WSDataCluster dc = new WSDataCluster(
                            (String)key,
                            "",
                            ""												//vocabulary
                    );
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.DATA_CLUSTER,
                                    new WSDataClusterPK((String)key),
                                    dc
                    );                  
                    break;  }        
                
                case TreeObject.STORED_PROCEDURE: {
                    //check if already exists
                    if (port.existsStoredProcedure(new WSExistsStoredProcedure(new WSStoredProcedurePK((String)key))).is_true()) {
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Stored Procedure "+(String)key+" already exists");
                        return;
                    }
                    //add
                    WSStoredProcedure storedProcedure = new WSStoredProcedure(
                    		(String)key,
                    		"",
                    		""
                    );
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.STORED_PROCEDURE,
                                    new WSStoredProcedurePK((String)key),
                                    storedProcedure
                    );                  
                    break;  }    

                case TreeObject.ROLE: {
                    //check if already exists
                    if (port.existsRole(new WSExistsRole(new WSRolePK((String)key))).is_true()) {
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Role "+(String)key+" already exists");
                        return;
                    }
                    //add
                    WSRole role = new WSRole(
                    		(String)key,
                    		"",
                    		null
                    );
        			
                    
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.ROLE,
                                    new WSRolePK((String)key),
                                    role
                    );                  
                    break;  }    

                case TreeObject.ROUTING_RULE: {
                    //check if already exists
                    if (port.existsRoutingRule(new WSExistsRoutingRule(new WSRoutingRulePK((String)key))).is_true()){
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Routing Rule "+(String)key+" already exists");
                        return;
                    }
                    //add
                    WSRoutingRule routingRule = new WSRoutingRule(
                    		(String)key,
                    		"",
                    		false,
                    		"*",
                    		"",
                    		"",
                    		new WSRoutingRuleExpression[0],
                    		null,                    		
                    		false
                    );
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.ROUTING_RULE,
                                    new WSRoutingRulePK((String)key),
                                    routingRule
                    );                  
                    break;  }    

                case TreeObject.TRANSFORMER: {
                    //check if already exists
                    if (port.existsTransformerV2(new WSExistsTransformerV2(new WSTransformerV2PK((String)key))).is_true()){
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Transformer "+(String)key+" already exists");
                        return;
                    }
                    //add
                    WSTransformerV2 transformer = new WSTransformerV2(
                    		(String)key,
                    		"",
                    		null
                    );
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.TRANSFORMER,
                                    new WSTransformerV2PK((String)key),
                                    transformer
                    );                  
                    break;  }                    

                case TreeObject.MENU: {
                    //check if already exists
                    if(port.existsMenu(new WSExistsMenu(new WSMenuPK((String)key))).is_true()){
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Menu "+(String)key+" already exists");
                        return;
                    }
                    //add
                    WSMenu menu = new WSMenu(
                    		(String)key,
                    		"",
                    		new WSMenuEntry[]{
                    			new WSMenuEntry(
                    				(String)key,
                    				new WSMenuMenuEntriesDescriptions[]{
                    					new WSMenuMenuEntriesDescriptions("en",(String)key)
                    				},
                    				null,
                    				null,
                    				null
                    			)
                    		}
                    );
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.MENU,
                                    new WSMenuPK((String)key),
                                    menu
                    );                  
                    break;  }      
                case TreeObject.UNIVERSE: {
                    //check if already exists
                    if(port.existsUniverse(new WSExistsUniverse(new WSUniversePK((String)key))).is_true()){
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","Universe "+(String)key+" already exists");
                        return;
                    }
                    //add
                    List<WSUniverseXtentisObjectsRevisionIDs> objectsId=new ArrayList<WSUniverseXtentisObjectsRevisionIDs>();
    				for(String object: port.getObjectsForUniverses(new WSGetObjectsForUniverses(new String[]{".*"})).getStrings()){//IConstants.XTENTISOBJECTS){
    					objectsId.add(new WSUniverseXtentisObjectsRevisionIDs(object,""));
    				}
                    WSUniverse universe = new WSUniverse(
                    		(String)key,
                    		"",
                    		objectsId.toArray(new WSUniverseXtentisObjectsRevisionIDs[objectsId.size()]),
                    		"",
                    		new WSUniverseItemsRevisionIDs[]{
                    		}
                    );
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.UNIVERSE,
                                    new WSUniversePK((String)key),
                                    universe
                    );                  
                    break;  }      
                case TreeObject.SYNCHRONIZATIONPLAN: {
                    //check if already exists
                    if(port.existsSynchronizationPlan(new WSExistsSynchronizationPlan(new WSSynchronizationPlanPK((String)key))).is_true()){
                        MessageDialog.openError(this.view.getSite().getShell(),"Error Creating Instance","SynchronizationPlan "+(String)key+" already exists");
                        return;
                    }
                    //add
                    List<WSSynchronizationPlanXtentisObjectsSynchronizations> objectsId=new ArrayList<WSSynchronizationPlanXtentisObjectsSynchronizations>();
    				for(String object: port.getObjectsForSynchronizationPlans(new WSGetObjectsForSynchronizationPlans(new String[]{".*"})).getStrings()){//IConstants.XTENTISOBJECTS){
    					objectsId.add(new WSSynchronizationPlanXtentisObjectsSynchronizations(object,new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[0]));
    				}
    				Calendar calendar=Calendar.getInstance();
    				calendar.setTimeInMillis(0);
                    WSSynchronizationPlan synchronizationPlan = new WSSynchronizationPlan(
                    		(String)key,
                    		"",
                    		"",
                    		"",
                    		"",
                    		"",
                    		"",
                    		"",
                    		"",
                    		"",
                    		objectsId.toArray(new WSSynchronizationPlanXtentisObjectsSynchronizations[objectsId.size()]),
                    		new WSSynchronizationPlanItemsSynchronizations[]{}
                    );
                    newInstance = new TreeObject(
                                    (String)key,
                                    xfolder.getServerRoot(),
                                    TreeObject.SYNCHRONIZATIONPLAN,
                                    new WSSynchronizationPlanPK((String)key),
                                    synchronizationPlan
                    );                  
                    break;  }      
                
	           	default:
	           		//server
	           		return;
            }//switch
            
            LocalTreeObjectRepository.getInstance().mergeNewTreeObject(newInstance);
            
            XObjectEditor editpart=(XObjectEditor)view.getSite().getWorkbenchWindow().getActivePage().openEditor(
                    new XObjectEditorInput(newInstance,newInstance.getDisplayName()),
                    "com.amalto.workbench.editors.XObjectEditor"
           	);
            
            /*
             * make the new page dirty
             */
            if(editpart.getSelectedPage() instanceof AMainPageV2){
            	((AMainPageV2)editpart.getSelectedPage()).markDirty();
            }
            
            /*
             * make the new page dirty
             */
            if(editpart.getSelectedPage() instanceof AMainPage)
            	((AMainPage)editpart.getSelectedPage()).markDirty();
            
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					view.getSite().getShell(),
					"Error", 
					"An error occured trying to create a new "+IConstants.TALEND+" Object Instance: "+e.getLocalizedMessage()
			);
		}		
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}