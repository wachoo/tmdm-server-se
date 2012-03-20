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

package com.amalto.core.save.context;

import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;

public class TimeMeasure implements DocumentSaver {

    private final DocumentSaver next;

    private static long count = 0;

    private static long totalTime = 0;

    TimeMeasure(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        long startTime = System.currentTimeMillis();
        {
            next.save(session, context);
        }
        totalTime += System.currentTimeMillis() - startTime;
        count++;

        if(count % 100 == 0) {
            System.out.println("Average time: " + totalTime / (count - 1));
        }
    }

    public static void reset() {
        count = 0;
        totalTime = 0;
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }
}
