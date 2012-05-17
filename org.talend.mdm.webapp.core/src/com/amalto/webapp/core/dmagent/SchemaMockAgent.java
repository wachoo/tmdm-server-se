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
package com.amalto.webapp.core.dmagent;

import java.util.LinkedHashMap;
import java.util.List;

import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelBean;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.commmon.util.datamodel.management.ReusableType;


/**
 * DOC HSHU class global comment. Detailled comment
 */
public class SchemaMockAgent extends SchemaAbstractWebAgent {


    private String dataModelSchema;

    private LinkedHashMap<DataModelID, DataModelBean> map;

    private DataModelID dataModelID;
    /**
     * DOC Administrator SchemaMockAgent constructor comment.
     */
    public SchemaMockAgent(String dataModelSchema, DataModelID dataModelID) {
        this.dataModelSchema = dataModelSchema;
        this.map = new LinkedHashMap<DataModelID, DataModelBean>();
        this.dataModelID = dataModelID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#existInPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected boolean existInPool(DataModelID dataModelID) {
        DataModelBean dataModelBean = map.get(dataModelID);
        return !(dataModelBean == null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#removeFromPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected void removeFromPool(DataModelID dataModelID) {
        map.remove(dataModelID);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#addToPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID, org.talend.mdm.commmon.util.datamodel.management.DataModelBean)
     */
    @Override
    protected void addToPool(DataModelID dataModelID, DataModelBean dataModelBean) {
        map.put(dataModelID, dataModelBean);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#getFromPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected DataModelBean getFromPool(DataModelID dataModelID) throws Exception {

        if(map.get(dataModelID)!=null)
            return map.get(dataModelID);
        
        DataModelBean dmBean = instantiateDataModelBean(dataModelSchema);
        map.put(dataModelID, dmBean);

        return dmBean;

    }

    public List<ReusableType> getMySubtypes(String parentTypeName) throws Exception {

        return getMySubtypes(parentTypeName, false, getFromPool(dataModelID));

    }

    public List<ReusableType> getMySubtypes(String parentTypeName, boolean deep) throws Exception {

        return getMySubtypes(parentTypeName, deep, getFromPool(dataModelID));

    }

    public ReusableType getReusableType(String typeName) throws Exception {

        return getReusableType(typeName, dataModelID);

    }

    public BusinessConcept getBusinessConcept(String conceptName) throws Exception {

        return getBusinessConcept(conceptName, dataModelID);

    }

}
