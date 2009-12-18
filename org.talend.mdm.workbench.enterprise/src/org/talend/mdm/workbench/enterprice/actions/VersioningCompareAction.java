package org.talend.mdm.workbench.enterprice.actions;

import java.rmi.RemoteException;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;
import org.talend.mdm.commmon.util.core.ICoreConstants;

import com.amalto.workbench.compare.CompareManager;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSGetItem;
import com.amalto.workbench.webservices.WSItem;
import com.amalto.workbench.webservices.WSItemPK;
import com.amalto.workbench.webservices.WSString;
import com.amalto.workbench.webservices.WSVersioningGetItemContent;
import com.amalto.workbench.webservices.WSVersioningHistoryEntry;

/**
 * @author Starkey
 * 
 * compare with two revisions
 * 
 * used for items only currently
 *
 */
public class VersioningCompareAction  extends Action{
	
	protected Shell shell = null;
	protected Viewer viewer;
	protected TreeObject xobject=null;
	protected WSItemPK wsItemPK=null;
	
	public VersioningCompareAction(Shell shell,Viewer viewer,TreeObject xobject,WSItemPK wsItemPK) {
		super();
		this.shell = shell;
		this.viewer = viewer;
		this.xobject = xobject;
		this.wsItemPK = wsItemPK;
		setImageDescriptor(ImageCache.getImage(EImage.SYNCH.getPath()));
		setText("Compare with Each Other");
		setToolTipText("Compare item with different revisions");
	}
	
	public void run() {
		try {
			super.run();
			
			IStructuredSelection selection=((IStructuredSelection)viewer.getSelection());
			int selectSize=selection.size();
			if(selectSize!=2){
				MessageDialog.openWarning(null, "Warning", "Please select two revisions to compare! ");
				return;
			}
			
			List<WSVersioningHistoryEntry> liList = selection.toList();

			WSVersioningHistoryEntry leftHistoryEntry = liList.get(0);
			WSVersioningHistoryEntry rightHistoryEntry = liList.get(1);
				
			WSString leftItemHistoryContent=null;
			WSString rightItemHistoryContent=null;
			try{
				leftItemHistoryContent=Util.getPort(xobject).versioningGetItemContent(
						new WSVersioningGetItemContent(
								ICoreConstants.DEFAULT_SVN,
								wsItemPK,
								leftHistoryEntry.getRevision()
				));
				
				rightItemHistoryContent=Util.getPort(xobject).versioningGetItemContent(
						new WSVersioningGetItemContent(
								ICoreConstants.DEFAULT_SVN,
								wsItemPK,
								rightHistoryEntry.getRevision()
				));
			}catch (Exception e) {
				MessageDialog.openWarning(null, "Warning", e.getLocalizedMessage());
				return;
			}
			
			String leftContent=Util.getItemContent(leftItemHistoryContent.getValue());
			String rightContent=Util.getItemContent(rightItemHistoryContent.getValue());
			if(leftContent!=null&&rightContent!=null){
				CompareManager.getInstance().compareTwoStream(leftContent,rightContent,true,null,leftHistoryEntry.getRevision(),rightHistoryEntry.getRevision(),false,false);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					shell,
					"Error", 
					"An error occured trying to compare item with different revisions: "+e.getLocalizedMessage()
			);
		}		
	}
	
	private String getDataModelForItem(WSItemPK wsItemPK) throws RemoteException, XtentisException {
		
		WSItem wsItem=Util.getPort(xobject).getItem(
				new WSGetItem(
						wsItemPK
				)
		);
		return wsItem.getDataModelName();

	}

}
