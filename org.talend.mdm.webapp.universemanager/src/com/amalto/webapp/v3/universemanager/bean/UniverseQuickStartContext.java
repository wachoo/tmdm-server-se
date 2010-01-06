package com.amalto.webapp.v3.universemanager.bean;

import com.amalto.core.objects.universe.ejb.UniversePOJO;


public class UniverseQuickStartContext {
	
	private UniversePOJO sourceUniverse;
	
	public UniverseQuickStartContext(UniversePOJO sourceUniverse) {
		super();
		this.sourceUniverse = sourceUniverse;
	}

	public UniversePOJO getSourceUniverse() {
		return sourceUniverse;
	}

	public void setSourceUniverse(UniversePOJO sourceUniverse) {
		this.sourceUniverse = sourceUniverse;
	}

}
