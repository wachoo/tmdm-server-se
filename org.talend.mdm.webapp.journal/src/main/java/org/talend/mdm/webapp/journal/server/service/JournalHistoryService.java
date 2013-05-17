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
package org.talend.mdm.webapp.journal.server.service;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;

import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.TypeMetadata;
import org.talend.mdm.webapp.journal.server.model.ForeignKeyInfoTransformer;
import org.talend.mdm.webapp.journal.shared.JournalParameters;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.history.Action;
import com.amalto.core.history.DocumentHistory;
import com.amalto.core.history.DocumentHistoryFactory;
import com.amalto.core.history.DocumentHistoryNavigator;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.EmptyDocument;
import com.amalto.core.history.ModificationMarker;
import com.amalto.core.history.UniqueIdTransformer;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.webapp.core.util.Util;


/**
 * created by talend2 on 2013-1-31
 * Detailled comment
 *
 */
public class JournalHistoryService {
    
    private DocumentHistory doucmentHistory;
    
    private static final String CURRENT_ACTION = "current"; //$NON-NLS-1$

    private static final String PREVIOUS_ACTION = "before";  //$NON-NLS-1$

    private static final String NEXT_ACTION = "next";  //$NON-NLS-1$    
    
    private JournalHistoryService() {
        doucmentHistory = DocumentHistoryFactory.getInstance().create();      
    }
    
    private static JournalHistoryService service;    
    
    public static synchronized JournalHistoryService getInstance(){    
        if (service == null) {
            service = new JournalHistoryService();
        }             
        return service;           
    }
    
    public String getComparisionTreeString(JournalParameters parameter) throws Exception {
        DocumentHistoryNavigator navigator = doucmentHistory.getHistory(parameter.getDataClusterName(), parameter.getDataModelName(),
                parameter.getConceptName(), parameter.getId(), parameter.getRevisionId());

        MetadataRepository metadataRepository = new MetadataRepository();
        TypeMetadata documentTypeMetadata = metadataRepository.getType(parameter.getConceptName());
        if (documentTypeMetadata == null) {
            try {
                DataModelPOJO dataModel = com.amalto.core.util.Util.getDataModelCtrlLocal().getDataModel(
                        new DataModelPOJOPK(parameter.getDataModelName()));
                if (dataModel == null) {
                    throw new IllegalArgumentException("Data model '" + parameter.getDataModelName() + "' does not exist."); //$NON-NLS-1$ //$NON-NLS-2$
                }

                String schemaString = dataModel.getSchema();
                metadataRepository.load(new ByteArrayInputStream(schemaString.getBytes("UTF-8"))); //$NON-NLS-1$

                documentTypeMetadata = metadataRepository.getType(parameter.getConceptName());
                if (documentTypeMetadata == null) {
                    throw new IllegalArgumentException(
                            "Cannot find type information for type '" + parameter.getDataModelName() + "' in data cluster '" + parameter.getDataClusterName() + "', in data model '" + parameter.getDataModelName() + "'"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                }
            } catch (Exception e) {
                throw new ServletException("Could not initialize type information", e); //$NON-NLS-1$
            }
        }

        navigator.goTo(new Date(parameter.getDate()));
        Action modificationMarkersAction = navigator.currentAction();
        com.amalto.core.history.Document document = EmptyDocument.INSTANCE;
        if (CURRENT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            document = navigator.current();
        } else if (PREVIOUS_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasPrevious()) {
                document = navigator.previous();
            }
        } else if (NEXT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasNext()) {
                document = navigator.next();
            }
        } else {
            throw new ServletException(new IllegalArgumentException("Action '" + parameter.getAction() + " is not supported.")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        List<DocumentTransformer> transformers = Arrays.asList(
                new ForeignKeyInfoTransformer(documentTypeMetadata, parameter.getDataClusterName()), new UniqueIdTransformer(),
                new ModificationMarker(modificationMarkersAction));
        com.amalto.core.history.Document transformedDocument = document;
        for (DocumentTransformer transformer : transformers) {
            transformedDocument = document.transform(transformer);
        }

        return transformedDocument.exportToString();
    }
    
    public boolean restoreRecord(JournalParameters parameter) throws Exception {
        Date historyDate = new Date(parameter.getDate());
        DocumentHistoryNavigator navigator = doucmentHistory.getHistory(parameter.getDataClusterName(), parameter.getDataModelName(),
                parameter.getConceptName(), parameter.getId(), parameter.getRevisionId());
        navigator.goTo(historyDate);
        
        com.amalto.core.history.Document document = EmptyDocument.INSTANCE;
        if (CURRENT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            document = navigator.current();
        } else if (PREVIOUS_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasPrevious()) {
                document = navigator.previous();
            }
        } else if (NEXT_ACTION.equalsIgnoreCase(parameter.getAction())) {
            if (navigator.hasNext()) {
                document = navigator.next();
            }
        } else {
            throw new ServletException(new IllegalArgumentException("Action '" + parameter.getAction() + " is not supported.")); //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        document.restore();
        String xml = Util.createUpdateReport(parameter.getId(), parameter.getConceptName(), UpdateReportPOJO.OPERATION_TYPE_RESTORED, null);
        Util.persistentUpdateReport(xml, true);
        return true;
    }
}
