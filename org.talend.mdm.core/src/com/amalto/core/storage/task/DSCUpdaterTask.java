package com.amalto.core.storage.task;

import com.amalto.core.load.io.ResettableStringWriter;
import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.UserStagingQueryBuilder;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.httpclient.HttpConnection;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.amalto.core.query.user.UserQueryBuilder.*;

/**
 *
 */
public class DSCUpdaterTask extends MetadataRepositoryTask {

    private final Storage origin;

    private final Storage destination;

    private final XMLOutputFactory xmlOutputFactory;

    private int recordsCount;

    DSCUpdaterTask(Storage origin, Storage destination, MetadataRepository repository, ClosureExecutionStats stats) {
        super(origin, repository, stats);
        this.origin = origin;
        this.destination = destination;
        xmlOutputFactory = XMLOutputFactory.newInstance();
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Select select = from(type)
                .where(eq(UserStagingQueryBuilder.status(), StagingConstants.SUCCESS_MERGE_CLUSTER_TO_RESOLVE))
                .getSelect();
        StorageResults records = storage.fetch(select);
        try {
            recordsCount += records.getCount();
        } finally {
            records.close();
        }
        return new SingleThreadedTask(type.getName(), destination, select, new DSCTaskClosure(origin, xmlOutputFactory), stats);
    }

    @Override
    public int getRecordCount() {
        return recordsCount;
    }

    @Override
    public int getErrorCount() {
        return 0;
    }

    private static class DSCTaskClosure implements Closure {

        private final Storage origin;

        private final XMLOutputFactory xmlOutputFactory;

        private XMLStreamWriter writer;

        private ResettableStringWriter output;
        private GetMethod get;

        public DSCTaskClosure(Storage origin, XMLOutputFactory xmlOutputFactory) {
            this.origin = origin;
            this.xmlOutputFactory = xmlOutputFactory;
        }

        public void begin() {
            output = new ResettableStringWriter();
        }

        public void execute(DataRecord stagingRecord, ClosureExecutionStats stats) {
            String taskId = stagingRecord.getRecordMetadata().getTaskId();
            Select select = from(stagingRecord.getType()).where(eq(taskId(), taskId)).getSelect();
            StorageResults originRecords = origin.fetch(select);
            try {
                writer = xmlOutputFactory.createXMLStreamWriter(output);
                writer.writeStartDocument();
                writer.writeStartElement("Tasks"); //$NON-NLS-1$
                writer.writeStartElement("Task"); //$NON-NLS-1$
                {
                    writer.writeStartElement("taskName"); //$NON-NLS-1$
                    writer.writeCharacters(taskId);
                    writer.writeEndElement();
                    writer.writeStartElement("taskType"); //$NON-NLS-1$
                    writer.writeCharacters("1"); //$NON-NLS-1$
                    writer.writeEndElement();
                    writer.writeStartElement("createdBy"); //$NON-NLS-1$
                    writer.writeCharacters("Autonomous resolver"); //$NON-NLS-1$
                    writer.writeEndElement();

                    for (DataRecord originRecord : originRecords) {
                        writer.writeStartElement("srcRecord"); //$NON-NLS-1$
                        {
                            // TODO Extra info (timestamp...)
                            writer.writeStartElement("source"); //$NON-NLS-1$
                            writer.writeCharacters("TODO"); //$NON-NLS-1$ // TODO
                            writer.writeEndElement();

                            writer.writeStartElement("score"); //$NON-NLS-1$
                            writer.writeCharacters("1.0"); //$NON-NLS-1$ // TODO
                            writer.writeEndElement();

                            writer.writeStartElement("weights"); //$NON-NLS-1$
                            writer.writeCharacters("10"); //$NON-NLS-1$ // TODO
                            writer.writeEndElement();

                            ComplexTypeMetadata originRecordType = originRecord.getType();
                            for (FieldMetadata field : originRecordType.getFields()) {
                                Object value = originRecord.get(field);
                                if (value != null) {
                                    writer.writeStartElement("srcColumn"); //$NON-NLS-1$
                                    {
                                        writer.writeStartElement("colName"); //$NON-NLS-1$
                                        writer.writeCharacters(field.getName());
                                        writer.writeEndElement();

                                        writer.writeStartElement("colValue"); //$NON-NLS-1$
                                        writer.writeCharacters(String.valueOf(value));
                                        writer.writeEndElement();

                                        writer.writeStartElement("colType"); //$NON-NLS-1$
                                        writer.writeCharacters(getFieldType(field));
                                        writer.writeEndElement();

                                        writer.writeStartElement("colIskey"); //$NON-NLS-1$
                                        if (field.isKey()) {
                                            writer.writeCharacters("1"); //$NON-NLS-1$
                                        } else {
                                            writer.writeCharacters("0"); //$NON-NLS-1$
                                        }
                                        writer.writeEndElement();
                                    }
                                    writer.writeEndElement();
                                }
                            }
                        }
                        writer.writeEndElement();
                    }

                    writer.writeStartElement("tgtRecord"); //$NON-NLS-1$
                    {
                        // TODO Extra info (timestamp...)
                        writer.writeStartElement("resolvedBy"); //$NON-NLS-1$
                        writer.writeCharacters("Autonomous resolver"); //$NON-NLS-1$ // TODO
                        writer.writeEndElement();

                        writer.writeStartElement("resolvedOn"); //$NON-NLS-1$
                        Date modificationDate = new Date(stagingRecord.getRecordMetadata().getLastModificationTime());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); //$NON-NLS-1$
                        writer.writeCharacters(dateFormat.format(modificationDate));
                        writer.writeEndElement();

                        ComplexTypeMetadata originRecordType = stagingRecord.getType();
                        for (FieldMetadata field : originRecordType.getFields()) {
                            Object value = stagingRecord.get(field);
                            if (value != null) {
                                writer.writeStartElement("tgtColumn"); //$NON-NLS-1$
                                {
                                    writer.writeStartElement("defColName"); //$NON-NLS-1$
                                    writer.writeCharacters(field.getName());
                                    writer.writeEndElement();

                                    writer.writeStartElement("defColValue"); //$NON-NLS-1$
                                    writer.writeCharacters(String.valueOf(value));
                                    writer.writeEndElement();

                                    writer.writeStartElement("defColType"); //$NON-NLS-1$
                                    writer.writeCharacters(getFieldType(field));
                                    writer.writeEndElement();

                                    writer.writeStartElement("defColIskey"); //$NON-NLS-1$
                                    if (field.isKey()) {
                                        writer.writeCharacters("1"); //$NON-NLS-1$
                                    } else {
                                        writer.writeCharacters("0"); //$NON-NLS-1$
                                    }
                                    writer.writeEndElement();
                                }
                                writer.writeEndElement();
                            }
                        }
                    }
                    writer.writeEndElement();
                }

                try {
                    writer.writeEndElement();
                    writer.writeEndDocument();

                    HttpState state = new HttpState();
                    state.setCredentials(new AuthScope("localhost", 8080), new UsernamePasswordCredentials("administrator", "administrator")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    HttpConnection connection = new HttpConnection("localhost", 8080); //$NON-NLS-1$
                    connection.open();
                    get = new GetMethod("/org.talend.datastewardship-5.1.0-SNAPSHOT/dataloader");
                    get.setQueryString(new NameValuePair[]{
                            new NameValuePair("INPUT_TASKS", output.toString()) //$NON-NLS-1$
                    });
                    get.execute(state, connection);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    output.reset();
                    get = null;
                }
            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }

        }

        @Override
        public void cancel() {
            if (get != null) {
                get.abort();
            }
        }

        private String getFieldType(FieldMetadata field) {
            if(field instanceof ReferenceFieldMetadata) {
                return "string"; //$NON-NLS-1$
            }

            TypeMetadata currentType = field.getType();
            while(!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(currentType.getNamespace())) {
                currentType = currentType.getSuperTypes().iterator().next();
            }
            if("dateTime".equals(currentType.getName())) { //$NON-NLS-1$
                return "date"; //$NON-NLS-1$
            } else if("time".equals(currentType.getName())) { //$NON-NLS-1$
                return "date"; //$NON-NLS-1$
            } else {
                return currentType.getName();
            }
        }

        public void end(ClosureExecutionStats stats) {

        }

        public Closure copy() {
            return new DSCTaskClosure(origin, xmlOutputFactory);
        }
    }
}
