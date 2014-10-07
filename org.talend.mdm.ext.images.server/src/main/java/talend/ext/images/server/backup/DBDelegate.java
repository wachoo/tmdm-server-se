package talend.ext.images.server.backup;

public interface DBDelegate {
	
	public boolean putResource(ResourcePK resourcePK,String fileName);
	
	public byte[] getResource(ResourcePK resourcePK);
	
	public boolean deleteResource(ResourcePK resourcePK);

}
