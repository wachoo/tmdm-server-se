package com.amalto.webapp.v3.itemsbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.v3.itemsbrowser.util.PropsUtils;

public class RefreshPropertiesServlet extends HttpServlet{

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

        resp.setContentType("text/html; charset=\"UTF-8\"");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter writer = resp.getWriter();
        writer.write("<html><body>");
		try {
			PropsUtils.refreshProperties();
			writer.write(
   					"<p style='color:green'>The cache of the items-browser configuration has been updated successfully! </p>"
    			);
		} catch (Exception e) {
			e.printStackTrace();
			writer.write(
   					"<p style='color:red'>Failed to reset the cache of the items-browser configuration! </p>"
    			);
		}
		writer.write("</body></html>");
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
