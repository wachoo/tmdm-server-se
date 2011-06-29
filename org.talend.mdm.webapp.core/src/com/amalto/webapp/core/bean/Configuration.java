package com.amalto.webapp.core.bean;

import javax.security.jacc.PolicyContextException;
import javax.servlet.http.HttpSession;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSBoolean;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSExistsDataCluster;
import com.amalto.webapp.util.webservices.WSExistsDataModel;
import com.amalto.webapp.util.webservices.WSPutItem;

public class Configuration {

    private String cluster;

    private String model;

    // private static Configuration instance;

    /**
     * DO NOT USE THIS CONSTRUCTOR
     * 
     */
    public Configuration() {

    }

    private Configuration(String cluster, String model) {
        this.cluster = cluster;
        this.model = model;
    }

    public static Configuration getInstance() throws Exception {
        // aiming modify load the configure from db instead of session
        WebContext ctx = WebContextFactory.get();
        Configuration instance;
        if (ctx == null || ctx.getSession().getAttribute("configuration") == null) {
            org.apache.log4j.Logger.getLogger(Configuration.class).info("instance null, loading");
            instance = load();

        } else {
            instance = (Configuration) ctx.getSession().getAttribute("configuration");
        }

        return instance;
    }

    public static Configuration getInstance(HttpSession session) throws Exception {
        Configuration instance;
        if (session.getAttribute("configuration") == null) {
            org.apache.log4j.Logger.getLogger(Configuration.class).info("instance null, loading");
            instance = load(session);
        } else {
            instance = (Configuration) session.getAttribute("configuration");
        }

        return instance;
    }

    public static Configuration getInstance(boolean forceReload) throws Exception {

        Configuration instance;
        if (forceReload) {
            instance = load();
        } else {
            instance = getInstance();
        }
        return instance;
    }

    public static void initialize(String cluster, String model) throws Exception {
        WebContext ctx = WebContextFactory.get();
        ctx.getSession().setAttribute("configuration", null);
        store(cluster, model);
    }

    private static void store(String cluster, String model) throws Exception {
        if (cluster == null || cluster.trim().length() == 0)
            throw new Exception("Data Container can't be empty!");
        if (model == null || model.trim().length() == 0)
            throw new Exception("Data Model can't be empty!");
        WebContext ctx = WebContextFactory.get();
        ctx.getSession().setAttribute("configuration", new Configuration(cluster, model));

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

    private static Configuration load() throws Exception {
        WebContext ctx = WebContextFactory.get();
        Configuration configuration = loadConfigurationFromDB();
        if (ctx != null)
            ctx.getSession().setAttribute("configuration", configuration);
        org.apache.log4j.Logger.getLogger(Configuration.class).info(
                "MDM set up with " + configuration.getCluster() + " and " + configuration.getModel());

        return configuration;
    }
    
    
    /**
     * DOC HSHU Comment method "getConfiguration".
     */
    public static Configuration getConfiguration() throws Exception {
        Configuration configuration = null;
        WebContext ctx = WebContextFactory.get();
        if (ctx != null&&ctx.getSession().getAttribute("configuration")!=null) {
            configuration = (Configuration) ctx.getSession().getAttribute("configuration");
        }else {
            configuration = loadConfigurationFromDB();
        }
        return configuration;
    }

    private static Configuration load(HttpSession session) throws Exception {
        Configuration configuration = loadConfigurationFromDB();
        session.setAttribute("configuration", configuration);
        org.apache.log4j.Logger.getLogger(Configuration.class).info(
                "MDM set up with " + configuration.getCluster() + " and " + configuration.getModel());
        return configuration;
    }

    public static Configuration loadConfigurationFromDBDirectly() throws Exception {
        return loadConfigurationFromDB();
    }

    private static Configuration loadConfigurationFromDB() throws PolicyContextException, Exception, XtentisWebappException {
        Configuration configuration = new Configuration();

        Element user = null;
        try {
            user = Util.getLoginProvisioningFromDB();
        } catch (Exception e) {
        }
        if (user == null) {
            String userxml = Util.getAjaxSubject().getXml();
            user = (Element) Util.getNodeList(Util.parse(userxml), "//" + "User").item(0);
        }

        // Document d = Util.parse(userString);
        NodeList nodeList = Util.getNodeList(user, "//property");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if ("cluster".equals(Util.getFirstTextNode(node, "name"))) {
                // configuration.setCluster(Util.getNodeList(node, "value").item(0).getTextContent());
                Node fchild = Util.getNodeList(node, "value").item(0).getFirstChild();
                if (fchild != null) {
                    WSExistsDataCluster wsExistsDataCluster = new WSExistsDataCluster();
                    wsExistsDataCluster.setWsDataClusterPK(new WSDataClusterPK(fchild.getNodeValue()));
                    WSBoolean wsBoolean = Util.getPort().existsDataCluster(wsExistsDataCluster);
                    if (wsBoolean.is_true()) {
                        configuration.setCluster(fchild.getNodeValue());
                    }

                }

            }
        }
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if ("model".equals(Util.getFirstTextNode(node, "name"))) {
                // configuration.setModel(Util.getNodeList(node, "value").item(0).getTextContent());
                Node fchild = Util.getNodeList(node, "value").item(0).getFirstChild();
                if (fchild != null) {
                    WSExistsDataModel wsExistsDataModel = new WSExistsDataModel();
                    wsExistsDataModel.setWsDataModelPK(new WSDataModelPK(fchild.getNodeValue()));
                    WSBoolean wsBoolean = Util.getPort().existsDataModel(wsExistsDataModel);
                    if (wsBoolean.is_true()) {
                        configuration.setModel(fchild.getNodeValue());
                    }
                }

            }
        }
        if (configuration.getCluster() == null && CommonDWR.getClusters().length > 0) {
            configuration.setCluster(CommonDWR.getClusters()[0]);
        }
        if (configuration.getModel() == null && CommonDWR.getModels().length > 0) {
            configuration.setModel(CommonDWR.getModels()[0]);
        }
        return configuration;
    }

    /*
     * public static Configuration getInstance() throws Exception { //instance=null; if (instance == null) {
     * org.apache.log4j.Logger.getLogger(Configuration.class).info( "instance null, loading"); instance = load();
     * 
     * } else{ org.apache.log4j.Logger.getLogger(Configuration.class).info(
     * "MDM set up with "+instance.getCluster()+" and "+instance.getModel()); }
     * 
     * return instance; }
     * 
     * private static void store(String cluster, String model) { try { StringWriter sw = new StringWriter(); Marshaller
     * marshaller = new Marshaller(sw); marshaller.marshal(new Configuration(cluster, model)); String xml =
     * sw.toString(); org.apache.log4j.Logger.getLogger(Configuration.class).debug( "Configuration store xml"); xml =
     * StringEscapeUtils.escapeXml(xml); String xml2 = (new StringBuilder(
     * "<Conf><id>MDMConfig</id><Config>")).append(xml) .append("</Config></Conf>").toString(); try {
     * Util.getPort().putItem( new WSPutItem(new WSDataClusterPK("MDMCONF"), xml2, new WSDataModelPK("CONF"))); } catch
     * (Exception e) { e.printStackTrace(); } } catch (MarshalException e) { e.printStackTrace(); } catch
     * (ValidationException e) { e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); } }
     * 
     * private static Configuration load() { Configuration configuration = new Configuration(); Unmarshaller
     * unmarshaller = new Unmarshaller(configuration); String xml = ""; try { xml = Util.getPort().getItem( new
     * WSGetItem(new WSItemPK(new WSDataClusterPK("MDMCONF"), "Conf", new String[] { "MDMConfig" }))) .getContent(); }
     * catch (RemoteException e1) { e1.printStackTrace(); return null; } catch (Exception e1) { e1.printStackTrace();
     * return null; } org.apache.log4j.Logger.getLogger(Configuration.class).debug( "Configuration load xml"); try {
     * org.w3c.dom.Document document = Util.parse(xml); String xml2 =
     * StringEscapeUtils.unescapeXml(Util.getFirstTextNode( document, "Conf/Config")); xml2 =
     * xml2.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",""); //System.out.println("xml2 "+xml2);
     * if(xml2.equals("<configuration/>")) return null; Configuration config = (Configuration) unmarshaller
     * .unmarshal(new InputSource(new StringReader(xml2))); return config; } catch (MarshalException e) {
     * e.printStackTrace(); return null; } catch (ValidationException e) { e.printStackTrace(); return null; } catch
     * (Exception e1) { e1.printStackTrace(); } return null; }
     */
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
