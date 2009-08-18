package talend.ext.service.template;


public class TemplateFillAssistant {
	
	private static final String schedulePlanIDPattern = "<schedulePlanID>.*</schedulePlanID>";
	
	// TODO had better change parameter to map
	public static String fill(String serviceName,String methodName,String lawContent,String schedulePlanId) {
		
		String replacedContent=lawContent;
		
		if(methodName.equals("fetchFromOutbound")){
			replacedContent=lawContent.replaceAll(schedulePlanIDPattern, "<schedulePlanID>"+schedulePlanId+"</schedulePlanID>");
		}
		
		return replacedContent;
		

	}

}
