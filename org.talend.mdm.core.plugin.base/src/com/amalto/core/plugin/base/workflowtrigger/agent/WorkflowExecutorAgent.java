package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.util.Iterator;
import java.util.Map;

import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.BonitaException;

import com.amalto.core.util.XtentisException;

/**
 * @author starkey
 */
public final class WorkflowExecutorAgent extends WorkflowAgent{
	

	public WorkflowExecutorAgent() {
	   super();
	}

	public ProcessInstanceUUID execute(WorkflowProcessPK processPK,WorkflowProcessVariableBox processVariableBox,WorkflowActivityVariableBoxes activityVariableBoxes) throws BonitaException, XtentisException {
		
	
		// instantiation
		final ProcessInstanceUUID instanceUUID = getWorkflowService().instantiateProcess(processPK.getProcessId(), processPK.getProcessVersion());
		if(!processVariableBox.isEmpty()){
			Map<String, Object> gvars=processVariableBox.getVariables();
			for (Iterator<String> iterator = gvars.keySet().iterator(); iterator.hasNext();) {
				String gvariableId =  iterator.next();
				Object gvariableValue = gvars.get(gvariableId);
				getWorkflowService().setProcessInstanceVariable(instanceUUID,gvariableId,gvariableValue );
			}
		}
		this.console.writeln("Started a new Process Instance: " + instanceUUID);

		// tasks execution
//		// FIXME: maybe this is muti thread
//		Collection<TaskInstance> activities = getWorkflowService().getTaskList(instanceUUID, ActivityState.READY);
//	    if (activities.isEmpty()) {
//	      throw new BonitaRuntimeException("No task found? Bad User?");
//	    }
//	    
//		while (!activities.isEmpty()) {
//			
//			for (TaskInstance activity : activities) {
//				final ActivityInstanceUUID taskUUID = activity.getUUID();
//		        final String activityId = activity.getActivityName();
//				this.console.writeln("Starting task associated to activity: " + activityId);
//				getWorkflowService().startTask(taskUUID);
//				if(activityVariableBoxes.contains(activityId)){
//					
//					Map<String, Object> vars=activityVariableBoxes.getVariablesMap(activityId);
//					for (Iterator<String> iterator = vars.keySet().iterator(); iterator.hasNext();) {
//						String key = iterator.next();
//						Object value = vars.get(key);
//						getWorkflowService().setActivityInstanceVariable(activity.getUUID(),key, value);
//					}
//					
//				}
//				this.console.writeln("Finishing task associated to activity: " + activityId);
//				getWorkflowService().finishTask(taskUUID);
//				this.console.writeln("Task associated to activity: " + activityId + " finished.");
//			}
//			System.out.println("Process Instance Variables on each step: "+getWorkflowService().getProcessInstanceVariables(instanceUUID));
//			
//			activities = getWorkflowService().getTaskList(instanceUUID,ActivityState.READY);
//			
//		}
		
		return instanceUUID;
	}


}
