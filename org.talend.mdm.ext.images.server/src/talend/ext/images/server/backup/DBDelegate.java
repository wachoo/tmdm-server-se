package talend.ext.images.server.backup;

import com.amalto.core.util.XtentisException;

public interface DBDelegate {
	
	//public boolean putResource(ResourcePK resourcePK,byte[] content) throws DBDelegateException;
	
	public boolean putResource(ResourcePK resourcePK,String fileName);
	
	public byte[] getResource(ResourcePK resourcePK);
	
	public boolean deleteResource(ResourcePK resourcePK);
	
    public String findResourceURI(String imageId) throws XtentisException;

}
