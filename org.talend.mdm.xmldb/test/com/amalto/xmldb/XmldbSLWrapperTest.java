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
package com.amalto.xmldb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import junit.framework.TestCase;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.XmlServerException;

@SuppressWarnings("nls")
public class XmldbSLWrapperTest extends TestCase {



    private AbstractXmldbSLWrapper testXmldbSLWrapper;

    @Override
    protected void setUp() throws Exception {
        testXmldbSLWrapper = new TestXmldbSLWrapper();
    }

    public void testGetChildrenItemsQuery() throws Exception {
        String clusterName="Product";
        String conceptName="ProductFamily";
        String[] PKXpaths={"ProductFamily/Id"};
        String FKXpath=null;
        String labelXpath="ProductFamily/Name";
        String fatherPK=null;
        LinkedHashMap<String, String> itemsRevisionIDs = new LinkedHashMap<String, String>();
        String defaultRevisionID=null;
        IWhereItem whereItem = null;
        int start=0;
        int limit=20;
        
        String query = testXmldbSLWrapper.getChildrenItemsQuery(clusterName, conceptName, PKXpaths, FKXpath, labelXpath,
                fatherPK,
                itemsRevisionIDs, defaultRevisionID, whereItem, start, limit);
        String expected = "let $list := \nfor $ProductFamily in collection(\"/Product\")/ii/p/ProductFamily where (1=1) return <result><result-key>{$ProductFamily/Id/text()}</result-key><result-label>{$ProductFamily/Name/text()}</result-label></result>  \n return subsequence($list,1,20)";
        assertEquals(expected, query);
        
        // Test with FK XPath
        FKXpath = "Product/Family";
        fatherPK = "2";
        query = testXmldbSLWrapper.getChildrenItemsQuery(clusterName, conceptName, PKXpaths, FKXpath, labelXpath, fatherPK,
                itemsRevisionIDs, defaultRevisionID, whereItem, start, limit);
        expected = "let $list := \nfor $ProductFamily in collection(\"/Product\")/ii/p/ProductFamily where (1=1)  and ($Product/Family = '2' or $Product/Family=concat('[','2',']')) return <result><result-key>{$ProductFamily/Id/text()}</result-key><result-label>{$ProductFamily/Name/text()}</result-label></result>  \n return subsequence($list,1,20)";
        assertEquals(expected, query);

        // Test multi display labels
        FKXpath = null;
        labelXpath = "ProductFamily/Name,ProductFamily/ChangeStatus";
        query = testXmldbSLWrapper.getChildrenItemsQuery(clusterName, conceptName, PKXpaths, FKXpath, labelXpath, fatherPK,
                itemsRevisionIDs, defaultRevisionID, whereItem, start, limit);
        expected = "let $list := \nfor $ProductFamily in collection(\"/Product\")/ii/p/ProductFamily where (1=1) return <result><result-key>{$ProductFamily/Id/text()}</result-key><result-label>{$ProductFamily/Name/text()}</result-label><result-label>{$ProductFamily/ChangeStatus/text()}</result-label></result>  \n return subsequence($list,1,20)";
        assertEquals(expected, query);

    }

    class TestXmldbSLWrapper extends AbstractXmldbSLWrapper {

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#getAllClusters(java.lang.String)
         */
        @Override
        public String[] getAllClusters(String revisionID) throws XmlServerException {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#deleteCluster(java.lang.String, java.lang.String)
         */
        @Override
        public long deleteCluster(String revisionID, String clusterName) throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#deleteAllClusters(java.lang.String)
         */
        @Override
        public long deleteAllClusters(String revisionID) throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#createCluster(java.lang.String, java.lang.String)
         */
        @Override
        public long createCluster(String revisionID, String clusterName) throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#putDocumentFromFile(java.lang.String,
         * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public long putDocumentFromFile(String fileName, String uniqueID, String clusterName, String revisionID,
                String documentType) throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#existCluster(java.lang.String, java.lang.String)
         */
        @Override
        public boolean existCluster(String revision, String cluster) throws XmlServerException {

            return false;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#putDocumentFromString(java.lang.String,
         * java.lang.String, java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public long putDocumentFromString(String string, String uniqueID, String clusterName, String revisionID,
                String documentType) throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#putDocumentFromDOM(org.w3c.dom.Element,
         * java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public long putDocumentFromDOM(Element root, String uniqueID, String clusterName, String revisionID)
                throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#putDocumentFromSAX(java.lang.String,
         * org.xml.sax.XMLReader, org.xml.sax.InputSource, java.lang.String)
         */
        @Override
        public long putDocumentFromSAX(String dataClusterName, XMLReader docReader, InputSource input, String revisionId)
                throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#getDocumentAsString(java.lang.String,
         * java.lang.String, java.lang.String, java.lang.String)
         */
        @Override
        public String getDocumentAsString(String revisionID, String clusterName, String uniqueID, String encoding)
                throws XmlServerException {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#getDocumentBytes(java.lang.String, java.lang.String,
         * java.lang.String, java.lang.String)
         */
        @Override
        public byte[] getDocumentBytes(String revisionID, String clusterName, String uniqueID, String documentType)
                throws XmlServerException {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#getAllDocumentsUniqueID(java.lang.String,
         * java.lang.String)
         */
        @Override
        public String[] getAllDocumentsUniqueID(String revisionID, String clusterName) throws XmlServerException {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#deleteDocument(java.lang.String, java.lang.String,
         * java.lang.String, java.lang.String)
         */
        @Override
        public long deleteDocument(String revisionID, String clusterName, String uniqueID, String documentType)
                throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#deleteXtentisObjects(java.util.HashMap,
         * java.util.HashMap, java.lang.String, com.amalto.xmlserver.interfaces.IWhereItem)
         */
        @Override
        public int deleteXtentisObjects(HashMap<String, String> objectRootElementNameToRevisionID,
                HashMap<String, String> objectRootElementNameToClusterName, String objectRootElementName, IWhereItem whereItem)
                throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#deleteItems(java.util.LinkedHashMap,
         * java.util.LinkedHashMap, java.lang.String, com.amalto.xmlserver.interfaces.IWhereItem)
         */
        @Override
        public int deleteItems(LinkedHashMap<String, String> conceptPatternsToRevisionID,
                LinkedHashMap<String, String> conceptPatternsToClusterName, String conceptName, IWhereItem whereItem)
                throws XmlServerException {

            return 0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#runQuery(java.lang.String, java.lang.String,
         * java.lang.String, java.lang.String[])
         */
        @Override
        public ArrayList<String> runQuery(String revisionID, String clusterName, String query, String[] parameters)
                throws XmlServerException {

            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmlserver.interfaces.IXmlServerSLWrapper#close()
         */
        @Override
        public void close() throws XmlServerException {

        }

        /*
         * (non-Javadoc)
         * 
         * @see com.amalto.xmldb.AbstractXmldbSLWrapper#newQueryBuilder()
         */
        @Override
        protected QueryBuilder newQueryBuilder() {

            return null;
        }

    }

}
