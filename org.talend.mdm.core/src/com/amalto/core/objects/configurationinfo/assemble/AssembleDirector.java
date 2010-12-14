/**
 * 
 */
package com.amalto.core.objects.configurationinfo.assemble;

public class AssembleDirector {

	/** Stores the Builder instance of the Director */
	private final AssembleBuilder fBuilder;

	/** 
	 * This construtor creates a Director instance.
	 * @param builder Concrete Builder implementation.
	 */
	public AssembleDirector(AssembleBuilder builder) {
		super();
		fBuilder = builder;
	}

	/** 
	 * This method constructs an object by using the Builder interface.
	 */
	public void constructAll() {
		fBuilder.buildPartCleanJob();
		fBuilder.buildPartInitData();
		fBuilder.buildPartMigrateVersion();
		fBuilder.buildPartStartEngine();
		fBuilder.buildFaildMessage();
		fBuilder.buildStartService();
		fBuilder.buildInitJobox();
		fBuilder.buildInitDataModelPools();
	}
	
	public void constructCleanPart() {
		fBuilder.buildPartCleanJob();
	}
	
	public void constructInitPart() {
		fBuilder.buildPartInitData();
	}
	
	public void constructMigratePart() {
		fBuilder.buildPartMigrateVersion();
	}

}
