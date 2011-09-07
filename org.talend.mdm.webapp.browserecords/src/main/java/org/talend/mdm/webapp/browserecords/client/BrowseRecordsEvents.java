// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client;

import com.extjs.gxt.ui.client.event.EventType;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class BrowseRecordsEvents {

    public static final EventType Error = new EventType();

    public static final EventType InitFrame = new EventType();

    public static final EventType InitSearchContainer = new EventType();

    public static final EventType GetView = new EventType();

    public static final EventType SearchView = new EventType();

    public static final EventType CreateForeignKeyView = new EventType();

    public static final EventType SelectForeignKeyView = new EventType();

    public static final EventType ViewItem = new EventType();

    public static final EventType ViewForeignKey = new EventType();

    public static final EventType SaveItem = new EventType();

    public static final EventType UpdatePolymorphism = new EventType();

}
