/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history;

/**
 *
 */
public class DocumentHistoryFactory {

    public static final String IMPLEMENTATION_CLASS_NAME = "com.amalto.core.history.DocumentHistoryImpl";

    private static final DocumentHistoryFactory INSTANCE = new DocumentHistoryFactory();

    private DocumentHistoryFactory() {
    }

    public static DocumentHistoryFactory getInstance() {
        return INSTANCE;
    }

    public DocumentHistory create() {
        try {
            Object documentHistoryObject = Class.forName(IMPLEMENTATION_CLASS_NAME).newInstance();
            return (DocumentHistory) documentHistoryObject;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
