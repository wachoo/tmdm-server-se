// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.CacheMode;
import org.hibernate.SessionFactory;
import org.hibernate.search.backend.OptimizeLuceneWork;
import org.hibernate.search.backend.PurgeAllLuceneWork;
import org.hibernate.search.backend.impl.batchlucene.BatchBackend;
import org.hibernate.search.batchindexing.Executors;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.engine.SearchFactoryImplementor;

import com.amalto.core.storage.hibernate.HibernateStorage;


/**
 * created by John on Apr 23, 2015
 * Detailled comment
 *
 */
public class MDMBatchCoordinator implements Runnable {
    
    private final int idFetchSize;
    
    private static final Logger log = Logger.getLogger(MDMBatchCoordinator.class);
    
    private final Class<?>[] rootEntities; //entity types to reindex exluding all subtypes of each-other
    private final SearchFactoryImplementor searchFactoryImplementor;
    private final SessionFactory sessionFactory;
    private final int objectLoadingThreads;
    private final int collectionLoadingThreads;
    private final CacheMode cacheMode;
    private final int objectLoadingBatchSize;
    private final boolean optimizeAtEnd;
    private final boolean purgeAtStart;
    private final boolean optimizeAfterPurge;
    private final CountDownLatch endAllSignal;
    private final MassIndexerProgressMonitor monitor;
    private final long objectsLimit;
    
    private BatchBackend backend;

    public MDMBatchCoordinator(Set<Class<?>> rootEntities,
            SearchFactoryImplementor searchFactoryImplementor,
            SessionFactory sessionFactory, int objectLoadingThreads,
            int collectionLoadingThreads, CacheMode cacheMode,
            int objectLoadingBatchSize, long objectsLimit,
            boolean optimizeAtEnd,
            boolean purgeAtStart, boolean optimizeAfterPurge,
            MassIndexerProgressMonitor monitor, int idFetchSize) {
                this.rootEntities = rootEntities.toArray( new Class<?>[ rootEntities.size() ] );
                this.searchFactoryImplementor = searchFactoryImplementor;
                this.sessionFactory = sessionFactory;
                this.objectLoadingThreads = objectLoadingThreads;
                this.collectionLoadingThreads = collectionLoadingThreads;
                this.cacheMode = cacheMode;
                this.objectLoadingBatchSize = objectLoadingBatchSize;
                this.optimizeAtEnd = optimizeAtEnd;
                this.purgeAtStart = purgeAtStart;
                this.optimizeAfterPurge = optimizeAfterPurge;
                this.monitor = monitor;
                this.objectsLimit = objectsLimit;
                this.endAllSignal = new CountDownLatch( rootEntities.size() );
                this.idFetchSize = idFetchSize;
    }

    public void run() {
        backend = searchFactoryImplementor.makeBatchBackend( monitor );
        try {
            beforeBatch(); // purgeAll and pre-optimize activities
            doBatchWork();
            backend.stopAndFlush( 60L*60*24, TimeUnit.SECONDS ); //1 day : enough to flush to indexes?
//          backend.stopAndFlush( 10, TimeUnit.SECONDS );
            afterBatch();
        } catch (InterruptedException e) {
            log.error( "Batch indexing was interrupted" );
            Thread.currentThread().interrupt();
        }
        finally {
            backend.close();
        }
    }

    /**
     * Will spawn a thread for each type in rootEntities, they will all re-join
     * on endAllSignal when finished.
     * @throws InterruptedException if interrupted while waiting for endAllSignal.
     */
    private void doBatchWork() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool( rootEntities.length, "BatchIndexingWorkspace" );
        for ( Class<?> type : rootEntities ) {
            executor.execute( new MDMBatchIndexingWorkspace(
                    searchFactoryImplementor, sessionFactory, type,
                    objectLoadingThreads, collectionLoadingThreads,
                    cacheMode, objectLoadingBatchSize,
                    endAllSignal, monitor, backend, objectsLimit, idFetchSize ) );
        }
        executor.shutdown();
        endAllSignal.await(); //waits for the executor to finish
    }

    /**
     * Operations to do after all subthreads finished their work on index
     */
    private void afterBatch() {
        if ( this.optimizeAtEnd ) {
            Set<Class<?>> targetedClasses = searchFactoryImplementor.getIndexedTypesPolymorphic( rootEntities );
            optimize( targetedClasses );
        }
    }

    /**
     * Optional operations to do before the multiple-threads start indexing
     */
    private void beforeBatch() {
        if ( this.purgeAtStart ) {
            //purgeAll for affected entities
            Set<Class<?>> targetedClasses = searchFactoryImplementor.getIndexedTypesPolymorphic( rootEntities );
            for ( Class<?> clazz : targetedClasses ) {
                //needs do be in-sync work to make sure we wait for the end of it.
                backend.doWorkInSync( new PurgeAllLuceneWork( clazz ) ); 
            }
            if ( this.optimizeAfterPurge ) {
                optimize( targetedClasses );
            }
        }
    }

    private void optimize(Set<Class<?>> targetedClasses) {
        for ( Class<?> clazz : targetedClasses ) {
            //TODO the backend should remove duplicate optimize work to the same DP (as entities might share indexes)
            backend.doWorkInSync( new OptimizeLuceneWork( clazz ) );
        }
    }
    

}
