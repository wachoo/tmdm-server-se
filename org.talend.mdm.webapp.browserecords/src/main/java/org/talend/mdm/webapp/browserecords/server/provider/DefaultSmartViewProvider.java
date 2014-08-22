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
package org.talend.mdm.webapp.browserecords.server.provider;

import java.rmi.RemoteException;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.core.webservice.WSGetTransformer;
import com.amalto.core.webservice.WSGetTransformerPKs;
import com.amalto.core.webservice.WSTransformer;
import com.amalto.core.webservice.WSTransformerPK;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class DefaultSmartViewProvider implements SmartViewProvider {

    public WSTransformerPK[] getWSTransformerPKs() throws XtentisWebappException, RemoteException {
        return Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK();//$NON-NLS-1$;
    }

    public String getDescription(WSTransformerPK transformerPK) throws XtentisWebappException, RemoteException {
        WSTransformer wst = Util.getPort().getTransformer(new WSGetTransformer(transformerPK));
        return wst.getDescription();
    }
}
