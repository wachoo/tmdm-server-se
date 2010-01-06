package talend.ext.service.template;

public interface ITemplateHandler {
	
	public String getContent(String serviceName);
	
	public Object[] parse(String inputInstance);

}
