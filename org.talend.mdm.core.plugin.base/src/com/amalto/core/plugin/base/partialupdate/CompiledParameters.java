package com.amalto.core.plugin.base.partialupdate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CompiledParameters implements Serializable {
	private String dataCluster = null;
	private String dataModel = null;
	private boolean overwrite = true;
	private String parentPivotPath = null;
	private String pivotLeaf = null;
	private String keyPathFromPivot = null;
	private int startingPosition = -1;
	
	public String getDataCluster() {
		return dataCluster;
	}
	public void setDataCluster(String dataCluster) {
		this.dataCluster = dataCluster;
	}
	public String getDataModel() {
		return dataModel;
	}
	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
	}
	public boolean isOverwrite() {
		return overwrite;
	}
	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}
	public String getKeyPathFromPivot() {
		return keyPathFromPivot;
	}
	public void setKeyPathFromPivot(String keyPathFromPivot) {
		this.keyPathFromPivot = keyPathFromPivot;
	}
	public String getParentPivotPath() {
		return parentPivotPath;
	}
	public void setParentPivotPath(String parentPivotPath) {
		this.parentPivotPath = parentPivotPath;
	}
	public String getPivotLeaf() {
		return pivotLeaf;
	}
	public void setPivotLeaf(String pivotLeaf) {
		this.pivotLeaf = pivotLeaf;
	}
	public int getStartingPosition() {
		return startingPosition;
	}
	public void setStartingPosition(int startingPosition) {
		this.startingPosition = startingPosition;
	}
	public String serialize() throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream ois = new ObjectOutputStream(bos);
		ois.writeObject(this);
		return new BASE64Encoder().encode(bos.toByteArray());
	}
	
	public static CompiledParameters deserialize(String base64String) throws IOException,ClassNotFoundException{
		byte[] bytes = new BASE64Decoder().decodeBuffer(base64String); 
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (CompiledParameters)ois.readObject();
	}
	
}


