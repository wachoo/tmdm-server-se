// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.commons.core.datamodel.synchronization;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

public class DataModelChangeNotifier {

    private final List<DMUpdateEvent> messageList = new LinkedList<DMUpdateEvent>();

    public void addUpdateMessage(DMUpdateEvent dmUpdateEvent) {
        synchronized (messageList) {
            messageList.add(dmUpdateEvent);
        }
    }

    public void sendMessages() {
        throw new NotImplementedException();
    }
}
