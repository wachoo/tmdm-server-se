package com.amalto.core.objects.configurationinfo.localutil;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class ConfigurationHelper {

    private static final Logger logger = Logger.getLogger(ConfigurationHelper.class);

    private static XmlServerSLWrapperLocal server = null;// Do not use this field directly

    public static XmlServerSLWrapperLocal getServer() throws XtentisException {
        if (server == null) {
            try {
                server = ((XmlServerSLWrapperLocalHome) new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME))
                        .create();
            } catch (Exception e) {
                String err = "Unable to access the XML Server wrapper";
                logger.error(err, e);
                throw new XtentisException(err, e);
            }
        }
        return server;
    }

    public static void createCluster(String revisionID, Class<? extends ObjectPOJO> objectClass) throws XtentisException {
        createCluster(revisionID, ObjectPOJO.getCluster(objectClass));
    }

    /**
     * @param revisionID
     * @param clusterName
     * @throws XtentisException
     * 
     * override : false
     */
    public static void createCluster(String revisionID, String clusterName) throws XtentisException {

        try {
            boolean exist = getServer().existCluster(revisionID, clusterName);
            if (!exist) {
                getServer().createCluster(revisionID, clusterName);
                logger.info("Created a new datacluster " + clusterName);
            }
        } catch (Exception e) {
            throw new XtentisException(e);
        }

    }

    /**
     * @param revisionID
     * @param clusterName
     * @throws XtentisException
     * 
     * delete if exist
     */
    public static void removeCluster(String revisionID, String clusterName) throws XtentisException {

        try {
            boolean exist = getServer().existCluster(revisionID, clusterName);
            if (exist) {
                getServer().deleteCluster(revisionID, clusterName);
                logger.info("Deleted a datacluster " + clusterName);
            }

        } catch (Exception e) {
            throw new XtentisException(e);
        }
    }

    /**
     * @param datacluster
     * @param xmlString
     * @param uniqueID
     * @throws XtentisException
     * 
     * override : false
     */
    public static void putDomcument(String datacluster, String xmlString, String uniqueID) throws XtentisException {
        XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
        if (xmlServerCtrlLocal.getDocumentAsString(null, datacluster, uniqueID) == null) {
            xmlServerCtrlLocal.start(datacluster);
            xmlServerCtrlLocal.putDocumentFromString(xmlString, uniqueID, datacluster, null);
            xmlServerCtrlLocal.commit(datacluster);
            logger.info("Inserted document " + uniqueID + " to datacluster " + datacluster);
        }
    }

    /**
     * @param revisionID
     * @param clusterName
     * @param uniqueID
     * @throws XtentisException
     */
    public static void deleteDocumnet(String revisionID, String clusterName, String uniqueID) throws XtentisException {
        if (Util.getXmlServerCtrlLocal().getDocumentAsString(null, clusterName, uniqueID) != null) {
            Util.getXmlServerCtrlLocal().deleteDocument(revisionID, clusterName, uniqueID);
        }
    }
}
