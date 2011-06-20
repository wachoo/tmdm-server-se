package com.amalto.core.jobox.watch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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

    public void fileChanged(List newFiles, List deleteFiles, List modifyFiles) {

        if (newFiles.size() > 0) {
            // new
            for (Iterator iterator = newFiles.iterator(); iterator.hasNext();) {
                String jobPackageName = (String) iterator.next();
                // deploy
                JobContainer.getUniqueInstance().getJobDeploy().deploy(jobPackageName);
                // add to classpath
                JobInfo jobInfo = JobContainer.getUniqueInstance().getJobAware()
                        .loadJobInfo(JoboxUtil.trimExtension(jobPackageName));
                JobContainer.getUniqueInstance().updateJobLoadersPool(jobInfo);
            }
        }

        if (deleteFiles.size() > 0) {
            // delete
            for (Iterator iterator = deleteFiles.iterator(); iterator.hasNext();) {
                String jobPackageName = (String) iterator.next();
                String jobEntityName = JoboxUtil.trimExtension(jobPackageName);
                // undeploy
                JobContainer.getUniqueInstance().getJobDeploy().undeploy(jobEntityName);
                // remove classpath
                JobContainer.getUniqueInstance().removeFromJobLoadersPool(jobEntityName);
            }
        }

        if (modifyFiles.size() > 0) {
            // modify
            for (Iterator iterator = modifyFiles.iterator(); iterator.hasNext();) {
                String jobPackageName = (String) iterator.next();
                // deploy
                JobContainer.getUniqueInstance().getJobDeploy().deploy(jobPackageName);
                // add to classpath
                JobInfo jobInfo = JobContainer.getUniqueInstance().getJobAware()
                        .loadJobInfo(JoboxUtil.trimExtension(jobPackageName));
                JobContainer.getUniqueInstance().updateJobLoadersPool(jobInfo);
            }
        }
    }

    public void contextChanged(String entityPath, String context) {

        File entity = new File(entityPath);
        String sourcePath = entityPath;
        int dotMark = entityPath.lastIndexOf("."); //$NON-NLS-1$
        int separateMark = entityPath.lastIndexOf(File.separatorChar);
        if (dotMark != -1) {
            sourcePath = System.getProperty("java.io.tmpdir") + File.separatorChar + entityPath.substring(separateMark, dotMark); //$NON-NLS-1$
        }
        try {
            JoboxUtil.extract(entityPath, System.getProperty("java.io.tmpdir") + File.separatorChar); //$NON-NLS-1$
        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }
        List<File> resultList = new ArrayList<File>();
        JoboxUtil.findFirstFile(new File(sourcePath), "classpath.jar", resultList); //$NON-NLS-1$
        if (!resultList.isEmpty()) {
            JarInputStream jarIn = null;
            JarOutputStream jarOut = null;
            try {
                JarFile jarFile = new JarFile(resultList.get(0));
                Manifest mf = jarFile.getManifest();

                jarIn = new JarInputStream(new FileInputStream(resultList.get(0)));
                Manifest newmf = jarIn.getManifest();
                if (newmf == null) {
                    newmf = new Manifest();
                }
                newmf.getMainAttributes().putAll(mf.getMainAttributes());
                newmf.getMainAttributes().putValue("activeContext", context); //$NON-NLS-1$
                jarOut = new JarOutputStream(new FileOutputStream(resultList.get(0)), newmf);
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
                    JoboxUtil.zip(sourceFile, entityPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
