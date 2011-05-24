/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * <p>
 * "Cut" a stream that contains several XML documents into <code>String</code> instances, each
 * of them containing a valid XML fragment.
 * </p>
 * <p>
 * The stream :
 * <br/>
 * <code>
 * &lt;root&gt;&lt;/root&gt;&lt;root&gt;&lt;/root&gt;
 * </code>
 * </p>
 * <p>
 * Will give the following 2 String instances :
 * <br/>
 * <code>
 * &lt;root&gt;&lt;/root&gt;
 * </code>
 * <br/>
 * <code>
 * &lt;root&gt;&lt;/root&gt;
 * </code>
 * </p>
 * <p/>
 * <p>
 * The contract of {@link Enumeration} is fully implemented so <code>hasMoreElements</code> must be called before
 * <code>nextElement()</code>
 * </p>
 */
public class XMLStreamTokenizer implements Enumeration<String> {
    private final ResettableStringWriter stringWriter = new ResettableStringWriter();
    private final InputStream inputStream;
    private String currentNextElement;
    private String rootElementName = null;
    private int previousCharacter;

    public XMLStreamTokenizer(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public boolean hasMoreElements() {
        return moveNext();
    }

    public String nextElement() {
        if (currentNextElement == null) {
            throw new IllegalStateException("Should call hasMoreElements() before nextElement()");
        }

        String retValue = currentNextElement;
        currentNextElement = null;
        return retValue;
    }

    private boolean moveNext() {
        try {
            boolean isFragmentComplete = false;
            int read;
            boolean inElement = false;
            boolean isEndElement = false;
            String currentElementName = "";
            int currentLevel = 0;

            while (!isFragmentComplete && (read = inputStream.read()) > 0) {
                switch (read) {
                    case '<':
                        inElement = true;
                        break;
                    case '/':
                        if (inElement) {
                            isEndElement = true;
                        }
                        break;
                    case '>':
                        if (previousCharacter != '/') {
                            if (isEndElement) {
                                currentLevel--;
                            } else {
                                currentLevel++;
                            }
                        }

                        if (inElement) {
                            if (isEndElement && rootElementName != null
                                    && rootElementName.equals(currentElementName)
                                    && currentLevel == 0) {
                                isFragmentComplete = true;
                            } else if (rootElementName == null) {
                                rootElementName = currentElementName;
                            }
                            currentElementName = "";

                            isEndElement = false;
                            inElement = false;
                        }
                        break;
                }
                if (inElement && read != '<' && read != '/') {
                    currentElementName += (char) read;
                }

                previousCharacter = read;
                stringWriter.append((char) read);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        currentNextElement = stringWriter.toString();
        stringWriter.reset();
        return !currentNextElement.isEmpty();
    }

}
