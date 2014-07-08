package com.amalto.core.util;

import java.io.Serializable;

/**
 * @author bgrieder
 *
 */
public class XSDKey implements Serializable {

    private final String selector;

	private final String[] fields;

    private final String[] fieldTypes;

    /**
	 * @param selector
	 * @param fields
     * @param fieldTypes
	 */
	public XSDKey(String selector, String[] fields, String[] fieldTypes) {
		this.selector = selector;
		this.fields = fields;
        this.fieldTypes = fieldTypes;
    }

	public String[] getFields() {
		return fields;
	}

    public String getSelector() {
		return selector;
	}

    public String[] getFieldTypes() {
        return fieldTypes;
    }
}
