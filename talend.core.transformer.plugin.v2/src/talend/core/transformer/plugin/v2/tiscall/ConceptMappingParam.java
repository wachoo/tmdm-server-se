package talend.core.transformer.plugin.v2.tiscall;


public class ConceptMappingParam {
	
	private String concept;
	
	//using json format
	//TODO now only support 2 level mapping
	//maybe we can support tree structure
	//or simple functions
	private String fields;
	
	public ConceptMappingParam(String concept, String fields) {
		super();
		this.concept = concept;
		this.fields = fields;
	}

	public String getConcept() {
		return concept==null?"":concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public String getFields() {
		return fields==null?"":fields;
	}

	public void setFields(String fields) {
		this.fields = fields;
	}
	
	
}
