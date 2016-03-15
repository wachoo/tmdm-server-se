package com.amalto.core.history;

import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.context.StorageDocument;
import com.amalto.core.server.ServerContext;
import com.amalto.core.server.StorageAdmin;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.record.DataRecord;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import java.util.Iterator;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

public class StorageDocumentFactory implements DocumentFactory {
    @Override
    public MutableDocument create(String dataClusterName, String dataModelName, String conceptName, String[] id) {
        // Get type instances for selected storage
        StorageAdmin storageAdmin = ServerContext.INSTANCE.get().getStorageAdmin();
        Storage dataStorage = storageAdmin.get(dataClusterName, StorageType.MASTER);
        MetadataRepository repository = dataStorage.getMetadataRepository();
        ComplexTypeMetadata type = repository.getComplexType(conceptName);
        // Get document from storage
        MutableDocument storageDocument = EmptyDocument.INSTANCE;
        UserQueryBuilder qb = from(type);
        int i = 0;
        for (FieldMetadata keyField : type.getKeyFields()) {
            qb.where(eq(keyField, id[i++]));
        }
        StorageResults results = dataStorage.fetch(qb.getSelect()); // Expects a transaction here.
        Iterator<DataRecord> iterator = results.iterator();
        try {
            if (iterator.hasNext()) {
                storageDocument = new StorageDocument(dataModelName, repository, iterator.next());
                if (iterator.hasNext()) {
                    throw new IllegalStateException("Expected only one record in query.");
                }
                return storageDocument;
            }
        } finally {
            results.close();
        }
        return storageDocument;
    }
}
