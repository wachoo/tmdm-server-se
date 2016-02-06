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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.search.batchindexing.MassIndexerProgressMonitor;
import org.hibernate.search.batchindexing.ProducerConsumerQueue;


/**
 * created by John on Apr 23, 2015
 * Detailled comment
 *
 */
public class MDMIdentifierProducer implements Runnable {
    
    private final int idFetchSize;

    private static final Logger log = Logger.getLogger(MDMBatchCoordinator.class);

    private final ProducerConsumerQueue<List<Serializable>> destination;
    private final SessionFactory sessionFactory;
    private final int batchSize;
    private final Class<?> indexedType;
    private final MassIndexerProgressMonitor monitor;

    private final long objectsLimit;

    /**
     * @param fromIdentifierListToEntities the target queue where the produced identifiers are sent to
     * @param sessionFactory the Hibernate SessionFactory to use to load entities
     * @param objectLoadingBatchSize affects mostly the next consumer: IdentifierConsumerEntityProducer
     * @param indexedType the entity type to be loaded
     * @param monitor to monitor indexing progress
     * @param objectsLimit if not zero
     */
    public MDMIdentifierProducer(
            ProducerConsumerQueue<List<Serializable>> fromIdentifierListToEntities,
            SessionFactory sessionFactory,
            int objectLoadingBatchSize,
            Class<?> indexedType, MassIndexerProgressMonitor monitor,
            long objectsLimit, int idFetchSize) {
                this.destination = fromIdentifierListToEntities;
                this.sessionFactory = sessionFactory;
                this.batchSize = objectLoadingBatchSize;
                this.indexedType = indexedType;
                this.monitor = monitor;
                this.objectsLimit = objectsLimit;
                this.idFetchSize = idFetchSize;
                log.trace( "created" );
    }
    
    public void run() {
        log.trace( "started" );
        try {
            inTransactionWrapper();
        }
        finally{
            destination.producerStopping();
        }
        log.trace( "finished" );
    }

    private void inTransactionWrapper() {
        StatelessSession session = sessionFactory.openStatelessSession();
        try {
            Transaction transaction = session.beginTransaction();
            loadAllIdentifiers( session );
            transaction.commit();
        } catch (InterruptedException e) {
            // just quit
        }
        finally {
            session.close();
        }
    }

    private void loadAllIdentifiers(final StatelessSession session) throws InterruptedException {
        Number countAsNumber = (Number) session
            .createCriteria( indexedType )
            .setProjection( Projections.rowCount() )
            .setCacheable( false )            
            .uniqueResult();
        long totalCount = countAsNumber.longValue(); 
        if ( objectsLimit != 0 && objectsLimit < totalCount ) {
            totalCount = objectsLimit;
        }
        log.debug( "going to fetch {" + totalCount + "} primary keys");
        monitor.addToTotalCount( totalCount );
        
        Criteria criteria = session
            .createCriteria( indexedType )
            .setProjection( Projections.id() )
            .setCacheable( false )
            .setFetchSize( idFetchSize );
        
        ScrollableResults results = criteria.scroll( ScrollMode.FORWARD_ONLY );
        ArrayList<Serializable> destinationList = new ArrayList<Serializable>( batchSize );
        long counter = 0;
        try {
            while ( results.next() ) {
                Serializable id = (Serializable) results.get( 0 );
                destinationList.add( id );
                if ( destinationList.size() == batchSize ) {
                    enqueueList( destinationList );
                    destinationList = new ArrayList<Serializable>( batchSize ); 
                }
                counter++;
                if ( counter == totalCount ) {
                    break;
                }
            }
        }
        finally {
            results.close();
        }
        enqueueList( destinationList );
    }
    
    private void enqueueList(final List<Serializable> idsList) throws InterruptedException {
        if ( ! idsList.isEmpty() ) {
            destination.put( idsList );
            log.trace( "produced a list of ids { " + idsList  + " }");
        }
    }

}
