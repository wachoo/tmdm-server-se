package org.talend.mdm.workbench.enterprice.dialog;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.workbench.enterprice.actions.VersioningProgressAction;

import com.amalto.workbench.actions.ServerRefreshAction;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.VersionUtil;
import com.amalto.workbench.views.ServerView;
import com.amalto.workbench.webservices.WSBackgroundJobPK;
import com.amalto.workbench.webservices.WSGetCurrentUniverse;
import com.amalto.workbench.webservices.WSGetItemsPivotIndexPivotWithKeysTypedContentEntry;
import com.amalto.workbench.webservices.WSLinkedHashMap;
import com.amalto.workbench.webservices.WSStringArray;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSVersioningGetUniverseVersions;
import com.amalto.workbench.webservices.WSVersioningRestoreUniverse;
import com.amalto.workbench.webservices.WSVersioningTagUniverse;
import com.amalto.workbench.webservices.WSVersioningUniverseVersions;
import com.amalto.workbench.webservices.WSVersioningUniverseVersionsTagStructure;
import com.amalto.workbench.webservices.XtentisPort;
import com.amalto.workbench.widgets.RepositoryCheckTreeViewer;

public class VersioningUniverseDialog extends Dialog {
	private IStructuredSelection sel;
	private RepositoryCheckTreeViewer treeViewer;
	private XtentisPort port;
	private TreeObject selectedXObject;
	
	public VersioningUniverseDialog(Shell parentShell,XtentisPort port,IStructuredSelection selection,TreeObject sampleXObject) {
		
		super(parentShell);
		this.sel=selection;
		this.port=port;
		this.selectedXObject=sampleXObject;
	}
	
	protected Control createDialogArea(Composite parent) {
		
		try {
			//Should not really be here but well,....
			parent.getShell().setText("Universe Versioning");
			Composite composite = (Composite) super.createDialogArea(parent);
			GridLayout layout = (GridLayout)composite.getLayout();
			layout.numColumns=1;
			treeViewer =new RepositoryCheckTreeViewer(sel,getDefautTag(),false);
			treeViewer.setTagSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {
					
					if ("".equals(treeViewer.getTagText())) {
						MessageDialog.openError(VersioningUniverseDialog.this.getShell(), "Error", "Please enter a tag value");
						return;
					}

					if ("".equals(treeViewer.getComment())) {
						MessageDialog.openError(VersioningUniverseDialog.this.getShell(), "Error", "Please enter a comment");
						return;
					}
					
					
					TreeObject[]  checkNodes=treeViewer.getCheckNodes();
					WSLinkedHashMap typeInstances=null;
					
					typeInstances = getTypeInstancesFromTreeObjects(checkNodes,typeInstances);
					
					
					//Tag universe
					try {
				        WSBackgroundJobPK jobPK =
				        	port.versioningTagUniverse(new WSVersioningTagUniverse(
									null,
									treeViewer.getTagText(),
									treeViewer.getComment(),
									typeInstances
									));
				        
				        new VersioningProgressAction(VersioningUniverseDialog.this.getShell(),port,jobPK).run();
				         
					} catch (Exception exx) {
						exx.printStackTrace();
						MessageDialog.openError(
								VersioningUniverseDialog.this.getShell(),
								"Error", 
								"Unable to tag the documents : "+exx.getLocalizedMessage()
						);
					}
					
					try {
						treeViewer.refreshHistoryTable(getHistoryData());
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					
				}
								
			});
			treeViewer.setRestoreSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {}
				public void widgetSelected(SelectionEvent e) {
					//Restore universe
					WSVersioningUniverseVersionsTagStructure entry = (WSVersioningUniverseVersionsTagStructure) ((IStructuredSelection)treeViewer.getTagsViewer().getSelection()).getFirstElement();
					if (entry == null) return;
					try {
				        WSBackgroundJobPK jobPK = 
					        port.versioningRestoreUniverse(new WSVersioningRestoreUniverse(
					        		null,
					        		entry.getTagName(),
					        		entry.getClusters().getStrings()
					    ));
				        
				        new VersioningProgressAction(VersioningUniverseDialog.this.getShell(),port,jobPK).run();

					} catch (Exception exx) {
						exx.printStackTrace();
						MessageDialog.openError(
								VersioningUniverseDialog.this.getShell(),
								"Error", 
								"Unable to restore the documents : "+exx.getLocalizedMessage()
						);
					}
					ServerRefreshAction serverRefreshAction=new ServerRefreshAction(ServerView.show(),VersioningUniverseDialog.this.selectedXObject.getServerRoot());
					serverRefreshAction.run();
				}				
			});
			treeViewer.setTagsViewerDoubleClickListener(new IDoubleClickListener() {
	        	public void doubleClick(DoubleClickEvent event) {
	        		//Restore universe
					WSVersioningUniverseVersionsTagStructure entry = (WSVersioningUniverseVersionsTagStructure) ((IStructuredSelection)treeViewer.getTagsViewer().getSelection()).getFirstElement();
					if (entry == null) return;
					try {
				        WSBackgroundJobPK jobPK = 
					        port.versioningRestoreUniverse(new WSVersioningRestoreUniverse(
					        		null,
					        		entry.getTagName(),
					        		entry.getClusters().getStrings()
					    ));
				        
				        new VersioningProgressAction(VersioningUniverseDialog.this.getShell(),port,jobPK).run();

					} catch (Exception exx) {
						exx.printStackTrace();
						MessageDialog.openError(
								VersioningUniverseDialog.this.getShell(),
								"Error", 
								"Unable to restore the documents : "+exx.getLocalizedMessage()
						);
					}
					ServerRefreshAction serverRefreshAction=new ServerRefreshAction(ServerView.show(),VersioningUniverseDialog.this.selectedXObject.getServerRoot());
					serverRefreshAction.run();
	            }
            });
			treeViewer.setHisEntries(getHistoryData());
			treeViewer.createContents(composite);
			
		    return composite;
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					this.getShell(),
					"Error", 
					"An error occured trying to create the Versioning Dialog: "+e.getLocalizedMessage()
			);
			return null;
		}
	}

	private ArrayList<WSVersioningUniverseVersionsTagStructure> getHistoryData() throws Exception{
		//Init history universes in svn
		ArrayList<WSVersioningUniverseVersionsTagStructure> history = new ArrayList<WSVersioningUniverseVersionsTagStructure>();
		try {
			WSVersioningUniverseVersions universeVersions = port.versioningGetUniverseVersions(new WSVersioningGetUniverseVersions(null));
			WSVersioningUniverseVersionsTagStructure[] tagsStructure = universeVersions.getTagStructure();
			
			if(tagsStructure!=null&&tagsStructure.length>0){
				for (int i = 0; i < tagsStructure.length; i++) {
					WSVersioningUniverseVersionsTagStructure tagStructure=tagsStructure[i];
					history.add(tagStructure);
				}
			}
			
		} catch (RemoteException e) {
			throw new Exception(e);
		}

		return history;
	}
	
	private String getDefautTag() {
		String tagName="";
		try {
			WSUniverse CUniverse=port.getCurrentUniverse(new WSGetCurrentUniverse());
			tagName = "UNIVERSE_"+CUniverse.getName();//UNIVERSE is keyword
			tagName = tagName.replace("[", "").replace("]", "");
			
			//timestamp
			Date dateNow=new Date();
			SimpleDateFormat  dateFormat=new SimpleDateFormat ("yyyyMMddHHmmss");
		    String dateNowStr=dateFormat.format(dateNow);
			tagName+="_"+dateNowStr;
			
		} catch (RemoteException e) {
			e.printStackTrace();
		}
        return tagName;
	}
	
	private WSLinkedHashMap getTypeInstancesFromTreeObjects(
			TreeObject[] checkNodes, WSLinkedHashMap typeInstances) {
		Map<String, List> classifiedMap=new HashMap<String, List>();
		if(checkNodes.length>0){
			for (int i = 0; i < checkNodes.length; i++) {
				
				TreeObject checkNode=checkNodes[i];
				String type=VersionUtil.determineTypeByTreeObjectType(checkNode.getType());
				String instance=VersionUtil.determineInstanceByTreeObjectType(checkNode.getType(), checkNode);
				
				if(classifiedMap.get(type)==null){
					List<String> instances=new ArrayList<String>();
					instances.add(instance);
					classifiedMap.put(type,instances);
				}else{
					List<String> instances=classifiedMap.get(type);
					instances.add(instance);
				}
			}
		}
	    
		if(classifiedMap.size()>0){
			WSGetItemsPivotIndexPivotWithKeysTypedContentEntry[] typedContentEntries=new WSGetItemsPivotIndexPivotWithKeysTypedContentEntry[classifiedMap.size()];
			int i=0;
			for (Iterator iterator = classifiedMap.keySet().iterator(); iterator.hasNext();i++) {
				String type = (String) iterator.next();
				List<String> instancesList=classifiedMap.get(type);
				String[] instances = instancesList.toArray(new String[instancesList.size()]);
				WSGetItemsPivotIndexPivotWithKeysTypedContentEntry typedContentEntry=new WSGetItemsPivotIndexPivotWithKeysTypedContentEntry(type,new WSStringArray(instances));
				typedContentEntries[i]=typedContentEntry;
			}
			
			typeInstances=new WSLinkedHashMap(typedContentEntries);
		}
		return typeInstances;
	}
	
}
