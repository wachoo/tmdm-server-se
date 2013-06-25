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
package org.talend.mdm.webapp.journal.server.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.DefaultMetadataVisitor;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.history.Document;
import com.amalto.core.history.DocumentTransformer;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class ForeignKeyInfoTransformer implements DocumentTransformer {

    private final TypeMetadata metadata;

    private final String dataClusterName;

    private final static Logger LOG = Logger.getLogger(ForeignKeyInfoTransformer.class);

    public ForeignKeyInfoTransformer(TypeMetadata metadata, String dataClusterName) {
        this.metadata = metadata;
        this.dataClusterName = dataClusterName;
    }

    public Document transform(MutableDocument document) {
        Map<String, ReferenceFieldMetadata> pathToForeignKeyInfo = metadata.accept(new ForeignKeyInfoResolver());

        Set<Map.Entry<String, ReferenceFieldMetadata>> entries = pathToForeignKeyInfo.entrySet();
        for (Map.Entry<String, ReferenceFieldMetadata> entry : entries) {
            String path = entry.getKey();
            ReferenceFieldMetadata fieldMetadata = entry.getValue();
            Accessor accessor = document.createAccessor(path);

            if (accessor.exist()) { // The field might not be set, so check if it exists.
                String foreignKeyValue = accessor.get(); // Raw foreign key value (surrounded by "[")
                String resolvedForeignKeyInfo = resolveForeignKeyValue(fieldMetadata, foreignKeyValue); // Value to be
                // displayed to
                // users
                accessor.set(resolvedForeignKeyInfo);
            }
        }

        return document;
    }

    private String resolveForeignKeyValue(ReferenceFieldMetadata foreignKeyField, String foreignKeyValue) {
        String referencedTypeName = foreignKeyField.getReferencedType().getName();
        ItemPOJO item;
        try {
            ItemPOJOPK pk = new ItemPOJOPK();
            pk.setConceptName(referencedTypeName);
            pk.setDataClusterPOJOPK(new DataClusterPOJOPK(dataClusterName));
            // For composite keys, format is "[id0][id1]...[idN]"
            String[] allKeys = foreignKeyValue.split("]"); //$NON-NLS-1$
            String[] key = new String[allKeys.length];
            int i = 0;
            for (String currentKey : allKeys) {
                key[i++] = currentKey.substring(1);
            }
            pk.setIds(key);

            item = Util.getItemCtrl2Local().getItem(pk);
        } catch (Exception e) {
            LOG.warn("Unable to access referenced entity in field '" //$NON-NLS-1$
                    + foreignKeyField.getName() + "' in type '" //$NON-NLS-1$
                    + foreignKeyField.getContainingType().getName() + "' (foreign key value: '" //$NON-NLS-1$
                    + foreignKeyValue + "')"); //$NON-NLS-1$
            return foreignKeyValue;
        }

        try {
            Element element = item.getProjection();
            StringBuilder foreignKeyInfo = new StringBuilder();
            for (FieldMetadata fieldMetadata : foreignKeyField.getForeignKeyInfoFields()) {
                NodeList nodeList = Util.getNodeList(element,
                        "/" + referencedTypeName + "/" + fieldMetadata.getName()); //$NON-NLS-1$ //$NON-NLS-2$
                if (nodeList.getLength() == 1 && nodeList.item(0).getTextContent() != null && !nodeList.item(0).getTextContent().isEmpty()) {
                    if (foreignKeyInfo.length() > 0) {
                        foreignKeyInfo.append("-"); //$NON-NLS-1$
                    } 
                    foreignKeyInfo.append(nodeList.item(0).getTextContent());
                }
            }
            if (foreignKeyField.getForeignKeyInfoFields().size() > 0) {
                return foreignKeyInfo.toString();   
            } else {
                return foreignKeyValue;
            }
        } catch (XtentisException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ForeignKeyInfoResolver extends DefaultMetadataVisitor<Map<String, ReferenceFieldMetadata>> {

        private final Map<String, ReferenceFieldMetadata> pathToForeignKeyInfo = new HashMap<String, ReferenceFieldMetadata>();

        private Stack<String> currentPosition = new Stack<String>();

        private String getCurrentPath() {
            String path = ""; //$NON-NLS-1$
            for (String pathElement : currentPosition) {
                path += "/" + pathElement; //$NON-NLS-1$
            }
            return path;
        }

        @Override
        public Map<String, ReferenceFieldMetadata> visit(ComplexTypeMetadata metadata) {
            super.visit(metadata);
            return pathToForeignKeyInfo;
        }

        @Override
        public Map<String, ReferenceFieldMetadata> visit(ReferenceFieldMetadata metadata) {
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
        public Map<String, ReferenceFieldMetadata> visit(FieldMetadata fieldMetadata) {
            currentPosition.push(fieldMetadata.getName());
            {
                super.visit(fieldMetadata);
            }
            currentPosition.pop();

            return pathToForeignKeyInfo;
        }

        @Override
        public Map<String, ReferenceFieldMetadata> visit(SimpleTypeFieldMetadata metadata) {
            currentPosition.push(metadata.getName());
            {
                super.visit(metadata);
            }
            currentPosition.pop();

            return pathToForeignKeyInfo;
        }
    }
}
