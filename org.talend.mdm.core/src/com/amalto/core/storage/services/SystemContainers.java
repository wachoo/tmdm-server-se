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

import com.amalto.core.objects.datacluster.DataClusterPOJO;
import com.amalto.core.util.XtentisException;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

@Path("/system/containers")
@Api(value="Containers management", tags="Administration")
public class SystemContainers {

    public SystemContainers() {
    }

    @POST
    @ApiOperation("Creates a new data container with the provided name")
    public void createContainer(@ApiParam("The new container name") @QueryParam("name") String containerName) {
        try {
            DataClusterPOJO dataClusterPOJO = new DataClusterPOJO(containerName);
            dataClusterPOJO.store();
        } catch (XtentisException e) {
            throw new RuntimeException("Could not store new data container.", e); //$NON-NLS-1$
        }
    }
}
