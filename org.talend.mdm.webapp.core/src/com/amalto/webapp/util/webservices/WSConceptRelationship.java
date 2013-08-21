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
package com.amalto.webapp.util.webservices;

import java.util.Map;

public class WSConceptRelationship {

    String[] concepts;

    Map<String, String[]> relationShipMap;

    public WSConceptRelationship() {

    }

    public WSConceptRelationship(String[] concepts, Map<String, String[]> relationShipMap) {
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
