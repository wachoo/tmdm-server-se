package com.amalto.core.migration.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.w3c.dom.NodeList;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.Util;

public class UpdateOldRolesWithNewRoleSchemeTask extends AbstractMigrationTask{
	static final HashMap<String, String> oldRoleToNewRoleMap = new HashMap<String, String>(); 
	static{
		oldRoleToNewRoleMap.put("Default_Admin", ICoreConstants.SYSTEM_ADMIN_ROLE);
		oldRoleToNewRoleMap.put("Default_User", ICoreConstants.SYSTEM_INTERACTIVE_ROLE);
		oldRoleToNewRoleMap.put("Default_Viewer", ICoreConstants.SYSTEM_VIEW_ROLE);
	}
	
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
                    String newUniqueID = oldRoleToNewRoleMap.get(uniqueID);
					 if(newUniqueID != null)
					 {
						 for (Map.Entry<String, String> pair : oldRoleToNewRoleMap.entrySet())
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
					 for (Map.Entry<String, String> pair : oldRoleToNewRoleMap.entrySet())
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
	@Override
	protected Boolean execute() {
		
		return updateRolesInProvision() && updateRolesInDataModel();
	}
}
