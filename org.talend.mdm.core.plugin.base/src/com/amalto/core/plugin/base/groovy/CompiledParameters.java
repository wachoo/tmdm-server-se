/*
 * Copyright (C) 2006-2018 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.plugin.base.groovy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

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
		return new String(Base64.getEncoder().encode(bos.toByteArray()));
	}
	
	public static CompiledParameters deserialize(String base64String) throws IOException,ClassNotFoundException{
		if(base64String==null||base64String.length()==0)return new CompiledParameters();
		byte[] bytes = Base64.getDecoder().decode(base64String); 
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bis);
		return (CompiledParameters)ois.readObject();
	}
	
}


