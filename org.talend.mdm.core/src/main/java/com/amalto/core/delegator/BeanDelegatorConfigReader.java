package com.amalto.core.delegator;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.util.Util;

public class BeanDelegatorConfigReader {

    private static final Logger LOGGER = Logger.getLogger(BeanDelegatorConfigReader.class);

    private static final Map<String, String> beanImplNamesMap = new HashMap<String, String>();

    public static Map<String, String> readConfiguration() {
        synchronized (beanImplNamesMap) {
            if (!beanImplNamesMap.isEmpty()) {
                return beanImplNamesMap;
            }
            try {
                InputStream in = BeanDelegatorConfigReader.class.getResourceAsStream("/com/amalto/core/delegator/delegator.xml"); //$NON-NLS-1$
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(in);
                NodeList nodelist = Util.getNodeList(doc.getDocumentElement(), "/bean/delegator/name"); //$NON-NLS-1$
                for (int i = 0; i < nodelist.getLength(); i++) {
                    Node node = nodelist.item(i);
                    String textContent = node.getTextContent();
                    String[] tmp = textContent.split("#"); //$NON-NLS-1$
                    beanImplNamesMap.put(tmp[0], tmp[1]);
                }
            } catch (Exception e) {
                LOGGER.error(e);
            }
            return beanImplNamesMap;
        }
    }
}
