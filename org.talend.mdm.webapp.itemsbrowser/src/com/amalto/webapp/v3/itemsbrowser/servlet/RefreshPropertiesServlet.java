package com.amalto.webapp.v3.itemsbrowser.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amalto.webapp.v3.itemsbrowser.util.PropsUtils;

public class RefreshPropertiesServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String toReport = req.getParameter("output_report");
        if (toReport.equals("true")) {
            ServletOutputStream ouputStream = null;
            try {
                String process = req.getParameter("process");
                String id = req.getParameter("id");
                byte[] mimecontbytes = (byte[]) req.getSession().getAttribute(process + id);
                byte[] mimetypebytes = (byte[]) req.getSession().getAttribute(process + id + "mimetype");
                String mimetype = new String(mimetypebytes);
                mimetype = mimetype.split(";")[0];
                resp.setContentType(mimetype);
                resp.setContentLength(mimecontbytes.length);
                resp.setHeader("Content-Disposition", "inline");
                resp.setHeader("Cache-Control", "cache, must-revalidate");
                resp.setHeader("Pragma", "public");
                ouputStream = resp.getOutputStream();
                ouputStream.write(mimecontbytes, 0, mimecontbytes.length);
                ouputStream.flush();
            } finally {
                if (ouputStream != null) {
                    ouputStream.close();
                }
            }
        } else {
            resp.setContentType("text/html; charset=\"UTF-8\"");
            resp.setCharacterEncoding("UTF-8");
            PrintWriter writer = resp.getWriter();
            writer.write("<html><body>");
            try {
                PropsUtils.refreshProperties();
                writer.write("<p style='color:green'>The cache of the items-browser configuration has been updated successfully! </p>");
            } catch (Exception e) {
                e.printStackTrace();
                writer.write("<p style='color:red'>Failed to reset the cache of the items-browser configuration! </p>");
            }
            writer.write("</body></html>");
        }

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
