package com.amalto.core.util;

/**
 * 
 * @author bgrieder
 * @deprecated - use TransformerV2 package
 *
 */
public interface TransformerPluginCallBack {
	
	public void contentIsReady(int pluginHandle, TypedContent content, TransformerPluginContext context) throws XtentisException;
	
	public void done(int pluginHandle, TransformerPluginContext context) throws XtentisException;
	
	public void stopped(int pluginHandle, TransformerPluginContext context) throws XtentisException;

}
