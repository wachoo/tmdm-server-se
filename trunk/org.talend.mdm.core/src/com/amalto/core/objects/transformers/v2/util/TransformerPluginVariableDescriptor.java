package com.amalto.core.objects.transformers.v2.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TransformerPluginVariableDescriptor {

    private String variableName;

    private Map<String, String> descriptions = new HashMap<String, String>();

    private List<Pattern> contentTypesRegex;

    private boolean mandatory = false;

    private List<Pattern> possibleValuesRegex = null;

    public List<Pattern> getContentTypesRegex() {
        return contentTypesRegex;
    }

    public void setContentTypesRegex(List<Pattern> contentTypes) {
        this.contentTypesRegex = contentTypes;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<String, String> descriptions) {
        this.descriptions = descriptions;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String name) {
        this.variableName = name;
    }

    public List<Pattern> getPossibleValuesRegex() {
        return possibleValuesRegex;
    }

    public void setPossibleValuesRegex(List<Pattern> possibleValues) {
        this.possibleValuesRegex = possibleValues;
    }
}
