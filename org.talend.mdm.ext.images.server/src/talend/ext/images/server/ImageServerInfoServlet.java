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

import com.mchange.v2.ssim.SsimServlet;

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
    
    private final Logger LOG = Logger.getLogger(SsimServlet.class);

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
        
        //this only happens when uploading resource images first through WebUI
        if ( realUploadPath == null || realTempUploadPath == null ) {
            String jbossServerDir = System.getProperty("jboss.server.home.dir"); //$NON-NLS-1$
            if (jbossServerDir != null) {
                realUploadPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                        + "mdm_resources" + File.separator + UPLOAD_PATH; //$NON-NLS-1$
                realTempUploadPath = jbossServerDir + File.separator + "data" + File.separator //$NON-NLS-1$
                        + "mdm_resources" + File.separator + TEMP_PATH; //$NON-NLS-1$
            }
            File uploadFolder = new File(realUploadPath);
            File uploadTempFolder = new File(realTempUploadPath);
            uploadFolder.mkdirs();
            uploadTempFolder.mkdirs();
            sc.setAttribute(UPLOAD_PATH, realUploadPath);
            sc.setAttribute(TEMP_PATH, realTempUploadPath);
        }
        
        if ( realUploadPath == null || realTempUploadPath == null ) {
            throw new UnavailableException("Image Upload directory or Upload temp direcotry is not available!"); //$NON-NLS-1$
        } else {
            LOG.debug("Images Upload Base Path: " + realUploadPath); //$NON-NLS-1$
        }
    }

}
