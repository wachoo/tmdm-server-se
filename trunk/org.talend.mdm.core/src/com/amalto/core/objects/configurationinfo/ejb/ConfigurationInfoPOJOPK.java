package com.amalto.core.objects.configurationinfo.ejb;

import com.amalto.core.ejb.ObjectPOJOPK;


public class ConfigurationInfoPOJOPK extends ObjectPOJOPK{
	
	/**
	 * @param pk
	 */
	public ConfigurationInfoPOJOPK(ObjectPOJOPK pk) {
		super(pk.getIds());
	}
	
	/**
	 * @param name
	 */
	public ConfigurationInfoPOJOPK(String name) {
		super(name);
	}

}
