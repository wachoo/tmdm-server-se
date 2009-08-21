/*
 * Created on 7 oct. 2005
 */
package com.amalto.core.util;

import java.util.HashMap;

import com.swabunga.spell.engine.Configuration;

/**
 * @author bgrieder
 *
 *"Hardcoded" configuration for Jazzy
 */
public class JazzyConfiguration extends Configuration {

	
	private HashMap<String, Object> props = new HashMap<String, Object>();
	
	public JazzyConfiguration() {

		setInteger(COST_CHANGE_CASE,1);
		setInteger(COST_REMOVE_CHAR,100);
		//setInteger(COST_SUBST_CHARS,100);
		setInteger(COST_INSERT_CHAR,80);
		setInteger(COST_SWAP_CHARS,50);
		setInteger(COST_SUBST_CHARS,70);
		
		setInteger(SPELL_THRESHOLD,150);
		//setInteger(SPELL_THRESHOLD,140);
		//setInteger(SPELL_THRESHOLD,10000);
		setBoolean(SPELL_IGNOREUPPERCASE,false);
		setBoolean(SPELL_IGNOREMIXEDCASE,false);
		setBoolean(SPELL_IGNOREINTERNETADDRESSES,false);
		setBoolean(SPELL_IGNOREDIGITWORDS,true);
		setBoolean(SPELL_IGNOREMULTIPLEWORDS,false);
		setBoolean(SPELL_IGNORESENTENCECAPITALIZATION,true);
				
	}
	
	/* (non-Javadoc)
	 * @see com.swabunga.spell.engine.Configuration#getInteger(java.lang.String)
	 */
	public int getInteger(String key) {
		if (props.get(key)!=null) return ((Integer)props.get(key)).intValue();
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.swabunga.spell.engine.Configuration#getBoolean(java.lang.String)
	 */
	public boolean getBoolean(String key) {
		if (props.get(key)!=null) return ("true".equals(props.get(key)));
		return false;
	}

	/* (non-Javadoc)
	 * @see com.swabunga.spell.engine.Configuration#setInteger(java.lang.String, int)
	 */
	public void setInteger(String key, int value) {
		props.put(key, new Integer(value));
	}

	/* (non-Javadoc)
	 * @see com.swabunga.spell.engine.Configuration#setBoolean(java.lang.String, boolean)
	 */
	public void setBoolean(String key, boolean value) {
		props.put(key, (value ? "true" : "false"));
	}

	/*****************************************************************************
	 * Added by BG 
	 * 
	 *****************************************************************************/
	 public int getAverageTreshold() {
		 return getInteger(SPELL_THRESHOLD);
	 }
	 
	 public int getMaxTreshold() {
		 return 3*getAverageTreshold()+1;
	 }
	 
	 public int getMinTreshold() {
		 return 1;
	 }
	
}
