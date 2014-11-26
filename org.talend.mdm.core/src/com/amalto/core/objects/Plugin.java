package com.amalto.core.objects;

import com.amalto.core.objects.transformers.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.util.TransformerPluginVariableDescriptor;
import com.amalto.core.util.XtentisException;

import java.util.ArrayList;

/**
 *
 */
public abstract class Plugin {
    public abstract String getJNDIName() throws XtentisException;

    public abstract String getDescription(String twoLettersLanguageCode) throws XtentisException;

    public abstract String getDocumentation(String twoLettersLanguageCode) throws XtentisException;

    public abstract ArrayList<TransformerPluginVariableDescriptor> getInputVariableDescriptors(String twoLettersLanguageCode) throws XtentisException;

    public abstract ArrayList<TransformerPluginVariableDescriptor> getOutputVariableDescriptors(String twoLettersLanguageCode) throws XtentisException;

    public abstract String getParametersSchema() throws XtentisException;

    public abstract String compileParameters(String parameters) throws XtentisException;

    public abstract void init(TransformerPluginContext context, String compiledParameters) throws XtentisException;

    protected abstract String loadConfiguration();

    public abstract void execute(TransformerPluginContext context) throws XtentisException;
}
