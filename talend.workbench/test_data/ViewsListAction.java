package com.amalto.workbench.actions;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.amalto.workbench.dialogs.ViewsListDialog;

public class ViewsListAction extends AServerViewAction{
	
	public ViewsListAction() {
		super();
		setImageDescriptor(ImageCache.getImage( "icons/search.gif"));
		setText("Browse Views...");
		setToolTipText("Select a views from the ist and browse");
	}
	
	public void run() {
		try {
			super.run();
			ViewsListDialog dialog = new ViewsListDialog(getServerView());
			dialog.setBlockOnOpen(false);
			dialog.open();

		} catch (Exception e) {
			e.printStackTrace();
			MessageDialog.openError(
					getServerView().getSite().getShell(),
					"Error", 
					"An error occured trying to open the Views List Dialog: "+e.getLocalizedMessage()
			);
		}		
	}
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
	


}