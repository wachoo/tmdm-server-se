package org.talend.mdm.workbench.enterprice.availablemodel;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.talend.mdm.workbench.enterprice.actions.VersioningXObjectAction;

import com.amalto.workbench.availablemodel.AbstractAvailableModel;
import com.amalto.workbench.editors.DataClusterBrowserMainPage;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.views.ServerView;

public class SvnAvailableModel extends AbstractAvailableModel {
	@Override
	public void fillContextMenu(TreeObject xobject,IMenuManager manager) {
		switch(xobject.getType()){
		case TreeObject._SERVER_:
			manager.add(new VersioningXObjectAction(ServerView.show(),VersioningXObjectAction.ACTION_TYPE_TAGUNIVERSE));
			break;
		default:
			if(xobject.getType()!=TreeObject.WORKFLOW_PROCESS && xobject.getType()!=TreeObject.JOB && Util.hasTags(xobject) && xobject.getType()!=TreeObject.DATA_MODEL_TYPES_RESOURCE && xobject.getType()!=TreeObject.DATA_MODEL_RESOURCE)
				manager.add(new VersioningXObjectAction(ServerView.show(),VersioningXObjectAction.ACTION_TYPE_VERSIONS));
			break;
		}
		
	}
	
	@Override
	public void menuAboutToShow(IMenuManager manager, DataClusterBrowserMainPage page) {
		manager.appendToGroup(
				IWorkbenchActionConstants.MB_ADDITIONS,
				new VersioningXObjectAction(
						page.getSite().getShell(),
						page.getResultsViewer(),
						page.getXObject(),
						VersioningXObjectAction.ACTION_TYPE_COMMIT
				)
		);
		manager.appendToGroup(
				IWorkbenchActionConstants.MB_ADDITIONS,
				new VersioningXObjectAction(
						page.getSite().getShell(),
						page.getResultsViewer(),
						page.getXObject(),
						VersioningXObjectAction.ACTION_TYPE_HISTORY
				)
		);
		manager.appendToGroup(
				IWorkbenchActionConstants.MB_ADDITIONS,
				new VersioningXObjectAction(
						page.getSite().getShell(),
						page.getResultsViewer(),
						page.getXObject(),
						VersioningXObjectAction.ACTION_TYPE_VERSIONS
				)
		);
		//compare item with each other
		manager.appendToGroup(
				IWorkbenchActionConstants.MB_ADDITIONS,
				page.new CompareItemWithEachOtherAction(
						page.getSite().getShell(),
						page.getResultsViewer()
				)
		);
		//compare item with svn
		manager.appendToGroup(
				IWorkbenchActionConstants.MB_ADDITIONS,
				page.new CompareItemWithLatestRevisionAction(
						page.getSite().getShell(),
						page.getResultsViewer()
				)
		);
	}
}
