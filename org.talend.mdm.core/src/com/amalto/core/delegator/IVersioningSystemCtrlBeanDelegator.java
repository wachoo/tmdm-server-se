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

public interface IVersioningSystemCtrlBeanDelegator extends BeanDelegator{
	public VersioningSystemPOJOPK putVersioningSystem(VersioningSystemPOJO versioningSystem) throws XtentisException;
	public VersioningSystemPOJO getVersioningSystem(VersioningSystemPOJOPK pk) throws XtentisException;
	public VersioningSystemPOJO existsVersioningSystem(VersioningSystemPOJOPK pk)    throws XtentisException;
	public VersioningSystemPOJOPK removeVersioningSystem(VersioningSystemPOJOPK pk) 
    throws XtentisException;
	public ArrayList<VersioningSystemPOJOPK> getVersioningSystemPKs(String regex) throws XtentisException;
	public VersioningServiceCtrlLocalBI setDefaultVersioningSystem(VersioningSystemPOJOPK pk) throws XtentisException;
	public String getVersioningSystemAvailability(VersioningSystemPOJOPK pk) throws XtentisException;
	public BackgroundJobPOJOPK tagUniverseAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String comment,
    		Map<String,String[]> typeInstances
		)throws XtentisException;
	public BackgroundJobPOJOPK tagObjectsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String comment,
    		String type,
    		String[] instances
		)throws XtentisException;
	public BackgroundJobPOJOPK tagItemsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String comment,
    		ItemPOJOPK[] itemPKs
		)throws XtentisException;
	public BackgroundJobPOJOPK restoreObjectsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String type,
    		String[] instances
		)throws XtentisException;
	public BackgroundJobPOJOPK restoreUniverseAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String[] encodedClusterNames
		)throws XtentisException;
	public BackgroundJobPOJOPK restoreItemsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		ItemPOJOPK[] itemPKs
		)throws XtentisException;
	public ItemPOJOPK commitItem(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPK,
    		String comment
    )throws XtentisException;
	public BackgroundJobPOJOPK commitItemsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK[] itemPKs,
    		String comment
    )throws XtentisException;
	public void restoreItemByRevision(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPK,
    		String revision
    )throws XtentisException;
	public HistoryInfos getObjectsVersions(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String type,
    		String instances[]
		)throws XtentisException;
	public TagStructureInfo[] getUniverseVersions(
    		VersioningSystemPOJOPK versioningSystemPOJOPK
		)throws XtentisException;
	public HistoryInfos getItemsVersions(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK[] itemPOJOPKs
		)throws XtentisException;
	public HistoryInfos getItemHistory(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPOJOPK
		)throws XtentisException;
	public String getItemContent(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPK,
    		String revision
		)throws XtentisException;
	public void setSessionContext(SessionContext ctx);
	
	public void ejbTimeout(Timer timer);
}
