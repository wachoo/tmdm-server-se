package com.amalto.core.migration.tasks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
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

				ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
						dataModelPOJO.getSchema().getBytes("UTF-8"));

				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(new ByteArrayInputStream(
						dataModelPOJO.getSchema().getBytes()));
				Element element = (Element) document
						.selectSingleNode("xsd:schema/xsd:complexType/xsd:sequence/xsd:element/xsd:simpleType/xsd:restriction");
				List list = element.elements();

				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					Element elementXsdEnumeration = (Element) iterator.next();
					Attribute attribute = elementXsdEnumeration.attribute(0);
					if ("ACTION".equals(attribute.getValue()))
						return null;

				}
				Element xsdEnumeration = element.addElement("xsd:enumeration");
				xsdEnumeration.addAttribute("value", "ACTION");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				OutputFormat format = new OutputFormat(" ", true, "utf-8");
				XMLWriter writer = new XMLWriter(out, format);
				writer.write(document);
				String schema = out.toString("utf-8");
				dataModelPOJO.setSchema(schema);
				dataModelPOJO.store();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
