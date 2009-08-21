package com.amalto.webapp.core.bean;

import org.w3c.dom.Element;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Util2;
import com.amalto.webapp.core.util.XtentisWebappException;

//Castor Marshallable Class to use with the new b2box Configuration Mechanism

public class XMLConfiguration_Searchable extends XMLConfiguration_XPath {
	
	private String type;
	
	public static XMLConfiguration_Searchable parseXMLConfiguration_Searchable(Element element) throws XtentisWebappException {
		XMLConfiguration_Searchable xmlSearchable = new XMLConfiguration_Searchable();
		try {
			xmlSearchable.setName(Util.getFirstTextNode(element, "Name"));
			xmlSearchable.setXPath(Util.getFirstTextNode(element, "XPath"));
			xmlSearchable.setType(Util.getFirstTextNode(element, "Type"));
			Element desc = Util2.getFirstElement(element, "Description");
			if (desc !=null) {
				xmlSearchable.setDescription(XMLConfiguration_Description.parseXMLConfiguration_Description(desc));
			} else {
				org.apache.log4j.Logger.getLogger(XMLConfiguration_XPath.class).info(
						"parseXMLConfiguration_XPath() No description for XPath "+xmlSearchable.getName());
			} 
			return xmlSearchable;
		} catch (XtentisWebappException e) {
			throw(e);
		} catch (Exception e) {
			try {
				throw new XtentisWebappException("Unable to read the XPath from element "+
						Util.nodeToString(element));
			} catch (Exception ex) {
				throw new XtentisWebappException("Unable to read the XPath from element "+element.getLocalName());
			}
		}
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	/**
	 * Return the XMLized version of the xPath withthe propoer nodeName as Root
	 * @param nodeName - the root node name
	 * @return the XML
	 */
	
	@Override
	public String toString(String nodeName) {
		String xml = "<"+nodeName+">";
		xml+= "	<Name>" + getName() + "</Name>";
		xml+= getDescription().toString();
		xml+= "	<XPath>" + getXPath() + "</XPath>" +
		"<Type>" + getType() + "</Type>" +
		"</"+nodeName+">";
		return xml;
	}
	
	
}
