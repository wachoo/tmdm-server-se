package talend.ext.images.server.backup;

import com.amalto.core.server.api.XmlServer;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import org.apache.log4j.Logger;

public class DBDelegateExistImpl implements DBDelegate {

    private static final String CLUSTER_NAME = "MDMItemImages"; //$NON-NLS-1$

    private static final Logger logger = Logger.getLogger(DBDelegateExistImpl.class);

    private XmlServer server;

    public DBDelegateExistImpl() {
        try {
            server = Util.getXmlServerCtrlLocal();
        } catch (Exception e) {
            String err = "Unable to access the XML Server wrapper";
            logger.error(err, e);
        }
    }

    public byte[] getResource(ResourcePK resourcePK) {
        try {
            return server.getDocumentBytes(CLUSTER_NAME, resourcePK.toString(), "BINARY"); //$NON-NLS-1$
        } catch (XtentisException e) {
            logger.error("Error during getResource call.", e);
            return null;
        }
    }

    public boolean putResource(ResourcePK resourcePK, String fileName) {
        try {
            long rtnStatus = server.putDocumentFromFile(fileName, resourcePK.toString(), CLUSTER_NAME, "BINARY"); //$NON-NLS-1$
            return rtnStatus != -1;
        } catch (Exception e) {
            logger.error("Error during putResource call.", e);
            return false;
        }
    }

    public boolean deleteResource(ResourcePK resourcePK) {
        try {
            long rtnStatus = server.deleteDocument(CLUSTER_NAME, resourcePK.toString(), "BINARY"); //$NON-NLS-1$
            return rtnStatus != -1;
        } catch (XtentisException e) {
            logger.error("Error during deleteResource call.", e);
            return false;
        }

    }

}
