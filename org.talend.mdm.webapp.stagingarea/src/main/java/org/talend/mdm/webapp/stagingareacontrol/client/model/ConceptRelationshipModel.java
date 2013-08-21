// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareacontrol.client.model;

import java.io.Serializable;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ConceptRelationshipModel implements Serializable, IsSerializable {

    String[] concepts;

    Map<String, String[]> relationShipMap;

    public ConceptRelationshipModel() {

    }

    public ConceptRelationshipModel(String[] concepts, Map<String, String[]> relationShipMap) {
        this.concepts = concepts;
        this.relationShipMap = relationShipMap;
    }

    public String[] getConcepts() {
        return this.concepts;
    }

    public void setConcepts(String[] concepts) {
        this.concepts = concepts;
    }

    public Map<String, String[]> getRelationShipMap() {
        return this.relationShipMap;
    }

    public void setRelationShipMap(Map<String, String[]> relationShipMap) {
        this.relationShipMap = relationShipMap;
    }

}
