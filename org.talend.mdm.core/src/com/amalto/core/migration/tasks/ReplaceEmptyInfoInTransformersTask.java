package com.amalto.core.migration.tasks;

import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2CtrlBean;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocalHome;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.transformers.v2.util.TransformerVariablesMapping;

public class ReplaceEmptyInfoInTransformersTask extends AbstractMigrationTask{

	@Override
	protected Boolean execute() {
		//Update Transformer POJOS
		org.apache.log4j.Logger.getLogger(ReplaceEmptyInfoInTransformersTask.class).info("Updating Transformers");
		try {
			
			String[] ids = ConfigurationHelper.getServer().getAllDocumentsUniqueID(null, ObjectPOJO.getCluster(TransformerV2POJO.class));
			if (ids != null) {
				
				TransformerV2CtrlLocal tCtrl = ((TransformerV2CtrlLocalHome)new InitialContext().lookup(TransformerV2CtrlLocalHome.JNDI_NAME)).create();
				
				for (int i = 0; i < ids.length; i++) {
			
					String xml = ConfigurationHelper.getServer().getDocumentAsString(null, ObjectPOJO.getCluster(TransformerV2POJO.class), ids[i]);
					TransformerV2POJO transformer = ObjectPOJO.unmarshal(TransformerV2POJO.class, xml);

					
					
					ArrayList<TransformerProcessStep> steps = transformer.getProcessSteps();
                	if (steps!=null) {
        	        	for (Iterator<TransformerProcessStep> iter = steps.iterator(); iter.hasNext(); ) {
        	        		TransformerProcessStep step = iter.next();
        	        		//add some text to empty descriptions
        	        		if (step.getDescription() == null || "".equals(step.getDescription().trim()))
        	        			step.setDescription("[no description]");
        	        		
        	        		//Replace empty variable with "_DEFAULT_" variable
        			    	ArrayList<TransformerVariablesMapping> inputMappings = step.getInputMappings();
        			    	for (Iterator<TransformerVariablesMapping> iterator = inputMappings.iterator(); iterator.hasNext(); ) {
        						TransformerVariablesMapping transformerVariablesMapping = iterator.next();
        						String pipelineVariable = transformerVariablesMapping.getPipelineVariable();
        						if (pipelineVariable==null || "".equals(pipelineVariable.trim()))
        							transformerVariablesMapping.setPipelineVariable(TransformerV2CtrlBean.DEFAULT_VARIABLE);
        					}
        			    	ArrayList<TransformerVariablesMapping> outputMappings = step.getOutputMappings();
        			    	for (Iterator<TransformerVariablesMapping> iterator = outputMappings.iterator(); iterator.hasNext(); ) {
        						TransformerVariablesMapping transformerVariablesMapping = iterator.next();
        						String pipelineVariable = transformerVariablesMapping.getPipelineVariable();
        						if (pipelineVariable==null || "".equals(pipelineVariable.trim()))
        							transformerVariablesMapping.setPipelineVariable(TransformerV2CtrlBean.DEFAULT_VARIABLE);
        					}
        				}//for
                	}
                	tCtrl.putTransformer(transformer);
                	org.apache.log4j.Logger.getLogger(ReplaceEmptyInfoInTransformersTask.class).info("Processed '"+transformer.getName()+"'");
				}
			}
			
			
		} catch (Exception e) {
			String err = "Unable to Rename Menu Entries.";
			org.apache.log4j.Logger.getLogger(ReplaceEmptyInfoInTransformersTask.class).error(err, e);
			return false;
		}
		org.apache.log4j.Logger.getLogger(ReplaceEmptyInfoInTransformersTask.class).info("Done Updating Transformers");
		
		return true;
	}

}
