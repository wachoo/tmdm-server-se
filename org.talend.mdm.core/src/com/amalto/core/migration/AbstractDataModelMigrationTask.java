package com.amalto.core.migration;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.Util;


public abstract class AbstractDataModelMigrationTask extends AbstractMigrationTask{
    
    protected final String cluster = "amaltoOBJECTSDataModel";//$NON-NLS-1$
    
    
    /**
     * DOC HSHU Comment method "getDataModel".
     */
    protected abstract String getDataModel();
    
    protected Boolean execute() {
        
        
        try {
            String dataModelXml = ConfigurationHelper.getServer().getDocumentAsString(null, cluster, getDataModel());
            Document doc = Util.parse(dataModelXml);
            updateSchema(doc);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    protected Document getSchemaFromDataModelPojoDocument(Document doc) throws Exception {

        NodeList nodeList = Util.getNodeList(doc, "./schema/text()"); //$NON-NLS-1$
        Document schemaRoot = null;
        if (nodeList.getLength() > 0) {
            Object obj = nodeList.item(0);
            if (obj instanceof Text) {
                String wholeSchema = ((Text) obj).getWholeText();
                schemaRoot = Util.parseXSD(wholeSchema);
            }
        }
        return schemaRoot;

    }

    protected abstract void updateSchema(Document doc) throws Exception;

}
