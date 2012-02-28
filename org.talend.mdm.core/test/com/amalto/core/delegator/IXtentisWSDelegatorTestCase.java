// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.delegator;

import java.util.HashMap;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.remote.DataClusterCtrl;
import com.amalto.core.objects.datacluster.ejb.remote.DataClusterCtrlHome;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.remote.DataModelCtrl;
import com.amalto.core.objects.datamodel.ejb.remote.DataModelCtrlHome;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.remote.TransformerV2Ctrl;
import com.amalto.core.objects.transformers.v2.ejb.remote.TransformerV2CtrlHome;
import com.amalto.core.webservice.WSDataClusterPK;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSPutItem;
import com.amalto.core.webservice.WSPutItemWithReport;

/**
 * this only, the jboss server must already is running
 * 
 */
@SuppressWarnings("nls")
public class IXtentisWSDelegatorTestCase extends TestCase {

    private static final Logger LOG = Logger.getLogger(IXtentisWSDelegatorTestCase.class);

    private static String login = "administrator";

    private static String password = "administrator";

    private String host = "localhost";
    private String product = "Product";

    private static HashMap<String, EJBHome> ejbHomes = new HashMap<String, javax.ejb.EJBHome>();

    @Override
    protected void setUp() throws Exception {
        initdata();
    }

    private static Properties getContextProperties() {
        Properties p = new Properties();
        p.put("api-type", "EJB2");
        p.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        p.put(InitialContext.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        p.put(InitialContext.PROVIDER_URL, "jnp://127.0.0.1:1199");
        p.put(InitialContext.SECURITY_PRINCIPAL, login);
        p.put(InitialContext.SECURITY_CREDENTIALS, password);
        return p;
    }

    public static EJBHome getHome(String jndi) throws NamingException {
        EJBHome home = null;
        if (true) {
            home = ejbHomes.get(jndi);
            if (home == null) {
                home = (EJBHome) new InitialContext(getContextProperties()).lookup(jndi);
                ejbHomes.put(jndi, home);
            }
        } else {
            home = (EJBHome) new InitialContext().lookup(jndi);
        }
        // dumpClass(localHome.getClass());
        return home;
    }
    protected void initdata() throws Exception {
        // put datamodel
        DataModelPOJO dm = new DataModelPOJO();

        dm.setName(product);
        dm.setSchema(IOUtils.toString(IXtentisWSDelegatorTestCase.class.getResourceAsStream("Product.xsd")));
        DataModelCtrl dmCtrl = ((DataModelCtrlHome) getHome(DataModelCtrlHome.JNDI_NAME)).create();
        dmCtrl.putDataModel(dm);

        // create datacluser
        DataClusterPOJO dc = new DataClusterPOJO();
        dc.setName(product);
        DataClusterCtrl dcCtrl = ((DataClusterCtrlHome) getHome(DataClusterCtrlHome.JNDI_NAME)).create();
        dcCtrl.putDataCluster(dc);

        // create before saving process

        String xml = IOUtils.toString(IXtentisWSDelegatorTestCase.class.getResourceAsStream("beforeSaving_Product.xml"));
        TransformerV2POJO transformer = TransformerV2POJO.unmarshal(TransformerV2POJO.class, xml);

        TransformerV2Ctrl tfCtrl = ((TransformerV2CtrlHome) getHome(TransformerV2CtrlHome.JNDI_NAME)).create();

        tfCtrl.putTransformer(transformer);

        // create item
        // ItemPOJO item = new ItemPOJO();
        // item.setConceptName(product);
        // item.setProjectionAsString("<Product><Id>1</Id><Name>name1</Name><Description>desc1</Description><Price>1</Price></Product>");
        // item.setDataClusterPK(new DataClusterPOJOPK(product));
        // item.setDataModelName(product);
        //
        // ItemCtrl2 itemCtrl = ((ItemCtrl2Home) getHome(ItemCtrl2Home.JNDI_NAME)).create();
        //
        // itemCtrl.putItem(item, dm);

    }

    public void testBeforeSaving() throws Exception {
        WSPutItemWithReport itemReport = new WSPutItemWithReport();
        WSPutItem item = new WSPutItem();
        item.setWsDataClusterPK(new WSDataClusterPK(product));
        item.setWsDataModelPK(new WSDataModelPK(product));
        String itemxml = "<Product>  <Id>1</Id>  <Name>beforeSaving_Product</Name>  <Description>beforeSavingProduct junit test</Description>  <Price>1</Price></Product>";
        item.setXmlString(itemxml);
        item.setIsUpdate(false);

        itemReport.setInvokeBeforeSaving(true);
        itemReport.setSource("junittest");
        itemReport.setWsPutItem(item);

        String updatereportxml = "<Update><UserName>administrator</UserName><Source>genericUI</Source><TimeInMillis>1330398525160</TimeInMillis><OperationType>UPDATE</OperationType><RevisionID>null</RevisionID><DataCluster>Product</DataCluster><DataModel>Product</DataModel><Concept>Product</Concept><Key>1</Key><Item>   <path>Name</path>   <oldValue>name1</oldValue>   <newValue>beforeSaving_Product</newValue></Item></Update>";

        // invoke beforeSaving

        JunitXtentisWSDelegator delegator = new JunitXtentisWSDelegator();
        // boolean isok = delegator.beforeSaving(itemReport, product, itemxml, updatereportxml);
        // assertEquals(isok, true);
        //
        // LOG.info("record modified by beforeSaving_Product -->" + itemReport.getWsPutItem().getXmlString());
        // LOG.info("output_report message -->" + itemReport.getSource());
    }

}
