/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load.action;

import javax.servlet.http.HttpServletRequest;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.load.LoadParser;
import com.amalto.core.load.LoadParserCallback;
import com.amalto.core.load.io.XMLRootInputStream;
import com.amalto.core.util.XSDKey;
import org.apache.log4j.Logger;

/**
*
*/
public class OptimizedLoadAction implements LoadAction {
    private static final Logger log = Logger.getLogger(OptimizedLoadAction.class);
    private final String dataClusterName;
    private final String typeName;
    private final boolean needAutoGenPK;

    public OptimizedLoadAction(String dataClusterName, String typeName, boolean needAutoGenPK) {
        this.dataClusterName = dataClusterName;
        this.typeName = typeName;
        this.needAutoGenPK = needAutoGenPK;
    }

    public boolean supportValidation() {
        return false;
    }

    public boolean supportAutoGenPK() {
        return true;
    }

    public void load(HttpServletRequest request, XSDKey keyMetadata, XmlServerSLWrapperLocal server) throws Exception {
        if (!".".equals(keyMetadata.getSelector())) { //$NON-NLS-1$
            throw new UnsupportedOperationException("Selector '" + keyMetadata.getSelector() + "' isn't supported.");
        }

        // Creates a load parser callback that loads data in server using a SAX handler
        ServerParserCallback callback = new ServerParserCallback(server, dataClusterName);

        java.io.InputStream inputStream = new XMLRootInputStream(request.getInputStream(), "root"); //$NON-NLS-1$
        LoadParser.Configuration configuration = new LoadParser.Configuration(typeName, keyMetadata.getFields(), needAutoGenPK, dataClusterName);
        LoadParser.parse(inputStream, configuration, callback);

        if (log.isDebugEnabled()) {
            log.debug("Number of documents loaded: " + callback.getCount());
        }
    }
}
