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

package com.amalto.core.storage.task;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
class ClosureExecutionStats {

    private final AtomicInteger errorCount = new AtomicInteger();

    private final AtomicInteger successCount = new AtomicInteger();

    void reportError() {
        errorCount.incrementAndGet();
    }

    void reportSuccess() {
        successCount.incrementAndGet();
    }

    int getErrorCount() {
        return errorCount.get();
    }

    int getSuccessCount() {
        return successCount.get();
    }
}
