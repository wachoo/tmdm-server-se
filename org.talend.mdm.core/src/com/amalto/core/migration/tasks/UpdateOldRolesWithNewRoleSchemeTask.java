package com.amalto.core.migration.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.w3c.dom.NodeList;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.Util;

public class UpdateOldRolesWithNewRoleSchemeTask extends AbstractMigrationTask{
	
	static final String BAR_ZIP = ".bar";
	static final String SUFFIX_BAR = "_r";
	
	private boolean updateRolesInProvision()
	{
		final String userClusterName = "amaltoOBJECTSRole";
		String query = "collection(\"" + userClusterName + "\")/role-pOJO/PK/ids";    //collection("/amaltoOBJECTSRole")/role-pOJO/PK/ids
		try {
			ArrayList<String> list = ConfigurationHelper.getServer().runQuery(null, userClusterName, query, null);
			for (String user: list)
			{
				NodeList users = Util.getNodeList(Util.parse(user), "/ids");
				for (int i = 0; i < users.getLength(); i++)
				{
					String uniqueID = users.item(i).getFirstChild().getNodeValue();
					String userXml = ConfigurationHelper.getServer().getDocumentAsString(null, userClusterName, uniqueID);
                    String newUniqueID = ICoreConstants.rolesConvert.oldRoleToNewRoleMap.get(uniqueID);
					 if(newUniqueID != null)
					 {
						 for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet())
						 {
							 userXml = userXml.replaceAll(pair.getKey().toString(), pair.getValue().toString());
						 }
						 ConfigurationHelper.getServer().putDocumentFromString(userXml, newUniqueID, userClusterName, null);
						 ConfigurationHelper.getServer().deleteDocument(null, userClusterName, uniqueID);
					 }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private boolean updateRolesInDataModel()
	{
		final String  dataModelClusterName = "amaltoOBJECTSDataModel";
		String query = "collection(\"" + dataModelClusterName + "\")/data-model-pOJO/name";   //collection('amaltoOBJECTSDataModel')/data-model-pOJO/name
		try
		{
			ArrayList<String> list = ConfigurationHelper.getServer().runQuery(null, dataModelClusterName, query, null);
			for (String role: list)
			{
				NodeList roles = Util.getNodeList(Util.parse(role), "/name");
				for (int i = 0; i < roles.getLength(); i++)
				{
					String uniqueID = roles.item(i).getFirstChild().getNodeValue();
					if(uniqueID.equals("XMLSCHEMA---"))continue;
					String dataModelXml = ConfigurationHelper.getServer().getDocumentAsString(null, dataModelClusterName, uniqueID);
					String cpyXml = new String(dataModelXml);
					 for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet())
					 {
						 dataModelXml = dataModelXml.replaceAll(pair.getKey().toString(), pair.getValue().toString());
					 }
					 if(!dataModelXml.equals(cpyXml))
					 {
						 ConfigurationHelper.getServer().putDocumentFromString(dataModelXml, uniqueID, dataModelClusterName, null);
					 }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
    private static boolean updateRolesInWorkFlow()
    {
        String barHome = Util.getBarHomeDir();
        File barDir = new File(barHome);
        File monitorFiles[] = barDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name)
            {
                return name.endsWith(".bar");
            }
        });

        ArrayList<String> oldBarFiles = new ArrayList<String>();
        ArrayList<String> newBarFiles = new ArrayList<String>();
        for (File barFile : monitorFiles)
        {
            String outName = barFile.getAbsolutePath().substring(0, barFile.getAbsolutePath().length() - 4) + SUFFIX_BAR + BAR_ZIP;
            JarInputStream jarIn = null;
            JarOutputStream jarOut = null;
            ByteArrayOutputStream outBytes = null;
            try {
                 jarIn = new JarInputStream(new FileInputStream(barFile));
                 jarOut = new JarOutputStream(new FileOutputStream(outName));
                JarEntry entry;
                byte[] buf = new byte[4096];
                while ((entry = jarIn.getNextJarEntry()) != null) {

                  if ("META-INF/MANIFEST.MF".equals(entry.getName())) continue;
                  int read;
                  jarOut.putNextEntry(entry);
                  if(!entry.getName().endsWith(".proc") && !entry.getName().endsWith(".xml"))
                  {
                      while ((read = jarIn.read(buf)) != -1) {
                          jarOut.write(buf, 0, read);
                      }
                  }
                  else
                  {
                      outBytes = new ByteArrayOutputStream();
                      while ((read = jarIn.read(buf, 0, 4096)) != -1) {
                          outBytes.write(buf, 0 , read);
                      }

                      String orgSrc = new String(outBytes.toByteArray());
                      String orgCpy = new String(orgSrc);
                      for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet())
                      {
                          orgSrc = orgSrc.replaceAll(pair.getKey().toString(), pair.getValue().toString());
                      }

                      if(orgSrc.equals(orgCpy))
                      {
                          jarOut.write(orgCpy.getBytes(), 0, orgCpy.getBytes().length);
                      }
                      else
                      {
                          jarOut.write(orgSrc.getBytes(), 0, orgSrc.getBytes().length);
                          if(oldBarFiles.indexOf(barFile.getAbsolutePath()) == -1){
                              oldBarFiles.add(barFile.getAbsolutePath());
                          }
                          if(newBarFiles.indexOf(outName) == -1){
                              newBarFiles.add(outName);  
                          }
                      }

                  }

                  jarOut.closeEntry();
                }
                
                if(newBarFiles.indexOf(outName) == -1){
                	oldBarFiles.add(outName);  
                }
                jarOut.flush();
            } catch (Exception e) {
                return false;
            }
            finally
            {
                try {
	                outBytes.close();
					jarOut.close();
	                jarIn.close();
				} catch (IOException e) {
					return false;
				}
            }
        }

        for (String fileName: oldBarFiles)
        {
            File toDelFile = new File(fileName);
            if(toDelFile.exists())
                toDelFile.delete();
        }


        boolean rename = true;
        for (String fileName : newBarFiles)
        {
            File newFile = new File(fileName);
            if(newFile.exists())
            {
                int sign = newFile.getAbsolutePath().indexOf(SUFFIX_BAR);
                if(newFile.renameTo(new File(newFile.getAbsolutePath().substring(0, sign) + BAR_ZIP)) == false)
                {
                	rename = false;
                }
            }
        }
        
        return rename;
  }

	
	@Override
	protected Boolean execute() {
		boolean execute = updateRolesInProvision() && updateRolesInDataModel();
		if(Util.isEnterprise()){
			execute &= updateRolesInWorkFlow();
		}
		return execute;
	}
}
