package com.amalto.core.storage.history;

import com.amalto.core.query.user.*;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;

import java.util.HashMap;
import java.util.Map;

class HistoryNodeBuilder extends VisitorAdapter<HistoryNode> {

    private final Map<String, HistoryNode> nodes = new HashMap<String, HistoryNode>();

    @Override
    public HistoryNode visit(Select select) {
        // Create all HistoryNode instances
        for (ComplexTypeMetadata type : select.getTypes()) {
            nodes.put(type.getName(), new HistoryNode(type));
        }
        HistoryNode rootNode = nodes.get(select.getTypes().get(0).getName());
        for (Join join : select.getJoins()) {
            join.accept(this);
        }
        // Compute query to get rootNode ids
        Select copy = select.copy();
        copy.getSelectedFields().clear();
        for (FieldMetadata keyField : rootNode.type.getKeyFields()) {
            copy.getSelectedFields().add(new Field(keyField));
        }
        // Also select joined fields
        for (Join join : select.getJoins()) {
            String leftEntity = join.getLeftField().getFieldMetadata().getEntityTypeName();
            if (leftEntity.equals(rootNode.type.getName())) {
                copy.getSelectedFields().add(new Field(join.getLeftField().getFieldMetadata()));
            }
        }
        rootNode.query = copy;
        return rootNode;
    }

    @Override
    public HistoryNode visit(Join join) {
        FieldMetadata fieldMetadata = join.getLeftField().getFieldMetadata();
        String entityTypeName = fieldMetadata.getEntityTypeName();
        HistoryNode leftNode = nodes.get(entityTypeName);
        if (leftNode == null) {
            throw new IllegalStateException("Excepted a node for type '" + entityTypeName + "'. Is it selected in query?");
        }
        HistoryNode rightNode = nodes.get(join.getRightField().getFieldMetadata().getEntityTypeName());
        if (rightNode == null) {
            throw new IllegalStateException("Excepted a node for type '" + entityTypeName + "'. Is it selected in query?");
        }
        if (!(fieldMetadata instanceof ReferenceFieldMetadata)) {
            throw new IllegalStateException("Field for join (left field) must be a FK.");
        }
        leftNode.addChild((ReferenceFieldMetadata) fieldMetadata, rightNode);
        return null;
    }
}
