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
package com.amalto.core.query;

import java.sql.Timestamp;
import java.util.Date;

import junit.framework.Assert;

import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.server.ServerContext;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.DataRecordMetadataImpl;
import com.amalto.core.storage.transaction.Transaction;
import com.amalto.core.storage.transaction.Transaction.Lifetime;
import com.amalto.core.storage.transaction.TransactionManager;

/**
 * Test transactions behaviour with Hibernate
 */
public class LongTransactionTest extends StorageTestCase {

    @Override
    public void setUp() throws Exception {
        // override to avoid storage.begin in superclass setup
    }
    
    @Override
    public void tearDown() throws Exception{
        storage.begin();
        storage.delete(UserQueryBuilder.from(country).getSelect());
        storage.commit();
        storage.end();
    }
    
    /**
     * Tests that rows updated within a long transaction are not visible
     * to others as long as the transaction is not committed
     */
    public void testLongTransactionIsolation() throws Exception {
        Assert.assertEquals(0, countCountries());
        TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = tm.create(Lifetime.LONG);
        transaction.include(storage);
        transaction.begin();

        transaction.begin(); // this should have no effect
        storage.begin(); // this should have no effect

        // create record
        storage.update(createDataRecord(1));
        
        storage.commit(); // this should have no effect
        
        // launch a new thread and count data records
        // should be 0 because the main long transaction has not committed yet
        Assert.assertEquals(0, countCountriesInAnotherThread());
        
        // create another record
        storage.update(createDataRecord(2));
        
        Assert.assertEquals(0, countCountriesInAnotherThread());
        
        // commit long transaction
        transaction.commit();
        
        // check data is visible from another thread
        Assert.assertEquals(2, countCountriesInAnotherThread());
    }
    
    /**
     * Tests it is possible to make changes in an AD_HOC transaction, commit it
     * and then start a long transaction that will behave correctly 
     * 
     * Expected behavior: both transactions are isolated and work correctly
     */
    public void testAdHocTransactionCommittedThenLong() throws Exception {
        Assert.assertEquals(0, countCountries());
        storage.begin(); // this will implicitly create an AD_HOC transaction
        storage.update(createDataRecord(1));
        storage.commit();
        storage.end();
        Assert.assertEquals(1, countCountries());
        
        TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = tm.create(Lifetime.LONG);
        transaction.include(storage);
        transaction.begin();
        storage.update(createDataRecord(2));
        Assert.assertEquals(1, countCountriesInAnotherThread());
        transaction.commit();
        Assert.assertEquals(2, countCountriesInAnotherThread());
    }
    
    /**
     * Tests it is possible the make changes in a long transaction, commit it
     * and then make changes in an AD_HOC transaction.
     * 
     * Expected behavior: both transactions are isolated and work correctly
     * 
     * @throws Exception
     */
    public void testLongTransactionCommittedThenAdHoc() throws Exception {
        Assert.assertEquals(0, countCountries());
        TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = tm.create(Lifetime.LONG);
        transaction.include(storage);
        transaction.begin();
        storage.update(createDataRecord(1));
        Assert.assertEquals(0, countCountriesInAnotherThread());
        transaction.commit();
        Assert.assertEquals(1, countCountriesInAnotherThread());
        
        storage.begin();
        storage.update(createDataRecord(2));
        storage.commit();
        storage.end();
        Assert.assertEquals(2, countCountries());
    }
    
    /**
     * Creates a long transaction begin it, do updates and create 
     * a new implicit AD_HOC transaction at storage level using {@link Storage#begin()}, make new changes
     * commit at storage level and do other changes and commit the long transaction.
     * 
     * Expected behavior: The AD_HOC transaction should be committed when {@link Storage#commit} is called
     * and the LONG transaction should be committed when {@link Transaction#commit()} is called
     * 
     * @throws Exception
     */
    public void testNestingImplicitAdHocInLongTransaction() throws Exception {
        Assert.assertEquals(0, countCountries());
        TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = tm.create(Lifetime.LONG);
        transaction.include(storage);
        transaction.begin();
        storage.update(createDataRecord(1));
        Assert.assertEquals(0, countCountriesInAnotherThread());
        
        // now perform changes in an AD_HOC transaction
        storage.begin();
        storage.update(createDataRecord(2));
        storage.commit();
        storage.end();
        Assert.assertEquals(0, countCountriesInAnotherThread());
        
        storage.update(createDataRecord(3));
        
        Assert.assertEquals(0, countCountriesInAnotherThread());
        
        transaction.commit();
        Assert.assertEquals(3, countCountriesInAnotherThread());
    }
    
    /**
     * Creates a long transaction begin it, do updates and create explicitly a new
     * AD_HOC transaction using {@link TransactionManager#create(Lifetime)} make new changes
     * commit the AD_HOC transaction, do other changes and commit the long transaction.
     * 
     * Expected behavior: same as above
     *  
     * @throws Exception
     */
    public void testNestingExplicitAdHocInLongTransaction() throws Exception {
        Assert.assertEquals(0, countCountries());
        TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction transaction = tm.create(Lifetime.LONG);
        transaction.include(storage);
        transaction.begin();
        storage.update(createDataRecord(1));
        Assert.assertEquals(0, countCountriesInAnotherThread());
        
        Transaction adHoc = tm.create(Lifetime.AD_HOC);
        adHoc.include(storage);
        adHoc.begin();
        storage.update(createDataRecord(2));
        adHoc.commit();
        Assert.assertEquals(1, countCountriesInAnotherThread());
        
        storage.update(createDataRecord(3));
        
        transaction.commit();
        
        Assert.assertEquals(3, countCountriesInAnotherThread());
    }
    
    /**
     * Creates an AD_HOC implicitly using {@link Storage#begin()}, do updates and create explicitly a new
     * LONG transaction using {@link TransactionManager#create(Lifetime)} make new changes
     * commit the LONG transaction, do other changes and commit the AD_HOC transaction using {@link Storage#commit}.
     * 
     * Expected behavior: same as above
     * 
     * @throws Exception
     */
    public void testNestingLongInAdHocTransaction() throws Exception {
        Assert.assertEquals(0, countCountries());
        TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
        
        storage.begin();
        storage.update(createDataRecord(1));

        Transaction transaction = tm.create(Lifetime.LONG);
        transaction.include(storage);
        transaction.begin();
        storage.update(createDataRecord(2));
        transaction.commit();
        
        Assert.assertEquals(1, countCountriesInAnotherThread());
        
        storage.update(createDataRecord(3));
        storage.commit();
        storage.end();
        
        Assert.assertEquals(3, countCountriesInAnotherThread());
    }
    
    /**
     * Creates a long transaction on the main thread,
     * @throws Exception
     */
    public void testReusingLongTransactionInSeveralThreads() throws Exception {
        Assert.assertEquals(0, countCountries());
        TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
        Transaction longTransaction = tm.create(Lifetime.LONG);
        longTransaction.include(storage);
        longTransaction.begin();
        int nbThreads = 10;
        Thread[] threads = new Thread[nbThreads];
        for(int i=0; i<nbThreads; i++){
            threads[i] = new Thread(new UpdateRunnable(longTransaction.getId(), i));
            threads[i].start();
        }
        for(int i=0; i<nbThreads; i++){
            threads[i].join();
        }
        Assert.assertEquals(0, countCountriesInAnotherThread());
        longTransaction.commit();
        Assert.assertEquals(nbThreads, countCountriesInAnotherThread());
    }
    
    private static DataRecord createDataRecord(int id){
        DataRecord result = new DataRecord(country, new DataRecordMetadataImpl(System.currentTimeMillis(), "T1")); //$NON-NLS-1$
        result.set(country.getField("id"), id); //$NON-NLS-1$
        result.set(country.getField("name"), "Country" + id); //$NON-NLS-1$ //$NON-NLS-2$
        result.set(country.getField("creationTime"), new Timestamp(new Date().getTime())); //$NON-NLS-1$
        result.set(country.getField("creationDate"), new Timestamp(new Date().getTime())); //$NON-NLS-1$
        return result;
    }
    
    private int countCountriesInAnotherThread() throws Exception {
        FetchRunnable fetch = new FetchRunnable();
        Thread t = new Thread(fetch);
        t.start();
        t.join();
        return fetch.getResultSize();
    }
    
    private int countCountries() throws Exception {
        FetchRunnable fetch = new FetchRunnable();
        fetch.run();
        return fetch.getResultSize();
    }
    
    private static class UpdateRunnable implements Runnable {
        
        private int id;
        private String transactionId;
        
        public UpdateRunnable(String transactionId, int id){
            this.transactionId = transactionId;
            this.id = id;
        }
        
        @Override
        public void run() {
            TransactionManager tm = ServerContext.INSTANCE.get().getTransactionManager();
            Transaction transaction = tm.get(this.transactionId);
            tm.associate(transaction);
            storage.begin();
            storage.update(createDataRecord(id));
            storage.commit();
            tm.dissociate(transaction);
        }
    }
    
    private static class FetchRunnable implements Runnable{
        
        private int resultSize;
        
        @Override
        public void run() {
            storage.begin();
            StorageResults result = storage.fetch(UserQueryBuilder.from(country).select(country.getField("id")).getSelect());
            this.resultSize = result.getCount();
            storage.commit();
            storage.end();
        }
        
        public int getResultSize(){
            return this.resultSize;
        }
    }
    
}
