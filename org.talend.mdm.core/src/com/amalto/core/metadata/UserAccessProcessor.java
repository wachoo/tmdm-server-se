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
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.w3c.dom.Element;

import java.util.Iterator;

class UserAccessProcessor implements XmlSchemaAnnotationProcessor {

    public void process(MetadataRepository repository, ComplexTypeMetadata containingType, XmlSchemaAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            Iterator annotations = annotation.getItems().getIterator();
            while (annotations.hasNext()) {
                Object next = annotations.next();
                if (next instanceof XmlSchemaAppInfo) {
                    XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) next;
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

    @Override
    public void process(MetadataRepository repository, ComplexTypeMetadata type, XSDAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            EList<Element> appInfoElements = annotation.getApplicationInformation();
            for (Element appInfo : appInfoElements) {
                if (BusinessConcept.APPINFO_X_HIDE.equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    state.getHide().add(appInfo.getTextContent());
                } else if (BusinessConcept.APPINFO_X_WRITE.equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    state.getAllowWrite().add(appInfo.getTextContent());
                } else if ("X_Deny_Create".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getDenyCreate().add(appInfo.getTextContent());
                } else if ("X_Deny_LogicalDelete".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getDenyLogicalDelete().add(appInfo.getTextContent());
                } else if ("X_Deny_PhysicalDelete".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    state.getDenyPhysicalDelete().add(appInfo.getTextContent());
                }
            }
        }
    }
}
