package org.talend.mdm.server.api;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJOPK;
import com.amalto.core.util.XtentisException;

import java.util.Collection;

/**
 *
 */
public interface StoredProcedure {
    StoredProcedurePOJOPK putStoredProcedure(StoredProcedurePOJO storedProcedure) throws XtentisException;

    StoredProcedurePOJO getStoredProcedure(StoredProcedurePOJOPK pk) throws XtentisException;

    StoredProcedurePOJO existsStoredProcedure(StoredProcedurePOJOPK pk)    throws XtentisException;

    StoredProcedurePOJOPK removeStoredProcedure(StoredProcedurePOJOPK pk)
    throws XtentisException;

    Collection<String> execute(
            StoredProcedurePOJOPK sppk,
            String revisionID,
            DataClusterPOJOPK dcpk,
            String[] parameters
    )throws XtentisException;

    Collection<StoredProcedurePOJOPK> getStoredProcedurePKs(String regex) throws XtentisException;
}
