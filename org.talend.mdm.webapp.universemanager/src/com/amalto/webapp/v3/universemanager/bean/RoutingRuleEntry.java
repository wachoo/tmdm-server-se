package com.amalto.webapp.v3.universemanager.bean;

public class RoutingRuleEntry extends ObjectRevisionEntry{
	
	public RoutingRuleEntry() {
		
	}
	
	public RoutingRuleEntry(String routingRuleName, String localRevisionID) {
		super();
		this.routingRuleName = routingRuleName;
		this.localRevisionID = localRevisionID;
	}

	private String routingRuleName;
	
	private String localRevisionID;

	public String getRoutingRuleName() {
		return routingRuleName;
	}

	public void setRoutingRuleName(String routingRuleName) {
		this.routingRuleName = routingRuleName;
	}

	public String getLocalRevisionID() {
		return cleanRevisionName(localRevisionID);
	}

	public void setLocalRevisionID(String localRevisionID) {
		this.localRevisionID = localRevisionID;
	}

}
