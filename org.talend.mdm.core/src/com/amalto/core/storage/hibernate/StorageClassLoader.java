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

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

class StorageClassLoader extends ClassLoader {

    public static final String MAPPING_PUBLIC_ID = "-//Hibernate/Hibernate Mapping DTD 3.0//EN"; //$NON-NLS-1$

    public static final String CONFIGURATION_PUBLIC_ID = "-//Hibernate/Hibernate Configuration DTD 3.0//EN"; //$NON-NLS-1$

    public static final String HIBERNATE_CONFIG = "hibernate.cfg.xml"; //$NON-NLS-1$

    public static final String EHCACHE_XML_CONFIG = "ehcache.xml";

    private static final Logger LOGGER = Logger.getLogger(StorageClassLoader.class);

    private static final String HIBERNATE_CONFIG_TEMPLATE = "hibernate.cfg.template.xml"; //$NON-NLS-1$

    private static final String HIBERNATE_MAPPING = "hibernate.hbm.xml"; //$NON-NLS-1$

    private static final String HIBERNATE_MAPPING_TEMPLATE = "hibernate.hbm.template.xml"; //$NON-NLS-1$

    private static final XPath pathFactory = XPathFactory.newInstance().newXPath();

    private final Map<String, Class<? extends Wrapper>> registeredClasses = new TreeMap<String, Class<? extends Wrapper>>();

    private final Map<String, ComplexTypeMetadata> knownTypes = new HashMap<String, ComplexTypeMetadata>();

    private final TableResolver resolver;

    private final String storageName;

    private RDBMSDataSource dataSource;

    private boolean isClosed;

    public StorageClassLoader(TableResolver resolver, ClassLoader parent, String storageName) {
        super(parent);
        this.resolver = resolver;
        this.storageName = storageName;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        assertNotClosed();
        try {
            if (HIBERNATE_CONFIG.equals(name)) {
                return generateHibernateConfig();
            } else if (HIBERNATE_MAPPING.equals(name)) {
                return generateHibernateMapping();
            } else if (EHCACHE_XML_CONFIG.equals(name)) {
                return StorageClassLoader.class.getResourceAsStream(EHCACHE_XML_CONFIG);
            }
        } catch (Exception e) {
            // Hibernate tends to hide errors when getResourceAsStream fails.
            Logger.getLogger(StorageClassLoader.class).error("Error during dynamic creation of configurations", e);
        }
        return super.getResourceAsStream(name);
    }

    private void assertNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("Class loader was closed.");
        }
    }

    @Override
    public URL getResource(String name) {
        assertNotClosed();
        if (EHCACHE_XML_CONFIG.equals(name)) {
            try {
                final ClassLoader classLoaderForLookup = this;
                return new URL("file", "localhost", 0, EHCACHE_XML_CONFIG, new URLStreamHandler() { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    @Override
                    protected URLConnection openConnection(URL u) throws IOException {
                        return new URLConnection(u) {
                            @Override
                            public void connect() throws IOException {
                            }

                            @Override
                            public InputStream getInputStream() throws IOException {
                                return classLoaderForLookup.getResourceAsStream(EHCACHE_XML_CONFIG); //$NON-NLS-1$
                            }
                        };
                    }
                });
            } catch (MalformedURLException e) {
                return null;
            }
        }
        return super.getResource(name);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        assertNotClosed();
        Class registeredClass = registeredClasses.get(name);
        if (registeredClass != null) {
            return registeredClass;
        }
        return super.findClass(name);
    }

    public ComplexTypeMetadata getTypeFromClass(Class<?> clazz) {
        assertNotClosed();
        // First pass: strict class name equality (don't use isAssignable).
        for (Map.Entry<String, Class<? extends Wrapper>> typeMetadata : registeredClasses.entrySet()) {
            if (typeMetadata.getValue().getName().equals(clazz.getName())) {
                return knownTypes.get(typeMetadata.getKey());
            }
        }
        // In case first pass didn't find anything, try isAssignable.
        for (Map.Entry<String, Class<? extends Wrapper>> typeMetadata : registeredClasses.entrySet()) {
            if (typeMetadata.getValue().isAssignableFrom(clazz)) {
                return knownTypes.get(typeMetadata.getKey());
            }
        }
        throw new IllegalArgumentException("Class '" + clazz.getName() + "' is not registered.");
    }

    public Class<? extends Wrapper> getClassFromType(ComplexTypeMetadata type) {
        assertNotClosed();
        Class<? extends Wrapper> registeredClass = registeredClasses.get(type.getName());
        if (registeredClass != null) {
            return registeredClass;
        }
        throw new IllegalArgumentException("Type '" + type.getName() + "' is not registered.");
    }

    public void register(ComplexTypeMetadata metadata, Class<? extends Wrapper> newClass) {
        assertNotClosed();
        knownTypes.put(metadata.getName(), metadata);
        register(metadata.getName(), newClass);
    }

    public void register(String typeName, Class<? extends Wrapper> newClass) {
        assertNotClosed();
        registeredClasses.put(typeName, newClass);
    }

    private InputStream generateHibernateMapping() {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            documentBuilder.setEntityResolver(HibernateStorage.ENTITY_RESOLVER);
            Document document = documentBuilder.parse(this.getClass().getResourceAsStream(HIBERNATE_MAPPING_TEMPLATE));

            MappingGenerator mappingGenerator = getMappingGenerator(document, resolver);
            for (Map.Entry<String, Class<? extends Wrapper>> classNameToClass : registeredClasses.entrySet()) {
                ComplexTypeMetadata typeMetadata = knownTypes.get(classNameToClass.getKey());
                if (typeMetadata != null) {
                    Element classElement = typeMetadata.accept(mappingGenerator);
                    if (classElement != null) { // Class element might be null if mapping is not applicable for this type
                        document.getDocumentElement().appendChild(classElement);
                    }
                }
            }

            OutputFormat format = new OutputFormat(document);
            StringWriter stringOut = new StringWriter();
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.serialize(document);
            return new ByteArrayInputStream(stringOut.toString().getBytes("UTF-8")); //$NON-NLS-1$
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected MappingGenerator getMappingGenerator(Document document, TableResolver resolver) {
        return new MappingGenerator(document, resolver);
    }

    private InputStream generateHibernateConfig() {
        try {
            Document document = generateHibernateConfiguration(dataSource);
            OutputFormat format = new OutputFormat(document);
            StringWriter stringOut = new StringWriter();
            XMLSerializer serial = new XMLSerializer(stringOut, format);
            serial.serialize(document);
            return new ByteArrayInputStream(stringOut.toString().getBytes("UTF-8")); //$NON-NLS-1$
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Document generateHibernateConfiguration(RDBMSDataSource rdbmsDataSource) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(false);

        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        documentBuilder.setEntityResolver(HibernateStorage.ENTITY_RESOLVER);
        Document document = documentBuilder.parse(StorageClassLoader.class.getResourceAsStream(HIBERNATE_CONFIG_TEMPLATE));

        String connectionUrl = rdbmsDataSource.getConnectionURL();
        String userName = rdbmsDataSource.getUserName();
        String driverClass = rdbmsDataSource.getDriverClassName();
        RDBMSDataSource.DataSourceDialect dialectType = rdbmsDataSource.getDialectName();
        String dialect;
        switch (dialectType) {
            case MYSQL:
                dialect = "org.hibernate.dialect.MySQLDialect"; //$NON-NLS-1$
                break;
            case H2:
                dialect = "org.hibernate.dialect.H2Dialect"; //$NON-NLS-1$
                break;
            case ORACLE_10G:
                dialect = "org.hibernate.dialect.Oracle10gDialect"; //$NON-NLS-1$
                break;
            case SQL_SERVER:
                dialect = "org.hibernate.dialect.SQLServer2008Dialect"; //$NON-NLS-1$
                break;
            default:
                throw new NotImplementedException("No supported dialect type '" + dialectType + "'");
        }
        String password = rdbmsDataSource.getPassword();
        String indexBase = rdbmsDataSource.getIndexDirectory();

        setPropertyValue(document, "hibernate.connection.url", connectionUrl); //$NON-NLS-1$
        setPropertyValue(document, "hibernate.connection.username", userName); //$NON-NLS-1$
        setPropertyValue(document, "hibernate.connection.driver_class", driverClass); //$NON-NLS-1$
        setPropertyValue(document, "hibernate.dialect", dialect); //$NON-NLS-1$
        setPropertyValue(document, "hibernate.connection.password", password); //$NON-NLS-1$

        Node sessionFactoryElement = document.getElementsByTagName("session-factory").item(0); //$NON-NLS-1$
        if (rdbmsDataSource.supportFullText()) {
            addProperty(document, sessionFactoryElement, "hibernate.search.default.directory_provider", "org.hibernate.search.store.FSMasterDirectoryProvider"); //$NON-NLS-1$ //$NON-NLS-2$
            addProperty(document, sessionFactoryElement, "hibernate.search.default.indexBase", indexBase + '/' + storageName); //$NON-NLS-1$
            addProperty(document, sessionFactoryElement, "hibernate.search.default.sourceBase", indexBase + '/' + storageName); //$NON-NLS-1$
            addProperty(document, sessionFactoryElement, "hibernate.search.default.source", indexBase); //$NON-NLS-1$
            addProperty(document, sessionFactoryElement, "hibernate.search.default.exclusive_index_use", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Order of elements highly matters and mapping shall be declared after <property/> and before <event/>.
        Element mapping = document.createElement("mapping"); //$NON-NLS-1$
        Attr resource = document.createAttribute("resource"); //$NON-NLS-1$
        resource.setValue(HIBERNATE_MAPPING);
        mapping.getAttributes().setNamedItem(resource);
        sessionFactoryElement.appendChild(mapping);

        if (rdbmsDataSource.supportFullText()) {
            addEvent(document, sessionFactoryElement, "post-update", "org.hibernate.search.event.FullTextIndexEventListener"); //$NON-NLS-1$ //$NON-NLS-2$
            addEvent(document, sessionFactoryElement, "post-insert", "org.hibernate.search.event.FullTextIndexEventListener"); //$NON-NLS-1$ //$NON-NLS-2$
            addEvent(document, sessionFactoryElement, "post-delete", "org.hibernate.search.event.FullTextIndexEventListener"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            LOGGER.debug("Hibernate configuration does not define full text extensions due to datasource configuration."); //$NON-NLS-1$
        }
        return document;
    }

    private static void addEvent(Document document, Node sessionFactoryElement, String eventType, String listenerClass) {
        Element event = document.createElement("event");
        Attr type = document.createAttribute("type");
        type.setValue(eventType);
        event.getAttributes().setNamedItem(type);
        {
            Element listener = document.createElement("listener");
            Attr clazz = document.createAttribute("class");
            clazz.setValue(listenerClass);
            listener.getAttributes().setNamedItem(clazz);
            event.appendChild(listener);
        }
        sessionFactoryElement.appendChild(event);
    }

    private static void addProperty(Document document, Node sessionFactoryElement, String propertyName, String propertyValue) {
        Element property = document.createElement("property");
        Attr name = document.createAttribute("name");
        name.setValue(propertyName);
        property.getAttributes().setNamedItem(name);
        property.appendChild(document.createTextNode(propertyValue));
        sessionFactoryElement.appendChild(property);
    }

    private static void setPropertyValue(Document document, String propertyName, String value) throws XPathExpressionException {
        XPathExpression compile = pathFactory.compile("hibernate-configuration/session-factory/property[@name='" + propertyName + "']"); //$NON-NLS-1$ //$NON-NLS-2$
        Node node = (Node) compile.evaluate(document, XPathConstants.NODE);
        node.appendChild(document.createTextNode(value));
    }

    public void setDataSourceConfiguration(DataSource dataSource) {
        assertNotClosed();
        if (!(dataSource instanceof RDBMSDataSource)) {
            throw new IllegalArgumentException("Expected an instance of " + RDBMSDataSource.class.getName() + " but was " + dataSource);
        }
        this.dataSource = (RDBMSDataSource) dataSource;
    }

    public void close() {
        if (!isClosed) {
            registeredClasses.clear();
            knownTypes.clear();
            isClosed = true;
        }
    }

    public boolean isClosed() {
        return isClosed;
    }
}
