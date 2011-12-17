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
	
	/**
	 * This method resend the autocommittosvn failed message
	 */
	public void buildFaildMessage();
	/** 
	 * This method constructs and assembles a particular part of a Proc.
	 */
	public void buildStartService();
	
	/** 
	 * This method constructs and assembles a particular part of a Proc.
	 */
	public void buildInitJobox();
	
	/** 
     * This method constructs and assembles a particular part of a Proc.
     */
    public void buildInitDataModelPools();
	
	/** 
	 * This method constructs and assembles a particular part of a Proc.
	 */
	public void buildStartSchedule();
}
