package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.BonitaException;
import org.ow2.bonita.util.BonitaRuntimeException;

/**
 * @author starkey
 */
public final class WorkflowExecutorAgent extends WorkflowAgent{
	

	public WorkflowExecutorAgent() {
	   super();
	}

	public ProcessInstanceUUID execute(WorkflowProcessPK processPK,WorkflowProcessVariableBox processVariableBox,WorkflowActivityVariableBoxes activityVariableBoxes) throws BonitaException {
		
		ProcessDefinition processDefinition = null;
		
		processDefinition = queryDefinitionAPI.getProcess(processPK.getProcessId(), processPK.getProcessVersion());
		
		// instantiation
		final ProcessInstanceUUID instanceUUID = runtimeAPI.instantiateProcess(processDefinition.getUUID());
		this.console.writeln("Init a new Process Instance: " + instanceUUID);
		if(!processVariableBox.isEmpty()){
			Map<String, Object> gvars=processVariableBox.getVariables();
			for (Iterator<String> iterator = gvars.keySet().iterator(); iterator.hasNext();) {
				String gvariableId =  iterator.next();
				Object gvariableValue = gvars.get(gvariableId);
				runtimeAPI.setProcessInstanceVariable(instanceUUID,gvariableId,gvariableValue );
			}
		}
		

		// tasks execution
		// FIXME: maybe this is muti thread
		Collection<TaskInstance> activities = queryRuntimeAPI.getTaskList(instanceUUID, ActivityState.READY);
	    if (activities.isEmpty()) {
	      throw new BonitaRuntimeException("No task found? Bad User?");
	    }
	    
		while (!activities.isEmpty()) {
			
			for (TaskInstance activity : activities) {
				final ActivityInstanceUUID taskUUID = activity.getUUID();
		        final String activityId = activity.getActivityName();
				this.console.writeln("Starting task associated to activity: " + activityId);
				runtimeAPI.startTask(taskUUID, true);
				if(activityVariableBoxes.contains(activityId)){
					
					Map<String, Object> vars=activityVariableBoxes.getVariablesMap(activityId);
					for (Iterator<String> iterator = vars.keySet().iterator(); iterator.hasNext();) {
						String key = iterator.next();
						Object value = vars.get(key);
						runtimeAPI.setActivityInstanceVariable(activity.getUUID(),key, value);
					}
					
				}
				this.console.writeln("Finishing task associated to activity: " + activityId);
				runtimeAPI.finishTask(taskUUID, true);
				this.console.writeln("Task associated to activity: " + activityId + " finished.");
			}
			System.out.println("Process Instance Variables on each step: "+queryRuntimeAPI.getProcessInstanceVariables(instanceUUID));
			
			activities = queryRuntimeAPI.getTaskList(instanceUUID,ActivityState.READY);
			
		}
		
		

		return instanceUUID;
	}
	
	 public void cleanPackage(ProcessInstanceUUID instanceUUID) throws BonitaException {
		   
		    final ProcessInstance instance = queryRuntimeAPI.getProcessInstance(instanceUUID);
		    final ProcessDefinitionUUID processUUID = instance.getProcessDefinitionUUID();

		    //undeployment
		    managementAPI.undeploy(processUUID);
		    //journal + history cleaning
		    managementAPI.deleteProcess(processUUID);
		    
	 }
	 

}
