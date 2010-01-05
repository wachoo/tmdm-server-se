/**
 * 
 */
package com.amalto.core.objects.configurationinfo.assemble;


public class Test {

	
	public static void main(String[] args) {
		
		final AssembleConcreteBuilder concreteBuilder = new AssembleConcreteBuilder();
		
		final AssembleDirector director = new AssembleDirector(concreteBuilder);
		director.constructAll();
		director.constructCleanPart();
		director.constructInitPart();
		director.constructMigratePart();
		
		AssembleProc assembleProc = concreteBuilder.getAssembleProc();
		
		
		assembleProc.run();
		
	}

}
