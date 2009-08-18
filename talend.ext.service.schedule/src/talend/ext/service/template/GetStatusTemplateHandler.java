package talend.ext.service.template;

public class GetStatusTemplateHandler implements ITemplateHandler{

	public String getContent(String serviceName) {
		StringBuffer sb = new StringBuffer();
//		sb.append("<input> \n"); 
//		sb.append("   <!-- Input is empty --> \n"); 
//		sb.append("</input> \n");
		return sb.toString();
	}

	public Object[] parse(String inputInstance) {
		
		return new Object[0];
	}

}
