package com.amalto.workbench.models;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;


public class TreeObjectTransfer extends ByteArrayTransfer{
	private static final String TYPENAME = "treeobjectdata";
	private static final int TYPEID = registerType(TYPENAME);
	private static TreeObjectTransfer _instance = new TreeObjectTransfer();
	
	public static TreeObjectTransfer getInstance () {
		return _instance;
	}
	
	public void javaToNative (Object object, TransferData transferData) {
		if (!checkType(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		
		TreeObject[] treeObjs = (TreeObject[])object;
		ByteArrayOutputStream byteS = new ByteArrayOutputStream();
		DataOutputStream writeOut = new DataOutputStream(byteS);
		
		try {
			for (TreeObject treeData : treeObjs) {
				writeOut.writeInt(treeData.getType());
			}
			byte[] buffer = byteS.toByteArray();
			super.javaToNative(buffer, transferData);
			writeOut.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
		}	
		
	}
	
	public Object nativeToJava(TransferData transferData){	
		if (!isSupportedType(transferData)) return null;
			
		byte[] buffer = (byte[])super.nativeToJava(transferData);
		if (buffer == null) return null;
		
		return null;
	}
	
	protected String[] getTypeNames(){
		return new String[]{TYPENAME};
	}
	protected int[] getTypeIds(){
		return new int[] {TYPEID};
	}
	boolean checkType(Object object) {
		return (object != null  && object instanceof TreeObject[]);
	}
	protected boolean validate(Object object) {
		return checkType(object);
	}
}
