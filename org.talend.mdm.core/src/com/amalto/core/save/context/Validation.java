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

package com.amalto.core.save.context;

import com.amalto.core.save.DocumentSaverContext;
import com.amalto.core.save.SaverSession;
import com.amalto.core.schema.validation.Validator;
import com.amalto.core.schema.validation.XmlSchemaValidator;
import org.w3c.dom.Element;

class Validation implements DocumentSaver {

    private final DocumentSaver next;

    Validation(DocumentSaver next) {
        this.next = next;
    }

    public void save(SaverSession session, DocumentSaverContext context) {
        try {
            // TODO Schematron
            Validator validator = new XmlSchemaValidator(context.getDataModelName(),
                    context.getSaverSource().getSchema(context.getDataModelName()),
                    Validator.NO_OP_VALIDATOR);
            Element element = context.getDatabaseValidationDocument().asDOM().getDocumentElement();
            validator.validate(element);

            next.save(session, context);
        } catch (Exception e) {
            System.out.println("context = " + context.getDatabaseValidationDocument().exportToString());
            throw new RuntimeException("Failed to validate document", e);
        }
    }

    public String[] getSavedId() {
        return next.getSavedId();
    }

    public String getSavedConceptName() {
        return next.getSavedConceptName();
    }
}
