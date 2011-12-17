package com.amalto.core.plugin.base.groovy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CompiledParameters implements Serializable {
	
	private String script;
	
	private boolean autoParseXml;
	
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public boolean isAutoParseXml() {
		return autoParseXml;
	}

	public void setAutoParseXml(boolean autoParseXml) {
		this.autoParseXml = autoParseXml;
	}

	public String serialize() throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream ois = new ObjectOutputStream(bos);
		ois.writeObject(this);
		return new BASE64Encoder().encode(bos.toByteArray());
	}
	
	public static CompiledParameters deserialize(String base64String) throws IOException,ClassNotFoundException{
		if(base64String==null||base64String.length()==0)return new CompiledParameters();
		byte[] bytes = new BASE64Decoder().decodeBuffer(base64String); 
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (CompiledParameters)ois.readObject();
	}
	
}


