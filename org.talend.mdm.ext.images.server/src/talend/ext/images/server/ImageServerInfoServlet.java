package talend.ext.images.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import talend.ext.images.server.util.FolderUtil;


/**
 * Servlet implementation class ImageLocateServlet
 */
public class ImageServerInfoServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1211531979103254445L;
    
    private static final String ACTION_GET_UPLOAD_HOME = "getUploadHome";

    public static final String UPLOAD_PATH = "upload"; // the folder to upload

    public static final String TEMP_PATH = "upload_tmp"; // the folder used to store temporary files of uploading
    
    private final Logger LOG = Logger.getLogger(ImageServerInfoServlet.class);

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String action = request.getParameter("action"); //$NON-NLS-1$
        ServletContext sc = this.getServletConfig().getServletContext();
        if (action != null) {
            if (action.equals("getUploadHome")) {
                String realUploadPath = (String) sc.getAttribute(UPLOAD_PATH);
                
                File uploadFolder = new File(realUploadPath);
                
                if(!uploadFolder.isDirectory() || !uploadFolder.canWrite()){
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                    
                response.setContentType("text/plain");
                PrintWriter writer = response.getWriter();
                writer.write(realUploadPath);
                writer.close();
            }
        }
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        ServletContext sc  = config.getServletContext();
        String realUploadPath = (String) sc.getAttribute(ImageServerInfoServlet.UPLOAD_PATH);
        String realTempUploadPath = (String) sc.getAttribute(ImageServerInfoServlet.TEMP_PATH);
        
        //this only happens when initial uploading images first through WebUI
        if ( realUploadPath == null || realTempUploadPath == null ) {
            FolderUtil.setUp();            
            sc.setAttribute(UPLOAD_PATH, FolderUtil.getUploadPath());
            sc.setAttribute(TEMP_PATH, FolderUtil.getTempUploadPath());
        }
        
        if ( !FolderUtil.IsUploadFolderReady() || !FolderUtil.IsTempUploadFolderReady() ) {
            throw new UnavailableException("Image Upload directory or Upload temp direcotry is not available for writting!"); //$NON-NLS-1$
        } else {
            LOG.debug("Images Upload Base Path: " + realUploadPath); //$NON-NLS-1$
        }
    }

}
