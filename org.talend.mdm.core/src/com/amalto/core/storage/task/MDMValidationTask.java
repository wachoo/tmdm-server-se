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
import com.amalto.core.query.user.Select;
import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.save.context.DocumentSaver;
import com.amalto.core.save.context.SaverSource;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.enhancement.TypeMappingRepository;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordXmlWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;
import static com.amalto.core.query.user.UserStagingQueryBuilder.status;

public class MDMValidationTask extends MetadataRepositoryTask implements Task {

    private final SaverSource source;

    private final SaverSession.Committer committer;

    private final Storage destinationStorage;

    public MDMValidationTask(Storage storage, Storage destinationStorage, MetadataRepository repository, SaverSource source, SaverSession.Committer committer) {
        super(storage, repository);
        this.source = source;
        this.committer = committer;
        this.destinationStorage = destinationStorage;
    }

    @Override
    public String toString() {
        return "CLUSTERS VALIDATION";
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type)  {
        Closure closure = new MDMValidationTask.MDMValidationClosure(source, committer, destinationStorage);
        Select select = from(type).where(eq(status(), StagingConstants.SUCCESS_MERGE_CLUSTERS)).getSelect();
        return new MultiThreadedTask(type.getName(), storage, select, 2, closure);
    }

    private class MDMValidationClosure implements Closure {

        private final SaverSource source;

        private final SaverSession.Committer committer;

        private final DataRecordXmlWriter writer;

        private final Storage destinationStorage;

        private SaverSession session;

        public MDMValidationClosure(SaverSource source, SaverSession.Committer committer, Storage destinationStorage) {
            this.source = source;
            this.committer = committer;
            writer = new DataRecordXmlWriter();
            this.destinationStorage = destinationStorage;
        }

        public synchronized void begin() {
            session = SaverSession.newSession(source);
            session.begin(destinationStorage.getName(), committer);
            storage.begin();
        }

        public void execute(DataRecord record) {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                writer.write(record, output);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            DocumentSaverContext context = session.getContextFactory().create(destinationStorage.getName(), destinationStorage.getName(), true, new ByteArrayInputStream(output.toByteArray()));
            context.setTaskId(record.getRecordMetadata().getTaskId());
            DocumentSaver saver = context.createSaver();
            Map<String,String> recordProperties = record.getRecordMetadata().getRecordProperties();
            try {
                saver.save(session, context);
                recordProperties.put(TypeMappingRepository.METADATA_STAGING_STATUS, StagingConstants.SUCCESS_VALIDATE);
            } catch (Exception e) {
                recordProperties.put(TypeMappingRepository.METADATA_STAGING_STATUS, StagingConstants.FAIL_VALIDATE_VALIDATION);
                recordProperties.put(TypeMappingRepository.METADATA_STAGING_ERROR, e.getMessage());
            }
            storage.update(record);
        }

        public synchronized void end() {
            session.end(committer);
            storage.commit();
            storage.end();
        }

        public Closure copy() {
            return new MDMValidationClosure(source, committer, destinationStorage);
        }
    }

}


