package com.amalto.core.server;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.objects.ItemPOJO;
import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.ObjectPOJOPK;
import com.amalto.core.objects.backgroundjob.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.BackgroundJobPOJOPK;
import com.amalto.core.objects.transformers.TransformerV2POJO;
import com.amalto.core.objects.transformers.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.util.*;
import com.amalto.core.objects.universe.UniversePOJO;
import com.amalto.core.query.user.DateTimeConstant;
import com.amalto.core.util.JobActionInfo;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import com.amalto.core.server.api.Transformer;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.regex.Pattern;


public class DefaultTransformer implements TransformerPluginCallBack, com.amalto.core.server.api.Transformer {

    public static final long serialVersionUID = 1986745965402456L;

    public static final Logger LOGGER = Logger.getLogger(DefaultTransformer.class);

    /**
     * Creates or updates a Transformer
     */
    @Override
    public TransformerV2POJOPK putTransformer(TransformerV2POJO transformer) throws XtentisException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("putTransformer() " + transformer.getName());
        }
        try {
            //Check and compile the parameters one by one
            ArrayList<TransformerProcessStep> specs = transformer.getProcessSteps();
            for (TransformerProcessStep step : specs) {
                //get the plugin
                TransformerPluginV2LocalInterface plugin = getPlugin(step.getPluginJNDI());
                step.setCompiledParameters(plugin.compileParameters(step.getParameters()));
            }
            return new TransformerV2POJOPK(transformer.store());
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to create/update the Transfomer " + transformer.getName()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err);
        }
    }

    /**
     * Get item
     */
    @Override
    public TransformerV2POJO getTransformer(TransformerV2POJOPK pk) throws XtentisException {
        try {
            TransformerV2POJO transformer = ObjectPOJO.load(TransformerV2POJO.class, pk);
            if (transformer == null) {
                String err = "The Transformer " + pk.getUniqueId() + " does not exist.";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return transformer;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the Transformer " + pk.toString()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err);
        }
    }

    /**
     * Get a Transformer - no exception is thrown: returns null if not found
     */
    @Override
    public TransformerV2POJO existsTransformer(TransformerV2POJOPK pk) throws XtentisException {
        try {
            return ObjectPOJO.load(TransformerV2POJO.class, pk);
        } catch (XtentisException e) {
            return null;
        } catch (Exception e) {
            String info = "Could not check whether this Transformer exists:  " + pk.toString()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.debug("existsTransformer() " + info, e);
            return null;
        }
    }

    /**
     * Remove an item
     */
    @Override
    public TransformerV2POJOPK removeTransformer(TransformerV2POJOPK pk)
            throws XtentisException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("removeTransformer() " + pk.getUniqueId());
        }
        try {
            return new TransformerV2POJOPK(ObjectPOJO.remove(TransformerV2POJO.class, pk));
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to remove the Transformer " + pk.toString()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err);
        }
    }

    /**
     * Retrieve all Transformer PKS
     */
    @Override
    public Collection<TransformerV2POJOPK> getTransformerPKs(String regex) throws XtentisException {
        Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(TransformerV2POJO.class, regex);
        ArrayList<TransformerV2POJOPK> l = new ArrayList<TransformerV2POJOPK>();
        for (ObjectPOJOPK objectPOJOPK : c) {
            l.add(new TransformerV2POJOPK(objectPOJOPK));
        }
        return l;
    }

    /**
     * Read an item and process it through a transformer.
     * The content of the item is mapped to the {@link #DEFAULT_VARIABLE} variable
     *
     * @return The pipeline after the transformer is run
     * @throws com.amalto.core.util.XtentisException
     */
    @Override
    public TransformerContext extractThroughTransformer(
            TransformerV2POJOPK transformerV2POJOPK,
            ItemPOJOPK itemPOJOPK
    ) throws XtentisException {
        TransformerContext ctx = new TransformerContext(transformerV2POJOPK);
        try {
            ItemPOJO item = Util.getItemCtrl2Local().getItem(itemPOJOPK);
            ctx.putInPipeline(
                    Transformer.DEFAULT_VARIABLE,
                    new TypedContent(
                            item.getProjectionAsString().getBytes("UTF-8"),
                            "text/xml; charset=utf-8"
                    )
            );
        } catch (Exception e) {
            String err = "Unable to extract '" + itemPOJOPK.getUniqueID() + "' through the Transformer '" + transformerV2POJOPK.getUniqueId() + "'";
            LOGGER.error(err, e);
            throw new XtentisException(err);
        }

        return executeUntilDone(ctx);

    }

    /**
     * Executes theTransformer
     */
    @Override
    public BackgroundJobPOJOPK executeAsJob(
            TransformerContext context,
            TransformerCallBack callBack
    ) throws XtentisException {
        try {
            //create a Background Job
            BackgroundJobPOJO bgPOJO = new BackgroundJobPOJO();
            bgPOJO.setDescription("Execute Transformer " + context.getTransformerV2POJOPK().getUniqueId() + " as a Background Job");
            bgPOJO.setMessage("Scheduling the job");
            bgPOJO.setPercentage(-1);
            bgPOJO.setSerializedObject(null);
            bgPOJO.setStatus(BackgroundJobPOJO._SCHEDULED_);
            synchronized (DateTimeConstant.DATE_FORMAT) {
                bgPOJO.setTimestamp(DateTimeConstant.DATE_FORMAT.format(new Date(System.currentTimeMillis())));
            }
            bgPOJO.store();
            //launch job in background
            JobActionInfo actionInfo = new JobActionInfo(
                    bgPOJO.getId(),
                    LocalUser.getLocalUser().getUniverse(),
                    context.getTransformerV2POJOPK().getUniqueId(), //action
                    context
            );
            throw new NotImplementedException();
            // TODO create timer
            // return new BackgroundJobPOJOPK(bgPOJO.getPK());
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to execute the Transformer: " + context.getTransformerV2POJOPK().getUniqueId()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err);
        }
    }

    /**
     * Executes the Transformer Asynchronously by specifying the universe<br/>
     * The user must have the 'administration" role
     */
    @Override
    public void execute(
            UniversePOJO universe,
            TransformerContext context,
            TransformerCallBack callBack
    ) throws XtentisException {
        ILocalUser user = LocalUser.getLocalUser();
        if (!user.getRoles().contains("administration")) {
            String err = "The user '" + LocalUser.getLocalUser().getUsername() + "' does not have the 'administration' role";
            LOGGER.error(err);
            throw new XtentisException(err);
        }
        //switch the Universe
        user.setUniverse(universe);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Executing as Job Transformer '" + context.getTransformerV2POJOPK().getUniqueId() + "' " +
                            "with user '" + user.getUsername() + "' in Universe '" + user.getUniverse().getName() + "'"
            );
        }
        execute(context, callBack);
    }

    /**
     * Executes the Transformer Asynchronously
     */
    @Override
    public void execute(
            TransformerContext context,
            TransformerCallBack callBack
    ) throws XtentisException {
        //transform the context as a global context if not already done
        //The context will be a TransformerContext if called directly,
        //a TransformerGlobalCOntext if called from process
        TransformerGlobalContext globalContext = null;
        if (context instanceof TransformerGlobalContext) {
            globalContext = (TransformerGlobalContext) context;
        } else {
            globalContext = new TransformerGlobalContext(context);
        }
        //sets the callback in the transformers used by the plugins when the content is ready
        //{@link contentIsReady}
        globalContext.setExecuteCallBack(callBack);
        try {
            //and run the plugin
            TransformerV2POJO transformerPOJO = globalContext.getTransformerPOJO();
            if (
                    (transformerPOJO.getProcessSteps() != null) &&
                            (transformerPOJO.getProcessSteps().size() > 0)
                    ) {
                executePlugin(globalContext, 0);
                //signal done to the call back
                callBack.done(globalContext);
            }

        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to execute the Transformer: " + context.getTransformerV2POJOPK().getUniqueId()
                    + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err);
        } finally {
            //End the plugins
            try {
                LinkedHashMap<Integer, TransformerPluginV2LocalInterface> instantiatedPlugins = globalContext.getInstantiatedPlugins();
                Set<Integer> pluginNumbers = instantiatedPlugins.keySet();
                for (Integer pluginNumber : pluginNumbers) {
                    TransformerPluginV2LocalInterface plugin = instantiatedPlugins.get(pluginNumber);
                    plugin.end(globalContext.getTransformerPluginContext(pluginNumber.intValue()));
                }
            } catch (Exception e) {
                String err = "Error ending plugins: " + e.getClass().getName() + ": " + e.getMessage();
                LOGGER.warn("endPlugins() " + err);
            }
        }
    }

    /**
     * Executes the Transformer Synchronously
     * The Typed Content passed is stored in the DEFAULT pipeline variable
     */
    @Override
    public void execute(
            TransformerContext context,
            TypedContent content,
            TransformerCallBack callBack
    ) throws XtentisException {
        if (content != null) {
            context.putInPipeline(DEFAULT_VARIABLE, content);
        }
        execute(context, callBack);
    }


    /**
     * Executes the Transformer and returns only when it is done
     */
    @Override
    public TransformerContext executeUntilDone(
            TransformerContext context
    ) throws XtentisException {
        //Set ready variable
        context.put("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready", Boolean.FALSE);
        //execute the Transformer
        execute(context, new TransformerCallBack() {
            public void contentIsReady(TransformerContext context) throws XtentisException {
            }

            public void done(TransformerContext context) throws XtentisException {
                context.put("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready", Boolean.TRUE);
            }
        });
        while (context.get("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready").equals(Boolean.FALSE)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        context.remove("com.amalto.core.objects.transformers.v2.transformerCtrlV2.ready");
        return context;
    }


    /**
     * Executes the Transformer and returns only when it is done
     * The Typed Content passed is stored in the DEFAULT pipeline variable
     */
    @Override
    public TransformerContext executeUntilDone(
            TransformerContext context,
            TypedContent content
    ) throws XtentisException {
        //Store the content in the default pipeline variable
        context.putInPipeline(DEFAULT_VARIABLE, content);
        return executeUntilDone(context);
    }

    /**
     * Executes a plugin
     */
    protected void executePlugin(
            TransformerGlobalContext globalContext,
            int pluginNumber
    ) throws XtentisException {
        //fetch the Transformer and the instantiated plugins
        TransformerV2POJO transformerPOJO = globalContext.getTransformerPOJO();
        //fetch the Process Step
        TransformerProcessStep processStep = transformerPOJO.getProcessSteps().get(pluginNumber);
        //fetch the Plugin context
        TransformerPluginContext pluginContext = globalContext.getTransformerPluginContext(pluginNumber);
        /*******************************************************************
         * Initialize the Plugin  - if not already done
         *******************************************************************/
        HashMap<Integer, TransformerPluginV2LocalInterface> instantiatedPlugins = globalContext.getInstantiatedPlugins();
        //get the plugin parameters
        String parameters = processStep.getCompiledParameters();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("executePlugin() " + processStep.getDescription() + " -- Initializing plugin " + processStep.getPluginJNDI());
        }
        //Check if plugin is already instantiated and initialized
        TransformerPluginV2LocalInterface plugin = null;
        if ((plugin = instantiatedPlugins.get(new Integer(pluginNumber))) == null) {
            try {
                //fetch and initialize the plugin
                plugin = getPlugin(processStep.getPluginJNDI());
                //set global context to each plugin by aiming
                plugin.setGlobalContext(globalContext);
                //the plugin handle is the plugin number
                plugin.init(
                        globalContext.getTransformerPluginContext(pluginNumber),
                        parameters
                );
            } catch (XtentisException e) {
                throw (e);
            }
            //update the Map of running plugins
            instantiatedPlugins.put(pluginNumber, plugin);
        }
        /*******************************************************************
         * Check the input mappings
         *  Stop this Step if a mandatory Input mapping is missing
         *******************************************************************/
        try {
            //fetch descriptors
            ArrayList<TransformerPluginVariableDescriptor> descriptors = plugin.getInputVariableDescriptors("en");
            //loop over the input variables - map content and determine if all mandatory variables have content
            for (TransformerPluginVariableDescriptor descriptor : descriptors) {
                TypedContent content = getMappedInputVariable(descriptor, processStep, globalContext);
                if (content instanceof TypedContent_Do_Not_Process) {
                    //we stop the plugin processing
                    String msg =
                            "Transformer step '" + processStep.getDescription() + "' of Transformer '" + globalContext.getTransformerV2POJOPK().getUniqueId() + "': " +
                                    "not executed --> input variable '" + descriptor.getVariableName() + "' is marked as stopped";
                    LOGGER.debug("executeNextPlugin() " + msg);
                    return;
                }
                //insert the plugin input variable content in the plugin context
                pluginContext.put(descriptor.getVariableName(), content);
            }
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err =
                    "Transformer step '" + processStep.getDescription() + "' of Transformer '" + globalContext.getTransformerV2POJOPK().getUniqueId() + "':" +
                            "the mapping of the input variables failed " + e.getClass().getName() + ": " + e.getMessage();
            LOGGER.error("executeNextplugin() " + err);
            throw new XtentisException(err, e);
        }
        /*******************************************************************
         * Execute the plugin
         *******************************************************************/
        try {
            //content will be sent through the contentIsReady method below.
            pluginContext.setPluginCallBack(this);
            //check if disabled should not exe this plugin by aiming
            if (!processStep.isDisabled()) {
                //execute the plugin
                plugin.execute(pluginContext);
            } else {
                //sigal content is ready by aiming
                pluginContext.getPluginCallBack().contentIsReady(pluginContext);
            }
        } catch (XtentisException e) {
            throw (e);
        }

        //Done
    }

    private TransformerPluginV2LocalInterface getPlugin(String pluginJNDI) {
        return null;
    }

    /**
     * Returns the content to be mapped to the input of a plugin
     *
     * @param descriptor
     * @param processStep
     * @param context
     * @return the TypedContent to be mapped
     * @throws com.amalto.core.util.XtentisException
     */
    private TypedContent getMappedInputVariable(
            TransformerPluginVariableDescriptor descriptor,
            TransformerProcessStep processStep,
            TransformerContext context
    ) throws XtentisException {

        try {

            String pluginVariable = descriptor.getVariableName();

            //find input in mappings and detemine pipeline variable name
            TransformerVariablesMapping mapping = null;
            for (Iterator<TransformerVariablesMapping> iterator = processStep.getInputMappings().iterator(); iterator.hasNext(); ) {
                TransformerVariablesMapping mpg = iterator.next();
                if (pluginVariable.equals(mpg.getPluginVariable())) {
                    mapping = mpg;
                    break;
                }
            }

            //Mapping not found
            if (mapping == null) {
                //The input variable is mandatory - it is a design time error
                if (descriptor.isMandatory()) {
                    String err =
                            "Transformer step '" + processStep.getDescription() + "' of Transformer '" + context.getTransformerV2POJOPK().getUniqueId() + "':" +
                                    "The input variable '" + descriptor.getVariableName() + "' of plugin '" + processStep.getPluginJNDI() +
                                    "' is mandatory and was not mapped during the Transformer '" + context.getTransformerV2POJOPK().getUniqueId() + "' design.";
                    LOGGER.error("getMappedInputVariable() " + err);
                    throw new XtentisException(err);
                }
                //not mandatory --> return the special USE_DEFAULT Typed Content
                return new TypedContent_Use_Default();
            }

            String pipelineVariable = mapping.getPipelineVariable();
            if (pipelineVariable == null) pipelineVariable = Transformer.DEFAULT_VARIABLE;

            LOGGER.debug("getMappedInputVariable() Mapping pipeline " + pipelineVariable + " ---> " + pluginVariable);

            //Mapping is found --> retrieve the pipeline content;
            TypedContent content = context.getFromPipeline(pipelineVariable);

            //if content is null, the variable was never initialized, do not process it
            if (content == null) {
                return new TypedContent_Do_Not_Process();
            }

            //if content is marked as do not process, well.... do not process
            if (content instanceof TypedContent_Do_Not_Process) {
                return content;
            }

            //check if content-types match
            boolean match = false;
            for (Iterator<Pattern> iter = descriptor.getContentTypesRegex().iterator(); iter.hasNext(); ) {
                Pattern pattern = iter.next();
                if (pattern.matcher(Util.extractTypeAndSubType(content.getContentType())).matches()) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                String err =
                        "Transformer step '" + processStep.getDescription() + "' of Transformer '" + context.getTransformerV2POJOPK().getUniqueId() + "':" +
                                "The input variable '" + descriptor.getVariableName() + "' of plugin '" + processStep.getPluginJNDI() +
                                "' cannot accept the pipeline variable '" + mapping.getPipelineVariable() + "' content-type of '" + content.getContentType() + "'";
                LOGGER.error("getMappedInputVariable() " + err);
                throw new XtentisException(err);
            }

            //check possible values regex
            if ((descriptor.getPossibleValuesRegex() != null) &&
                    (descriptor.getPossibleValuesRegex().size() != 0) &&
                    (!descriptor.getPossibleValuesRegex().contains(".*")) &&
                    (Util.extractTypeAndSubType(content.getContentType()).startsWith("text"))
                    ) {
                String charset = Util.extractCharset(content.getContentType());
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int b;
                while ((b = content.getContentStream().read()) != -1) bos.write(b);
                String text = new String(bos.toByteArray(), charset);
                match = false;
                for (Iterator<Pattern> iter = descriptor.getContentTypesRegex().iterator(); iter.hasNext(); ) {
                    Pattern pattern = iter.next();
                    if (pattern.matcher(text).matches()) {
                        match = true;
                        break;
                    }
                }
                if (!match) {
                    String err =
                            "Transformer step '" + processStep.getDescription() + "' of Transformer '" + context.getTransformerV2POJOPK().getUniqueId() + "':" +
                                    "The input variable '" + descriptor.getVariableName() + "' of plugin '" + processStep.getPluginJNDI() +
                                    "' cannot accept the pipeline variable '" + mapping.getPipelineVariable() + "' value of '" + text + "'";
                    LOGGER.error("getMappedInputVariable() " + err);
                    throw new XtentisException(err);
                }
            }

            return content;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            e.printStackTrace();
            String err =
                    "Transformer step '" + processStep.getDescription() + "' of Transformer '" + context.getTransformerV2POJOPK().getUniqueId() + "':" +
                            "the mapping of the input variables failed " + e.getClass().getName() + ": " + e.getMessage();
            LOGGER.error("executeNextplugin() " + err);
            throw new XtentisException(err);
        }

    }


    /*****************************************************************
     *  TransformerPluginCallBack Implementation
     *****************************************************************/

    /**
     * Implementation of {@link com.amalto.core.objects.transformers.util.TransformerPluginCallBack#contentIsReady(com.amalto.core.objects.transformers.util.TransformerPluginContext)}
     */
    @Override
    public void contentIsReady(TransformerPluginContext pluginContext) throws XtentisException {

        //fetch the process step
        TransformerGlobalContext globalContext = pluginContext.getTransformerGlobalContext();
        TransformerProcessStep processStep = pluginContext.getTransformerPOJO().getProcessSteps().get(pluginContext.getPluginNumber());
//		TransformerPluginV2LocalInterface plugin = globalContext.getInstantiatedPlugins().get(new Integer(pluginContext.getPluginNumber()));

        /*******************************************************************
         * Map the output variables to the pipeline
         *******************************************************************/
        try {

            //fetch output Mappings
            ArrayList<TransformerVariablesMapping> outputMappings = processStep.getOutputMappings();

            //first create all the pipeline variables
            for (Iterator<TransformerVariablesMapping> iter = outputMappings.iterator(); iter.hasNext(); ) {
                TransformerVariablesMapping mapping = iter.next();
                if (mapping.getPipelineVariable() != null) {
                    String pluginvariable = mapping.getPluginVariable();
                    if (pluginvariable != null) {
                        //map the output of the plugin to the pipeline variable
                        TypedContent content = (TypedContent) pluginContext.get(pluginvariable);
                        //check content null
                        if (content != null) {
                            if (content instanceof TypedContent_Drop_Variable) {
                                LOGGER.debug(
                                        "contentIsReady() Dropping pipeline variable: " + mapping.getPipelineVariable()
                                );
                                globalContext.removeFrompipeline(mapping.getPipelineVariable());
                            } else {
                                String s = content.toString();
                                LOGGER.debug(
                                        "contentIsReady() Mapping plugin variable: " + pluginvariable + " -----> pipeline: " + mapping.getPipelineVariable()
                                                + "   Content: " + s.substring(0, Math.min(0, s.length())) + "..."
                                );
                                globalContext.putInPipeline(mapping.getPipelineVariable(), content);
                            }
                        }
                    } else if (mapping.getHardCoding() != null) {
                        //harcode the value into the pipeline
                        if (mapping.getHardCoding() instanceof TypedContent_Drop_Variable) {
                            LOGGER.debug(
                                    "contentIsReady() Hard Dropping pipeline variable: " + mapping.getPipelineVariable()
                            );
                            globalContext.removeFrompipeline(mapping.getPipelineVariable());
                        } else {
                            LOGGER.debug(
                                    "contentIsReady() Hard Coding the result in pipeline variable: " + mapping.getPipelineVariable()
                                            + "   Content: " + mapping.getHardCoding().getContentType().toString().substring(0, 100) + "..."
                            );
                            globalContext.putInPipeline(mapping.getPipelineVariable(), mapping.getHardCoding());
                        }
                    } else {
                        String err = "Output Mapping incorrect for pipeline variable " + mapping.getPipelineVariable() + " in process step " + processStep.getDescription() + ": "
                                + "both the plugin variable name and the hard codings are empty";
                        LOGGER.error(err);
                        throw new XtentisException(err);
                    }
                }
            }


        } catch (XtentisException e) {
            throw (e);
        }


        /*******************************************************************
         * If more plugins --> Execute the next Plugin
         * If not, signal to the main callback that content is ready
         *******************************************************************/
        if (pluginContext.getPluginNumber() + 1 < pluginContext.getTransformerPOJO().getProcessSteps().size()) {
            executePlugin(globalContext, pluginContext.getPluginNumber() + 1);
            return;
        }

        //end of transformer branch, signal that content is ready
        String msg =
                "End of current branch of Transformer '" + pluginContext.getTransformerV2POJOPK().getUniqueId() + "' calling execute callback";
        LOGGER.debug("contentIsReady() " + msg);

        globalContext.getExecuteCallBack().contentIsReady(globalContext);
    }
}