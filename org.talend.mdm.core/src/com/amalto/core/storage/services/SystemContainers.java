// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.storage.services;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.objects.datacluster.DataClusterPOJO;
import com.amalto.core.save.SaverSession;
import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageType;
import com.amalto.core.util.XtentisException;

@Path("/system/containers")
public class SystemContainers {

    public SystemContainers() {
    }

    @POST
    public void createContainer(@QueryParam("name") String containerName) {
        try {
            DataClusterPOJO dataClusterPOJO = new DataClusterPOJO(containerName);
            dataClusterPOJO.store();
        } catch (XtentisException e) {
            throw new RuntimeException("Could not store new data container.", e); //$NON-NLS-1$
        }
    }
}
