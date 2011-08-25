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
package org.talend.mdm.bulkload.client;

/**
 * DOC bchen class global comment. Detailled comment
 */
public class SyncInt {

    private int count = 0;

    public synchronized void add(int i) {
        count += i;
    }

    public synchronized int getCount() {
        return count;
    }
}
