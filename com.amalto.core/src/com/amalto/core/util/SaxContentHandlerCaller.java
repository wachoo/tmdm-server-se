/*
 */
package com.amalto.core.util;

import java.io.Writer;

/**
 * @author bgrieder
 */
public interface SaxContentHandlerCaller {
	
	/**
	 * Called by the SAX content handler when it has finished processing a full item
	 *
	 */
	public void processSaxItem(SaxContentHandler sch, Writer sw, Object[] parameters) throws XtentisException;

}
