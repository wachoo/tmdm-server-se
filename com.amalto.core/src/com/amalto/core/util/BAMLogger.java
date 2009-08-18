package com.amalto.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;

public class BAMLogger {
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.S z");
	private static boolean log = false;
	
	static {
		String val = (String)MDMConfiguration.getConfiguration().get("bam.logging");
		if ("true".equals(val) || "yes".equals(val)) log=true;
	}
	
	public static void log(
			String key,
			String username,
			String permission,
			Class<? extends ObjectPOJO> objectClass,
			ObjectPOJOPK pk,
			boolean authorized 
	) {
		if (!log) return;
		String line =
			"[BAM "+key+"] "+
			"[DATE "+sdf.format(new Date(System.currentTimeMillis()))+"] "+
			"[USER "+username+"] " +
			"[PERMISSION "+permission+"] " +
			"[OBJECT "+ObjectPOJO.getObjectName(objectClass)+"] " +
			"[INSTANCE "+pk.getUniqueId()+"] " +
			"[AUTHORIZED"+(authorized?" YES":" NO")+"]";

		if ("read".equals(permission)) 
			org.apache.log4j.Logger.getLogger(BAMLogger.class).debug(line);
		else
			org.apache.log4j.Logger.getLogger(BAMLogger.class).info(line);
	}

}
