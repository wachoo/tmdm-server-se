package com.amalto.core.server.api;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.XtentisException;

import java.util.Collection;

/**
 *
 */
public interface Transformer {
    String DEFAULT_VARIABLE = "_DEFAULT_";

    TransformerV2POJOPK putTransformer(TransformerV2POJO transformer) throws XtentisException;

    TransformerV2POJO getTransformer(TransformerV2POJOPK pk) throws XtentisException;

    TransformerV2POJO existsTransformer(TransformerV2POJOPK pk)    throws XtentisException;

    TransformerV2POJOPK removeTransformer(TransformerV2POJOPK pk)
    throws XtentisException;

    Collection<TransformerV2POJOPK> getTransformerPKs(String regex) throws XtentisException;

    TransformerContext extractThroughTransformer(
            TransformerV2POJOPK transformerV2POJOPK,
            ItemPOJOPK itemPOJOPK
    ) throws XtentisException;

    BackgroundJobPOJOPK executeAsJob(
            TransformerContext context,
            TransformerCallBack callBack
    )throws XtentisException;

    void execute(
            UniversePOJO universe,
            TransformerContext context,
            TransformerCallBack callBack
    )throws XtentisException;

    void execute(
            TransformerContext context,
            TransformerCallBack callBack
    )throws XtentisException;

    void execute(
            TransformerContext context,
            TypedContent content,
            TransformerCallBack callBack
    )throws XtentisException;

    TransformerContext executeUntilDone(
            TransformerContext context
    )throws XtentisException;

    TransformerContext executeUntilDone(
            TransformerContext context,
            TypedContent content
    )throws XtentisException;

    void contentIsReady(TransformerPluginContext pluginContext) throws XtentisException;
}
