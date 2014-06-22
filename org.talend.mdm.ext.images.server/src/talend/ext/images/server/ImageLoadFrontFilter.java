package talend.ext.images.server;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import talend.ext.images.server.backup.DBDelegate;
import talend.ext.images.server.backup.ResourcePK;
import talend.ext.images.server.util.IOUtil;
import talend.ext.images.server.util.ReflectionUtil;

public class ImageLoadFrontFilter {

	private static Logger logger = Logger.getLogger(ImageLoadFrontFilter.class);

	private static String resourceCatalogName = "";

	private static String resourceFileName = "";

	public static boolean doFilter(ServletContext sc, HttpServletRequest req,
			HttpServletResponse res,boolean inUseDBBackup,String inDBDelegateClass) throws IOException {

		try {
			String resourcePath = parseResourcePath(sc, req);
			logger.debug("Resource Path: " + resourcePath);
			File resourceFile = new File(resourcePath);
			if (resourceFile.exists()) {

				return true;

			} else {
				logger.debug("Resource Missing! ");
				// get from db backup and recreate in catalog
				if(inUseDBBackup){
					DBDelegate dbDelegate=(DBDelegate) ReflectionUtil.newInstance(inDBDelegateClass,new Object[0]);
					byte[] fileBytes=dbDelegate.getResource(new ResourcePK(resourceCatalogName,resourceFileName));
					if(fileBytes!=null){
						if(IOUtil.byteToImage(fileBytes,resourcePath)){
							logger.debug("Restore file from backup database! ");
							return true;
						}
					}
				}

				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	private static String parseResourcePath(ServletContext sc,
			HttpServletRequest req) {
		String path = sc.getRealPath("/upload");
		String input = req.getRequestURI();
		if (input.indexOf("?") != -1) {
			input = input.substring(0, input.indexOf("?"));
		}
		input = input.replaceAll("//", "/");
		input = input.substring(input.indexOf("/upload") + 7);
		parseCatalogAndFile(input);
		input = input.replace("/", File.separator);
		path += input;
		return path;
	}

	private static void parseCatalogAndFile(String in) {
		if (in.startsWith("/"))
			in = in.substring(1);
		String[] inArray = in.split("/");
		if (inArray.length == 1) {
			resourceCatalogName = "/";
			resourceFileName = inArray[0];
		} else if (inArray.length == 2) {
			resourceCatalogName = inArray[0];
			resourceFileName = inArray[1];
		}
	}

	

}
