package com.amalto.core.query.processor;

import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.query.user.*;
import com.amalto.xmldb.XmldbSLWrapper;

import java.util.List;

public class QueryProcessorFactory {

    private QueryProcessorFactory() {
    }

    public static QueryProcessor getQueryProcessor(Select select, XmldbSLWrapper server) {
        List<Expression> selectedFields = select.getSelectedFields();

        boolean canUseGetById = false;
        VisitorAdapter<Boolean> check = new CanUseGetByIdCheck();
        for (Expression selectedField : selectedFields) {
            canUseGetById = selectedField.accept(check);
            if (!canUseGetById) {
                break;
            }
        }

        if (!canUseGetById) {
            return new QueryStrategy(server);
        } else {
            return new GetByIdStrategy();
        }
    }

    static class CanUseGetByIdCheck extends VisitorAdapter<Boolean> {
        TypeMetadata currentType = null;

        @Override
        public Boolean visit(Count count) {
            return true;
        }

        @Override
        public Boolean visit(Field selectedField) {
            FieldMetadata fieldMetadata = selectedField.getFieldMetadata();

            if (currentType == null) {
                currentType = fieldMetadata.getContainingType();
            } else {
                if (currentType != fieldMetadata.getContainingType()) {
                    return false;
                }
            }

            return fieldMetadata.isKey();
        }
    }
}
