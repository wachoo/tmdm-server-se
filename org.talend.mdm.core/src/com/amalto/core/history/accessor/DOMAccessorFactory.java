/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history.accessor;

import com.amalto.core.history.MutableDocument;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class DOMAccessorFactory {

    private static final Pattern pattern = Pattern.compile("(\\w+)\\[(\\d+)\\]"); //$NON-NLS-1$

    private DOMAccessorFactory() {
    }

    public static Accessor createAccessor(String xpath, MutableDocument document) {
        DOMAccessor current = new RootAccessor(document);
        if (xpath == null || xpath.isEmpty()) {
            return current;
        }

        StringTokenizer tokenizer = new StringTokenizer(xpath, "/"); //$NON-NLS-1$

        while (tokenizer.hasMoreElements()) {
            String element = (String) tokenizer.nextElement();

            if (element.startsWith("@")) { //$NON-NLS-1$
                current = new AttributeAccessor(current, element.substring(1), document);
            } else if (element.contains("[")) { //$NON-NLS-1$
                Matcher matcher = pattern.matcher(element);
                if (matcher.matches()) {
                    current = new ManyFieldAccessor(current, matcher.group(1), Integer.parseInt(matcher.group(2)) - 1, document);
                } else {
                    throw new RuntimeException("Field name '" + element + "' did not match many field pattern in path '" + xpath + "'.");
                }
            } else {
                current = new UnaryFieldAccessor(current, element, document);
            }
        }

        return current;
    }
}
