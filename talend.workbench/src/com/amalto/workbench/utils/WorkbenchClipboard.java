package com.amalto.workbench.utils;

import java.util.ArrayList;

import com.amalto.workbench.models.TreeObject;

public class WorkbenchClipboard {
	
	
	//singleton
	private static WorkbenchClipboard instance;
	
	protected WorkbenchClipboard() {}
	
	public static WorkbenchClipboard getWorkbenchClipboard() {
		if (instance==null) instance = new WorkbenchClipboard();
		return instance;
	}
	
	
	//holds a vector of copied TreeObject
	ArrayList<TreeObject> list = new ArrayList<TreeObject>();
	
	public void put(ArrayList<TreeObject> list) {
		this.list = list;
	}

	public ArrayList<TreeObject>  get() {
		return this.list;
	}
	
	public void reset() {
		this.list = new ArrayList<TreeObject>();
	}
	
	public void add(TreeObject obj) {
		this.list.add(obj);
	}
	
	public void remove(TreeObject obj) {
		this.list.remove(obj);
	}
	
	public boolean isEmpty() {
		return (this.list == null) || (this.list.size() == 0);
	}
	
}
