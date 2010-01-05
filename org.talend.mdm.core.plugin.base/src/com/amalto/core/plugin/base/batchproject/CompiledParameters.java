package com.amalto.core.plugin.base.batchproject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CompiledParameters implements Serializable {
	
	private String dataClusterName = null;
	private String dataModelName = null;
	private String conceptName = null;
	private boolean overwrite=true;
	
	public String getDataClusterName() {
		return dataClusterName;
	}

	public void setDataClusterName(String dataClusterName) {
		this.dataClusterName = dataClusterName;
	}

	public String getDataModelName() {
		return dataModelName;
	}

	public void setDataModelName(String dataModelName) {
		this.dataModelName = dataModelName;
	}

	public String getConceptName() {
		return conceptName;
	}

	public void setConceptName(String conceptName) {
		this.conceptName = conceptName;
	}
	
	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
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


