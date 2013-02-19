// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.journal.sever.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Set;

import org.talend.mdm.webapp.journal.server.service.WebService;

import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.sun.xml.xsom.XSElementDecl;


/**
 * created by talend2 on 2013-2-7
 * Detailled comment
 *
 */
public class WebServiceMock implements WebService {

    public WSItem getItem(WSGetItem wSGetItem) throws XtentisWebappException, RemoteException {   
        String[] ids = {"genericUI","1360140140037"}; //$NON-NLS-1$ //$NON-NLS-2$
        WSItemPK pk = wSGetItem.getWsItemPK();
        if ("UpdateReport".equals(pk.getWsDataClusterPK().getPk()) && "Update".equals(pk.getConceptName()) && "genericUI".equals(pk.getIds()[0]) && "1360140140037".equals(pk.getIds()[1])) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            WSItem item = new WSItem();
            item.setConceptName("Update"); //$NON-NLS-1$
            item.setContent("<Update><UserName>administrator</UserName><Source>genericUI</Source><TimeInMillis>1361153957282</TimeInMillis><RevisionID>null</RevisionID><DataCluster>Product</DataCluster><DataModel>Product</DataModel><Concept>Product</Concept><Key>1</Key><Item><path>Name</path><oldValue>1</oldValue><newValue>123</newValue></Item><OperationType>UPDATE</OperationType></Update>"); //$NON-NLS-1$
            item.setDataModelName("UpdateReport"); //$NON-NLS-1$
            item.setIds(ids);
            item.setWsDataClusterPK(new WSDataClusterPK("UpdateReport")); //$NON-NLS-1$
            return item;
        } else if ("Product".equals(pk.getWsDataClusterPK().getPk()) && "ProductFamily".equals(pk.getConceptName()) && "1".equals(pk.getIds()[0])) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            WSItem item = new WSItem();
            item.setConceptName("ProductFamily"); //$NON-NLS-1$
            item.setContent("<ProductFamily><Id>1</Id><Name>Test_Product_FKInfo</Name></ProductFamily>"); //$NON-NLS-1$
            item.setDataModelName("UpdateReport"); //$NON-NLS-1$ 
            item.setWsDataClusterPK(new WSDataClusterPK("Product")); //$NON-NLS-1$
            return item;
        } else {
            return null;
        }        
    }

    public WSStringArray getItems(WSGetItems wsGetItems) throws XtentisWebappException, RemoteException {
        if ("Update".equals(wsGetItems.getConceptName()) && "UpdateReport".equals(wsGetItems.getWsDataClusterPK().getPk())) { //$NON-NLS-1$ //$NON-NLS-2$
            String[] result = {"<totalCount>1</totalCount>","<result><Update><UserName>administrator</UserName><Source>genericUI</Source><TimeInMillis>1360140140037</TimeInMillis><OperationType>CREATE</OperationType><RevisionID>null</RevisionID><DataCluster>Product</DataCluster><DataModel>Product</DataModel><Concept>Product</Concept><Key>123</Key></Update></result>"}; //$NON-NLS-1$ //$NON-NLS-2$
            return new WSStringArray(result);
        } else {
            return null;
        } 
    }

    public XSElementDecl getXSElementDecl(String dataModel, String concept) throws Exception {
        String xsd = ""; //$NON-NLS-1$
        if ("Product".equals(dataModel) && "Product".equals(concept)) { //$NON-NLS-1$ //$NON-NLS-2$
            xsd = readFile("Product.xsd"); //$NON-NLS-1$
        }
        return com.amalto.core.util.Util.getConceptMap(xsd).get(concept);
    }
    
    public boolean isAuth(Set<String> roleSet) throws Exception {
        return true;
    }
    
    private String readFile(String fileName) throws IOException{
        String txt = ""; //$NON-NLS-1$
        String tempString = ""; //$NON-NLS-1$
        InputStream in = WebServiceMock.class.getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        while ((tempString = reader.readLine()) != null) {
            txt += tempString;
        }
        reader.close();      
        return txt;
    }


}
