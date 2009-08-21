package com.amalto.core.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;


/**
 * @author bgrieder
 * 
 *
 */

public class UploadFile extends HttpServlet {


    
    /**
     * UploadFile.java
     * Constructor
     * 
     */
    public UploadFile() {
        super();
    }

    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //start JCA Adapters
        
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
            doGet(request,response);
        }
    

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException,
        IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/plain; charset=\"UTF-8\"");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter writer = resp.getWriter();
        
        if (!FileUploadBase.isMultipartContent(req)) {
            throw new ServletException("Upload File Error: the request is not multipart!");
        }
        //Create a new file upload handler
        DiskFileUpload upload = new DiskFileUpload();

        //Set upload parameters
        upload.setSizeThreshold(0);
        upload.setSizeMax(-1);
        
        //Parse the request
        List<FileItem>  items; // FileItem
        try {
             items = upload.parseRequest(req);
        } catch(Exception e) {
            throw new ServletException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }

        File tempFile = File.createTempFile("xtentis-", ".tmp");
        
        //String returnURL=null;
        ArrayList<String> files = new ArrayList<String>();
        
        String filename=null;
        //Process the uploaded items
        Iterator<FileItem> iter = items.iterator();
        //while (iter.hasNext()) {
        	//FIXME: should handle more than files in parts e.g. text passed as parameter
            FileItem item = iter.next();
            if (item.isFormField()) {
            	//we are not expecting any field just (one) file(s)
            } else {
                try {
                    item.write(tempFile);
                    files.add(tempFile.getAbsolutePath());
                } catch(Exception e) {
                    throw new ServletException(e.getClass().getName()+": "+e.getLocalizedMessage());
                }                    
            }//if field
        //}// while item
        
        writer.write(filename);
        writer.close();
        
    }
    
  
}