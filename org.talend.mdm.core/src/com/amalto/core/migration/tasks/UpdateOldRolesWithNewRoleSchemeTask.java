package com.amalto.core.migration.tasks;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import org.apache.commons.lang.StringEscapeUtils;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public class UpdateOldRolesWithNewRoleSchemeTask extends AbstractMigrationTask {

    static final String BAR_ZIP = ".bar";

    static final String SUFFIX_BAR = "_r";

    private boolean updateRolesInProvision() {
        final String userClusterName = "amaltoOBJECTSRole";
        String query = "collection(\"" + userClusterName + "\")/role-pOJO/PK/ids"; // collection("/amaltoOBJECTSRole")/role-pOJO/PK/ids
        try {
            XmlServerSLWrapperLocal server = ConfigurationHelper.getServer();
            ArrayList<String> list = server.runQuery(null, userClusterName, query, null);
            for (String user : list) {
                NodeList users = Util.getNodeList(Util.parse(user), "/ids");
                for (int i = 0; i < users.getLength(); i++) {
                    String uniqueID = users.item(i).getFirstChild().getNodeValue();
                    String userXml = server.getDocumentAsString(null, userClusterName, uniqueID);
                    String newUniqueID = ICoreConstants.rolesConvert.oldRoleToNewRoleMap.get(uniqueID);
                    if (newUniqueID != null) {
                        for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet()) {
                            userXml = userXml.replaceAll(pair.getKey().toString(), pair.getValue().toString());
                        }
                        server.start(userClusterName);
                        server.putDocumentFromString(userXml, newUniqueID, userClusterName, null);
                        server.commit(userClusterName);
                        server.deleteDocument(null, userClusterName, uniqueID);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean updateUsersWithNewRoleScheme() {
        final String userClusterName = "PROVISIONING";
        String query = "collection(\"" + userClusterName + "\")/ii/p/User/username";
        try {
            XmlServerSLWrapperLocal server = ConfigurationHelper.getServer();
            ArrayList<String> list = server.runQuery(null, userClusterName, query, null);
            server.start(userClusterName);
            for (String user : list) {
                NodeList users = Util.getNodeList(Util.parse(user), "/username");
                for (int i = 0; i < users.getLength(); i++) {
                    String entry = users.item(i).getFirstChild().getNodeValue();
                    String uniqueID = userClusterName + ".User." + entry;
                    String userXml = server.getDocumentAsString(null, userClusterName, uniqueID);
                    String updateXml = new String(userXml);
                    for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet()) {
                        userXml = userXml.replaceAll(pair.getKey().toString(), pair.getValue().toString());
                    }
                    if (!updateXml.equals(userXml)) {
                        server.putDocumentFromString(userXml, uniqueID, userClusterName, null);
                    }
                }
            }
            server.commit(userClusterName);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private List<String> getRoleDefinitions() {
        final String userClusterName = "amaltoOBJECTSRole";
        String query = "collection(\"" + userClusterName + "\")/role-pOJO/PK/ids/text()"; //
        ArrayList<String> list = null;
        try {
            list = ConfigurationHelper.getServer().runQuery(null, userClusterName, query, null);
        } catch (XtentisException e) {
        }

        return list;
    }

    private boolean updateRolesInDataModel() {
        final String dataModelClusterName = "amaltoOBJECTSDataModel";
        String query = "collection(\"" + dataModelClusterName + "\")/data-model-pOJO/name"; // collection('amaltoOBJECTSDataModel')/data-model-pOJO/name
        List<String> roleNames = getRoleDefinitions();
        try {
            XmlServerSLWrapperLocal server = ConfigurationHelper.getServer();
            ArrayList<String> list = server.runQuery(null, dataModelClusterName, query, null);
            for (String role : list) {
                NodeList roles = Util.getNodeList(Util.parse(role), "/name");
                for (int i = 0; i < roles.getLength(); i++) {
                    String uniqueID = roles.item(i).getFirstChild().getNodeValue();
                    if (uniqueID.equals("XMLSCHEMA---"))
                        continue;
                    String dataModelXml = server.getDocumentAsString(null, dataModelClusterName,
                            uniqueID);
                    String cpyXml = new String(dataModelXml);
                    for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet()) {
                        dataModelXml = dataModelXml.replaceAll(pair.getKey().toString(), pair.getValue().toString());
                    }
                    Document doc = Util.parse(dataModelXml);
                    NodeList nodeList = Util.getNodeList(doc, "./schema/text()");
                    Document schemaRoot = null;
                    if (nodeList.getLength() > 0) {
                        Object obj = nodeList.item(0);
                        if (obj instanceof Text) {
                            String wholeSchema = ((Text) obj).getWholeText();
                            schemaRoot = Util.parseXSD(wholeSchema);
                        }
                    }
                    if (schemaRoot != null) {
                        Element rootNS = Util
                                .getRootElement("nsholder", schemaRoot.getDocumentElement().getNamespaceURI(), "xsd");
                        HashMap<Node, Node> toDelSet = new HashMap<Node, Node>();
                        NodeList users = Util.getNodeList(schemaRoot, "//xsd:appinfo[@source='X_Write']",
                                rootNS.getNamespaceURI(), "xsd");
                        for (int m = 0; m < users.getLength(); m++) {
                            String roleDes = users.item(m).getTextContent();
                            if (!roleNames.contains(roleDes)) {
                                toDelSet.put(users.item(m), users.item(m).getParentNode());
                            }
                        }

                        for (Map.Entry<Node, Node> entry : toDelSet.entrySet()) {
                            Node parent = entry.getValue();
                            Node node = entry.getKey();
                            if (parent != null) {
                                parent.removeChild(node);
                            }
                        }

                        if (!toDelSet.isEmpty()) {
                            String newSchema = "<schema>" + StringEscapeUtils.escapeXml(Util.nodeToString(schemaRoot))
                                    + "</schema>";
                            Node oldChild = Util.getNodeList(doc, "./schema").item(0);
                            Node elem = doc.importNode(Util.parse(newSchema).getDocumentElement(), true);
                            doc.getDocumentElement().replaceChild(elem, oldChild);
                            dataModelXml = Util.nodeToString(doc);
                        }
                    }
                    if (!dataModelXml.equals(cpyXml)) {
                        server.start(dataModelClusterName);
                        server.putDocumentFromString(dataModelXml, uniqueID, dataModelClusterName, null);
                        server.commit(dataModelClusterName);
                        ObjectPOJO.clearCache();
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static boolean updateRolesInWorkFlow() {
        String barHome = Util.getBarHomeDir();
        File barDir = new File(barHome);
        File monitorFiles[] = barDir.listFiles(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name.endsWith(".bar");
            }
        });

        if (monitorFiles == null)
            return true;
        ArrayList<String> oldBarFiles = new ArrayList<String>();
        ArrayList<String> newBarFiles = new ArrayList<String>();
        for (File barFile : monitorFiles) {
            String outName = barFile.getAbsolutePath().substring(0, barFile.getAbsolutePath().length() - 4) + SUFFIX_BAR
                    + BAR_ZIP;
            JarInputStream jarIn = null;
            JarOutputStream jarOut = null;
            ByteArrayOutputStream outBytes = null;
            try {
                jarIn = new JarInputStream(new FileInputStream(barFile));
                jarOut = new JarOutputStream(new FileOutputStream(outName));
                JarEntry entry;
                byte[] buf = new byte[4096];
                while ((entry = jarIn.getNextJarEntry()) != null) {

                    if ("META-INF/MANIFEST.MF".equals(entry.getName()))
                        continue;
                    int read;
                    jarOut.putNextEntry(entry);
                    if (!entry.getName().endsWith(".proc") && !entry.getName().endsWith(".xml")) {
                        while ((read = jarIn.read(buf)) != -1) {
                            jarOut.write(buf, 0, read);
                        }
                    } else {
                        outBytes = new ByteArrayOutputStream();
                        while ((read = jarIn.read(buf, 0, 4096)) != -1) {
                            outBytes.write(buf, 0, read);
                        }

                        String orgSrc = new String(outBytes.toByteArray());
                        String orgCpy = new String(orgSrc);
                        for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet()) {
                            orgSrc = orgSrc.replaceAll(pair.getKey().toString(), pair.getValue().toString());
                        }

                        if (orgSrc.equals(orgCpy)) {
                            jarOut.write(orgCpy.getBytes(), 0, orgCpy.getBytes().length);
                        } else {
                            jarOut.write(orgSrc.getBytes(), 0, orgSrc.getBytes().length);
                            if (oldBarFiles.indexOf(barFile.getAbsolutePath()) == -1) {
                                oldBarFiles.add(barFile.getAbsolutePath());
                            }
                            if (newBarFiles.indexOf(outName) == -1) {
                                newBarFiles.add(outName);
                            }
                        }

                    }

                    jarOut.closeEntry();
                }

                if (newBarFiles.indexOf(outName) == -1) {
                    oldBarFiles.add(outName);
                }
                jarOut.flush();
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (outBytes != null)
                        outBytes.close();
                    jarOut.close();
                    jarIn.close();
                } catch (IOException e) {
                    return false;
                }
            }
        }

        for (String fileName : oldBarFiles) {
            File toDelFile = new File(fileName);
            if (toDelFile.exists())
                toDelFile.delete();
        }

        boolean rename = true;
        for (String fileName : newBarFiles) {
            File newFile = new File(fileName);
            if (newFile.exists()) {
                int sign = newFile.getAbsolutePath().indexOf(SUFFIX_BAR);
                if (newFile.renameTo(new File(newFile.getAbsolutePath().substring(0, sign) + BAR_ZIP)) == false) {
                    rename = false;
                }
            }
        }

        return rename;
    }

    @Override
    protected Boolean execute() {
        boolean execute = updateRolesInProvision() && updateRolesInDataModel() && updateUsersWithNewRoleScheme();
        if (Util.isEnterprise()) {
            execute &= updateRolesInWorkFlow();
        }
        return execute;
    }
}
