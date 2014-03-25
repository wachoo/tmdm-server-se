// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.metadata;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.amalto.core.schema.manage.AppinfoSourceHolder;
import com.amalto.core.schema.manage.AppinfoSourceHolderPK;
import com.amalto.core.schema.manage.SchemaCoreAgent;
import junit.framework.TestCase;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;

public class SchemaRightsAnalyzerTest extends TestCase {

    public void testMultiThreadAnalysis() throws Exception {
        final SchemaCoreAgent instance = SchemaCoreAgent.getInstance();

        InputStream resourceAsStream = this.getClass().getResourceAsStream("product.xsd");
        assertNotNull(resourceAsStream);
        BufferedReader bis = new BufferedReader(new InputStreamReader(resourceAsStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = bis.readLine()) != null) {
            builder.append(line);
        }

        instance.updateToDatamodelPool(null, null, builder.toString());
        AppinfoSourceHolder holder = new AppinfoSourceHolder(new AppinfoSourceHolderPK(null, null));
        AnalysisRunnable r = new AnalysisRunnable(instance, holder);

        Set<Thread> threads = new HashSet<Thread>();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(r));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        assertFalse(r.hasFailed());
    }

    private static class AnalysisRunnable implements Runnable {
        private boolean hasFailed;
        private final SchemaCoreAgent instance;
        private final AppinfoSourceHolder holder;

        public AnalysisRunnable(SchemaCoreAgent instance, AppinfoSourceHolder holder) {
            this.instance = instance;
            this.holder = holder;
            hasFailed = false;
        }

        public boolean hasFailed() {
            return hasFailed;
        }

        public void run() {
            for (int i = 0; i < 100; i++) {
                try {
                    instance.analyzeAccessRights(new DataModelID(null, null), "Product", holder);
                } catch (Exception e) {
                    synchronized (AnalysisRunnable.class) {
                        if (!hasFailed) {
                            hasFailed = true;
                        }
                    }
                    break;
                }
            }
        }
    }
}
