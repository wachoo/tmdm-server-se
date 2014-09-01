package com.amalto.core.util;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @deprecated - use TransformerV2 package
 * @author bgrieder
 *
 */

public class TransformerPluginContext {
	
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

}
