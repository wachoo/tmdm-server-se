package talend.ext.images.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import talend.ext.images.server.backup.DBDelegate;
import talend.ext.images.server.backup.DBDelegateException;
import talend.ext.images.server.backup.ResourcePK;
import talend.ext.images.server.util.IOUtil;
import talend.ext.images.server.util.ReflectionUtil;

public class ImageDeleteServlet extends HttpServlet {

	private Logger logger = Logger.getLogger(this.getClass());

	private String outputFormat = "";

	private String deleteInDB = "false";

	private String dbDelegateClass = "";

	private String deleteUseTransaction = "false";

	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		outputFormat = config.getInitParameter("output-format");
		deleteInDB = config.getInitParameter("delete-in-db");
		dbDelegateClass = config.getInitParameter("db-delegate-class");
		deleteUseTransaction = config.getInitParameter("delete-use-transaction");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String result = "";

		result = onDelete(request, response);

		response.setContentType("text/html");
		PrintWriter writer = response.getWriter();
		writer.write(result.toString());
		writer.close();
	}

	private String onDelete(HttpServletRequest request,
			HttpServletResponse response) {

		try {
			String uri = request.getParameter("uri");
			logger.debug("Input URI: " + uri);
			if (uri == null || uri.length() == 0)
				return buildDeleteResult(false,
						"Request parameter 'uri' can not be empty! ");

            String contextPath = request.getSession().getServletContext().getContextPath();
            // normalize uri
            uri = normalizeURI(uri, contextPath);
			String toDeleteFilePath = getServletContext().getRealPath(uri);
			logger.debug("To Delete File Path: " + toDeleteFilePath);

			// delete on file system
			// TODO care about synchronized when delete file
			File toDeleteFile = new File(toDeleteFilePath);
			if (toDeleteFile.exists()) {

				if (toDeleteFile.delete()) {
					
					// also delete in backup db
					if (Boolean.parseBoolean(deleteInDB)) {
						DBDelegate dbDelegate = (DBDelegate) ReflectionUtil.newInstance(dbDelegateClass, new Object[0]);
						ResourcePK resourcePK = new ResourcePK(uri);
						
						if (dbDelegate.deleteResource(resourcePK)) {
							
							return buildDeleteResult(true,"The target file has been deleted from MDM Image Server successfully! ");
							
						}else if(Boolean.parseBoolean(deleteUseTransaction)){
							// roll back
							byte[] fileBytes = dbDelegate.getResource(resourcePK);
							if (fileBytes != null) {
								if (IOUtil.byteToImage(fileBytes, toDeleteFilePath)) {
									logger.debug("Roll back while deleting! ");
									return buildDeleteResult(false,"Failed in deleting in backup database, Roll back while deleting! ");
								} else {
									throw new DBDelegateException("Roll back failed! ");
								}//end if recover file
							} else {
								throw new DBDelegateException("Roll back failed: Can not get resource! ");
							}//end if get resource
							
						}else{
							logger.debug("Deleting from file system is OK, but from backup db failed. It's still OK, since no Transaction! ");
							return buildDeleteResult(true,"The target file has been deleted from MDM Image Server successfully! ");
						}//end if delete from db
					
					}else{
						return buildDeleteResult(true,
						"The target file has been deleted from MDM Image Server successfully! ");
					}//end if whether delete from db

				} else {
					return buildDeleteResult(false,
							"To delete the target file from file system failed! ");
				}// end if delete from folder
			} else {
				return buildDeleteResult(false,
						"The target file does not exist on file system! ");
			}// end if target file check exist
		} catch (Exception e) {
			e.printStackTrace();
			String err = "Exception occured during deleting! ";
			logger.error(err, e);
			return buildDeleteResult(false, err);
		}
	}

    /**
     * DOC Starkey Comment method "normalizeURI".
     * 
     * @param uri
     * @param contextPath
     * @return
     */
    private String normalizeURI(String uri, String contextPath) {

        if (uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        if (uri.startsWith("/")) //$NON-NLS-1$
            uri = uri.substring(1);
        return uri;
    }

	private String buildDeleteResult(boolean success, String message) {
		StringBuffer sb = new StringBuffer();

		if (outputFormat.equals("xml")) {

			sb.append("<DeleteResult>");
			sb.append("<success>" + success + "</success>");
			sb.append("<message>").append(message).append("</message>");
			sb.append("</DeleteResult>");

		} else if (outputFormat.equals("json")) {

			sb.append("{");
			sb.append("\"success\":" + success + ",");
			sb.append("\"message\":\"").append(message).append("\"");
			sb.append("}");

		}

		return sb.toString();

	}

}
