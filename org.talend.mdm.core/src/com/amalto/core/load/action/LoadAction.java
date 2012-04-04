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

package com.amalto.core.load.action;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.XSDKey;

import java.io.InputStream;

/**
 * Load strategy to be used during bulk load of documents in MDM.
 *
 * @see com.amalto.core.servlet.LoadServlet
 */
public interface LoadAction {
    /**
     * @return true if action supports XML validation, false otherwise
     */
    boolean supportValidation();

    /**
     * @return true if action supports ID generated server side (auto increment), false otherwise.
     */
    boolean supportAutoGenPK();

    /**
     * Loads XML documents from <code>request</code> in <code>server</code>.
     *
     * @param stream     The stream that contains all XML documents to be loaded in MDM.
     * @param keyMetadata Key metadata <b>or <code>null</code> in case of autoGenPK</b>.
     * @param server      The database where the documents must be persisted.
     * @throws Exception In case anything goes wrong during load.
     */
    void load(InputStream stream, XSDKey keyMetadata, XmlServerSLWrapperLocal server) throws Exception;

    /**
     * End load and perform all post-load actions (such as save counter state in case of autogen pk).
     * @param server MDM underlying storage that might need operations (commit for instance).
     */
    void endLoad(XmlServerSLWrapperLocal server);
}
