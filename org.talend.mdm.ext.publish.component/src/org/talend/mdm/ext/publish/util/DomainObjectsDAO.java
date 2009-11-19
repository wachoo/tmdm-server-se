package org.talend.mdm.ext.publish.util;

import com.amalto.core.util.XtentisException;

public interface DomainObjectsDAO {
	
	public String[] getAllPKs() throws XtentisException;
	
	public boolean putResource(String domainObjectName,String xmlContent);
	
	public String getResource(String domainObjectName) throws XtentisException;
	
}
