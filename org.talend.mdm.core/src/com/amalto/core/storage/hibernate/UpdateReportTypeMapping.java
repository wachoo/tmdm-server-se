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

    private ComplexTypeMetadata updateReportType;

    private ComplexTypeMetadata databaseUpdateReportType;

    private MetadataRepository repository;

    public UpdateReportTypeMapping(ComplexTypeMetadata updateReportType, ComplexTypeMetadata databaseUpdateReportType, MappingRepository mappings, MetadataRepository repository) {
        super(updateReportType, mappings);
        this.updateReportType = updateReportType;
        this.databaseUpdateReportType = databaseUpdateReportType;
        this.repository = repository;

        map(updateReportType.getField("UserName"), databaseUpdateReportType.getField("UserName")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("Source"), databaseUpdateReportType.getField("Source")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("TimeInMillis"), databaseUpdateReportType.getField("TimeInMillis")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("OperationType"), databaseUpdateReportType.getField("OperationType")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("RevisionID"), databaseUpdateReportType.getField("RevisionID")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("DataCluster"), databaseUpdateReportType.getField("DataCluster")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("DataModel"), databaseUpdateReportType.getField("DataModel")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("Concept"), databaseUpdateReportType.getField("Concept")); //$NON-NLS-1$ //$NON-NLS-2$
        map(updateReportType.getField("Key"), databaseUpdateReportType.getField("Key")); //$NON-NLS-1$ //$NON-NLS-2$
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
        to.set("UserName", from.get("UserName")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("Source", from.get("Source")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("TimeInMillis", from.get("TimeInMillis")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("OperationType", from.get("OperationType")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("RevisionID", from.get("RevisionID")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("DataCluster", from.get("DataCluster")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("DataModel", from.get("DataModel")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("Concept", from.get("Concept")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set("Key", from.get("Key")); //$NON-NLS-1$ //$NON-NLS-2$
        try {
            List<DataRecord> dataRecord = (List<DataRecord>) from.get("Item"); //$NON-NLS-1$
            DataRecordXmlWriter writer = new DataRecordXmlWriter("Item"); //$NON-NLS-1$
            StringWriter stringWriter = new StringWriter();
            for (DataRecord record : dataRecord) {
                writer.write(record, new BufferedWriter(stringWriter));
            }
            to.set("Items_xml", stringWriter.toString()); //$NON-NLS-1$
        } catch (IOException e) {
            throw new RuntimeException("Could not set Items XML value", e);
        }
    }

    @Override
    public DataRecord setValues(Wrapper from, DataRecord to) {
        String itemXmlContent = (String) from.get("Items_xml"); //$NON-NLS-1$
        DataRecordReader<String> itemReader = new XmlStringDataRecordReader();
        DataRecord items = itemReader.read(1, repository, updateReportType, "<Update>" + itemXmlContent + "</Update>");  //$NON-NLS-1$ //$NON-NLS-2$

        to.set(updateReportType.getField("UserName"), from.get("UserName")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("Source"), from.get("Source")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("TimeInMillis"), from.get("TimeInMillis")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("OperationType"), from.get("OperationType")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("RevisionID"), from.get("RevisionID")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("DataCluster"), from.get("DataCluster")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("DataModel"), from.get("DataModel")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("Concept"), from.get("Concept")); //$NON-NLS-1$ //$NON-NLS-2$
        to.set(updateReportType.getField("Key"), from.get("Key")); //$NON-NLS-1$ //$NON-NLS-2$
        List<DataRecord> itemList = (List<DataRecord>) items.get("Item"); //$NON-NLS-1$
        for (DataRecord dataRecord : itemList) {
            to.set(updateReportType.getField("Item"), dataRecord); //$NON-NLS-1$
        }

        return to;
    }
}
