package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.MetadataRepository;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordReader;
import com.amalto.core.storage.record.DataRecordXmlWriter;
import com.amalto.core.storage.record.XmlStringDataRecordReader;
import org.hibernate.Session;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
*
*/
class UpdateReportTypeMapping extends TypeMapping {

    private static final String MAPPING_NAME = "FIXED UPDATE REPORT MAPPING"; //$NON-NLS-1$

    private ComplexTypeMetadata updateReportType;

    private ComplexTypeMetadata databaseUpdateReportType;

    private MetadataRepository repository;

    public UpdateReportTypeMapping(ComplexTypeMetadata updateReportType, ComplexTypeMetadata databaseUpdateReportType, MappingRepository mappings, MetadataRepository repository) {
        super(updateReportType, mappings);
        this.updateReportType = updateReportType;
        this.databaseUpdateReportType = databaseUpdateReportType;
        this.repository = repository;

        map(updateReportType.getField("UserName"), databaseUpdateReportType.getField("x_user_name")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("Source"), databaseUpdateReportType.getField("x_source")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("TimeInMillis"), databaseUpdateReportType.getField("x_time_in_millis")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("OperationType"), databaseUpdateReportType.getField("x_operation_type")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("RevisionID"), databaseUpdateReportType.getField("x_revision_id")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("DataCluster"), databaseUpdateReportType.getField("x_data_cluster")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("DataModel"), databaseUpdateReportType.getField("x_data_model")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("Concept"), databaseUpdateReportType.getField("x_concept")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("Key"), databaseUpdateReportType.getField("x_key")); //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public ComplexTypeMetadata getUser() {
        return updateReportType;
    }

    @Override
    public ComplexTypeMetadata getDatabase() {
        return databaseUpdateReportType;
    }

    @Override
    public void setValues(Session session, DataRecord from, Wrapper to) {
        to.set("x_user_name", from.get("UserName")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_source", from.get("Source")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_time_in_millis", from.get("TimeInMillis")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_operation_type", from.get("OperationType")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_revision_id", from.get("RevisionID")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_data_cluster", from.get("DataCluster")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_data_model", from.get("DataModel")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_concept", from.get("Concept")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("x_key", from.get("Key")); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            List<DataRecord> dataRecord = (List<DataRecord>) from.get("Item"); //$NON-NLS-1$
            DataRecordXmlWriter writer = new DataRecordXmlWriter("Item"); //$NON-NLS-1$
            StringWriter stringWriter = new StringWriter();
            for (DataRecord record : dataRecord) {
                writer.write(record, new BufferedWriter(stringWriter));
            }
            to.set("x_items_xml", stringWriter.toString()); //$NON-NLS-1$
        } catch (IOException e) {
            throw new RuntimeException("Could not set Items XML value", e);
        }
    }

    @Override
    public DataRecord setValues(Wrapper from, DataRecord to) {
        String itemXmlContent = (String) from.get("x_items_xml"); //$NON-NLS-1$
        DataRecordReader<String> itemReader = new XmlStringDataRecordReader();
        DataRecord items = itemReader.read(1, repository, updateReportType, "<Update>" + itemXmlContent + "</Update>");  //$NON-NLS-1$ //$NON-NLS-2$

        to.set(updateReportType.getField("UserName"), from.get("x_user_name")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("Source"), from.get("x_source")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("TimeInMillis"), from.get("x_time_in_millis")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("OperationType"), from.get("x_operation_type")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("RevisionID"), from.get("x_revision_id")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("DataCluster"), from.get("x_data_cluster")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("DataModel"), from.get("x_data_model")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("Concept"), from.get("x_concept")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("Key"), from.get("x_key")); //$NON-NLS-1$ //$NON-NLS-2$
        List<DataRecord> itemList = (List<DataRecord>) items.get("Item"); //$NON-NLS-1$
        for (DataRecord dataRecord : itemList) {
            to.set(updateReportType.getField("Item"), dataRecord); //$NON-NLS-1$
        }

        return to;
    }

    @Override
    public String toString() {
        return MAPPING_NAME;
    }
}
