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
package com.amalto.core.util;

import org.apache.commons.lang.StringUtils;
import com.amalto.core.objects.configurationinfo.ejb.ConfigurationInfoPOJO;
import com.amalto.core.objects.customform.ejb.CustomFormPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;

/**
 * created by talend2 on 2013-8-27
 * Detailled comment
 *
 */
public class DigestHelper {
    
    private String TYPE_DATACLUSTER = "MDM.DataCluster"; //$NON-NLS-1$
    
    private String TYPE_DATAMODEL = "MDM.DataModel"; //$NON-NLS-1$
    
    private String TYPE_VIEW = "MDM.View"; //$NON-NLS-1$
    
    private String TYPE_ROLE = "MDM.Role"; //$NON-NLS-1$
    
    private String TYPE_MENU = "MDM.Menu"; //$NON-NLS-1$
    
    private String TYPE_STOREPROCEDURE = "MDM.StoredProcedure"; //$NON-NLS-1$
    
    private String TYPE_UNIVERSE = "MDM.Universe"; //$NON-NLS-1$
    
    private String TYPE_SYNCHRONIZATIONPLAN = "MDM.SynchronizationPlan"; //$NON-NLS-1$
    
    private String TYPE_WORKFLOW = "MDM.Workflow"; //$NON-NLS-1$
    
    private String TYPE_TRANSFORMERV2 = "MDM.TransformerV2"; //$NON-NLS-1$
    
    private String TYPE_ROUTINGRULE = "MDM.RoutingRule"; //$NON-NLS-1$
    
    private String TYPE_JOBMODEL = "MDM.JobModel"; //$NON-NLS-1$
    
    private String TYPE_SERVICECONFIGURATION = "MDM.ServiceConfiguration"; //$NON-NLS-1$
    
    private String TYPE_RESOURCE = "MDM.Resource"; //$NON-NLS-1$
    
    private String TYPE_CUSTOM_FORM = "MDM.CustomForm"; //$NON-NLS-1$
    
    private static DigestHelper instance;
    
    public static synchronized DigestHelper getInstance() {
        if (instance == null) {
            instance = new DigestHelper();
        }

        return instance;
    }
    
    public String getTypeName(String type) {
        String name = null;
        if (TYPE_DATACLUSTER.equals(type)) {
            name = DataClusterPOJO.class.getName();
        } else if (TYPE_DATAMODEL.equals(type)) {
            name = DataModelPOJO.class.getName();
        } else if (TYPE_VIEW.equals(type)) {
            name = ViewPOJO.class.getName();
        } else if (TYPE_ROLE.equals(type)) {
            name = RolePOJO.class.getName();
        } else if (TYPE_MENU.equals(type)) {
            name = MenuPOJO.class.getName();
        } else if (TYPE_STOREPROCEDURE.equals(type)) {
            name = StoredProcedurePOJO.class.getName();
        } else if (TYPE_UNIVERSE.equals(type)) {
            name = UniversePOJO.class.getName();
        } else if (TYPE_SYNCHRONIZATIONPLAN.equals(type)) {
            name = SynchronizationPlanPOJO.class.getName();
        } else if (TYPE_WORKFLOW.equals(type)) {
            // don't support now
        } else if (TYPE_TRANSFORMERV2.equals(type)) {
            name = TransformerV2POJO.class.getName();
        } else if (TYPE_ROUTINGRULE.equals(type)) {
            name = RoutingRulePOJO.class.getName();
        } else if (TYPE_JOBMODEL.equals(type)) {
            //name = BackgroundJobPOJO.class.getName();
            //don't suppport now
        } else if (TYPE_SERVICECONFIGURATION.equals(type)) {
            name = ConfigurationInfoPOJO.class.getName();
        } else if (TYPE_RESOURCE.equals(type)) {
            // don't support now
        } else if (TYPE_CUSTOM_FORM.equals(type)) {
            name = CustomFormPOJO.class.getName();
        } else if (TYPE_MENU.equals(type)) {
            name = DataClusterPOJO.class.getName();
        }
        return name != null ? StringUtils.substringAfterLast(name, ".") : null;  //$NON-NLS-1$
    }

}
