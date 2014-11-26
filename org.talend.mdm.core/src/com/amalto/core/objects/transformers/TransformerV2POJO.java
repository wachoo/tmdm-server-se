package com.amalto.core.objects.transformers;

import java.util.ArrayList;

import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.ObjectPOJOPK;
import com.amalto.core.objects.transformers.util.TransformerProcessStep;



/**
 * @author bgrieder
 * 
 */
public class TransformerV2POJO extends ObjectPOJO{
   

    private String name;
    private String description;
    private ArrayList<TransformerProcessStep> processSteps;

       
    /**
     * 
     */
    public TransformerV2POJO() {
        super();
    }
    
    
    
    
	public TransformerV2POJO(
			String name, 
			ArrayList<TransformerProcessStep> processSteps
	) {
		super();
		this.name = name;
		this.processSteps = processSteps;
	}


	public TransformerV2POJO(
			String name,
			String description,
			ArrayList<TransformerProcessStep> processSteps
	) {
		super();
		this.name = name;
		this.description = description;
		this.processSteps = processSteps;
	}

	
	@Override
	public ObjectPOJOPK getPK() {
		return new ObjectPOJOPK(new String[] {name});
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return Returns the Description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	

	public ArrayList<TransformerProcessStep> getProcessSteps() {
		return processSteps;
	}



	public void setProcessSteps(ArrayList<TransformerProcessStep> processSteps) {
		this.processSteps = processSteps;
	}





 

}
