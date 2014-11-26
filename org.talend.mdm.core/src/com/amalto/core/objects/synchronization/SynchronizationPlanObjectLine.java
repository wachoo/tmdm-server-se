package com.amalto.core.objects.synchronization;

public class SynchronizationPlanObjectLine {
	
	private String instancePattern;
	private String sourceRevisionID;
	private String destinationRevisionID;
	private String algorithm;
	
	/** for marshaling */
	public SynchronizationPlanObjectLine() {
    }
	
	public SynchronizationPlanObjectLine(String conceptPattern, String sourceRevisionID, String destinationRevisionID, String algorithm) {
	    super();
	    this.instancePattern = conceptPattern;
	    this.sourceRevisionID = sourceRevisionID;
	    this.destinationRevisionID = destinationRevisionID;
	    this.algorithm = algorithm;
    }

	public String getInstancePattern() {
    	return instancePattern;
    }

	public void setInstancePattern(String conceptPattern) {
    	this.instancePattern = conceptPattern;
    }

	public String getSourceRevisionID() {
    	return sourceRevisionID;
    }

	public void setSourceRevisionID(String sourceRevisionID) {
    	this.sourceRevisionID = sourceRevisionID;
    }

	public String getDestinationRevisionID() {
    	return destinationRevisionID;
    }

	public void setDestinationRevisionID(String destinationRevisionID) {
    	this.destinationRevisionID = destinationRevisionID;
    }

	public String getAlgorithm() {
    	return algorithm;
    }

	public void setAlgorithm(String algorithm) {
    	this.algorithm = algorithm;
    }
	
	

}
