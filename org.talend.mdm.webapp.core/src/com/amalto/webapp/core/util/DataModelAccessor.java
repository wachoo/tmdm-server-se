// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataModel;


/**
 * created by talend2 on 2013-6-13
 * Detailled comment
 *
 */
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
                String dataModelXSD = getDataModelXSD(dataModelName);
                return dataModelXSD != null ? checkReadAccessHelper(dataModelXSD, conceptName, roleList) : false; 
            } else {
                return false;
            }   
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean checkReadAccessHelper(String modelXSD, String conceptName, List<String> roles) {
        boolean result = false;

        if (LOG.isDebugEnabled())
            LOG.debug("Check read permission on " + conceptName + " for roles " + roles); //$NON-NLS-1$ //$NON-NLS-2$

        try {
            MetadataRepository repository = new MetadataRepository();
            InputStream is = new ByteArrayInputStream(modelXSD.getBytes("UTF-8")); //$NON-NLS-1$
            repository.load(is);

            ComplexTypeMetadata metadata = repository.getComplexType(conceptName);

            if (metadata != null) {
                List<String> noAccessRoles = metadata.getHideUsers();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Roles without access " + noAccessRoles); //$NON-NLS-1$
                }
                noAccessRoles.retainAll(roles);
                boolean userIsNoAccess = !noAccessRoles.isEmpty();
                result = !userIsNoAccess;
            } else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Complex Type " + conceptName + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    public boolean checkRestoreAccess(String dataModelName, String conceptName) {
        try {
            if (dataModelName != null && conceptName != null) {
                String roles = com.amalto.webapp.core.util.Util.getPrincipalMember("Roles"); //$NON-NLS-1$
                List<String> roleList = Arrays.asList(roles.split(",")); //$NON-NLS-1$
                String dataModelXSD = getDataModelXSD(dataModelName);
                return dataModelXSD != null ? checkRestoreAccessHelper(dataModelXSD, conceptName, roleList) : false;
            } else {
                return false;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    public boolean checkRestoreAccessHelper(String modelXSD, String conceptName, List<String> roles) {
        boolean result = false;

        if (LOG.isDebugEnabled())
            LOG.debug("Check restore permission on " + conceptName + " for roles " + roles); //$NON-NLS-1$ //$NON-NLS-2$

        try {
            MetadataRepository repository = new MetadataRepository();
            InputStream is = new ByteArrayInputStream(modelXSD.getBytes("UTF-8")); //$NON-NLS-1$
            repository.load(is);

            ComplexTypeMetadata metadata = repository.getComplexType(conceptName);

            if (metadata != null) {
                List<String> noAccessRoles = metadata.getHideUsers();
                if (LOG.isDebugEnabled())
                    LOG.debug("Roles without access " + noAccessRoles); //$NON-NLS-1$
                List<String> writeAccessRoles = metadata.getWriteUsers();
                if (LOG.isDebugEnabled())
                    LOG.debug("Roles with write permission " + writeAccessRoles); //$NON-NLS-1$

                noAccessRoles.retainAll(roles);
                boolean userIsNoAccess = !noAccessRoles.isEmpty();
                writeAccessRoles.retainAll(roles);
                boolean userHasWriteAccess = !writeAccessRoles.isEmpty();

                result = !userIsNoAccess && userHasWriteAccess;
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("Complex Type " + conceptName + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }
}
