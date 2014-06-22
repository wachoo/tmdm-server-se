package org.talend.mdm.webapp.browserecords.server.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;


public class NoAccessCheckUtil {
    public static boolean checkNoAccess(String modelXSD, String conceptName) {
        boolean result = false;

        try {
            String roles = com.amalto.webapp.core.util.Util.getPrincipalMember("Roles"); //$NON-NLS-1$
            List<String> roleList = Arrays.asList(roles.split(",")); //$NON-NLS-1$
            result = checkNoAccessHelper(modelXSD, conceptName, roleList);
        } catch (Exception e) {
        }

        return result;
    }

    public static boolean checkNoAccessHelper(String modelXSD, String conceptName, List<String> roles) {
        boolean result = false;

        try {
            MetadataRepository repository = new MetadataRepository();
            InputStream is = new ByteArrayInputStream(modelXSD.getBytes("UTF-8")); //$NON-NLS-1$
            repository.load(is);

            ComplexTypeMetadata metadata = repository.getComplexType(conceptName);

            if (metadata != null) {
                List<String> noAccessRoles = metadata.getHideUsers();
                noAccessRoles.retainAll(roles);
                result = !noAccessRoles.isEmpty();
            }
        } catch (Exception e) {
        }

        return result;
    }
}
