package org.talend.mdm.workflow.client.util;

import java.util.List;

public class CommonUtil {
	
	public static String joinStrings(List<String> strings, String separator) {
		if (strings == null) return null;
		String res = "";
		for (int i = 0; i < strings.size(); i++) {
			res+= (i>0) ? separator : "";
			res+=(strings.get(i) == null ? "" : strings.get(i));
		}
		return res;
	}
	
	public static String filterNullString(String input) {
		if(input==null)return "";
		return input;
	}
}
