/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package talend.webapp.v3.updatereport.servlet;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.history.Document;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.metadata.*;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 *
 */
class ForeignKeyInfoTransformer implements DocumentTransformer {

    private final TypeMetadata metadata;

    private final String dataClusterName;

    private final static Logger LOG = Logger.getLogger(ForeignKeyInfoTransformer.class);

    public ForeignKeyInfoTransformer(TypeMetadata metadata, String dataClusterName) {
        this.metadata = metadata;
        this.dataClusterName = dataClusterName;
    }

    public Document transform(MutableDocument document) {
        Map<String, FieldMetadata> pathToForeignKeyInfo = metadata.accept(new ForeignKeyInfoResolver());

        Set<Map.Entry<String, FieldMetadata>> entries = pathToForeignKeyInfo.entrySet();
        for (Map.Entry<String, FieldMetadata> entry : entries) {
            String path = entry.getKey();
            FieldMetadata fieldMetadata = entry.getValue();
            Accessor accessor = document.createAccessor("/ii/p/" + path); //$NON-NLS-1$

            if (accessor.exist()) {  // The field might not be set, so check if it exists.
                String foreignKeyValue = accessor.get(); // Raw foreign key value (surrounded by "[")
                String resolvedForeignKeyInfo = resolveForeignKeyValue(fieldMetadata, foreignKeyValue); // Value to be displayed to users
                accessor.set(resolvedForeignKeyInfo);
            }
        }

        return document;
    }

    private String resolveForeignKeyValue(FieldMetadata foreignKeyField, String foreignKeyValue) {

        ItemPOJO item;
        try {
            ItemPOJOPK pk = new ItemPOJOPK();
            pk.setConceptName(foreignKeyField.getType());
            pk.setDataClusterPOJOPK(new DataClusterPOJOPK(dataClusterName));
            // For composite keys, format is "[id0][id1]...[idN]"
            String[] allKeys = foreignKeyValue.split("]");
            String[] key = new String[allKeys.length];
            int i = 0;
            for (String currentKey : allKeys) {
                key[i++] = currentKey.substring(1);
            }
            pk.setIds(key);

            item = Util.getItemCtrl2Local().getItem(pk);
        } catch (Exception e) {
            LOG.warn("Unable to access referenced entity in field '"  //$NON-NLS-1$
                    + foreignKeyField.getName()
                    + "' in type '" //$NON-NLS-1$
                    + foreignKeyField.getContainingType().getName()
                    + "' (foreign key value: '" //$NON-NLS-1$
                    + foreignKeyValue
                    + "')"); //$NON-NLS-1$
            return foreignKeyValue;
        }

        try {
            Element element = item.getProjection();
            NodeList nodeList = Util.getNodeList(element, "/" + foreignKeyField.getType() + "/" + foreignKeyField.getForeignKeyInfoField());
            if (nodeList.getLength() == 1) {
                return nodeList.item(0).getTextContent();
            } else {
                throw new IllegalStateException("Expected 1 match but got " + nodeList.getLength() + ".");
            }
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ForeignKeyInfoResolver extends DefaultMetadataVisitor<Map<String, FieldMetadata>> {

        private final Map<String, FieldMetadata> pathToForeignKeyInfo = new HashMap<String, FieldMetadata>();

        private Stack<String> currentPosition = new Stack<String>();

        private String getCurrentPath() {
            String path = "";  //$NON-NLS-1$
            for (String pathElement : currentPosition) {
                path += "/" + pathElement; //$NON-NLS-1$
            }
            return path;
        }

        @Override
        public Map<String, FieldMetadata> visit(ComplexTypeMetadata metadata) {
            currentPosition.push(metadata.getName());
            {
                super.visit(metadata);
            }
            currentPosition.pop();

            return pathToForeignKeyInfo;
        }

        @Override
        public Map<String, FieldMetadata> visit(ReferenceFieldMetadata metadata) {
            currentPosition.push(metadata.getName());
            {
                if (metadata.hasForeignKeyInfo()) {
                    pathToForeignKeyInfo.put(getCurrentPath(), metadata);
                }
                super.visit(metadata);
            }
            currentPosition.pop();

            return pathToForeignKeyInfo;
        }

        @Override
        public Map<String, FieldMetadata> visit(SimpleTypeFieldMetadata metadata) {
            currentPosition.push(metadata.getName());
            {
                if (metadata.hasForeignKeyInfo()) {
                    pathToForeignKeyInfo.put(getCurrentPath(), metadata);
                }
                super.visit(metadata);
            }
            currentPosition.pop();

            return pathToForeignKeyInfo;
        }

        @Override
        public Map<String, FieldMetadata> visit(FieldMetadata metadata) {
            currentPosition.push(metadata.getName());
            {
                if (metadata.hasForeignKeyInfo()) {
                    pathToForeignKeyInfo.put(getCurrentPath(), metadata);
                }
                super.visit(metadata);
            }
            currentPosition.pop();

            return pathToForeignKeyInfo;
        }
    }

}
