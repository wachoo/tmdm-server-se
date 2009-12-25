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

public class TransformerV2CtrlBeanDefaultDelegator implements
		ITransformerV2CtrlBeanDelegator {

	public void contentIsReady(TransformerPluginContext pluginContext)
			throws XtentisException {
		throw new XtentisException("Not Support!");

	}

	public void ejbTimeout(Timer timer) {
		// TODO Auto-generated method stub

	}

	public void execute(UniversePOJO universe, TransformerContext context,
			TransformerCallBack callBack) throws XtentisException {
		throw new XtentisException("Not Support!");

	}

	public void execute(TransformerContext context, TransformerCallBack callBack)
			throws XtentisException {
		throw new XtentisException("Not Support!");

	}

	public void execute(TransformerContext context, TypedContent content,
			TransformerCallBack callBack) throws XtentisException {
		throw new XtentisException("Not Support!");

	}

	public BackgroundJobPOJOPK executeAsJob(TransformerContext context,
			TransformerCallBack callBack) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TransformerContext executeUntilDone(TransformerContext context)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TransformerContext executeUntilDone(TransformerContext context,
			TypedContent content) throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TransformerV2POJO existsTransformer(TransformerV2POJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TransformerContext extractThroughTransformer(
			TransformerV2POJOPK transformerV2POJOPK, ItemPOJOPK itemPOJOPK)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TransformerV2POJO getTransformer(TransformerV2POJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public Collection<TransformerV2POJOPK> getTransformerPKs(String regex)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TransformerV2POJOPK putTransformer(TransformerV2POJO transformer)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public TransformerV2POJOPK removeTransformer(TransformerV2POJOPK pk)
			throws XtentisException {
		throw new XtentisException("Not Support!");
	}

	public void setSessionContext(SessionContext ctx) {
		// TODO Auto-generated method stub

	}

}
