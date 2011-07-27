package org.talend.mdm.webapp.itemsbrowser2.server.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.webapp.itemsbrowser2.client.util.ViewUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.RoleHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.ViewHelper;
import org.talend.mdm.webapp.itemsbrowser2.server.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.server.util.XmlUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;
import com.amalto.webapp.util.webservices.WSViewSearch;

public class DownloadData extends HttpServlet{

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName"); //$NON-NLS-1$
        String language = request.getParameter("language"); //$NON-NLS-1$
        
        ViewBean viewBean = null;
        try {
            viewBean = getView(tableName, language);
        } catch (Exception e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
        
        String concept = ViewHelper.getConceptFromDefaultViewName(viewBean.getViewPK());
        
        response.reset();
        response.setContentType("application/vnd.ms-excel"); //$NON-NLS-1$
        String theReportFile = concept + ".xls"; //$NON-NLS-1$
        response.setHeader("Content-Disposition", "attachment; filename=\"" + theReportFile + "\""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Talend MDM"); //$NON-NLS-1$
        sheet.setDefaultColumnWidth((short) 20);
        
        List<String> fieldNameList = new ArrayList<String>();
        List<String> viewableXpaths = viewBean.getViewableXpaths();
        EntityModel entityModel = viewBean.getBindingEntityModel();
        Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
        for (String xpath : viewableXpaths) {
            TypeModel typeModel = dataTypes.get(xpath);
            fieldNameList.add(ViewUtil.getViewableLabel(language, typeModel));
        }
        
        String[] fieldNames = new String[fieldNameList.size()];
        for(int i=0; i<fieldNameList.size(); i++)
            fieldNames[i] = fieldNameList.get(i);
                
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
        
        try {
            this.getTableContent(viewBean,concept, sheet);
        } catch (Exception e) {
            e.printStackTrace();
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

    private void getTableContent(ViewBean viewBean, String concept, HSSFSheet sheet) throws Exception {          
        List<String> xPathList = viewBean.getViewableXpaths();
        String[] results = CommonUtil.getPort().viewSearch(new WSViewSearch(new WSDataClusterPK(getCurrentDataCluster()), 
                new WSViewPK(viewBean.getViewPK()), null, -1, 0, -1, null, null)).getStrings();

        for(int i=1; i<results.length; i++){
            Document doc = parseResultDocument(results[i], "result"); //$NON-NLS-1$
            HSSFRow row = sheet.createRow((short) i);
            int colCount = 0;
            for(String xpath : xPathList){
                Node dateNode = XmlUtil.queryNode(doc, xpath.replaceFirst(concept + "/", "result/")); //$NON-NLS-1$ //$NON-NLS-2$
                String tmp = dateNode.getText();
                if (tmp != null) {
                    tmp = tmp.trim();
                    tmp = tmp.replaceAll("__h", ""); //$NON-NLS-1$ //$NON-NLS-2$
                    tmp = tmp.replaceAll("h__", ""); //$NON-NLS-1$//$NON-NLS-2$
                }else{
                    tmp = ""; //$NON-NLS-1$
                }
                row.createCell((short) colCount).setCellValue(tmp);
                colCount++;
            }
        }  
    }
    
    public ViewBean getView(String viewPk, String language) throws Exception {
        try {

            ViewBean vb = new ViewBean();
            vb.setViewPK(viewPk);

            // get WSView
            WSView wsView = CommonUtil.getPort().getView(new WSGetView(new WSViewPK(viewPk)));

            // bind entity model
            String model = getCurrentDataModel();
            String concept = ViewHelper.getConceptFromDefaultViewName(viewPk);
            EntityModel entityModel = new EntityModel();
            DataModelHelper.parseSchema(model, concept, entityModel, RoleHelper.getUserRoles());
            vb.setBindingEntityModel(entityModel);

            // viewables
            String[] viewables = ViewHelper.getViewables(wsView);
            // FIXME remove viewableXpath
            if (viewables != null) {
                for (String viewable : viewables) {
                    vb.addViewableXpath(viewable);
                }
            }
            vb.setViewables(viewables);

            // searchables
            vb.setSearchables(ViewHelper.getSearchables(wsView, model, language, entityModel));

            return vb;
        } catch (XtentisWebappException e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        } catch (Exception e) {
            throw new ServletException(e.getClass().getName() + ": " + e.getLocalizedMessage()); //$NON-NLS-1$
        }
    }

    private Document parseResultDocument(String result, String expectedRootElement) throws DocumentException {
        Document doc = XmlUtil.parseText(result);
        Element rootElement = doc.getRootElement();
        if (!rootElement.getName().equals(expectedRootElement)) {
            // When there is a null value in fields, the viewable fields sequence is not enclosed by expected element
            // FIXME Better to find out a solution at the underlying stage
            rootElement.detach();
            Element resultElement = doc.addElement(expectedRootElement);
            resultElement.add(rootElement);
        }
        return doc;
    }
}
