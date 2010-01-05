package com.amalto.core.delegator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.core.util.Util;

public class DefaultValidationDelegator implements IValidation,IBeanDelegator {

	public Document validation(Element element, String schema) throws Exception {
		return Util.defaultValidate(element, schema);
	}
}
