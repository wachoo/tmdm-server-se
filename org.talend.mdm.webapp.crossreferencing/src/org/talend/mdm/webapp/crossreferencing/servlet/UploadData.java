package org.talend.mdm.webapp.crossreferencing.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.talend.mdm.webapp.crossreferencing.bean.Configuration;
import org.talend.mdm.webapp.crossreferencing.bean.CrossReferencingTableDescription;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSPutItem;

/**
 *
 * @author asaintguilhem
 *
 *read excel and csv file
 */

@SuppressWarnings("serial")
public class UploadData  extends HttpServlet {


	public UploadData() {
		super();
	}

	@Override
	protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doPost(arg0, arg1);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("doPost() ");
		String concept = "";
		String fileType = "";
		String sep = ",";
		String textDelimiter = "\"";
		String language="en"; //default
		String encoding = "utf-8";
		boolean headersOnFirstLine = true;

        request.setCharacterEncoding("UTF-8");

        if (!FileUploadBase.isMultipartContent(request)) {
            throw new ServletException("Upload File Error: the request is not multipart!");
        }
        //Create a new file upload handler
        DiskFileUpload upload = new DiskFileUpload();

        //Set upload parameters
        upload.setSizeThreshold(0);
        upload.setSizeMax(-1);

        //Parse the request
        List  items; // FileItem
        try {
             items = upload.parseRequest(request);
        } catch(Exception e) {
            throw new ServletException(e.getClass().getName()+": "+e.getLocalizedMessage());
        }



    	String path= "/tmp/";
        if (System.getProperty("os.name").toLowerCase().toLowerCase().matches(".*windows.*"))	path = "c:/tmp/";

        SimpleDateFormat sd = new SimpleDateFormat("yyyyMMyy-HHmmssSSS");
        //String fileId= DocumentCtrl2Util.generateGUID(this)+"-"+sd.format(new Date(System.currentTimeMillis()));
        String fileId = sd.format(new Date(System.currentTimeMillis()));
        //String returnURL=null;
//        ArrayList files = new ArrayList();

        File file=null;
        //Process the uploaded items
        Iterator iter = items.iterator();
        while (iter.hasNext()) {
        	//FIXME: should handle more than files in parts e.g. text passed as parameter
            FileItem item = (FileItem) iter.next();
            if (item.isFormField()) {
            	//we are not expecting any field just (one) file(s)
				String name = item.getFieldName();
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("doPost() Field: '"+name+"' - value:'"+item.getString()+"'");
				if(name.equals("concept")) concept = item.getString();
				if(name.equals("fileType")) fileType = item.getString();
				if(name.equals("sep")) sep = item.getString();
				if(name.equals("delimiter")) textDelimiter = item.getString();
				if(name.equals("language")) language = item.getString();
				if(name.equals("encoding")) encoding = item.getString();
				if(name.equals("headersOnFirstLine")) headersOnFirstLine = "on".equals(item.getString());
            } else {
                try {
                    file=File.createTempFile("upload", "tmp");
                    org.apache.log4j.Logger.getLogger(this.getClass()).debug("doPost() data uploaded in "+file.getAbsolutePath());
                    file.deleteOnExit();
                    item.write(file);
                } catch(Exception e) {
                    throw new ServletException(e.getClass().getName()+": "+e.getLocalizedMessage());
                }
            }//if field
        }// while item


        if("excel".equals(fileType)){
        	int lineNum = 0;
    		try {
    			//String[] fields = getCrossReferencingTableDescription(concept).getFields();
    			String[] fields = CrossReferencingTableDescription.getCrossReferencingTableDescription(concept).getFields();
    	        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
    	        HSSFWorkbook wb = new HSSFWorkbook(fs);
    	        HSSFSheet sh = wb.getSheetAt(0);
    	        Iterator it = sh.rowIterator();
    	        while(it.hasNext()){
    	        	if (++lineNum == 1 && headersOnFirstLine) continue;
    	        	HSSFRow row = (HSSFRow) it.next();
    	        	StringBuffer xml = new StringBuffer();
    	        	boolean allCellsEmpty = true;
    	        	xml.append("<"+concept+">");
    	            for (int i=0;i<fields.length;i++) {
    	            	xml.append("<"+fields[i]+">");
    	            	HSSFCell tmpCell = row.getCell((short) i);
    	            	int cellType = tmpCell.getCellType();
    	            	String cellValue = "";
    	            	switch (cellType) {
	    	            	case HSSFCell.CELL_TYPE_NUMERIC : {
	    	            		double tmp = tmpCell.getNumericCellValue();
	    	            		cellValue = getStringRepresentation(tmp);
	    	            		break;
	    	            	}
	    	            	case HSSFCell.CELL_TYPE_STRING : {
	    	            		cellValue = tmpCell.getRichStringCellValue().getString();
	    	            		break;
	    	            	}
	    	            	case HSSFCell.CELL_TYPE_BOOLEAN : {
	    	            		boolean tmp = tmpCell.getBooleanCellValue();
	    	            		if (tmp)
	    	            			cellValue = "true";
	    	            		else
	    	            			cellValue = "false";
	    	            		break;
	    	            	}
	    	            	case HSSFCell.CELL_TYPE_FORMULA : {
	    	            		cellValue = tmpCell.getCellFormula();
	    	            		break;
	    	            	}
	    	            	case HSSFCell.CELL_TYPE_ERROR : {
	    	            		break;
	    	            	}
	    	            	case HSSFCell.CELL_TYPE_BLANK : {
	    	            	}
    	            	default : {}
    	            	}

    	            	if (cellValue!=null && !"".equals(cellValue))
    	            		allCellsEmpty = false;

    	            	//xml.append(row.getCell((short) i).getStringCellValue());
    	            	xml.append(StringEscapeUtils.escapeXml(cellValue));
    	            	xml.append("</"+fields[i]+">");
    				}
    	            xml.append("</"+concept+">");

                	//put document (except empty lines)
    	            if (!allCellsEmpty)
    	            	putDocument(xml.toString());
//    	            System.out.println(xml.toString());
    	        }
    		} catch (XtentisWebappException e) {
    			// TODO Auto-generated catch block
    			throw new ServletException("Error importing CSV on line "+lineNum+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			throw new ServletException("Error importing CSV on line "+lineNum+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
    		}
        }
        else if("csv".equals(fileType)){
        	int lineNum = 0;
    		try {
    			String[] fields = CrossReferencingTableDescription.getCrossReferencingTableDescription(concept).getFields();
    			String line;
    			BufferedReader br = new BufferedReader (new InputStreamReader(new FileInputStream(file), "utf-8"));
    		    while ((line = br.readLine()) != null){
    		    	if (++lineNum == 1 && headersOnFirstLine) continue;
    	        	StringBuffer xml = new StringBuffer();
    	        	xml.append("<"+concept+">");
    	        	String separator = ",";
    	        	if("semicolon".equals(sep)) separator=";";
    	        	String[] splits = line.split(separator);
    	        	//rebuild the values by checking delimiters
    	        	ArrayList<String> values = new ArrayList<String>();
    	        	if (textDelimiter == null || "".equals(textDelimiter.trim())) {
    	        		values.addAll(Arrays.asList(splits));
    	        	} else {
        	        	String currentText="";
        	        	boolean textOpened = false;
        	        	for (int j = 0; j < splits.length; j++) {
    	                    if (splits[j].startsWith(textDelimiter)) {
    	                    	if (splits[j].endsWith(textDelimiter)) {
    	                    		//we have a full text
    	                    		values.add(splits[j].substring(textDelimiter.length(), splits[j].length()-textDelimiter.length()));
    	                    	} else {
    	                    		//we have the beginning of a text
    	                    		textOpened = true;
    	                    		currentText+=splits[j].substring(textDelimiter.length());
    	                    	}
    	                    } else {
    	                    	if (splits[j].endsWith(textDelimiter) && !splits[j].endsWith("\\"+textDelimiter) ) {
    	                    		//we are finishing a text
    	                    		currentText += separator+splits[j].substring(0, splits[j].length()-textDelimiter.length());
    	                    		values.add(currentText);
    	                    		currentText = "";
    	                    		textOpened = false;
    	                    	} else {
    	                    		if (textOpened) {
    	                    			//the continuation of a text
    	                    			currentText+=separator+splits[j];
    	                    		} else {
    	                    			//a number or not delimited string
    	                    			values.add(splits[j]);
    	                    		}
    	                    	}
    	                    }
                        }
    	        	}
    	        	//build xml
    	        	if (values.size()>0) {
	    	            for (int j=0; j<fields.length; j++) {
	    	            	xml.append("<"+fields[j]+">");
	    	            	if (j<values.size()) xml.append(StringEscapeUtils.escapeXml(values.get(j)));
	    	            	xml.append("</"+fields[j]+">");
	    				}
    	        	}
    	            xml.append("</"+concept+">");
    	            org.apache.log4j.Logger.getLogger(this.getClass()).debug("Added line "+lineNum);
    	            org.apache.log4j.Logger.getLogger(this.getClass()).trace("--val:\n"+xml);
                	//put document
    	            putDocument(xml.toString());
    	        }
    		} catch (XtentisWebappException e) {
    			throw new ServletException("Error importing CSV on line "+lineNum+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
    		} catch (Exception e) {
    			e.printStackTrace();
    			throw new ServletException("Error importing CSV on line "+lineNum+": "+e.getClass().getName()+": "+e.getLocalizedMessage());
    		}
        }

        //response.setContentType("text/html");
		ServletContext ctx = request.getSession().getServletContext().getContext("/b2box");
		RequestDispatcher dispatcher = ctx.getRequestDispatcher(
				"/secure/?context=crossreferencing&applicationName=CrossReferencing"
				+"&language="+language
		);
	    // forward the request to the dispatcher
	    //dispatcher.forward(request, response);
		PrintWriter writer = response.getWriter();
        writer.write("true");
        writer.close();
    }


	/**
	 * Return a specific Cross Referencing table description (keys and fields)
	 * @param concept
	 * @return the requested CorossReferencing table
	 * @throws XtentisWebappException
	 * @throws Exception
	 */
	/*
	private CrossReferencingTableDescription getCrossReferencingTableDescription(String concept) throws XtentisWebappException, Exception{
		String xsd = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(ControllerServlet.datamodel))).getXsdSchema();
		Document document = Util.parse(xsd);
		NodeList list = Util.getNodeList(document,"xsd:element");
		for(int i=0;i<list.getLength();i++){
			CrossReferencingTableDescription crossReferencing = new CrossReferencingTableDescription();
			crossReferencing.setName(list.item(i).getAttributes().getNamedItem("name").toString().replaceAll("name=\"(.*?)\"","$1"));
			Node node = Util.getNodeList(list.item(i),"xsd:complexType").item(0);
			Node node2 = Util.getNodeList(node,"xsd:sequence").item(0);
			NodeList list2 = Util.getNodeList(node2,"xsd:element");
			String[] fields = new String[list2.getLength()];
			ArrayList keys = new ArrayList();
			for(int j=0;j<list2.getLength();j++){
				fields[j] = list2.item(j).getAttributes().getNamedItem("name").toString().replaceAll("name=\"(.*?)\"","$1");
				//System.out.println("field "+j+" "+fields[j]);
				String minOccurs = list2.item(j).getAttributes().getNamedItem("minOccurs").toString().replaceAll("minOccurs=\"(.*?)\"","$1");
				if(minOccurs.equals("1")) {
					keys.add(fields[j]);
					//System.out.println("key "+fields[j]);
				}
			}
			crossReferencing.setFields(fields);
			crossReferencing.setKeys((String[])(keys.toArray(new String[keys.size()])));
			if(crossReferencing.getName().equals(concept)) return crossReferencing;
		}
		return null;
	}
	*/

	private void putDocument(String xml) throws ServletException{
        try {
        	Util.getPort().putItem(
			        new WSPutItem(
			        		new WSDataClusterPK(Configuration.datacluster),
			                xml.toString(),
			                new WSDataModelPK(Configuration.datamodel),false
			        )
			);
		} catch (RemoteException e) {
			throw new ServletException(e.getClass().getName()+": "+e.getLocalizedMessage());
		} catch (Exception e) {
			throw new ServletException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}


	/*
	 * Returns a string corresponding to the double value given in parameter
	 * Exponent is removed and "0" are added at the end of the string if necessary
	 * This method is useful when you import long itemid that you don't want to see
	 * modified by importation method.
	 * */
	private String getStringRepresentation(double value) {
		String result = "";

		result = Double.toString(value);


		int index = result.indexOf("E");

		String base = result;

		if (index >0) {
			try {
			base = result.substring(0, index);
			String puissance = result.substring(index+1);


			int puissanceValue = Integer.parseInt(puissance);

			int indexPoint = base.indexOf(".");

			if (indexPoint > 0) {
				String beforePoint = base.substring(0, indexPoint);
				String afterPoint = base.substring(indexPoint+1);

				if (puissanceValue >= afterPoint.length()) {
					base = beforePoint+""+afterPoint;
					puissanceValue -= afterPoint.length();
				} else {
					String newBeforePoint = beforePoint+""+afterPoint.substring(0,puissanceValue);
					String newAfterPoint = afterPoint.substring(puissanceValue);
					base = newBeforePoint+"."+newAfterPoint;
					puissanceValue = 0;
				}
			}

			for (int j = 0; j< puissanceValue; j++) {
				base += "0";
			}

			result = base;

			} catch (NumberFormatException e) {}
		}
		return result;
	}


}