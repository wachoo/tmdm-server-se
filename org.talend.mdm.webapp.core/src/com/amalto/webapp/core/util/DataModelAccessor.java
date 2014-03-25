// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

import java.io.ByteArrayInputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import com.amalto.core.server.MetadataRepositoryAdmin;
import com.amalto.core.server.ServerContext;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataModel;


public class DataModelAccessor {
    
    private static final Logger LOG = Logger.getLogger(DataModelAccessor.class);
    
    private static DataModelAccessor accessor;    
    
    public static synchronized DataModelAccessor getInstance(){    
        if (accessor == null) {
            accessor = new DataModelAccessor();
        }             
        return accessor;           
    }
    
    public String getDataModelXSD(String dataModelName) throws RemoteException, XtentisWebappException {
        if (dataModelName != null && !dataModelName.isEmpty()) {
            WSDataModel dataModel = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelName)));            
            if (dataModel != null) {
                return dataModel.getXsdSchema();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public boolean checkReadAccess(String dataModelName, String conceptName) {
        try {
            if (dataModelName != null && conceptName != null) {
                String roles = com.amalto.webapp.core.util.Util.getPrincipalMember("Roles"); //$NON-NLS-1$
                List<String> roleList = Arrays.asList(roles.split(",")); //$NON-NLS-1$
                MetadataRepositoryAdmin admin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
                MetadataRepository repository = admin.get(dataModelName);
                return _checkReadAccess(repository, conceptName, roleList);
            } else {
                return false;
            }   
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    protected boolean checkReadAccess(String modelXSD, String conceptName, List<String> roles) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(new ByteArrayInputStream(modelXSD.getBytes()));
        return _checkReadAccess(repository, conceptName, roles);
    }

    private boolean _checkReadAccess(MetadataRepository repository, String conceptName, List<String> roleList) {
        ComplexTypeMetadata type = repository.getComplexType(conceptName);
        if (type != null) {
            List<String> noAccessRoles = type.getHideUsers();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Roles without access " + noAccessRoles); //$NON-NLS-1$
            }
            noAccessRoles.retainAll(roleList);
            boolean userIsNoAccess = !noAccessRoles.isEmpty();
            return !userIsNoAccess;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Complex Type " + conceptName + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return false;
        }
    }

    public boolean checkRestoreAccess(String dataModelName, String conceptName) {
        try {
            if (dataModelName != null && conceptName != null) {
                String roles = com.amalto.webapp.core.util.Util.getPrincipalMember("Roles"); //$NON-NLS-1$
                List<String> roleList = Arrays.asList(roles.split(",")); //$NON-NLS-1$
                MetadataRepositoryAdmin admin = ServerContext.INSTANCE.get().getMetadataRepositoryAdmin();
                MetadataRepository repository = admin.get(dataModelName);
                return _checkRestoreAccess(repository, conceptName, roleList);
            } else {
                return false;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    protected boolean checkRestoreAccess(String modelXSD, String conceptName, List<String> roles) {
        MetadataRepository repository = new MetadataRepository();
        repository.load(new ByteArrayInputStream(modelXSD.getBytes()));
        return _checkRestoreAccess(repository, conceptName, roles);
    }

    private boolean _checkRestoreAccess(MetadataRepository repository, String conceptName, List<String> roleList) {
        ComplexTypeMetadata type = repository.getComplexType(conceptName);
        if (type != null) {
            List<String> noAccessRoles = type.getHideUsers();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Roles without access " + noAccessRoles); //$NON-NLS-1$
            }
            List<String> writeAccessRoles = type.getWriteUsers();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Roles with write permission " + writeAccessRoles); //$NON-NLS-1$
            }
            noAccessRoles.retainAll(roleList);
            boolean userIsNoAccess = !noAccessRoles.isEmpty();
            writeAccessRoles.retainAll(roleList);
            boolean userHasWriteAccess = !writeAccessRoles.isEmpty();
            return !userIsNoAccess && userHasWriteAccess;
        } else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Complex Type " + conceptName + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return false;
        }
    }
}
