package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityInstance;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.PackageDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.facade.uuid.TaskUUID;
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
		
		processDefinition = queryDefinitionAPI.getProcess(processPK.getPackageId(), processPK.getProcessId(), processPK.getProcessVersion());
		
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
		Collection<ActivityInstance<TaskInstance>> activities = queryRuntimeAPI.getTaskList(instanceUUID, ActivityState.READY);
		if (activities.isEmpty()) {
			throw new BonitaRuntimeException("No task found? Bad User?");
		}
		while (!activities.isEmpty()) {
			
			for (ActivityInstance<TaskInstance> activity : activities) {
				final TaskUUID taskUUID = activity.getBody().getUUID();
				final String activityId = activity.getActivityId();
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
		    final PackageDefinitionUUID packageUUID = instance.getPackageDefinitionUUID();

		    //undeployment
		    managementAPI.undeploy(packageUUID);
		    //journal + history cleaning
		    managementAPI.deletePackage(packageUUID);
		    
	 }
	 

}
