// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.storage.record;

import java.io.StringWriter;
import java.io.Writer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataRepository;

import com.amalto.core.storage.SecuredStorage;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;


public class DataRecordJSONWriterTestCase {
    
    private DataRecordJSONWriter writer;
    
    private SecuredStorage.UserDelegator delegate;
    
    private MetadataRepository repository;
    
    @Before
    public void setup() throws Exception{
        writer = new DataRecordJSONWriter();
        delegate = Mockito.mock(SecuredStorage.UserDelegator.class);
        Mockito.when(delegate.hide(Mockito.any(ComplexTypeMetadata.class))).thenReturn(false);
        Mockito.when(delegate.hide(Mockito.any(FieldMetadata.class))).thenReturn(false);
        writer.setSecurityDelegator(delegate);
        repository = new MetadataRepository();
        repository.load(this.getClass().getResourceAsStream("metadata.xsd"));
    }
    
    @Test
    public void testSimpleComplexType() throws Exception {
        DataRecord record = createDataRecord(repository.getComplexType("SimpleProduct"));
        setDataRecordField(record, "Id", "12345");
        setDataRecordField(record, "Name", "Name");
        setDataRecordField(record, "Description", "Desc");
        setDataRecordField(record, "Availability", Boolean.FALSE);
        String result = toJSON(record);
        Assert.assertEquals("{\"simpleproduct\":{\"id\":\"12345\",\"name\":\"Name\",\"description\":\"Desc\",\"availability\":\"false\"}}", result);
    }
    
    @Test
    public void testTypeWithComplexType() throws Exception {
        DataRecord record = createDataRecord(repository.getComplexType("Customer"));
        setDataRecordField(record, "Id", "12345");
        setDataRecordField(record, "Name", "Name");
        String result = toJSON(record);
        Assert.assertEquals("{\"customer\":{\"id\":\"12345\",\"name\":\"Name\",\"Address\":{}}}", result);
    }
    
    private void setDataRecordField(DataRecord record, String name, Object value) throws Exception {
        FieldMetadata fieldMd = record.getType().getField(name);
        Assert.assertNotNull("Unknown field " + name, fieldMd);
        record.set(fieldMd, value);
    }
    
    private DataRecord createDataRecord(ComplexTypeMetadata type) throws Exception {
        DataRecordMetadataImpl recordMeta = new DataRecordMetadataImpl(System.currentTimeMillis(), "taskId");
        DataRecord record = new DataRecord(type, recordMeta);
        return record;
    }
    
    private String toJSON(DataRecord record) throws Exception {
        Writer w = new StringWriter();
        writer.write(record, w);
        return w.toString();
    }

}
