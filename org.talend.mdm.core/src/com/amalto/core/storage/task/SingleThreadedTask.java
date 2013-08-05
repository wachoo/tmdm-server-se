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

package com.amalto.core.storage.task;

import com.amalto.core.query.user.Expression;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.transaction.Transaction;

/**
 *
 */
class SingleThreadedTask extends MultiThreadedTask {
    public SingleThreadedTask(String name,
                              Storage storage,
                              Expression expression,
                              Closure closure,
                              ClosureExecutionStats stats) {
        super(name, storage, expression, 1, closure, stats);
    }
}
