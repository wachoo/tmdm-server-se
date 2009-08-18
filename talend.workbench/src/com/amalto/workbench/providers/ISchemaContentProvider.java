package com.amalto.workbench.providers;

import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.utils.DataModelFilter;

public interface ISchemaContentProvider {
	public XSDSchema getXsdSchema() ;
	
	
	public void setXsdSchema(String xsd);
	
	public void setXsdSchema(XSDSchema xsdSchema);
	public void setFilter(DataModelFilter filter);
}
