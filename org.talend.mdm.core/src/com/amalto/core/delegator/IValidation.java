package com.amalto.core.delegator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface IValidation {
	Document validate(Element element, String schema) throws Exception;
}
