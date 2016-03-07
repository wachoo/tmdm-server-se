/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history;

import com.amalto.core.objects.UpdateReportItemPOJO;
import com.amalto.core.objects.UpdateReportPOJO;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 *
 */
public class UpdateReportPOJOParser {
    private static final String NULL = "null";  //$NON-NLS-1$

    private final XMLReader xmlReader;

    private final UpdateReportPOJOHandler handler = new UpdateReportPOJOHandler();

    public UpdateReportPOJOParser() throws SAXException {
        xmlReader = XMLReaderFactory.createXMLReader();
        xmlReader.setContentHandler(handler);
    }

    /**
     * Read an {@link UpdateReportPOJO} from the <code>inputStream</code> parameter. This parsing uses SAX.
     *
     * @param inputStream The input stream that contains the XML form of an UpdateReportPOJO.
     * @return A valid {@link UpdateReportPOJO} instance.
     * @throws IOException  Thrown by SAX.
     * @throws SAXException Thrown by SAX.
     */
    public UpdateReportPOJO parse(InputStream inputStream) throws IOException, SAXException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream argument cannot be null.");
        }

        try {
            xmlReader.parse(new InputSource(inputStream));
            return handler.getParsedUpdateReport();
        } finally {
            // No need to check null input stream (done at beginning).
            inputStream.close();
        }
    }

    private static class UpdateReportPOJOHandler extends DefaultHandler {
        public static final int USER_NAME = 0;
        public static final int SOURCE = 1;
        public static final int TIME = 2;
        public static final int OPERATION_TYPE = 3;
        public static final int DATA_CLUSTER = 5;
        public static final int DATA_MODEL = 6;
        public static final int CONCEPT = 7;
        public static final int KEY = 8;
        public static final int ITEM = 9;
        public static final int PATH = 10;
        public static final int OLD_VALUE = 11;
        public static final int NEW_VALUE = 12;

        public static final String USER_NAME_ELEMENT = "UserName";  //$NON-NLS-1$
        public static final String SOURCE_ELEMENT = "Source";    //$NON-NLS-1$
        public static final String TIME_ELEMENT = "TimeInMillis";    //$NON-NLS-1$
        public static final String OPERATION_TYPE_ELEMENT = "OperationType";   //$NON-NLS-1$
        public static final String DATA_CLUSTER_ELEMENT = "DataCluster"; //$NON-NLS-1$
        public static final String DATA_MODEL_ELEMENT = "DataModel";   //$NON-NLS-1$
        public static final String CONCEPT_ELEMENT = "Concept";   //$NON-NLS-1$
        public static final String KEY_ELEMENT = "Key";   //$NON-NLS-1$
        public static final String ITEM_ELEMENT = "Item"; //$NON-NLS-1$
        public static final String PATH_ELEMENT = "path";  //$NON-NLS-1$
        public static final String OLD_VALUE_ELEMENT = "oldValue";  //$NON-NLS-1$
        public static final String NEW_VALUE_ELEMENT = "newValue";   //$NON-NLS-1$

        private UpdateReportPOJO updateReport;
        private int status;
        private String updateFieldName = ""; //$NON-NLS-1$
        private String updateFieldOldValue = ""; //$NON-NLS-1$
        private String updateFieldNewValue = ""; //$NON-NLS-1$

        @Override
        public void startDocument() throws SAXException {
            updateReport = new UpdateReportPOJO(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY, 0);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (USER_NAME_ELEMENT.equals(localName)) {
                status = USER_NAME;
            } else if (SOURCE_ELEMENT.equals(localName)) {
                status = SOURCE;
            } else if (TIME_ELEMENT.equals(localName)) {
                status = TIME;
            } else if (OPERATION_TYPE_ELEMENT.equals(localName)) {
                status = OPERATION_TYPE;
            } else if (DATA_CLUSTER_ELEMENT.equals(localName)) {
                status = DATA_CLUSTER;
            } else if (DATA_MODEL_ELEMENT.equals(localName)) {
                status = DATA_MODEL;
            } else if (CONCEPT_ELEMENT.equals(localName)) {
                status = CONCEPT;
            } else if (KEY_ELEMENT.equals(localName)) {
                status = KEY;
            } else if (ITEM_ELEMENT.equals(localName)) {
                status = ITEM;
            } else if (PATH_ELEMENT.equals(localName)) {
                status = PATH;
            } else if (OLD_VALUE_ELEMENT.equals(localName)) {
                status = OLD_VALUE;
            } else if (NEW_VALUE_ELEMENT.equals(localName)) {
                status = NEW_VALUE;
            }

            super.startElement(uri, localName, qName, attributes);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            String characters = new String(ch, start, length);
            switch (status) {
                case USER_NAME:
                    updateReport.setUserName(characters);
                    break;
                case SOURCE:
                    updateReport.setSource(characters);
                    break;
                case TIME:
                    updateReport.setTimeInMillis(Long.parseLong(characters));
                    break;
                case OPERATION_TYPE:
                    updateReport.setOperationType(characters);
                    break;
                case DATA_CLUSTER:
                    updateReport.setDataCluster(characters);
                    break;
                case DATA_MODEL:
                    updateReport.setDataModel(characters);
                    break;
                case CONCEPT:
                    updateReport.setConcept(characters);
                    break;
                case KEY:
                    updateReport.setKey(characters);
                    break;
                case ITEM:
                    // Nothing to do
                    break;
                case PATH:
                    updateFieldName += characters;
                    break;
                case OLD_VALUE:
                    updateFieldOldValue += characters;
                    break;
                case NEW_VALUE:
                    updateFieldNewValue += characters;
                    break;
                default:
                    // Ignore everything else
            }

            super.characters(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            status = -1;
            if (ITEM_ELEMENT.equals(localName)) {
                if (NULL.equals(updateFieldOldValue) || StringUtils.EMPTY.equals(updateFieldOldValue)) { //$NON-NLS-1$
                    updateFieldOldValue = null;
                }
                if (NULL.equals(updateFieldNewValue) || StringUtils.EMPTY.equals(updateFieldNewValue)) { //$NON-NLS-1$
                    updateFieldNewValue = null;
                }

                UpdateReportItemPOJO updatedField = new UpdateReportItemPOJO();
                updatedField.setPath(updateFieldName);
                updatedField.setOldValue(updateFieldOldValue);
                updatedField.setNewValue(updateFieldNewValue);
                updateReport.getUpdateReportItemsMap().put(updateFieldName, updatedField);

                updateFieldOldValue = ""; //$NON-NLS-1$
                updateFieldNewValue = ""; //$NON-NLS-1$
                updateFieldName = ""; //$NON-NLS-1$
            }
            super.endElement(uri, localName, qName);
        }

        public UpdateReportPOJO getParsedUpdateReport() {
            // Reorder fields
            Map<String, UpdateReportItemPOJO> unsortedField = updateReport.getUpdateReportItemsMap();
            List<String> fields = new ArrayList<String>(unsortedField.keySet());
            Collections.sort(fields, Collections.reverseOrder());
            LinkedHashMap<String, UpdateReportItemPOJO> sortedFields = new LinkedHashMap<String, UpdateReportItemPOJO>();
            for (String field : fields) {
                sortedFields.put(field, unsortedField.get(field));
            }
            updateReport.setUpdateReportItemsMap(sortedFields);
            return updateReport;
        }
    }

}
