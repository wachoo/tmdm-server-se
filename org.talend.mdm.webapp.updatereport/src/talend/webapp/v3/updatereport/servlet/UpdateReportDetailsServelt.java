package talend.webapp.v3.updatereport.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
        String export = req.getParameter("exportContent");

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
                }
            } catch (XtentisWebappException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PrintWriter out = resp.getWriter();
        if (export != null && export.length() > 0) {
            resp.setHeader("Pragma", "public");
            resp.setHeader("Expires", "0");
            resp.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            resp.setHeader("Content-Type", "application/force-download");
            resp.setHeader("Content-Type", "application/vnd.ms-excel");
            resp.setHeader("Content-Disposition", "attachment;filename=MyReport.xls");
            out.println(export);
        } else
            out.println(jsonTree);
        out.close();

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
