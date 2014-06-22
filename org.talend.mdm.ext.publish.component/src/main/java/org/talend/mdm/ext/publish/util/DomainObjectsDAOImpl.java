/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package org.talend.mdm.ext.publish.util;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.XtentisException;

public class DomainObjectsDAOImpl implements DomainObjectsDAO {

    private Logger logger = Logger.getLogger(this.getClass());

    private static final String CLUSTER_NAME = "MDMDomainObjects"; //$NON-NLS-1$

    private XmlServerSLWrapperLocal server;

    public DomainObjectsDAOImpl(XmlServerSLWrapperLocal server) {
        this.server = server;

    }

    public String[] getAllPKs() throws XtentisException {

        String[] pks = server.getAllDocumentsUniqueID(null, CLUSTER_NAME);
        return pks;

    }

    public boolean putResource(String domainObjectName, String xmlContent) {

        try {
            server.start(CLUSTER_NAME);
            long rtnStatus = server.putDocumentFromString(xmlContent, domainObjectName, CLUSTER_NAME, null);
            if (rtnStatus == -1) {
                server.rollback(CLUSTER_NAME);
                return false;
            } else {
                server.commit(CLUSTER_NAME);
                return true;
            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return false;
        }

    }

    public String getResource(String domainObjectName) throws XtentisException {
        return server.getDocumentAsString(null, CLUSTER_NAME, domainObjectName);
    }

}
