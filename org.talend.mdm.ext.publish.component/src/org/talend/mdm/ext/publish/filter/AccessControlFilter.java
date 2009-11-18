package org.talend.mdm.ext.publish.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.restlet.Filter;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.talend.mdm.ext.publish.ResourceType;

public class AccessControlFilter extends Filter {

	protected int beforeHandle(Request request, Response response) {

		try {
			String inputPath = request.getResourceRef().getPath();
			Pattern pattern = Pattern.compile("/pubcomponent/secure/(.*)");
			Matcher matcher = pattern.matcher(inputPath);
			while (matcher.find())

			{
				List<String> parts = new ArrayList<String>();
				String part = matcher.group(1);
				if (part != null && part.length() > 0) {
					String[] tmp = part.split("/");
					if (tmp != null && tmp.length > 0) {
						parts = Arrays.asList(tmp);
					}
				}

				if (parts.size() > 0) {
					String resourceType = parts.get(0);
					parts=parts.subList(1, parts.size());

					AccessController accessController = null;
					AccessControlPropertiesReader propertiesReader = AccessControlPropertiesReader
							.getInstance();
					if (resourceType.equals(ResourceType.DATAMODELS.getName())) {
						accessController = new DataModelsAccessControl();
					}else if(resourceType.equals(ResourceType.DATAMODELSTYPES.getName())){
						accessController = new DataModelsTypesAccessControl();
					}else {
						//TODO support more types
					}

					if (accessController != null
							&& !accessController.validate(parts,
									propertiesReader)) {
						return Filter.STOP;
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Filter.CONTINUE;

	}

}
