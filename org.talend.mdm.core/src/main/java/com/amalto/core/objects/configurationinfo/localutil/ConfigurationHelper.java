package com.amalto.core.objects.configurationinfo.localutil;

import com.amalto.core.server.XmlServer;
import org.apache.log4j.Logger;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class ConfigurationHelper {

    private static final Logger logger = Logger.getLogger(ConfigurationHelper.class);

    private static XmlServer server = null;// Do not use this field directly

    public static XmlServer getServer() throws XtentisException {
        if (server == null) {
            try {
                server = Util.getXmlServerCtrlLocal();
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

    public static void createCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            boolean exist = getServer().existCluster(revisionID, clusterName);
            if (!exist) {
                getServer().createCluster(revisionID, clusterName);
                logger.info("Created a new data cluster " + clusterName);
            }
        } catch (Exception e) {
            throw new XtentisException(e);
        }

    }

    public static void removeCluster(String revisionID, String clusterName) throws XtentisException {
        try {
            boolean exist = getServer().existCluster(revisionID, clusterName);
            if (exist) {
                getServer().deleteCluster(revisionID, clusterName);
                logger.info("Deleted a data cluster " + clusterName);
            }
        } catch (Exception e) {
            throw new XtentisException(e);
        }
    }

    public static void putDocument(String dataCluster, String xmlString, String uniqueID) throws XtentisException {
        XmlServer server = getServer();
        if (server.getDocumentAsString(null, dataCluster, uniqueID) == null) {
            server.start(dataCluster);
            try {
                server.putDocumentFromString(xmlString, uniqueID, dataCluster, null);
                server.commit(dataCluster);
            } catch (Exception e) {
                server.rollback(dataCluster);
                throw new XtentisException(e);
            }
            logger.info("Inserted document " + uniqueID + " to data cluster " + dataCluster);
        }
    }

    public static void deleteDocument(String revisionID, String clusterName, String uniqueID) throws XtentisException {
        XmlServer server = getServer();
        if (server.getDocumentAsString(null, clusterName, uniqueID) != null) {
            try {
                server.deleteDocument(revisionID, clusterName, uniqueID);
            } catch (Exception e) {
                throw new XtentisException(e);
            }
        }
    }
}
