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

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.XtentisException;

public class PicturesDAODBImpl implements PicturesDAO {

    private static final String CLUSTER_NAME = "MDMItemImages"; //$NON-NLS-1$

    private XmlServerSLWrapperLocal server;

    public PicturesDAODBImpl(XmlServerSLWrapperLocal server) {
        this.server = server;

    }

    public String[] getAllPKs() throws XtentisException {

        String[] pks = server.getAllDocumentsUniqueID(null, CLUSTER_NAME);
        return pks;

    }

}
