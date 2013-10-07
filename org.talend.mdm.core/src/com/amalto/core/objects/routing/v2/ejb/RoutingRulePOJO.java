package com.amalto.core.objects.routing.v2.ejb;

import java.util.ArrayList;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.metadata.LongString;

/**
 * @author bgrieder
 * 
 */
public class RoutingRulePOJO extends ObjectPOJO{
   
    private String name;
    private String description;
    private ArrayList<RoutingRuleExpressionPOJO> routingExpressions = new ArrayList<RoutingRuleExpressionPOJO>();
    private boolean synchronous=false;
    private String concept;
    private String serviceJNDI;
    private String parameters;
    private String condition;
    private boolean deActive;

    public RoutingRulePOJO() {}
    
	public RoutingRulePOJO(String name) {
		super();
		this.name = name;
	}


	public RoutingRulePOJO(String name, String description, ArrayList<RoutingRuleExpressionPOJO> routingExpressions, boolean synchronous, String concept, String serviceJNDI, String parameters,String condition,boolean deActive) {
		super();
		this.name = name;
		this.description = description;
		this.routingExpressions = routingExpressions;
		this.synchronous = synchronous;
		this.concept = concept;
		this.serviceJNDI = serviceJNDI;
		this.parameters = parameters;
		this.condition=condition;
		this.deActive=deActive;
	}
	
	

	public boolean isDeActive() {
		return deActive;
	}

	public void setDeActive(boolean deActive) {
		this.deActive = deActive;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @LongString
	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public ArrayList<RoutingRuleExpressionPOJO> getRoutingExpressions() {
		return routingExpressions;
	}

	public void setRoutingExpressions(
			ArrayList<RoutingRuleExpressionPOJO> routingExpressions) {
		this.routingExpressions = routingExpressions;
	}

	public String getServiceJNDI() {
		return serviceJNDI;
	}

	public void setServiceJNDI(String serviceJNDI) {
		this.serviceJNDI = serviceJNDI;
	}

	public boolean isSynchronous() {
		return synchronous;
	}

	public void setSynchronous(boolean synchronous) {
		this.synchronous = synchronous;
	}

	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	

}
