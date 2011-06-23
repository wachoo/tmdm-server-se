package com.amalto.core.jobox.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.amalto.core.jobox.JobInfo;

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
                delAllFile(path + "/" + tempList[i]);//$NON-NLS-1$
                delFolder(path + "/" + tempList[i]);//$NON-NLS-1$
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
        return (f != null) ? getExtension(f.getName()) : ""; //$NON-NLS-1$
    }

    public static String getExtension(String filename) {
        return getExtension(filename, ""); //$NON-NLS-1$
    }

    public static String getExtension(String filename, String defExt) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');

            if ((i > -1) && (i < (filename.length() - 1))) {
                return filename.substring(i + 1);
            }
        }
        return defExt;
    }

    public static String trimExtension(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int i = filename.lastIndexOf('.');
            if ((i > -1) && (i < (filename.length()))) {
                return filename.substring(0, i);
            }
        }
        return filename;
    }

    public static ArrayList extract(String sZipPathFile, String sDestPath) throws Exception {

        ArrayList allFileName = new ArrayList();

        FileInputStream fins = new FileInputStream(sZipPathFile);
        ZipInputStream zins = new ZipInputStream(fins);
        ZipEntry ze = null;
        byte ch[] = new byte[256];
        while ((ze = zins.getNextEntry()) != null) {
            File zfile = new File(sDestPath + ze.getName());
            File fpath = new File(zfile.getParentFile().getPath());

            if (ze.isDirectory()) {
                if (!zfile.exists())
                    zfile.mkdirs();
                zins.closeEntry();
            } else {
                if (!fpath.exists())
                    fpath.mkdirs();
                FileOutputStream fouts = new FileOutputStream(zfile);
                int i;
                allFileName.add(zfile.getAbsolutePath());
                while ((i = zins.read(ch)) != -1)
                    fouts.write(ch, 0, i);
                zins.closeEntry();
                fouts.close();
            }
        }
        fins.close();
        zins.close();

        return allFileName;

    }

    public static void findFirstFile(JobInfo jobInfo, File root, String fileName, List<File> resultList) {

        if (resultList.size() > 0)
            return;

        if (root.isFile()) {
            if (root.getName().equals(fileName)) {
            	if(jobInfo==null || root.getParentFile().getParentFile().getName().toLowerCase().startsWith(jobInfo.getName().toLowerCase())) 
            	 	resultList.add(root); 
            }
        } else if (root.isDirectory()) {
            File[] files = root.listFiles();
            for (int i = 0; i < files.length; i++) {
                findFirstFile(jobInfo,files[i], fileName, resultList);
            }
        }

    }

    private static void zipContents(File dir, String zipPath, ZipOutputStream zos) {
        String[] children = dir.list();
        if (children == null) {
            return;
        }

        for (int i = 0; i < children.length; i++) {
            File child = new File(dir, children[i]);

            String childZipPath = zipPath + File.separator + child.getName();

            if (child.isDirectory()) {
                zipContents(child, childZipPath, zos);
            } else {
                try {
                    zip(child, childZipPath, zos);
                } catch (Exception e) {
                }
            }
        }
    }

    public static String zip(File file, String zipFilePath) throws IOException {

        if (zipFilePath == null) {
            zipFilePath = file.getAbsolutePath() + ".zip";//$NON-NLS-1$
        }

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath));

        if (file.isDirectory()) {
            JoboxUtil.zipContents(file, file.getName(), zos);
        } else {
            try {
                JoboxUtil.zip(file, file.getName(), zos);
            } catch (Exception e) {
            }
        }

        zos.close();
        return zipFilePath;
    }

    private static void zip(File file, String zipPath, ZipOutputStream zos) throws IOException {
        byte[] buf = new byte[1024];

        // Add ZIP entry to output stream.
        zos.putNextEntry(new ZipEntry(zipPath));

        // Transfer bytes from the file to the ZIP file
        int len = 0;
        FileInputStream is = new FileInputStream(file);
        while ((len = is.read(buf)) > 0) {
            zos.write(buf, 0, len);
        }

        is.close();
    }

}
