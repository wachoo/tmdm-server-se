/**
 * 
 */
package com.amalto.core.objects.configurationinfo.assemble;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;


public class AssembleProc implements Serializable{
	
	private static final Logger logger = Logger.getLogger(AssembleProc.class);
	
	private List<AssembleSubProc> runtimeAssembleSubProcEntities = new ArrayList<AssembleSubProc>();

	/** 
	 * This construtor creates a AssembleProc instance.
	 */
	public AssembleProc() {
		super();
	}
	
	public void add(AssembleSubProc subProc) {
		
		runtimeAssembleSubProcEntities.add(subProc);

	}
	
	public void run() {
		
		for (Iterator<AssembleSubProc> iterator = runtimeAssembleSubProcEntities.iterator(); iterator.hasNext();) {
			AssembleSubProc subProc = (AssembleSubProc) iterator.next();
			try {
				logger.info("----Starting "+subProc.getClass().getName()+"... ");
				subProc.run();
				logger.info("----Done "+subProc.getClass().getName()+". ");
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("Failed to execute "+subProc.getClass().getName()+"! ");
			}
			
		}

	}

}
