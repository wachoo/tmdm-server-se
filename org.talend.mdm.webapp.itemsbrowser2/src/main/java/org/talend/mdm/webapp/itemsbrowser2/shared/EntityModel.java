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
package org.talend.mdm.webapp.itemsbrowser2.shared;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class EntityModel implements IsSerializable {

    private String conceptName;

    private String[] keys;

    private Map<String, TypeModel> metaDataTypes;//TODO: Change to SortedMap

    /**
     * DOC HSHU EntityModel constructor comment.
     */
    public EntityModel() {
        this.metaDataTypes = new HashMap<String, TypeModel>();
    }

    public String getConceptName() {
        return conceptName;
    }

    public void setConceptName(String conceptName) {
        this.conceptName = conceptName;
    }

    public String[] getKeys() {
        return keys;
    }

    public void setKeys(String[] keys) {
        this.keys = keys;
    }

    public Map<String, TypeModel> getMetaDataTypes() {
        return metaDataTypes;
    }

    public void setMetaDataTypes(Map<String, TypeModel> metaDataTypes) {
        this.metaDataTypes = metaDataTypes;
    }

}
