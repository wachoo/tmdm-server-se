package org.talend.mdm.ext.publish.filter;

import java.util.List;

public class DataModelAccessControl extends AccessController {

	@Override
	public boolean validate(List<String> resourceInstances,AccessControlPropertiesReader propertiesReader) {
		
		String bannedPattern=propertiesReader.getProperty("datamodel.name.ban.pattern");
		
		if(resourceInstances!=null&&resourceInstances.size()>0&&bannedPattern!=null){
			String datamodelName=resourceInstances.get(0);
			if(datamodelName.matches(bannedPattern)){
				getLocalLogger().debug("Datamodel "+datamodelName+" was banned! ");
				return false;
			}
		}
		
		return true;
	}

}
