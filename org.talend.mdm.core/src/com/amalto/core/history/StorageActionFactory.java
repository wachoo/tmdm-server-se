/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.history;

import java.io.ByteArrayInputStream;
import java.util.*;

import com.amalto.core.load.io.ResettableStringWriter;
import com.amalto.core.objects.UpdateReportItemPOJO;
import com.amalto.core.objects.UpdateReportPOJO;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordWriter;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import com.amalto.core.history.action.*;
import com.amalto.core.util.Util;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

/**
 *
 */
public class StorageActionFactory implements ActionFactory {

    private static final Logger logger = Logger.getLogger(StorageActionFactory.class);

    private static final ActionFactoryStrategy createStrategy = new CreateStrategy();

    private static final ActionFactoryStrategy updateStrategy = new UpdateStrategy();

    private static final ActionFactoryStrategy noOpStrategy = new NoOpStrategy();

    private static final ActionFactoryStrategy physicalDeleteStrategy = new PhysicalDeleteStrategy();

    private static final ActionFactoryStrategy logicalDeleteStrategy = new LogicalDeleteStrategy();

    private static final ActionFactoryStrategy logicalRestoredStrategy = new LogicalRestoreStrategy();

    private final Storage storage;

    public StorageActionFactory(Storage storage) {
        this.storage = storage;
    }

    @Override
    public List<Action> createActions(String dataClusterName, String dataModelName, String conceptName, String[] id) {
        MetadataRepository repository = storage.getMetadataRepository();
        ComplexTypeMetadata update = repository.getComplexType("Update"); //$NON-NLS-1$
        if (update == null) {
            throw new IllegalArgumentException("Storage does not manage update reports.");
        }
        UserQueryBuilder qb = from(update);
        qb.where(eq(update.getField("DataModel"), dataModelName)); //$NON-NLS-1$
        qb.where(eq(update.getField("DataCluster"), dataClusterName)); //$NON-NLS-1$
        qb.where(eq(update.getField("Concept"), conceptName)); //$NON-NLS-1$
        qb.where(eq(update.getField("Key"), Util.joinStrings(id, "."))); //$NON-NLS-1$ //$NON-NLS-2$
        // Get the update reports for the MDM document we're looking for
        List<String> updateReportsContent;
        try {
            storage.begin();
            StorageResults updateReports = storage.fetch(qb.getSelect());
            DataRecordWriter writer = new DataRecordXmlWriter();
            ResettableStringWriter output = new ResettableStringWriter();
            updateReportsContent = new ArrayList<String>(updateReports.getCount());
            for (DataRecord updateReport : updateReports) {
                writer.write(updateReport, output);
                updateReportsContent.add(output.reset());
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Number of update reports read from database: " + updateReportsContent.size()); //$NON-NLS-1$
            }
            storage.commit();
        } catch (Exception e) {
            storage.rollback();
            throw new RuntimeException(e);
        }
        // Create the UpdateReportPOJOs to ease action creation
        List<UpdateReportPOJO> updateReports = new ArrayList<UpdateReportPOJO>();
        try {
            UpdateReportPOJOParser parser = new UpdateReportPOJOParser();
            for (String updateReportContent : updateReportsContent) {
                updateReports.add(parser.parse(new ByteArrayInputStream(updateReportContent.getBytes("UTF-8")))); //$NON-NLS-1$
            }
            if (logger.isDebugEnabled()) {
                logger.debug("Number of update reports POJO created: " + updateReports.size()); //$NON-NLS-1$
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Generate actions
        List<Action> actions = new ArrayList<Action>();
        actions.add(NoOpAction.instance()); // Adds a no op action (see bug #23355)
        for (UpdateReportPOJO updateReport : updateReports) {
            String operationType = updateReport.getOperationType();
            ActionFactoryStrategy actionFactoryStrategy = getActionFactoryStrategy(operationType);
            actions.add(actionFactoryStrategy.createAction(updateReport));
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Number of actions: " + actions.size()); //$NON-NLS-1$
        }
        return actions;
    }

    private ActionFactoryStrategy getActionFactoryStrategy(String operationType) {
        if (UpdateReportPOJO.OPERATION_TYPE_CREATE.equals(operationType)) {
            return createStrategy;
        } else if (UpdateReportPOJO.OPERATION_TYPE_UPDATE.equals(operationType)) {
            return updateStrategy;
        } else if (UpdateReportPOJO.OPERATION_TYPE_PHYSICAL_DELETE.equals(operationType)) {
            return physicalDeleteStrategy;
        } else if (UpdateReportPOJO.OPERATION_TYPE_LOGICAL_DELETE.equals(operationType)) {
            return logicalDeleteStrategy;
        } else if (UpdateReportPOJO.OPERATION_TYPE_RESTORED.equals(operationType)) {
            return logicalRestoredStrategy;
        } else {
            if (!UpdateReportPOJO.OPERATION_TYPE_ACTION.equals(operationType)) {
                logger.warn("Operation type '" + operationType + "' is not supported. Ignore action for document history."); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return noOpStrategy;
        }
    }

    interface ActionFactoryStrategy {

        Action createAction(UpdateReportPOJO updateReport);
    }

    private static class NoOpStrategy implements ActionFactoryStrategy {

        @Override
        public Action createAction(UpdateReportPOJO updateReport) {
            return NoOpAction.instance();
        }
    }

    private static class CreateStrategy implements ActionFactoryStrategy {

        @Override
        public Action createAction(UpdateReportPOJO updateReport) {
            ComplexTypeMetadata createdType = null; // TODO It's not mandatory to use this (may be implemented later)
            return new CreateAction(new Date(updateReport.getTimeInMillis()), updateReport.getSource(),
                    updateReport.getUserName(), createdType);
        }
    }

    private static class UpdateStrategy implements ActionFactoryStrategy {

        @Override
        public Action createAction(UpdateReportPOJO updateReport) {
            Date date = new Date(updateReport.getTimeInMillis());
            String source = updateReport.getSource();
            String userName = updateReport.getUserName();
            Map<String, UpdateReportItemPOJO> updatedFields = updateReport.getUpdateReportItemsMap();

            Set<Map.Entry<String, UpdateReportItemPOJO>> entries = updatedFields.entrySet();
            List<Action> actions = new ArrayList<Action>();
            for (Map.Entry<String, UpdateReportItemPOJO> entry : entries) {
                UpdateReportItemPOJO updatedField = entry.getValue();
                String path = updatedField.getPath();
                String oldValue = updatedField.getOldValue();
                String newValue = updatedField.getNewValue();

                FieldMetadata field = null; // TODO It's not mandatory to use this (may be implemented later)
                Action updateAction = new FieldUpdateAction(date, source, userName, path, oldValue, newValue, field);
                actions.add(updateAction);
            }

            return new CompositeAction(date, source, userName, actions);
        }

    }

    private static class PhysicalDeleteStrategy implements ActionFactoryStrategy {

        @Override
        public Action createAction(UpdateReportPOJO updateReport) {
            ComplexTypeMetadata deletedType = null; // TODO It's not mandatory to use this (may be implemented later)
            return new PhysicalDeleteAction(new Date(updateReport.getTimeInMillis()), updateReport.getSource(),
                    updateReport.getUserName(), deletedType);
        }
    }

    private static class LogicalDeleteStrategy implements ActionFactoryStrategy {

        @Override
        public Action createAction(UpdateReportPOJO updateReport) {
            ComplexTypeMetadata deletedType = null; // TODO It's not mandatory to use this (may be implemented later)
            return new LogicalDeleteAction(new Date(updateReport.getTimeInMillis()), updateReport.getSource(),
                    updateReport.getUserName(), deletedType);
        }
    }

    private static class LogicalRestoreStrategy implements ActionFactoryStrategy {

        @Override
        public Action createAction(UpdateReportPOJO updateReport) {
            ComplexTypeMetadata deletedType = null; // TODO It's not mandatory to use this (may be implemented later)
            return new LogicalRestoreAction(new Date(updateReport.getTimeInMillis()), updateReport.getSource(),
                    updateReport.getUserName(), deletedType);
        }
    }

}
