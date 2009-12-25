package com.amalto.core.delegator;

import java.util.Collection;

import javax.ejb.SessionContext;
import javax.ejb.Timer;

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

public interface ITransformerV2CtrlBeanDelegator extends BeanDelegator {
	public TransformerV2POJOPK putTransformer(TransformerV2POJO transformer) throws XtentisException;
	public TransformerV2POJO getTransformer(TransformerV2POJOPK pk) throws XtentisException;
	public TransformerV2POJO existsTransformer(TransformerV2POJOPK pk)    throws XtentisException;
	public TransformerV2POJOPK removeTransformer(TransformerV2POJOPK pk) 
    throws XtentisException;
	public Collection<TransformerV2POJOPK> getTransformerPKs(String regex) throws XtentisException;
	public TransformerContext extractThroughTransformer(
			TransformerV2POJOPK transformerV2POJOPK,
			ItemPOJOPK itemPOJOPK
		) throws XtentisException;
	public BackgroundJobPOJOPK executeAsJob(
    		TransformerContext context,
    		TransformerCallBack callBack
		)throws XtentisException;
	public void execute(
    		UniversePOJO universe,
    		TransformerContext context,
    		TransformerCallBack callBack
		)throws XtentisException;
	public void execute(
    		TransformerContext context,
    		TransformerCallBack callBack
		)throws XtentisException;
	public void execute(
    		TransformerContext context,
    		TypedContent content,
    		TransformerCallBack callBack
		)throws XtentisException;
	public TransformerContext executeUntilDone(
    		TransformerContext context
		)throws XtentisException;
	public TransformerContext executeUntilDone(
    		TransformerContext context,
    		TypedContent content
		)throws XtentisException;
	public void contentIsReady(TransformerPluginContext pluginContext) throws XtentisException;
	public void setSessionContext(SessionContext ctx);
	public void ejbTimeout(Timer timer);
}
