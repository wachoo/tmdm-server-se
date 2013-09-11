package talend.ext.images.server.util;

import java.io.File;

import talend.ext.images.server.ImageServerInfoServlet;

public class FolderUtil {

    private static String uploadPath = null;

    private static String tempUploadPath = null;

    private static File uploadFolder = null;

    private static File tempUploadFolder = null;

    public static void setUp() {

        String jbossServerDir = System.getProperty("jboss.server.home.dir"); //$NON-NLS-1$
        if (jbossServerDir != null) {
            uploadPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                    + "mdm_resources" + File.separator + ImageServerInfoServlet.UPLOAD_PATH; //$NON-NLS-1$
            tempUploadPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                    + "mdm_resources" + File.separator + ImageServerInfoServlet.TEMP_PATH; //$NON-NLS-1$
        }

        uploadFolder = new File(uploadPath);
        tempUploadFolder = new File(tempUploadPath);
        uploadFolder.mkdirs();
        tempUploadFolder.mkdirs();
    }

    public static String getUploadPath() {
        return uploadPath;
    }

    public static String getTempUploadPath() {
        return tempUploadPath;
    }

    public static boolean IsUploadFolderReady() {
        if (!uploadFolder.exists() || !uploadFolder.canWrite()) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean IsTempUploadFolderReady() {
        if (!tempUploadFolder.exists() || !tempUploadFolder.canWrite()) {
            return false;
        } else {
            return true;
        }
    }
}
