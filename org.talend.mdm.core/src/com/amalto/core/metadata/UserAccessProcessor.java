/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.metadata;

import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;

import java.util.Iterator;

class UserAccessProcessor implements XmlSchemaAnnotationProcessor {

    public void process(MetadataRepository repository, ComplexTypeMetadata containingType, XmlSchemaAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            Iterator annotations = annotation.getItems().getIterator();
            while (annotations.hasNext()) {
                XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) annotations.next();
                if (BusinessConcept.APPINFO_X_HIDE.equals(appInfo.getSource())) {
                    state.getHide().add(appInfo.getMarkup().item(0).getNodeValue());
                } else if (BusinessConcept.APPINFO_X_WRITE.equals(appInfo.getSource())) {
                    state.getAllowWrite().add(appInfo.getMarkup().item(0).getNodeValue());
                } else if ("X_Deny_Create".equals(appInfo.getSource())) { //$NON-NLS-1$
                    state.getDenyCreate().add(appInfo.getMarkup().item(0).getNodeValue());
                } else if ("X_Deny_LogicalDelete".equals(appInfo.getSource())) { //$NON-NLS-1$
                    state.getDenyLogicalDelete().add(appInfo.getMarkup().item(0).getNodeValue());
                } else if ("X_Deny_PhysicalDelete".equals(appInfo.getSource())) { //$NON-NLS-1$
                    state.getDenyPhysicalDelete().add(appInfo.getMarkup().item(0).getNodeValue());
                }
            }
        }
    }
}
