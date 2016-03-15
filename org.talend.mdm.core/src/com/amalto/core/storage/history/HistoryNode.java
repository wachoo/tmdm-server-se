package com.amalto.core.storage.history;

import java.util.*;

import org.apache.commons.lang.NotImplementedException;
import org.talend.mdm.commmon.metadata.*;

import com.amalto.core.history.Action;
import com.amalto.core.history.DocumentHistory;
import com.amalto.core.history.DocumentHistoryNavigator;
import com.amalto.core.query.user.At;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.save.context.StorageDocument;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageMetadataUtils;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;

class HistoryNode {

    final ComplexTypeMetadata type;

    private final Map<ReferenceFieldMetadata, HistoryNode> children = new HashMap<>();

    Select query;

    private List<DataRecord> historyRecords = null;

    HistoryNode(ComplexTypeMetadata type) {
        this.type = type;
    }

    void evaluate(Storage storage, final DocumentHistory history, At historyQuery) {
        Map<ReferenceFieldMetadata, List<String>> childrenIds = new HashMap<ReferenceFieldMetadata, List<String>>();
        // Get history for current node's records
        StorageResults results = storage.fetch(query);
        historyRecords = new ArrayList<>(results.getCount());
        List<String[]> ids = new LinkedList<>();
        for (DataRecord result : results) {
            String[] id = new String[type.getKeyFields().size()];
            Iterator<FieldMetadata> keyIterator = type.getKeyFields().iterator();
            for (int i = 0; i < type.getKeyFields().size(); i++) {
                FieldMetadata key = keyIterator.next();
                id[i] = StorageMetadataUtils.toString(result.get(key), key);
            }
            ids.add(id);
        }
        // Find history for records
        for (String[] id : ids) {
            String container = storage.getName();
            String model = storage.getName();
            DocumentHistoryNavigator navigator = history.getHistory(container, model, type.getName(), id);
            navigator.goTo(new Date(historyQuery.getDateTime()));
            switch (historyQuery.getSwing()) {
            case CLOSEST:
                break;
            case BEFORE:
                if (navigator.hasPrevious()) {
                    navigator.previous();
                }
                break;
            case AFTER:
                if (navigator.hasNext()) {
                    navigator.next();
                }
                break;
            }
            StorageDocument current = (StorageDocument) navigator.current();
            if (current.isDeleted()) {
                // Don't consider this record, it didn't exist at this date.
                continue;
            }
            final DataRecord historyRecord = current.getDataRecord();
            if (historyRecord.isEmpty()) {
                // Don't consider this record, it didn't exist at this date.
                continue;
            }
            Action action = navigator.currentAction();
            long lastModificationTime = action.getDate().getTime(); // Set timestamp to action date
            // No op action uses Long min value (not interesting info).
            if (lastModificationTime != Long.MIN_VALUE) {
                historyRecord.getRecordMetadata().setLastModificationTime(lastModificationTime);
            }
            historyRecords.add(historyRecord);
            // Find history for children nodes
            for (Map.Entry<ReferenceFieldMetadata, HistoryNode> entry : children.entrySet()) {
                ReferenceFieldMetadata fkField = entry.getKey();
                List<DataRecord> values;
                if (fkField.isMany()) {
                    values = (List<DataRecord>) historyRecord.get(fkField);
                } else {
                    values = Collections.singletonList((DataRecord) historyRecord.get(fkField));
                }
                for (DataRecord referencedEntity : values) {
                    if (referencedEntity != null) {
                        if (childrenIds.get(fkField) == null) {
                            childrenIds.put(fkField, new ArrayList<String>());
                        }
                        FieldMetadata referencedField = fkField.getReferencedField();
                        childrenIds.get(fkField).add(String.valueOf(referencedEntity.get(referencedField)));
                    }
                }
            }
        }
        // Evaluate children (build get by queries first)
        for (Map.Entry<ReferenceFieldMetadata, HistoryNode> entry : children.entrySet()) {
            HistoryNode childNode = children.get(entry.getKey());
            UserQueryBuilder childNodeIdQuery = UserQueryBuilder.from(childNode.type);
            if (entry.getKey().getReferencedField() instanceof CompoundFieldMetadata) {
                // TODO Support for composite id
                throw new NotImplementedException();
            } else { // Unique field id
                List<String> idList = childrenIds.get(entry.getKey());
                String[] values = idList.toArray(new String[idList.size()]);
                childNodeIdQuery.where(UserQueryBuilder.eq(entry.getKey().getReferencedField(), values));
            }
            for (ReferenceFieldMetadata field : childNode.children.keySet()) {
                childNodeIdQuery.select(field);
            }
            childNode.query = childNodeIdQuery.getSelect();
            childNode.evaluate(storage, history, historyQuery); // Evaluate child
        }
    }

    void fill(Storage storage) {
        if (historyRecords == null) {
            // must call evaluate() before fill().
            throw new IllegalStateException("Node has not been evaluated.");
        }
        // Asks child nodes to fill storage too
        for (HistoryNode historyNode : children.values()) {
            historyNode.fill(storage);
        }
        // Fills storage with record
        storage.update(historyRecords);
    }

    void addChild(ReferenceFieldMetadata field, HistoryNode node) {
        children.put(field, node);
    }
}
