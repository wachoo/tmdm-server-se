package com.amalto.core.delegator;

import java.util.ArrayList;
import java.util.Map;

import javax.ejb.SessionContext;
import javax.ejb.Timer;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJO;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;
import com.amalto.core.objects.versioning.util.HistoryInfos;
import com.amalto.core.objects.versioning.util.TagStructureInfo;
import com.amalto.core.objects.versioning.util.VersioningServiceCtrlLocalBI;
import com.amalto.core.util.XtentisException;

public class VersioningSystemCtrlBeanDefaultDelegator implements
		IVersioningSystemCtrlBeanDelegator {

	public ItemPOJOPK commitItem(VersioningSystemPOJOPK versioningSystemPOJOPK,
			ItemPOJOPK itemPK, String comment) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public BackgroundJobPOJOPK commitItemsAsJob(
			VersioningSystemPOJOPK versioningSystemPOJOPK,
			ItemPOJOPK[] itemPKs, String comment) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public VersioningSystemPOJO existsVersioningSystem(VersioningSystemPOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public String getItemContent(VersioningSystemPOJOPK versioningSystemPOJOPK,
			ItemPOJOPK itemPK, String revision) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public HistoryInfos getItemHistory(
			VersioningSystemPOJOPK versioningSystemPOJOPK, ItemPOJOPK itemPOJOPK)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public HistoryInfos getItemsVersions(
			VersioningSystemPOJOPK versioningSystemPOJOPK,
			ItemPOJOPK[] itemPOJOPKs) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public HistoryInfos getObjectsVersions(
			VersioningSystemPOJOPK versioningSystemPOJOPK, String type,
			String[] instances) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TagStructureInfo[] getUniverseVersions(
			VersioningSystemPOJOPK versioningSystemPOJOPK)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public VersioningSystemPOJO getVersioningSystem(VersioningSystemPOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public String getVersioningSystemAvailability(VersioningSystemPOJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public ArrayList<VersioningSystemPOJOPK> getVersioningSystemPKs(String regex)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public VersioningSystemPOJOPK putVersioningSystem(
			VersioningSystemPOJO versioningSystem) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public VersioningSystemPOJOPK removeVersioningSystem(
			VersioningSystemPOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public void restoreItemByRevision(
			VersioningSystemPOJOPK versioningSystemPOJOPK, ItemPOJOPK itemPK,
			String revision) throws XtentisException {
		throw new XtentisException("Not Support!");

	}

	public BackgroundJobPOJOPK restoreItemsAsJob(
			VersioningSystemPOJOPK versioningSystemPOJOPK, String tag,
			ItemPOJOPK[] itemPKs) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public BackgroundJobPOJOPK restoreObjectsAsJob(
			VersioningSystemPOJOPK versioningSystemPOJOPK, String tag,
			String type, String[] instances) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public BackgroundJobPOJOPK restoreUniverseAsJob(
			VersioningSystemPOJOPK versioningSystemPOJOPK, String tag,
			String[] encodedClusterNames) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public VersioningServiceCtrlLocalBI setDefaultVersioningSystem(
			VersioningSystemPOJOPK pk) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public BackgroundJobPOJOPK tagItemsAsJob(
			VersioningSystemPOJOPK versioningSystemPOJOPK, String tag,
			String comment, ItemPOJOPK[] itemPKs) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public BackgroundJobPOJOPK tagObjectsAsJob(
			VersioningSystemPOJOPK versioningSystemPOJOPK, String tag,
			String comment, String type, String[] instances)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public BackgroundJobPOJOPK tagUniverseAsJob(
			VersioningSystemPOJOPK versioningSystemPOJOPK, String tag,
			String comment, Map<String, String[]> typeInstances)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public void setSessionContext(SessionContext ctx) {
		// TODO Auto-generated method stub
		
	}

	public void ejbTimeout(Timer timer) {
		// TODO Auto-generated method stub
		
	}

}
