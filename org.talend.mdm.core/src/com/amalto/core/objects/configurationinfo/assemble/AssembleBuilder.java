/**
 * 
 */
package com.amalto.core.objects.configurationinfo.assemble;


public interface AssembleBuilder {

	/** 
	 * This method constructs and assembles a particular part of a Proc.
	 */
	public void buildPartCleanJob();

	/** 
	 * This method constructs and assembles a particular part of a Proc.
	 */
	public void buildPartInitData();
	
	/** 
	 * This method constructs and assembles a particular part of a Proc.
	 */
	public void buildPartMigrateVersion();
	
	/** 
	 * This method constructs and assembles a particular part of a Proc.
	 */
	public void buildPartStartEngine();

}
