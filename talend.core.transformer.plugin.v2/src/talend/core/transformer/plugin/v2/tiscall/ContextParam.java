package talend.core.transformer.plugin.v2.tiscall;


public class ContextParam {
	String name;
	String value;
	boolean isPipleVariableName=false;
	
	
	public boolean isPipleVariableName() {
		return isPipleVariableName;
	}

	public void setPipleVariableName(boolean isPipleVariableName) {
		this.isPipleVariableName = isPipleVariableName;
	}

	public ContextParam(String key, String value, boolean ispiple){
		this.name=key;
		this.value=value;
		this.isPipleVariableName=ispiple;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
