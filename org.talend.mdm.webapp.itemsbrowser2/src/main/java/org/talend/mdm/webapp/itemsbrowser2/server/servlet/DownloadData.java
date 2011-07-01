package org.talend.mdm.webapp.itemsbrowser2.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.webapp.itemsbrowser2.server.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.DownloadBaseModel;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSGetItems;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;

public class DownloadData extends HttpServlet{

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName"); //$NON-NLS-1$
        response.reset();
        response.setContentType("application/vnd.ms-excel"); //$NON-NLS-1$
        String theReportFile = tableName + ".xls"; //$NON-NLS-1$
        response.setHeader("Content-Disposition", "attachment; filename=\"" + theReportFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Talend MDM"); //$NON-NLS-1$
        sheet.setDefaultColumnWidth((short) 20);
        String[] fieldNames = null;
        try {
            fieldNames = getTableFieldNames(tableName);
        } catch (Exception e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
        List<DownloadBaseModel> list = this.getTableContent(tableName, fieldNames);
        
        HSSFCellStyle cs = wb.createCellStyle();
        HSSFFont f = wb.createFont();
        f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        cs.setFont(f);
        HSSFRow row = sheet.createRow((short) 0);
        for (int i = 0; i < fieldNames.length; i++) {
            row.createCell((short) i).setCellValue(fieldNames[i]);
        }

        for (int i = 0; i < fieldNames.length; i++) {
            row.getCell((short) i).setCellStyle(cs);
        }

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow((short) i + 1);
            for (int j = 0; j < fieldNames.length; j++) {
                String tmp = list.get(i).get(fieldNames[j]);
                if (tmp != null) {
                    tmp = tmp.trim();
                    tmp = tmp.replaceAll("__h", ""); //$NON-NLS-1$ //$NON-NLS-2$
                    tmp = tmp.replaceAll("h__", ""); //$NON-NLS-1$//$NON-NLS-2$
                } else {
                    tmp = ""; //$NON-NLS-1$
                }
                row.createCell((short) j).setCellValue(tmp);
            }
        }

        OutputStream out = response.getOutputStream();
        wb.write(out);
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    public String getCurrentDataModel() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getModel();
    }

    public String getCurrentDataCluster() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getCluster();
    }

    private List<DownloadBaseModel> getTableContent(String tableName, String[] fieldNames) throws ServletException {
        try {

            int count = CommonUtil.getPort()
                    .getItems(new WSGetItems(new WSDataClusterPK(this.getCurrentDataCluster()), tableName, null, -1, 0, -1))
                    .getStrings().length;

            String[] results = CommonUtil.getPort()
                    .getItems(new WSGetItems(new WSDataClusterPK(this.getCurrentDataCluster()), tableName, null, -1, 0, count))
                    .getStrings();

            List<DownloadBaseModel> list = new ArrayList<DownloadBaseModel>();

            for (int i = 0; i < results.length; i++) {
                DownloadBaseModel model = new DownloadBaseModel();
                Element element = DocumentHelper.parseText(results[i]).getRootElement();

                for (int k = 0; k < fieldNames.length; k++) {
                    Node node = element.element(fieldNames[k]);
                    String value = null;
                    if (node != null)
                        value = node.getText();
                    model.set(fieldNames[k], value != null ? value : "");//$NON-NLS-1$
                }
                list.add(model);
            }
            return list;

        } catch (RemoteException e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        } catch (Exception e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    private String[] getTableFieldNames(String tableName) throws Exception {
        try {
            // grab the table fileds (e.g. the concept sub-elements)
            String schema = CommonUtil.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(this.getCurrentDataModel())))
                    .getXsdSchema();

            XSOMParser parser = new XSOMParser();
            parser.parse(new StringReader(schema));
            XSSchemaSet xss = parser.getResult();

            XSElementDecl decl;
            decl = xss.getElementDecl("", tableName);//$NON-NLS-1$
            if (decl == null) {
                throw new Exception(""); //$NON-NLS-1$
            }
            XSComplexType type = (XSComplexType) decl.getType();
            XSParticle[] xsp = type.getContentType().asParticle().getTerm().asModelGroup().getChildren();
            ArrayList<String> fieldNames = new ArrayList<String>();
            for (int i = 0; i < xsp.length; i++) {
                fieldNames.add(xsp[i].getTerm().asElementDecl().getName());
            }
            return fieldNames.toArray(new String[fieldNames.size()]);
        } catch (Exception e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

}
