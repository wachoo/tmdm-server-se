package com.amalto.core.plugin.base.workflowtrigger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CompiledParameters implements Serializable {
	
	private String initialContextFactory = null;
	private String providerURL = null;
	private String apiType = null;
	private String packageId = null;
	private String processId = null;
	private String processVersion = null;
	private String username = null;
	private String password = null;
	private VariableParameter[] variableParameters = null;
	
	
	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	public void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}

	public String getProviderURL() {
		return providerURL;
	}

	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public String getPackageId() {
		return packageId;
	}

	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	public String getProcessId() {
		return processId;
	}

	public void setProcessId(String processId) {
		this.processId = processId;
	}

	public String getProcessVersion() {
		return processVersion;
	}

	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public VariableParameter[] getVariableParameters() {
		return variableParameters;
	}

	public void setVariableParameters(VariableParameter[] variableParameters) {
		this.variableParameters = variableParameters;
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
	
	public static void main(String[] args) {
		CompiledParameters params=new CompiledParameters();
		params.setApiType("EJB2");
		params.setUsername("Test");
		VariableParameter vparam=new VariableParameter();
		vparam.setName("vp");
		vparam.setFromItem(false);
		params.setVariableParameters(new VariableParameter[]{vparam});
		try {
			String str=params.serialize();
			System.out.println(str);
			System.out.println(params.deserialize(str).getApiType());
			System.out.println(params.deserialize(str).getUsername());
			VariableParameter[] vps=params.deserialize(str).getVariableParameters();
			for (int i = 0; i < vps.length; i++) {
				System.out.println(vps[i].getName());
				System.out.println(vps[i].isFromItem());
				System.out.println(vps[i].getScope());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}


