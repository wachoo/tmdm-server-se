package org.talend.mdm.server.api;

import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.util.XtentisException;

import java.util.Collection;

/**
 *
 */
public interface BackgroundJob {
    BackgroundJobPOJOPK putBackgroundJob(BackgroundJobPOJO backgroundJob) throws XtentisException;

    BackgroundJobPOJO getBackgroundJob(BackgroundJobPOJOPK pk) throws XtentisException;

    BackgroundJobPOJO existsBackgroundJob(BackgroundJobPOJOPK pk)    throws XtentisException;

    BackgroundJobPOJOPK removeBackgroundJob(BackgroundJobPOJOPK pk)
    throws XtentisException;

    Collection<BackgroundJobPOJOPK> getBackgroundJobPKs(String regex) throws XtentisException;
}
