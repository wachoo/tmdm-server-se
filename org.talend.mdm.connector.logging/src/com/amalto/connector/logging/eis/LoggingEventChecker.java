package com.amalto.connector.logging.eis;

import javax.resource.spi.work.Work;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 * 
 * @author jfeltesse
 */
public class LoggingEventChecker extends Thread implements Work {
	
	
	private static final String BR = System.getProperty("line.separator");
	
	private LoggingListener caller;
	
	private boolean kill = false;
	
	
	
	public LoggingEventChecker(LoggingListener caller) {
		super("LoggingEventChecker");
		this.caller = caller;
	}
	
	
	
	public void run() {
		
		Logger.getLogger(this.getClass()).debug("run() : start running");
		
		while (!kill) {
			
			LoggingEvent le = caller.getPendingEvent();
			
			try {
				
				if (le == null) {
					synchronized (this) { wait(); }
					le = caller.getPendingEvent();
				}
				
				
				if (le != null) {
					
					StringBuffer sb = new StringBuffer();
					sb.append("<logging_event>"+BR);
					addXmlElement(sb, "time", DateFormatUtils.format(le.timeStamp, "yyyy-MM-dd HH:mm:ss,S"), false);
					addXmlElement(sb, "level", le.getLevel().toString(), false);
					addXmlElement(sb, "logger", le.getLoggerName(), false);
					addXmlElement(sb, "message", le.getRenderedMessage(), true);
					addXmlElement(sb, "ndc", le.getNDC(), true);
					addXmlElement(sb, "thread", le.getThreadName(), true);
					
					String[] throwable = le.getThrowableStrRep();
					if (throwable != null) {
						String tmp = "";
						for (int i = 0; i < throwable.length; i++) {
							tmp = tmp + throwable[i] + BR;
						}
						StringUtils.chop(tmp);	// Removes the last character
						addXmlElement(sb, "throwable", tmp, true);
					}
					sb.append("</logging_event>");
					
					caller.handlingDone(sb.toString());
				}
				
			}
			catch (Exception e) {
				kill = true;
				Logger.getLogger(this.getClass()).debug("run() : "+e.getMessage(), e);
			}
			
		}
		
		Logger.getLogger(this.getClass()).debug("run() : end running");
		kill = false;
	}
	
	
	public void release() {
		Logger.getLogger(this.getClass()).debug("release() : sending the kill signal");
		kill = true;
	}
	
	
	
	
	private void addXmlElement(StringBuffer target, String name, String value, boolean escapeXML) {
		
		if (value == null) value = "";
		else if (escapeXML) value = StringEscapeUtils.escapeXml(value);
		
		target.append("<"+name+">"+value+"</"+name+">"+BR);
	}
	
}
