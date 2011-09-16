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

package com.amalto.core.metadata;

/**
*
*/
@SuppressWarnings({"HardCodedStringLiteral", "nls"})
class ConsoleDumpMetadataVisitor extends DefaultMetadataVisitor<Void> {
    public Void visit(ComplexTypeMetadata metadata) {
        System.out.println("[Type] " + metadata.getName());
        String keyFields = "";
        for (FieldMetadata keyFieldMetadata : metadata.getKeyFields()) {
            keyFields += keyFieldMetadata.getName() + " ";
        }
        if(!keyFields.isEmpty()) {
            System.out.println("\t[Key fields] " + keyFields);
        }

        return super.visit(metadata);
    }

    @Override
    public Void visit(ReferenceFieldMetadata metadata) {
        if (metadata.isKey()) {
            System.out.print("\t[Field (Complex) (Key)] " + metadata.getName());
        } else {
            System.out.print("\t[Field (Complex)] " + metadata.getName());
        }

        System.out.println(" [FKIntegrity=" + metadata.isFKIntegrity() + " / FKIntegrityOverride=" + metadata.allowFKIntegrityOverride() + "]");

        return super.visit(metadata);
    }

    public Void visit(SimpleTypeFieldMetadata metadata) {
        if (metadata.isKey()) {
            System.out.println("\t[Field (Simple) (Key)] " + metadata.getName());
        } else {
            System.out.println("\t[Field (Simple)] " + metadata.getName());
        }
        return super.visit(metadata);
    }

    public Void visit(EnumerationFieldMetadata metadata) {
        System.out.println("\t[Field (Enumeration)] " + metadata.getName());
        return super.visit(metadata);
    }
}
