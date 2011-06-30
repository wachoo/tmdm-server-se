package org.talend.mdm.webapp.itemsbrowser2.server.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSPutItem;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

/**
 * 
 * @author asaintguilhem
 * 
 *read excel and csv file
 */

@SuppressWarnings("serial")
public class UploadData extends HttpServlet {

    private static final Logger LOG = Logger.getLogger(UploadData.class);

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.crossreference.client.i18n.CrossreferenceMessages", UploadData.class.getClassLoader()); //$NON-NLS-1$

    public UploadData() {
        super();
    }

    public String getCurrentDataModel() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getModel();
    }

    public String getCurrentDataCluster() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getCluster();
    }

    @Override
    protected void doGet(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
        doPost(arg0, arg1);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String concept = "";//$NON-NLS-1$
        String fileType = "";//$NON-NLS-1$
        String sep = ",";//$NON-NLS-1$
        String textDelimiter = "\"";//$NON-NLS-1$
        String language = "en"; // default//$NON-NLS-1$
        String encoding = "utf-8";//$NON-NLS-1$
        boolean headersOnFirstLine = true;
        int lineNum = 0;
        PrintWriter writer = response.getWriter();

        request.setCharacterEncoding("UTF-8");//$NON-NLS-1$

        try {
            if (!FileUploadBase.isMultipartContent(request)) {
                throw new ServletException(MESSAGES.getMessage("error_upload"));//$NON-NLS-1$
            }
            // Create a new file upload handler
            DiskFileUpload upload = new DiskFileUpload();

            // Set upload parameters
            upload.setSizeThreshold(0);
            upload.setSizeMax(-1);

            // Parse the request
            List items; // FileItem

            items = upload.parseRequest(request);

            String path = "/tmp/";//$NON-NLS-1$
            if (System.getProperty("os.name").toLowerCase().toLowerCase().matches(".*windows.*"))//$NON-NLS-1$//$NON-NLS-2$
                path = "c:/tmp/";//$NON-NLS-1$

            SimpleDateFormat sd = new SimpleDateFormat("yyyyMMyy-HHmmssSSS"); //$NON-NLS-1$     
            String fileId = sd.format(new Date(System.currentTimeMillis()));

            File file = null;
            // Process the uploaded items
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                // FIXME: should handle more than files in parts e.g. text passed as parameter
                FileItem item = (FileItem) iter.next();
                if (item.isFormField()) {
                    // we are not expecting any field just (one) file(s)
                    String name = item.getFieldName();
                    LOG.debug("doPost() Field: '" + name + "' - value:'" + item.getString() + "'");
                    if (name.equals("concept"))//$NON-NLS-1$
                        concept = item.getString();
                    if (name.equals("fileType"))//$NON-NLS-1$
                        fileType = item.getString();
                    if (name.equals("sep"))//$NON-NLS-1$
                        sep = item.getString();
                    if (name.equals("delimiter"))//$NON-NLS-1$
                        textDelimiter = item.getString();
                    if (name.equals("language"))//$NON-NLS-1$
                        language = item.getString();
                    if (name.equals("encodings"))//$NON-NLS-1$
                        encoding = item.getString();
                    if (name.equals("headersOnFirstLine"))//$NON-NLS-1$
                        headersOnFirstLine = "on".equals(item.getString());//$NON-NLS-1$
                } else {

                    file = File.createTempFile("upload", "tmp");//$NON-NLS-1$//$NON-NLS-2$
                    LOG.debug("doPost() data uploaded in " + file.getAbsolutePath());
                    file.deleteOnExit();
                    item.write(file);
                }// if field
            }// while item

            if ("excel".equals(fileType.toLowerCase())) {//$NON-NLS-1$

                String[] fields = getTableFieldNames(concept);
                POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
                HSSFWorkbook wb = new HSSFWorkbook(fs);
                HSSFSheet sh = wb.getSheetAt(0);
                Iterator it = sh.rowIterator();
                while (it.hasNext()) {
                    if (++lineNum == 1 && headersOnFirstLine)
                        continue;
                    HSSFRow row = (HSSFRow) it.next();
                    StringBuffer xml = new StringBuffer();
                    boolean allCellsEmpty = true;
                    xml.append("<" + concept + ">");//$NON-NLS-1$//$NON-NLS-2$
                    for (int i = 0; i < fields.length; i++) {
                        xml.append("<" + fields[i] + ">");//$NON-NLS-1$//$NON-NLS-2$
                        HSSFCell tmpCell = row.getCell((short) i);
                        int cellType = tmpCell.getCellType();
                        String cellValue = "";//$NON-NLS-1$
                        switch (cellType) {
                        case HSSFCell.CELL_TYPE_NUMERIC: {
                            double tmp = tmpCell.getNumericCellValue();
                            cellValue = getStringRepresentation(tmp);
                            break;
                        }
                        case HSSFCell.CELL_TYPE_STRING: {
                            cellValue = tmpCell.getRichStringCellValue().getString();
                            break;
                        }
                        case HSSFCell.CELL_TYPE_BOOLEAN: {
                            boolean tmp = tmpCell.getBooleanCellValue();
                            if (tmp)
                                cellValue = "true";//$NON-NLS-1$
                            else
                                cellValue = "false";//$NON-NLS-1$
                            break;
                        }
                        case HSSFCell.CELL_TYPE_FORMULA: {
                            cellValue = tmpCell.getCellFormula();
                            break;
                        }
                        case HSSFCell.CELL_TYPE_ERROR: {
                            break;
                        }
                        case HSSFCell.CELL_TYPE_BLANK: {
                        }
                        default: {
                        }
                        }

                        if (cellValue != null && !"".equals(cellValue))//$NON-NLS-1$
                            allCellsEmpty = false;

                        xml.append(StringEscapeUtils.escapeXml(cellValue));
                        xml.append("</" + fields[i] + ">");//$NON-NLS-1$//$NON-NLS-2$
                    }
                    xml.append("</" + concept + ">");//$NON-NLS-1$//$NON-NLS-2$

                    // put document (except empty lines)
                    if (!allCellsEmpty)
                        putDocument(xml.toString());
                }

            } else if ("csv".equals(fileType.toLowerCase())) {
                String[] fields = getTableFieldNames(concept);
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));//$NON-NLS-1$
                while ((line = br.readLine()) != null) {
                    if (++lineNum == 1 && headersOnFirstLine)
                        continue;
                    StringBuffer xml = new StringBuffer();
                    xml.append("<" + concept + ">");//$NON-NLS-1$//$NON-NLS-2$
                    String separator = ",";//$NON-NLS-1$
                    if ("semicolon".equals(sep))//$NON-NLS-1$
                        separator = ";";//$NON-NLS-1$
                    String[] splits = line.split(separator);
                    // rebuild the values by checking delimiters
                    ArrayList<String> values = new ArrayList<String>();
                    if (textDelimiter == null || "".equals(textDelimiter.trim())) {//$NON-NLS-1$
                        values.addAll(Arrays.asList(splits));
                    } else {
                        String currentText = "";//$NON-NLS-1$
                        boolean textOpened = false;
                        for (int j = 0; j < splits.length; j++) {
                            if (splits[j].startsWith(textDelimiter)) {
                                if (splits[j].endsWith(textDelimiter)) {
                                    // we have a full text
                                    values.add(splits[j].substring(textDelimiter.length(), splits[j].length()
                                            - textDelimiter.length()));
                                } else {
                                    // we have the beginning of a text
                                    textOpened = true;
                                    currentText += splits[j].substring(textDelimiter.length());
                                }
                            } else {
                                if (splits[j].endsWith(textDelimiter) && !splits[j].endsWith("\\" + textDelimiter)) {//$NON-NLS-1$
                                    // we are finishing a text
                                    currentText += separator
                                            + splits[j].substring(0, splits[j].length() - textDelimiter.length());
                                    values.add(currentText);
                                    currentText = "";//$NON-NLS-1$
                                    textOpened = false;
                                } else {
                                    if (textOpened) {
                                        // the continuation of a text
                                        currentText += separator + splits[j];
                                    } else {
                                        // a number or not delimited string
                                        values.add(splits[j]);
                                    }
                                }
                            }
                        }
                    }
                    // build xml
                    if (values.size() > 0) {
                        for (int j = 0; j < fields.length; j++) {
                            xml.append("<" + fields[j] + ">");//$NON-NLS-1$//$NON-NLS-2$
                            if (j < values.size())
                                xml.append(StringEscapeUtils.escapeXml(values.get(j)));
                            xml.append("</" + fields[j] + ">");//$NON-NLS-1$//$NON-NLS-2$
                        }
                    }
                    xml.append("</" + concept + ">");//$NON-NLS-1$//$NON-NLS-2$
                    LOG.debug("Added line " + lineNum);//$NON-NLS-1$
                    LOG.trace("--val:\n" + xml);//$NON-NLS-1$
                    // put document
                    putDocument(xml.toString());
                }
            }
            writer.print("true");//$NON-NLS-1$
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            writer.print(MESSAGES.getMessage("error_import", lineNum, e.getClass().getName(), e//$NON-NLS-1$
                    .getLocalizedMessage()));
            throw new ServletException(MESSAGES.getMessage("error_import", lineNum, e.getClass().getName(), e//$NON-NLS-1$
                    .getLocalizedMessage()));
        } finally {
            writer.close();
        }

    }

    private String[] getTableFieldNames(String tableName) {
        try {
            // grab the table fileds (e.g. the concept sub-elements)
            String schema = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(this.getCurrentDataModel())))
                    .getXsdSchema();

            XSOMParser parser = new XSOMParser();
            parser.parse(new StringReader(schema));
            XSSchemaSet xss = parser.getResult();

            XSElementDecl decl;
            decl = xss.getElementDecl("", tableName);//$NON-NLS-1$
            if (decl == null) {
                return null;
            }
            XSComplexType type = (XSComplexType) decl.getType();
            XSParticle[] xsp = type.getContentType().asParticle().getTerm().asModelGroup().getChildren();
            ArrayList<String> fieldNames = new ArrayList<String>();
            for (int i = 0; i < xsp.length; i++) {
                fieldNames.add(xsp[i].getTerm().asElementDecl().getName());
            }
            return fieldNames.toArray(new String[fieldNames.size()]);
        } catch (Exception e) {
            return null;
        }
    }

    private void putDocument(String xml) throws ServletException {
        try {
            Util.getPort().putItem(
                    new WSPutItem(new WSDataClusterPK(this.getCurrentDataModel()), xml.toString(), new WSDataModelPK(this
                            .getCurrentDataModel()), false));
        } catch (RemoteException e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        } catch (Exception e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }
    }

    /*
     * Returns a string corresponding to the double value given in parameter Exponent is removed and "0" are added at
     * the end of the string if necessary This method is useful when you import long itemid that you don't want to see
     * modified by importation method.
     */
    private String getStringRepresentation(double value) {
        String result = "";

        result = Double.toString(value);

        int index = result.indexOf("E");//$NON-NLS-1$

        String base = result;

        if (index > 0) {
            try {
                base = result.substring(0, index);
                String puissance = result.substring(index + 1);

                int puissanceValue = Integer.parseInt(puissance);

                int indexPoint = base.indexOf(".");//$NON-NLS-1$

                if (indexPoint > 0) {
                    String beforePoint = base.substring(0, indexPoint);
                    String afterPoint = base.substring(indexPoint + 1);

                    if (puissanceValue >= afterPoint.length()) {
                        base = beforePoint + "" + afterPoint;//$NON-NLS-1$
                        puissanceValue -= afterPoint.length();
                    } else {
                        String newBeforePoint = beforePoint + "" + afterPoint.substring(0, puissanceValue);//$NON-NLS-1$
                        String newAfterPoint = afterPoint.substring(puissanceValue);
                        base = newBeforePoint + "." + newAfterPoint;//$NON-NLS-1$
                        puissanceValue = 0;
                    }
                }

                for (int j = 0; j < puissanceValue; j++) {
                    base += "0";//$NON-NLS-1$
                }

                result = base;

            } catch (NumberFormatException e) {
            }
        }
        return result;
    }

}
