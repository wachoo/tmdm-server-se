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

package com.amalto.core.save;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.save.context.SaverContextFactory;
import com.amalto.core.save.context.TimeMeasure;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

import java.util.HashSet;
import java.util.Set;

public class SaverSession {

    private final SaverContextFactory contextFactory;

    private final Set<String> startedTransactions = new HashSet<String>();

    private SaverSession() {
        contextFactory = new SaverContextFactory();
    }

    public static SaverSession newSession() {
        return new SaverSession();
    }

    public SaverContextFactory getContextFactory() {
        return contextFactory;
    }

    public void end() {
        end(new DefaultCommitter());
    }

    public void end(Committer committer) {
        System.out.println("SaverSession.end");
        try {
            for (String startedTransaction : startedTransactions) {
                System.out.println("Commit on container: '" + startedTransaction + "'.");
                committer.commit(startedTransaction);
            }
        } finally {
            TimeMeasure.reset();
        }
    }

    public void begin(String dataCluster) {
        startedTransactions.add(dataCluster);
    }

    public interface Committer {
        void commit(String dataCluster);
    }

    public class DefaultCommitter implements Committer {

        private XmlServerSLWrapperLocal xmlServerCtrlLocal;

        public DefaultCommitter() {
            try {
                xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            } catch (XtentisException e) {
                throw new RuntimeException(e);
            }
        }

        public void commit(String dataCluster) {
            try {
                if (xmlServerCtrlLocal.supportTransaction()) {
                    xmlServerCtrlLocal.commit(dataCluster);
                }
            } catch (XtentisException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
