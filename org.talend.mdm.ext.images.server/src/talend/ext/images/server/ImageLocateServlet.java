package talend.ext.images.server;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ImageLocateServlet
 */
public class ImageLocateServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -3012919798771313147L;


    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

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

        String imgId = request.getParameter("imgId"); //$NON-NLS-1$
        String imgCatalog = request.getParameter("imgCatalog"); //$NON-NLS-1$
        if (imgId == null||imgCatalog == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String imgURI = "/upload/" + imgCatalog + "/" + imgId; //$NON-NLS-1$ //$NON-NLS-2$
        RequestDispatcher rd = request.getRequestDispatcher(imgURI);
        rd.forward(request, response);
    }

}
