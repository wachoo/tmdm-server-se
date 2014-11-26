package com.amalto.core.objects.transformers.util;

import java.io.IOException;
import java.io.InputStream;


public class TypedContent_Do_Not_Process extends TypedContent {
	
	TypedContent content = null;
	
	public TypedContent_Do_Not_Process(){
		super();
	}
	
	public TypedContent_Do_Not_Process(TypedContent content){
		this.content = content;
	}

	@Override
	public void closeContentStream() {
		if (content!=null) content.closeContentStream();
	}

	@Override
	public byte[] getContentBytes() throws IOException {
		if (content!=null) return content.getContentBytes();
		return null;
	}

	@Override
	public InputStream getContentStream() throws IOException {
		if (content!=null) return content.getContentStream();
		return null;
	}

	@Override
	public String getContentType() {
		if (content!=null) return content.getContentType();
		return null;
	}

	@Override
	public String getUrl() {
		if (content!=null) return content.getUrl();
		return null;
	}
	
}
