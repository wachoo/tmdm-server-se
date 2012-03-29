package talend.ext.images.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import talend.ext.images.server.backup.DBDelegate;
import talend.ext.images.server.util.ReflectionUtil;

/**
 * Servlet implementation class ImageLocateServlet
 */
public class ImageLocateServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = -3012919798771313147L;

    private String resourceLocatorDelegateClass = null;

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        resourceLocatorDelegateClass = config.getInitParameter("resource-locator-delegate-class");
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
        if (imgId == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // Assume we always have db backup
        String imgURI = null;
        try {
            DBDelegate dbDelegate = (DBDelegate) ReflectionUtil.newInstance(resourceLocatorDelegateClass, new Object[0]);
            imgURI = dbDelegate.findResourceURI(imgId);
        } catch (Exception e) {
            e.printStackTrace();// FIXME I know sb. hate this
        }

        if (imgURI != null)
            response.sendRedirect(imgURI);
        else
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

}
