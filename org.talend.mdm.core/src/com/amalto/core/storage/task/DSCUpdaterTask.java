package com.amalto.core.storage.task;

import com.amalto.core.load.io.ResettableStringWriter;
import com.amalto.core.metadata.*;
import com.amalto.core.query.user.Select;
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

    DSCUpdaterTask(Storage origin, Storage destination, MetadataRepository repository) {
        super(origin, repository);
        this.origin = origin;
        this.destination = destination;
        xmlOutputFactory = XMLOutputFactory.newInstance();
    }

    @Override
    protected Task createTypeTask(ComplexTypeMetadata type) {
        Select select = from(type).getSelect();
        return new SingleThreadedTask(type.getName(), destination, select, new DSCTaskClosure(origin, xmlOutputFactory));
    }


    private static class DSCTaskClosure implements Closure {

        private Storage origin;

        private XMLOutputFactory xmlOutputFactory;
        private XMLStreamWriter writer;
        private ResettableStringWriter output;

        public DSCTaskClosure(Storage origin, XMLOutputFactory xmlOutputFactory) {
            this.origin = origin;
            this.xmlOutputFactory = xmlOutputFactory;
        }

        @Override
        public void begin() {
            output = new ResettableStringWriter();
        }

        @Override
        public void execute(DataRecord record) {
            String taskId = record.getRecordMetadata().getTaskId();
            Select select = from(record.getType()).where(eq(taskId(), taskId)).getSelect();
            StorageResults originRecords = origin.fetch(select);
            try {
                writer = xmlOutputFactory.createXMLStreamWriter(output);
                writer.writeStartDocument();
                writer.writeStartElement("Tasks");
                writer.writeStartElement("Task");
                {
                    writer.writeStartElement("taskName");
                    writer.writeCharacters(taskId);
                    writer.writeEndElement();
                    writer.writeStartElement("taskType");
                    writer.writeCharacters("1");
                    writer.writeEndElement();
                    writer.writeStartElement("createdBy");
                    writer.writeCharacters("Autonomous resolver");
                    writer.writeEndElement();

                    for (DataRecord originRecord : originRecords) {
                        writer.writeStartElement("srcRecord");
                        {
                            // TODO Extra info (timestamp...)
                            writer.writeStartElement("source");
                            writer.writeCharacters("TODO"); // TODO
                            writer.writeEndElement();

                            writer.writeStartElement("score");
                            writer.writeCharacters("1.0"); // TODO
                            writer.writeEndElement();

                            writer.writeStartElement("weights");
                            writer.writeCharacters("10"); // TODO
                            writer.writeEndElement();

                            ComplexTypeMetadata originRecordType = originRecord.getType();
                            for (FieldMetadata field : originRecordType.getFields()) {
                                Object value = originRecord.get(field);
                                if (value != null) {
                                    writer.writeStartElement("srcColumn");
                                    {
                                        writer.writeStartElement("colName");
                                        writer.writeCharacters(field.getName());
                                        writer.writeEndElement();

                                        writer.writeStartElement("colValue");
                                        writer.writeCharacters(String.valueOf(value));
                                        writer.writeEndElement();

                                        writer.writeStartElement("colType");
                                        writer.writeCharacters(getFieldType(field));
                                        writer.writeEndElement();

                                        writer.writeStartElement("colIskey");
                                        if (field.isKey()) {
                                            writer.writeCharacters("1");
                                        } else {
                                            writer.writeCharacters("0");
                                        }
                                        writer.writeEndElement();
                                    }
                                    writer.writeEndElement();
                                }
                            }
                        }
                        writer.writeEndElement();
                    }

                    writer.writeStartElement("tgtRecord");
                    {
                        // TODO Extra info (timestamp...)
                        writer.writeStartElement("resolvedBy");
                        writer.writeCharacters("Autonomous resolver"); // TODO
                        writer.writeEndElement();

                        writer.writeStartElement("resolvedOn");
                        Date modificationDate = new Date(record.getRecordMetadata().getLastModificationTime());
                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        writer.writeCharacters(dateFormat.format(modificationDate));
                        writer.writeEndElement();

                        ComplexTypeMetadata originRecordType = record.getType();
                        for (FieldMetadata field : originRecordType.getFields()) {
                            Object value = record.get(field);
                            if (value != null) {
                                writer.writeStartElement("tgtColumn");
                                {
                                    writer.writeStartElement("defColName");
                                    writer.writeCharacters(field.getName());
                                    writer.writeEndElement();

                                    writer.writeStartElement("defColValue");
                                    writer.writeCharacters(String.valueOf(value));
                                    writer.writeEndElement();

                                    writer.writeStartElement("defColType");
                                    writer.writeCharacters(getFieldType(field));
                                    writer.writeEndElement();

                                    writer.writeStartElement("defColIskey");
                                    if (field.isKey()) {
                                        writer.writeCharacters("1");
                                    } else {
                                        writer.writeCharacters("0");
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
                    state.setCredentials(new AuthScope("localhost", 8080), new UsernamePasswordCredentials("administrator", "administrator"));
                    // http://localhost:8080/org.talend.datastewardship-5.1.0-SNAPSHOT/dataloader
                    HttpConnection connection = new HttpConnection("localhost", 8080);
                    connection.open();
                    GetMethod get = new GetMethod("/org.talend.datastewardship-5.1.0-SNAPSHOT/dataloader");
                    get.setQueryString(new NameValuePair[]{
                            new NameValuePair("INPUT_TASKS", output.toString())
                    });
                    get.execute(state, connection);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    output.reset();
                }

            } catch (XMLStreamException e) {
                throw new RuntimeException(e);
            }
        }

        private String getFieldType(FieldMetadata field) {
            if(field instanceof ReferenceFieldMetadata) {
                return "string";
            }

            TypeMetadata currentType = field.getType();
            while(!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(currentType.getNamespace())) {
                currentType = currentType.getSuperTypes().iterator().next();
            }
            if("dateTime".equals(currentType.getName())) {
                return "date";
            } else if("time".equals(currentType.getName())) {
                return "date";
            } else {
                return currentType.getName();
            }
        }

        @Override
        public void end() {

        }

        @Override
        public Closure copy() {
            return new DSCTaskClosure(origin, xmlOutputFactory);
        }
    }
}
