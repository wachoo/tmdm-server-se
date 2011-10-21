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

package com.amalto.core.jobox.watch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.JobInfo;
import com.amalto.core.jobox.util.JoboxUtil;

public class JoboxListener implements DirListener {

    public void fileChanged(List<String> newFiles, List<String> deleteFiles, List<String> modifyFiles) {

        if (newFiles.size() > 0) {
            // new
            for (String jobPackageName : newFiles) {
                // deploy
                JobContainer.getUniqueInstance().getJobDeployer().deploy(jobPackageName);
                // add to classpath
                JobInfo jobInfo = JobContainer.getUniqueInstance().getJobAware()
                        .loadJobInfo(JoboxUtil.trimExtension(jobPackageName));
                JobContainer.getUniqueInstance().updateJobLoadersPool(jobInfo);
            }
        }

        if (deleteFiles.size() > 0) {
            // delete
            for (String jobPackageName : deleteFiles) {
                String jobEntityName = JoboxUtil.trimExtension(jobPackageName);
                // undeploy
                JobContainer.getUniqueInstance().getJobDeployer().undeploy(jobEntityName);
                // remove classpath
                JobContainer.getUniqueInstance().removeFromJobLoadersPool(jobEntityName);
            }
        }

        if (modifyFiles.size() > 0) {
            // modify
            for (String jobPackageName : modifyFiles) {
                // deploy
                JobContainer.getUniqueInstance().getJobDeployer().deploy(jobPackageName);
                // add to classpath
                JobInfo jobInfo = JobContainer.getUniqueInstance().getJobAware()
                        .loadJobInfo(JoboxUtil.trimExtension(jobPackageName));
                JobContainer.getUniqueInstance().updateJobLoadersPool(jobInfo);
            }
        }
    }

    public void contextChanged(String jobFile, String context) {

        File entity = new File(jobFile);
        String sourcePath = jobFile;
        int dotMark = jobFile.lastIndexOf("."); //$NON-NLS-1$
        int separateMark = jobFile.lastIndexOf(File.separatorChar);
        if (dotMark != -1) {
            sourcePath = System.getProperty("java.io.tmpdir") + File.separatorChar + jobFile.substring(separateMark, dotMark); //$NON-NLS-1$
        }
        try {
            JoboxUtil.extract(jobFile, System.getProperty("java.io.tmpdir") + File.separatorChar); //$NON-NLS-1$
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        List<File> resultList = new ArrayList<File>();
        JoboxUtil.findFirstFile(null, new File(sourcePath), "classpath.jar", resultList); //$NON-NLS-1$
        if (!resultList.isEmpty()) {
            JarInputStream jarIn = null;
            JarOutputStream jarOut = null;
            try {
                JarFile jarFile = new JarFile(resultList.get(0));
                Manifest mf = jarFile.getManifest();

                jarIn = new JarInputStream(new FileInputStream(resultList.get(0)));
                Manifest newManifest = jarIn.getManifest();
                if (newManifest == null) {
                    newManifest = new Manifest();
                }
                newManifest.getMainAttributes().putAll(mf.getMainAttributes());
                newManifest.getMainAttributes().putValue("activeContext", context); //$NON-NLS-1$
                jarOut = new JarOutputStream(new FileOutputStream(resultList.get(0)), newManifest);
                byte[] buf = new byte[4096];
                JarEntry entry;
                while ((entry = jarIn.getNextJarEntry()) != null) {
                    if ("META-INF/MANIFEST.MF".equals(entry.getName()))//$NON-NLS-1$
                        continue;
                    jarOut.putNextEntry(entry);
                    int read;
                    while ((read = jarIn.read(buf)) != -1) {
                        jarOut.write(buf, 0, read);
                    }
                    jarOut.closeEntry();

                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (jarIn != null)
                        jarIn.close();
                    jarIn = null;
                    if (jarOut != null)
                        jarOut.close();
                    jarOut = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // re-zip file
            if (entity.getName().endsWith(".zip")) { //$NON-NLS-1$
                File sourceFile = new File(sourcePath);
                try {
                    JoboxUtil.zip(sourceFile, jobFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
