package com.amalto.core.jobox.util;

import java.io.File;

public class JoboxUtil {

	// param folderPath
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath);
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void cleanFolder(String folderPath) {
		try {
			delAllFile(folderPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean delAllFile(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			return flag;
		}
		if (!file.isDirectory()) {
			return flag;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);
				delFolder(path + "/" + tempList[i]);
				flag = true;
			}
		}
		return flag;
	}
	
	/**
     * Return the extension portion of the file's name .
     *
     * @see #getExtension
     */
    public static String getExtension(File f) {
        return (f != null) ? getExtension(f.getName()) : "";
    }

    public static String getExtension(String filename) {
        return getExtension(filename, "");
    }

    public static String getExtension(String filename, String defExt) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');

            if ((i >-1) && (i < (filename.length() - 1))) {
                return filename.substring(i + 1);
            }
        }
        return defExt;
    }

    public static String trimExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');
            if ((i >-1) && (i < (filename.length()))) {
                return filename.substring(0, i);
            }
        }
        return filename;
    } 

}
