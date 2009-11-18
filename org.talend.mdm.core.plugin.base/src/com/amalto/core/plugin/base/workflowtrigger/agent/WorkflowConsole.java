package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WorkflowConsole {
	
	private StringWriter broad;
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public WorkflowConsole() {
		broad=new StringWriter();
	}
	
	public void writeln(String msg) {
		this.broad.write("[");
		this.broad.write(df.format(new Date()));
		this.broad.write("]:");
		this.broad.write(msg);
		this.broad.write("\n");
	}
	public String getContent() {
		return getContent(true);
	}
	
	public String getContent(boolean withFlush) {
		String content=this.broad.getBuffer().toString();
		if(withFlush)this.broad.flush();
		return content;
	}

}
