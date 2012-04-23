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

        try {
            MetadataRepository repository = new MetadataRepository();
            InputStream is = new ByteArrayInputStream(modelXSD.getBytes("UTF-8")); //$NON-NLS-1$
            repository.load(is);

            ComplexTypeMetadata metadata = repository.getComplexType(conceptName);

            if (metadata != null) {
                List<String> noAccessRoles = metadata.getHideUsers();

                noAccessRoles.retainAll(roles);
                boolean userIsNoAccess = !noAccessRoles.isEmpty();

                result = !userIsNoAccess;
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

        try {
            MetadataRepository repository = new MetadataRepository();
            InputStream is = new ByteArrayInputStream(modelXSD.getBytes("UTF-8")); //$NON-NLS-1$
            repository.load(is);

            ComplexTypeMetadata metadata = repository.getComplexType(conceptName);

            if (metadata != null) {
                List<String> noAccessRoles = metadata.getHideUsers();
                List<String> writeAccessRoles = metadata.getWriteUsers();

                noAccessRoles.retainAll(roles);
                boolean userIsNoAccess = !noAccessRoles.isEmpty();
                writeAccessRoles.retainAll(roles);
                boolean userHasWriteAccess = !writeAccessRoles.isEmpty();

                result = !userIsNoAccess && userHasWriteAccess;
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }
}
