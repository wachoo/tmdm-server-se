package com.amalto.core.delegator.enterprise;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.core.delegator.IBeanDelegator;
import com.amalto.core.delegator.IValidation;
import com.amalto.core.enterpriseutil.EnterpriseUtil;

public class EnterpriseValidationDelegator implements IValidation,IBeanDelegator {

	public Document validate(Element element, String schema) throws Exception {
		return EnterpriseUtil.validate(element, schema);
	}

}
