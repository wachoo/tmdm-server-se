package com.amalto.core.migration;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.util.Util;

public class MigrationRepository{

    private static boolean isExecuted = false;

    private static MigrationRepository repository = null;

    private MigrationRepository() {
    }

    synchronized public static MigrationRepository getInstance() {
        if (repository == null) {
            repository = new MigrationRepository();
        }
        return repository;
    }

    public void execute(boolean force) {
        if (!isExecuted || force) {
            List<String> list = new ArrayList<String>();
            // look over the handlers dir, call up each handler to execute if it can
            try {
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                InputStream in = MigrationRepository.class.getResourceAsStream("/com/amalto/core/migration/migration.xml"); //$NON-NLS-1$
                InputStream extIn = MigrationRepository.class.getResourceAsStream("/com/amalto/core/migration/migration-extension.xml"); //$NON-NLS-1$
                parseConfigList(list, builder, in);
                if (extIn != null) {
                    parseConfigList(list, builder, extIn);
                }
                // get the class definition and invoke the trigger function
                for (String clazz : list) {
                    try {
                        AbstractMigrationTask task = (AbstractMigrationTask) Class.forName(clazz).newInstance();
                        task.setForceExe(force);
                        task.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //clear the cache objects, is this still need?
                Util.getXmlServerCtrlLocal().clearCache();
                ObjectPOJO.clearCache();
                ItemPOJO.clearCache();
            } catch (Exception e) {
                org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getCause());
                return;
            }
        }
        isExecuted = true;
    }

    private void parseConfigList(List<String> list, DocumentBuilder builder,
                                 InputStream in) throws SAXException, IOException {
        Document doc = builder.parse(in);
        NodeList nodelist = doc.getElementsByTagName("Root"); //$NON-NLS-1$
        Node root = nodelist.item(0);
        for (int id = 0; id < root.getChildNodes().getLength(); id++) {
            Node node = root.getChildNodes().item(id);
            if (node instanceof Element)
                list.add(node.getNodeName());
        }
    }

    public void connect(boolean forceUpgrade) {
        execute(forceUpgrade);
    }

}
