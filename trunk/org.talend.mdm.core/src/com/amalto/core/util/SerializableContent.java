package com.amalto.core.util;


public class SerializableContent {
	byte[] bytes = null;;
	String contentType="";
	
	public SerializableContent() {
		super();
	}
	
	public SerializableContent(byte[] bytes, String contentType) {
		super();
		this.bytes = bytes;
		this.contentType = contentType;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
}
