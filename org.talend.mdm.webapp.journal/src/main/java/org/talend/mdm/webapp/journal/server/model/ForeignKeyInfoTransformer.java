/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package org.talend.mdm.webapp.journal.server.model;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public Document transform(MutableDocument document) {
        List<ReferenceFieldMetadata> referenceFieldMetadatas = metadata.accept(new ForeignKeyInfoResolver());

        for (ReferenceFieldMetadata referenceFieldMetadata : referenceFieldMetadatas) {
            String path = referenceFieldMetadata.getPath();
            // FIXME
            // path can contain repeatable elements in the middle
            // path should be taken from instance document and not metadata (case of non-anonymous types)
            if (referenceFieldMetadata.isMany()) {
                boolean occurrencePathExists = true;
                for (int i = 1; occurrencePathExists == true; i++) {
                    occurrencePathExists = setForeignKeyValue(document, path + '[' + i + ']', referenceFieldMetadata);
                }
            } else {
                setForeignKeyValue(document, path, referenceFieldMetadata);
            }

        }

        return document;
    }

    private boolean setForeignKeyValue(MutableDocument document, String path, ReferenceFieldMetadata referenceFieldMetadata) {
        Accessor accessor = document.createAccessor(path);
        if (accessor.exist()) { // The field might not be set, so check if it exists.
            String foreignKeyValue = accessor.get(); // Raw foreign key value (surrounded by "[")
            String resolvedForeignKeyInfo = resolveForeignKeyValue(referenceFieldMetadata, foreignKeyValue); // Value
                                                                                                             // to
                                                                                                             // be
            // displayed to
            // users
            accessor.set(resolvedForeignKeyInfo);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Set value " + resolvedForeignKeyInfo + " to " + path); //$NON-NLS-1$ //$NON-NLS-2$
            }
            return true;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(path + " does not exists"); //$NON-NLS-1$
        }
        return false;
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
                NodeList nodeList = Util.getNodeList(element, "/" + referencedTypeName + "/" + fieldMetadata.getName()); //$NON-NLS-1$ //$NON-NLS-2$
                if (nodeList.getLength() == 1 && nodeList.item(0).getTextContent() != null
                        && !nodeList.item(0).getTextContent().isEmpty()) {
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

    private static class ForeignKeyInfoResolver extends DefaultMetadataVisitor<List<ReferenceFieldMetadata>> {

        private final List<ReferenceFieldMetadata> references = new ArrayList<ReferenceFieldMetadata>();

        @Override
        public List<ReferenceFieldMetadata> visit(ComplexTypeMetadata metadata) {
            super.visit(metadata);
            return references;
        }

        @Override
        public List<ReferenceFieldMetadata> visit(ReferenceFieldMetadata metadata) {
            if (metadata.hasForeignKeyInfo()) {
                references.add(metadata);
            }
            super.visit(metadata);
            return references;
        }

        @Override
        public List<ReferenceFieldMetadata> visit(FieldMetadata fieldMetadata) {
            super.visit(fieldMetadata);
            return references;
        }

        @Override
        public List<ReferenceFieldMetadata> visit(SimpleTypeFieldMetadata metadata) {
            super.visit(metadata);
            return references;
        }
    }
}
