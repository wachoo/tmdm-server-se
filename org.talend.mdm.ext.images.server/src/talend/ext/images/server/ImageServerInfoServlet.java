package talend.ext.images.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImageLocateServlet
 */
public class ImageServerInfoServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1211531979103254445L;
    
    private static final String ACTION_GET_UPLOAD_HOME = "getUploadHome";

    public static final String UPLOAD_PATH = "/upload"; // the folder to upload

    public static final String TEMP_PATH = "/upload_tmp"; // the folder used to store temporary files of uploading

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
        if (action != null) {
            if (action.equals("getUploadHome")) {
                String realUploadPath = getServletContext().getRealPath(UPLOAD_PATH);
                
                if(!new File(realUploadPath).exists()){
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                    
                response.setContentType("text/plain");
                PrintWriter writer = response.getWriter();
                writer.write(realUploadPath);
                writer.close();
            }
        }
    }

}
