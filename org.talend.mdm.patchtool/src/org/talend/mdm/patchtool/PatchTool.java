package org.talend.mdm.patchtool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class PatchTool {
    
    private static String userDir = System.getProperty("user.dir"); //$NON-NLS-1$

    private static String tempDir = userDir + "/temp"; //$NON-NLS-1$
        
    public static void main(String[] args) throws Exception {

        System.out.println("Create a temp Folder..."); //$NON-NLS-1$
        File file = new File(tempDir);
        file.mkdir();

        System.out.println("Unzip the tem.ear package...."); //$NON-NLS-1$
        Process process = Runtime.getRuntime().exec("jar xvf ../../../../server/default/deploy/tem.ear", null, file); //$NON-NLS-1$
        InputStream is = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        List<String> list = new ArrayList<String>();
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            if(!line.contains(":")) //$NON-NLS-1$
                list.add(line.substring(line.lastIndexOf(" "), line.length()).trim()); //$NON-NLS-1$
        }
        process.waitFor();
        is.close();
        reader.close();
        process.destroy();
        
        if(args.length == 0) {
            System.out.println("Invalid command"); //$NON-NLS-1$
            cleanup(file);
            file.delete();
            return;
        }
            
        System.out.println("Replace the file.....");  //$NON-NLS-1$
        for(String fileName : args){
            File fromFile = null;
            if(fileName.contains("/") || fileName.contains("\\"))  //$NON-NLS-1$//$NON-NLS-2$
                fromFile = new File(fileName.trim());
            else
                fromFile = new File(userDir + "/" + fileName.trim()); //$NON-NLS-1$
            
            File toFile = null;
            for(String str : list) {
                if(str.contains(fileName.trim())) {
                    toFile = new File(tempDir + "/" + str); //$NON-NLS-1$
                    break;
                }
            }
            
            if(toFile == null)
                toFile = new File(tempDir + "/" + fileName); //$NON-NLS-1$
            moveFile(fromFile, toFile);
        }
        
        System.out.println("Re-Package tem.ear"); //$NON-NLS-1$
        Process packageProcess = Runtime.getRuntime().exec("jar cvf tem.ear ./*", null, file); //$NON-NLS-1$
        is = packageProcess.getInputStream();
        reader = new BufferedReader(new InputStreamReader(is));
        line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        packageProcess.waitFor();
        is.close();
        reader.close();
        packageProcess.destroy();
        
        System.out.println("Move new tem.ear to deploy folder"); //$NON-NLS-1$
        String[] arr = userDir.split("bin"); //$NON-NLS-1$
        File deployFile = new File(arr[0] + "server/default/deploy/tem.ear"); //$NON-NLS-1$
        moveFile(new File(tempDir + "/tem.ear"), deployFile); //$NON-NLS-1$
        System.out.println("Move Successfully!"); //$NON-NLS-1$
        cleanup(file);
        file.delete();
    }
    
    private static void moveFile(File fromFile, File toFile) throws IOException {
        if(toFile.exists()) {
            toFile.delete();
        }
        
        FileInputStream in = new FileInputStream(fromFile);
        FileOutputStream out=new FileOutputStream(toFile);
        byte[] buffer = new byte[4096];
        while (true) {
            int index = in.read(buffer);
            if (index == -1) {
                in.close();
                out.flush();
                out.close();
                break;               
            } else {
                out.write(buffer, 0, index);
            }
        }      
    }
    
    private static void cleanup(File f) {
        File[] files = f.listFiles();
        for (File file : files) {
            if (file.isFile())
                file.delete();
            else if (file.isDirectory()) {
                cleanup(file);
                file.delete();
            }
        }
    }

}