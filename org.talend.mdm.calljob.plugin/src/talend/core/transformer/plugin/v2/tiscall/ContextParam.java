package talend.core.transformer.plugin.v2.tiscall;


public class ContextParam {
    private String name;

    private String value;

    private boolean isPipelineVariableName = false;

    public boolean isPipelineVariableName() {
        return isPipelineVariableName;
    }

    public void setPipelineVariableName(boolean isPipelineVariableName) {
        this.isPipelineVariableName = isPipelineVariableName;
    }

    public ContextParam(String key, String value, boolean isPipeline) {
        this.name = key;
        this.value = value;
        this.isPipelineVariableName = isPipeline;
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
