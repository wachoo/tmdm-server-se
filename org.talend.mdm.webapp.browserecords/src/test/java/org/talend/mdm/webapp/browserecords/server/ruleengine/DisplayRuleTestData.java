package org.talend.mdm.webapp.browserecords.server.ruleengine;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;


public class DisplayRuleTestData {

    private static final Logger LOG = Logger.getLogger(DisplayRuleTestData.class);

    public static Document getDocument(String name) {
        Document document = null;
        try {
            InputStream is = DisplayRuleTestData.class.getResourceAsStream(name);
            document = org.talend.mdm.webapp.base.server.util.XmlUtil.parse(is);

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return document;
    }

    public static Map<String, TypeModel> getMetaData(String model, String concept) {
        XSElementDecl e = getElementDecl(model, concept);
        EntityModel entityModel = new EntityModel();
        DataModelHelper.travelXSElement(e, e.getName(), entityModel, null, Arrays.asList("System_Admin"), 0, 0); //$NON-NLS-1$
        return entityModel.getMetaDataTypes();
    }

    public static XSElementDecl getElementDecl(String model, String concept) {
        try {
            XSOMParser reader = new XSOMParser();
            reader.setAnnotationParser(new DomAnnotationParserFactory());
            BufferedReader br = new BufferedReader(new InputStreamReader(DisplayRuleTestData.class.getResourceAsStream(model)));
            reader.parse(br);
            XSSchemaSet xss = reader.getResult();
            XSElementDecl eleDecl = getElementDeclByName(concept, xss);
            return eleDecl;
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }

    private static XSElementDecl getElementDeclByName(String eleName, XSSchemaSet xss) {
        Collection<XSSchema> schemas = xss.getSchemas();
        for (XSSchema xsa : schemas) {
            Map<String, XSElementDecl> elems = xsa.getElementDecls();
            XSElementDecl eleDecl = elems.get(eleName);
            if (eleDecl != null) {
                return eleDecl;
            }
        }
        return null;
    }
}
