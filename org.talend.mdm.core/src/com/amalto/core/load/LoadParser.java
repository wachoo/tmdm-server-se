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

package com.amalto.core.load;

import com.amalto.core.load.context.AutoGenStateContext;
import com.amalto.core.load.context.DefaultStateContext;
import com.amalto.core.load.context.StateContext;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;

/**
 * <p>
 * A parser that reads XML from input stream and transform it into XML documents
 * that can be stored in MDM.
 * </p>
 * <p>
 * This parser reads "full" XML documents, and XML document must be valid. In case the XML stream contains :
 * <br/>
 * &lt;doc&gt;<br/>
 * ...<br/>
 * &lt;/doc&gt;<br/>
 * &lt;doc&gt;<br/>
 * ...<br/>
 * &lt;/doc&gt;<br/>
 * <br/>
 * Consider using {@link com.amalto.core.load.io.XMLRootInputStream} to wrap the XML fragments.
 * </p>
 * <p>
 * <b>Note:</b> This class is thread-safe.
 * </p>
 */
public class LoadParser {
    private static final XMLInputFactory inputFactory = XMLInputFactory.newFactory();
    private static final Logger log = Logger.getLogger(LoadParser.class);

    static {
        inputFactory.setProperty(XMLInputFactory.IS_VALIDATING, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.FALSE);
        inputFactory.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
    }

    private LoadParser() {
    }

    /**
     * <p>
     * Parse XML from the input stream and call back once data is ready to persisted in MDM.
     * </p>
     * <p>
     * This method ends once the input stream is exhausted.
     * </p>
     *
     * @param inputStream        The input stream to the XML fragments.
     * @param config             Configuration of the LoadParser.
     * @param callback           The callback called when a document is ready to be persisted in MDM.
     */
    public static void parse(InputStream inputStream, Configuration config, LoadParserCallback callback) {
        parse(inputStream, config, Constants.DEFAULT_PARSER_LIMIT, callback);
    }

    /**
     * <p>
     * Parse XML from the input stream and call back once data is ready to persisted in MDM.
     * </p>
     * <p>
     * This method ends once the input stream is exhausted <b>OR</b> when the number of callback has reached
     * <code>limit</code>.
     * </p>
     *
     * @param inputStream        The input stream to the XML fragments.
     * @param config             Configuration of the LoadParser.
     * @param limit              A limit for documents to persist. Once this limit is reached, this method ends.
     * @param callback           The callback called when a document is ready to be persisted in MDM.
     */
    public static void parse(InputStream inputStream, Configuration config, int limit, LoadParserCallback callback) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream cannot be null");
        }
        if (callback == null) {
            throw new IllegalArgumentException("LoadParser callback cannot be null");
        }
        if (config == null) {
            throw new IllegalArgumentException("Configuration cannot be null");
        }

        XMLStreamReader reader = null;
        try {
            reader = inputFactory.createXMLStreamReader(inputStream);

            StateContext context = new DefaultStateContext(config.getPayLoadElementName(),
                    config.getIdPaths(),
                    config.getDataClusterName(),
                    limit,
                    callback);
            if (config.isAutoGenPK()) {
                // Change the context to auto-generate metadata
                context = AutoGenStateContext.decorate(context);
            }

            while (!context.hasFinished()) {
                context.parse(reader);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (XMLStreamException e) {
                log.error("Exception on close reader.", e);
            }
        }
    }

    public static class Configuration {
        private final String payLoadElementName;
        private final String[] idPaths;
        private final boolean autoGenPK;
        private String dataClusterName;

        public Configuration(String payLoadElementName, String[] idPaths, boolean autoGenPK, String dataClusterName) {
            this.payLoadElementName = payLoadElementName;
            this.idPaths = idPaths;
            this.autoGenPK = autoGenPK;
            this.dataClusterName = dataClusterName;
        }

        /**
         * @return the local name of the XML element to look for while parsing (e.g. if top level XML element for TypeA
         *         is 'typeA', pass 'typeA' as parameter.
         */
        public String getPayLoadElementName() {
            return payLoadElementName;
        }

        /**
         * @return The XPaths expressions to evaluate when build ID for the document. Array allows to pass several
         *         XPaths for ID composed of 1+ values.
         */
        public String[] getIdPaths() {
            return idPaths;
        }

        public boolean isAutoGenPK() {
            return autoGenPK;
        }

        public String getDataClusterName() {
            return dataClusterName;
        }

    }
}
