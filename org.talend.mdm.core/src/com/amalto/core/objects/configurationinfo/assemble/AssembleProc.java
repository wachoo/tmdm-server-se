/**
 * 
 */
package com.amalto.core.objects.configurationinfo.assemble;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


public class AssembleProc implements Serializable{
	
	private static final Logger logger = Logger.getLogger(AssembleProc.class);
	
	private final List<AssembleSubProc> runtimeAssembleSubProcEntities = new LinkedList<AssembleSubProc>();

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
        for (AssembleSubProc subProc : runtimeAssembleSubProcEntities) {
            try {
                logger.info("----Starting " + subProc.getClass().getName() + "... ");
                subProc.run();
                logger.info("----Done " + subProc.getClass().getName() + ". ");
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Failed to execute " + subProc.getClass().getName() + "! ");
            }
        }
	}

}
