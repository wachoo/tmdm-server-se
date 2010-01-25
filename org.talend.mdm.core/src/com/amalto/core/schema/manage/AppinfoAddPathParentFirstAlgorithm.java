package com.amalto.core.schema.manage;
import java.util.Iterator;
import java.util.List;

public class AppinfoAddPathParentFirstAlgorithm extends
		AppinfoAddPathAbstractAlgorithm {

	@Override
	public boolean check(List<String> existXpaths, String newPath) {
		boolean pass = true;

		if (existXpaths != null && existXpaths.size() > 0) {
			for (Iterator iterator = existXpaths.iterator(); iterator.hasNext();) {
				String existXpath = (String) iterator.next();
				if (existXpath != null && existXpath.trim().length() > 0) {
					if (newPath.startsWith(existXpath)) {
						pass = false;
						break;
					}// means existPath is newPath's parent
				}
				
			}
		}

		return pass;
	}

}
