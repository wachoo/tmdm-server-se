package com.amalto.core.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			String str= writer.toString();
			writer.close();
			xmlwriter.close();
			return str;
		} catch (Exception e) {
			
		}
		return xmlSource;

	}
    /**
     * change base type to xsd:type e.g base="string" => base="xsd:string"
     * @param xsd
     * @return
     */
    public static String convertBaseType(String xsd){
    	String simpletypes="allNNI, anySimpleType, anyURI, base64Binary, blockSet, boolean, byte, date, dateTime, decimal, derivationControl, derivationSet, double, duration, ENTITIES, ENTITY, float, formChoice, fullDerivationSet, gDay, gMonth, gMonthDay, gYear, gYearMonth, hexBinary, ID, IDREF, IDREFS, int, integer, language, long, Name, namespaceList, NCName, negativeInteger, NMTOKEN, NMTOKENS, nonNegativeInteger, nonPositiveInteger, normalizedString, NOTATION, positiveInteger, public, QName, reducedDerivationControl, short, simpleDerivationSet, string, time, token, typeDerivationControl, unsignedByte, unsignedInt, unsignedLong, unsignedShort";
    	String[] simpleBaseTypes=simpletypes.split(", ");
    	List<String> list=Arrays.asList(simpleBaseTypes);
    	Map<String, String> map=new HashMap<String, String>();
    	Pattern p=Pattern.compile("base=\"(.*?)\"");
    	Matcher m=p.matcher(xsd);
    	while(m.find()){
    		String type=m.group(1);
    		if(!type.startsWith("xsd:") && list.contains(type)){
    			map.put("base=\""+type+"\"", "base=\"xsd:"+type+"\"");
    		}
    	}
    	Set<Entry<String, String>>entry=map.entrySet();
    	for(Entry<String, String> item: entry){
    		xsd=xsd.replace(item.getKey(), item.getValue());
    	}
    	return xsd;
    }
}
