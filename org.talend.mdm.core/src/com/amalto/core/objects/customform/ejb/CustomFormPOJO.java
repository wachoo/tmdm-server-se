// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.objects.customform.ejb;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;

/**
 * DOC achen  class global comment. Detailled comment
 */
public class CustomFormPOJO extends ObjectPOJO {

    private String datamodel;

    private String entity;

    private String name;

    private String xml;

    private String role;

    public CustomFormPOJO() {
        super();
    }

    public CustomFormPOJO(String datamodel, String entity, String xml) {
        this.datamodel = datamodel;
        this.entity = entity;
        this.xml = xml.replaceFirst("<\\?xml.*\\?>", ""); //$NON-NLS-1$//$NON-NLS-2$

    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDatamodel() {
        return datamodel;
    }

    public void setDatamodel(String datamodel) {
        this.datamodel = datamodel;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }


    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml.replaceFirst("<\\?xml.*\\?>", ""); //$NON-NLS-1$//$NON-NLS-2$
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.amalto.core.ejb.ObjectPOJO#getPK()
     */
    @Override
    public ObjectPOJOPK getPK() {
        return new CustomFormPOJOPK(datamodel, entity, name);
    }
}
