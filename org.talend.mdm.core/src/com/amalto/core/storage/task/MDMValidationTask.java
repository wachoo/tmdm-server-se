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

package com.amalto.core.storage.task;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.metadata.SimpleTypeFieldMetadata;
import com.amalto.core.query.user.Select;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.DataRecordXmlWriter.OverrideValue;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import java.io.*;
import java.util.Map;

import static com.amalto.core.query.user.UserQueryBuilder.*;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class MDMValidationTask extends MetadataRepositoryTask {

    private static final boolean GENERATE_UPDATE_REPORT;

    private static final int CONSUMER_POOL_SIZE;

    private static final Logger LOGGER = Logger.getLogger(MDMValidationTask.class);

    private final SaverSource source;

    private final SaverSession.Committer committer;

    private final Storage destinationStorage;

    private int recordsCount;

    static {
        // staging.validation.updatereport tells whether validation should generate update reports
        String value = MDMConfiguration.getConfiguration().getProperty("staging.validation.updatereport"); //$NON-NLS-1$
        GENERATE_UPDATE_REPORT = value == null ? true : Boolean.valueOf(value);
        // staging.validation.pool tells how many threads do staging validation
        value = MDMConfiguration.getConfiguration().getProperty("staging.validation.pool"); //$NON-NLS-1$
        CONSUMER_POOL_SIZE = value == null ? 2 : Integer.valueOf(value);
    }

    public MDMValidationTask(Storage storage, Storage destinationStorage, MetadataRepository repository, SaverSource source, SaverSession.Committer committer, ClosureExecutionStats stats) {
        super(storage, repository, stats);
        this.source = source;
        this.committer = committer;
        this.destinationStorage = destinationStorage;
    }

    @Override
    public String toString() {
        return "CLUSTERS VALIDATION"; //$NON-NLS-1$
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Closure closure = new MDMValidationTask.MDMValidationClosure(source, committer, destinationStorage);
        Select select = from(type).where(
                or(eq(status(), StagingConstants.SUCCESS_MERGED_RECORD),
                        or(eq(status(), StagingConstants.NEW),
                                or(isNull(status()),
                                        or(eq(status(), StagingConstants.FAIL_VALIDATE_CONSTRAINTS),
                                                eq(status(), StagingConstants.FAIL_VALIDATE_VALIDATION)))))).getSelect();
        StorageResults records = storage.fetch(select);
        try {
            recordsCount += records.getCount();
        } finally {
            records.close();
        }
        return new MultiThreadedTask(type.getName(), storage, select, CONSUMER_POOL_SIZE, closure, stats);
    }

    @Override
    public int getRecordCount() {
        return recordsCount;
    }

    private class MDMValidationClosure implements Closure {

        private final SaverSource source;

        private final SaverSession.Committer committer;

        private final DataRecordXmlWriter writer;

        private final Storage destinationStorage;

        private SaverSession session;

        private int commitCount;

        public MDMValidationClosure(SaverSource source, SaverSession.Committer committer, Storage destinationStorage) {
            this.source = source;
            this.committer = committer;
            writer = new DataRecordXmlWriter(new OverrideValue() {

                @Override
                public Object overrideValue(DataRecord record, SimpleTypeFieldMetadata simpleField, Object originalValue) {
                    if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(simpleField.getType().getName())
                            || EUUIDCustomType.UUID.getName().equalsIgnoreCase(simpleField.getType().getName())) {
                        return StringUtils.EMPTY;
                    }
                    return originalValue;
                }
            });
            this.destinationStorage = destinationStorage;
        }

        public synchronized void begin() {
            session = SaverSession.newSession(source);
            session.begin(destinationStorage.getName(), committer);
            storage.begin();
        }

        public void execute(DataRecord stagingRecord, ClosureExecutionStats stats) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                writer.write(stagingRecord, output);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DocumentSaverContext context = session.getContextFactory().create(destinationStorage.getName(),
                    destinationStorage.getName(),
                    "Staging", //$NON-NLS-1$
                    new ByteArrayInputStream(output.toByteArray()),
                    true,
                    true,
                    GENERATE_UPDATE_REPORT,
                    false,
                    false);
            context.setTaskId(stagingRecord.getRecordMetadata().getTaskId());
            DocumentSaver saver = context.createSaver();
            Map<String, String> recordProperties = stagingRecord.getRecordMetadata().getRecordProperties();
            try {
                saver.save(session, context);
                commitCount++;
                if (commitCount % 1000 == 0) {
                    end(stats);
                    begin();
                }
                recordProperties.put(Storage.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_VALIDATE);
                recordProperties.put(Storage.METADATA_STAGING_ERROR, StringUtils.EMPTY);
                storage.update(stagingRecord);
                stats.reportSuccess();
            } catch (Exception e) {
                recordProperties.put(Storage.METADATA_STAGING_STATUS, StagingConstants.FAIL_VALIDATE_VALIDATION);
                StringWriter exceptionMessages = new StringWriter();
                Throwable current = e;
                while (current != null) {
                    exceptionMessages.append(current.getMessage());
                    current = current.getCause();
                    if (current != null) {
                        exceptionMessages.append('\n');
                    }
                }
                recordProperties.put(Storage.METADATA_STAGING_ERROR, exceptionMessages.toString());
                storage.update(stagingRecord);
                stats.reportError();
            }
        }

        @Override
        public void cancel() {
        }

        public synchronized void end(ClosureExecutionStats stats) {
            try {
                session.end(committer);
            } catch (Exception e) {
                // This is unexpected (session should only contain records that won't fail commit).
                LOGGER.error("Could not commit changes.", e);
                session.abort(committer);
            }
            storage.commit();
            storage.end();
        }

        public Closure copy() {
            return new MDMValidationClosure(source, committer, destinationStorage);
        }
    }

}


