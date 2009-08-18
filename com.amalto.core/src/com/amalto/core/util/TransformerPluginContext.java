package com.amalto.core.util;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.TransformerCtrlBean;
import com.amalto.core.ejb.TransformerPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;

/**
 * @deprecated - use TransformerV2 package
 * @author bgrieder
 *
 */

public class TransformerPluginContext {
	
	protected TransformerContext underlyingNewContext = null;
	
	private Map<String, Object> map = Collections.synchronizedMap(new HashMap<String, Object>());
	
	public void put(String key, Object value) {
		map.put(key, value);
	}

	public Object get(String key) {
		return map.get(key);
	}
	
	public Set<String> getKeys() {
		return map.keySet();
	}
	
	public Object remove(String key) {
		return map.remove(key);
	}
	
	public static TransformerContext getNewTransformerContext(TransformerPluginContext oldContext, TransformerPOJOPK transformerPOJOPK) {
    	TransformerContext newContext = new TransformerContext(new TransformerV2POJOPK(transformerPOJOPK.getUniqueId()));
    	
		synchronized (oldContext) {
	    	HashMap<String, TypedContent> pipeline = (HashMap<String, TypedContent>) oldContext.get(TransformerCtrlBean.CTX_PIPELINE);
	    	if (pipeline != null) {
		    	Set<String> variables = pipeline.keySet();
		    	for (Iterator iter = variables.iterator(); iter.hasNext(); ) {
					String variable= (String) iter.next();
					TypedContent ctnt = pipeline.get(variable);
					newContext.putInPipeline(variable, TypedContent.getNewTypedContent(ctnt));
				}
	    	}
	    	Set keys = oldContext.getKeys();
	    	for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				newContext.put(key, oldContext.get(key));
			}
		}
    	return newContext;
	}

	
	public static TransformerPluginContext getOldTransformerContext(TransformerContext newContext) throws IOException{
		org.apache.log4j.Logger.getLogger(TransformerPluginContext.class).debug("getOldTransformerContext() ");
    	TransformerPluginContext oldContext = new TransformerPluginContext();
    	oldContext.underlyingNewContext = newContext;
    	
    	synchronized (newContext) {
    		HashMap<String, TypedContent> pipeline = new HashMap<String, TypedContent>();
    		//Process keys
	    	Set<String> keys = newContext.keySet();
	    	for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				//org.apache.log4j.Logger.getLogger(TransformerPluginContext.class).debug("getOldTransformerContext() key "+key);
				oldContext.put(key, newContext.get(key));
			}
	    	//process pipeline
	    	Set variables = newContext.getPipelineVariableNames();
	    	for (Iterator iter = variables.iterator(); iter.hasNext(); ) {
				String variable = (String) iter.next();
				pipeline.put(variable, TypedContent.getOldTypedContent(newContext.getFromPipeline(variable)));
//				org.apache.log4j.Logger.getLogger(TransformerPluginContext.class).debug("getOldTransformerContext() pipeline "+variable+" ---> "+newContext.getFromPipeline(variable).toString());
			}
	    	oldContext.put(TransformerCtrlBean.CTX_PIPELINE, pipeline);
	    	//process projected PKs
	    	SortedSet<ItemPOJOPK> pks = newContext.getProjectedPKs();
	    	oldContext.put(TransformerCtrlBean.CTX_PKS, new ArrayList<ItemPOJOPK>(pks));
    	}
    	//dumpPipeline(oldContext);
    	return oldContext;
	}
	
	
	public static void dumpPipeline(TransformerPluginContext context) {
		org.apache.log4j.Logger.getLogger(TransformerPluginContext.class).debug("dumpPipeline() ");
		//dump pipeline
		HashMap<String, TypedContent> pipeline  = (HashMap<String, TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
		Set<String> keys = new TreeSet(pipeline.keySet());
		for (Iterator iter2 = keys.iterator(); iter2.hasNext(); ) {
			String key = (String) iter2.next();
			org.apache.log4j.Logger.getLogger(TransformerPluginContext.class).info("Pipeline Dump: "+key+" -"+pipeline.get(key));
		}
	}
}
