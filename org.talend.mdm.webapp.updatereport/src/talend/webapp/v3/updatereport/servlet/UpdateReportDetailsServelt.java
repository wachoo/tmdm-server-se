package talend.webapp.v3.updatereport.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import talend.webapp.v3.updatereport.bean.DataChangeLog;
import talend.webapp.v3.updatereport.dwr.UpdateReportDWR;

import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.json.JSONArray;
import com.amalto.webapp.core.json.JSONObject;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.sun.xml.xsom.XSAnnotation;
import com.sun.xml.xsom.XSComplexType;
import com.sun.xml.xsom.XSContentType;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSTerm;
import com.sun.xml.xsom.XSType;

public class UpdateReportDetailsServelt extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String ids = req.getParameter("ids");
        String export = req.getParameter("params");

        String jsonTree = "";
        JSONObject json = new JSONObject();

        if (ids != null && ids.length() > 0) {
            try {
                WSDataClusterPK wsDataClusterPK = new WSDataClusterPK(XSystemObjects.DC_UPDATE_PREPORT.getName());
                String conceptName = "Update";// Hard Code
                String[] idss = ids.split("\\.");
                WSGetItem wsGetItem = new WSGetItem(new WSItemPK(wsDataClusterPK, conceptName, idss));
                WSItem wsItem = Util.getPort().getItem(wsGetItem);
                String content = wsItem.getContent();
                if (content != null && content.length() > 0) {
                    // no recursion
                    Document doc = Util.parse(content);
                    String userName = Util.getFirstTextNode(doc, "/Update/UserName");
                    String source = Util.getFirstTextNode(doc, "/Update/Source");
                    String timeInMillis = Util.getFirstTextNode(doc, "/Update/TimeInMillis");
                    String operationType = Util.getFirstTextNode(doc, "/Update/OperationType");
                    String revisionID = Util.getFirstTextNode(doc, "/Update/RevisionID");
                    String dataCluster = Util.getFirstTextNode(doc, "/Update/DataCluster");
                    String dataModel = Util.getFirstTextNode(doc, "/Update/DataModel");
                    String concept = Util.getFirstTextNode(doc, "/Update/Concept");
                    String key = Util.getFirstTextNode(doc, "/Update/Key");

                    ArrayList<JSONObject> rootGroup = new ArrayList<JSONObject>();
                    JSONObject userNameNode = new JSONObject();
                    userNameNode.put("id", "userName");
                    userNameNode.put("text", "UserName:" + cleanOutput(userName));
                    userNameNode.put("leaf", true);
                    rootGroup.add(userNameNode);

                    JSONObject sourceNode = new JSONObject();
                    sourceNode.put("id", "source");
                    sourceNode.put("text", "Source:" + source);
                    sourceNode.put("leaf", true);
                    rootGroup.add(sourceNode);

                    JSONObject timeInMillisNode = new JSONObject();
                    timeInMillisNode.put("id", "timeInMillis");
                    timeInMillisNode.put("text", "TimeInMillis:" + timeInMillis);
                    timeInMillisNode.put("leaf", true);
                    rootGroup.add(timeInMillisNode);

                    JSONObject operationTypeNode = new JSONObject();
                    operationTypeNode.put("id", "operationType");
                    operationTypeNode.put("text", "OperationType:" + operationType);
                    operationTypeNode.put("leaf", true);
                    rootGroup.add(operationTypeNode);

                    JSONObject conceptNode = new JSONObject();
                    conceptNode.put("id", "concept");
                    conceptNode.put("text", "Concept:" + concept);
                    conceptNode.put("leaf", true);
                    rootGroup.add(conceptNode);

                    JSONObject revisionIDNode = new JSONObject();
                    revisionIDNode.put("id", "revisionID");
                    revisionIDNode.put("text", "RevisionID:" + cleanOutput(revisionID));
                    revisionIDNode.put("leaf", true);
                    rootGroup.add(revisionIDNode);

                    JSONObject dataClusterNode = new JSONObject();
                    dataClusterNode.put("id", "dataCluster");
                    dataClusterNode.put("text", "DataCluster:" + cleanOutput(dataCluster));
                    dataClusterNode.put("leaf", true);
                    rootGroup.add(dataClusterNode);

                    JSONObject dataModelNode = new JSONObject();
                    dataModelNode.put("id", "dataModel");
                    dataModelNode.put("text", "DataModel:" + cleanOutput(dataModel));
                    dataModelNode.put("leaf", true);
                    rootGroup.add(dataModelNode);

                    JSONObject keyNode = new JSONObject();
                    keyNode.put("id", "key");
                    keyNode.put("text", "Key:" + key);
                    keyNode.put("leaf", true);
                    rootGroup.add(keyNode);

                    NodeList ls = Util.getNodeList(doc, "/Update/Item");

                    if (ls.getLength() > 0) {

                        for (int i = 0; i < ls.getLength(); i++) {
                            String path = Util.getFirstTextNode(doc, "/Update/Item[" + (i + 1) + "]/path");
                            String elementPath = path.replaceAll("\\[\\d+\\]$", "");
                            FKInstance fkInstance = getRetrieveConf(dataModel, concept, elementPath);

                            String oldValue = Util.getFirstTextNode(doc, "/Update/Item[" + (i + 1) + "]/oldValue");

                            if (oldValue == null || oldValue.equals("null"))
                                oldValue = "";
                            String newValue = Util.getFirstTextNode(doc, "/Update/Item[" + (i + 1) + "]/newValue");

                            if (newValue == null || newValue.equals("null"))
                                newValue = "";

                            if (fkInstance.retireveFKInfo && !"".equals(oldValue) && fkInstance.getFkInfo() != null) {
                                oldValue = getFKInfoByRetrieveConf(dataCluster, fkInstance.getFkInfo(), oldValue);
                            }

                            if (fkInstance.retireveFKInfo && !"".equals(newValue) && fkInstance.getFkInfo() != null) {
                                newValue = getFKInfoByRetrieveConf(dataCluster, fkInstance.getFkInfo(), newValue);
                            }

                            JSONArray array = new JSONArray();
                            JSONObject pathNode = new JSONObject();
                            pathNode.put("id", "item" + i + "-path");
                            pathNode.put("text", "path:" + path);
                            pathNode.put("leaf", true);
                            array.put(pathNode);
                            JSONObject oldValueNode = new JSONObject();
                            oldValueNode.put("id", "item" + i + "-oldValue");
                            oldValueNode.put("text", "oldValue:" + oldValue);
                            oldValueNode.put("leaf", true);
                            array.put(oldValueNode);
                            JSONObject newValueNode = new JSONObject();
                            newValueNode.put("id", "item" + i + "-newValue");
                            newValueNode.put("text", "newValue:" + newValue);
                            newValueNode.put("leaf", true);
                            array.put(newValueNode);

                            JSONObject itemNode = new JSONObject();
                            itemNode.put("id", "item" + i);
                            itemNode.put("text", "Item");
                            itemNode.put("leaf", false);
                            itemNode.put("children", array);
                            rootGroup.add(itemNode);

                        }
                    }

                    json.put("head", rootGroup);
                    jsonTree = ((JSONArray) json.get("head")).toString();
                    resp.setCharacterEncoding("utf-8");
                    PrintWriter out = resp.getWriter();
                    out.println(jsonTree);
                    out.close();
                }
            } catch (XtentisWebappException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (export != null) {
            // export to excel
            org.apache.log4j.Logger.getLogger(this.getClass()).info("SERVLET Reporting export for excel ");

            DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            resp.reset();
            resp.setContentType("application/vnd.ms-excel");
            String theReportFile = "Journal_" + df.format(new Date()) + ".xls";
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + theReportFile + "\"");

            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("new sheet");
            sheet.setDefaultColumnWidth((short) 20);

            UpdateReportDWR report = new UpdateReportDWR();
            try {
                ListRange list = report.getUpdateReportList(0, 0, null, null, export);

                // create a cell style
                HSSFCellStyle cs = wb.createCellStyle();
                HSSFFont f = wb.createFont();
                f.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
                cs.setFont(f);
                HSSFRow row = sheet.createRow((short) 0);
                String[][] cols = new String[2][];
                cols[0] = new String[] { "Data Container", "Data Model", "Entity", "Key", "Revision ID", "Operation Type",
                        "Operation Time", "Source", "User Name" };
                cols[1] = new String[] { "Conteneur de données", "Modèle de données", "Entité", "Clé", "ID de révision", "Type d\'opération",
                        "Date d\'opération", "Source", "Utilisateur" };
                String language = req.getParameter("language") != null ? req.getParameter("language") : "en";
                if (language.equals("en"))
                    for (int i = 0; i < cols[0].length; i++) {
                        row.createCell((short) i).setCellValue(cols[0][i]);
                        row.getCell((short) i).setCellStyle(cs);
                    }
                else
                    for (int i = 0; i < cols[1].length; i++) {
                        row.createCell((short) i).setCellValue(cols[1][i]);
                        row.getCell((short) i).setCellStyle(cs);
                    }

                // populate sheet
                int i = 1;
                for (Object oo : list.getData()) {
                    row = sheet.createRow((short) i++);
                    DataChangeLog log = (DataChangeLog) oo;
                    // set each cell value
                    row.createCell((short) 0).setCellValue(log.getDataCluster());
                    row.createCell((short) 1).setCellValue(log.getDataModel());
                    row.createCell((short) 2).setCellValue(log.getConcept());
                    row.createCell((short) 3).setCellValue(log.getKey());
                    row.createCell((short) 4).setCellValue(log.getRevisionID());
                    row.createCell((short) 5).setCellValue(log.getOperationType());
                    row.createCell((short) 6).setCellValue(log.getTimeInMillis());
                    row.createCell((short) 7).setCellValue(log.getSource());
                    row.createCell((short) 8).setCellValue(log.getUserName());
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Write the output
            OutputStream out = resp.getOutputStream();
            resp.setCharacterEncoding("utf-8");
            wb.write(out);
            out.close();
        }
    }

    private String cleanOutput(String output) {

        if (output == null || output.equals("null"))
            output = "";

        output = output.trim();

        return output;
    }

    /**
     * get fkinfo value by specify rightValueOrPath. DOC Administrator Comment method "getFKInfoByRetrieveConf".
     * 
     * @param dataCluster
     * @param fkInfo
     * @param rightValueOrPath
     * @return
     */
    private String getFKInfoByRetrieveConf(String dataCluster, String fkInfo, String rightValueOrPath) {
        String fkInfoValue = "";
        String conceptName = fkInfo.substring(0, fkInfo.indexOf("/"));
        String value = rightValueOrPath;

        if (rightValueOrPath.indexOf("[") == 0 && rightValueOrPath.lastIndexOf("]") == rightValueOrPath.length() - 1) {
            value = rightValueOrPath.subSequence(1, rightValueOrPath.length() - 1).toString();
        }

        String ids[] = { value };

        try {
            WSItem wsItem = Util.getPort().getItem(
                    new WSGetItem(new WSItemPK(new WSDataClusterPK(dataCluster), conceptName, ids)));
            Document document = Util.parse(wsItem.getContent());
            NodeList list = Util.getNodeList(document, "/" + fkInfo);
            Node it = list.item(0);

            if (it != null) {
                fkInfoValue = it.getTextContent();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return fkInfoValue;
    }

    /**
     * get specify element fkinfo properties by specify datacluster concept and xpath. DOC Administrator Comment method
     * "getRetrieveConf".
     * 
     * @param dataModel
     * @param concept
     * @param path
     * @return
     * @throws Exception
     */
    private FKInstance getRetrieveConf(String dataModel, String concept, String path) throws Exception {
        FKInstance fkInstance = new FKInstance();
        Map<String, XSElementDecl> map = CommonDWR.getConceptMap(dataModel);
        XSElementDecl decl = map.get(concept);
        XSType type = decl.getType();

        if (type instanceof XSComplexType) {
            XSComplexType cmpxType = (XSComplexType) type;
            XSContentType conType = cmpxType.getContentType();
            XSParticle[] children = conType.asParticle().getTerm().asModelGroup().getChildren();

            for (XSParticle child : children) {
                XSTerm term = child.getTerm();

                if (term instanceof XSElementDecl && ((XSElementDecl) term).getName().equals(path)) {
                    XSElementDecl childElem = (XSElementDecl) child.getTerm();
                    XSAnnotation xsa = childElem.getAnnotation();
                    Element el = (Element) xsa.getAnnotation();
                    NodeList annotList = el.getChildNodes();

                    for (int k = 0; k < annotList.getLength(); k++) {
                        if ("appinfo".equals(annotList.item(k).getLocalName())) {
                            Node source1 = annotList.item(k).getAttributes().getNamedItem("source");

                            if (source1 == null)
                                continue;
                            String appinfoSource = annotList.item(k).getAttributes().getNamedItem("source").getNodeValue();

                            if ("X_ForeignKeyInfo".equals(appinfoSource)) {
                                fkInstance.setFkInfo(annotList.item(k).getFirstChild().getNodeValue());
                            }

                            if ("X_Retrieve_FKinfos".equals(appinfoSource)) {
                                fkInstance.setRetireveFKInfo("true".equals(annotList.item(k).getFirstChild().getNodeValue()));
                                break;
                            }
                        }
                    }
                }
            }
        }

        return fkInstance;
    }

    public class FKInstance {

        String fkInfo = null;

        boolean retireveFKInfo = false;

        public boolean isRetireveFKInfo() {
            return retireveFKInfo;
        }

        public void setRetireveFKInfo(boolean retireveFKInfo) {
            this.retireveFKInfo = retireveFKInfo;
        }

        public String getFkInfo() {
            return fkInfo;
        }

        public void setFkInfo(String fkInfo) {
            this.fkInfo = fkInfo;
        }

    }
}
