// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.save;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractDocumentSaverContext implements DocumentSaverContext {

    private final Map<String, String> autoIncrementFieldMap = new HashMap<String, String>();

    @Override
    public int getPartialUpdateIndex() {
        return -1;
    }

    public Map<String, String> getAutoIncrementFieldMap() {
        return autoIncrementFieldMap;
    }

}
