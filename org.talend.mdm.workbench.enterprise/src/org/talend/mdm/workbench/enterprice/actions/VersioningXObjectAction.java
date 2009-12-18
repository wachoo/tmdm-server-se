package org.talend.mdm.workbench.enterprice.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.workbench.enterprice.dialog.VersioningCommitDialog;
import org.talend.mdm.workbench.enterprice.dialog.VersioningDialog;
import org.talend.mdm.workbench.enterprice.dialog.VersioningHistoryDialog;
import org.talend.mdm.workbench.enterprice.dialog.VersioningUniverseDialog;

import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.LineItem;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.VersionUtil;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSItemPK;

public class VersioningXObjectAction extends Action{
	
	public final static int ACTION_TYPE_VERSIONS = 1 << 1;
	public final static int ACTION_TYPE_HISTORY = 1 << 2;
	public final static int ACTION_TYPE_COMMIT = 1 << 3;
	public final static int ACTION_TYPE_TAGUNIVERSE = 1 << 4;

	private Shell shell = null;
	private Viewer viewer = null;
	private TreeObject sampleXObject=null;
	private boolean isItems=false;
	private int actionType;
	
	/**
	 * @param serverView
	 * 
	 * used for objects
	 */
	public VersioningXObjectAction(ServerView serverView, int actionType) {

		this(serverView.getSite().getShell(),serverView.getViewer(),actionType);
		this.isItems=false;
	}
	
	/**
	 * @param shell
	 * @param viewer
	 * @param xObject
	 * 
	 * used for items
	 */
	public VersioningXObjectAction(Shell shell,Viewer viewer,TreeObject xObject, int actionType) {

		this(shell,viewer,actionType);
		this.sampleXObject=xObject;
		this.isItems=true;
	}
	
	protected VersioningXObjectAction(Shell shell,Viewer viewer, int actionType) {
		super();
		this.shell=shell;
		this.viewer = viewer;
		this.actionType = actionType;
		setActionProfile(actionType);
		
	}

	private void setActionProfile(int actionType) {
		
		switch (actionType) {
		case ACTION_TYPE_VERSIONS:
			setImageDescriptor(ImageCache.getImage( "icons/versioning.gif"));
			setText("Versioning");
			setToolTipText("Manages the versioning of the Object/Item(s)");
			break;
		case ACTION_TYPE_TAGUNIVERSE:
			setImageDescriptor(ImageCache.getImage( "icons/versioning.gif"));
			setText("Versioning");
			setToolTipText("Manages the versioning of the Object/Item(s)");
			break;
		case ACTION_TYPE_HISTORY:
			setImageDescriptor(ImageCache.getImage( "icons/versioning.gif"));
			setText("Show History");
			setToolTipText("Show history of this item");
			break;
		case ACTION_TYPE_COMMIT:
			setImageDescriptor(ImageCache.getImage( "icons/versioning.gif"));
			setText("Commit");
			setToolTipText("Commit the item(s)");
			break;
		default:
			break;
		}
	}
	
	public void run() {
		try {
			super.run();
			IStructuredSelection selection = (IStructuredSelection)viewer.getSelection();
			Iterator<TreeObject> selectedIterator=selection.iterator();
			
			//validate
			if(selection.isEmpty()) {
					MessageDialog.openWarning(
							shell, 
							"Warning", 
							"Please select an Object/Item first! "
					);
					return;
			}
			
			//get SampleXObject
			if(!isItems){
				sampleXObject=(TreeObject) selection.getFirstElement();
			}
			
			if(!isItems){
				
				//classified tree objects
				Map<Integer, List<TreeObject>> selectedObjectsMap=new HashMap<Integer, List<TreeObject>>();
				for (;selectedIterator.hasNext();) {
					TreeObject treeObject = (TreeObject) selectedIterator.next();
					Integer treeObjectType = new Integer(treeObject.getType());
					//first time
					if(selectedObjectsMap.get(treeObjectType)==null){
						List<TreeObject> list=new ArrayList<TreeObject>();
						if(treeObject.isXObject())list.add(treeObject);
						selectedObjectsMap.put(treeObjectType, list);
					//more times	
					}else{
						List<TreeObject> list=selectedObjectsMap.get(treeObjectType);
						if(treeObject.isXObject()){
							if(list.size()>=1)list.add(treeObject);
						}else{
							list=new ArrayList<TreeObject>();
						}
						selectedObjectsMap.put(treeObjectType, list);
					}	
				}
				

				if(selectedObjectsMap.size()>1){
					    //TODO support muti-type
						MessageDialog.openWarning(
								shell, 
								"Warning", 
								"Versioning is not supported for muti-type objects! "
						);
						return;
				}
				
				
				//get type
				String type=null;
				if(sampleXObject!=null)type=VersionUtil.determineTypeByTreeObjectType(sampleXObject.getType());
				if (type==null) {
					MessageDialog.openWarning(
							shell, 
							"Versioning Not Supported", 
							"Versioning is not supported for these deprecated objects"
					);
					return;
				}
				
				//get instances
				String[] instances=null;
				
				List<TreeObject> treeObjects=selectedObjectsMap.get(new Integer(sampleXObject.getType()));
				if(treeObjects.size()>0){
					instances=new String[treeObjects.size()];
					for (int i = 0; i < treeObjects.size(); i++) {
						instances[i]=VersionUtil.determineInstanceByTreeObjectType(sampleXObject.getType(),treeObjects.get(i));
					}
				}
				  
		        //open dialog
				if(sampleXObject!=null){
					Dialog	dialog = null;
					
					switch (actionType) {
					case VersioningXObjectAction.ACTION_TYPE_VERSIONS:
						dialog = new VersioningDialog(
								shell, 
								sampleXObject,
								type,
								instances
						);
						break;
					case VersioningXObjectAction.ACTION_TYPE_TAGUNIVERSE:
						dialog = new VersioningUniverseDialog(
								shell,
								Util.getPort(sampleXObject),
								selection,
								sampleXObject
						);
						break;
					default:
						MessageDialog.openWarning(
								shell, 
								"Sorry", 
								"The Action-Type is not supported by Talend MDM. "
						);
						break;
					}
					
					if(dialog!=null){
						dialog.setBlockOnOpen(true);
						dialog.open();
					}
				}
				
			}else{
				
				//get itemPKs
				WSItemPK[] wsItemPKs=null;
				List<LineItem> lineItems = selection.toList();
				wsItemPKs=new WSItemPK[lineItems.size()];
				for (int i = 0; i < lineItems.size(); i++) {
					LineItem lineItem = lineItems.get(i);
					wsItemPKs[i]=new WSItemPK(
									(WSDataClusterPK)sampleXObject.getWsKey(),
									lineItem.getConcept(),
									lineItem.getIds()
						         );
				}
				
				//open dialog
				if(sampleXObject!=null){
					Dialog	dialog = null;
					
					switch (actionType) {
					case VersioningXObjectAction.ACTION_TYPE_VERSIONS:
						dialog = new VersioningDialog(
								shell, 
								Util.getPort(sampleXObject),
								wsItemPKs
						);
						break;
					case VersioningXObjectAction.ACTION_TYPE_HISTORY:
						dialog = new VersioningHistoryDialog(
								shell, 
								sampleXObject,
								wsItemPKs
						);
						break;
					case VersioningXObjectAction.ACTION_TYPE_COMMIT:
						dialog = new VersioningCommitDialog(
								shell, 
								Util.getPort(sampleXObject),
								wsItemPKs
						);
						break;
					default:
						MessageDialog.openWarning(
								shell, 
								"Sorry", 
								"The Action-Type is not supported by Talend MDM. "
						);
						break;
					}
					
					if(dialog!=null){
						dialog.setBlockOnOpen(true);
						dialog.open();
					}
					
				}
				
			}
			
                   
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					shell,
					"Error", 
					"An error occured trying to version the "+IConstants.TALEND+" object: "+e.getLocalizedMessage()
			);
		}		
	}
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}