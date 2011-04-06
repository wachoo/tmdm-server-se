// ============================================================================
//
// Copyright (c) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.load.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 * An input stream that is designed to wrap XML documents without a unique XML root.
 * </p>
 * <p>
 * On {@link java.io.InputStream#close()}, this implementation also closes the input stream to the XML documents.
 * </p>
 */
public class XMLRootInputStream extends InputStream {
    private final InputStream wrapped;
    private final char[] startXmlElement;
    private final char[] endXmlElement;
    private int startXmlElementIndex = 0;
    private int endXmlElementIndex = 0;

    /**
     * @param wrapped        The input stream to the XML documents to be wrapped in a unique XML root element.
     * @param xmlElementName The name of the unique XML root element to be added.
     */
    public XMLRootInputStream(InputStream wrapped, String xmlElementName) {
        this.wrapped = wrapped;
        startXmlElement = ('<' + xmlElementName + '>').toCharArray();
        endXmlElement = ("</" + xmlElementName + '>').toCharArray();
    }

    @Override
    public int read() throws IOException {
        if (startXmlElementIndex != startXmlElement.length) {
            return startXmlElement[startXmlElementIndex++];
        }

        int wrappedRead = wrapped.read();
        if (wrappedRead >= 0) {
            return wrappedRead;
        } else if (endXmlElementIndex != endXmlElement.length) {
            return endXmlElement[endXmlElementIndex++];
        } else {
            return -1;
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        wrapped.close();
    }
}
