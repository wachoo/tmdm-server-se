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

package com.amalto.core.query;

import com.amalto.core.metadata.*;
import com.amalto.core.server.MockServerLifecycle;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.HibernateStorage;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import junit.framework.TestCase;
import org.apache.commons.lang.NotImplementedException;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Random;
import java.util.Stack;

public class RTETestCase extends TestCase {

    private final InputStream resourceAsStream;

    public RTETestCase() {
        resourceAsStream = RTETestCase.class.getResourceAsStream("rte.xsd");
    }

    public void testModel() throws Exception {
        // Here so JUnit does not complain about a test case with no test.
    }

    // Disables this test for test suite execution
    public void __test() throws Exception {
        System.out.println("Setting up MDM server environment...");
        ServerContext.INSTANCE.get(new MockServerLifecycle());
        System.out.println("MDM server environment set.");

        System.out.println("Preparing storage for tests...");
        Storage storage = new HibernateStorage("MDM");
        MetadataRepository repository = new MetadataRepository();
        repository.load(resourceAsStream);

        storage.init(StorageTestCase.DATABASE + "-Default");
        storage.prepare(repository, true);
        System.out.println("Storage prepared.");

        Collection<ComplexTypeMetadata> types = MetadataUtils.sortTypes(repository);
        try {
            int fail = 0;
            for (ComplexTypeMetadata type : types) {
                if (type.getKeyFields().size() > 0) {
                    try {
                        storage.begin();
                        storage.update(type.accept(new TestDataRecordCreator()));
                        storage.commit();
                    } catch (Exception e) {
                        fail++;
                        storage.rollback();
                    }
                }
            }
            assertEquals(0, fail);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class TestDataRecordCreator extends DefaultMetadataVisitor<DataRecord> {

        private Stack<DataRecord> records = new Stack<DataRecord>();

        @Override
        public DataRecord visit(ComplexTypeMetadata complexType) {
            records.push(new DataRecord(complexType, UnsupportedDataRecordMetadata.INSTANCE));
            super.visit(complexType);
            return records.peek();
        }

        @Override
        public DataRecord visit(ReferenceFieldMetadata referenceField) {
            ComplexTypeMetadata referencedType = referenceField.getReferencedType();
            DataRecord newRecord = new DataRecord(referencedType, UnsupportedDataRecordMetadata.INSTANCE);
            for (FieldMetadata keyField : referencedType.getKeyFields()) {
                newRecord.set(keyField, createSimpleValue(keyField));
            }
            records.peek().set(referenceField, newRecord);
            return records.peek();
        }

        @Override
        public DataRecord visit(ContainedTypeFieldMetadata containedField) {
            DataRecord record = new DataRecord(containedField.getContainedType(), UnsupportedDataRecordMetadata.INSTANCE);
            records.peek().set(containedField, record);
            records.push(record);
            super.visit(containedField);
            return records.pop();
        }

        @Override
        public DataRecord visit(SimpleTypeFieldMetadata simpleField) {
            records.peek().set(simpleField, createSimpleValue(simpleField));
            return records.peek();
        }

        @Override
        public DataRecord visit(EnumerationFieldMetadata enumField) {
            records.peek().set(enumField, createSimpleValue(enumField));
            return records.peek();
        }

        private Object createSimpleValue(FieldMetadata field) {
            if (field.isKey()) {
                TypeMetadata type = field.getType();
                while (!type.getSuperTypes().isEmpty()) {
                    type = type.getSuperTypes().iterator().next();
                }
                if ("string".equals(type.getName())) {
                    return "1";
                } else if ("int".equals(type.getName())) {
                    return 1;
                } else {
                    throw new NotImplementedException("Support for key with type " + type.getName());
                }
            }

            // Move up the inheritance tree to find the "most generic" type (used when simple types inherits from XSD types,
            // in this case, the XSD type is interesting, not the custom one).
            TypeMetadata type = field.getType();
            while (!type.getSuperTypes().isEmpty()) {
                type = type.getSuperTypes().iterator().next();
            }

            Random random = new Random(System.currentTimeMillis());
            if (!(field instanceof ContainedTypeFieldMetadata)) { // Don't set contained (anonymous types) values
                if ("string".equals(type.getName())) {
                    return "" + random.nextLong();
                } else if ("integer".equals(type.getName())
                        || "positiveInteger".equals(type.getName())
                        || "negativeInteger".equals(type.getName())
                        || "nonNegativeInteger".equals(type.getName())
                        || "nonPositiveInteger".equals(type.getName())
                        || "int".equals(type.getName())
                        || "unsignedInt".equals(type.getName())) {
                    return random.nextInt();
                } else if ("date".equals(type.getName())) {
                    return new Timestamp(random.nextInt());
                } else if ("dateTime".equals(type.getName())) {
                    return new Timestamp(random.nextLong());
                } else if ("boolean".equals(type.getName())) {
                    return random.nextBoolean();
                } else if ("decimal".equals(type.getName())) {
                    return new BigDecimal(random.nextInt());
                } else if ("float".equals(type.getName())) {
                    return random.nextFloat();
                } else if ("long".equals(type.getName()) || "unsignedLong".equals(type.getName())) {
                    return random.nextLong();
                } else if ("anyURI".equals(type.getName())) {
                    return "http://www.talend.com";
                } else if ("short".equals(type.getName()) || "unsignedShort".equals(type.getName())) {
                    return random.nextInt() % 255;
                } else if ("QName".equals(type.getName())) {
                    return "Qname";
                } else if ("base64Binary".equals(type.getName())) {
                    return "EF56AE";
                } else if ("hexBinary".equals(type.getName())) {
                    return "EF56AE";
                } else if ("byte".equals(type.getName()) || "unsignedByte".equals(type.getName())) {
                    return random.nextInt() % 2;
                } else if ("double".equals(type.getName()) || "unsignedDouble".equals(type.getName())) {
                    return random.nextDouble();
                } else if ("duration".equals(type.getName()) || "time".equals(type.getName())) {
                    return new Timestamp(random.nextInt());
                } else {
                    throw new NotImplementedException("No support for type '" + type.getName() + "'");
                }
            } else {
                return null;
            }
        }

    }
}
