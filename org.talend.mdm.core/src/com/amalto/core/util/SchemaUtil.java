package com.amalto.core.util;

import java.io.StringReader;
import java.io.StringWriter;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class SchemaUtil {

	public static String formatXsdSource(String xmlSource) {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(new StringReader(xmlSource));
			StringWriter writer = new StringWriter();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			format.setIndentSize(4);
			format.setSuppressDeclaration(true);
			XMLWriter xmlwriter = new XMLWriter(writer, format);
			xmlwriter.write(document);
			return writer.toString();
		} catch (Exception e) {
			
		}
		return xmlSource;

	}

}
