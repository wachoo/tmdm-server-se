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

import com.amalto.core.metadata.*;
import com.amalto.core.query.optimization.ContainsOptimizer;
import com.amalto.core.query.optimization.Optimizer;
import com.amalto.core.query.optimization.RangeOptimizer;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.Select;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.StorageType;
import com.amalto.core.storage.datasource.DataSource;
import com.amalto.core.storage.datasource.RDBMSDataSource;
import com.amalto.core.storage.prepare.*;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.DataRecordConverter;
import com.amalto.core.storage.record.metadata.DataRecordMetadata;
import net.sf.ehcache.CacheManager;
import org.apache.log4j.Logger;
import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.Search;
import org.hibernate.search.event.ContextHolder;
import org.hibernate.search.impl.SearchFactoryImpl;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class HibernateStorage implements Storage {

    public static final HibernateStorage.LocalEntityResolver ENTITY_RESOLVER = new HibernateStorage.LocalEntityResolver();

    public static final String CLASS_LOADER = "com.amalto.core.storage.hibernate.DefaultStorageClassLoader"; //$NON-NLS-1$

    public static final String ALTERNATE_CLASS_LOADER = "com.amalto.core.storage.hibernate.FullStorageClassLoader"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(HibernateStorage.class);

    private static final Optimizer[] OPTIMIZERS = new Optimizer[]{new RangeOptimizer(), new ContainsOptimizer()};

    private static final String FORBIDDEN_PREFIX = "x_talend_"; //$NON-NLS-1$

    private static final MetadataChecker METADATA_CHECKER = new MetadataChecker();

    // Default value is "true" (meaning the storage will try to create database if it doesn't exist).
    private static final boolean autoPrepare = Boolean.valueOf(MDMConfiguration.getConfiguration().getProperty("db.autoPrepare", "true")); //$NON-NLS-1$ //$NON-NLS-2$

    private final String storageName;

    private final StorageType storageType;

    private MappingRepository mappingRepository;

    private InternalRepository typeMappingRepository;

    private ClassCreator hibernateClassCreator;

    private StorageClassLoader storageClassLoader;

    private boolean isPrepared = false;

    private SessionFactory factory;

    private Configuration configuration;

    private RDBMSDataSource dataSource;

    private MetadataRepository userMetadataRepository;

    /**
     * Create a {@link StorageType#MASTER} storage.
     *
     * @param storageName Name for this storage. <b>by convention</b>, this is the MDM container name.
     * @see StorageType#MASTER
     */
    public HibernateStorage(String storageName) {
        this(storageName, StorageType.MASTER);
    }

    /**
     * @param storageName Name for this storage. <b>By convention</b>, this is the MDM container name.
     * @param type        Tells whether this storage is a staging area or not.
     * @see StorageType
     */
    public HibernateStorage(String storageName, StorageType type) {
        this.storageName = storageName;
        this.storageType = type;
    }

    public void init(DataSource dataSource) {
        // Stateless components
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source named '" + dataSource + "' does not exist.");
        }
        if (!(dataSource instanceof RDBMSDataSource)) {
            throw new IllegalArgumentException("Data source is expected to be a RDBMS data source.");
        }
        this.dataSource = (RDBMSDataSource) dataSource;
        internalInit();
    }

    private void internalInit() {
        if (!dataSource.supportFullText()) {
            LOGGER.warn("Storage '" + storageName + "' (" + storageType + ") is not configured to support full text queries.");
        }
        configuration = new Configuration();
        // Setting our own entity resolver allows to ensure the DTD found/used are what we expect (and not potentially ones
        // provided by the application server).
        configuration.setEntityResolver(ENTITY_RESOLVER);
    }

    public synchronized void prepare(MetadataRepository repository, Set<FieldMetadata> indexedFields, boolean force, boolean dropExistingData) {
        if (!force && isPrepared) {
            return; // No op operation
        }
        if (isPrepared) {
            close();
            internalInit();
        }
        // Create class loader for storage's dynamically created classes.
        ClassLoader contextClassLoader = HibernateStorage.class.getClassLoader();
        Class<? extends StorageClassLoader> clazz;
        try {
            try {
                clazz = (Class<? extends StorageClassLoader>) Class.forName(ALTERNATE_CLASS_LOADER);
            } catch (ClassNotFoundException e) {
                clazz = (Class<? extends StorageClassLoader>) Class.forName(CLASS_LOADER);
            }
            Constructor<? extends StorageClassLoader> constructor = clazz.getConstructor(ClassLoader.class, String.class, StorageType.class);
            storageClassLoader = constructor.newInstance(contextClassLoader, storageName, storageType);
            storageClassLoader.setDataSourceConfiguration(dataSource);
            storageClassLoader.generateHibernateConfig(); // Checks if configuration can be generated.
        } catch (Exception e) {
            throw new RuntimeException("Could not create storage class loader", e);
        }
        if (dropExistingData) {
            LOGGER.info("Cleaning existing database content.");
            StorageCleaner cleaner = new JDBCStorageCleaner(new FullTextIndexCleaner());
            cleaner.clean(this);
        } else {
            LOGGER.info("*NOT* cleaning existing database content.");
        }
        if (autoPrepare) {
            LOGGER.info("Preparing database before schema generation.");
            StorageInitializer initializer = new JDBCStorageInitializer();
            if (!initializer.isInitialized(this)) {
                initializer.initialize(this);
            } else {
                LOGGER.info("Database is already prepared.");
            }
        } else {
            LOGGER.info("*NOT* preparing database before schema generation.");
        }
        // No support for data models including inheritance AND for g* XSD simple types AND fields that start with X_TALEND_
        try {
            MetadataUtils.sortTypes(repository); // Do a "sort" to ensure there's no cyclic dependency.
            repository.accept(METADATA_CHECKER);
            userMetadataRepository = repository;
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during unsupported features check.", e);
        }
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);
            // Mapping of data model types to RDBMS (i.e. 'flatten' representation of types).
            MetadataRepository internalRepository;
            try {
                InternalRepository typeEnhancer = getTypeEnhancer();
                internalRepository = userMetadataRepository.accept(typeEnhancer);
                mappingRepository = typeEnhancer.getMappings();
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during type mapping creation.", e);
            }
            // Set MDM type to database resolver.
            Set<FieldMetadata> databaseIndexedFields = new HashSet<FieldMetadata>();
            for (FieldMetadata indexedField : indexedFields) {
                TypeMapping mapping = mappingRepository.getMappingFromUser(indexedField.getContainingType());
                databaseIndexedFields.add(mapping.getDatabase(indexedField));
            }
            TableResolver tableResolver = new StorageTableResolver(databaseIndexedFields);
            storageClassLoader.setTableResolver(tableResolver);
            // Master and Staging share same class creator.
            switch (storageType) {
                case MASTER:
                case STAGING:
                    hibernateClassCreator = new ClassCreator(storageClassLoader);
                    break;
            }
            // Create Hibernate classes (after some modifications to the types).
            try {
                internalRepository.accept(hibernateClassCreator);
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during dynamic classes creation.", e);
            }
            // Last step: configuration of Hibernate
            try {
                // Hibernate needs to have dynamic classes in context class loader during configuration.
                InputStream ehCacheConfig = storageClassLoader.getResourceAsStream(StorageClassLoader.EHCACHE_XML_CONFIG);
                if (ehCacheConfig != null) {
                    CacheManager.create(ehCacheConfig);
                }
                configuration.configure(StorageClassLoader.HIBERNATE_CONFIG);
                // This method is deprecated but using a 4.1+ hibernate initialization, Hibernate Search can't be started
                // (wait for Hibernate Search 4.1 to be ready before considering changing this).
                factory = configuration.buildSessionFactory();
            } catch (Exception e) {
                throw new RuntimeException("Exception occurred during Hibernate initialization.", e);
            }

            // All set: set prepared flag to true.
            isPrepared = true;
            LOGGER.info("Storage '" + storageName + "' (" + storageType + ") is ready.");
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }

    private InternalRepository getTypeEnhancer() {
        if (typeMappingRepository == null) {
            switch (storageType) {
                case MASTER:
                    typeMappingRepository = new UserTypeMappingRepository();
                    break;
                case STAGING:
                    typeMappingRepository = new StagingTypeMappingRepository();
                    break;
                default:
                    throw new IllegalArgumentException("Storage type '" + storageType + "' is not supported.");
            }
        }
        return typeMappingRepository;
    }

    public synchronized void prepare(MetadataRepository repository, boolean dropExistingData) {
        if (!isPrepared) {
            prepare(repository, Collections.<FieldMetadata>emptySet(), false, dropExistingData);
        }
    }

    @Override
    public MetadataRepository getMetadataRepository() {
        if (!isPrepared) {
            throw new IllegalStateException("Storage '" + storageName + "' has not been prepared.");
        }
        return userMetadataRepository;
    }

    public StorageResults fetch(Expression userQuery) {
        assertPrepared();

        final ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(storageClassLoader);

        final Session session = factory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            // Implicitly start a transaction
            transaction.begin();
        }
        // Call back closes session once calling code has consumed all results.
        Set<EndOfResultsCallback> callbacks = Collections.<EndOfResultsCallback>singleton(new EndOfResultsCallback() {
            public void onEndOfResults() {
                if (session.isOpen()) { // Prevent any problem if anyone (Hibernate...) already closed session.
                    session.getTransaction().commit();
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Attempted to close session on end of query result, but it has already been done.");
                    }
                }
                Thread.currentThread().setContextClassLoader(previousClassLoader);
            }
        });

        try {
            return internalFetch(session, userQuery, callbacks);
        } catch (Exception e) {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
            throw new RuntimeException("Exception occurred during fetch operation", e);
        }
    }

    public void update(DataRecord record) {
        update(Collections.singleton(record));
    }

    public void update(Iterable<DataRecord> records) {
        assertPrepared();
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);

            Session session = factory.getCurrentSession();
            DataRecordConverter<Object> converter = new ObjectDataRecordConverter(storageClassLoader, session);
            for (DataRecord currentDataRecord : records) {
                TypeMapping mapping = mappingRepository.getMappingFromUser(currentDataRecord.getType());
                Wrapper o = (Wrapper) currentDataRecord.convert(converter, mapping);
                o.timestamp(System.currentTimeMillis());
                o.revision(currentDataRecord.getRevisionId());

                DataRecordMetadata recordMetadata = currentDataRecord.getRecordMetadata();
                o.taskId(recordMetadata.getTaskId());
                Map<String, String> recordProperties = recordMetadata.getRecordProperties();
                for (Map.Entry<String, String> currentProperty : recordProperties.entrySet()) {
                    String key = currentProperty.getKey();
                    String value = currentProperty.getValue();
                    ComplexTypeMetadata database = mapping.getDatabase();
                    if (database.hasField(key)) {
                        Object convertedValue = MetadataUtils.convert(value, database.getField(key));
                        o.set(key, convertedValue);
                    } else {
                        throw new IllegalArgumentException("Can not store value '" + key + "' because there is no database field '" + key + "' in type '" + mapping.getName() + "'");
                    }
                }
                session.saveOrUpdate(o);
            }
        } catch(ConstraintViolationException e) {
            throw new com.amalto.core.storage.exception.ConstraintViolationException(e);
        } catch (PropertyValueException e) {
            throw new RuntimeException("Invalid value in record to update.", e);
        } catch (NonUniqueObjectException e) {
            throw new RuntimeException("Attempted to update multiple times same record within same transaction.", e);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during update.", e);
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }
    }

    public void begin() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        session.beginTransaction();
        session.setFlushMode(FlushMode.MANUAL);
    }

    public void commit() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("[" + this + "] Transaction #" + transaction.hashCode() + " -> Commit " + session.getStatistics().getEntityCount() + " record(s).");
        }
        if (!transaction.isActive()) {
            throw new IllegalStateException("Can not commit transaction, no transaction is active.");
        }
        session.flush();
        if (!transaction.wasCommitted()) {
            transaction.commit();
        } else {
            LOGGER.warn("Transaction was already committed.");
        }

    }

    public void rollback() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        Transaction transaction = session.getTransaction();
        if (!transaction.isActive()) {
            LOGGER.warn("Can not rollback transaction, no transaction is active.");
            return;
        }
        session.clear();
        if (!transaction.wasRolledBack()) {
            transaction.rollback();
        } else {
            LOGGER.warn("Transaction was already rollbacked.");
        }
    }

    public synchronized void end() {
        assertPrepared();
        Session session = factory.getCurrentSession();
        if (session.getTransaction().isActive()) {
            LOGGER.warn("Current session has not been ended by either a commit or a rollback. Rolling back transaction.");
            session.getTransaction().rollback();
            if (session.isOpen()) {
                session.close();
                session.disconnect();
            }
        }
    }

    public void reindex() {
        Session session = factory.getCurrentSession();

        MassIndexer indexer = Search.getFullTextSession(session).createIndexer();
        indexer.optimizeOnFinish(true);
        indexer.optimizeAfterPurge(true);
        try {
            session.getTransaction().begin();
            indexer.startAndWait();
            session.getTransaction().commit();
        } catch (InterruptedException e) {
            session.getTransaction().rollback();
            throw new RuntimeException(e);
        }
    }

    public Set<String> getFullTextSuggestion(String keyword, FullTextSuggestion mode, int suggestionSize) {
        // TODO Need Lucene 3.0+ to implement this.
        /*
        FullTextSession fullTextSession = Search.getFullTextSession(factory.getCurrentSession());
        SearchFactory searchFactory = fullTextSession.getSearchFactory();

        Collection<ComplexTypeMetadata> complexTypes = internalRepository.getUserComplexTypes();
        Set<String> fields = new HashSet<String>();
        List<DirectoryProvider> directoryProviders = new LinkedList<DirectoryProvider>();
        for (ComplexTypeMetadata complexType : complexTypes) {
            for (FieldMetadata fieldMetadata : complexType.getFields()) {
                fields.add(fieldMetadata.getName());
            }
            Class<?> generatedClass = storageClassLoader.getClassFromType(complexType);
            DirectoryProvider[] providers = searchFactory.getDirectoryProviders(generatedClass);
            Collections.addAll(directoryProviders, providers);
        }

        DirectoryProvider[] providers = directoryProviders.toArray(new DirectoryProvider[directoryProviders.size()]);
        IndexReader reader = searchFactory.getReaderProvider().openReader(providers);

        try {
            switch (mode) {
                case START:
                    try {
                        IndexSearcher searcher = new IndexSearcher(reader);

                        String[] fieldsAsArray = fields.toArray(new String[fields.size()]);
                        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, fieldsAsArray, new KeywordAnalyzer());
                        StringBuilder queryBuffer = new StringBuilder();
                        Iterator<String> fieldsIterator = fields.iterator();
                        while (fieldsIterator.hasNext()) {
                            queryBuffer.append(fieldsIterator.next()).append(':').append(keyword).append("*");
                            if (fieldsIterator.hasNext()) {
                                queryBuffer.append(" OR ");
                            }
                        }
                        org.apache.lucene.search.Query query = parser.parse(queryBuffer.toString());

                        MatchedWordsCollector collector = new MatchedWordsCollector(reader);
                        searcher.search(query, collector);
                        return collector.getMatchedWords();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                case ALTERNATE:
                    try {
                        IndexSearcher searcher = new IndexSearcher(reader);

                        String[] fieldsAsArray = fields.toArray(new String[fields.size()]);
                        BooleanQuery query = new BooleanQuery();
                        for (String field : fieldsAsArray) {
                            FuzzyQuery fieldQuery = new FuzzyQuery(new Term(field, '~' + keyword));
                            query.add(fieldQuery, BooleanClause.Occur.SHOULD);
                        }

                        MatchedWordsCollector collector = new MatchedWordsCollector(reader);
                        searcher.search(query, collector);
                        return collector.getMatchedWords();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                default:
                    throw new NotImplementedException("No support for suggestion mode '" + mode + "'");
            }
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                LOGGER.error("Exception occurred during full text suggestion searches.", e);
            }
        }
        */
        throw new UnsupportedOperationException("No support due to version of Lucene in use.");
    }

    public String getName() {
        return storageName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void delete(Expression userQuery) {
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);
            Session session = factory.getCurrentSession();

            Iterable<DataRecord> records = internalFetch(session, userQuery, Collections.<EndOfResultsCallback>emptySet());
            for (DataRecord currentDataRecord : records) {
                ComplexTypeMetadata currentType = currentDataRecord.getType();
                Class<?> clazz = storageClassLoader.getClassFromType(currentType);

                Serializable idValue;
                List<FieldMetadata> keyFields = currentType.getKeyFields();
                if (keyFields.size() == 1) {
                    idValue = (Serializable) currentDataRecord.get(keyFields.get(0));
                } else {
                    List<Object> compositeIdValues = new LinkedList<Object>();
                    for (FieldMetadata keyField : keyFields) {
                        compositeIdValues.add(currentDataRecord.get(keyField));
                    }
                    idValue = ObjectDataRecordConverter.createCompositeId(storageClassLoader, clazz, compositeIdValues);
                }

                Object object = session.get(clazz, idValue);
                if (object != null) {
                    session.delete(object);
                } else {
                    LOGGER.warn("Instance of type '" + currentType.getName() + "' and ID '" + idValue.toString() + "' has already been deleted within same transaction.");
                }
            }
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }
    }

    public synchronized void close() {
        LOGGER.info("Closing storage '" + storageName + "' (" + storageType + ").");
        ClassLoader previousClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(storageClassLoader);
            try {
                // Hack to prevent Hibernate Search to cause ConcurrentModificationException
                try {
                    Field contexts = ContextHolder.class.getDeclaredField("contexts"); //$NON-NLS-1$
                    contexts.setAccessible(true); // 'contexts' field is private.
                    ThreadLocal<WeakHashMap<Configuration, SearchFactoryImpl>> contextsPerThread = (ThreadLocal<WeakHashMap<Configuration, SearchFactoryImpl>>) contexts.get(null);
                    WeakHashMap<Configuration, SearchFactoryImpl> contextMap = contextsPerThread.get();
                    if (contextMap != null) {
                        contextMap.remove(configuration);
                    }
                } catch (Exception e) {
                    LOGGER.error("Exception occurred during Hibernate Search clean up.", e);
                }

                if (factory != null) {
                    factory.close();
                    factory = null; // close() documentation advises to remove all references to SessionFactory.
                }
            } finally {
                if (storageClassLoader != null) {
                    storageClassLoader.close();
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(previousClassLoader);
        }

        // Reset caches
        ListIterator.resetTypeReaders();
        ScrollableIterator.resetTypeReaders();

        LOGGER.info("done.");
    }

    private StorageResults internalFetch(Session session, Expression userQuery, Set<EndOfResultsCallback> callbacks) {
        SelectAnalyzer selectAnalysis = new SelectAnalyzer(mappingRepository, storageClassLoader, session, callbacks, this);
        AbstractQueryHandler queryHandler = userQuery.accept(selectAnalysis);
        // Always normalize the query to ensure query has expected format.
        Expression expression = userQuery.normalize();
        for (Optimizer optimizer : OPTIMIZERS) {
            optimizer.optimize((Select) expression);
        }
        return expression.accept(queryHandler);
    }

    private void assertPrepared() {
        if (!isPrepared) {
            throw new IllegalStateException("Storage has not been prepared.");
        }
        if (storageClassLoader.isClosed()) {
            throw new IllegalStateException("Storage has been closed.");
        }
    }

    private static class MetadataChecker extends DefaultMetadataVisitor<Object> {
        @Override
        public Object visit(SimpleTypeFieldMetadata simpleField) {
            String simpleFieldTypeName = simpleField.getType().getName();
            if ("gYearMonth".equals(simpleFieldTypeName)  //$NON-NLS-1$
                    || "gYear".equals(simpleFieldTypeName)   //$NON-NLS-1$
                    || "gMonthDay".equals(simpleFieldTypeName) //$NON-NLS-1$
                    || "gDay".equals(simpleFieldTypeName)  //$NON-NLS-1$
                    || "gMonth".equals(simpleFieldTypeName)) {  //$NON-NLS-1$
                throw new IllegalArgumentException("No support for field type '" + simpleFieldTypeName + "' (field '" + simpleField.getName() + "' of type '" + simpleField.getContainingType().getName() + "').");
            }
            assertField(simpleField);
            return super.visit(simpleField);
        }

        @Override
        public Object visit(ReferenceFieldMetadata referenceField) {
            assertField(referenceField);
            return super.visit(referenceField);
        }

        @Override
        public Object visit(ContainedTypeFieldMetadata containedField) {
            assertField(containedField);
            return super.visit(containedField);
        }

        @Override
        public Object visit(EnumerationFieldMetadata enumField) {
            assertField(enumField);
            return super.visit(enumField);
        }

        private static void assertField(FieldMetadata field) {
            if (field.getName().toLowerCase().startsWith(FORBIDDEN_PREFIX)) {
                throw new IllegalArgumentException("Field '" + field.getName() + "' of type '" + field.getContainingType().getName() + "' is not allowed to start with " + FORBIDDEN_PREFIX);
            }
        }
    }

    private static class LocalEntityResolver implements EntityResolver {
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            if (StorageClassLoader.CONFIGURATION_PUBLIC_ID.equals(publicId)) {
                InputStream resourceAsStream = HibernateStorage.class.getResourceAsStream("hibernate.cfg.dtd"); //$NON-NLS-1$
                if (resourceAsStream == null) {
                    throw new IllegalStateException("Expected class path to contain Hibernate configuration DTD.");
                }
                return new InputSource(resourceAsStream);
            } else if (StorageClassLoader.MAPPING_PUBLIC_ID.equals(publicId)) {
                InputStream resourceAsStream = HibernateStorage.class.getResourceAsStream("hibernate.hbm.dtd"); //$NON-NLS-1$
                if (resourceAsStream == null) {
                    throw new IllegalStateException("Expected class path to contain Hibernate mapping DTD.");
                }
                return new InputSource(resourceAsStream);
            }
            return null;
        }
    }

    @Override
    public String toString() {
        return storageName + '(' + storageType + ')';
    }

    public static enum TypeMappingStrategy {
        FLAT,
        SCATTERED,
        AUTO
    }
}