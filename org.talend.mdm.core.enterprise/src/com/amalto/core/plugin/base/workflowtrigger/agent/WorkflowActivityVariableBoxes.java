package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkflowActivityVariableBoxes {
	
	private List<WorkflowActivityVariableBox> boxes=null;
	
	public WorkflowActivityVariableBoxes() {
		boxes=new ArrayList<WorkflowActivityVariableBox>();
	}
	
	public void addVariable(String activityId,String variableId,String variableValue,String variableType) {
		
		if(boxes.contains(new WorkflowActivityVariableBox(activityId))){
			int pos=boxes.indexOf(new WorkflowActivityVariableBox(activityId));
			WorkflowActivityVariableBox box=boxes.get(pos);
			box.addVariable(variableId, variableValue, variableType);
		}else{
			WorkflowActivityVariableBox box=new WorkflowActivityVariableBox(activityId);
			box.addVariable(variableId, variableValue, variableType);
			boxes.add(box);
		}

	}
	
	public boolean contains(String activityId) {
		
		return boxes.contains(new WorkflowActivityVariableBox(activityId));

	}
	
	public Map<String, Object> getVariablesMap(String activityId) {
		
		int pos=boxes.indexOf(new WorkflowActivityVariableBox(activityId));
		WorkflowActivityVariableBox box=boxes.get(pos);
		return box.getVariables();

	}

}
