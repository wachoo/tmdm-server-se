package com.amalto.workbench.models;

import java.util.ArrayList;

public class TreeParent extends TreeObject {

	private ArrayList children;
	
	public TreeParent(
			String displayName, 
			TreeParent serverRoot, 
			int type, 
			Object wsKey, 
			Object wsObject) {
		
		super(
				displayName,
				serverRoot, 
				type,
				wsKey,
				wsObject
		);
		children = new ArrayList();
		this.setXObject(false);
	}
	
	public void addChild(TreeObject child) {
		children.add(child);
		child.setParent(this);
		child.fireEvent(IXObjectModelListener.ADD, this, child);
	}
	
	public void removeChild(TreeObject child) {
		if (child instanceof TreeParent) {
			TreeObject[] subchildren = ((TreeParent)child).getChildren();
			for (int i = 0; i < subchildren.length; i++) {
				((TreeParent)child).removeChild(subchildren[i]);
			}
        }		
		
		children.remove(child);
		child.setParent(null);
		this.fireEvent(IXObjectModelListener.DELETE, this, child);
		
	}
    
    public void removeChildren() {
        TreeObject[] allchildren = this.getChildren();
        for (int i = 0; i < allchildren.length; i++) {
            this.removeChild(allchildren[i]);
        }
    }
	
	public TreeObject [] getChildren() {
		return (TreeObject [])children.toArray(new TreeObject[children.size()]);
	}
	
	public boolean hasChildren() {
		return children.size()>0;
	}
    
    
    public void synchronizeWith(TreeParent target) {
        TreeObject[] thisChildren = this.getChildren();
        TreeObject[] targetChildren = target.getChildren();
        //check what to remove
        for (int i = 0; i < thisChildren.length; i++) {
            TreeObject targetChild = null;
            for (int j = 0; j < targetChildren.length; j++) {
                if (
                        targetChildren[j].getDisplayName().equals(thisChildren[i].getDisplayName())
                        &&  targetChildren[j].getClass().equals(thisChildren[i].getClass())
                    ) {
                    targetChild = targetChildren[j];
                    break;
                }
            }//for target
            if (targetChild != null) { 
                if (thisChildren[i] instanceof TreeParent) ((TreeParent)thisChildren[i]).synchronizeWith((TreeParent)targetChild);
            } else {
                this.removeChild(thisChildren[i]);
            }
        }
        
        //check what to add
        for (int j = 0; j < targetChildren.length; j++) {
            boolean found = false;
            for (int i = 0; i < thisChildren.length; i++) {
                if (
                        targetChildren[j].getDisplayName().equals(thisChildren[i].getDisplayName())
                        &&  targetChildren[j].getClass().equals(thisChildren[i].getClass())
                    ) {
                    found = true;
                    break;
                }
            }//for thist
            if (!found) 
            {
            	this.addChild(targetChildren[j]); 
            	// fliu  set the root node value of the new created child  
            	targetChildren[j].setServerRoot(this.getServerRoot());
            }
        }//for add
        
    }//synchronizewith
    
    
	public TreeObject findObject(int type, String name) {
		TreeObject[] allchildren = this.getChildren();
		TreeObject obj=null;
		for (int i = 0; i < allchildren.length; i++) {
			if (allchildren[i] instanceof TreeParent) {
				obj= ((TreeParent)allchildren[i]).findObject(type, name);
			}
			else if (	(allchildren[i].getType() == type)
					&&(allchildren[i].getDisplayName().equals(name)))
				obj= allchildren[i];
		}
		return obj;
	}

	
}