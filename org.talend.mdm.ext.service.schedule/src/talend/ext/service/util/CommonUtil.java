package talend.ext.service.util;

import java.io.StringReader;
import java.lang.reflect.Method;

import org.apache.commons.lang.WordUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;

import talend.ext.service.entity.SrvSchedulePlan;
import talend.ext.service.template.ITemplateHandler;

import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class CommonUtil {

	public static ITemplateHandler getMethodTemplateHandler(String methodName)throws Exception {
		String handlerMethodName = "talend.ext.service.template."+ WordUtils.capitalize(methodName) + "TemplateHandler";
		ITemplateHandler templateHandler = (ITemplateHandler) ReflectionUtil.newInstance(handlerMethodName, new Object[0]);
		return templateHandler;
	}
	
	public static String invokeService(String serviceName, String methodName,String parameters) throws XtentisException {
		String rtn = "";

		// serviceName and methodName can not be empty
		if (serviceName == null || serviceName.length() == 0)
			return "Error:Service name can not be empty! ";

		// do invoke
		try {

			Object service = Util.retrieveComponent(null,
					"amalto/local/service/" + serviceName);

			if (AvailableMethodList.isAvailable(methodName)) {

				ITemplateHandler templateHandler = getMethodTemplateHandler(methodName);

				Method toInvokeMethod = Util.getMethod(service, methodName);
				Object rtnObj = toInvokeMethod.invoke(service, templateHandler
						.parse(parameters));
				if (rtnObj != null && rtnObj instanceof String)
					rtn = (String) rtnObj;

			} else {
				return "Error:Nonsupport Method! ";
			}

		} catch (XtentisException e) {
			e.printStackTrace();
			throw new XtentisException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisException(e);
		}
		return rtn;
	}
	
	public static SrvSchedulePlan convertPlanStrToPOJO(String srvSchedulePlanXml){
		SrvSchedulePlan srvSchedulePlan = null;
		try {
			srvSchedulePlan = (SrvSchedulePlan) Unmarshaller.unmarshal(SrvSchedulePlan.class, new InputSource(new StringReader(srvSchedulePlanXml)));
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		return srvSchedulePlan;
	}

}
