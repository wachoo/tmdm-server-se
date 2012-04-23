// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.recyclebin.server.actions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;

public class Util {

    private static final Logger LOG = Logger.getLogger(Util.class);

    public static boolean checkReadAccess(String modelXSD, String conceptName) {
        boolean result = false;

        try {
            String roles = com.amalto.webapp.core.util.Util.getPrincipalMember("Roles"); //$NON-NLS-1$
            List<String> roleList = Arrays.asList(roles.split(",")); //$NON-NLS-1$
            result = checkReadAccessHelper(modelXSD, conceptName, roleList);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    public static boolean checkReadAccessHelper(String modelXSD, String conceptName, List<String> roles) {
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
                if (LOG.isDebugEnabled())
                    LOG.debug("Roles without access " + noAccessRoles); //$NON-NLS-1$
                noAccessRoles.retainAll(roles);
                boolean userIsNoAccess = !noAccessRoles.isEmpty();

                result = !userIsNoAccess;
            } else {
                if (LOG.isDebugEnabled())
                    LOG.debug("Complex Type " + conceptName + " not found"); //$NON-NLS-1$ //$NON-NLS-2$
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    public static boolean checkRestoreAccess(String modelXSD, String conceptName) {
        boolean result = false;

        try {
            String roles = com.amalto.webapp.core.util.Util.getPrincipalMember("Roles"); //$NON-NLS-1$
            List<String> roleList = Arrays.asList(roles.split(",")); //$NON-NLS-1$
            result = checkRestoreAccessHelper(modelXSD, conceptName, roleList);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    public static boolean checkRestoreAccessHelper(String modelXSD, String conceptName, List<String> roles) {
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
