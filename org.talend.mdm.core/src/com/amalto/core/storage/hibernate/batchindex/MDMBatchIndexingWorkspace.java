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
package com.amalto.core.storage.hibernate.batchindex;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.SessionFactory;
import org.hibernate.search.SearchException;
import org.hibernate.search.backend.impl.batchlucene.BatchBackend;
import org.hibernate.search.batchindexing.EntityConsumerLuceneworkProducer;
import org.hibernate.search.batchindexing.Executors;
import org.hibernate.search.batchindexing.IdentifierConsumerEntityProducer;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.batchindexing.ProducerConsumerQueue;
import org.hibernate.search.engine.SearchFactoryImplementor;


/**
 * created by John on Apr 23, 2015
 * Detailled comment
 *
 */
public class MDMBatchIndexingWorkspace implements Runnable {
    
    private final int idFetchSize;
    
    private static final Logger log = Logger.getLogger(MDMBatchCoordinator.class);
    
    private final SearchFactoryImplementor searchFactory;
    private final SessionFactory sessionFactory;
    
    //following order shows the 4 stages of an entity flowing to the index:
    private final ThreadPoolExecutor        execIdentifiersLoader;
    private final ProducerConsumerQueue<List<Serializable>>     fromIdentifierListToEntities;
    private final ThreadPoolExecutor        execFirstLoader;
    private final ProducerConsumerQueue<List<?>>    fromEntityToAddwork;
    private final ThreadPoolExecutor        execDocBuilding;
    
    private final int objectLoadingThreadNum;
    private final int luceneworkerBuildingThreadNum;
    private final Class<?> indexedType;
    
    // status control
    private final CountDownLatch producerEndSignal; //released when we stop adding Documents to Index 
    private final CountDownLatch endAllSignal; //released when we release all locks and IndexWriter
    
    // progress monitor
    private final MassIndexerProgressMonitor monitor;

    // loading options
    private final CacheMode cacheMode;
    private final int objectLoadingBatchSize;

    private final BatchBackend backend;
    
    private final long objectsLimit;

    public MDMBatchIndexingWorkspace(SearchFactoryImplementor searchFactoryImplementor, SessionFactory sessionFactory,
            Class<?> entityType,
            int objectLoadingThreads, int collectionLoadingThreads,
            CacheMode cacheMode, int objectLoadingBatchSize,
            CountDownLatch endAllSignal,
            MassIndexerProgressMonitor monitor, BatchBackend backend,
            long objectsLimit, int idFetchSize) {
        
        this.indexedType = entityType;
        this.searchFactory = searchFactoryImplementor;
        this.sessionFactory = sessionFactory;
        
        //thread pool sizing:
        this.objectLoadingThreadNum = objectLoadingThreads;
        this.luceneworkerBuildingThreadNum = collectionLoadingThreads;//collections are loaded as needed by building the document
        
        //loading options:
        this.cacheMode = cacheMode;
        this.objectLoadingBatchSize = objectLoadingBatchSize;
        this.backend = backend;
        
        //executors: (quite expensive constructor)
        //execIdentifiersLoader has size 1 and is not configurable: ensures the list is consistent as produced by one transaction
        this.execIdentifiersLoader = Executors.newFixedThreadPool( 1, "identifierloader" );
        this.execFirstLoader = Executors.newFixedThreadPool( objectLoadingThreadNum, "entityloader" );
        this.execDocBuilding = Executors.newFixedThreadPool( luceneworkerBuildingThreadNum, "collectionsloader" );
        
        //pipelining queues:
        this.fromIdentifierListToEntities = new ProducerConsumerQueue<List<Serializable>>( 1 );
        this.fromEntityToAddwork = new ProducerConsumerQueue<List<?>>( objectLoadingThreadNum );
        
        //end signal shared with other instances:
        this.endAllSignal = endAllSignal;
        this.producerEndSignal = new CountDownLatch( luceneworkerBuildingThreadNum );
        
        this.monitor = monitor;
        this.objectsLimit = objectsLimit;
        
        this.idFetchSize = idFetchSize;
    }

    public void run() {
        try {
            
            //first start the consumers, then the producers (reverse order):
            for ( int i=0; i < luceneworkerBuildingThreadNum; i++ ) {
            //from entity to LuceneWork:
                execDocBuilding.execute( new EntityConsumerLuceneworkProducer(
                        fromEntityToAddwork, monitor,
                        sessionFactory, producerEndSignal, searchFactory,
                        cacheMode, backend) );
            }
            for ( int i=0; i < objectLoadingThreadNum; i++ ) {
            //from primary key to loaded entity:
                execFirstLoader.execute( new IdentifierConsumerEntityProducer(
                        fromIdentifierListToEntities, fromEntityToAddwork, monitor,
                        sessionFactory, cacheMode, indexedType) );
            }
            //from class definition to all primary keys:
            execIdentifiersLoader.execute( new MDMIdentifierProducer(
                    fromIdentifierListToEntities, sessionFactory,
                    objectLoadingBatchSize, indexedType, monitor,
                    objectsLimit, idFetchSize ) );
            
            //shutdown all executors:
            execIdentifiersLoader.shutdown();
            execFirstLoader.shutdown();
            execDocBuilding.shutdown();
            try {
                producerEndSignal.await(); //await for all work being sent to the backend
                log.debug( "All work for type {} "+ indexedType.getName() + "has been produced");
            } catch (InterruptedException e) {
                //restore interruption signal:
                Thread.currentThread().interrupt();
                throw new SearchException( "Interrupted on batch Indexing; index will be left in unknown state!", e );
            }
        }
        finally {
            endAllSignal.countDown();
        }
    }

}
