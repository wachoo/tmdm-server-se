package com.amalto.core.server.api;

import java.util.Collection;

import com.amalto.core.objects.backgroundjob.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.BackgroundJobPOJOPK;
import com.amalto.core.util.XtentisException;

/**
 *
 */
public interface BackgroundJob {

    BackgroundJobPOJOPK putBackgroundJob(BackgroundJobPOJO backgroundJob) throws XtentisException;

    BackgroundJobPOJO getBackgroundJob(BackgroundJobPOJOPK pk) throws XtentisException;

    BackgroundJobPOJO existsBackgroundJob(BackgroundJobPOJOPK pk) throws XtentisException;

    BackgroundJobPOJOPK removeBackgroundJob(BackgroundJobPOJOPK pk) throws XtentisException;

    Collection<BackgroundJobPOJOPK> getBackgroundJobPKs(String regex) throws XtentisException;
}
