package talend.ext.service.util;

import java.util.ArrayList;
import java.util.List;

public class AvailableMethodList {

	public final static List<String> methods = new ArrayList<String>();

	static {
		methods.add("start");
		methods.add("stop");
		methods.add("getStatus");
		methods.add("fetchFromOutbound");
	}

	public static boolean isAvailable(String methodName) {
		if (methods.contains(methodName))
			return true;
		return false;
	}

}
