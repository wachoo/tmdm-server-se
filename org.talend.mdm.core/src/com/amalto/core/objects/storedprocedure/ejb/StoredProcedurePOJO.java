package com.amalto.core.objects.storedprocedure.ejb;

import java.util.Collection;

import javax.ejb.EJBException;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.metadata.LongString;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;


public class StoredProcedurePOJO extends ObjectPOJO {

    private final Logger LOGGER = Logger.getLogger(StoredProcedurePOJO.class);

    private String name;

    private String description;

    private String procedure;

    private boolean refreshCache;

    public StoredProcedurePOJO() {
    }

    public StoredProcedurePOJO(String name, String query) {
        this.name = name;
        this.procedure = query;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isRefreshCache() {
        return refreshCache;
    }

    public void setRefreshCache(boolean refreshCache) {
        this.refreshCache = refreshCache;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @LongString
    public String getProcedure() {
        return procedure;
    }

    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    public Collection<String> execute(String revisionID, DataClusterPOJOPK dataClusterPOJOPK, String[] parameters) throws XtentisException {
        if (getProcedure() == null) {
            return null;
        }
        try {
            if (refreshCache) {
                ItemPOJO.clearCache();
            }
            XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
            String cluster = null;
            if (dataClusterPOJOPK != null) {
                cluster = dataClusterPOJOPK.getUniqueId();
            }
            return server.runQuery(revisionID, cluster, getProcedure(), parameters);
        } catch (Exception e) {
            String err = "Unable to execute the Stored Procedure " + getPK().getUniqueId()
                    + ": " + e.getLocalizedMessage();
            LOGGER.error(err);
            throw new EJBException(err, e);
        }
    }

    @Override
    public ObjectPOJOPK getPK() {
        if (getName() == null) {
            return null;
        }
        return new ObjectPOJOPK(new String[]{name});
    }
}
