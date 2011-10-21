// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.jobox.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.amalto.core.jobox.JobInfo;

public class JoboxUtil {

    private JoboxUtil() {
    }

    public static void deleteFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            File myFilePath = new File(folderPath);
            if (!myFilePath.delete()) {
                // TODO Exception
            }
        } catch (Exception e) {
            throw new JoboxException(e);
        }
    }

    public static void cleanFolder(String folderPath) {
        try {
            delAllFile(folderPath);
        } catch (Exception e) {
            throw new JoboxException(e);
        }
    }

    private static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            // TODO Exception
        }
        String[] tempList = file.list();
        File temp;
        for (String currentTempFile : tempList) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + currentTempFile);
            } else {
                temp = new File(path + File.separator + currentTempFile);
            }
            if (temp.isFile()) {
                if (!temp.delete()) {
                    // TODO Exception
                }
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + currentTempFile);//$NON-NLS-1$
                deleteFolder(path + "/" + currentTempFile);//$NON-NLS-1$
            }
        }
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

    public static void extract(String zipPathFile, String destinationPath) throws Exception {
        FileInputStream fins = new FileInputStream(zipPathFile);
        ZipInputStream zipInputStream = new ZipInputStream(fins);
        try {
            ZipEntry ze;
            byte ch[] = new byte[256];
            while ((ze = zipInputStream.getNextEntry()) != null) {
                File zipFile = new File(destinationPath + ze.getName());
                File zipFilePath = new File(zipFile.getParentFile().getPath());

                if (ze.isDirectory()) {
                    if (!zipFile.exists()) {
                        if (!zipFile.mkdirs()) {
                            // TODO Exception
                        }
                    }
                    zipInputStream.closeEntry();
                } else {
                    if (!zipFilePath.exists()) {
                        if (!zipFilePath.mkdirs()) {
                            // TODO Exception
                        }
                    }
                    FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
                    int i;
                    while ((i = zipInputStream.read(ch)) != -1)
                        fileOutputStream.write(ch, 0, i);
                    zipInputStream.closeEntry();
                    fileOutputStream.close();
                }
            }
        } finally {
            try {
                fins.close();
            } finally {
                zipInputStream.close();
            }
        }

    }

    public static void findFirstFile(JobInfo jobInfo, File root, String fileName, List<File> resultList) {
        if (resultList.size() > 0)
            return;

        if (root.isFile()) {
            if (root.getName().equals(fileName)) {
                if (jobInfo == null || root.getParentFile().getParentFile().getName().toLowerCase().startsWith(jobInfo.getName().toLowerCase()))
                    resultList.add(root);
            }
        } else if (root.isDirectory()) {
            File[] files = root.listFiles();
            for (File file : files) {
                findFirstFile(jobInfo, file, fileName, resultList);
            }
        }
    }

    private static void zipContents(File dir, String zipPath, ZipOutputStream zos) {
        String[] children = dir.list();
        if (children == null) {
            return;
        }

        for (String currentChild : children) {
            File child = new File(dir, currentChild);

            String childZipPath = zipPath + File.separator + child.getName();

            if (child.isDirectory()) {
                zipContents(child, childZipPath, zos);
            } else {
                try {
                    zip(child, childZipPath, zos);
                } catch (Exception e) {
                    throw new JoboxException(e);
                }
            }
        }
    }

    public static void zip(File file, String zipFilePath) throws IOException {
        if (zipFilePath == null) {
            zipFilePath = file.getAbsolutePath() + ".zip";//$NON-NLS-1$
        }

        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilePath));
        try {
            if (file.isDirectory()) {
                JoboxUtil.zipContents(file, file.getName(), zos);
            } else {
                try {
                    JoboxUtil.zip(file, file.getName(), zos);
                } catch (Exception e) {
                    throw new JoboxException(e);
                }
            }
        } finally {
            zos.close();
        }
    }

    private static void zip(File file, String zipPath, ZipOutputStream zos) throws IOException {
        FileInputStream is = null;
        try {
            byte[] buf = new byte[1024];

            // Add ZIP entry to output stream.
            zos.putNextEntry(new ZipEntry(zipPath));

            // Transfer bytes from the file to the ZIP file
            int len;
            is = new FileInputStream(file);
            while ((len = is.read(buf)) > 0) {
                zos.write(buf, 0, len);
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
