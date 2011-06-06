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

package com.amalto.core.history;

import java.util.Date;

/**
 *
 */
public interface DocumentHistoryNavigator {
    Document current();

    Document next();

    boolean hasNext();

    Document previous();

    Document previous(Date date);

    boolean hasPrevious();

    Document last();

    int size();

    Action currentAction();
}
