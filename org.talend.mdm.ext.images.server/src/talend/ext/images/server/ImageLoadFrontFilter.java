package talend.ext.images.server;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.mchange.v2.ssim.SsimServlet;
import com.mchange.v2.util.PatternReplacementMap;

import talend.ext.images.server.backup.DBDelegate;
import talend.ext.images.server.backup.ResourcePK;
import talend.ext.images.server.util.IOUtil;
import talend.ext.images.server.util.ReflectionUtil;

public class ImageLoadFrontFilter {

    private static Logger logger = Logger.getLogger(ImageLoadFrontFilter.class);

    private String resourceCatalogName = ""; //$NON-NLS-1$

    private String resourceFileName = ""; //$NON-NLS-1$

    private boolean restoreFromDB = false;

    public boolean doFilter(ServletContext sc, HttpServletRequest req, HttpServletResponse res, boolean inUseDBBackup,
            String inDBDelegateClass) throws IOException {

        try {
            String resourcePath = parseResourcePath(sc, req);
            logger.debug("Resource Path: " + resourcePath); //$NON-NLS-1$
            File resourceFile = new File(resourcePath);
            if (resourceFile.exists()) {

                return true;

            } else {
                logger.debug("Resource Missing! "); //$NON-NLS-1$

                if (restoreFromDB && inUseDBBackup) {
                    DBDelegate dbDelegate = (DBDelegate) ReflectionUtil.newInstance(inDBDelegateClass, new Object[0]);
                    byte[] fileBytes = dbDelegate.getResource(new ResourcePK(resourceCatalogName, resourceFileName));
                    if (fileBytes != null) {
                        if (IOUtil.byteToImage(fileBytes, resourcePath)) {
                            logger.debug("Restore file from backup database!"); //$NON-NLS-1$
                            return true;
                        }
                    }
                }

                return false;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return false;
        }

    }


    public String getServletPath() {

        String path = ""; //$NON-NLS-1$

        if (resourceCatalogName != null && resourceCatalogName.length() > 0)
            path += ("/" + resourceCatalogName); //$NON-NLS-1$

        path += ("/" + resourceFileName); //$NON-NLS-1$

        return path;

    }

    private String parseResourcePath(ServletContext sc, HttpServletRequest req) {
        String path =  (String) sc.getAttribute(ImageServerInfoServlet.UPLOAD_PATH);

        String input = req.getRequestURI();
        if (input.indexOf("?") != -1) { //$NON-NLS-1$
            input = input.substring(0, input.indexOf("?")); //$NON-NLS-1$
        }
        input = input.replaceAll("//", "/");  //$NON-NLS-1$//$NON-NLS-2$
        input = input.substring(input.indexOf("/upload") + 7); //$NON-NLS-1$
        try {
            parseCatalogAndFile(input);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        path += (File.separator + resourceCatalogName + File.separator + resourceFileName);
        path = path.replaceAll("\\\\", "/");  //$NON-NLS-1$//$NON-NLS-2$

        PatternReplacementMap patternReplacementMap = (PatternReplacementMap) sc
                .getAttribute(SsimServlet.PATTERN_REPLACEMENT_MAP_APPKEY);

        if (patternReplacementMap != null) {
            patternReplacementMap.addMapping(Pattern.compile(input), "file:" + path); //$NON-NLS-1$
        }        
        return path;
    }

    private void parseCatalogAndFile(String in) throws UnsupportedEncodingException {
        if (in.startsWith("/")) //$NON-NLS-1$
            in = in.substring(1);
        String[] inArray = in.split("/"); //$NON-NLS-1$
        if (inArray.length == 1) {
            resourceCatalogName = "/"; //$NON-NLS-1$
            resourceFileName = inArray[0];
        } else if (inArray.length == 2) {
            resourceCatalogName = inArray[0];
            resourceFileName = inArray[1];
        }
        
        resourceCatalogName = URLDecoder.decode(resourceCatalogName, "UTF-8"); //$NON-NLS-1$
        resourceFileName = URLDecoder.decode(resourceFileName, "UTF-8"); //$NON-NLS-1$
    }

}