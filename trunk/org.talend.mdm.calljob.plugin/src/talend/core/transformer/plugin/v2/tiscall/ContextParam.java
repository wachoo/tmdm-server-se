package talend.core.transformer.plugin.v2.tiscall;


public class ContextParam {
    private String name;

    private String value;

    private boolean isPipleVariableName = false;

    public boolean isPipelineVariableName() {
        return isPipleVariableName;
    }

    public void setPipelineVariableName(boolean isPipleVariableName) {
        this.isPipleVariableName = isPipleVariableName;
    }

    public ContextParam(String key, String value, boolean isPipeline) {
        this.name = key;
        this.value = value;
        this.isPipleVariableName = isPipeline;
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
