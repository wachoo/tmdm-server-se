package talend.ext.images.server.backup;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.amalto.core.util.Util;
import org.apache.log4j.Logger;

import com.amalto.core.ejb.remote.XmlServerSLWrapper;

public class DBDelegateExistImpl implements DBDelegate {

    private static final String FROM_HOST = "from.host";//$NON-NLS-1$

    private static final String FROM_JNDI_PORT = "from.jndi.port";//$NON-NLS-1$
    
    private static final String CLUSTER_NAME = "MDMItemImages"; //$NON-NLS-1$

    private static final Logger logger = Logger.getLogger(DBDelegateExistImpl.class);

    private XmlServerSLWrapper server;

    public DBDelegateExistImpl() {
        InputStream is = null;
        try {
            is = DBDelegateExistImpl.class.getResourceAsStream("ejb.remote.jndi.lookup.properties"); //$NON-NLS-1$
            Properties properties = new Properties();
            properties.load(is);
            server = Util.getXmlServerCtrlHome(properties.getProperty(FROM_HOST), properties.getProperty(FROM_JNDI_PORT));
        } catch (Exception e) {
            String err = "Unable to access the XML Server wrapper";
            logger.error(err, e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                 // do nothing
                }
            }
        }
    }

    public byte[] getResource(ResourcePK resourcePK) {
        try {
            return server.getDocumentBytes(null, CLUSTER_NAME, resourcePK.toString(), "BINARY"); //$NON-NLS-1$
        } catch (Exception e) {
            logger.error("Error during getResource call.", e);
            return null;
        } 
    }

    public boolean putResource(ResourcePK resourcePK, String fileName) {
        try {
            long rtnStatus = server.putDocumentFromFile(fileName, resourcePK.toString(), CLUSTER_NAME, null, "BINARY"); //$NON-NLS-1$
            return rtnStatus != -1;
        } catch (Exception e) {
            logger.error("Error during putResource call.", e);
            return false;
        }
    }

    public boolean deleteResource(ResourcePK resourcePK) {
        try {
            long rtnStatus = server.deleteDocument(null, CLUSTER_NAME, resourcePK.toString(), "BINARY"); //$NON-NLS-1$
            return rtnStatus != -1;
        } catch (Exception e) {
            logger.error("Error during deleteResource call.", e);
            return false;
        }

    }

}
