package com.amalto.workbench.models;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.runtime.IAdaptable;

import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.LocalTreeObjectRepository;
import com.amalto.workbench.utils.UserInfo;

public class TreeObject implements IAdaptable {
	public static final String UNKNOWN="Unknown";
	
	public final static int _ROOT_ = -1;
	public final static int _SERVER_ = 0;
	
	public final static int INBOUND_ADAPTOR = 1;
	public final static int INBOUND_PLUGIN = 2;
	public final static int DESTINATION = 3;
	public final static int OUTBOUND_ADAPTOR = 4;
	public final static int OUTBOUND_PLUGIN = 5;
	public final static int DOCUMENT = 6;
	public final static int ITEM = 7;
	public final static int VIEW = 8;
	public final static int DATA_MODEL = 9;
	public final static int SOURCE = 10;
	public final static int DATA_CLUSTER = 11;
	public final static int STORED_PROCEDURE = 12;
	public final static int ROLE = 13;
	public final static int ROUTING_RULE = 14;
	public final static int SUBSCRIPTION_ENGINE = 15;
	public final static int TRANSFORMER = 16;
	public final static int MENU = 17;
	public final static int UNIVERSE=18;
	public final static int SYNCHRONIZATIONPLAN=19;
	public final static int TRANSFORMER_PLUGIN=20;
	public final static int CATEGORY_FOLDER = 21;
	
	public final static int _ACTION_ = 100;
	public final static int _WIZARD_ = 101;

	private String displayName;  
	private TreeParent parent;
	private TreeParent serverRoot;
	private Object wsKey;
	private Object wsObject;
	private int type;
	private boolean isXObject;
	private Object[] additionalInfo;
	
	private UserInfo user;
	protected ArrayList<IXObjectModelListener> listeners = null; //at root level only
	

	/**
	 * @param displayName
	 * @param serverRoot
	 * @param type
	 * @param wsKey
	 * @param wsObject
	 */
	public TreeObject(String displayName, TreeParent serverRoot, int type, Object wsKey, Object wsObject) {
		this(displayName,serverRoot,type,wsKey,wsObject,null);
	}
		
	public TreeObject(String displayName, TreeParent serverRoot, int type, Object wsKey, Object wsObject, Object[] additionalInfo) {
		super();
		this.displayName = displayName;
		this.serverRoot = serverRoot;
		this.type = type;
		this.wsKey = wsKey;
		this.wsObject = wsObject;
		this.isXObject = true;
		this.additionalInfo = additionalInfo;
		
		addListener(LocalTreeObjectRepository.getInstance());
	}

	protected void setParent(TreeParent parent) {
		this.parent = parent;
	}
	
	public TreeParent getParent() {
		return parent;
	}
	
	public String toString() {
		return getDisplayName();
	}
	
	public Object getAdapter(Class key) {
		return null;
	}
	


	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public TreeParent getServerRoot() {
		if (this.serverRoot == null) return (TreeParent)this; //we are the server root
		return serverRoot;
	}
	public void setServerRoot(TreeParent serverRoot) {
		this.serverRoot = serverRoot;
	}
	public Object getWsObject() {
		return wsObject;
	}
	public void setWsObject(Object wsObject) {
		this.wsObject = wsObject;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Object getWsKey() {
		return wsKey;
	}

	public void setWsKey(Object wsKey) {
		this.wsKey = wsKey;
	}

	
	public boolean isXObject() {
		return isXObject;
	}

	protected void setXObject(boolean isXObject) {
		this.isXObject = isXObject;
	}
	
	public void addListener(IXObjectModelListener listener) {
        //System.out.println("ADD LISTENER: "+listener.getClass().getName());
		if (listeners==null) {
			listeners = new ArrayList<IXObjectModelListener>();
			listeners.add(listener);
		} else {
			if (!listeners.contains(listener)) listeners.add(listener);
		}
	}
	public void removeListener(IXObjectModelListener listener) {
		if (listeners==null) return;
		if (listeners.contains(listener)) listeners.remove(listener);
	}
	
	public synchronized void fireEvent(int eventType, TreeObject objectParent, TreeObject child) {
		 TreeObject current = this;
		do {
			if (current.listeners!=null) {
				//System.out.println("FIRE EVENT on "+current.getDisplayName()+"  type: "+type+" Parent: "+(parent==null ? "no parent ":parent.getDisplayName())+"-->"+child.getDisplayName() +" --- "+current.listeners.size()+" listeners");
				for (Iterator<IXObjectModelListener> iter = current.listeners.iterator(); iter.hasNext(); ) {
					IXObjectModelListener listener =  iter.next();
					listener.handleEvent(eventType, objectParent, child);
				}
			} else {
			    //System.out.println("SKIP FIRE ON: "+current.getDisplayName());
            }
			current = current.getParent();
		} while ((current!=null));
	}

	public Object[] getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(Object[] additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	/*
	public TreeObject findObject(int type, String name) {
		if (	(this.getType() == type)
				&&(this.getDisplayName().equals(name)))
				return this;
		return null;
	}
	*/
	
	public TreeParent findServerFolder(int objectType) {
		if (this.getServerRoot() == null) return null;
		TreeObject[] children = this.getServerRoot().getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof TreeParent) 
				if (children[i].getType() == objectType)
					return (TreeParent)children[i];
		}
		return null;
	}
	

	public UserInfo getUser() {
		return user;
	}

	public void setUser(UserInfo user) {
		this.user = user;
	}

	public String getUsername() {
        return getServerRoot().getUser().getUsername();
	}
	
	public String getPassword() {
		return getServerRoot().getUser().getPassword();
	}
	
	public String getUniverse() {
		return getServerRoot().getUser().getUniverse();
	}
	
	public String getEndpointAddress() {
		return getServerRoot().getWsKey().toString();
	}
	
	
	public static String getTypeName(int type) {
		EXtentisObjects obj=EXtentisObjects.getXtentisObjexts().get(String.valueOf(type));
		if(obj!=null)
			return obj.getName();
		else
			return UNKNOWN;
//		switch (type) {
//			case INBOUND_ADAPTOR :
//				return "Inbound Adaptor";
//			case INBOUND_PLUGIN:
//				return "Inbound Plugin";
//			case DESTINATION :
//				return "Destination";
//			case OUTBOUND_ADAPTOR:
//				return "Outbound Adaptor";
//			case OUTBOUND_PLUGIN:
//				return "Outbound Plugin";
//			case DOCUMENT:
//				return "Document";
//			case ITEM:
//				return "Item";
//			case VIEW:
//				return "View";
//			case DATA_MODEL:
//				return "Data Model";
//			case SOURCE:
//				return "Source";
//			case DATA_CLUSTER:
//				return "Data Cluster";
//			case SUBSCRIPTION_ENGINE:
//				return "Subscription Engine";
//			case ROLE:
//				return "Role";
//			case STORED_PROCEDURE:
//				return "Stored Procedure";	
//			case TRANSFORMER:
//				return "Transfomer";
//			case MENU:
//				return "Menu";	
//			case UNIVERSE:
//				return "Universe";
//			case SYNCHRONIZATIONPLAN:
//				return "Synchronization Plan";
//		}
	}
		
}

