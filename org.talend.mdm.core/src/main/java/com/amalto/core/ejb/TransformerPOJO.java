package com.amalto.core.ejb;

import java.util.ArrayList;
import java.util.Iterator;

import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.transformers.v2.util.TransformerVariablesMapping;
import com.amalto.core.util.TransformerPluginSpec;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * @author bgrieder
 * @deprecated - use TransformerV2 package
 * 
 */
public class TransformerPOJO extends ObjectPOJO{
   
	
    private String name;
    private String description;
    private ArrayList<TransformerPluginSpec> pluginSpecs;

    
    
    /**
     * 
     */
    public TransformerPOJO() {
        super();
    }
    
    
    
    
	public TransformerPOJO(
			String name, 
			ArrayList<TransformerPluginSpec> pluginSpecs
	) {
		super();
		this.name = name;
		this.pluginSpecs = pluginSpecs;
	}


	public TransformerPOJO(
			String name,
			String description,
			ArrayList<TransformerPluginSpec> pluginSpecs
	) {
		super();
		this.name = name;
		this.description = description;
		this.pluginSpecs = pluginSpecs;
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
	

	public ArrayList<TransformerPluginSpec> getPluginSpecs() {
		return pluginSpecs;
	}



	public void setPluginSpecs(ArrayList<TransformerPluginSpec> pluginSpecs) {
		this.pluginSpecs = pluginSpecs;
	}

	  /************************************************************************************
	   * V1 to V2 Stuff
	   ************************************************************************************/
	    
	    
	    public static TransformerV2POJO V1toV2(TransformerPOJO transformer) throws XtentisException{
	    	TransformerV2POJO transformerV2 = new TransformerV2POJO();
	    	transformerV2.setDescription(transformer.getDescription());
	    	transformerV2.setName(transformer.getName());
	    	
	    	ArrayList<TransformerProcessStep> processSteps = new ArrayList<TransformerProcessStep>();
//	    	Process plugins Specs in Process Steps
	    	ArrayList<TransformerPluginSpec> specs = transformer.getPluginSpecs();
	    	if (specs!=null) {
		    	for (Iterator<TransformerPluginSpec> iter = specs.iterator(); iter.hasNext(); ) {
					TransformerPluginSpec spec = iter.next();
					//fetch the plugin
					Object plugin= null;
					try {
						plugin = Util.retrieveComponent(null, spec.getPluginJNDI());
					} catch (Exception e) {
						throw new XtentisException(
								"The plugin "+
								spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
								" cannot be found"
						);
					}
					//get the plugin input descriptors
					ArrayList<TransformerPluginVariableDescriptor> inputDescriptors = null;
					try {
						inputDescriptors = (ArrayList<TransformerPluginVariableDescriptor>)
							Util.getMethod(plugin, "getInputVariableDescriptors").invoke(
									plugin,
									new Object[] {"en"}
							);
					} catch (Exception e) {
						throw new XtentisException(
								"The input parameters of plugin "+
								spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
								" could not be accessed : "+e.getLocalizedMessage()
						);
					}
					//get the plugin output descriptors
					ArrayList<TransformerPluginVariableDescriptor> outputDescriptors = null;
					try {
						outputDescriptors = (ArrayList<TransformerPluginVariableDescriptor>)
							Util.getMethod(plugin, "getOutputVariableDescriptors").invoke(
									plugin,
									new Object[] {"en"}
							);
					} catch (Exception e) {
						throw new XtentisException(
								"The parameters of plugin "+
								spec.getPluginJNDI().replaceAll("amalto/local/transformer/plugin/", "")+
								" could not be accessed : "+e.getLocalizedMessage()
						);
					}
					//map the pipeline input to the first plugin input
					ArrayList<TransformerVariablesMapping> inputMappings = new ArrayList<TransformerVariablesMapping>();
					TransformerVariablesMapping inputMapping = new TransformerVariablesMapping();
					inputMapping.setPluginVariable(inputDescriptors.get(0).getVariableName());
					inputMapping.setPipelineVariable(spec.getInput());
					inputMappings.add(inputMapping);
	//				map the pipeline poutput to the first plugin output
					ArrayList<TransformerVariablesMapping> outputMappings = new ArrayList<TransformerVariablesMapping>();
					TransformerVariablesMapping outputMapping = new TransformerVariablesMapping();
					outputMapping.setPluginVariable(outputDescriptors.get(0).getVariableName());
					outputMapping.setPipelineVariable(spec.getOutput());
					outputMappings.add(outputMapping);
					//build the the Transformer Process Step
					TransformerProcessStep processStep = new TransformerProcessStep();
					//processStep.setCompiledParameters(compiledParameters) --> done on save
					processStep.setDescription(spec.getDescription());
					processStep.setDisabled(false);
					processStep.setInputMappings(inputMappings);
					processStep.setOutputMappings(outputMappings);
					processStep.setParameters(spec.getParameters());
					processStep.setPluginJNDI(spec.getPluginJNDI());
					processSteps.add(processStep);
		    	}
	    	}
	    	transformerV2.setProcessSteps(processSteps);
	    	return transformerV2;
	    }
	    
	    public static TransformerPOJO V2toV1(TransformerV2POJO transformerV2) throws XtentisException{
	    	TransformerPOJO transformerV1 = new TransformerPOJO();
	    	transformerV1.setDescription(transformerV2.getDescription());
	    	transformerV1.setName(transformerV2.getName());
	    	ArrayList<TransformerPluginSpec> specs = new ArrayList<TransformerPluginSpec>();
	    	if(transformerV2.getProcessSteps()!=null) {
	    		for (Iterator<TransformerProcessStep> iter = transformerV2.getProcessSteps().iterator(); iter.hasNext(); ) {
					TransformerProcessStep processStep = iter.next();
					TransformerPluginSpec spec = new TransformerPluginSpec();
					spec.setDescription(processStep.getDescription());
					spec.setParameters(processStep.getParameters());
					spec.setPluginJNDI(processStep.getPluginJNDI());
					spec.setInput(processStep.getInputMappings().get(0).getPipelineVariable());
					spec.setOutput(processStep.getOutputMappings().get(0).getPipelineVariable());
					specs.add(spec);
				}
	    	}
	    	transformerV1.setPluginSpecs(specs);
	    	return transformerV1;
	    }
 

		
}
