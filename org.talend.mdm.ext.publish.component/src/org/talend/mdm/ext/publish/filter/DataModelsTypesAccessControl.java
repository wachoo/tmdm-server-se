package org.talend.mdm.ext.publish.filter;

import java.util.List;

public class DataModelsTypesAccessControl extends AccessController {

	@Override
	public boolean validate(List<String> resourceInstances,AccessControlPropertiesReader propertiesReader) {
		
		String bannedPattern=propertiesReader.getProperty("datamodelstypes.name.ban.pattern");
		
		if(resourceInstances!=null&&resourceInstances.size()>0&&bannedPattern!=null){
			String datamodelName=resourceInstances.get(0);
			if(datamodelName.matches(bannedPattern)){
				getLocalLogger().debug("The types of datamodel "+datamodelName+" was banned! ");
				return false;
			}
		}
		
		return true;
	}

}
