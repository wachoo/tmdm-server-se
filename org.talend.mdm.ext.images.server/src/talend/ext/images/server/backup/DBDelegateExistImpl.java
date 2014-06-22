package talend.ext.images.server.backup;

import java.util.ArrayList;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.util.XtentisException;

public class DBDelegateExistImpl implements DBDelegate{
	
	private static final String BACKUP_CLUSTER_NAME = "MDMItemImages";
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	private XmlServerSLWrapperLocal server;
	
	public DBDelegateExistImpl() {
		
		try {
			server = ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Unable to access the XML Server wrapper";
			logger.error(err,e);
		}
	}

	public byte[] getResource(ResourcePK resourcePK) {
		
		try {
			return server.getDocumentBytes(null, BACKUP_CLUSTER_NAME, resourcePK.toString(), "BINARY");
		} catch (XtentisException e) {
			e.printStackTrace();
			return null;
		}
	}

    public String findResourceURI(String imageId) throws XtentisException {

        String path = "/imageserver/upload"; //$NON-NLS-1$
        
        String[] params={imageId};
        ArrayList<String> foundItemURIs = server
                .runQuery(
                        null,
                        BACKUP_CLUSTER_NAME,
                        "for $pivot in collection(\"" + BACKUP_CLUSTER_NAME + "\") where fn:ends-with(fn:base-uri($pivot), '%0') return fn:base-uri($pivot) ", //$NON-NLS-1$ //$NON-NLS-2$
                        params);

        if (foundItemURIs != null && foundItemURIs.size() > 0) {

            String pk;
            String foundItemURI = foundItemURIs.get(0);// FIXME: only get the first one
            if (foundItemURI.lastIndexOf("/") != -1) {
                pk = foundItemURI.substring(foundItemURI.lastIndexOf("/") + 1);
            } else {
                pk = foundItemURI;
            }

            if (pk.indexOf("-") == -1)pk = "-" + pk; //$NON-NLS-1$ //$NON-NLS-2$
            String[] pkParts = pk.split("-"); //$NON-NLS-1$
            String catalog = pkParts[0];
            String file = pkParts[1];
            if (!catalog.equals("") && !catalog.equals("/") && !catalog.equals("//"))path = path + "/" + catalog; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
            path = path + "/" + file; //$NON-NLS-1$

        }

        return path;

    }

	public boolean putResource(ResourcePK resourcePK, String fileName){
		
		try {
			
			long rtnStatus=server.putDocumentFromFile(fileName, resourcePK.toString(), BACKUP_CLUSTER_NAME, null, "BINARY");
			if(rtnStatus==-1){
				return false;
			}else{
				return true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
	}

	public boolean deleteResource(ResourcePK resourcePK) {
		try {
			long rtnStatus=server.deleteDocument(null, BACKUP_CLUSTER_NAME, resourcePK.toString(), "BINARY");
			if(rtnStatus==-1){
				return false;
			}else{
				return true;
			}
		} catch (XtentisException e) {
			e.printStackTrace();
			return false;
		}
		
	}

}
