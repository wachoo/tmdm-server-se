package talend.ext.images.server.backup;

public class ResourcePK {
	
	private String catalogName;
	
	private String fileName;

	public ResourcePK(String catalogName, String fileName) {
		super();
		this.catalogName = catalogName;
		this.fileName = fileName;
	}
	
	public ResourcePK(String uri) {
		super();
		
		uri = uri.replaceAll("//", "/");
		if (uri.startsWith("/"))uri = uri.substring(1);
		uri = uri.substring(uri.indexOf("upload/") + 7);
		
		String[] inArray = uri.split("/");
		if (inArray.length == 1) {
			this.catalogName = "/";
			this.fileName = inArray[0];
		} else if (inArray.length == 2) {
			this.catalogName = inArray[0];
			this.fileName  = inArray[1];
		}
	}
	
	@Override
	public String toString() {
		if(catalogName==null||catalogName.equals("")){
			return fileName;
		}else{
			return catalogName+"-"+fileName;
		}
		
	}
}
