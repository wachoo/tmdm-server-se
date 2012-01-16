package org.talend.mdm.webapp.base.shared;


public class FileUtil {
    
    public static String getFileType(String fileName){
        String fileType = ""; //$NON-NLS-1$
        if (fileName != null && !"".equals(fileName) && fileName.indexOf(".") != -1){ //$NON-NLS-1$ //$NON-NLS-2$
            fileType = fileName.substring(fileName.indexOf(".") + 1); //$NON-NLS-1$
        }
        return fileType;
    }
}
