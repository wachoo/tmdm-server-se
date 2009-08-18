package com.amalto.workbench.actions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Event;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.views.ServerView;

public class NewCategoryAction extends Action{
	private ServerView view = null;
	private TreeParent xfolder = null;
	
	public NewCategoryAction(ServerView view) {
		super();
		this.view = view;
		setImageDescriptor(ImageCache.getImage(EImage.COMPRESSED_FOLDER_OBJ.getPath()));
		setText("New Category");
		setToolTipText("Create a new Category for special XObjects");
	}
	
	public void run() {
		super.run();
        ISelection selection = view.getViewer().getSelection();
        TreeObject xobject = (TreeObject)((IStructuredSelection)selection).getFirstElement();
        xfolder = (TreeParent)xobject;
        
   		InputDialog id1 = new InputDialog(
   				view.getSite().getShell(),
   				"New Category",
   				"Enter a Name for the New Category",
   				null,
   				new IInputValidator() {
   					public String isValid(String newText) {
   						if ((newText==null) || "".equals(newText)) 
   							return "The Name cannot be empty";
   						Pattern p = Pattern.compile("([\\s*|\\W*]+)");
   				    	Matcher m = p.matcher(newText);
   						if(m.find()){
   							m.group(1);
   							return "The name cannot contains invalid character!";
   						}
   						TreeObject [] childList = xfolder.getChildren();
   						for (TreeObject theObj: childList)
   						{
   							if (theObj.getType() == TreeObject.CATEGORY_FOLDER)
   							{
   								if (theObj.getDisplayName().equals(newText))
   									return "The name is being used !";
   							}
   						}
   						return null;
   					};
   				}
   		);
   		id1.setBlockOnOpen(true);
   		if (id1.open() == Window.CANCEL) return;
   		String categoryName  = id1.getValue();
   		TreeParent category = new TreeParent(
				(String)categoryName,
				xfolder.getServerRoot(),
				TreeObject.CATEGORY_FOLDER,
				null,
				null); 
   		
   		xfolder.addChild(category);
	}
	
	
	public void runWithEvent(Event event) {
		super.runWithEvent(event);
	}
}
