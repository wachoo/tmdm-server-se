package com.amalto.core.objects.transformers.util;

import com.amalto.core.util.XtentisException;


public interface TransformerCallBack {
	
	public void contentIsReady(TransformerContext context) throws XtentisException;
	
	public void done(TransformerContext context) throws XtentisException;
	
}
