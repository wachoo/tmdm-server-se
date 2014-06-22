package org.talend.mdm.bulkload.client;

public class BulkloadOptions {
	boolean smartpk;
	boolean validate;	
	int arraySize=500; //the performance become better when the arraySize is bigger
	public BulkloadOptions(boolean smartpk,boolean validate, int arraySize) {
		this.smartpk=smartpk;
		this.validate=validate;
		this.arraySize=arraySize;
	}
	public BulkloadOptions( int arraySize) {
		this.arraySize=arraySize;
	}
	public BulkloadOptions() {}
	public boolean isSmartpk() {
		return smartpk;
	}
	public void setSmartpk(boolean smartpk) {
		this.smartpk = smartpk;
	}
	public boolean isValidate() {
		return validate;
	}
	public void setValidate(boolean validate) {
		this.validate = validate;
	}
	public int getArraySize() {
		return arraySize;
	}
	public void setArraySize(int arraySize) {
		this.arraySize = arraySize;
	}
	
}
