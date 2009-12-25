package com.amalto.core.delegator;

import java.util.Collection;

import com.amalto.core.objects.universe.ejb.RevisionItem;
import com.amalto.core.objects.universe.ejb.RevisionPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSUniversePKArray;

public class UniverseCtrlBeanDefaultDelegator implements
		IUniverseCtrlBeanDelegator {

	public UniversePOJO existsUniverse(UniversePOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public Collection<RevisionItem> getAllCreatedRevisions(UniversePOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public Collection<RevisionItem> getAllQuotedRevisions(UniversePOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public UniversePOJO getUniverse(UniversePOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public WSUniversePKArray getUniverseByRevision(String name,
			String revision, String type) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public UniversePOJOPK getUniverseCreator(RevisionPOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public Collection<UniversePOJOPK> getUniversePKs(String regex)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public Collection<UniversePOJOPK> getUniverseQuoter(RevisionPOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public UniversePOJOPK putUniverse(UniversePOJO universe)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public UniversePOJOPK removeUniverse(UniversePOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

}
