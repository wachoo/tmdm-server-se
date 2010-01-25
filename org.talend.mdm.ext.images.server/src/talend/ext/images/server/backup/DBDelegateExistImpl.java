package talend.ext.images.server.backup;

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
