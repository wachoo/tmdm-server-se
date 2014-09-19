package com.amalto.core.migration.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;

public class AddActionInUpdateReportTask extends AbstractMigrationTask {

	@Override
	protected Boolean execute() {
		if (MDMConfiguration.isExistDb()) {
			try {
				DataModelPOJO dataModelPOJO = Util.getDataModelCtrlLocal()
						.getDataModel(
								new DataModelPOJOPK(
										XSystemObjects.DM_UPDATEREPORT
												.getName()));
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(new ByteArrayInputStream(
						dataModelPOJO.getSchema().getBytes()));
				Element element = (Element) document
						.selectSingleNode("xsd:schema/xsd:complexType/xsd:sequence/xsd:element/xsd:simpleType/xsd:restriction"); //$NON-NLS-1$
				List list = element.elements();

                for (Object aList : list) {
                    Element elementXsdEnumeration = (Element) aList;
                    Attribute attribute = elementXsdEnumeration.attribute(0);
                    if ("ACTION".equals(attribute.getValue())) { //$NON-NLS-1$
                        return null;
                    }
                }
				Element xsdEnumeration = element.addElement("xsd:enumeration"); //$NON-NLS-1$
				xsdEnumeration.addAttribute("value", "ACTION"); //$NON-NLS-1$ //$NON-NLS-2$
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputFormat format = new OutputFormat(" ", true, "utf-8"); //$NON-NLS-1$ //$NON-NLS-2$
				XMLWriter writer = new XMLWriter(out, format);
				writer.write(document);
				String schema = out.toString("utf-8"); //$NON-NLS-1$
				dataModelPOJO.setSchema(schema);
				dataModelPOJO.store();
			} catch (Exception e) {
                Logger.getLogger(AddActionInUpdateReportTask.class).error("Task exception occurred.", e);
            }
		}
		return null;
	}

}
