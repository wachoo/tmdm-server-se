package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.universe.ejb.RevisionItem;
import com.amalto.core.objects.universe.ejb.RevisionPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSUniversePKArray;

public interface IUniverseCtrlBeanDelegator extends BeanDelegator {
	public UniversePOJOPK putUniverse(UniversePOJO universe) throws XtentisException;
	public WSUniversePKArray getUniverseByRevision(String  name,String revision, String type) throws XtentisException;
	public UniversePOJO getUniverse(UniversePOJOPK pk) throws XtentisException;
	public UniversePOJO existsUniverse(UniversePOJOPK pk)    throws XtentisException;
	public UniversePOJOPK removeUniverse(UniversePOJOPK pk) 
    throws XtentisException;
	public Collection<RevisionItem> getAllCreatedRevisions(UniversePOJOPK pk)throws XtentisException;
	public Collection<RevisionItem> getAllQuotedRevisions(UniversePOJOPK pk)throws XtentisException;
	public UniversePOJOPK getUniverseCreator(RevisionPOJOPK pk)throws XtentisException;
	public Collection<UniversePOJOPK> getUniverseQuoter(RevisionPOJOPK pk)throws XtentisException;
	public Collection<UniversePOJOPK> getUniversePKs(String regex) throws XtentisException;
}
