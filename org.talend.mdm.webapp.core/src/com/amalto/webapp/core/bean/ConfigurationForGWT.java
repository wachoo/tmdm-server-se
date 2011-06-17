package com.amalto.webapp.core.bean;

import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSPutItem;


public class ConfigurationForGWT {
    
    private static HttpSession session;
    
    private String cluster;

    private String model;
    
    /**
     * DO NOT USE THIS CONSTRUCTOR
     * 
     */
    public ConfigurationForGWT() {

    }

    private ConfigurationForGWT(String cluster, String model) {
        this.cluster = cluster;
        this.model = model;
    }

    public static void initialize(HttpSession session, String cluster, String model) throws Exception {
        ConfigurationForGWT.session = session;
        ConfigurationForGWT.session.setAttribute("configuration", null);
        store(cluster, model);
    }
    
    private static void store(String cluster, String model) throws Exception {
        if (cluster == null || cluster.trim().length() == 0)
            throw new Exception("Data Container can't be empty!");
        if (model == null || model.trim().length() == 0)
            throw new Exception("Data Model can't be empty!");
        WebContext ctx = WebContextFactory.get();
        ctx.getSession().setAttribute("configuration", new ConfigurationForGWT(cluster, model));

        String xml = Util.getAjaxSubject().getXml();
        Document d = Util.parse(xml);
        NodeList nodeList = Util.getNodeList(d, "//property");
        if (nodeList.getLength() == 0) {
            if (Util.getNodeList(d, "//properties").item(0) == null)
                d.getDocumentElement().appendChild(d.createElement("properties"));
            Node node = Util.getNodeList(d, "//properties").item(0).appendChild(d.createElement("property"));
            node.appendChild(d.createElement("name")).appendChild(d.createTextNode("cluster"));
            ;
            node.appendChild(d.createElement("value")).appendChild(d.createTextNode(cluster));
            ;
            Node node2 = Util.getNodeList(d, "//properties").item(0).appendChild(d.createElement("property"));
            node2.appendChild(d.createElement("name")).appendChild(d.createTextNode("model"));
            ;
            node2.appendChild(d.createElement("value")).appendChild(d.createTextNode(model));
            ;
        }

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if ("cluster".equals(Util.getFirstTextNode(node, "name"))) {
                if (Util.getFirstTextNode(node, "value") == null)
                    Util.getNodeList(node, "value").item(0).appendChild(d.createTextNode(cluster));
                else
                    Util.getNodeList(node, "value").item(0).getFirstChild().setNodeValue(cluster);
            }
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if ("model".equals(Util.getFirstTextNode(node, "name"))) {
                if (Util.getFirstTextNode(node, "value") == null)
                    Util.getNodeList(node, "value").item(0).appendChild(d.createTextNode(model));
                else
                    Util.getNodeList(node, "value").item(0).getFirstChild().setNodeValue(model);
            }
        }
        if (com.amalto.core.util.Util.isEnterprise()) {
            Util.getPort().putItem(
                    new WSPutItem(new WSDataClusterPK("PROVISIONING"), CommonDWR.getXMLStringFromDocument(d).replaceAll(
                            "<\\?xml.*?\\?>", ""), new WSDataModelPK("PROVISIONING"), false));
        } else {
            Util.storeProvisioning(Util.getLoginUserName(), CommonDWR.getXMLStringFromDocument(d)
                    .replaceAll("<\\?xml.*?\\?>", ""));
        }
    }
    
    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    @Override
    public String toString() {

        return "cluster:" + cluster + "," + "model:" + model;
    }
}
